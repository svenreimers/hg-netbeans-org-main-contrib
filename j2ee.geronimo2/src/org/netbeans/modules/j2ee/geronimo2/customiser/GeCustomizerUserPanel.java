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

package org.netbeans.modules.j2ee.geronimo2.customiser;

import javax.swing.JSpinner;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;

/**
 *
 * @author  Max Sauer
 */
public class GeCustomizerUserPanel extends javax.swing.JPanel {
    
    private final GeCustomizerDataSupport custData;
    private InstanceProperties ip;
    
    /** Creates new form GeCustomizerUserPanel */
    public GeCustomizerUserPanel(GeCustomizerDataSupport custData) {
        this.custData = custData;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userLabel = new javax.swing.JLabel();
        userTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        homeLabel = new javax.swing.JLabel();
        homeTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        passwordFiled = new javax.swing.JPasswordField();
        serverLabel = new javax.swing.JLabel();
        adminLabel = new javax.swing.JLabel();
        serverPortSpinner = new javax.swing.JSpinner();
        adminPortSpinner = new javax.swing.JSpinner();
        warningLabel = new javax.swing.JLabel();

        userLabel.setDisplayedMnemonic('U');
        userLabel.setLabelFor(userTextField);
        org.openide.awt.Mnemonics.setLocalizedText(userLabel, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "LBL_UserPanel_1")); // NOI18N

        userTextField.setColumns(15);
        userTextField.setDocument(custData.getUsenameModel());

        jLabel1.setDisplayedMnemonic('P');
        jLabel1.setLabelFor(passwordFiled);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "GeCustomizerUserPanel.jLabel1.text")); // NOI18N

        homeLabel.setLabelFor(homeTextField);
        org.openide.awt.Mnemonics.setLocalizedText(homeLabel, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "GeCustomizerUserPanel.homeLabel.text")); // NOI18N

        homeTextField.setColumns(30);
        homeTextField.setDocument(custData.getGeHomeModel());
        homeTextField.setEditable(false);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "GeCustomizerUserPanel.jLabel2.text")); // NOI18N

        passwordFiled.setColumns(15);
        passwordFiled.setDocument(custData.getPaswordModel());

        org.openide.awt.Mnemonics.setLocalizedText(serverLabel, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "GeCustomizerUserPanel.serverLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(adminLabel, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "GeCustomizerUserPanel.adminLabel.text")); // NOI18N

        serverPortSpinner.setModel(custData.getServerPortModel());
        serverPortSpinner.setEditor(new JSpinner.NumberEditor(serverPortSpinner, "#"));

        adminPortSpinner.setModel(custData.getAdminPortModel());
        adminPortSpinner.setEditor(new JSpinner.NumberEditor(adminPortSpinner, "#"));

        warningLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(GeCustomizerUserPanel.class, "GeCustomizerUserPanel.warningLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(homeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(homeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                    .add(jLabel2)
                    .add(adminLabel)
                    .add(warningLabel)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(serverLabel)
                            .add(userLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(userTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(passwordFiled, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, adminPortSpinner)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, serverPortSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)))
                        .add(252, 252, 252)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {passwordFiled, userTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(homeLabel)
                    .add(homeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userLabel)
                    .add(userTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(passwordFiled, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serverLabel)
                    .add(serverPortSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(adminLabel)
                    .add(adminPortSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(warningLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminLabel;
    private javax.swing.JSpinner adminPortSpinner;
    private javax.swing.JLabel homeLabel;
    private javax.swing.JTextField homeTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField passwordFiled;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JSpinner serverPortSpinner;
    private javax.swing.JLabel userLabel;
    private javax.swing.JTextField userTextField;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
    
}
