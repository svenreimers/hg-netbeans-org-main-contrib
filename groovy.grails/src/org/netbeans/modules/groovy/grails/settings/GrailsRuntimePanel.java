package org.netbeans.modules.groovy.grails.settings;

import java.awt.Cursor;
import org.netbeans.modules.groovy.grails.settings.GrailsSettings;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

final class GrailsRuntimePanel extends javax.swing.JPanel {

    private final GrailsRuntimeOptionsPanelController controller;
    private final GrailsSettings settings;

    GrailsRuntimePanel(GrailsRuntimeOptionsPanelController controller) {
        this.controller = controller;
        this.settings = GrailsSettings.getInstance();
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }
    
    boolean checkForGrailsExecutable ( File pathToGrails ) {
        String GRAILS_BINARY = "grails";
        
        if(Utilities.isWindows()){
            GRAILS_BINARY = "grails.bat";
        }
        
        return new File (new File (pathToGrails, "bin"), GRAILS_BINARY).isFile ();
        }
    
    void displayGrailsHomeWarning() {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
            NbBundle.getMessage(GrailsRuntimePanel.class, "LBL_Not_grails_home"),
            NotifyDescriptor.Message.WARNING_MESSAGE
            ));
        }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grailsHomeLocation = new javax.swing.JTextField();
        chooseDir = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        linkLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(chooseDir, org.openide.util.NbBundle.getMessage(GrailsRuntimePanel.class, "LBL_Choose")); // NOI18N
        chooseDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseDirActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(GrailsRuntimePanel.class, "SupportPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(linkLabel, "<html><a href=\"http://www.grails.org\">http://www.grails.org</a></html>"); // NOI18N
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                linkLabelMousePressed(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                linkLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                linkLabelMouseEntered(evt);
            }
        });

        jLabel5.setLabelFor(grailsHomeLocation);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(GrailsRuntimePanel.class, "GrailsRuntimePanel.jLabel5.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(linkLabel)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(grailsHomeLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(chooseDir))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(chooseDir)
                    .add(grailsHomeLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(linkLabel))
                .add(24, 24, 24))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chooseDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseDirActionPerformed
        
            JFileChooser chooser = new JFileChooser (grailsHomeLocation.getText ());
            chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            int r = chooser.showDialog (
                SwingUtilities.getWindowAncestor (this), NbBundle.getMessage(GrailsRuntimePanel.class, "LBL_Select_Directory"));
            if (r == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile ();
                if (!checkForGrailsExecutable(file)) {
                    displayGrailsHomeWarning();
                    return;
                }
                grailsHomeLocation.setText (file.getAbsolutePath ());

            }
        
        
    }//GEN-LAST:event_chooseDirActionPerformed

    private void linkLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkLabelMouseEntered
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_linkLabelMouseEntered

    private void linkLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkLabelMouseExited
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_linkLabelMouseExited

    private void linkLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkLabelMousePressed
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL("http://www.grails.org")); // NOI18N
        } catch (MalformedURLException murle) {
            Exceptions.printStackTrace(murle);
        }
    }//GEN-LAST:event_linkLabelMousePressed

    void load() {

        grailsHomeLocation.setText(settings.getGrailsBase());
    }

    void store() {
        String location = grailsHomeLocation.getText();
        
        if (!checkForGrailsExecutable(new File(location))) {
            displayGrailsHomeWarning();
            return;
            }
        else {
            settings.setGrailsBase(location);
            }
        
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton chooseDir;
    private javax.swing.JTextField grailsHomeLocation;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel linkLabel;
    // End of variables declaration//GEN-END:variables

}
