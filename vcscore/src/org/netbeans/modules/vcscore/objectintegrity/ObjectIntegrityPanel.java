/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.objectintegrity;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Panel, that allows users to add secondary local files to keep the integrity
 * of DayaObjects in VCS.
 *
 * @author  Martin Entlicher
 */
public class ObjectIntegrityPanel extends javax.swing.JPanel {
    
    /** Creates new form ObjectIntegrityPanel */
    public ObjectIntegrityPanel() {
        initComponents();
        javax.swing.InputMap map = javax.swing.SwingUtilities.getUIInputMap(filesTable, javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if (map != null) {
            map.remove(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
            map.remove(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
            javax.swing.SwingUtilities.replaceUIInputMap(filesTable, filesTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, map);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesTable = new javax.swing.JTable();
        selectAllButton = new javax.swing.JButton();
        deselectAllButton = new javax.swing.JButton();
        doNotShowCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        descriptionLabel.setLabelFor(filesTable);
        descriptionLabel.setText(org.openide.util.NbBundle.getBundle(ObjectIntegrityPanel.class).getString("LBL.descriptionLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 11, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(descriptionLabel, gridBagConstraints);
        descriptionLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.descriptionLabel.a11yDescription"));

        filesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Add", "File Path"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        filesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jScrollPane1.setViewportView(filesTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 11, 11);
        add(jScrollPane1, gridBagConstraints);

        selectAllButton.setMnemonic(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.selectAllButton.mnemonic").charAt(0));
        selectAllButton.setText(org.openide.util.NbBundle.getBundle(ObjectIntegrityPanel.class).getString("LBL.selectAllButton.text"));
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 11, 11);
        add(selectAllButton, gridBagConstraints);
        selectAllButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.selectAllButton.a11yDescription"));

        deselectAllButton.setMnemonic(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.deselectAllButton.mnemonic").charAt(0));
        deselectAllButton.setText(org.openide.util.NbBundle.getBundle(ObjectIntegrityPanel.class).getString("LBL.deselectAllButton.text"));
        deselectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectAllButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 11, 11);
        add(deselectAllButton, gridBagConstraints);
        deselectAllButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.deselectAllButton.a11yDescription"));

        doNotShowCheckBox.setMnemonic(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.doNotShowCheckBox.mnemonic").charAt(0));
        doNotShowCheckBox.setText(org.openide.util.NbBundle.getBundle(ObjectIntegrityPanel.class).getString("LBL.doNotShowCheckBox.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 11, 11);
        add(doNotShowCheckBox, gridBagConstraints);
        doNotShowCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ObjectIntegrityPanel.class, "LBL.doNotShowCheckBox.a11yDescription"));

    }//GEN-END:initComponents

    private void deselectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectAllButtonActionPerformed
        // Add your handling code here:
        TableModel model = filesTable.getModel();
        int n = model.getRowCount();
        for (int i = 0; i < n; i++) {
            // Slow, but is there anything better???
            filesTable.getCellEditor(i, 0).stopCellEditing();
            filesTable.setValueAt(Boolean.FALSE, i, 0);
        }
    }//GEN-LAST:event_deselectAllButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        // Add your handling code here:
        TableModel model = filesTable.getModel();
        int n = model.getRowCount();
        for (int i = 0; i < n; i++) {
            // Slow, but is there anything better???
            filesTable.getCellEditor(i, 0).stopCellEditing();
            filesTable.setValueAt(Boolean.TRUE, i, 0);
        }
    }//GEN-LAST:event_selectAllButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JButton deselectAllButton;
    private javax.swing.JCheckBox doNotShowCheckBox;
    private javax.swing.JTable filesTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton selectAllButton;
    // End of variables declaration//GEN-END:variables
    
    
    /**
     * Set the array of file paths. If you need it sorted for users'
     * convenience, supply a sorted array. The order will be preserved.
     */
    public void setFilePaths(String[] filePaths) {
        String[] columnNames = new String[] {
            org.openide.util.NbBundle.getBundle(ObjectIntegrityPanel.class).getString("TTL.filesTable.add.title"),
            org.openide.util.NbBundle.getBundle(ObjectIntegrityPanel.class).getString("TTL.filesTable.paths.title")
        };
        Boolean[] willAdd = new Boolean[filePaths.length];
        Arrays.fill(willAdd, Boolean.TRUE);
        DefaultTableModel model = (DefaultTableModel) filesTable.getModel();
        model.setColumnCount(0);
        model.addColumn(columnNames[0], willAdd);
        model.addColumn(columnNames[1], filePaths);
        setColumnWidth();
    }
    
    private void setColumnWidth() {
        TableColumn firstColumn = filesTable.getColumnModel().getColumn(0);
        TableCellRenderer firstHeaderRenderer = firstColumn.getHeaderRenderer();
        if (firstHeaderRenderer == null) {
            firstHeaderRenderer = filesTable.getTableHeader().getDefaultRenderer();
        }
        TableCellRenderer firstColumnRenderer = firstColumn.getCellRenderer();
        if (firstColumnRenderer == null) {
            firstColumnRenderer = filesTable.getDefaultRenderer(filesTable.getColumnClass(0));
        }
        Component hc = firstHeaderRenderer.getTableCellRendererComponent(filesTable, firstColumn.getHeaderValue(),
                                                              false, true, 0, 0);
        Component cc = firstColumnRenderer.getTableCellRendererComponent(filesTable, Boolean.TRUE,
                                                              true, true, 0, 0);
        int hcw = hc.getPreferredSize().width;
        if (hc instanceof AbstractButton) {
            hcw += ((AbstractButton) hc).getMargin().left + ((AbstractButton) hc).getMargin().right;
        }
        int ccw = cc.getPreferredSize().width;
        if (cc instanceof AbstractButton) {
            ccw += ((AbstractButton) cc).getMargin().left + ((AbstractButton) cc).getMargin().right;
        }
        int maxFirstWidth = Math.max(hcw, ccw) + 6;
        firstColumn.setMaxWidth(maxFirstWidth);
    }
    
    /**
     * Get the array of file paths, that were selected to be added.
     */
    public String[] getSelectedFilePaths() {
        TableModel model = filesTable.getModel();
        int n = model.getRowCount();
        ArrayList pathsToAdd = new ArrayList(n);
        for (int i = 0; i < n; i++) {
            if (((Boolean) model.getValueAt(i, 0)).booleanValue()) {
                pathsToAdd.add(model.getValueAt(i, 1));
            }
        }
        return (String[]) pathsToAdd.toArray(new String[pathsToAdd.size()]);
    }
    
    /**
     * Get the array of file paths, that were selected to be further ignored.
     */
    public String[] getIgnoredFilePaths() {
        if (doNotShowCheckBox.isSelected()) {
            TableModel model = filesTable.getModel();
            int n = model.getRowCount();
            ArrayList pathsToIgnore = new ArrayList(n);
            for (int i = 0; i < n; i++) {
                if (!((Boolean) model.getValueAt(i, 0)).booleanValue()) {
                    pathsToIgnore.add(model.getValueAt(i, 1));
                }
            }
            return (String[]) pathsToIgnore.toArray(new String[pathsToIgnore.size()]);
        } else {
            return new String[0];
        }
    }
}
