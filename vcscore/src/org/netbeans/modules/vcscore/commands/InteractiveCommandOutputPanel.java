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

package org.netbeans.modules.vcscore.commands;

import org.netbeans.modules.vcscore.ui.OutputPanel;
import org.openide.util.NbBundle;

/**
 * The interactive display of command output with an input field.
 *
 * @author  Martin Entlicher
 */
//class InteractiveCommandOutputPanel extends CommandOutputPanel {
class InteractiveCommandOutputPanel extends OutputPanel{

    private javax.swing.JLabel inputStringLabel;
    private javax.swing.JTextField inputStringTextField;
    private TextInput input;

    /** Creates a new instance of InteractiveCommandOutputPanel */
    public InteractiveCommandOutputPanel() {
        initComponents();
    }
    
    private void initComponents() {
        //javax.swing.JPanel ioPanel = new javax.swing.JPanel();
        //javax.swing.JPanel outputPanel = new javax.swing.JPanel();
        javax.swing.JPanel inputPanel = new javax.swing.JPanel();
        
        //ioPanel.setLayout(new java.awt.GridBagLayout());
        //outputPanel.setLayout(new java.awt.GridBagLayout());
        inputPanel.setLayout(new java.awt.GridBagLayout());
        
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        //remove(1);
        //addImpl(ioPanel, gridBagConstraints, 1);
        getOutputPanel().add(inputPanel, gridBagConstraints);
        
        inputStringLabel = new javax.swing.JLabel(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.label"));
        inputStringLabel.setDisplayedMnemonic(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.mnemonic").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        inputPanel.add(inputStringLabel, gridBagConstraints);
        
        inputStringTextField = new javax.swing.JTextField();
        inputStringLabel.setLabelFor(inputStringTextField);
        inputStringTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.acsd"));
        inputStringTextField.setToolTipText(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.acsd"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        inputPanel.add(inputStringTextField, gridBagConstraints);
        
        inputStringTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (input == null) return ;
                String text = inputStringTextField.getText() + '\n';
                input.sendInput(text);
                inputStringTextField.setText("");
            }
        });
        
        /*
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        ioPanel.add(outputPanel, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        ioPanel.add(inputPanel, gridBagConstraints);
         */
    }
    
    void setInput(TextInput input) {
        this.input = input;
    }
    
    public void commandFinished(boolean isFinished) {
    //    super.commandFinished(isFinished);
        if (isFinished) {
            inputStringLabel.setEnabled(false);
            inputStringTextField.setEnabled(false);
        }
    }
    
}
