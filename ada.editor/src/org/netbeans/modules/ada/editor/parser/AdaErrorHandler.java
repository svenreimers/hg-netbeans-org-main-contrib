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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import org.netbeans.modules.ada.editor.parser.AdaParser.Context;
import org.netbeans.modules.ada.editor.ast.ASTError;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.csl.api.Severity;
import org.openide.util.NbBundle;
import org.netbeans.modules.csl.api.Error;

/**
 * Based on org.netbeans.modules.php.editor.parser.PHP5ErrorHandler
 *
 * @author Andrea Lucarelli
 */
public class AdaErrorHandler implements ParserErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(AdaErrorHandler.class.getName());
    
    public static class SyntaxError {
        private final short[] expectedTokens;
        private final Symbol currentToken;
        private final Symbol previousToken;

        public SyntaxError(short[] expectedTokens, Symbol currentToken, Symbol previousToken) {
            this.expectedTokens = expectedTokens;
            this.currentToken = currentToken;
            this.previousToken = previousToken;
        }

        public Symbol getCurrentToken() {
            return currentToken;
        }

        public Symbol getPreviousToken() {
            return previousToken;
        }

        public short[] getExpectedTokens() {
            return expectedTokens;
        }
    }
    
    private final List<SyntaxError> syntaxErrors;

    private final Context context;
    AdaParser outer;

    public AdaErrorHandler(Context context, AdaParser outer) {
        super();
        this.outer = outer;
        this.context = context;
        syntaxErrors = new ArrayList<SyntaxError>();
        //LOGGER.setLevel(Level.FINE);
    }

    @Override
    public void handleError(Type type, short[] expectedtokens, Symbol current, Symbol previous) {
        Error error;
        if (type == ParserErrorHandler.Type.SYNTAX_ERROR) {
            // logging syntax error
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Syntax error:"); //NOI18N
                LOGGER.log(Level.FINE, "Current [{0}, {1}]({2}): {3}", new Object[]{current.left, current.right, Utils.getASTScannerTokenName(current.sym), current.value}); //NOI18N
                LOGGER.log(Level.FINE, "Previous [{0}, {1}] ({2}):{3}", new Object[]{previous.left, previous.right, Utils.getASTScannerTokenName(previous.sym), previous.value}); //NOI18N
                StringBuilder message = new StringBuilder();
                message.append("Expected tokens:"); //NOI18N
                for (int i = 0; i < expectedtokens.length; i += 2) {
                    message.append(" ").append( Utils.getASTScannerTokenName(expectedtokens[i])); //NOI18N
                }
                LOGGER.fine(message.toString());
            }
            syntaxErrors.add(new SyntaxError(expectedtokens, current, previous));
        } else {
            String message = null;
            if (current != null) {
                String tagText = getTokenTextForm(current.sym);
                if (tagText != null) {
                    message = NbBundle.getMessage(AdaErrorHandler.class, "SE_Unexpected") + " " + tagText;
                }
                else {
                    message = NbBundle.getMessage(AdaErrorHandler.class, "SE_Unexpected") + " " + Utils.getASTScannerTokenName(current.sym);
                }
            }
            if (message == null) {
                message = "Parser error"; // NOI18N
            }
            error = new AdaError(message, context.getSnapshot().getSource().getFileObject(), current.left, current.right, Severity.ERROR, null);
            //context.getListener().error(error);
        }
    }

    @Override
    public void handleError(Type type, Symbol symbol, String message) {
        Error error;
        if (symbol != null) {
            if (message == null) {
                message = "Parser error";
            }
            error = new AdaError(message,  context.getSnapshot().getSource().getFileObject(), symbol.left, symbol.right, Severity.ERROR, null);
            //TODO: context.getListener().error(error);
        }
    }

    public List<Error> displayFatalError(){
        Error error = new FatalError();
        return Arrays.asList(error);
    }
    
    public List<Error>  displaySyntaxErrors(Program program) {
        List<Error> errors = new ArrayList<Error>();
        for (SyntaxError syntaxError : syntaxErrors) {
            ASTNode astError = null;
            if (program != null) {
                astError = org.netbeans.modules.ada.editor.ast.ASTUtils.getNodeAtOffset(program, syntaxError.currentToken.left);
                if (!(astError instanceof ASTError)) {
                    astError = org.netbeans.modules.ada.editor.ast.ASTUtils.getNodeAtOffset(program, syntaxError.previousToken.right);
                    if (!(astError instanceof ASTError)) {
                        astError = null;
                    }
                }
                if (astError != null) {
                    LOGGER.log(Level.FINE, "ASTError [{0}, {1}]", new Object[]{astError.getStartOffset(), astError.getEndOffset()}); //NOI18N
                } else {
                    LOGGER.fine("ASTError was not found");  //NOI18N
                }
            }
            Error error = defaultSyntaxErrorHandling(syntaxError, astError);
            errors.add(error);
        }
        return errors;
    }
    
    // This is just defualt handling. We can do a logic, which will find metter 
    private Error defaultSyntaxErrorHandling(SyntaxError syntaxError, ASTNode astError) {
        Error error = null;
        String unexpectedText = "";     //NOI18N
        StringBuilder message = new StringBuilder();
        boolean isUnexpected = false;
        int start  = syntaxError.getCurrentToken().left;
        int end = syntaxError.getCurrentToken().right;
        
        if (syntaxError.getCurrentToken().sym == AdaASTSymbols.EOF) {
            isUnexpected = true;
            unexpectedText = NbBundle.getMessage(AdaErrorHandler.class, "SE_EOF");
            start = end - 1;
        }
        else {
            String currentText = (String)syntaxError.getCurrentToken().value;
            isUnexpected = currentText != null && currentText.trim().length() > 0;
            if (isUnexpected) {
                unexpectedText = currentText.trim();
                end = start + unexpectedText.length();
            }
        }
        
        List<String> possibleTags = new ArrayList<String>();
        for (int i = 0; i < syntaxError.getExpectedTokens().length; i += 2) {
            String text = getTokenTextForm(syntaxError.getExpectedTokens()[i]);
            if (text != null) {
                possibleTags.add(text);
            }
        }

        message.append(NbBundle.getMessage(AdaErrorHandler.class, "SE_Message"));
        message.append(':'); //NOI18N
        if (isUnexpected) {
            message.append(' ').append(NbBundle.getMessage(AdaErrorHandler.class, "SE_Unexpected"));
            message.append(": "); //NOI18N
            message.append(unexpectedText);
        }
        if (possibleTags.size() > 0) {
            message.append('\n').append(NbBundle.getMessage(AdaErrorHandler.class, "SE_Expected"));
            message.append(": "); //NOI18N
            boolean addOR = false;
            for (String tag : possibleTags) {
                if (addOR) {
                    message.append(" ").append(NbBundle.getMessage(AdaErrorHandler.class, "SE_Or")).append(" ");
                }
                else {
                    addOR = true;
                }
                
                message.append(tag);
            }
        }

        if (astError != null){
            start = astError.getStartOffset();
            end = astError.getEndOffset();
            // if the asterror is trough two lines, the problem is ussually at the end
            String text = context.getSource().substring(start, end);
            int lastNewLine = text.length()-1;
            while (text.charAt(lastNewLine) == '\n' || text.charAt(lastNewLine) == '\r'
                    || text.charAt(lastNewLine) == '\t' || text.charAt(lastNewLine) == ' ') {
                lastNewLine--;
                if (lastNewLine < 0) {
                    break;
                }
            }
            lastNewLine = text.lastIndexOf('\n', lastNewLine);   //NOI18N
            if (lastNewLine > 0) {
                start = start + lastNewLine + 1;
            }
        }
        error = new AdaError(message.toString(), context.getSnapshot().getSource().getFileObject(), start, end, Severity.ERROR, new Object[]{syntaxError});
        return error;
    }

    public List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    private String getTokenTextForm (int token) {
        String text = null;
        switch (token) {
            case AdaASTSymbols.BASED_LITERAL : text = "number"; break; //NOI18N
            case AdaASTSymbols.DECIMAL_LITERAL : text = "number"; break; //NOI18N
            case AdaASTSymbols.IDENTIFIER : text = "identifier"; break; //NOI18N
            case AdaASTSymbols.ABORT : text = "abort"; break; //NOI18N
            case AdaASTSymbols.ABS : text = "abs"; break; //NOI18N
            case AdaASTSymbols.ABSTRACT : text = "abstract"; break; //NOI18N
            case AdaASTSymbols.ACCESS : text = "access"; break; //NOI18N
            case AdaASTSymbols.ACCEPT : text = "access"; break; //NOI18N
            case AdaASTSymbols.ALIASED : text = "aliased"; break; //NOI18N
            case AdaASTSymbols.ALL : text = "all"; break; //NOI18N
            case AdaASTSymbols.AND : text = "and"; break; //NOI18N
            case AdaASTSymbols.ARRAY : text = "array"; break; //NOI18N
            case AdaASTSymbols.AT : text = "at"; break; //NOI18N
            case AdaASTSymbols.BEGIN : text = "begin"; break; //NOI18N
            case AdaASTSymbols.BODY : text = "body"; break; //NOI18N
            case AdaASTSymbols.CONSTANT : text = "constant"; break; //NOI18N
            case AdaASTSymbols.CASE : text = "case"; break; //NOI18N
            case AdaASTSymbols.DECLARE : text = "declare"; break; //NOI18N
            case AdaASTSymbols.DELAY : text = "delay"; break; //NOI18N
            case AdaASTSymbols.DELTA : text = "delta"; break; //NOI18N
            case AdaASTSymbols.DIGITS : text = "digits"; break; //NOI18N
            case AdaASTSymbols.DO : text = "do"; break; //NOI18N
            case AdaASTSymbols.ELSE : text = "else"; break; //NOI18N
            case AdaASTSymbols.ELSIF : text = "elsif"; break; //NOI18N
            case AdaASTSymbols.END : text = "end"; break; //NOI18N
            case AdaASTSymbols.ENTRY : text = "entry"; break; //NOI18N
            case AdaASTSymbols.EXCEPTION : text = "exception"; break; //NOI18N
            case AdaASTSymbols.EXIT : text = "exit"; break; //NOI18N
            case AdaASTSymbols.FOR : text = "for"; break; //NOI18N
            case AdaASTSymbols.FUNCTION : text = "function"; break; //NOI18N
            case AdaASTSymbols.GENERIC : text = "generic"; break; //NOI18N
            case AdaASTSymbols.GOTO : text = "goto"; break; //NOI18N
            case AdaASTSymbols.IF : text = "if"; break; //NOI18N
            case AdaASTSymbols.IN : text = "in"; break; //NOI18N
            case AdaASTSymbols.IS : text = "is"; break; //NOI18N
            case AdaASTSymbols.LIMITED : text = "limited"; break; //NOI18N
            case AdaASTSymbols.LOOP : text = "loop"; break; //NOI18N
            case AdaASTSymbols.MOD : text = "mod"; break; //NOI18N
            case AdaASTSymbols.NEW : text = "new"; break; //NOI18N
            case AdaASTSymbols.NOT : text = "not"; break; //NOI18N
            case AdaASTSymbols.NULL : text = "null"; break; //NOI18N
            case AdaASTSymbols.OF : text = "of"; break; //NOI18N
            case AdaASTSymbols.OR : text = "or"; break; //NOI18N
            case AdaASTSymbols.OTHERS : text = "others"; break; //NOI18N
            case AdaASTSymbols.OUT : text = "out"; break; //NOI18N
            case AdaASTSymbols.PACKAGE : text = "package"; break; //NOI18N
            case AdaASTSymbols.PRAGMA : text = "pragma"; break; //NOI18N
            case AdaASTSymbols.PRIVATE : text = "private"; break; //NOI18N
            case AdaASTSymbols.PROCEDURE : text = "procedure"; break; //NOI18N
            case AdaASTSymbols.PROTECTED : text = "protected"; break; //NOI18N
            case AdaASTSymbols.RETURN : text = "return"; break; //NOI18N
            case AdaASTSymbols.REVERSE : text = "reverse"; break; //NOI18N
            case AdaASTSymbols.RAISE : text = "raise"; break; //NOI18N
            case AdaASTSymbols.RANGE : text = "range"; break; //NOI18N
            case AdaASTSymbols.RECORD : text = "record"; break; //NOI18N
            case AdaASTSymbols.REM : text = "rem"; break; //NOI18N
            case AdaASTSymbols.RENAMES : text = "renames"; break; //NOI18N
            case AdaASTSymbols.REQUEUE : text = "requeue"; break; //NOI18N
            case AdaASTSymbols.SELECT : text = "select"; break; //NOI18N
            case AdaASTSymbols.SEPARATE : text = "separate"; break; //NOI18N
            case AdaASTSymbols.SUBTYPE : text = "subtype"; break; //NOI18N
            case AdaASTSymbols.TAGGED : text = "tagged"; break; //NOI18N
            case AdaASTSymbols.TASK : text = "task"; break; //NOI18N
            case AdaASTSymbols.TERMINATE : text = "terminate"; break; //NOI18N
            case AdaASTSymbols.THEN : text = "then"; break; //NOI18N
            case AdaASTSymbols.TYPE : text = "type"; break; //NOI18N
            case AdaASTSymbols.UNTIL : text = "until"; break; //NOI18N
            case AdaASTSymbols.USE : text = "use"; break; //NOI18N
            case AdaASTSymbols.WHEN : text = "when"; break; //NOI18N
            case AdaASTSymbols.WHILE : text = "while"; break; //NOI18N
            case AdaASTSymbols.WITH : text = "with"; break; //NOI18N
            case AdaASTSymbols.XOR : text = "xor"; break; //NOI18N
            case AdaASTSymbols.AMP : text = "&"; break; //NOI18N
            case AdaASTSymbols.TICK : text = "'"; break; //NOI18N
            case AdaASTSymbols.LPAREN : text = "("; break; //NOI18N
            case AdaASTSymbols.RPAREN : text = ")"; break; //NOI18N
            case AdaASTSymbols.STAR : text = "*"; break; //NOI18N
            case AdaASTSymbols.PLUS : text = "+"; break; //NOI18N
            case AdaASTSymbols.COMMA : text = ","; break; //NOI18N
            case AdaASTSymbols.MINUS : text = "-"; break; //NOI18N
            case AdaASTSymbols.DOT : text = "."; break; //NOI18N
            case AdaASTSymbols.SLASH : text = "/"; break; //NOI18N
            case AdaASTSymbols.COLON : text = ":"; break; //NOI18N
            case AdaASTSymbols.SEMICOLON : text = ";"; break; //NOI18N
            case AdaASTSymbols.GT : text = "<"; break; //NOI18N
            case AdaASTSymbols.EQ : text = "="; break; //NOI18N
            case AdaASTSymbols.LT : text = ">"; break; //NOI18N
            case AdaASTSymbols.BAR : text = "|"; break; //NOI18N
            case AdaASTSymbols.ARROW : text = "=>"; break; //NOI18N
            case AdaASTSymbols.DOT_DOT : text = ".."; break; //NOI18N
            case AdaASTSymbols.EXPON : text = "**"; break; //NOI18N
            case AdaASTSymbols.ASSIGNMENT : text = ":="; break; //NOI18N
            case AdaASTSymbols.INEQ : text = "/="; break; //NOI18N
            case AdaASTSymbols.GTEQ : text = ">="; break; //NOI18N
            case AdaASTSymbols.LTEQ : text = "<="; break; //NOI18N
            case AdaASTSymbols.LTLT : text = "<<"; break; //NOI18N
            case AdaASTSymbols.GTGT : text = ">>"; break; //NOI18N
            case AdaASTSymbols.BOX : text = "<>"; break; //NOI18N
        }
        return text;
    }

    private class FatalError extends AdaError{
        FatalError(){
            super(NbBundle.getMessage(AdaErrorHandler.class, "MSG_FatalError"),
                context.getSnapshot().getSource().getFileObject(),
                0, context.getSource().length(),
                Severity.ERROR, null);
        }

        @Override
        public boolean isLineError() {
            return false;
        }
    }
}
