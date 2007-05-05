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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;


import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;

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
        
        String v = (String) value;
        JComboBox cb = (JComboBox) editorComponent;
        AdvancedTreeTableNode n = (AdvancedTreeTableNode) ((TreeTable) table).
                getRenderedNode();
        UserTask ut = (UserTask) n.getObject();
        DefaultComboBoxModel m = new DefaultComboBoxModel(ut.getList().getOwners());
        cb.setModel(m);
        cb.setSelectedItem(v);
        
        return retValue;
    }
}
