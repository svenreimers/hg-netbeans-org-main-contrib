
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.ui.panels;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEPreferences;
import com.sun.tthub.gde.logic.GDEPreferencesController;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author  hr157577
 */
public class PreferencesJPanel extends javax.swing.JPanel {
    
    /**
     * Creates new form PreferencesJPanel
     */
    public PreferencesJPanel() {
        initComponents();
    }
    
    private void displayErrorMsg(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, 
                title, JOptionPane.ERROR_MESSAGE);
    }
    
    private boolean isValidDirectory(String dirName) {
        File file = new File(dirName);
        return  (file.isDirectory() && file.canRead());
    }
    private boolean validateGdePreferences() {
        String javaHome = txtJavaHome.getText();
        String antHome = txtAntHome.getText();
        String gdeFolder = txtGdeFolder.getText();
        String appServHome = txtAppServHome.getText();
        String portalServHome = txtPortalServHome.getText();
        
        String title = "Validation Error";
        
        if(javaHome == null || 
                javaHome.trim().equals("") || !isValidDirectory(javaHome)) {
            displayErrorMsg("Java Home should be a valid directory", title);
            txtJavaHome.grabFocus();
            return false;
        }
        
        if(antHome == null || 
                antHome.trim().equals("") || !isValidDirectory(antHome)) {
            displayErrorMsg("Ant Home should be a valid directory", title);
            txtAntHome.grabFocus();
            return false;
        }        
        
        if(gdeFolder == null || 
                gdeFolder.trim().equals("") || !isValidDirectory(gdeFolder)) {
            displayErrorMsg("GDE Folder should be a valid directory", title);
            txtGdeFolder.grabFocus();
            return false;
        }
        
        if(appServHome != null && !appServHome.trim().equals("") && 
                            !isValidDirectory(appServHome)){
            displayErrorMsg("App Server Home specified is not " +
                    "       a vaild directory.", title);
            txtAppServHome.grabFocus();
            return false;
        }
        
        if(portalServHome != null && !portalServHome.trim().equals("") && 
                            !isValidDirectory(portalServHome)){
            displayErrorMsg("Portal Server Home specified is not " +
                    "       a vaild directory.", title);
            txtPortalServHome.grabFocus();
            return false;
        }
        
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTItle = new javax.swing.JLabel();
        pnlPreferences = new javax.swing.JPanel();
        lblJavaHome = new javax.swing.JLabel();
        txtJavaHome = new javax.swing.JTextField();
        lblAntHome = new javax.swing.JLabel();
        txtAntHome = new javax.swing.JTextField();
        lblGdeFolder = new javax.swing.JLabel();
        txtGdeFolder = new javax.swing.JTextField();
        btnJavaHome = new javax.swing.JButton();
        btnAntHome = new javax.swing.JButton();
        btnGdeFolder = new javax.swing.JButton();
        lblAppServHome = new javax.swing.JLabel();
        txtAppServHome = new javax.swing.JTextField();
        btnAppServHome = new javax.swing.JButton();
        lblPortalServHome = new javax.swing.JLabel();
        txtPortalServHome = new javax.swing.JTextField();
        btnPortalServHome = new javax.swing.JButton();
        lblDirServUserDN = new javax.swing.JLabel();
        txtDirServUserDN = new javax.swing.JTextField();
        pnlControl = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        lblTItle.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTItle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTItle.setText("GDE Wizard Preferences");

        pnlPreferences.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblJavaHome.setText("Java Home:");

        txtJavaHome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblAntHome.setText("Ant Home:");

        txtAntHome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblGdeFolder.setText("GDE Folder:");

        txtGdeFolder.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnJavaHome.setText("...");
        btnJavaHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJavaHomeActionPerformed(evt);
            }
        });

        btnAntHome.setText("...");
        btnAntHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAntHomeActionPerformed(evt);
            }
        });

        btnGdeFolder.setText("...");
        btnGdeFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGdeFolderActionPerformed(evt);
            }
        });

        lblAppServHome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAppServHome.setText("App Server Home:");

        txtAppServHome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnAppServHome.setText("...");
        btnAppServHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAppServHomeActionPerformed(evt);
            }
        });

        lblPortalServHome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPortalServHome.setText("Portal Server Home:");

        txtPortalServHome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnPortalServHome.setText("...");
        btnPortalServHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPortalServHomeActionPerformed(evt);
            }
        });

        lblDirServUserDN.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDirServUserDN.setText("Dir Server User DN:");

        txtDirServUserDN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        org.jdesktop.layout.GroupLayout pnlPreferencesLayout = new org.jdesktop.layout.GroupLayout(pnlPreferences);
        pnlPreferences.setLayout(pnlPreferencesLayout);
        pnlPreferencesLayout.setHorizontalGroup(
            pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPreferencesLayout.createSequentialGroup()
                .add(25, 25, 25)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblAntHome)
                    .add(lblGdeFolder)
                    .add(lblJavaHome)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPreferencesLayout.createSequentialGroup()
                        .add(lblAppServHome)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPreferencesLayout.createSequentialGroup()
                        .add(lblPortalServHome)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPreferencesLayout.createSequentialGroup()
                        .add(lblDirServUserDN)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtJavaHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(txtAntHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(txtGdeFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(txtAppServHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(txtPortalServHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(txtDirServUserDN, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(btnJavaHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnAntHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(btnGdeFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnPortalServHome, 0, 0, Short.MAX_VALUE)
                    .add(btnAppServHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPreferencesLayout.setVerticalGroup(
            pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPreferencesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJavaHome)
                    .add(txtJavaHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnJavaHome))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPreferencesLayout.createSequentialGroup()
                        .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAntHome)
                            .add(txtAntHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblGdeFolder)
                            .add(txtGdeFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPreferencesLayout.createSequentialGroup()
                        .add(btnAntHome)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnGdeFolder)))
                .add(6, 6, 6)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btnAppServHome)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtAppServHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lblAppServHome)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtPortalServHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lblPortalServHome))
                    .add(btnPortalServHome))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPreferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtDirServUserDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblDirServUserDN))
                .addContainerGap())
        );

        pnlPreferencesLayout.linkSize(new java.awt.Component[] {btnAppServHome, btnPortalServHome}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pnlPreferencesLayout.linkSize(new java.awt.Component[] {btnJavaHome, lblJavaHome, txtJavaHome}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pnlPreferencesLayout.linkSize(new java.awt.Component[] {btnGdeFolder, lblAppServHome, lblDirServUserDN, lblGdeFolder, lblPortalServHome, txtAppServHome, txtDirServUserDN, txtGdeFolder, txtPortalServHome}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pnlPreferencesLayout.linkSize(new java.awt.Component[] {btnAntHome, lblAntHome, txtAntHome}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pnlControl.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");

        org.jdesktop.layout.GroupLayout pnlControlLayout = new org.jdesktop.layout.GroupLayout(pnlControl);
        pnlControl.setLayout(pnlControlLayout);
        pnlControlLayout.setHorizontalGroup(
            pnlControlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlControlLayout.createSequentialGroup()
                .addContainerGap(322, Short.MAX_VALUE)
                .add(btnSave)
                .add(18, 18, 18)
                .add(btnCancel)
                .addContainerGap())
        );
        pnlControlLayout.setVerticalGroup(
            pnlControlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlControlLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlControlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblTItle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, pnlControl)
                        .add(pnlPreferences)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lblTItle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPreferences, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlControl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    // </editor-fold>//GEN-END:initComponents

    private void btnPortalServHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPortalServHomeActionPerformed
        String folderName = getFolderFromFileChooser();
        if(folderName != null) {
            txtPortalServHome.setText(folderName);
        }
    }//GEN-LAST:event_btnPortalServHomeActionPerformed

    private void btnAppServHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppServHomeActionPerformed
        String folderName = getFolderFromFileChooser();
        if(folderName != null) {
            txtAppServHome.setText(folderName);
        }
    }//GEN-LAST:event_btnAppServHomeActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(!validateGdePreferences())
            return;
        GDEPreferences pref = new GDEPreferences(
                txtJavaHome.getText().trim(), txtAntHome.getText().trim(),
                txtGdeFolder.getText().trim(), txtAppServHome.getText().trim(),
                txtPortalServHome.getText().trim(), 
                txtDirServUserDN.getText().trim());
        GDEPreferencesController controller = 
                GDEAppContext.getInstance().getGdePrefsController();
        try {
            controller.saveGdePreferences(pref);
        } catch(GDEException ex) {
            displayErrorMsg(ex.getMessage(), "Error while saving the preferences");
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnGdeFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGdeFolderActionPerformed
        String folderName = getFolderFromFileChooser();
        if(folderName != null) {
            txtGdeFolder.setText(folderName);
        }
    }//GEN-LAST:event_btnGdeFolderActionPerformed

    private String getFolderFromFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        // Disable all file filters.
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Disable the multiselection feature
        fileChooser.setMultiSelectionEnabled(false); 
        //Set the filter so that the file chooser will display only the jsp files.
        fileChooser.setFileFilter(new CustomFileFilter("All Directories", true));
        
        int retVal = fileChooser.showOpenDialog(this); // Show the 'Open File' dialog.
        if(retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            file.getPath();
        }                
        return null;
    }
    private void btnAntHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAntHomeActionPerformed
        String folderName = getFolderFromFileChooser();
        if(folderName != null) {
            txtAntHome.setText(folderName);
        }
    }//GEN-LAST:event_btnAntHomeActionPerformed

    private void btnJavaHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJavaHomeActionPerformed
        String folderName = getFolderFromFileChooser();
        if(folderName != null) {
            txtJavaHome.setText(folderName);
        }
    }//GEN-LAST:event_btnJavaHomeActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnAntHome;
    public javax.swing.JButton btnAppServHome;
    public javax.swing.JButton btnCancel;
    public javax.swing.JButton btnGdeFolder;
    public javax.swing.JButton btnJavaHome;
    public javax.swing.JButton btnPortalServHome;
    public javax.swing.JButton btnSave;
    public javax.swing.JLabel lblAntHome;
    public javax.swing.JLabel lblAppServHome;
    public javax.swing.JLabel lblDirServUserDN;
    public javax.swing.JLabel lblGdeFolder;
    public javax.swing.JLabel lblJavaHome;
    public javax.swing.JLabel lblPortalServHome;
    public javax.swing.JLabel lblTItle;
    public javax.swing.JPanel pnlControl;
    public javax.swing.JPanel pnlPreferences;
    public javax.swing.JTextField txtAntHome;
    public javax.swing.JTextField txtAppServHome;
    public javax.swing.JTextField txtDirServUserDN;
    public javax.swing.JTextField txtGdeFolder;
    public javax.swing.JTextField txtJavaHome;
    public javax.swing.JTextField txtPortalServHome;
    // End of variables declaration//GEN-END:variables
    
}
