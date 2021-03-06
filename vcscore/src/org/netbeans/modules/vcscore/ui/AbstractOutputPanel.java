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



package org.netbeans.modules.vcscore.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.vcscore.commands.CommandOutputCollector;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.netbeans.modules.vcscore.commands.RegexErrorListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.SaveToFilePanel;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * AbstractOutputPanel.java
 *
 * Created on December 21, 2003, 11:16 AM
 * @author  Richard Gregor
 */
public abstract class AbstractOutputPanel extends javax.swing.JPanel {
    
   // private JMenuItem kill;
    private ArrayList killActionListeners = new ArrayList();
    private JTextArea stdDataOutput;
    private JTextArea errDataOutput;
    private boolean ignoreFailure;
    private CommandOutputCollector outputCollector;
    private Action discardAction;
    private JButton navigationButton;

    /** Creates new form OutputPanel */
    public AbstractOutputPanel() {
        initComponents(); 
        initPopupMenu();
        if (Boolean.getBoolean("netbeans.vcs.dev")) {
            addDataOutputButtons();
        }
        Font font = btnErr.getFont();
        FontMetrics fm = btnErr.getFontMetrics(font);
        int height = fm.getHeight();
        Dimension dim = toolbar.getPreferredSize();
        toolbar.setPreferredSize(new Dimension(dim.width,height+6));   
        toolbar.setMinimumSize(new Dimension(dim.width,height+6));
        toolbar.setMaximumSize(new Dimension(dim.width,height+6));
        dim = btnStop.getPreferredSize();
        btnStop.setPreferredSize(new Dimension(dim.width,height+6));
        btnStop.setMinimumSize(new Dimension(dim.width,height+6));
        btnStop.setMaximumSize(new Dimension(dim.width,height+6));       
        if (getErrOutputArea() != null) {
            getErrOutputArea().getDocument().addDocumentListener(new OutputButtonEnabler(btnErr));
        }
        if (btnDataStd != null && getDataStdOutputArea() != null) {
            getDataStdOutputArea().getDocument().addDocumentListener(new OutputButtonEnabler(btnDataStd));
        }
        if (btnDataErr != null && getDataErrOutputArea() != null) {
            getDataErrOutputArea().getDocument().addDocumentListener(new OutputButtonEnabler(btnDataErr));
        }
        setStandardContent();
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(       
        KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK),
        "discard"); //NOI18N
        getActionMap().put("discard", discardAction);//NOI18N
        if (Utilities.getOperatingSystem() == Utilities.OS_MAC &&
            System.getProperty("java.version").equals("1.4.2_05")) {
            macOSHack();
        }
    }
    
    /**
     * Increase the preferred size of buttons and labels because of issue #51404.
     * TODO Remove this when the problem is fixed.
     */
    private void macOSHack() {
        adjustPreferredSize(btnStd);
        adjustPreferredSize(btnErr);
        adjustPreferredSize(btnStop);
        if (btnDataStd != null) adjustPreferredSize(btnDataStd);
        if (btnDataErr != null) adjustPreferredSize(btnDataErr);
        adjustPreferredSize(lblStatus);
    }
    
    private void adjustPreferredSize(JComponent c) {
        Dimension d = c.getPreferredSize();
        d.width += 10;
        c.setPreferredSize(d);
    }
    
    /**
     * Set whether the failure of the command should be ignored or not.
     */
    public void setIgnoreFailure(boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
    }
    
    /**
     * Set the output collector.
     */
    public void setOutputCollector(CommandOutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }
    
    /**
     * Get the output collector.
     */
    protected CommandOutputCollector getOutputCollector() {
        return outputCollector;
    }
    
    protected void initPopupMenu() {
        JPopupMenu menu = new JPopupMenu();        
        java.awt.event.ActionListener discardAllListener = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discardAll();
            }
        };
        java.awt.event.ActionListener saveListener = new java.awt.event.ActionListener () {
           public void actionPerformed (java.awt.event.ActionEvent event) {
               saveToFile();
           }
        };
        discardAction = new AbstractAction(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab")) { //NOI18N
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discard(AbstractOutputPanel.this);
            }
        };    
        JMenuItem discardTab = new JMenuItem();//NOI18N
        discardTab.setAction(discardAction);
        discardTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
        discardAll.addActionListener(discardAllListener);
        JMenuItem save = new JMenuItem (NbBundle.getBundle (OutputPanel.class).getString("CMD_Save"));//NOI18N
        save.addActionListener (saveListener);
        menu.add(save);
        menu.addSeparator();
        menu.add(discardTab);
        menu.add(discardAll);
        JPopupMenu viewMenu;
        if (isViewTextLogEnabled()) {
            viewMenu = new JPopupMenu();
            JMenuItem viewText = new JMenuItem (NbBundle.getBundle (OutputPanel.class).getString("CMD_ViewText"));//NOI18N
            viewText.addActionListener ( new java.awt.event.ActionListener () {
               public void actionPerformed (java.awt.event.ActionEvent event) {
                   viewTextLog();
               }
            });
            discardTab = new JMenuItem();//NOI18N
            discardTab.setAction(discardAction);
            discardTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK));
            discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
            discardAll.addActionListener(discardAllListener);
            save = new JMenuItem (NbBundle.getBundle (OutputPanel.class).getString("CMD_Save"));//NOI18N
            save.addActionListener (saveListener);
            viewMenu.add(viewText);
            viewMenu.add(save);
            viewMenu.addSeparator();
            viewMenu.add(discardTab);
            viewMenu.add(discardAll);
        } else {
            viewMenu = menu;
        }
        
        if(getStdOutputArea() != null)
            getStdOutputArea().add(viewMenu);
        if(getErrOutputArea() != null)
            getErrOutputArea().add(menu);
        
        PopupListener popupListener = new PopupListener(menu);
        PopupListener popupListenerView = new PopupListener(viewMenu);
        getErrComponent().addMouseListener(popupListener);
        adjustInputMap(getErrComponent());
        getStdComponent().addMouseListener(popupListenerView);
        adjustInputMap(getStdComponent());
                       
        JComponent c = getDataStdComponent();
        if (c != null){
            c.addMouseListener(popupListener);
            adjustInputMap(c);
        }
        c = getDataErrComponent();
        if (c != null)  {
            c.addMouseListener(popupListener);
            adjustInputMap(c);
        }
        this.addMouseListener(popupListener);
        toolbar.addMouseListener(popupListener);
        scroll.addMouseListener(popupListenerView);
        
    }
    
    private void adjustInputMap(JComponent c){
        c.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK),
        "discard");
        c.getActionMap().put("discard", discardAction);//NOI18N
    }
    
    private void saveToFile() {
        SaveToFilePanel pnl = new SaveToFilePanel();
        pnl.setCurrentPanel(btnStd.isSelected() ? 0 : (btnErr.isSelected() ? 1 : 0));//jTabbedPane1.getSelectedIndex());
        for (int i = 0; i < 4; i++) {
            pnl.setEnabledOutput(i, outputCollector.isCmdOutput(i));
        }
        java.io.File file = null;
        NotifyDescriptor descriptor = new DialogDescriptor(pnl, NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFile.title"));//NOI18N
        boolean ok = false;
        while (!ok) {
            ok = true;
            Object retVal = DialogDisplayer.getDefault().notify(descriptor);
            if (retVal.equals(NotifyDescriptor.OK_OPTION)) {
                java.io.File init = new java.io.File(pnl.getFile());
                if (!init.isAbsolute()) init = new java.io.File(System.getProperty("user.home"), init.getPath());
                if (init.exists()) {
                    NotifyDescriptor mess = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(SaveToFilePanel.class, "SaveToFile.fileExistsQuestion", init.getName()), //NOI18N
                    NotifyDescriptor.YES_NO_OPTION);
                    Object rVal = DialogDisplayer.getDefault().notify(mess);
                    if (!rVal.equals(NotifyDescriptor.YES_OPTION)) {
                        ok = false;
                        continue;
                    }
                    file = init;
                } else {
                    java.io.File parent = init.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();
                    file = init;
                }
            } else {
                return;
            }
        }
        final java.io.File finFile = file;
        final SaveToFilePanel finPnl = pnl;
        
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                java.io.BufferedWriter writer = null;
                try {
                    writer = new java.io.BufferedWriter(new java.io.FileWriter(finFile));
                    if (finPnl.includeStdOut()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addTextOutputListener(new TextOutputListener() {
                                public void outputLine(String line) {
                                    try {
                                        fwriter.write(line);
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getStdOutputArea();
                            if (outputArea != null) {
                                writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                                writer.newLine();
                            }
                        }
                    }
                    if (finPnl.includeStdErr()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addTextErrorListener(new TextErrorListener() {
                                public void outputLine(String line) {
                                    try {
                                        fwriter.write(line);
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getErrOutputArea();
                            writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                            writer.newLine();
                        }
                    }
                    if (finPnl.includeDatOut()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addRegexOutputListener(new RegexOutputListener() {
                                public void outputMatchedGroups(String[] elements) {
                                    try {
                                        fwriter.write(VcsUtilities.arrayToString(elements));
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getDataStdOutputArea();
                            writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                            writer.newLine();
                        }
                    }
                    if (finPnl.includeDatErr()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addRegexErrorListener(new RegexErrorListener() {
                                public void outputMatchedGroups(String[] elements) {
                                    try {
                                        fwriter.write(VcsUtilities.arrayToString(elements));
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getDataErrOutputArea();
                            writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                            writer.newLine();
                        }
                    }
                } catch (Exception exc) {
                    ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(exc,
                    NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFile.errorWhileWriting"))); //NOI18N
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (java.io.IOException ioex) {}
                    }
                }
            }
        }, 0);
    }
    
    /**
     * Whether the View Text action should be enabled.
     */
    protected boolean isViewTextLogEnabled() {
        return false;
    }
    
    private void viewTextLog() {
        javax.swing.JTextArea outputArea = getStdOutputArea();
        String value;
        if (outputCollector != null) {
            final StringBuffer buff = new StringBuffer();
            outputCollector.addTextOutputListener(new TextOutputListener() {
                public void outputLine(String line) {
                    buff.append(line);
                    buff.append(System.getProperty("line.separator"));
                }
            }, false);
            value = buff.toString();
        } else {
            if (outputArea == null) {
                value = "";
            } else {
                try {
                    value = outputArea.getDocument().getText(0, outputArea.getDocument().getLength());
                } catch (javax.swing.text.BadLocationException blex) {
                    ErrorManager.getDefault().notify(blex);
                    return ;
                }
            }
        }
        final String finalValue = value;
        PropertySupport.ReadOnly property = new PropertySupport.ReadOnly("value", String.class, "", "") {
            public Object getValue() {
                return finalValue;
            }
        };
        java.awt.Component c = new PropertyPanel(property, PropertyPanel.PREF_CUSTOM_EDITOR);
        javax.swing.JButton closeButton = new javax.swing.JButton(NbBundle.getMessage(AbstractOutputPanel.class, "OutputPanel.Close"));
        closeButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AbstractOutputPanel.class, "ACSD_OutputPanel.Close"));
        DialogDescriptor dd = new DialogDescriptor(c, NbBundle.getBundle(OutputPanel.class).getString("CMD_TextOutput"),
                                                   true, new Object[] { closeButton }, closeButton,
                                                   DialogDescriptor.DEFAULT_ALIGN, null, null);
        DialogDisplayer ddisp = DialogDisplayer.getDefault();
        ddisp.notify(dd);
    }
    
    public void addKillActionListener(java.awt.event.ActionListener l) {
        btnStop.addActionListener(l);
        killActionListeners.add(l);
    }
    
    public void removeKillActionListener(java.awt.event.ActionListener l) {
        btnStop.removeActionListener(l);
        killActionListeners.remove(l);
    }
    
    public void commandFinished(final int exit) {
        while (killActionListeners.size() > 0) {
            java.awt.event.ActionListener l = (java.awt.event.ActionListener)killActionListeners.remove(0);
            btnStop.removeActionListener(l);
        }
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                btnStop.setEnabled(false);
                if (exit == CommandTask.STATUS_INTERRUPTED) {
                    lblStatus.setText(NbBundle.getBundle(OutputPanel.class).getString("OutputPanel.StatusInterrupted"));
                } else if (ignoreFailure || exit == CommandTask.STATUS_SUCCEEDED) {
                    lblStatus.setText(NbBundle.getBundle(OutputPanel.class).getString("OutputPanel.StatusFinished"));
                } else {
                    lblStatus.setText(NbBundle.getBundle(OutputPanel.class).getString("OutputPanel.StatusFailed"));
                }
                progress.setIndeterminate(false);
                progress.setValue(100);
                progress.setVisible(false);
                btnStop.setVisible(false);
                if (!ignoreFailure && exit != CommandTask.STATUS_SUCCEEDED && outputCollector.isCmdOutput(1)) {
                    btnErrActionPerformed(new ActionEvent(btnErr,ActionEvent.ACTION_PERFORMED,btnErr.getText()));
                }
                /**
                 * Increase the preferred size of buttons and labels because of issue #51404.
                 * TODO Remove this when the problem is fixed.
                 */
                if (Utilities.getOperatingSystem() == Utilities.OS_MAC &&
                    System.getProperty("java.version").equals("1.4.2_05")) {
                    adjustPreferredSize(lblStatus);
                }
            }
        });
    }
    
    
    class PopupListener extends java.awt.event.MouseAdapter {
        
        private JPopupMenu menu;
        
        public PopupListener(JPopupMenu menu) {
            this.menu = menu;
        }
        
        public void mousePressed(java.awt.event.MouseEvent event) {
            if ((event.getModifiers() & java.awt.event.MouseEvent.BUTTON3_MASK) == java.awt.event.MouseEvent.BUTTON3_MASK) {
                menu.show((java.awt.Component)event.getSource(),event.getX(),event.getY());
            }
        }
    }
    
    private static class OutputButtonEnabler extends Object implements DocumentListener {
        
        javax.swing.JToggleButton btn;
        
        public OutputButtonEnabler(javax.swing.JToggleButton button) {
            this.btn = button;
        }
        
        public void changedUpdate(DocumentEvent e) {}
        
        public void insertUpdate(DocumentEvent e) {
            btn.setEnabled(true);
        }
        
        public void removeUpdate(DocumentEvent e) {}
        
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        toolbar = new javax.swing.JToolBar();
        btnStd = new javax.swing.JToggleButton();
        btnErr = new javax.swing.JToggleButton();
        rightPanel = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        separator = new javax.swing.JSeparator();
        btnStop = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel"));
        toolbar.setBorder(null);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setPreferredSize(new java.awt.Dimension(205, 24));
        btnStd.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnStd_mnc").charAt(0));
        btnStd.setSelected(true);
        btnStd.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnStd"));
        btnStd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStdActionPerformed(evt);
            }
        });

        toolbar.add(btnStd);
        btnStd.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnStd"));

        btnErr.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnErr_mnc").charAt(0));
        btnErr.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnErr"));
        btnErr.setEnabled(false);
        btnErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnErrActionPerformed(evt);
            }
        });

        toolbar.add(btnErr);
        btnErr.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnErr"));

        rightPanel.setLayout(new java.awt.GridBagLayout());

        lblStatus.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.StatusRunning"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rightPanel.add(lblStatus, gridBagConstraints);

        progress.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 6);
        rightPanel.add(progress, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.8;
        rightPanel.add(jPanel2, gridBagConstraints);

        separator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        rightPanel.add(separator, gridBagConstraints);

        toolbar.add(rightPanel);

        btnStop.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnStop"));
        toolbar.add(btnStop);
        btnStop.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnStop"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 1);
        add(toolbar, gridBagConstraints);
        toolbar.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.toolbar"));
        toolbar.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.toolbar"));

        scroll.setPreferredSize(new java.awt.Dimension(400, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 1);
        add(scroll, gridBagConstraints);
        scroll.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.scroll"));
        scroll.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.scroll"));

    }//GEN-END:initComponents

    private void btnErrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnErrActionPerformed
        btnStd.setSelected(false);
        btnErr.setSelected(true);
        if (btnDataStd != null) btnDataStd.setSelected(false);
        if (btnDataErr != null) btnDataErr.setSelected(false);
        setErrorContent();        
    }//GEN-LAST:event_btnErrActionPerformed

    private void setErrorContent(){
        scroll.setViewportView(getErrComponent());
    }
    private void btnStdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStdActionPerformed
        btnErr.setSelected(false);            
        btnStd.setSelected(true);
        if (btnDataStd != null) btnDataStd.setSelected(false);
        if (btnDataErr != null) btnDataErr.setSelected(false);
        setStandardContent();
    }//GEN-LAST:event_btnStdActionPerformed

    private void setStandardContent(){
        scroll.setViewportView(getStdComponent());       
    }

    private void addDataOutputButtons() {
        btnDataStd = new javax.swing.JToggleButton();
        btnDataErr = new javax.swing.JToggleButton();
        
        btnDataStd.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnDataStd_mnc").charAt(0));
        btnDataStd.setEnabled(false);
        btnDataStd.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnDataStd"));
        btnDataStd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataStdActionPerformed(evt);
            }
        });

        toolbar.add(btnDataStd, 2);
        btnDataStd.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnDataStd"));

        btnDataErr.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnDataErr_mnc").charAt(0));
        btnDataErr.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnDataErr"));
        btnDataErr.setEnabled(false);
        btnDataErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataErrActionPerformed(evt);
            }
        });

        toolbar.add(btnDataErr, 3);
        btnDataErr.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnDataErr"));

    }

    /**
     * Request to show one navigation action in toolbar.
     * Typicaly used after command finishes.
     * @param action action to add or null to remove recent one
     */
    public final void setNavigationAction(Action action) {
        if (navigationButton != null) {
            toolbar.remove(navigationButton);
        }
        if (action != null) {
            navigationButton = new JButton(action);
            toolbar.add(navigationButton);  // it should go after rightPanel assuring right alignment
        }
    }

    private void btnDataStdActionPerformed(java.awt.event.ActionEvent evt) {
        btnStd.setSelected(false);
        btnErr.setSelected(false);
        btnDataStd.setSelected(true);
        btnDataErr.setSelected(false);
        setDataStandardContent();
    }

    private void setDataStandardContent(){
        scroll.setViewportView(getDataStdComponent());       
    }

    private void btnDataErrActionPerformed(java.awt.event.ActionEvent evt) {
        btnStd.setSelected(false);
        btnErr.setSelected(false);
        btnDataStd.setSelected(false);
        btnDataErr.setSelected(true);
        setDataErrorContent();        
    }

    private void setDataErrorContent(){
        scroll.setViewportView(getDataErrComponent());
    }
    
    public javax.swing.JTextArea getStdOutputArea(){
        if(getStdComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getStdComponent();
        else
            return null;
    }
    
    public javax.swing.JTextArea getErrOutputArea(){
        if(getErrComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getErrComponent();
        else
            return null;
    }
    
    public javax.swing.JTextArea getDataStdOutputArea(){
        if(getDataStdComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getDataStdComponent();
        else
            return null;
    }
    
    public javax.swing.JTextArea getDataErrOutputArea(){
        if(getDataErrComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getDataErrComponent();
        else
            return null;
    }
    
    
    protected JPanel getOutputPanel() {
        return this;
    }
    
    
    protected abstract JComponent getErrComponent();
    
    protected abstract JComponent getStdComponent();
    
    /**
     * The component that display standard data output.
     * Returns a JTextArea by default.
     * Subclasses can return a different component here.
     */
    protected JComponent getDataStdComponent() {
        if(stdDataOutput == null){
            stdDataOutput = new JTextArea();
            stdDataOutput.setEditable(false);
            java.awt.Font font = stdDataOutput.getFont();
            stdDataOutput.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));
        }
        return stdDataOutput;
    }
    
    /**
     * The component that display error data output.
     * Returns a JTextArea by default.
     * Subclasses can return a different component here.
     */
    protected JComponent getDataErrComponent() {
        if(errDataOutput == null){
            errDataOutput = new JTextArea();
            errDataOutput.setEditable(false);
            java.awt.Font font = errDataOutput.getFont();
            errDataOutput.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));
        }
        return errDataOutput;
    }
      
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnErr;
    private javax.swing.JToggleButton btnStd;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JSeparator separator;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
    
    private javax.swing.JToggleButton btnDataStd;
    private javax.swing.JToggleButton btnDataErr;
}
