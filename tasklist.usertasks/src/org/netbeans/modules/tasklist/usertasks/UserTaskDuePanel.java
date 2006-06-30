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

package org.netbeans.modules.tasklist.usertasks;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * OK, it might be "overkill" to use this class. I should rather use the
 * EditTaskPanel, (but I need to figure out where I can get all the information
 * I need ;)
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 *
 * @author  Trond Norbye
 * @author  Tor Norbye
 */
public class UserTaskDuePanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1;

    /** Creates new form UserTaskDuePanel */
    public UserTaskDuePanel() {
        initComponents();
        initA11y();
    }
    
    public UserTaskDuePanel(UserTask task) {
        this();
        detailsArea.setText(task.getDetails());
        descriptionFld.setText(task.getSummary());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        descriptionFld = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detailsArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(descriptionFld);
        /*
        jLabel1.setText("Summary:");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel1, gridBagConstraints);

        descriptionFld.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
        add(descriptionFld, gridBagConstraints);

        jLabel2.setLabelFor(detailsArea);
        /*
        jLabel2.setText("Description:");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel2, gridBagConstraints);

        detailsArea.setPreferredSize(new java.awt.Dimension(400, 200));
        detailsArea.setEnabled(false);
        jScrollPane1.setViewportView(detailsArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 11, 11);
        add(jScrollPane1, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void initA11y() {
        /*
          I couldn't figure out how to use Mnemonics.setLocalizedText
          to set labels and checkboxes with a mnemonic using the
          form builder, so the closest I got was to use "/*" and "* /
          as code pre-init/post-init blocks, such that I don't actually
          execute the bundle lookup code - and then call it explicitly
          below. (I wanted to keep the text on the components so that
          I can see them when visually editing the GUI.
        */

        Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(
                UserTaskDuePanel.class, "Brief_Description")); // NOI18N
        Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(
                UserTaskDuePanel.class, "DetailsLabel")); // NOI18N

        // accessible 
        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(UserTaskDuePanel.class, 
                                    "ACSD_DuePanel")); // NOI18N
        descriptionFld.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(UserTaskDuePanel.class, 
                                    "ACSD_Brief_Description")); // NOI18N
        detailsArea.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(UserTaskDuePanel.class, 
                                    "ACSD_Details")); // NOI18N
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField descriptionFld;
    private javax.swing.JTextArea detailsArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
