/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.corba.idl.editor.coloring;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
* Syntax analyzes for IDL source files.
* Tokens and internal states are given below.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class IDLSyntax extends Syntax {

    // Spacial characters
    private static final char CHAR_SPACE = ' '; // No I18N
    private static final char CHAR_TAB = '\t';  // No I18N

    // Internal states
    private static final int ISI_ERROR = 1; // after carriage return
    private static final int ISI_TEXT = 2; // inside white space
    private static final int ISI_LINE_COMMENT = 4; // inside line comment //
    private static final int ISI_BLOCK_COMMENT = 5; // inside block comment /* ... */
    private static final int ISI_STRING = 6; // inside string constant
    private static final int ISI_STRING_A_BSLASH = 7; // inside string constant after backslash
    private static final int ISI_CHAR = 8; // inside char constant
    private static final int ISI_CHAR_A_BSLASH = 9; // inside char constant after backslash
    private static final int ISI_IDENTIFIER = 10; // inside identifier
    private static final int ISA_SLASH = 11; // slash char
    private static final int ISA_EQ = 12; // after '='
    private static final int ISA_GT = 13; // after '>'
    private static final int ISA_GTGT = 14; // after '>>'
    private static final int ISA_GTGTGT = 15; // after '>>>'
    private static final int ISA_LT = 16; // after '<'
    private static final int ISA_LTLT = 17; // after '<<'
    private static final int ISA_PLUS = 18; // after '+'
    private static final int ISA_MINUS = 19; // after '-'
    private static final int ISA_STAR = 20; // after '*'
    private static final int ISA_STAR_I_BLOCK_COMMENT = 21; // after '*'
    private static final int ISA_PIPE = 22; // after '|'
    private static final int ISA_PERCENT = 23; // after '%'
    private static final int ISA_AND = 24; // after '&'
    private static final int ISA_XOR = 25; // after '^'
    private static final int ISA_EXCLAMATION = 26; // after '!'
    private static final int ISA_ZERO = 27; // after '0'
    private static final int ISI_INT = 28; // integer number
    private static final int ISI_OCTAL = 29; // octal number
    private static final int ISI_FLOAT = 30; // float number
    private static final int ISI_FLOAT_EXP = 31; // float number
    private static final int ISI_HEX = 32; // hex number
    private static final int ISA_DOT = 33; // after '.'
    private static final int ISA_HASH = 34; // right after '#'
    private static final int ISA_HASH_CONT = 35; // continuation of preprocessor dirrective
    private static final int ISA_DIRECTIVE = 36; // after directive
    private static final int ISI_HERROR = 37; // after hash got error

    private int pstartOff;
    
    public IDLSyntax() {
        tokenContextPath = IDLTokenContext.contextPath;
    }
    /*
      public int nextToken() {
      int tokenID = super.nextToken();
      System.out.println("tokenID=" + getTokenName(tokenID));
      return tokenID;
      }
    */  

    public boolean isIdentifierPart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    protected TokenID parseToken() {
        char actChar;

        while(offset < stopOffset) {
            actChar = buffer[offset];

            switch (state) {
            case INIT:
                switch (actChar) {
                case '\n':
                    offset++;
                    return IDLTokenContext.EOL;
                case ' ':
                case '\t':
                    state = ISI_TEXT;
                    break;
                case '"':
                    state = ISI_STRING;
                    break;
                case '\'':
                    state = ISI_CHAR;
                    break;
                case '/':
                    state = ISA_SLASH;
                    break;
                case '=':
                    state = ISA_EQ;
                    break;
                case '>':
                    state = ISA_GT;
                    break;
                case '<':
                    state = ISA_LT;
                    break;
                case '+':
                    state = ISA_PLUS;
                    break;
                case '-':
                    state = ISA_MINUS;
                    break;
                case '*':
                    state = ISA_STAR;
                    break;
                case '|':
                    state = ISA_PIPE;
                    break;
                case '%':
                    state = ISA_PERCENT;
                    break;
                case '&':
                    state = ISA_AND;
                    break;
                case '^':
                    state = ISA_XOR;
                    break;
                case '!':
                    state = ISA_EXCLAMATION;
                    break;
                case '0':
                    state = ISA_ZERO;
                    break;
                case '.':
                    state = ISA_DOT;
                    break;
                case '#':
                    state = ISA_HASH;
                    break;
                default:
                    if (actChar >= '1' && actChar <= '9') { // '0' already handled
                        state = ISI_INT;
                        break;
                    }

                    if (Character.isJavaIdentifierStart(actChar)) { // identifier
                        state = ISI_IDENTIFIER;
                        break;
                    }

                    // everything else is an operator
                    offset++;
                    return IDLTokenContext.OPERATOR;
                }
                break;

            case ISI_ERROR:
                switch (actChar) {
                case ' ':
                case '\t':
                case '\n':
                    state = INIT;
                    return IDLTokenContext.ERROR;
                }
                break;

            case ISI_TEXT: // white space
                if (actChar != ' ' && actChar != '\t') {
                    state = INIT;
                    return IDLTokenContext.TEXT;
                }
                break;

            case ISI_LINE_COMMENT:
                switch (actChar) {
                case '\n':
                    state = INIT;
                    return IDLTokenContext.LINE_COMMENT;
                }
                break;

            case ISI_BLOCK_COMMENT:
                switch (actChar) {
                case '\n':
                    if (offset == tokenOffset) { // only '\n'
                        offset++;
                        return IDLTokenContext.EOL; // stay in ISI_BLOCK_COMMENT state for next line
                    } else { // return comment token to qualify for previous if()
                        return IDLTokenContext.BLOCK_COMMENT;
                    }
                case '*':
                    state = ISA_STAR_I_BLOCK_COMMENT;
                    break;
                }
                break;

            case ISI_STRING:
                switch (actChar) {
                case '\\':
                    state = ISI_STRING_A_BSLASH;
                    break;
                case '\n':
                    state = INIT;
                    return IDLTokenContext.STRING_LITERAL;
                case '"':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.STRING_LITERAL;
                }
                break;

            case ISI_STRING_A_BSLASH:
                switch (actChar) {
                case '"':
                case '\\':
                    break;
                default:
                    offset--;
                    break;
                }
                state = ISI_STRING;
                break;

            case ISI_CHAR:
                switch (actChar) {
                case '\\':
                    state = ISI_CHAR_A_BSLASH;
                    break;
                case '\n':
                    state = INIT;
                    return IDLTokenContext.CHAR_LITERAL;
                case '\'':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.CHAR_LITERAL;
                }
                break;

            case ISI_CHAR_A_BSLASH:
                switch (actChar) {
                case '\'':
                case '\\':
                    break;
                default:
                    offset--;
                    break;
                }
                state = ISI_CHAR;
                break;

            case ISI_IDENTIFIER:
                if (!(Character.isJavaIdentifierPart(actChar))) {
                    state = INIT;
                    TokenID kwd = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                    return (kwd != null) ? kwd : IDLTokenContext.IDENTIFIER;
                }
                break;

            case ISA_SLASH:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                case '/':
                    state = ISI_LINE_COMMENT;
                    break;
                case '*':
                    state = ISI_BLOCK_COMMENT;
                    break;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                break;

            case ISA_EQ:
                switch (actChar) {
                case '=':
                    offset++;
                    return  IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_GT:
                switch (actChar) {
                case '>':
                    state = ISA_GTGT;
                    break;
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                break;

            case ISA_GTGT:
                switch (actChar) {
                case '>':
                    state = ISA_GTGTGT;
                    break;
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                break;

            case ISA_GTGTGT:
                switch (actChar) {
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;


            case ISA_LT:
                switch (actChar) {
                case '<':
                    state = ISA_LTLT;
                    break;
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                break;

            case ISA_LTLT:
                switch (actChar) {
                case '<':
                    state = ISI_ERROR;
                    break;
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                break;

            case ISA_PLUS:
                switch (actChar) {
                case '+':
                    // let it flow to '='
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_MINUS:
                switch (actChar) {
                case '-':
                    // let it flow to '='
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_STAR:
                switch (actChar) {
                case '=':
                    offset++;
                    return IDLTokenContext.OPERATOR;
                case '/':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.ERROR; // '*/' outside comment
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_STAR_I_BLOCK_COMMENT:
                switch (actChar) {
                case '/':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.BLOCK_COMMENT;
                default:
                    offset--;
                    state = ISI_BLOCK_COMMENT;
                    break;
                }
                break;

            case ISA_PIPE:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_PERCENT:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_AND:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_XOR:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_EXCLAMATION:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                default:
                    state = INIT;
                    return IDLTokenContext.OPERATOR;
                }
                // break;

            case ISA_ZERO:
                switch (actChar) {
                case '.':
                    state = ISI_FLOAT;
                    break;
                case 'x':
                case 'X':
                    state = ISI_HEX;
                    break;
                case 'l':
                case 'L':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.LONG_LITERAL;
                case 'f':
                case 'F':
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.FLOAT_LITERAL;
                case 'e':
                case 'E':
                    state = ISI_FLOAT_EXP;
                    break;
                case '8': // it's error to have '8' and '9' in octal number
                case '9':
                    state = ISI_ERROR;
                    break;
                default:
                    if (actChar >= '0' && actChar <= '7') {
                        state = ISI_OCTAL;
                        break;
                    }
                    state = INIT;
                    return IDLTokenContext.INT_LITERAL;
                }
                break;

            case ISI_INT:
                switch (actChar) {
                case 'l':
                case 'L':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.LONG_LITERAL;
                case 'f':
                case 'F':
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.FLOAT_LITERAL;
                case 'e':
                case 'E':
                    state = ISI_FLOAT_EXP;
                    break;
                case '.':
                    state = ISI_FLOAT;
                    break;
                default:
                    if (!(actChar >= '0' && actChar <= '9')) {
                        state = INIT;
                        return IDLTokenContext.INT_LITERAL;
                    }
                }
                break;

            case ISI_OCTAL:
                if (!(actChar >= '0' && actChar <= '7')) {

                    state = INIT;
                    return IDLTokenContext.OCTAL_LITERAL;
                }
                break;

            case ISI_FLOAT:
                switch (actChar) {
                case 'f':
                case 'F':
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.FLOAT_LITERAL;
                case 'e':
                case 'E':
                    state = ISI_FLOAT_EXP;
                    break;
                default:
                    if (!((actChar >= '0' && actChar <= '9')
                            || actChar == '.')) {

                        state = INIT;
                        return IDLTokenContext.FLOAT_LITERAL;
                    }
                }
                break;

            case ISI_FLOAT_EXP:
                switch (actChar) {
                case 'f':
                case 'F':
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return IDLTokenContext.FLOAT_LITERAL;
                default:
                    if (!((actChar >= '0' && actChar <= '9')
                            || actChar == '-' || actChar == '+')) {
                        state = INIT;
                        return IDLTokenContext.FLOAT_LITERAL;
                    }
                }
                break;

            case ISI_HEX:
                if (!((actChar >= 'a' && actChar <= 'f')
                        || (actChar >= 'A' && actChar <= 'F')
                        || (actChar >= '0' && actChar <= '9'))) {

                    state = INIT;
                    return IDLTokenContext.HEX_LITERAL;
                }
                break;

            case ISA_DOT:
                if (actChar >= '0' && actChar <= '9') {
                    state = ISI_FLOAT;
                    break;
                }
                state = INIT;
                return IDLTokenContext.OPERATOR;

            case ISA_HASH:
                if (actChar == CHAR_SPACE || actChar == CHAR_TAB) {
                    break;
                }
                else if (Character.isJavaIdentifierPart(actChar)) {
                    pstartOff = offset;
                    state = ISA_HASH_CONT;
                    break;
                }
                else {
                    state = ISI_HERROR; // directive error
                    return IDLTokenContext.ERROR;
                }
            case ISA_HASH_CONT:
                if (Character.isJavaIdentifierPart(actChar)) {
                    break; // continue possible directive string
                }
                if (matchDirective()) { // directive found
                    state = ISA_DIRECTIVE;
                    return IDLTokenContext.DIRECTIVE;
                }
                switch (actChar) {
                case '\n':
                    state = INIT;
                    return IDLTokenContext.TEXT;
                }
                state = ISI_HERROR; // directive error
                return IDLTokenContext.ERROR;
                
            case ISA_DIRECTIVE:
                switch (actChar) {
                case '\n':
                    state = INIT;
                    return IDLTokenContext.DIRECTIVE;
                }
                break;

            case ISI_HERROR:
                switch (actChar) {
                case '\n':
                    state = INIT;
                    return IDLTokenContext.ERROR;
                }
                break;

            } // end of switch(state)

            offset = ++offset;
        } // end of while(offset...)

        /** At this stage there's no more text in the scanned buffer.
         * Scanner first checks whether this is completely the last
         * available buffer.
         */

        if (lastBuffer) {
            switch(state) {
                case ISI_IDENTIFIER:
                    TokenID kwd = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                    return (kwd != null) ? kwd : IDLTokenContext.IDENTIFIER;
                case ISA_HASH:
                    return IDLTokenContext.TEXT;
                case ISA_HASH_CONT:
                    return matchDirective() ? IDLTokenContext.DIRECTIVE : IDLTokenContext.TEXT;
                case ISA_DOT:
                case ISA_SLASH:
                case ISA_EQ:
                case ISA_GT:
                case ISA_GTGT:
                case ISA_GTGTGT:
                case ISA_LT:
                case ISA_LTLT:
                case ISA_PLUS:
                case ISA_MINUS:
                case ISA_STAR:
                case ISA_PIPE:
                case ISA_PERCENT:
                case ISA_AND:
                case ISA_XOR:
                case ISA_EXCLAMATION:
                    return IDLTokenContext.OPERATOR;
                case ISI_ERROR:
                case ISI_HERROR:
                    return IDLTokenContext.ERROR;
                case ISI_TEXT:
                    return IDLTokenContext.TEXT;
                case ISI_LINE_COMMENT:
                    return IDLTokenContext.LINE_COMMENT;
                case ISI_BLOCK_COMMENT:
                case ISA_STAR_I_BLOCK_COMMENT:
                    return IDLTokenContext.BLOCK_COMMENT;
                case ISI_STRING:
                case ISI_STRING_A_BSLASH:
                    return IDLTokenContext.STRING_LITERAL;
                case ISI_CHAR:
                case ISI_CHAR_A_BSLASH:
                    return IDLTokenContext.CHAR_LITERAL;
                case ISA_ZERO:
                case ISI_INT:
                    return IDLTokenContext.INT_LITERAL;
                case ISI_OCTAL:
                    return IDLTokenContext.OCTAL_LITERAL;
                case ISI_FLOAT:
                case ISI_FLOAT_EXP:
                    return IDLTokenContext.FLOAT_LITERAL;
                case ISI_HEX:
                    return IDLTokenContext.HEX_LITERAL;
                case ISA_DIRECTIVE:
                    return IDLTokenContext.DIRECTIVE;
            }
        }

        /* At this stage there's no more text in the scanned buffer, but
         * this buffer is not the last so the scan will continue on another buffer.
         * The scanner tries to minimize the amount of characters
         * that will be prescanned in the next buffer.
         */

        return null;

    }

    public static TokenID matchKeyword(char[] buffer, int offset, int len) {
        if (len > 11)
            return null;
        if (len <= 1)
            return null;
        switch (buffer[offset++]) {
        case 'F':
            return (len == 5
                    && buffer[offset++] == 'A'
                    && buffer[offset++] == 'L'
                    && buffer[offset++] == 'S'
                    && buffer[offset++] == 'E')
                   ? IDLTokenContext.FALSE : null;
        case 'O':
            return (len == 6
                    && buffer[offset++] == 'b'
                    && buffer[offset++] == 'j'
                    && buffer[offset++] == 'e'
                    && buffer[offset++] == 'c'
                    && buffer[offset++] == 't')
                   ? IDLTokenContext.OBJECT : null;
        case 'T':
            return (len == 4
                    && buffer[offset++] == 'R'
                    && buffer[offset++] == 'U'
                    && buffer[offset++] == 'E')
                   ? IDLTokenContext.TRUE : null;
        case 'V':
            return (len == 9
                    && buffer[offset++] == 'a'
                    && buffer[offset++] == 'l'
                    && buffer[offset++] == 'u'
                    && buffer[offset++] == 'e'
                    && buffer[offset++] == 'B'
                    && buffer[offset++] == 'a'
                    && buffer[offset++] == 's'
                    && buffer[offset++] == 'e')
                   ? IDLTokenContext.VALUEBASE : null;
        case 'a':
            if (len <= 2)
                return null;
            switch (buffer[offset++]) {
            case 'b':
                return (len == 8
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.ABSTRACT : null;
            case 'n':
                return (len == 3
                        && buffer[offset++] == 'y')
                       ? IDLTokenContext.ANY : null;
            case 't':
                return (len == 9
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'b'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.ATTRIBUTE : null;
            default:
                return null;
            }
        case 'b':
            return (len == 7
                    && buffer[offset++] == 'o'
                    && buffer[offset++] == 'o'
                    && buffer[offset++] == 'l'
                    && buffer[offset++] == 'e'
                    && buffer[offset++] == 'a'
                    && buffer[offset++] == 'n')
                   ? IDLTokenContext.BOOLEAN : null;
        case 'c':
            if (len <= 3)
                return null;
            switch (buffer[offset++]) {
            case 'a':
                return (len == 4
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.CASE : null;
            case 'h':
                return (len == 4
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'r')
                       ? IDLTokenContext.CHAR : null;
            case 'o':
                if (len <= 4)
                    return null;
                if (buffer[offset++] != 'n')
                    return null;
                switch (buffer[offset++]) {
                case 's':
                    return (len == 5
                            && buffer[offset++] == 't')
                           ? IDLTokenContext.CONST : null;
                case 't':
                    return (len == 7
                            && buffer[offset++] == 'e'
                            && buffer[offset++] == 'x'
                            && buffer[offset++] == 't')
                           ? IDLTokenContext.CONTEXT : null;
                default:
                    return null;
                }
            case 'u':
                return (len == 6
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'm')
                       ? IDLTokenContext.CUSTOM : null;
            default:
                return null;
            }
        case 'd':
            if (len <= 5)
                return null;
            switch (buffer[offset++]) {
            case 'e':
                return (len == 7
                        && buffer[offset++] == 'f'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.DEFAULT : null;
            case 'o':
                return (len == 6
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'b'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.DOUBLE : null;
            default:
                return null;
            }
        case 'e':
            if (len <= 3)
                return null;
            switch (buffer[offset++]) {
            case 'n':
                return (len == 4
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'm')
                       ? IDLTokenContext.ENUM : null;
            case 'x':
                return (len == 9
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'p'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'n')
                       ? IDLTokenContext.EXCEPTION : null;
            default:
                return null;
            }
        case 'f':
            if (len <= 4)
                return null;
            switch (buffer[offset++]) {
            case 'a':
                return (len == 7
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'y')
                       ? IDLTokenContext.FACTORY : null;
            case 'i':
                return (len == 5
                        && buffer[offset++] == 'x'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'd')
                       ? IDLTokenContext.FIXED : null;
            case 'l':
                return (len == 5
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.FLOAT : null;
            default:
                return null;
            }
        case 'i':
            if (buffer[offset++] != 'n')
                return null;
            if (len == 2)
                return IDLTokenContext.IN;
            if (len <= 4)
                return null;
            switch (buffer[offset++]) {
            case 'o':
                return (len == 5
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.INOUT : null;
            case 't':
                return (len == 9
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'f'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.INTERFACE : null;
            default:
                return null;
            }
        case 'l':
            return (len == 4
                    && buffer[offset++] == 'o'
                    && buffer[offset++] == 'n'
                    && buffer[offset++] == 'g')
                   ? IDLTokenContext.LONG : null;
        case 'm':
            return (len == 6
                    && buffer[offset++] == 'o'
                    && buffer[offset++] == 'd'
                    && buffer[offset++] == 'u'
                    && buffer[offset++] == 'l'
                    && buffer[offset++] == 'e')
                   ? IDLTokenContext.MODULE : null;
        case 'n':
            return (len == 6
                    && buffer[offset++] == 'a'
                    && buffer[offset++] == 't'
                    && buffer[offset++] == 'i'
                    && buffer[offset++] == 'v'
                    && buffer[offset++] == 'e')
                   ? IDLTokenContext.NATIVE : null;
        case 'o':
            if (len <= 2)
                return null;
            switch (buffer[offset++]) {
            case 'c':
                return (len == 5
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.OCTET : null;
            case 'n':
                return (len == 6
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'w'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'y')
                       ? IDLTokenContext.ONEWAY : null;
            case 'u':
                return (len == 3
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.OUT : null;
            default:
                return null;
            }
        case 'p':
            if (len <= 5)
                return null;
            switch (buffer[offset++]) {
            case 'r':
                return (len == 7
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'v'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.PRIVATE : null;
            case 'u':
                return (len == 6
                        && buffer[offset++] == 'b'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'c')
                       ? IDLTokenContext.PUBLIC : null;
            default:
                return null;
            }
        case 'r':
            if (len <= 5)
                return null;
            switch (buffer[offset++]) {
            case 'a':
                return (len == 6
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 's')
                       ? IDLTokenContext.RAISES : null;
            case 'e':
                return (len == 8
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'd'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'y')
                       ? IDLTokenContext.READONLY : null;
            default:
                return null;
            }
        case 's':
            if (len <= 4)
                return null;
            switch (buffer[offset++]) {
            case 'e':
                return (len == 8
                        && buffer[offset++] == 'q'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.SEQUENCE : null;
            case 'h':
                return (len == 5
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 't')
                       ? IDLTokenContext.SHORT : null;
            case 't':
                if (len <= 5)
                    return null;
                if (buffer[offset++] != 'r')
                    return null;
                switch (buffer[offset++]) {
                case 'i':
                    return (len == 6
                            && buffer[offset++] == 'n'
                            && buffer[offset++] == 'g')
                           ? IDLTokenContext.STRING : null;
                case 'u':
                    return (len == 6
                            && buffer[offset++] == 'c'
                            && buffer[offset++] == 't')
                           ? IDLTokenContext.STRUCT : null;
                default:
                    return null;
                }
            case 'u':
                return (len == 8
                        && buffer[offset++] == 'p'
                        && buffer[offset++] == 'p'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 's')
                       ? IDLTokenContext.SUPPORTS : null;
            case 'w':
                return (len == 6
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'h')
                       ? IDLTokenContext.SWITCH : null;
            default:
                return null;
            }
        case 't':
            if (len <= 6)
                return null;
            switch (buffer[offset++]) {
            case 'r':
                return (len == 11
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'b'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.TRUNCATABLE : null;
            case 'y':
                return (len == 7
                        && buffer[offset++] == 'p'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'd'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'f')
                       ? IDLTokenContext.TYPEDEF : null;
            default:
                return null;
            }
        case 'u':
            if (len <= 4)
                return null;
            if (buffer[offset++] != 'n')
                return null;
            switch (buffer[offset++]) {
            case 'i':
                return (len == 5
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'n')
                       ? IDLTokenContext.UNION : null;
            case 's':
                return (len == 8
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'g'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'd')
                       ? IDLTokenContext.UNSIGNED : null;
            default:
                return null;
            }
        case 'v':
            if (len <= 3)
                return null;
            switch (buffer[offset++]) {
            case 'a':
                return (len == 9
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'y'
                        && buffer[offset++] == 'p'
                        && buffer[offset++] == 'e')
                       ? IDLTokenContext.VALUETYPE : null;
            case 'o':
                return (len == 4
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'd')
                       ? IDLTokenContext.VOID : null;
            default:
                return null;
            }
        case 'w':
            if (len <= 4)
                return null;
            switch (buffer[offset++]) {
            case 'c':
                return (len == 5
                        && buffer[offset++] == 'h'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'r')
                       ? IDLTokenContext.WCHAR : null;
            case 's':
                return (len == 7
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'g')
                       ? IDLTokenContext.WSTRING : null;
            default:
                return null;
            }
        case '_':
            if (len != 8 || buffer[offset++] != '_')
                return null;
            switch (buffer[offset++]) {
            case 'L':
                return (buffer[offset++] == 'I'
                        && buffer[offset++] == 'N'
                        && buffer[offset++] == 'E'
                        
                        && buffer[offset++] == '_'
                        && buffer[offset++] == '_')
                       ? IDLTokenContext.DIRECTIVE : null;
            case 'F':
                return (buffer[offset++] == 'I'
                        && buffer[offset++] == 'L'
                        && buffer[offset++] == 'E'
                        
                        && buffer[offset++] == '_'
                        && buffer[offset++] == '_')
                       ? IDLTokenContext.DIRECTIVE : null;
                       
            default:
                
                return null;
                
            }
        default:
            return null;
        }
    }

    private boolean matchDirective () {
        if (offset - pstartOff > 7)
            return false;
        if (offset - pstartOff < 2)
            return false;
        switch (buffer[pstartOff]) {
            case 'd':
                return offset - pstartOff == 6
                && buffer[pstartOff + 1] == 'e'
                && buffer[pstartOff + 2] == 'f'
                && buffer[pstartOff + 3] == 'i'
                && buffer[pstartOff + 4] == 'n'
                && buffer[pstartOff + 5] == 'e';
            case 'e':
                switch (buffer[pstartOff + 1]) {
                    case 'l':
                        if (offset - pstartOff <= 2)
                            return false;
                        switch (buffer[pstartOff + 2]) {
                            case 'i':
                                return offset - pstartOff == 4
                                && buffer[pstartOff + 3] == 'f';
                            case 's':
                                return offset - pstartOff == 4
                                && buffer[pstartOff + 3] == 'e';
                        }
                    case 'n':
                        return offset - pstartOff == 5
                        && buffer[pstartOff + 2] == 'd'
                        && buffer[pstartOff + 3] == 'i'
                        && buffer[pstartOff + 4] == 'f';
                }
            case 'i':
                switch (buffer[pstartOff + 1]) {
                    case 'f':
                        if (offset - pstartOff <= 2)
                            return true;
                        switch (buffer[pstartOff + 2]) {
                            case 'd':
                                return offset - pstartOff == 5
                                && buffer[pstartOff + 3] == 'e'
                                && buffer[pstartOff + 4] == 'f';
                            case 'n':
                                return offset - pstartOff == 6
                                && buffer[pstartOff + 3] == 'd'
                                && buffer[pstartOff + 4] == 'e'
                                && buffer[pstartOff + 5] == 'f';
                            default:
                                return false;
                        }
                    case 'n':
                        return offset - pstartOff == 7
                        && buffer[pstartOff + 2] == 'c'
                        && buffer[pstartOff + 3] == 'l'
                        && buffer[pstartOff + 4] == 'u'
                        && buffer[pstartOff + 5] == 'd'
                        && buffer[pstartOff + 6] == 'e';
                    default:
                        return false;
                }
            case 'p':
                return offset - pstartOff == 6
                && buffer[pstartOff + 1] == 'r'
                && buffer[pstartOff + 2] == 'a'
                && buffer[pstartOff + 3] == 'g'
                && buffer[pstartOff + 4] == 'm'
                && buffer[pstartOff + 5] == 'a';
            case 'u':
                return offset - pstartOff == 5
                && buffer[pstartOff + 1] == 'n'
                && buffer[pstartOff + 2] == 'd'
                && buffer[pstartOff + 3] == 'e'
                && buffer[pstartOff + 4] == 'f';
            default:
                return false;
        }
    }

    public String getStateName(int stateNumber) {
        switch(stateNumber) {
        case ISI_ERROR:
            return "ISI_ERROR";
        case ISI_TEXT:
            return "ISI_TEXT";
        case ISI_LINE_COMMENT:
            return "ISI_LINE_COMMENT";
        case ISI_BLOCK_COMMENT:
            return "ISI_BLOCK_COMMENT";
        case ISI_STRING:
            return "ISI_STRING";
        case ISI_STRING_A_BSLASH:
            return "ISI_STRING_A_BSLASH";
        case ISI_CHAR:
            return "ISI_CHAR";
        case ISI_CHAR_A_BSLASH:
            return "ISI_CHAR_A_BSLASH";
        case ISI_IDENTIFIER:
            return "ISI_IDENTIFIER";
        case ISA_SLASH:
            return "ISA_SLASH";
        case ISA_EQ:
            return "ISA_EQ";
        case ISA_GT:
            return "ISA_GT";
        case ISA_GTGT:
            return "ISA_GTGT";
        case ISA_GTGTGT:
            return "ISA_GTGTGT";
        case ISA_LT:
            return "ISA_LT";
        case ISA_LTLT:
            return "ISA_LTLT";
        case ISA_PLUS:
            return "ISA_PLUS";
        case ISA_MINUS:
            return "ISA_MINUS";
        case ISA_STAR:
            return "ISA_STAR";
        case ISA_STAR_I_BLOCK_COMMENT:
            return "ISA_STAR_I_BLOCK_COMMENT";
        case ISA_PIPE:
            return "ISA_PIPE";
        case ISA_PERCENT:
            return "ISA_PERCENT";
        case ISA_AND:
            return "ISA_AND";
        case ISA_XOR:
            return "ISA_XOR";
        case ISA_EXCLAMATION:
            return "ISA_EXCLAMATION";
        case ISA_ZERO:
            return "ISA_ZERO";
        case ISI_INT:
            return "ISI_INT";
        case ISI_OCTAL:
            return "ISI_OCTAL";
        case ISI_FLOAT:
            return "ISI_FLOAT";
        case ISI_FLOAT_EXP:
            return "ISI_FLOAT_EXP";
        case ISI_HEX:
            return "ISI_HEX";
        case ISA_DOT:
            return "ISA_DOT";

        default:
            return super.getStateName(stateNumber);
        }
    }

}

/*
 * <<Log>>
 *  4    Jaga      1.2.1.0     3/15/00  Miloslav Metelka Structural change
 *  3    Gandalf   1.2         2/8/00   Karel Gardas    
 *  2    Gandalf   1.1         12/28/99 Miloslav Metelka Structural change and 
 *       some renamings
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */
