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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import javax.lang.model.element.ExecutableElement;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ChangeData;

/**
 * Is given a set of data about the changes the user has requested and scans
 * parameter trees, marking those that should be skipped, either because of
 * name conflicts or the policy the user has specified.
 * 
 * @author Tim Boudreau
 */
public class ParameterScanner extends TreeScanner <Void, ParameterChangeContext> {
    public ParameterScanner () {
    }
    
    @Override
    public Void visitVariable(VariableTree tree, ParameterChangeContext ctx) {
        String name = tree.getName().toString();
        RequestedParameterChanges pendingChanges = ctx.mods;
        ChangeData data = ctx.changeData;
        if (pendingChanges.isNewOrChangedParameterName(name)) {
            ExecutableElement method = data.getCurrentMethodElement();
            boolean skip;
            switch (pendingChanges.getPolicy()) {
                case RENAME_IF_SAME :
                    int ix = data.getParameterIndex();
                    skip = !pendingChanges.matchesOriginalParameterName(ix, name);
                    break;
                case RENAME_UNLESS_CONFLICT :
                    skip = pendingChanges.isNewParameterName(name);
                    break;
                default :
                    throw new AssertionError();
            }
            if (skip) {
                System.err.println("Will skip " + name + " in " + tree.getName());
                data.skipCurrentParameter(ElementHandle.create(method));
            }
        }
        return super.visitVariable(tree, ctx);
    }
}
