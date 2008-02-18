package com.sun.jbi.sapbc.sapwsdlgenerator.wizard;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public final class bapiwizardVisualPanel2
        extends JPanel
        implements TreeSelectionListener {
    
    /**
     * Creates new form bapiwizardVisualPanel2
     */
    public bapiwizardVisualPanel2() {
        initComponents();
    }
    
    public String getName() {
        return NbBundle.getMessage(getClass(), "bapiwizardWizardPanel2.step_name");
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        final TreePath[] paths = e.getPaths();
        boolean update = false;
        for (TreePath path: paths) {
            if (e.isAddedPath(path)) {
                update = true;
                Object selection = path.getLastPathComponent();
                if (selection instanceof TreeNode) {
                    TreeNode node = (TreeNode) selection;
                    synchronized (mLock) {
                        mSelectedPath = (node.isLeaf() ? path : null);
                    }
                }
            }
        }
        if (update) {
            firePropertyChange(PROPERTY_INPUT_UPDATE, null, null);
        }
    }
    
    /**
     * Decides if the panel contains the necessary input to be sufficiently
     * usable.
     */
    boolean ready() {
        synchronized (mLock) {
            return mSelectedPath != null;
        }
    }
    
    Object[] getSelectionPath() {
        return (mSelectedPath != null ? mSelectedPath.getPath() : new Object[0]);
    }

    void loadSettings(final TreeModel desc) {
        // Because we are populating UI components, ensure it is done
        // via the Swing dispatch thread.
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        loadSettings(desc);
                    }
                });
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        treeBapiRfc.setModel(desc);
        firePropertyChange(PROPERTY_INPUT_UPDATE, null, null);
    }
    
    void storeSettings(final WizardDescriptor desc) {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelBapiRfcSelect = new javax.swing.JPanel();
        labelDirections = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeBapiRfc = new javax.swing.JTree();

        labelDirections.setFont(new java.awt.Font("Tahoma", 1, 11));
        org.openide.awt.Mnemonics.setLocalizedText(labelDirections, "Select a BAPI/RFC method to retrieve and describe.");

        treeBapiRfc.setLargeModel(true);
        treeBapiRfc.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeBapiRfc.addTreeSelectionListener(this);
        jScrollPane1.setViewportView(treeBapiRfc);

        org.jdesktop.layout.GroupLayout panelBapiRfcSelectLayout = new org.jdesktop.layout.GroupLayout(panelBapiRfcSelect);
        panelBapiRfcSelect.setLayout(panelBapiRfcSelectLayout);
        panelBapiRfcSelectLayout.setHorizontalGroup(
            panelBapiRfcSelectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelBapiRfcSelectLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelBapiRfcSelectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, labelDirections))
                .addContainerGap())
        );
        panelBapiRfcSelectLayout.setVerticalGroup(
            panelBapiRfcSelectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelBapiRfcSelectLayout.createSequentialGroup()
                .addContainerGap()
                .add(labelDirections)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panelBapiRfcSelect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panelBapiRfcSelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelDirections;
    private javax.swing.JPanel panelBapiRfcSelect;
    private javax.swing.JTree treeBapiRfc;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Property indicating that user input in this visual panel has been changed.
     */
    static final String PROPERTY_INPUT_UPDATE =
            bapiwizardVisualPanel2.class.getName().concat("/property_input_update");

    private final Object mLock = new Object();
    private TreePath mSelectedPath = null;
}
