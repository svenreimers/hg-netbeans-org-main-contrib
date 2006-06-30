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

package org.netbeans.modules.vcscore.grouping;

import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.JMenuItem;
import org.openide.awt.JMenuPlus;
import javax.swing.JMenu;
import javax.swing.event.*;
import java.io.*;

import org.openide.awt.Actions;
import org.openide.util.actions.*;
import org.openide.filesystems.FileObject;
import org.openide.*;
import org.openide.DialogDisplayer;
import org.openide.util.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;


/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class RemoveVcsGroupAction extends NodeAction implements Runnable {

    private static final long serialVersionUID = 2854875989517967757L;
    
    private DataFolder group; // The group to be removed.
    
    protected void performAction (Node[] nodes) {
        // do work based on the current node selection, e.g.:
        // ...
    }


    public String getName () {
        return NbBundle.getMessage(RemoveVcsGroupAction.class, "LBL_RemoveVcsGroupAction");//NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/vcscore/grouping/RemoveVcsGroupActionIcon.gif";//NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (RemoveVcsGroupAction.class);
    }

    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     */
    public JMenuItem getMenuPresenter() {
        return getPresenter(true);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        return getPresenter(false);
    }
    
    public JMenuItem getPresenter(boolean isMenu){
        JMenu menu=new JMenuPlus(NbBundle.getMessage(RemoveVcsGroupAction.class, "LBL_RemoveVcsGroupAction")); // NOI18N
        Actions.setMenuText (menu, NbBundle.getMessage(RemoveVcsGroupAction.class, "LBL_RemoveVcsGroupAction"), isMenu);// NOI18N
        if (isMenu) {
            menu.setIcon(getIcon());
        }
        HelpCtx.setHelpIDString (menu, RemoveVcsGroupAction.class.getName ());

        JMenuItem item=null;
        DataFolder folder = GroupUtils.getMainVcsGroupFolder();
        FileObject foFolder = folder.getPrimaryFile();
        Enumeration children = foFolder.getData(false);
        while (children.hasMoreElements()) {
            FileObject fo = (FileObject)children.nextElement();
            if (fo.getExt().equals(VcsGroupNode.PROPFILE_EXT)) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fo.getInputStream()));
                    String dispName = getBundleValue(reader.readLine());
                    FileObject f = foFolder.getFileObject(fo.getName());
                    if (f != null && f.isFolder()) {
                        menu.add(createItem(fo.getName(), dispName));
                    }
                } catch (Exception exc) {
                    // just ignore missing resource or error while reading the props..
                    System.out.println("remove from group exc=" + exc.getClass());
                }
            }
        }
        return menu;
    }
    
    private String getBundleValue(String keyValue) {
        if (keyValue != null) {
            int index = keyValue.indexOf('=');
            if (index > 0 && keyValue.length() > index) {
                return keyValue.substring(index + 1);
            }
        }
        return "";
    }    

    //-------------------------------------------
    private JMenuItem createItem(String name, String dispName){
        JMenuItem item=null ;
        
        //item=new JMenuItem(g(name));
        item = new JMenuItem ();
        Actions.setMenuText (item, dispName, false);
        item.setActionCommand(dispName);
        item.addActionListener(this);
        return item;
    }    

    protected boolean enable(org.openide.nodes.Node[] node) {
        FileObject folder = GroupUtils.getMainVcsGroupFolder().getPrimaryFile();
        Enumeration children = folder.getFolders(false);
        if (children.hasMoreElements()) {
            return true;
        } else {
            return false;
        }
    }    

    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        String groupName = actionEvent.getActionCommand();
        Node grFolder = GroupUtils.getMainVcsGroupNodeInstance();
        Node[] dobjs = grFolder.getChildren().getNodes();
        DataFolder group = null;
        if (dobjs == null) return;
        for (int i = 0; i < dobjs.length; i++) {
            if (dobjs[i].getName().equals(groupName)) {
                DataFolder fold = (DataFolder)dobjs[i].getCookie(DataObject.class);
                group = fold;
                break;
            }
        }
        if (group == null) return;
        NotifyDescriptor.Confirmation conf = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(RemoveVcsGroupAction.class, "RemoveVcsGroupAction.removeGroupQuestion", groupName), //NOI18N

            NotifyDescriptor.YES_NO_OPTION);
        Object retValue = DialogDisplayer.getDefault().notify(conf);
        if (retValue.equals(NotifyDescriptor.NO_OPTION)) {
            return;
        }
        if (retValue.equals(NotifyDescriptor.YES_OPTION)) {
            this.group = group;
            // Remove asynchronously so that AWT is not blocked:
            org.openide.util.RequestProcessor.getDefault().post(this);
        }
    }
    
    /**
     * Perform the actual delete of the group.
     */
    public void run() {
        if (group == null) return ;
        try {
            group.delete();
        } catch (IOException exc) {
            NotifyDescriptor excMess = new NotifyDescriptor.Message(
               NbBundle.getBundle(RemoveVcsGroupAction.class).getString("RemoveVcsGroupAction.removingError"), //NOI18N
               NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(excMess);
        }
    }
    
}
