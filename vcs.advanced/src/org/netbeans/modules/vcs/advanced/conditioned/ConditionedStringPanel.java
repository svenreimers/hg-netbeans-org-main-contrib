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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.NbBundle;

/**
 * Panel that is used for editing of conditioned String value.
 *
 * @author  Martin Entlicher
 */
public class ConditionedStringPanel extends javax.swing.JPanel implements EnhancedCustomPropertyEditor {
    
    private ConditionedString cs;
    
    /** Creates new form ConditionedStringPanel */
    public ConditionedStringPanel(ConditionedString cs) {
        this.cs = cs;
        initComponents();
        postInitComponents();
        fillConditions();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        infoLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        valueTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        conditionLabel = new javax.swing.JLabel();
        conditionComboBox = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel"));
        infoLabel.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.infoLabel_mnc").charAt(0));
        infoLabel.setLabelFor(valueTextArea);
        infoLabel.setText("Value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 11);
        add(infoLabel, gridBagConstraints);
        infoLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.infoLabel"));

        valueTextArea.addFocusListener(formListener);

        jScrollPane1.setViewportView(valueTextArea);
        valueTextArea.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.valueTextArea"));
        valueTextArea.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.valueTextArea"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 6, 11);
        add(jScrollPane1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        conditionLabel.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.conditionedLabel_mnc").charAt(0));
        conditionLabel.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.Condition"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(conditionLabel, gridBagConstraints);
        conditionLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.conditionedLabel"));

        conditionComboBox.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(conditionComboBox, gridBagConstraints);
        conditionComboBox.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.conditionComboBox"));
        conditionComboBox.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.conditionComboBox"));

        addButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.addButtonS_mnc").charAt(0));
        addButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.addButton"));
        addButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.addButton"));

        editButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.editButton_mnc").charAt(0));
        editButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.editButton"));
        editButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(editButton, gridBagConstraints);
        editButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.editButton"));

        removeButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACS_ConditionedStringPanel.removeButtonSt_mnc").charAt(0));
        removeButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.removeButton"));
        removeButton.addActionListener(formListener);

        jPanel1.add(removeButton, new java.awt.GridBagConstraints());
        removeButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/conditioned/Bundle").getString("ACSD_ConditionedStringPanel.removeButton"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(jPanel1, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.FocusListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == conditionComboBox) {
                ConditionedStringPanel.this.conditionComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == addButton) {
                ConditionedStringPanel.this.addButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editButton) {
                ConditionedStringPanel.this.editButtonActionPerformed(evt);
            }
            else if (evt.getSource() == removeButton) {
                ConditionedStringPanel.this.removeButtonActionPerformed(evt);
            }
        }

        public void focusGained(java.awt.event.FocusEvent evt) {
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == valueTextArea) {
                ConditionedStringPanel.this.valueTextAreaFocusLost(evt);
            }
        }
    }//GEN-END:initComponents

    private void valueTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueTextAreaFocusLost
        // Add your handling code here:
        String value = valueTextArea.getText();
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        cs.setValue(iuc, value);
    }//GEN-LAST:event_valueTextAreaFocusLost

    private void conditionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conditionComboBoxActionPerformed
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        valueTextArea.setText(cs.getValue(iuc));
    }//GEN-LAST:event_conditionComboBoxActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        cs.removeValue(iuc);
        conditionComboBox.removeItem(iuc);
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }//GEN-LAST:event_removeButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        IfUnlessConditionPanel panel = new IfUnlessConditionPanel(iuc, new String[0]);
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(IfUnlessConditionPanel.class, "IfUnlessConditionPanel.title"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            iuc = panel.getCondition();
            cs.setValue(iuc, cs.getValue((IfUnlessCondition) conditionComboBox.getSelectedItem()));  // Rather leave the last text
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        IfUnlessCondition iuc = new IfUnlessCondition(null);
        IfUnlessConditionPanel panel = new IfUnlessConditionPanel(iuc, new String[0]);
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(IfUnlessConditionPanel.class, "IfUnlessConditionPanel.title"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            iuc = panel.getCondition();
            conditionComboBox.addItem(iuc);
            cs.setValue(iuc, cs.getValue((IfUnlessCondition) conditionComboBox.getSelectedItem()));  // Rather leave the last text
            conditionComboBox.setSelectedItem(iuc);
        }
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }//GEN-LAST:event_addButtonActionPerformed

    private void postInitComponents() {
        //valueTextPane.setFont(valueTextPane.getFont().deriveFont(java.awt.Font.
        valueTextArea.setRows(10);
        valueTextArea.setColumns(60);
        java.awt.Dimension cd = conditionComboBox.getPreferredSize();
        cd.width = conditionComboBox.getFont().getSize()*15;
        conditionComboBox.setPreferredSize(cd);
    }
    
    private void fillConditions() {
        infoLabel.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.title", cs.getName()));
        IfUnlessCondition[] iucs = cs.getIfUnlessConditions();
        Arrays.sort(iucs, new IfUnlessCondition.IfUnlessComparator());
        for (int i = 0; i < iucs.length; i++) {
            conditionComboBox.addItem(iucs[i]);
        }
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }
    
    public Object getPropertyValue() throws IllegalStateException {
        return cs;
    }    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox conditionComboBox;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextArea valueTextArea;
    // End of variables declaration//GEN-END:variables

}
