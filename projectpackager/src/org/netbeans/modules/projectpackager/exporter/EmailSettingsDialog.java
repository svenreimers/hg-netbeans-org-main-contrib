/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.projectpackager.exporter;

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
        // XXX use of JFrame is totally wrong; should use DialogDescriptor instead
        initComponents();
        setLocationRelativeTo(null);
        EmailSettingsUITools.setEmailSettingsDialog(this);
        smtpServerField.setText(ProjectPackagerSettings.getSmtpServer());
        smtpUsernameField.setText(ProjectPackagerSettings.getSmtpUsername());
        smtpPasswordField.setText(ProjectPackagerSettings.getSmtpPassword());
        useSSL.setSelected(ProjectPackagerSettings.getSmtpUseSSL());
        mailFromField.setText(ProjectPackagerSettings.getMailFrom());
        mailSubjectField.setText(ProjectPackagerSettings.getMailSubject());
        mailBodyField.setText(ProjectPackagerSettings.getMailBody());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        smtpServer = new javax.swing.JLabel();
        smtpServerField = new javax.swing.JTextField();
        smtpUsername = new javax.swing.JLabel();
        smtpUsernameField = new javax.swing.JTextField();
        smtpPassword = new javax.swing.JLabel();
        useSSL = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        smtpPasswordField = new javax.swing.JPasswordField();
        mailFromLabel = new javax.swing.JLabel();
        mailFromField = new javax.swing.JTextField();
        mailSubjectLabel = new javax.swing.JLabel();
        mailSubjectField = new javax.swing.JTextField();
        mailBodyLabel = new javax.swing.JLabel();
        mailBodyField = new javax.swing.JTextField();

        jLabel3.setText("jLabel3");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("E-mail Settings");
        setResizable(false);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        smtpServer.setText("SMTP Server:");

        smtpUsername.setText("SMTP Username:");

        smtpPassword.setText("SMTP Password:");

        useSSL.setText("Use SSL for SMTP");
        useSSL.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useSSL.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel5.setText("Storing of passwords is not recommended.");

        jLabel6.setText("SMTP server is required, username and password is optional.");

        smtpPasswordField.setPreferredSize(new java.awt.Dimension(11, 18));

        mailFromLabel.setLabelFor(mailFromField);
        mailFromLabel.setText(org.openide.util.NbBundle.getMessage(EmailSettingsDialog.class, "SMTP_Mail_From")); // NOI18N

        mailSubjectLabel.setLabelFor(mailSubjectField);
        mailSubjectLabel.setText(org.openide.util.NbBundle.getMessage(EmailSettingsDialog.class, "SMTP_Mail_Subject")); // NOI18N

        mailBodyLabel.setLabelFor(mailBodyField);
        mailBodyLabel.setText(org.openide.util.NbBundle.getMessage(EmailSettingsDialog.class, "SMTP_Mail_Body")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(useSSL)
                    .add(jLabel6)
                    .add(jLabel5)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(smtpServer)
                            .add(smtpUsername)
                            .add(smtpPassword))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 13, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, smtpServerField)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(smtpPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(smtpUsernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, Short.MAX_VALUE))
                                .add(87, 87, 87))))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mailSubjectLabel)
                            .add(mailBodyLabel)
                            .add(mailFromLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mailFromField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                            .add(mailBodyField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                            .add(mailSubjectField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(smtpServer)
                    .add(smtpServerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(smtpUsername)
                    .add(smtpUsernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(smtpPassword)
                    .add(smtpPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(useSSL)
                .add(17, 17, 17)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel6)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mailFromLabel)
                    .add(mailFromField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mailSubjectLabel)
                    .add(mailSubjectField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mailBodyLabel)
                    .add(mailBodyField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        EmailSettingsUITools.processOkButton();//GEN-LAST:event_okButtonActionPerformed
    }                                        
                                        
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        EmailSettingsUITools.processCancelButton();//GEN-LAST:event_cancelButtonActionPerformed
    }                                            

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

    public String getMailFrom() {
        return mailFromField.getText();
    }
    public String getMailSubject() {
        return mailSubjectField.getText();
    }
    public String getMailBody() {
        return mailBodyField.getText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField mailBodyField;
    private javax.swing.JLabel mailBodyLabel;
    private javax.swing.JTextField mailFromField;
    private javax.swing.JLabel mailFromLabel;
    private javax.swing.JTextField mailSubjectField;
    private javax.swing.JLabel mailSubjectLabel;
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