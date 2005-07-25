/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views;

import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.accessibility.*;

import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.explorer.*;
import org.openide.util.WeakListeners;
import org.openide.nodes.*;
import org.openide.util.actions.*;

import org.netbeans.modules.vcscore.util.table.*;

/** Explorer view to display items in a list.
 * @author Milos Kleint
*/
public class TableView extends JScrollPane implements Externalizable {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 0;

    /** Explorer manager to work with. Is not null only if the component is showing
    * in components hierarchy
    */
    private transient ExplorerManager manager;

    /** The actual JList list */
    transient protected JTable table;
    /** model to use */
    transient protected TableInfoModel model;


    //
    // listeners
    //

    /** Listener to nearly everything */
    transient Listener managerListener;

    /** weak variation of the listener for property change on the explorer manager */
    transient PropertyChangeListener wlpc;
    /** weak variation of the listener for vetoable change on the explorer manager */
    transient VetoableChangeListener wlvc;

    /** popup */
    transient PopupAdapter popupListener;

    //
    // properties
    //

    /** if true, the icon view displays a popup on right mouse click, if false, the popup is not displayed */
    private boolean popupAllowed = true;
    /** if true, the hierarchy traversal is allowed, if false, it is disabled */
    private boolean traversalAllowed = true;

    /** action preformer */
    private ActionListener defaultProcessor;

    /** True, if the selection listener is attached. */
    transient boolean listenerActive;

    private String compositeAttributeName;

    private transient FileVcsInfo.Composite composite = null;
    private transient FileVcsInfo info = null;
    
    
    private CallableSystemAction[] actions;

    // init .................................................................................

    /** Default constructor.
    */
    public TableView(String compositeAttributeName, TableInfoModel model) {
        this.model = model;
        initializeTable();
        this.compositeAttributeName = compositeAttributeName;
    }

    /** Initializes the table & model.
    */
    private void initializeTable () {
        // initilizes the JTree
        table = createTable();
        table.setModel (model);
        javax.swing.table.JTableHeader head = table.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(table);
        head.addMouseListener(listen);

        setViewportView(table);

/*        {
            AbstractAction action = new GoUpAction ();
            KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
            table.registerKeyboardAction(action, key, JComponent.WHEN_FOCUSED);
        }

        {
            AbstractAction action = new EnterAction ();
            KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            table.registerKeyboardAction(action, key, JComponent.WHEN_FOCUSED);
        }
 */

        managerListener = new Listener ();
        popupListener = new PopupAdapter ();

        model.addTableModelListener(managerListener);

        table.addMouseListener(managerListener);
        table.addMouseListener(popupListener);
        table.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        ToolTipManager.sharedInstance ().registerComponent (table);
        
        table.addFocusListener(new FocusAdapter());
    }


    /*
    * Write view's state to output stream.
    */
    public void writeExternal (ObjectOutput out) throws IOException {
        out.writeObject (popupAllowed ? Boolean.TRUE : Boolean.FALSE);
        out.writeObject (traversalAllowed ? Boolean.TRUE : Boolean.FALSE);
        out.writeObject (compositeAttributeName);
        // TODO.. write the tableModel??
    }

    /*
    * Reads view's state form output stream.
    */
    public void readExternal (ObjectInput in) throws IOException, ClassNotFoundException {
        popupAllowed = ((Boolean)in.readObject ()).booleanValue ();
        traversalAllowed = ((Boolean)in.readObject ()).booleanValue ();
        compositeAttributeName = in.readUTF();
    }


    // properties ...........................................................................

    /** Test whether display of a popup menu is enabled.
     * @return <code>true</code> if so */
    public boolean isPopupAllowed () {
        return popupAllowed;
    }

    /** Enable/disable displaying popup menus on list view items. Default is enabled.
    * @param value <code>true</code> to enable
    */
    public void setPopupAllowed (boolean value) {
        popupAllowed = value;
    }

    /** Test whether hierarchy traversal shortcuts are permitted.
    * @return <code>true</code> if so */
    public boolean isTraversalAllowed () {
        return false;
    }

    /** Enable/disable hierarchy traversal using <code>CTRL+click</code> (down) and <code>Backspace</code> (up), default is enabled.
    * @param value <code>true</code> to enable
    */
    public void setTraversalAllowed (boolean value) {
        traversalAllowed = value;
        // IGNORED.. table cannot be traversed..??
    }

    /** Get the current processor for default actions.
    * If not <code>null</code>, double-clicks or pressing Enter on 
    * items in the view will not perform the default action on the selected node; rather the processor 
    * will be notified about the event.
    * @return the current default-action processor, or <code>null</code>
    */
    public ActionListener getDefaultProcessor () {
        return defaultProcessor;
    }

    /** Set a new processor for default actions.
    * @param value the new default-action processor, or <code>null</code> to restore use of the selected node's declared default action
    * @see #getDefaultProcessor
    */
    public void setDefaultProcessor (ActionListener value) {
        defaultProcessor = value;
    }


    /** Get the selection mode.
     * @return the mode
     * @see #setSelectionMode
     */
    public int getSelectionMode() {
        return table.getSelectionModel().getSelectionMode();
    }
    
    public void setColumnModel(TableColumnModel model) {
        table.setColumnModel(model);
    }
    
    /**
     * finds and selects rows in the model.
     * @param items items to select. if null, will clear the selection.
     * @param fireEvent fires the table update event which in turn will fire
     * property change on the Selected items in the FileVcsInfo.Composite
     */
    public void setSelectedItems(FileVcsInfo.CompositeItem[] items, boolean fireEvent) {
        if (items != null) {
            int[] rows = new int[items.length];
            table.clearSelection();
            if (!fireEvent) {
                table.getSelectionModel().setValueIsAdjusting(true);
            }
            for (int i = 0; i < items.length; i++) {
                rows[i] = model.getElementRow(items[i]);
                if (fireEvent) {
                    table.changeSelection(rows[i], 0, false, false);
                } else {
                    table.getSelectionModel().addSelectionInterval(rows[i], rows[i]);
                }
            }
            if (!fireEvent) {
                table.getSelectionModel().setValueIsAdjusting(false);
            }
        } else {
            if (fireEvent) {
                table.clearSelection();
            } else {
                table.getSelectionModel().clearSelection();
            }
        }
    }
    
    
    public void setAdditionalActions(CallableSystemAction[] actions) {
        this.actions = actions;
    }

    /********** Support for the Drag & Drop operations *********/

    /** @return true if dragging from the view is enabled, false
    * otherwise.<br>
    * Drag support is disabled by default.
    */
    public boolean isDragSource () {
        return false;
    }

    /** Enables/disables dragging support.
    * @param state true enables dragging support, false disables it.
    */
    public void setDragSource (boolean state) {
/*        if (state == dragActive)
            return;
        dragActive = state;
        // create drag support if needed
        if (dragActive && (dragSupport == null))
            dragSupport = new ListViewDragSupport(this, list);
        // activate / deactivate support according to the state
        dragSupport.activate(dragActive);
 */
    }

    /** @return true if dropping to the view is enabled, false
    * otherwise<br>
    * Drop support is disabled by default.
    */
    public boolean isDropTarget () {
        return false;
    }

    /** Enables/disables dropping support.
    * @param state true means drops into view are allowed,
    * false forbids any drops into this view.
    */
    public void setDropTarget (boolean state) {
/*        if (state == dropActive)
            return;
        dropActive = state;
        // create drop support if needed
        if (dropActive && (dropSupport == null))
            dropSupport = new ListViewDropSupport(this, list);
        // activate / deactivate support according to the state
        dropSupport.activate(dropActive);
 */
    }

    /** @return Set of actions which are allowed when dragging from
    * asociated component.
    * Actions constants comes from DnDConstants.XXX constants.
    * All actions (copy, move, link) are allowed by default.
    */
/*    public int getAllowedDragActions () {
        // PENDING
        return DnDConstants.ACTION_MOVE | DnDConstants.ACTION_COPY |
               DnDConstants.ACTION_LINK;
    }
 */

    /** Sets allowed actions for dragging
    * @param actions new drag actions, using DnDConstants.XXX 
    */  
/*    public void setAllowedDragActions (int actions) {
        // PENDING
    }
 */

    /** @return Set of actions which are allowed when dropping
    * into the asociated component.
    * Actions constants comes from DnDConstants.XXX constants.
    * All actions are allowed by default.
    */
/*    public int getAllowedDropActions () {
        // PENDING
        return DnDConstants.ACTION_MOVE | DnDConstants.ACTION_COPY |
               DnDConstants.ACTION_LINK;
    }
 */

    /** Sets allowed actions for dropping.
    * @param actions new allowed drop actions, using DnDConstants.XXX 
    */  
/*    public void setAllowedDropActions (int actions) {
        // PENDING
    }
 */


    //
    // Methods to override
    //

    /** Creates the list that will display the data.
    */
    protected JTable createTable () {
        JTable table = new NbTable ();
        return table;
    }

    public TableInfoModel getTableModel() {
        return model;
    }
    
    

    /** Called when the list changed selection and the explorer manager
    * should be updated.
    * @param nodes list of nodes that should be selected
    * @param em explorer manager
    * @exception PropertyVetoException if the manager does not allow the
    *   selection
    */
    protected void selectionChanged (Node[] nodes, ExplorerManager em)
        throws PropertyVetoException {
        em.setSelectedNodes (nodes);
    }

    /** Called when explorer manager is about to change the current selection.
    * The view can forbid the change if it is not able to display such
    * selection.
    *
    * @param nodes the nodes to select
    * @return false if the view is not able to change the selection
    */
    protected boolean selectionAccept (Node[] nodes) {
        return true;
//TODO
/*        // if the selection is just the root context, confirm the selection
        if (nodes.length == 1 && manager.getRootContext().equals(nodes[0])) {
            return true;
        }

        Node cntx = manager.getExploredContext ();

        // we do not allow selection in other than the exploredContext
        for (int i = 0; i < nodes.length; i++) {
            VisualizerNode v = VisualizerNode.getVisualizer (null, nodes[i]);
            if (model.getIndex (v) == -1) {
                return false;
            }
        }

        return true;
 */
    }


    //
    // Working methods
    //
    
    private void fillModelWithData(Node node) {
        clearModel();
        info = (FileVcsInfo)node.getCookie(FileVcsInfo.class);
        info.addPropertyChangeListener(managerListener);
        Object obj = info.getAttribute(compositeAttributeName);
        if (obj != null && obj instanceof FileVcsInfo.Composite) {
            composite = (FileVcsInfo.Composite)obj;
            if (composite.getCount() > 0) {
                for (int i = 0; i < composite.getCount(); i++) {
                    model.addElement(composite.getRow(i));
                }
            }
        }
    }
    
    private void clearModel() {
        if (info != null) {
            info.removePropertyChangeListener(managerListener);
            info = null;
        }
        if (composite != null) {
            FileVcsInfo.CompositeItem[] items = null;
            composite.setSelectedItems(items);
            composite = null;
        }
        model.clear();
    }


    /* Initilizes the view.
    */
    public void addNotify () {
        super.addNotify ();
        // run under mutex

        ExplorerManager em = ExplorerManager.find (this);

        if (em != manager) {
            if (manager != null) {
                manager.removeVetoableChangeListener (wlvc);
                manager.removePropertyChangeListener (wlpc);
            }

            manager = em;

            manager.addVetoableChangeListener(wlvc = WeakListeners.vetoableChange (managerListener, manager));
            manager.addPropertyChangeListener(wlpc = WeakListeners.propertyChange (managerListener, manager));
            
            updateSelection();
        };
        if (!listenerActive) {
            listenerActive = true;
            table.getSelectionModel ().addListSelectionListener (managerListener);
        }
    }

    /** Removes listeners.
    */
    public void removeNotify () {
        super.removeNotify ();
        listenerActive = false;
        table.getSelectionModel ().removeListSelectionListener (managerListener);
        if (info != null) {
             info.removePropertyChangeListener(managerListener);
        }
    }

    /* Requests focus for the list component. Overrides superclass method. */
    public void requestFocus () {
        table.requestFocus();
    }
    
    /* Requests focus for the list component. Overrides superclass method. */
    public boolean requestFocusInWindow () {
        return table.requestFocusInWindow();
    }

    /** This method is called when user double-clicks on some object or
    * presses Enter key.
    * @param index Index of object in current explored context
    */
    final void performObjectAt(int index, int modifiers) {
        if (index < 0 || index >= model.getRowCount()) {
            return;
        }

/*TODO        VisualizerNode v = (VisualizerNode)model.getElementAt (index);
        Node node = v.node;

        // if DefaultProcessor is set, the default action is notified to it overriding the default action on nodes
        if (defaultProcessor != null) {
            defaultProcessor.actionPerformed (new ActionEvent (node, 0, null, modifiers));
            return;
        }

        // on double click - invoke default action, if there is any
        // (unless user holds CTRL key what means that we should always dive into the context)
        SystemAction sa = node.getDefaultAction ();
        if (sa != null && (modifiers & java.awt.event.InputEvent.CTRL_MASK) == 0) {
            TreeView.invokeAction
                (sa, new ActionEvent (node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
        }
        // otherwise dive into the context
        else if (traversalAllowed && (!node.isLeaf()))
            manager.setExploredContext (node, manager.getSelectedNodes());
 */
    }

    /** Called when selection has been changed.
    */
    private void updateSelection() {
        Node[] nodes = manager.getSelectedNodes();
        if (nodes != null && nodes.length == 1 && nodes[0].getCookie(FileVcsInfo.class) != null) {
            fillModelWithData(nodes[0]);
            java.util.Collections.sort(model.getList(), model);
        } else {
            clearModel();
            //TODO - put a blank node here..
        }
        table.tableChanged(new javax.swing.event.TableModelEvent(model));
//??        table.repaint();
        
        /*        Node[] sel = manager.getSelectedNodes ();
        int[] indices = new int[sel.length];

        for (int i = 0; i < sel.length; i++) {
            VisualizerNode v = VisualizerNode.getVisualizer (null, sel[i]);
            indices[i] = model.getIndex (v);
        }

        // going to change list because of E.M.'s order -- temp disable the
        // listener
        if (listenerActive)
            list.getSelectionModel ().removeListSelectionListener(managerListener);
        try {
            showSelection (indices);
        } finally {
            if (listenerActive)
                list.getSelectionModel ().addListSelectionListener(managerListener);
        }
 */
    }


    // innerclasses .........................................................................

    /** Enhancement of standart JList.
    */
    final class NbTable extends JTable {
        static final long serialVersionUID = 0;

        /**
         * Overrides JComponent's getToolTipText method in order to allow 
         * renderer's tips to be used if it has text set.
         * <p>
         * NOTE: For JTree to properly display tooltips of its renderers
         *       JTree must be a registered component with the ToolTipManager.
         *       This can be done by invoking
         *       <code>ToolTipManager.sharedInstance().registerComponent(tree)</code>.
         *       This is not done automaticly!
         *
         * @param event the MouseEvent that initiated the ToolTip display
         */
        public String getToolTipText (MouseEvent event) {
            if (event != null) {
                Point p = event.getPoint ();
                int row = this.rowAtPoint(p);
                int column = this.columnAtPoint(p);
                if (row >= 0 && column >= 0) {
                    Object obj = model.getValueAt (row, column);
                    if ((obj != null)) {
                        return obj.toString();
                    }
                }
            }
            return null;
        }

/* TODO
 public AccessibleContext getAccessibleContext() {
            if (accessibleContext == null) {
                accessibleContext = new AccessibleExplorerTable();
            }
            return accessibleContext;
        }

        private class AccessibleExplorerTable extends AccessibleJTable {
            public String getAccessibleName() {
                return TableView.this.getAccessibleContext().getAccessibleName();
            }
            public String getAccessibleDescription() {
                return TableView.this.getAccessibleContext().getAccessibleDescription();
            }
        }
 */
    }

    /** Popup menu listener. */
    private final class PopupAdapter extends
        org.openide.awt.MouseUtils.PopupMouseAdapter {
        
        public PopupAdapter () {}
        
        protected void showPopup (MouseEvent e) {
            int i = table.rowAtPoint(new Point (e.getX (), e.getY ()));
            if (!(table.getSelectedRow() == i)) {
                table.getSelectionModel().setSelectionInterval(i, i);
            }
            createPopup(e.getX(), e.getY());
        }
    } // end of PopupAdapter
    
    void createPopup(int xpos, int ypos) {
        if (manager == null) {
            return;
        }
        if (!popupAllowed) {
            return;
        }
        JPopupMenu popup = NodeOp.findContextMenu(manager.getSelectedNodes());
        if ((popup != null) && (popup.getSubElements().length > 0)/* && (TreeView.shouldPopupBeDisplayed(TableView.this)) */) {
            if (actions != null && actions.length > 0) {
                MenuElement[] elements = popup.getSubElements();
                if (elements != null && elements.length > 1) {
                    if (!elements[0].getComponent().getClass().equals(javax.swing.JSeparator.class)) {
//                        System.out.println("adding separator...");
                        popup.insert(new JSeparator(), 0);
                    }
                }
                for (int i = 0; i < actions.length; i++) {
                    CallableSystemAction act = actions[i];
                    popup.insert(act.getPopupPresenter(), i);
                }
            }
            java.awt.Point p = getViewport().getViewPosition();
            p.x = xpos - p.x;
            p.y = ypos - p.y;
            SwingUtilities.convertPointToScreen(p, TableView.this);
            Dimension popupSize = popup.getPreferredSize ();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (p.x + popupSize.width > screenSize.width) p.x = screenSize.width - popupSize.width;
            if (p.y + popupSize.height > screenSize.height) p.y = screenSize.height - popupSize.height;
            SwingUtilities.convertPointFromScreen(p, TableView.this);
//            popup.addPopupMenuListener(new TableView.MyPopupMenuListener());
            popup.show(this, p.x, p.y);
        }
    }
    
/*    private static final class MyPopupMenuListener implements PopupMenuListener {
        
        public void popupMenuCanceled(javax.swing.event.PopupMenuEvent popupMenuEvent) {
            System.out.println("cancelled popup menu..");
        }
        
        public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent popupMenuEvent) {
            System.out.println("will become invisible..");
        }
        
        public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent popupMenuEvent) {
        }
        
    }
 */
    
    final class PopupPerformer implements org.openide.util.actions.ActionPerformer {
        public void performAction(SystemAction act) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ListSelectionModel mod = table.getSelectionModel();
                    boolean multisel = (mod.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION);
                    int i = (multisel ? mod.getLeadSelectionIndex() : mod.getLeadSelectionIndex());
                    if (i < 0) {
                        return;
                    }
                    Rectangle rect = table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), false);
                    Point p = rect.getLocation();
                    if (p == null) {
                        return;
                    }
                    createPopup(p.x, p.y);
                }
            });
        }
    }
    
    final class FocusAdapter implements java.awt.event.FocusListener {
        
        CallbackSystemAction csa;
        PopupPerformer performer;

        public void focusGained(java.awt.event.FocusEvent ev) {
            if (csa == null) {
                try {
                    Class popup = Class.forName("org.openide.actions.PopupAction"); // NOI18N
                    csa = (CallbackSystemAction) CallbackSystemAction.get(popup);
                    performer = new PopupPerformer();
                } catch (ClassNotFoundException e) {
                    Error err = new NoClassDefFoundError();
                    ErrorManager em = (ErrorManager)
                        org.openide.util.Lookup.getDefault ().lookup (ErrorManager.class);
                    if (em != null) {
                        em.annotate(err, e);
                    }
                    throw err;
                }
            }
            csa.setActionPerformer(performer);
            //ev.consume();
        }
        
        public void focusLost(java.awt.event.FocusEvent ev) {
            if (csa != null && (csa.getActionPerformer() instanceof PopupPerformer)) {
                csa.setActionPerformer(null);
            }
//            System.out.println("focuslost");
        }
    }

    /**
    */
    private final class Listener extends MouseAdapter
        implements TableModelListener, ListSelectionListener,
        PropertyChangeListener, VetoableChangeListener {
        
        public Listener () {}
        
        public void tableChanged(TableModelEvent e) {
//            updateSelection();
        }
        
        
        public void mouseClicked(MouseEvent e) {
            if (MouseUtils.isDoubleClick(e)) {
/**TODO                int index = list.locationToIndex(e.getPoint());
                performObjectAt(index, e.getModifiers());
 */
            }
        }


        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (manager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] newNodes = (Node[])evt.getNewValue();
                if (!selectionAccept (newNodes)) {
                    throw new PropertyVetoException("", evt); // NOI18N
                }
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (manager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                updateSelection();
                return;
            }

            if (ExplorerManager.PROP_EXPLORED_CONTEXT.equals(evt.getPropertyName())) {
                updateSelection();
//                model.setNode (manager.getExploredContext ());
                //System.out.println("Children: " + java.util.Arrays.asList (list.getValues ())); // NOI18N
                return;
            }
            if (evt.getSource() != null && evt.getSource() instanceof FileVcsInfo) {
                FileVcsInfo inf = (FileVcsInfo)evt.getSource();
                if (evt.getPropertyName().equals(TableView.this.compositeAttributeName)) {
//                    System.out.println("changed the composite attribute..");
                    updateSelection();
                }
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            int[] rows = table.getSelectedRows();
            if (rows.length > 0 && composite != null) {
                FileVcsInfo.CompositeItem[] items = new FileVcsInfo.CompositeItem[rows.length];
                for (int i = 0; i < rows.length; i++) {
                    items[i] = (FileVcsInfo.CompositeItem)model.getElementAt(rows[i]);
                }
                composite.setSelectedItems(items);
            } else {
                if (composite != null) {
                    composite.setSelectedItems(null);
                }
            }
                
/*            Object[] values = table.getSelectedValues ();
            Node[] nodes = new Node[values.length];

            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = Visualizer.findNode (values[i]);
            }

            // forwarding TO E.M., so we won't listen to its cries for a while
            manager.removePropertyChangeListener (wlpc);
            manager.removeVetoableChangeListener (wlvc);
            try {
                selectionChanged (nodes, manager);
            } catch (java.beans.PropertyVetoException ex) {
                // selection vetoed - restore previous selection
                updateSelection();
            } finally {
                manager.addPropertyChangeListener (wlpc);
                manager.addVetoableChangeListener (wlvc);
            }
 */
        }
    }

    /*
    // Backspace jumps to parent folder of explored context
    private final class GoUpAction extends AbstractAction {
        static final long serialVersionUID =1599999335583246715L;
        public GoUpAction () {
            super ("GoUpAction"); // NOI18N
        }

        public void actionPerformed(ActionEvent e) {
            if (traversalAllowed) {
                Node pan = manager.getExploredContext();
                pan = pan.getParentNode();
                if (pan != null)
                    manager.setExploredContext(pan, manager.getSelectedNodes());
            }
        }
        public boolean isEnabled() {
            return true;
        }
    }

    //Enter key performObjectAt selected index.
    private final class EnterAction extends AbstractAction {
        static final long serialVersionUID =-239805141416294016L;
        public EnterAction () {
            super ("Enter"); // NOI18N
        }

        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            performObjectAt(index, e.getModifiers());
        }
        public boolean isEnabled() {
            return true;
        }
    }
     */
}
