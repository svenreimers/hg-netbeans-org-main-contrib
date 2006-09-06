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

package org.netbeans.modules.visual.examples;

import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

/**
 * @author David Kaspar
 */
public class RunDialog extends javax.swing.JFrame {

    private static final String[] strings = {
        "javaone.demo1.IconNodeWidget",
        "javaone.demo2.ActionDemo",
        "javaone.demo3.ConnectionDemo",
        "javaone.demo4.GraphDemo",
        "javaone.demo5.ManyObjectsDemo",
        "javaone.demo6.LODDemo",
        "test.alignwith.AlignWithTest",
        "test.animator.AnimatorTest",
        "test.animator.ColorAnimatorTest",
        "test.card.CardContainerWidget",
        "test.component.ComponentTest",
        "test.connect.ConnectScene",
        "test.connect.ConnectGraphScene",
        "test.connectionlabels.ConnectionLabelsTest",
        "test.controlpoint.AddDeleteControlPointTest",
        "test.devolve.DevolveTest",
        "test.general.GraphSceneTest",
        "test.graph.LoopEdgeTest",
        "test.graphlayout.TreeGraphLayoutTest",
        "test.huge.HugeTest",
        "test.inplace.InplaceEditorTest",
        "test.justify.JustifyTest",
        "test.list.ListTest",
        "test.lod.LevelOfDetailsTest",
        "test.multiview.MultiViewTest",
        "test.multiview.MultiViewTableTest",
        "test.object.ObjectTest",
        "test.repaint.RepaintTest",
        "test.resize.ResizeTest",
        "test.scroll.ScrollTest",
        "test.scroll.SwingScrollTest",
        "test.swingborder.SwingBorderTest",
        "test.tool.ToolTest",
        "test.vmd.VMDCollisionTest",
        "test.vmd.VMDTest",
    };

    /** Creates new form RunDialog */
    public RunDialog() {
        initComponents();
        list.setModel(new javax.swing.AbstractListModel() {
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        bRun = new javax.swing.JButton();
        bQuit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Run Dialog");
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        bRun.setText("Run");
        bRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRunActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        getContentPane().add(bRun, gridBagConstraints);

        bQuit.setText("Quit");
        bQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bQuitActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        getContentPane().add(bQuit, gridBagConstraints);

        jPanel1.setLayout(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        getContentPane().add(jPanel1, gridBagConstraints);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-276)/2, (screenSize.height-286)/2, 276, 286);
    }// </editor-fold>//GEN-END:initComponents

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        if (evt.getKeyCode() != KeyEvent.VK_ENTER)
            return;
        run ();
    }//GEN-LAST:event_listKeyPressed

    private void bQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bQuitActionPerformed
        setVisible (false);
        System.exit(0);
    }//GEN-LAST:event_bQuitActionPerformed

    private void bRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRunActionPerformed
        run ();
    }//GEN-LAST:event_bRunActionPerformed

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        if (evt.getClickCount () != 2)
            return;
        run ();
    }//GEN-LAST:event_listMouseClicked

    public void run () {
        final String value = (String) list.getSelectedValue();
        if (value == null)
            return;
        //setVisible(false);
        try {
            Class c = Class.forName(value);
            Method m = c.getDeclaredMethod("main", new Class[] { String[].class });
            m.invoke(null, new Object[] { new String[0] });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new RunDialog().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bQuit;
    private javax.swing.JButton bRun;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    // End of variables declaration//GEN-END:variables

}
