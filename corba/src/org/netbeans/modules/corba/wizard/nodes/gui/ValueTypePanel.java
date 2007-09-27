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

package org.netbeans.modules.corba.wizard.nodes.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.netbeans.modules.corba.wizard.nodes.utils.IdlUtilities;
/**
 *
 * @author  tzezula
 */
public class ValueTypePanel extends ExPanel implements javax.swing.event.DocumentListener, ActionListener {

    /** Creates new form ValueTypePanel */
    public ValueTypePanel() {
        initComponents();
        this.custom.addActionListener(this);
        this.abst.addActionListener(this);
        this.truncatable.addActionListener(this);
        this.base.getDocument().addDocumentListener(this);
        this.name.getDocument().addDocumentListener(this);
        this.supports.getDocument().addDocumentListener(this);
        this.truncatable.setEnabled(false);
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString ("TXT_ModuleName_MNE").charAt(0));
        this.jLabel2.setDisplayedMnemonic (this.bundle.getString ("TXT_ValuetypeBase_MNE").charAt(0));
        this.jLabel3.setDisplayedMnemonic (this.bundle.getString ("TXT_ValueTypeSupports_MNE").charAt(0));
        this.custom.setMnemonic (this.bundle.getString ("TXT_ValuetypeCustom_MNE").charAt(0));
        this.abst.setMnemonic (this.bundle.getString ("TXT_ValuetypeAbstract_MNE").charAt(0));
        this.truncatable.setMnemonic (this.bundle.getString ("TXT_ValueTypeTrucatable_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_ValueTypePanel"));
    }
    
    public boolean isAbstract() {
        return (this.abst.isEnabled() && this.abst.isSelected());
    }
    
    public boolean isCustom() {
        return (this.custom.isEnabled() && this.custom.isSelected());
    }
    
    public boolean isTruncatable() {
        return (this.truncatable.isEnabled() && this.truncatable.isSelected());
    }
    
    public void setAbstract(boolean b) {
        this.abst.setSelected(b);
        if (b) {
            this.abst.setEnabled(true);
            this.truncatable.setSelected(false);
            this.truncatable.setEnabled(false);
            this.custom.setSelected(false);
            this.custom.setEnabled(false);
        }
        else if (!this.truncatable.isSelected() && !this.custom.isSelected()) {
            this.abst.setEnabled(true);
            if (this.base.getText().length()>0)
                this.truncatable.setEnabled(true);
            this.custom.setEnabled(true);
        }
    }
    
    public void setTruncatable(boolean b) {
        if (this.base.getText().length()>0) {
            this.truncatable.setSelected(b);
            if (b) {
                this.truncatable.setEnabled(true);
                this.abst.setSelected(false);
                this.abst.setEnabled(false);
                this.custom.setSelected(false);
                this.custom.setEnabled(false);
            }
            else if (!this.abst.isSelected() && !this.custom.isSelected()) {
                this.abst.setEnabled(true);
                this.truncatable.setEnabled(true);
                this.custom.setEnabled(true);
            }
        }
    }
    
    public void setCustom(boolean b) {
        this.custom.setSelected(b);
        if (b) {
            this.custom.setEnabled(true);
            this.truncatable.setSelected(false);
            this.truncatable.setEnabled(false);
            this.abst.setSelected(false);
            this.abst.setEnabled(false);
        }
        else if (!this.truncatable.isSelected() && !this.abst.isSelected()) {
            this.custom.setEnabled(true);
            if (this.base.getText().length()>0)
                this.truncatable.setEnabled(true);
            this.abst.setEnabled(true);
        }
    }
    
    public String getName() {
        return this.name.getText().trim();
    }
    
    public String getBase() {
        return this.base.getText().trim();
    }
    
    public String getSupports() {
        return this.supports.getText().trim();
    }
    
    public void setName(String name) {
        this.name.setText(name);
    }
    
    public void setBase(String base) {
        this.base.setText(base);
    }
    
    public void setSupports(String supports) {
        this.supports.setText(supports);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        base = new javax.swing.JTextField();
        supports = new javax.swing.JTextField();
        custom = new javax.swing.JCheckBox();
        abst = new javax.swing.JCheckBox();
        truncatable = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(280, 150));
        jLabel1.setText(bundle.getString("TXT_ModuleName"));
        jLabel1.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 4);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText(bundle.getString("TXT_ValuetypeBase"));
        jLabel2.setLabelFor(base);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText(bundle.getString("TXT_ValueTypeSupports"));
        jLabel3.setLabelFor(supports);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        add(jLabel3, gridBagConstraints);

        name.setToolTipText(bundle.getString("TIP_ValuetypeName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 8);
        add(name, gridBagConstraints);

        base.setToolTipText(bundle.getString("TIP_ValuetypeBase"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        add(base, gridBagConstraints);

        supports.setToolTipText(bundle.getString("TIP_ValuetypeSupoorts"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        add(supports, gridBagConstraints);

        custom.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_CustomValue"));
        custom.setText(bundle.getString("TXT_ValuetypeCustom"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        add(custom, gridBagConstraints);

        abst.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_ValueAbstract"));
        abst.setText(bundle.getString("TXT_ValuetypeAbstract"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 8);
        add(abst, gridBagConstraints);

        truncatable.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_TrucatableValue"));
        truncatable.setText(bundle.getString("TXT_ValueTypeTrucatable"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        add(truncatable, gridBagConstraints);

    }//GEN-END:initComponents
    
    public void changedUpdate(javax.swing.event.DocumentEvent event) {
        checkState();
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent event) {
        checkState();
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent event) {
        checkState();
    }
    
    public void actionPerformed(ActionEvent event) {
        checkState();
        java.lang.Object source = event.getSource();
        
        if (source == this.truncatable) {
            if (this.truncatable.isSelected()) {
                this.abst.setEnabled(false);
                this.custom.setEnabled(false);
            }
            else {
                this.abst.setEnabled(true);
                this.custom.setEnabled(true);
            }
        }
        if (source == this.abst) {
            if (this.abst.isSelected()) {
                this.custom.setEnabled(false);
                this.truncatable.setEnabled(false);
            }else {
                this.custom.setEnabled(true);
                if (this.base.getText().length()>0)
                    this.truncatable.setEnabled(true);
            }
        }
        if (source == this.custom) {
            if (this.custom.isSelected()) {
                this.abst.setEnabled(false);
                this.truncatable.setEnabled(false);
            }
            else {
                this.abst.setEnabled(true);
                if (this.base.getText().length()>0)
                    this.truncatable.setEnabled(true);
            }
        }
    }
    
    private void checkState() {
        if (base.getText().length() > 0) {
            if (!this.truncatable.isEnabled() && !this.custom.isSelected() && !this.abst.isSelected())
                this.truncatable.setEnabled(true);
        }
        else {
            if (this.truncatable.isEnabled())
                this.truncatable.setEnabled(false);
            if (this.truncatable.isSelected()) {
                this.truncatable.setSelected(false);
                this.custom.setEnabled(true);
                this.abst.setEnabled(true);
            }
        }
        if (IdlUtilities.isValidIDLIdentifier(name.getText()))
            this.enableOk();
        else
            this.disableOk();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox custom;
    private javax.swing.JCheckBox abst;
    private javax.swing.JCheckBox truncatable;
    private javax.swing.JTextField supports;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField name;
    private javax.swing.JTextField base;
    // End of variables declaration//GEN-END:variables
    
    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle");
    
}
