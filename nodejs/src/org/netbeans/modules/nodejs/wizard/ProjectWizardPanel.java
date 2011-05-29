package org.netbeans.modules.nodejs.wizard;

import java.awt.Component;
import java.io.File;
import java.util.MissingResourceException;
import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.validation.adapters.WizardDescriptorAdapter;
import org.netbeans.modules.nodejs.DefaultExectable;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;

/**
 *
 * @author Tim Boudreau
 */
public class ProjectWizardPanel extends JPanel implements DocumentListener {

    private final ValidationGroup grp;
    private final WizardDescriptor desc;
    final WizardDescriptor.Panel<WizardDescriptor> pnl = new WizardDescriptor.Panel<WizardDescriptor>() {

        @Override
        public Component getComponent() {
            return ProjectWizardPanel.this;
        }

        @Override
        public HelpCtx getHelp() {
            return HelpCtx.DEFAULT_HELP;
        }

        @Override
        public boolean isValid() {
            Problem p = grp.validateAll();
            return p == null || !p.isFatal();
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        @Override
        public void readSettings(WizardDescriptor settings) {
            String name = (String) settings.getProperty(ProjectWizardKeys.WIZARD_PROP_PROJECT_NAME);
            if (name != null) {
                nameField.setText(name);
            }
            String port = (String) settings.getProperty(ProjectWizardKeys.WIZARD_PROP_PORT);
            if (port != null) {
                portField.setText(port);
            }
            Boolean gen = (Boolean) settings.getProperty(ProjectWizardKeys.WIZARD_PROP_GENERATE_PACKAGE_JSON);
            if (gen == null) {
                packageJsonBox.setSelected(true);
            } else {
                packageJsonBox.setSelected(gen);
            }
        }

        @Override
        public void storeSettings(WizardDescriptor settings) {
            settings.putProperty(ProjectWizardKeys.WIZARD_PROP_PROJECT_NAME, nameField.getText());
            settings.putProperty(ProjectWizardKeys.WIZARD_PROP_PORT, portField.getText());
            settings.putProperty(ProjectWizardKeys.WIZARD_PROP_PROJECT_DIR, getProposedProjectDir());
            settings.putProperty(ProjectWizardKeys.WIZARD_PROP_GENERATE_PACKAGE_JSON, packageJsonBox.isSelected());
        }
    };
    private final ChangeSupport changeSupport = new ChangeSupport(pnl);

    private ComboBoxModel createLicensesModel() {
        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        FileObject fo = FileUtil.getConfigFile("Templates/Licenses");
        DataFolder dob = DataFolder.findFolder(fo);
        mdl.addElement(NbBundle.getMessage(ProjectWizardPanel.class, "NONE"));
        for (DataObject ob : NbCollections.iterable(dob.children())) {
            mdl.addElement(new License(ob));
        }
        return mdl;
    }

    private static final class License {

        private final DataObject ob;

        public License(DataObject ob) {
            this.ob = ob;
        }

        public String toString() {
            String s = ob.getNodeDelegate().getDisplayName();
            String s1 = ob.getPrimaryFile().getName();
            if (s.equals(s1)) {
                try {
                    s = NbBundle.getMessage(ProjectWizardPanel.class, s);
                } catch (MissingResourceException e) {
                    //do nothing
                    if (s.startsWith("license-") && !s.equals("license-")) {
                        s = s.substring("license-".length());
                    }
                }
            }
            return s;
        }
        
        public String getLicenseName() {
            String s = ob.getPrimaryFile().getName();
            System.out.println("license name is " + s);
            if (s.startsWith("license-") && !s.equals("license-")) {
                s = s.substring ("license-".length());
            }
            System.out.println(" converted to " + s);
            return s;
        }
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public ProjectWizardPanel(WizardDescriptor desc) {
        this.desc = desc;
        initComponents();
        configureCaptions(getComponents());
        grp = ValidationGroup.create(new WizardDescriptorAdapter(desc));
        grp.add(nameField,
                Validators.REQUIRE_VALID_FILENAME,
                Validators.REQUIRE_NON_EMPTY_STRING);

        grp.add(portField,
                Validators.REQUIRE_NON_EMPTY_STRING,
                Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.numberRange(1, 65535),
                Validators.REQUIRE_VALID_INTEGER);

        grp.add(createInField,
                Validators.REQUIRE_NON_EMPTY_STRING,
                Validators.FILE_MUST_EXIST,
                new ParentMustExistValidator());

        nameField.getDocument().addDocumentListener(this);
        nameField.setName(nameLabel.getText());
        portField.setName(portLabel.getText());
        createInField.setName(createInLabel.getText());
    }

    static final class ParentMustExistValidator implements Validator<String> {

        @Override
        public boolean validate(Problems prblms, String compName, String file) {
            File f = new File(file);
            File parent = f.getParentFile();
            boolean result = true;
            if (f != null && parent != null) {
                result = Validators.REQUIRE_VALID_FILENAME.validate(prblms, compName, f.getName());
                result |= Validators.merge(Validators.FILE_MUST_EXIST, Validators.FILE_MUST_BE_DIRECTORY).validate(prblms, compName, parent.getAbsolutePath());
            }
            return result;
        }
    }

    private void configureCaptions(Component... components) {
        for (Component c : components) {
            if (c instanceof JLabel) {
                Mnemonics.setLocalizedText((JLabel) c, ((JLabel) c).getText());
            } else if (c instanceof AbstractButton) {
                Mnemonics.setLocalizedText((AbstractButton) c, ((AbstractButton) c).getText());
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        createInLabel = new javax.swing.JLabel();
        createInField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        licenseLabel = new javax.swing.JLabel();
        licenseCombo = new javax.swing.JComboBox();
        packageJsonBox = new javax.swing.JCheckBox();

        nameLabel.setLabelFor(nameField);
        nameLabel.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.nameLabel.text")); // NOI18N

        nameField.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.nameField.text")); // NOI18N
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameFieldFocusChange(evt);
            }
        });

        portLabel.setLabelFor(portField);
        portLabel.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.portLabel.text")); // NOI18N
        portLabel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                portLabelFocusGained(evt);
            }
        });

        portField.setText("" + new DefaultExectable().getDefaultPort());

        createInLabel.setLabelFor(createInLabel);
        createInLabel.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.createInLabel.text")); // NOI18N

        createInField.setText(Templates.getTargetFolder(desc) != null ? FileUtil.toFile(Templates.getTargetFolder(desc)).getAbsolutePath() : System.getProperty("user.home") + "/NetBeans Projects");
        createInField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                createInFieldFocusGained(evt);
            }
        });

        browseButton.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBrowse(evt);
            }
        });

        licenseLabel.setLabelFor(licenseCombo);
        licenseLabel.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.licenseLabel.text")); // NOI18N

        licenseCombo.setModel(createLicensesModel());

        packageJsonBox.setSelected(true);
        packageJsonBox.setText(org.openide.util.NbBundle.getMessage(ProjectWizardPanel.class, "ProjectWizardPanel.packageJsonBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(packageJsonBox)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(portLabel)
                            .addComponent(createInLabel)
                            .addComponent(licenseLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(createInField, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton))
                            .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                            .addComponent(licenseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(366, 366, 366)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createInField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createInLabel)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(licenseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(licenseLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(packageJsonBox)
                .addContainerGap(164, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nameFieldFocusChange(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusChange
        nameField.selectAll();
    }//GEN-LAST:event_nameFieldFocusChange

    private void portLabelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_portLabelFocusGained
        portField.selectAll();
    }//GEN-LAST:event_portLabelFocusGained

    private void createInFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_createInFieldFocusGained
        createInField.selectAll();
    }//GEN-LAST:event_createInFieldFocusGained

    private void onBrowse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onBrowse
        File dir = new FileChooserBuilder(ProjectWizardPanel.class).setDirectoriesOnly(true).setTitle(NbBundle.getMessage(ProjectWizardPanel.class, "TTL_BrowseProjectLocation")).showOpenDialog();
        if (dir != null) {
            createInField.setText(dir.getAbsolutePath());
        }
    }//GEN-LAST:event_onBrowse
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField createInField;
    private javax.swing.JLabel createInLabel;
    private javax.swing.JComboBox licenseCombo;
    private javax.swing.JLabel licenseLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JCheckBox packageJsonBox;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent de) {
        change();
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        change();
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        change();
    }

    private File getProposedProjectDir() {
        File f = new File (createInField.getText());
        return new File(f, nameField.getText());
    }

    public String getLicense() {
        if (licenseCombo.getSelectedItem() instanceof License) {
            return ((License) licenseCombo.getSelectedItem()).getLicenseName();
        }
        return null;
    }

    private void change() {
        changeSupport.fireChange();
    }
}
