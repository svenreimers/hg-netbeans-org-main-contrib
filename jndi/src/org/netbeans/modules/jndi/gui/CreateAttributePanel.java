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

package org.netbeans.modules.jndi.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.netbeans.modules.jndi.JndiRootNode;
import org.netbeans.modules.jndi.utils.SimpleListModel;
import org.netbeans.modules.jndi.utils.ExtAttribute;
/**
 *
 * @author  tzezula
 * @version 
 */
public class CreateAttributePanel extends javax.swing.JPanel implements ListSelectionListener, ActionListener {
    
    private ExtAttribute attr;

    /** Creates new form CreateAttributePanel */
    public CreateAttributePanel() {
        this.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_CreateAttributePanel"));
        initComponents ();
        this.postInitComponents ();
    }
    

    public void setModel (ExtAttribute attr) {
        this.attr = attr;
        String attrName = attr.getID();
        if (attrName == null || attrName.length () == 0)
            attrName = JndiRootNode.getLocalizedString ("TXT_DefaultAttrName");
        this.name.setText (attrName);
        SimpleListModel model = (SimpleListModel) this.values.getModel();
        for (int i=0; i< attr.size(); i++) {
            try {
                model.addElement (attr.get (i));
            } catch (NamingException ne) {
            }
        }
    }
    
    public void updateData () {
        if (this.attr != null) {
            this.attr.setID (this.name.getText());
            while (this.attr.size () > 0)
                this.attr.remove (0);
            for (Iterator it = ((SimpleListModel)this.values.getModel()).asVector().iterator(); it.hasNext();) {
                this.attr.add (it.next());
            }
        }
    }


    private void postInitComponents () {
        this.values.setModel (new SimpleListModel());
        this.newAttrValue.setEnabled (false);
        this.jLabel1.setEnabled (false);
        this.values.addListSelectionListener(this);
        this.nameLabel.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_AttributeName_MNEM").charAt(0));
        this.valueLabel.setDisplayedMnemonic (JndiRootNode.getLocalizedString("TXT_AttributeValues_MNEN").charAt(0));
        this.jLabel1.setDisplayedMnemonic (JndiRootNode.getLocalizedString("TXT_AttributeValue_MNEN").charAt(0));
        this.nameLabel.requestFocus();
        name.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString ("AD_AttributeName"));
        newAttrValue.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString ("AD_AttributeValues"));
        values.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString("AD_AttributeValue"));
        jButton1.setMnemonic(JndiRootNode.getLocalizedString ("TXT_AddValue_Mnemonic").charAt(0));
        jButton1.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString ("ACSD_AddValue"));
        jButton3.setMnemonic(JndiRootNode.getLocalizedString ("TXT_Rem_Mnemonic").charAt(0));
        jButton3.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString ("ACSD_Rem"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        valueLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        values = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        newAttrValue = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(400, 200));
        nameLabel.setLabelFor(name);
        nameLabel.setText(JndiRootNode.getLocalizedString("TXT_AttributeName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 6);
        add(nameLabel, gridBagConstraints);

        name.setColumns(16);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 6, 12);
        add(name, gridBagConstraints);

        valueLabel.setLabelFor(values);
        valueLabel.setText(JndiRootNode.getLocalizedString("TXT_AttributeValues"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        add(valueLabel, gridBagConstraints);

        jScrollPane1.setViewportView(values);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 6);
        add(jScrollPane1, gridBagConstraints);

        jButton1.setText(JndiRootNode.getLocalizedString ("TXT_AddValue"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 12);
        add(jButton1, gridBagConstraints);

        jButton3.setText(JndiRootNode.getLocalizedString ("TXT_Rem"));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 12);
        add(jButton3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jLabel1.setLabelFor(newAttrValue);
        jLabel1.setText(JndiRootNode.getLocalizedString("TXT_AttributeValue"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 6);
        add(jLabel1, gridBagConstraints);

        newAttrValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 6);
        add(newAttrValue, gridBagConstraints);

    }//GEN-END:initComponents

    private void change(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change
        // Add your handling code here:
        ((SimpleListModel)this.values.getModel()).changeElementAt (this.values.getSelectedIndex(), this.newAttrValue.getText());
    }//GEN-LAST:event_change

    private void remove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remove
        // Add your handling code here:
        int index = this.values.getSelectedIndex();
        if (index != -1) {
            ((SimpleListModel)this.values.getModel()).removeElementAt (index);
        }
    }//GEN-LAST:event_remove

    private void add(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add
        // Add your handling code here:
        ((SimpleListModel)this.values.getModel()).addElement (JndiRootNode.getLocalizedString("TXT_DefaultAttrValue"));
        this.values.setSelectedIndex (((SimpleListModel)this.values.getModel()).getSize()-1);
    }//GEN-LAST:event_add

    
    public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
        String value = (String)this.values.getSelectedValue();
        if (value != null) {
            this.newAttrValue.setText (value);
            jButton3.setEnabled (true);
            newAttrValue.setEnabled (true);
            jLabel1.setEnabled (true);
        }
        else {
            jButton3.setEnabled (false);
            newAttrValue.setEnabled (false);
            jLabel1.setEnabled (false);
        }
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JList values;
    private javax.swing.JTextField name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JTextField newAttrValue;
    // End of variables declaration//GEN-END:variables

}