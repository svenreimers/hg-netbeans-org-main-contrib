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

/** 
 *
 * @author  root
 * @version 
 */
public class AttributePanel extends javax.swing.JPanel {

  /** Creates new form AttributePanel */
  public AttributePanel() {
    initComponents ();
  }
  
  public String getName () {
    return this.name.getText ();
  }
  
  public String getType () {
    return this.type.getText ();
  }
  
  public boolean isReadOnly () {
    return this.readonly.isSelected ();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    jLabel1 = new javax.swing.JLabel ();
    jLabel2 = new javax.swing.JLabel ();
    name = new javax.swing.JTextField ();
    type = new javax.swing.JTextField ();
    readonly = new javax.swing.JCheckBox ();
    setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints1;

    jLabel1.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_ModuleName"));


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.insets = new java.awt.Insets (8, 8, 4, 4);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    add (jLabel1, gridBagConstraints1);

    jLabel2.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Type"));


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 1;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 4);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    add (jLabel2, gridBagConstraints1);

    name.setToolTipText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_AttributeName"));


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (8, 4, 4, 8);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints1.weightx = 1.0;
    add (name, gridBagConstraints1);

    type.setToolTipText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_AttributeType"));


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 1;
    gridBagConstraints1.gridy = 1;
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 4, 4, 8);
    gridBagConstraints1.weightx = 1.0;
    add (type, gridBagConstraints1);

    readonly.setToolTipText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_AttributeMode"));
    readonly.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Mode"));


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 8, 8);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints1.weightx = 1.0;
    add (readonly, gridBagConstraints1);

  }//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JTextField name;
  private javax.swing.JTextField type;
  private javax.swing.JCheckBox readonly;
  // End of variables declaration//GEN-END:variables

}