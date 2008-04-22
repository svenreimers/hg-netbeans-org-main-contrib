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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.ui;

import javax.xml.namespace.QName;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  Satyaranjan
 */
public class AddEventPanel extends javax.swing.JDialog {
    
   // private QName qName;
   // private String name;
    private EventObject evtObject;
    /** Creates new form AddEventPanel */
    public AddEventPanel(java.awt.Frame parent,String title) {
        super(parent, true);
        initComponents();
        setTitle(title);
        qNameCB.setSelected(true);
        setLocation(parent.getX()+(parent.getWidth()-getWidth())/2,parent.getY()+(parent.getHeight()-getHeight())/2);
        setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        nameSpaceTf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        localPartTf = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        prefixTf = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        qNameCB = new javax.swing.JCheckBox();
        valueTypeLabel = new javax.swing.JLabel();
        valueType = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.title")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "LBL_NAMESPACE")); // NOI18N

        nameSpaceTf.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.nameSpaceTf.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "LBL_LOCAL_PART")); // NOI18N

        localPartTf.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.localPartTf.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "LBL_PREFIX")); // NOI18N

        prefixTf.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.prefixTf.text")); // NOI18N

        cancelButton.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "LBL_CANCEL")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "LBL_OK")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        qNameCB.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.qNameCB.text")); // NOI18N
        qNameCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qNameCBActionPerformed(evt);
            }
        });

        valueTypeLabel.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.valueTypeLabel.text")); // NOI18N

        valueType.setText(org.openide.util.NbBundle.getMessage(AddEventPanel.class, "AddEventPanel.valueType.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cancelButton))
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel2)
                            .add(jLabel4)
                            .add(valueTypeLabel))
                        .add(30, 30, 30)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(prefixTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(localPartTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                            .add(nameSpaceTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                            .add(valueType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                            .add(qNameCB))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(qNameCB)
                .add(9, 9, 9)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(localPartTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameSpaceTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(prefixTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(valueTypeLabel)
                    .add(valueType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO add your handling code here:
    String prefix = prefixTf.getText();
    String namespace = nameSpaceTf.getText();
    String localPart = localPartTf.getText();
    
   /* if(identifier == null || identifier.length() == 0)
    {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "NOT_A_VALID_IDENTIFIER"),NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        return;
    }*/
    
    if(localPart == null || localPart.length() == 0)
    {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "NOT_A_VALID_LOCAL_PART"),NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        return;
    }
    
    if(localPart.endsWith("."))
    {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "DOT_NOT_ALLOWED_AT_END_OF_LOCALPART"),NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        return;
    }
    String value = valueType.getText();
    if(value == null || value.trim().length() == 0 || !CoreUtil.validatePackageName(value))
    {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "NOT_A_VALID_VALUE_TYPE"),NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        return;
    }
    if(qNameCB.isSelected())
    {
         if(namespace == null || namespace.length() ==0){
             NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "NOT_A_VALID_NAMESPACE"),NotifyDescriptor.ERROR_MESSAGE);
             DialogDisplayer.getDefault().notify(nd);
             return;
         }
    }
    //QName qName = null;

   /* if((prefix == null || prefix.length() == 0) && namespace == null)
    {
        name = localPart;
    }else{
        if(prefix == null || prefix.length() == 0)
            qName = new QName(namespace,localPart);
        else
            qName = new QName(namespace,localPart,prefix);
    }*/
    if(qNameCB.isSelected())
    {
        QName qName = null;
        if(prefix == null || prefix.length() == 0)
            qName = new QName(namespace,localPart);
        else
            qName = new QName(namespace,localPart,prefix);
        evtObject = new EventObject();
        evtObject.setQName(qName);
        evtObject.setValueType(value);
    }else{
        evtObject = new EventObject();
        evtObject.setName(localPart);
        evtObject.setValueType(value);
    }
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void qNameCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qNameCBActionPerformed
        // TODO add your handling code here:
        if(qNameCB.isSelected())
        {
            prefixTf.setEnabled(true);
            nameSpaceTf.setEnabled(true);
        }else{
            prefixTf.setEnabled(false);
            nameSpaceTf.setEnabled(false);
        }
    }//GEN-LAST:event_qNameCBActionPerformed

    
    public EventObject getEvent()
    {
        return evtObject;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddEventPanel dialog = new AddEventPanel(new javax.swing.JFrame(),"test");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField localPartTf;
    private javax.swing.JTextField nameSpaceTf;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField prefixTf;
    private javax.swing.JCheckBox qNameCB;
    private javax.swing.JTextField valueType;
    private javax.swing.JLabel valueTypeLabel;
    // End of variables declaration//GEN-END:variables
    
}
