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

package org.netbeans.modules.helpbuilder.ui;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.helpbuilder.tree.HelpTreeNode;
import org.openide.ErrorManager;

import org.openide.util.NbBundle;

/** A single panel for a wizard.
 * You probably want to make a wizard iterator to hold it.
 *
 * @author  rg125988
 */
public class ProjectFinishPanel extends javax.swing.JPanel {

    /** Create the wizard panel and set up some basic properties. */
    public ProjectFinishPanel() {
        initComponents ();
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ProjectFinishPanel.class, "TITLE_ProjectFinishPanel"));
        /*
        // Optional: provide a special description for this pane.
        // You must have turned on WizardDescriptor.WizardPanel_helpDisplayed
        // (see descriptor in standard iterator template for an example of this).
        try {
            putClientProperty ("WizardPanel_helpURL", // NOI18N
                new URL ("nbresloc:/wizard/Step3PanelHelp.html")); // NOI18N
        } catch (MalformedURLException mfue) {
            throw new IllegalStateException (mfue.toString ());
        }
        */
    }

    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        add(jButton1, new java.awt.GridBagConstraints());

    }//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        HelpTreeNode node = TocSetupPanel.getNode();
        try{
            FileOutputStream out = new FileOutputStream(ProjectSetupPanel.getTargetLocation()+File.separator+"TOC.xml");
            node.export(out);
        }catch(Exception fnf){
            ErrorManager.getDefault().notify(fnf);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables


}
