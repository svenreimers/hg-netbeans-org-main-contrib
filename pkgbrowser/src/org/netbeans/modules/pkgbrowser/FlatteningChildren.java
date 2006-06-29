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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.pkgbrowser;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Children implementation which wraps a node's children and presents as the 
 * actual children the descendants of the root node to a specified depth,
 * with minor hacks to weed out "Methods" and "Constructors" and such nodes.
 *
 * @author Timothy Boudreau
 */
class FlatteningChildren extends Children.Keys implements Runnable {

    private final int depth;
    private W w = null;
    private final Node root;
    private final RequestProcessor rp;
    
    public FlatteningChildren(int depth, Node root, RequestProcessor rp) {
        this.depth = depth;
        this.root = root;
        this.rp = rp;
    }
    
    private RequestProcessor.Task task = null;
    private Object lock = new Object();
    public void addNotify() {
        setKeys (Collections.singleton (NbBundle.getMessage(FlatteningChildren.class, "LBL_WAIT")));
        synchronized (lock) {
            if (task != null) {
                task.cancel();
            }
            task = rp.post(this);
        }
    }
    
    public void removeNotify() {
         synchronized (lock) {
             if (task != null) {
                 task.cancel();
                 task = null;
             }
             if (w != null) {
                 w.detach();
                 w = null;
             }
         }
    }

    protected Node[] createNodes(Object key) {
        if (key instanceof String) {
            Node[] result = new Node[] { new AbstractNode(Children.LEAF) };
            result[0].setDisplayName(key.toString());
            return result;
        } else {
            Node n = (Node) key;
            String name = n.getName();
            if ("Fields".equals(name) || "Methods".equals(name) || "Constructors".equals(name)) {
                return new Node[0];
            } else {
//                System.err.println(root.getDisplayName() + " child " + n.getDisplayName() + " name '" + n.getName() + "'");
                return new Node[] { new FilterNode (n) };
            }
        }
    }

    public void run() {
        synchronized (lock) {
            w = new W (root, depth);
            task = null;
        }
        setKeys(w.getNodes());
    }
    
    private class W implements NodeListener {
        private final Node nd;
        private final int depth;
        private W w;
        private W[] kids;
        public W(Node nd, int depth) {
            this.nd = nd;
            this.depth = depth;
            nd.addNodeListener (this);
        }
        
        W (W w, Node nd, int depth) {
            this (nd, depth);
            this.w = w;
        }
        
        void detach() {
            nd.removeNodeListener(this);
            kids = null;
        }
        
        public Node[] getNodes() {
            if (depth != 0) {
                Node[] n = nd.getChildren().getNodes(true);
                kids = new W[n.length];
                ArrayList result = new ArrayList(n.length * 2);
                for (int i=0; i < n.length; i++) {
                    kids[i] = new W (this, n[i], depth-1);
                    result.addAll (Arrays.asList(kids[i].getNodes()));
                }
                Node[] nodes = (Node[]) result.toArray(new Node[result.size()]);
                return nodes;
            } else {
                return nd.getChildren().getNodes(true);
            }
        }
        
        public void childrenAdded(NodeMemberEvent ev) {
            sync();
        }

        public void childrenRemoved(NodeMemberEvent ev) {
            sync();
        }

        public void childrenReordered(NodeReorderEvent ev) {
            sync();
        }

        public void nodeDestroyed(NodeEvent ev) {
            sync();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            //do nothings
        }
        
        private void sync() {
            if (this.w != null) {
                this.w.sync();
            } else {
                FlatteningChildren.this.setKeys (getNodes());
            }
        }
    }
}
