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

package org.netbeans.modules.cnd.make2netbeans.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.modules.cnd.makeproject.api.MakeArtifact;
import org.netbeans.modules.cnd.makeproject.api.configurations.Configuration;
import org.netbeans.modules.cnd.makeproject.api.configurations.Folder;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.makeproject.api.configurations.LibraryItem;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfigurationDescriptor;
import org.netbeans.modules.cnd.makeproject.api.configurations.VectorConfiguration;
import org.netbeans.modules.cnd.makeproject.api.remote.FilePathAdaptor;
import org.netbeans.modules.cnd.makeproject.api.wizards.IteratorExtension;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The class to create a project with existing makefile
 * @author Andrey Gubichev
 */
public class ProjectCreator {

    //default makefile name
    private static final String MAKEFILE_NAME = "Makefile";
    //project type
    private static final String TYPE = "org.netbeans.modules.cnd.makeproject";
    //configuration namespace
    private static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/make-project/1";
    private String workingDir;
    private String buildCommand;
    private String cleanCommand;
    private String output;
    private String makefileName;
    private List<File> sourceFiles;
    private List<String> requiredProjects;
    private String makefilePath;
    private String projectFolder;
    private String projectName;
    private String baseDir;
    private Folder rootFolder;

    /** Creates a new instance of ProjectCreator */
    public ProjectCreator() {
        makefileName = MAKEFILE_NAME;
        workingDir = null;
        cleanCommand = null;
        buildCommand = null;
        output = null;
        sourceFiles = null;
    }

    /**
     * initialize
     * @param newProjectFolder project folder
     * @param newWorkingDir working directory (for build and clean commands)
     * @param newMakefilePath path to existing makefile
     */
    public void init(String newProjectFolder, String newWorkingDir, String newMakefilePath) {
        projectFolder = newProjectFolder;
        workingDir = newWorkingDir;
        makefilePath = newMakefilePath;
    }

    /**
     *
     * @param com new build command
     */
    public void setBuildCommand(String com) {
        buildCommand = com;
    }

    /**
     *
     * @param com new clean command
     */
    public void setCleanCommand(String com) {
        cleanCommand = com;
    }

    /**
     *
     * @param out new output
     */
    public void setOutput(String out) {
        output = out;
    }

    /**
     *
     * @param p list of source files to be added to the project
     */
    public void setSourceFiles(List<File> p) {
        sourceFiles = p;
    }

    public void setRequiredProjects(List<String> p) {
        requiredProjects = p;
    }

    
    /**
     * Create project
     * @param name project name
     * @return the helper object permitting it to be further customized
     * @throws java.io.IOException see createProject(File, String, String, Configuration[], Iterator, Iterator)
     */
    public AntProjectHelper createProject(String name, String displayName, boolean runDiscovery) throws IOException {
        String baseDir = projectFolder + File.separator + name;
        File dirF = new File(projectFolder);
        if (dirF != null) {
            dirF = FileUtil.normalizeFile(dirF);
        }
        CodeAssistance ca = new CodeAssistance();
        Vector<String> includes = new Vector<String>(ca.getIncludes(makefilePath));
        String target = "Default";

        MakeConfiguration extConf = new MakeConfiguration(dirF.getPath(), target, MakeConfiguration.TYPE_MAKEFILE);
        String workingDirRel = IpeUtils.toRelativePath(dirF.getPath(), FilePathAdaptor.naturalize(workingDir));
        workingDirRel = FilePathAdaptor.normalize(workingDirRel);
        extConf.getMakefileConfiguration().getBuildCommandWorkingDir().setValue(workingDirRel);
        extConf.getMakefileConfiguration().getBuildCommand().setValue(buildCommand);
        extConf.getMakefileConfiguration().getCleanCommand().setValue(cleanCommand);
        extConf.getMakefileConfiguration().getOutput().setValue(output);
        
        if (requiredProjects != null) {
            for(String sub : requiredProjects) {
                extConf.getRequiredProjectsConfiguration().add(new LibraryItem.ProjectItem(new MakeArtifact(
                        sub, //String projectLocation
                        0, // int configurationType
                        "Default", // String configurationName
                        true, // boolean active
                        false, // boolean build
                        sub, // String workingDirectory
                        "${MAKE}  -f "+sub+"-Makefile.mk CONF=Default", // String buildCommand
                        "${MAKE}  -f "+sub+"-Makefile.mk CONF=Default clean", // String cleanCommand
                        "" //String output
                        )));
            }
        }

        //INCLUDES
        //    MakefileLexer lexer = new MakefileLexer(new FileReader());
        //      ca = new CodeAssistance();
        includes = new Vector<String>(ca.getIncludes(makefilePath));

        VectorConfiguration v = extConf.getCCCompilerConfiguration().getIncludeDirectories();
        v.setValue(includes);
        VectorConfiguration w = extConf.getCCompilerConfiguration().getIncludeDirectories();
        w.setValue(includes);
        //MACROS
        String macro = Global.evalMacros();
        extConf.getCCCompilerConfiguration().getPreprocessorConfiguration().setValue(macro);
        extConf.getCCompilerConfiguration().getPreprocessorConfiguration().setValue(macro);
        Iterator importantItemsIterator = null;
        if (makefilePath != null && makefilePath.length() > 0) {
            Vector<String> importantItems = new Vector<String>();
            makefilePath = IpeUtils.toRelativePath(dirF.getPath(), FilePathAdaptor.naturalize(makefilePath));
            makefilePath = FilePathAdaptor.normalize(makefilePath);
            importantItems.add(makefilePath);
            importantItemsIterator = importantItems.iterator();
        }

        Iterator it = null;
        if (sourceFiles != null) {
            it = sourceFiles.iterator();
        }
        AntProjectHelper h1 = null;
        makefileName = name + "-" + makefileName + ".mk";
        h1 = createProject(dirF, displayName, makefileName, new MakeConfiguration[]{extConf}, it, importantItemsIterator, runDiscovery);
        return h1;
    }

    /**
     * Create a new  Make project.
     * @param dir the top-level directory (need not yet exist but if it does it must be empty)
     * @param name the name for the project
     * @return the helper object permitting it to be further customized
     * @throws IOException in case something went wrong
     */
    public AntProjectHelper createProject(File dir, String displayName, String makefileName, Configuration[] confs, Iterator sourceFiles, Iterator importantItems, boolean runDiscovery) throws IOException {
        FileObject dirFO = createProjectDir(dir);
        AntProjectHelper h = createProject(dirFO, displayName, makefileName, confs, sourceFiles, importantItems);
        Project p = ProjectManager.getDefault().findProject(dirFO);
        if (runDiscovery) {
            applyDiscovery(p);
        }
        ProjectManager.getDefault().saveProject(p);
        return h;
    }

    //Create a project with specified project folder, makefile, name, source files and important items
    private AntProjectHelper createProject(FileObject dirFO, String displayName, String makefileName, Configuration[] confs, Iterator sourceFiles, Iterator importantItems) throws IOException {
        //Create a helper object
        AntProjectHelper h = ProjectGenerator.createProject(dirFO, TYPE);
        Element data = h.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element nameEl = doc.createElementNS(PROJECT_CONFIGURATION_NAMESPACE, "name");
        nameEl.appendChild(doc.createTextNode(displayName));
        data.appendChild(nameEl);
        Element nativeProjectType = doc.createElementNS(PROJECT_CONFIGURATION_NAMESPACE, "make-project-type");
        nativeProjectType.appendChild(doc.createTextNode("" + 0));
        data.appendChild(nativeProjectType);
        h.putPrimaryConfigurationData(data, true);
        EditableProperties ep = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        h.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        ep = h.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
        h.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, ep);

        // Create new project descriptor with default configurations and save it to disk
        MakeConfigurationDescriptor projectDescriptor = new MakeConfigurationDescriptor(FileUtil.toFile(dirFO).getPath());
        projectDescriptor.setProjectMakefileName(makefileName);
        projectDescriptor.init(confs);
        baseDir = projectDescriptor.getBaseDir();
        projectDescriptor.initLogicalFolders(null, sourceFiles == null, importantItems);
        rootFolder = projectDescriptor.getLogicalFolders();
        addFiles(sourceFiles);
        
        projectDescriptor.save();

        // create Makefile
        copyURLFile("nbresloc:/org/netbeans/modules/cnd/makeproject/resources/MasterMakefile", projectDescriptor.getBaseDir() + File.separator + projectDescriptor.getProjectMakefileName());
        return h;
    }

    private void applyDiscovery(Project project){
        IteratorExtension extension = (IteratorExtension)Lookup.getDefault().lookup(IteratorExtension.class);
        if (extension == null) {
            return;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("DW:rootFolder", workingDir); // NOI18N
        map.put("DW:buildResult", output); // NOI18N
        map.put("DW:libraries", null); // NOI18N
        map.put("DW:consolidationLevel", "project"); // NOI18N
        if (extension.canApply(map, project)){
            try {
                extension.apply(map, project);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    //add source files from filelist
    private void addFiles(Iterator filelist) {
        if (filelist == null) {
            return;
        }
        while (filelist.hasNext()) {
            File f = (File) filelist.next();
            String path = IpeUtils.toRelativePath(workingDir, f.getPath());
            StringTokenizer tok = new StringTokenizer(path, File.separator);
            String relativePath = IpeUtils.toRelativePath(baseDir, f.getPath());
            addFile(rootFolder, tok, f.getPath(), relativePath);
        }
    }

    //auxiliary function for addFiles(Iterator)
    private void addFile(Folder fld, StringTokenizer tok, String path, String relativePath) {
        if (tok.countTokens() == 1) {
            String t = path.substring(0, path.lastIndexOf(File.separator));
            String name = t.substring(t.lastIndexOf(File.separator) + 1);
            Folder top = null;
            top = fld.findFolderByName(name);
            if (top == null) {
                top = new Folder(fld.getConfigurationDescriptor(), fld, name, name, true);
                fld.addFolder(top);
            }
            fld = top;
        }
        while (tok.hasMoreElements()) {
            String part = tok.nextToken();
            if (part.contains(":") || part.equals("..")) {
                continue;
            }
            if (part.contains(".")) {
                fld.addItem(new Item(relativePath));
                continue;
            }
            Folder top = null;
            top = fld.findFolderByName(part);
            if (top == null) {
                top = new Folder(fld.getConfigurationDescriptor(), fld, part, part, true);
                fld.addFolder(top);
            }
            fld = top;
        }
    }

    //copy from one file with specified URL to another
    private void copyURLFile(String fromURL, String toFile) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(fromURL);
            is = url.openStream();
        } catch (Exception e) {
            //
        }
        if (is != null) {
            FileOutputStream os = new FileOutputStream(toFile);
            FileUtil.copy(is, os);
        }
    }

    //create project directory
    //return FileObject created with specified File dir
    private FileObject createProjectDir(File dir) throws IOException {
        FileObject dirFO;
        if (!dir.exists()) {
            //Refresh before mkdir not to depend on window focus
            refreshFileSystem(dir);
            if (!dir.mkdirs()) {
                throw new IOException("Can not create project folder."); // NOI18N
            }
            refreshFileSystem(dir);
        }
        dirFO = FileUtil.toFileObject(dir);
        assert dirFO != null : "No such dir on disk: " + dir; // NOI18N
        assert dirFO.isFolder() : "Not really a dir: " + dir; // NOI18N
        return dirFO;
    }

    //refresh file system
    private void refreshFileSystem(final File dir) throws FileStateInvalidException {
        File rootF = dir;
        while (rootF.getParentFile() != null) {
            rootF = rootF.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(rootF);
        assert dirFO != null : "At least disk roots must be mounted! " + rootF; // NOI18N
        dirFO.getFileSystem().refresh(false);
    }
}