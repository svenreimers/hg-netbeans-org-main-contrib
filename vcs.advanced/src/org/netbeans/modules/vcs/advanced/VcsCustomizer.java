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

package org.netbeans.modules.vcs.advanced;

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
import org.openide.filesystems.*;
import org.openide.loaders.DataObject;
import org.openide.loaders.XMLDataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.cmdline.*;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;

/** Customizer
 *
 * @author Michal Fadljevic, Martin Entlicher
 */

public class VcsCustomizer extends javax.swing.JPanel implements Customizer {
    private Debug E=new Debug("VcsCustomizer", true); // NOI18N
    private Debug D = E;

    /**
     * The name of the variable, that contains pairs of variables and commands.
     * When the variables listed here change their value, the corresponding command
     * is executed to fill values of remaining variables. This can be used to automatically
     * fill in VCS configuartion, when it can be obtained from local configuration files.
     */
    public static final String VAR_AUTO_FILL = "AUTO_FILL_VARS";

    private HashMap autoFillVars = new HashMap();
    
    private HashMap cache = new HashMap ();
    
    //private static transient FileLock configSaveLock = FileLock.NONE;

    static final long serialVersionUID = -8801742771957370172L;
    /** Creates new form VcsCustomizer */
    public VcsCustomizer () {
        changeSupport = new PropertyChangeSupport (this);
        initComponents ();
        removeEnterFromKeymap ();

        //Configuration tab
        saveAsButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.saveAsButton.mnemonic").charAt (0));
        removeConfigButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.removeConfigButton.mnemonic").charAt (0));
        jLabel2.setDisplayedMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.jLabel2.mnemonic").charAt (0));
        jLabel2.setLabelFor (rootDirTextField);
        browseButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.browseButton.mnemonic").charAt (0));
        relMountLabel.setDisplayedMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountLabel.mnemonic").charAt (0));
        relMountLabel.setLabelFor (relMountTextField);
        relMountButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountButton.mnemonic").charAt (0));
        jLabel4.setDisplayedMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.jLabel4.mnemonic").charAt (0));
        jLabel4.setLabelFor (refreshTextField);
        //Advanced tab
        varButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.varButton.mnemonic").charAt (0));
        cmdButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.cmdButton.mnemonic").charAt (0));
        advancedModeCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.advancedModeCheckBox.mnemonic").charAt (0));
        offLineCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.offLineCheckBox.mnemonic").charAt (0));
        editCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.editCheckBox.mnemonic").charAt (0));
        promptEditCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptEditCheckBox.mnemonic").charAt (0));
        lockCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.lockCheckBox.mnemonic").charAt (0));
        promptLockCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptLockCheckBox.mnemonic").charAt (0));
        debugCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.debugCheckBox.mnemonic").charAt (0));

        linkLabel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        HelpCtx.setHelpIDString (this, VcsCustomizer.class.getName ());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jTabbedPane1 = new javax.swing.JTabbedPane();
        configPanel = new javax.swing.JPanel();
        vcsPanel = new javax.swing.JPanel();
        configCombo = new javax.swing.JComboBox();
        saveAsButton = new javax.swing.JButton();
        removeConfigButton = new javax.swing.JButton();
        propsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rootDirTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        relMountLabel = new javax.swing.JLabel();
        relMountTextField = new javax.swing.JTextField();
        relMountButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        refreshTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        linkLabel = new javax.swing.JLabel();
        advancedPanel = new javax.swing.JPanel();
        advancedModeCheckBox = new javax.swing.JCheckBox();
        offLineCheckBox = new javax.swing.JCheckBox();
        editCheckBox = new javax.swing.JCheckBox();
        promptEditCheckBox = new javax.swing.JCheckBox();
        lockCheckBox = new javax.swing.JCheckBox();
        promptLockCheckBox = new javax.swing.JCheckBox();
        debugCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        cmdButton = new javax.swing.JButton();
        varButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        configPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        vcsPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints3;
        
        vcsPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), " " + java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.congifurationTitle.text") + " "));
        configCombo.setNextFocusableComponent(saveAsButton);
        configCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                configComboItemStateChanged(evt);
            }
        });
        
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.insets = new java.awt.Insets(4, 12, 11, 0);
        gridBagConstraints3.weightx = 1.0;
        vcsPanel.add(configCombo, gridBagConstraints3);
        
        saveAsButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.saveAsButton.text"));
        saveAsButton.setNextFocusableComponent(removeConfigButton);
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.insets = new java.awt.Insets(4, 5, 11, 0);
        vcsPanel.add(saveAsButton, gridBagConstraints3);
        
        removeConfigButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.removeConfigButton.text"));
        removeConfigButton.setNextFocusableComponent(propsPanel);
        removeConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeConfigButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.insets = new java.awt.Insets(4, 5, 11, 11);
        vcsPanel.add(removeConfigButton, gridBagConstraints3);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets(11, 11, 0, 10);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.weightx = 1.0;
        configPanel.add(vcsPanel, gridBagConstraints2);
        
        propsPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints4;
        
        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.jLabel2.text"));
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 12);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(jLabel2, gridBagConstraints4);
        
        rootDirTextField.setColumns(15);
        rootDirTextField.setText(".");
        rootDirTextField.setNextFocusableComponent(browseButton);
        rootDirTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootDirTextFieldActionPerformed(evt);
            }
        });
        
        rootDirTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rootDirTextFieldFocusLost(evt);
            }
        });
        
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 5);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.weightx = 1.0;
        propsPanel.add(rootDirTextField, gridBagConstraints4);
        
        browseButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.browseButton.text"));
        browseButton.setNextFocusableComponent(refreshTextField);
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 2;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(browseButton, gridBagConstraints4);
        
        relMountLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountLabel.text"));
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 12);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(relMountLabel, gridBagConstraints4);
        
        relMountTextField.setMinimumSize(new java.awt.Dimension(165, 21));
        relMountTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relMountTextFieldActionPerformed(evt);
            }
        });
        
        relMountTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                relMountTextFieldFocusLost(evt);
            }
        });
        
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 5);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.weightx = 1.0;
        propsPanel.add(relMountTextField, gridBagConstraints4);
        
        relMountButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountButton.text"));
        relMountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relMountButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 2;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(relMountButton, gridBagConstraints4);
        
        jLabel4.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.jLabel4.text"));
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 2;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 12);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(jLabel4, gridBagConstraints4);
        
        refreshTextField.setColumns(8);
        refreshTextField.setText("0");
        refreshTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshTextFieldActionPerformed(evt);
            }
        });
        
        refreshTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                refreshTextFieldFocusLost(evt);
            }
        });
        
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.gridy = 2;
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 5);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.weightx = 1.0;
        propsPanel.add(refreshTextField, gridBagConstraints4);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        configPanel.add(propsPanel, gridBagConstraints2);
        
        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints5;
        
        gridBagConstraints5 = new java.awt.GridBagConstraints();
        gridBagConstraints5.gridwidth = 2;
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.insets = new java.awt.Insets(0, 0, 4, 0);
        gridBagConstraints5.weightx = 1.0;
        jPanel1.add(jSeparator1, gridBagConstraints5);
        
        jLabel1.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("AdditionalProfilesText"));
        jLabel1.setForeground(java.awt.Color.black);
        gridBagConstraints5 = new java.awt.GridBagConstraints();
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel1, gridBagConstraints5);
        
        linkLabel.setText("http://vcsgeneric.netbeans.org/profiles/index.html");
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                linkLabelMouseReleased(evt);
            }
        });
        
        gridBagConstraints5 = new java.awt.GridBagConstraints();
        gridBagConstraints5.gridx = 1;
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.insets = new java.awt.Insets(0, 6, 0, 0);
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints5.weightx = 1.0;
        jPanel1.add(linkLabel, gridBagConstraints5);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets(12, 12, 11, 11);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints2.weightx = 1.0;
        configPanel.add(jPanel1, gridBagConstraints2);
        
        jTabbedPane1.addTab("Configuration", configPanel);
        
        advancedPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints6;
        
        advancedModeCheckBox.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.advancedModeCheckBox.text"));
        advancedModeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedModeCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(12, 0, 0, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(advancedModeCheckBox, gridBagConstraints6);
        
        offLineCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.offLineCheckBox.text"));
        offLineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offLineCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 1;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(0, 0, 12, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(offLineCheckBox, gridBagConstraints6);
        
        editCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.editCheckBox.text"));
        editCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(0, 0, 0, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(editCheckBox, gridBagConstraints6);
        
        promptEditCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptEditCheckBox.text"));
        promptEditCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptEditCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 3;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(5, 24, 5, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(promptEditCheckBox, gridBagConstraints6);
        
        lockCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.lockCheckBox.text"));
        lockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 4;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(0, 0, 5, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(lockCheckBox, gridBagConstraints6);
        
        promptLockCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptLockCheckBox.text"));
        promptLockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptLockCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 5;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(0, 24, 12, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(promptLockCheckBox, gridBagConstraints6);
        
        debugCheckBox.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.debugCheckBox.text"));
        debugCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugCheckBoxActionPerformed(evt);
            }
        });
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 6;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets(0, 0, 0, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.weightx = 1.0;
        advancedPanel.add(debugCheckBox, gridBagConstraints6);
        
        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 5, 0));
        
        cmdButton.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.cmdButton.text"));
        cmdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdButtonActionPerformed(evt);
            }
        });
        
        jPanel2.add(cmdButton);
        
        varButton.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.varButton.text"));
        varButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                varButtonActionPerformed(evt);
            }
        });
        
        jPanel2.add(varButton);
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 8;
        gridBagConstraints6.insets = new java.awt.Insets(12, 12, 11, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints6.weighty = 1.0;
        advancedPanel.add(jPanel2, gridBagConstraints6);
        
        jLabel5.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.modesLabel.text"));
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        advancedPanel.add(jLabel5, gridBagConstraints6);
        
        jLabel6.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.actionsLabel.text"));
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.insets = new java.awt.Insets(0, 12, 0, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        advancedPanel.add(jLabel6, gridBagConstraints6);
        
        jLabel7.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.otherLabel.text"));
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 6;
        gridBagConstraints6.insets = new java.awt.Insets(0, 12, 0, 11);
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        advancedPanel.add(jLabel7, gridBagConstraints6);
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 7;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.VERTICAL;
        advancedPanel.add(jLabel8, gridBagConstraints6);
        
        jTabbedPane1.addTab("Advanced", advancedPanel);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jTabbedPane1, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void linkLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkLabelMouseReleased
        // Add your handling code here:
        try {
            TopManager.getDefault().showUrl(new java.net.URL(linkLabel.getText()));
        } catch (java.net.MalformedURLException exc) {
            TopManager.getDefault().notifyException(exc);
        }
    }//GEN-LAST:event_linkLabelMouseReleased

    private void cmdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdButtonActionPerformed
        // Add your handling code here:
        UserCommandsEditor commandsEditor = new UserCommandsEditor();
        commandsEditor.setValue(fileSystem.getCommands());
        UserCommandsPanel advancedPanel = new UserCommandsPanel(commandsEditor);
        DialogDescriptor dd = new DialogDescriptor (advancedPanel, org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("TIT_CommandsEditor"));//, "Advanced Properties Editor");
        dd.setHelpCtx (new HelpCtx ("VCS_CommandEditor"));
        TopManager.getDefault ().createDialog (dd).setVisible(true);
        commandsEditor.setValue(advancedPanel.getPropertyValue());
        if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
            fileSystem.setCommands ((Node) commandsEditor.getValue ());
        }
    }//GEN-LAST:event_cmdButtonActionPerformed

    private void varButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_varButtonActionPerformed
        // Add your handling code here:
        UserVariablesEditor variableEditor= new UserVariablesEditor();
        variableEditor.setValue( fileSystem.getVariables() );
        UserVariablesPanel variablePanel = new UserVariablesPanel (variableEditor);

        DialogDescriptor dd = new DialogDescriptor (variablePanel, org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("TIT_VariablesEditor"));//, "Advanced Properties Editor");
        dd.setHelpCtx (new HelpCtx ("VCS_VariableEditor"));
        TopManager.getDefault ().createDialog (dd).setVisible(true);
        if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
            fileSystem.setVariables ((Vector) variableEditor.getValue ());
        }
        initAdditionalComponents ();
    }//GEN-LAST:event_varButtonActionPerformed

    private void offLineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offLineCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setOffLine(offLineCheckBox.isSelected());
    }//GEN-LAST:event_offLineCheckBoxActionPerformed

    private void promptLockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptLockCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setPromptForLockOn(promptLockCheckBox.isSelected());
    }//GEN-LAST:event_promptLockCheckBoxActionPerformed

    private void lockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockCheckBoxActionPerformed
        // Add your handling code here:
        promptLockCheckBox.setEnabled(lockCheckBox.isSelected());
        fileSystem.setLockFilesOn(lockCheckBox.isSelected());
    }//GEN-LAST:event_lockCheckBoxActionPerformed

    private void promptEditCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptEditCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setPromptForEditOn(promptEditCheckBox.isSelected());
    }//GEN-LAST:event_promptEditCheckBoxActionPerformed

    private void editCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCheckBoxActionPerformed
        // Add your handling code here:
        promptEditCheckBox.setEnabled(editCheckBox.isSelected());
        fileSystem.setCallEditFilesOn(editCheckBox.isSelected());
    }//GEN-LAST:event_editCheckBoxActionPerformed

    private void debugCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setDebug(debugCheckBox.isSelected());
    }//GEN-LAST:event_debugCheckBoxActionPerformed

    private void advancedModeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedModeCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setExpertMode(advancedModeCheckBox.isSelected());
    }//GEN-LAST:event_advancedModeCheckBoxActionPerformed

  private void relMountTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_relMountTextFieldFocusLost
// Add your handling code here:
        relMountPointChanged();
  }//GEN-LAST:event_relMountTextFieldFocusLost

  private void relMountTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relMountTextFieldActionPerformed
// Add your handling code here:
        relMountPointChanged();
  }//GEN-LAST:event_relMountTextFieldActionPerformed

  private void relMountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relMountButtonActionPerformed
// Add your handling code here:
        String work = rootDirTextField.getText();
        String dir = work + File.separator + relMountTextField.getText();
        RelativeMountDialog mountDlg = new RelativeMountDialog();
        mountDlg.setDir(work, relMountTextField.getText());
        java.awt.Dialog dlg = TopManager.getDefault().createDialog(mountDlg);
        //VcsUtilities.centerWindow (mountDlg);
        //HelpCtx.setHelpIDString (dlg.getRootPane (), CvsCustomizer.class.getName ());
        dlg.setVisible(true);
        String selected = mountDlg.getRelMount();
        if (selected != null) {
            relMountTextField.setText(selected);
            relMountPointChanged();
        }
  }//GEN-LAST:event_relMountButtonActionPerformed

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
        VcsUtilities.centerWindow (chooseDir);
        chooseDir.show();
        String selected=chooseDir.getSelectedDir();
        if( selected==null ){
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        rootDirTextField.setText(selected);
        rootDirChanged();
        /*
        String module = getModuleValue();
        String moduleDir = module.equals ("") ? selected : selected + java.io.File.separator + module; // NOI18N
        File dir=new File(moduleDir);
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
        */
    }//GEN-LAST:event_browseButtonActionPerformed

    private void rootDirTextFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootDirTextFieldActionPerformed
        // Add your handling code here:
        rootDirChanged ();
    }//GEN-LAST:event_rootDirTextFieldActionPerformed

    private void removeConfigButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeConfigButtonActionPerformed
        // Add your handling code here:
        String label = (String) configCombo.getSelectedItem ();
        NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation (g("DLG_DeleteConfig", label), NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
        if(NotifyDescriptor.Confirmation.CANCEL_OPTION.equals (TopManager.getDefault ().notify (nd))) return;
        FileObject file = fileSystem.getConfigRootFO();
        if (file != null) file = file.getFileObject((String) configNamesByLabel.get (label));
        if (file != null) {
            try {
                file.delete(file.lock());
            } catch (IOException e) {
                return;
            }
            promptForConfigComboChange = false;
            if(configCombo.getSelectedIndex() == 0) if (configCombo.getModel().getSize() > 1) configCombo.setSelectedIndex(1);
                else configCombo.setSelectedIndex(0);
            promptForConfigComboChange = false;
            updateConfigurations ();
        }
        /*
        File f = new File (fileSystem.getConfigRoot()+File.separator + configNamesByLabel.get (label) + ".properties"); // NOI18N
        if(f.isFile () && f.canWrite ()) {
          f.delete ();
          // set config to fileSystem, it will be rereaded after refresh in updateConfigurations ()
          promptForConfigComboChange = false;
          if(configCombo.getSelectedIndex ()==0) configCombo.setSelectedIndex (1);
          else configCombo.setSelectedIndex (0);
          promptForConfigComboChange = false;
          updateConfigurations ();
    }
        */
    }//GEN-LAST:event_removeConfigButtonActionPerformed

    private void saveAsButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        // Add your handling code here:
        FileObject dir = fileSystem.getConfigRootFO();
        ConfigSaveAsDialog chooseFile = new ConfigSaveAsDialog(new JFrame(), true, dir);
        VcsUtilities.centerWindow (chooseFile);
        chooseFile.show();
        String selected=chooseFile.getSelectedFile ();
        if (selected == null) return;
        String configLabel = chooseFile.getSelectedConfigLabel();
        FileObject file = dir.getFileObject(selected, VariableIO.CONFIG_FILE_EXT);
        if (file == null) {
            try {
                file = dir.createData(selected, VariableIO.CONFIG_FILE_EXT);
            } catch(IOException e) {
                E.err("Can not create file '"+selected+"'");
                return;
            }
        } else {
            if (NotifyDescriptor.Confirmation.NO_OPTION.equals (
                TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (g("DLG_OverwriteSettings", file.getName()), NotifyDescriptor.Confirmation.YES_NO_OPTION)))) {
                    return;
            }
        }
        org.w3c.dom.Document doc = null;
        /*
        DataObject dobj = null;
        try {
            dobj = DataObject.find(file);
        } catch (DataObjectNotFoundException exc) {
            dobj = null;
        }
        if (dobj != null && dobj instanceof XMLDataObject) {
            doc = ((XMLDataObject) dobj).createDocument();
        }
         */
        doc = org.openide.xml.XMLUtil.createDocument(VariableIO.CONFIG_ROOT_ELEM, null, null, null);
        Vector variables = fileSystem.getVariables ();
        Node commands = fileSystem.getCommands();
        if (configLabel == null) configLabel = selected;
        //String label = selected;
        if (doc != null) {
            FileLock lock = null;
            try {
                lock = file.lock();
                VariableIO.writeVariables(doc, configLabel, variables);
                UserCommandIO.writeCommands(doc, commands);
                org.openide.xml.XMLUtil.write(doc, file.getOutputStream(lock), null);
                //XMLDataObject.write(doc, new BufferedWriter(new OutputStreamWriter(file.getOutputStream(lock))));
            } catch (org.w3c.dom.DOMException exc) {
                org.openide.TopManager.getDefault().notifyException(exc);
            } catch (java.io.IOException ioexc) {
                org.openide.TopManager.getDefault().notifyException(ioexc);
            } finally {
                if (lock != null) lock.releaseLock();
            }
        } else {
            //VariableIOCompat.write (file, label, variables, advanced, fileSystem.getVcsFactory ().getVcsAdvancedCustomizer ());
        }
        promptForConfigComboChange = false;
        fileSystem.setConfig (configLabel);
        fileSystem.setConfigFileName(file.getNameExt());
        updateConfigurations ();
        promptForConfigComboChange = true;
        /*
          ChooseFileDialog chooseFile=new ChooseFileDialog(new JFrame(), new File(fileSystem.getConfigRoot()), true);
          VcsUtilities.centerWindow (chooseFile);
          chooseFile.show();
          String selected=chooseFile.getSelectedFile ();
          if(selected==null) return;
          if(!selected.endsWith(".properties")) { // NOI18N
            selected += ".properties"; // NOI18N
          }
          File f = new File (selected); 
          if( selected==null ){
            //D.deb("no directory selected"); // NOI18N
            return ;
          }
          String label = selected;
          label = f.getName ().substring (0, f.getName ().length() - ".properties".length()); // NOI18N
          Vector variables=fileSystem.getVariables ();
          Object advanced=fileSystem.getAdvancedConfig ();
          VcsConfigVariable.writeConfiguration (selected, label, variables, advanced, fileSystem.getVcsFactory ().getVcsAdvancedCustomizer ()); 
          promptForConfigComboChange = false;
          fileSystem.setConfig (label);
          updateConfigurations ();
        */
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void configComboItemStateChanged (java.awt.event.ItemEvent evt) {//GEN-FIRST:event_configComboItemStateChanged
        // Add your handling code here:

        switch( evt.getStateChange() ){
        case ItemEvent.SELECTED:
            String selectedLabel=(String)evt.getItem();
            updateVariables (selectedLabel);
            E.deb ("config state changed to:"+selectedLabel);
            if(selectedLabel.equalsIgnoreCase("empty")) { // NOI18N
                removeConfigButton.setEnabled (false);
                saveAsButton.setNextFocusableComponent (propsPanel);
            } else {
                removeConfigButton.setEnabled (true);
                saveAsButton.setNextFocusableComponent (removeConfigButton);
            }
            int selectedIndex=configCombo.getSelectedIndex();

            if( oldIndex==selectedIndex ){
                //D.deb("nothing has changed oldIndex==selectedIndex=="+oldIndex); // NOI18N
                return ;
            }

            String msg=g("MSG_Do_you_really_want_to_discard_current_commands",selectedLabel); // NOI18N
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation (msg, NotifyDescriptor.YES_NO_OPTION );
            if (!promptForConfigComboChange || TopManager.getDefault().notify( nd ).equals( NotifyDescriptor.YES_OPTION ) ) {
                //D.deb("yes"); // NOI18N
                // just do not display prompt for the first change if config was not edited
                promptForConfigComboChange = true;
                loadConfig(selectedLabel);
                oldIndex=selectedIndex;
            }
            else{
                //D.deb("no"); // NOI18N
                String oldLabel=(String)configCombo.getItemAt(oldIndex);
                //D.deb("oldLabel="+oldLabel+", oldIndex="+oldIndex); // NOI18N
                loadConfig(oldLabel);
                configCombo.setSelectedIndex(oldIndex);
            }
            break ;

        case ItemEvent.DESELECTED:
            break ;
        }
    }//GEN-LAST:event_configComboItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel configPanel;
    private javax.swing.JPanel vcsPanel;
    private javax.swing.JComboBox configCombo;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JButton removeConfigButton;
    private javax.swing.JPanel propsPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField rootDirTextField;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel relMountLabel;
    private javax.swing.JTextField relMountTextField;
    private javax.swing.JButton relMountButton;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField refreshTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel linkLabel;
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JCheckBox advancedModeCheckBox;
    private javax.swing.JCheckBox offLineCheckBox;
    private javax.swing.JCheckBox editCheckBox;
    private javax.swing.JCheckBox promptEditCheckBox;
    private javax.swing.JCheckBox lockCheckBox;
    private javax.swing.JCheckBox promptLockCheckBox;
    private javax.swing.JCheckBox debugCheckBox;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton cmdButton;
    private javax.swing.JButton varButton;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    // End of variables declaration//GEN-END:variables

    private static final double ADVANCED_DLG_WIDTH_RELATIVE = 0.9;
    private static final double ADVANCED_DLG_HEIGHT_RELATIVE = 0.6;

    private Vector varLabels = new Vector ();
    private Vector varTextFields = new Vector ();
    private Vector varButtons = new Vector();
    private Vector varVariables = new Vector ();
    private CommandLineVcsFileSystem fileSystem = null;
    private PropertyChangeSupport changeSupport = null;
    private Vector configLabels;
    private int oldIndex=0;
    private boolean promptForConfigComboChange = false;

    // Entries in hashtables are maintained as a cache of properties read from disk
    // and are read only. Changes are applied only to fileSystem.variables (fileSystem.commands).
    private Hashtable configVariablesByLabel;
    private Hashtable configAdvancedByLabel;
    private Hashtable configNamesByLabel;
    private boolean isRootNotSetDlg = true;
    
    private void setAutoFillVars(String autoFillVarsStr) {
        String[] varsCmds = VcsUtilities.getQuotedStrings(autoFillVarsStr);
        autoFillVars = new HashMap();
        for (int i = 0; (i + 1) < varsCmds.length; i += 2) {
            autoFillVars.put(varsCmds[i], varsCmds[i+1]);
        }
    }

    private void relMountPointChanged() {
        String module = relMountTextField.getText();
        try {
            fileSystem.setRelativeMountPoint(module);
        } catch (PropertyVetoException exc) {
            module = null;
        } catch (IOException ioexc) {
            module = null;
        }
        if (module == null) {
            relMountTextField.setText(fileSystem.getRelativeMountPoint());
        }
        String cmd = (String) autoFillVars.get("MODULE");
        if (cmd != null) autoFillVariables(cmd);
    }
    
    //-------------------------------------------
    private void loadConfig(String label){
        if(!label.equals (fileSystem.getConfig ())) {
            Vector variables = (Vector) configVariablesByLabel.get(label);
            Node commands = (Node) configAdvancedByLabel.get(label);
            fileSystem.setVariables(variables);
            fileSystem.setCommands(commands);
            fileSystem.setConfig(label);
            fileSystem.setConfigFileName((String) configNamesByLabel.get(label));
            String autoFillVarsStr = (String) fileSystem.getVariablesAsHashtable().get(VAR_AUTO_FILL);
            if (autoFillVarsStr != null) setAutoFillVars(autoFillVarsStr);
        }
        initAdditionalComponents ();
    }

    //-------------------------------------------
    public static void main(java.lang.String[] args) {
        JDialog dialog = new JDialog(new Frame (), true );
        VcsCustomizer customizer = new VcsCustomizer();
        dialog.getContentPane().add(customizer);
        dialog.pack ();
        dialog.show();
    }


    //-------------------------------------------
    public void addPropertyChangeListener(PropertyChangeListener l) {
        //D.deb("addPropertyChangeListener()"); // NOI18N
        changeSupport.addPropertyChangeListener(l);
    }


    //-------------------------------------------
    public void removePropertyChangeListener(PropertyChangeListener l) {
        //D.deb("removePropertyChangeListener()"); // NOI18N
        changeSupport.removePropertyChangeListener(l);
    }

    private void removeEnterFromKeymap() {
        VcsUtilities.removeEnterFromKeymap(rootDirTextField);
        VcsUtilities.removeEnterFromKeymap(refreshTextField);
    }

    //-------------------------------------------
    private void advancedConfiguration () {
        JPanel panel = new JPanel ();
        panel.setLayout (new java.awt.GridBagLayout ());

        java.awt.GridBagConstraints gridBagConstraints1;
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets (12, 12, 0, 11);
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1;
        gridBagConstraints1.weighty = 0.5;


        final UserVariablesEditor variableEditor= new UserVariablesEditor();
        variableEditor.setValue( fileSystem.getVariables() );
        UserVariablesPanel variablePanel = new UserVariablesPanel (variableEditor);
        panel.add (variablePanel, gridBagConstraints1);

        PropertyEditor advancedEditor = CommandLineVcsAdvancedCustomizer.getEditor (fileSystem);
        JPanel advancedPanel = CommandLineVcsAdvancedCustomizer.getPanel (advancedEditor);

        gridBagConstraints1.gridy = 1;
        panel.add (advancedPanel, gridBagConstraints1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.setSize((int) (screenSize.width*ADVANCED_DLG_WIDTH_RELATIVE),
                           variablePanel.getPreferredSize().height+advancedPanel.getPreferredSize().height+16);
        panel.setPreferredSize(screenSize);

        DialogDescriptor dd = new DialogDescriptor (panel, "Advanced Properties Editor");
        TopManager.getDefault ().createDialog (dd).show ();
        if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
            fileSystem.setVariables ((Vector) variableEditor.getValue ());
            fileSystem.setCommands ((Node) advancedEditor.getValue ());
        }
        initAdditionalComponents ();
    }

    private void initAdditionalComponents () {
        refreshTextField.setVisible (true);
        jLabel4.setVisible (true);
        /*
        if(!fileSystem.getCache ().isLocalFilesAdd ()) {
            refreshTextField.setVisible (false);
            jLabel4.setVisible (false);
        }
         */
        varVariables = new Vector ();
        while(varLabels.size ()>0) {
            propsPanel.remove ((JComponent) varLabels.get (0));
            propsPanel.remove ((JComponent) varTextFields.get (0));
            JComponent button = (JComponent) varButtons.get(0);
            if (button != null) propsPanel.remove (button);
            varLabels.remove (0);
            varTextFields.remove (0);
            varButtons.remove(0);
        }
        Enumeration vars = fileSystem.getVariables ().elements ();
        while (vars.hasMoreElements ()) {
            VcsConfigVariable var = (VcsConfigVariable) vars.nextElement ();
            if(var.isBasic ()) {
                JLabel lb;
                JTextField tf;
                JButton button = null;
                lb = new JLabel ();
                tf = new JTextField ();
                varLabels.add (lb);
                varTextFields.add (tf);

                java.awt.GridBagConstraints gridBagConstraints1;
                gridBagConstraints1 = new java.awt.GridBagConstraints ();
                gridBagConstraints1.gridx = 0;
                gridBagConstraints1.gridy = varLabels.size () + 4;
                gridBagConstraints1.insets = new java.awt.Insets (0, 0, 5, 12);
                gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints1.weightx = 0;
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
                gridBagConstraints1.insets = new java.awt.Insets (0, 0, 5, 5);
                gridBagConstraints1.weightx = 1;
                propsPanel.add (tf, gridBagConstraints1);
                varVariables.add (var);
                String varLabel = var.getLabel ().trim ();
                if(!varLabel.endsWith (":")) varLabel += ":"; // NOI18N
                lb.setText (varLabel);
                tf.setText (var.getValue ());
                if (var.isLocalFile ()) {
                    button = new JButton ();
                    button.addActionListener (new BrowseLocalFile (tf));
                    button.setText (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.browseButton.text"));
                } else if (var.isLocalDir ()) {
                    button = new JButton ();
                    button.addActionListener (new BrowseLocalDir (tf));
                    button.setText (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.browseButton.text"));
                }
                String selector = var.getCustomSelector();
                if (selector != null && selector.length() > 0) {
                    button = new JButton ();
                    button.addActionListener (new RunCustomSelector (tf, var));
                    button.setText (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.selectButton.text"));
                }
                if (button != null) {
                    gridBagConstraints1.gridx = 2;
                    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    gridBagConstraints1.weightx = 0;
                    gridBagConstraints1.insets = new java.awt.Insets (0, 0, 5, 0);
                    propsPanel.add (button, gridBagConstraints1);
                }
                varButtons.add (button);
                VcsUtilities.removeEnterFromKeymap(tf);
            }
        }
        java.awt.Component comp = this;
        while (comp!=null && !(comp instanceof java.awt.Window)) comp = comp.getParent ();
        if(comp!=null) {
            ((java.awt.Window) comp).pack ();
        }
        for (Iterator it = autoFillVars.values().iterator(); it.hasNext(); ) {
            String cmd = (String) it.next();
            autoFillVariables(cmd);
        }
        updateAdvancedConfig();
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
            var.setValue (tf.getText ().trim());
            if (var.getName().equals("MODULE")) { // NOI18N
                /*
                String value = var.getValue();
                if (value.length() > 0 && !value.endsWith(File.separator)) value = value.concat(File.separator);
                var.setValue(value);
                */
                rootDirChanged();
            }
            // enable fs to react on change in variables
            fileSystem.setVariables(fileSystem.getVariables());
            //D.deb("variableChanged(): filesystemVariables = "+fileSystem.getVariables()); // NOI18N
            String cmd = (String) autoFillVars.get(var.getName());
            if (cmd != null) autoFillVariables(cmd);
        } else {
            E.deb ("Error setting variable:"+tf.getText ());
        }
    }
    
    private void autoFillVariables(String cmdName) {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return ;
        Hashtable vars = fileSystem.getVariablesAsHashtable();
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        CommandsPool pool = fileSystem.getCommandsPool();
        pool.startExecutor(vce);
        pool.waitToFinish(vce);
        int len = varTextFields.size();
        for (int i = 0; i < len; i++) {
            VcsConfigVariable var = (VcsConfigVariable) varVariables.get(i);
            String value = (String) vars.get(var.getName());
            if (value != null) {
                JTextField field = (JTextField) varTextFields.get(i);
                field.setText(value);
                var.setValue(value);
            }
        }
        // enable fs to react on change in variables
        fileSystem.setVariables(fileSystem.getVariables());
    }

    /**
    * Read configurations from disk.
    */
    //-------------------------------------------
    private void updateConfigurations(){
        //D.deb("configRoot = "+fileSystem.getConfigRoot()); // NOI18N
        ArrayList configNames = VariableIO.readConfigurations(fileSystem.getConfigRootFO());
        //Vector configNames = VcsConfigVariable.readConfigurations(fileSystem.getConfigRootFO());
        D.deb("configNames="+configNames); // NOI18N

        if (configCombo.getItemCount() > 0) {
            configCombo.removeAllItems();
        }
        
        // Clear all current settings
        if (this.cache.size() >0)
            this.cache.clear();
        configLabels = new Vector();
        configVariablesByLabel = new Hashtable();
        configAdvancedByLabel = new Hashtable();
        configNamesByLabel = new Hashtable();


        String selectedConfig = fileSystem.getConfig();
        int newIndex = 0;

        for(int i = 0; i < configNames.size(); i++){
            String name = (String) configNames.get(i);
            if (CommandLineVcsFileSystem.TEMPORARY_CONFIG_FILE_NAME.equals(name)) continue;
            String label;
            if (name.endsWith(VariableIOCompat.CONFIG_FILE_EXT)) {
                Properties props = VariableIOCompat.readPredefinedProperties
                                  (fileSystem.getConfigRootFO(), name);
                //( fileSystem.getConfigRoot()+File.separator+name+".properties"); // NOI18N
                label = props.getProperty("label", g("CTL_No_label_configured"));
                this.cache.put (label,props);
            } else {
                org.w3c.dom.Document doc = VariableIO.readPredefinedConfigurations
                                          (fileSystem.getConfigRootFO(), name);
                if (doc == null) continue;
                try {
                    label = VariableIO.getConfigurationLabel(doc);
                    this.cache.put (label, doc);
                } catch (org.w3c.dom.DOMException exc) {
                    org.openide.TopManager.getDefault().notifyException(exc);
//                    variables = new Vector();
//                    advanced = null;
                    label = g("CTL_No_label_configured");
                }
            }
            
            configNamesByLabel.put(label,name);
            
            if (label == null) label = "";
            configLabels.addElement(label);
            if (label.equals(selectedConfig)) {
                newIndex = i;
            }
            //configCombo.addItem(label);
        }
        String[] sortedLabels = (String[]) configLabels.toArray(new String[0]);
        Arrays.sort(sortedLabels);
        configLabels = new Vector(Arrays.asList(sortedLabels));
        for(int i = 0; i < configLabels.size(); i++) {
            String label = (String) configLabels.elementAt(i);
            if( label.equals(selectedConfig) ){
                newIndex=i;
            }
            configCombo.addItem(label);
        }

        if (configCombo.getItemCount() > 0)
            configCombo.setSelectedIndex( newIndex );
        promptForConfigComboChange = false;
    }
    
    private void updateVariables (String label) {
        if (configVariablesByLabel.get(label) != null)
            return;         // Already done
        Object obj = this.cache.get (label);
        if (obj == null)
            throw new IllegalArgumentException (label);
        Vector variables;
        Object advanced;
        if (obj instanceof java.util.Properties) {
            java.util.Properties props = (java.util.Properties) obj;
            variables = VariableIOCompat.readVariables(props);
            advanced = CommandLineVcsAdvancedCustomizer.readConfig (props);
        }
        else {
            org.w3c.dom.Document doc = (org.w3c.dom.Document) obj;
            variables = VariableIO.readVariables(doc);
            advanced = CommandLineVcsAdvancedCustomizer.readConfig (doc);
        }
        configVariablesByLabel.put(label,variables);
        configAdvancedByLabel.put(label, advanced);
    }

    private void updateAdvancedConfig() {
        advancedModeCheckBox.setSelected(fileSystem.isExpertMode());
        debugCheckBox.setSelected(fileSystem.getDebug());
        editCheckBox.setSelected(fileSystem.isCallEditFilesOn());
        promptEditCheckBox.setSelected(fileSystem.isPromptForEditOn());
        promptEditCheckBox.setEnabled(editCheckBox.isSelected());
        lockCheckBox.setSelected(fileSystem.isLockFilesOn());
        promptLockCheckBox.setSelected(fileSystem.isPromptForLockOn());
        promptLockCheckBox.setEnabled(lockCheckBox.isSelected());
        offLineCheckBox.setSelected(fileSystem.isOffLine());
        boolean isEdit = fileSystem.isEnabledEditFiles();
        editCheckBox.setEnabled(isEdit);
        promptEditCheckBox.setEnabled(isEdit);
        boolean isLock = fileSystem.isEnabledLockFiles();
        lockCheckBox.setEnabled(isLock);
        promptLockCheckBox.setEnabled(isLock);
    }
    
    //-------------------------------------------
    public void setObject(Object bean){
        D.deb("setObject("+bean+")"); // NOI18N
        fileSystem=(CommandLineVcsFileSystem) bean;

        rootDirTextField.setText (VcsFileSystem.substractRootDir (fileSystem.getRootDirectory ().toString (), getModuleValue ()));
        refreshTextField.setText (""+fileSystem.getCustomRefreshTime ()); // NOI18N
        String module = getModuleValue();
        if (module == null) module = "";
        try {
            fileSystem.setRelativeMountPoint(module);
        } catch (PropertyVetoException exc) {
            module = "";
        } catch (IOException ioexc) {
            module = "";
        }
        relMountTextField.setText(module);
        updateConfigurations();
        updateAdvancedConfig();
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

    /**
     * Search for MODULE variable and return its value
     */
    private String getModuleValue() {
        Vector variables = fileSystem.getVariables();
        for(int i = 0; i < variables.size(); i++) {
            VcsConfigVariable var = (VcsConfigVariable) variables.get(i);
            if (var.getName().equals("MODULE")) return var.getValue(); // NOI18N
        }
        return null;
    }

    private void rootDirChanged () {
        // root dir set by hand
        String selected= rootDirTextField.getText ();
        if( selected==null ){
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        /*
        String module = getModuleValue();
        String moduleDir = selected;
        if (module != null && module.length() > 0) moduleDir += File.separator + module;
        D.deb("rootDirChanged(): module = "+module+", selected = "+selected); // NOI18N
        File dir=new File(moduleDir);
        */
        File root = new File(selected);
        /*
        if( !root.isDirectory() ){
          E.err("not directory "+root);
          final String badDir = root.toString();
          javax.swing.SwingUtilities.invokeLater(new Runnable () {
            public void run () {
              if (isRootNotSetDlg) {
                isRootNotSetDlg = false;
                TopManager.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.notDirectory"), new Object[] { badDir } )));
                isRootNotSetDlg = true;
              }
            }
          });
          return ;
    }
        */
        try{
            fileSystem.setRootDirectory(root);
            //rootDirTextField.setText(selected);
            String cmd = (String) autoFillVars.get("ROOTDIR");
            if (cmd != null) autoFillVariables(cmd);
        }
        catch (PropertyVetoException veto){
            fileSystem.debug(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.canNotChangeWD"));
            //E.err(veto,"setRootDirectory() failed"); // NOI18N
        }
        catch (IOException e){
            E.err(e,"setRootDirectory() failed");
            final String badDir = root.toString();
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           if (isRootNotSetDlg) {
                                                               isRootNotSetDlg = false;
                                                               TopManager.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.cannotSetDirectory"), new Object[] { badDir } )));
                                                               isRootNotSetDlg = true;
                                                           }
                                                       }
                                                   });
        }
    }

    private void refreshChanged () {
        try {
            int time = Integer.parseInt(refreshTextField.getText());
            if (time < 0) throw new NumberFormatException(""+time); // NOI18N
            fileSystem.setCustomRefreshTime (time);
            E.deb("refresh time set to:" + time);
        } catch (NumberFormatException e) {
            final String msg = e.getMessage();
            E.deb(e.getMessage());
            refreshTextField.setText (""+fileSystem.getCustomRefreshTime ()); // NOI18N
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           TopManager.getDefault ().notify (new NotifyDescriptor.Message("'"+msg+"': Non-negative integer value is expected."));
                                                       }
                                                   });
        }
    }

    //------------------------------
    private class BrowseLocalFile implements java.awt.event.ActionListener {

        private JTextField tf;

        public BrowseLocalFile(JTextField tf) {
            this.tf = tf;
        }

        public void actionPerformed (java.awt.event.ActionEvent evt) {
            ChooseFileDialog chooseFile=new ChooseFileDialog(new JFrame(), new File(tf.getText ()), false);
            VcsUtilities.centerWindow (chooseFile);
            chooseFile.show();
            String selected=chooseFile.getSelectedFile();
            if( selected==null ){
                //D.deb("no directory selected"); // NOI18N
                return ;
            }
            tf.setText(selected);
            variableChanged(new java.awt.event.ActionEvent(tf, 0, "")); // NOI18N
        }
    }

    //------------------------------
    private class BrowseLocalDir implements java.awt.event.ActionListener {

        private JTextField tf;

        public BrowseLocalDir(JTextField tf) {
            this.tf = tf;
        }

        public void actionPerformed (java.awt.event.ActionEvent evt) {
            ChooseDirDialog chooseDir = new ChooseDirDialog(new JFrame(), new File(tf.getText ()));
            VcsUtilities.centerWindow (chooseDir);
            chooseDir.show();
            String selected=chooseDir.getSelectedDir();
            if( selected==null ){
                //D.deb("no directory selected"); // NOI18N
                return ;
            }
            tf.setText(selected);
            variableChanged(new java.awt.event.ActionEvent(tf, 0, "")); // NOI18N
        }
    }

    private class RunCustomSelector implements java.awt.event.ActionListener {

        private JTextField tf;
        private VcsConfigVariable selector;

        public RunCustomSelector(JTextField tf, VcsConfigVariable selector) {
            this.tf = tf;
            this.selector = selector;
        }

        public void actionPerformed (java.awt.event.ActionEvent evt) {
            VcsCommand cmd = fileSystem.getCommand(selector.getCustomSelector());
            if (cmd == null) {
                NotifyDescriptor.Message nd = new NotifyDescriptor.Message (g("DLG_SelectorNotExist", selector.getCustomSelector()), NotifyDescriptor.WARNING_MESSAGE);
                TopManager.getDefault ().notify (nd);
                return ;
            }
            final Hashtable vars = fileSystem.getVariablesAsHashtable();
            VcsCommandExecutor executor = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            final CommandsPool pool = fileSystem.getCommandsPool();
            final StringBuffer selection = new StringBuffer();
            pool.addCommandListener(new CommandListener() {
                public void commandStarted(VcsCommandExecutor vce) {
                }
                
                public void commandDone(VcsCommandExecutor vce) {
                    if (selection.length() > 0) {
                        tf.setText(selection.toString());
                        variableChanged(new java.awt.event.ActionEvent(tf, 0, "")); // NOI18N
                    }
                    pool.removeCommandListener(this);
                }
            });
            executor.addDataOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] elements) {
                    if (elements.length > 0) {
                        selection.append(elements[0]);
                    }
                }
            });
            pool.startExecutor(executor);
        }
    }

    //-------------------------------------------
    String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcs.advanced.Bundle").getString (s);
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

