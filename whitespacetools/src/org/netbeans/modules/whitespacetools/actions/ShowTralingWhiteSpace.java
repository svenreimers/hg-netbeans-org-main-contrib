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
public final class ShowTralingWhiteSpace extends CookieAction {

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
                        findProps.put(SettingsNames.FIND_WHAT, WhiteSpaceConstants.TRAILING_WHITESPACE_REGEXP);
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
        return NbBundle.getMessage(ShowTralingWhiteSpace.class, "CTL_ShowTralingWhiteSpace");
    }

    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }

    protected String iconResource() {
        return "org/netbeans/modules/whitespacetools/actions/showtralingwhitespace.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

}

