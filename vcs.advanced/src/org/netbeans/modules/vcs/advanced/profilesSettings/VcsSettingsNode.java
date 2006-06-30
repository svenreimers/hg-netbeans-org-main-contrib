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

package org.netbeans.modules.vcs.advanced.profilesSettings;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import org.netbeans.modules.vcs.advanced.ConfigSaveAsDialog;

import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcscore.commands.CommandsTree;

import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Children.SortedArray;
import org.openide.nodes.CookieSet;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.actions.PasteAction;


/** 
 * Subnodes of this node are nodes representing profile settings
 *
 * @author Richard Gregor
 */
public class VcsSettingsNode extends AbstractNode {
    
    /** Array of the actions of the java methods, constructors and fields. */
    private static final SystemAction[] DEFAULT_ACTIONS =
        new SystemAction[] { SystemAction.get(NewAction.class),
                             SystemAction.get(PasteAction.class) } ;
    
    public static final String ICON_BASE =
    "org/netbeans/modules/vcs/advanced/vcsGeneric"; // NOI18N         
    
    public VcsSettingsNode() {        
        super(new VcsSettingsChildren());                     
        setName( NbBundle.getBundle(VcsSettingsNode.class).getString("LBL_VcsSettingsNode"));
        setIconBase(ICON_BASE);
        setActions(DEFAULT_ACTIONS);
    }
    
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(VcsSettingsNode.class);
    }
    
    /** Set all actions for this node.
     * @param actions new list of actions
     */
    public void setActions(SystemAction[] actions) {
        systemActions = actions;
    }
    
    /** Serialization */
    public Node.Handle getHandle() {
        return new VcsHandle();
    }
    
    /** Handle for this node, it is serialized instead of node */
    static final class VcsHandle implements Node.Handle {
        static final long serialVersionUID =-3256331604791682300L;
        public Node getNode() {
            return new VcsSettingsNode();
        }
    }
    
    /** Get the new types that can be created in this node.
     */
    public NewType[] getNewTypes() {
        //if (list == null) return new NewType[0];
        return new NewType[] { new NewProfile() };
    }
    
    private final class NewProfile extends NewType {

        public String getName() {
            return org.openide.util.NbBundle.getBundle(VcsSettingsNode.class).getString("CTL_NewProfile_ActionName");
        }
        
        public void create() throws java.io.IOException {
            NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                org.openide.util.NbBundle.getBundle(VcsSettingsNode.class).getString("CTL_NewProfileName"),
                org.openide.util.NbBundle.getBundle(VcsSettingsNode.class).getString("CTL_NewProfileTitle")
                );
            //input.setInputText(org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommandLabel"));
            if (DialogDisplayer.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                return;

            String profileName = input.getInputText();
            
            try {
                while (profileName.indexOf('_') > 0 && ConfigSaveAsDialog.willChangeNameWithUnderscore(profileName)) {
                    if (DialogDisplayer.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                        return;
                    profileName = input.getInputText();
                }
            } catch (UserCancelException ucex) {
                return ;
            }
            
            ProfilesFactory.getDefault().addProfile(profileName, profileName, null, null,
                new HashSet(), new HashSet(), new ConditionedVariables(new ArrayList(), new HashMap(), new HashMap()),
                new ConditionedCommandsBuilder(CommandsTree.EMPTY).getConditionedCommands());
        }
    }
    
}

