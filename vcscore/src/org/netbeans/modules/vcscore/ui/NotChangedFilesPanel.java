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


package org.netbeans.modules.vcscore.ui;

/**
 *
 * @author  Milos Kleint
 */

import org.netbeans.modules.vcscore.util.table.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.*;
import java.lang.reflect.*;
import org.openide.util.*;


public class NotChangedFilesPanel extends javax.swing.JPanel {

    private TableInfoModel model;
    /** Creates new panel with not changed files (verify action)
     * @param dataObjectList list with dataobjects that were not changed.
     */
    public NotChangedFilesPanel(List dataObjectList) {
        initComponents();
        cbPerform.setMnemonic (org.openide.util.NbBundle.getBundle (NotChangedFilesPanel.class).getString ("NotChangedFilesPanel.cbPerform.mnemonic").charAt (0));
        rbAll.setMnemonic (org.openide.util.NbBundle.getBundle (NotChangedFilesPanel.class).getString ("NotChangedFilesPanel.rbAll.mnemonic").charAt (0));
        rbSelectedOnly.setMnemonic (org.openide.util.NbBundle.getBundle (NotChangedFilesPanel.class).getString ("NotChangedFilesPanel.rbSelectedOnly.mnemonic").charAt (0));
        jTextArea1.setFont (javax.swing.UIManager.getFont ("Label.font"));
        jTextArea1.setForeground (javax.swing.UIManager.getColor ("Label.foreground"));

        rbAll.setSelected(true);
        rbSelectedOnly.setSelected(false);
        rbAll.setEnabled(false);
        rbSelectedOnly.setEnabled(false);
        
        buttonGroup1.add(rbAll);
        buttonGroup1.add(rbSelectedOnly);
        // setting the model....
        model = new TableInfoModel();
        Class classa = MyTableObject.class;
        String  column1 = NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.fileColumn"); // NOI18N
        String  column2 = NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.pathColumn"); // NOI18N
        String  column3 = NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.fsColumn"); // NOI18N
        try {
            Method method1 = classa.getMethod("getName", null);     // NOI18N
            Method method2 = classa.getMethod("getPackg", null);     // NOI18N
            Method method3 = classa.getMethod("getFilesystem", null);     // NOI18N
            model.setColumnDefinition(0, column1, method1, true, null);
            model.setColumnDefinition(1, column2, method2, true, null);
            model.setColumnDefinition(2, column3, method3, true, null);
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        Iterator it = dataObjectList.iterator();
        while (it.hasNext()) {
            DataObject obj = (DataObject)it.next();
            model.addElement(new MyTableObject(obj));
        }
        tblNotChangedFiles.setModel(model);
        JTableHeader head = tblNotChangedFiles.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblNotChangedFiles);
        head.addMouseListener(listen);        
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTextArea1 = new javax.swing.JTextArea();
        cbPerform = new javax.swing.JCheckBox();
        rbAll = new javax.swing.JRadioButton();
        rbSelectedOnly = new javax.swing.JRadioButton();
        spNotChangedFiles = new javax.swing.JScrollPane();
        tblNotChangedFiles = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;

        setPreferredSize(new java.awt.Dimension(250, 250));
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setLineWrap(true);
        jTextArea1.setEditable(false);
        jTextArea1.setText(org.openide.util.NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.lblDescription.text"));
        jTextArea1.setOpaque(false);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jTextArea1, gridBagConstraints1);

        cbPerform.setText(org.openide.util.NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.cbPerform.text"));
        cbPerform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPerformActionPerformed(evt);
            }
        });

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(cbPerform, gridBagConstraints1);

        rbAll.setText(org.openide.util.NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.rbAll.text"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(rbAll, gridBagConstraints1);

        rbSelectedOnly.setText(org.openide.util.NbBundle.getBundle(NotChangedFilesPanel.class).getString("NotChangedFilesPanel.rbSelectedOnly.text"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(rbSelectedOnly, gridBagConstraints1);

        spNotChangedFiles.setMinimumSize(new java.awt.Dimension(150, 120));
        tblNotChangedFiles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        spNotChangedFiles.setViewportView(tblNotChangedFiles);

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(5, 30, 11, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(spNotChangedFiles, gridBagConstraints1);

    }//GEN-END:initComponents

    private void cbPerformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPerformActionPerformed
        // Add your handling code here:
        rbAll.setEnabled(cbPerform.isSelected());
        rbSelectedOnly.setEnabled(cbPerform.isSelected());
    }//GEN-LAST:event_cbPerformActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JCheckBox cbPerform;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbSelectedOnly;
    private javax.swing.JScrollPane spNotChangedFiles;
    private javax.swing.JTable tblNotChangedFiles;
    // End of variables declaration//GEN-END:variables

    
    /** 
     * returns the selected fileobjects. if the action should not be performed, returns null
     */
    
    public java.util.List getSelectedDataObjects() {
        if (cbPerform.isSelected()) {
            java.util.List list = new LinkedList();
            if (rbAll.isSelected()) {
               Iterator it = model.getList().iterator();
               while (it.hasNext()) {
                    MyTableObject obj = (MyTableObject)it.next();
                    list.add(obj.getDataObject());
               }
            } else {
                int[] rows = tblNotChangedFiles.getSelectedRows();
                for (int i = 0; i< rows.length; i++) {
                    MyTableObject obj = (MyTableObject)model.getElementAt(rows[i]);
                    list.add(obj.getDataObject());
                }
            }
            return list;
        }
        return null;
    }    
    
    


}
