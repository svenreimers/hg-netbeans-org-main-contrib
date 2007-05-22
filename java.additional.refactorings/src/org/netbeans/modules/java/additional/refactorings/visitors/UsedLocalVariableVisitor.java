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
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TypeMirrorHandle;

/**
 *
 * @author Tim
 */
public final class UsedLocalVariableVisitor extends TreePathScanner<Void, Set<ParamDesc>> {
    private final CompilationInfo info;
    private final long excludeStart;
    private final long excludeEnd;
    
    public UsedLocalVariableVisitor(CompilationInfo info, long excludeStart, long excludeEnd) {
        this.excludeStart = excludeStart;
        this.excludeEnd = excludeEnd;
        this.info = info;
    }
    
    private static final Set<ElementKind> VARIABLES = EnumSet.of(ElementKind.EXCEPTION_PARAMETER, ElementKind.PARAMETER, ElementKind.LOCAL_VARIABLE);
    
    @Override
    public Void visitIdentifier(IdentifierTree node, Set<ParamDesc> p) {
        Element el = info.getTrees().getElement(getCurrentPath());
        TreePath elPath = el != null ? info.getTrees().getPath(el) : null;
        SourcePositions positions = info.getTrees().getSourcePositions();
        long start = positions.getStartPosition(info.getCompilationUnit(), node);
        long end = positions.getEndPosition(info.getCompilationUnit(), node);
        boolean include = notInExcludedRange (start, end);
        if (include) {
            if (el != null && elPath != null && VARIABLES.contains(el.getKind())) {
                VariableElement v = (VariableElement) el;
                if (!locallyDefinedNames.contains(node.getName().toString())) {
                    p.add(new ParamDesc(v));
                }
                saveType (v);
            }
        }
        return super.visitIdentifier(node, p);
    }
    
    private boolean notInExcludedRange (long start, long end) {
        return !((start < excludeStart && end < excludeStart) ||
                (start > excludeEnd));
    }

    Set <String> locallyDefinedNames = new HashSet <String> ();

    
    @Override
    public Void visitVariable(VariableTree tree, Set<ParamDesc> set) {
        SourcePositions positions = info.getTrees().getSourcePositions();
        long start = positions.getStartPosition(info.getCompilationUnit(), tree);
        long end = positions.getEndPosition(info.getCompilationUnit(), tree);
        //Make sure we didn't catch anything like loop variables that are actually
        //defined in the new method
        if (notInExcludedRange(start, end)) {
            String nm = tree.getName().toString();
            locallyDefinedNames.add (nm);
        }
        saveType (tree);
        return super.visitVariable(tree, set);
    }
    
    private void saveType (VariableElement el) {
        TypeMirror typeMirror = el.asType();
        TypeMirrorHandle handle = TypeMirrorHandle.create(typeMirror);
        VariableTree vt = (VariableTree) info.getTrees().getTree(el);
        names2types.put (vt.getName().toString(), handle);
    }
    
    private void saveType (VariableTree tree) {
        Tree type = tree.getType();
        TypeMirror typeMirror = info.getTrees().getTypeMirror(TreePath.getPath(info.getCompilationUnit(), type));
        TypeMirrorHandle handle = TypeMirrorHandle.create(typeMirror);
        names2types.put (tree.getName().toString(), handle);
    }
    
    Map <String, TypeMirrorHandle> names2types = new HashMap<String, TypeMirrorHandle>();
    Set <String> locallyAssigned = new HashSet<String> ();
    
    public TypeMirrorHandle getType (String varName) {
        TypeMirrorHandle result = names2types.get(varName);
        return result;
    }
    
    @Override
    public Void visitAssignment(AssignmentTree tree, Set<ParamDesc> set) {
        ExpressionTree var = tree.getVariable();
        if (var.getKind() == Kind.IDENTIFIER && var instanceof IdentifierTree) {
            IdentifierTree id = (IdentifierTree) var;
            String nm = id.getName().toString();
            if (!locallyDefinedNames.contains(nm)) {
                locallyAssigned.add (nm);
            }
        }
        return super.visitAssignment(tree, set);
    }
    
    public Set <String> getLocallyAssigned() {
        return Collections.<String>unmodifiableSet(locallyAssigned);
    }
}
