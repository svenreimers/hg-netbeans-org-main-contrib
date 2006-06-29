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
/*
 * PovRayDataNode.java
 *
 * Created on February 16, 2005, 4:36 PM
 */

package org.netbeans.modules.povray;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.povproject.ViewService;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.EditAction;
import org.openide.actions.RenameAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;



/**
 * A DataNode for POV-Ray files
 *
 * @author Timothy Boudreau
 */
public class PovRayDataNode extends DataNode {
    
    /** Creates a new instance of PovRayDataNode */
    public PovRayDataNode(PovRayDataObject obj) {
        super (obj, Children.LEAF);
    }
    
    public Image getIcon(int type) {
        return Utilities.loadImage(
            "org/netbeans/modules/povray/resources/povicon.gif"); //NOI18N
    }
    
    public Action[] getActions(boolean context) {
        Action[] result = new Action[] {
            SystemAction.get (EditAction.class),
            new ViewImageAction (getDataObject()),
            SystemAction.get (CutAction.class),
            SystemAction.get (CopyAction.class),
            SystemAction.get (RenameAction.class),
            SystemAction.get (DeleteAction.class),
            new SetMainFileAction (getDataObject()),
        };
        return result;
    }
    
    public Action getPreferredAction() {
        return SystemAction.get (EditAction.class);
    } 
    
    public String getHtmlDisplayName() {
        return isMainFile() ? "<b>" + getDisplayName() + "</b>" : null;
    }    
    
    private boolean isMainFile() {
        FileObject file = getDataObject().getPrimaryFile();
        Project project = FileOwnerQuery.getOwner(file);
        if (project != null) {
            MainFileProvider provider = (MainFileProvider) 
                project.getLookup().lookup(MainFileProvider.class);
            
            return provider != null && provider.getMainFile() != null && 
                provider.getMainFile().equals(file);
        }
        return false;
    }
    
    public Node.Cookie getCookie(Class clazz) {
        if (clazz == NotifyMainChangedCookie.class) {
            return new NotifyMainChangedCookie();
        } else {
            return super.getCookie(clazz);
        }
    }
    
    private class NotifyMainChangedCookie implements Node.Cookie {
        public void notifyNoLongerMain () {
            fireDisplayNameChange ("<b>" + getDisplayName() + "</b>", 
                getDisplayName());
        }
    }    
    
    private final class SetMainFileAction extends AbstractAction {
        private final DataObject obj;
        
        public SetMainFileAction (DataObject obj) {
            this.obj = obj;
            
            //Set a display name
            putValue (Action.NAME, NbBundle.getMessage(PovRayDataNode.class, 
                "ACTION_SetMainFile"));
        }
        
        public void actionPerformed (ActionEvent ae) {
            Project project = FileOwnerQuery.getOwner(obj.getPrimaryFile());
            MainFileProvider provider = (MainFileProvider) 
                project.getLookup().lookup(MainFileProvider.class);

            FileObject oldMain = provider.getMainFile();
            provider.setMainFile(obj.getPrimaryFile());
            fireDisplayNameChange(getDisplayName(), getHtmlDisplayName());
            if (oldMain != null && oldMain.isValid()) {
                try {
                    Node oldMainFilesNode = DataObject.find(oldMain).getNodeDelegate();
                    
                    NotifyMainChangedCookie notifier = (NotifyMainChangedCookie) 
                        oldMainFilesNode.getCookie(NotifyMainChangedCookie.class);

                    if (notifier != null) {
                        //Just makes the node fire a display name change
                        notifier.notifyNoLongerMain();
                    }

                } catch (DataObjectNotFoundException donfe) { //Should never happen
                    ErrorManager.getDefault().notify (donfe);
                }
            }
        }
        
        public boolean isEnabled () {
            FileObject file = getDataObject().getPrimaryFile();
            Project project = FileOwnerQuery.getOwner(file);
            
            return project != null && 
                   project.getLookup().lookup(MainFileProvider.class) != null 
                   && !isMainFile();
        }
    }
    
    private final class ViewImageAction extends AbstractAction {
        private final DataObject obj;
        
        public ViewImageAction (DataObject obj) {
            this.obj = obj;
            putValue (Action.NAME, NbBundle.getMessage (PovRayDataNode.class, 
                    "ACTION_ViewImage"));
        }
        
        /**
         * Disables the action if it doesn't belong to a project, or if it
         * belongs to a non PovProject project
         */
        public boolean isEnabled() {
            Project project = FileOwnerQuery.getOwner(obj.getPrimaryFile());
            if (project != null) { //otherwise, some loose .pov file in Favorites, etc.
                ViewService view = (ViewService) project.getLookup().lookup(ViewService.class);
                if (view != null) { //It's not a PovProject, maybe a .pov file in a Java project 
                    return true;
                }
            }
            return false;
        }
        
        public void actionPerformed (ActionEvent ae) {
            Project project = FileOwnerQuery.getOwner(obj.getPrimaryFile());
            ViewService view = (ViewService) project.getLookup().lookup(ViewService.class);
            
            view.view(obj.getPrimaryFile());
        }
    }    
}
