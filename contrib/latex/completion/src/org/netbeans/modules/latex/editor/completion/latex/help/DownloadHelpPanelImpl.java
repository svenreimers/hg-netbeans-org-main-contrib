/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.completion.latex.help;

import java.io.File;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.WizardDescriptor;
import org.openide.awt.HtmlBrowser;

/**
 *
 * @author Jan Lahoda
 */
/*public*/ class DownloadHelpPanelImpl extends javax.swing.JPanel implements HyperlinkListener, DocumentListener {
    
    private static final String textContent = "<html> <body> <a href=\"http://www.emerson.emory.edu/services/latex/latex2e/latex2e.tar.gz\">Download from here.</a> </body> </html> ";
    
    /** Creates new form DownloadHelpPanelImpl */
    public DownloadHelpPanelImpl() {
        initComponents();
        jTextField1.getDocument().addDocumentListener(this);
        updateValid();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        text = new javax.swing.JEditorPane();
        text.setEditorKit(new HTMLEditorKit());
        text.addHyperlinkListener(this);

        error = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Downloaded archive:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        add(jLabel1, gridBagConstraints);

        jTextField1.setColumns(20);
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 6);
        add(jTextField1, gridBagConstraints);

        jButton1.setText("...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        add(jButton1, gridBagConstraints);

        text.setEditable(false);
        text.setFont(javax.swing.UIManager.getFont("Label.font"));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/editor/completion/latex/help/Bundle"); // NOI18N
        text.setText(bundle.getString("TEXT_Download")); // NOI18N
        text.setDisabledTextColor(javax.swing.UIManager.getColor("Label.foreground"));
        text.setEnabled(false);
        text.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(text, gridBagConstraints);

        error.setEditable(false);
        error.setFont(javax.swing.UIManager.getFont("Label.font"));
        error.setBorder(null);
        error.setDisabledTextColor(UIManager.getColor("nb.errorForeground"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(error, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1FocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser(jTextField1.getText());
        
        chooser.setApproveButtonText("Select");
        chooser.setApproveButtonMnemonic('S');
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
            jTextField1.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            HtmlBrowser.URLDisplayer.getDefault().showURL(e.getURL());
        }
    }
    
    private boolean valid;
    
    public boolean isContentValid() {
        return valid;
    }
    
    private boolean updateRunning = false;
    
    private synchronized void updateValid() {
        if (updateRunning)
            return ;
        
        updateRunning = true;
        
        boolean oldValid = valid;
        File help = new File(jTextField1.getText());
        
        valid = help.canRead() && help.isDirectory() && new File(help, "latex2e_176.html").canRead();
        
        if (!valid) {
            error.setText("The selected directory is not valid.");
        } else {
            error.setText("");
        }
        
        if (valid != oldValid) {
            firePropertyChange(PROP_CONTENT_VALID, Boolean.valueOf(oldValid), Boolean.valueOf(valid));
        }
        
        updateRunning = false;
    }
    
    public String getHelpDirectory() {
        return jTextField1.getText();
    }
    
    public static final String PROP_CONTENT_VALID = "contentValid";
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField error;
    public javax.swing.JButton jButton1;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JTextField jTextField1;
    public javax.swing.JEditorPane text;
    // End of variables declaration//GEN-END:variables
    
    public void insertUpdate(DocumentEvent e) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                    jTextField1.requestFocus();
                updateValid();
//            }
//        });
    }

    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //                    jTextField1.requestFocus();
                updateValid();
            }
        });
    }

    public void changedUpdate(DocumentEvent e) {
//        updateValid();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
//                    jTextField1.requestFocus();
                updateValid();
            }
        });
    }

}
