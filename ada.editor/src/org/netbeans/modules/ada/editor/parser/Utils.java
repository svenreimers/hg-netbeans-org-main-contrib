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

package org.netbeans.modules.ada.editor.parser;

/**
 * Based on org.netbeans.modules.php.editor.parser.Utils
 *
 * @author Andrea Lucarelli
 */
public class Utils {

    /**
     * This method is mainly used for debugging purpose.
     * 
     * @param id token id
     * @return text representation for the token
     */
    public static String getASTScannerTokenName(int id) {
        String name;
        switch (id) {
            case AdaASTSymbols.REVERSE:
                name = "REVERSE";
                break;
            case AdaASTSymbols.PROCEDURE:
                name = "PROCEDURE";
                break;
            case AdaASTSymbols.OF:
                name = "OF";
                break;
            case AdaASTSymbols.ABORT:
                name = "ABORT";
                break;
            case AdaASTSymbols.AMP:
                name = "AMP";
                break;
            case AdaASTSymbols.SEMICOLON:
                name = "SEMICOLON";
                break;
            case AdaASTSymbols.CASE:
                name = "CASE";
                break;
            case AdaASTSymbols.BASED_LITERAL:
                name = "BASED_LITERAL";
                break;
            case AdaASTSymbols.DECIMAL_LITERAL:
                name = "DECIMAL_LITERAL";
                break;
            case AdaASTSymbols.MOD:
                name = "MOD";
                break;
            case AdaASTSymbols.ARRAY:
                name = "ARRAY";
                break;
            case AdaASTSymbols.WITH:
                name = "WITH";
                break;
            case AdaASTSymbols.USE:
                name = "USE";
                break;
            case AdaASTSymbols.BAR:
                name = "BAR";
                break;
            case AdaASTSymbols.GENERIC:
                name = "GENERIC";
                break;
            case AdaASTSymbols.EXCEPTION:
                name = "EXCEPTION";
                break;
            case AdaASTSymbols.TAGGED:
                name = "TAGGED";
                break;
            case AdaASTSymbols.FOR:
                name = "FOR";
                break;
            case AdaASTSymbols.IDENTIFIER:
                name = "IDENTIFIER";
                break;
            case AdaASTSymbols.SLASH:
                name = "SLASH";
                break;
            case AdaASTSymbols.AT:
                name = "AT";
                break;
            case AdaASTSymbols.TYPE:
                name = "TYPE";
                break;
            case AdaASTSymbols.EQ:
                name = "EQ";
                break;
            case AdaASTSymbols.WHILE:
                name = "WHILE";
                break;
            case AdaASTSymbols.DELAY:
                name = "DELAY";
                break;
            case AdaASTSymbols.ENTRY:
                name = "ENTRY";
                break;
            case AdaASTSymbols.DELTA:
                name = "DELTA";
                break;
            case AdaASTSymbols.DIGITS:
                name = "DIGITS";
                break;
            case AdaASTSymbols.ABSTRACT:
                name = "ABSTRACT";
                break;
            case AdaASTSymbols.LOOP:
                name = "LOOP";
                break;
            case AdaASTSymbols.ACCESS:
                name = "ACCESS";
                break;
            case AdaASTSymbols.REQUEUE:
                name = "REQUEUE";
                break;
            case AdaASTSymbols.TASK:
                name = "TASK";
                break;
            case AdaASTSymbols.ABS:
                name = "ABS";
                break;
            case AdaASTSymbols.END:
                name = "END";
                break;
            case AdaASTSymbols.REM:
                name = "REM";
                break;
            case AdaASTSymbols.MINUS:
                name = "MINUS";
                break;
            case AdaASTSymbols.ASSIGNMENT:
                name = "ASSIGNMENT";
                break;
            case AdaASTSymbols.THEN:
                name = "THEN";
                break;
            case AdaASTSymbols.GOTO:
                name = "GOTO";
                break;
            case AdaASTSymbols.NEW:
                name = "NEW";
                break;
            case AdaASTSymbols.WHEN:
                name = "WHEN";
                break;
            case AdaASTSymbols.TERMINATE:
                name = "TERMINATE";
                break;
            case AdaASTSymbols.COLON:
                name = "COLON";
                break;
            case AdaASTSymbols.EOF:
                name = "EOF";
                break;
            case AdaASTSymbols.PLUS:
                name = "PLUS";
                break;
            case AdaASTSymbols.LTEQ:
                name = "LTEQ";
                break;
            case AdaASTSymbols.BEGIN:
                name = "BEGIN";
                break;
            case AdaASTSymbols.FUNCTION:
                name = "FUNCTION";
                break;
            case AdaASTSymbols.RECORD:
                name = "RECORD";
                break;
            case AdaASTSymbols.RANGE:
                name = "RANGE";
                break;
            case AdaASTSymbols.PROTECTED:
                name = "PROTECTED";
                break;
            case AdaASTSymbols.PRIVATE:
                name = "PRIVATE";
                break;
            case AdaASTSymbols.INEQ:
                name = "INEQ";
                break;
            case AdaASTSymbols.SEPARATE:
                name = "SEPARATE";
                break;
            case AdaASTSymbols.CONSTANT:
                name = "CONSTANT";
                break;
            case AdaASTSymbols.SELECT:
                name = "SELECT";
                break;
            case AdaASTSymbols.OTHERS:
                name = "OTHERS";
                break;
            case AdaASTSymbols.ALIASED:
                name = "ALIASED";
                break;
            case AdaASTSymbols.ELSE:
                name = "ELSE";
                break;
            case AdaASTSymbols.DO:
                name = "DO";
                break;
            case AdaASTSymbols.GT:
                name = "GT";
                break;
            case AdaASTSymbols.RENAMES:
                name = "RENAMES";
                break;
            case AdaASTSymbols.LIMITED:
                name = "LIMITED";
                break;
            case AdaASTSymbols.STAR:
                name = "STAR";
                break;
            case AdaASTSymbols.NULL:
                name = "NULL";
                break;
            case AdaASTSymbols.SUBTYPE:
                name = "SUBTYPE";
                break;
            case AdaASTSymbols.RETURN:
                name = "RETURN";
                break;
            case AdaASTSymbols.ALL:
                name = "ALL";
                break;
            case AdaASTSymbols.RAISE:
                name = "RAISE";
                break;
            case AdaASTSymbols.AND:
                name = "AND";
                break;
            case AdaASTSymbols.GTEQ:
                name = "GTEQ";
                break;
            case AdaASTSymbols.UNTIL:
                name = "UNTIL";
                break;
            case AdaASTSymbols.BODY:
                name = "BODY";
                break;
            case AdaASTSymbols.EXIT:
                name = "EXIT";
                break;
            case AdaASTSymbols.ACCEPT:
                name = "ACCEPT";
                break;
            case AdaASTSymbols.PRAGMA:
                name = "PRAGMA";
                break;
            case AdaASTSymbols.IS:
                name = "IS";
                break;
            case AdaASTSymbols.OR:
                name = "OR";
                break;
            case AdaASTSymbols.OUT:
                name = "OUT";
                break;
            case AdaASTSymbols.RPAREN:
                name = "RPAREN";
                break;
            case AdaASTSymbols.ELSIF:
                name = "ELSIF";
                break;
            case AdaASTSymbols.NOT:
                name = "NOT";
                break;
            case AdaASTSymbols.XOR:
                name = "XOR";
                break;
            case AdaASTSymbols.LPAREN:
                name = "LPAREN";
                break;
            case AdaASTSymbols.IN:
                name = "IN";
                break;
            case AdaASTSymbols.COMMA:
                name = "COMMA";
                break;
            case AdaASTSymbols.LT:
                name = "LT";
                break;
            case AdaASTSymbols.PACKAGE:
                name = "PACKAGE";
                break;
            case AdaASTSymbols.DOT:
                name = "DOT";
                break;
            case AdaASTSymbols.IF:
                name = "IF";
                break;
            case AdaASTSymbols.DECLARE:
                name = "DECLARE";
                break;
            default:
                name = "unknown";
        }
        return name;
    }

    public static String getSpaces(int length) {
        StringBuffer sb = new StringBuffer(length);
        for (int index = 0; index < length; index++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static int getRowStart(String text, int offset) {
        // Search backwards
        for (int i = offset - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i + 1;
            }
        }
        return 0;
    }

    public static int getRowEnd(String text, int offset) {
        int i = offset - 1;
        if (i < 0 ) {
            return 0;
        }
        for (; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i;
            }
        }
        return i;
    }
}
