/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;


import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * TableCellEditor for the owner
 */
public class OwnerTableCellEditor extends DefaultCellEditor {
    /**
     * Creates a new instance of PriorityTableCellRenderer
     */
    public OwnerTableCellEditor() {
        super(new JComboBox());
        ((JComboBox) editorComponent).setEditable(true);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column) {

        java.awt.Component retValue;
        
        retValue = super.getTableCellEditorComponent(table, value, isSelected, 
            row, column);
        
        UserTask ut = (UserTask) value;
        JComboBox cb = (JComboBox) editorComponent;
        DefaultComboBoxModel m = new DefaultComboBoxModel(ut.getList().getOwners());
        cb.setModel(m);
        cb.setSelectedItem(ut.getOwner());
        
        return retValue;
    }
}
