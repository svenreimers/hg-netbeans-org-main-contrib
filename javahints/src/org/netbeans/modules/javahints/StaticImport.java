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
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.editor.imports.ComputeImports;
import org.netbeans.modules.java.editor.imports.ComputeImports.Pair;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;
import static org.netbeans.modules.editor.java.Utilities.getElementName;

/**
 * Hint offering to convert a qualified static method into a static import. e.g.
 * <code>Math.abs(-1)</code> -> <code>abs(-1)</code>.
 * <p>
 * Future versions might support other member types.
 *
 * @author Sam Halliday
 * @see <a href="http://www.netbeans.org/issues/show_bug.cgi?id=89258">RFE 89258</a>
 * @see <a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/static-import.html>Static Imports</a>
 */
public class StaticImport extends AbstractHint {

    private final AtomicBoolean cancel = new AtomicBoolean();

    public StaticImport() {
        super(false, false, HintSeverity.CURRENT_LINE_WARNING);
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(StaticImport.class, "DSC_StaticImport");
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.MEMBER_SELECT);
    }

    public List<ErrorDescription> run(CompilationInfo info, TreePath treePath) {
        if (treePath == null || treePath.getLeaf().getKind() != Kind.MEMBER_SELECT) {
            return null;
        }
        cancel.set(false);
        TreePath mitp = treePath.getParentPath();
        if (mitp == null || mitp.getLeaf().getKind() != Kind.METHOD_INVOCATION) {
            return null;
        }
        Element e = info.getTrees().getElement(treePath);
        if (e == null || !e.getModifiers().contains(Modifier.STATIC) || e.getKind() != ElementKind.METHOD) {
            return null;
        }
        if (!supportsStaticImports(info)) {
            return null;
        }
        Element enclosingEl = e.getEnclosingElement();
        if (enclosingEl == null) {
            return null;
        }
        String sn = e.getSimpleName().toString();
        List<Fix> fixes;
        if (!isValidStaticMethod(info, getElementName(enclosingEl, true).toString(), sn)) {
            // XXX: handling errors, see tracker for patch with handling in hints.errors
            Collection<String> fqns = guessCandidates(info, ((MemberSelectTree)treePath.getLeaf()).getExpression().toString(), sn);
            if (fqns.isEmpty()) {
                return null;
            }
            fixes = new ArrayList<Fix>();
            for (String fqn : fqns) {
                fixes.add(new FixImpl(TreePathHandle.create(treePath, info), fqn, sn));
            }
        } else {
            TreePath cc = getContainingClass(treePath);
            if (cc == null){
                return null;
            }
            Element klass = info.getTrees().getElement(cc);
            if (klass.getKind() != ElementKind.CLASS) {
                return null;
            }
            String fqn = null;
            String fqn1 = getMethodFqn(e);
            if (!isSubTypeOrInnerOfSubType(info, klass, enclosingEl) && !isStaticallyImported(info, fqn1)) {
                if (hasMethodNameClash(info, klass, sn) || hasStaticImportSimpleNameClash(info, sn)) {
                    return null;
                }
                fqn = fqn1;
            }
            fixes = Collections.<Fix>singletonList(new FixImpl(TreePathHandle.create(treePath, info), fqn, sn));
        }
        String desc = NbBundle.getMessage(StaticImport.class, "ERR_StaticImport");
        int start = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), treePath.getLeaf());
        int end = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), treePath.getLeaf());
        ErrorDescription ed = ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), desc, fixes, info.getFileObject(), start, end);
        if (cancel.get()) {
            return null;
        }
        return Collections.singletonList(ed);
    }

    public String getId() {
        return StaticImport.class.getName();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(StaticImport.class, "DN_StaticImport");
    }

    public void cancel() {
        cancel.set(true);
    }

    /**
     * @param info
     * @return true if the source level supports the static import language feature
     */
    public static boolean supportsStaticImports(CompilationInfo info) {
        String level = SourceLevelQuery.getSourceLevel(info.getFileObject());
        if (level == null) {
            return false;
        }
        try {
            double dLevel = Double.valueOf(level);
            if (dLevel < 1.5) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static final class FixImpl implements Fix, Task<WorkingCopy> {

        private final TreePathHandle handle;
        private final String fqn;
        private final String sn;

        /**
         * @param handle to the MEMBER_SELECT
         * @param fqn to static import, or null to not perform any imports
         * @param sn simple name
         */
        public FixImpl(TreePathHandle handle, String fqn, String sn) {
            this.handle = handle;
            this.fqn = fqn;
            this.sn = sn;
        }

        public String getText() {
            if (fqn == null) {
                return NbBundle.getMessage(StaticImport.class, "HINT_StaticImport", sn);
            } else {
                return NbBundle.getMessage(StaticImport.class, "HINT_StaticImport2", fqn);
            }
        }

        public ChangeInfo implement() throws Exception {
            JavaSource js = JavaSource.forFileObject(handle.getFileObject());
            js.runModificationTask(this).commit();
            return null;
        }

        public void run(WorkingCopy copy) throws Exception {
            if (copy.toPhase(Phase.RESOLVED).compareTo(Phase.RESOLVED) < 0) {
                return;
            }
            TreePath treePath = handle.resolve(copy);
            if (treePath == null || treePath.getLeaf().getKind() != Kind.MEMBER_SELECT) {
                return;
            }
            TreePath mitp = treePath.getParentPath();
            if (mitp == null || mitp.getLeaf().getKind() != Kind.METHOD_INVOCATION) {
                return;
            }
            Element e = copy.getTrees().getElement(treePath);
            if (e == null || !e.getModifiers().contains(Modifier.STATIC)) {
                return;
            }
            TreeMaker make = copy.getTreeMaker();
            copy.rewrite(treePath.getLeaf(), make.Identifier(sn));
            if (fqn == null) {
                return;
            }
            CompilationUnitTree cut = copy.getCompilationUnit();
            CompilationUnitTree nue = addStaticImports(cut, Collections.singletonList(fqn), make);
            copy.rewrite(cut, nue);
        }
    }

    // returns true if a METHOD is enclosed in element with simple name sn
    private static boolean hasMethodWithSimpleName(CompilationInfo info, Element element, final String sn) {
        Iterable<? extends Element> members =
                info.getElementUtilities().getMembers(element.asType(), new ElementUtilities.ElementAcceptor() {

            public boolean accept(Element e, TypeMirror type) {
                if (e.getKind() == ElementKind.METHOD && e.getSimpleName().toString().equals(sn)) {
                    return true;
                }
                return false;
            }
        });
        return members.iterator().hasNext();
    }

    /**
     * @param info
     * @param simpleName of static method.
     * @return true if a static import exists with the same simple name.
     * Caveat, expect false positives on protected and default visibility methods from wildcard static imports.
     */
    public static boolean hasStaticImportSimpleNameClash(CompilationInfo info, String simpleName) {
        for (ImportTree i : info.getCompilationUnit().getImports()) {
            if (!i.isStatic()) {
                continue;
            }
            String q = i.getQualifiedIdentifier().toString();
            if (q.endsWith(".*")) { //NOI18N
                TypeElement ie = info.getElements().getTypeElement(q.substring(0, q.length() - 2));
                if (ie == null) {
                    continue;
                }
                for (Element enclosed : ie.getEnclosedElements()) {
                    Set<Modifier> modifiers = enclosed.getModifiers();
                    if (enclosed.getKind() != ElementKind.METHOD || !modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE)) {
                        continue;
                    }
                    String sn1 = enclosed.getSimpleName().toString();
                    if (simpleName.equals(sn1)) {
                        return true;
                    }
                }
            } else {
                int endIndex = q.lastIndexOf("."); //NOI18N
                if (endIndex == -1 || endIndex >= q.length() - 1) {
                    continue;
                }
                if (q.substring(endIndex).equals(simpleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param info
     * @param t1
     * @param t3
     * @return true iff the first type (or its containing class in the case of inner classes)
     * is a subtype of the second.
     * @see Types#isSubtype(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)
     */
    private static boolean isSubTypeOrInnerOfSubType(CompilationInfo info, Element t1, Element t2) {
        boolean isSubtype = info.getTypes().isSubtype(t1.asType(), t2.asType());
        boolean isInnerClass = t1.getEnclosingElement().getKind() == ElementKind.CLASS;
        return isSubtype || (isInnerClass && info.getTypes().isSubtype(t1.getEnclosingElement().asType(), t2.asType()));
    }

    /**
     * @param info
     * @param klass the element for a CLASS
     * @param member the STATIC, MEMBER_SELECT Element for a MethodInvocationTree
     * @return true if member has a simple name which would clash with local or inherited
     * methods in klass (which may be an inner or static class).
     */
    public static boolean hasMethodNameClash(CompilationInfo info, Element klass, String simpleName) {
        assert klass != null;
        assert klass.getKind() == ElementKind.CLASS;

        // check the members and inherited members of the klass
        if (hasMethodWithSimpleName(info, klass, simpleName)) {
            return true;
        }
        Element klassEnclosing = klass.getEnclosingElement();
        return (klassEnclosing != null && klassEnclosing.getKind() == ElementKind.CLASS && hasMethodWithSimpleName(info, klassEnclosing, simpleName));
    }

    /**
     * @param e
     * @return the FQN for a METHOD Element
     */
    public static String getMethodFqn(Element e) {
        // XXX or alternatively, upgrade getElementName to handle METHOD
        assert e.getKind() == ElementKind.METHOD;
        return getElementName(e.getEnclosingElement(), true) + "." + e.getSimpleName();
    }

    /**
     * @param tp
     * @return the first path which is a CLASS or null if none found
     */
    public static TreePath getContainingClass(TreePath tp) {
        while (tp != null && tp.getLeaf().getKind() != Kind.CLASS) {
            tp = tp.getParentPath();
        }
        return tp;
    }

    // return true if the fqn already has a static import
    private static boolean isStaticallyImported(CompilationInfo info, String fqn) {
        for (ImportTree i : info.getCompilationUnit().getImports()) {
            if (!i.isStatic()) {
                continue;
            }
            String q = i.getQualifiedIdentifier().toString();
            if (q.endsWith(".*") && fqn.startsWith(q.substring(0, q.length() - 1))) { //NOI18N
                return true;
            }
            if (q.equals(fqn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param info
     * @param fqn of the containing class
     * @param simpleName of the method
     * @return true if {@code fqn.simpleName} represents a valid static method
     */
    public static boolean isValidStaticMethod(CompilationInfo info, String fqn, String simpleName) {
        TypeElement ie = info.getElements().getTypeElement(fqn);
        if (ie == null) {
            return false;
        }
        for (Element enclosed : ie.getEnclosedElements()) {
            Set<Modifier> modifiers = enclosed.getModifiers();
            if (enclosed.getKind() != ElementKind.METHOD || !modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE)) {
                continue;
            }
            String sn1 = enclosed.getSimpleName().toString();
            if (simpleName.equals(sn1)) {
                return true;
            }
        }
        return false;
    }

    // XXX should be in SourceUtils
    private static CompilationUnitTree addStaticImports(CompilationUnitTree cut, List<String> toImport, TreeMaker make) {
        return addImports(cut, toImport, make, true);
    }

    // XXX should be in SourceUtils
    private static CompilationUnitTree addImports(CompilationUnitTree cut, List<String> toImport, TreeMaker make, boolean doStatic) {
        // XXX old (private) version claimed to throw IOException, but it didn't really.
        // do not modify the list given by the caller (may be reused or immutable).
        toImport = new ArrayList<String>(toImport);
        Collections.sort(toImport);

        List<ImportTree> imports = new ArrayList<ImportTree>(cut.getImports());
        int currentToImport = toImport.size() - 1;
        int currentExisting = imports.size() - 1;

        while (currentToImport >= 0 && currentExisting >= 0) {
            String currentToImportText = toImport.get(currentToImport);

            boolean ignore = (doStatic ^ imports.get(currentExisting).isStatic());
            while (currentExisting >= 0 && (ignore || imports.get(currentExisting).getQualifiedIdentifier().toString().compareTo(currentToImportText) > 0)) {
                currentExisting--;
            }

            if (currentExisting >= 0) {
                imports.add(currentExisting + 1, make.Import(make.Identifier(currentToImportText), doStatic));
                currentToImport--;
            }
        }
        // we are at the head of import section and we still have some imports
        // to add, put them to the very beginning
        while (currentToImport >= 0) {
            String importText = toImport.get(currentToImport);
            imports.add(0, make.Import(make.Identifier(importText), doStatic));
            currentToImport--;
        }
        // return a copy of the unit with changed imports section
        return make.CompilationUnit(cut.getPackageName(), imports, cut.getTypeDecls(), cut.getSourceFile());
    }

    // XXX workaround for errors, but very inefficient and probably full of bugs
    private Collection<String> guessCandidates(CompilationInfo info, String klass, String sn) {
        System.out.println("GUESS " + klass + "." + sn);
        Pair<Map<String, List<TypeElement>>, Map<String, List<TypeElement>>> candidates = new ComputeImports().computeCandidates(info);
        Set<String> fqns = new HashSet<String>();
        if (candidates == null || candidates.a == null) {
            return fqns;
        }
        for (String k : candidates.a.keySet()) {
            if (!klass.equals(k)) {
                continue;
            }
            for (TypeElement klazz : candidates.a.get(k)) {
                String klazzFqn = klazz.getQualifiedName().toString();
                String fqn = klazzFqn + "." + sn; // NOI18N
                System.out.println(fqn);
                if (!fqns.contains(fqn) && isValidStaticMethod(info, klazzFqn, sn)) {
                    fqns.add(fqn);
                }
            }
        }
        return fqns;
    }
}
