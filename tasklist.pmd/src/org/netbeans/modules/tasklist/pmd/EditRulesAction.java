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

package org.netbeans.modules.tasklist.pmd;

import java.awt.Component;
import java.awt.Dialog;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import org.openide.explorer.propertysheet.*;
import org.netbeans.modules.tasklist.client.Suggestion;

import pmd.config.PMDOptionsSettings;
import pmd.config.ui.RuleEditor;

/**
 * Edit the set of PMD rules used by the rule violation provider.
 * <p>
 *
 * @author Tor Norbye
 */

public class EditRulesAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }
    
    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length != 1)) {
            return false;
        }
        Suggestion s = (Suggestion)node[0].getCookie(Suggestion.class);
        if (s == null) {
            return false;
        }
        return true;
    }

    protected void performAction(Node[] node) {
        PMDOptionsSettings settings = PMDOptionsSettings.getDefault();
        String rules = settings.getRules();
        RuleEditor editor = new RuleEditor();
        editor.setValue(rules);
        Component customizer = editor.getCustomEditor();

        DialogDescriptor d = new DialogDescriptor(customizer,
                    NbBundle.getMessage(EditRulesAction.class,
                    "TITLE_editRules")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
        if (d.getValue() == NotifyDescriptor.OK_OPTION) {
            Object value = editor.getValue();
            settings.setRules(value.toString());
            Suggestion s = (Suggestion)node[0].getCookie(Suggestion.class);
            if (s != null) {
                Object seed = s.getSeed();
                if (seed instanceof ViolationProvider) {
                    // TODO invalidate the suggestion instead 
                    //((ViolationProvider)seed).rescan();
                }
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(EditRulesAction.class,
                                   "EditRules"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/pmd/editRules.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
