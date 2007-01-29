/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.contrib.javadocfirstsentence;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.java.lexer.JavadocTokenId;
import org.netbeans.api.lexer.TokenChange;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyListener;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;
import org.openide.util.WeakListeners;

/**
 * Highlights the first sentence of a javadoc comment.
 * 
 * @author Vita Stejskal
 */
public class Highlighting extends AbstractHighlightsContainer implements TokenHierarchyListener {

    private static final Logger LOG = Logger.getLogger(Highlighting.class.getName());
    
    public static final String LAYER_ID = "org.netbeans.modules.firstsentence.Highlighting"; //NOI18N
    
    private static final AttributeSet ATTR = AttributesUtilities.createImmutable(
        StyleConstants.Bold, Boolean.TRUE
//        StyleConstants.Background, new Color(255, 0, 0)
    );
    
    private Document document;
    private TokenHierarchy<? extends Document> hierarchy = null;
    private long version = 0;
    
    /** Creates a new instance of Highlighting */
    public Highlighting(Document doc) {
        this.document = doc;
    }

    public HighlightsSequence getHighlights(int startOffset, int endOffset) {
        synchronized (this) {
            if (hierarchy == null) {
                hierarchy = TokenHierarchy.get(document);
                if (hierarchy != null) {
                    hierarchy.addTokenHierarchyListener(WeakListeners.create(TokenHierarchyListener.class, this, hierarchy));
                }
            }

            if (hierarchy != null) {
                return new HSImpl(version, hierarchy, startOffset, endOffset);
            } else {
                return HighlightsSequence.EMPTY;
            }
        }
    }

    // ----------------------------------------------------------------------
    //  TokenHierarchyListener implementation
    // ----------------------------------------------------------------------

    public void tokenHierarchyChanged(TokenHierarchyEvent evt) {
        TokenChange<? extends TokenId> tc = evt.tokenChange();
        int affectedArea [] = null;
        
        TokenSequence<? extends TokenId> seq = tc.currentTokenSequence();
        if (seq.language().equals(JavadocTokenId.language())) {
            // Change inside javadoc
            int [] firstSentence = findFirstSentence(seq);
            if (firstSentence != null) {
                if (tc.offset() <= firstSentence[1]) {
                    // Change before the end of the first sentence
                    affectedArea = firstSentence;
                }
            } else {
                // XXX: need the embedding token (i.e. JavaTokenId.JAVADOC_COMMENT*)
                // and fire a change in its whole area
                affectedArea = new int [] { tc.offset(), Integer.MAX_VALUE };
            }
        } else {
            // The change may or may not involve javadoc, so reset everyting.
            // It would be more efficient to traverse the changed area and
            // find out whether it really involves javadoc or not.
            affectedArea = new int [] { tc.offset(), Integer.MAX_VALUE };
        }
        
        if (affectedArea != null) {
            synchronized (this) {
                version++;
            }

            fireHighlightsChange(affectedArea[0], affectedArea[1]);
        }
    }

    // ----------------------------------------------------------------------
    //  Private implementation
    // ----------------------------------------------------------------------

    private int [] findFirstSentence(TokenSequence<? extends TokenId> seq) {
        seq.moveStart();
        if (seq.moveNext()) {
            int start = seq.offset();
            do {
                if (seq.token().id() == JavadocTokenId.DOT) {
                    return new int [] { start, seq.offset() };
                }
            } while (seq.moveNext());
        }
        
        return null;
    }

    private final class HSImpl implements HighlightsSequence {
        
        private long version;
        private TokenHierarchy<? extends Document> scanner;
        private List<TokenSequence<? extends TokenId>> sequences;
        private int startOffset;
        private int endOffset;
        
        private List<Integer> lines = null;
        private int linesIdx = -1;
        
        public HSImpl(long version, TokenHierarchy<? extends Document> scanner, int startOffset, int endOffset) {
            this.version = version;
            this.scanner = scanner;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.sequences = null;
        }

        public boolean moveNext() {
            synchronized (Highlighting.this) {
                checkVersion();
                
                if (sequences == null) {
                    // initialize
                    TokenSequence<? extends TokenId> seq = scanner.tokenSequence().subSequence(startOffset, endOffset);
                    sequences = new ArrayList<TokenSequence<? extends TokenId>>();
                    sequences.add(seq);
                }

                if (lines != null) {
                    if (linesIdx + 2 < lines.size()) {
                        linesIdx += 2;
                        return true;
                    }
                    
                    lines = null;
                    linesIdx = -1;
                }
                
                while (!sequences.isEmpty()) {
                    TokenSequence<? extends TokenId> seq = sequences.get(sequences.size() - 1);

                    if (seq.language().equals(JavadocTokenId.language())) {
                        int [] firstSentence = findFirstSentence(seq);
                        sequences.remove(sequences.size() - 1);

                        if (firstSentence != null) {
                            lines = splitByLines(firstSentence[0], firstSentence[1]);
                            if (lines != null) {
                                linesIdx = 0;
                                return true;
                            }
                        }
                    } else {
                        boolean hasNextToken;

                        while (true == (hasNextToken = seq.moveNext())) {
                            TokenSequence<? extends TokenId> embeddedSeq = seq.embedded();
                            if (embeddedSeq != null) {
                                sequences.add(sequences.size(), embeddedSeq);
                                break;
                            }
                        }

                        if (!hasNextToken) {
                            sequences.remove(sequences.size() - 1);
                        }
                    }
                }

                return false;
            }
        }

        public int getStartOffset() {
            synchronized (Highlighting.this) {
                checkVersion();
                
                if (sequences == null) {
                    throw new NoSuchElementException("Call moveNext() first."); //NOI18N
                }

                if (lines != null) {
                    return lines.get(linesIdx);
                } else {
                    throw new NoSuchElementException();
                }
            }
        }

        public int getEndOffset() {
            synchronized (Highlighting.this) {
                checkVersion();
                
                if (sequences == null) {
                    throw new NoSuchElementException("Call moveNext() first."); //NOI18N
                }

                if (lines != null) {
                    return lines.get(linesIdx + 1);
                } else {
                    throw new NoSuchElementException();
                }
            }
        }

        public AttributeSet getAttributes() {
            synchronized (Highlighting.this) {
                checkVersion();
                
                if (sequences == null) {
                    throw new NoSuchElementException("Call moveNext() first."); //NOI18N
                }

                if (lines != null) {
                    return ATTR;
                } else {
                    throw new NoSuchElementException();
                }
            }
        }
        
        private void checkVersion() {
            if (this.version != Highlighting.this.version) {
                throw new ConcurrentModificationException();
            }
        }
        
        private List<Integer> splitByLines(int sentenceStart, int sentenceEnd) {
            ArrayList<Integer> lines = new ArrayList<Integer>();
            int offset = sentenceStart;
            
            try {
                while (offset < sentenceEnd) {
                    Element lineElement = document.getDefaultRootElement().getElement(
                        document.getDefaultRootElement().getElementIndex(offset));

                    int rowStart = offset == sentenceStart ? offset : lineElement.getStartOffset();
                    int rowEnd = lineElement.getEndOffset();

                    String line = document.getText(rowStart, rowEnd - rowStart);
                    int idx = 0;
                    while (idx < line.length() && 
                        (line.charAt(idx) == ' ' || 
                        line.charAt(idx) == '\t' || 
                        line.charAt(idx) == '*'))
                    {
                        idx++;
                    }

                    if (rowStart + idx < rowEnd) {
                        lines.add(rowStart + idx);
                        lines.add(Math.min(rowEnd, sentenceEnd));
                    }

                    offset = rowEnd + 1;
                }
            } catch (BadLocationException e) {
                LOG.log(Level.WARNING, "Can't determine javadoc first sentence", e);
            }
            
            return lines.isEmpty() ? null : lines;
        }
    } // End of HSImpl class
}
