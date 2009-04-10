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

package org.netbeans.modules.javahints;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.java.source.support.CaretAwareJavaSourceTaskFactory;
import org.netbeans.modules.java.hints.errors.Utilities;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ExpandEnhancedForLoop extends AbstractHint {

    public ExpandEnhancedForLoop() {
        super(true, false, HintSeverity.CURRENT_LINE_WARNING);
    }
    
    @Override
    public String getDescription() {
        return "Expand Enhanced For Loop";
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.ENHANCED_FOR_LOOP);
    }

    public List<ErrorDescription> run(CompilationInfo info, TreePath tp) {
        if (tp.getLeaf().getKind() != Kind.ENHANCED_FOR_LOOP) {
            return null;
        }

        EnhancedForLoopTree efl = (EnhancedForLoopTree) tp.getLeaf();
        long statementStart = info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), efl.getStatement());
        int caret = CaretAwareJavaSourceTaskFactory.getLastPosition(info.getFileObject());

        if (caret >= statementStart) {
            return null;
        }
        
        TypeMirror expressionType = info.getTrees().getTypeMirror(new TreePath(tp, efl.getExpression()));

        if (expressionType == null || expressionType.getKind() != TypeKind.DECLARED) {
            return null;
        }

        ExecutableElement iterator = findIterable(info);
        Types t = info.getTypes();
        if (iterator == null || !t.isSubtype(((DeclaredType) expressionType), t.erasure(iterator.getEnclosingElement().asType()))) {
            return null;
        }

        FixImpl fix = new FixImpl(info.getFileObject(), TreePathHandle.create(tp, info));
        List<Fix> fixes = Collections.<Fix>singletonList(fix);
        ErrorDescription ed = ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(),
                                                                             "Convert to long for loop",
                                                                             fixes,
                                                                             info.getFileObject(),
                                                                             caret,
                                                                             caret);

        return Collections.singletonList(ed);
    }

    public String getId() {
        return ExpandEnhancedForLoop.class.getName();
    }

    public String getDisplayName() {
        return "Expand Enhanced For Loop";
    }

    public void cancel() {}

    private static ExecutableElement findIterable(CompilationInfo info) {
        TypeElement iterable = info.getElements().getTypeElement("java.lang.Iterable");

        if (iterable == null) {
            return null;
        }

        for (ExecutableElement ee : ElementFilter.methodsIn(iterable.getEnclosedElements())) {
            if (ee.getParameters().isEmpty() && ee.getSimpleName().contentEquals("iterator")) {
                return ee;
            }
        }

        return null;
    }

    private static final class FixImpl implements Fix {

        private final FileObject file;
        private final TreePathHandle forLoop;

        public FixImpl(FileObject file, TreePathHandle forLoop) {
            this.file = file;
            this.forLoop = forLoop;
        }

        public String getText() {
            return "Convert to long for loop";
        }

        public ChangeInfo implement() throws Exception {
            JavaSource source = JavaSource.forFileObject(file);
            ModificationResult mr = source.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy copy) throws Exception {
                    copy.toPhase(Phase.RESOLVED);

                    TreePath path = forLoop.resolve(copy);

                    if (path == null) {
                        return ; //XXX: log
                    }

                    EnhancedForLoopTree efl = (EnhancedForLoopTree) path.getLeaf();
                    TypeMirror expressionType = copy.getTrees().getTypeMirror(new TreePath(path, efl.getExpression()));

                    if (expressionType == null || expressionType.getKind() != TypeKind.DECLARED) {
                        return ; //XXX: log
                    }

                    ExecutableElement getIterator = findIterable(copy);
                    ExecutableType    getIteratorType = (ExecutableType) copy.getTypes().asMemberOf((DeclaredType) expressionType, getIterator);
                    TypeMirror        iteratorType = Utilities.resolveCapturedType(copy, getIteratorType.getReturnType());
                    TreeMaker         make = copy.getTreeMaker();
                    Tree              iteratorTypeTree = make.Type(iteratorType);
                    ExpressionTree    getIteratorTree = make.MethodInvocation(Collections.<ExpressionTree>emptyList(),
                                                                              make.MemberSelect(efl.getExpression(), "iterator"),
                                                                              Collections.<ExpressionTree>emptyList());
                    ExpressionTree    getNextTree = make.MethodInvocation(Collections.<ExpressionTree>emptyList(),
                                                                          make.MemberSelect(make.Identifier("it"), "next"),
                                                                          Collections.<ExpressionTree>emptyList());
                    ExpressionTree    hasNextTree = make.MethodInvocation(Collections.<ExpressionTree>emptyList(),
                                                                          make.MemberSelect(make.Identifier("it"), "hasNext"),
                                                                          Collections.<ExpressionTree>emptyList());
                    VariableTree orig = efl.getVariable();
                    VariableTree init = make.Variable(orig.getModifiers(), "it", iteratorTypeTree, getIteratorTree);
                    VariableTree value = make.Variable(orig.getModifiers(), orig.getName(), orig.getType(), getNextTree);
                    List<StatementTree> statements = new LinkedList<StatementTree>();

                    statements.add(0, value);

                    if (efl.getStatement() != null) {
                        switch (efl.getStatement().getKind()) {
                            case BLOCK:
                                BlockTree oldBlock = (BlockTree) efl.getStatement();
                                statements.addAll(oldBlock.getStatements());
                                break;
                            case EMPTY_STATEMENT:
                                break;
                            default:
                                statements.add(efl.getStatement());
                                break;
                        }
                    }

                    BlockTree newBlock = make.Block(statements, false);
                    ForLoopTree forLoop = make.ForLoop(Collections.singletonList(init), hasNextTree, Collections.<ExpressionStatementTree>emptyList(), newBlock);

                    copy.rewrite(efl, forLoop);
                }
            });

            mr.commit();

            return null;
        }

    }
}
