/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.bugs;

import org.netbeans.modules.tasklist.core.TaskNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;


/**
   @author Tor Norbye
*/
public class ViewBugAction extends NodeAction {

    protected boolean enable(Node[] node) {
        return ((node != null) &&
		(node.length == 1) &&
                (TaskNode.getTaskNode(node[0]) != null) &&
                (TaskNode.getTaskNode(node[0]) instanceof BugNode));
    }
    
    protected void performAction(Node[] node) {
	BugNode n = (BugNode)TaskNode.getTaskNode(node[0]);
	Bug bug = (Bug)n.getTodoItem();
	bug.view();
    }
    
    public String getName() {
        return NbBundle.getMessage(ViewBugAction.class, "ViewBug"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/bugs/viewbug.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
