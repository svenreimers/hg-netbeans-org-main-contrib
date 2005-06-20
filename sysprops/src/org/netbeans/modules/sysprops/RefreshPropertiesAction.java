/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */

package org.netbeans.modules.sysprops;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/** Action that refreshs all Properties.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
public class RefreshPropertiesAction extends CallableSystemAction {
    
    /**
     * Performs the Action.
     */
    public void performAction() {
        PropertiesNotifier.getDefault().changed();
    }
    
    /**
     * Returns the Name of this Action.
     * @return a localized display name
     */
    public String getName() {
        return NbBundle.getMessage(RefreshPropertiesAction.class, "LBL_RefreshProps");
    }
    
    /**
     * Returns the HelpContext for this Action.
     * @return no particular help
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
}
