/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import java.util.*;
import java.awt.event.*;
import java.io.*;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;

/**
 * Dialog that enables users to set variable values before running the command.
 *
 * @author  Martin Entlicher
 */
public class VariableInputDialog extends javax.swing.JPanel {

    //public static final String PROMPT_DIR = "_DIR";
    //public static final String PROMPT_FILE = "_FILE";
    //public static final String PROMPT_DATE_CVS = "_DATE_CVS";
    //public static final String PROMPT_DEFAULT_VALUE_SEPARATOR = "\"";
    
    private static final int TEXTFIELD_COLUMNS = 20;
    private static final int TEXTAREA_COLUMNS = 40;
    private static final int TEXTAREA_ROWS = 6;
    private boolean validInput = false;
    private javax.swing.JLabel[] varPromptLabels = new javax.swing.JLabel[0];
    private javax.swing.JLabel[] filePromptLabels = new javax.swing.JLabel[0];
    private javax.swing.JLabel[] userPromptLabels = new javax.swing.JLabel[0];
    private String[]             userPromptLabelTexts = null;
    private javax.swing.JTextArea[] filePromptAreas = new javax.swing.JTextArea[0];
    private javax.swing.JTextField[] varPromptFields = new javax.swing.JTextField[0];
    private javax.swing.JTextField[] userPromptFields = new javax.swing.JTextField[0];
    private javax.swing.JCheckBox[] varAskCheckBoxes = new javax.swing.JCheckBox[0];
    private int labelOffset = 0;
    private String[] fileNames = new String[0];
    
    private VariableInputDialog.FilePromptDocumentListener docListener = null;
    private Object docIdentif = null;
    
    private VcsFileSystem fileSystem = null;
    private Hashtable vars = null;
    private boolean expert = false;
    
    private VariableInputDescriptor inputDescriptor;
    
    private ArrayList actionList = new ArrayList();
    private ActionListener closeListener = null;
    private int promptAreaNum = 0;
    
    static final long serialVersionUID = 8363935602008486018L;
    
    /** Creates new form VariableInputDialog. This JPanel should be used
     * with DialogDescriptor to get the whole dialog.
     * @param files the files to get the input for
     * @param actionListener the listener to OK and Cancel buttons
     */
    public VariableInputDialog(String[] files, VariableInputDescriptor inputDescriptor, boolean expert) {
        initComponents();
        this.inputDescriptor = inputDescriptor;
        this.expert = expert;
        initComponentsFromDescriptor();
        //initFileLabel(files[0]);
    }

    public void setFilePromptDocumentListener(VariableInputDialog.FilePromptDocumentListener docListener) {
        this.docListener = docListener;
    }
    
    public void setFilePromptDocumentListener(VariableInputDialog.FilePromptDocumentListener docListener, Object docIdentif) {
        this.docListener = docListener;
        this.docIdentif = docIdentif;
    }
    
    public VariableInputDialog.FilePromptDocumentListener getFilePromptDocumentListener() {
        return this.docListener;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        variablePanel = new javax.swing.JPanel();
        promptEachSeparator = new javax.swing.JSeparator();
        promptEachCheckBox = new javax.swing.JCheckBox();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        variablePanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 12, 12);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(variablePanel, gridBagConstraints1);
        
        
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 0, 12);
        add(promptEachSeparator, gridBagConstraints1);
        
        
        promptEachCheckBox.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.promptEachCheckBox.text"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(promptEachCheckBox, gridBagConstraints1);
        
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel variablePanel;
    private javax.swing.JSeparator promptEachSeparator;
    private javax.swing.JCheckBox promptEachCheckBox;
    // End of variables declaration//GEN-END:variables

    private void initComponentsFromDescriptor() {
        int gridy = 0;
        if (inputDescriptor != null) {
            VariableInputComponent[] components = inputDescriptor.components();
            for (int i = 0; i < components.length; i++) {
                gridy = addComponent(components[i], gridy);
            }
        }
        labelOffset = gridy;
    }
    
    private int addComponent(VariableInputComponent component, int gridy) {
        if (expert || !component.isExpert()) {
            int componentId = component.getComponent();
            switch (componentId) {
                case VariableInputDescriptor.INPUT_PROMPT_FIELD:
                    addVarPromptField(component, gridy);
                    gridy++;
                    break;
                case VariableInputDescriptor.INPUT_PROMPT_AREA:
                    addVarPromptArea(component, gridy, promptAreaNum++);
                    gridy += 2;
                    break;
                case VariableInputDescriptor.INPUT_ASK:
                    addAskChBox(component, gridy);
                    gridy++;
                    break;
                case VariableInputDescriptor.INPUT_SELECT_RADIO:
                    gridy = addSelectRadio(component, gridy);
                    break;
                case VariableInputDescriptor.INPUT_SELECT_COMBO:
                    addSelectCombo(component, gridy);
                    gridy++;
                    break;
            }
        }
        return gridy;
    }
    
    /**
     * Test if the input is valid and warn the user if it is not.
     * @return true if the input is valid, false otherwise
     */
    private boolean testValidInput() {
        VariableInputValidator validator = inputDescriptor.validate();
        boolean valid = validator.isValid();
        if (!valid) {
            TopManager.getDefault().notify(new NotifyDescriptor.Message(validator.getMessage(), NotifyDescriptor.Message.WARNING_MESSAGE));
        }
        return valid;
    }
    
    public ActionListener getActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getID() == ActionEvent.ACTION_PERFORMED) {
                    if (NotifyDescriptor.OK_OPTION.equals(ev.getSource())) {
                        if (testValidInput()) {
                            validInput = true;
                            if (closeListener != null) {
                                closeListener.actionPerformed(ev);
                                closeListener = null;
                            }
                            //setVisible(false);
                        }
                        //writeFileContents();
                        //processActions(); -- do not do it now in AWT !
                    } else {
                        validInput = false;
                        freeReferences();
                    }
                }
            }
        };
    }
    
    public void setCloseListener(ActionListener closeListener) {
        this.closeListener = closeListener;
    }
    
    private void freeReferences() {
        fileSystem = null;
        docIdentif = null;
        docListener = null;
    }
    
    private void addActionToProcess(ActionListener l) {
        actionList.add(l);
    }
    
    public void processActions() {
        for (Iterator it = actionList.iterator(); it.hasNext(); ) {
            ActionListener listener = (ActionListener) it.next();
            listener.actionPerformed(null);
        }
        freeReferences();
    }
    
    /**
     * Test, whether the input in this dialog is valid and variables can be assigned.
     * @return true if the input is valid, false otherwise
     */
    public boolean isValidInput() {
        return validInput;
    }

    /**
     * Set the VCS file system, that is needed to execute the selector command
     * and the variables table.
     */
    public void setVCSFileSystem(VcsFileSystem fileSystem, Hashtable vars) {
        this.fileSystem = fileSystem;
        this.vars = vars;
    }
    
    /*
     * Set the file name.
     *
    private void initFileLabel(String file) {
        if (file == null || file.trim().length() == 0) return;
        javax.swing.JLabel label;
        if (file.endsWith(java.io.File.separator)) {
            file = file.substring(0, file.length() - 1);
            if (file.trim().length() == 0) return;
            label = new javax.swing.JLabel(
                java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.folderLabel"),
                                               new Object[] { file }));
        } else {
            label = new javax.swing.JLabel(
                java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.fileLabel"),
                                               new Object[] { file }));
        }
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        //gridBagConstraints1.gridx = 0;
        //gridBagConstraints1.gridy = i + labelOffset;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (12, 12, 0, 12);
        this.add(label, gridBagConstraints1, 0);
        //pack();
    }
     */
    
    private void addVarPromptField(final VariableInputComponent component, int gridy) {
        String varLabel = component.getLabel();
        //String varType = (String) varLabels.get(varLabel);
        javax.swing.JLabel label = new javax.swing.JLabel(varLabel);
        final javax.swing.JTextField field = new javax.swing.JTextField(TEXTFIELD_COLUMNS);
        String value = component.getValue();
        if (value != null) field.setText(value);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 8);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = gridy;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
        variablePanel.add(label, gridBagConstraints1);
        variablePanel.add(field, gridBagConstraints2);
        VcsUtilities.removeEnterFromKeymap(field);
        String selector = component.getSelector();
        if (selector != null) {
            if (VariableInputDescriptor.SELECTOR_DIR.equals(selector)) {
                addBrowseDir(variablePanel, field, gridy);
            } else if (VariableInputDescriptor.SELECTOR_FILE.equals(selector)) {
                addBrowseFile(variablePanel, field, gridy);
            } else if (VariableInputDescriptor.SELECTOR_DATE_CVS.equals(selector)) {
                addDateCVS(variablePanel, field, gridy);
            } else if (selector.indexOf(VariableInputDescriptor.SELECTOR_CMD) == 0) {
                addSelector(variablePanel, field, gridy,
                            selector.substring(VariableInputDescriptor.SELECTOR_CMD.length()));
            }
        }
        /*
        field.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent aevt) {
                System.out.println("action Performed: "+aevt);
                component.setValue(field.getText());
                if ((aevt.getID() & ActionEvent.KEY_EVENT_MASK) != 0) {
                    System.out.println("KEY Action !");
                    VariableInputDialog.this.dispatchEvent(new ActionEvent(aevt.getSource(), aevt.getID(), aevt.getActionCommand(), aevt.getModifiers()));
                }
            }
        });
        */
        field.addInputMethodListener(new InputMethodListener() {
            public void caretPositionChanged(InputMethodEvent event) {
            }
            public void inputMethodTextChanged(InputMethodEvent event) {
                component.setValue(field.getText());
            }
        });
        field.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fevt) {}
            public void focusLost(FocusEvent fevt) {
                component.setValue(field.getText());
            }
        });
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                component.setValue(field.getText());
                if (vars != null) vars.put(component.getVariable(), field.getText());
            }
        });
    }
    
    private void addBrowseDir(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Browse"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseDirDialog chooseDir = new ChooseDirDialog(new javax.swing.JFrame(), new File(field.getText ()));
                VcsUtilities.centerWindow (chooseDir);
                chooseDir.show();
                String selected = chooseDir.getSelectedDir();
                if (selected == null) {
                    //D.deb("no directory selected"); // NOI18N
                    return ;
                }
                field.setText(selected);
            }
        });
    }
    
    private void addBrowseFile(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Browse"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseFileDialog chooseFile = new ChooseFileDialog(new javax.swing.JFrame(), new File(field.getText ()), false);
                VcsUtilities.centerWindow (chooseFile);
                chooseFile.show();
                String selected = chooseFile.getSelectedFile();
                if (selected == null) {
                    //D.deb("no directory selected"); // NOI18N
                    return ;
                }
                field.setText(selected);
            }
        });
    }
    
    private void addDateCVS(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y) {
        field.setToolTipText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.DateCVS"));
    }

    private void addSelector(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y,
                             final String commandName) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Select"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Thread selection = new Thread("VCS Variable Selector Command") {
                    public void run() {
                        String selected = getSelectorText(commandName, field.getText());
                        //System.out.println("selected = "+selected);
                        if (selected != null) {
                            field.setText(selected);
                        }
                    }
                };
                selection.start();
            }
        });
    }
    
    private String getSelectorText(String commandName, String oldText) {
        VcsCommand cmd = fileSystem.getCommand(commandName);
        //OutputContainer container = new OutputContainer(cmd);
        Hashtable varsCopy = new Hashtable(vars);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCopy);
        //ec.setErrorNoRegexListener(container);
        //ec.setOutputNoRegexListener(container);
        //ec.setErrorContainer(container);
        final StringBuffer selectorOutput = new StringBuffer();
        final boolean[] selectorMatched = new boolean[] { false };
        ec.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                if (elements != null) {
                    selectorMatched[0] = true;
                    selectorOutput.append(VcsUtilities.array2string(elements).trim());
                }
            }
        });
        CommandsPool pool = fileSystem.getCommandsPool();
        int preprocessStatus = pool.preprocessCommand(ec, varsCopy);
        if (preprocessStatus != CommandsPool.PREPROCESS_DONE) return null;
        pool.startExecutor(ec);
        pool.waitToFinish(ec);
        if (ec.getExitStatus() == VcsCommandExecutor.SUCCEEDED
            && selectorMatched[0]) {
            return selectorOutput.toString();
        } else return null;
    }
    
    private void addAskChBox(final VariableInputComponent component, int gridy) {
        String label = component.getLabel();
        final javax.swing.JCheckBox chbox = new javax.swing.JCheckBox(" "+label);
        String askDefault = component.getValue();
        if (askDefault != null) {
            chbox.setSelected(Boolean.TRUE.toString().equalsIgnoreCase(askDefault));
        }
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints1.gridwidth = 2;
        variablePanel.add(chbox, gridBagConstraints1);
        chbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean selected = chbox.isSelected();
                String valueSelected = component.getValueSelected();
                String valueUnselected = component.getValueUnselected();
                if (selected && valueSelected != null) {
                    component.setValue(valueSelected);
                } else if (!selected && valueUnselected != null) {
                    component.setValue(valueUnselected);
                } else {
                    component.setValue(selected ? Boolean.TRUE.toString() : "");
                }
            }
        });
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                boolean selected = chbox.isSelected();
                String valueSelected = component.getValueSelected();
                String valueUnselected = component.getValueUnselected();
                if (selected && valueSelected != null) {
                    component.setValue(valueSelected);
                } else if (!selected && valueUnselected != null) {
                    component.setValue(valueUnselected);
                } else {
                    component.setValue(selected ? Boolean.TRUE.toString() : "");
                }
                if (vars != null) vars.put(component.getVariable(), component.getValue());
            }
        });
    }

    private void addVarPromptArea(final VariableInputComponent component, int gridy, final int promptAreaNum) {
        String message = component.getLabel();
        javax.swing.JLabel label = new javax.swing.JLabel(message);
        java.awt.Dimension dimension = component.getDimension();
        if (dimension == null) dimension = new java.awt.Dimension(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
        final javax.swing.JTextArea area = new javax.swing.JTextArea(dimension.width, dimension.height);
        javax.swing.JScrollPane scrollArea = new javax.swing.JScrollPane(area);
        //javax.swing.JTextField field = new javax.swing.JTextField(TEXTFIELD_COLUMNS);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = gridy + 1;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints2.gridwidth = 2;
        area.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        variablePanel.add(label, gridBagConstraints1);
        variablePanel.add(scrollArea, gridBagConstraints2);
        //fileLabels.addElement(label);
        //areas.addElement(area);
        //VcsUtilities.removeEnterFromKeymap(field);
        //fileNames.add(filePrompts.get(message));
        initArea(area, component.getValue());
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                //component.setValue(chbox.isSelected() ? Boolean.TRUE.toString() : "");
                writeFileContents(area, component.getValue(), promptAreaNum);
                if (vars != null) vars.put(component.getVariable(), component.getValue());
            }
        });
    }

    private int addSelectRadio(final VariableInputComponent component, int gridy) {
        String message = component.getLabel();
        if (message != null && message.length() > 0) {
            javax.swing.JLabel label = new javax.swing.JLabel(message);
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = gridy;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 4, 0);
            gridBagConstraints1.gridwidth = 2;
            variablePanel.add(label, gridBagConstraints1);
            gridy++;
        }
        final VariableInputComponent[] subComponents = component.subComponents();
        final javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        for (int i = 0; i < subComponents.length; i++) {
            addRadioButton(subComponents[i], gridy++, group);
        }
        selectButton(component.getValue(), group);
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                int selected = 0;
                Enumeration enum = group.getElements();
                for (int i = 0; enum.hasMoreElements(); i++) {
                    javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
                    if (radio.isSelected()) {
                        selected = i;
                        break;
                    }
                }
                component.setValue(subComponents[selected].getValue());
                if (vars != null) vars.put(component.getVariable(), component.getValue());
            }
        });
        return gridy;
    }
    
    private void addRadioButton(final VariableInputComponent component, int gridy,
                                javax.swing.ButtonGroup group) {
        String label = component.getLabel();
        javax.swing.JRadioButton button = new javax.swing.JRadioButton(label);
        group.add(button);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 0, 4, 0);
        gridBagConstraints1.gridwidth = 2;
        variablePanel.add(button, gridBagConstraints1);
    }
    
    private static void selectButton(String value, javax.swing.ButtonGroup group) {
        int index;
        try {
            index = Integer.parseInt(value);
        } catch (NumberFormatException exc) {
            index = 0;
        }
        Enumeration enum = group.getElements();
        for (int i = 0; enum.hasMoreElements(); i++) {
            javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
            if (i == index) radio.setSelected(true);
        }
    }

    private void addSelectCombo(final VariableInputComponent component, int gridy) {
        String message = component.getLabel();
        if (message != null && message.length() > 0) {
            javax.swing.JLabel label = new javax.swing.JLabel(message);
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = gridy;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 0);
            gridBagConstraints1.gridwidth = 1;
            variablePanel.add(label, gridBagConstraints1);
        }
        final VariableInputComponent[] subComponents = component.subComponents();
        int items = subComponents.length;
        final String[] labels = new String[items];
        final String[] values = new String[items];
        for (int i = 0; i < items; i++) {
            labels[i] = subComponents[i].getLabel();
            values[i] = subComponents[i].getValue();
        }
        final javax.swing.JComboBox comboBox = new javax.swing.JComboBox(labels);
        java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = gridy;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints2.gridwidth = 1;
        variablePanel.add(comboBox, gridBagConstraints2);
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                int selected = comboBox.getSelectedIndex();
                component.setValue(subComponents[selected].getValue());
                if (vars != null) vars.put(component.getVariable(), component.getValue());
            }
        });
    }

    /**
     * Create additional user labels and text fields.
     * @param varLabels Table of labels and default values.
     */
    public void setUserParamsPromptLabels(Table varLabels, String advancedName) {
        Vector labels = new Vector();
        Vector fields = new Vector();
	int i = 0;
	this.userPromptLabelTexts = new String[varLabels.size()];
        if (advancedName != null && varLabels.size() > 0) {
            javax.swing.JSeparator sep = new javax.swing.JSeparator();
            javax.swing.JLabel label = new javax.swing.JLabel(
                java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.advancedNameLabel"),
                                               new Object[] { advancedName }));
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridy = i + labelOffset;
            //gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 0);
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridy = i + labelOffset + 1;
            //gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            //gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
            if (labelOffset > 0) variablePanel.add(sep, gridBagConstraints1);
            variablePanel.add(label, gridBagConstraints2);
            labelOffset += 2;
        }
        for(Enumeration enum = varLabels.keys(); enum.hasMoreElements(); i++) {
            String labelStr = (String) enum.nextElement();
            this.userPromptLabelTexts[i] = labelStr;
            javax.swing.JLabel label = new javax.swing.JLabel(labelStr+":");
            javax.swing.JTextField field = new javax.swing.JTextField(TEXTFIELD_COLUMNS);
            field.setText((String) varLabels.get(labelStr));
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = i + labelOffset;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 8);
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = i + labelOffset;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
            variablePanel.add(label, gridBagConstraints1);
            variablePanel.add(field, gridBagConstraints2);
            labels.addElement(label);
            fields.addElement(field);
            VcsUtilities.removeEnterFromKeymap(field);
        }
        labelOffset += varLabels.size();
        //pack();
        this.userPromptLabels = (javax.swing.JLabel[]) labels.toArray(new javax.swing.JLabel[0]);
        this.userPromptFields = (javax.swing.JTextField[]) fields.toArray(new javax.swing.JTextField[0]);
    }

    /**
     * Set whether to show check box for prompt on next file. When not called default is true.
     * @param show true to show, false not to show
     */
    public void showPromptEach(boolean show) {
        promptEachCheckBox.setVisible(show);
        promptEachSeparator.setVisible(show);
        //pack();
    }

    /**
     * Set whether the initial state of the check box for prompt on next file. When not called default is false.
     * @param prompt the initial state
     */
    public void setPromptEach(boolean prompt) {
        promptEachCheckBox.setSelected(prompt);
    }

    /**
     * Get the variable prompt values.
     */
    public String[] getVarPromptValues() {
        String[] varValues = new String[varPromptFields.length];
        for(int i = 0; i < varPromptFields.length; i++) {
            varValues[i] = varPromptFields[i].getText();
        }
        return varValues;
    }

    /**
     * Get the variable ask values.
     */
    public String[] getVarAskValues() {
        String[] varValues = new String[varAskCheckBoxes.length];
        for(int i = 0; i <  varAskCheckBoxes.length; i++) {
            varValues[i] = (varAskCheckBoxes[i].isSelected()) ? "true" : "";
        }
        return varValues;
    }

    /**
     * Get the table of additional user variables labels and values.
     */
    public Hashtable getUserParamsValuesTable() {
        Hashtable result = new Hashtable();
        for(int i = 0; i < userPromptLabels.length; i++) {
            result.put(userPromptLabelTexts[i], userPromptFields[i].getText());
        }
        return result;
    }

    /**
     * Whether to prompt for variables for each file separately or use these variables for all files.
     */
    public boolean getPromptForEachFile() {
        return promptEachCheckBox.isSelected();
    }
    
    /**
     * Read content of input files into Text Areas.
     */
    private void initArea(javax.swing.JTextArea filePromptArea, String fileName) {
        //for(int i = 0; i < filePromptAreas.length; i++) {
        //    String name = fileNames[i];
            if (fileName.length() == 0) return ;
            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                try {
                    filePromptArea.read(new FileReader(file), null);
                } catch (FileNotFoundException exc) {
                    TopManager.getDefault().notifyException(exc);
                } catch (IOException exc) {
                    TopManager.getDefault().notifyException(exc);
                }
            }
        //}
    }
    
    private void writeFileContents(javax.swing.JTextArea filePromptArea, String fileName, int promptAreaNum) {
        //for(int i = 0; i < filePromptAreas.length; i++) {
            if (docListener != null) docListener.filePromptDocumentCleanup(filePromptArea, promptAreaNum, docIdentif);
            //String name = fileNames[i];
            if (fileName.length() == 0) return ;
            File file = new File(fileName);
            try {
                filePromptArea.write(new FileWriter(file));
            } catch (IOException exc) {
                TopManager.getDefault().notifyException(exc);
            }
        //}
    }
    
    public interface FilePromptDocumentListener {
        public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif);
    }
}
