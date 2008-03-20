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

/*
 * HibernateConfigurationWizardPanel.java
 *
 * Created on January 9, 2008, 4:26 PM
 */
package org.netbeans.modules.hibernate.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.hibernate.framework.HibernateWebModuleExtender;

/**
 *
 * @author  gowri
 */
public class HibernateConfigurationWizardPanel extends javax.swing.JPanel implements DocumentListener, ItemListener {

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private HibernateWebModuleExtender webModuleExtender;
    private ExtenderController controller;
    private boolean forNewProjectWizard = false;

    /** Creates new form HibernateConfigurationWizardPanel */
    public HibernateConfigurationWizardPanel() {
        initComponents();
        setDefaults();
    }

    public void setDefaults() {
        cmbDbConnection.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));
        DatabaseExplorerUIs.connect(cmbDbConnection, ConnectionManager.getDefault());
        cmbURL.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));
    }

    public HibernateConfigurationWizardPanel(HibernateWebModuleExtender webModuleExtender,
            ExtenderController controller, boolean forNewProjectWizard) {
        this.webModuleExtender = webModuleExtender;
        this.controller = controller;
        this.forNewProjectWizard = forNewProjectWizard;
        initComponents();
        setDefaults();
        fillPanel();
        txtSessionName.getDocument().addDocumentListener(this);
        cmbDbConnection.addItemListener(this);
        txtDriver.getDocument().addDocumentListener(this);
        cmbURL.addItemListener(this);
        txtUserName.getDocument().addDocumentListener(this);
        txtPassword.getDocument().addDocumentListener(this);
        

    }

    public void fillPanel() {
        if (cmbDbConnection.getItemCount() != 0) {
            cmbDbConnection.setSelectedIndex(1);
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "LBL_HibernateConfigurationPanel_Name");
    }

    private void fillComponents() {
        DatabaseConnection dbConn = getDatabaseConnection();
        txtDialect.setText(Util.getDialectName(dbConn.getDriverClass()));
        txtDriver.setText(dbConn.getDriverClass());
        cmbURL.setModel(new javax.swing.DefaultComboBoxModel(new String[]{dbConn.getDatabaseURL()}));
        txtUserName.setText(dbConn.getUser());
        txtPassword.setText(dbConn.getPassword());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbURL = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbDbConnection = new javax.swing.JComboBox();
        txtDialect = new javax.swing.JTextField();
        txtDriver = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtSessionName = new javax.swing.JTextField();

        setName(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "LBL_HibernateConfigurationPanel_Name")); // NOI18N

        jLabel4.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("Dialect_mnemonic").charAt(0));
        jLabel4.setLabelFor(txtDialect);
        jLabel4.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel4.text")); // NOI18N

        jLabel5.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("driver_mnemonic").charAt(0));
        jLabel5.setLabelFor(txtDriver);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel5.text")); // NOI18N

        jLabel6.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("URL_mnemonic").charAt(0));
        jLabel6.setLabelFor(cmbURL);
        jLabel6.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel6.text")); // NOI18N

        cmbURL.setEditable(true);
        cmbURL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("Username_mnemonic").charAt(0));
        jLabel1.setLabelFor(txtUserName);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel1.text")); // NOI18N

        txtUserName.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtUserName.text")); // NOI18N

        jLabel2.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("Password_mnemonic").charAt(0));
        jLabel2.setLabelFor(txtPassword);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel2.text")); // NOI18N

        txtPassword.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtPassword.text")); // NOI18N

        jLabel3.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("DatabaseConnection_mnemonic").charAt(0));
        jLabel3.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel3.text")); // NOI18N

        cmbDbConnection.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbDbConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDbConnectionActionPerformed(evt);
            }
        });

        txtDialect.setEditable(false);
        txtDialect.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtDialect.text")); // NOI18N

        txtDriver.setEditable(false);
        txtDriver.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtDriver.text")); // NOI18N

        jLabel7.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("SessionName_mnemonic").charAt(0));
        jLabel7.setLabelFor(txtSessionName);
        jLabel7.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel7.text")); // NOI18N

        txtSessionName.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtSessionName.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(57, 57, 57))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel7)
                                .add(40, 40, 40)
                                .add(txtSessionName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cmbURL, 0, 552, Short.MAX_VALUE)
                                    .add(txtUserName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtDriver, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                                    .add(cmbDbConnection, 0, 552, Short.MAX_VALUE)
                                    .add(txtDialect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(txtSessionName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(cmbDbConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(txtDialect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtDriver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(cmbURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(txtPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel4.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel4.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel5.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel6.AccessibleContext.accessibleDescription")); // NOI18N
        cmbURL.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.cmbURL.AccessibleContext.accessibleName")); // NOI18N
        cmbURL.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.cmbURL.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel1.AccessibleContext.accessibleDescription")); // NOI18N
        txtUserName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtUserName.AccessibleContext.accessibleName")); // NOI18N
        txtUserName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtUserName.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel2.AccessibleContext.accessibleDescription")); // NOI18N
        txtPassword.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtPassword.AccessibleContext.accessibleName")); // NOI18N
        txtPassword.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.txtPassword.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    private void cmbDbConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDbConnectionActionPerformed
        // TODO add your handling code here:
        fillComponents();
    }//GEN-LAST:event_cmbDbConnectionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbDbConnection;
    private javax.swing.JComboBox cmbURL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField txtDialect;
    private javax.swing.JTextField txtDriver;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtSessionName;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
    public void actionPerformed(ActionEvent e) {
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    
    public void insertUpdate(DocumentEvent e) {
        webModuleExtender.fireChangeEvent();
    }

    public void removeUpdate(DocumentEvent e) {
        webModuleExtender.fireChangeEvent();
    }

    public void changedUpdate(DocumentEvent e) {
        webModuleExtender.fireChangeEvent();
    }

    public void itemStateChanged(ItemEvent e) {
        webModuleExtender.fireChangeEvent();
    }

    public String getSessionName() {
        if (txtSessionName.getText() != null) {
            return txtSessionName.getText().trim();
        }
        return null;
    }

    public void setSessionName(String newSessionName) {
        txtSessionName.setText(newSessionName);
    }

    public String getSelectedDialect() {
        if (txtDialect.getText() != null) {
            return txtDialect.getText().trim();
        }
        return null;
    }

    public void setDialect(String dialectName) {
        txtDialect.setText(Util.getDailectCode(dialectName));
    }

    public String getSelectedDriver() {
        if (txtDriver.getText() != null) {
            return txtDriver.getText().trim();
        }
        return null;
    }

    public void setDriver(String driver) {
        txtDriver.setText(driver);
    }

    public String getSelectedURL() {
        if (cmbURL.getSelectedItem() != null) {
            return cmbURL.getSelectedItem().toString();
        }
        return null;
    }

    public void setConnectionURL(String url) {
        cmbURL.setSelectedItem(url);
    }

    public String getUserName() {
        if (txtUserName.getText() != null) {
            return txtUserName.getText().trim();
        }
        return null;
    }

    public void setUserName(String username) {
        txtUserName.setText(username);
    }

    public String getPassword() {
        if (txtPassword.getText() != null) {
            return txtPassword.getText().trim();
        }
        return null;
    }

    public void setPassword(String password) {
        txtPassword.setText(password);
    }

    public DatabaseConnection getDatabaseConnection() {
        return (DatabaseConnection) cmbDbConnection.getSelectedItem();
    }

    public boolean isPanelValid() {
        if (forNewProjectWizard) { // Validate only in case of New Project Wizard.
            if (cmbURL.getSelectedItem() != null &&
                    cmbURL.getSelectedItem().toString().trim().equals("")) {
                controller.setErrorMessage(NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "MSG_connectionUrlEmpty"));
                return false;
            }
            if (txtUserName.getText().trim().equals("")) {
                controller.setErrorMessage(NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "MSG_usernameEmpty"));
                return false;
            }
        }
        return true;
    }
}
