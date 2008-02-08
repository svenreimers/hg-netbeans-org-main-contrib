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
package org.netbeans.modules.websvc.axis2.actions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.prefs.Preferences;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.websvc.axis2.AxisUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;
import org.openide.util.NbPreferences;
import org.openide.util.actions.NodeAction;

public class DeployAction extends NodeAction  {
    
    public String getName() {
        return NbBundle.getMessage(DeployAction.class, "LBL_DeployAction");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
        
    @Override
    protected boolean asynchronous() {
        return true;
    }
    
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes==null || activatedNodes.length != 1) return false;      
        else return true;
    }
    
    protected void performAction(Node[] activatedNodes) {
        Project project = activatedNodes[0].getLookup().lookup(Project.class);
        if (project == null) {
            FileObject srcRoot = activatedNodes[0].getLookup().lookup(FileObject.class);
            project = FileOwnerQuery.getOwner(srcRoot);
        }
        AxisUtils.runTargets(project.getProjectDirectory(), new String[]{"axis2-deploy"}); //NOI18N
        Preferences preferences = NbPreferences.forModule(AxisConfigurationAction.class);
        String tomcatUser = preferences.get("TOMCAT_MANAGER_USER", null);
        if (tomcatUser != null) {
            try {
                String tomcatPassword = preferences.get("TOMCAT_MANAGER_PASSWORD", null);
                URL reloadAxisUrl = new URL("http://localhost:8080/manager/html/reload?path=/axis2");
                URLConnection conn = reloadAxisUrl.openConnection();
                HttpURLConnection hconn = (HttpURLConnection) conn;
                hconn.setAllowUserInteraction(false);
                hconn.setRequestProperty("User-Agent", // NOI18N
                         "NetBeansIDE-Tomcat-Manager/1.0"); // NOI18N
                String input = tomcatUser + ":" + tomcatPassword;
                String auth = new String(Base64.encode(input.getBytes()));                
                //String auth = input;
                hconn.setRequestProperty("Authorization", // NOI18N
                                         "Basic " + auth); // NOI18N
                hconn.connect();
                int respCode = hconn.getResponseCode();
                System.out.println("Server response = "+respCode);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }

}
