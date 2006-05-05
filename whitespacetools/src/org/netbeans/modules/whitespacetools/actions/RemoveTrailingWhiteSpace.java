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

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import org.netbeans.editor.FindSupport;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.Utilities;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class RemoveTrailingWhiteSpace extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        if (ec != null) {
            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes != null) {
                TopComponent activetc = TopComponent.getRegistry().getActivated();
                for (int i = 0; i < panes.length; i++) {
                    if (activetc.isAncestorOf(panes[i])) {
                        if (!panes[i].isEditable()) {
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }
                        FindSupport findSupport = FindSupport.getFindSupport();
                        Boolean hightligtSearch = null;
                        hightligtSearch = (Boolean) findSupport.getFindProperty(SettingsNames.FIND_HIGHLIGHT_SEARCH);
                        if (hightligtSearch == null) {
                            hightligtSearch =  Boolean.FALSE;
                        }
                        // find trailing whitespace
                        Map findProps = new HashMap();
                        findProps.put(SettingsNames.FIND_BACKWARD_SEARCH, Boolean.FALSE);
                        findProps.put(SettingsNames.FIND_REG_EXP, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_HIGHLIGHT_SEARCH, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_WRAP_SEARCH, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_WHAT, WhiteSpaceConstants.TRAILING_WHITESPACE_REGEXP);
                        findSupport.putFindProperties(findProps);
                        boolean found = findSupport.find(null, false);
                        if (found) {
                            if (findSupport.find(null, true)) {
                                findSupport.find(null, true);
                            }
                        }
                        // if found ask the user
                        if (found && JOptionPane.showConfirmDialog(
                                WindowManager.getDefault().getMainWindow(),
                                NbBundle.getMessage(RemoveTrailingWhiteSpace.class, "CTL_RemoveTrailingWhiteSpace_Question"), // NOI18N
                                NbBundle.getMessage(RemoveTrailingWhiteSpace.class, "CTL_RemoveTrailingWhiteSpace_Title"), // NOI18N
                                JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                            findSupport = FindSupport.getFindSupport();
                            findProps = new HashMap();
                            findProps.put(SettingsNames.FIND_REG_EXP, Boolean.TRUE);
                            findProps.put(SettingsNames.FIND_HIGHLIGHT_SEARCH, Boolean.FALSE);
                            findProps.put(SettingsNames.FIND_WRAP_SEARCH, Boolean.TRUE);
                            findProps.put(SettingsNames.FIND_WHAT, WhiteSpaceConstants.TRAILING_WHITESPACE_REGEXP);
                            findProps.put(SettingsNames.FIND_REPLACE_WITH, ""); // NOI18N
                            findSupport.putFindProperties(findProps);
                            findSupport.replaceAll(null);
                        }
                        // not found or user clicked cancel - reset the
                        // highlight search to original state
                        findProps = new HashMap();
                        findProps.put(SettingsNames.FIND_REG_EXP, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_HIGHLIGHT_SEARCH, hightligtSearch);
                        findProps.put(SettingsNames.FIND_WRAP_SEARCH, Boolean.TRUE);
                        findProps.put(SettingsNames.FIND_WHAT, WhiteSpaceConstants.TRAILING_WHITESPACE_REGEXP);
                        findSupport.putFindProperties(findProps);
                        findSupport.find(null, false);
                        Utilities.clearStatusText(Utilities.getLastActiveComponent());
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
        return NbBundle.getMessage(RemoveTrailingWhiteSpace.class, "CTL_RemoveTrailingWhiteSpace");
    }

    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }

    protected String iconResource() {
        return "org/netbeans/modules/whitespacetools/actions/removetralingwhitespace.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

}

