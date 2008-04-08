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

/*
 * WS70WebModulesRootNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


import java.util.Collection;

import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.RefreshWebModulesAction;
/**
 *
 * @author Administrator
 */
public class WS70WebModulesRootNode extends AbstractNode implements Node.Cookie{
    
    /** Creates a new instance of WS70WebModulesRootNode */
    public WS70WebModulesRootNode(Lookup lookup) {
        super(new WS70WebModulesChildren(lookup));
        setDisplayName(NbBundle.getMessage(WS70WebModulesRootNode.class, "LBL_WEAPP_NODE_NAME"));
        getCookieSet().add(this);
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/WebAppFolderIcon.gif");
    }
    public javax.swing.Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(RefreshWebModulesAction.class)
        };        
    }    
    // Refresh to be called from the RefreshWebModulesAction performAction    
    public void refresh(){
        
        ((WS70WebModulesChildren)getChildren()).updateKeys();
    }
    
}