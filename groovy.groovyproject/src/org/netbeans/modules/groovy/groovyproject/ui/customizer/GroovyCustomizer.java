/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.groovy.groovyproject.ui.customizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;
import org.openide.DialogDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author  phrebejk
 */
public class GroovyCustomizer extends javax.swing.JPanel implements HelpCtx.Provider {
    
    private Component currentCustomizer;

    private GridBagConstraints fillConstraints;
    
    private GroovyProjectProperties groovyProperties;
    
    private DialogDescriptor dialogDescriptor;
    
    /** Creates new form GroovyCustomizer */
    public GroovyCustomizer( GroovyProjectProperties groovyProperties, String preselectedNodeName ) {
        initComponents();
        HelpCtx.setHelpIDString( customizerPanel, "org.netbeans.modules.groovy.groovyproject.ui.customizer.J2SECustomizer" ); // NOI18N
        this.groovyProperties = groovyProperties;
        this.getAccessibleContext().setAccessibleDescription (NbBundle.getMessage(GroovyCustomizer.class,"AD_J2SECustomizer"));
        fillConstraints = new GridBagConstraints();
        fillConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        fillConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        fillConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fillConstraints.weightx = 1.0;
        fillConstraints.weighty = 1.0;
        CategoryView cv = new CategoryView( createRootNode( groovyProperties ), preselectedNodeName );
        cv.getAccessibleContext().setAccessibleName(NbBundle.getMessage(GroovyCustomizer.class,"AN_BeanTreeViewCategories"));
        cv.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(GroovyCustomizer.class,"AD_BeanTreeViewCategories"));
        categoryPanel.add( cv, fillConstraints );
                
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        categoryPanel = new javax.swing.JPanel();
        customizerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(750, 450));
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GroovyCustomizer.class, "ACSN_J2SECustomizer"));
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GroovyCustomizer.class, "ACSD_J2SECustomizer"));
        categoryPanel.setLayout(new java.awt.GridBagLayout());

        categoryPanel.setBorder(new javax.swing.border.EtchedBorder());
        categoryPanel.setMinimumSize(new java.awt.Dimension(220, 4));
        categoryPanel.setPreferredSize(new java.awt.Dimension(220, 4));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        add(categoryPanel, gridBagConstraints);
        categoryPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GroovyCustomizer.class, "ACSN_J2SECustomizer_categoryPanel"));
        categoryPanel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GroovyCustomizer.class, "ACSD_J2SECustomizer_categoryPanel"));

        customizerPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 8);
        add(customizerPanel, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JPanel customizerPanel;
    // End of variables declaration//GEN-END:variables
    
    
    public void setDialogDescriptor( DialogDescriptor dialogDescriptor ) {
        this.dialogDescriptor = dialogDescriptor;
    }
    
    // HelpCtx.Provider implementation -----------------------------------------
    
    public HelpCtx getHelpCtx() {
        if ( currentCustomizer != null ) {
            return HelpCtx.findHelp( currentCustomizer );
        }
        else {
            return HelpCtx.findHelp( customizerPanel );
        }
    }
    
    
    
    // Private innerclasses ----------------------------------------------------

    private class CategoryView extends JPanel implements ExplorerManager.Provider {
        
        private ExplorerManager manager;
        private BeanTreeView btv;
        
        CategoryView( Node rootNode, String preselectedNodeName ) {
        
            // See #36315
            manager = new ExplorerManager();
            
            setLayout( new BorderLayout() );
            
            Dimension size = new Dimension( 220, 4 );
            btv = new BeanTreeView();    // Add the BeanTreeView
            btv.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
            btv.setPopupAllowed( false );
            btv.setRootVisible( false );
            btv.setDefaultActionAllowed( false );            
            btv.setMinimumSize( size );
            btv.setPreferredSize( size );
            btv.setMaximumSize( size );
            btv.setDragSource (false);
            this.add( btv, BorderLayout.CENTER );                        
            manager.setRootContext( rootNode );
            manager.addPropertyChangeListener( new ManagerChangeListener() );
            selectNode( preselectedNodeName );
            btv.expandAll();
                                                
        }
        
        public ExplorerManager getExplorerManager() {
            return manager;
        }
        
        public void addNotify() {
            super.addNotify();
            btv.expandAll();
        }
        
        private void selectNode( String name ) {
            
            Children ch = manager.getRootContext().getChildren();
            if ( ch != null ) {
                Node nodes[] = ch.getNodes( true );
                
                if ( nodes != null && nodes.length > 0 ) {
                    try {                   
                        Node node = nodes[0];
                        
                        if ( name != null  ) {
                            // Find the node
                            try {
                                List<String> path = new ArrayList<String>();
                                StringTokenizer strtok = new StringTokenizer(name, "/"); // NOI18N
                                while (strtok.hasMoreTokens()) {
                                    String token = strtok.nextToken();
                                   path.add(token);
                                }
                                node = NodeOp.findPath(manager.getRootContext(), Collections.enumeration(path)); // NOI18N
                            }
                            catch ( NodeNotFoundException e ) {
                                // First node will be selected
                            }
                        }
                                                
                        manager.setSelectedNodes( new Node[] { node } );
                    }
                    catch ( PropertyVetoException e ) {
                        // No node will be selected
                    }
                }
            }
            
        }
                
        
        
        /** Listens to selection change and shows the customizers as
         *  panels
         */
        
        private class ManagerChangeListener implements PropertyChangeListener {

            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getSource() != manager) {
                    return;
                }

                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    Node nodes[] = manager.getSelectedNodes(); 
                    if ( nodes == null || nodes.length <= 0 ) {
                        return;
                    }
                    Node node = nodes[0];

                    if ( currentCustomizer != null ) {
                        customizerPanel.remove( currentCustomizer );
                    }
                    if ( node.hasCustomizer() ) {
                        currentCustomizer = node.getCustomizer();
                        
                        if ( currentCustomizer instanceof Panel ) {
                            ((Panel)currentCustomizer).initValues();
                        }
                        
                        /*
                        if ( currentCustomizer instanceof javax.swing.JComponent ) {
                            ((javax.swing.JComponent)currentCustomizer).setPreferredSize( new java.awt.Dimension( 600, 0 ) );
                        }
                        */
                        customizerPanel.add( currentCustomizer, fillConstraints );
                        customizerPanel.validate();
                        customizerPanel.repaint();
                        if ( GroovyCustomizer.this.dialogDescriptor != null ) {
                            GroovyCustomizer.this.dialogDescriptor.setHelpCtx( GroovyCustomizer.this.getHelpCtx() );
                        }
                    }
                    else {
                        currentCustomizer = null;
                    }

                    return;
                }
            }
        }
    }
             
    // Private methods ---------------------------------------------------------
    
    private static Node createRootNode( GroovyProjectProperties groovyProperties ) {
        
        String ICON = "org/netbeans/modules/groovy/groovyproject/resources/general"; // NOI18N
        ResourceBundle bundle = NbBundle.getBundle( GroovyCustomizer.class );
        
        ConfigurationDescription buildDescriptions[] = new ConfigurationDescription[] {
            new ConfigurationDescription(
                "Build", // NOI18N
                bundle.getString( "LBL_Config_Build" ), // NOI18N
                ICON,
                new CustomizerCompile( groovyProperties ),
                null),
            new ConfigurationDescription(
                "Jar", // NOI18N
                bundle.getString( "LBL_Config_Jar" ), // NOI18N
                ICON, // NOI18N
                new CustomizerJar( groovyProperties ),
                null ),
        };
        
        ConfigurationDescription runDescriptions[] = new ConfigurationDescription[] {
            new ConfigurationDescription(
                "Run", // NOI18N
                bundle.getString( "LBL_Config_Run" ), // NOI18N
                ICON, // NOI18N
                new CustomizerRun( groovyProperties ),
                null ),                                
            /*
            new ConfigurationDescription(
                "Debug",
                bundle.getString( "LBL_Config_Debug" ), // NOI18N
                ICON_FOLDER + "debug", // NOI18N
                createEmptyLabel( "< Nothing to configure in Debugging. (prototype implementation) >" ), // XXX TEMP
                null ),
             */
        };
        
        ConfigurationDescription descriptions[] = new ConfigurationDescription[] {
            new ConfigurationDescription(
                "General", // NOI18N
                bundle.getString( "LBL_Config_General" ), // NOI18N
                ICON, // NOI18N
                new CustomizerGeneral( groovyProperties ),
                null ),
            new ConfigurationDescription(
                "BuildCategory", // NOI18N
                bundle.getString( "LBL_Config_BuildCategory" ), // NOI18N
                ICON, // NOI18N
                createEmptyLabel( null ), 
                buildDescriptions ),  // NOI18N    
            new ConfigurationDescription(
                "RunCategory", // NOI18N
                bundle.getString( "LBL_Config_RunCategory" ), // NOI18N
                ICON, // NOI18N
                createEmptyLabel( null ), 
                runDescriptions ),  // NOI18N    
                            
            /*    
            new ConfigurationDescription(
                "ProjectDependencies",
                bundle.getString( "LBL_Config_ProjectDependencies" ), // NOI18N
                ICON_FOLDER + "projectDependencies", // NOI18N
                new CustomizerSubprojects(),
                null ),
            */
        };
        
        ConfigurationDescription rootDescription = new ConfigurationDescription(
        "InvisibleRoot", "InvisibleRoot", null, null, descriptions );  // NOI18N
        
        return new ConfigurationNode( rootDescription );
        
        
    }
    
    // Private meyhods ---------------------------------------------------------
    
    // XXX Remove when all panels have some options
    
    private static javax.swing.JLabel createEmptyLabel( String text ) {
        
        JLabel label;
        if ( text == null ) {
            label = new JLabel();
        }
        else {
            label = new JLabel( text );        
            label.setHorizontalAlignment( JLabel.CENTER );
        }
                
        return label;        
    }
    
    // Private innerclasses ----------------------------------------------------
    
    /** Class describing the configuration node. Prototype of the
     *  configuration node.
     */
    private static class ConfigurationDescription {
        
        
        private String name;
        private String displayName;
        private String iconBase;
        private Component customizer;
        private ConfigurationDescription[] children;
        // XXX Add Node.Properties
        
        ConfigurationDescription( String name,
        String displayName,
        String iconBase,
        Component customizer,
        ConfigurationDescription[] children ) {
            
            this.name = name;
            this.displayName = displayName;
            this.iconBase = iconBase;
            this.customizer = customizer;
            this.children = children;
        }
        
    }
    
    
    /** Node to be used for configuration
     */
    private static class ConfigurationNode extends AbstractNode {
        
        private Component customizer;
        
        public ConfigurationNode( ConfigurationDescription description ) {
            super( description.children == null ? Children.LEAF : new ConfigurationChildren( description.children ) );
            setName( description.name );
            setDisplayName( description.displayName );
            if ( description.iconBase != null ) {
                setIconBase( description.iconBase );
            }
            this.customizer = description.customizer;
        }
        
        public boolean hasCustomizer() {
            return true;
        }
        
        public Component getCustomizer() {
            return customizer;
        }
        
    }
    
    /** Children used for configuration
     */
    private static class ConfigurationChildren extends Children.Keys {
        
        private Collection descriptions;
        
        public ConfigurationChildren( ConfigurationDescription[] descriptions ) {
            this.descriptions = Arrays.asList( descriptions );
        }
        
        // Children.Keys impl --------------------------------------------------
        
        public void addNotify() {
            setKeys( descriptions );
        }
        
        public void removeNotify() {
            setKeys( Collections.EMPTY_LIST );
        }
        
        protected Node[] createNodes( Object key ) {
            return new Node[] { new ConfigurationNode( (ConfigurationDescription)key ) };
        }
    }
    
    static interface Panel {
        
        public void initValues();
        
    }
    
    
}