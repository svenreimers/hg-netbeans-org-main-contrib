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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.openide.*;
import org.openide.util.*;
import org.netbeans.modules.corba.wizard.CorbaWizardData;

/** 
 *
 * @author  tzezula
 * @version 
 */
public class StartPanel extends AbstractWizardPanel {

  /** Creates new form StartPanel */
  public StartPanel() {
    initComponents ();
    this.appRadio.setSelected(true);
  }
  
  public void readCorbaSettings (CorbaWizardData data){
  }
  
  public void storeCorbaSettings (CorbaWizardData data){
    int mask = CorbaWizardData.IMPL;
    
    if (this.appRadio.isSelected()){
      if (this.client.isSelected())
        mask |= CorbaWizardData.CLIENT;
      if (this.server.isSelected())
        mask |= CorbaWizardData.SERVER;
    }
    data.setGenerate (mask);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    jLabel1 = new javax.swing.JLabel ();
    jPanel1 = new javax.swing.JPanel ();
    appRadio = new javax.swing.JRadioButton ();
    idlRadio = new javax.swing.JRadioButton ();
    client = new javax.swing.JCheckBox ();
    server = new javax.swing.JCheckBox ();
    jPanel2 = new javax.swing.JPanel ();
    setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints1;
    setPreferredSize (new java.awt.Dimension(480, 320));

    jLabel1.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_TypeOfApp"));
    jLabel1.setFont (new java.awt.Font ("Dialog", 0, 18));


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints1.insets = new java.awt.Insets (8, 8, 4, 8);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints1.weightx = 1.0;
    add (jLabel1, gridBagConstraints1);

    jPanel1.setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints2;

      appRadio.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_Application"));
      appRadio.setActionCommand ("clientSelected");
      appRadio.addActionListener (new java.awt.event.ActionListener () {
        public void actionPerformed (java.awt.event.ActionEvent evt) {
          clientAction (evt);
        }
      }
      );
  
      gridBagConstraints2 = new java.awt.GridBagConstraints ();
      gridBagConstraints2.gridwidth = 0;
      gridBagConstraints2.insets = new java.awt.Insets (8, 8, 4, 8);
      gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel1.add (appRadio, gridBagConstraints2);
  
      idlRadio.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_Idl"));
      idlRadio.setActionCommand ("idlSelected");
      idlRadio.addActionListener (new java.awt.event.ActionListener () {
        public void actionPerformed (java.awt.event.ActionEvent evt) {
          idlAction (evt);
        }
      }
      );
  
      gridBagConstraints2 = new java.awt.GridBagConstraints ();
      gridBagConstraints2.gridx = 0;
      gridBagConstraints2.gridy = 3;
      gridBagConstraints2.gridwidth = 0;
      gridBagConstraints2.insets = new java.awt.Insets (4, 8, 8, 8);
      gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel1.add (idlRadio, gridBagConstraints2);
  
      client.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_Client"));
  
      gridBagConstraints2 = new java.awt.GridBagConstraints ();
      gridBagConstraints2.gridx = 0;
      gridBagConstraints2.gridy = 1;
      gridBagConstraints2.insets = new java.awt.Insets (4, 8, 4, 8);
      jPanel1.add (client, gridBagConstraints2);
  
      server.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_Server"));
  
      gridBagConstraints2 = new java.awt.GridBagConstraints ();
      gridBagConstraints2.gridx = 0;
      gridBagConstraints2.gridy = 2;
      gridBagConstraints2.insets = new java.awt.Insets (4, 8, 4, 8);
      jPanel1.add (server, gridBagConstraints2);
  

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 1;
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 8, 8);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints1.weightx = 1.0;
    gridBagConstraints1.weighty = 1.0;
    add (jPanel1, gridBagConstraints1);



    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 2;
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.gridheight = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints1.weightx = 1.0;
    gridBagConstraints1.weighty = 1.0;
    add (jPanel2, gridBagConstraints1);

  }//GEN-END:initComponents

  private void idlAction (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idlAction
// Add your handling code here:
    this.appRadio.setSelected(false);
    this.client.setEnabled(false);
    this.server.setEnabled(false);
  }//GEN-LAST:event_idlAction

  private void clientAction (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientAction
// Add your handling code here:
    this.idlRadio.setSelected(false);
    this.client.setEnabled(true);
    this.server.setEnabled(true);
  }//GEN-LAST:event_clientAction


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JRadioButton appRadio;
  private javax.swing.JRadioButton idlRadio;
  private javax.swing.JCheckBox client;
  private javax.swing.JCheckBox server;
  private javax.swing.JPanel jPanel2;
  // End of variables declaration//GEN-END:variables

}