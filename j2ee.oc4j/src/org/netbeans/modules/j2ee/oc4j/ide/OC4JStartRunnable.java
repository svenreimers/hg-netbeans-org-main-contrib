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

package org.netbeans.modules.j2ee.oc4j.ide;

import java.io.File;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.swing.JOptionPane;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.openide.ErrorManager;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * @author Michal Mocnak
 */
class OC4JStartRunnable implements Runnable {
    
    static final String SCRIPT_UNIX = "oc4j";
    static final String SCRIPT_WIN = "oc4j.cmd";
    
    private OC4JDeploymentManager dm;
    private String instanceName;
    private OC4JStartServer startServer;
    private InstanceProperties ip;
    
    /**
     * The amount of time in milliseconds during which the server should
     * start
     */
    private static final int TIMEOUT = 120000;
    
    /**
     * The amount of time in milliseconds that we should wait between checks
     */
    private static final int DELAY = 1000;
    
    public OC4JStartRunnable(OC4JDeploymentManager dm, OC4JStartServer startServer) {
        this.dm = dm;
        this.ip = dm.getProperties().getInstanceProperties();
        this.instanceName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        this.startServer = startServer;
    }
    
    public void run() {
        // Save the current time so that we can deduct that the startup
        // Failed due to timeout
        long start = System.currentTimeMillis();
               
        Process serverProcess = createProcess();
        
        if (serverProcess == null) {
            return;
        }
        
        fireStartProgressEvent(StateType.RUNNING, createProgressMessage("MSG_START_SERVER_IN_PROGRESS"));
        
        // create a logger to the server's output stream so that a user
        // can observe the progress
        OC4JLogger.getInstance(dm.getUri()).readInputStreams(new InputStream[] {
            serverProcess.getInputStream(), serverProcess.getErrorStream()});
        
        // Waiting for server to start
        while (System.currentTimeMillis() - start < TIMEOUT) {
            // Send the 'completed' event and return when the server is running
            if (startServer.isRunning()) {
                fireStartProgressEvent(StateType.COMPLETED, createProgressMessage("MSG_SERVER_STARTED")); // NOI18N
                return;
            }
            
            // Sleep for a little so that we do not make our checks too
            // Often
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {}
        }
        
        // If the server did not start in the designated time limits
        // We consider the startup as failed and warn the user
        fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED"));
    }
    
    private String[] createEnvironment() {
        StringBuilder javaOpts = new StringBuilder();
        List<String> envp = new ArrayList<String>(3);
        String rootDir = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        
        if(startServer.getMode() == OC4JStartServer.MODE.DEBUG) {
            javaOpts.append(" -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address="). // NOI18N
                    append(dm.getProperties().getDebugPort()).
                    append(",server=y,suspend=n"); // NOI18N
        }
        
        envp.add("ORACLE_HOME=" + rootDir); // NOI18N
        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        envp.add("OC4J_JVM_ARGS=" + javaOpts); // NOI18N
        envp.add("VERBOSE=on"); // NOI18N
        
        return (String[]) envp.toArray(new String[envp.size()]);
    }
    
    private NbProcessDescriptor createProcessDescriptor() {
        final String serverLocation = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        final String startScript = serverLocation + File.separator + "bin" + File.separator +
                (Utilities.isWindows() ? SCRIPT_WIN : SCRIPT_UNIX); //NOI18N
        OC4JDebug.log(getClass().getName(), "serverLocation: " + serverLocation + ", startScript: " + startScript);
        
        if (!new File(startScript).exists()){
            OC4JDebug.log(getClass().getName(), "startScript " + startScript + " doesn't exist");
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED_FNF")); //NOI18N
            return null;
        }
        
        OC4JDebug.log(getClass().getName(), "EXEC: " + startScript + " -start ");
        return new NbProcessDescriptor(startScript, "-start"); //NOI18N
    }
    
    private Process createProcess() {
        NbProcessDescriptor pd = createProcessDescriptor();
        
        if (pd == null)
            return null;
        
        try {
            return pd.exec(null, createEnvironment(), true, new File(ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME)));
        } catch (java.io.IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED_PD"));
            return null;
        }
    }
    
    private String createProgressMessage(final String resName) {
        return createProgressMessage(resName, null);
    }
    
    private String createProgressMessage(final String resName, final String param) {
        return NbBundle.getMessage(OC4JStartRunnable.class, resName, instanceName, param);
    }
    
    private void fireStartProgressEvent(StateType stateType, String msg) {
        startServer.fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, stateType, msg));
    }
}