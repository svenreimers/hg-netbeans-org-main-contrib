/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import java.util.HashMap;

import org.openide.TopManager;
import org.openide.filesystems.*;

import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;

/**
 * The Save As dialog, that works on FileObjects. It is used to save configuration files.
 * @author  Martin Entlicher
 */
public class ConfigSaveAsDialog extends javax.swing.JDialog {

    private FileObject dir;
    private String selectedFile;
    private HashMap configLabels = new HashMap();

    static final long serialVersionUID =-4678964678643578543L;
    /** Creates new form ConfigSaveAsDialog */
    public ConfigSaveAsDialog(java.awt.Frame parent, boolean modal, FileObject dir) {
        super (parent, modal);
        this.dir = dir;
        setTitle(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.title"));
        initComponents ();
        fileNameLabel.setDisplayedMnemonic (org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString ("ConfigSaveAsDialog.fileNameLabel.mnemonic").charAt (0));
        configLabelLabel.setDisplayedMnemonic (org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString ("ConfigSaveAsDialog.configLabelLabel.mnemonic").charAt (0));
        fillFileList();
        this.setSize(340, 265);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        listPanel = new javax.swing.JPanel();
        fileScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        configLabelLabel = new javax.swing.JLabel();
        configLabelTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        
        getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        listPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });
        
        fileScrollPane.setViewportView(fileList);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        listPanel.add(fileScrollPane, gridBagConstraints2);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        getContentPane().add(listPanel, gridBagConstraints1);
        
        jPanel2.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints3;
        
        fileNameLabel.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.fileNameLabel.text"));
        fileNameLabel.setLabelFor(fileNameTextField);
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel2.add(fileNameLabel, gridBagConstraints3);
        
        fileNameTextField.setColumns(10);
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.weightx = 1.0;
        jPanel2.add(fileNameTextField, gridBagConstraints3);
        
        configLabelLabel.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.configLabelLabel.text"));
        configLabelLabel.setLabelFor(configLabelTextField);
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new java.awt.Insets(6, 0, 0, 12);
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(configLabelLabel, gridBagConstraints3);
        
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(configLabelTextField, gridBagConstraints3);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 17, 11);
        gridBagConstraints1.weightx = 1.0;
        getContentPane().add(jPanel2, gridBagConstraints1);
        
        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 5, 0));
        
        saveButton.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.saveButton.text"));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        
        jPanel1.add(saveButton);
        
        cancelButton.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.cancelButton.text"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        
        jPanel1.add(cancelButton);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 11, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel1, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void fileListMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMouseClicked
        // Add your handling code here:
        selectedFile = (String) fileList.getSelectedValue();
        fileNameTextField.setText(selectedFile);
        //fileNameTextField.repaint();
        String label = (String) configLabels.get(selectedFile);
        if (label != null) configLabelTextField.setText(label);
    }//GEN-LAST:event_fileListMouseClicked

    private void saveButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // Add your handling code here:
        selectedFile = fileNameTextField.getText();
        closeDialog(null);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        selectedFile = null;
        closeDialog(null);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible (false);
        dispose ();
    }//GEN-LAST:event_closeDialog

    private void fillFileList() {
        javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
        FileObject[] ch = dir.getChildren();
        for(int i = 0; i < ch.length; i++) {
            if (ch[i].getExt().equalsIgnoreCase(VariableIO.CONFIG_FILE_EXT)) {
                model.addElement(ch[i].getName());
                String label = null;
                org.w3c.dom.Document doc = VariableIO.readPredefinedConfigurations(dir, ch[i].getNameExt());
                if (doc != null) {
                    label = VariableIO.getConfigurationLabel(doc);
                } else {
                    label = org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("CTL_No_label_configured");
                }
                configLabels.put(ch[i].getName(), label);
            }
            if (ch[i].getExt().equalsIgnoreCase(VariableIOCompat.CONFIG_FILE_EXT)) {
                model.addElement(ch[i].getName());
                String label = VariableIOCompat.readPredefinedProperties(dir, ch[i].getNameExt()).
                                getProperty("label", org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("CTL_No_label_configured"));
                configLabels.put(ch[i].getName(), label);
            }
        }
        fileList.setModel(model);
    }

    public String getSelectedFile() {
        return selectedFile;
    }
    
    public String getSelectedConfigLabel() {
        return configLabelTextField.getText();
    }

    /*
    * @param args the command line arguments

    public static void main (String args[]) {
      new ConfigSaveAsDialog (new javax.swing.JFrame (), true).show ();
}
    */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel listPanel;
    private javax.swing.JScrollPane fileScrollPane;
    private javax.swing.JList fileList;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JLabel configLabelLabel;
    private javax.swing.JTextField configLabelTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton cancelButton;
    // End of variables declaration//GEN-END:variables

}
