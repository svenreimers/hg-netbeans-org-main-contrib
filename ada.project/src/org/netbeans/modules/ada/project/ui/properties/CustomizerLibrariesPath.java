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


package org.netbeans.modules.ada.project.ui.properties;

import java.io.IOException;
import javax.swing.JFileChooser;

import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.api.ada.platform.AdaPlatformManager;
import org.netbeans.modules.ada.platform.ui.PathListModel;
import org.netbeans.modules.ada.project.ui.Utils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;
import org.openide.util.Exceptions;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Andrea Lucarelli
 */
public class CustomizerLibrariesPath extends javax.swing.JPanel {
    private final AdaProjectProperties uiProperties;
    
    /** Creates new form CustomizerLibrariesPath */
    public CustomizerLibrariesPath(AdaProjectProperties properties) {
        this.uiProperties = properties;
        initComponents();
        adaPathModel.setModel(uiProperties.getLibrariesPath());
        this.platforms.setRenderer(Utils.createPlatformRenderer());
        this.platforms.setModel(Utils.createPlatformModel());
        final AdaPlatformManager manager = AdaPlatformManager.getInstance();
        String pid = uiProperties.getActivePlatformId();
        if (pid == null) {
            pid = manager.getDefaultPlatform();
        }
        final AdaPlatform activePlatform = manager.getPlatform(pid);
        if (activePlatform != null) {
            platforms.setSelectedItem(activePlatform);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        adaPath = new javax.swing.JList();
        addAdaPath = new javax.swing.JButton();
        removeAdaPath = new javax.swing.JButton();
        moveUpAdaPath = new javax.swing.JButton();
        moveDownAdaPath = new javax.swing.JButton();
        manage = new javax.swing.JButton();
        platforms = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        adaPath.setModel(adaPathModel);
        adaPath.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(adaPath);

        addAdaPath.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.addAdaPath.text_1")); // NOI18N
        addAdaPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAdaPathActionPerformed(evt);
            }
        });

        removeAdaPath.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.removeAdaPath.text_1")); // NOI18N
        removeAdaPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAdaPathActionPerformed(evt);
            }
        });

        moveUpAdaPath.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.moveUpAdaPath.text_1")); // NOI18N
        moveUpAdaPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpAdaPathActionPerformed(evt);
            }
        });

        moveDownAdaPath.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.moveDownAdaPath.text_1")); // NOI18N
        moveDownAdaPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownAdaPathActionPerformed(evt);
            }
        });

        manage.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.manage.text")); // NOI18N
        manage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageActionPerformed(evt);
            }
        });

        platforms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        platforms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platformsActionPerformed(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(CustomizerLibrariesPath.class, "CustomizerLibrariesPath.jLabel2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(platforms, 0, 337, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(manage)
                        .add(30, 30, 30))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 397, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(moveDownAdaPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                            .add(moveUpAdaPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                            .add(removeAdaPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                            .add(addAdaPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)))
                    .add(jLabel2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(platforms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel1))
                    .add(manage))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addAdaPath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeAdaPath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveUpAdaPath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveDownAdaPath))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addAdaPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAdaPathActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setFileHidingEnabled(false);
        fc.setDialogTitle("Select Ada Lib Directory");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            String cmd = fc.getSelectedFile().getAbsolutePath();
            adaPathModel.add( cmd);
        }
}//GEN-LAST:event_addAdaPathActionPerformed

    private void removeAdaPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAdaPathActionPerformed
        adaPathModel.remove(adaPath.getSelectedIndex());
}//GEN-LAST:event_removeAdaPathActionPerformed

    private void moveUpAdaPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpAdaPathActionPerformed
        adaPathModel.moveUp(adaPath.getSelectedIndex());
}//GEN-LAST:event_moveUpAdaPathActionPerformed

    private void moveDownAdaPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownAdaPathActionPerformed
        adaPathModel.moveDown(adaPath.getSelectedIndex());
}//GEN-LAST:event_moveDownAdaPathActionPerformed

    private void manageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageActionPerformed
        //Workaround, Needs an API to display platform customizer
        final FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Actions/Ada/org-netbeans-modules-ada-platform-AdaPlatformAction.instance");  //NOI18N
        if (fo != null) {
            try {
                InstanceDataObject ido = (InstanceDataObject) DataObject.find(fo);
                CallableSystemAction action = (CallableSystemAction) ido.instanceCreate();
                action.performAction();
                platforms.setModel(Utils.createPlatformModel());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
}//GEN-LAST:event_manageActionPerformed

    private void platformsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platformsActionPerformed
        uiProperties.setActivePlatformId(
                        ((AdaPlatform)platforms.getSelectedItem()).getName());
    }//GEN-LAST:event_platformsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList adaPath;
    private javax.swing.JButton addAdaPath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton manage;
    private javax.swing.JButton moveDownAdaPath;
    private javax.swing.JButton moveUpAdaPath;
    private javax.swing.JComboBox platforms;
    private javax.swing.JButton removeAdaPath;
    // End of variables declaration//GEN-END:variables
    private PathListModel adaPathModel = new PathListModel();
}