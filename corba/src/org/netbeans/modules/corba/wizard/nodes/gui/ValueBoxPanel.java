/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.nodes.gui;

import org.netbeans.modules.corba.wizard.nodes.utils.IdlUtilities;
/**
 *
 * @author  Tomas Zezula
 */
public class ValueBoxPanel extends ExPanel implements javax.swing.event.DocumentListener {

    /** Creates new form ValueBoxPanel */
    public ValueBoxPanel() {
        initComponents();
        this.name.getDocument().addDocumentListener (this);
        this.type.getDocument().addDocumentListener (this);
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_ModuleName_MNE").charAt(0));
        this.jLabel2.setDisplayedMnemonic (this.bundle.getString("TXT_Type_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString ("AD_ValueBoxPanel"));
    }
    
    public String getName () {
        return this.name.getText().trim();
    }
    
    public String getType () {
        return this.type.getText().trim();
    }
    
    public void setName (String name) {
        this.name.setText (name);
    }
    
    public void setType (String type) {
        this.type.setText (type);
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
        name = new javax.swing.JTextField();
        type = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(250, 56));
        jLabel1.setText(bundle.getString("TXT_ModuleName"));
        jLabel1.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 4);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText(bundle.getString("TXT_Type"));
        jLabel2.setLabelFor(type);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        add(jLabel2, gridBagConstraints);

        name.setToolTipText(bundle.getString("TIP_ValueBoxName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 8);
        add(name, gridBagConstraints);

        type.setToolTipText(bundle.getString("TIP_ValueBoxType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        add(type, gridBagConstraints);

    }//GEN-END:initComponents

    public void changedUpdate(javax.swing.event.DocumentEvent p1) {
        checkState();
    }    

    public void removeUpdate(javax.swing.event.DocumentEvent p1) {
        checkState();
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent p1) {
        checkState();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField type;
    private javax.swing.JTextField name;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle");    

    
    private void checkState () {
        String name = this.name.getText();
        String type = this.type.getText();
        if (IdlUtilities.isValidIDLIdentifier(name)  && type.length() > 0)
            enableOk();
        else
            disableOk();
    }
}
