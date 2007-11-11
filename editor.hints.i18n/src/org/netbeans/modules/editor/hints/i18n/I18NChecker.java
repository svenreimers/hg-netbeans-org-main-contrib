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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.editor.hints.i18n;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.i18n.java.JavaI18nSupport;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public class I18NChecker extends AbstractHint {
    
    static Logger LOG = Logger.getLogger(I18NChecker.class.getName());
    
    private AtomicBoolean cancelled = new AtomicBoolean(false);
    
    public I18NChecker() {
        super(true, true, HintSeverity.CURRENT_LINE_WARNING);
    }

    public void cancel() {
        cancelled.set(true);
    }

    private boolean hashI18NComment(TokenHierarchy th, Document doc, long offset) throws BadLocationException {
        TokenSequence ts = th.tokenSequence();

        ts.move((int) offset);

        while (ts.moveNext()) {
            if (ts.token().id() == JavaTokenId.WHITESPACE && ts.token().text().toString().contains("\n")) {
                return false;
            }
            if (ts.token().id() == JavaTokenId.LINE_COMMENT && ts.token().text().toString().contains("NOI18N")) {
                return true;
            }
        }
        
        return false;
    }
    
    private static final Set<String> LOCALIZING_METHODS = new HashSet<String>(Arrays.asList("getString", "getBundle", "getMessage"));
    
    public List<ErrorDescription> run(CompilationInfo compilationInfo, TreePath treePath) {
        //TODO: generate unique 
        try {
            final DataObject od = DataObject.find(compilationInfo.getFileObject());
            final Document doc = compilationInfo.getDocument();
            
            if (doc == null || treePath.getParentPath() == null || !getTreeKinds().contains(treePath.getLeaf().getKind())) {
                return null;
            }
            
            //check that the treePath is a "top-level" String expression:
            TreePath expression = treePath;
            
            while (expression.getParentPath().getLeaf().getKind() == Kind.PLUS) {
                expression = expression.getParentPath();
            }
            
            if (expression != treePath) {
                return null;
            }
            
            //check for localized string:
            if (treePath.getParentPath().getLeaf().getKind() == Kind.METHOD_INVOCATION) {
                MethodInvocationTree mit = (MethodInvocationTree) treePath.getParentPath().getLeaf();
                ExpressionTree  method = mit.getMethodSelect();
                String methodName = null;
                
                switch (method.getKind()) {
                    case MEMBER_SELECT:
                        methodName = ((MemberSelectTree) method).getIdentifier().toString();
                        break;
                    case IDENTIFIER:
                        methodName = ((IdentifierTree) method).getName().toString();
                        break;
                }
                
                if (LOCALIZING_METHODS.contains(methodName)) {
                    return null;
                }
            }
            
            final long hardCodedOffset = compilationInfo.getTrees().getSourcePositions().getStartPosition(compilationInfo.getCompilationUnit(), treePath.getLeaf());
            if (hashI18NComment(compilationInfo.getTokenHierarchy(), doc, hardCodedOffset)) {
                return null;
            }
            
            if (hashI18NComment(compilationInfo.getTokenHierarchy(), doc, compilationInfo.getTrees().getSourcePositions().getStartPosition(compilationInfo.getCompilationUnit(), treePath.getLeaf()))) {
                return null;
            }
            
            BuildArgumentsVisitor v = new BuildArgumentsVisitor(compilationInfo);
            
            v.scan(treePath, null);
            
            final JavaI18nSupport support = new JavaI18nSupport(od);
            final FileObject bundleFO = od.getPrimaryFile().getParent().getFileObject("Bundle.properties"); // NOI18N
            final PropertiesDataObject bundle = (PropertiesDataObject) (bundleFO != null ? DataObject.find(bundleFO) : null); //TODO: cast
            
            Fix addToBundle = new AddToBundleFix(bundle, od, TreePathHandle.create(treePath, compilationInfo), support, v.format.toString(), v.arguments);
            
            Fix addNOI18N = new Fix() {
                public String getText() {
                    return "Add // NOI18N";
                }
                
                public ChangeInfo implement() {
                    try {
                        int line = NbDocument.findLineNumber((StyledDocument) doc, (int) hardCodedOffset);
                        int writeOffset = NbDocument.findLineOffset((StyledDocument) doc, line + 1) - 1; //TODO: last line in the document not handled correctly
                        
                        doc.insertString(writeOffset, " // NOI18N", null);
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                    return null;
                }
            };
            
            Severity severity = Severity.VERIFIER;
            
            final List<ErrorDescription> result = new ArrayList<ErrorDescription>();
            
            result.add(ErrorDescriptionFactory.createErrorDescription(severity, "Hardcoded String", Arrays.asList(new Fix[] {addToBundle, addNOI18N}), compilationInfo.getFileObject(), (int) hardCodedOffset, (int) hardCodedOffset));
            
            return result;
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        } catch (IndexOutOfBoundsException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return null;
    }
    
    private static class BuildArgumentsVisitor extends TreePathScanner<Void, Object> {
        
        private CompilationInfo info;
        private StringBuffer format = new StringBuffer();
        private List<String> arguments = new ArrayList<String>();
        private int argIndex;
        
        public BuildArgumentsVisitor(CompilationInfo info) {
            this.info = info;
        }
        
        public @Override Void visitBinary(BinaryTree tree, Object p) {
            handleTree(new TreePath(getCurrentPath(), tree.getLeftOperand()));
            handleTree(new TreePath(getCurrentPath(), tree.getRightOperand()));
            return null;
        }
        
        public @Override Void visitLiteral(LiteralTree tree, Object p) {
            format.append((String) tree.getValue());
            return null;
        }
        
        public void handleTree(TreePath tp) {
            Tree tree = tp.getLeaf();
            if (tree.getKind() == Kind.STRING_LITERAL) {
                format.append((String) ((LiteralTree) tree).getValue());
                return;
            }
            if (tree.getKind() == Kind.PLUS) {
                scan(tree, null);
                return;
            }
            SourcePositions pos = info.getTrees().getSourcePositions();
            int start = (int)pos.getStartPosition(tp.getCompilationUnit(), tree);
            int end = (int)pos.getEndPosition(tp.getCompilationUnit(), tree);            
            TypeMirror type = info.getTrees().getTypeMirror(tp);
            
            if (type != null && type.getKind() == TypeKind.DECLARED && "java.lang.String".equals((((TypeElement)((DeclaredType)type).asElement()).getQualifiedName()))) { // NOI18N
                arguments.add(info.getText().substring(start, end));
            } else {
                arguments.add("String.valueOf(" + info.getText().substring(start, end) + ")"); // NOI18N
            }
            format.append("{" + (argIndex++) + "}"); // NOI18N
        }
        
    }
    
    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.STRING_LITERAL, Kind.PLUS);
    }

    public String getId() {
        return I18NChecker.class.getName();
    }

    public String getDisplayName() {
        return "I18N Checker";
    }

    public String getDescription() {
        return "I18N Checker";
    }

}
