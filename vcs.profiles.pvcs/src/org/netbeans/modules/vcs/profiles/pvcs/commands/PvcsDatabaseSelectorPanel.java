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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandsPool;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.ChooseDirDialog;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * Selector panel for the location of PVCS database.
 *
 * @author  Martin Entlicher
 */
public class PvcsDatabaseSelectorPanel extends javax.swing.JPanel implements java.awt.event.ActionListener,
                                                                             java.awt.event.FocusListener,
                                                                             javax.swing.event.ListSelectionListener,
                                                                             Runnable,
                                                                             CommandDataOutputListener {
    
    private String selectedDatabase;
    private final String messageLoading = org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "CTL_LoadingDBList");
    private final String messageNoDBFound = org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "CTL_NoDBFound");
    
    private volatile boolean loading = false;
    private volatile boolean validList = false;
    
    private VcsFileSystem fileSystem;
    //private Hashtable vars;
    private String[] args;
    private Table dummyFiles;
    private Set spawnedCommands;
    
    /** Creates new form PvcsDatabaseSelectorPanel */
    public PvcsDatabaseSelectorPanel(VcsFileSystem fileSystem, String[] args, String database) {
        initComponents();
        setName(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "TITLE_DatabaseSelector"));
        dbButtonsGroup.add(dbRadioButtonCustom);
        dbButtonsGroup.add(dbRadioButtonGUI);
        dbButtonsGroup.add(dbRadioButtonSearch);
        this.fileSystem = fileSystem;
        //this.vars = vars;
        this.args = args;
        this.dummyFiles = new Table();
        this.spawnedCommands = new HashSet();
        initListeners();
        lastButtonSelected = dbRadioButtonCustom.isSelected() ? dbRadioButtonCustom :
                             dbRadioButtonGUI.isSelected() ? dbRadioButtonGUI :
                                                             dbRadioButtonSearch;
        enableDisableComponents();
        if (database != null) {
            customDbTextField.setText(database);
            selectedDatabase = database;
        }
        dbRadioButtonCustom.setMnemonic(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("LABEL_DatabaseLocation_Mnemonic").charAt(0));  // NOI18N
        customDbBrowseButton.setMnemonic(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("LABEL_Browse_Mnemonic").charAt(0));  // NOI18N
        customDbBrowseButton.setToolTipText(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_LABEL_BrowseA11yDesc"));
        dbRadioButtonGUI.setMnemonic(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("LABEL_SelectGUIDatabases_Mnemonic").charAt(0));  // NOI18N
        dbRadioButtonGUI.setToolTipText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "ACS_LABEL_SelectGUIDatabasesA11yDesc")); // NOI18N
        dbRadioButtonSearch.setMnemonic(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("LABEL_SearchInFolder_Mnemonic").charAt(0));  // NOI18N
        dbRadioButtonSearch.setToolTipText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "ACS_TEXTFIELD_SearchInFolderA11yDesc")); // NOI18N
        dbFolderBrowseButton.setMnemonic(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("LABEL_BrowseSubfolder_Mnemonic").charAt(0));  // NOI18N
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_DatabaseSelectorPanelA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_DatabaseSelectorPanelA11yDesc"));  // NOI18N
        customDbTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_TEXTFIELD_DatabaseLocationA11yName"));  // NOI18N
        customDbTextField.setToolTipText(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_TEXTFIELD_DatabaseLocationA11yDesc"));  // NOI18N
        dbFolderTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_TEXTFIELD_SearchInFolderA11yName"));  // NOI18N
        dbFolderTextField.setToolTipText(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_TEXTFIELD_SearchInFolderA11yDesc"));  // NOI18N
        dbList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_LIST_DatabaseListA11yName"));  // NOI18N
        dbList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_LIST_DatabaseListA11yDesc"));  // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        dbButtonsGroup = new javax.swing.ButtonGroup();
        dbRadioButtonCustom = new javax.swing.JRadioButton();
        customDbTextField = new javax.swing.JTextField();
        customDbBrowseButton = new javax.swing.JButton();
        dbRadioButtonGUI = new javax.swing.JRadioButton();
        dbRadioButtonSearch = new javax.swing.JRadioButton();
        dbFolderTextField = new javax.swing.JTextField();
        dbFolderBrowseButton = new javax.swing.JButton();
        dbScrollPane = new javax.swing.JScrollPane();
        dbList = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        dbRadioButtonCustom.setSelected(true);
        dbRadioButtonCustom.setText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "LABEL_DatabaseLocation"));
        dbRadioButtonCustom.setToolTipText(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_LABEL_DatabaseLocationA11yDesc"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(dbRadioButtonCustom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 12);
        add(customDbTextField, gridBagConstraints);

        customDbBrowseButton.setText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "LABEL_Browse"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(customDbBrowseButton, gridBagConstraints);

        dbRadioButtonGUI.setText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "LABEL_SelectGUIDatabases"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(dbRadioButtonGUI, gridBagConstraints);

        dbRadioButtonSearch.setText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "LABEL_SearchInFolder"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(dbRadioButtonSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 12);
        add(dbFolderTextField, gridBagConstraints);

        dbFolderBrowseButton.setText(org.openide.util.NbBundle.getMessage(PvcsDatabaseSelectorPanel.class, "LABEL_dbFolderBrowse"));
        dbFolderBrowseButton.setToolTipText(org.openide.util.NbBundle.getBundle(PvcsDatabaseSelectorPanel.class).getString("ACS_LABEL_dbFolderBrowseSubfolderA11yDesc"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(dbFolderBrowseButton, gridBagConstraints);

        dbScrollPane.setViewportView(dbList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 11, 12);
        add(dbScrollPane, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void initListeners() {
        dbRadioButtonCustom.addActionListener(this);
        dbRadioButtonGUI.addActionListener(this);
        dbRadioButtonSearch.addActionListener(this);
        customDbBrowseButton.addActionListener(this);
        dbFolderBrowseButton.addActionListener(this);
        customDbTextField.addFocusListener(this);
        dbFolderTextField.addFocusListener(this);
        dbListModel = new javax.swing.DefaultListModel();
        dbList.setModel(dbListModel);
        dbList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dbList.addListSelectionListener(this);
    }
    
    private void enableDisableComponents() {
        customDbTextField.setEnabled(dbRadioButtonCustom.isSelected());
        customDbBrowseButton.setEnabled(dbRadioButtonCustom.isSelected());
        dbFolderTextField.setEnabled(dbRadioButtonSearch.isSelected());
        dbFolderBrowseButton.setEnabled(dbRadioButtonSearch.isSelected());
        dbScrollPane.setEnabled(!dbRadioButtonCustom.isSelected());
    }
    
    /** Action listener on radio and browse buttons */
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof javax.swing.JRadioButton) {
            if (loading) {
                lastButtonSelected.setSelected(true);
                return ;
            }
            lastButtonSelected = (javax.swing.JRadioButton) source;
            enableDisableComponents();
            if (dbRadioButtonGUI.isSelected() ||
                dbRadioButtonSearch.isSelected() && dbFolderTextField.getText().length() > 0) {
                retrieveDatabaseLocations();
            }
            if (dbRadioButtonCustom == source && dbRadioButtonCustom.isSelected()) {
                customDbTextField.requestFocusInWindow();
            } else if (dbRadioButtonSearch == source && dbRadioButtonSearch.isSelected()) {
                dbFolderTextField.requestFocusInWindow();
            }
            
        } else if (customDbBrowseButton.equals(source)) {
            ChooseDirDialog chooseDir = new ChooseDirDialog(new javax.swing.JFrame(),
                                                            new java.io.File(customDbTextField.getText()));
            VcsUtilities.centerWindow(chooseDir);
            chooseDir.show();
            String selected = chooseDir.getSelectedDir();
            if (selected != null) {
                customDbTextField.setText(selected);
                selectedDatabase = selected;
            }
        } else if (dbFolderBrowseButton.equals(source)) {
            ChooseDirDialog chooseDir = new ChooseDirDialog(new javax.swing.JFrame(),
                                                            new java.io.File(dbFolderTextField.getText()));
            VcsUtilities.centerWindow(chooseDir);
            chooseDir.show();
            String selected = chooseDir.getSelectedDir();
            if (selected != null) {
                dbFolderTextField.setText(selected);
                retrieveDatabaseLocations();
            }
        }
    }
    
    /** List selection listener on database list */
    public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
        if (validList) {
            Object value = dbList.getSelectedValue();
            if (value != null) {
                selectedDatabase = value.toString();
            } else {
                selectedDatabase = null;
            }
        } else {
            dbList.clearSelection();
        }
    }
    
    /** A textfield gained focus */
    public void focusGained(java.awt.event.FocusEvent focusEvent) {
    }
    
    /** A textfield lost focus */
    public void focusLost(java.awt.event.FocusEvent focusEvent) {
        if (customDbTextField.equals(focusEvent.getSource())) {
            selectedDatabase = customDbTextField.getText();
        } else if (dbFolderTextField.equals(focusEvent.getSource()) &&
                   dbFolderTextField.getText().length() > 0) {
            retrieveDatabaseLocations();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton customDbBrowseButton;
    private javax.swing.JTextField dbFolderTextField;
    private javax.swing.JTextField customDbTextField;
    private javax.swing.JScrollPane dbScrollPane;
    private javax.swing.JList dbList;
    private javax.swing.ButtonGroup dbButtonsGroup;
    private javax.swing.JRadioButton dbRadioButtonSearch;
    private javax.swing.JRadioButton dbRadioButtonGUI;
    private javax.swing.JButton dbFolderBrowseButton;
    private javax.swing.JRadioButton dbRadioButtonCustom;
    // End of variables declaration//GEN-END:variables
    private javax.swing.DefaultListModel dbListModel;
    private javax.swing.JRadioButton lastButtonSelected;
    
    private void retrieveDatabaseLocations() {
        selectedDatabase = null;
        dbListModel.removeAllElements();
        dbListModel.addElement(messageLoading);
        validList = false;
        loading = true;
        RequestProcessor.getDefault().post(this);
    }
    
    /** Retrieve database locations */
    public void run() {
        VcsCommand cmd = fileSystem.getCommand(args[0]);
        if (cmd != null) {
            Hashtable additionalVars = new Hashtable();
            dummyFiles.put("foo.txt", null);
            additionalVars.put("GUI_DEF", dbRadioButtonGUI.isSelected() ? "true" : "");
            additionalVars.put("SEARCH_PATH", dbFolderTextField.getText());
            VcsCommandExecutor[] execs = VcsAction.doCommand(dummyFiles, cmd, additionalVars, fileSystem,
                                                             null, null, this, null);
            spawnedCommands.addAll(Arrays.asList(execs));
            try {
                for (int i = 0; i < execs.length; i++) {
                    fileSystem.getCommandsPool().waitToFinish(execs[i]);
                }
            } catch (InterruptedException intrEx) {
            }
            loading = false;
            if (!validList) {
                dbListModel.removeAllElements();
                dbListModel.addElement(messageNoDBFound);
            }
        }
    }
    
    /* Called when the selector is no longet active */
    void killRunningCommands() {
        CommandsPool cpool = fileSystem.getCommandsPool();
        for (Iterator it = spawnedCommands.iterator(); it.hasNext(); ) {
            VcsCommandExecutor exec = (VcsCommandExecutor) it.next();
            if (cpool.isRunning(exec)) cpool.kill(exec);
        }
    }
    
    /**
     * This method is called, with elements of the output data (database locations).
     * @param elements the elements of output data.
     */
    public void outputData(String[] elements) {
        if (elements[0] == null || elements[0].length() == 0) return ;
        if (!validList) {
            dbListModel.removeAllElements();
            validList = true;
        }
        dbListModel.addElement(elements[0]);
    }
    
    public String getSelectedDatabase() {
        return selectedDatabase;
    }
    
}
