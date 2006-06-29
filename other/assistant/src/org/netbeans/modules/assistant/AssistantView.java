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

package org.netbeans.modules.assistant;

import org.netbeans.modules.assistant.ui.*;
import org.netbeans.modules.assistant.event.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.*;
import javax.accessibility.*;
import javax.swing.border.EmptyBorder;

import org.openide.*;
import org.openide.util.*;
/**
 *@author Richard Gregor
 *
 *Created on May 27, 2002, 6:14 PM
 */

public class AssistantView extends javax.swing.JPanel implements TreeSelectionListener, AssistantModelListener{
    private ResourceBundle bundle;
    private JButton button;
    private String page = null;
    private String language = null;
    private JEditorPane jEditorPane1;
    private AssistantModel model;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private SearchPanel search;
    
    public AssistantView(AssistantModel model) {
        this.model = model;
        model.addAssistantModelListener(this);
        setLayout(new BorderLayout());
        rootNode = new DefaultMutableTreeNode();
        tree = new JTree(loadData());
        expandTree();
        tree.addTreeSelectionListener(this);
        tree.setRootVisible(false);  
        tree.putClientProperty("JTree.lineStyle", "None");

        tree.setCellRenderer(new AssistantCellRenderer());        
        tree.setBorder(new EmptyBorder(6,6,6,6));
        language = Locale.getDefault().getLanguage();
        bundle = ResourceBundle.getBundle("org/netbeans/modules/assistant/Bundle");        
        setLayout(new BorderLayout());
        
        JScrollPane pane = new JScrollPane(tree);        
        add(pane,BorderLayout.CENTER);
        search = new SearchPanel();
        add(search,BorderLayout.SOUTH);
        setMinimumSize(new java.awt.Dimension(170,240));
        setPreferredSize(new java.awt.Dimension(170,240));               

        
    }
    private synchronized DefaultMutableTreeNode loadData(){ 
        debug("load data");
        if (rootNode == null)
            return rootNode;
        rootNode.removeAllChildren();
        AssistantID id = model.getCurrentID();
        if(id == null)
            return rootNode;
        Enumeration sections = id.getSections();
        if(sections == null)
            return rootNode;
        while(sections.hasMoreElements())
                rootNode.add((AssistantSection)sections.nextElement());
        if(tree != null){ 
            ((DefaultTreeModel)tree.getModel()).reload();
            expandTree();
        }
        return rootNode;
    }
    
    private void expandTree(){
        if(rootNode == null)
            return;
        DefaultMutableTreeNode node = null;
        Enumeration nodes = rootNode.children();
        while(nodes.hasMoreElements()){
            node = (DefaultMutableTreeNode)nodes.nextElement();
            if(node.getChildCount() > 0){
                DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getFirstChild();
                TreePath path = new TreePath(child.getPath());
                tree.makeVisible(path);
            }
        }
    }       
              
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     *
     */
    public void valueChanged(TreeSelectionEvent e) {
        debug("value changed");
        TreePath selectedTreePath = e.getNewLeadSelectionPath();
        if(selectedTreePath == null)
            return;
        Object obj = selectedTreePath.getLastPathComponent();
        if (obj != null){
            if(obj instanceof DefaultMutableTreeNode){
                debug("obj is DMTN");
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
                Object object = node.getUserObject();
                if((object != null) && (object instanceof AssistantItem)){
                    debug("item");
                    AssistantItem item = (AssistantItem)object;
                    if(item != null){
                        debug("item url: "+item.getURL());
                        URL newURL = item.getURL();
                        if((newURL != null)&& (model != null)){
                            debug("item with url:"+item);                            
                            model.setCurrentURL(item.getURL());
                            
                        }
                        String action = item.getAction();
                        debug("action:"+action);
                        if((action != null)&&(model != null))
                            model.performAction(action);
                    }
                }
            }
        }
    }      
    
    /** Tells the listener that the current ID in the AssistantModel has
     * changed.
     *
     * @param e The event
     *
     */
    public void idChanged(AssistantModelEvent e) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                loadData();
            }
        });
    }
    
    /** Tells the listener that the current URL has changed.
     *
     * @param e The event
     *
     */
    public void urlChanged(AssistantModelEvent e) {
       //ignore
    }
    
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("View: "+msg);
    }
}
