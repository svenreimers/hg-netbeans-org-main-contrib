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

package org.netbeans.modules.portalpack.servers.core.ui;

import javax.swing.JOptionPane;
import org.netbeans.modules.portalpack.servers.core.util.Util;
import org.openide.util.NbBundle;

/**

 *

 * @author  satya

 */

public class CreateContainerChannelPanel extends javax.swing.JDialog {

    private String name;

    private String type;

    /** Creates new form CreateContainerChannelPanel */

    public CreateContainerChannelPanel(java.awt.Frame parent,String nameLabel,String typeLabel,String title,String[] typeValues) {

        super(parent, true);

        initComponents();

        lname.setText(nameLabel);

        ltype.setText(typeLabel);

        setTitle(title);

        for(int i=0;i<typeValues.length;i++)

        {

            typeCombo.addItem(typeValues[i]);

        }
        setLocation(parent.getX()+(parent.getWidth()-getWidth())/2,parent.getY()+(parent.getHeight()-getHeight())/2);

    }

    

    

    public String getName()

    {

        return name;

    }

    

    public String getType()

    {

        return type;

    }

    

    /** This method is called from within the constructor to

     * initialize the form.

     * WARNING: Do NOT modify this code. The content of this method is

     * always regenerated by the Form Editor.

     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents

    private void initComponents() {

        lname = new javax.swing.JLabel();

        ltype = new javax.swing.JLabel();

        typeCombo = new javax.swing.JComboBox();

        nameTf = new javax.swing.JTextField();

        okButton = new javax.swing.JButton();

        cancelButton = new javax.swing.JButton();

        jSeparator1 = new javax.swing.JSeparator();



        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setTitle("Create Container/Channel");

        setAlwaysOnTop(true);

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lname.setFont(new java.awt.Font("Tahoma", 1, 11));

        lname.setText(org.openide.util.NbBundle.getMessage(CreateContainerChannelPanel.class, "LBL_CONTAINER"));



        ltype.setFont(new java.awt.Font("Tahoma", 1, 11));

        ltype.setText(org.openide.util.NbBundle.getMessage(CreateContainerChannelPanel.class, "LBL_PROVIDER"));



        okButton.setFont(new java.awt.Font("Tahoma", 1, 11));

        okButton.setText(org.openide.util.NbBundle.getMessage(CreateContainerChannelPanel.class, "LBL_OK"));

        okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {

                okButtonActionPerformed(evt);

            }

        });



        cancelButton.setFont(new java.awt.Font("Tahoma", 1, 11));

        cancelButton.setText(org.openide.util.NbBundle.getMessage(CreateContainerChannelPanel.class, "LBL_CANCEL"));

        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {

                cancelButtonActionPerformed(evt);

            }

        });



        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(

            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

            .add(layout.createSequentialGroup()

                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

                    .add(layout.createSequentialGroup()

                        .add(189, 189, 189)

                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)

                        .add(cancelButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))

                    .add(layout.createSequentialGroup()

                        .add(27, 27, 27)

                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()

                                .add(ltype, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

                                .add(48, 48, 48)

                                .add(typeCombo, 0, 239, Short.MAX_VALUE))

                            .add(layout.createSequentialGroup()

                                .add(lname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

                                .add(22, 22, 22)

                                .add(nameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)))))

                .addContainerGap())

            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)

        );

        layout.setVerticalGroup(

            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

            .add(layout.createSequentialGroup()

                .add(24, 24, 24)

                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)

                    .add(typeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

                    .add(ltype))

                .add(16, 16, 16)

                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)

                    .add(lname)

                    .add(nameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))

                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)

                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)

                    .add(cancelButton)

                    .add(okButton))

                .add(19, 19, 19))

        );

        pack();

    }// </editor-fold>//GEN-END:initComponents



    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

// TODO add your handling code here:

        name = "";

        type = "";

        this.dispose();

    }//GEN-LAST:event_cancelButtonActionPerformed



    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

// TODO add your handling code here:

        name = nameTf.getText();

        type = (String)typeCombo.getSelectedItem();
        
        if(!Util.validateString(name,false))
        {
            JOptionPane.showMessageDialog(this,NbBundle.getMessage(CreateContainerChannelPanel.class,
                                            "MSG_INVALID_NAME"));
            return;
        }

        this.dispose();

    }//GEN-LAST:event_okButtonActionPerformed

    

    /**

     * @param args the command line arguments

     */

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

           //     new CreateContainerChannelPanel(new javax.swing.JFrame(), true).setVisible(true);

            }

        });

    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables

    private javax.swing.JButton cancelButton;

    private javax.swing.JSeparator jSeparator1;

    private javax.swing.JLabel lname;

    private javax.swing.JLabel ltype;

    private javax.swing.JTextField nameTf;

    private javax.swing.JButton okButton;

    private javax.swing.JComboBox typeCombo;

    // End of variables declaration//GEN-END:variables

    

}

