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

package org.netbeans.modules.php.fuse.lexer;

import org.junit.Test;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.lexer.test.LexerTestUtilities;

/**
 *
 * @author Martin Fousek
 */
public class FuseLexerBatchTest {

    public FuseLexerBatchTest() {
    }

    protected void setUp() throws java.lang.Exception {
        // Set-up testing environment
        LexerTestUtilities.setTesting(true);
    }

    protected void tearDown() throws java.lang.Exception {
    }

    @Test
    public void testTopFuseAndHtmlTokens() {
        String text = "<{IF (go)}><div><{variable }></div><{ELSE}>error<{/IF}>";

        TokenHierarchy<?> hi = TokenHierarchy.create(text, FuseTopTokenId.language());
        TokenSequence<?> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_OPEN_DELIMITER, "<{");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE, "IF (go)");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_CLOSE_DELIMITER, "}>");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_HTML, "<div>");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_OPEN_DELIMITER, "<{");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE, "variable ");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_CLOSE_DELIMITER, "}>");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_HTML, "</div>");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_OPEN_DELIMITER, "<{");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE, "ELSE");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_CLOSE_DELIMITER, "}>");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_HTML, "error");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_OPEN_DELIMITER, "<{");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE, "/IF");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTopTokenId.T_FUSE_CLOSE_DELIMITER, "}>");
    }

    @Test
    public void testFuseTokens() {
        String text = "IF (go)variable ELSE/IF";

        TokenHierarchy<?> hi = TokenHierarchy.create(text, FuseTokenId.language());
        TokenSequence<?> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.IF, "if");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.LPAREN, "(");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.IDENTIFIER, "go");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.RPAREN, ")");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.IDENTIFIER, "variable");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.ELSE, "else");
        LexerTestUtilities.assertNextTokenEquals(ts, FuseTokenId.IF_END, "/if");
    }
}