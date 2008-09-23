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

package org.netbeans.modules.tasklist.bugs.scarab;

import org.netbeans.modules.tasklist.bugs.BugQuery;
import org.netbeans.modules.tasklist.bugs.QueryPanelIF;
import org.netbeans.modules.tasklist.bugs.BugEngine;
import org.netbeans.modules.tasklist.bugs.ProjectDesc;
import org.netbeans.modules.tasklist.bugs.javanet.ProjectList;
import org.netbeans.modules.tasklist.bugs.issuezilla.Issuezilla;
import org.openide.util.RequestProcessor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Allows to customize Scarab database connection.
 *
 * @author  Petr Kuzel
 */
public class SourcePanel extends javax.swing.JPanel implements QueryPanelIF {

    /**
     * Creates new form SourcePanel
     */
    public SourcePanel() {
        initComponents();

        serviceTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    serviceExampleLabel.setText("probing...");
                    final URL url = new URL(serviceTextField.getText());
                    serviceExampleLabel.setText("Server OK");
                } catch (MalformedURLException e1) {
                    serviceExampleLabel.setText("Invalid server URL!");
                }
            }
        });
    }


    public BugQuery getQueryOptions(final BugQuery inQuery) {
        final ScarabBugQuery sbq = new ScarabBugQuery(inQuery);
        sbq.setBaseUrl(serviceTextField.getText());
        sbq.setQueryString(customTextField.getText());
        sbq.setAttributeName(Issue.SUMMARY,summaryField.getText());
        sbq.setAttributeName(Issue.STATUS,statusField.getText());
        sbq.setAttributeName(Issue.COMPONENT,componentField.getText());
        sbq.setAttributeName(Issue.SUBCOMPONENT,subcomponentField.getText());
        sbq.setAttributeName(Issue.ASSIGNED_TO,assignedToField.getText());
        sbq.setAttributeName(Issue.PRIORITY,priorityField.getText());
        sbq.setAttributeName(Issue.TARGET,targetField.getText());
        sbq.setAttributeName(Issue.VOTES,votesField.getText());
        sbq.setAttributeName(Issue.KEYWORDS,keywordsField.getText());
        return sbq;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel4 = new javax.swing.JLabel();
        serviceLabel = new javax.swing.JLabel();
        serviceTextField = new javax.swing.JTextField();
        serviceExampleLabel = new javax.swing.JLabel();
        customLabel = new javax.swing.JLabel();
        customTextField = new javax.swing.JTextField();
        customExLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        summaryLabel = new javax.swing.JLabel();
        priorityLabel = new javax.swing.JLabel();
        componentLabel = new javax.swing.JLabel();
        subcomponentLabel = new javax.swing.JLabel();
        keywordsLabel = new javax.swing.JLabel();
        assignedToLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        targetLabel = new javax.swing.JLabel();
        votesLabel = new javax.swing.JLabel();
        summaryField = new javax.swing.JTextField();
        statusField = new javax.swing.JTextField();
        componentField = new javax.swing.JTextField();
        subcomponentField = new javax.swing.JTextField();
        assignedToField = new javax.swing.JTextField();
        priorityField = new javax.swing.JTextField();
        targetField = new javax.swing.JTextField();
        votesField = new javax.swing.JTextField();
        keywordsField = new javax.swing.JTextField();

        jLabel4.setText("jLabel4");

        setLayout(new java.awt.GridBagLayout());

        serviceLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("ServiceUrl_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(serviceLabel, gridBagConstraints);

        serviceTextField.setColumns(60);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(serviceTextField, gridBagConstraints);

        serviceExampleLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("ServiceUrlExample_Label"));
        serviceExampleLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(serviceExampleLabel, gridBagConstraints);

        customLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Query_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        add(customLabel, gridBagConstraints);

        customTextField.setColumns(60);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(customTextField, gridBagConstraints);

        customExLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("QueryExample_Label"));
        customExLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(customExLabel, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("AttributeMappings_Label")));
        jPanel1.setToolTipText("Enter names for scarab's attributes to match NetBeans Tasklist names.");
        summaryLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Summary_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(summaryLabel, gridBagConstraints);

        priorityLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Priority_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(priorityLabel, gridBagConstraints);

        componentLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Component_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(componentLabel, gridBagConstraints);

        subcomponentLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Subcomponent_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(subcomponentLabel, gridBagConstraints);

        keywordsLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Keywords_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(keywordsLabel, gridBagConstraints);

        assignedToLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("AssignedTo_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(assignedToLabel, gridBagConstraints);

        statusLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Status_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(statusLabel, gridBagConstraints);

        targetLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Target_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(targetLabel, gridBagConstraints);

        votesLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Votes_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(votesLabel, gridBagConstraints);

        summaryField.setText("Summary");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(summaryField, gridBagConstraints);

        statusField.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(statusField, gridBagConstraints);

        componentField.setText("Component");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(componentField, gridBagConstraints);

        subcomponentField.setText("Subcomponent");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(subcomponentField, gridBagConstraints);

        assignedToField.setText("AssignedTo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(assignedToField, gridBagConstraints);

        priorityField.setText("Priority");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(priorityField, gridBagConstraints);

        targetField.setText("Target");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(targetField, gridBagConstraints);

        votesField.setText("Votes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(votesField, gridBagConstraints);

        keywordsField.setText("Keywords");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        jPanel1.add(keywordsField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField assignedToField;
    public javax.swing.JLabel assignedToLabel;
    public javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JTextField componentField;
    public javax.swing.JLabel componentLabel;
    public javax.swing.JLabel customExLabel;
    public javax.swing.JLabel customLabel;
    public javax.swing.JTextField customTextField;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JTextField keywordsField;
    public javax.swing.JLabel keywordsLabel;
    public javax.swing.JTextField priorityField;
    public javax.swing.JLabel priorityLabel;
    public javax.swing.JLabel serviceExampleLabel;
    public javax.swing.JLabel serviceLabel;
    public javax.swing.JTextField serviceTextField;
    public javax.swing.JTextField statusField;
    public javax.swing.JLabel statusLabel;
    public javax.swing.JTextField subcomponentField;
    public javax.swing.JLabel subcomponentLabel;
    public javax.swing.JTextField summaryField;
    public javax.swing.JLabel summaryLabel;
    public javax.swing.JTextField targetField;
    public javax.swing.JLabel targetLabel;
    public javax.swing.JTextField votesField;
    public javax.swing.JLabel votesLabel;
    // End of variables declaration//GEN-END:variables
    
}