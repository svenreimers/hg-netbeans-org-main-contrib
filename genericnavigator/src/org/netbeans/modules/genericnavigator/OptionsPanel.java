/*
 * OptionsPanel.java
 *
 * Created on July 16, 2006, 2:57 PM
 */

package org.netbeans.modules.genericnavigator;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeSelectionModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;

/**
 *
 * @author  Tim Boudreau
 */
public class OptionsPanel extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener, Runnable {
    private final GenericNavigatorOptionsOptionsPanelController  ctrllr;
    /** Creates new form OptionsPanel */
    public OptionsPanel(GenericNavigatorOptionsOptionsPanelController ctrllr) {
        this.ctrllr = ctrllr;
        initComponents();
    }

    private void config (BeanTreeView btv) {
        btv.setRootVisible(false);
        btv.expandAll();
        btv.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void addNotify() {
        super.addNotify();
        //force init
        if (!(mgr.getRootContext() instanceof PatternsRoot)) {
            load();
        }
        mgr.getRootContext().getChildren().getNodes(true);
    }

    void load() {
        PatternsRoot root = new PatternsRoot (PatternItem.getConfigRoot());
        mgr.setRootContext(root);
        root.addPropertyChangeListener(this);
        mgr.addPropertyChangeListener(this);
        traverse (root);
        EventQueue.invokeLater(this);
    }

    private void traverse (Node n) {
        Node[] nds = n.getChildren().getNodes(true);
        for (int i = 0; i < nds.length; i++) {
            traverse (nds[i]);
        }
    }

    void store() {
        paintImmediately(0,0,getWidth(),getHeight());
        setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            System.err.println("OPTIONS PANEL STORE");
            PatternsRoot root = (PatternsRoot) mgr.getRootContext();
            try {
                root.store();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
            }
        } finally {
            setCursor (Cursor.getDefaultCursor());
        }
    }

    private final List l = Collections.synchronizedList(new LinkedList());
    public void addChangeListener(ChangeListener cl) {
        l.add (cl);
    }

    public void removeChangeListener (ChangeListener cl) {
        l.remove (cl);
    }

    private void fire() {
        ChangeListener[] c = (ChangeListener[]) l.toArray(new ChangeListener[0]);
        for (int i = 0; i < c.length; i++) {
            c[i].stateChanged(new ChangeEvent(this));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        itemsTree = new BeanTreeView();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();

        setBackground(java.awt.Color.white);
        itemsTree.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        itemsTree.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        addButton.setText(org.openide.util.NbBundle.getMessage(OptionsPanel.class, "OptionsPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setText(org.openide.util.NbBundle.getMessage(OptionsPanel.class, "OptionsPanel.editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        delButton.setText(org.openide.util.NbBundle.getMessage(OptionsPanel.class, "OptionsPanel.delButton.text")); // NOI18N
        delButton.setEnabled(false);
        delButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(itemsTree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(delButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(editButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {addButton, delButton, editButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(addButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(delButton)
                .addContainerGap(208, Short.MAX_VALUE))
            .add(itemsTree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void delButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButtonActionPerformed
        delete();
    }//GEN-LAST:event_delButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        edit();
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        add();
    }//GEN-LAST:event_addButtonActionPerformed

    private void delete() {
        Node[] n = mgr.getSelectedNodes();
        if (n.length == 1) {
            PatternItemProvider pat = pat = (PatternItemProvider) n[0].getLookup().lookup(
                    PatternItemProvider.class);
            if (pat != null) {
                System.err.println("DELETE " + pat.getPatternItem() + " from " + pat);
                PatternsRoot root = (PatternsRoot) mgr.getRootContext();
//                root.remove(pat.getPatternItem(), pat.getMimeType());
                root.remove (pat);
                setModified (true);
            }
        }
    }

    private boolean modified = false;
    void setModified (boolean val) {
        this.modified = val;
    }

    boolean isModified () {
        return modified;
    }

    private final ExplorerManager mgr = new ExplorerManager();
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (mgr.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] n = mgr.getSelectedNodes();
            PatternItemProvider pat = null;
            if (n.length == 1) {
                pat = (PatternItemProvider) n[0].getLookup().lookup(
                    PatternItemProvider.class);
            }
            editButton.setEnabled(pat != null);
            delButton.setEnabled(pat != null);
        }
    }

    private void add() {
        showEditDialog (null);
    }

    private void edit() {
        Node[] n = mgr.getSelectedNodes();
        if (n.length > 0) {
            PatternItemProvider prov = (PatternItemProvider) n[0].getLookup().lookup(PatternItemProvider.class);
            if (prov != null) {
                showEditDialog (prov);
            } else {
                assert false;
            }
        }
    }

    public void showEditDialog(PatternItemProvider prov) {
        final AddExpressionPanel adder = new AddExpressionPanel();
        if (prov != null) {
            adder.setMimeType(prov.getMimeType());
            adder.setPatternItem(prov.getPatternItem());
        } else {
            adder.setMimeType("text/plain");
        }


        class L implements ActionListener, ChangeListener {
            public void actionPerformed(ActionEvent e) {

            }
            public void stateChanged (ChangeEvent ce) {
                System.err.println("CHANGE: " + adder.isProblem());
                if (desc != null) {
                    desc.setValid (!adder.isProblem());
                }
            }
            DialogDescriptor desc = null;
        };
        L l = new L();

        DialogDescriptor dlg = new DialogDescriptor (adder,
                GenericNavPanel.getString("TTL_AddItem"), //NOI18N
                true,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_OPTION,
                l);

        l.desc = dlg;
        adder.addChangeListener (l);
        adder.check();

        boolean ok = NotifyDescriptor.OK_OPTION.equals(
                DialogDisplayer.getDefault().notify(dlg));

        if (ok) {
            assert adder.getPatternItem() != null;
            setModified (true);
            PatternsRoot root = (PatternsRoot) mgr.getRootContext();
            PatternItem item = adder.getPatternItem();

            if (prov != null) {
//                if (item.differs(prov.getPatternItem())) {
                    root.remove(prov);
//                }
            }
            root.add(adder.getPatternItem(), adder.getMimeType());
        }
    }

    public void run() {
        config ((BeanTreeView) itemsTree);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton delButton;
    private javax.swing.JButton editButton;
    private javax.swing.JScrollPane itemsTree;
    // End of variables declaration//GEN-END:variables

}
