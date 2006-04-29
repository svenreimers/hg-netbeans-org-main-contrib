/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.fixtabs;

import java.awt.event.ActionEvent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

/**
 *
 * @author Andrei Badea
 */
public final class HighlightTabsAction extends BooleanStateAction {

    public String getName() {
        return NbBundle.getMessage(HighlightTabsAction.class, "LBL_HighlightWhitespaceAction");
    }

    protected void initialize() {
        super.initialize();
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    public void actionPerformed(ActionEvent ev) {
        super.actionPerformed(ev);
        ConvertTabsOptions.getDefault().setHighlightingEnabled(getBooleanState());
    }
}
