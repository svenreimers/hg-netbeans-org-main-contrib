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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.glassfish.javaee;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.glassfish.javaee.ide.MonitorProgressObject;
import org.netbeans.modules.glassfish.javaee.ide.DummyProgressObject;
import org.netbeans.modules.glassfish.javaee.ide.Hk2PluginProperties;
import org.netbeans.modules.glassfish.javaee.ide.Hk2Target;
import org.netbeans.modules.glassfish.javaee.ide.Hk2TargetModuleID;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.spi.glassfish.AppDesc;
import org.netbeans.spi.glassfish.GlassfishModule;
import org.netbeans.spi.glassfish.ServerUtilities;


/**
 *
 * @author Ludovic Champenois
 * @author Peter Williams
 */
public class Hk2DeploymentManager implements DeploymentManager {

    private volatile ServerInstance serverInstance;
    private volatile InstanceProperties instanceProperties;
    private Hk2PluginProperties pluginProperties;
    private String uname;
    private String passwd;
    private String uri;

    
    /**
     * 
     * @param uri 
     * @param uname 
     * @param passwd 
     */
    public Hk2DeploymentManager(String uri, String uname, String passwd) {
        this.uri = uri;
        this.uname = uname;
        this.passwd = passwd;
        pluginProperties = new Hk2PluginProperties(this);
    }
        
    
    /**
     * 
     * @param deployableObject 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.InvalidModuleException 
     */
    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) 
            throws InvalidModuleException {
        return new Hk2Configuration(deployableObject);
    }

    /**
     * 
     * @param targetList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target [] targetList, File moduleArchive, File deploymentPlan) 
            throws IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.distribute(target, file, file) not supported yet.");
    }

    /**
     * 
     * @param targetList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target [] targetList, InputStream moduleArchive, InputStream deploymentPlan) 
            throws IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.distribute(target [], stream, stream) not supported yet.");
    }

    /**
     * 
     * @param targetList
     * @param type
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target [] targetList, ModuleType type, InputStream moduleArchive, InputStream deploymentPlan) 
            throws IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.distribute(target [], module_type, stream, stream) not supported yet.");
    }

    /**
     * 
     * @param moduleIDList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject redeploy(TargetModuleID [] moduleIDList, File moduleArchive, File deploymentPlan) 
            throws UnsupportedOperationException, IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.redeploy(target_module [], file, file) not supported yet.");
    }

    /**
     * 
     * @param moduleIDList
     * @param moduleArchive
     * @param deploymentPlan
     * @return
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject redeploy(TargetModuleID [] moduleIDList, InputStream moduleArchive, InputStream deploymentPlan) 
            throws UnsupportedOperationException, IllegalStateException {
        throw new UnsupportedOperationException(
                "Hk2DeploymentManager.redeploy(target_module [], stream, stream) not supported yet.");
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject undeploy(TargetModuleID [] targetModuleIDs) 
            throws IllegalStateException {
        // !PW FIXME handle arrays with length > 1 (EARs?)
        if(targetModuleIDs != null && targetModuleIDs.length > 0) {
            GlassfishModule commonSupport = getCommonServerSupport();
            MonitorProgressObject progressObject = new MonitorProgressObject(
                    this, targetModuleIDs[0], CommandType.UNDEPLOY);
            commonSupport.undeploy(progressObject, targetModuleIDs[0].getModuleID());
            return progressObject;
        } else {
            throw new IllegalArgumentException("No TargetModuleID's specified.");
        }
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject stop(TargetModuleID[] moduleIDList) throws IllegalStateException {
        return new DummyProgressObject(moduleIDList[0]);
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject start(TargetModuleID [] moduleIDList) throws IllegalStateException {
        return new DummyProgressObject(moduleIDList[0]);
    }

    /**
     * 
     * @param locale 
     * @throws java.lang.UnsupportedOperationException 
     */
    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
    }

    /**
     * 
     * @param locale 
     * @return 
     */
    public boolean isLocaleSupported(java.util.Locale locale) {
        return false;
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    public TargetModuleID [] getAvailableModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        return getDeployedModules(moduleType, targetList);
    }
        

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    public TargetModuleID [] getNonRunningModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        Logger.getLogger("glassfish.javaee").log(Level.WARNING, 
                "Hk2DeploymentManager.getNonRunningModules() not supported yet.");
        return new TargetModuleID[0];
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    public TargetModuleID [] getRunningModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        return getDeployedModules(moduleType, targetList);
    }
    
    private TargetModuleID [] getDeployedModules(ModuleType moduleType, Target [] targetList) 
            throws TargetException, IllegalStateException {
        List<TargetModuleID> moduleList = new ArrayList<TargetModuleID>();
        GlassfishModule commonSupport = getCommonServerSupport();
        if(commonSupport != null) {
            AppDesc [] appList = commonSupport.getModuleList(GlassfishModule.WEB_CONTAINER);
            if(appList != null && appList.length > 0) {
                for(AppDesc app: appList) {
                    moduleList.add(new Hk2TargetModuleID(targetList[0], app.getName(), 
                            app.getName(), app.getPath()));
                }
            }
        }
        return moduleList.size() > 0 ? moduleList.toArray(new TargetModuleID[moduleList.size()]) : null;
    }

    /**
     * 
     * @param dConfigBeanVersionType 
     * @throws javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException 
     */
    public void setDConfigBeanVersion(DConfigBeanVersionType version) throws DConfigBeanVersionUnsupportedException {
    }

    /**
     * 
     * @param dConfigBeanVersionType 
     * @return 
     */
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType version) {
        return false;
    }

    /**
     * 
     */
    public void release() {
    }

    /**
     * 
     * @return 
     */
    public boolean isRedeploySupported() {
        return true;
    }

    /**
     * 
     * @return 
     */
    public java.util.Locale getCurrentLocale() {
        return null;
    }

    /**
     * 
     * @return 
     */
    public DConfigBeanVersionType getDConfigBeanVersion() {
        return null;
    }

    /**
     * 
     * @return 
     */
    public java.util.Locale getDefaultLocale() {
        return null;
    }

    /**
     * 
     * @return 
     */
    public java.util.Locale[] getSupportedLocales() {
        return null;
    }

    /**
     * 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public Target[] getTargets() throws IllegalStateException {
        InstanceProperties ip = getInstanceProperties();
        String serverUri = constructServerUri(ip.getProperty(GlassfishModule.HOSTNAME_ATTR),
                ip.getProperty(GlassfishModule.HTTPPORT_ATTR), null);
        Hk2Target target = new Hk2Target(serverUri);
        Hk2Target targets[] = {target};
        return targets;
    }

    /**
     * 
     * @return 
     */
    public String getUri() {
        return uri;
    }
    
    /**
     * 
     * @return 
     */
    public Hk2PluginProperties getProperties() {
        return pluginProperties;
    }
    
    /**
     * 
     * @return 
     */
    public InstanceProperties getInstanceProperties() {
        // !PW FIXME synchronization - using volatile for now, could do a little better
        if(instanceProperties == null) {
            instanceProperties = InstanceProperties.getInstanceProperties(getUri());
        }
        return instanceProperties;
    }
    
    /**
     * Get the GlassfishInstance associated with this deployment manager.
     *  
     * @return
     */
    public ServerInstance getServerInstance() {
        // !PW FIXME synchronization - using volatile for now, could do a little better
        if(serverInstance == null) {
            serverInstance = ServerUtilities.getServerInstance(uri);
            if(serverInstance == null) {
                String warning = "Common server instance not found for " + uri;
                Logger.getLogger("glassfish-javaee").log(Level.WARNING, warning);
                throw new IllegalStateException(warning);
            }
        }
        return serverInstance;
    }

    /**
     * Get a reference to the common support module for the server instance
     * associated with this deployment manager.
     * 
     * @return Reference to common server support impl.
     */
    public GlassfishModule getCommonServerSupport() {
        ServerInstance si = getServerInstance();
        return si.getBasicNode().getLookup().lookup(GlassfishModule.class);
    }
    
    /** 
     * Returns URI of GF (manager application).
     * 
     * @return URI without home and base specification
     */
    public String getPlainUri() {
        InstanceProperties ip = getInstanceProperties();
        return constructServerUri(ip.getProperty(GlassfishModule.HOSTNAME_ATTR),
                ip.getProperty(GlassfishModule.HTTPPORT_ATTR), "/__asadmin/");
    }
    
    /** 
     * Returns URI of hk2.
     * 
     * @return URI without home and base specification
     */
    public String getServerUri() {
        InstanceProperties ip = getInstanceProperties();
        return constructServerUri(ip.getProperty(GlassfishModule.HOSTNAME_ATTR),
                ip.getProperty(GlassfishModule.ADMINPORT_ATTR), null);
    }

    private final String constructServerUri(String host, String port, String path) {
        StringBuilder builder = new StringBuilder(128);
        builder.append("http://"); // NOI18N
        builder.append(host);
        builder.append(":"); // NOI18N
        builder.append(port);
        if(path != null && path.length() > 0) {
            builder.append(path);
        }
        return builder.toString();
    }

}
