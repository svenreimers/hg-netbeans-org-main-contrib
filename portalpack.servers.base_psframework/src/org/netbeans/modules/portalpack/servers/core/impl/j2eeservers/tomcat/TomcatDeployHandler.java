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
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author root
 */
public class TomcatDeployHandler implements ServerDeployHandler {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    private FileObject taskFile;
    private String DEPLOY_XML = ".liferaytask.xml";
    private static String TOMCAT_CONF_DIR = "conf" + File.separator + "Catalina" + File.separator + "localhost";

    /**
     * Creates a new instance of TomcatDeployHandler
     */
    public TomcatDeployHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.taskFile = taskFile;
    //initBuildScript();
    }

    private void initBuildScript() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("org/netbeans/modules/portalpack/" +
            "servers/liferay/antscripts/task.xml");
        OutputStream output = null;


        File tempFile = new File(System.getProperty("user.home"), ".liferaytask.xml");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        File buildFile = new File(System.getProperty("user.home"), DEPLOY_XML);
        buildFile.deleteOnExit();

        try {
            output = new FileOutputStream(buildFile);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "error", ex);
        }

        try {
            FileUtil.copy(input, output);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "error", ex);
        }

        if (output != null) {
            try {
                output.flush();
                output.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "error", ex);
            }

        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "error", ex);
            }
        }

        taskFile = FileUtil.toFileObject(buildFile);

    }

    private void deployOnTomcat(String warFile) throws IOException {

        File file = new File(warFile);
        String fileName = file.getName();

        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            fileName = fileName.substring(0, index);
        }

        String context = fileName;

        Properties props = new Properties();
        props.setProperty("war", warFile);
        props.setProperty("path", "/" + context);
        props.setProperty("host", psconfig.getHost());
        props.setProperty("port", psconfig.getPort());
        props.setProperty("username", psconfig.getProperty(TomcatConstant.MANAGER_USER));
        props.setProperty("password", psconfig.getProperty(TomcatConstant.MANAGER_PASSWORD));
        props.setProperty("taskjar", psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator + "server" + File.separator + "lib" + File.separator + "catalina-ant.jar");

        ActionUtils.runTarget(taskFile, new String[]{"deploy"}, props);

    }

    private void undeployOnTomcat(String appName) throws IOException {


        Properties props = new Properties();

        props.setProperty("path", "/" + appName);

        props.setProperty("username", psconfig.getProperty(TomcatConstant.MANAGER_USER));
        props.setProperty("password", psconfig.getProperty(TomcatConstant.MANAGER_PASSWORD));
        props.setProperty("host", psconfig.getHost());
        props.setProperty("port", psconfig.getPort());
        props.setProperty("taskjar", psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator + "server" + File.separator + "lib" + File.separator + "catalina-ant.jar");


        ActionUtils.runTarget(taskFile, new String[]{"undeploy"}, props);

    }

    public boolean deploy(String warFile) throws Exception {
        try {
            deployOnTomcat(warFile);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            return false;
        }
        return true;
    }

    public boolean _undeployFromTomcat(String appName) {
        File appDepDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME) 
                            + File.separator + "webapps" + File.separator + appName);
        
        File warFile = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME) 
                            + File.separator + "webapps" + File.separator + appName
                            + ".war");
        if(warFile.exists())
            warFile.delete();
        
        if (appDepDir.exists()) {
            undeployDir(appDepDir);
            
        } 
        
        //Incase of exploded directory deployment
            
        File confDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME)
                                        + File.separator + TOMCAT_CONF_DIR);
        String contextXml = appName + ".xml";
            
        File contextXmlFile = new File(confDir,contextXml);
            
        if(contextXmlFile.exists())
            contextXmlFile.delete();
        
        try {

            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    protected void undeployDir(File dir) {

        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                undeployDir(file);
            } else {
                file.delete();
            }
            dir.delete();
        }
    }

    public boolean undeploy(String appName) throws Exception {
        try {
            //undeployOnTomcat(appName);
            return _undeployFromTomcat(appName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            return false;
        }
    }

    public boolean install() throws Exception {
        return true;
    }

    public boolean deploy(String deployDir, String contextName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
