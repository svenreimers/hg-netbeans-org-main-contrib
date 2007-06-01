/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.api.tableview;

import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

import java.awt.Component;
import java.awt.dnd.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.tree.*;
import java.util.ArrayList;


/** Support for the drag operations in the TreeView.
*
* @author Dafe Simonek, Jiri Rechtacek
*/
final class OutlineViewDragSupport extends ExplorerDragSupport {
    // Attributes
    // Associations

    /** The view that manages viewing the data in a tree. */
    protected OutlineView view;

    /** The table which we are supporting (our client) */
    private JTable table;

    /** Cell renderer - PENDING - do we need it? */

    //protected DnDTreeViewCellRenderer cellRenderer;
    // Operations

    /** Creates new TreeViewDragSupport, initializes gesture */
    public OutlineViewDragSupport(OutlineView view, JTable table) {
        this.view = view;
        this.comp = table;
        this.table = table;
    }

    public int getAllowedDragActions() {
        return view.getAllowedDragActions();
    }

    int getAllowedDropActions() {
        return view.getAllowedDropActions();
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        super.dragGestureRecognized(dge);

        // TODO
        
        // notify tree cell editor that DnD operationm is active
//        if (exDnD.isDnDActive()) {
//            TreeCellEditor tce = ((JTree) tree).getCellEditor();
//
//            if (tce instanceof TreeViewCellEditor) {
//                ((TreeViewCellEditor) tce).setDnDActive(true);
//            }
//        }
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        // get the droped nodes
        Node[] dropedNodes = exDnD.getDraggedNodes();
        super.dragDropEnd(dsde);

        // if any original glass pane was stored (the DnD was broken e.g. by Esc)
        if (DropGlassPane.isOriginalPaneStored()) {
            // give back the orig glass pane
            DropGlassPane.putBackOriginal();

            // DnD is not active
            exDnD.setDnDActive(false);
        }

        // select the droped nodes
        try {
            if (dropedNodes != null) {
                ExplorerManager.Provider panel = (ExplorerManager.Provider) SwingUtilities.getAncestorOfClass(
                        ExplorerManager.Provider.class, view
                    );

                if (panel != null) {
                    panel.getExplorerManager().setSelectedNodes(dropedNodes);
                }
            }
        } catch (Exception e) {
            // don't care
        }

        // TODO
        
        // notify tree cell editor that DnD operationm is active
        // no more
//        TreeCellEditor tce = tree.getCellEditor();
//
//        if (tce instanceof TreeViewCellEditor) {
//            ((TreeViewCellEditor) tce).setDnDActive(false);
//        }
    }

    /** Utility method. Returns either selected nodes in tree
    * (if cursor hotspot is above some selected node) or the node
    * the cursor points to.
    * @return Node array or null if position of the cursor points
    * to no node.
    */
    Node[] obtainNodes(DragGestureEvent dge) {
        int[] selRows = view.getOutline().getSelectedRows();
        ArrayList al = new ArrayList(selRows.length);
        for (int i = 0; i < selRows.length; i++) {
            Node n = view.getNodeFromRow(selRows[i]);
            if (n != null) {
                al.add(n);
            }
        }
        Node[] result = (Node[])al.toArray(new Node[al.size()]);
        return result;
        // dge.getDragOrigin()
        
//        Node[] result = new Node[tps.length];
//
//        int cnt = 0;
//
//        for (int i = 0; i < tps.length; i++) {
//            if (tree.getPathBounds(tps[i]).contains()) {
//                cnt++;
//            }
//
//            result[i] = DragDropUtilities.secureFindNode(tps[i].getLastPathComponent());
//        }
//
//        // #41954:
//        // if the drag source is not at all in path location, do not return
//        // any nodes
//        return (cnt == 0) ? null : result;
    }
}
