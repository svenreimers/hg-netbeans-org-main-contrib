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

import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.openide.awt.Mnemonics;
import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.DialogDisplayer;

/**
 * Panel used to enter/edit a user task.
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
class EditTaskPanel extends JPanel implements ActionListener {
    private static final Logger LOGGER = TLUtils.getLogger(EditTaskPanel.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }
    
    private static String[] PERCENTS = {
        "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", // NOI18N
        "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" // NOI18N
    };
    
    /**
     * Parses values with % sign
     *
     * @param text text to parse
     * @return parsed value
     */
    private static int parsePercents(String text) throws NumberFormatException {
        text = text.trim();
        if (text.endsWith("%")) // NOI18N
            text = text.substring(0, text.length() - 1);
        int n = Integer.parseInt(text);
        if (n < 0 || n > 100)
            throw new NumberFormatException("Wrong range"); // NOI18N
        return n;
    }
    
    /**
     * InputVerifier for the combobox with percents
     */
    private static class PercentsInputVerifier extends InputVerifier {
        public boolean verify(javax.swing.JComponent input) {
            String p = ((JTextComponent) input).getText();
            try {
                parsePercents(p);
                return true;
            } catch (NumberFormatException e) {
                LOGGER.fine("wrong format");
                return false;
            }
        }
    }
    
    private SimpleDateFormat format;
    private ComboBoxModel prioritiesModel = 
        new DefaultComboBoxModel(SuggestionPriority.getPriorityNames());
    private ListCellRenderer priorityRenderer = new PriorityListCellRenderer();
    
    private static boolean appendDefault = Settings.getDefault().getAppend();
    
    /** 
     * Creates new form NewTodoItemPanel.
     *
     * @param editing true = no append/prepend options
     */
    public EditTaskPanel(boolean editing) {
        initComponents();
        initA11y();
        
        priorityComboBox.setSelectedIndex(2);
        
        format = new SimpleDateFormat();
        
        addSourceButton.addActionListener(this);
        
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
        
        jComboBoxProgress.setModel(new DefaultComboBoxModel(PERCENTS));
        ((JComponent) jComboBoxProgress.getEditor().getEditorComponent()).
            setInputVerifier(new EditTaskPanel.PercentsInputVerifier());
    }
    
    /**
     * Fills the panel with the values of a user task
     *
     * @param item a user task
     */
    public void fillPanel(UserTask item) {
        if (item.getSummary() != null) {
            descriptionTextField.setText(item.getSummary());
        }
        int p = item.getPriority().intValue() - 1;
        priorityComboBox.setSelectedIndex(p);
        if (item.hasAssociatedFilePos()) {
            fileTextField.setText(item.getFilename());
            if (fileTextField.getText().length() > 0)
                fileTextField.setCaretPosition(fileTextField.getText().length()-1);
            fileCheckBox.setSelected(true);
            if (item.getLineNumber() > 0) {
                lineTextField.setText(Integer.toString(item.getLineNumber()));
            }
        } else {
            fileCheckBox.setSelected(false);
        }
        detailsTextArea.setText(item.getDetails());
        setDueDate(item.getDueDate());

        // Initialize the Categories list
        String[] categories = ((UserTaskList) item.getList()).getCategories();
        if (categories.length > 0) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(categories);
            categoryCombo.setModel(model);
            LOGGER.fine("categories.size = " + categories.length); // NOI18N
        }
        categoryCombo.setSelectedItem(item.getCategory());
        
        jComboBoxProgress.setSelectedItem(item.getPercentComplete() + "%"); // NOI18N
        jCheckBoxComputeProgress.setSelected(item.isProgressComputed());
        
        DateFormat df = DateFormat.getDateTimeInstance(
            DateFormat.LONG, DateFormat.LONG);
        jLabelCreated.setText(df.format(new Date(item.getCreatedDate())));
        jLabelLastEdited.setText(df.format(new Date(item.getLastEditedDate())));
    }
    
    /**
     * Fills an object with the values from this panel
     *
     * @param task a user object
     */
    public void fillObject(UserTask task) {
        task.setSummary(descriptionTextField.getText().trim());
        task.setDetails(detailsTextArea.getText().trim());
        if (categoryCombo.getSelectedItem() == null)
            task.setCategory(""); // NOI18N
        else
            task.setCategory(categoryCombo.getSelectedItem().toString().trim());
        task.setPriority(SuggestionPriority.getPriority(priorityComboBox.getSelectedIndex() + 1));
        if (fileCheckBox.isSelected()) {
            task.setFilename(fileTextField.getText().trim());
            try {
                task.setLineNumber(Integer.parseInt(lineTextField.getText()));
            } catch (NumberFormatException e) {
                // TODO validation
            }
        } else {
            task.setFilename(null);
            task.setLine(null);
        }
        
        task.setDueDate(getDueDate());
        task.setProgressComputed(jCheckBoxComputeProgress.isSelected());
        if (!jCheckBoxComputeProgress.isSelected()) {
            task.setPercentComplete(
                parsePercents((String) jComboBoxProgress.getSelectedItem()));
        }
    }
    
    /**
     * Returns the value of the due date
     *
     * @return due date
     */
    private Date getDueDate() {
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
     * TODO - preserve this setting from run to run! (Unless you change
     * the default!)
     *
     * @return true = the task should be appended
     */
    public boolean getAppend() {
        appendDefault = endToggle.isSelected();
        return appendDefault;
    }
    
    /**
     * Set the due date field
     *
     * @param d the due date
     */
    private void setDueDate(Date d) {
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
            dueDateTextField.setText(""); // NOI18N
            dueDateBrowseButton.setEnabled(false);
            dueDateTextField.setEnabled(false);
            dueCheckBox.setSelected(false);
            dueDateTextField.setEditable(false);
        }
    }
    
    /**
     * Changes associated file position in the dialog
     *
     * @param file filename
     * @param line line number
     */
    public void setFilePosition(String file, int line) {
        fileTextField.setText(file);
        lineTextField.setText(String.valueOf(line));
    }
    
    void setAssociatedFilePos(boolean set) {
        fileCheckBox.setSelected(set);
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == addSourceButton) {
            HelpCtx help = new HelpCtx("NewTask"); // NOI18N
            
            // This copied from openide/deprecated/.../TopManager.showHelp:
            
            // Awkward but should work.
            // XXX could instead just make tasklist-usertasks.jar
            // depend on javahelp-api.jar.
            ClassLoader systemClassLoader = (ClassLoader)Lookup.getDefault().
            lookup(ClassLoader.class);
            
            try {
                Class c = systemClassLoader.
                loadClass("org.netbeans.api.javahelp.Help"); // NOI18N
                Object o = Lookup.getDefault().lookup(c);
                if (o != null) {
                    Method m = c.getMethod("showHelp",
                    new Class[] {HelpCtx.class}); // NOI18N
                    m.invoke(o, new Object[] {help});
                    return;
                }
            } catch (ClassNotFoundException cnfe) {
                // ignore - maybe javahelp module is not installed, not
                // so strange
            } catch (Exception e) {
                // potentially more serious
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            // Did not work.
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        addButtonGroup = new javax.swing.ButtonGroup();
        descLabel = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        detailsLabel = new javax.swing.JLabel();
        detailsScrollPane = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        prioLabel = new javax.swing.JLabel();
        priorityComboBox = new javax.swing.JComboBox();
        opt1Label = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox();
        opt2Label = new javax.swing.JLabel();
        jLabelProgress = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        fileCheckBox = new javax.swing.JCheckBox();
        fileTextField = new javax.swing.JTextField();
        lineLabel = new javax.swing.JLabel();
        lineTextField = new javax.swing.JTextField();
        dueCheckBox = new javax.swing.JCheckBox();
        dueDateTextField = new javax.swing.JTextField();
        dueDateBrowseButton = new javax.swing.JButton();
        addLabel = new javax.swing.JLabel();
        beginningToggle = new javax.swing.JRadioButton();
        endToggle = new javax.swing.JRadioButton();
        addSourceButton = new javax.swing.JButton();
        jCheckBoxComputeProgress = new javax.swing.JCheckBox();
        jComboBoxProgress = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelCreated = new javax.swing.JLabel();
        jLabelLastEdited = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12)));
        setPreferredSize(new java.awt.Dimension(400, 300));
        descLabel.setLabelFor(descriptionTextField);
        /*
        descLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "Brief_Description")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(descLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(descriptionTextField, gridBagConstraints);

    detailsLabel.setLabelFor(detailsTextArea);
    /*
    detailsLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "DetailsLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
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

    prioLabel.setLabelFor(priorityComboBox);
    /*
    prioLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "PriorityLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(prioLabel, gridBagConstraints);

    priorityComboBox.setModel(prioritiesModel);
    priorityComboBox.setRenderer(priorityRenderer);
    priorityComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            priorityComboBoxActionPerformed(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(priorityComboBox, gridBagConstraints);

    /*
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(opt1Label, gridBagConstraints);

    categoryLabel.setLabelFor(categoryCombo);
    /*
    categoryLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "CategoryLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(categoryLabel, gridBagConstraints);

    categoryCombo.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(categoryCombo, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(opt2Label, gridBagConstraints);

    jLabelProgress.setLabelFor(jCheckBoxComputeProgress);
    /*
    jLabelProgress.setText(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "ProgressLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jLabelProgress, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(jPanel1, gridBagConstraints);

    /*
    fileCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "AssociatedFile")); // NOI18N);
    */
    fileCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            fileCheckBoxItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(fileCheckBox, gridBagConstraints);

    fileTextField.setColumns(100);
    fileTextField.setEditable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(fileTextField, gridBagConstraints);

    lineLabel.setLabelFor(lineTextField);
    /*
    lineLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "LineLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(lineLabel, gridBagConstraints);

    lineTextField.setEditable(false);
    lineTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    add(lineTextField, gridBagConstraints);

    /*
    dueCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "DueDateCb")); // NOI18N();
    */
    dueCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            dueCheckBoxItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(dueCheckBox, gridBagConstraints);

    dueDateTextField.setColumns(14);
    dueDateTextField.setEditable(false);
    dueDateTextField.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(dueDateTextField, gridBagConstraints);

    /*
    dueDateBrowseButton.setText("...");
    */
    dueDateBrowseButton.setEnabled(false);
    dueDateBrowseButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dueDateBrowseButtonActionPerformed(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(dueDateBrowseButton, gridBagConstraints);

    addLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "AddTo")); // NOI18N();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(addLabel, gridBagConstraints);

    /*
    beginningToggle.setText(NbBundle.getMessage(EditTaskPanel.class, "BeginningList")); // NOI18N();
    */
    addButtonGroup.add(beginningToggle);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(beginningToggle, gridBagConstraints);

    /*
    endToggle.setText(NbBundle.getMessage(EditTaskPanel.class, "EndList")); // NOI18N();
    */
    addButtonGroup.add(endToggle);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(endToggle, gridBagConstraints);

    /*
    addSourceButton.setText(NbBundle.getMessage(EditTaskPanel.class, "AddToSource")); // NOI18N();
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    add(addSourceButton, gridBagConstraints);

    /*
    jCheckBoxComputeProgress.setText(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "ProgressComputed")); // NOI18N);
    */
    jCheckBoxComputeProgress.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            jCheckBoxComputeProgressItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jCheckBoxComputeProgress, gridBagConstraints);

    jComboBoxProgress.setEditable(true);
    jComboBoxProgress.setInputVerifier(new PercentsInputVerifier());
    jComboBoxProgress.setMinimumSize(new java.awt.Dimension(60, 21));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jComboBoxProgress, gridBagConstraints);

    jLabel5.setText(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "CreatedLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jLabel5, gridBagConstraints);

    jLabel6.setText(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "LastEditedLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jLabel6, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jLabelCreated, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    add(jLabelLastEdited, gridBagConstraints);

    }//GEN-END:initComponents

    private void jCheckBoxComputeProgressItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxComputeProgressItemStateChanged
        jComboBoxProgress.setEnabled(!jCheckBoxComputeProgress.isSelected());
    }//GEN-LAST:event_jCheckBoxComputeProgressItemStateChanged

    private void priorityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priorityComboBoxActionPerformed
        // I don't know why JComboBox does not use my renderer to draw 
        // selected value
        int sel = priorityComboBox.getSelectedIndex();
        priorityComboBox.setForeground(PriorityListCellRenderer.COLORS[sel]);
    }//GEN-LAST:event_priorityComboBoxActionPerformed

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
        */

        Mnemonics.setLocalizedText(descLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "Brief_Description")); // NOI18N
        Mnemonics.setLocalizedText(detailsLabel, NbBundle.getMessage(
                    EditTaskPanel.class, "DetailsLabel")); // NOI18N
        Mnemonics.setLocalizedText(prioLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "PriorityLabel")); // NOI18N
        Mnemonics.setLocalizedText(fileCheckBox, NbBundle.getMessage(
                 EditTaskPanel.class, "AssociatedFile")); // NOI18N
        Mnemonics.setLocalizedText(categoryLabel, NbBundle.getMessage(
                     EditTaskPanel.class, "CategoryLabel")); // NOI18N
        Mnemonics.setLocalizedText(jLabelProgress, NbBundle.getMessage(
            EditTaskPanel.class, "ProgressLabel")); // NOI18N
        Mnemonics.setLocalizedText(jCheckBoxComputeProgress, NbBundle.getMessage(
            EditTaskPanel.class, "ProgressComputed")); // NOI18N
        Mnemonics.setLocalizedText(lineLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "LineLabel")); // NOI18N
        Mnemonics.setLocalizedText(dueCheckBox, NbBundle.getMessage(
                   EditTaskPanel.class, "DueDateCb")); // NOI18N
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

        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_NewTask")); // NOI18N
        descriptionTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Brief_Description")); // NOI18N
        detailsTextArea.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Details")); // NOI18N
        priorityComboBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Priority")); // NOI18N
        categoryCombo.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Category")); // NOI18N
        fileTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_File")); // NOI18N

        // We're using a checkbox to "label" the textfield - of course JCheckBox
        // doesn't have a setLabelFor (since it is itself an input component)
        // so we have to label the associated component ourselves
        fileTextField.getAccessibleContext().setAccessibleName(fileCheckBox.getText());
        dueDateTextField.getAccessibleContext().setAccessibleName(dueCheckBox.getText());

        lineTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Line")); // NOI18N
        dueDateTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Due")); // NOI18N
        fileCheckBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_FileCb")); // NOI18N
        dueCheckBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_DueCb")); // NOI18N
        beginningToggle.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Beginning")); // NOI18N
        endToggle.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_End")); // NOI18N
        addSourceButton.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_AddSource")); // NOI18N

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
            String title = NbBundle.getMessage(EditTaskPanel.class, "SelectDateLabel"); // NOI18N
            DialogDescriptor d = new DialogDescriptor(pnl, title);
            d.setModal(true);
            d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
            d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
            Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup addButtonGroup;
    private javax.swing.JLabel addLabel;
    private javax.swing.JButton addSourceButton;
    private javax.swing.JRadioButton beginningToggle;
    private javax.swing.JComboBox categoryCombo;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JTextArea detailsTextArea;
    private javax.swing.JCheckBox dueCheckBox;
    private javax.swing.JButton dueDateBrowseButton;
    private javax.swing.JTextField dueDateTextField;
    private javax.swing.JRadioButton endToggle;
    private javax.swing.JCheckBox fileCheckBox;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JCheckBox jCheckBoxComputeProgress;
    private javax.swing.JComboBox jComboBoxProgress;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelCreated;
    private javax.swing.JLabel jLabelLastEdited;
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JTextField lineTextField;
    private javax.swing.JLabel opt1Label;
    private javax.swing.JLabel opt2Label;
    private javax.swing.JLabel prioLabel;
    private javax.swing.JComboBox priorityComboBox;
    // End of variables declaration//GEN-END:variables
}
