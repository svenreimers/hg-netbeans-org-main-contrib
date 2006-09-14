/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70AddServerChoiceVisualPanel.java
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;
/**
 *
 * @author  Administrator
 */
public class WS70AddServerChoiceVisualPanel extends javax.swing.JPanel {
    private static final String LOCALHOST="localhost";//NOI18N
    private String installDirName;    
    private String hostName=LOCALHOST;
    private String portNumber;
    private String userName;
    private String password;
    private final List listeners = new ArrayList();
    
    /**
     * Creates new form WS70AddServerChoiceVisualPanel
     */
    public WS70AddServerChoiceVisualPanel() {
        initComponents();
    }
    public String getAdminUserName(){
        return jUserNameTxt.getText().trim();
    }
    public String getAdminPassword(){
        char[] password = jPasswordTxt.getPassword();
        return String.valueOf(password);
    }
    
    public String getAdminHost(){
        return jAdminHostTxt.getText().trim();        
    }
    public String getAdminPort(){
        return jAdminPortTxt.getText().trim();        
    }
    

    public String getServerLocation(){
        return jLocationTxt.getText().trim();
    }
    public boolean isLocalServer(){
        return !jCBRemote.isSelected();
    }
    public boolean isAdminOnSSL(){
        return jCBSSLPort.isSelected();
    }    
    public boolean isValid(WizardDescriptor wizard){        
        if(!validateDirctory()){
            jLocationTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
            return false;
        }        
        if(!validateAdminHost()){
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_HOSTNAME"));
            jAdminHostTxt.setFocusable(true);
            return false;
        }
        if(!validateAdminPort()){
            jUserNameTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_VALID_PORT"));
            return false;
        }
        if(!validateUserName()){
            jUserNameTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_USERNAME"));
            return false;
        }
        wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE, null);
        return true;
    }
    private boolean validateAdminHost(){
        if(hostName==null || hostName.length()<=0){            
            return false;
        }
        return true;
    }    
    private boolean validateAdminPort(){
        if(portNumber==null || portNumber.length()<=0){
            return false;
        }
        int port;
        try{
            port = Integer.parseInt(portNumber);
        }catch(NumberFormatException e){
            port = -1;
        }
        if(port<=0 || port>65535){            
            return false;
        }
        return true;
    }
    private boolean validateUserName(){
        if(userName==null || userName.length()<=0){
            return false;
        }
        return true;
    }        
    private boolean validateDirctory() {
        if(installDirName==null){
            return false;
        }
        if(installDirName.equals("")){
            return false;
        }
        
        if(!isValidServer(new File(installDirName))){
            
            // Bug# 84607 Just show the error at the bottom of the panel. 
            // Message box is only needed when user has selected a wrong 
            // directory name using the FileChooser
            
            //Util.showError(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
            jLocationTxt.requestFocusInWindow();
            jLocationTxt.selectAll();
            return false;
        }
        return true;
    }
    
    private boolean isValidServer(File dir){        
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().equals("admin-server"); //NO I18N
            }
        };
        File[] adminFolders = dir.listFiles( filter );
        if ( adminFolders == null || adminFolders.length == 0 ){
            return false;
        }
        
        File[] configFolders = adminFolders[0].listFiles( new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().equals("config");//NO I18N
            }
        } );

        if ( configFolders == null || configFolders.length == 0 ){
            return false;
        }
        
        File[] serverFiles = configFolders[0].listFiles( new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().equals("server.xml");//NO I18N
            }
        } );
        if ( serverFiles == null || serverFiles.length == 0 ){
            return false;
        }        
        return true;
    }
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public String getName(){
        return NbBundle.getMessage(WS70AddServerChoiceVisualPanel.class, "LBL_AddServerWizardTitle");
    }
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }    
    private void fireChange() {
        ChangeEvent event = new ChangeEvent(this);
        ArrayList tempList;

        synchronized(listeners) {
            tempList = new ArrayList(listeners);
        }

        Iterator iter = tempList.iterator();
        while (iter.hasNext())
            ((ChangeListener)iter.next()).stateChanged(event);
    }    

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jServerInstructionsLbl = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDirectoryLbl = new javax.swing.JLabel();
        jLocationTxt = new javax.swing.JTextField();
        jBrowseBtn = new javax.swing.JButton();
        jAdminHostLbl = new javax.swing.JLabel();
        jAdminHostTxt = new javax.swing.JTextField();
        jAdminPortLbl = new javax.swing.JLabel();
        jAdminPortTxt = new javax.swing.JTextField();
        jUserNameLbl = new javax.swing.JLabel();
        jUserNameTxt = new javax.swing.JTextField();
        jPasswordLbl = new javax.swing.JLabel();
        jPasswordTxt = new javax.swing.JPasswordField();
        jAdminInstructionsLbl = new javax.swing.JLabel();
        jCBRemote = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jCBSSLPort = new javax.swing.JCheckBox();

        jServerInstructionsLbl.setText(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("LBL_AddServerVisualPanelTitle"));
        jServerInstructionsLbl.getAccessibleContext().setAccessibleName(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_NAME_AddServerVisualPanelTitle"));
        jServerInstructionsLbl.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_DESC_AddServerVisualPanelTitle"));

        jDirectoryLbl.setDisplayedMnemonic(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_Inst_Dir_Mnem").charAt(0));
        jDirectoryLbl.setLabelFor(jLocationTxt);
        jDirectoryLbl.setText(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("LBL_AddServerVisualPanel_Directory"));
        jDirectoryLbl.getAccessibleContext().setAccessibleName(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_NAME_AddServerVisualPanel_Directory"));
        jDirectoryLbl.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_DESC_AddServerVisualPanel_Directory"));
        jDirectoryLbl.getAccessibleContext().setAccessibleParent(this);

        jLocationTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLocationTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jLocationTxtFocusLost(evt);
            }
        });

        jBrowseBtn.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Browse_Mnem").charAt(0));
        jBrowseBtn.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanel_Browse"));
        jBrowseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseBtnActionPerformed(evt);
            }
        });

        jBrowseBtn.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanel_Browse"));
        jBrowseBtn.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanel_Browse"));
        jBrowseBtn.getAccessibleContext().setAccessibleParent(this);

        jAdminHostLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Host_Mnem").charAt(0));
        jAdminHostLbl.setLabelFor(jAdminHostTxt);
        jAdminHostLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelHost"));
        jAdminHostLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelHost"));
        jAdminHostLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelHost"));
        jAdminHostLbl.getAccessibleContext().setAccessibleParent(this);

        jAdminHostTxt.setEditable(false);
        jAdminHostTxt.setText(LOCALHOST);
        jAdminHostTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jAdminHostTxtCaretUpdate(evt);
            }
        });

        jAdminPortLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Port_Mnem").charAt(0));
        jAdminPortLbl.setLabelFor(jAdminPortTxt);
        jAdminPortLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelPort"));
        jAdminPortLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelPort"));
        jAdminPortLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelPort"));
        jAdminPortLbl.getAccessibleContext().setAccessibleParent(this);

        jAdminPortTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jAdminPortTxtCaretUpdate(evt);
            }
        });

        jUserNameLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_UserName_Mnem").charAt(0));
        jUserNameLbl.setLabelFor(jUserNameTxt);
        jUserNameLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelUserName"));
        jUserNameLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelUserName"));
        jUserNameLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelUserName"));
        jUserNameLbl.getAccessibleContext().setAccessibleParent(this);

        jUserNameTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jUserNameTxtCaretUpdate(evt);
            }
        });

        jPasswordLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Password_Mnem").charAt(0));
        jPasswordLbl.setLabelFor(jPasswordTxt);
        jPasswordLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelPassword"));
        jPasswordLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelPassword"));
        jPasswordLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelPassword"));
        jPasswordLbl.getAccessibleContext().setAccessibleParent(this);

        jPasswordTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPasswordTxtFocusLost(evt);
            }
        });

        jAdminInstructionsLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelAdmin"));
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelAdmin"));
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelAdmin"));
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleParent(this);

        jCBRemote.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Remote_Mnem").charAt(0));
        jCBRemote.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_RegisterRemoteServer"));
        jCBRemote.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("ToolTip_RegisterRemoteServer"));
        jCBRemote.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCBRemote.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCBRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBRemoteActionPerformed(evt);
            }
        });
        jCBRemote.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCBRemoteStateChanged(evt);
            }
        });

        jCBSSLPort.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_SSL_Mnem").charAt(0));
        jCBSSLPort.setSelected(true);
        jCBSSLPort.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelSSLPort"));
        jCBSSLPort.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("Tooltip_AddServerVisualPanelSSLPort"));
        jCBSSLPort.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCBSSLPort.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCBSSLPort.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelSSLPort"));
        jCBSSLPort.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelSSLPort"));
        jCBSSLPort.getAccessibleContext().setAccessibleParent(this);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(36, 36, 36)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCBRemote)
                    .add(layout.createSequentialGroup()
                        .add(jAdminInstructionsLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPasswordLbl)
                            .add(jAdminHostLbl)
                            .add(jAdminPortLbl)
                            .add(jUserNameLbl))
                        .add(31, 31, 31)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPasswordTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                            .add(jUserNameTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                            .add(jAdminHostTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jAdminPortTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(25, 25, 25)
                                .add(jCBSSLPort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                .add(84, 84, 84))))
                    .add(layout.createSequentialGroup()
                        .add(jDirectoryLbl)
                        .add(16, 16, 16)
                        .add(jLocationTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jBrowseBtn))
                    .add(jServerInstructionsLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 350, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(jServerInstructionsLbl)
                .add(16, 16, 16)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLocationTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jDirectoryLbl)
                        .add(jBrowseBtn)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCBRemote)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jAdminInstructionsLbl)
                .add(16, 16, 16)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jAdminHostTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jAdminHostLbl))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jAdminPortLbl)
                    .add(jAdminPortTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jCBSSLPort))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jUserNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jUserNameLbl))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jPasswordLbl))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jPasswordTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCBRemoteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCBRemoteStateChanged
        if(!jCBRemote.isSelected()){
            jAdminHostTxt.setText(LOCALHOST);
            jAdminHostTxt.setEditable(false);            
        }else{
            if(!jAdminHostTxt.isEditable()){
                jAdminHostTxt.setEditable(true);                
            }
        }
    }//GEN-LAST:event_jCBRemoteStateChanged

    private void jCBRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBRemoteActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jCBRemoteActionPerformed

    private void jLocationTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationTxtFocusLost
        String dirName = jLocationTxt.getText().trim();
        if (dirName.equals(this.installDirName) ) {
            return;
        }
        installDirName = dirName;        
        fireChange();        
    }//GEN-LAST:event_jLocationTxtFocusLost

    private void jLocationTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationTxtFocusGained
        String dirName = jLocationTxt.getText().trim();
        if (dirName.equals(this.installDirName) ) {
            return;
        }
        installDirName = dirName;        
        fireChange();        
    }//GEN-LAST:event_jLocationTxtFocusGained

    private void jAdminHostTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jAdminHostTxtCaretUpdate
        String host = jAdminHostTxt.getText().trim();
        if (host.equals(this.hostName) ) {
            return;
        }
        hostName = host;
        fireChange();          
    }//GEN-LAST:event_jAdminHostTxtCaretUpdate

    private void jAdminPortTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jAdminPortTxtCaretUpdate
       String port = jAdminPortTxt.getText().trim();
        if (port.equals(this.portNumber)) {
            return;
        }
        portNumber = port;
        fireChange();            
    }//GEN-LAST:event_jAdminPortTxtCaretUpdate

    private void jUserNameTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jUserNameTxtCaretUpdate
        String uname = jUserNameTxt.getText().trim();
        if (uname.equals(this.userName) ) {
            return;
        }
        userName = uname;
        fireChange();
    }//GEN-LAST:event_jUserNameTxtCaretUpdate

    private void jPasswordTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordTxtFocusLost
        String passwd = String.copyValueOf(jPasswordTxt.getPassword());
        if (passwd.equals(this.password) ) {
            return;
        }
        password = passwd;
        fireChange();        
    }//GEN-LAST:event_jPasswordTxtFocusLost

    private void jPasswordTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordTxtFocusGained
        String passwd = String.copyValueOf(jPasswordTxt.getPassword());
        if (passwd.equals(this.password) ) {
            return;
        }
        password = passwd;
        fireChange();        
    }//GEN-LAST:event_jPasswordTxtFocusGained

    private void jBrowseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseBtnActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File installDir = null;
        if (installDir != null )
            chooser.setSelectedFile(installDir);
        boolean repeatchooser = true;
        while ( repeatchooser ) {
            repeatchooser = false;
            if ( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
                installDir = chooser.getSelectedFile();
                if (isValidServer(installDir)){
                    jLocationTxt.setText(installDir.getAbsolutePath());
                    installDirName = installDir.getAbsolutePath();
                } else {
                    Util.showError(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
                    jLocationTxt.setText( "" );  // NOI18N
                    installDir = null;
                    repeatchooser = true;                    
                }
            }
        }
    }//GEN-LAST:event_jBrowseBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jAdminHostLbl;
    private javax.swing.JTextField jAdminHostTxt;
    private javax.swing.JLabel jAdminInstructionsLbl;
    private javax.swing.JLabel jAdminPortLbl;
    private javax.swing.JTextField jAdminPortTxt;
    private javax.swing.JButton jBrowseBtn;
    private javax.swing.JCheckBox jCBRemote;
    private javax.swing.JCheckBox jCBSSLPort;
    private javax.swing.JLabel jDirectoryLbl;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jLocationTxt;
    private javax.swing.JLabel jPasswordLbl;
    private javax.swing.JPasswordField jPasswordTxt;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel jServerInstructionsLbl;
    private javax.swing.JLabel jUserNameLbl;
    private javax.swing.JTextField jUserNameTxt;
    // End of variables declaration//GEN-END:variables
    
}
