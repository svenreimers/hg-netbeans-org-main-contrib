/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Component Generator panel
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @author Jiri.Skrivanek@sun.com
 */
public class ComponentGeneratorPanel extends javax.swing.JPanel implements java.beans.PropertyChangeListener, java.beans.VetoableChangeListener, ActionListener {
    
    private static java.awt.Dialog dialog;
    private static ComponentGeneratorPanel panel;
    private String packageName;
    // data folder where to place generated sources
    private DataFolder targetDataFolder;
    private Thread thread;
    private java.util.Properties props;
    // "<default package>"
    private static final String DEFAULT_PACKAGE_LABEL = NbBundle.getBundle("org.netbeans.modules.java.project.Bundle").getString("LBL_DefaultPackage");

    
    /** creates ans shows Component Generator dialog
     */
    public static void showDialog(Node[] nodes){
        if (dialog==null) {
            panel = new ComponentGeneratorPanel(nodes);
            dialog = DialogDisplayer.getDefault().createDialog(new DialogDescriptor(panel, NbBundle.getMessage(ComponentGeneratorPanel.class, "Title"), false, new Object[]{DialogDescriptor.CLOSED_OPTION}, null, DialogDescriptor.BOTTOM_ALIGN, new HelpCtx(ComponentGeneratorPanel.class), panel)); // NOI18N
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    panel.actionPerformed(null);
                }
            });
        }
        // XXX doesn't work for projects
        //panel.setSelectedNodes(nodes);
        dialog.show();
    }
    
    /** Creates new ComponentGeneratorPanel
     */
    public ComponentGeneratorPanel(Node[] nodes) {
        loadProperties();
        initComponents();
        packagesPanel.getExplorerManager().setRootContext(createPackagesNode());
        packagesPanel.getExplorerManager().addVetoableChangeListener(this);
        packagesPanel.getExplorerManager().addPropertyChangeListener(this);
    }

    
    private void setSelectedNodes(Node[] nodes) {
        // XXX this doesnt work anymore
        DataFolder df;
        if (packagesTreeView.isEnabled() && nodes!=null && nodes.length>0 && (df=(DataFolder)nodes[0].getCookie(DataFolder.class))!=null) {
            try {
                StringTokenizer packageName = new StringTokenizer(df.getPrimaryFile().getPackageName('.'), "."); // NOI18N
                Node node = packagesPanel.getExplorerManager().getRootContext().getChildren().findChild(df.getPrimaryFile().getFileSystem().getSystemName());
                while (packageName.hasMoreTokens()) {
                    node = node.getChildren().findChild(packageName.nextToken());
                }
                packagesPanel.getExplorerManager().setSelectedNodes(new Node[]{node});
            }
            catch(Exception e) {}
        }
    }
    
    private Node createPackagesNode() {
        Lookup context = Utilities.actionsGlobalContext();
        DataObject dataObject = (DataObject)context.lookup(DataObject.class);
        if(dataObject != null) {
            Project p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            if(p != null) {
                LogicalViewProvider lvp = (LogicalViewProvider)p.getLookup().lookup(LogicalViewProvider.class);
                return new DataFolderFilterNode(lvp.createLogicalView().cloneNode());
            }
        }
        // no project selected => create a dummy node with message "Please, close the dialog and select a project."
        Node node = new AbstractNode(Children.LEAF);
        node.setDisplayName(NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_SelectProjectNode")); // NOI18N
        return node;
    }
    
    void loadProperties() {
        props = new java.util.Properties();
        try {
            props.load( this.getClass().getClassLoader().getResourceAsStream("org/netbeans/modules/jemmysupport/generator/ComponentGenerator.properties")); // NOI18N
        } catch (Exception e) {
            e.printStackTrace();
            throw new java.lang.reflect.UndeclaredThrowableException(e, NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_PropertiesNotLoaded")); // NOI18N
        }
    }
    
    void saveProperties() {
        try {
            org.openide.filesystems.FileObject fo=org.openide.filesystems.Repository.getDefault().getDefaultFileSystem().getRoot();
            org.openide.filesystems.FileObject fo2=fo.getFileObject("jemmysupport"); // NOI18N
            if (fo2==null) {
                fo2=fo.createFolder("jemmysupport"); // NOI18N
            }
            fo=fo2.getFileObject("ComponentGenerator","properties"); // NOI18N
            if (fo==null) {
                fo=fo2.createData("ComponentGenerator","properties"); // NOI18N
            }
            props.store(fo.getOutputStream(fo.lock()),NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_PropertiesTitle")); // NOI18N
        } catch (Exception e) {
            throw new java.lang.reflect.UndeclaredThrowableException(e, NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_PropertiesNoSaved")); // NOI18N
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        packagesPanel = new org.openide.explorer.ExplorerPanel();
        packagesTreeView = new org.openide.explorer.view.BeanTreeView();
        selectLabel = new javax.swing.JLabel();
        helpLabel = new javax.swing.JLabel();
        helpLabel.setVisible(false);
        stopButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        stopButton.setVisible(false);
        screenShot = new javax.swing.JCheckBox();
        showEditor = new javax.swing.JCheckBox();
        mergeConflicts = new javax.swing.JCheckBox();
        cbUseComponentName = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setPreferredSize(new java.awt.Dimension(600, 300));
        packagesPanel.setName("");
        packagesTreeView.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Package"));
        packagesTreeView.setPopupAllowed(false);
        packagesTreeView.setAutoscrolls(true);
        packagesPanel.add(packagesTreeView, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(packagesPanel, gridBagConstraints);

        selectLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Package").charAt(0));
        selectLabel.setLabelFor(packagesTreeView);
        selectLabel.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_Package"));
        selectLabel.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Package"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(selectLabel, gridBagConstraints);

        helpLabel.setFont(new java.awt.Font("Dialog", 2, 12));
        helpLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        helpLabel.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_Help"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(helpLabel, gridBagConstraints);
        helpLabel.getAccessibleContext().setAccessibleDescription("N/A");

        stopButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Stop").charAt(0));
        stopButton.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_Stop"));
        stopButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Stop"));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 12, 12);
        add(stopButton, gridBagConstraints);

        startButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Start").charAt(0));
        startButton.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_Start"));
        startButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Start"));
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 12, 12);
        add(startButton, gridBagConstraints);

        screenShot.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_ScreenShot").charAt(0));
        screenShot.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_ScreenShot"));
        screenShot.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_ScreenShot"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(screenShot, gridBagConstraints);

        showEditor.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_ShowEditor").charAt(0));
        showEditor.setSelected(true);
        showEditor.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_ShowEditor"));
        showEditor.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_ShowEditor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(showEditor, gridBagConstraints);

        mergeConflicts.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Merge").charAt(0));
        mergeConflicts.setSelected(true);
        mergeConflicts.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("CTL_Merge"));
        mergeConflicts.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("TTT_Merge"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(mergeConflicts, gridBagConstraints);

        cbUseComponentName.setMnemonic(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "MNM_UseComponentName").charAt(0));
        cbUseComponentName.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_UseComponentName"));
        cbUseComponentName.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_UseComponentName"));
        cbUseComponentName.setName("cbUseComponentName");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(cbUseComponentName, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        if (thread!=null) {
            thread.interrupt();
            thread=null;
        }
        stopButton.setVisible(false);
        helpLabel.setVisible(false);
        packagesTreeView.setEnabled(true);
        startButton.setVisible(true);
        //        customizeButton.setEnabled(true);
        screenShot.setEnabled(true);
        showEditor.setEnabled(true);
        cbUseComponentName.setEnabled(true);
        mergeConflicts.setEnabled(true);
    }//GEN-LAST:event_stopButtonActionPerformed
    
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        packagesTreeView.setEnabled(false);
        startButton.setVisible(false);
        stopButton.setVisible(true);
        //        customizeButton.setEnabled(false);
        screenShot.setEnabled(false);
        showEditor.setEnabled(false);
        cbUseComponentName.setEnabled(false);
        mergeConflicts.setEnabled(false);
        helpLabel.setVisible(true);
        if (thread!=null) {
            thread.interrupt();
        }
        helpLabel.setText(NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_Help")); // NOI18N
        thread = new Thread(new ComponentGeneratorRunnable(targetDataFolder, packageName, this, props, screenShot.isSelected(), showEditor.isSelected(), mergeConflicts.isSelected(), cbUseComponentName.isSelected()));
        thread.start();
    }//GEN-LAST:event_startButtonActionPerformed
    
    /** Allow only simple selection.
     * @param ev PropertyChangeEvent
     * @throws PropertyVetoException PropertyVetoException
     */
    public void vetoableChange(java.beans.PropertyChangeEvent ev) throws java.beans.PropertyVetoException {
        if (org.openide.explorer.ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName())) {
            Node n[] = (Node[])ev.getNewValue();
            if (n.length > 1 ) {
                throw new java.beans.PropertyVetoException(NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_SingleSelection"), ev); // NOI18N
            }
        }
    }
    
    /** Changes in selected node in packages.
     * @param ev PropertyChangeEvent
     */
    public void propertyChange(java.beans.PropertyChangeEvent ev) {
        if (org.openide.explorer.ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName())) {
            startButton.setEnabled(false);
            Node[] arr = packagesPanel.getExplorerManager().getSelectedNodes();
            if (arr.length == 1) {
                org.openide.loaders.DataFolder df = (org.openide.loaders.DataFolder)arr[0].getCookie(org.openide.loaders.DataFolder.class);
                try {
                    if ((df != null) && (!df.getPrimaryFile().getFileSystem().isReadOnly())) {
                        startButton.setEnabled(true);
                        packageName = arr[0].getDisplayName();
                        if(packageName.equals(DEFAULT_PACKAGE_LABEL)) {
                            // if default package selected
                            packageName = "";
                        }
                        targetDataFolder = df;
                    }
                } catch (org.openide.filesystems.FileStateInvalidException e) {}
            }
        }
    }
    
    /** returns JLabel used as status line
     * @return JLabel used as status line
     */
    public javax.swing.JLabel getHelpLabel() {
        return helpLabel;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbUseComponentName;
    private javax.swing.JLabel helpLabel;
    private javax.swing.JCheckBox mergeConflicts;
    private org.openide.explorer.ExplorerPanel packagesPanel;
    private org.openide.explorer.view.BeanTreeView packagesTreeView;
    private javax.swing.JCheckBox screenShot;
    private javax.swing.JLabel selectLabel;
    private javax.swing.JCheckBox showEditor;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    
    /** creates Component Generator dialog for debugging purposes
     * @param args command line arguments
     */
    public static void main(String args[]) {
        showDialog(null);
    }
    
    /** Invoked when an action occurs.
     *
     */
    public void actionPerformed(ActionEvent evt) {
        stopButtonActionPerformed(evt);
        dialog.dispose();
        dialog=null;
    }
    
}
