/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExistingPythonProjectPanelVisual.java
 *
 * Created on Sep 12, 2008, 8:06:29 AM
 */

package org.netbeans.modules.python.project.templates;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 */
class ExistingPythonProjectPanelVisual extends SettingsPanel implements DocumentListener {

    private PanelConfigureProject panel;

    /** Creates new form ExistingPythonProjectPanelVisual */
    public ExistingPythonProjectPanelVisual(final PanelConfigureProject panel) {
        assert panel != null;
        this.panel = panel;
        initComponents();
        postInitComponents();
    }
    
    private void postInitComponents () {
        this.projectFolder.getDocument().addDocumentListener(this);
        this.projectName.getDocument().addDocumentListener(this);
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
        projectName = new javax.swing.JTextField();
        projectFolder = new javax.swing.JTextField();
        browse = new javax.swing.JButton();

        jLabel1.setLabelFor(projectName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ExistingPythonProjectPanelVisual.projectName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ExistingPythonProjectPanelVisual.projectFolder.text")); // NOI18N

        projectName.setText(org.openide.util.NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ExistingPythonProjectPanelVisual.projectName.text")); // NOI18N

        projectFolder.setText(org.openide.util.NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ExistingPythonProjectPanelVisual.projectFolder.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browse, org.openide.util.NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ExistingPythonProjectPanelVisual.browse.text")); // NOI18N
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseAction(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(projectFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        .addGap(1, 1, 1)
                        .addComponent(browse)
                        .addGap(48, 48, 48))
                    .addComponent(projectName, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(projectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(projectFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browse))
                .addContainerGap(29, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseAction
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "LBL_SelectProjectFolder"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String path = this.projectFolder.getText();
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectDir = chooser.getSelectedFile();
            projectFolder.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
        }
        panel.fireChangeEvent();
    }//GEN-LAST:event_browseAction


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField projectFolder;
    private javax.swing.JTextField projectName;
    // End of variables declaration//GEN-END:variables

    @Override
    void store(WizardDescriptor settings) {
        assert  settings != null;
        String name = projectName.getText().trim();
        String folder = projectFolder.getText().trim();
        settings.putProperty(NewPythonProjectWizardIterator.PROP_PROJECT_LOCATION, FileUtil.normalizeFile(new File(folder)));
        settings.putProperty(NewPythonProjectWizardIterator.PROP_PROJECT_NAME, name);
    }

    @Override
    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty(NewPythonProjectWizardIterator.PROP_PROJECT_LOCATION);
        String defaultName = null;
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            defaultName = NewPythonProjectWizardIterator.getFreeFolderName(ProjectChooser.getProjectsFolder(), "NewPythonProject");     //NOI18N
            projectLocation = new File (ProjectChooser.getProjectsFolder(),defaultName);
        }
        this.projectFolder.setText(projectLocation.getAbsolutePath());

        String projectName = (String) settings.getProperty(NewPythonProjectWizardIterator.PROP_PROJECT_NAME);
        if (projectName == null) {
            assert defaultName != null;
            projectName = defaultName;
        }
        this.projectName.setText(projectName);
        this.projectName.selectAll();
    }

    @Override
    boolean valid(WizardDescriptor settings) {
        if (projectName.getText().length() == 0) {
            settings.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ERR_WrongName"));
            return false; // Display name not specified
        }
        File f = FileUtil.normalizeFile(new File(projectFolder.getText()).getAbsoluteFile());
        if (f.exists() && !f.isDirectory()) {
            String message = NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ERR_WrongProjectFolder");
            settings.putProperty("WizardPanel_errorMessage", message);
            return false;
        }

        if (f.exists() && !f.canWrite()) {
            settings.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ERR_ReadOnlyProjectFolder"));
            return false;
        }

        if (f.exists() && FileUtil.toFileObject(f) == null) {
            String message = NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ERR_WrongProjectFolder");
            settings.putProperty("WizardPanel_errorMessage", message);
            return false;
        }

        File[] kids = f.listFiles();
        if (kids != null) {
            for (File kid : kids) {
                if ("nbproject".equals(kid.getName())) {    //NOI18N
                    settings.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(ExistingPythonProjectPanelVisual.class, "ERR_AlreadyAnProject"));
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

    @Override
    public void insertUpdate(DocumentEvent e) {
        handleDocChange ();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleDocChange ();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        handleDocChange ();
    }
    
    private void handleDocChange () {
        this.panel.fireChangeEvent();
    }

}
