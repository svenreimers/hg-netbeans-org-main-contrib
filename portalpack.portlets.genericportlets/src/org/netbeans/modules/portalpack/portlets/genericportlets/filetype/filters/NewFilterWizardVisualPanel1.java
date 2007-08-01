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

package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.FilterContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.CodeGenConstants;
import org.openide.WizardDescriptor;

public final class NewFilterWizardVisualPanel1 extends JPanel implements DocumentListener{
    
    private NewFilterWizardWizardPanel1 panel;
    /** Creates new form NewFilterWizardVisualPanel1 */
    public NewFilterWizardVisualPanel1(NewFilterWizardWizardPanel1 panel) {
        this.panel = panel;
        initComponents();
        initData();
        filterNameTxt.getDocument().addDocumentListener(this);
    }
    
    public String getName() {
        return "Step #1";
    }
    
    public void initData() {
        renderCB.setSelected(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        filterNameTxt = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        initParamTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        addParamButton = new javax.swing.JButton();
        deleteParamButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        renderCB = new javax.swing.JCheckBox();
        actionCB = new javax.swing.JCheckBox();
        eventCB = new javax.swing.JCheckBox();
        resourceCB = new javax.swing.JCheckBox();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Filter Name");

        initParamTableModel = new InitParamTableModel();
        initParamTable.setModel(initParamTableModel
        );
        jScrollPane2.setViewportView(initParamTable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Init Params");

        org.openide.awt.Mnemonics.setLocalizedText(addParamButton, "Add");
        addParamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addParamButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteParamButton, "Delete");
        deleteParamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteParamButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter Type"));
        jPanel1.setName("filterType"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(renderCB, "RENDER");
        renderCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        renderCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(actionCB, "ACTION");
        actionCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        actionCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(eventCB, "EVENT");
        eventCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        eventCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(resourceCB, "RESOURCE");
        resourceCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        resourceCB.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(renderCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(actionCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .add(39, 39, 39)
                .add(eventCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .add(33, 33, 33)
                .add(resourceCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(renderCB)
                    .add(resourceCB)
                    .add(actionCB)
                    .add(eventCB))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                        .add(jLabel2)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(filterNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 251, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 287, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                            .add(deleteParamButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(addParamButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)))
                        .add(11, 11, 11)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(filterNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jLabel3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(addParamButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(deleteParamButton))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void deleteParamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteParamButtonActionPerformed
    // TODO add your handling code here:
    int row = initParamTable.getSelectedRow();
    if(row != -1)
        initParamTableModel.deleteRow(row);
}//GEN-LAST:event_deleteParamButtonActionPerformed

private void addParamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addParamButtonActionPerformed
    // TODO add your handling code here:
    initParamTableModel.addRow();
}//GEN-LAST:event_addParamButtonActionPerformed

public void read(WizardDescriptor wizardDescriptor) {
    
}
public void store(WizardDescriptor wizardDescriptor) {
     
    FilterContext context = (FilterContext)wizardDescriptor.getProperty("context");
    
    if(context == null)
    {
        context = new FilterContext();
        wizardDescriptor.putProperty("context", context);
    }
    initParamTable.editCellAt(-1, -1);
    context.setFilterName(filterNameTxt.getText());
    context.setInitParams((InitParam [])initParamTableModel.getInitParams().toArray(new InitParam[0]));
    List filterTypes = new ArrayList();
    if(renderCB.isSelected())
        filterTypes.add(CodeGenConstants.RENDER_FILTER_TYPE);
    if(actionCB.isSelected())
        filterTypes.add(CodeGenConstants.ACTION_FILTER_TYPE);
    if(eventCB.isSelected())
        filterTypes.add(CodeGenConstants.EVENT_FILTER_TYPE);
    if(resourceCB.isSelected())
        filterTypes.add(CodeGenConstants.RESOURCE_FILTER_TYPE);
    context.setFilterType((String [])filterTypes.toArray(new String[0]));
        
}

public boolean valid(WizardDescriptor wd)
{
    if(wd == null)
        return true;
    String filterName = filterNameTxt.getText();
    if(filterName == null || filterName.trim().length() == 0)
    {
         wd.putProperty("WizardPanel_errorMessage",
                    "Invalid Filter Name");
            return false; 
    }else if(panel.getAvailableFilters().contains(filterName))
    {
        wd.putProperty("WizardPanel_errorMessage",
                    "A Filter with same name already present");
            return false; 
    }
    wd.putProperty("WizardPanel_errorMessage",
                    "");
    return true;
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox actionCB;
    private javax.swing.JButton addParamButton;
    private javax.swing.JButton deleteParamButton;
    private javax.swing.JCheckBox eventCB;
    private javax.swing.JTextField filterNameTxt;
    private javax.swing.JTable initParamTable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JCheckBox renderCB;
    private javax.swing.JCheckBox resourceCB;
    // End of variables declaration//GEN-END:variables
    private InitParamTableModel initParamTableModel;

    public void insertUpdate(DocumentEvent arg0) {
        panel.fireChangeEvent();
    }

    public void removeUpdate(DocumentEvent arg0) {
        panel.fireChangeEvent();
    }

    public void changedUpdate(DocumentEvent arg0) {
        panel.fireChangeEvent();
    }
    
    
}

