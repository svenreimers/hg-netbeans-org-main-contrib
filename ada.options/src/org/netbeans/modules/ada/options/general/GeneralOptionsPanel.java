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

package org.netbeans.modules.ada.options.general;

import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * @author  Tomas Mysik
 */
public class GeneralOptionsPanel extends JPanel {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public GeneralOptionsPanel() {
        initComponents();
        errorLabel.setText(" "); // NOI18N

        // listeners
        DocumentListener documentListener = new DefaultDocumentListener();
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        standardsLabel = new javax.swing.JLabel();
        standardsLineSeparator = new javax.swing.JSeparator();
        adaDialectsLabel = new javax.swing.JLabel();
        namingLabel = new javax.swing.JLabel();
        namingLineSeparator = new javax.swing.JSeparator();
        errorLabel = new javax.swing.JLabel();
        adaRestrictionsLabel = new javax.swing.JLabel();
        adaDialectsComboBox = new javax.swing.JComboBox();
        adaRestrictionsComboBox = new javax.swing.JComboBox();
        pkgSpecMaskLabel = new javax.swing.JLabel();
        pkgBodyMaskLabel = new javax.swing.JLabel();
        separatorMaskLabel = new javax.swing.JLabel();
        pkgSpecSepTextField = new javax.swing.JTextField();
        separateSepTextField = new javax.swing.JTextField();
        pkgBodySepTextField = new javax.swing.JTextField();
        extTitleLabel = new javax.swing.JLabel();
        pkgSpecPrefixComboBox = new javax.swing.JComboBox();
        prefixTitleLabel = new javax.swing.JLabel();
        pkgBodyPrefixComboBox = new javax.swing.JComboBox();
        postfixTitleLabel = new javax.swing.JLabel();
        pkgSpecPostfixComboBox = new javax.swing.JComboBox();
        separatePrefixComboBox = new javax.swing.JComboBox();
        pkgBodyPostfixComboBox = new javax.swing.JComboBox();
        separatePostfixComboBox = new javax.swing.JComboBox();
        pkgSpecExtComboBox = new javax.swing.JComboBox();
        pkgBodyExtComboBox = new javax.swing.JComboBox();
        separateExtComboBox = new javax.swing.JComboBox();
        separatorTitleLabel = new javax.swing.JLabel();




        org.openide.awt.Mnemonics.setLocalizedText(standardsLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_Standards")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(adaDialectsLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_AdaDialects")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(namingLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_Naming")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, "ERROR");









        org.openide.awt.Mnemonics.setLocalizedText(adaRestrictionsLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_adaRestrictions")); // NOI18N
        adaDialectsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ada 83", "Ada 95", "Ada 2005" }));

        adaRestrictionsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Spark", "MIL-STD-498" }));

        org.openide.awt.Mnemonics.setLocalizedText(pkgSpecMaskLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_pkgSpecMask")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pkgBodyMaskLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_pkgBodyMask")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(separatorMaskLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_separatorMask")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(extTitleLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_extTitle")); // NOI18N
        pkgSpecPrefixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<package name>", "<free name>" }));

        org.openide.awt.Mnemonics.setLocalizedText(prefixTitleLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_prefixTitle")); // NOI18N
        pkgBodyPrefixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<package name>", "<free name>" }));

        org.openide.awt.Mnemonics.setLocalizedText(postfixTitleLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "LBL_postfixTitle")); // NOI18N
        pkgSpecPostfixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<none>", "<free name>" }));

        separatePrefixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<package name>", "<free name>" }));

        pkgBodyPostfixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<none>", "<free name>" }));

        separatePostfixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<procedure name>", "<free name>", "<none>" }));

        pkgSpecExtComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ".ads", ".ada" }));

        pkgBodyExtComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ".adb", ".ada" }));

        separateExtComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ".adb", ".ada" }));

        org.openide.awt.Mnemonics.setLocalizedText(separatorTitleLabel, org.openide.util.NbBundle.getMessage(GeneralOptionsPanel.class, "GeneralOptionsPanel.separatorTitleLabel.text")); // NOI18N
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(standardsLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(standardsLineSeparator, GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                            .add(errorLabel)
                            .add(layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(layout.createParallelGroup(GroupLayout.LEADING)
                                    .add(adaRestrictionsLabel)
                                    .add(adaDialectsLabel, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(GroupLayout.LEADING)
                                    .add(adaRestrictionsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .add(adaDialectsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(373, 373, 373))))
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(layout.createParallelGroup(GroupLayout.TRAILING, false)
                            .add(GroupLayout.LEADING, pkgBodyMaskLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(GroupLayout.LEADING, pkgSpecMaskLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(GroupLayout.LEADING, separatorMaskLabel, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(layout.createParallelGroup(GroupLayout.TRAILING)
                                .add(layout.createParallelGroup(GroupLayout.LEADING)
                                    .add(pkgSpecPrefixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .add(pkgBodyPrefixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(prefixTitleLabel))
                            .add(separatePrefixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.TRAILING)
                            .add(separatorTitleLabel)
                            .add(pkgSpecSepTextField, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .add(pkgBodySepTextField, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .add(separateSepTextField, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(pkgBodyPostfixComboBox, 0, 110, Short.MAX_VALUE)
                            .add(separatePostfixComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(pkgSpecPostfixComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(GroupLayout.TRAILING, postfixTitleLabel))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(separateExtComboBox, 0, 0, Short.MAX_VALUE)
                            .add(pkgBodyExtComboBox, 0, 0, Short.MAX_VALUE)
                            .add(pkgSpecExtComboBox, 0, 0, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(41, 41, 41)
                                .add(extTitleLabel))))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(namingLabel)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(namingLineSeparator, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {adaDialectsLabel, adaRestrictionsLabel}, GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {adaDialectsComboBox, adaRestrictionsComboBox}, GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {pkgBodyMaskLabel, pkgSpecMaskLabel, separatorMaskLabel}, GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {pkgBodySepTextField, pkgSpecSepTextField, separateSepTextField}, GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {pkgBodyPostfixComboBox, pkgBodyPrefixComboBox, pkgSpecPostfixComboBox, pkgSpecPrefixComboBox, separatePostfixComboBox, separatePrefixComboBox}, GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.CENTER)
                    .add(standardsLineSeparator, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
                    .add(standardsLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.CENTER)
                    .add(adaDialectsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(adaDialectsLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.CENTER)
                    .add(adaRestrictionsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(adaRestrictionsLabel))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(GroupLayout.CENTER)
                    .add(namingLineSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .add(namingLabel))
                .add(1, 1, 1)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                        .add(prefixTitleLabel)
                        .add(separatorTitleLabel))
                    .add(extTitleLabel)
                    .add(postfixTitleLabel))
                .add(4, 4, 4)
                .add(layout.createParallelGroup(GroupLayout.CENTER)
                    .add(pkgSpecMaskLabel)
                    .add(pkgSpecPrefixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(pkgSpecSepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(pkgSpecPostfixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(pkgSpecExtComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createParallelGroup(GroupLayout.CENTER)
                        .add(pkgBodyPrefixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(pkgBodyMaskLabel))
                    .add(pkgBodyExtComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                        .add(pkgBodyPostfixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(pkgBodySepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.CENTER)
                    .add(separatorMaskLabel)
                    .add(separatePrefixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(separateSepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(separatePostfixComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(separateExtComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.RELATED, 150, Short.MAX_VALUE)
                .add(errorLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox adaDialectsComboBox;
    private javax.swing.JLabel adaDialectsLabel;
    private javax.swing.JComboBox adaRestrictionsComboBox;
    private javax.swing.JLabel adaRestrictionsLabel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel extTitleLabel;
    private javax.swing.JLabel namingLabel;
    private javax.swing.JSeparator namingLineSeparator;
    private javax.swing.JComboBox pkgBodyExtComboBox;
    private javax.swing.JLabel pkgBodyMaskLabel;
    private javax.swing.JComboBox pkgBodyPostfixComboBox;
    private javax.swing.JComboBox pkgBodyPrefixComboBox;
    private javax.swing.JTextField pkgBodySepTextField;
    private javax.swing.JComboBox pkgSpecExtComboBox;
    private javax.swing.JLabel pkgSpecMaskLabel;
    private javax.swing.JComboBox pkgSpecPostfixComboBox;
    private javax.swing.JComboBox pkgSpecPrefixComboBox;
    private javax.swing.JTextField pkgSpecSepTextField;
    private javax.swing.JLabel postfixTitleLabel;
    private javax.swing.JLabel prefixTitleLabel;
    private javax.swing.JComboBox separateExtComboBox;
    private javax.swing.JComboBox separatePostfixComboBox;
    private javax.swing.JComboBox separatePrefixComboBox;
    private javax.swing.JTextField separateSepTextField;
    private javax.swing.JLabel separatorMaskLabel;
    private javax.swing.JLabel separatorTitleLabel;
    private javax.swing.JLabel standardsLabel;
    private javax.swing.JSeparator standardsLineSeparator;
    // End of variables declaration//GEN-END:variables

    private final class DefaultDocumentListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }
}