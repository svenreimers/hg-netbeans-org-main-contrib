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

package org.netbeans.modules.vcscore.grouping;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import java.io.*;
import org.openide.DialogDisplayer;

/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class AddVcsGroupAction extends NodeAction implements Runnable {

    private static final long serialVersionUID = -3385132838696775732L;
    
    private String newName;
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction (Node[] nodes) {
        // do work based on the current node selection, e.g.:
        NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine( 
             NbBundle.getBundle(AddVcsGroupAction.class).getString("AddVcsGroupAction.groupName"), //NOI18N
             NbBundle.getBundle(AddVcsGroupAction.class).getString("AddVcsGroupAction.groupNameTitle"));//NOI18N
        Object retValue = DialogDisplayer.getDefault().notify(line);
        if (!retValue.equals(DialogDescriptor.OK_OPTION)) return;
        
        this.newName = line.getInputText();
        org.openide.util.RequestProcessor.getDefault().post(this);
    }

    /**
     * Perform the actual addition.
     */
    public void run() {
        if (newName == null) return ;
        DataFolder rootFolder = GroupUtils.getMainVcsGroupFolder();
        if (rootFolder != null) {
            FileObject fo = rootFolder.getPrimaryFile();
            String foldName = FileUtil.findFreeFolderName(fo, "group");//NOI18N
            FileObject props = fo.getFileObject(foldName, VcsGroupNode.PROPFILE_EXT);
            PrintWriter writer = null;
            FileLock lock = null;
            try {
                if (props == null) {
                    props = fo.createData(foldName, VcsGroupNode.PROPFILE_EXT);
                }
                lock = props.lock();
                writer = new PrintWriter(props.getOutputStream(lock));
                writer.println(VcsGroupNode.PROP_NAME + "=" + newName);//NOI18N
                writer.close();
                FileObject group = rootFolder.getPrimaryFile().createFolder(foldName);
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            } finally {
                if (writer != null) {
                    writer.close();
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
             
        // ...
    }
    
    protected boolean enable (Node[] nodes) {
        // e.g.:
        return true;
    }

    public String getName () {
        return NbBundle.getMessage(AddVcsGroupAction.class, "LBL_AddVcsGroupAction");//NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/vcscore/grouping/AddVcsGroupActionIcon.gif";//NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (AddVcsGroupAction.class);
    }

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
    protected void initialize () {
	super.initialize ();
     * putProperty (Action.SHORT_DESCRIPTION, NbBundle.getMessage (AddVcsGroupAction.class, "HINT_Action"));
    }
    */

}
