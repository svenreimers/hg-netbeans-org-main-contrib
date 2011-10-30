/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints;

import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.util.TreePath;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import org.netbeans.modules.java.hints.jackpot.code.spi.Hint;
import org.netbeans.modules.java.hints.jackpot.code.spi.TriggerPattern;
import org.netbeans.modules.java.hints.jackpot.spi.HintContext;
import org.netbeans.modules.java.hints.jackpot.spi.support.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.util.NbBundle;

/**
 *
 * @author lahvac
 */
@Hint(category="bugs", suppressWarnings="ResultOfMethodCallIgnored")
public class CheckReturnValueHint {

    @TriggerPattern("$method($params$);")
    public static ErrorDescription hint(HintContext ctx) {
        Element invoked = ctx.getInfo().getTrees().getElement(new TreePath(ctx.getPath(), ((ExpressionStatementTree) ctx.getPath().getLeaf()).getExpression()));

        if (invoked == null || invoked.getKind() != ElementKind.METHOD || ((ExecutableElement) invoked).getReturnType().getKind() == TypeKind.VOID) return null;

        boolean found = false;

        for (AnnotationMirror am : invoked.getAnnotationMirrors()) {
            String simpleName = am.getAnnotationType().asElement().getSimpleName().toString();

            if ("CheckReturnValue".equals(simpleName)) {
                found = true;
                break;
            }
        }

        if (!found && !checkReturnValueForJDKMethods((ExecutableElement) invoked)) return null;

        String displayName = NbBundle.getMessage(CheckReturnValueHint.class, "ERR_org.netbeans.modules.javahints.CheckReturnValueHint");
        
        return ErrorDescriptionFactory.forName(ctx, ctx.getPath(), displayName);
    }

    private static final Set<String> JDK_IMMUTABLE_CLASSES = new HashSet<String>(Arrays.asList("java.lang.String"));

    private static boolean checkReturnValueForJDKMethods(ExecutableElement method) {
        Element owner = method.getEnclosingElement();

        if (!owner.getKind().isClass() && !owner.getKind().isInterface()) return false;

        if (JDK_IMMUTABLE_CLASSES.contains(((TypeElement) owner).getQualifiedName().toString())) return true;

        return false;
    }
}
