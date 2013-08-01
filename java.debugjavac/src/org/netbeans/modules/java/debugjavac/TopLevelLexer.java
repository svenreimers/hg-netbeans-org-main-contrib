/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.debugjavac;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author lahvac
 */
public class TopLevelLexer implements Lexer<TopLevelTokenId> {
    
    private static final Pattern SECTION_PATTERN = Pattern.compile("#Section\\(([^)]*)\\)[^\n]*\n");
    private final TokenFactory<TopLevelTokenId> factory;
    private final LexerInput input;
    private TopLevelTokenId futureToken;

    public TopLevelLexer(LexerRestartInfo<TopLevelTokenId> restart) {
        this.factory = restart.tokenFactory();
        this.input = restart.input();
        this.futureToken = restart.state() != null ? (TopLevelTokenId) restart.state() : TopLevelTokenId.OTHER;
    }
    
    @Override
    public Token<TopLevelTokenId> nextToken() {
        StringBuilder text = new StringBuilder();
        int read;
        
        while ((read = input.read()) != LexerInput.EOF) {
            text.append((char) read);
            
            Matcher m = SECTION_PATTERN.matcher(text);
            
            if (m.find()) {
                if (m.start() == 0) {
                    String mimeType = m.group(1);
                    
                    switch (mimeType) {
                        case "text/x-java": futureToken = TopLevelTokenId.JAVA; break;
                        case "text/x-java-bytecode": futureToken = TopLevelTokenId.ASM; break;
                        default: futureToken = TopLevelTokenId.OTHER; break;
                    }
                    
                    return factory.createToken(TopLevelTokenId.SECTION_HEADER);
                } else {
                    input.backup(input.readLength() - m.start());
                    break;
                }
            }
        }
        
        if (input.readLength() > 0)
            return factory.createToken(futureToken);
        
        return null;
    }

    @Override
    public Object state() {
        return futureToken;
    }

    @Override
    public void release() {}
    
}
