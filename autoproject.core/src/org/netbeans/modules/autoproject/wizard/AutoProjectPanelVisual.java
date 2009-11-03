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

package org.netbeans.modules.autoproject.wizard;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class AutoProjectPanelVisual extends JPanel implements DocumentListener {

    private AutoProjectWizardPanel panel;

    public AutoProjectPanelVisual(AutoProjectWizardPanel panel) {
        initComponents();
        this.panel = panel;
        projectLocationTextField.getDocument().addDocumentListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        configureDetectorsSeparator = new javax.swing.JSeparator();
        configureDetectorsLabel = new javax.swing.JLabel();
        configureDetectorsButton = new javax.swing.JButton();

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, org.openide.util.NbBundle.getMessage(AutoProjectPanelVisual.class, "AutoProjectPanelVisual.projectLocationLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(AutoProjectPanelVisual.class, "AutoProjectPanelVisual.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(configureDetectorsLabel, org.openide.util.NbBundle.getMessage(AutoProjectPanelVisual.class, "AutoProjectPanelVisual.configureDetectorsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(configureDetectorsButton, org.openide.util.NbBundle.getMessage(AutoProjectPanelVisual.class, "AutoProjectPanelVisual.configureDetectorsButton.text")); // NOI18N
        configureDetectorsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configureDetectorsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(configureDetectorsSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(projectLocationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton))
                    .add(configureDetectorsLabel)
                    .add(configureDetectorsButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectLocationLabel)
                    .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .add(18, 18, 18)
                .add(configureDetectorsSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(configureDetectorsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(configureDetectorsButton)
                .addContainerGap(183, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle("Select Project Location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String path = this.projectLocationTextField.getText();
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectDir = chooser.getSelectedFile();
            projectLocationTextField.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
        }
        panel.fireChangeEvent();
    }//GEN-LAST:event_browseButtonActionPerformed

    private void configureDetectorsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureDetectorsButtonActionPerformed
        DialogDescriptor dd = new DialogDescriptor(new ConfigureDetectorsDialog(),
                NbBundle.getMessage(AutoProjectPanelVisual.class, "AutoProjectPanelVisual.configureDetectors.title"));
        dd.setOptions(new Object[] {NotifyDescriptor.OK_OPTION});
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }//GEN-LAST:event_configureDetectorsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton configureDetectorsButton;
    private javax.swing.JLabel configureDetectorsLabel;
    private javax.swing.JSeparator configureDetectorsSeparator;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectLocationLabel.requestFocus();
    }

    boolean valid(WizardDescriptor wizardDescriptor) {
        File f = FileUtil.normalizeFile(new File(projectLocationTextField.getText()).getAbsoluteFile());
        if (!f.isDirectory()) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, NbBundle.getMessage(AutoProjectPanelVisual.class, "ERR_not_a_dir"));
            return false;
        }
        wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        if (new File(f, "nbproject/project.xml").isFile()) { // #153232
            wizardDescriptor.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(AutoProjectPanelVisual.class, "AutoProjectPanelVisual.nbproject"));
        }
        return true;
    }

    void store(WizardDescriptor d) {
        d.putProperty("projdir", FileUtil.normalizeFile(new File(projectLocationTextField.getText()).getAbsoluteFile()));
    }

    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty("projdir");
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        this.projectLocationTextField.setText(projectLocation.getAbsolutePath());
    }

    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    // Implementation of DocumentListener --------------------------------------
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        panel.fireChangeEvent(); // Notify that the panel changed
    }
}
