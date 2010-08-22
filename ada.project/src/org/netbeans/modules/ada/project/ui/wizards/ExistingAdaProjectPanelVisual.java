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

package org.netbeans.modules.ada.project.ui.wizards;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrea Lucarelli
 */
class ExistingAdaProjectPanelVisual extends SettingsPanel implements DocumentListener {

    private PanelConfigureProject panel;

    /**
     *
     * Creates new form ExistingAdaProjectPanelVisual
     */
    public ExistingAdaProjectPanelVisual(final PanelConfigureProject panel) {
        assert panel != null;
        this.panel = panel;
        initComponents();
        postInitComponents();
    }
    
    private void postInitComponents () {
        this.projectFolderTextField.getDocument().addDocumentListener(this);
        this.projectNameTextField.getDocument().addDocumentListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectFolderTextField = new javax.swing.JTextField();
        browse = new javax.swing.JButton();

        jLabel1.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ExistingAdaProjectPanelVisual.projectName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ExistingAdaProjectPanelVisual.projectFolder.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browse, org.openide.util.NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ExistingAdaProjectPanelVisual.browse.text")); // NOI18N
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                        .add(1, 1, 1)
                        .add(browse))
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browse))
                .addContainerGap(29, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseAction
        JFileChooser chooser = new JFileChooser();
//        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "LBL_SelectProjectFolder"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String path = this.projectFolderTextField.getText();
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectDir = chooser.getSelectedFile();
            projectFolderTextField.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
        }
        panel.fireChangeEvent();
    }//GEN-LAST:event_browseAction


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField projectFolderTextField;
    private javax.swing.JTextField projectNameTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    void store(WizardDescriptor settings) {
        assert  settings != null;
        String name = projectNameTextField.getText().trim();
        String folder = projectFolderTextField.getText().trim();
        settings.putProperty(NewAdaProjectWizardIterator.PROP_PROJECT_LOCATION, FileUtil.normalizeFile(new File(folder)));
        settings.putProperty(NewAdaProjectWizardIterator.PROP_PROJECT_NAME, name);
    }

    @Override
    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty(NewAdaProjectWizardIterator.PROP_PROJECT_LOCATION);
        String defaultName = null;
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            defaultName = NewAdaProjectWizardIterator.getFreeFolderName(ProjectChooser.getProjectsFolder(), "AdaApplication");     //NOI18N
            projectLocation = new File (ProjectChooser.getProjectsFolder(),defaultName);
        }
        this.projectFolderTextField.setText(projectLocation.getAbsolutePath());

        String projectName = (String) settings.getProperty(NewAdaProjectWizardIterator.PROP_PROJECT_NAME);
        if (projectName == null) {
            assert defaultName != null;
            projectName = defaultName;
        }
        this.projectNameTextField.setText(projectName);
        this.projectNameTextField.selectAll();
    }

    @Override
    boolean valid(WizardDescriptor settings) {
        if (projectNameTextField.getText().length() == 0) {
            settings.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ERR_WrongName"));
            return false; // Display name not specified
        }
        File f = FileUtil.normalizeFile(new File(projectFolderTextField.getText()).getAbsoluteFile());
        if (f.exists() && !f.isDirectory()) {
            String message = NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ERR_WrongProjectFolder");
            settings.putProperty("WizardPanel_errorMessage", message);
            return false;
        }

        if (f.exists() && !f.canWrite()) {
            settings.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ERR_ReadOnlyProjectFolder"));
            return false;
        }

        if (f.exists() && FileUtil.toFileObject(f) == null) {
            String message = NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ERR_WrongProjectFolder");
            settings.putProperty("WizardPanel_errorMessage", message);
            return false;
        }

        File[] kids = f.listFiles();
        if (kids != null) {
            for (File kid : kids) {
                if ("nbproject".equals(kid.getName())) {    //NOI18N
                    settings.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(ExistingAdaProjectPanelVisual.class, "ERR_AlreadyAnProject"));
                    return false;
                }
            }
        }
        settings.putProperty("WizardPanel_errorMessage", "");   //NOI18N
        return true;
    }

    @Override
    void validate(WizardDescriptor settings) throws WizardValidationException {        
    }

    public void insertUpdate(DocumentEvent e) {
        handleDocChange ();
        }

    public void removeUpdate(DocumentEvent e) {
        handleDocChange ();
        }

    public void changedUpdate(DocumentEvent e) {
        handleDocChange ();
        }
    
    private void handleDocChange () {
        this.panel.fireChangeEvent();
        }

}
