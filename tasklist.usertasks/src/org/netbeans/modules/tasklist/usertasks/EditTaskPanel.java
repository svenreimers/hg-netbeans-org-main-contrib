/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.openide.awt.Mnemonics;
import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel used to enter/edit a user task.
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 */

class EditTaskPanel extends JPanel implements ActionListener {
    private Task parent = null;
    private SimpleDateFormat format;
    private ComboBoxModel prioritiesModel = 
        new DefaultComboBoxModel(Task.getPriorityNames());
    private ComboBoxModel subtaskModel = null;
    
    private static boolean appendDefault = Settings.getDefault().getAppend();
    
    /** Creates new form NewTodoItemPanel.
        @param parent A possible suggestion for a parent 
        @param item Item to edit. If null, create new.
     */
    EditTaskPanel(UserTaskList tlv, UserTask parent, UserTask item, boolean editing) {
        // Create a new item with the given suggested parent
        this.parent = parent;
        initComponents();
        initA11y();
        
        priorityComboBox.setSelectedIndex(3);
        
        format = new SimpleDateFormat();
        
        // Initialize the Categories list
        Set categories = tlv.getCategories();
        if ((categories != null) && (categories.size() > 0)) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            model.addElement(""); // Default to "nothing" -- make an option for default category?
            Iterator it = categories.iterator();
            while (it.hasNext()) {
                String category = (String)it.next();
                model.addElement(category);
            }
            categoryCombo.setModel(model);
        }

        // Create subtask model
        Vector parents = new Vector(100);
        parents.addElement(NbBundle.getMessage(EditTaskPanel.class,
                                               "NoSubtasks")); // NOI18N
        tlv.addAllTasks(parents);
        subtaskModel = new DefaultComboBoxModel(parents);
        subtaskCombo.setModel(subtaskModel);
        subtaskCombo.setRenderer(new TaskCellRenderer());
        
        if (parent != null) {
            subtaskCombo.setSelectedItem(parent);
        }
        
        if (item != null) {
            if (item.getSummary() != null) {
                descriptionTextField.setText(item.getSummary());
            }
            int p = item.getPriorityNumber();
            if (p < 0)
                p = 0;
            if (p > 5)
                p = 5;
            priorityComboBox.setSelectedIndex(p);
            if (item.getFilename() != null) {
                fileTextField.setText(item.getFilename());
                fileCheckBox.setSelected(true);
            }
            if (item.getLineNumber() > 0) {
                lineTextField.setText(Integer.toString(item.getLineNumber()));
            }
            if (item.getCategory() != null) {
                categoryCombo.setSelectedItem(item.getCategory());
            }
            if (item.getDetails() != null) {
                detailsTextArea.setText(item.getDetails());
            }
            setDueDate(item.getDueDate());
        }
        
        addSourceButton.addActionListener(this);
        
        descriptionTextField.requestDefaultFocus();
        descriptionTextField.requestFocus();

        if (editing) {
            remove(addLabel);
            remove(beginningToggle);
            remove(endToggle);
            remove(addSourceButton);
        } else {
            boolean append = appendDefault;
            if (append) {
                endToggle.setSelected(true);
            } else {
                beginningToggle.setSelected(true);
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        prioGroup = new javax.swing.ButtonGroup();
        appendGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        descLabel = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        detailsLabel = new javax.swing.JLabel();
        detailsScrollPane = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        subtaskLabel = new javax.swing.JLabel();
        subtaskCombo = new javax.swing.JComboBox();
        prioLabel = new javax.swing.JLabel();
        priorityComboBox = new javax.swing.JComboBox();
        opt1Label = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox();
        opt2Label = new javax.swing.JLabel();
        fileCheckBox = new javax.swing.JCheckBox();
        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        lineLabel = new javax.swing.JLabel();
        lineTextField = new javax.swing.JTextField();
        dueCheckBox = new javax.swing.JCheckBox();
        dueLabel = new javax.swing.JLabel();
        dueDateTextField = new javax.swing.JTextField();
        dueDateBrowseButton = new javax.swing.JButton();
        addLabel = new javax.swing.JLabel();
        beginningToggle = new javax.swing.JRadioButton();
        endToggle = new javax.swing.JRadioButton();
        addSourceButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12)));
        setPreferredSize(new java.awt.Dimension(400, 300));
        /*
        descLabel.setLabelFor(descriptionTextField);
        descLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "Brief_Description")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(descLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(descriptionTextField, gridBagConstraints);

    /*
    detailsLabel.setLabelFor(detailsTextArea);
    detailsLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "DetailsLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(detailsLabel, gridBagConstraints);

    detailsTextArea.setRows(5);
    detailsScrollPane.setViewportView(detailsTextArea);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(detailsScrollPane, gridBagConstraints);

    /*
    subtaskLabel.setLabelFor(subtaskCombo);
    subtaskLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "IsSubtaskOf")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(subtaskLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(subtaskCombo, gridBagConstraints);

    /*
    prioLabel.setLabelFor(priorityComboBox);
    prioLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "PriorityLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(prioLabel, gridBagConstraints);

    priorityComboBox.setModel(prioritiesModel);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(priorityComboBox, gridBagConstraints);

    /*
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
    add(opt1Label, gridBagConstraints);

    /*
    categoryLabel.setLabelFor(categoryCombo);
    categoryLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "CategoryLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(categoryLabel, gridBagConstraints);

    categoryCombo.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(categoryCombo, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
    add(opt2Label, gridBagConstraints);

    /*
    fileCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "AssociatedFile")); // NOI18N);
    */
    fileCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            EditTaskPanel.this.fileCheckBoxItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(fileCheckBox, gridBagConstraints);

    /*
    fileLabel.setLabelFor(fileTextField);
    fileLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "FileName")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(fileLabel, gridBagConstraints);

    fileTextField.setColumns(100);
    fileTextField.setEditable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(fileTextField, gridBagConstraints);

    /*
    lineLabel.setLabelFor(lineTextField);
    lineLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "LineLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(lineLabel, gridBagConstraints);

    lineTextField.setEditable(false);
    lineTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(lineTextField, gridBagConstraints);

    /*
    dueCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "DueDateCb")); // NOI18N();
    */
    dueCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            EditTaskPanel.this.dueCheckBoxItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(dueCheckBox, gridBagConstraints);

    /*
    dueLabel.setLabelFor(dueDateTextField);
    dueLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "DueDateLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(dueLabel, gridBagConstraints);

    dueDateTextField.setEditable(false);
    dueDateTextField.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(dueDateTextField, gridBagConstraints);

    /*
    dueDateBrowseButton.setText("...");
    dueDateBrowseButton.setEnabled(false);
    */
    dueDateBrowseButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            EditTaskPanel.this.dueDateBrowseButtonActionPerformed(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(dueDateBrowseButton, gridBagConstraints);

    /*
    addLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "AddTo")); // NOI18N();
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(addLabel, gridBagConstraints);

    /*
    beginningToggle.setText(NbBundle.getMessage(EditTaskPanel.class, "BeginningList")); // NOI18N();
    buttonGroup1.add(beginningToggle);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(beginningToggle, gridBagConstraints);

    /*
    endToggle.setText(NbBundle.getMessage(EditTaskPanel.class, "EndList")); // NOI18N();
    buttonGroup1.add(endToggle);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(endToggle, gridBagConstraints);

    /*
    addSourceButton.setText(NbBundle.getMessage(EditTaskPanel.class, "AddToSource")); // NOI18N();
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    add(addSourceButton, gridBagConstraints);

    }//GEN-END:initComponents

    /** Initialize accessibility settings on the panel */
    private void initA11y() {
        /*
          I couldn't figure out how to use Mnemonics.setLocalizedText
          to set labels and checkboxes with a mnemonic using the
          form builder, so the closest I got was to use "/*" and "* /
          as code pre-init/post-init blocks, such that I don't actually
          execute the bundle lookup code - and then call it explicitly
          below. (I wanted to keep the text on the components so that
          I can see them when visually editing the GUI.

          However, this doesn't only comment out the label setting,
          but labelFor, enabled, etc. - so these are included below.

          Be careful when editing attributes using the gui builder -
          you may need to copy them down here.

        */
        descLabel.setLabelFor(descriptionTextField);
        detailsLabel.setLabelFor(detailsTextArea);
        subtaskLabel.setLabelFor(subtaskCombo);
        prioLabel.setLabelFor(priorityComboBox);
        categoryLabel.setLabelFor(categoryCombo);
        fileLabel.setLabelFor(fileTextField);
        lineLabel.setLabelFor(lineTextField);
        dueLabel.setLabelFor(dueDateTextField);
        dueDateBrowseButton.setEnabled(false);
        buttonGroup1.add(endToggle);
        buttonGroup1.add(beginningToggle);

        Mnemonics.setLocalizedText(descLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "Brief_Description")); // NOI18N
        Mnemonics.setLocalizedText(detailsLabel, NbBundle.getMessage(
                    EditTaskPanel.class, "DetailsLabel")); // NOI18N
        Mnemonics.setLocalizedText(subtaskLabel, NbBundle.getMessage(
                    EditTaskPanel.class, "IsSubtaskOf")); // NOI18N
        Mnemonics.setLocalizedText(prioLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "PriorityLabel")); // NOI18N
        Mnemonics.setLocalizedText(fileLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "AssociatedFile")); // NOI18N
        Mnemonics.setLocalizedText(categoryLabel, NbBundle.getMessage(
                     EditTaskPanel.class, "CategoryLabel")); // NOI18N
        Mnemonics.setLocalizedText(lineLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "LineLabel")); // NOI18N
        Mnemonics.setLocalizedText(dueCheckBox, NbBundle.getMessage(
                   EditTaskPanel.class, "DueDateCb")); // NOI18N
        Mnemonics.setLocalizedText(dueLabel, NbBundle.getMessage(
                EditTaskPanel.class, "DueDateLabel")); // NOI18N
        Mnemonics.setLocalizedText(addLabel, NbBundle.getMessage(
                EditTaskPanel.class, "AddTo")); // NOI18N
        Mnemonics.setLocalizedText(dueDateBrowseButton, NbBundle.getMessage(
                EditTaskPanel.class, "Browse")); // NOI18N
        Mnemonics.setLocalizedText(beginningToggle, NbBundle.getMessage(
                       EditTaskPanel.class, "BeginningList")); // NOI18N
        Mnemonics.setLocalizedText(endToggle, NbBundle.getMessage(
                 EditTaskPanel.class, "EndList")); // NOI18N
        Mnemonics.setLocalizedText(addSourceButton, NbBundle.getMessage(
                       EditTaskPanel.class, "AddToSource")); // NOI18N

        //this.getAccessibleContext().setAccessibleDescription(getstr("ACSN_"));

        // Gotta set accessible name - no more that I've set label for?
        // gotta set accessible description "everywhere" ?

    }

    private void fileCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fileCheckBoxItemStateChanged
        boolean s = fileCheckBox.isSelected();
        fileTextField.setEditable(s);
        lineTextField.setEditable(s);
    }//GEN-LAST:event_fileCheckBoxItemStateChanged

    /**
     * Callback function to enable / disable the due-date fields
     * @param evt the callback event
     */
    private void dueCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dueCheckBoxItemStateChanged
        if (evt.getID() == evt.ITEM_STATE_CHANGED) {
            boolean enable = false;
            if (evt.getStateChange() == evt.SELECTED) {
                enable = true;
            }
            dueDateBrowseButton.setEnabled(enable);
            dueDateTextField.setEnabled(enable);
            dueDateTextField.setEditable(enable);
        }
    }//GEN-LAST:event_dueCheckBoxItemStateChanged

    private void dueDateBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dueDateBrowseButtonActionPerformed
        if (evt.getID() == evt.ACTION_PERFORMED) {
            DateSelectionPanel pnl;
            Date date = getDueDate();
            if (date != null) {
                pnl = new DateSelectionPanel(date);
            } else {
                pnl = new DateSelectionPanel();
            }
            String title = NbBundle.getMessage(EditTaskPanel.class, "SelectDateLabel");
            DialogDescriptor d = new DialogDescriptor(pnl, title);
            d.setModal(true);
            d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
            d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
            Dialog dlg = TopManager.getDefault().createDialog(d);
            dlg.pack();
            dlg.show();
            
            if (d.getValue() == NotifyDescriptor.OK_OPTION) {
                Date due = pnl.getDate();
                if (due != null) {
                    SimpleDateFormat format = new SimpleDateFormat();
                    dueDateTextField.setText(format.format(due));
                }
            }
        }
    }//GEN-LAST:event_dueDateBrowseButtonActionPerformed
    
    // TODO prioGroup is unused; get rid of it 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JCheckBox fileCheckBox;
    private javax.swing.JLabel dueLabel;
    private javax.swing.ButtonGroup appendGroup;
    private javax.swing.JLabel addLabel;
    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JButton dueDateBrowseButton;
    private javax.swing.JTextField dueDateTextField;
    private javax.swing.JComboBox priorityComboBox;
    private javax.swing.JTextArea detailsTextArea;
    private javax.swing.JTextField fileTextField;
    private javax.swing.ButtonGroup prioGroup;
    private javax.swing.JCheckBox dueCheckBox;
    private javax.swing.JComboBox categoryCombo;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JLabel opt1Label;
    private javax.swing.JLabel subtaskLabel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JLabel fileLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox subtaskCombo;
    private javax.swing.JRadioButton beginningToggle;
    private javax.swing.JRadioButton endToggle;
    private javax.swing.JTextField lineTextField;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JLabel opt2Label;
    private javax.swing.JButton addSourceButton;
    private javax.swing.JLabel prioLabel;
    // End of variables declaration//GEN-END:variables
    
    String getSummary() {
        return descriptionTextField.getText().trim();
    }
    
    String getDetails() {
        return detailsTextArea.getText().trim();
    }
    
    String getCategory() {
        if (categoryCombo.getSelectedItem() == null) {
            return null;
        }
        return categoryCombo.getSelectedItem().toString().trim();
    }
    
    int getPrio() {
        return priorityComboBox.getSelectedIndex();
    }
    
    boolean hasAssociatedFilePos() {
        return fileCheckBox.isSelected();
    }
    
    void setAssociatedFilePos(boolean set) {
        fileCheckBox.setSelected(set);
    }
    
    String getFilename() {
        return fileTextField.getText().trim();
    }
    
    void setFilename(String filename) {
        fileTextField.setText(filename);
        fileTextField.setCaretPosition(fileTextField.getText().length()-1);
    }
    
    int getLineNumber() {
        try {
            int i = Integer.parseInt(lineTextField.getText());
            return i;
        } catch (NumberFormatException e) {
        }
        return 0;
    }
    
    void setLineNumber(int lineno) {
        lineTextField.setText(Integer.toString(lineno));
    }
    
    /**
     * get the due date
     * @return the due date or null
     */
    Date getDueDate() {
        Date ret;
        if (dueCheckBox.isSelected()) {
            try {
                ret = format.parse(dueDateTextField.getText());
            } catch (ParseException e) {
                ret = null;
            }
        } else {
            ret = null;
        }
        return ret;
    }
    
    /**
     * Set the due date field
     * @param d the due date
     */
    void setDueDate(Date d) {
        String s = null;
        
        if (d != null) {
            s = format.format(d);
        }

        if (s != null) {
            dueDateTextField.setText(s);
            dueCheckBox.setSelected(true);
            dueDateBrowseButton.setEnabled(true);
            dueDateTextField.setEnabled(true);
            dueDateTextField.setEditable(true);
        } else {
            dueDateTextField.setText("");
            dueDateBrowseButton.setEnabled(false);
            dueDateTextField.setEnabled(false);
            dueCheckBox.setSelected(false);            
            dueDateTextField.setEditable(false);
        }
        
    }
    
    // TODO - preserve this setting from run to run! (Unless you change
    // the default!)
    boolean getAppend() {
        appendDefault = endToggle.isSelected();
        return appendDefault;
    }

    Task getParentItem() {
        Object selected = subtaskCombo.getSelectedItem();
        if (selected instanceof UserTask) {
            return (UserTask)selected;
        } else { // selected string "None"
            return null;
        }
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == addSourceButton) {
           HelpCtx help = new HelpCtx("NewTask"); // NOI18N
           TopManager.getDefault().showHelp(help);
        }
    }

    class TaskCellRenderer extends DefaultListCellRenderer {
        private Icon taskIcon = null;
        
	public TaskCellRenderer() {
            super();
            taskIcon = new ImageIcon(Task.class.getResource (
              "/org/netbeans/modules/tasklist/core/task.gif")); // NOI18N
            
	}
     
	public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value,
                                                             index, isSelected,
                                                             cellHasFocus);
            if (value instanceof UserTask) {
                UserTask task = (UserTask)value;
                String desc = task.getSummary();
                // Indent subtasks
                while ((task.getParent() != null) &&
                       (task.getParent().getParent() != null)) {
                    desc = "       " + desc;
                    task = (UserTask)task.getParent();
                }
                setText(desc);
                setIcon(taskIcon);
            } else {
                setText(value.toString());
            }
            return c;
        }
    }
    
}
