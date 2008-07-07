/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.java.highlightboxingunboxingvarargs.impl;

final class HighlightboxingunboxingvarargsPanel extends javax.swing.JPanel {

    private final HighlightboxingunboxingvarargsOptionsPanelController controller;

    HighlightboxingunboxingvarargsPanel(HighlightboxingunboxingvarargsOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        ColorComboBox.init(boxingHighlightBackgroundComboBox);
        ColorComboBox.init(unboxingHighlightBackgroundComboBox);
        ColorComboBox.init(varargsHighlightBackgroundComboBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        boxingHighlightBackgroundLabel = new javax.swing.JLabel();
        unboxingHighlightBackgroundLabel = new javax.swing.JLabel();
        varargsHighlightBackgroundLabel = new javax.swing.JLabel();
        boxingHighlightBackgroundComboBox = new javax.swing.JComboBox();
        unboxingHighlightBackgroundComboBox = new javax.swing.JComboBox();
        varargsHighlightBackgroundComboBox = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(boxingHighlightBackgroundLabel, "Boxing Highlight Backround:");

        org.openide.awt.Mnemonics.setLocalizedText(unboxingHighlightBackgroundLabel, "Unboxing Highlight Backround:");

        org.openide.awt.Mnemonics.setLocalizedText(varargsHighlightBackgroundLabel, "Varargs Highlight Backround:");

        boxingHighlightBackgroundComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        unboxingHighlightBackgroundComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        varargsHighlightBackgroundComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(boxingHighlightBackgroundLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(boxingHighlightBackgroundComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(unboxingHighlightBackgroundLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(unboxingHighlightBackgroundComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(varargsHighlightBackgroundLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(varargsHighlightBackgroundComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {boxingHighlightBackgroundComboBox, unboxingHighlightBackgroundComboBox, varargsHighlightBackgroundComboBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {boxingHighlightBackgroundLabel, unboxingHighlightBackgroundLabel, varargsHighlightBackgroundLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(boxingHighlightBackgroundLabel)
                    .add(boxingHighlightBackgroundComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(unboxingHighlightBackgroundLabel)
                    .add(unboxingHighlightBackgroundComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(varargsHighlightBackgroundLabel)
                    .add(varargsHighlightBackgroundComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    void load() {
        boxingHighlightBackgroundComboBox.setSelectedItem(
                new ColorValue(HighlightBoxingUnboxingVarargs.getBoxingHighlightBackground()));
        unboxingHighlightBackgroundComboBox.setSelectedItem(
                new ColorValue(HighlightBoxingUnboxingVarargs.getUnboxingHighlightBackground()));
        varargsHighlightBackgroundComboBox.setSelectedItem(
                new ColorValue(HighlightBoxingUnboxingVarargs.getVarargsHighlightBackground()));
    }

    void store() {
        HighlightBoxingUnboxingVarargs.setHighlightBackgrounds(
                ((ColorValue) boxingHighlightBackgroundComboBox.getSelectedItem()).getColor(),
                ((ColorValue) unboxingHighlightBackgroundComboBox.getSelectedItem()).getColor(),
                ((ColorValue) varargsHighlightBackgroundComboBox.getSelectedItem()).getColor());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox boxingHighlightBackgroundComboBox;
    private javax.swing.JLabel boxingHighlightBackgroundLabel;
    private javax.swing.JComboBox unboxingHighlightBackgroundComboBox;
    private javax.swing.JLabel unboxingHighlightBackgroundLabel;
    private javax.swing.JComboBox varargsHighlightBackgroundComboBox;
    private javax.swing.JLabel varargsHighlightBackgroundLabel;
    // End of variables declaration//GEN-END:variables
}