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

/*
 * CommonPropertyVisualPanel.java

 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.ListSelectionModel;
import org.openide.util.NbBundle;
import java.util.ResourceBundle;

import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroup;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldHelper;
/**
 * Code reused from Appserver common API module 
 * 
 */
public class CommonPropertyVisualPanel extends javax.swing.JPanel implements WS70WizardConstants, TableModelListener {
    
    private final CommonPropertyPanel panel; 
    private ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.Bundle");
    private FieldGroup propertiesGroup;
    private PropertiesTableModel tableModel;
    private javax.swing.table.TableColumn propNameColumn;
    private javax.swing.table.TableColumn propValueColumn;
    private ResourceConfigHelper helper;
    
        
    /** Creates new form PropertyVisualPanel */
    public CommonPropertyVisualPanel(CommonPropertyPanel panel) {
        this.panel = panel;
        this.helper = panel.getHelper();
        this.propertiesGroup = panel.getFieldGroup(__Properties);  
        tableModel = new PropertiesTableModel(this.helper.getData());
        initComponents();
        propNameColumn = propertyTable.getColumnModel().getColumn(0);
        setPropTableCellEditor();
        tableModel.addTableModelListener(this);
        propertyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propertyTable.setRowSelectionAllowed(true);
        
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(CommonPropertyVisualPanel.class, "TITLE_CommonPropertyPanel"));  //NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        descriptionTextArea = new javax.swing.JTextArea();
        propertyInfo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        propertyTable = new javax.swing.JTable();
        buttonsPane = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("TITLE_CommonPropertyPanel"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommonPropertyVisualPanel.class, "CommonPropertyPanel_Description", this.helper.getData().getString(__JndiName)));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setText(NbBundle.getMessage(CommonPropertyVisualPanel.class, "CommonPropertyPanel_Description", this.helper.getData().getString(__JndiName)));
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 11);
        add(descriptionTextArea, gridBagConstraints);
        descriptionTextArea.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommonPropertyVisualPanel.class, "CommonPropertyPanel_Description", this.helper.getData().getString(__JndiName)));
        descriptionTextArea.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommonPropertyVisualPanel.class, "CommonPropertyPanel_Description", this.helper.getData().getString(__JndiName)));

        propertyInfo.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_properties_Mnemonic").charAt(0));
        propertyInfo.setLabelFor(propertyTable);
        propertyInfo.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_properties"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        add(propertyInfo, gridBagConstraints);
        propertyInfo.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("ACS_propTableCommon_A11yDesc"));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(descriptionTextArea.getWidth(), this.getHeight()));
        propertyTable.setModel(tableModel);
        jScrollPane1.setViewportView(propertyTable);
        propertyTable.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_AddProperty"));
        propertyTable.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("ACS_propTableCommon_A11yDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 10, 11);
        add(jScrollPane1, gridBagConstraints);
        jScrollPane1.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_AddProperty"));
        jScrollPane1.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("ACS_propTableCommon_A11yDesc"));

        buttonsPane.setLayout(new java.awt.GridBagLayout());

        addButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_Add_Mnemonic").charAt(0));
        addButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_Add"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        buttonsPane.add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("ACS_AddButtonA11yDesc"));

        removeButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_Remove_Mnemonic").charAt(0));
        removeButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_Remove"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        buttonsPane.add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("ACS_RemoveButtonA11yDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 150;
        add(buttonsPane, gridBagConstraints);
        buttonsPane.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("LBL_AddProperty"));
        buttonsPane.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/Bundle").getString("ACS_propTableCommon_A11yDesc"));

    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        //Fix for bug#4958730 - value overwrites into next row
        propertyTable.editingStopped(new ChangeEvent (this));
        ResourceConfigData data = this.helper.getData();
        data.addProperty(new NameValuePair()); 
        tableModel.fireTableDataChanged();
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        int selectedRow = propertyTable.getSelectedRow();
        if (selectedRow != -1) {
            //Fix for bug#4958730 - value overwrites into next row
            propertyTable.editingStopped(new ChangeEvent (this));
            this.helper.getData().removeProperty(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    public void tableChanged(TableModelEvent evt) {
        setPropTableCellEditor();
        this.panel.fireChangeEvent();
    }
    
    public void setPropTableCellEditor() {
        
        javax.swing.JComboBox propNameComboBox = new javax.swing.JComboBox();
                
        String[] remainingProperties = null;
        remainingProperties = FieldHelper.getRemainingFieldNames(propertiesGroup, this.helper.getData().getPropertyNames());
        
        for (int i = 0; i < remainingProperties.length; i++)
            propNameComboBox.addItem(remainingProperties[i]);
        propNameComboBox.setEditable(true);
        this.propNameColumn = propertyTable.getColumnModel().getColumn(0);
        propNameColumn.setCellEditor(new javax.swing.DefaultCellEditor(propNameComboBox));
        this.propValueColumn = propertyTable.getColumnModel().getColumn(1);
        
        javax.swing.DefaultCellEditor editor = new javax.swing.DefaultCellEditor(new javax.swing.JTextField());
        editor.setClickCountToStart(1);
        propValueColumn.setCellEditor(editor);
    }
    
    public void refreshFields() {
        ResourceConfigData data = this.helper.getData();
        ((PropertiesTableModel)propertyTable.getModel()).setData(this.helper.getData());
        descriptionTextArea.setText(NbBundle.getMessage(CommonPropertyVisualPanel.class, "CommonPropertyPanel_Description", data.getString(__JndiName)));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonsPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel propertyInfo;
    private javax.swing.JTable propertyTable;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
     public void setInitialFocus(){
         new setFocus(addButton);
     } 
}
