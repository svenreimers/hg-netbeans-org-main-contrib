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

package org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.jsr168;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * 
 * @author Satyaranjan
 */
    public class PortletApplicationPanelVisual extends JPanel implements DocumentListener {
   
    public static final String PROP_PROJECT_NAME = "projectName";
    
    private PortletApplicationWizardPanel panel;
    private WebModule wm;
    private ExtenderController controller;
    private int type;
    
    /** Creates new form PanelProjectLocationVisual */
    public PortletApplicationPanelVisual(PortletApplicationWizardPanel panel,WebModule wm,ExtenderController controller) {
        initComponents();
        this.panel = panel;
        this.type = type;
        this.wm = wm;
        this.controller = controller;
        initData();
        
        portletClassNameTf.getDocument().addDocumentListener(this);
        portletNameTf.getDocument().addDocumentListener(this);
        portletTitleTf.getDocument().addDocumentListener(this);
        portletDescTf.getDocument().addDocumentListener(this);
        portletDisplayNameTf.getDocument().addDocumentListener(this);
        portletShortTitleTf.getDocument().addDocumentListener(this);
        pkgTf.getDocument().addDocumentListener(this);
    }

    void update() {
        viewCheckbox.setEnabled(false);
        enableCheckBoxes(false);
        srcCombo.setEditable(false);
        srcCombo.setEnabled(false);
       
        enableTextComponents(false);
    }
     
    private void initData()
    {       
        String prjName = null;
        if(wm != null)
        {         
            Project project = FileOwnerQuery.getOwner(wm.getDocumentBase());
            Sources sources = (Sources)project.getLookup().lookup(Sources.class);
            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
          
            for(int i=0;i<groups.length;i++)
                srcCombo.addItem(new CustomSourceGroup(groups[i]));
            
            //Initialize portlet name. This will most likely happen while adding framework later.
            prjName = ProjectUtils.getInformation(project).getName();
            
        }else{
            
            if(controller != null)
                prjName = (String) controller.getProperties().getProperty("name");
            
        }
        
        if(prjName != null){
            portletClassNameTf.setText(prjName);
            portletNameTf.setText(prjName);
            portletTitleTf.setText(prjName);
            portletDescTf.setText(prjName);
            portletDisplayNameTf.setText(prjName);
            portletShortTitleTf.setText(prjName);
        }
        
        portletVersion.removeAllItems();
        portletVersion.addItem(NetbeanConstants.PORTLET_1_0);
        portletVersion.addItem(NetbeanConstants.PORTLET_2_0);
        portletVersion.setSelectedItem(NetbeanConstants.PORTLET_2_0);
            
    }
    
    class CustomSourceGroup
    {  
        private SourceGroup srcGroup;
        public CustomSourceGroup(SourceGroup srcGroup) {
            this.srcGroup = srcGroup;
        }
        
        public String toString()
        {
            return srcGroup.getDisplayName();
        }
        
        public FileObject getRootFolder()
        {
            return srcGroup.getRootFolder();
                 
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        srcRootLbl = new javax.swing.JLabel();
        srcCombo = new javax.swing.JComboBox();
        portletClassNameTf = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        portletDisplayNameTf = new javax.swing.JTextField();
        portletDescTf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        portletTitleTf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        portletShortTitleTf = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pkgTf = new javax.swing.JTextField();
        portletNameTf = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        isCreatePortlet = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        viewCheckbox = new javax.swing.JCheckBox();
        editCheckbox = new javax.swing.JCheckBox();
        helpCheckbox = new javax.swing.JCheckBox();
        isCreateJsps = new javax.swing.JCheckBox();
        portletVersion = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();

        setAutoscrolls(true);

        srcRootLbl.setLabelFor(srcCombo);
        org.openide.awt.Mnemonics.setLocalizedText(srcRootLbl, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_SRC_ROOT")); // NOI18N

        srcCombo.setEnabled(false);

        portletClassNameTf.setText("HelloWorld");
        portletClassNameTf.setEnabled(false);

        jLabel1.setLabelFor(portletClassNameTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_CLASS_NAME")); // NOI18N

        jLabel2.setLabelFor(portletDisplayNameTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_DISPLAY_NAME")); // NOI18N

        portletDisplayNameTf.setText("HelloWorldPortlet");
        portletDisplayNameTf.setEnabled(false);

        portletDescTf.setText("HelloWorldPortlet");
        portletDescTf.setEnabled(false);

        jLabel3.setLabelFor(portletDescTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_DESCRIPTION")); // NOI18N

        jLabel4.setLabelFor(portletTitleTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_TITLE")); // NOI18N

        portletTitleTf.setText("HelloWorldPortlet");
        portletTitleTf.setEnabled(false);

        jLabel5.setLabelFor(portletShortTitleTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_SHORT_TITLE")); // NOI18N

        portletShortTitleTf.setText("HelloWorld");
        portletShortTitleTf.setEnabled(false);

        jLabel6.setLabelFor(pkgTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PACKAGE")); // NOI18N

        pkgTf.setText("com.test");
        pkgTf.setEnabled(false);

        portletNameTf.setText("HelloWorld");
        portletNameTf.setEnabled(false);

        jLabel7.setLabelFor(portletNameTf);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_NAME")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/portalpack/portlets/genericportlets/frameworks/jsr168/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(isCreatePortlet, bundle.getString("LBL_CREATE_PORTLET")); // NOI18N
        isCreatePortlet.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        isCreatePortlet.setMargin(new java.awt.Insets(0, 0, 0, 0));
        isCreatePortlet.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                isCreatePortletStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_MODE")); // NOI18N

        viewCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(viewCheckbox, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_VIEW")); // NOI18N
        viewCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        viewCheckbox.setEnabled(false);
        viewCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        editCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(editCheckbox, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_EDIT")); // NOI18N
        editCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        editCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        helpCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(helpCheckbox, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_HELP")); // NOI18N
        helpCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        helpCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        isCreateJsps.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(isCreateJsps, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_CREATE_JSPS")); // NOI18N
        isCreateJsps.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        isCreateJsps.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel9.setLabelFor(portletVersion);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_SPEC_VERSION")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .add(24, 24, 24))
                    .add(layout.createSequentialGroup()
                        .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(srcRootLbl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(portletClassNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(srcCombo, 0, 263, Short.MAX_VALUE)
                    .add(pkgTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(viewCheckbox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                        .add(73, 73, 73)
                        .add(editCheckbox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                        .add(61, 61, 61)
                        .add(helpCheckbox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                    .add(portletTitleTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(portletNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(portletVersion, 0, 58, Short.MAX_VALUE)
                        .add(20, 20, 20)
                        .add(isCreatePortlet, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                        .add(40, 40, 40)
                        .add(isCreateJsps, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
                .add(21, 21, 21))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(isCreateJsps)
                        .add(isCreatePortlet)
                        .add(portletVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(srcCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(srcRootLbl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(pkgTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(portletClassNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel4)
                    .add(portletTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(viewCheckbox)
                    .add(editCheckbox)
                    .add(helpCheckbox))
                .add(43, 43, 43))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void isCreatePortletStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_isCreatePortletStateChanged
// TODO add your handling code here:
        boolean selected = isCreatePortlet.isSelected();
        
        if(!selected){
        
            enableTextComponents(selected);
            enableCheckBoxes(false);
            srcCombo.setEnabled(false);
            srcCombo.setEditable(false);
        }else{
            
            enableTextComponents(selected);
            enableCheckBoxes(true);
            
            if(!panel.getCustomizer() && srcCombo.getModel().getSize() != 0)
                srcCombo.setEnabled(true);
        }
        
        panel.fireChangeEvent();
    }//GEN-LAST:event_isCreatePortletStateChanged
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox editCheckbox;
    private javax.swing.JCheckBox helpCheckbox;
    private javax.swing.JCheckBox isCreateJsps;
    private javax.swing.JCheckBox isCreatePortlet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField pkgTf;
    private javax.swing.JTextField portletClassNameTf;
    private javax.swing.JTextField portletDescTf;
    private javax.swing.JTextField portletDisplayNameTf;
    private javax.swing.JTextField portletNameTf;
    private javax.swing.JTextField portletShortTitleTf;
    private javax.swing.JTextField portletTitleTf;
    private javax.swing.JComboBox portletVersion;
    private javax.swing.JComboBox srcCombo;
    private javax.swing.JLabel srcRootLbl;
    private javax.swing.JCheckBox viewCheckbox;
    // End of variables declaration//GEN-END:variables
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        
    }
    
    boolean valid() {
        ExtenderController controller = panel.getController();
        if(controller == null) return true;
        boolean selected = isCreatePortlet.isSelected();
        if(!selected){
            controller.setErrorMessage(null);
            return true;
        }
        
        if(pkgTf.getText() == null || pkgTf.getText().trim().length() == 0)
        {
            
        } else{
            if(!CoreUtil.validatePackageName(pkgTf.getText()))
            {
                 controller.setErrorMessage(NbBundle.getMessage(PortletApplicationPanelVisual.class,"MSG_INVALID_PACKAGE_NAME"));
                 return false;
            }
        }
        if(portletNameTf.getText() == null || portletNameTf.getText().trim().length() == 0)
        {
              controller.setErrorMessage(NbBundle.getMessage(PortletApplicationPanelVisual.class,"MSG_PORTLET_NAME_CANNOT_BE_EMPTY")); 
              return false;
        }
        if(portletClassNameTf.getText() == null || portletClassNameTf.getText().trim().length() == 0)
        {
              controller.setErrorMessage(NbBundle.getMessage(PortletApplicationPanelVisual.class,"MSG_PORTLET_CLASS_CANNOT_BE_EMPTY")); 
              return false;
        }else if(!CoreUtil.validateJavaTypeName(portletClassNameTf.getText()))
        {
            controller.setErrorMessage(NbBundle.getMessage(PortletApplicationPanelVisual.class,"MSG_INVALID_CLASS")); 
              return false;
        }else if(!CoreUtil.validateString(portletNameTf.getText(),false))
        {
            
            controller.setErrorMessage(org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_NAME"));
            return false;
        }else if(!CoreUtil.validateString(portletTitleTf.getText(),true))
        {
            controller.setErrorMessage(org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_TITLE"));
            return false;
        }else if(portletShortTitleTf.getText() != null &&
                    portletShortTitleTf.getText().trim().length() != 0 &&
                    !CoreUtil.validateString(portletShortTitleTf.getText(),true))
        {
           controller.setErrorMessage(org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_SHORT_TITLE"));
            return false;
        }else if(!CoreUtil.validateXmlString(portletDisplayNameTf.getText().trim()))
        {
            controller.setErrorMessage(org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_DISPLAY_NAME"));
            return false;
        }else if(!CoreUtil.validateXmlString(portletDescTf.getText().trim()))
        {
            controller.setErrorMessage(org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_DESC"));
            return false;
        }
        controller.setErrorMessage(null);
        return true;
    }
   
    /**
     * 
     * @return 
     */
    public Map getData()
    {
        Map d = new HashMap();
        d.put("package",pkgTf.getText());
        if(isCreatePortlet.isSelected())
            d.put("generate_portlet","true");
        else
            d.put("generate_portlet","false");
        
        CustomSourceGroup custSourceGroup = (CustomSourceGroup)srcCombo.getSelectedItem();
        if(custSourceGroup != null)
            d.put("src_folder",custSourceGroup.getRootFolder());
  
        PortletContext context = new PortletContext();
        List modeList = new ArrayList();
            
            if(viewCheckbox.isSelected())
                modeList.add("VIEW");
            if(editCheckbox.isSelected())
                modeList.add("EDIT");
            if(helpCheckbox.isSelected())
                modeList.add("HELP");
            
        context.setModes((String [])modeList.toArray(new String[0]));
        
        context.setPortletClass(portletClassNameTf.getText().trim());
        context.setPortletName(portletNameTf.getText().trim());
        context.setPortletDescription(portletDescTf.getText().trim());
        context.setPortletDisplayName(portletDisplayNameTf.getText().trim());
        context.setPortletTitle(portletTitleTf.getText().trim());
        context.setPortletShortTitle(portletShortTitleTf.getText().trim());
        context.setHasJsps(isCreateJsps.isSelected());
        context.setPortletVersion((String)portletVersion.getSelectedItem());
        d.put("context", context);
        return d;
        
       
    }
    
    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
        
    }
    
    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        
        Document doc = e.getDocument();
        
         if (doc == portletClassNameTf.getDocument()) {
            // Change in the project name
            
            String portletClassName = portletClassNameTf.getText();
            portletNameTf.setText(portletClassName);
            portletDescTf.setText(portletClassName);
            portletDisplayNameTf.setText(portletClassName);
            portletTitleTf.setText(portletClassName);
            portletShortTitleTf.setText(portletClassName);   
            
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    private void enableCheckBoxes(boolean enable)
    {
        isCreateJsps.setEnabled(enable);
        editCheckbox.setEnabled(enable);
        helpCheckbox.setEnabled(enable);
        viewCheckbox.setEnabled(false);
        
    }
    private void enableTextComponents(boolean enable)
    {  
        portletClassNameTf.setEditable(enable);
        portletNameTf.setEditable(enable);
        portletTitleTf.setEditable(enable);
        portletDescTf.setEditable(enable);
        portletDisplayNameTf.setEditable(enable);
        portletShortTitleTf.setEditable(enable);
        pkgTf.setEditable(enable);
       
        portletClassNameTf.setEnabled(enable);
        portletNameTf.setEnabled(enable);
        portletTitleTf.setEnabled(enable);
        portletDescTf.setEnabled(enable);
        portletDisplayNameTf.setEnabled(enable);
        portletShortTitleTf.setEnabled(enable);
        pkgTf.setEnabled(enable);
      
    }
   
}
