/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;


import org.openide.windows.Workspace;
import org.netbeans.modules.vcscore.ui.OutputPanel;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Mutex;
import org.openide.util.Utilities;

/**
 * TopComponent for vcs command output
 * @author  Richard Gregor
 */
public class CommandOutputTopComponent extends TopComponent {

    //private CommandOutputPanel outputPanel;
    private OutputPanel outputPanel;
    private ArrayList closeListeners = new ArrayList(); 
    private java.awt.event.ActionListener killListener = null;
    private JTabbedPane tabPane;
    private String name;
    protected Object eventSource;
    private JPopupMenu menu;
    private static CommandOutputTopComponent outputTopComponent;
    
    private static final long serialVersionUID = -8901733341334731237L;
    
    private CommandOutputTopComponent() {        
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/commandOutputWindow.gif"));
        putClientProperty("PersistenceType", "Never");        
        initComponents();
        setName(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.topName"));
        initPopupMenu();
        OutputTabPopupListener.install();
        
   }
    
    public static CommandOutputTopComponent getInstance(){
        if(outputTopComponent == null){
            outputTopComponent = new CommandOutputTopComponent();
            Workspace ws = WindowManager.getDefault().getCurrentWorkspace();
            Mode mode = ws.findMode("output");
            mode.dockInto(outputTopComponent);
        }
        return outputTopComponent;
    }
    
    
    private void initComponents() {
        tabPane = new JTabbedPane();  
        tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabPane, gridBagConstraints);
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSN_CommandOutputVisualizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSD_CommandOutputVisualizer"));
    }
    
    private void initPopupMenu() {
        this.menu = new JPopupMenu();
        JMenuItem discardTab = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab"));//NOI18N
        discardTab.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                tabPane.remove(tabPane.getSelectedIndex());
            }
        });
        JMenuItem discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
        discardAll.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                tabPane.removeAll();
            }
        });
        
        this.menu.add(discardTab);
        this.menu.add(discardAll);
       
    }    
       
    Component getSelectedComponent() {
        return tabPane.getSelectedComponent();
    }
    
    void requestVisible(final Component c) {
        Mutex.EVENT.readAccess(new Runnable() {
            public void run() {
                if (!c.equals(tabPane.getSelectedComponent())) {
                    tabPane.setSelectedComponent(c);
                }
            }
        });
    }                
      
    /** 
     * Shows given popup on given coordinations and takes care about the
     * situation when menu can exceed screen limits 
     */
    protected void showPopupMenu (Point p, Component comp) {
        SwingUtilities.convertPointToScreen (p, comp);
        Dimension popupSize = menu.getPreferredSize ();
        Rectangle screenBounds = Utilities.getUsableScreenBounds(getGraphicsConfiguration());
        
        if (p.x + popupSize.width > screenBounds.x + screenBounds.width) {
            p.x = screenBounds.x + screenBounds.width - popupSize.width;
        }
        if (p.y + popupSize.height > screenBounds.y + screenBounds.height) {
            p.y = screenBounds.y + screenBounds.height - popupSize.height;
        }
        
        SwingUtilities.convertPointFromScreen (p, comp);
        menu.show(comp, p.x, p.y);
    }
  
    /**
     * Open the component on the given workspace.
     */
    public void open() {                
        super.open();
        requestVisible();
    }

    public void addVisualizer(String name, JComponent component, boolean selected){
        tabPane.addTab(name,component);
        if(selected)
            tabPane.setSelectedComponent(component);
    }
    
    public void discard(JComponent comp){        
        tabPane.remove(tabPane.getSelectedComponent());
    }
    
    public void discardAll(){
        tabPane.removeAll();
    }
    
    protected void componentActivated() {
        super.componentActivated();
   //     outputPanel.componentActivated();
    }
    
    protected void componentDeactivated() {
        super.componentDeactivated();
  //      outputPanel.componentDeactivated();
    }
    
    /**
     * Disable serialization.
     * @return null
     */
    protected Object writeReplace () throws java.io.ObjectStreamException {
        close();
        return null;
    }
 

    public void addCloseListener(TopComponentCloseListener l) {
        synchronized (closeListeners) {
            closeListeners.add(l);
        }
    }
    
    /**
     * Override for clean up reasons.
     * Will be moved to the appropriate method when will be made.
     */
    public boolean canClose(Workspace workspace, boolean last) {
        boolean can = super.canClose(workspace, last);
        if (last && can) {
            closing();
        }
        return can;
    }
    
    /**
     * Called when the TopComponent is being to close.
     */
    private void closing() {
     //   outputPanel.removeKillActionListener(killListener);
        //synchronized (this) {
        //    pool = null;
        //}
        synchronized (closeListeners) {
            for (Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                TopComponentCloseListener l = (TopComponentCloseListener) it.next();
                l.closing();
            }
            closeListeners.clear();
        }
    }
    

}
