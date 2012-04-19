/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints.jdk5;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Collection;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.JavaFixUtilities;
import org.netbeans.spi.java.hints.TriggerPattern;
import org.openide.util.NbBundle.Messages;

@Hint(displayName="#DN_IteratorToFor", description="#DESC_IteratorToFor", category="rules15")
@Messages({
    "DN_IteratorToFor=Use JDK 5 for-loop",
    "DESC_IteratorToFor=Replaces simple uses of Iterator with a corresponding for-loop.",
    "ERR_IteratorToFor=Use of Iterator for simple loop",
    "FIX_IteratorToFor=Convert to for-loop"
})
public class IteratorToFor {

    @TriggerPattern("java.util.Iterator $it = $coll.iterator(); while ($it.hasNext()) {$type $elem = ($type) $it.next(); $rest$;}")
    public static ErrorDescription whileIdiom(HintContext ctx) {
        if (uses(ctx, ctx.getMultiVariables().get("$rest$"), ctx.getVariables().get("$it"))) {
            return null;
        }
        if (!iterable(ctx, ctx.getVariables().get("$coll"), ctx.getVariables().get("$type"))) {
            return null;
        }
        return ErrorDescriptionFactory.forName(ctx, ctx.getPath(), Bundle.ERR_IteratorToFor(),
                JavaFixUtilities.rewriteFix(ctx, Bundle.FIX_IteratorToFor(), ctx.getPath(), "for ($type $elem : $coll) {$rest$;}"));
    }

    @TriggerPattern("for (java.util.Iterator $it = $coll.iterator(); $it.hasNext(); ) {$type $elem = ($type) $it.next(); $rest$;}")
    public static ErrorDescription forIdiom(HintContext ctx) {
        if (uses(ctx, ctx.getMultiVariables().get("$rest$"), ctx.getVariables().get("$it"))) {
            return null;
        }
        if (!iterable(ctx, ctx.getVariables().get("$coll"), ctx.getVariables().get("$type"))) {
            return null;
        }
        return ErrorDescriptionFactory.forName(ctx, ctx.getPath(), Bundle.ERR_IteratorToFor(),
                JavaFixUtilities.rewriteFix(ctx, Bundle.FIX_IteratorToFor(), ctx.getPath(), "for ($type $elem : $coll) {$rest$;}"));
    }

    // adapted from org.netbeans.modules.java.hints.declarative.conditionapi.Matcher.referencedIn
    private static boolean uses(final HintContext ctx, Collection<? extends TreePath> statements, TreePath var) {
        final Element e = ctx.getInfo().getTrees().getElement(var);
        for (TreePath tp : statements) {
            boolean occurs = Boolean.TRUE.equals(new TreePathScanner<Boolean, Void>() {
                @Override public Boolean scan(Tree tree, Void p) {
                    if (tree == null) {
                        return false;
                    }
                    TreePath currentPath = new TreePath(getCurrentPath(), tree);
                    Element currentElement = ctx.getInfo().getTrees().getElement(currentPath);
                    if (e.equals(currentElement)) {
                        return true;
                    }
                    return super.scan(tree, p);
                }
                @Override public Boolean reduce(Boolean r1, Boolean r2) {
                    if (r1 == null) {
                        return r2;
                    }
                    if (r2 == null) {
                        return r1;
                    }
                    return r1 || r2;
                }
            }.scan(tp, null));
            if (occurs) {
                return true;
            }
        }
        return false;
    }

    private static boolean iterable(HintContext ctx, TreePath collection, TreePath type) {
        TypeMirror collectionType = ctx.getInfo().getTrees().getTypeMirror(collection);
        TypeElement iterable = ctx.getInfo().getElements().getTypeElement("java.lang.Iterable");
        TypeMirror iterableType = ctx.getInfo().getTypes().getDeclaredType(iterable, ctx.getInfo().getTypes().getWildcardType(ctx.getInfo().getTrees().getTypeMirror(type), null));
        TypeMirror bogusIterableType = ctx.getInfo().getTypes().getDeclaredType(iterable, ctx.getInfo().getTypes().getNullType());
        return ctx.getInfo().getTypes().isAssignable(collectionType, iterableType) && !ctx.getInfo().getTypes().isAssignable(collectionType, bogusIterableType);
    }

}
