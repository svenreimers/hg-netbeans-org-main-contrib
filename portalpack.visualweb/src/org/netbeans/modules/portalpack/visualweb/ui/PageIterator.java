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
package org.netbeans.modules.portalpack.visualweb.ui;

// XXX org.netbeans.modules.visualweb.project.jsf is not accessible under NetBeans 6.0; needs friend-package
// Use local copy now
// import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
// import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectConstants;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.api.webmodule.WebFrameworks;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeEventNotificationHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletInfoType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletXMLFactory;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SupportsType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import org.openide.WizardDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.*;
import org.openide.util.NbBundle;

/** A template wizard iterator for new JSF page action
 *
 * @author Po-Ting Wu
 */
public class PageIterator implements TemplateWizard.Iterator {

    private static final long serialVersionUID = 1L;
    private int index;
    private transient WizardDescriptor.Panel[] panels;
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

    public void initialize(TemplateWizard wizard) {
        index = 0;
        // obtaining target folder
        Project project = Templates.getProject(wizard);
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        PagebeanPackagePanel packagePanel = new PagebeanPackagePanel(project);
        WizardDescriptor.Panel javaPanel = new SimpleTargetChooserPanel(project, sourceGroups, packagePanel, false);
        String templateType = Templates.getTemplate(wizard).getExt();
        panels = new WizardDescriptor.Panel[]{javaPanel};

        // Creating steps.
        Object prop = wizard.getProperty("WizardPanel_contentData"); // NOI18N

        String[] beforeSteps = null;
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        String[] steps = createSteps(beforeSteps, panels);

        for (int i = 0; i < panels.length; i++) {
            JComponent jc = (JComponent) panels[i].getComponent();
            if (steps[i] == null) {
                steps[i] = jc.getName();
            }
            jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N

            jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N

        }

        // no support for non-web project
        if (!JsfProjectUtils.isWebProject(project)) {
            return;
        }
        
        //decide if to create a new portlet 
        if(JsfProjectUtils.isJsfProject(project))
            packagePanel.disableNewPortletCreateOption();
        else{
            WebModule webModule = JsfProjectUtils.getWebModule(project);
            FileObject webInf = webModule.getWebInf();
            if(webInf != null)
            {
                FileObject portletXmlOb = webInf.getFileObject("portlet", "xml");
                if(portletXmlOb != null)
                {
                    File portletXml = FileUtil.toFile(portletXmlOb);
                    PortletApp portletApp = getPortletApp(portletXml);
                    if(isJSFPortletEntryPresent(portletApp))
                        packagePanel.disableNewPortletCreateOption();
                    else
                        packagePanel.enableNewPortletCreateOption();

                }
            }
        }

        // Always start with the document root or under
        FileObject docRoot = JsfProjectUtils.getDocumentRoot(project);
        FileObject javaDir = JsfProjectUtils.getPageBeanRoot(project);
        FileObject jspDir = Templates.getTargetFolder(wizard);
        String relativePath = (jspDir == null) ? null : FileUtil.getRelativePath(docRoot, jspDir);
        if ((relativePath == null) || (relativePath.indexOf("WEB-INF") != -1)) {
            Templates.setTargetFolder(wizard, docRoot);
            jspDir = docRoot;
        } else if (relativePath.length() > 0) {
            javaDir = javaDir.getFileObject(relativePath);
        }

        // Find a free page name
        String ext = Templates.getTemplate(wizard).getExt();
        String prefix = "jsp".equals(ext) ? "PortletPage" : "PortletFragment"; // NOI18N

        for (int pageIndex = 1;; pageIndex++) {
            String name = prefix + pageIndex;
            if ((jspDir.getFileObject(name + "." + ext) == null) && ((javaDir == null) || (javaDir.getFileObject(name + ".java") == null))) { // NOI18N

                wizard.setTargetName(name);
                return;
            }
        }        
        
    }

    public void uninitialize(TemplateWizard wizard) {
        panels = null;
    }

    public Set instantiate(TemplateWizard wizard) throws IOException/*, IllegalStateException*/ {
        // Here is the default plain behavior. Simply takes the selected
        // template (you need to have included the standard second panel
        // in createPanels(), or at least set the properties pageName and
        // targetFolder correctly), instantiates it in the provided
        // position, and returns the result.
        // More advanced wizards can create multiple objects from template
        // (return them all in the result of this method), populate file
        // contents on the fly, etc.
        FileObject dir = Templates.getTargetFolder(wizard);
        DataFolder df = DataFolder.findFolder(dir);
        FileObject template = Templates.getTemplate(wizard);
        Project project = Templates.getProject(wizard);
        DataObject dTemplate = DataObject.find(template);
        String targetName = Templates.getTargetName(wizard);
        boolean createPortlet = (Boolean)wizard.getProperty("create_portlet");
        
        PortletContext context = (PortletContext)wizard.getProperty("context");
        Set result = Collections.EMPTY_SET;

        // Visual Web framework is not initialized
        if (!JsfProjectUtils.isJsfProject(project)) {
            List<WebFrameworkProvider> frameworks = WebFrameworks.getFrameworks();
            for (WebFrameworkProvider framework : frameworks) {
                if (JsfProjectUtils.isJsfFramework(framework)) {
                    WebModule webModule = JsfProjectUtils.getWebModule(project);

                    String beanPackage = (String) wizard.getProperty(JsfProjectConstants.PROP_JSF_PAGEBEAN_PACKAGE);
                    if (beanPackage == null) {
                        beanPackage = JsfProjectUtils.deriveSafeName(project.getProjectDirectory().getName());
                    }
                    JsfProjectUtils.createProjectProperty(project, JsfProjectConstants.PROP_JSF_PAGEBEAN_PACKAGE, beanPackage);
                    
                    // XXX JsfProjectConstants.NO_START_PAGE will be handled by visualweb under NetBeans 6.1.
                    // Note: VisualWeb will skip the start page "/" since it's already existed.
                    // JsfProjectUtils.createProjectProperty(project, JsfProjectConstants.PROP_START_PAGE, JsfProjectConstants.NO_START_PAGE);
                    JsfProjectUtils.createProjectProperty(project, JsfProjectConstants.PROP_START_PAGE, "/");
                    
                   // addPortletXMLEntry(webModule, project, dir, context, targetName, template);

                    // Add OpenPortal JSF Portlet Bridge Support library
                    ClassPath cp = ClassPath.getClassPath(webModule.getDocumentBase(), ClassPath.COMPILE);
                    if (cp.findResource("com/sun/faces/portlet/FacesPortlet.class") == null) { //NOI18N

                        Library libBridge = LibraryManager.getDefault().getLibrary("jsf-portlet-runtime-1.2"); // NOI18N

                        if (libBridge != null) {
                            try {
                                JsfProjectUtils.addLibraryReferences(project, new Library[]{libBridge                                        }, ClassPath.EXECUTE);
                            } catch (IOException ioExceptoin) {
                            }
                        }
                    }

                    WebModuleExtender extender = framework.createWebModuleExtender(webModule, ExtenderController.create());
                    result = extender.extend(webModule);

                    if (dir.getFileObject(targetName + "." + template.getExt()) != null) { // NOI18N

                        return result;
                    }
                }
            }
        }
        
        WebModule webModule = JsfProjectUtils.getWebModule(project);
        if(webModule != null) {
            if(createPortlet)
                addPortletXMLEntry(webModule, project, dir, context, targetName, template);
        }

        DataObject obj;
        try {
            if (targetName == null) {
                // Default name.
                obj = dTemplate.createFromTemplate(df);
            } else {
                Map<String, String> templateParameters = new HashMap<String, String>();
                templateParameters.put("j2eePlatformVersion", JsfProjectUtils.getJ2eePlatformVersion(project)); //NOI18N

                templateParameters.put("sourceLevel", JsfProjectUtils.getSourceLevel(project)); //NOI18N

                if ("jsp".equals(template.getExt())) { // NOI18N

                    FileObject webDocbase = JsfProjectUtils.getDocumentRoot(project);
                    String folder;
                    if (dir == webDocbase) {
                        folder = "";
                    } else {
                        folder = FileUtil.getRelativePath(webDocbase, dir);
                        if (folder == null) {
                            folder = "";
                        } else {
                            folder = folder.replace('/', '$') + "$";
                        }
                    }
                    templateParameters.put("folder", folder); //NOI18N

                }

                obj = dTemplate.createFromTemplate(df, targetName, templateParameters);
            }
        } catch (IOException jsfe) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(PageIterator.class, "TXT_CantCreatePage", df.getName())));
            return result;
        }

        if (result == Collections.EMPTY_SET) {
            result = Collections.singleton(obj);
        } else {
            result.add(obj);
        }

        // Open the new document
        OpenCookie open = (OpenCookie) obj.getCookie(OpenCookie.class);
        if (open != null) {
            open.open();
        }
        return result;
    }

    private void addPortletXMLEntry(WebModule webModule, Project project, FileObject jspFolder, PortletContext context, String pageName, FileObject template) {

        // Create portlet.xml if not exist
        File filePortlet = new File(FileUtil.toFile(webModule.getWebInf()), "portlet.xml"); // NOI18N
        //create messages.properties if doesn't exis
        FileObject sourceRoot = JsfProjectUtils.getSourceRoot(project);
        if (sourceRoot != null) {
            try {
                FileObject mObj = sourceRoot.getFileObject("messages.properties");
                if (mObj == null) {
                    FileObject data = sourceRoot.createData("messages", "properties");

                    if (data == null) {
                        logger.log(Level.WARNING, "messages.properties could not be created");
                    }
                }
            } catch (IOException ex) {
                //ignore any error
            }
        }

        String jsfPortletFolderRelativePath = JsfProjectUtils.getRelativePathForJsfPortlet(webModule.getDocumentBase(), jspFolder);

        if (!filePortlet.exists()) {

            context.setPortletVersion(NetbeanConstants.PORTLET_1_0);
            context.setPortletClass(JsfProjectConstants.JSF_PORTLET); //NOI18N
            WebDescriptorGenerator wGen = new WebDescriptorGenerator();
            try {
                wGen.createPortletXml(FileUtil.toFile(webModule.getWebInf()).getAbsolutePath(), context, new HashMap());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Unable to create portlet.xml", ex);
            }
        }

        {
            //If portlet.xml exists, may be created by PortletSupport framework.
            //Check if a JSF portlet entry is there in portlet.xml.
            //If yes, then do nothing in portlet.xml
            //If no then add an entry for JSF portlet
            PortletApp portletApp = getPortletApp(filePortlet);
            if (portletApp != null) {
                PortletType portletType = portletApp.newPortletType();
                portletType.addDescription(context.getPortletDescription());
                portletType.setPortletName(context.getPortletName());
                portletType.addDisplayName(context.getPortletDisplayName());
                portletType.setPortletClass(JsfProjectConstants.JSF_PORTLET); //NOI18N
                InitParamType initParam = portletType.newInitParamType();
                initParam.setDescription(new String[]{"Portlet Init View Page"});
                initParam.setName("com.sun.faces.portlet.INIT_VIEW"); //NOI18N
                initParam.setValue(jsfPortletFolderRelativePath + pageName + "." + template.getExt()); //NOI18N
                portletType.addInitParam(initParam);
                portletType.setExpirationCache(0);

                SupportsType support = portletType.newSupportsType();
                support.setMimeType("text/html"); //NOI18N
                support.addPortletMode("VIEW"); //NOI18N
                portletType.addSupports(support);
                portletType.setSupportedLocale(new String[]{"en"}); //NOI18N
                PortletInfoType portletInfo = portletType.newPortletInfoType();
                portletInfo.setTitle(context.getPortletTitle());
                portletInfo.setShortTitle(context.getPortletShortTitle());

                portletType.setPortletInfo(portletInfo);
                portletType.setResourceBundle("messages"); //NOI18N
                
                portletApp.addPortlet(portletType);
                //add VisualJSFPortlet page as the first portlet entry in portlet.xml
                /*PortletType[] portletTypes = portletApp.getPortlet();
                if (portletTypes.length == 0) {
                    portletApp.addPortlet(portletType);
                } else {

                    PortletType firstPortlet = portletApp.getPortlet(0);
                    portletApp.setPortlet(0, portletType);
                    portletApp.addPortlet(firstPortlet);
                }*/

                savePortletXML(portletApp, filePortlet);

                //fire add portlet event
                context.setPortletClass(JsfProjectConstants.JSF_PORTLET);
                if (webModule.getWebInf() != null) {
                    String webInfPath = FileUtil.toFile(webModule.getWebInf()).getAbsolutePath();
                    PortletXMLChangeEventNotificationHelper.firePortletAddEvent(context, new AppContext(), webInfPath);
                }
            }
        }
    }

    private boolean isJSFPortletEntryPresent(PortletApp portletApp) {
        if (portletApp == null) {
            return false;
        }
        PortletType[] portletTypes = portletApp.getPortlet();
        for (PortletType portletType : portletTypes) {
            if (portletType.getPortletClass().equals(JsfProjectConstants.JSF_PORTLET)) { //NOI18N

                return true;
            }
        }
        return false;
    }

    private PortletApp getPortletApp(File portletXml) {
        try {
            if (!portletXml.exists()) {
                return null;
            }
            PortletApp portletApp = PortletXMLFactory.createGraph(portletXml);
            if (portletApp == null) {
                return null;
            }
            return portletApp;

        } catch (Exception ex) {
            return null;
        }
    }

    private void savePortletXML(PortletApp portletApp, File portletXML) {
        try {
            FileObject fileObject = FileUtil.toFileObject(portletXML);
            FileLock lock = fileObject.lock();
            OutputStream out = fileObject.getOutputStream(lock);

            portletApp.write(out);
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
            }

            lock.releaseLock();

        } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(PageIterator.class, "TXT_CantUpdatePortletXML")));
        }

    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public String name() {
        return NbBundle.getMessage(PageIterator.class, "TITLE_x_of_y", new Integer(index + 1), new Integer(panels.length));
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }

    private String[] createSteps(String[] before, WizardDescriptor.Panel[] panels) {
        int diff = 0;
        if (before == null) {
            before = new String[0];
        } else if (before.length > 0) {
            diff = ("...".equals(before[before.length - 1])) ? 1 : 0; // NOI18N

        }
        String[] res = new String[(before.length - diff) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (before.length - diff)) {
                res[i] = before[i];
            } else {
                res[i] = panels[i - before.length + diff].getComponent().getName();
            }
        }
        return res;
    }
}
