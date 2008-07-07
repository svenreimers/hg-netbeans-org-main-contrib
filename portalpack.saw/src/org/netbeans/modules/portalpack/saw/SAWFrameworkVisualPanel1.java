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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.portalpack.saw;

import javax.swing.JPanel;
import org.openide.WizardDescriptor;

public final class SAWFrameworkVisualPanel1 extends JPanel {
    private static String selectedValue;
    static final String SELECTED_VALUE = "selectedValue";
    /** Creates new form SAWFrameworkVisualPanel1 */
    public SAWFrameworkVisualPanel1() {
        initComponents();
    }
    
    public String getName() {
        return "Step #1";
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCapsRadioButton = new javax.swing.JRadioButton();
        openESBRadioButton = new javax.swing.JRadioButton();
        jBPMRadioButton = new javax.swing.JRadioButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Select SAW Implementation"));

        jCapsRadioButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCapsRadioButton, "JCAPS");
        jCapsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCapsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(openESBRadioButton, "OpenESB");
        openESBRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        openESBRadioButton.setEnabled(false);
        openESBRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(jBPMRadioButton, "jBPM");
        jBPMRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jBPMRadioButton.setEnabled(false);
        jBPMRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(19, 19, 19)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jBPMRadioButton)
                    .add(openESBRadioButton)
                    .add(jCapsRadioButton))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jCapsRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(openESBRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jBPMRadioButton)
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton jBPMRadioButton;
    private javax.swing.JRadioButton jCapsRadioButton;
    private javax.swing.JRadioButton openESBRadioButton;
    // End of variables declaration//GEN-END:variables
    public void read(WizardDescriptor wDescriptor) {
        
    }
    public String getSelectedValue() {
        
       if(jCapsRadioButton.isSelected()) {
           return "JCAPS";
       }else if(openESBRadioButton.isSelected()) {
           return "OpenESB";
       }else if(jBPMRadioButton.isSelected()) {
           return "jBPM";
       }else {
            return "";
       } 
    }
    
    public void disableCheckBoxes()
    {
        jBPMRadioButton.setEnabled(false);
        openESBRadioButton.setEnabled(false);
    }
    
}
