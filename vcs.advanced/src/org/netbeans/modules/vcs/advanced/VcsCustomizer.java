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

package com.netbeans.enterprise.modules.vcs.cmdline;
import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.*;

import org.openide.*;
import org.openide.util.*;

import com.netbeans.developer.modules.vcs.util.*;
import com.netbeans.developer.modules.vcs.*;

/** Customizer
 *
 * @author Michal Fadljevic
 */

public class VcsCustomizer extends javax.swing.JPanel implements Customizer {
  private Debug E=new Debug("VcsCustomizer", false);

  static final long serialVersionUID =-8801742771957370172L;
  /** Creates new form VcsCustomizer */
  public VcsCustomizer() {
    changeSupport=new PropertyChangeSupport(this);
    initComponents ();
    saveAsButton.setMnemonic(KeyEvent.VK_S);
    removeConfigButton.setMnemonic(KeyEvent.VK_R);
    advancedButton.setMnemonic(KeyEvent.VK_A);
    browseButton.setMnemonic(KeyEvent.VK_B);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    advancedButton = new javax.swing.JButton ();
    propsPanel = new javax.swing.JPanel ();
    jLabel2 = new javax.swing.JLabel ();
    rootDirTextField = new javax.swing.JTextField ();
    browseButton = new javax.swing.JButton ();
    jLabel4 = new javax.swing.JLabel ();
    refreshTextField = new javax.swing.JTextField ();
    vcsPanel = new javax.swing.JPanel ();
    configCombo = new javax.swing.JComboBox ();
    saveAsButton = new javax.swing.JButton ();
    removeConfigButton = new javax.swing.JButton ();
    setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints1;

    advancedButton.setLabel ("Advanced...");
    advancedButton.addActionListener (new java.awt.event.ActionListener () {
      public void actionPerformed (java.awt.event.ActionEvent evt) {
        advancedButtonActionPerformed (evt);
      }
    }
    );


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 2;
    gridBagConstraints1.insets = new java.awt.Insets (0, 8, 8, 4);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    add (advancedButton, gridBagConstraints1);

    propsPanel.setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints2;

    jLabel2.setText ("Root Directory:");

    gridBagConstraints2 = new java.awt.GridBagConstraints ();
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.gridy = 2;
    gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
    gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
    propsPanel.add (jLabel2, gridBagConstraints2);

    rootDirTextField.setText (".");
    rootDirTextField.setNextFocusableComponent (browseButton);
    rootDirTextField.addActionListener (new java.awt.event.ActionListener () {
      public void actionPerformed (java.awt.event.ActionEvent evt) {
        rootDirTextFieldActionPerformed (evt);
      }
    }
    );
    rootDirTextField.addFocusListener (new java.awt.event.FocusAdapter () {
      public void focusLost (java.awt.event.FocusEvent evt) {
        rootDirTextFieldFocusLost (evt);
      }
    }
    );

    gridBagConstraints2 = new java.awt.GridBagConstraints ();
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.gridy = 2;
    gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
    gridBagConstraints2.weightx = 0.8;
    propsPanel.add (rootDirTextField, gridBagConstraints2);

    browseButton.setText ("Browse...");
    browseButton.setNextFocusableComponent (refreshTextField);
    browseButton.addActionListener (new java.awt.event.ActionListener () {
      public void actionPerformed (java.awt.event.ActionEvent evt) {
        browseButtonActionPerformed (evt);
      }
    }
    );

    gridBagConstraints2 = new java.awt.GridBagConstraints ();
    gridBagConstraints2.gridx = 2;
    gridBagConstraints2.gridy = 2;
    gridBagConstraints2.gridwidth = 0;
    gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
    propsPanel.add (browseButton, gridBagConstraints2);

    jLabel4.setText ("Refresh Period (milliseconds):");

    gridBagConstraints2 = new java.awt.GridBagConstraints ();
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.gridy = 3;
    gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
    gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
    propsPanel.add (jLabel4, gridBagConstraints2);

    refreshTextField.setText ("0");
    refreshTextField.addActionListener (new java.awt.event.ActionListener () {
      public void actionPerformed (java.awt.event.ActionEvent evt) {
        refreshTextFieldActionPerformed (evt);
      }
    }
    );
    refreshTextField.addFocusListener (new java.awt.event.FocusAdapter () {
      public void focusLost (java.awt.event.FocusEvent evt) {
        refreshTextFieldFocusLost (evt);
      }
    }
    );

    gridBagConstraints2 = new java.awt.GridBagConstraints ();
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.gridy = 3;
    gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
    gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
    propsPanel.add (refreshTextField, gridBagConstraints2);


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 1;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (0, 8, 8, 8);
    gridBagConstraints1.weightx = 1.0;
    add (propsPanel, gridBagConstraints1);

    vcsPanel.setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints3;

    configCombo.setNextFocusableComponent (saveAsButton);
    configCombo.addItemListener (new java.awt.event.ItemListener () {
      public void itemStateChanged (java.awt.event.ItemEvent evt) {
        configComboItemStateChanged (evt);
      }
    }
    );

    gridBagConstraints3 = new java.awt.GridBagConstraints ();
    gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints3.insets = new java.awt.Insets (4, 4, 4, 4);
    gridBagConstraints3.weightx = 0.8;
    vcsPanel.add (configCombo, gridBagConstraints3);

    saveAsButton.setText ("Save as...");
    saveAsButton.setNextFocusableComponent (removeConfigButton);
    saveAsButton.addActionListener (new java.awt.event.ActionListener () {
      public void actionPerformed (java.awt.event.ActionEvent evt) {
        saveAsButtonActionPerformed (evt);
      }
    }
    );

    gridBagConstraints3 = new java.awt.GridBagConstraints ();
    gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints3.insets = new java.awt.Insets (4, 4, 4, 4);
    vcsPanel.add (saveAsButton, gridBagConstraints3);

    removeConfigButton.setText ("Remove");
    removeConfigButton.setNextFocusableComponent (propsPanel);
    removeConfigButton.addActionListener (new java.awt.event.ActionListener () {
      public void actionPerformed (java.awt.event.ActionEvent evt) {
        removeConfigButtonActionPerformed (evt);
      }
    }
    );

    gridBagConstraints3 = new java.awt.GridBagConstraints ();
    gridBagConstraints3.gridx = 2;
    gridBagConstraints3.gridy = 0;
    gridBagConstraints3.gridwidth = 0;
    gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints3.insets = new java.awt.Insets (4, 4, 4, 4);
    vcsPanel.add (removeConfigButton, gridBagConstraints3);


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints1.weightx = 1.0;
    add (vcsPanel, gridBagConstraints1);

  }//GEN-END:initComponents

private void advancedButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedButtonActionPerformed
// Add your handling code here:
    advancedConfiguration ();
  }//GEN-LAST:event_advancedButtonActionPerformed

private void refreshTextFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_refreshTextFieldFocusLost
// Add your handling code here:
    refreshChanged ();
  }//GEN-LAST:event_refreshTextFieldFocusLost

private void rootDirTextFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rootDirTextFieldFocusLost
// Add your handling code here:
    rootDirChanged ();
  }//GEN-LAST:event_rootDirTextFieldFocusLost

private void refreshTextFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshTextFieldActionPerformed
// Add your handling code here:
    refreshChanged ();
  }//GEN-LAST:event_refreshTextFieldActionPerformed

private void browseButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
// Add your handling code here:
    ChooseDirDialog chooseDir=new ChooseDirDialog(new JFrame(), new File(rootDirTextField.getText ()));
    MiscStuff.centerWindow (chooseDir);
    chooseDir.show();
    String selected=chooseDir.getSelectedDir();
    if( selected==null ){
      //D.deb("no directory selected");
      return ;
    }
    File dir=new File(selected);
    if( !dir.isDirectory() ){
      E.err("not directory "+dir);
      return ;
    }
    try{
      rootDirTextField.setText(selected);
      fileSystem.setRootDirectory(dir);
    }
    catch (PropertyVetoException veto){
      fileSystem.debug("I can not change the working directory");
      //E.err(veto,"setRootDirectory() failed");
    }
    catch (IOException e){
      E.err(e,"setRootDirectory() failed");
    }  
  }//GEN-LAST:event_browseButtonActionPerformed

private void rootDirTextFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootDirTextFieldActionPerformed
// Add your handling code here:
    rootDirChanged ();
  }//GEN-LAST:event_rootDirTextFieldActionPerformed

private void removeConfigButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeConfigButtonActionPerformed
// Add your handling code here:
    String label = (String) configCombo.getSelectedItem ();
    NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation ("Are you sure you want to delete configuration: " + label, NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
    if(NotifyDescriptor.Confirmation.CANCEL_OPTION.equals (TopManager.getDefault ().notify (nd))) return;
    File f = new File (fileSystem.getConfigRoot()+File.separator + configNamesByLabel.get (label) + ".properties"); 
    if(f.isFile () && f.canWrite ()) {
      f.delete ();
      // set config to fileSystem, it will be rereaded after refresh in updateConfigurations ()
      promptForConfigComboChange = false;
      if(configCombo.getSelectedIndex ()==0) configCombo.setSelectedIndex (1);
      else configCombo.setSelectedIndex (0);
      promptForConfigComboChange = false;
      updateConfigurations ();
    }
  }//GEN-LAST:event_removeConfigButtonActionPerformed

private void saveAsButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
// Add your handling code here:
    ChooseFileDialog chooseFile=new ChooseFileDialog(new JFrame(), new File(fileSystem.getConfigRoot()));
    MiscStuff.centerWindow (chooseFile);
    chooseFile.show();
    String selected=chooseFile.getSelectedFile ();
    if(selected==null) return;
    if(!selected.endsWith(".properties")) {
      selected += ".properties";
    }
    File f = new File (selected); 
    if( selected==null ){
      //D.deb("no directory selected");
      return ;
    }
    String label = selected;
    label = f.getName ().substring (0, f.getName ().length() - ".properties".length());
    Vector variables=fileSystem.getVariables ();
    Object advanced=fileSystem.getAdvancedConfig ();
    VcsConfigVariable.writeConfiguration (selected, label, variables, advanced, fileSystem.getVcsFactory ().getVcsAdvancedCustomizer ()); 
    promptForConfigComboChange = false;
    fileSystem.setConfig (label);
    updateConfigurations ();
  }//GEN-LAST:event_saveAsButtonActionPerformed

private void configComboItemStateChanged (java.awt.event.ItemEvent evt) {//GEN-FIRST:event_configComboItemStateChanged
// Add your handling code here:
   
    switch( evt.getStateChange() ){
    case ItemEvent.SELECTED:
      String selectedLabel=(String)evt.getItem();
      E.deb ("config state changed to:"+selectedLabel);
      if(selectedLabel.equalsIgnoreCase("empty")) {
        removeConfigButton.setEnabled (false);
        saveAsButton.setNextFocusableComponent (propsPanel);
      } else {
        removeConfigButton.setEnabled (true);
        saveAsButton.setNextFocusableComponent (removeConfigButton);
      }      
      int selectedIndex=configCombo.getSelectedIndex();

      if( oldIndex==selectedIndex ){
	//D.deb("nothing has changed oldIndex==selectedIndex=="+oldIndex);
	return ;
      }

      String msg=g("MSG_Do_you_really_want_to_discard_current_commands",selectedLabel);
      NotifyDescriptor nd = new NotifyDescriptor.Confirmation (msg, NotifyDescriptor.YES_NO_OPTION );
      if (!promptForConfigComboChange || TopManager.getDefault().notify( nd ).equals( NotifyDescriptor.YES_OPTION ) ) {
	//D.deb("yes");
        // just do not display prompt for the first change if config was not edited
        promptForConfigComboChange = true;
	loadConfig(selectedLabel);
	oldIndex=selectedIndex;
      }
      else{
	//D.deb("no");
	String oldLabel=(String)configCombo.getItemAt(oldIndex);
	//D.deb("oldLabel="+oldLabel+", oldIndex="+oldIndex);
	loadConfig(oldLabel);
	configCombo.setSelectedIndex(oldIndex);
      }
      break ;

    case ItemEvent.DESELECTED: 
      break ;
    }
  }//GEN-LAST:event_configComboItemStateChanged


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton advancedButton;
  private javax.swing.JPanel propsPanel;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JTextField rootDirTextField;
  private javax.swing.JButton browseButton;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JTextField refreshTextField;
  private javax.swing.JPanel vcsPanel;
  private javax.swing.JComboBox configCombo;
  private javax.swing.JButton saveAsButton;
  private javax.swing.JButton removeConfigButton;
  // End of variables declaration//GEN-END:variables

  private Vector varLabels = new Vector ();
  private Vector varTextFields = new Vector ();
  private Vector varVariables = new Vector ();
  private VcsFileSystem fileSystem=null; 
  private PropertyChangeSupport changeSupport=null;
  private Vector configLabels;
  private int oldIndex=0;
  private boolean promptForConfigComboChange = false;
  
  // Entries in hashtables are maintained as a cache of properties read from disk 
  // and are read only. Changes are applied only to fileSystem.variables (fileSystem.commands).
  private Hashtable configVariablesByLabel;
  private Hashtable configAdvancedByLabel;
  private Hashtable configNamesByLabel;
    
  //-------------------------------------------
  private void loadConfig(String label){
    if(!label.equals (fileSystem.getConfig ())) {
      Vector variables=(Vector)configVariablesByLabel.get(label);
      Vector advanced=(Vector)configAdvancedByLabel.get(label);
      fileSystem.setVariables(variables);
      fileSystem.setAdvancedConfig(advanced);
      fileSystem.setConfig(label);
    }
    initAdditionalComponents ();
  }

  //-------------------------------------------
  public static void main(java.lang.String[] args) {
    JDialog dialog=new JDialog(new Frame (), true );
    VcsCustomizer customizer= new VcsCustomizer();
    dialog.getContentPane().add(customizer);
    dialog.pack (); 
    dialog.show();
  }


  //-------------------------------------------
  public void addPropertyChangeListener(PropertyChangeListener l) {
    //D.deb("addPropertyChangeListener()");
    changeSupport.addPropertyChangeListener(l);
  }


  //-------------------------------------------
  public void removePropertyChangeListener(PropertyChangeListener l) {
    //D.deb("removePropertyChangeListener()");
    changeSupport.removePropertyChangeListener(l);
  }


  //-------------------------------------------
  private void advancedConfiguration () {
    JPanel panel = new JPanel ();
    panel.setLayout (new java.awt.GridBagLayout ());
    
    java.awt.GridBagConstraints gridBagConstraints1;
    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints1.weightx = 1;
    gridBagConstraints1.weighty = 0.5;

    
    final UserVariablesEditor variableEditor= new UserVariablesEditor();
    variableEditor.setValue( fileSystem.getVariables() );
    panel.add (new UserVariablesPanel (variableEditor), gridBagConstraints1);

    PropertyEditor advancedEditor = fileSystem.getVcsFactory ().getVcsAdvancedCustomizer ().getEditor (fileSystem);
    JPanel advancedPanel = fileSystem.getVcsFactory ().getVcsAdvancedCustomizer ().getPanel (advancedEditor);
    
    gridBagConstraints1.gridy = 1;
    panel.add (advancedPanel, gridBagConstraints1);
    
    DialogDescriptor dd = new DialogDescriptor (panel, "Advanced Properties Editor");
    TopManager.getDefault ().createDialog (dd).show ();
    if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
      fileSystem.setVariables ( (Vector)variableEditor.getValue ());
      fileSystem.setAdvancedConfig (advancedEditor.getValue ());
    }
    initAdditionalComponents ();
  }

  private void initAdditionalComponents () {
    refreshTextField.setVisible (true);
    jLabel4.setVisible (true);
    if(!fileSystem.getCache ().isLocalFilesAdd ()) {
      refreshTextField.setVisible (false);
      jLabel4.setVisible (false);
    }
    varVariables = new Vector ();
    while(varLabels.size ()>0) {
      propsPanel.remove ((JComponent) varLabels.get (0));
      propsPanel.remove ((JComponent) varTextFields.get (0));
      varLabels.remove (0);
      varTextFields.remove (0);
    }
    Enumeration vars = fileSystem.getVariables ().elements ();
    while (vars.hasMoreElements ()) {
      VcsConfigVariable var = (VcsConfigVariable) vars.nextElement ();
      if(var.isBasic ()) {
        JLabel lb;
        JTextField tf;
        lb = new JLabel ();
        tf = new JTextField ();
        varLabels.add (lb);
        varTextFields.add (tf);
        
        java.awt.GridBagConstraints gridBagConstraints1;
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = varLabels.size () + 4;
        gridBagConstraints1.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add (lb, gridBagConstraints1);
         tf.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            variableChanged (evt);
          } 
        }
        );
        tf.addFocusListener (new java.awt.event.FocusAdapter () {
          public void focusLost (java.awt.event.FocusEvent evt) {
            variableChanged (evt);
          }
        }
        );
        
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        propsPanel.add (tf, gridBagConstraints1);
        varVariables.add (var);
        String varLabel = var.getLabel ().trim ();
        if(!varLabel.endsWith (":")) varLabel += ":";
        lb.setText (varLabel);
        tf.setText (var.getValue ());
      }      
    }
    java.awt.Component comp = this;
    while (comp!=null && !(comp instanceof java.awt.Window)) comp = comp.getParent ();
    if(comp!=null) {
      ((java.awt.Window) comp).pack ();
    }
  }
  
  private void variableChanged (java.awt.AWTEvent evt) {
    JTextField tf = (JTextField) evt.getSource ();
    VcsConfigVariable var=null;
    for(int i=0; i<varTextFields.size () && var==null; i++) {
      if(tf == varTextFields.get (i)) {
        var = (VcsConfigVariable) varVariables.get (i);
      }
    }
    if(var!=null){
      var.setValue (tf.getText ());
      // enable fs to react on change in variables
      fileSystem.setVariables(fileSystem.getVariables());
    } else {
      E.deb ("Error setting variable:"+tf.getText ());
    }
  }
  
  /**
  * Read configurations from disk.
  */
  //-------------------------------------------
  private void updateConfigurations(){
    Vector configNames=VcsConfigVariable.readConfigurations(fileSystem.getConfigRoot());
    //D.deb("configNames="+configNames);

    if( configCombo.getItemCount()>0 ){ // necessary on Linux 
      configCombo.removeAllItems();
    }

    configLabels=new Vector(5);
    configVariablesByLabel=new Hashtable(8);
    configAdvancedByLabel=new Hashtable(8);
    configNamesByLabel=new Hashtable(8);

    String selectedConfig=fileSystem.getConfig();
    int newIndex=0;

    for(int i=0;i<configNames.size();i++){
      String name=(String)configNames.elementAt(i);

      Properties props= VcsConfigVariable.readPredefinedProperties
	( fileSystem.getConfigRoot()+File.separator+name+".properties");

      String label=props.getProperty("label", g("CTL_No_label_configured"));
      configLabels.addElement(label);

      if( label.equals(selectedConfig) ){
	newIndex=i;
      }
      
      configNamesByLabel.put(label,name);
      
      Vector variables=VcsConfigVariable.readVariables(props);
      configVariablesByLabel.put(label,variables);

      
      Object advanced=fileSystem.getVcsFactory (). getVcsAdvancedCustomizer ().readConfig (props);;
      configAdvancedByLabel.put(label, advanced);
      
      configCombo.addItem(label);
    }
    
    configCombo.setSelectedIndex( newIndex );
    promptForConfigComboChange = false;
  }
  

  //-------------------------------------------
  public void setObject(Object bean){
    //D.deb("setObject("+bean+")");
    fileSystem=(VcsFileSystem)bean;

    rootDirTextField.setText( fileSystem.getRootDirectory().toString() );
    refreshTextField.setText (""+fileSystem.getCustomRefreshTime ());
    updateConfigurations();
    initAdditionalComponents ();
/*
    // find if this fs is in the repository
    boolean alreadyMounted = false;
    Enumeration en = TopManager.getDefault ().getRepository ().getFileSystems ();
    while (en.hasMoreElements ()) {
      if(fileSystem==en.nextElement ()) alreadyMounted = true;
    }
    System.out.println ("mounted:"+alreadyMounted);
    if(alreadyMounted) {
      String label = fileSystem.getConfig ();
      Object backupV = configVariablesByLabel.get (label);
      Object backupC = configAdvancedByLabel.get (label);
      
      // fake config in hashtables by values from fs
      configVariablesByLabel.put (label, fileSystem.getVariables ());
      configAdvancedByLabel.put (label, fileSystem.getAdvancedConfig ());
      oldIndex = -1;
      // let it read variables and commands
      configCombo.setSelectedItem (label);
      configVariablesByLabel.put (label, backupV);
      configAdvancedByLabel.put (label, backupC);
    }
*/
  }
  
  private void rootDirChanged () {
    // root dir set by hand
    String selected= rootDirTextField.getText ();
    if( selected==null ){
      //D.deb("no directory selected");
      return ;
    }
    File dir=new File(selected);
    if( !dir.isDirectory() ){
      E.err("not directory "+dir);
      return ;
    }
    try{
      fileSystem.setRootDirectory(dir);
      rootDirTextField.setText(selected);
    }
    catch (PropertyVetoException veto){
      fileSystem.debug("I can not change the working directory");
      //E.err(veto,"setRootDirectory() failed");
    }
    catch (IOException e){
      E.err(e,"setRootDirectory() failed");
    }  
  }

  private void refreshChanged () {
    try {
      int time = Integer.parseInt(refreshTextField.getText());
      fileSystem.setCustomRefreshTime (time);
      E.deb("refresh time set to:" + time);
    } catch (NumberFormatException e) {
      E.deb(e.getMessage());    
    }
  }

  //-------------------------------------------
  String g(String s) {
    return NbBundle.getBundle
      ("com.netbeans.developer.modules.vcs.cmdline.Bundle").getString (s);
  }
  String  g(String s, Object obj) {
    return MessageFormat.format (g(s), new Object[] { obj });
  }
  String g(String s, Object obj1, Object obj2) {
    return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
  }
  String g(String s, Object obj1, Object obj2, Object obj3) {
    return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
  }

  
}

/*
* <<Log>>
*  2    Gandalf   1.1         11/27/99 Patrik Knakal   
*  1    Gandalf   1.0         11/24/99 Martin Entlicher 
* $
*/
