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

package org.netbeans.modules.j2ee.oc4j.util;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.customizer.OC4JCustomizerSupport;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 * @author pblaha
 */
public class OC4JPluginProperties {
    
    public static final String PROPERTY_DISPLAY_NAME = InstanceProperties.DISPLAY_NAME_ATTR;
    public static final String PROPERTY_ADMIN_PORT = "adminPort"; //NOI18N
    public static final String PROPERTY_WEB_SITE = "webSite"; //NOI18N
    public static final String PROPERTY_OC4J_HOME = "oc4jHome"; //NOI18N
    public static final String PROPERTY_HOST = "host"; //NOI18N
    public static final String PROP_JAVA_PLATFORM = "java_platform"; //NOI18N
    public static final String PROP_JAVADOCS      = "javadocs";        // NOI18N
    public static final String PLAT_PROP_ANT_NAME = "platform.ant.name"; //NOI18N
    
    private InstanceProperties ip;
    private OC4JDeploymentManager dm;
    
    private static final int DEBUGPORT = 8787;
    
    public OC4JPluginProperties(OC4JDeploymentManager dm) {
        this.dm = dm;
        ip = InstanceProperties.getInstanceProperties(dm.getUri());
    }
    
    public String getOC4JHomeLocation() {
        return ip.getProperty(PROPERTY_OC4J_HOME);
    }
    
    public JavaPlatform getJavaPlatform() {
        String currentJvm = ip.getProperty(PROP_JAVA_PLATFORM);
        JavaPlatformManager jpm = JavaPlatformManager.getDefault();
        JavaPlatform[] installedPlatforms = jpm.getPlatforms(null, new Specification("J2SE", null)); // NOI18N
        for (int i = 0; i < installedPlatforms.length; i++) {
            String platformName = (String)installedPlatforms[i].getProperties().get(PLAT_PROP_ANT_NAME);
            if (platformName != null && platformName.equals(currentJvm)) {
                return installedPlatforms[i];
            }
        }
        // return default platform if none was set
        return jpm.getDefaultPlatform();
    }
    
    public InstanceProperties getInstanceProperties() {
        return ip;
    }
    
    public List<URL> getClasses() {
        List<URL> list = new ArrayList<URL>();
        File serverDir = new File(getOC4JHomeLocation());
        try{
            for(File file:new File(serverDir, "j2ee/home/applib").listFiles()) {
                if(FileUtil.isArchiveFile(file.toURI().toURL()))
                    list.add(OC4JPluginUtils.fileToUrl(file));
            }
            for(File file:new File(serverDir, "j2ee/home/lib").listFiles()) {
                if(FileUtil.isArchiveFile(file.toURI().toURL()))
                    list.add(OC4JPluginUtils.fileToUrl(file));
            }
            for(File file:new File(serverDir, "lib").listFiles()) {
                if(FileUtil.isArchiveFile(file.toURI().toURL()))
                    list.add(OC4JPluginUtils.fileToUrl(file));
            }
            for(File file:new File(serverDir, "webservices/lib").listFiles()) {
                if(FileUtil.isArchiveFile(file.toURI().toURL()))
                    list.add(OC4JPluginUtils.fileToUrl(file));
            }
            for(File file:new File(serverDir, "toplink/jlib").listFiles()) {
                if(FileUtil.isArchiveFile(file.toURI().toURL()))
                    list.add(OC4JPluginUtils.fileToUrl(file));
            }
        } catch(MalformedURLException ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        return list;
    }
    
    public List<URL> getJavadocs() {
        String path = ip.getProperty(PROP_JAVADOCS);
        if (path == null) {
            ArrayList<URL> list = new ArrayList<URL>();
            try {
                File j2eeDoc = InstalledFileLocator.getDefault().locate("docs/javaee5-doc-api.zip", null, false); // NOI18N
                if (j2eeDoc != null) {
                    list.add(OC4JPluginUtils.fileToUrl(j2eeDoc));
                }
            } catch (MalformedURLException e) {
                Logger.getLogger("global").log(Level.INFO, null, e);
            }
            return list;
        }
        return OC4JCustomizerSupport.tokenizePath(path);
    }
    
    public void setJavadocs(List<URL> path) {
        ip.setProperty(PROP_JAVADOCS, OC4JCustomizerSupport.buildPath(path));
        dm.getOC4JPlatform().notifyLibrariesChanged();
    }
    
    public int getDebugPort() {
        return DEBUGPORT;
    }
    
    public static boolean isRunning(String host, int port) {
        if(null == host)
            return false;
        
        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket socket = new Socket();
            socket.connect(isa, 1);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static boolean isRunning(String host, String port) {
        try {
            return isRunning(host, Integer.parseInt(port));
        } catch(NumberFormatException e) {
            if(OC4JDebug.isEnabled()) {
                OC4JDebug.log("org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties", "HOST: " + host);
                OC4JDebug.log("org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties", "PORT: " + port);
            }
            return false;
        }
    }
}