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

package org.netbeans.modules.helpbuilder.ui;

import org.netbeans.modules.helpbuilder.*;
import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.helpbuilder.processors.MapProcessor;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** 
 *
 * @author  Richard Gregor
 */
public class AddIndexPanel extends javax.swing.JPanel {    
    public static JButton OK_OPTION;
    public static JButton CANCEL_OPTION;
    //stores last path to folder choosen in filechooser
    private String folder;
    
    public AddIndexPanel() {        
        initComponents ();                
        setName(NbBundle.getMessage(AddPanel.class, "TITLE_AddPanel")); 
        OK_OPTION = new JButton(NbBundle.getMessage(AddPanel.class, "LBL_OK_OPTION"));
        OK_OPTION.setMnemonic(NbBundle.getMessage(AddPanel.class, "ACS_OK_OPTION_mnc").charAt(0));
        CANCEL_OPTION = new JButton(NbBundle.getMessage(AddPanel.class, "LBL_CANCEL_OPTION"));
        CANCEL_OPTION.setMnemonic(NbBundle.getMessage(AddPanel.class, "ACS_CANCEL_OPTION_mnc").charAt(0));
        OK_OPTION.setEnabled(false);
        txName.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            private void setOK_OPTION(){
                String name = txName.getText();
                String url = txtURL.getText();
                String map = txtMap.getText();
                if((name != null)&&(name.length() > 0))                    
                    OK_OPTION.setEnabled(true);
                else
                    OK_OPTION.setEnabled(false);
                
            }
        });
        txtMap.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            private void setOK_OPTION(){
                String map = txtMap.getText();
                if((map!= null) && (map.length() >0))
                    OK_OPTION.setEnabled(true);
                else
                    OK_OPTION.setEnabled(false);
            }
        });
        
        txtURL.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                setOK_OPTION();
            }
            private void setOK_OPTION(){
                String name = txName.getText();
                String url = txtURL.getText();
                String map = txtMap.getText();
                if((url != null) &&(url.length()>0)){
                    lblMap.setEnabled(true);
                    txtMap.setEnabled(true);
                    if((map == null)||(map.length()==0)){                        
                        txtMap.setText(MapProcessor.getDefault().getMapTarget(url));
                    }
                    if((name != null)&&(name.length() >0))
                        OK_OPTION.setEnabled(true);
                    else
                        OK_OPTION.setEnabled(false);
                }else{
                    lblMap.setEnabled(false);
                    txtMap.setEnabled(false);
                    if((name != null)&&(name.length() >0))
                        OK_OPTION.setEnabled(true);
                    else
                        OK_OPTION.setEnabled(false);
                }                                
            }
        });        
        
    }       

    public Object[] getOptions(){
        Object options[] = {OK_OPTION,CANCEL_OPTION};
        return options;
    }
    
    public void setName(String name){
        txName.setText(name);
    }
    
    public String getName(){
        return txName.getText();
    }
    
    public void setUrlSpec(String url){
        txtURL.setText(url);
    }
    
    public String getUrlSpec(){
        return txtURL.getText();
    }
    
    public String getMapTarget(){
        return txtMap.getText();
    }
    
    public void setMapTarget(String map){
        txtMap.setText(map);
    }
    
    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        lblURL = new javax.swing.JLabel();
        urlPanel = new javax.swing.JPanel();
        txtURL = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        lblMap = new javax.swing.JLabel();
        txtMap = new javax.swing.JTextField();
        topPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        setMinimumSize(new java.awt.Dimension(700, 417));
        setPreferredSize(new java.awt.Dimension(515, 150));
        centerPanel.setLayout(new java.awt.GridBagLayout());

        lblName.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_lblTitle_mnc").charAt(0));
        lblName.setLabelFor(txName);
        lblName.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("lblName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        centerPanel.add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        centerPanel.add(txName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        centerPanel.add(jPanel1, gridBagConstraints);

        lblURL.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_lblURL_mnc").charAt(0));
        lblURL.setLabelFor(txtURL);
        lblURL.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("lblURL"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        centerPanel.add(lblURL, gridBagConstraints);

        urlPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        urlPanel.add(txtURL, gridBagConstraints);

        btnBrowse.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnBrowse_mnc").charAt(0));
        btnBrowse.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("LBL_btnBrowse"));
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        urlPanel.add(btnBrowse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        centerPanel.add(urlPanel, gridBagConstraints);

        lblMap.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_lblMap_mnc").charAt(0));
        lblMap.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("lblProjectSetupPanel_mapLabel"));
        lblMap.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        centerPanel.add(lblMap, gridBagConstraints);

        txtMap.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 12);
        centerPanel.add(txtMap, gridBagConstraints);

        add(centerPanel, java.awt.BorderLayout.CENTER);

        add(topPanel, java.awt.BorderLayout.NORTH);

        add(bottomPanel, java.awt.BorderLayout.SOUTH);

        add(rightPanel, java.awt.BorderLayout.EAST);

        leftPanel.setLayout(new java.awt.GridBagLayout());

        add(leftPanel, java.awt.BorderLayout.WEST);

    }//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        JFileChooser chooser = new JFileChooser(folder);
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if(file != null){
                txtURL.setText(file.getAbsolutePath());
                folder = file.getParent();
            }
            
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    public void clear(){        
        txName.setText("");
        txtURL.setText("");
        txtMap.setText("");
    }
        
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblMap;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblURL;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txName;
    private javax.swing.JTextField txtMap;
    private javax.swing.JTextField txtURL;
    private javax.swing.JPanel urlPanel;
    // End of variables declaration//GEN-END:variables



}
