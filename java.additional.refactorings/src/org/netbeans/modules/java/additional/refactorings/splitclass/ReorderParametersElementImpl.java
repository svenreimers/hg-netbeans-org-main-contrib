/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Tim
 */
public class ReorderParametersElementImpl extends AbstractParameterChangeElementImpl {
    private List<Swap> oldToNewPositions;
    public ReorderParametersElementImpl(TreePathHandle methodPathHandle, String name, Lookup context, FileObject file, Collection <Swap> oldToNewPositions) {
        super (methodPathHandle, name, context, file);
        this.oldToNewPositions = new ArrayList <Swap> (oldToNewPositions);
        Collections.sort (this.oldToNewPositions);
    }

    public String getText() {
        return "Reorder parameters on " + name;
    }

    protected void modifyArgs(List<ExpressionTree> args, TreeMaker maker) {
        for (Swap swap : oldToNewPositions) {
            swap.swap (args);
        }
    }

    protected void modifyOverrideArgs(List<VariableTree> args, TreeMaker maker) {
        for (Swap swap : oldToNewPositions) {
            swap.swap (args);
        }
    }
}