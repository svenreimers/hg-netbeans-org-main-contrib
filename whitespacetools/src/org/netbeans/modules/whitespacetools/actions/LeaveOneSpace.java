/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.whitespacetools.actions;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.netbeans.editor.BaseDocument;
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
public final class LeaveOneSpace extends CookieAction {
    
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
                        int caretPosition = panes[i].getCaretPosition();
                        Document doc = panes[i].getDocument();
                        if (doc instanceof BaseDocument) {
                            ((BaseDocument)doc).atomicLock();
                        }
                        try {
                            if (caretPosition < 0 || caretPosition >= doc.getLength()) {
                                return;
                            }
                            try {
                                FindSupport findSupport = FindSupport.getFindSupport();
                                Map findProps = new HashMap();
                                findProps.put(SettingsNames.FIND_REG_EXP, Boolean.TRUE);
                                findProps.put(SettingsNames.FIND_WHAT, WhiteSpaceConstants.WHITESPACE_REGEXP);
                                findProps.put(SettingsNames.FIND_HIGHLIGHT_SEARCH, Boolean.FALSE);
                                int[] foundAt = findSupport.findInBlock(panes[i], caretPosition, caretPosition, doc.getLength(), findProps, false);
                                
                                // found a sequence of spaces or tabs at the caret position followed by a non space or tab.
                                if (foundAt != null && foundAt[0] == caretPosition) {
                                    // replace with a space and the trailing non space or tab.
                                    findProps.put(SettingsNames.FIND_REPLACE_WITH, " $2"); // NOI18N
                                    findSupport.putFindProperties(findProps);
                                    if (findSupport.replace(null, false)) {
                                        panes[i].setCaretPosition(Math.max(0, panes[i].getCaretPosition() -2));
                                    }
                                } else if (doc.getText(caretPosition, 1).equals(" ")) {
                                    doc.remove(caretPosition, 1);
                                } else if (caretPosition > 0) {
                                    caretPosition--;
                                    if (doc.getText(caretPosition, 1).equals(" ")) {
                                        doc.remove(caretPosition, 1);
                                    }
                                }
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        } finally {
                            if (doc instanceof BaseDocument) {
                                ((BaseDocument)doc).atomicUnlock();
                            }
                        }
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
        return NbBundle.getMessage(LeaveOneSpace.class, "CTL_LeaveOneSpace");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/whitespacetools/actions/leaveonespace.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}

