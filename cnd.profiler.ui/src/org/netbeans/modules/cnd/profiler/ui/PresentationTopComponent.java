/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.modules.cnd.profiler.providers.GprofProvider;
import org.netbeans.modules.cnd.profiler.providers.TestProvider;
import org.netbeans.modules.cnd.profiler.views.CalleeView;
import org.netbeans.modules.cnd.profiler.views.CallersView;
import org.netbeans.modules.cnd.profiler.views.FunctionView;
import org.netbeans.modules.cnd.profiler.views.PlainView;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final class PresentationTopComponent extends TopComponent {

    private static PresentationTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/cnd/profiler/resources/leaf.png";

    private static final String PREFERRED_ID = "PresentationTopComponent";
    
    private final FunctionView plainView = new PlainView();
    private final FunctionView calleesView = new CalleeView();
    private final FunctionView callersView = new CallersView();

    private PresentationTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PresentationTopComponent.class, "CTL_PresentationTopComponent"));
        setToolTipText(NbBundle.getMessage(PresentationTopComponent.class, "HINT_PresentationTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
        
        topPanel.add(plainView.getComponent());
        callerPanel.add(callersView.getComponent());
        calleePanel.add(calleesView.getComponent());
        
        plainView.setRoot(new TestProvider().getFunctions());
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // add selection listener
        ExplorerManager.Provider provider = (ExplorerManager.Provider) plainView.getComponent();
        final ExplorerManager manager = provider.getExplorerManager();
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Node[] nodes = manager.getSelectedNodes();
                if (nodes.length != 0) {
                    Collection<Function> selFunctions = nodes[0].getLookup().lookup(new Lookup.Template(Function.class)).allInstances();
                    if (!selFunctions.isEmpty()) {
                        Function function = selFunctions.iterator().next();
                        calleesView.setRoot(function);
                        callersView.setRoot(function);
                    }
                }
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        topPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        calleePanel = new javax.swing.JPanel();
        callerPanel = new javax.swing.JPanel();
        customize = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        topPanel.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(topPanel);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        calleePanel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(PresentationTopComponent.class, "PresentationTopComponent.calleePanel.TabConstraints.tabTitle_1"), new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/profiler/resources/node.png")), calleePanel); // NOI18N

        callerPanel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(PresentationTopComponent.class, "PresentationTopComponent.callerPanel.TabConstraints.tabTitle_1"), new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/profiler/resources/reverseNode.png")), callerPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(PresentationTopComponent.class, "PresentationTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        customize.add(jButton1);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(PresentationTopComponent.class, "PresentationTopComponent.customize.TabConstraints.tabTitle"), customize); // NOI18N

        bottomPanel.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(bottomPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private final JFileChooser fileChooser = new JFileChooser();
    
private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
    {
        plainView.setRoot(new GprofProvider(fileChooser.getSelectedFile()).getFunctions());
    }
}//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel calleePanel;
    private javax.swing.JPanel callerPanel;
    private javax.swing.JPanel customize;
    private javax.swing.JButton jButton1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized PresentationTopComponent getDefault() {
        if (instance == null) {
            instance = new PresentationTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the PresentationTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized PresentationTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(PresentationTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PresentationTopComponent) {
            return (PresentationTopComponent) win;
        }
        Logger.getLogger(PresentationTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return PresentationTopComponent.getDefault();
        }
    }
}