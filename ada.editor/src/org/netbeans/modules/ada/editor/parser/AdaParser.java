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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.ada.editor.lexer.AdaASTLexer;
import org.netbeans.modules.ada.editor.ast.ASTError;
import org.netbeans.modules.ada.editor.ast.nodes.Comment;
import org.netbeans.modules.ada.editor.ast.nodes.NullStatement;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.Statement;
import org.netbeans.modules.ada.project.api.AdaLanguageOptions;
import org.netbeans.modules.ada.project.api.AdaLanguageOptions.AdaVersion;
import org.netbeans.modules.ada.project.api.AdaLanguageOptions.Properties;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 * Based on org.netbeans.modules.php.editor.parser.GSFPHPParser
 * 
 * @author Andrea Lucarelli
 */
public class AdaParser extends Parser implements PropertyChangeListener {

//    private PositionManager positionManager = null;
    private static final Logger LOGGER = Logger.getLogger(AdaParser.class.getName());

    private ParserResult result = null;
    private ChangeSupport changeSupport = new ChangeSupport(this);
    private boolean projectPropertiesListenerAdded = false;
    private AdaVersion adaVersion;

    @Override
    public Result getResult(Task task) throws ParseException {
        return result;
    }

    @Override
    public void cancel() {
        // TODO
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
    }

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        FileObject file = snapshot.getSource().getFileObject();
        LOGGER.log(Level.FINE, "parseFiles {0}", file);
//        ParseListener listener = request.listener;

//        ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
//        request.listener.started(beginEvent);
        Properties languageProperties = AdaLanguageOptions.getDefault().getProperties(file);

        if (!projectPropertiesListenerAdded){
            PropertyChangeListener weakListener = WeakListeners.propertyChange(this, languageProperties);
            AdaLanguageOptions.getDefault().addPropertyChangeListener(weakListener);
            projectPropertiesListenerAdded = true;
        }

        adaVersion = languageProperties.getAdaVersion();
        int end = 0;
        try {
            String source = snapshot.getText().toString();
            end = source.length();
            int caretOffset = GsfUtilities.getLastKnownCaretOffset(snapshot, event);
            LOGGER.log(Level.FINE, "caretOffset: {0}", caretOffset); //NOI18N
            Context context = new Context(snapshot, source, caretOffset);
            result = parseBuffer(context, Sanitize.NONE, null);
        } catch (Exception exception) {
            LOGGER.log (Level.FINE, "Exception during parsing: {0}", exception);
            ASTError error = new ASTError(0, end);
            List<Statement> statements = new ArrayList<Statement>();
            statements.add(error);
            Program emptyProgram = new Program(0, end, statements, Collections.<Comment>emptyList());
            result = new AdaParseResult(snapshot, emptyProgram);
        }

    }

    protected AdaParseResult parseBuffer(final Context context, final Sanitize sanitizing, AdaErrorHandler errorHandler) throws Exception {
        boolean sanitizedSource = false;
        String source = context.source;
        if (errorHandler == null) {
            errorHandler = new AdaErrorHandler(context, this);
        }
        if (!((sanitizing == Sanitize.NONE) || (sanitizing == Sanitize.NEVER))) {
            boolean ok = sanitizeSource(context, sanitizing, errorHandler);

            if (ok) {
                assert context.sanitizedSource != null;
                sanitizedSource = true;
                source = context.sanitizedSource;
            } else {
                // Try next trick
                return sanitize(context, sanitizing, errorHandler);
            }
        }

        AdaParseResult localResult;
        // calling the ada ast parser itself
        AdaASTLexer scanner = new AdaASTLexer(new StringReader(source), adaVersion);
        AdaASTParser parser = new AdaASTParser(scanner);

        if (!sanitizedSource) {
            parser.setErrorHandler(errorHandler);
        } else {
            parser.setErrorHandler(null);
        }

        java_cup.runtime.Symbol rootSymbol = parser.parse();
        if (rootSymbol != null) {
            Program program = null;
            if (rootSymbol.value instanceof Program) {
                program = (Program) rootSymbol.value; // call the parser itself
                List<Statement> statements = program.getStatements();
                //do we need sanitization?
                boolean ok = false;
                for (Statement statement : statements) {
                    if (!(statement instanceof ASTError) && !(statement instanceof NullStatement)) {
                        ok = true;
                        break;
                    }
                }
                if (ok) {
                    localResult = new AdaParseResult(context.getSnapshot(), program);
                } else {
                    localResult = sanitize(context, sanitizing, errorHandler);
                }
            } else {
                LOGGER.log(Level.FINE, "The parser value is not a Program: {0}", rootSymbol.value);
                localResult = sanitize(context, sanitizing, errorHandler);
            }
            //if (!sanitizedSource) {
                localResult.setErrors(errorHandler.displaySyntaxErrors(program));
            //}
        } else {
            localResult = sanitize(context, sanitizing, errorHandler);
            localResult.setErrors(errorHandler.displayFatalError());
        }

        return localResult;
    }

    private boolean sanitizeSource(Context context, Sanitize sanitizing, AdaErrorHandler errorHandler) {
        if (sanitizing == Sanitize.SYNTAX_ERROR_CURRENT) {
            List<AdaErrorHandler.SyntaxError> syntaxErrors = errorHandler.getSyntaxErrors();
            if (syntaxErrors.size() > 0) {
                AdaErrorHandler.SyntaxError error = syntaxErrors.get(0);
                String source;
                if (context.sanitized == Sanitize.NONE) {
                    source = context.source;
                } else {
                    source = context.sanitizedSource;
                }

                int end = error.getCurrentToken().right;
                int start = error.getCurrentToken().left;

                context.sanitizedSource = source.substring(0, start) + Utils.getSpaces(end - start) + source.substring(end);
                context.sanitizedRange = new OffsetRange(start, end);
                return true;
            }
        }
        if (sanitizing == Sanitize.SYNTAX_ERROR_PREVIOUS) {
            List<AdaErrorHandler.SyntaxError> syntaxErrors = errorHandler.getSyntaxErrors();
            if (syntaxErrors.size() > 0) {
                AdaErrorHandler.SyntaxError error = syntaxErrors.get(0);
                String source = context.source;

                int end = error.getPreviousToken().right;
                int start = error.getPreviousToken().left;

                context.sanitizedSource = source.substring(0, start) + Utils.getSpaces(end - start) + source.substring(end);
                context.sanitizedRange = new OffsetRange(start, end);
                return true;
            }
        }
        if (sanitizing == Sanitize.SYNTAX_ERROR_PREVIOUS_LINE) {
            List<AdaErrorHandler.SyntaxError> syntaxErrors = errorHandler.getSyntaxErrors();
            if (syntaxErrors.size() > 0) {
                AdaErrorHandler.SyntaxError error = syntaxErrors.get(0);
                String source = context.source;

                int end = Utils.getRowEnd(source, error.getPreviousToken().right);
                int start = Utils.getRowStart(source, error.getPreviousToken().left);

                StringBuilder sb = new StringBuilder(end - start);
                for (int index = start; index < end; index++) {
                    if (source.charAt(index) == ' ' || source.charAt(index) == '\n' || source.charAt(index) == '\r') {
                        sb.append(source.charAt(index));
                    } else {
                        sb.append(' ');
                    }
                }

                context.sanitizedSource = source.substring(0, start) + sb.toString() + source.substring(end);
                context.sanitizedRange = new OffsetRange(start, end);
                return true;
            }
        }
        if (sanitizing == Sanitize.EDITED_LINE) {
            if (context.caretOffset > -1) {
                String source = context.getSource();
                int start = context.caretOffset - 1;
                int end = context.caretOffset;
                // fix until new line or }
                char c = source.charAt(start);
                while (start > 0 && c != '\n' && c != '\r') {
                    c = source.charAt(--start);
                }
                start++;
                if (end < source.length()) {
                    c = source.charAt(end);
                    while (end < source.length() && c != '\n' && c != '\r') {
                        c = source.charAt(end++);
                    }
                }
                context.sanitizedSource = source.substring(0, start) + Utils.getSpaces(end - start) + source.substring(end);
                context.sanitizedRange = new OffsetRange(start, end);
                return true;
            }
        }
        if (sanitizing == Sanitize.SYNTAX_ERROR_BLOCK) {
            List<AdaErrorHandler.SyntaxError> syntaxErrors = errorHandler.getSyntaxErrors();
            if (syntaxErrors.size() > 0) {
                AdaErrorHandler.SyntaxError error = syntaxErrors.get(0);
                return sanitizeRemoveBlock(context, error.getCurrentToken().left);
            }
        }
        return false;
    }

    private boolean sanitizeRemoveBlock(Context context, int index) {
        String source = context.getSource();
        AdaASTLexer lexer = new AdaASTLexer(new StringReader(source), adaVersion);
        Symbol token = null;
        int start = -1;
        int end = -1;
        try {
            token = lexer.next_token();
            while (token.sym != AdaASTSymbols.EOF && end == -1) {
                /*
                if (token.sym == Ada95ASTSymbols.T_CURLY_OPEN && token.left <= index) {
                start = token.right;
                }
                if (token.sym == Ada95ASTSymbols.T_CURLY_CLOSE && token.left >= index ) {
                end = token.right - 1;
                }
                 */
                token = lexer.next_token();
            }
        } catch (IOException exception) {
            LOGGER.log(Level.INFO, "Exception during removing block", exception);   //NOI18N
        }
        if (start > -1 && start < end) {
            context.sanitizedSource = source.substring(0, start) + Utils.getSpaces(end - start) + source.substring(end);
            context.sanitizedRange = new OffsetRange(start, end);
            return true;
        }
        return false;
    }

    private AdaParseResult sanitize(final Context context, final Sanitize sanitizing, AdaErrorHandler errorHandler) throws Exception {

        switch (sanitizing) {
            case NONE:
                return parseBuffer(context, Sanitize.SYNTAX_ERROR_CURRENT, errorHandler);
            case SYNTAX_ERROR_CURRENT:
                // one more time
                return parseBuffer(context, Sanitize.SYNTAX_ERROR_PREVIOUS, errorHandler);
            case SYNTAX_ERROR_PREVIOUS:
                return parseBuffer(context, Sanitize.SYNTAX_ERROR_PREVIOUS_LINE, errorHandler);
            case SYNTAX_ERROR_PREVIOUS_LINE:
                return parseBuffer(context, Sanitize.EDITED_LINE, errorHandler);
            case EDITED_LINE:
                return parseBuffer(context, Sanitize.SYNTAX_ERROR_BLOCK, errorHandler);
            default:
                int end = context.getSource().length();
                // add the ast error, some features can recognized that there is something wrong.
                // for example folding.
                ASTError error = new ASTError(0, end);
                List<Statement> statements = new ArrayList<Statement>();
                statements.add(error);
                Program emptyProgram = new Program(0, end, statements, Collections.<Comment>emptyList());

                return new AdaParseResult(context.getSnapshot(), emptyProgram);
        }

    }

    private static String asString(CharSequence sequence) {
        if (sequence instanceof String) {
            return (String) sequence;
        } else {
            return sequence.toString();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (AdaLanguageOptions.PROP_ADA_VERSION.equals(evt.getPropertyName())){
            forceReparsing();
        }
    }

    private void forceReparsing(){
        changeSupport.fireChange();
    }


    /** Attempts to sanitize the input buffer */
    public static enum Sanitize {

        /** Only parse the current file accurately, don't try heuristics */
        NEVER,
        /** Perform no sanitization */
        NONE,
        /** Remove current error token */
        SYNTAX_ERROR_CURRENT,
        /** Remove token before error */
        SYNTAX_ERROR_PREVIOUS,
        /** remove line with error */
        SYNTAX_ERROR_PREVIOUS_LINE,
        /** try to delete the whole block, where is the error*/
        SYNTAX_ERROR_BLOCK,
        /** Try to remove the trailing . or :: at the caret line */
        EDITED_DOT,
        /** Try to remove the trailing . or :: at the error position, or the prior
         * line, or the caret line */
        ERROR_DOT,
        /** Try to remove the initial "if" or "unless" on the block
         * in case it's not terminated
         */
        BLOCK_START,
        /** Try to cut out the error line */
        ERROR_LINE,
        /** Try to cut out the current edited line, if known */
        EDITED_LINE,
        /** Attempt to add an "end" to the end of the buffer to make it compile */
        MISSING_END,
    }

    /** Parsing context */
    public static class Context {
        private final Snapshot snapshot;
        private int errorOffset;
        private String source;
        private String sanitizedSource;
        private OffsetRange sanitizedRange = OffsetRange.NONE;
        private String sanitizedContents;
        private int caretOffset;
        private Sanitize sanitized = Sanitize.NONE;


        public Context(Snapshot snapshot, String source, int caretOffset) {
            this.snapshot = snapshot;
            this.source = source;
            this.caretOffset = caretOffset;
        }

        @Override
        public String toString() {
            return "AdaParser.Context(" + snapshot.getSource().getFileObject() + ")"; // NOI18N
        }

        public OffsetRange getSanitizedRange() {
            return sanitizedRange;
        }

        public Sanitize getSanitized() {
            return sanitized;
        }

        public String getSanitizedSource() {
            return sanitizedSource;
        }

        public int getErrorOffset() {
            return errorOffset;
        }

        /**
         * @return the file
         */
        public Snapshot getSnapshot() {
            return snapshot;
        }

        /**
         * @return the source
         */
        public String getSource() {
            return source;
        }
    }
}
