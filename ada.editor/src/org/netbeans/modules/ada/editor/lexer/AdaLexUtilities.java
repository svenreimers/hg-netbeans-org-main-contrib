/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.editor.lexer;

import java.util.HashSet;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.Parser;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;


/**
 *
 * Based on org.netbeans.modules.ruby.lexer.LexUtilities (Tor Norbye)
 *
 * Utilities associated with lexing or analyzing the document at the
 * lexical level, unlike AstUtilities which is contains utilities
 * to analyze parsed information about a document.
 *
 * @author Andrea Lucarelli
 */
public class AdaLexUtilities {
    /**
     * Tokens that match a corresponding END statement. Even though while, unless etc.
     * can be statement modifiers, those luckily have different token ids so are not a problem
     * here.
     */
    private static final Set<TokenId> END_PAIRS = new HashSet<TokenId>();

    /**
     * Tokens that should cause indentation of the next line. This is true for all {@link #END_PAIRS},
     * but also includes tokens like "else" that are not themselves matched with end but also contribute
     * structure for indentation.
     *
     */
    private static final Set<TokenId> INDENT_WORDS = new HashSet<TokenId>();

    static {
        END_PAIRS.add(AdaTokenId.PROCEDURE);
        END_PAIRS.add(AdaTokenId.FUNCTION);
        END_PAIRS.add(AdaTokenId.FOR);
        END_PAIRS.add(AdaTokenId.WHILE);
        END_PAIRS.add(AdaTokenId.IF);
        END_PAIRS.add(AdaTokenId.PACKAGE);
        END_PAIRS.add(AdaTokenId.CASE);
        END_PAIRS.add(AdaTokenId.LOOP);

        INDENT_WORDS.addAll(END_PAIRS);
        // Add words that are not matched themselves with an "end",
        // but which also provide block structure to indented content
        // (usually part of a multi-keyword structure such as if-then-elsif-else-end if
        // where only the "if" is considered an end-pair.)
        INDENT_WORDS.add(AdaTokenId.ELSE);
        INDENT_WORDS.add(AdaTokenId.ELSIF);
        INDENT_WORDS.add(AdaTokenId.BEGIN);
    }

    private AdaLexUtilities() {
    }

    /**
     * For a possibly generated offset in an AST, return the corresponding lexing/true document offset
     */
    public static int getLexerOffset(Parser.Result result, int astOffset) {
        return result.getSnapshot().getOriginalOffset(astOffset);
    }

    public static OffsetRange getLexerOffsets(Parser.Result result, OffsetRange astRange) {
        int rangeStart = astRange.getStart();
        int start = result.getSnapshot().getOriginalOffset(rangeStart);
        if (start == rangeStart) {
            return astRange;
        } else if (start == -1) {
            return OffsetRange.NONE;
        } else {
            // Assumes the translated range maintains size
            return new OffsetRange(start, start + astRange.getLength());
        }
    }

    /**
     * Find the Ada token sequence (in case it's embedded in something else at the top level
     */
    @SuppressWarnings("unchecked")
    public static TokenSequence<?extends AdaTokenId> getAdaTokenSequence(BaseDocument doc, int offset) {
        TokenHierarchy<Document> th = TokenHierarchy.get((Document)doc);
        return getAdaTokenSequence(th, offset);
    }

    @SuppressWarnings("unchecked")
    public static TokenSequence<?extends AdaTokenId> getAdaTokenSequence(TokenHierarchy<Document> th, int offset) {
        TokenSequence<?extends AdaTokenId> ts = th.tokenSequence(AdaTokenId.language());
        return ts;
    }

    public static TokenSequence<?extends AdaTokenId> getPositionedSequence(BaseDocument doc, int offset) {
        TokenSequence<?extends AdaTokenId> ts = getAdaTokenSequence(doc, offset);

        if (ts != null) {
            try {
                ts.move(offset);
            } catch (AssertionError e) {
                DataObject dobj = (DataObject)doc.getProperty(Document.StreamDescriptionProperty);

                if (dobj != null) {
                    Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile()));
                }

                throw e;
            }

            if (!ts.moveNext() && !ts.movePrevious()) {
                return null;
            }

            return ts;
        }

        return null;
    }


    public static Token<?extends AdaTokenId> getToken(BaseDocument doc, int offset) {
        TokenSequence<?extends AdaTokenId> ts = getPositionedSequence(doc, offset);

        if (ts != null) {
            return ts.token();
        }

        return null;
    }

    public static char getTokenChar(BaseDocument doc, int offset) {
        Token<?extends AdaTokenId> token = getToken(doc, offset);

        if (token != null) {
            String text = token.text().toString();

            if (text.length() > 0) { // Usually true, but I could have gotten EOF right?

                return text.charAt(0);
            }
        }

        return 0;
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findHeredocEnd(TokenSequence<?extends AdaTokenId> ts,  Token<?extends AdaTokenId> startToken) {
//        // Look for the end of the given heredoc
//        String text = startToken.text().toString();
//        assert text.startsWith("<<");
//        text = text.substring(2);
//        if (text.startsWith("-")) {
//            text = text.substring(1);
//        }
//        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
//            text = text.substring(0, text.length()-2);
//        }
//        String textn = text+"\n";
//
//        while (ts.moveNext()) {
//            Token<?extends AdaTokenId> token = ts.token();
//            TokenId id = token.id();
//
//            if (id == AdaTokenId.STRING_END || id == AdaTokenId.QUOTED_STRING_END) {
//                String t = token.text().toString();
//                if (text.equals(t) || textn.equals(t)) {
//                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
//                }
//            }
//        }
//
        return OffsetRange.NONE;
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findHeredocBegin(TokenSequence<?extends AdaTokenId> ts,  Token<?extends AdaTokenId> endToken) {
//        // Look for the end of the given heredoc
//        String text = endToken.text().toString();
//        if (text.endsWith("\n")) {
//            text = text.substring(0, text.length()-1);
//        }
//        String textQuotes = "\"" + text + "\"";
//        String textSQuotes = "'" + text + "'";
//
//        while (ts.movePrevious()) {
//            Token<?extends AdaTokenId> token = ts.token();
//            TokenId id = token.id();
//
//            if (id == AdaTokenId.STRING_BEGIN || id == AdaTokenId.QUOTED_STRING_BEGIN) {
//                String t = token.text().toString();
//                String marker = null;
//                if (t.startsWith("<<-")) {
//                    marker = t.substring(3);
//                } else if (t.startsWith("<<")) {
//                    marker = t.substring(2);
//                }
//                if (marker != null && (text.equals(marker) || textQuotes.equals(marker) || textSQuotes.equals(marker))) {
//                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
//                }
//            }
//        }
//
        return OffsetRange.NONE;
    }

    /**
     * Search forwards in the token sequence until a token of type <code>down</code> is found
     */
    public static OffsetRange findFwd(BaseDocument doc, TokenSequence<?extends AdaTokenId> ts, TokenId up,
        TokenId down) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<?extends AdaTokenId> token = ts.token();
            TokenId id = token.id();

            if (id == up) {
                balance++;
            } else if (id == down) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    /**
     * Search backwards in the token sequence until a token of type <code>up</code> is found
     */
    public static OffsetRange findBwd(BaseDocument doc, TokenSequence<?extends AdaTokenId> ts, TokenId up,
        TokenId down) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<?extends AdaTokenId> token = ts.token();
            TokenId id = token.id();

            if (id == up) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance++;
            } else if (id == down) {
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    /**
     * Find the token that begins a block terminated by "end". This is a token
     * in the END_PAIRS array. Walk backwards and find the corresponding token.
     * It does not use indentation for clues since this could be wrong and be
     * precisely the reason why the user is using pair matching to see what's wrong.
     */
    public static OffsetRange findBegin(BaseDocument doc, TokenSequence<?extends AdaTokenId> ts) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<?extends AdaTokenId> token = ts.token();
            TokenId id = token.id();

            if (isBeginToken(id, doc, ts)) {
                // No matching dot for "do" used in conditionals etc.)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance--;
            } else if (id == AdaTokenId.END || id == AdaTokenId.END_CASE || id == AdaTokenId.END_IF || id == AdaTokenId.END_LOOP) {
                balance++;
            }
        }

        return OffsetRange.NONE;
    }

    public static OffsetRange findEnd(BaseDocument doc, TokenSequence<?extends AdaTokenId> ts) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<?extends AdaTokenId> token = ts.token();
            TokenId id = token.id();

            if (isBeginToken(id, doc, ts)) {
                balance--;
            } else if (id == AdaTokenId.END || id == AdaTokenId.END_CASE || id == AdaTokenId.END_IF || id == AdaTokenId.END_LOOP) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance++;
            }
        }

        return OffsetRange.NONE;
    }

    /**
     * Determine whether "loop" is an indent-token (e.g. matches an end) or if
     * it's simply a separator in while, for expressions)
     */
    public static boolean isEndmatchingLoop(BaseDocument doc, int offset) {
        // In the following case, loop is dominant:
        //     loop
        //        whatever
        //     end loop;
        //
        // However, not here:
        //     while true loop
        //        whatever
        //     end loop;
        //
        // In the second case, the end matches the while, but in the first case
        // the end matches the loop

        // Look at the first token of the current line
        try {
            int first = Utilities.getRowFirstNonWhite(doc, offset);
            if (first != -1) {
                Token<? extends AdaTokenId> token = getToken(doc, first);
                if (token != null) {
                    TokenId id = token.id();
                    if (id == AdaTokenId.WHILE || id == AdaTokenId.FOR) {
                        return false;
                    }
                }
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return true;
    }

    /**
     * Return true iff the given token is a token that should be matched
     * with a corresponding "end" token, such as "begin", "package"
     * etc.
     */
    public static boolean isBeginToken(TokenId id, BaseDocument doc, int offset) {
        if (id == AdaTokenId.LOOP) {
            return isEndmatchingLoop(doc, offset);
        }
        return END_PAIRS.contains(id);
    }

    /**
     * Return true iff the given token is a token that should be matched
     * with a corresponding "end" token, such as "begin", "package"
     * etc.
     */
    public static boolean isBeginToken(TokenId id, BaseDocument doc, TokenSequence<?extends AdaTokenId> ts) {
        if (id == AdaTokenId.LOOP) {
            return isEndmatchingLoop(doc, ts.offset());
        }
        return END_PAIRS.contains(id);
    }

    /**
     * Return true iff the given token is a token that indents its content,
     * such as the various begin tokens as well as "else", "when", etc.
     */
    public static boolean isIndentToken(TokenId id) {
        return INDENT_WORDS.contains(id);
    }

    /** Compute the balance of begin/end tokens on the line.
     * @param doc the document
     * @param offset The offset somewhere on the line
     * @param upToOffset If true, only compute the line balance up to the given offset (inclusive),
     *   and if false compute the balance for the whole line
     */
    public static int getBeginEndLineBalance(BaseDocument doc, int offset, boolean upToOffset) {
        try {
            int begin = Utilities.getRowStart(doc, offset);
            int end = upToOffset ? offset : Utilities.getRowEnd(doc, offset);

            TokenSequence<?extends AdaTokenId> ts = AdaLexUtilities.getAdaTokenSequence(doc, begin);
            if (ts == null) {
                return 0;
            }

            ts.move(begin);

            if (!ts.moveNext()) {
                return 0;
            }

            int balance = 0;

            do {
                Token<?extends AdaTokenId> token = ts.token();
                TokenId id = token.id();

                if (isBeginToken(id, doc, ts)) {
                    balance++;
                } else if (id == AdaTokenId.END || id == AdaTokenId.END_CASE || id == AdaTokenId.END_IF || id == AdaTokenId.END_LOOP) {
                    balance--;
                }
            } while (ts.moveNext() && (ts.offset() <= end));

            return balance;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return 0;
        }
    }

    /** Compute the balance of begin/end tokens on the line */
    public static int getLineBalance(BaseDocument doc, int offset, TokenId up, TokenId down) {
        try {
            int begin = Utilities.getRowStart(doc, offset);
            int end = Utilities.getRowEnd(doc, offset);

            TokenSequence<?extends AdaTokenId> ts = AdaLexUtilities.getAdaTokenSequence(doc, begin);
            if (ts == null) {
                return 0;
            }

            ts.move(begin);

            if (!ts.moveNext()) {
                return 0;
            }

            int balance = 0;

            do {
                Token<?extends AdaTokenId> token = ts.token();
                TokenId id = token.id();

                if (id == up) {
                    balance++;
                } else if (id == down) {
                    balance--;
                }
            } while (ts.moveNext() && (ts.offset() <= end));

            return balance;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return 0;
        }
    }

    /**
     * The same as braceBalance but generalized to any pair of matching
     * tokens.
     * @param open the token that increses the count
     * @param close the token that decreses the count
     */
    public static int getTokenBalance(BaseDocument doc, TokenId open, TokenId close, int offset)
        throws BadLocationException {
        TokenSequence<?extends AdaTokenId> ts = AdaLexUtilities.getAdaTokenSequence(doc, 0);
        if (ts == null) {
            return 0;
        }

        // XXX Why 0? Why not offset?
        ts.moveIndex(0);

        if (!ts.moveNext()) {
            return 0;
        }

        int balance = 0;

        do {
            Token t = ts.token();

            if (t.id() == open) {
                balance++;
            } else if (t.id() == close) {
                balance--;
            }
        } while (ts.moveNext());

        return balance;
    }

    /**
     * Return true iff the line for the given offset is a Ada comment line.
     * This will return false for lines that contain comments (even when the
     * offset is within the comment portion) but also contain code.
     */
    public static boolean isCommentOnlyLine(BaseDocument doc, int offset)
        throws BadLocationException {
        int begin = Utilities.getRowFirstNonWhite(doc, offset);

        if (begin == -1) {
            return false; // whitespace only
        }

        Token<? extends AdaTokenId> token = AdaLexUtilities.getToken(doc, begin);
        if (token != null) {
            return token.id() == AdaTokenId.COMMENT;
        }

        return false;
    }

    /**
     * Return the string at the given position, or null if none
     */
    @SuppressWarnings("unchecked")
    public static String getStringAt(int caretOffset, TokenHierarchy<Document> th) {
        TokenSequence<?extends AdaTokenId> ts = getAdaTokenSequence(th, caretOffset);

        if (ts == null) {
            return null;
        }

        ts.move(caretOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return null;
        }

        if (ts.offset() == caretOffset) {
            // We're looking at the offset to the RIGHT of the caret
            // and here I care about what's on the left
            if (!ts.movePrevious()) {
                return null;
            }
        }

        Token<?extends AdaTokenId> token = ts.token();

        if (token != null) {
            TokenId id = token.id();

            String string = null;

            // Skip over embedded Ada segments and literal strings until you find the beginning
            int segments = 0;

            while ((id == AdaTokenId.UNKNOWN_TOKEN) || (id == AdaTokenId.STRING_LITERAL)) {
                string = token.text().toString();
                segments++;
                if (!ts.movePrevious()) {
                    return null;
                }
                token = ts.token();
                id = token.id();
            }

            if (id == AdaTokenId.STRING_LITERAL) {
                if (segments == 1) {
                    return string;
                } else {
                    // Build up the String from the sequence
                    StringBuilder sb = new StringBuilder();

                    while (ts.moveNext()) {
                        token = ts.token();
                        id = token.id();

                        if ((id == AdaTokenId.UNKNOWN_TOKEN) || (id == AdaTokenId.STRING_LITERAL)) {
                            sb.append(token.text());
                        } else {
                            break;
                        }
                    }

                    return sb.toString();
                }
            }
        }

        return null;
    }

    /**
     * Check if the caret is inside a literal string that is associated with
     * a require statement.
     *
     * @return The offset of the beginning of the require string, or -1
     *     if the offset is not inside a require string.
     */
    public static int getRequireStringOffset(int caretOffset, TokenHierarchy<Document> th) {
        TokenSequence<?extends AdaTokenId> ts = getAdaTokenSequence(th, caretOffset);

        if (ts == null) {
            return -1;
        }

        ts.move(caretOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return -1;
        }

        if (ts.offset() == caretOffset) {
            // We're looking at the offset to the RIGHT of the caret
            // and here I care about what's on the left
            if (!ts.movePrevious()) {
                return -1;
            }
        }

        Token<?extends AdaTokenId> token = ts.token();

        if (token != null) {
            TokenId id = token.id();

            // Skip over embedded Ada segments and literal strings until you find the beginning
            while ((id == AdaTokenId.UNKNOWN_TOKEN) || (id == AdaTokenId.STRING_LITERAL)) {
                if (!ts.movePrevious()) {
                    return -1;
                }
                token = ts.token();
                id = token.id();
            }

            int stringStart = ts.offset() + token.length();

            if (id == AdaTokenId.STRING_LITERAL) {
                // Completion of literal strings within require calls
                while (ts.movePrevious()) {
                    token = ts.token();

                    id = token.id();

                    if ((id == AdaTokenId.WHITESPACE) || (id == AdaTokenId.LPAREN) ||
                            (id == AdaTokenId.STRING_LITERAL)) {
                        continue;
                    }

                    if (id == AdaTokenId.IDENTIFIER) {
                        String text = token.text().toString();

                        if (text.equals("require") || text.equals("load")) {
                            return stringStart;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                }
            }
        }

        return -1;
    }

    public static int getSingleQuotedStringOffset(int caretOffset, TokenHierarchy<Document> th) {
        return getLiteralStringOffset(caretOffset, th, AdaTokenId.STRING_LITERAL);
    }

    public static int getDoubleQuotedStringOffset(int caretOffset, TokenHierarchy<Document> th) {
        return getLiteralStringOffset(caretOffset, th, AdaTokenId.STRING_LITERAL);
    }

    /**
     * Determine if the caret is inside a literal string, and if so, return its starting
     * offset. Return -1 otherwise.
     */
    @SuppressWarnings("unchecked")
    private static int getLiteralStringOffset(int caretOffset, TokenHierarchy<Document> th,
        AdaTokenId begin) {
        TokenSequence<?extends AdaTokenId> ts = getAdaTokenSequence(th, caretOffset);

        if (ts == null) {
            return -1;
        }

        ts.move(caretOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return -1;
        }

        if (ts.offset() == caretOffset) {
            // We're looking at the offset to the RIGHT of the caret
            // and here I care about what's on the left
            if (!ts.movePrevious()) {
                return -1;
            }
        }

        Token<?extends AdaTokenId> token = ts.token();

        if (token != null) {
            TokenId id = token.id();

            // Skip over embedded Ada segments and literal strings until you find the beginning
            while ((id == AdaTokenId.UNKNOWN_TOKEN) || (id == AdaTokenId.STRING_LITERAL)) {
                if (!ts.movePrevious()) {
                    return -1;
                }
                token = ts.token();
                id = token.id();
            }

            if (id == begin) {
                if (!ts.moveNext()) {
                    return -1;
                }

                return ts.offset();
            }
        }

        return -1;
    }

    public static boolean isInsideQuotedString(BaseDocument doc, int offset) {
        TokenSequence<?extends AdaTokenId> ts = AdaLexUtilities.getAdaTokenSequence(doc, offset);

        if (ts == null) {
            return false;
        }

        ts.move(offset);

        if (ts.moveNext()) {
            Token<?extends AdaTokenId> token = ts.token();
            TokenId id = token.id();
            if (id == AdaTokenId.STRING_LITERAL) {
                return true;
            }
        }
        if (ts.movePrevious()) {
            Token<?extends AdaTokenId> token = ts.token();
            TokenId id = token.id();
            if (id == AdaTokenId.STRING_LITERAL) {
                return true;
            }
        }

        return false;
    }


    public static OffsetRange getCommentBlock(BaseDocument doc, int caretOffset) {
        // Check if the caret is within a comment, and if so insert a new
        // leaf "node" which contains the comment line and then comment block
        try {
            Token<?extends AdaTokenId> token = AdaLexUtilities.getToken(doc, caretOffset);

            if ((token != null) && (token.id() == AdaTokenId.COMMENT)) {
                // First add a range for the current line
                int begin = Utilities.getRowStart(doc, caretOffset);
                int end = Utilities.getRowEnd(doc, caretOffset);

                if (AdaLexUtilities.isCommentOnlyLine(doc, caretOffset)) {

                    while (begin > 0) {
                        int newBegin = Utilities.getRowStart(doc, begin - 1);

                        if ((newBegin < 0) || !AdaLexUtilities.isCommentOnlyLine(doc, newBegin)) {
                            begin = Utilities.getRowFirstNonWhite(doc, begin);
                            break;
                        }

                        begin = newBegin;
                    }

                    int length = doc.getLength();

                    while (true) {
                        int newEnd = Utilities.getRowEnd(doc, end + 1);

                        if ((newEnd >= length) || !AdaLexUtilities.isCommentOnlyLine(doc, newEnd)) {
                            end = Utilities.getRowLastNonWhite(doc, end)+1;
                            break;
                        }

                        end = newEnd;
                    }

                    if (begin < end) {
                        return new OffsetRange(begin, end);
                    }
                } else {
                    // It's just a line comment next to some code
                    TokenHierarchy<Document> th = TokenHierarchy.get((Document)doc);
                    int offset = token.offset(th);
                    return new OffsetRange(offset, offset + token.length());
                }
            }
            //else if (token != null && token.id() == AdaTokenId.DOCUMENTATION) {
            //    // Select the whole token block
            //    TokenHierarchy<BaseDocument> th = TokenHierarchy.get(doc);
            //    int begin = token.offset(th);
            //    int end = begin + token.length();
            //    return new OffsetRange(begin, end);
            //}
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return OffsetRange.NONE;
    }

    /**
     * Back up to the first space character prior to the given offset - as long as
     * it's on the same line!  If there's only leading whitespace on the line up
     * to the lex offset, return the offset itself
     */
    public static int findSpaceBegin(BaseDocument doc, int lexOffset) {
        TokenSequence ts = AdaLexUtilities.getAdaTokenSequence(doc, lexOffset);
        if (ts == null) {
            return lexOffset;
        }
        boolean allowPrevLine = false;
        int lineStart;
        try {
            lineStart = Utilities.getRowStart(doc, Math.min(lexOffset, doc.getLength()));
            int prevLast = lineStart-1;
            if (lineStart > 0) {
                prevLast = Utilities.getRowLastNonWhite(doc, lineStart-1);
                if (prevLast != -1) {
                    char c = doc.getText(prevLast, 1).charAt(0);
                    if (c == ',') {
                        // Arglist continuation? // TODO : check lexing
                        allowPrevLine = true;
                    }
                }
            }
            if (!allowPrevLine) {
                int firstNonWhite = Utilities.getRowFirstNonWhite(doc, lineStart);
                if (lexOffset <= firstNonWhite || firstNonWhite == -1) {
                    return lexOffset;
                }
            } else {
                // Make lineStart so small that Math.max won't cause any problems
                int firstNonWhite = Utilities.getRowFirstNonWhite(doc, lineStart);
                if (prevLast >= 0 && (lexOffset <= firstNonWhite || firstNonWhite == -1)) {
                    return prevLast+1;
                }
                lineStart = 0;
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
            return lexOffset;
        }
        ts.move(lexOffset);
        if (ts.moveNext()) {
            if (lexOffset > ts.offset()) {
                // We're in the middle of a token
                return Math.max((ts.token().id() == AdaTokenId.WHITESPACE) ?
                    ts.offset() : lexOffset, lineStart);
            }
            while (ts.movePrevious()) {
                Token token = ts.token();
                if (token.id() != AdaTokenId.WHITESPACE) {
                    return Math.max(ts.offset() + token.length(), lineStart);
                }
            }
        }

        return lexOffset;
    }

    /**
     * Get the rdoc documentation associated with the given node in the given document.
     * The node must have position information that matches the source in the document.
     */
    public static OffsetRange findRDocRange(BaseDocument baseDoc, int methodBegin) {
        int begin = methodBegin;
        try {
            if (methodBegin >= baseDoc.getLength()) {
                return OffsetRange.NONE;
            }

            // Search to previous lines, locate comments. Once we have a non-whitespace line that isn't
            // a comment, we're done

            int offset = Utilities.getRowStart(baseDoc, methodBegin);
            offset--;

            // Skip empty and whitespace lines
            while (offset >= 0) {
                // Find beginning of line
                offset = Utilities.getRowStart(baseDoc, offset);

                if (!Utilities.isRowEmpty(baseDoc, offset) &&
                        !Utilities.isRowWhite(baseDoc, offset)) {
                    break;
                }

                offset--;
            }

            if (offset < 0) {
                return OffsetRange.NONE;
            }

            while (offset >= 0) {
                // Find beginning of line
                offset = Utilities.getRowStart(baseDoc, offset);

                if (Utilities.isRowEmpty(baseDoc, offset) || Utilities.isRowWhite(baseDoc, offset)) {
                    // Empty lines not allowed within an rdoc
                    break;
                }

                // This is a comment line we should include
                int lineBegin = Utilities.getRowFirstNonWhite(baseDoc, offset);
                int lineEnd = Utilities.getRowLastNonWhite(baseDoc, offset) + 1;
                String line = baseDoc.getText(lineBegin, lineEnd - lineBegin);

                // TODO: should be changed
				// Tolerate "public", "private" and "protected" here --
                // Test::Unit::Assertions likes to put these in front of each
                // method.
                if (line.startsWith("#")) {
                    begin = lineBegin;
                } else if (line.startsWith("=end") &&
                        (lineBegin == Utilities.getRowStart(baseDoc, offset))) {
                    // It could be a =begin,=end document - see scanf.rb in Ada lib for example. Treat this differently.
                    int docBegin = findInlineDocStart(baseDoc, offset);
                    if (docBegin != -1) {
                        begin = docBegin;
                    } else {
                        return OffsetRange.NONE;
                    }
                } else if (line.equals("public") || line.equals("private") ||
                        line.equals("protected")) { // NOI18N
                                                    // Skip newlines back up to the comment
                    offset--;

                    while (offset >= 0) {
                        // Find beginning of line
                        offset = Utilities.getRowStart(baseDoc, offset);

                        if (!Utilities.isRowEmpty(baseDoc, offset) &&
                                !Utilities.isRowWhite(baseDoc, offset)) {
                            break;
                        }

                        offset--;
                    }

                    continue;
                } else {
                    // No longer in a comment
                    break;
                }

                // Previous line
                offset--;
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        if (methodBegin > begin) {
            return new OffsetRange(begin, methodBegin);
        } else {
            return OffsetRange.NONE;
        }
    }

    private static int findInlineDocStart(BaseDocument baseDoc, int offset) throws BadLocationException {
        // offset points to a line containing =end
        // Skip the =end list
        offset = Utilities.getRowStart(baseDoc, offset);
        offset--;

        // Search backwards in the document for the =begin (if any) and add all lines in reverse
        // order in between.
        while (offset >= 0) {
            // Find beginning of line
            offset = Utilities.getRowStart(baseDoc, offset);

            // This is a comment line we should include
            int lineBegin = offset;
            int lineEnd = Utilities.getRowEnd(baseDoc, offset);
            String line = baseDoc.getText(lineBegin, lineEnd - lineBegin);

            if (line.startsWith("=begin")) {
                // We're done!
                return lineBegin;
            }

            // Previous line
            offset--;
        }

        return -1;
    }
}
