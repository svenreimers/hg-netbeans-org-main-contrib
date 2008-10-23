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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import org.netbeans.modules.portalpack.servers.core.WizardPropertyReader;
import org.netbeans.modules.portalpack.servers.core.api.ConfigPanel;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.Util;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.xml.sax.SAXParseException;

/**
 *
 * @author  satya
 */
public class SunAppServerConfigPanel extends ConfigPanel implements SunAppServerConstants, DocumentListener{
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    
    /** Creates new form SunAppServerConfigPanel */
    public SunAppServerConfigPanel() {
        initComponents();
        initData();
        userNameTf.getDocument().addDocumentListener(this);
        portTf.getDocument().addDocumentListener(this);
        adminPortTf.getDocument().addDocumentListener(this);
        homeTf.getDocument().addDocumentListener(this);
        domainDirTf.getDocument().addDocumentListener(this);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        homeTf = new javax.swing.JTextField();
        homeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        domainTf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        domainDirTf = new javax.swing.JTextField();
        domainDirButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        userNameTf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        passwordTf = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        adminPortTf = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        portTf = new javax.swing.JTextField();

        setFont(new java.awt.Font("Tahoma", 1, 11));

        jLabel1.setLabelFor(homeTf);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_APPSERVER_HOME")); // NOI18N

        homeTf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                homeTfFocusLost(evt);
            }
        });

        homeButton.setText("...");
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        jLabel2.setLabelFor(domainDirTf);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_DOMAIN_DIR")); // NOI18N

        jLabel3.setLabelFor(domainTf);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_DOMAIN")); // NOI18N

        domainDirTf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                domainDirTfFocusLost(evt);
            }
        });

        domainDirButton.setText("..");
        domainDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                domainDirButtonActionPerformed(evt);
            }
        });

        jLabel4.setLabelFor(userNameTf);
        jLabel4.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_USER_NAME")); // NOI18N

        userNameTf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                userNameTfFocusLost(evt);
            }
        });

        jLabel5.setLabelFor(passwordTf);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_PASSWORD")); // NOI18N

        jLabel6.setLabelFor(adminPortTf);
        jLabel6.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_ADMIN_PORT")); // NOI18N

        Document textDocOne1 = adminPortTf.getDocument();
        DocumentFilter filterOne1 =
        new org.netbeans.modules.portalpack.servers.core.ui.IntegerDocumentFilter();
        ((AbstractDocument)
            textDocOne1).setDocumentFilter(filterOne1);
        adminPortTf.setDocument(textDocOne1);

        jLabel7.setLabelFor(portTf);
        jLabel7.setText(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_PORT")); // NOI18N

        Document textDocOne = portTf.getDocument();
        DocumentFilter filterOne =
        new org.netbeans.modules.portalpack.servers.core.ui.IntegerDocumentFilter();
        ((AbstractDocument)
            textDocOne).setDocumentFilter(filterOne);
        portTf.setDocument(textDocOne);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(jLabel2)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(jLabel7)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, portTf)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, adminPortTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, domainDirTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, homeTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, userNameTf)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, domainTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, passwordTf)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(homeButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                            .add(domainDirButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                        .add(20, 20, 20)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(homeTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(homeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(domainDirTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(domainTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(userNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel5)
                            .add(passwordTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(portTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6)
                            .add(adminPortTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(domainDirButton))
                .addContainerGap())
        );

        jLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_APPSERVER_HOME")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void userNameTfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userNameTfFocusLost
// TODO add your handling code here:
        fireChangeEvent();
    }//GEN-LAST:event_userNameTfFocusLost

    private void homeTfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_homeTfFocusLost
        _performHomeTfFocusLost();
    }//GEN-LAST:event_homeTfFocusLost

    private void _performHomeTfFocusLost() {
// TODO add your handling code here:
        String home = homeTf.getText();
        String domain = domainTf.getText();
        if(domain == null || domain.trim().length() == 0)
        {
            if(home == null || home.trim().length() == 0)
            {
                //do nothing
            }
            else{
                if(new File(home + File.separator + "domains" + File.separator + "domain1").exists())
                {
                    domainDirTf.setText(home + File.separator + "domains" + File.separator + "domain1");
                    populateAllDefaultValues();
                }
            }
        }else
            fireChangeEvent();
    }

    public void initData()
    {
        userNameTf.setText("admin");
        domainTf.setEnabled(false);    }
    private void domainDirTfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_domainDirTfFocusLost
// TODO add your handling code here:
        populateAllDefaultValues();
    }//GEN-LAST:event_domainDirTfFocusLost

    private void domainDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_domainDirButtonActionPerformed
// TODO add your handling code here:
        
        String home = homeTf.getText();
        String domainDir = domainDirTf.getText();
        
        String defaultDir = "";
        if(domainDir == null || domainDir.trim().length() == 0)
        {
            if(home == null || home.trim().length() == 0)
                defaultDir = System.getProperty("user.home");
            else{
                if(new File(home + File.separator + "domains" + File.separator + "domain1").exists())
                {
                    defaultDir = home + File.separator + "domains" + File.separator + "domain1";
                }else if(new File(home,"domains").exists())
                    defaultDir = home + File.separator + "domains";
                else{
                    if(new File(home).exists())
                       defaultDir = home;
                    else 
                       defaultDir = System.getProperty("user.home"); 
                }
            }
        }
        else{
            if(new File(domainDir).exists())
                defaultDir = domainDir;
            else
                defaultDir = System.getProperty("user.home"); 
        }
        String dir = browseInstallLocation(defaultDir);
        if(dir != null)
            domainDirTf.setText(dir);
        
        populateAllDefaultValues();
    }//GEN-LAST:event_domainDirButtonActionPerformed

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed
// TODO add your handling code here:
        
        String domainDir = domainDirTf.getText();
        
        String home = homeTf.getText();
        String defaultDir = "";
        if(home == null || home.trim().length() == 0)
        {
            if(domainDir == null || domainDir.trim().length() == 0)
                defaultDir = System.getProperty("user.home");
            else
            {
                if(new File(domainDir).exists())
                    defaultDir = domainDir;
                else
                    defaultDir = System.getProperty("user.home");
            }
        }
        else{
            if(new File(home).exists())
                defaultDir = home;
            else
                defaultDir = System.getProperty("user.home");
        }
        String dir = browseInstallLocation(defaultDir);
        if(dir != null)
            homeTf.setText(dir);
        _performHomeTfFocusLost();
    }//GEN-LAST:event_homeButtonActionPerformed

    
    private void populateAllDefaultValues()
    {
        String domainDir = domainDirTf.getText();
        SunAppConfigUtil configUtil = null;
        try {
            configUtil = new SunAppConfigUtil(new File(domainDir));
        } catch (SAXParseException ex) {      
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_DOMAIN_XML"));
            fireChangeEvent();
            clearDomainXmlData();
            return;
        } catch (IOException ex) {
            
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_DOMAIN_DIR"));
            fireChangeEvent();
            clearDomainXmlData();
            return;
        }catch(SunAppConfigUtil.ReadAccessDeniedException e){
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"READ_ACCESS_DENIED"));
            fireChangeEvent();
            clearDomainXmlData();
            return;
            
        }catch(Exception e){
            
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_DOMAIN_DIR"));
            clearDomainXmlData();
            fireChangeEvent();
            return;
        }
         
         setErrorMessage("");
        String port = configUtil.getPort();
        portTf.setText(port);
        String adminPort =  configUtil.getAdminPort();
        adminPortTf.setText(adminPort);
        domainTf.setText(configUtil.getDomainName());
        fireChangeEvent();
    }
    
    private void clearDomainXmlData()
    {
        portTf.setText("");
        adminPortTf.setText("");
        domainTf.setText("");
       
    }
    public void populateDataForCustomizer(PSConfigObject object) {
        
        homeTf.setText(object.getServerHome());
        domainDirTf.setText(object.getDomainDir());
        adminPortTf.setText(object.getAdminPort());
        domainTf.setText(object.getDefaultDomain());
        userNameTf.setText(object.getProperty(SERVER_USER));
        passwordTf.setText(object.getProperty(SERVER_PASSWORD));
        portTf.setText(object.getPort());
        
        //disable fields for editing
        homeTf.setEnabled(false);
        domainDirTf.setEnabled(false);
        adminPortTf.setEnabled(false);
        domainTf.setEnabled(false);
        portTf.setEnabled(false);
        homeButton.setEnabled(false);
        domainDirButton.setEnabled(false);
        
    }

    public void read(WizardDescriptor wizardDescriptor) {
     
    }

    public void store(WizardDescriptor d) {
        
        WizardPropertyReader wr = new WizardPropertyReader(d);
        wr.setServerHome(homeTf.getText());
        wr.setDomainDir(domainDirTf.getText());
        wr.setAdminPort(adminPortTf.getText());
        wr.setDefaultDomain(domainTf.getText());
        wr.setProperty(SERVER_USER,userNameTf.getText());
        wr.setProperty(SERVER_PASSWORD,new String(passwordTf.getPassword()));
        wr.setPort(portTf.getText());
    }

    public boolean validate(Object wizardDescriptor) {
        
        String home = homeTf.getText();
        String ext = "";
        if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        if(!new File(home,"bin" + File.separator + "asadmin"+ext).exists())
        {
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_HOME"));
            return false;
        }
       
        String domainDir = domainDirTf.getText();
        SunAppConfigUtil configUtil = null;
        try {
            configUtil = new SunAppConfigUtil(new File(domainDir));
        } catch (SAXParseException ex) {
            
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_DOMAIN_XML"));
            clearDomainXmlData();
            return false;
        } catch (IOException ex) {
            
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_DOMAIN_DIR"));
            clearDomainXmlData();
            return false;
        } catch(SunAppConfigUtil.ReadAccessDeniedException e){
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"READ_ACCESS_DENIED"));
            clearDomainXmlData();
            return false;
        }catch(Exception e){
            
            setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"INVALID_DOMAIN_DIR"));
            clearDomainXmlData();
            return false;
        }
         
         setErrorMessage("");
         
         if(!Util.isValidPort(portTf.getText()))
         {
             setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"MSG_INVALID_PORT"));
             return false;
         }
         
         if(!Util.isValidPort(adminPortTf.getText()))
         {
             setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"MSG_INVALID_ADMIN_PORT"));
             return false;
         }
         
         if(userNameTf.getText() == null || userNameTf.getText().trim().length() == 0)
         {
             setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"MSG_INVALID_ADMIN_USER"));
             return false;
         }
         if(portTf.getText() == null || portTf.getText().trim().length() == 0 
                || adminPortTf.getText() == null || adminPortTf.getText().trim().length() == 0
                || domainTf.getText() == null || domainTf.getText().trim().length() == 0)
         {
             setErrorMessage(NbBundle.getMessage(SunAppServerConfigPanel.class,"ENTER_VALID_PORT_ADMIN_PORT_DOMAIN"));
             return false;
         }
         setErrorMessage("");
        return true;
    }
    
     private String browseInstallLocation(String defaultDir){
        String insLocation = null;
        JFileChooser chooser = getJFileChooser(defaultDir);
        int returnValue = chooser.showDialog(SwingUtilities.getWindowAncestor(this),
                NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_BrowseButton")); //NOI18N
        
        if(returnValue == JFileChooser.APPROVE_OPTION){
            insLocation = chooser.getSelectedFile().getAbsolutePath();
        }
        return insLocation;
    }
    
    private JFileChooser getJFileChooser(String defaultDir){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_ChooserName")); //NOI18N
        chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonMnemonic("Choose_Button_Mnemonic".charAt(0)); //NOI18N
        chooser.setMultiSelectionEnabled(false);
        chooser.setApproveButtonToolTipText(NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_ChooserName")); //NOI18N

        chooser.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_ChooserName")); //NOI18N
        chooser.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SunAppServerConfigPanel.class, "LBL_ChooserName")); //NOI18N

        // set the current directory
        File file = new File(defaultDir);
        if(file != null)
            chooser.setSelectedFile(file);

        return chooser;
    }


    public String getDescription() {
        return "Sun Java System Application Server 9";
    }
    
    private String checkForNull(String txt) {
        if(txt == null)
            return "";
        else return txt.trim();
    }
    
    public void insertUpdate(DocumentEvent e) {
        fireChangeEvent();
    }

    public void removeUpdate(DocumentEvent e) {
        fireChangeEvent();
    }

    public void changedUpdate(DocumentEvent e) {
        fireChangeEvent();
    }
    
     class NullValueInputVerifier extends InputVerifier {
         public boolean verify(JComponent input) {
               JTextField tf = (JTextField) input;
               if(tf.getText().trim().length() == 0)
               {
                   fireChangeEvent();
                   return false;
               }
               return true;
         }
     }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField adminPortTf;
    private javax.swing.JButton domainDirButton;
    private javax.swing.JTextField domainDirTf;
    private javax.swing.JTextField domainTf;
    private javax.swing.JButton homeButton;
    private javax.swing.JTextField homeTf;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPasswordField passwordTf;
    private javax.swing.JTextField portTf;
    private javax.swing.JTextField userNameTf;
    // End of variables declaration//GEN-END:variables
    
}