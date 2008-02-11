/*
 * GeneralCustomizerPanel.java
 *
 * Created on September 28, 2007, 3:19 PM
 */

package org.netbeans.modules.groovy.grailsproject.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.DocumentEvent;
import org.netbeans.api.project.Project;
import java.io.File;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.groovy.grails.api.GrailsProjectConfig;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;
import javax.swing.JCheckBox;

/**
 *
 * @author  schmidtm
 */
public class GeneralCustomizerPanel extends javax.swing.JPanel implements DocumentListener, ItemListener {
    GrailsProjectConfig prjConfig;
    
    /** Creates new form GeneralCustomizerPanel */
    public GeneralCustomizerPanel(Project prj) {
        prjConfig = new GrailsProjectConfig(prj);
        initComponents();
        
        projectFolderTextField.setText(FileUtil.getFileDisplayName(prj.getProjectDirectory()));
        
        // populating the port field
        
        grailsServerPort.getDocument().addDocumentListener( this );
        grailsServerPort.setText(prjConfig.getPort());
        
        // should we turn-on the Autodeploy flag?
        
        autodeployCheckBox.setSelected(prjConfig.getAutoDeployFlag());
                
        // populating the autodeploy field
        
        autodeployLocation.getDocument().addDocumentListener( this );
        autodeployLocation.setText(prjConfig.getDeployDir());
        
        // Here we define the indexes for the default enviroments as this:
        // 0 : "Development", 
        // 1 : "Production", 
        // 2 : "Test"
        
        grailsEnvChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Development", "Production", "Test" }));
        
        String env = prjConfig.getEnv();
        int idx = 0;
        
        if(env != null) {
            if       (env.equals("Development")) { idx = 0; } 
            else if  (env.equals("Production"))  { idx = 1; }
            else if  (env.equals("Test"))        { idx = 2; }
        }
        
        grailsEnvChooser.setSelectedIndex(idx);
        grailsEnvChooser.addItemListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        activeGrailsEnvironmentLabel = new javax.swing.JLabel();
        grailsEnvChooser = new javax.swing.JComboBox();
        projectFolderLabel = new javax.swing.JLabel();
        projectFolderTextField = new javax.swing.JTextField();
        grailsServerPortLabel = new javax.swing.JLabel();
        grailsServerPort = new javax.swing.JTextField();
        autodeployLocationLabel = new javax.swing.JLabel();
        autodeployLocation = new javax.swing.JTextField();
        autodeployCheckBox = new javax.swing.JCheckBox();

        activeGrailsEnvironmentLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.activeGrailsEnvironmentLabel.text")); // NOI18N

        grailsEnvChooser.setMaximumRowCount(3);
        grailsEnvChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        grailsEnvChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grailsEnvChooserActionPerformed(evt);
            }
        });

        projectFolderLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.projectFolderLabel.text")); // NOI18N

        projectFolderTextField.setEditable(false);
        projectFolderTextField.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.projectFolderTextField.text")); // NOI18N

        grailsServerPortLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.grailsServerPortLabel.text")); // NOI18N

        grailsServerPort.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.grailsServerPort.text")); // NOI18N

        autodeployLocationLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.autodeployLocationLabel.text")); // NOI18N

        autodeployLocation.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.autodeployLocation.text")); // NOI18N

        autodeployCheckBox.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.autodeployCheckBox.text")); // NOI18N
        autodeployCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autodeployCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(activeGrailsEnvironmentLabel)
                    .add(projectFolderLabel)
                    .add(grailsServerPortLabel)
                    .add(autodeployLocationLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(autodeployLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(autodeployCheckBox)
                        .addContainerGap())
                    .add(grailsServerPort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .add(grailsEnvChooser, 0, 315, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectFolderLabel)
                    .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(activeGrailsEnvironmentLabel)
                    .add(grailsEnvChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(grailsServerPortLabel)
                    .add(grailsServerPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(autodeployCheckBox)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(autodeployLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(autodeployLocationLabel))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void grailsEnvChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grailsEnvChooserActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_grailsEnvChooserActionPerformed

private void autodeployCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autodeployCheckBoxActionPerformed
    prjConfig.setAutoDeployFlag(autodeployCheckBox.isSelected());
}//GEN-LAST:event_autodeployCheckBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activeGrailsEnvironmentLabel;
    private javax.swing.JCheckBox autodeployCheckBox;
    private javax.swing.JTextField autodeployLocation;
    private javax.swing.JLabel autodeployLocationLabel;
    private javax.swing.JComboBox grailsEnvChooser;
    private javax.swing.JTextField grailsServerPort;
    private javax.swing.JLabel grailsServerPortLabel;
    private javax.swing.JLabel projectFolderLabel;
    private javax.swing.JTextField projectFolderTextField;
    // End of variables declaration//GEN-END:variables

    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    private void updateTexts( DocumentEvent e ) {
        
        Document doc = e.getDocument();
                
        if ( doc == grailsServerPort.getDocument() ) {
            prjConfig.setPort(grailsServerPort.getText());
            }
        else if ( doc == autodeployLocation.getDocument() ) {
            prjConfig.setDeployDir(autodeployLocation.getText());
            }

    }

    public void itemStateChanged(ItemEvent e) {
        
        prjConfig.setEnv((String)e.getItem());
        
    }
    
}
