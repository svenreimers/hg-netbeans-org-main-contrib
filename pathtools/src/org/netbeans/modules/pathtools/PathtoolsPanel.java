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

package org.netbeans.modules.pathtools;

import java.util.prefs.Preferences;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
final class PathtoolsPanel extends javax.swing.JPanel {
    
    private final PathtoolsOptionsPanelController controller;
    
    PathtoolsPanel(PathtoolsOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        folderExploreCommandTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
        });
        fileExploreCommandTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
        });
        folderShellCommandTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
        });
        fileShellCommandTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
        });
        folderEditCommandTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
        });
        fileEditCommandTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                PathtoolsPanel.this.controller.changed();
            }
        });
    }
        
    public JTextField getFolderExploreCommandTextField() {
        return folderExploreCommandTextField;
    }
    
    public JTextField getFileExploreCommandTextField() {
        return fileExploreCommandTextField;
    }
    
    public JTextField getFolderShellCommandTextField() {
        return folderShellCommandTextField;
    }
    
    public JTextField getFileShellCommandTextField() {
        return fileShellCommandTextField;
    }
    
    public JTextField getFolderEditCommandTextField() {
        return folderEditCommandTextField;
    }
    
    public JTextField getFileEditCommandTextField() {
        return fileEditCommandTextField;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        exploreCommandPanel = new javax.swing.JPanel();
        folderExploreCommandLabel = new javax.swing.JLabel();
        folderExploreCommandTextField = new javax.swing.JTextField();
        fileExploreCommandLabel = new javax.swing.JLabel();
        fileExploreCommandTextField = new javax.swing.JTextField();
        shellCommandPanel = new javax.swing.JPanel();
        folderShellCommandLabel = new javax.swing.JLabel();
        folderShellCommandTextField = new javax.swing.JTextField();
        fileShellCommandLabel = new javax.swing.JLabel();
        fileShellCommandTextField = new javax.swing.JTextField();
        editCommandPanel = new javax.swing.JPanel();
        folderEditCommandLabel = new javax.swing.JLabel();
        folderEditCommandTextField = new javax.swing.JTextField();
        fileEditCommandLabel = new javax.swing.JLabel();
        fileEditCommandTextField = new javax.swing.JTextField();
        exploreCommandHelpScrollPane = new javax.swing.JScrollPane();
        exploreCommandTextArea = new javax.swing.JTextArea();

        setBackground(java.awt.Color.white);
        exploreCommandPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Explore Commands"));
        exploreCommandPanel.setOpaque(false);
        folderExploreCommandLabel.setDisplayedMnemonic('d');
        org.openide.awt.Mnemonics.setLocalizedText(folderExploreCommandLabel, org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ForFolderLabel"));

        fileExploreCommandLabel.setDisplayedMnemonic('l');
        org.openide.awt.Mnemonics.setLocalizedText(fileExploreCommandLabel, org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ForFileLabel"));

        org.jdesktop.layout.GroupLayout exploreCommandPanelLayout = new org.jdesktop.layout.GroupLayout(exploreCommandPanel);
        exploreCommandPanel.setLayout(exploreCommandPanelLayout);
        exploreCommandPanelLayout.setHorizontalGroup(
            exploreCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(exploreCommandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(exploreCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(folderExploreCommandLabel)
                    .add(fileExploreCommandLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exploreCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileExploreCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                    .add(folderExploreCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE))
                .addContainerGap())
        );
        exploreCommandPanelLayout.setVerticalGroup(
            exploreCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(exploreCommandPanelLayout.createSequentialGroup()
                .add(exploreCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(folderExploreCommandLabel)
                    .add(folderExploreCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exploreCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileExploreCommandLabel)
                    .add(fileExploreCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        shellCommandPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Shell Commands"));
        shellCommandPanel.setOpaque(false);
        folderShellCommandLabel.setDisplayedMnemonic('d');
        org.openide.awt.Mnemonics.setLocalizedText(folderShellCommandLabel, org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ForFolderLabel"));

        fileShellCommandLabel.setDisplayedMnemonic('l');
        org.openide.awt.Mnemonics.setLocalizedText(fileShellCommandLabel, org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ForFileLabel"));

        org.jdesktop.layout.GroupLayout shellCommandPanelLayout = new org.jdesktop.layout.GroupLayout(shellCommandPanel);
        shellCommandPanel.setLayout(shellCommandPanelLayout);
        shellCommandPanelLayout.setHorizontalGroup(
            shellCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(shellCommandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(shellCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(folderShellCommandLabel)
                    .add(fileShellCommandLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(shellCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(fileShellCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                    .add(folderShellCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE))
                .addContainerGap())
        );
        shellCommandPanelLayout.setVerticalGroup(
            shellCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(shellCommandPanelLayout.createSequentialGroup()
                .add(shellCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(folderShellCommandLabel)
                    .add(folderShellCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(shellCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fileShellCommandLabel)
                    .add(fileShellCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        editCommandPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edit Commands"));
        editCommandPanel.setOpaque(false);
        folderEditCommandLabel.setDisplayedMnemonic('d');
        org.openide.awt.Mnemonics.setLocalizedText(folderEditCommandLabel, org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ForFolderLabel"));

        fileEditCommandLabel.setDisplayedMnemonic('l');
        org.openide.awt.Mnemonics.setLocalizedText(fileEditCommandLabel, org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ForFileLabel"));

        org.jdesktop.layout.GroupLayout editCommandPanelLayout = new org.jdesktop.layout.GroupLayout(editCommandPanel);
        editCommandPanel.setLayout(editCommandPanelLayout);
        editCommandPanelLayout.setHorizontalGroup(
            editCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editCommandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(editCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(folderEditCommandLabel)
                    .add(fileEditCommandLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileEditCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                    .add(folderEditCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE))
                .addContainerGap())
        );
        editCommandPanelLayout.setVerticalGroup(
            editCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editCommandPanelLayout.createSequentialGroup()
                .add(editCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(folderEditCommandLabel)
                    .add(folderEditCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editCommandPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileEditCommandLabel)
                    .add(fileEditCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        exploreCommandTextArea.setColumns(20);
        exploreCommandTextArea.setRows(8);
        exploreCommandTextArea.setText(org.openide.util.NbBundle.getMessage(PathtoolsPanel.class, "CTL_ExploreCommandHelp"));
        exploreCommandHelpScrollPane.setViewportView(exploreCommandTextArea);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, exploreCommandHelpScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, editCommandPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, shellCommandPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, exploreCommandPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(exploreCommandPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(shellCommandPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editCommandPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exploreCommandHelpScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    void load() {
        String folderExploreCommand =
            Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                PathtoolsOptionsPanelController.PROP_FOLDER_EXPLORE_COMMAND,
                controller.getDEFAULT_FOLDER_EXPLORE_COMMAND());
        folderExploreCommandTextField.setText(folderExploreCommand);
        String fileExploreCommand =
            Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                PathtoolsOptionsPanelController.PROP_FILE_EXPLORE_COMMAND,
                controller.getDEFAULT_FILE_EXPLORE_COMMAND());
        fileExploreCommandTextField.setText(fileExploreCommand);
        String folderShellCommand =
            Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                PathtoolsOptionsPanelController.PROP_FOLDER_SHELL_COMMAND,
                controller.getDEFAULT_FOLDER_SHELL_COMMAND());
        folderShellCommandTextField.setText(folderShellCommand);
        String fileShellCommand =
            Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                PathtoolsOptionsPanelController.PROP_FILE_SHELL_COMMAND,
                controller.getDEFAULT_FILE_SHELL_COMMAND());
        fileShellCommandTextField.setText(fileShellCommand);
        String folderEditCommand =
            Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                PathtoolsOptionsPanelController.PROP_FOLDER_EDIT_COMMAND,
                controller.getDEFAULT_FOLDER_EDIT_COMMAND());
        folderEditCommandTextField.setText(folderEditCommand);
        String fileEditCommand =
            Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                PathtoolsOptionsPanelController.PROP_FILE_EDIT_COMMAND,
                controller.getDEFAULT_FILE_EDIT_COMMAND());
        fileEditCommandTextField.setText(fileEditCommand);        
    }
    
    void store() {
        Preferences.userNodeForPackage(PathtoolsPanel.class).put(
                PathtoolsOptionsPanelController.PROP_FOLDER_EXPLORE_COMMAND,
                folderExploreCommandTextField.getText());
        Preferences.userNodeForPackage(PathtoolsPanel.class).put(
                PathtoolsOptionsPanelController.PROP_FILE_EXPLORE_COMMAND,
                fileExploreCommandTextField.getText());
        Preferences.userNodeForPackage(PathtoolsPanel.class).put(
                PathtoolsOptionsPanelController.PROP_FOLDER_SHELL_COMMAND,
                folderShellCommandTextField.getText());
        Preferences.userNodeForPackage(PathtoolsPanel.class).put(
                PathtoolsOptionsPanelController.PROP_FILE_SHELL_COMMAND,
                fileShellCommandTextField.getText());
        Preferences.userNodeForPackage(PathtoolsPanel.class).put(
                PathtoolsOptionsPanelController.PROP_FOLDER_EDIT_COMMAND,
                folderEditCommandTextField.getText());
        Preferences.userNodeForPackage(PathtoolsPanel.class).put(
                PathtoolsOptionsPanelController.PROP_FILE_EDIT_COMMAND,
                fileEditCommandTextField.getText());
    }
    
    boolean valid() {
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel editCommandPanel;
    private javax.swing.JScrollPane exploreCommandHelpScrollPane;
    private javax.swing.JPanel exploreCommandPanel;
    private javax.swing.JTextArea exploreCommandTextArea;
    private javax.swing.JLabel fileEditCommandLabel;
    private javax.swing.JTextField fileEditCommandTextField;
    private javax.swing.JLabel fileExploreCommandLabel;
    private javax.swing.JTextField fileExploreCommandTextField;
    private javax.swing.JLabel fileShellCommandLabel;
    private javax.swing.JTextField fileShellCommandTextField;
    private javax.swing.JLabel folderEditCommandLabel;
    private javax.swing.JTextField folderEditCommandTextField;
    private javax.swing.JLabel folderExploreCommandLabel;
    private javax.swing.JTextField folderExploreCommandTextField;
    private javax.swing.JLabel folderShellCommandLabel;
    private javax.swing.JTextField folderShellCommandTextField;
    private javax.swing.JPanel shellCommandPanel;
    // End of variables declaration//GEN-END:variables
    
}
