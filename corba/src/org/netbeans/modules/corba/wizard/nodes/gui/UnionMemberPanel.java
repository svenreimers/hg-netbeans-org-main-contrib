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

import javax.swing.event.DocumentListener;
/** 
 *
 * @author  root
 * @version 
 */
public class UnionMemberPanel extends ExPanel implements DocumentListener {

  /** Creates new form UnionMemberPanel */
  public UnionMemberPanel() {
    initComponents ();
    postInitComponents ();
  }
  
  public String getName () {
    return this.name.getText ();
  }
  
  public String getType () {
    return this.type.getText ();
  }
  
  public String getLength () {
    return this.length.getText ();
  }
  
  public String getLabel () {
    return this.label.getText ();
  }

  private void postInitComponents () {
    this.name.getDocument().addDocumentListener ( this);
    this.type.getDocument().addDocumentListener (this);
    this.label.getDocument().addDocumentListener (this);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents() {//GEN-BEGIN:initComponents
      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();
      jLabel4 = new javax.swing.JLabel();
      name = new javax.swing.JTextField();
      type = new javax.swing.JTextField();
      length = new javax.swing.JTextField();
      label = new javax.swing.JTextField();
      setLayout(new java.awt.GridBagLayout());
      java.awt.GridBagConstraints gridBagConstraints1;
      setPreferredSize(new java.awt.Dimension(250, 104));

      jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_ModuleName"));
      jLabel1.setLabelFor(name);

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.insets = new java.awt.Insets(8, 8, 4, 4);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      add(jLabel1, gridBagConstraints1);


      jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Type"));
      jLabel2.setLabelFor(type);

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 1;
      gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      add(jLabel2, gridBagConstraints1);


      jLabel3.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Length"));
      jLabel3.setLabelFor(length);

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 2;
      gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      add(jLabel3, gridBagConstraints1);


      jLabel4.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Label"));
      jLabel4.setLabelFor(label);

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 3;
      gridBagConstraints1.insets = new java.awt.Insets(4, 8, 8, 4);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      add(jLabel4, gridBagConstraints1);


      name.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_UMbrName"));

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridwidth = 0;
      gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints1.insets = new java.awt.Insets(8, 4, 4, 8);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints1.weightx = 1.0;
      add(name, gridBagConstraints1);


      type.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_UMbrType"));

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.gridy = 1;
      gridBagConstraints1.gridwidth = 0;
      gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints1.weightx = 1.0;
      add(type, gridBagConstraints1);


      length.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_UMbrLength"));

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.gridy = 2;
      gridBagConstraints1.gridwidth = 0;
      gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints1.weightx = 1.0;
      add(length, gridBagConstraints1);


      label.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_UMbrLabel"));

      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.gridy = 3;
      gridBagConstraints1.gridwidth = 0;
      gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints1.insets = new java.awt.Insets(4, 4, 8, 8);
      gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints1.weightx = 1.0;
      add(label, gridBagConstraints1);

  }//GEN-END:initComponents


// Variables declaration - do not modify//GEN-BEGIN:variables
private javax.swing.JLabel jLabel1;
private javax.swing.JLabel jLabel2;
private javax.swing.JLabel jLabel3;
private javax.swing.JLabel jLabel4;
private javax.swing.JTextField name;
private javax.swing.JTextField type;
private javax.swing.JTextField length;
private javax.swing.JTextField label;
// End of variables declaration//GEN-END:variables


    public void removeUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    public void changedUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    public void insertUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    private void checkState () {
        if (this.name.getText().length() >0 && this.type.getText().length() >0 && this.label.getText().length() >0) {
            enableOk();
        }
        else {
            disableOk();
        }
    }
}