/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.fuse.editor;

import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Martin Fousek
 */
class TmplElLexer implements Lexer<TmplElTokenId> {

    private static final int EOF = LexerInput.EOF;
    private final LexerInput input;
    private final InputAttributes inputAttributes;
    private final TokenFactory<TmplElTokenId> tokenFactory;

    public Object state() {
        return lexerState;
    }

    private boolean EL_ENABLED;
    //main internal lexer state
    private int lexerState = INIT;
    // Internal analyzer states
    private static final int INIT = 0;  // initial lexer state = content language
    private static final int ISA_EL_DELIM = 1; //after < in content language
    private static final int ISI_EL_DELIM = 2; //after <{ in content language
    private static final int ISI_EL = 3; //after <{ ... } in content language

    public TmplElLexer(LexerRestartInfo<TmplElTokenId> info) {
        this.input = info.input();
        this.inputAttributes = info.inputAttributes();
        this.tokenFactory = info.tokenFactory();
        if (info.state() == null) {
            lexerState = INIT;
        } else {
            lexerState = ((Integer) info.state()).intValue();
        }
        if (inputAttributes != null) {
            EL_ENABLED = inputAttributes.getValue(LanguagePath.get(TmplElTokenId.language()), "el_enabled") != null; //NOI18N
        } else {
            EL_ENABLED = false;
        }
    }

    public Token<TmplElTokenId> nextToken() {
        int actChar;
        while (true) {
            actChar = input.read();

            if (actChar == EOF) {
                if (input.readLengthEOF() == 1) {
                    return null; //just EOL is read
                } else {
                    //there is something else in the buffer except EOL
                    //we will return last token now
                    input.backup(1); //backup the EOL, we will return null in next nextToken() call
                    break;
                }
            }

            switch (lexerState) {
                case INIT:
                    switch (actChar) {
                        case '<':  //maybe expression language
                            if(EL_ENABLED) {
                                lexerState = ISA_EL_DELIM;
                                break;
                            }
                    }
                    break;

                case ISA_EL_DELIM:
                    switch (actChar) {
                        case '{':
                            if (input.readLength() > 2) {
                                //we have something read except the '<{' => it's content language
                                input.backup(2); //put back the '<{'
                                return token(TmplElTokenId.HTML);
                            }
                            lexerState = ISI_EL_DELIM;
                            break;
                        default:
                            input.backup(1); //put the read char back
                            lexerState = INIT;
                    }
                    break;

                case ISI_EL_DELIM:
                    if (actChar == '}') {
                        //first part of return EL token
                        lexerState = ISI_EL;
                    }
                    break;

                case ISI_EL:
                    if (actChar == '>') {
                        //return EL token
                        lexerState = INIT;
                        return token(TmplElTokenId.EL);
                    }
                    //stay in EL
                    break;

            }

        }

        // At this stage there's no more text in the scanned buffer.
        // Scanner first checks whether this is completely the last
        // available buffer.

        switch (lexerState) {
            case INIT:
                if (input.readLength() == 0) {
                    return null;
                } else {
                    return token(TmplElTokenId.HTML);
                }
            case ISA_EL_DELIM:
                return token(TmplElTokenId.HTML);
            case ISI_EL_DELIM:
                return token(TmplElTokenId.HTML);
            case ISI_EL:
                return token(TmplElTokenId.EL);
            default:
                break;
        }

        return null;

    }

    private Token<TmplElTokenId> token(TmplElTokenId tokenId) {
        return tokenFactory.createToken(tokenId);
    }

    public void release() {
    }

}
