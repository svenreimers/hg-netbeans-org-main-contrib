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

package org.netbeans.modules.corba.wizard.panels;

/**
 *
 * @author  Dusan Balek
 */
public class StringPanel extends BindingDetail implements javax.swing.event.DocumentListener {

    /** Creates new form FilePanel */
    public StringPanel() {
        initComponents();
        this.name.getDocument().addDocumentListener (this);
        this.label.setDisplayedMnemonic (this.bundle.getString ("TXT_FileName_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_StringPanel"));
    }
    
    public void setData (Object data) {
        if (data != null && data instanceof String)
            this.name.setText ((String)data);
    }
    
    public Object getData () {
        return this.name.getText();
    }

    public void setTitle (String title) {
        this.label.setText (title);
    }
    
    public String getTitle () {
        return this.label.getText();
    }

    public boolean isValid () {
    	return (this.name.getText() != null && this.name.getText().length() > 0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        label = new javax.swing.JLabel();
        name = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        label.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_FileName"));
        label.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        add(label, gridBagConstraints);

        name.setToolTipText(bundle.getString("MSG_IORFileName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        add(name, gridBagConstraints);

    }//GEN-END:initComponents

    public void changedUpdate(javax.swing.event.DocumentEvent event) {
        this.fireChange(this);
    }    

    public void removeUpdate(javax.swing.event.DocumentEvent event) {
        this.fireChange(this);
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent event) {
        this.fireChange(this);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    private javax.swing.JTextField name;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");    

}
