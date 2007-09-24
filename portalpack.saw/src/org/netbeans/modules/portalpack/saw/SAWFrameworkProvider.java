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

package org.netbeans.modules.portalpack.saw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Vihang
 */
public class SAWFrameworkProvider extends WebFrameworkProvider {

    private SAWFrameworkWizardPanel1 SAWFrameworkWizardPanel1;
    
    /** Creates a new instance of WorkflowFrameworkProvider */
    public SAWFrameworkProvider() {
        //super("Workflow Framework", "Framework to add Workflow API Support");
        super(NbBundle.getBundle(SAWFrameworkProvider.class).getString("SAWFramework"), NbBundle.getBundle(SAWFrameworkProvider.class).getString("SAWFramework_desc"));
        
    }

    public Set extend(WebModule wm) {

        try {

            String selectedValue = SAWFrameworkWizardPanel1.getSelectedValueFromVisualPanel();
            if (selectedValue.equals("JCAPS")) {
                createPropertyFiles(wm,selectedValue);
                Library bpLibrary = LibraryManager.getDefault().getLibrary("saw"); //NOI18N
                if (bpLibrary != null) {
                    FileObject[] sources = wm.getJavaSources();
                    for (int i = 0; i < sources.length; i++) {
                        ProjectClassPathModifier.addLibraries(new Library[]{bpLibrary}, sources[i], ClassPath.COMPILE);
                    }
                } else {
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInWebModule(WebModule webModule) {

        try {
            FileObject fileObject = webModule.getWebInf().getFileObject("ImplementationType", "properties");
        } catch (Exception e) {
            return false;
            // e.printStackTrace();
        }
        return true;
    }

    public File[] getConfigurationFiles(WebModule arg0) {
        return null;
    }

    public FrameworkConfigurationPanel getConfigurationPanel(WebModule webModule) {
        if (webModule != null && isInWebModule(webModule)) {
            return null;
        }
        SAWFrameworkWizardPanel1 = new SAWFrameworkWizardPanel1();

        return SAWFrameworkWizardPanel1;
        //  return null;
    }

    private void createPropertyFiles(WebModule wm,String selectedValue) {
        try {
            
             final  FileObject documentBase = wm.getDocumentBase();
             Project project = FileOwnerQuery.getOwner(documentBase);
             Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] sourceGroup  = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for(int j=0;j<sourceGroup.length;j++) {                    
                    if(sourceGroup[j].getRootFolder().getParent().getName().equals("src")) {
                    FileObject[] fileObjectArray = sourceGroup[j].getRootFolder().getParent().getChildren();
                    
            for(int i=0; i<fileObjectArray.length;i++) {
                
                if(fileObjectArray[i].getName().equals("java")) {
                    org.openide.filesystems.FileObject fObject10 = fileObjectArray[i].createData("ImplementationType", "properties");
                    org.openide.filesystems.FileLock fLock1 = fObject10.lock();
                    java.io.OutputStream ostream = fObject10.getOutputStream(fLock1);
                    ostream.write(("ImplementationType=" + selectedValue).getBytes());
                    ostream.flush();
                    ostream.close();
                    fLock1.releaseLock();
                    org.openide.filesystems.FileObject fObject11 = fileObjectArray[i].createData("WorkflowConfig", "properties");
                    org.openide.filesystems.FileLock fLock2 = fObject11.lock();
                    java.io.OutputStream ostream1 = fObject11.getOutputStream(fLock2);
                    ostream1.write(("businessProcess=" + "com.sun.saw.impls.jcaps.JCAPSWorkflow\n").getBytes());
                    ostream1.write(("logFileLocation=" + "Specify location of log file").getBytes());
                    ostream1.flush();
                    ostream1.close();
                    fLock2.releaseLock();
                    org.openide.filesystems.FileObject fObject12 = fileObjectArray[i].createData("JCAPSWorkflowConfig", "properties");
                    org.openide.filesystems.FileLock fLock3 = fObject12.lock();
                    java.io.OutputStream ostream2 = fObject12.getOutputStream(fLock3);
                    ostream2.write(("appserverhost=" + "host where jcaps is installed e.g. test.domain.com\n").getBytes());
                    ostream2.write(("appserverport=" + "port where jcaps workflow service is available e.g. 8080\n").getBytes());
                    ostream2.write(("appserverusername=" + "admin user name of jcaps app server e.g. admin\n").getBytes());
                    ostream2.write(("appserverpassword=" + "password of jcaps app server e.g. abc \n").getBytes());
                    ostream2.write(("contextfactory=" + "context factory of workflow service e.g. com.sun.jndi.cosnaming.CNCtxFactory \n").getBytes());
                    ostream2.write(("serviceJndi=" + "jndi context of workflow service provided by jcaps e.g. WorkflowService").getBytes());
                    ostream2.flush();
                    ostream2.close();
                    fLock3.releaseLock();             
                }
            }
                    }
            }
                 
            
            
            String content = readResource (Repository.getDefault().getDefaultFileSystem().findResource("velocity/templates/SAW").getInputStream (), "UTF-8"); //NOI18N
            FileObject target = FileUtil.createData(wm.getWebInf(), "SAW.tld");//NOI18N
            createFile(target, content, "UTF-8"); //NOI18N 
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
   private static String readResource(InputStream is, String encoding) throws IOException {
       // read the config from resource first
       StringBuffer sb = new StringBuffer();
       String lineSep = System.getProperty("line.separator");//NOI18N
       BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
       String line = br.readLine();
       while (line != null) {
           sb.append(line);
           sb.append(lineSep);
           line = br.readLine();
       }
       br.close();
       return sb.toString();
   }


private void createFile(FileObject target, String content, String encoding) throws IOException{                      
            FileLock lock = target.lock();
           try {
               BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
               bw.write(content);
               bw.close();

           }
           finally {
               lock.releaseLock();
           }
       } 
}
