package org.netbeans.modules.tasklist.usertasks.dependencies;

import java.awt.Dialog;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Dependencies for a task
 */
public class DependenciesPanel extends javax.swing.JPanel {
    private static final String BEGIN_BEGIN_MSG = 
        NbBundle.getMessage(DependenciesPanel.class, "BeginBeginDep"); // NOI18N
    private static final String END_BEGIN_MSG = 
        NbBundle.getMessage(DependenciesPanel.class, "EndBeginDep"); // NOI18N

    private UserTask ut;
    private List dependencies = new ArrayList();
    
    /**
     * Creates a panel
     */
    public DependenciesPanel() {
        initComponents();
        jList.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    jButtonRemove.setEnabled(jList.getSelectedIndex() >= 0);
                }
            }
        );
    }
    
    /**
     * Fills the panel with the data.
     *
     * @param ut a user task
     */
    public void fillPanel(UserTask ut) {
        this.ut = ut;
        DefaultListModel dlm = new DefaultListModel();
        Iterator it = ut.getDependencies().iterator();
        while (it.hasNext()) {
            Dependency d = (Dependency) it.next();
            dependencies.add(d);
            dlm.addElement(createTaskDescription(d.getDependsOn(), d.getType()));
        }
        jList.setModel(dlm);
        if (jList.getModel().getSize() > 0)
            jList.setSelectedIndex(0);
    }
    
    /**
     * Creates a description for a task.
     *
     * @param ut a task
     * @param type dependency type. Dependency.BEGIN_BEGIN or END_BEGIN
     * @return path to the task. e.g. "A/B/C"
     */
    private String createTaskDescription(UserTask ut, int type) {
        StringBuffer sb = new StringBuffer();
        while (ut != null) {
            if (sb.length() != 0)
                sb.insert(0, '/');
            sb.insert(0, ut.getSummary());
            ut = ut.getParent();
        }
        sb.append(" - "); // NOI18N
        if (type == Dependency.BEGIN_BEGIN)
            sb.append(BEGIN_BEGIN_MSG);
        else
            sb.append(END_BEGIN_MSG);
        return sb.toString();
    }
    
    /**
     * Fills in the fillPanel specified user task with the data from this panel
     */
    public void fillObject() {
        this.ut.getDependencies().clear();
        this.ut.getDependencies().addAll(dependencies);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jList = new javax.swing.JList();
        jButtonRemove = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(jList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonRemove, org.openide.util.NbBundle.getBundle(DependenciesPanel.class).getString("LBL_RemoveDependency"));
        jButtonRemove.setEnabled(false);
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jButtonRemove, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAdd, org.openide.util.NbBundle.getBundle(DependenciesPanel.class).getString("LBL_AddDependency"));
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jButtonAdd, gridBagConstraints);

    }//GEN-END:initComponents

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        DialogDescriptor.Confirmation conf = new DialogDescriptor.Confirmation(
            NbBundle.getMessage(DependenciesPanel.class, "DeleteDepConf"), // NOI18N
            DialogDescriptor.YES_NO_OPTION, DialogDescriptor.QUESTION_MESSAGE
        );
        Object ret = DialogDisplayer.getDefault().notify(conf);
        if (ret == DialogDescriptor.YES_OPTION) {
            int index = jList.getSelectedIndex();
            ((DefaultListModel) jList.getModel()).remove(index);
            dependencies.remove(index);
            if (jList.getModel().getSize() > index)
                jList.setSelectedIndex(index);
            else if (jList.getModel().getSize() > index - 1)
                jList.setSelectedIndex(index - 1);
        }
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        DependencyPanel dp = new DependencyPanel(ut);
        dp.setBorder(new EmptyBorder(11, 11, 12, 12));
        DialogDescriptor dd = new DialogDescriptor(dp, 
            NbBundle.getMessage(
                DependenciesPanel.class, "AddDependency")); // NOI18N
        dp.setDialogDescriptor(dd);
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.setBounds(Utilities.findCenterBounds(new Dimension(400, 400)));
        d.show();
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            UserTask sel = dp.getSelectedTask();
            int type = dp.getDependencyType();
            dependencies.add(new Dependency(sel, type));
            ((DefaultListModel) jList.getModel()).addElement(
                createTaskDescription(sel, type));
            jList.setSelectedIndex(jList.getModel().getSize() - 1);
        }        
    }//GEN-LAST:event_jButtonAddActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JList jList;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
