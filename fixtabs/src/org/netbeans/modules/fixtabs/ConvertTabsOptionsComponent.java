/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.fixtabs;

import java.awt.Color;
import javax.swing.JColorChooser;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrei Badea
 */
public class ConvertTabsOptionsComponent extends javax.swing.JPanel {

    private Color highlightingColor;

    public ConvertTabsOptionsComponent() {
        initComponents();
    }

    public void setHighlightingColor(Color highlightingColor) {
        this.highlightingColor = highlightingColor;
    }

    public Color getHighlightingColor() {
        return highlightingColor;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        previewLabel = new javax.swing.JLabel();
        changeColorButton = new javax.swing.JButton();
        previewScrollPane = new javax.swing.JScrollPane();
        previewEditorPane = new javax.swing.JEditorPane();

        org.openide.awt.Mnemonics.setLocalizedText(previewLabel, org.openide.util.NbBundle.getMessage(ConvertTabsOptionsComponent.class, "LBL_Preview"));

        org.openide.awt.Mnemonics.setLocalizedText(changeColorButton, org.openide.util.NbBundle.getMessage(ConvertTabsOptionsComponent.class, "LBL_ChangeColor"));
        changeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeColorButtonActionPerformed(evt);
            }
        });

        // pre-init code (need to set content type first, it resets the document)
        previewEditorPane.setContentType("text/x-java");
        previewEditorPane.setEditable(false);
        previewEditorPane.setText("    /** The main method. */    \n    public static void main(String[] args) {    \n        System.out.println(\"Hello\");\n\tSystem.out.println(\"World\");\n    }    ");
        previewScrollPane.setViewportView(previewEditorPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(previewLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(previewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(changeColorButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(previewLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(changeColorButton)
                    .add(previewScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(154, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void changeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeColorButtonActionPerformed
        Color newColor = JColorChooser.showDialog(this,
                NbBundle.getMessage(ConvertTabsOptionsComponent.class, "LBL_SelectColor"),
                highlightingColor);

        if (newColor != null) {
            highlightingColor = newColor;
            HighlightTabs.getDefault().setColor(previewEditorPane, highlightingColor);
        }
    }//GEN-LAST:event_changeColorButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeColorButton;
    private javax.swing.JEditorPane previewEditorPane;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JScrollPane previewScrollPane;
    // End of variables declaration//GEN-END:variables

}
