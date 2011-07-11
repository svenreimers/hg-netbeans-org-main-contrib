/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.openide.util.NbBundle;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;

/**
 *
 * @author  satyaranjan
 */
public class AddServiceUI extends javax.swing.JDialog implements DocumentListener, ChangeListener{
    
    private String serviceName;
    private boolean remoteService;
    private boolean localService;
    private String tableName;
    private boolean addMode;
 
    /** Creates new form AddServiceUI */
    public AddServiceUI(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocation(parent.getX()+(parent.getWidth()-getWidth())/2,parent.getY()+(parent.getHeight()-getHeight())/2);
        setTitle(NbBundle.getMessage(AddServiceUI.class, "LBL_Service_Dlg_Title"));
        getRootPane().setDefaultButton(addButton);
        serviceTf.getDocument().addDocumentListener(this);
        tableTf.getDocument().addDocumentListener(this);
        //setBackground(Color.WHITE);
        //setVisible(true);
        addMode = true;
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
    }
    
    public AddServiceUI(java.awt.Frame parent, Entity entity) {
        this(parent);
        
        serviceName = entity.getName();
        serviceTf.setText(serviceName);
        serviceTf.setEnabled(false);
        
        tableName = entity.getTable();
        if(tableName != null && tableName.trim().length() != 0) {
            tableTf.setText(tableName);
        }
        String rservice = entity.getRemoteService();
        if(rservice != null && rservice.length() != 0)
            remoteService = Boolean.parseBoolean(entity.getRemoteService());
        
        remoteServiceCB.setSelected(remoteService);
        
        localService = true;
        addMode = false;
        addButton.setEnabled(false);
        getRootPane().setDefaultButton(updateButton);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        serviceTf = new javax.swing.JTextField();
        remoteServiceLabel = new javax.swing.JLabel();
        remoteServiceCB = new javax.swing.JCheckBox();
        localServiceCB = new javax.swing.JCheckBox();
        localServiceLabel = new javax.swing.JLabel();
        tableLabel = new javax.swing.JLabel();
        tableTf = new javax.swing.JTextField();
        errorPanel = new javax.swing.JPanel();
        errorLabel = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();

        jButton1.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.jButton1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        addButton.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "LBL_Service_Dlg_Title"))); // NOI18N

        nameLabel.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.nameLabel.text")); // NOI18N

        serviceTf.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.serviceTf.text")); // NOI18N

        remoteServiceLabel.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.remoteServiceLabel.text")); // NOI18N

        remoteServiceCB.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.remoteServiceCB.text")); // NOI18N
        remoteServiceCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                remoteServiceCBItemStateChanged(evt);
            }
        });

        localServiceCB.setSelected(true);
        localServiceCB.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.localServiceCB.text")); // NOI18N
        localServiceCB.setEnabled(false);

        localServiceLabel.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.localServiceLabel.text")); // NOI18N

        tableLabel.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.tableLabel.text")); // NOI18N

        tableTf.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.tableTf.text")); // NOI18N

        errorLabel.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.errorLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout errorPanelLayout = new org.jdesktop.layout.GroupLayout(errorPanel);
        errorPanel.setLayout(errorPanelLayout);
        errorPanelLayout.setHorizontalGroup(
            errorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(errorPanelLayout.createSequentialGroup()
                .add(errorLabel)
                .addContainerGap(401, Short.MAX_VALUE))
        );
        errorPanelLayout.setVerticalGroup(
            errorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, errorPanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .add(errorLabel)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(errorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(nameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .add(27, 27, 27))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(tableLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                .add(65, 65, 65)))
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tableTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, serviceTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                        .add(24, 24, 24))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(remoteServiceLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .add(18, 18, 18))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(localServiceLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                .add(31, 31, 31)))
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(localServiceCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                                .add(217, 217, 217))
                            .add(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(remoteServiceCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(nameLabel)
                    .add(serviceTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tableLabel)
                    .add(tableTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(4, 4, 4)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(remoteServiceLabel)
                    .add(remoteServiceCB))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(localServiceLabel)
                    .add(localServiceCB))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(errorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updateButton.setText(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.updateButton.text")); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(222, 222, 222)
                        .add(addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updateButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(updateButton)
                    .add(addButton))
                .addContainerGap())
        );

        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.addButton.AccessibleContext.accessibleDescription")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
// TODO add your handling code here:
    serviceName = serviceTf.getText();
    remoteService = remoteServiceCB.isSelected();
    localService = localServiceCB.isSelected();
    tableName = tableTf.getText();
    dispose();
}//GEN-LAST:event_addButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// TODO add your handling code here:
    serviceName = null;
    dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
// TODO add your handling code here:
    serviceName = serviceTf.getText();
    remoteService = remoteServiceCB.isSelected();
    localService = localServiceCB.isSelected();
    tableName = tableTf.getText();
    dispose();
}//GEN-LAST:event_updateButtonActionPerformed

private void remoteServiceCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_remoteServiceCBItemStateChanged
// TODO add your handling code here:
    if (!addMode){
        setErrorMessage("");
        updateButton.setEnabled(true);
    }
}//GEN-LAST:event_remoteServiceCBItemStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddServiceUI dialog = new AddServiceUI(new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public boolean isLocalService() {
        return localService;
    }

    public boolean isRemoteService() {
        return remoteService;
    }

    public String getServiceName() {
        return serviceName;
    }
    
    public String getTableName() {
        return tableName;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel errorPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox localServiceCB;
    private javax.swing.JLabel localServiceLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JCheckBox remoteServiceCB;
    private javax.swing.JLabel remoteServiceLabel;
    private javax.swing.JTextField serviceTf;
    private javax.swing.JLabel tableLabel;
    private javax.swing.JTextField tableTf;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

    private void setErrorMessage(String msg) {
        errorLabel.setForeground(Color.RED);
        errorLabel.setText(msg);
    }

    public void insertUpdate(DocumentEvent e) {
        updateText(e);
    }

    public void removeUpdate(DocumentEvent e) {
        updateText(e);
    }

    public void changedUpdate(DocumentEvent e) {
        updateText(e);
    }

    private void updateText(DocumentEvent e) {
        if(addMode && !valid()) {
            addButton.setEnabled(false);
        } else if (!addMode && !valid()){
            //setErroMessage("");
            updateButton.setEnabled(false);
        } else if (!addMode && valid()){
            setErrorMessage("");
            updateButton.setEnabled(true);
        } else if (addMode && valid()){
            // Add mode and Valid
            setErrorMessage("");
            addButton.setEnabled(true);
        } 
    }
    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
     public boolean valid() {
        String entityName = serviceTf.getText();
        String enTableName = tableTf.getText();
        if ((entityName == null || entityName.trim().length() == 0) || !CoreUtil.validateJavaTypeName(entityName)) {
            setErrorMessage(NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.errorLabel.servicename"));
            return false;
        } 
        if (enTableName.trim().length() != 0 && !CoreUtil.validateJavaTypeName(enTableName)) {
                setErrorMessage(NbBundle.getMessage(AddServiceUI.class, "AddServiceUI.errorLabel.tablename"));
                return false;
        }
        return true;
     }
}
