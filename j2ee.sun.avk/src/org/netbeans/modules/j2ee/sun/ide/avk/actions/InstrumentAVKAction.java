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
package org.netbeans.modules.j2ee.sun.ide.avk.actions;

import java.io.File;
import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.netbeans.modules.j2ee.sun.ide.j2ee.runtime.nodes.ManagerNode;
import org.netbeans.modules.j2ee.sun.ide.j2ee.DeploymentManagerProperties;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;

import org.netbeans.modules.j2ee.sun.ide.avk.AVKSupport;

/** 
 *
 * @author ludo
 */
public class InstrumentAVKAction extends CookieAction {
    boolean instrumented = false;
    protected static final ResourceBundle bundle = ResourceBundle.getBundle("org.netbeans.modules.j2ee.sun.ide.avk.actions.Bundle");// NOI18N
    private RequestProcessor processor = new RequestProcessor("instrument"); //NOI18N    
    protected Class[] cookieClasses() {
        return new Class[] {/* SourceCookie.class */};
    }
    
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }
    
    protected void performAction(Node[] nodes) {
        if(nodes[0].getLookup().lookup(ManagerNode.class) != null){
            try{
                final ManagerNode node = (ManagerNode)nodes[0].getLookup().lookup(ManagerNode.class);
                processor.post(new Runnable() {
                    public void run() {
                        SunDeploymentManagerInterface sdm = node.getDeploymentManager();
                        AVKSupport support = new AVKSupport(sdm);
                        support.setAVK(true);
                    }
                });
            } catch (Exception e){
                //nothing to do, the NetBeasn node system is wierd sometimes...
            }
        }
    }
    
    public String getName() {
        return bundle.getString("LBL_Instrument"); //NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/j2ee/sun/ide/resources/AddInstanceActionIcon.gif"; //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return null; 
    }
    
    protected boolean enable(Node[] nodes) {
        if( (nodes == null) || (nodes.length < 1) )
             return false;

        if(nodes[0].getLookup().lookup(ManagerNode.class) != null){
             try{
                 ManagerNode node = (ManagerNode)nodes[0].getLookup().lookup(ManagerNode.class);
                 SunDeploymentManagerInterface sdm = node.getDeploymentManager();
                 DeploymentManagerProperties dmProps = new DeploymentManagerProperties((DeploymentManager) sdm);
                 instrumented = dmProps.getAVKOn();
                 return (sdm.isLocal() && !instrumented);
             } catch (Exception e){
                 //nothing to do, the NetBeasn node system is wierd sometimes...
             }
        }
        return false;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}
