
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */



package com.sun.tthub.gde.ui.panels;
import com.sun.tthub.gdelib.InvalidArgumentException;
import com.sun.tthub.gdelib.fields.ComplexEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.ui.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.openide.WizardValidationException;

/**
 *
 * @author  Hareesh Ravindran
 */
public class ComplexEntryAttrJPanel extends javax.swing.JPanel {
    
    private FieldInfo fieldInfo;
    private ComplexEntryFieldDisplayInfo fieldDisplayInfo;
    
    /** Creates new form ComplexEntryAttrJPanel */
    public ComplexEntryAttrJPanel() {
        initComponents();
    }    
    
    public void setFieldInfo(FieldInfo fieldInfo) {
        if(fieldInfo == null) {
            throw new InvalidArgumentException("The fieldInfo passed to the " +
                    "setFieldInfo cannot be null.");
        }
        this.fieldInfo = fieldInfo;
        this.fieldDisplayInfo = (ComplexEntryFieldDisplayInfo) 
                        fieldInfo.getFieldDisplayInfo();
        fillDisplayControlInfo();
    }
    
    public void fillDisplayControlInfo() {
        if(fieldDisplayInfo.getUsePopupWindow()) {
            txtPopupFileName.setText(fieldDisplayInfo.getPopupFileName());
            clearTree();
            optCustomPopup.setSelected(true);
        } else {
            clearTree();
            // create the tree root.
            FieldInfoJTreeUtil.paintComplexNode((DefaultTreeModel)
                            treeDisplayParams.getModel(), null, fieldInfo);
            treeDisplayParams.expandRow(0);
            txtPopupFileName.setText("");
            optDefineDisplayParams.setSelected(true);
        }
    }
    
    /**
     * Use this function to retrieve the modified display control attribute
     * at any time.
     */
    public FieldDisplayInfo getFieldDisplayInfo() {
        return this.fieldDisplayInfo;
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        btnGrpComplexEntry = new javax.swing.ButtonGroup();
        optCustomPopup = new javax.swing.JRadioButton();
        txtPopupFileName = new javax.swing.JTextField();
        btnChoosePopupFile = new javax.swing.JButton();
        optDefineDisplayParams = new javax.swing.JRadioButton();
        btnDefineDisplayParams = new javax.swing.JButton();
        scrlPaneDisplayParams = new javax.swing.JScrollPane();
        treeDisplayParams = new javax.swing.JTree();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnGrpComplexEntry.add(optCustomPopup);
        optCustomPopup.setText("Choose Popup File:");
        optCustomPopup.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optCustomPopup.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        optCustomPopup.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        optCustomPopup.setMargin(new java.awt.Insets(0, 0, 0, 0));
        optCustomPopup.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                optCustomPopupItemStateChanged(evt);
            }
        });

        txtPopupFileName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtPopupFileName.setEnabled(false);

        btnChoosePopupFile.setText("...");
        btnChoosePopupFile.setEnabled(false);
        btnChoosePopupFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoosePopupFileActionPerformed(evt);
            }
        });

        btnGrpComplexEntry.add(optDefineDisplayParams);
        optDefineDisplayParams.setText("Define Display Params:");
        optDefineDisplayParams.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optDefineDisplayParams.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        optDefineDisplayParams.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        optDefineDisplayParams.setMargin(new java.awt.Insets(0, 0, 0, 0));
        optDefineDisplayParams.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                optDefineDisplayParamsItemStateChanged(evt);
            }
        });

        btnDefineDisplayParams.setText("...");
        btnDefineDisplayParams.setEnabled(false);
        btnDefineDisplayParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefineDisplayParamsActionPerformed(evt);
            }
        });

        treeDisplayParams.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scrlPaneDisplayParams.setViewportView(treeDisplayParams);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(optDefineDisplayParams))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(optCustomPopup)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(scrlPaneDisplayParams, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                            .add(txtPopupFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))))
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnChoosePopupFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnDefineDisplayParams, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnChoosePopupFile, btnDefineDisplayParams}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {optCustomPopup, optDefineDisplayParams}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnChoosePopupFile)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(optCustomPopup)
                        .add(txtPopupFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(btnDefineDisplayParams, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(optDefineDisplayParams))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(scrlPaneDisplayParams, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {btnChoosePopupFile, optCustomPopup, optDefineDisplayParams, txtPopupFileName}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }
    // </editor-fold>//GEN-END:initComponents

    private void btnDefineDisplayParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefineDisplayParamsActionPerformed
        MainWizardUI ui = (MainWizardUI) 
                    GDEAppContext.getInstance().getWizardUI();        
        Map fieldInfoMap = fieldDisplayInfo.getFieldInfoMap();        
        ComplexEntryAttrJDailog dialog = new ComplexEntryAttrJDailog(
                            ui.getDialog(), fieldInfoMap);
        dialog.setLocationRelativeTo(ui.getDialog());
        dialog.setVisible(true);
        Map map = dialog.getFieldInfoMap();
        // reset the existing map with the new map obtained from the dialog.
        fieldDisplayInfo.setFieldInfoMap(map);        
        fillDisplayControlInfo();        
    }//GEN-LAST:event_btnDefineDisplayParamsActionPerformed

    /**
     * When the button is clicked, it pops up the filechooser which allows the
     * user to choose the jsp file to be included while packaging the portlet
     * war file.
     */
    private void btnChoosePopupFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoosePopupFileActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        // Disable all file filters.
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Disable the multiselection feature
        fileChooser.setMultiSelectionEnabled(false); 
        //Set the filter so that the file chooser will display only the jsp files.
        fileChooser.setFileFilter(new CustomFileFilter("All Folders", true));
        
        int retVal = fileChooser.showOpenDialog(this); // Show the 'Open File' dialog.
        if(retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fieldDisplayInfo.setPopupFileName(file.getPath());
        }        
    }//GEN-LAST:event_btnChoosePopupFileActionPerformed

    /**
     * This event is fired whenever the button is selected or deselected.
     * Depending on the state of the button, the text area that displays the 
     * display param details and the button to define the display parameters
     * will be enabled/disabled. This event handler and the event handler
     * associated with the optCustomPopup radio button will determine the state
     * of the controls.
     */
    private void optDefineDisplayParamsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_optDefineDisplayParamsItemStateChanged
        int curState = evt.getStateChange();
        if(curState == ItemEvent.SELECTED) {
            treeDisplayParams.setEnabled(true);
            btnDefineDisplayParams.setEnabled(true);
        } else {
            clearTree();
            treeDisplayParams.setEnabled(false);
            btnDefineDisplayParams.setEnabled(false);
        }        
    }//GEN-LAST:event_optDefineDisplayParamsItemStateChanged

    private void clearTree() {
        DefaultTreeModel model = (DefaultTreeModel) treeDisplayParams.getModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
        node.removeAllChildren();
        node.setUserObject("Custom Popup File specified.");
    }
    
    /**
     * This event is fired whenever the button is selected or deselected.
     * Depending on the state of the button, the textfield to enter the
     * file name and the button to choose the popup file will be enabled/disabled.
     * This event handler and the event handler associated with the 
     * optDefineDisplayParams radio button together will determine the state of 
     * the controls.
     */
    private void optCustomPopupItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_optCustomPopupItemStateChanged
        int curState = evt.getStateChange();
        if(curState == ItemEvent.SELECTED) {
            txtPopupFileName.setEnabled(true);
            btnChoosePopupFile.setEnabled(true);
            fieldDisplayInfo.setUsePopupWindow(true);
        } else { // if the state of the button is 'DESELECTED'
            txtPopupFileName.setText(""); // clear the text in the text field
            txtPopupFileName.setEnabled(false);
            btnChoosePopupFile.setEnabled(false);
            fieldDisplayInfo.setUsePopupWindow(false);            
        }
    }//GEN-LAST:event_optCustomPopupItemStateChanged

    
    /**
     * This function can be called to reset the states of the controls in this
     * panel. This function sets the setSelected method of the radio buttons to
     * false. The item state change events associated with the buttons will 
     * perform the additional steps required.
     *
     */
    public void clearComponentStates() {    
        this.optCustomPopup.setSelected(false); 
        this.optDefineDisplayParams.setSelected(false);
    }
    
    /**
     * This function will be invoked by the wizard to check the validity of the
     * data entered.  The function will be invoked from the validate method
     * of the main wizard panel. If this function throws a wizard validation
     * exception, it will be propogated back to the validate method so that the
     * 'Next' and the 'Previous' buttons in the wizard will not change the page
     * till the user corrects the issue.
     */
    public void validateUserEntry() throws WizardValidationException {        
        // Check if one of the radio buttons is selected. If not throw exception.
        if(!optCustomPopup.isSelected() && 
                        !optDefineDisplayParams.isSelected()) {
            throw new WizardValidationException(this.optCustomPopup, 
                    "Either the popup file name or the field parameters should" +
                    "be specified", "Select one of the radio buttons");            
        }
        
        // Check if the radio button for custom popup is selected. Throw an 
        // exception if the user has not specified a file name or has specified
        // the name of a file that does not exist or for which the user does 
        // not have read permissions.
        if(optCustomPopup.isSelected()) {
            String str = txtPopupFileName.getText();
            if(str == null || str.trim().equals("")) {
                throw new WizardValidationException(txtPopupFileName, 
                        "A valid Popup file name should be specified.",
                        "Popup File name cannot be null");
            }
            // Check if the file exists and is readable by the user executing
            // the plugin.
            File file = new File(str);
            if(!file.exists()) {
                throw new WizardValidationException(txtPopupFileName, 
                        "The specified file '" + str + "' either does not " +
                        "exist or is not readable.", 
                        "Specify a valid popup file name");
            }
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnChoosePopupFile;
    public javax.swing.JButton btnDefineDisplayParams;
    public javax.swing.ButtonGroup btnGrpComplexEntry;
    public javax.swing.JRadioButton optCustomPopup;
    public javax.swing.JRadioButton optDefineDisplayParams;
    public javax.swing.JScrollPane scrlPaneDisplayParams;
    public javax.swing.JTree treeDisplayParams;
    public javax.swing.JTextField txtPopupFileName;
    // End of variables declaration//GEN-END:variables
    
}