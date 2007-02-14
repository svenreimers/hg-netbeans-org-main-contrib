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

package org.netbeans.modules.languages.studio;

import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTPath;
import org.netbeans.api.languages.ParseException;
import org.netbeans.api.languages.ParserManager;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.tree.TreePath;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;


/**
 * Top component which displays something.
 */
final class ASTBrowserTopComponent extends TopComponent {
    
    private static final long   serialVersionUID = 1L;
    private static ASTBrowserTopComponent instance;
    private static final String PREFERRED_ID = "ASTBrowserTopComponent";
    
    private JTree               tree;
    private Listener            listener;
    private HighlighterSupport  highlighting = new HighlighterSupport (Color.yellow);

    
    private ASTBrowserTopComponent () {
        initComponents ();
        setLayout (new BorderLayout ());
        tree = new JTree ();
        tree.setCellRenderer (new Renderer ());
        tree.addTreeSelectionListener (new TreeSelectionListener () {
            public void valueChanged (TreeSelectionEvent e) {
                mark ();
            }
        });
        tree.addFocusListener (new FocusListener () {
            public void focusGained (FocusEvent e) {
                mark ();
            }
            public void focusLost (FocusEvent e) {
                mark ();
            }
        });
        tree.setShowsRootHandles (true);
        tree.setModel (new DefaultTreeModel (new TNode (null, null)));
        add (new JScrollPane (tree), BorderLayout.CENTER);
        setName (NbBundle.getMessage (ASTBrowserTopComponent.class, "CTL_ASTBrowserTopComponent"));
        setToolTipText (NbBundle.getMessage (ASTBrowserTopComponent.class, "HINT_ASTBrowserTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ASTBrowserTopComponent getDefault () {
        if (instance == null) {
            instance = new ASTBrowserTopComponent ();
        }
        return instance;
    }
    
    /**
     * Obtain the ASTBrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ASTBrowserTopComponent findInstance () {
        TopComponent win = WindowManager.getDefault ().findTopComponent (PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault ().log (ErrorManager.WARNING, "Cannot find ASTBrowser component. It will not be located properly in the window system.");
            return getDefault ();
        }
        if (win instanceof ASTBrowserTopComponent) {
            return (ASTBrowserTopComponent)win;
        }
        ErrorManager.getDefault ().log (ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault ();
    }
    
    public int getPersistenceType () {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentShowing () {
        // TODO add custom code on component opening
        refresh ();
        if (listener == null)
            listener = new Listener (this);
    }
    
    public void componentHidden () {
        // TODO add custom code on component closing
        if (listener != null) {
            listener.remove ();
            listener = null;
        }
    }
    
    /** replaces this in object stream */
    public Object writeReplace () {
        return new ResolvableHelper ();
    }
    
    protected String preferredID () {
        return PREFERRED_ID;
    }
    
    private void mark () {
        if (tree.isFocusOwner ()) {
            Node[] ns = TopComponent.getRegistry ().getActivatedNodes ();
            if (ns.length == 1) {
                EditorCookie editorCookie = (EditorCookie) ns [0].getLookup ().
                    lookup (EditorCookie.class);
                if (editorCookie != null) {
                    TNode tn = (TNode) tree.getLastSelectedPathComponent ();
                    if (tn != null) {
                        ASTItem item = tn.getASTItem ();
                        if (item == null) return;
                        highlighting.highlight (
                            editorCookie.getDocument (), 
                            item
                        );
                        return;
                    }
                }
            }
        }
        highlighting.removeHighlight ();
    }
    
    private CaretListener       caretListener;
    private JEditorPane         lastPane;
    private ASTNode             rootNode;
    
    
    private void refresh () {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ASTNode nr = getCurrentRootNode ();
                if (nr == rootNode) return;
                rootNode = nr;
                DefaultTreeModel model = new DefaultTreeModel (new TNode (null, rootNode));
                tree.setModel (model);
            }
        });
    }
    
    private ASTNode getCurrentRootNode () {
        Node[] ns = TopComponent.getRegistry ().getActivatedNodes ();
        if (ns.length != 1) return null;
        EditorCookie editorCookie = (EditorCookie) ns [0].getLookup ().
            lookup (EditorCookie.class);
        if (editorCookie == null) return null;
        if (editorCookie.getOpenedPanes () == null) return null;
        if (editorCookie.getOpenedPanes ().length < 1) return null;
        JEditorPane pane = editorCookie.getOpenedPanes () [0];
        if (caretListener == null)
            caretListener = new CListener ();
        if (lastPane != null && lastPane != pane) {
            lastPane.removeCaretListener (caretListener);
            lastPane = null;
        }
        if (lastPane == null) {
            pane.addCaretListener (caretListener);
            lastPane = pane;
        }
        Document doc = editorCookie.getDocument ();
        if (doc == null || !(doc instanceof NbEditorDocument)) return null;
        String mimeType = (String) doc.getProperty ("mimeType");
        try {
            return ParserManager.get ((NbEditorDocument) doc).getAST ();
        } catch (ParseException ex) {
            return ex.getASTNode ();
        }
    }

    
    // innerclasses ............................................................
    
    static class TNode implements TreeNode {
        
        private TNode       parent;
        private ASTItem     astItem;
        private List        children;
        private Map         map;
        
        TNode (TNode parent, ASTItem astItem) {
            this.parent = parent;
            this.astItem = astItem;
        }
        
        private void initChildren () {
            if (children != null) return;
            children = new ArrayList ();
            map = new HashMap ();
            if (astItem == null) return;
            List<ASTItem> chList = astItem.getChildren ();
            
            if (chList != null) {
                Iterator<ASTItem> it = chList.iterator ();
                while (it.hasNext ()) {
                    ASTItem item = it.next ();
                    TreeNode tn = new TNode (this, item);
                    children.add (tn);
                    map.put (item, tn);
                }
            }
        }
        
        String getName () {
            if (astItem == null)
                return "No syntax definition.";
            if (astItem instanceof ASTNode)
                return ((ASTNode) astItem).getNT ();
            return astItem.toString ();
        }
        
        ASTItem getASTItem () {
            return astItem;
        }
        
        TreeNode getTreeNode (Object o) {
            initChildren ();
            return (TreeNode) map.get (o);
        }
        
        public TreeNode getChildAt (int childIndex) {
            initChildren ();
            return (TreeNode) children.get (childIndex);
        }

        public int getChildCount () {
            initChildren ();
            return children.size ();
        }

        public TreeNode getParent () {
            return parent;
        }

        public int getIndex (TreeNode node) {
            initChildren ();
            return children.indexOf (node);
        }

        public boolean getAllowsChildren () {
            return true;
        }

        public boolean isLeaf () {
            if (astItem == null)
                return false;
            return astItem.getChildren ().isEmpty ();
        }

        public Enumeration children () {
            return new Enumeration () {
                private Iterator it = children.iterator ();
                public boolean hasMoreElements () {
                    return it.hasNext ();
                }

                public Object nextElement () {
                    return it.next ();
                }
            };
        }
    }
    
    class CListener implements CaretListener {
        public void caretUpdate (CaretEvent e) {
            if (rootNode == null) return;
            ASTPath path = rootNode.findPath (e.getDot ());
            if (path == null) return;
            TreeNode tNode = (TreeNode) tree.getModel ().getRoot ();
            List treePath = new ArrayList ();
            Iterator it = path.listIterator ();
            if (!it.hasNext ()) return;
            it.next ();
            treePath.add (tNode);
            while (tNode instanceof TNode && it.hasNext ()) {
                tNode = ((TNode) tNode).getTreeNode (it.next ());
                if (tNode == null) throw new NullPointerException ();
                treePath.add (tNode);
            }
            TreePath treePath2 = new TreePath (treePath.toArray ());
            DefaultTreeModel model = new DefaultTreeModel ((TreeNode) tree.getModel ().getRoot ());
            tree.setModel (model);
            tree.setSelectionPath (treePath2);
            tree.scrollPathToVisible (treePath2);
        }
    }

    private static class Renderer extends DefaultTreeCellRenderer {
        
        public Component getTreeCellRendererComponent (
            JTree       tree, 
            Object      value,
            boolean     sel,
            boolean     expanded,
            boolean     leaf, 
            int         row,
            boolean     hasFocus
        ) {
            return super.getTreeCellRendererComponent (
                tree, 
                ((TNode) value).getName (), 
                sel, expanded, leaf, row, hasFocus
            );
        }
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve () {
            return ASTBrowserTopComponent.getDefault ();
        }
    }
    
    private static class Listener implements PropertyChangeListener {
        
        private WeakReference component;
        
        
        Listener (ASTBrowserTopComponent c) {
            component = new WeakReference (c);
            TopComponent.getRegistry ().addPropertyChangeListener (this);
        }

        ASTBrowserTopComponent getComponent () {
            ASTBrowserTopComponent c = (ASTBrowserTopComponent) component.get ();
            if (c != null) return c;
            remove ();
            return null;
        }
        
        void remove () {
            TopComponent.getRegistry ().removePropertyChangeListener (this);
        }
        
        public void propertyChange (PropertyChangeEvent evt) {
            ASTBrowserTopComponent c = getComponent ();
            if (c == null) return;
            c.refresh ();
        }
    }
}
