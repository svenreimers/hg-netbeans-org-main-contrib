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

package org.netbeans.modules.vcscore.actions;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;

/**
 *
 * @author  Milos Kleint
 */
public class UpdateCommandAction extends GeneralCommandAction {

    static final long serialVersionUID = 3769929829459499506L;
    private String name = null;
    
    /** Creates new UpdateCommandAction */
    public UpdateCommandAction() {
    }

    public HelpCtx getHelpCtx() {
        HelpCtx retValue;
        
        retValue = super.getHelpCtx();
        return retValue;
    }
    
    protected String iconResource() {
       return "org/netbeans/modules/vcscore/actions/UpdateCommandActionIcon.gif";
    }
    
    public String getName() {
        if (name == null) {
            name = NbBundle.getBundle(UpdateCommandAction.class).getString("LBL_UpdateAction");
        }
        return name;
    }
    
}
