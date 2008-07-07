/*
 * RetrieveCopybookVisualPanel1.java
 *
 * Created on July 5, 2007, 2:01 PM
 */

package org.netbeans.modules.encoder.coco.ui.wizard;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.openide.WizardDescriptor;

/**
 * The visual panel of the first step of the wizard.
 * 
 * @author Jun Xu
 */
public class RetrieveCopybookVisualPanel1 extends javax.swing.JPanel {
    
    private final RetrieveCopybookWizardPanel1 mEnclosing;
    private final Set<ChangeListener> mChangeListener;
    
    /** Creates new form RetrieveCopybookVisualPanel1 */
    public RetrieveCopybookVisualPanel1(RetrieveCopybookWizardPanel1 enclosing) {
        mEnclosing = enclosing;
        mChangeListener = new HashSet<ChangeListener>();
        initComponents();
    }

    @Override
    public String getName() {
        return "Specify Resource Location";
    }
    
    public void addChangeListener(ChangeListener listener) {
        synchronized(mChangeListener) {
            mChangeListener.add(listener);
        }
    }
    
    public void removeChangeListener(ChangeListener listener) {
        synchronized(mChangeListener) {
            mChangeListener.remove(listener);
        }
    }
    
    public void notifyStateChange() {
        ChangeListener[] listeners =
                mChangeListener.toArray(new ChangeListener[0]);
        ChangeEvent e = new ChangeEvent(this);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].stateChanged(e);
        }
    }
    
    public String getTargetFolder() {
        return jTextTargetFolder.getText();
    }
    /**
     * Sets the target location.
     */
    public void setTargetLocation(String targetLocation) {
        jTextTargetFolder.setText(targetLocation);
    }
    
    public PropertyValue getSourceType() {
        if (jRadioFromFile.isSelected()) {
            return PropertyValue.FROM_FILE;
        } else if (jRadioFromURL.isSelected()) {
            return PropertyValue.FROM_URL;
        }
        return PropertyValue.UNKNOWN_SOURCE_TYPE;
    }
    
    /**
     * Sets the source type.
     * 
     * @param sourceType the source type.  Values should be the strings
     * defined in <code>PropertyValues</code>.
     */
    public void setSourceType(PropertyValue sourceType) {
        if (PropertyValue.FROM_FILE.equals(sourceType)) {
            jTextURL.setEnabled(false);
            jTextFileLocation.setEnabled(true);
            jButtonBrowseSource.setEnabled(true);
        } else if (PropertyValue.FROM_URL.equals(sourceType)) {
            jTextURL.setEnabled(true);
            jTextFileLocation.setEnabled(false);
            jButtonBrowseSource.setEnabled(false);
        } else {
            throw new IllegalArgumentException(
                    "Unknown source type: " + sourceType);
        }
    }
    
    public String getSourceLocation() {
        if (jRadioFromFile.isSelected()) {
            return jTextFileLocation.getText();
        }
        return jTextURL.getText();
    }
    
    public boolean getOverwriteExist() {
        return jCheckBoxOverwrite.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceTypeButtonGroup = new javax.swing.ButtonGroup();
        jLabelSpecifySource = new javax.swing.JLabel();
        jRadioFromURL = new javax.swing.JRadioButton();
        jTextURL = new javax.swing.JTextField();
        jRadioFromFile = new javax.swing.JRadioButton();
        jTextFileLocation = new javax.swing.JTextField();
        jButtonBrowseSource = new javax.swing.JButton();
        jLabelSpecifyTarget = new javax.swing.JLabel();
        jLabelSaveToFolder = new javax.swing.JLabel();
        jTextTargetFolder = new javax.swing.JTextField();
        jButtonBrowseTarget = new javax.swing.JButton();
        jCheckBoxOverwrite = new javax.swing.JCheckBox();

        jLabelSpecifySource.setText("Specify Source");
        jLabelSpecifySource.setName("lblSpecifySource"); // NOI18N

        sourceTypeButtonGroup.add(jRadioFromURL);
        jRadioFromURL.setSelected(true);
        jRadioFromURL.setText("From URL");
        jRadioFromURL.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioFromURL.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioFromURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioFromURLActionPerformed(evt);
            }
        });

        jTextURL.setText(null);
        jTextURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextURLActionPerformed(evt);
            }
        });

        sourceTypeButtonGroup.add(jRadioFromFile);
        jRadioFromFile.setText("From Local File System");
        jRadioFromFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioFromFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioFromFileActionPerformed(evt);
            }
        });

        jTextFileLocation.setText(null);
        jTextFileLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFileLocationActionPerformed(evt);
            }
        });

        jButtonBrowseSource.setText("Browse");
        jButtonBrowseSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseSourceActionPerformed(evt);
            }
        });

        jLabelSpecifyTarget.setText("Specify Target");

        jLabelSaveToFolder.setText(null);

        jTextTargetFolder.setEditable(false);
        jTextTargetFolder.setText(null);

        jButtonBrowseTarget.setText("Browse");
        jButtonBrowseTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseTargetActionPerformed(evt);
            }
        });

        jCheckBoxOverwrite.setText("Overwrite existing files with same name");
        jCheckBoxOverwrite.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxOverwrite.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabelSpecifySource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jRadioFromURL)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jRadioFromFile)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jCheckBoxOverwrite, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jTextTargetFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                            .add(jTextFileLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabelSaveToFolder)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jButtonBrowseSource)
                            .add(jButtonBrowseTarget)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTextURL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabelSpecifyTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 393, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabelSpecifySource)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jRadioFromURL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioFromFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonBrowseSource)
                    .add(jTextFileLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabelSpecifyTarget)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(jLabelSaveToFolder))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButtonBrowseTarget)
                            .add(jTextTargetFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBoxOverwrite)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jRadioFromURL.getAccessibleContext().setAccessibleName(null);
        jRadioFromURL.getAccessibleContext().setAccessibleDescription(null);
    }// </editor-fold>//GEN-END:initComponents

private void jTextFileLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFileLocationActionPerformed
    // TODO add your handling code here:
    notifyStateChange();
}//GEN-LAST:event_jTextFileLocationActionPerformed

private void jTextURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextURLActionPerformed
    // TODO add your handling code here:
    notifyStateChange();
}//GEN-LAST:event_jTextURLActionPerformed

private void jButtonBrowseTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseTargetActionPerformed
    // TODO add your handling code here:
    
}//GEN-LAST:event_jButtonBrowseTargetActionPerformed

private void jButtonBrowseSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseSourceActionPerformed
    // TODO add your handling code here:
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".cpy");
            }

            public String getDescription() {
                return ("COBOL Copybook file filter");
            }
        
    });
    String currentSelected = jTextFileLocation.getText();
    if (currentSelected != null && currentSelected.length() > 0) {
        fileChooser.setSelectedFile(new File(currentSelected));
    }
    if (fileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
        jTextFileLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }
    notifyStateChange();
}//GEN-LAST:event_jButtonBrowseSourceActionPerformed

private void jRadioFromFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioFromFileActionPerformed
    // TODO add your handling code here:
    setSourceType(PropertyValue.FROM_FILE);
    notifyStateChange();
}//GEN-LAST:event_jRadioFromFileActionPerformed

private void jRadioFromURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioFromURLActionPerformed
    // TODO add your handling code here:
    setSourceType(PropertyValue.FROM_URL);
    notifyStateChange();
}//GEN-LAST:event_jRadioFromURLActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseSource;
    private javax.swing.JButton jButtonBrowseTarget;
    private javax.swing.JCheckBox jCheckBoxOverwrite;
    private javax.swing.JLabel jLabelSaveToFolder;
    private javax.swing.JLabel jLabelSpecifySource;
    private javax.swing.JLabel jLabelSpecifyTarget;
    private javax.swing.JRadioButton jRadioFromFile;
    private javax.swing.JRadioButton jRadioFromURL;
    private javax.swing.JTextField jTextFileLocation;
    private javax.swing.JTextField jTextTargetFolder;
    private javax.swing.JTextField jTextURL;
    private javax.swing.ButtonGroup sourceTypeButtonGroup;
    // End of variables declaration//GEN-END:variables
    
}