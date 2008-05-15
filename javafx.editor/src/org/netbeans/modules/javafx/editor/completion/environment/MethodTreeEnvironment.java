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

package org.netbeans.modules.javafx.editor.completion.environment;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionEnvironment;

/**
 *
 * @author David Strupl
 */
public class MethodTreeEnvironment extends JavaFXCompletionEnvironment<MethodTree> {
    
    private static final Logger logger = Logger.getLogger(MethodTreeEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    @Override
    protected void inside(MethodTree t) throws IOException {
        log("inside MethodTree " + t);
        MethodTree mth = t;
        int startPos = (int) sourcePositions.getStartPosition(root, mth);
        Tree retType = mth.getReturnType();
        if (retType == null) {
            int modPos = (int) sourcePositions.getEndPosition(root, mth.getModifiers());
            if (modPos > startPos) {
                startPos = modPos;
            }
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last == null) {
                addMemberModifiers(mth.getModifiers().getFlags(), false);
                return;
            }
        } else {
            if (offset <= sourcePositions.getStartPosition(root, retType)) {
                addMemberModifiers(mth.getModifiers().getFlags(), false);
                return;
            }
            startPos = (int) sourcePositions.getEndPosition(root, retType) + 1;
        }
        Tree lastThr = null;
        for (Tree thr : mth.getThrows()) {
            int thrPos = (int) sourcePositions.getEndPosition(root, thr);
            if (thrPos == Diagnostic.NOPOS || offset <= thrPos) {
                break;
            }
            lastThr = thr;
            startPos = thrPos;
        }
        if (lastThr != null) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last != null && last.token().id() == JFXTokenId.COMMA) {
                // TODO:
            }
            return;
        }
        String headerText = controller.getText().substring(startPos, offset);
        int parStart = headerText.indexOf('(');
        if (parStart >= 0) {
            int parEnd = headerText.indexOf(')', parStart);
            if (parEnd > parStart) {
                headerText = headerText.substring(parEnd + 1).trim();
            } else {
                for (VariableTree param : mth.getParameters()) {
                    int parPos = (int) sourcePositions.getEndPosition(root, param);
                    if (parPos == Diagnostic.NOPOS || offset <= parPos) {
                        break;
                    }
                    parStart = parPos - startPos;
                }
                headerText = headerText.substring(parStart).trim();
                if ("(".equals(headerText) || ",".equals(headerText)) {
                    addMemberModifiers(Collections.<Modifier>emptySet(), true);
                }
            }
        } else if (retType != null && headerText.trim().length() == 0) {
            insideExpression(new TreePath(path, retType));
        }
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
