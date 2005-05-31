/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.awt.Component;
import javax.swing.event.ChangeListener;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.java.j2seproject.J2SEProject;
import org.netbeans.modules.java.j2seproject.SourceRoots;
import org.netbeans.spi.project.ui.templates.support.Templates;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;


/** Wizard iterator to create XTest testing infrastructure in J2SE project. It
 * was inspired by JavaWizardIterator.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class XTestWizardIterator implements TemplateWizard.Iterator {
    private static final long serialVersionUID = -1987345873459L;

    /** Array of panels that form the Wizard.
     */
    private transient WizardDescriptor.Panel[] panels;

    /**
     * Names for panels used in the Wizard.
     */
    private static String[] panelNames;
    
    /** Index of the current panel. Panels are numbered from 0 to PANEL_COUNT - 1.
     */
    private transient int panelIndex = 0;

    /**
     * Singleton instance of XTestWizardIterator, should it be ever needed.
     * 
     */
    private static XTestWizardIterator instance;
    
    /**
     * Holds a reference to the instance of TemplateWizard we are communicating with.
     */
    private transient TemplateWizard wizardInstance;

    public XTestWizardIterator() {
    }
    
    /**
     * Returns XTestWizardIterator singleton instance. This method is used
     * for constructing the instance from filesystem.attributes.
     * 
     */
    public static synchronized XTestWizardIterator singleton() {
        if (instance == null) {
            instance = new XTestWizardIterator();
        }
        return instance;
    }
    // ========================= TemplateWizard.Iterator ============================

    /** Instantiates the template using informations provided by the wizard.
     *
     * @param wiz the wizard
     * @return set of data objects that has been created (should contain at least one
     * @exception IOException if the instantiation fails
    */
    public java.util.Set instantiate(TemplateWizard wiz) throws IOException, IllegalArgumentException {
        HashSet createdObjects = new HashSet(5);
        Project project = Templates.getProject(wiz);
        FileObject projectDirFO = Templates.getProject(wiz).getProjectDirectory();
        FileObject templateFolderFO = wiz.getTemplatesFolder().getPrimaryFile();

        Map map = getReplaceMap(project);
        FileObject testFO = FileUtil.createFolder(projectDirFO, "test");  // NOI18N
        FileObject buildFO = templateFolderFO.getFileObject("TestingTools/build.xml");  // NOI18N
        //DataObject buildDO = DataObject.find(buildFO.copy(testFO, "build", "xml"));  // NOI18N
        DataObject buildDO = DataObject.find(createFromTemplate(buildFO, testFO, "build.xml", map));  // NOI18N
        // or also works
        //DataObject obj = DataObject.find(buildFO).createFromTemplate(DataFolder.findFolder(testFO), "build");
        createdObjects.add(buildDO);
        FileObject buildUnitFO = templateFolderFO.getFileObject("TestingTools/build-unit.xml");  // NOI18N
        DataObject buildUnitDO = DataObject.find(createFromTemplate(buildUnitFO, testFO, "build-unit.xml", map));  // NOI18N
        createdObjects.add(buildUnitDO);
        FileObject buildFunctionalFO = templateFolderFO.getFileObject("TestingTools/build-functional.xml");  // NOI18N
        DataObject buildFunctionalDO = DataObject.find(createFromTemplate(buildFunctionalFO, testFO, "build-functional.xml", map));  // NOI18N
        createdObjects.add(buildFunctionalDO);
        FileObject cfgUnitFO = templateFolderFO.getFileObject("TestingTools/cfg-unit.xml");  // NOI18N
        DataObject cfgUnitDO = DataObject.find(cfgUnitFO.copy(testFO, "cfg-unit", "xml"));  // NOI18N
        createdObjects.add(cfgUnitDO);
        FileObject cfgFunctionalFO = templateFolderFO.getFileObject("TestingTools/cfg-functional.xml");  // NOI18N
        DataObject cfgFunctionalDO = DataObject.find(cfgFunctionalFO.copy(testFO, "cfg-functional", "xml"));  // NOI18N
        createdObjects.add(cfgFunctionalDO);
        
        addXTestTestRoots(project);
        return createdObjects;
    }
    
    public WizardDescriptor.Panel current() {
        return panels[panelIndex];
    }
    
    public String name() {
        return ""; // NOI18N
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public boolean hasPrevious() {
        return false;
    }
    
    public void nextPanel() {
        throw new NoSuchElementException();
    }
    
    public void previousPanel() {
        throw new NoSuchElementException();
    }
    
    /** Add a listener to changes of the current panel.
     * The listener is notified when the possibility to move forward/backward changes.
     * @param l the listener to add
    */
    public void addChangeListener(ChangeListener l) {
    }
    
    /** Remove a listener to changes of the current panel.
     * @param l the listener to remove
    */
    public void removeChangeListener(ChangeListener l) {
    }

    public void initialize(TemplateWizard wizard) {
        this.wizardInstance = wizard;
	if (panels == null) {
            WizardDescriptor.Panel panel = new XTestWizardPanel(Templates.getProject(wizard));
            Component panelComponent = panel.getComponent();
            panelNames = new String[]{
                NbBundle.getBundle("org.netbeans.modules.project.ui.Bundle").  // NOI18N
                         getString("LBL_TemplateChooserPanelGUI_Name"),         // NOI18N
                panelComponent.getName()
            };
            if (panelComponent instanceof javax.swing.JComponent) {
                ((javax.swing.JComponent)panelComponent).putClientProperty(
                    "WizardPanel_contentData", panelNames); // NOI18N
            }
            panels = new WizardDescriptor.Panel[] {
                panel
            };
	}

    }
    
    public void uninitialize(TemplateWizard wiz) {
	panels = null;
        wizardInstance = null;
    }

    // ========================= IMPLEMENTATION ============================
    
    private Object readResolve() {
        return singleton();
    }
    
    /** Adds test/unit/src ("Unit Test Packages") and test/functional/src
     * ("Functional Test Packages") test roots to specified J2SE project.
     * Directories are created if needed.
     */
    private static void addXTestTestRoots(Project project) throws IOException, IllegalArgumentException {
        SourceRoots testRoots = ((J2SEProject)project).getTestSourceRoots();
        URL[] oldRoots = testRoots.getRootURLs();
        FileObject[] oldRootsFO = testRoots.getRoots();
        String[] oldNames = testRoots.getRootNames();
        String[] oldProperties = testRoots.getRootProperties();
        ArrayList/*<URL>*/ newRoots = new ArrayList();
        ArrayList/*<String>*/ newRootsLabels = new ArrayList();
        for(int i=0;i<oldRoots.length;i++) {
            // if old test root is empty, don't add it to new ones
            boolean notEmpty = false;
            if(oldRootsFO[i].getChildren().length != 0) {
                Enumeration children = oldRootsFO[i].getChildren(true);
                while(children.hasMoreElements()) {
                    if(((FileObject)children.nextElement()).hasExt("java")) { // NOI18N
                        notEmpty = true;
                        break;
                    }
                }
            }
            if(notEmpty) {
                newRoots.add(oldRoots[i]);
                newRootsLabels.add(testRoots.getRootDisplayName(oldNames[i], oldProperties[i]));
            }
        }
        newRoots.add(FileUtil.createFolder(project.getProjectDirectory(), "test/unit/src").getURL()); // NOI18N
        newRootsLabels.add("Unit Test Packages"); //NOI18N
        newRoots.add(FileUtil.createFolder(project.getProjectDirectory(), "test/functional/src").getURL()); // NOI18N
        newRootsLabels.add("Functional Test Packages"); //NOI18N
        testRoots.putRoots((URL[])newRoots.toArray(new URL[0]), (String[])newRootsLabels.toArray(new String[0]));
        ProjectManager.getDefault().saveProject(project);
    }
    
    private Map replaceMap;
    
    private Map getReplaceMap(Project project) throws IOException {
        if(replaceMap == null) {
            replaceMap = new HashMap();
            File xtestHome = InstalledFileLocator.getDefault().
                    locate("xtest-distribution", "org.netbeans.modules.xtest", false);  // NOI18N
            replaceMap.put("__XTEST_HOME__", xtestHome.getCanonicalPath()); // NOI18N
            String projectName = ProjectUtils.getInformation(project).getName();
            replaceMap.put("__XTEST_MODULE__", projectName); // NOI18N
            File netbeansDestDir = InstalledFileLocator.getDefault().
                    locate("core/core.jar", null, false);  // NOI18N
            netbeansDestDir = new File(netbeansDestDir, "../../.."); // NOI18N
            replaceMap.put("__NETBEANS_DEST_DIR__", netbeansDestDir.getCanonicalPath()); // NOI18N
            File jemmyJar = InstalledFileLocator.getDefault().
                    locate("modules/ext/jemmy.jar", "org.netbeans.modules.jemmy", false);  // NOI18N
            if(jemmyJar != null) {
                replaceMap.put("__JEMMY_JAR__", jemmyJar.getCanonicalPath()); // NOI18N
            } else {
                replaceMap.put("__JEMMY_JAR__", "jemmy.jar"); // NOI18N
            }
        }
        return replaceMap;
    }
    
    /** Creates a new file object with given name in destination directory. 
     * It copies input file object and replaces keys according to the map. */
    private static FileObject createFromTemplate(FileObject inputFO, FileObject destDirFO, String name, Map map) throws IOException {
        FileObject outputFO = FileUtil.createData(destDirFO, name);
        InputStream is = inputFO.getInputStream();
        Reader reader = new InputStreamReader(is);
        BufferedReader r = new BufferedReader(reader);
        try {
            FileLock lock = outputFO.lock ();
            try {
                OutputStream os = outputFO.getOutputStream(lock);
                OutputStreamWriter w = new OutputStreamWriter(os);
                String line;
                while ((line = r.readLine ()) != null) {
                    Iterator iter = map.keySet().iterator();
                    Object key;
                    while(iter.hasNext()) {
                        key = iter.next();
                        // need to handle backslahes otherwise they are swallowed
                        line = line.replaceAll(key.toString(), map.get(key).toString().replaceAll("\\\\", "\\\\\\\\"));
                    }
                    w.write(line+'\n');
                }
                w.close ();
            } finally {
                lock.releaseLock ();
            }
        } finally {
            r.close ();
        }
        return outputFO;
    }

    private void notifyError(String msg) {
        this.wizardInstance.putProperty("WizardPanel_errorMessage", msg); //NOI18N
        IllegalStateException ex = new IllegalStateException(msg);
        ErrorManager.getDefault().annotate(ex, ErrorManager.USER, null, msg, null, null);
        throw ex;
    }
}
