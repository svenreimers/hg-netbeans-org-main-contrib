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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd;

import org.openide.util.NbBundle;

/**
 * The GUI tracking the login process.
 *
 * @author  Martin Entlicher
 */
public class CvsLoginProgressPanel extends javax.swing.JPanel {
    
    /** Creates new form CvsLoginProgressPanel */
    public CvsLoginProgressPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        progressLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        setLayout(new java.awt.GridBagLayout());

        progressLabel.setText("Login");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 6, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(progressLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 12, 12);
        gridBagConstraints.weightx = 1.0;
        add(progressBar, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    // End of variables declaration//GEN-END:variables
    
    public void connectingTo(String serverName) {
        progressLabel.setText(NbBundle.getMessage(CvsLoginProgressPanel.class, "CvsLoginProgressPanel.connectingTo", serverName));
        progressBar.setIndeterminate(true);
    }
    
    public void loginFinished(String message) {
        progressLabel.setText(message);
        progressBar.setValue(progressBar.getMaximum());
        progressBar.setIndeterminate(false);
    }
}
