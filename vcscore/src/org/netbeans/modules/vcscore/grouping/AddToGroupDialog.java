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

package org.netbeans.modules.vcscore.grouping;

/**
 *
 * @author  Milos Kleint
 */

import org.openide.loaders.*;
import org.openide.*;
import org.openide.util.*;
import org.openide.nodes.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import org.openide.DialogDisplayer;

public class AddToGroupDialog extends javax.swing.JPanel {

    private DataObject dataObject;
    /** Creates new form AddToGroupDialog */
    public AddToGroupDialog() {
        initComponents();
        lblGroups.setDisplayedMnemonic(NbBundle.getBundle(AddToGroupDialog.class)
             .getString("AddToGroupDialog.lblGroup.mnemonic").charAt(0));   //NOI18N
        lblGroups.setLabelFor(lstGroups);
        cbDontShow.setMnemonic(NbBundle.getBundle(AddToGroupDialog.class)
             .getString("AddToGroupDialog.cbDontShow.mnemonic").charAt(0));   //NOI18N
        Enumeration en = GroupUtils.getMainVcsGroupNodeInstance().getChildren().nodes();
        DefaultListModel model = new DefaultListModel();
        while (en.hasMoreElements()) {
            VcsGroupNode node = (VcsGroupNode)en.nextElement();
            model.addElement(node.getDisplayName());
        }
        lstGroups.setModel(model);
        lstGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public AddToGroupDialog(DataObject obj) {
        this();
        setDataObject(obj);
    }

    public void setDataObject(DataObject obj) {
        dataObject = obj;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        lblGroups = new javax.swing.JLabel();
        spGroups = new javax.swing.JScrollPane();
        lstGroups = new javax.swing.JList();
        cbDontShow = new javax.swing.JCheckBox();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        lblGroups.setText(org.openide.util.NbBundle.getBundle(AddToGroupDialog.class).getString("AddToGroupDialog.lblGroup.text"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(lblGroups, gridBagConstraints1);
        
        spGroups.setPreferredSize(new java.awt.Dimension(360, 132));
        spGroups.setMinimumSize(new java.awt.Dimension(100, 50));
        spGroups.setViewportView(lstGroups);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(2, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(spGroups, gridBagConstraints1);
        
        cbDontShow.setText(org.openide.util.NbBundle.getBundle(AddToGroupDialog.class).getString("AddToGroupDialog.cbDontShow.text"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 11, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(cbDontShow, gridBagConstraints1);
        
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblGroups;
    private javax.swing.JScrollPane spGroups;
    private javax.swing.JList lstGroups;
    private javax.swing.JCheckBox cbDontShow;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = -7640736488251060985L;    

    public static void openChooseDialog(DataObject dataObject) {
        final AddToGroupDialog dialog = new AddToGroupDialog();
        dialog.setDataObject(dataObject);
        DialogDescriptor dd = new DialogDescriptor(dialog, 
             NbBundle.getBundle(AddToGroupDialog.class).getString("AddToGroupDialog.dialogTitle"));
        dd.setOptionType(DialogDescriptor.YES_NO_OPTION);
        dd.setModal(true);
        dd.setButtonListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (evt.getSource().equals(DialogDescriptor.NO_OPTION)) {
                        dialog.checkDontShow();
                        return;
                    }
                    if (evt.getSource().equals(DialogDescriptor.YES_OPTION)) {
                        dialog.checkDontShow();
                        dialog.addToSelectedGroup();
                    }
                }
            });
         final java.awt.Dialog dial = DialogDisplayer.getDefault().createDialog(dd);
         SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dial.show();
                }
         });        
    }
    
    private void checkDontShow() {
        if (cbDontShow.isSelected()) {
             VcsGroupSettings grSettings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
             grSettings.setAutoAddition(VcsGroupSettings.ADDITION_TO_DEFAULT);
        }
    }
    
    private void addToSelectedGroup() {
        Object obj = lstGroups.getSelectedValue();
        if (obj != null) {
            String grString = obj.toString();
            Node grFolder = GroupUtils.getMainVcsGroupNodeInstance();
            Node[] dobjs = grFolder.getChildren().getNodes();
            DataFolder group = null;
            if (dobjs == null) return;
            for (int i = 0; i < dobjs.length; i++) {
                if (dobjs[i].getName().equals(grString)) {
                    DataFolder fold = (DataFolder)dobjs[i].getCookie(DataObject.class);
                    group = fold;
                    break;
                }
            }
            if (group == null) return;
            Node[] dobjNode = new Node[] {dataObject.getNodeDelegate()};
            GroupUtils.addToGroup(group, dobjNode);
        }
    }
        
}
