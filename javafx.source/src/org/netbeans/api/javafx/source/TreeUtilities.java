/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.api.javafx.source;

import com.sun.javafx.api.tree.JavaFXTree;
import com.sun.javafx.api.tree.JavaFXTreePathScanner;
import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javafx.tree.JavafxPretty;
import java.io.IOException;
import java.io.StringWriter;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Jan Lahoda, Dusan Balek, Tomas Zezula
 */
public final class TreeUtilities {

    private static final Logger logger = Logger.getLogger(TreeUtilities.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);
    
    private final CompilationInfo info;
//    private final CommentHandlerService handler;
    
    /** Creates a new instance of CommentUtilities */
    TreeUtilities(final CompilationInfo info) {
        assert info != null;
        this.info = info;
//        this.handler = CommentHandlerService.instance(info.impl.getJavacTask().getContext());
    }
    
    /**Checks whether the given tree represents a class.
     */
    public boolean isClass(ClassTree tree) {
        throw new UnsupportedOperationException();
    }
    
    /**Returns whether or not the given tree is synthetic - generated by the parser.
     * Please note that this method does not check trees transitively - a child of a syntetic tree
     * may be considered non-syntetic.
     * 
     * @return true if the given tree is synthetic, false otherwise
     * @throws NullPointerException if the given tree is null
     */
    public boolean isSynthetic(TreePath path) throws NullPointerException {
        if (path == null)
            throw new NullPointerException();
        
        while (path != null) {
            if (isSynthetic(path.getCompilationUnit(), path.getLeaf()))
                return true;
            
            path = path.getParentPath();
        }
        
        return false;
    }
    
    private boolean isSynthetic(CompilationUnitTree cut, Tree leaf) throws NullPointerException {
        JCTree tree = (JCTree) leaf;
        
        if (tree.pos == (-1))
            return true;
        
        //check for synthetic superconstructor call:
        if (leaf.getKind() == Kind.EXPRESSION_STATEMENT) {
            ExpressionStatementTree est = (ExpressionStatementTree) leaf;
            
            if (est.getExpression().getKind() == Kind.METHOD_INVOCATION) {
                MethodInvocationTree mit = (MethodInvocationTree) est.getExpression();
                
                if (mit.getMethodSelect().getKind() == Kind.IDENTIFIER) {
                    IdentifierTree it = (IdentifierTree) mit.getMethodSelect();
                    
                    if ("super".equals(it.getName().toString())) {
                        SourcePositions sp = info.getTrees().getSourcePositions();
                        
                        return sp.getEndPosition(cut, leaf) == (-1);
                    }
                }
            }
        }
        
        return false;
    }
    
    /**Returns list of comments attached to a given tree. Can return either
     * preceding or trailing comments.
     *
     * @param tree for which comments should be returned
     * @param preceding true if preceding comments should be returned, false if trailing comments should be returned.
     * @return list of preceding/trailing comments attached to the given tree
     */
//    public List<Comment> getComments(Tree tree, boolean preceding) {
//        CommentSetImpl set = handler.getComments(tree);
//        
//        if (!set.areCommentsMapped()) {
//            boolean assertsEnabled = false;
//            boolean automap = true;
//            
//            assert assertsEnabled = true;
//            
//            if (assertsEnabled) {
//                TreePath tp = TreePath.getPath(info.getCompilationUnit(), tree);
//                
//                if (tp == null) {
//                    Logger.getLogger(TreeUtilities.class.getName()).log(Level.WARNING, "Comment automap requested for Tree not from the root compilation info. Please, make sure to call GeneratorUtilities.importComments before Treeutilities.getComments. Tree: {0}", tree);
//                    Logger.getLogger(TreeUtilities.class.getName()).log(Level.WARNING, "Caller", new Exception());
//                    automap = false;
//                }
//            }
//            
//            if (automap) {
//                try {
//                    TokenSequence<JFXTokenId> seq = ((SourceFileObject) info.getCompilationUnit().getSourceFile()).getTokenHierarchy().tokenSequence(JFXTokenId.language());
//                    new TranslateIdentifier(info, true, false, seq).translate(tree);
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//        }
//        
//        List<Comment> comments = preceding ? set.getPrecedingComments() : set.getTrailingComments();
//        
//        return Collections.unmodifiableList(comments);
//    }
    
    public TreePath pathFor(int pos) {
        return pathFor(new TreePath(info.getCompilationUnit()), pos);
    }

    /*XXX: dbalek
     */
    public TreePath pathFor(TreePath path, int pos) {
        return pathFor(path, pos, info.getTrees().getSourcePositions());
    }

    /*XXX: dbalek
     */
    public TreePath pathFor(TreePath path, int pos, SourcePositions sourcePositions) {
        if (info == null || path == null || sourcePositions == null)
            throw new IllegalArgumentException();
        
        class Result extends Error {
            TreePath path;
            Result(TreePath path) {
                this.path = path;
            }
        }
        
        class PathFinder extends JavaFXTreePathScanner<Void,Void> {
            private int pos;
            private SourcePositions sourcePositions;
            
            private PathFinder(int pos, SourcePositions sourcePositions) {
                this.pos = pos;
                this.sourcePositions = sourcePositions;
            }
            
            public Void scan(Tree tree, Void p) {
                if (tree != null) {
                    super.scan(tree, p);
                    long start = sourcePositions.getStartPosition(getCurrentPath().getCompilationUnit(), tree);
                    long end = sourcePositions.getEndPosition(getCurrentPath().getCompilationUnit(), tree);
                    if (start < pos && end >= pos) {
                        if (tree.getKind() == Tree.Kind.ERRONEOUS) {
                            tree.accept(this, p);
                            throw new Result(getCurrentPath());
                        }
                        throw new Result(new TreePath(getCurrentPath(), tree));
                    } else {
                        if ((start == -1) || (end == -1)) {
                            if (!isSynthetic(getCurrentPath().getCompilationUnit(), tree)) {
                                // here we might have a problem
                                if (LOGGABLE) {
                                    logger.finest("SCAN: Cannot determine start and end for: " + treeToString(info, tree));
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }
        
        try {
            new PathFinder(pos, sourcePositions).scan(path, null);
        } catch (Result result) {
            path = result.path;
        }
        
        if (path.getLeaf() == path.getCompilationUnit()) {
            log("pathFor returning compilation unit for position: " + pos);
            return path;
        }
        int start = (int)sourcePositions.getStartPosition(info.getCompilationUnit(), path.getLeaf());
        int end   = (int)sourcePositions.getEndPosition(info.getCompilationUnit(), path.getLeaf());
        while ((start == -1) || (end == -1)) {
            if (LOGGABLE) {
                logger.finer("pathFor moving to parent: " + treeToString(info, path.getLeaf()));
            }
            path = path.getParentPath();
            if (LOGGABLE) {
                logger.finer("pathFor moved to parent: " + treeToString(info, path.getLeaf()));
            }
            if (path.getLeaf() == path.getCompilationUnit()) {
                break;
            }
            start = (int)sourcePositions.getStartPosition(info.getCompilationUnit(), path.getLeaf());
            end   = (int)sourcePositions.getEndPosition(info.getCompilationUnit(), path.getLeaf());
        }
        if (LOGGABLE) {
            logger.finer("pathFor before checking the tokens: " + treeToString(info, path.getLeaf()));
        }
        TokenSequence<JFXTokenId> tokenList = tokensFor(path.getLeaf(), sourcePositions);
        tokenList.moveEnd();
        if (tokenList.movePrevious() && tokenList.offset() < pos) {
            switch (tokenList.token().id()) {
                case GT:
                    if (path.getLeaf().getKind() == Tree.Kind.MEMBER_SELECT || path.getLeaf().getKind() == Tree.Kind.CLASS || path.getLeaf().getKind() == Tree.Kind.GREATER_THAN)
                        break;
                case RPAREN:
                    if (path.getLeaf().getKind() == Tree.Kind.ENHANCED_FOR_LOOP || path.getLeaf().getKind() == Tree.Kind.FOR_LOOP ||
                            path.getLeaf().getKind() == Tree.Kind.IF || path.getLeaf().getKind() == Tree.Kind.WHILE_LOOP ||
                            path.getLeaf().getKind() == Tree.Kind.DO_WHILE_LOOP || path.getLeaf().getKind() == Tree.Kind.TYPE_CAST)
                        break;
                case SEMI:
                    if (path.getLeaf().getKind() == Tree.Kind.FOR_LOOP &&
                            tokenList.offset() <= sourcePositions.getStartPosition(path.getCompilationUnit(), ((ForLoopTree)path.getLeaf()).getUpdate().get(0)))
                        break;
                case RBRACE:
                    path = path.getParentPath();
                    switch (path.getLeaf().getKind()) {
                        case CATCH:
                            path = path.getParentPath();
                        case METHOD:
                        case FOR_LOOP:
                        case ENHANCED_FOR_LOOP:
                        case IF:
                        case SYNCHRONIZED:
                        case WHILE_LOOP:
                        case TRY:
                            path = path.getParentPath();
                    }
                    break;
            }
        }
        if (LOGGABLE) {
            log("pathFor(pos: " + pos + ") returning: " + treeToString(info, path.getLeaf()));
        }
        return path;
    }
    
    /**Computes {@link Scope} for the given position.
     */
    public Scope scopeFor(int pos) {
        List<? extends StatementTree> stmts = null;
        SourcePositions sourcePositions = info.getTrees().getSourcePositions();
        TreePath path = pathFor(pos);
        CompilationUnitTree root = path.getCompilationUnit();
        switch (path.getLeaf().getKind()) {
            case BLOCK:
                stmts = ((BlockTree)path.getLeaf()).getStatements();
                break;
            case FOR_LOOP:
                stmts = ((ForLoopTree)path.getLeaf()).getInitializer();
                break;
            case ENHANCED_FOR_LOOP:
                stmts = Collections.singletonList(((EnhancedForLoopTree)path.getLeaf()).getStatement());
                break;
            case METHOD:
                stmts = ((MethodTree)path.getLeaf()).getParameters();
                break;
        }
        if (stmts != null) {
            Tree tree = null;
            for (StatementTree st : stmts) {
                if (sourcePositions.getStartPosition(root, st) < pos)
                    tree = st;
            }
            if (tree != null)
                path = new TreePath(path, tree);
        }
        Scope scope = info.getTrees().getScope(path);
        if (path.getLeaf().getKind() == Tree.Kind.CLASS) {
            TokenSequence<JFXTokenId> ts = info.getTokenHierarchy().tokenSequence(JFXTokenId.language());
            ts.move(pos);
            while(ts.movePrevious()) {
                switch (ts.token().id()) {
                    case WS:
                    case LINE_COMMENT:
                    case COMMENT:
                    case DOC_COMMENT:
                        break;
                    default:
                        return scope;
                }
            }
        }
        return scope;
    }
    
    /**Returns tokens for a given tree.
     */
    public TokenSequence<JFXTokenId> tokensFor(Tree tree) {
        return tokensFor(tree, info.getTrees().getSourcePositions());
    }
    
    /**Returns tokens for a given tree. Uses specified {@link SourcePositions}.
     */
    public TokenSequence<JFXTokenId> tokensFor(Tree tree, SourcePositions sourcePositions) {
        int start = (int)sourcePositions.getStartPosition(info.getCompilationUnit(), tree);
        int end   = (int)sourcePositions.getEndPosition(info.getCompilationUnit(), tree);
        if ((start == -1) || (end == -1)) {
            throw new RuntimeException("RE Cannot determine start and end for: " + treeToString(info, tree));
        }
        TokenSequence<JFXTokenId> t = info.getTokenHierarchy().tokenSequence(JFXTokenId.language());
        if (t == null) {
            throw new RuntimeException("RE SDid not get a token sequence.");
        }
        return t.subSequence(start, end);
    }
    
    private static String treeToString(CompilationInfo info, Tree t) {
        JavaFXTree.JavaFXKind k = null;
        StringWriter s = new StringWriter();
        try {
            new JavafxPretty(s, false).printExpr((JCTree)t);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        if (t instanceof JavaFXTree && t.getKind() == Kind.OTHER) {
            JavaFXTree jfxt = (JavaFXTree)t;
            k = jfxt.getJavaFXKind();
        }
        String res = null;
        if (k != null) {
            res = k.toString();
        } else {
            res = String.valueOf(t.getKind());
        }
        SourcePositions pos = info.getTrees().getSourcePositions();
        res = res + '[' + pos.getStartPosition(info.getCompilationUnit(), t) + ',' + 
                pos.getEndPosition(info.getCompilationUnit(), t) + "]:" + s.toString();
        return res;
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
