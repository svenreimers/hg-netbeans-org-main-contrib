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

package org.netbeans.modules.projectpackager.exporter;

import java.util.Vector;
import org.netbeans.modules.projectpackager.tools.ProjectPackagerSettings;

/**
 * E-mail settings dialog
 * @author Roman "Roumen" Strobl
 */
public class EmailSettingsDialog extends javax.swing.JFrame {
    
    /**
     * Constructor of the dialog
     */
    public EmailSettingsDialog() {
        initComponents();
        setLocationRelativeTo(null);        
        EmailSettingsUITools.setEmailSettingsDialog(this);
        final ProjectPackagerSettings pps = ProjectPackagerSettings.getDefault();
        smtpServerField.setText(pps.getSmtpServer());
        smtpUsernameField.setText(pps.getSmtpUsername());
        smtpPasswordField.setText(pps.getSmtpPassword());
        useSSL.setSelected(pps.getSmtpUseSSL().booleanValue());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        SettingsPanel = new javax.swing.JPanel();
        smtpServer = new javax.swing.JLabel();
        smtpUsernameField = new javax.swing.JTextField();
        smtpUsername = new javax.swing.JLabel();
        smtpPassword = new javax.swing.JLabel();
        smtpServerField = new javax.swing.JTextField();
        smtpPasswordField = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        useSSL = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jLabel3.setText("jLabel3");
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("E-mail Settings");
        setResizable(false);
        SettingsPanel.setLayout(new java.awt.GridBagLayout());

        SettingsPanel.setOpaque(false);
        SettingsPanel.setPreferredSize(new java.awt.Dimension(260, 175));
        SettingsPanel.setRequestFocusEnabled(false);
        smtpServer.setText("SMTP Server:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(smtpServer, gridBagConstraints);

        smtpUsernameField.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(smtpUsernameField, gridBagConstraints);

        smtpUsername.setText("SMTP Username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(smtpUsername, gridBagConstraints);

        smtpPassword.setText("SMTP Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(smtpPassword, gridBagConstraints);

        smtpServerField.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(smtpServerField, gridBagConstraints);

        smtpPasswordField.setColumns(9);
        smtpPasswordField.setMaximumSize(new java.awt.Dimension(107, 18));
        smtpPasswordField.setMinimumSize(new java.awt.Dimension(11, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(smtpPasswordField, gridBagConstraints);

        jLabel1.setText("SMTP Server is required, other fields are optional.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 3, 3, 3);
        SettingsPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Storing of password is not recommended.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 3, 0, 3);
        SettingsPanel.add(jLabel2, gridBagConstraints);

        useSSL.setText("Use SSL for SMTP");
        useSSL.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        useSSL.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        SettingsPanel.add(useSSL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(SettingsPanel, gridBagConstraints);

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 10, 3);
        getContentPane().add(okButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 10, 10);
        getContentPane().add(cancelButton, gridBagConstraints);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
            
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        EmailSettingsUITools.processCancelButton();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        EmailSettingsUITools.processOkButton();
    }//GEN-LAST:event_okButtonActionPerformed
       
    /**
     * Returns filled in SMTP server
     * @return SMTP server
     */
    public String getSmtpServer() {
        return smtpServerField.getText();
    }

    /**
     * Returns filled in SMTP username
     * @return SMTP username
     */
    public String getSmtpUsername() {
        return smtpUsernameField.getText();
    }
    
    /**
     * Returns filled in SMTP password
     * @return SMTP password
     */
    public String getSmtpPassword() {
        return new String(smtpPasswordField.getPassword());
    }

    /**
     * Returns filled in Use SSL
     * @return Use SSL
     */
    public boolean getSmtpUseSSL() {
        return useSSL.isSelected();
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel smtpPassword;
    private javax.swing.JPasswordField smtpPasswordField;
    private javax.swing.JLabel smtpServer;
    private javax.swing.JTextField smtpServerField;
    private javax.swing.JLabel smtpUsername;
    private javax.swing.JTextField smtpUsernameField;
    private javax.swing.JCheckBox useSSL;
    // End of variables declaration//GEN-END:variables
    
}
