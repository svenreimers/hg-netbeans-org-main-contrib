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
package org.netbeans.modules.ada.project.options.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.ada.project.ui.properties.AdaProjectProperties;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author  Andrea Lucarelli
 */
public class AdaGeneralOptionsPanel extends JPanel implements ChangeListener, HelpCtx.Provider {

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final AdaProjectProperties uiProperties;
    private static final String UI_NONE = "<none>"; // NOI18N
    private static final String OPT_NONE = ""; // NOI18N
    private static final String UI_PACKAGE_NAME = "<package name>"; // NOI18N
    private static final String UI_PROCEDURE_NAME = "<procedure name>"; // NOI18N

    public AdaGeneralOptionsPanel(final AdaProjectProperties uiProperties) {
        this.uiProperties = uiProperties;
        initComponents();
        errorLabel.setText(" "); // NOI18N

        if (uiProperties != null) {
            setAdaDialects(uiProperties.getAdaDialects());
            setAdaRestrictions(uiProperties.getAdaRestrictions());
            setPkgSpecPrefix(uiProperties.getPkgSpecPrefix());
            setPkgBodyPrefix(uiProperties.getPkgBodyPrefix());
            setSeparatePrefix(uiProperties.getSeparatePrefix());
            setPkgSpecPostfix(uiProperties.getPkgSpecPostfix());
            setPkgBodyPostfix(uiProperties.getPkgBodyPostfix());
            setSeparatePostfix(uiProperties.getSeparatePostfix());
            setPkgSpecExt(uiProperties.getPkgSpecExt());
            setPkgBodyExt(uiProperties.getPkgBodyExt());
            setSeparateExt(uiProperties.getSeparateExt());

            this.addChangeListener(this);
        }
    }

    public String getAdaDialects() {
        if (adaDialectsComboBox.getSelectedItem() != null) {
            return adaDialectsComboBox.getSelectedItem().toString();
        }
        return OPT_NONE;
    }

    public void setAdaDialects(String adaDialects) {
        adaDialectsComboBox.setSelectedItem(adaDialects);
    }

    public String getAdaRestrictions() {
        if (adaRestrictionsComboBox.getSelectedItem() != null) {
            return adaRestrictionsComboBox.getSelectedItem().toString();
        }
        return OPT_NONE;
    }

    public void setAdaRestrictions(String adaRestrictions) {
        adaRestrictionsComboBox.setSelectedItem(adaRestrictions);
    }

    public String getPkgSpecPrefix() {
        return pkgSpecPrefixComboBox.getSelectedItem().toString();
    }

    public void setPkgSpecPrefix(String pkgSpecPrefix) {
        pkgSpecPrefixComboBox.setSelectedItem(pkgSpecPrefix);
    }

    public String getPkgBodyPrefix() {
        return pkgBodyPrefixComboBox.getSelectedItem().toString();
    }

    public void setPkgBodyPrefix(String pkgBodyPrefix) {
        pkgBodyPrefixComboBox.setSelectedItem(pkgBodyPrefix);
    }

    public String getSeparatePrefix() {
        return separatePrefixComboBox.getSelectedItem().toString();
    }

    public void setSeparatePrefix(String separatePrefix) {
        separatePrefixComboBox.setSelectedItem(separatePrefix);
    }

    public String getPkgSpecPostfix() {
        String pkgSpecPostfix = pkgSpecPostfixComboBox.getSelectedItem().toString();
        if (pkgSpecPostfix.equalsIgnoreCase(UI_NONE)) {
            pkgSpecPostfix = OPT_NONE;
        }
        return pkgSpecPostfix;
    }

    public void setPkgSpecPostfix(String pkgSpecPostfix) {
        if (pkgSpecPostfix.equalsIgnoreCase(OPT_NONE)) {
            pkgSpecPostfixComboBox.setSelectedItem(UI_NONE);
        } else {
            pkgSpecPostfixComboBox.setEditable(true);
            pkgSpecPostfixComboBox.addItem(pkgSpecPostfix);
            pkgSpecPostfixComboBox.setSelectedItem(pkgSpecPostfix);
        }
    }

    public String getPkgBodyPostfix() {
        String pkgBodyPostfix = pkgBodyPostfixComboBox.getSelectedItem().toString();
        if (pkgBodyPostfix.equalsIgnoreCase(UI_NONE)) {
            pkgBodyPostfix = OPT_NONE;
        }
        return pkgBodyPostfix;
    }

    public void setPkgBodyPostfix(String pkgBodyPostfix) {
        if (pkgBodyPostfix.equalsIgnoreCase(OPT_NONE)) {
            pkgBodyPostfixComboBox.setSelectedItem(UI_NONE);
        } else {
            pkgBodyPostfixComboBox.setEditable(true);
            pkgBodyPostfixComboBox.addItem(pkgBodyPostfix);
            pkgBodyPostfixComboBox.setSelectedItem(pkgBodyPostfix);
        }
    }

    public String getSeparatePostfix() {
        return separatePostfixComboBox.getSelectedItem().toString();
    }

    public void setSeparatePostfix(String separatePostfix) {
        separatePostfixComboBox.setSelectedItem(separatePostfix);
    }

    public String getPkgSpecExt() {
        return pkgSpecExtComboBox.getSelectedItem().toString();
    }

    public void setPkgSpecExt(String pkgSpecExt) {
        pkgSpecExtComboBox.setSelectedItem(pkgSpecExt);
    }

    public String getPkgBodyExt() {
        return pkgBodyExtComboBox.getSelectedItem().toString();
    }

    public void setPkgBodyExt(String pkgBodyExt) {
        pkgBodyExtComboBox.setSelectedItem(pkgBodyExt);
    }

    public String getSeparateExt() {
        return separateExtComboBox.getSelectedItem().toString();
    }

    public void setSeparateExt(String separateExt) {
        separateExtComboBox.setSelectedItem(separateExt);
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









        standardsLabel = new JLabel();
        standardsLineSeparator = new JSeparator();
        adaDialectsLabel = new JLabel();
        namingLabel = new JLabel();
        namingLineSeparator = new JSeparator();
        errorLabel = new JLabel();
        adaRestrictionsLabel = new JLabel();
        adaDialectsComboBox = new JComboBox();
        adaRestrictionsComboBox = new JComboBox();
        pkgSpecMaskLabel = new JLabel();
        pkgBodyMaskLabel = new JLabel();
        separatorMaskLabel = new JLabel();
        pkgSpecSepTextField = new JTextField();
        separateSepTextField = new JTextField();
        pkgBodySepTextField = new JTextField();
        extTitleLabel = new JLabel();
        pkgSpecPrefixComboBox = new JComboBox();
        prefixTitleLabel = new JLabel();
        pkgBodyPrefixComboBox = new JComboBox();
        postfixTitleLabel = new JLabel();
        pkgSpecPostfixComboBox = new JComboBox();
        separatePrefixComboBox = new JComboBox();
        pkgBodyPostfixComboBox = new JComboBox();
        separatePostfixComboBox = new JComboBox();
        pkgSpecExtComboBox = new JComboBox();
        pkgBodyExtComboBox = new JComboBox();
        separateExtComboBox = new JComboBox();
        separatorTitleLabel = new JLabel();
        Mnemonics.setLocalizedText(standardsLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_Standards"));
        Mnemonics.setLocalizedText(adaDialectsLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_AdaDialects"));
        Mnemonics.setLocalizedText(namingLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_Naming"));
        Mnemonics.setLocalizedText(errorLabel, "ERROR");
        Mnemonics.setLocalizedText(adaRestrictionsLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_adaRestrictions"));
        adaDialectsComboBox.setModel(new DefaultComboBoxModel(new String[] { "Ada 83", "Ada 95", "Ada 2005" }));
        adaDialectsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                adaDialectsComboBoxActionPerformed(evt);
            }
        });

        adaRestrictionsComboBox.setModel(new DefaultComboBoxModel(new String[] { "None", "Spark", "MIL-STD-498" }));
        adaRestrictionsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                adaRestrictionsComboBoxActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(pkgSpecMaskLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_pkgSpecMask"));
        Mnemonics.setLocalizedText(pkgBodyMaskLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_pkgBodyMask"));
        Mnemonics.setLocalizedText(separatorMaskLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_separatorMask"));
        pkgSpecSepTextField.setHorizontalAlignment(JTextField.CENTER);

        separateSepTextField.setHorizontalAlignment(JTextField.CENTER);

        pkgBodySepTextField.setHorizontalAlignment(JTextField.CENTER);
        Mnemonics.setLocalizedText(extTitleLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_extTitle"));
        pkgSpecPrefixComboBox.setModel(new DefaultComboBoxModel(new String[] { "<package name>", "<free name>" }));
        pkgSpecPrefixComboBox.setEnabled(false);

        Mnemonics.setLocalizedText(prefixTitleLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_prefixTitle"));
        pkgBodyPrefixComboBox.setModel(new DefaultComboBoxModel(new String[] { "<package name>", "<free name>" }));
        pkgBodyPrefixComboBox.setEnabled(false);


        Mnemonics.setLocalizedText(postfixTitleLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "LBL_postfixTitle"));
        pkgSpecPostfixComboBox.setModel(new DefaultComboBoxModel(new String[] { "<none>", "<free name>" }));
        pkgSpecPostfixComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pkgSpecPostfixComboBoxActionPerformed(evt);
            }
        });

        separatePrefixComboBox.setModel(new DefaultComboBoxModel(new String[] { "<package name>", "<free name>" }));
        separatePrefixComboBox.setEnabled(false);

        pkgBodyPostfixComboBox.setModel(new DefaultComboBoxModel(new String[] { "<none>", "<free name>" }));
        pkgBodyPostfixComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pkgBodyPostfixComboBoxActionPerformed(evt);
            }
        });

        separatePostfixComboBox.setModel(new DefaultComboBoxModel(new String[] { "<procedure name>", "<free name>", "<none>" }));
        separatePostfixComboBox.setEnabled(false);

        separatePostfixComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                separatePostfixComboBoxActionPerformed(evt);
            }
        });

        pkgSpecExtComboBox.setModel(new DefaultComboBoxModel(new String[] { "ads", "ada" }));
        pkgSpecExtComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pkgSpecExtComboBoxActionPerformed(evt);
            }
        });

        pkgBodyExtComboBox.setModel(new DefaultComboBoxModel(new String[] { "adb", "ada" }));
        pkgBodyExtComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pkgBodyExtComboBoxActionPerformed(evt);
            }
        });

        separateExtComboBox.setModel(new DefaultComboBoxModel(new String[] { "adb", "ada" }));
        separateExtComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                separateExtComboBoxActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(separatorTitleLabel, NbBundle.getMessage(AdaGeneralOptionsPanel.class, "AdaGeneralOptionsPanel.separatorTitleLabel.text"));
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
                        .add(namingLineSeparator, GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(new Component[] {adaDialectsLabel, adaRestrictionsLabel}, GroupLayout.HORIZONTAL);

        layout.linkSize(new Component[] {adaDialectsComboBox, adaRestrictionsComboBox}, GroupLayout.HORIZONTAL);

        layout.linkSize(new Component[] {pkgBodyMaskLabel, pkgSpecMaskLabel, separatorMaskLabel}, GroupLayout.HORIZONTAL);

        layout.linkSize(new Component[] {pkgBodySepTextField, pkgSpecSepTextField, separateSepTextField}, GroupLayout.HORIZONTAL);

        layout.linkSize(new Component[] {pkgBodyPostfixComboBox, pkgBodyPrefixComboBox, pkgSpecPostfixComboBox, pkgSpecPrefixComboBox, separatePostfixComboBox, separatePrefixComboBox}, GroupLayout.HORIZONTAL);

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

    private void adaDialectsComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_adaDialectsComboBoxActionPerformed
        if (this.uiProperties != null) {
            this.uiProperties.setAdaDialects(getAdaDialects());
        }
    }//GEN-LAST:event_adaDialectsComboBoxActionPerformed

    private void adaRestrictionsComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_adaRestrictionsComboBoxActionPerformed
        if (this.uiProperties != null) {
            this.uiProperties.setAdaRestrictions(getAdaRestrictions());
        }
    }//GEN-LAST:event_adaRestrictionsComboBoxActionPerformed

    private void pkgSpecPostfixComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pkgSpecPostfixComboBoxActionPerformed
        fireChange();
        if (pkgSpecPostfixComboBox.getSelectedIndex() != 0) {
            pkgSpecPostfixComboBox.setEditable(true);
            pkgSpecPostfixComboBox.getEditor().selectAll();
        } else {
            pkgSpecPostfixComboBox.setEditable(false);
        }
        if (this.uiProperties != null) {
            this.uiProperties.setPkgSpecPostfix(getPkgSpecPostfix());
        }
    }//GEN-LAST:event_pkgSpecPostfixComboBoxActionPerformed

    private void pkgBodyPostfixComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pkgBodyPostfixComboBoxActionPerformed
        fireChange();
        if (pkgBodyPostfixComboBox.getSelectedIndex() != 0) {
            pkgBodyPostfixComboBox.setEditable(true);
            pkgBodyPostfixComboBox.getEditor().selectAll();
        } else {
            pkgBodyPostfixComboBox.setEditable(false);
        }
        if (this.uiProperties != null) {
            this.uiProperties.setPkgBodyPostfix(getPkgBodyPostfix());
        }
    }//GEN-LAST:event_pkgBodyPostfixComboBoxActionPerformed

    private void separatePostfixComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_separatePostfixComboBoxActionPerformed
        if (this.uiProperties != null) {
            this.uiProperties.setSeparatePostfix(getSeparatePostfix());
        }
    }//GEN-LAST:event_separatePostfixComboBoxActionPerformed

    private void pkgSpecExtComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pkgSpecExtComboBoxActionPerformed
        if (this.uiProperties != null) {
            this.uiProperties.setPkgSpecExt(getPkgSpecExt());
        }
        fireChange();
    }//GEN-LAST:event_pkgSpecExtComboBoxActionPerformed

    private void pkgBodyExtComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pkgBodyExtComboBoxActionPerformed
        if (this.uiProperties != null) {
            this.uiProperties.setPkgBodyExt(getPkgBodyExt());
        }
        fireChange();
    }//GEN-LAST:event_pkgBodyExtComboBoxActionPerformed

    private void separateExtComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_separateExtComboBoxActionPerformed
        if (this.uiProperties != null) {
            this.uiProperties.setSeparateExt(getSeparateExt());
        }
    }//GEN-LAST:event_separateExtComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox adaDialectsComboBox;
    private JLabel adaDialectsLabel;
    private JComboBox adaRestrictionsComboBox;
    private JLabel adaRestrictionsLabel;
    private JLabel errorLabel;
    private JLabel extTitleLabel;
    private JLabel namingLabel;
    private JSeparator namingLineSeparator;
    private JComboBox pkgBodyExtComboBox;
    private JLabel pkgBodyMaskLabel;
    private JComboBox pkgBodyPostfixComboBox;
    private JComboBox pkgBodyPrefixComboBox;
    private JTextField pkgBodySepTextField;
    private JComboBox pkgSpecExtComboBox;
    private JLabel pkgSpecMaskLabel;
    private JComboBox pkgSpecPostfixComboBox;
    private JComboBox pkgSpecPrefixComboBox;
    private JTextField pkgSpecSepTextField;
    private JLabel postfixTitleLabel;
    private JLabel prefixTitleLabel;
    private JComboBox separateExtComboBox;
    private JComboBox separatePostfixComboBox;
    private JComboBox separatePrefixComboBox;
    private JTextField separateSepTextField;
    private JLabel separatorMaskLabel;
    private JLabel separatorTitleLabel;
    private JLabel standardsLabel;
    private JSeparator standardsLineSeparator;
    // End of variables declaration//GEN-END:variables

    public void stateChanged(ChangeEvent e) {
        // errors
        String postfixSpec = this.getPkgSpecPostfix();
        String postfixBody = this.getPkgBodyPostfix();
        String specExt = this.getPkgSpecExt();
        String bodyExt = this.getPkgBodyExt();

        // everything ok
        this.setError(" "); // NOI18N

        if (specExt.equalsIgnoreCase(bodyExt)) {
            if (postfixSpec.equalsIgnoreCase(postfixBody)) {
                this.setError(NbBundle.getMessage(AdaGeneralOptionsPanel.class, "MSG_ExtetionsError"));
            }
        }
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(this.getClass());
    }
}
