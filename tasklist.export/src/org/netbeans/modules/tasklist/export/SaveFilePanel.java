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

package org.netbeans.modules.tasklist.export;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Panel with a file chooser
 */
public class SaveFilePanel extends javax.swing.JPanel {
    private JFileChooser fc = new JFileChooser();
    private SimpleWizardPanel panel;

    /** Creates new form ChooseFilePanel */
    public SaveFilePanel() {
        initComponents();
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        jTextFieldFile.getDocument().addDocumentListener(
            new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    check();
                }
                public void removeUpdate(DocumentEvent e) {
                    check();
                }
                public void changedUpdate(DocumentEvent e) {
                    check();
                }
            }
        );
        jCheckBoxOpen.setVisible(false);
    }
    
    /**
     * Sets the visibility of "Open File" checkbox.
     *
     * @param b true = visible
     */
    public void setOpenFileCheckBoxVisible(boolean b) {
        jCheckBoxOpen.setVisible(b);
    }
    
    /**
     * Returns the state of the "open exported file" checkbox.
     *
     * @return true = the checkbox is checked.
     */
    public boolean getOpenExportedFile() {
        return jCheckBoxOpen.isSelected();
    }
    
    /**
     * Associates a wizard panel with this Swing panel.
     * This method should be called to initialize this panel.
     *
     * @param panel associated panel. 
     */
    public void setWizardPanel(SimpleWizardPanel panel) {
        this.panel = panel;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jTextFieldFile = new javax.swing.JTextField();
        jButtonChoose = new javax.swing.JButton();
        jCheckBoxOpen = new javax.swing.JCheckBox();

        setName(org.openide.util.NbBundle.getBundle(SaveFilePanel.class).getString("ChooseFile")); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(SaveFilePanel.class).getString("File")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jTextFieldFile, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonChoose, "...");
        jButtonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseActionPerformed(evt);
            }
        });
        add(jButtonChoose, new java.awt.GridBagConstraints());

        jCheckBoxOpen.setText(org.openide.util.NbBundle.getMessage(SaveFilePanel.class, "OpenExportedFile")); // NOI18N
        jCheckBoxOpen.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxOpen.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jCheckBoxOpen, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseActionPerformed
        fc.setSelectedFile(new File(jTextFieldFile.getText()));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) 
            return;
        
        if (fc.getSelectedFile().exists()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(SaveFilePanel.class, 
                    "FileExistsOverwrite"), // NOI18N
                NbBundle.getMessage(SaveFilePanel.class, 
                    "Warning"), // NOI18N
                NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != 
            NotifyDescriptor.OK_OPTION) 
                return;
        }
        jTextFieldFile.setText(fc.getSelectedFile().getAbsolutePath()); 
    }//GEN-LAST:event_jButtonChooseActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChoose;
    private javax.swing.JCheckBox jCheckBoxOpen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextFieldFile;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Returns the associated file chooser
     *
     * @return file chooser
     */
    public JFileChooser getFileChooser() {
        return fc;
    }

    /**
     * Sets choosed file
     *
     * @param f choosed file
     */
    public void setFile(File f) {
        jTextFieldFile.setText(f.getAbsolutePath());
        fc.setSelectedFile(f);
        check();
    }
    
    /**
     * Returns selected file
     *
     * @return selected file or null
     */
    public File getFile() {
        return new File(jTextFieldFile.getText());
    }
    
    /**
     * Sets the valid property
     */
    private void check() {
        String t = jTextFieldFile.getText().trim();
        if (t.length() == 0) {
            panel.setErrorMessage(NbBundle.getMessage(
                SaveFilePanel.class, "EmptyFileName")); // NOI18N
            return;
        }  
            
        File f = new File(t);
        if (f.isDirectory()) {
            panel.setErrorMessage(NbBundle.getMessage(
                SaveFilePanel.class, "NotAFile")); // NOI18N
            return;
        }
        
        File dir = f.getParentFile();
        if (dir == null || !dir.isDirectory()) {
            // Parent directory does not exist
            panel.setErrorMessage(NbBundle.getMessage(
                SaveFilePanel.class, "NoParent")); // NOI18N
            return;
        }
        
        if (!f.isAbsolute()) {
            panel.setErrorMessage(NbBundle.getMessage(
                SaveFilePanel.class, "NotAnAbsolutePath")); // NOI18N
            return;
        }        
        
        try {
            System.getSecurityManager().checkWrite(f.getAbsolutePath());
            panel.setErrorMessage(null);
        } catch (SecurityException e) {
            panel.setErrorMessage(e.getLocalizedMessage());
        }
    }

    /**
     * Changes the state of the "open file" checkbox.
     *
     * @param open true = selected
     */
    public void setOpenFileCheckBox(boolean open) {
        jCheckBoxOpen.setSelected(open);
    }
}
