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

package org.netbeans.modules.vcscore.runtime;

import org.openide.util.actions.NodeAction;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.netbeans.modules.vcscore.runtime.*;


/**
 * The action that attempts to stop the running command.
 *
 * @author  Milos Kleint
 */
public class KillRunningCommandAction extends NodeAction {

    private static final Object instanceLock = new Object();
    private static KillRunningCommandAction instance = null;
    
    private static final long serialVersionUID = 6386534364548632176L;
    
    /** Creates new KillRunningCommandAction */
    private KillRunningCommandAction() {
    }
    
    public static KillRunningCommandAction getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new KillRunningCommandAction();
                }
            }
        }
        return instance;
    }

    public String getName() {
        return g("CTL_Kill_Running_Command_Action"); // NOI18N
    }

    public void performAction(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof RuntimeCommandNode) {
                RuntimeCommandNode node = (RuntimeCommandNode) nodes[i];
                final RuntimeCommand comm = node.getRuntimeCommand();
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        comm.killCommand();
                    }
                });
            }
        }
    }

    public boolean enable(Node[] nodes){
        return nodes.length > 0;
    }

    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }


    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(KillRunningCommandAction.class).getString(s);
    }
    
}
