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
 * The Original Software is the CVSROOT Selector (RFE #65366).
 * The Initial Developer of the Original Software is Michael Nascimento Santos.
 * Portions created by Michael Nascimento Santos are Copyright (C) 2005.
 * All Rights Reserved.
 */

package net.java.dev.cvsrootselector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.RequestProcessor;

public class CvsRootSelectorPanel extends javax.swing.JPanel {
    private final File file;
    private final String originalRoot;
    private boolean ok;

    public CvsRootSelectorPanel(File file) throws IOException {
        this.file = file;
        
        initComponents();
        
        workDir.setText(file.getAbsolutePath());
        
        originalRoot = CvsRootRewriter.getCvsRoot(file);
        root.setText(originalRoot);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        workDirLabel = new javax.swing.JLabel();
        workDir = new javax.swing.JLabel();
        rootLabel = new javax.swing.JLabel();
        root = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 0, 12));
        workDirLabel.setText("Working Directory:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
        add(workDirLabel, gridBagConstraints);

        workDir.setText("aDir");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
        add(workDir, gridBagConstraints);

        rootLabel.setText("CVS/Root:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 3);
        add(rootLabel, gridBagConstraints);

        root.setColumns(45);
        root.setText(":pserver:user@server.com:/path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(root, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField root;
    public javax.swing.JLabel rootLabel;
    public javax.swing.JLabel workDir;
    public javax.swing.JLabel workDirLabel;
    // End of variables declaration//GEN-END:variables
    
    public void display() {
        DialogDescriptor descriptor = new DialogDescriptor(
                this, "Change CVS Root");
        descriptor.setButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok = e.getSource() == DialogDescriptor.OK_OPTION;
            }
        });
        DialogDisplayer.getDefault().createDialog(descriptor).setVisible(true);
        
        if (!ok) {
            return;
        }
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                new CvsRootRewriter(file, root.getText().trim()).rewrite();
            }
        });
    }
}