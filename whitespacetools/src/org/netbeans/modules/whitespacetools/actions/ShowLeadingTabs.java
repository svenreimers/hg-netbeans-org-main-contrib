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

package org.netbeans.modules.whitespacetools.actions;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import org.netbeans.editor.FindSupport;
import org.netbeans.editor.SettingsNames;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ShowLeadingTabs extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        if (ec != null) {
            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes != null) {
                TopComponent activetc = TopComponent.getRegistry().getActivated();
                for (int i = 0; i < panes.length; i++) {
                    if (activetc.isAncestorOf(panes[i])) {
                        FindSupport findSupport = FindSupport.getFindSupport();
                        Map findProps = new HashMap();
                        findProps.put(SettingsNames.FIND_REG_EXP, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_HIGHLIGHT_SEARCH, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_WRAP_SEARCH, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_WHAT, WhiteSpaceConstants.LEADING_TABS_REGEXP);
                        findSupport.putFindProperties(findProps);
                        findSupport.find(null, false);
                        break;
                    }
                }
            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(ShowLeadingTabs.class, "CTL_ShowLeadingTabs");
    }

    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }

    protected String iconResource() {
        return "org/netbeans/modules/whitespacetools/actions/showleadingtabs.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

}

