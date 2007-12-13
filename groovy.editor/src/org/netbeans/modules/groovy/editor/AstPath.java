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

package org.netbeans.modules.groovy.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;

/**
 * This represents a path in a Groovy AST.
 *
 * @author Tor Norbye
 * @author Martin Adamek
 */
public class AstPath implements Iterable<ASTNode> {
    private ArrayList<ASTNode> path = new ArrayList<ASTNode>(30);

    public AstPath() {
    }

    public AstPath(ArrayList<ASTNode> path) {
        this.path = path;
    }

    /**
     * Initialize a node path to the given caretOffset
     */
    public AstPath(ASTNode root, int line, int column) {
        findPathTo(root, line, column);
    }

    /**
     * Find the path to the given node in the AST
     */
    @SuppressWarnings("unchecked")
    public AstPath(ASTNode node, ASTNode target) {
        if (!find(node, target)) {
            path.clear();
        } else {
            // Reverse the list such that node is on top
            // When I get time rewrite the find method to build the list that way in the first place
            Collections.reverse(path);
        }
    }

    public void descend(ASTNode node) {
        path.add(node);
    }

    public void ascend() {
        path.remove(path.size() - 1);
    }

    /**
     * Find the position closest to the given offset in the AST. Place the path from the leaf up to the path in the
     * passed in path list.
     */
    @SuppressWarnings("unchecked")
    public ASTNode findPathTo(ASTNode node, int line, int column) {
        
        ASTNode result = find(node, line, column);
        path.add(node);

        // Reverse the list such that node is on top
        // When I get time rewrite the find method to build the list that way in the first place
        Collections.reverse(path);

        return result;
    }

    @SuppressWarnings("unchecked")
    private ASTNode find(ASTNode node, int line, int column) {
        
        assert line >=0 : "line number was negative: " + line;
        assert column >=0 : "column number was negative: " + column;
        
        if (node instanceof ModuleNode) {
            List<ASTNode> children = AstUtilities.children(node);

            for (ASTNode child : children) {
                ASTNode found = find(child, line, column);

                if (found != null) {
                    path.add(child);

                    return found;
                }
            }

            return node;
        } else if (isInside(node, line, column)) {
            List<ASTNode> children = AstUtilities.children(node);

            for (ASTNode child : children) {
                ASTNode found = find(child, line, column);

                if (found != null) {
                    path.add(child);

                    return found;
                }
            }

            return node;
        } else {
            List<ASTNode> children = AstUtilities.children(node);

            for (ASTNode child : children) {
                ASTNode found = find(child, line, column);

                if (found != null) {
                    path.add(child);

                    return found;
                }
            }

            return null;
        }
    }

    private static boolean isInside(ASTNode node, int line, int column) {
        
        int beginLine = node.getLineNumber();
        int beginColumn = node.getColumnNumber();
        int endLine = node.getLastLineNumber();
        int endColumn = node.getLastColumnNumber();

        if (beginLine == endLine) {
            if (line == beginLine && column >= beginColumn && column <= endColumn) {
                return true;
            }
        } else if (line == beginLine) {
            if (column >= beginColumn) {
                return true;
            }
        } else if (line == endLine) {
            if (column <= endColumn) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
    
    /**
     * Find the path to the given node in the AST
     */
    @SuppressWarnings("unchecked")
    public boolean find(ASTNode node, ASTNode target) {
        if (node == target) {
            return true;
        }

        List<ASTNode> children = AstUtilities.children(node);

        for (ASTNode child : children) {
            boolean found = find(child, target);

            if (found) {
                path.add(child);

                return found;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path(");
        sb.append(path.size());
        sb.append(")=[");

        for (ASTNode n : path) {
            String name = n.getClass().getName();
            name = name.substring(name.lastIndexOf('.') + 1);
            sb.append(name);
            sb.append(":");
        }

        sb.append("]");

        return sb.toString();
    }

    public ASTNode leaf() {
        if (path.size() == 0) {
            return null;
        } else {
            return path.get(path.size() - 1);
        }
    }

    public ASTNode leafParent() {
        if (path.size() < 2) {
            return null;
        } else {
            return path.get(path.size() - 2);
        }
    }

    public ASTNode leafGrandParent() {
        if (path.size() < 3) {
            return null;
        } else {
            return path.get(path.size() - 3);
        }
    }

    public ASTNode root() {
        if (path.size() == 0) {
            return null;
        } else {
            return path.get(0);
        }
    }

    /** Return an iterator that returns the elements from the leaf back up to the root */
    public Iterator<ASTNode> iterator() {
        return new LeafToRootIterator(path);
    }

    /** REturn an iterator that starts at the root and walks down to the leaf */
    public ListIterator<ASTNode> rootToLeaf() {
        return path.listIterator();
    }

    /** Return an iterator that walks from the leaf back up to the root */
    public ListIterator<ASTNode> leafToRoot() {
        return new LeafToRootIterator(path);
    }

    private static class LeafToRootIterator implements ListIterator<ASTNode> {
        private final ListIterator<ASTNode> it;

        private LeafToRootIterator(ArrayList<ASTNode> path) {
            it = path.listIterator(path.size());
        }

        public boolean hasNext() {
            return it.hasPrevious();
        }

        public ASTNode next() {
            return it.previous();
        }

        public boolean hasPrevious() {
            return it.hasNext();
        }

        public ASTNode previous() {
            return it.next();
        }

        public int nextIndex() {
            return it.previousIndex();
        }

        public int previousIndex() {
            return it.nextIndex();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void set(ASTNode arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void add(ASTNode arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}