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

package org.netbeans.modules.javahints.tasklist;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.swing.text.Document;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.support.CancellableTreePathScanner;
import org.netbeans.editor.GuardedDocument;
import org.netbeans.editor.MarkBlock;
import org.netbeans.editor.MarkBlockChain;
import org.netbeans.modules.java.hints.infrastructure.RulesManager;
import org.netbeans.modules.java.hints.options.HintsSettings;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.java.hints.spi.TreeRule;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.tasklist.Task;
import org.openide.util.Exceptions;

/**
 *
 * @author lahvac
 */
public class ComputeTasks {

    public static List<? extends Task> computeTasks(CompilationInfo info) throws IOException {
        final Map<Kind, List<TreeRule>> hints = RulesManager.getInstance().getHints();

        if (hints.isEmpty()) {
            return Collections.<Task>emptyList();
        }

        final List<ErrorDescription> errors = new LinkedList<ErrorDescription>();

        new ScannerImpl(info, hints).scan(info.getCompilationUnit(), errors);

        List<Task> result = new LinkedList<Task>();

        for (ErrorDescription e : errors) {
            result.add(Task.create(info.getFileObject(), "nb-tasklist-warning", e.getDescription(), e.getRange().getBegin().getLine() + 1));
        }

        return result;
    }
    private static final class ScannerImpl extends CancellableTreePathScanner<Void, List<ErrorDescription>> {
        
        private Stack<Set<String>> suppresWarnings = new Stack<Set<String>>();
        private CompilationInfo info;
        private Map<Kind, List<TreeRule>> hints;
        
        public ScannerImpl(CompilationInfo info, Map<Kind, List<TreeRule>> hints) {
            this.info = info;
            this.hints = hints;
        }
        
        private void runAndAdd(TreePath path, List<TreeRule> rules, List<ErrorDescription> d) {
            if (rules != null && !isInGuarded(info, path)) {
                for (TreeRule tr : rules) {
                    if (isCanceled()) {
                        return ;
                    }
                    
                    boolean enabled = false;
                    String[] suppressedBy = null;
                    
                    if (tr instanceof AbstractHint) {
                        enabled = HintsSettings.isEnabled((AbstractHint)tr) && HintsSettings.isShowInTaskList((AbstractHint)tr);
                        suppressedBy = HintsSettings.getSuppressedBy((AbstractHint)tr);
                    }
                    
                    if ( suppressedBy != null && suppressedBy.length != 0 ) {
                        for (String wname : suppressedBy) {
                            if( !suppresWarnings.empty() && suppresWarnings.peek().contains(wname)) {
                                return;
                            }
                        }
                    }
                    
                    if (enabled) {
                        List<ErrorDescription> errors = tr.run(info, path);
                        
                        if (errors != null) {
                            d.addAll(errors);
                        }
                    }
                }
            }
        }
        
        @Override
        public Void scan(Tree tree, List<ErrorDescription> p) {
            if (tree == null)
                return null;
            
            TreePath tp = new TreePath(getCurrentPath(), tree);
            Kind k = tree.getKind();
            
            runAndAdd(tp, hints.get(k), p);
            
            if (isCanceled()) {
                return null;
            }
            
            return super.scan(tree, p);
        }
        
        @Override
        public Void scan(TreePath path, List<ErrorDescription> p) {
            Kind k = path.getLeaf().getKind();
            runAndAdd(path, hints.get(k), p);
            
            if (isCanceled()) {
                return null;
            }
            
            return super.scan(path, p);
        }

        @Override
        public Void visitMethod(MethodTree tree, List<ErrorDescription> arg1) {
            pushSuppressWarrnings();
            Void r = super.visitMethod(tree, arg1);
            suppresWarnings.pop();
            return r;
        }

        @Override
        public Void visitClass(ClassTree tree, List<ErrorDescription> arg1) {
            pushSuppressWarrnings();
            Void r = super.visitClass(tree, arg1);
            suppresWarnings.pop();
            return r;
        }

        @Override
        public Void visitVariable(VariableTree tree, List<ErrorDescription> arg1) {
            pushSuppressWarrnings();
            Void r = super.visitVariable(tree, arg1);
            suppresWarnings.pop();
            return r;
        }
        
        private void pushSuppressWarrnings( ) {
            Set<String> current = suppresWarnings.size() == 0 ? null : suppresWarnings.peek();
            Set<String> nju = current == null ? new HashSet<String>() : new HashSet<String>(current);
            
            Element e = info.getTrees().getElement(getCurrentPath());
            
            if ( e != null) {
                for (AnnotationMirror am : e.getAnnotationMirrors()) {
                    String name = ((TypeElement)am.getAnnotationType().asElement()).getQualifiedName().toString();
                    if ( "java.lang.SuppressWarnings".equals(name) ) { // NOI18N
                        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = am.getElementValues();
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                            if( "value".equals(entry.getKey().getSimpleName().toString()) ) { // NOI18N
                                Object value = entry.getValue().getValue();
                                if ( value instanceof List) {
                                    for (Object av : (List)value) {
                                        if( av instanceof AnnotationValue ) {
                                            Object wname = ((AnnotationValue)av).getValue();
                                            if ( wname instanceof String ) {
                                                nju.add((String)wname);
                                            }
                                        }
                                    }
                                    
                                }                                                                
                            }
                        }

                    }
                }                
            }
            
            suppresWarnings.push(nju);
        }
    }

    static boolean isInGuarded(CompilationInfo info, TreePath tree) {
        try {
            Document doc = info.getDocument();

            if (doc instanceof GuardedDocument) {
                int start = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), tree.getLeaf());
                int end = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), tree.getLeaf());
                GuardedDocument gdoc = (GuardedDocument) doc;
                MarkBlockChain guardedBlockChain = gdoc.getGuardedBlockChain();

                if ((guardedBlockChain.compareBlock(start, end) & MarkBlock.INSIDE) != 0) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return false;
    }

}