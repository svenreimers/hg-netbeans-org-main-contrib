/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.tree.TreePath;

import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.export.ExportAction;
import org.netbeans.modules.tasklist.core.export.ImportAction;
import org.netbeans.modules.tasklist.usertasks.actions.ClearCompletedAction;
import org.netbeans.modules.tasklist.usertasks.actions.CollapseAllAction;
import org.netbeans.modules.tasklist.usertasks.actions.ExpandAllUserTasksAction;
import org.netbeans.modules.tasklist.usertasks.actions.GoToUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveDownAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveUpAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskListAction;
import org.netbeans.modules.tasklist.usertasks.actions.PauseAction;
import org.netbeans.modules.tasklist.usertasks.actions.PurgeTasksAction;
import org.netbeans.modules.tasklist.usertasks.actions.ShowTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.SingleLineCookie;
import org.netbeans.modules.tasklist.usertasks.actions.StartCookie;
import org.netbeans.modules.tasklist.usertasks.actions.StartTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.StopCookie;
import org.netbeans.modules.tasklist.usertasks.editors.DateEditor;
import org.netbeans.modules.tasklist.usertasks.editors.DurationPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PercentsPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PriorityPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.filter.FilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.RemoveFilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.openide.util.datatransfer.PasteType;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * Node for a user task
 */
public final class UserTaskNode extends AbstractNode {
    private UserTask item;
    private UserTaskList utl;
    private UserTaskTreeTableNode node;
    private UserTasksTreeTable tt;
    
    /**
     * Constructor
     *
     * @param node node in the tree associated with this one
     * @param item an user task that will be represented by this node.
     * @param utl user task list that this task belongs to. Should be != null
     * for root tasks
     * @param tt TreeTable
     */
    UserTaskNode(UserTaskTreeTableNode node, UserTask item, UserTaskList utl,
    UserTasksTreeTable tt) {
        super(Children.LEAF);
        assert item != null;
        
        this.utl = utl;
        this.item = item;
        this.node = node;
        this.tt = tt;
        
        setName(item.getSummary());
        
        item.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String n = e.getPropertyName();
                if (n == "progress") { // NOI18N
                    int old = Math.round(((Float) e.getOldValue()).floatValue());
                    int new_ = Math.round(((Float) e.getNewValue()).floatValue());
                    UserTaskNode.this.firePropertyChange(
                        "percentComplete", new Integer(old), new Integer(new_)); // NOI18N
                } else if (n == "line" || n == "started") { // NOI18N
                    // nothing
                } else {
                    UserTaskNode.this.firePropertyChange(e.getPropertyName(),
                        e.getOldValue(), e.getNewValue());
                }
                if (n == "started" || n == "spentTimeComputed" || n == "line") { // NOI18N
                    fireCookieChange();
                }
            }
        });
    } 

    /**
     * Returns the task associated with this node
     */
    public UserTask getTask() {
        return item;
    }
    
    protected void updateIcon() {
        UserTask uitem = (UserTask)item;
        if (uitem.isDone()) {
            setIconBase("org/netbeans/modules/tasklist/core/doneItem"); // NOI18N
        } else {
            setIconBase("org/netbeans/modules/tasklist/core/task"); // NOI18N
        }
    }
    
    public Action[] getActions(boolean empty) {
        if (empty) {
            return new SystemAction[] {
                SystemAction.get(NewTaskAction.class),
                SystemAction.get(NewTaskListAction.class),
                null,
                SystemAction.get(PauseAction.class),
                null,
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(FilterUserTaskAction.class),
                SystemAction.get(PurgeTasksAction.class),
                null,
                SystemAction.get(ExpandAllUserTasksAction.class),
                SystemAction.get(CollapseAllAction.class),
                null,
                SystemAction.get(ImportAction.class),
                SystemAction.get(ExportAction.class),
            };
        } else {
            return new SystemAction[] {
                SystemAction.get(NewTaskAction.class),
                SystemAction.get(NewTaskListAction.class),
                //SystemAction.get(ShowScheduleViewAction.class),
                null,
                SystemAction.get(StartTaskAction.class),
                SystemAction.get(PauseAction.class),
                null,
                SystemAction.get(ShowTaskAction.class),
                SystemAction.get(GoToUserTaskAction.class),
                null,
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(DeleteAction.class),
                null,
                SystemAction.get(MoveUpAction.class),
                SystemAction.get(MoveDownAction.class),
                null,
                SystemAction.get(FilterUserTaskAction.class),
                SystemAction.get(RemoveFilterUserTaskAction.class),
                null,
                SystemAction.get(PurgeTasksAction.class),
                SystemAction.get(ClearCompletedAction.class),
                null,
                SystemAction.get(ExpandAllUserTasksAction.class),
                SystemAction.get(CollapseAllAction.class),
                null,
                SystemAction.get(ImportAction.class),
                SystemAction.get(ExportAction.class),

                // Property: node specific, but by convention last in menu
                null,
                SystemAction.get(PropertiesAction.class)
            };
        }
    }

    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Set ss = s.get(Sheet.PROPERTIES);

        try {
            PropertySupport.Reflection p;
            p = new PropertySupport.Reflection(item, String.class, "getSummary", "setSummary"); // NOI18N
            p.setName(UserTask.PROP_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(TaskNode.class, "Description")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(TaskNode.class, "DescriptionHint")); // NOI18N
            ss.put(p);            
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, "getPriority", "setPriority"); // NOI18N
            p.setName(UserTask.PROP_PRIORITY);
            p.setPropertyEditorClass(PriorityPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_priorityProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_priorityProperty")); // NOI18N
            ss.put(p);
            
            
            p = new PropertySupport.Reflection(item, Boolean.TYPE, "isDone", "setDone"); // NOI18N
            p.setName("done"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_doneProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_doneProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, 
                "getPercentComplete", "setPercentComplete"); // NOI18N
            p.setName("percentComplete"); // NOI18N
            p.setPropertyEditorClass(PercentsPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_percentCompleteProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_percentCompleteProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Boolean.TYPE, 
                "isProgressComputed", "setProgressComputed"); // NOI18N
            p.setName("progressComputed"); // NOI18N
            p.setDisplayName(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "LBL_computeProgress")); // NOI18N
            p.setShortDescription(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "HNT_progressComputed")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, "getEffort", null); // NOI18N
            p.setName("effort"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_effortProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_effortProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, Boolean.TYPE, 
                "isEffortComputed", "setEffortComputed"); // NOI18N
            p.setName("effortComputed"); // NOI18N
            p.setDisplayName(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "LBL_computeEffort")); // NOI18N
            p.setShortDescription(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "HNT_effortComputed")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, "getRemainingEffort", null); // NOI18N
            p.setName("remainingEffort"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_remainingEffortProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_remainingEffortProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, "getSpentTime", null); // NOI18N
            p.setName("spentTime"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_spentTimeProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_spentTimeProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, Boolean.TYPE, 
                "isSpentTimeComputed", "setSpentTimeComputed"); // NOI18N
            p.setName("spentTimeComputed"); // NOI18N
            p.setDisplayName(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "LBL_spentTimeComputed")); // NOI18N
            p.setShortDescription(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "HNT_spentTimeComputed")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, String.class, "getDetails", "setDetails"); // NOI18N
            p.setName(UserTask.PROP_DETAILS);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_detailsProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_detailsProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, String.class, (String) null, null) { // NOI18N
                public boolean canRead() {
                    return true;
                }
                public boolean canWrite() {
                    return true;
                }
                public Object getValue () throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    URL url = ((UserTask) instance).getUrl();
                    if (url == null)
                        return ""; // NOI18N
                    else
                        return url.toExternalForm();
                }
                public void setValue (Object val) throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    try {
                        URL url = new URL((String) val);
                        ((UserTask) instance).setUrl(url);
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
            };
            p.setName(UserTask.PROP_URL);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_urlProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_urlProperty")); // NOI18N
            //p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, (String) null, null) { // NOI18N
                public boolean canRead() {
                    return true;
                }
                public boolean canWrite() {
                    return true;
                }
                public Object getValue () throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    return new Integer(((UserTask) instance).getLineNumber() + 1);
                }
                public void setValue (Object val) throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    int n = ((Integer) val).intValue();
                    if (n < 1)
                        throw new IllegalArgumentException(); 
                    ((UserTask) instance).setLineNumber(n - 1);
                }
            };
            p.setName(UserTask.PROP_LINE_NUMBER);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_lineProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_lineProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, String.class, "getCategory", "setCategory"); // NOI18N
            p.setName(UserTask.PROP_CATEGORY);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_categoryProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_categoryProperty")); // NOI18N
            p.setValue("canEditAsText", Boolean.TRUE); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getCreatedDate", null); // NOI18N
            p.setPropertyEditorClass(DateEditor.class);
            p.setName("created"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_createdProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_createdProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getLastEditedDate", null); // NOI18N
            p.setPropertyEditorClass(DateEditor.class);
            p.setName("edited"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_editedProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_editedProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getCompletedDate", null); // NOI18N
            p.setPropertyEditorClass(DateEditor.class);
            p.setName("completedDate"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_completedDateProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_completedDateProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getDueDate", "setDueDate"); // NOI18N            
            p.setPropertyEditorClass(DateEditor.class);
            p.setName(UserTask.PROP_DUE_DATE);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_dueDateProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_dueDateProperty")); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, String.class, "getOwner", "setOwner"); // NOI18N            
            p.setName(UserTask.PROP_OWNER);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_ownerProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_ownerProperty")); // NOI18N
            ss.put(p);
        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }

    public boolean canDestroy() {
        return true;
    }

    /** Can this node be copied?
    * @return <code>true</code>
    */
    public boolean canCopy () {
        return true;
    }

    /** Can this node be cut?
    * @return <code>false</code>
    */
    public boolean canCut () {
        return true;
    }    
    
    public javax.swing.Action getPreferredAction() {
        return SystemAction.get(ShowTaskAction.class);
    }
    
    public boolean canRename() {
        return true;
    }
    
    protected void createPasteTypes(java.awt.datatransfer.Transferable t, List s) {
        // UTUtils.LOGGER.fine("entering"); // NOI18N
        super.createPasteTypes(t, s);
        PasteType p = createTodoPasteType(this, t);
        if (p != null) {
            s.add(p);
        }
    }

    /** 
     * Create a paste type from a transferable.
     *
     * @param t the transferable to check
     * @param target parent for the pasted task
     * @return an appropriate paste type, or null if not appropriate
     */
    public static PasteType createTodoPasteType(
    UserTaskNode target, Transferable t) {
        // UTUtils.LOGGER.fine("entering");
        if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
            try {
                // Multiselection
                final MultiTransferObject mto = (MultiTransferObject)
                    t.getTransferData(ExTransferable.multiFlavor);
                if (mto.areDataFlavorsSupported(
                    new DataFlavor[] {UserTaskTransfer.TODO_FLAVOR})) {
                    return new UserTaskNode.TodoPaste(target, t);
                }
            } catch (UnsupportedFlavorException e) {
                ErrorManager.getDefault().notify(e);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        } 
        
        if (t.isDataFlavorSupported(UserTaskTransfer.TODO_FLAVOR)) {
            return new TodoPaste(target, t);
        } 
        return null;
    }

    public org.openide.nodes.Node.Cookie getCookie(Class type) {
        UserTask uitem = (UserTask) item;
        if (type == StartCookie.class) {
            if (uitem.isStarted() || uitem.isSpentTimeComputed()) {
                return null;
            } else {
                return new StartCookie(uitem);
            }
        } else if (type == StopCookie.class) {
            if (!uitem.isStarted()) {
                return null;
            } else {
                return new StopCookie(uitem);
            }
        } else if (type == UserTask.class) {
            return item;
        } else if (type == SingleLineCookie.class) {
            final Line l = uitem.getLine();
            if (l != null)
                return new SingleLineCookie() {
                    public Line getLine() {
                        return l;
                    }
                };
            else
                return null;
        } else {
            return super.getCookie(type);
        }
    }
    
    public void setName(String nue) {
        super.setName(nue);
        if (!nue.equals(item.getSummary())) {
            item.setSummary(nue);
        }
    }
    
    public Transferable clipboardCopy() throws IOException {
        UTUtils.LOGGER.fine("entering"); // NOI18N
        final UserTask copy = (UserTask) item.clone();
        return new ExTransferable.Single(UserTaskTransfer.TODO_FLAVOR) {
            protected Object getData() {
                return copy;
            }
        };
    }
    
    public Transferable clipboardCut() throws IOException {
        destroy();
        return clipboardCopy();
    }

    public void destroy() throws IOException {
        throw new InternalError("should never be called"); // NOI18N
/*      TODO: remove  
        AdvancedTreeTableNode n = 
            (AdvancedTreeTableNode) this.node.findNextNodeAfterDelete();
        UTUtils.LOGGER.fine("selected node after delete:" + n); // NOI18N
        item.destroy();
        if (item.getParent() != null)
            item.getParent().getSubtasks().remove(item);
        else
            utl.getSubtasks().remove(item);
        super.destroy();
        if (n != null) {
            TreePath tp = new TreePath(n.getPathToRoot());
            tt.select(tp);
            tt.scrollTo(tp);
        }*/
    }
    
    /**
     * Performs "Paste" for the specified task on this node
     *
     * @param t task to be pasted
     */
    public void pasteTask(UserTask t) {
        t = (UserTask) t.clone();
        item.getSubtasks().add(t);
        int index = this.node.getIndexOfObject(t);
        if (index >= 0) {
            AdvancedTreeTableNode n = 
                (AdvancedTreeTableNode) this.node.getChildAt(index);
            TreePath tp = new TreePath(n.getPathToRoot());
            tt.expandAllPath(tp);
            tt.select(tp);
            tt.scrollTo(tp);
        }
    }
    
    /**
     * Paste type for a pasted task
     */
    private static final class TodoPaste extends PasteType {
        private final Transferable t;
        private final UserTaskNode target;
        
        /**
         * Creates a paste type for a UserTask
         *
         * @param t a transferable object
         * @param target parent for the pasted task
         */
        public TodoPaste(UserTaskNode target, Transferable t) {
            this.t = t;
            this.target = target;
        }
        
        public String getName() {
            return NbBundle.getMessage(UserTaskTransfer.class, 
                "LBL_todo_paste_as_subtask"); // NOI18N
        }
        
        public HelpCtx getHelpCtx() {
            return new HelpCtx("org.netbeans.modules.todo"); // NOI18N
        }
        
        public Transferable paste() throws IOException {
            try {
                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                    // Multiselection
                    final MultiTransferObject mto = (MultiTransferObject)
                        t.getTransferData(ExTransferable.multiFlavor);
                    if (mto.areDataFlavorsSupported(
                        new DataFlavor[] {UserTaskTransfer.TODO_FLAVOR})) {
                        for (int i = 0; i < mto.getCount(); i++) {
                            UserTask item = (UserTask)
                                mto.getTransferData(i, UserTaskTransfer.TODO_FLAVOR);
                            addTask(item);
                        }
                        return null;
                    }
                } 
                
                if (t.isDataFlavorSupported(UserTaskTransfer.TODO_FLAVOR)) {
                    UTUtils.LOGGER.fine(t.getTransferData(
                        UserTaskTransfer.TODO_FLAVOR).getClass().getName());
                    UserTask item = 
                        (UserTask) t.getTransferData(UserTaskTransfer.TODO_FLAVOR);
                    addTask(item);
                } 
            } catch (UnsupportedFlavorException ufe) {
                // Should not happen.
                IOException ioe = new IOException(ufe.toString());
                ErrorManager.getDefault().annotate(ioe, ufe);
                throw ioe;
            }
            return null;
        }
        
        /**
         * Adds a task
         *
         * @param item a task
         */
        private void addTask(UserTask item) {
            UserTask ut;
            if (item instanceof UserTask) {
                ut = (UserTask) item;
            } else {
                ut = new UserTask(item.getSummary(), item.getList());
                ut.setDetails(item.getDetails());
                ut.setLine(item.getLine());
                ut.setPriority(item.getPriority());
            }
            target.pasteTask(ut);
        }
    }
}

