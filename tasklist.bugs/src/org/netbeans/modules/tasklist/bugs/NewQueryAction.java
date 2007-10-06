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

package org.netbeans.modules.tasklist.bugs;

import java.awt.Dialog;
import java.awt.Dimension;
import org.netbeans.modules.tasklist.core.TaskListView;

import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


/**
 * Action which brings up a dialog where you can create
 * a new query.
 * @author Tor Norbye, Trond Norbye, serff
 */
public class NewQueryAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean enable(Node[] node) {
        return true;
    }

    /** Utility method which attempts to find the activated nodes
	for the currently showing topcomponent in the editor window.
    */
    private static Node[] getEditorNodes() {
        // First try to get the editor window itself; if you right click
        // on a node in the Todo Window, that node becomes the activated
        // node (which is good - it makes the properties window show the
        // todo item's properties, etc.) but that means that we can't
        // find the editor position via the normal means.
        // So, we go hunting for the topmosteditor tab, and when we find it,
        // ask for its nodes.
        Node[] nodes = null;
        WindowManager wm = WindowManager.getDefault();
        
        // HACK ALERT !!! HACK ALERT!!! HACK ALERT!!!
        // Look for the source editor window, and then go through its
        // top components, pick the one that is showing - that's the
        // front one!
        Mode mode  = wm.findMode(CloneableEditorSupport.EDITOR_MODE);
	if (mode == null) {
	    return null;
	}
        TopComponent [] tcs = mode.getTopComponents();
        for (int j = 0; j < tcs.length; j++) {
            TopComponent tc = tcs[j];
            if (tc instanceof org.openide.text.EditorSupport.Editor) {
                // Found the source editor...
                if (tcs[j].isShowing()) {
                    nodes = tcs[j].getActivatedNodes();
                    break;
                }
            }
        }
	return nodes;
    }
     
    protected void performAction(Node[] node) {
        BugQuery query = new BugQuery();
        EditQueryPanel panel = new EditQueryPanel(query, false);

        DialogDescriptor d = new DialogDescriptor(panel,
             NbBundle.getMessage(NewQueryAction.class,
                                 "TITLE_NEW_QUERY")); // NOI18N
        d.setModal(true);
        d.setHelpCtx(new HelpCtx("NewQuery")); // NOI18N
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();

        if (d.getValue() == NotifyDescriptor.OK_OPTION) {
            //do the new bug query
            String bugEngine = panel.getBugEngine();
            String queryString = panel.getQueryString();
            query = panel.getQuery();
            System.out.println("bugEngine = " + bugEngine + "\nqueryString = " + queryString);

            TaskListView tv = new BugsView(query);
            tv.showInMode();
        }
    }

    protected boolean asynchronous() {
        return false;
    }
    /**
     * @param parentNode default parent; if null, don't parent
     * @param filename suggested filename
     * @parem line suggested line number (1-based)
     * @param associate if true, set the checkbox for the filename by default (only
     *         makes sense if filename != null)
     */
//    public static void performAction(final Task parent, 
//                                     final String filename,
//                                     final int line,
//                                     final boolean associate) {
//        // We've gotta do this from the AWT thread, and for some reason, even though
//        // these actions are initiated through Swing actions, they come in on
//        // the ModuleActions thread. So dispatch to the AWT thread.
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                // Pick the right list to use
//                UserTaskView view =  (UserTaskView)UserTaskView.getCurrent();
//                UserTaskList taskList = null;
//                if (view != null) {
//                    taskList = (UserTaskList)view.getList();
//                } else {
//                    taskList = UserTaskList.getDefault();
//                }
//                EditTaskPanel panel = new EditTaskPanel(taskList,
//                                                        (UserTask)parent, null,
//                                                        false);
//                if (filename != null) {
//                    panel.setFilename(filename);
//                    if (line != 0) {
//                        panel.setLineNumber(line);
//                    }
//                    panel.setAssociatedFilePos(associate);
//                }
//                
//                panel.setPreferredSize(new Dimension(600,500));
//                DialogDescriptor d = new DialogDescriptor(panel,
//                     NbBundle.getMessage(NewQueryAction.class,
//                                         "TITLE_add_todo")); // NOI18N
//                d.setModal(true);
//                d.setHelpCtx(new HelpCtx("NewTask")); // NOI18N
//                d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
//                d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
//                Dialog dlg = TopManager.getDefault().createDialog(d);
//                dlg.pack();
//                dlg.show();
//
//                if (d.getValue() == NotifyDescriptor.OK_OPTION) {
//                    String description = panel.getSummary();
//                    int priority = panel.getPrio();
//                    String details = panel.getDetails();
//                    String fname = null;
//                    int lineno = 0;
//                    if (panel.hasAssociatedFilePos()) {
//                        fname = panel.getFilename();
//                        lineno = panel.getLineNumber();
//                    }
//                    String category = panel.getCategory();
//                    UserTask parent = (UserTask)panel.getParentItem();
//                    UserTask item = new UserTask(description, false, priority,
//                                                 fname, lineno, details,
//                                                 category, parent);
//                    // See if the user wants to append or prepend
//                    boolean append = panel.getAppend();
//                    item.setDueDate(panel.getDueDate());
//                    taskList.add(item, append, true);
//
//                    // After the add - view the todo list as well!
//                    if (view != null) {
//			view.select(item);
//                        view.showInMode();
//                    } else {
//                        ViewTasksAction.show();
//                    }
//                }
//            }
//        });
//    }
    
    public String getName() {
        return "New Query"; // NOI18N
    }
    
    protected String iconResource() {
	// I made a taskView.png, but it had ugly display artifacts (it
	// looked fine in gimp but not when running the IDE)
        return "org/netbeans/modules/tasklist/bugs/bugsView.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
