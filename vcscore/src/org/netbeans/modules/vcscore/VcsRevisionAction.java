/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore;

import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.openide.ErrorManager;

import org.openide.awt.Actions;
import org.openide.awt.JInlineMenu;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.WeakList;
import org.netbeans.modules.vcscore.versioning.RevisionItem;

/**
 *
 * @author  Martin Entlicher
 */
public class VcsRevisionAction extends NodeAction implements ActionListener, Runnable {

    protected WeakReference provider = new WeakReference(null);
    protected WeakReference fileObject = new WeakReference(null);
    protected Collection selectedRevisionItems = null;
    private String commandName;

    private static final long serialVersionUID = 8803248742536265293L;
    
    //private VcsFileSystem fileSystem = null;
    //private FileObject fo = null;
    
    //private Hashtable additionalVars = new Hashtable();
    
    //private VcsCommand openRevisionCommand = null;

    /** Creates new RevisionAction 
     * Gets revision actions from filesystem and acts on a file object.
     * Both the filesystem and file object are obtained from the revision node.
     */
    public VcsRevisionAction() {
    }
    
    /* Creates new RevisionAction 
     * Gets revision actions from filesystem and acts on the givn file object.
     * @param fileSystem the file system to get the actions from
     * @param fo the file object to act on
     *
    public RevisionAction(VcsFileSystem fileSystem, FileObject fo) {
        this.fileSystem = fileSystem;
        this.fo = fo;
    }
     */

    public void setProvider(VcsProvider provider) {
        this.provider = new WeakReference(provider);
    }
    
    public void setFileObject(FileObject fileObject) {
        this.fileObject = new WeakReference(fileObject);
    }
    
    public void setSelectedRevisionItems(Collection items) {
        if (items == null) {
            this.selectedRevisionItems = null;
            return ;
        }
        this.selectedRevisionItems = new WeakList(items);
    }
    
    public String getName(){
        return NbBundle.getMessage(VcsRevisionAction.class, "CTL_Revision_Action"); // NOI18N
    }

    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    //-------------------------------------------
    public void performAction(Node[] nodes){
        //D.deb("performAction()"); // NOI18N
    }

    //-------------------------------------------
    public boolean enable(Node[] nodes){
        //D.deb("enable()"); // NOI18N
        return nodes.length > 0;
    }

    //-------------------------------------------
    public HelpCtx getHelpCtx(){
        return new HelpCtx(VcsRevisionAction.class);
    }

    protected JMenuItem createItem(VcsCommand cmd) {
        JMenuItem item = null;
        /*
        if (cmd == null) {
            //E.err("Command "+name+" not configured."); // NOI18N
            item = new JMenuItem("'"+name+"' not configured.");
            item.setEnabled(false);
            return item;
        }
        */
        //Hashtable vars = fileSystem.getVariablesAsHashtable();
        //String label = cmd.getDisplayName();
        /*
        if (label.indexOf('$') >= 0) {
            label = Variables.expandFast(vars, label, true);
        }
         */
        item = new JMenuItem();
        Actions.setMenuText(item, cmd.getDisplayName(), false);
        String[] props = cmd.getPropertyNames();
        if (props != null && props.length > 0) {
            item.setActionCommand(cmd.getName());
            item.addActionListener(this);
        }
        return item;
    }

    /**
     * Add a popup submenu.
     */
    private void addMenu(CommandsTree commands, JMenu parent, int numSelected) {
        //Children children = commands.getChildren();
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            //Node child = (Node) subnodes.nextElement();
            //VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
            CommandSupport cmdSupp = subCommands[i].getCommandSupport();
            if (!(cmdSupp instanceof UserCommandSupport)) continue;
            VcsCommand cmd = ((UserCommandSupport) cmdSupp).getVcsCommand();
            if (cmd == null) {
                parent.addSeparator();
                continue;
            }
            int numRev = VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS);
            if (numRev != numSelected
                || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)
                || cmd.getDisplayName() == null) continue;
            if (subCommands[i].hasChildren()) {
                JMenu submenu;
                String[] props = cmd.getPropertyNames();
                //if (props == null || props.length == 0) {
                //    submenu = new JMenuPlus(cmd.getDisplayName());
                //} else {
                    submenu = new JMenuPlus();
                //}
                Actions.setMenuText(submenu, cmd.getDisplayName(), false);
                addMenu(subCommands[i], submenu, numSelected);
                parent.add(submenu);
            } else {
                JMenuItem item = createItem(cmd);
                parent.add(item);
            }
        }
    }

    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        JInlineMenu inlineMenu = new JInlineMenu();
        ArrayList menuItems = new ArrayList();
        /*
        Node[] nodes = getActivatedNodes();
        RevisionList rList = null;
        for (int i = 0; i < nodes.length; i++) {
            RevisionList list = (RevisionList) nodes[i].getCookie(RevisionList.class);
            if (list == null) continue;
            RevisionItem item = (RevisionItem) nodes[i].getCookie(RevisionItem.class);
            if (item == null) continue;
            //if (nodes[i] instanceof RevisionNode) {
            //    VcsFileSystem nodeFS = (VcsFileSystem) ((RevisionNode) nodes[i]).getFileSystem();
            if (rList == null) rList = list;
            else if (rList != list) return null;
            }
        }
        if (rList == null) return null;
         */
        VcsProvider provider = (VcsProvider) this.provider.get();
        if (provider == null) return null;
        CommandsTree commands = provider.getCommands();
        //Node commands = fileSystem.getCommands();
        //Children children = commands.getChildren();
        CommandsTree[] commandRoots = commands.children();
        int numRevisions = selectedRevisionItems.size();
        for (int i = 0; i < commandRoots.length; i++) {
            CommandSupport cmdSupp = commandRoots[i].getCommandSupport();
            if (!(cmdSupp instanceof UserCommandSupport)) continue;
            VcsCommand cmd = ((UserCommandSupport) cmdSupp).getVcsCommand();
            //VcsCommand cmd = (VcsCommand) commandRoots[i].getCookie(VcsCommand.class);
            if (cmd != null
                && VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS) == numRevisions
                && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)
                && cmd.getDisplayName() != null) {
                    
                JMenuItem menuItem = getPopupPresenter(commandRoots[i], cmd, numRevisions);
                if (menuItem != null) menuItems.add(menuItem);
            }
        }
        inlineMenu.setMenuItems((JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]));
        return inlineMenu;
    }

    private JMenuItem getPopupPresenter(CommandsTree commandRoot, VcsCommand cmd, int numSelected) {
        //String name = commandRoot.getCommandSupport().getDisplayName();
        JMenuItem menu = new JMenuPlus();
        Actions.setMenuText(menu, commandRoot.getCommandSupport().getDisplayName(), false);
        addMenu(commandRoot, (JMenu) menu, numSelected);
        if (menu.getSubElements().length == 0) {
            menu = createItem(cmd);
        }
        return menu;
    }
    
    public void actionPerformed(final java.awt.event.ActionEvent e){
        //D.deb("actionPerformed("+e+")"); // NOI18N
        this.commandName = e.getActionCommand();
        //D.deb("cmd="+cmd); // NOI18N
        org.openide.util.RequestProcessor.getDefault().post(this);
    }
    
    private String getBranch(RevisionItem[] items) {
        String branchTag = "";
        for (int i = 0; i < items.length; i++) {
            if (items[0] != null && items[0].isBranch()) {
                String[] tags = items[0].getTagNames();
                if (tags.length > 0) branchTag = tags[0];
            }
        }
        return branchTag;
    }
    
    /**
     * Actually perform the action.
     */
    public void run() {
        if (this.commandName == null) return ;
        VcsProvider provider = (VcsProvider) this.provider.get();
        FileObject fo = (FileObject) this.fileObject.get();
        if (provider == null || fo == null) return ;
        RevisionItem[] items = (RevisionItem[]) selectedRevisionItems.toArray(new RevisionItem[0]);
        FileObject[] files = new FileObject[] { fo };
        String mimeType = fo.getMIMEType();
        String fileName = fo.getPath();
        Hashtable additionalVars = new Hashtable();
        if (mimeType != null) additionalVars.put("MIMETYPE", mimeType); // NOI18N
        if (items.length > 0) {
            additionalVars.put("REVISION", items[0].getRevisionVCS());
        }
        for(int i = 0; i < items.length; i++) {
            //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
            additionalVars.put("REVISION"+(i+1), items[i].getRevisionVCS());
        }
        additionalVars.put("BRANCH", getBranch(items));
        //D.deb("files="+files); // NOI18N

        //doCommand (files, cmd, fileSystem);
        CommandSupport supp = provider.getCommandSupport(commandName);
        if (supp == null) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalStateException("Command '"+commandName+"' does not exist."));
            return ;
        }
        Command cmd = supp.createCommand();
        ((VcsDescribedCommand) cmd).setAdditionalVariables(additionalVars);
        files = cmd.getApplicableFiles(files);
        if (files != null) {
            cmd.setFiles(files);
            if (VcsManager.getDefault().showCustomizer(cmd)) {
                cmd.execute();
            }
        }
    }
    
}
