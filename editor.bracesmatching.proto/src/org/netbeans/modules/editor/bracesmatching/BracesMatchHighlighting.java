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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.editor.bracesmatching;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import org.netbeans.spi.editor.highlighting.HighlightsChangeEvent;
import org.netbeans.spi.editor.highlighting.HighlightsChangeListener;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.WeakListeners;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatcherFactory;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatcher;
import org.netbeans.modules.editor.bracesmatching.spi.MatcherContext;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Vita Stejskal
 */
public class BracesMatchHighlighting extends AbstractHighlightsContainer 
    implements ChangeListener, PropertyChangeListener, HighlightsChangeListener, DocumentListener 
{
    private static final Logger LOG = Logger.getLogger(BracesMatchHighlighting.class.getName());
    
    private static final String BRACES_MATCH_COLORING = "highlight-match-brace"; //NOI18N    braces-match
//    private static final String BRACES_MISMATCH_COLORING = "braces-mismatch"; //NOI18N
    
    private final JTextComponent component;
    private final Document document;
    
    private Caret caret = null;
    private ChangeListener caretListener;
    
    private final OffsetsBag bag;
    private final AttributeSet bracesMatchColoring;
    private final AttributeSet bracesMismatchColoring;

    public BracesMatchHighlighting(JTextComponent component, Document document) {
        this.document = document;
        
        String mimeType = (String) document.getProperty("mimeType"); //NOI18N
        MimePath mimePath = MimePath.parse(mimeType);

        // Load the colorings
        FontColorSettings fcs = MimeLookup.getLookup(mimePath).lookup(FontColorSettings.class);
        this.bracesMatchColoring = fcs.getFontColors(BRACES_MATCH_COLORING);
        this.bracesMismatchColoring = SimpleAttributeSet.EMPTY; //fcs.getFontColors(BRACES_MISMATCH_COLORING);
        
        // Create and hook up the highlights bag
        this.bag = new OffsetsBag(document, false); // don't merge highlights
        this.bag.addHighlightsChangeListener(this);
        
        // Hook up the component
        this.component = component;
        this.component.addPropertyChangeListener(WeakListeners.propertyChange(this, this.component));

        // Hook up the caret
        this.caret = component.getCaret();
        if (this.caret != null) {
            this.caretListener = WeakListeners.change(this, this.caret);
            this.caret.addChangeListener(caretListener);
        }

        // Refresh the layer
        refresh();
    }

    // ------------------------------------------------
    // AbstractHighlightsContainer implementation
    // ------------------------------------------------
    
    public HighlightsSequence getHighlights(int startOffset, int endOffset) {
        return bag.getHighlights(startOffset, endOffset);
    }

    // ------------------------------------------------
    // HighlightsChangeListener implementation
    // ------------------------------------------------
    
    public void highlightChanged(HighlightsChangeEvent event) {
        fireHighlightsChange(event.getStartOffset(), event.getEndOffset());
// XXX: not neccessary
//        final int startOffset = event.getStartOffset();
//        final int endOffset = event.getEndOffset();
//        
//        SwingUtilities.invokeLater(new Runnable() {
//            private boolean inDocumentRender = false;
//            public void run() {
//                if (inDocumentRender) {
//                    fireHighlightsChange(startOffset, endOffset);
//                } else {
//                    inDocumentRender = true;
//                    document.render(this);
//                }
//            }
//        });
    }

    // ------------------------------------------------
    // DocumentListener implementation
    // ------------------------------------------------
    
    public void insertUpdate(DocumentEvent e) {
        refresh();
    }

    public void removeUpdate(DocumentEvent e) {
        refresh();
    }

    public void changedUpdate(DocumentEvent e) {
        refresh();
    }
    
    // ------------------------------------------------
    // ChangeListener implementation
    // ------------------------------------------------
    
    public void stateChanged(ChangeEvent e) {
        refresh();
    }

    // ------------------------------------------------
    // PropertyChangeListener implementation
    // ------------------------------------------------
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == null || "caret".equals(evt.getPropertyName())) { //NOI18N
            if (caret != null) {
                caret.removeChangeListener(caretListener);
                caretListener = null;
            }
            
            caret = component.getCaret();
            
            if (caret != null) {
                caretListener = WeakListeners.change(this, caret);
                caret.addChangeListener(caretListener);
            }
            
            refresh();
        }
    }

    // ------------------------------------------------
    // private implementation
    // ------------------------------------------------
    
    private void refresh() {
        Caret caret = this.caret;
        if (caret == null) {
            bag.clear();
        } else {
            MasterMatcher.get(document).highlight(
                caret.getDot(), 
                component.getClientProperty(MasterMatcher.PROP_ALLOWED_SEARCH_DIRECTION), 
                bag, 
                bracesMatchColoring, 
                bracesMismatchColoring
            );
        }
    }
    
    public static final class Factory implements HighlightsLayerFactory {
        public HighlightsLayer[] createLayers(Context context) {
            return new HighlightsLayer [] {
                HighlightsLayer.create(
                    "org-netbeans-modules-editor-bracesmatching-BracesMatchHighlighting", //NOI18N
                    ZOrder.SHOW_OFF_RACK, 
                    true, 
                    new BracesMatchHighlighting(context.getComponent(), context.getDocument())
                )
            };
        }
    } // End of Factory class
}
