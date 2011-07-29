/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

/*
 * PropertiesPanel.java
 *
 * Created on May 29, 2011, 6:35:47 PM
 */
package org.netbeans.modules.nodejs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.validation.adapters.DialogDescriptorAdapter;
import org.netbeans.modules.nodejs.ui.UiUtil;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author tim
 */
public class PropertiesPanel extends javax.swing.JPanel {

    private final ValidationGroup g;
    private final NodeJSProjectProperties props;

    /** Creates new form PropertiesPanel */
    public PropertiesPanel(NodeJSProjectProperties props) {
        this.props = props;
        g = ValidationGroup.create();
        initComponents();
        UiUtil.prepareComponents(this);
        set(authorEmailField, props.getAuthorEmail());
        set(authorNameField, props.getAuthorName());
        set(nameField, props.getDisplayName());
        set(descriptionField, props.getDescription());
        set(bugTrackerField, props.getBugTrackerURL());
        set(commandLineField, props.getRunArguments());
        if ("null".equals(bugTrackerField.getText())) {
            bugTrackerField.setText("");
        }
        String type = props.getLicenseType();
        if (type != null) {
            licenseField.setSelectedItem(type);
        }
        FileObject mainFile = props.getMainFile();
        if (mainFile != null) {
            String path = FileUtil.getRelativePath(props.project().getProjectDirectory(), mainFile);
            set(mainFileField, path);
        }
        List<String> l = props.getKeywords();
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = l.iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        set(keywordsField, sb.toString());
        g.add(bugTrackerField, new AllowNullValidator(Validators.URL_MUST_BE_VALID));
        g.add(nameField, Validators.REQUIRE_NON_EMPTY_STRING);
        g.add(authorEmailField, new AllowNullValidator(Validators.EMAIL_ADDRESS));
        g.add(mainFileField, new FileRelativeValidator());
        g.add(commandLineField, new WhitespaceValidator());
    }
    
    public void addNotify() {
        super.addNotify();
        g.validateAll();
    }

    private static final class AllowNullValidator implements Validator<String> {
        //Some validators do not allow nulls - they should and a newer version
        //of the lib fixes this

        private final Validator<String> other;

        public AllowNullValidator(Validator<String> other) {
            this.other = other;
        }

        @Override
        public boolean validate(Problems prblms, String string, String t) {
            if (t == null || "".equals(t.trim())) {
                return true;
            }
            return other.validate(prblms, string, t);
        }
    }

    private final class FileRelativeValidator implements Validator<String> {

        @Override
        public boolean validate(Problems prblms, String string, String model) {
            if (model == null || "".equals(model.trim())) {
                return true;
            }
            FileObject root = props.project().getProjectDirectory();
            FileObject fo = root.getFileObject(model);
            boolean result = fo != null;
            if (!result) {
                prblms.add(NbBundle.getMessage(PropertiesPanel.class, "MAIN_FILE_DOES_NOT_EXIST", model));
            }
            return result;
        }
    }
    
    private static final class WhitespaceValidator implements Validator<String> {
        private static final Pattern WHITESPACE = Pattern.compile (".*\\s.*");

        @Override
        public boolean validate(Problems prblms, String string, String model) {
            if (model != null && WHITESPACE.matcher(model).matches()) {
                prblms.add(NbBundle.getMessage(WhitespaceValidator.class, "INFO_MAIN_CLASS_WHITESPACE"), Severity.INFO);
                return false;
            }
            return true;
        }
        
    }

    private void set(JTextComponent c, String s) {
        if (s != null) {
            c.setText(s);
        }
    }

    void save() {
        props.setAuthor(authorNameField.getText().trim());
        props.setAuthorEmail(authorEmailField.getText().trim());
        props.setDisplayName(nameField.getText().trim());
        props.setDescription(descriptionField.getText().trim());
        props.setKeywords(keywordsField.getText().trim());
        props.setRunArguments(commandLineField.getText().trim());
        if (bugTrackerField.getText().trim().length() > 0) {
            try {
                props.setBugTrackerURL(new URL(bugTrackerField.getText().trim()));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            props.setBugTrackerURL(null);
        }
        props.setMainFile(props.project().getProjectDirectory().getFileObject(mainFileField.getText().trim()));
        if (!"none".equals(licenseField.getSelectedItem())) {
            props.setLicenseType(licenseField.getSelectedItem().toString());
        }
    }
    
    boolean notEmpty (JTextComponent c) {
        return c.getText().trim().length() > 0;
    }
    
    public void showDialog() {
        DialogDescriptor d = new DialogDescriptor(this, props.project().getLookup().lookup(ProjectInformation.class).getDisplayName());
        DialogDescriptorAdapter adap = new DialogDescriptorAdapter(d);
        g.addUI(adap);
        if (DialogDisplayer.getDefault().notify(d).equals(DialogDescriptor.OK_OPTION)) {
            save();
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

        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionField = new javax.swing.JTextArea();
        authorNameLabel = new javax.swing.JLabel();
        authorNameField = new javax.swing.JTextField();
        authorEmailLabel = new javax.swing.JLabel();
        authorEmailField = new javax.swing.JTextField();
        bugTrackerLabel = new javax.swing.JLabel();
        bugTrackerField = new javax.swing.JTextField();
        licenseLabel = new javax.swing.JLabel();
        licenseField = new javax.swing.JComboBox();
        mainFileLabel = new javax.swing.JLabel();
        mainFileField = new javax.swing.JTextField();
        status = g.createProblemLabel();
        keywordsLabel = new javax.swing.JLabel();
        keywordsField = new javax.swing.JTextField();
        commandLineField = new javax.swing.JTextField();
        commandLineLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        nameLabel.setLabelFor(nameField);
        nameLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.nameLabel.text")); // NOI18N

        nameField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.name.text")); // NOI18N
        nameField.setName("name"); // NOI18N

        descriptionLabel.setLabelFor(descriptionField);
        descriptionLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.descriptionLabel.text")); // NOI18N

        descriptionField.setColumns(20);
        descriptionField.setLineWrap(true);
        descriptionField.setRows(5);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setName("description"); // NOI18N
        jScrollPane1.setViewportView(descriptionField);

        authorNameLabel.setLabelFor(authorNameField);
        authorNameLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.authorNameLabel.text")); // NOI18N

        authorNameField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.author.name.text")); // NOI18N
        authorNameField.setName("author.name"); // NOI18N

        authorEmailLabel.setLabelFor(authorEmailField);
        authorEmailLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.authorEmailLabel.text")); // NOI18N

        authorEmailField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.author.email.text")); // NOI18N
        authorEmailField.setName("author.email"); // NOI18N

        bugTrackerLabel.setLabelFor(bugTrackerField);
        bugTrackerLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.bugTrackerLabel.text")); // NOI18N

        bugTrackerField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.bugs.web.text")); // NOI18N
        bugTrackerField.setName("bugs.web"); // NOI18N

        licenseLabel.setLabelFor(licenseField);
        licenseLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.licenseLabel.text")); // NOI18N

        licenseField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "bsd", "mit", "gplv2", "gplv3", "cddl", "apache20" }));
        licenseField.setName("license.type"); // NOI18N

        mainFileLabel.setLabelFor(mainFileField);
        mainFileLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.mainFileLabel.text")); // NOI18N

        mainFileField.setEditable(false);
        mainFileField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.main.text")); // NOI18N
        mainFileField.setName("main"); // NOI18N

        status.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.status.text")); // NOI18N

        keywordsLabel.setLabelFor(keywordsField);
        keywordsLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.keywordsLabel.text")); // NOI18N

        keywordsField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.keywords.text")); // NOI18N
        keywordsField.setName("keywords"); // NOI18N

        commandLineField.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.commandLineField.text")); // NOI18N

        commandLineLabel.setLabelFor(commandLineField);
        commandLineLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.commandLineLabel.text")); // NOI18N
        commandLineLabel.setToolTipText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.commandLineLabel.toolTipText")); // NOI18N

        jButton1.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMainFile(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(descriptionLabel)
                            .addComponent(authorNameLabel)
                            .addComponent(authorEmailLabel)
                            .addComponent(bugTrackerLabel)
                            .addComponent(licenseLabel)
                            .addComponent(mainFileLabel)
                            .addComponent(keywordsLabel)
                            .addComponent(commandLineLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(authorNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(authorEmailField, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(bugTrackerField, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(licenseField, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keywordsField, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(commandLineField, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(mainFileField, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addGap(6, 6, 6)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorNameLabel)
                    .addComponent(authorNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorEmailLabel)
                    .addComponent(authorEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bugTrackerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bugTrackerLabel))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(licenseLabel)
                    .addComponent(licenseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainFileLabel)
                    .addComponent(mainFileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keywordsLabel)
                    .addComponent(keywordsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(commandLineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commandLineLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(status)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void browseMainFile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMainFile
    FileObject fo = props.project().getLookup().lookup(NodeJSProject.class).showSelectMainFileDialog();
    if (fo != null) {
        String path = FileUtil.getRelativePath(props.project().getProjectDirectory(), fo);
        mainFileField.setText(path);
    }
}//GEN-LAST:event_browseMainFile

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField authorEmailField;
    private javax.swing.JLabel authorEmailLabel;
    private javax.swing.JTextField authorNameField;
    private javax.swing.JLabel authorNameLabel;
    private javax.swing.JTextField bugTrackerField;
    private javax.swing.JLabel bugTrackerLabel;
    private javax.swing.JTextField commandLineField;
    private javax.swing.JLabel commandLineLabel;
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField keywordsField;
    private javax.swing.JLabel keywordsLabel;
    private javax.swing.JComboBox licenseField;
    private javax.swing.JLabel licenseLabel;
    private javax.swing.JTextField mainFileField;
    private javax.swing.JLabel mainFileLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel status;
    // End of variables declaration//GEN-END:variables
}
