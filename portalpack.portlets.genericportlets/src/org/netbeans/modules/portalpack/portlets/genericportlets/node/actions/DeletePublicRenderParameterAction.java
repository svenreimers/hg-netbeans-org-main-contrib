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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node.actions;

import org.netbeans.modules.portalpack.portlets.genericportlets.node.SupportedPublicRenderParameterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.NodeAction;
/**
 *
 * @author Satyaranjan
 */
public class DeletePublicRenderParameterAction extends NodeAction{

    public DeletePublicRenderParameterAction() {
    }

     protected int mode() {
         return CookieAction.MODE_EXACTLY_ONE;
    }

    protected Class<?>[] cookieClasses() {
        return new Class[] {};
    }

    protected void performAction(Node[] nodes) {
        if( (nodes == null) || (nodes.length != 1))
            return;
        
        final SupportedPublicRenderParameterNode node = nodes[0].getLookup().lookup(SupportedPublicRenderParameterNode.class);
       
        if(node != null)
        {
            node.getDataObject().getPortletXmlHelper().removeSupportedPublicRenderParameter(node.getPortletName(), node.getSupportedPublicRenderParameterName());
 
        }

    }

    public String getName() {
        return NbBundle.getMessage(AddPublicRenderParameterAction.class, "LBL_DELETE_RENDER_PARAMETER");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
     protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] nodes) {
         if( (nodes == null) || (nodes.length != 1))
            return false;
         else
             return true;
         
    }
}
