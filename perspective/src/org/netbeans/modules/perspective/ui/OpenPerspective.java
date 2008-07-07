/*
 * OpenPerspective.java
 *
 * Created on December 16, 2007, 3:59 PM
 */
package org.netbeans.modules.perspective.ui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.tree.TreeSelectionModel;
import org.netbeans.modules.perspective.nodes.PerspectiveChildern;
import org.netbeans.modules.perspective.nodes.PerspectiveNode;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author  Anuradha
 */
public class OpenPerspective extends javax.swing.JDialog implements ExplorerManager.Provider {

    private ExplorerManager explorerManager = new ExplorerManager();
    private PerspectiveImpl selected;

    /** Creates new form OpenPerspective */
    public OpenPerspective() {
        super(WindowManager.getDefault().getMainWindow(), true);
        initComponents();
        BeanTreeView listView = (BeanTreeView) viewList;
        final AbstractAction action = (AbstractAction) btnOK.getAction();
        listView.setPopupAllowed(false);
        listView.setRootVisible(false);
        listView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        explorerManager.setRootContext(new AbstractNode(
                new PerspectiveChildern(action)));
        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Node[] nodes = explorerManager.getSelectedNodes();
                if (nodes.length == 0) {
                    return;
                }

                Node selectedNode = nodes[0];
                if (selectedNode instanceof PerspectiveNode) {
                    selected = ((PerspectiveNode) selectedNode).getPerspectiveImpl();
                    action.setEnabled(true);
                } else {
                    action.setEnabled(false);
                }
            }
        });
        action.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewList = new BeanTreeView();
        btnCancel = new JButton(new CancelAction());
        btnOK = new JButton(new OpenAction());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(OpenPerspective.class, "OpenPerspective.title")); // NOI18N

        viewList.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));

        org.openide.awt.Mnemonics.setLocalizedText(btnCancel, org.openide.util.NbBundle.getMessage(OpenPerspective.class, "Cancel")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnOK, org.openide.util.NbBundle.getMessage(OpenPerspective.class, "Open")); // NOI18N
        btnOK.setActionCommand(org.openide.util.NbBundle.getMessage(OpenPerspective.class, "Open")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(156, Short.MAX_VALUE)
                .add(btnOK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCancel)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(viewList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 282, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {btnCancel, btnOK}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(viewList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public static void createOpenUI() {
        OpenPerspective openPerspective = new OpenPerspective();
        openPerspective.pack();
        openPerspective.setLocationRelativeTo(null);
        openPerspective.setVisible(true);
    }

    private class CancelAction extends AbstractAction {

        private static final long serialVersionUID = 1l;
        PerspectiveImpl mode;

        public CancelAction() {
            putValue(NAME, NbBundle.getMessage(SaveAsUI.class, "Cancel"));
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private class OpenAction extends AbstractAction {

        private static final long serialVersionUID = 1l;
        PerspectiveImpl mode;

        public OpenAction() {
            putValue(NAME, NbBundle.getMessage(SaveAsUI.class, "Open"));
        }

        public void actionPerformed(ActionEvent e) {

            dispose();
            PerspectiveManagerImpl.getInstance().setSelected(selected);


            ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JScrollPane viewList;
    // End of variables declaration//GEN-END:variables
}