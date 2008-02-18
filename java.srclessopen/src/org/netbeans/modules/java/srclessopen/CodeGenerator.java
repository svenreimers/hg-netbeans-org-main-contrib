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

package org.netbeans.modules.java.srclessopen;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.AbstractElementVisitor6;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author lahvac
 */
public class CodeGenerator {

    public static FileObject generateCode(final ClasspathInfo cpInfo, final ElementHandle<? extends Element> toOpenHandle) {
        try {
            FileObject file = FileUtil.createMemoryFileSystem().getRoot().createData("test.java");
            OutputStream out = file.getOutputStream();
            
            try {
                FileUtil.copy(new ByteArrayInputStream(""/*"class test{}"*/.getBytes("UTF-8")), out);
            } finally {
                out.close();
            }
            
            JavaSource js = JavaSource.create(cpInfo, file);
            final FileObject[] result = new FileObject[1];
            final boolean[] sourceGenerated = new boolean[1];

            ModificationResult r = js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(Phase.PARSED);
                    
                    TreeMaker make = wc.getTreeMaker();
                    Element toOpen = toOpenHandle.resolve(wc);
                    TypeElement te = toOpen != null ? wc.getElementUtilities().outermostTypeElement(toOpen) : null;

                    if (te == null) {
                        return; //TODO
                    }

                    te = wc.getElementUtilities().outermostTypeElement(te);
                    
                    ClassPath cp = ClassPathSupport.createProxyClassPath(cpInfo.getClassPath(PathKind.BOOT), cpInfo.getClassPath(PathKind.COMPILE), cpInfo.getClassPath(PathKind.SOURCE));
                    FileObject resource = cp.findResource(te.getQualifiedName().toString().replace('.', '/') + ".class");
                    
                    if (resource == null) {
                        return ;
                    }
                    
                    FileObject root = cp.findOwnerRoot(resource);
                    
                    if (root == null) {
                        return ; //???
                    }
                    
                    File sourceRoot = Index.getSourceFolder(root.getURL(), false);
                    FileObject sourceRootFO = FileUtil.toFileObject(sourceRoot);
                    
                    if (sourceRootFO == null) {
                        return ; //???
                    }
                    
                    String path = te.getQualifiedName().toString().replace('.', '/') + ".java";
                    
                    FileObject source = sourceRootFO.getFileObject(path);
                    
                    if (source != null) {
                        result[0] = source;
                        return ;
                    }
                    
                    result[0] = FileUtil.createData(sourceRootFO, path);
                    
                    Tree clazz = new TreeBuilder(make, wc).visit(te);
                    CompilationUnitTree cut = make.CompilationUnit(make.Identifier(((PackageElement) te.getEnclosingElement()).getQualifiedName()), Collections.<ImportTree>emptyList(), Collections.singletonList(clazz), wc.getCompilationUnit().getSourceFile());

                    wc.rewrite(wc.getCompilationUnit(), cut);
                    
                    sourceGenerated[0] = true;
                }
            });

            if (sourceGenerated[0]) {
                out = result[0].getOutputStream();
                
                try {
                    FileUtil.copy(new ByteArrayInputStream(r.getResultingSource(file).getBytes("UTF-8")), out);
                } finally {
                    out.close();
                }
            }
            
            return result[0];
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    private static final class TreeBuilder extends AbstractElementVisitor6<Tree, Void> {

        private TreeMaker make;
        private WorkingCopy wc;

        public TreeBuilder(TreeMaker make, WorkingCopy wc) {
            this.make = make;
            this.wc = wc;
        }
        
        public Tree visitPackage(PackageElement e, Void p) {
            throw new UnsupportedOperationException("Not supported.");
        }

        public Tree visitType(TypeElement e, Void p) {
            List<Tree> members = new LinkedList<Tree>();
            
            for (Element m : e.getEnclosedElements()) {
                Tree member = visit(m);
                
                if (member != null)
                    members.add(member);
            }
            
            ModifiersTree mods = computeMods(e.getAnnotationMirrors(),e.getModifiers());
            
            System.err.println("e.getKind()=" + e.getKind());
            
            switch (e.getKind()) {
                case CLASS:
                    return make.Class(mods, e.getSimpleName(), constructTypeParams(e.getTypeParameters()), computeSuper(e.getSuperclass()), computeSuper(e.getInterfaces()), members);
                case INTERFACE:
                    return make.Interface(mods, e.getSimpleName(), constructTypeParams(e.getTypeParameters()), computeSuper(e.getInterfaces()), members);
                case ENUM:
                    return make.Enum(mods, e.getSimpleName(), computeSuper(e.getInterfaces()), members);
                case ANNOTATION_TYPE:
                    return make.AnnotationType(mods, e.getSimpleName(), members);
                default:
                    throw new UnsupportedOperationException();
            }
        }

        private ModifiersTree computeMods(List<? extends AnnotationMirror> mirror, Set<Modifier> mods) {
            return make.Modifiers(mods); //XXX: annotations
        }
        
        private Tree computeSuper(TypeMirror superClass) {
            if (superClass == null) return null;
            if (superClass.getKind() == TypeKind.NONE) return null; //for j.l.Object
            
            return make.Type(superClass);
        }
        
        private List<Tree> computeSuper(List<? extends TypeMirror> superTypes) {
            List<Tree> sup = new LinkedList<Tree>();
            
            if (superTypes != null) {
                for (TypeMirror tm : superTypes) {
                    sup.add(make.Type(tm));
                }
            }
            
            return sup;
        }
        
        private List<? extends TypeParameterTree> constructTypeParams(List<? extends TypeParameterElement> params) {
            List<TypeParameterTree> result = new LinkedList<TypeParameterTree>();
            
            for (TypeParameterElement e : params) {
                result.add((TypeParameterTree) visit(e));
            }
            
            return result;
        }
        
        public Tree visitVariable(VariableElement e, Void p) {
            if (e.getKind() == ElementKind.ENUM_CONSTANT) {
                int mods = 1 << 14;
                ModifiersTree modifiers = make.Modifiers(mods, Collections.<AnnotationTree>emptyList());
                
                return make.Variable(modifiers,
                                     e.getSimpleName().toString(),
                                     make.Identifier(e.getEnclosingElement().getSimpleName().toString()),
                                     null);
            }
            
            ModifiersTree mods = computeMods(e.getAnnotationMirrors(),e.getModifiers());
            LiteralTree init = e.getConstantValue() != null ? make.Literal(e.getConstantValue()) : null;
            
            return make.Variable(mods, e.getSimpleName(), make.Type(e.asType()), init);
        }

        public Tree visitExecutable(ExecutableElement e, Void p) {
            if (e.getKind() == ElementKind.STATIC_INIT || e.getKind() == ElementKind.INSTANCE_INIT) {
                return null; //XXX
            }
            
            if (wc.getElementUtilities().isSynthetic(e)) {
                return null;
            }
            
            //special case: <parent>[] values(), <parent> value(String) if <parent> is enum:
            if (e.getEnclosingElement().getKind() == ElementKind.ENUM) {
                if ("values".equals(e.getSimpleName().toString()) && e.getParameters().isEmpty()) {
                    return null;
                }
                
                if ("valueOf".equals(e.getSimpleName().toString()) && e.getParameters().size() == 1) {
                    TypeMirror param = e.getParameters().get(0).asType();
                    
                    if (param.getKind() == TypeKind.DECLARED && "java.lang.String".equals(((TypeElement) ((DeclaredType) param).asElement()).getQualifiedName().toString())) {
                        return null;
                    }
                }
            }
            
            ModifiersTree mods = computeMods(e.getAnnotationMirrors(),e.getModifiers());
            Tree returnValue = e.getReturnType() != null ? make.Type(e.getReturnType()) : null;
            List<VariableTree> parameters = new LinkedList<VariableTree>();
            
            for (VariableElement param : e.getParameters()) {
                parameters.add((VariableTree) visit(param));
            }
            
            List<ExpressionTree> throwsList = new LinkedList<ExpressionTree>();
            
            for (TypeMirror t : e.getThrownTypes()) {
                throwsList.add((ExpressionTree) make.Type(t));
            }
            
            if (e.getModifiers().contains(Modifier.ABSTRACT) || e.getModifiers().contains(Modifier.NATIVE)) {
                LiteralTree def = e.getDefaultValue() != null ? make.Literal(e.getDefaultValue()) : null;
                return make.Method(mods, e.getSimpleName(), returnValue, constructTypeParams(e.getTypeParameters()), parameters, throwsList, (BlockTree) null, def);
            } else {
                return make.Method(mods, e.getSimpleName(), returnValue, constructTypeParams(e.getTypeParameters()), parameters, throwsList, "{//compiled code\nthrow new RuntimeException(\"Compiled Code\");}", null);
            }
        }

        public Tree visitTypeParameter(TypeParameterElement e, Void p) {
            List<ExpressionTree> bounds = new LinkedList<ExpressionTree>();

            for (TypeMirror b : e.getBounds()) {
                bounds.add((ExpressionTree) make.Type(b));
            }

            return make.TypeParameter(e.getSimpleName(), bounds);
        }

    }

    private CodeGenerator() {
    }
    
}
