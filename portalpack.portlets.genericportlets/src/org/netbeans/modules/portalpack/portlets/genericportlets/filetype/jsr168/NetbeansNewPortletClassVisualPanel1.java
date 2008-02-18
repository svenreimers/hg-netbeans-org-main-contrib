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

package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.jsr168;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.openide.WizardDescriptor;
import javax.swing.JPanel;

public final class NetbeansNewPortletClassVisualPanel1 extends JPanel implements DocumentListener{
    
    private NetbeansNewPortletClassWizardPanel1 panel;
    /** Creates new form NetbeansNewPortletClassVisualPanel1 */
    public NetbeansNewPortletClassVisualPanel1(NetbeansNewPortletClassWizardPanel1 panel) {
        this.panel = panel;
        initComponents();
        pnameTf.getDocument().addDocumentListener(this);
        portletTitleTf.getDocument().addDocumentListener(this);
        portletShortTitleTf.getDocument().addDocumentListener(this);
        portletDisplayNameTf.getDocument().addDocumentListener(this);
        portletDescTf.getDocument().addDocumentListener(this);
    }
    
    public String getName() {
        return "Step #1";
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pnameTf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        portletDisplayNameTf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        portletDescTf = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        portletTitleTf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        portletShortTitleTf = new javax.swing.JTextField();
        viewCB = new javax.swing.JCheckBox();
        editCB = new javax.swing.JCheckBox();
        helpCB = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_PORTLET_NAME")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_PORTLET_DISPLAY_NAME")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_PORTLET_DESC")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_PORTLET_TITLE")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_PORTLET_SHORT_TITLE")); // NOI18N

        viewCB.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(viewCB, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_VIEW")); // NOI18N
        viewCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        viewCB.setEnabled(false);
        viewCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        editCB.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(editCB, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LB_EDIT")); // NOI18N
        editCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        editCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        helpCB.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(helpCB, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_HELP")); // NOI18N
        helpCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        helpCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "LBL_PORTLET_MODE")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel6))
                .add(48, 48, 48)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(viewCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 36, Short.MAX_VALUE)
                        .add(editCB)
                        .add(33, 33, 33)
                        .add(helpCB))
                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, portletTitleTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                .add(120, 120, 120))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(pnameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(portletTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(viewCB)
                    .add(editCB)
                    .add(jLabel6)
                    .add(helpCB))
                .addContainerGap(23, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox editCB;
    private javax.swing.JCheckBox helpCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField pnameTf;
    private javax.swing.JTextField portletDescTf;
    private javax.swing.JTextField portletDisplayNameTf;
    private javax.swing.JTextField portletShortTitleTf;
    private javax.swing.JTextField portletTitleTf;
    private javax.swing.JCheckBox viewCB;
    // End of variables declaration//GEN-END:variables
    
    
    
    boolean valid(WizardDescriptor wizardDescriptor)
    {
        String portalName = pnameTf.getText();
        if(!CoreUtil.validateString(portalName,false))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "MSG_INVALID_PORTLET_NAME"));
            return false; 
        }else if(panel.getAvailablePortlets().contains(portalName)){
             wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "MSG_PORTLET_ALREADY_PRESENT"));
            return false;
        }else if(!CoreUtil.validateString(portletTitleTf.getText(),true)){
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "MSG_INVALID_PORTLET_TITLE"));
            return false; 
        }else if(portletShortTitleTf.getText() != null &&
                    portletShortTitleTf.getText().trim().length() != 0 &&
                    !CoreUtil.validateString(portletShortTitleTf.getText(),true)){
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "MSG_INVALID_PORTLET_SHORT_TITLE"));
            return false; 
        }else if(!CoreUtil.validateXmlString(portletDisplayNameTf.getText().trim()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "MSG_INVALID_PORTLET_DISPLAY_NAME"));
            return false; 
        }else if(!CoreUtil.validateXmlString(portletDescTf.getText().trim()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(NetbeansNewPortletClassVisualPanel1.class, "MSG_INVALID_PORTLET_DESC"));
            return false; 
        }
        
        
        wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
        
        return true;
    }
    public void store(WizardDescriptor d) {
        
        String portalName =  pnameTf.getText();
        
        PortletContext context = (PortletContext)d.getProperty("context");
        
        if(context == null)
            context = new PortletContext();
        
        context.setPortletName(pnameTf.getText().trim());
        context.setPortletDescription(portletDescTf.getText().trim());
        context.setPortletDisplayName(portletDisplayNameTf.getText().trim());
        context.setPortletTitle(portletTitleTf.getText().trim());
        context.setPortletShortTitle(portletShortTitleTf.getText().trim());
        
        List modeList = new ArrayList();
            
            if(viewCB.isSelected())
                modeList.add("VIEW");
            if(editCB.isSelected())
                modeList.add("EDIT");
            if(helpCB.isSelected())
                modeList.add("HELP");
            
        context.setModes((String [])modeList.toArray(new String[0]));
            
        d.putProperty("context",context);
        
        
    }
    
    public void readSettings(WizardDescriptor settings)
    {
        
    }
        
    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
       // if (this.pnameTf.getDocument() == e.getDocument()) {
            //firePropertyChang
        //}
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
      //  if (this.pnameTf.getDocument() == e.getDocument()) {
            //firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
      //  }
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
      //  if (this.pnameTf.getDocument() == e.getDocument()) {
            //firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
      //  }
    }
    
    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        
        Document doc = e.getDocument();
        
        if (doc == pnameTf.getDocument()) {
            // Change in the project name
            
            String portletName = pnameTf.getText();
            portletDescTf.setText(portletName);
            portletDisplayNameTf.setText(portletName);
            portletTitleTf.setText(portletName);
            portletShortTitleTf.setText(portletName);
            
            
            
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
   
}

