/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2008-2012 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.api.project.ant.AntBuildExtender.Extension;
import org.netbeans.modules.contrib.testng.api.TestNGSupport.Action;
import org.netbeans.modules.contrib.testng.spi.TestConfig;
import org.netbeans.modules.contrib.testng.spi.TestNGSupportImplementation;
import org.netbeans.modules.contrib.testng.spi.XMLSuiteSupport;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lukas
 */
@ServiceProvider(service=TestNGSupportImplementation.class)
public class AntTestNGSupport extends TestNGSupportImplementation {

    private static final Logger LOGGER = Logger.getLogger(AntTestNGSupport.class.getName());
    private static final Set<Action> SUPPORTED_ACTIONS;

    static {
        Set<Action> s = new HashSet<Action>();
        s.add(Action.CREATE_TEST);
        s.add(Action.RUN_FAILED);
        s.add(Action.RUN_TESTMETHOD);
        s.add(Action.RUN_TESTSUITE);
        s.add(Action.DEBUG_TEST);
        s.add(Action.DEBUG_TESTMETHOD);
        s.add(Action.DEBUG_TESTSUITE);
        SUPPORTED_ACTIONS = Collections.unmodifiableSet(s);
    }

    @Override
    public boolean isActionSupported(Action action, Project p) {
        return p != null && p.getLookup().lookup(AntArtifactProvider.class) != null && SUPPORTED_ACTIONS.contains(action);
    }

    @Override
    public void configureProject(FileObject createdFile) {
        try {
            addLibrary(createdFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        Project p = FileOwnerQuery.getOwner(createdFile);
        AntBuildExtender extender = p.getLookup().lookup(AntBuildExtender.class);
        if (extender != null) {
            String ID = "test-ng-1.0"; //NOI18N
            Extension extension = extender.getExtension(ID);
            if (extension == null) {
                LOGGER.log(Level.FINER, "Extensible targets: {0}", extender.getExtensibleTargets());
                try {
                    // create testng-build.xml
                    FileObject testng = p.getProjectDirectory().getFileObject("nbproject").createData("testng-impl", "xml"); //NOI18N
                    InputStream is = AntTestNGSupport.class.getResourceAsStream("testng-build.xml"); //NOI18N
                    FileLock lock = testng.lock();
                    OutputStream os = testng.getOutputStream(lock);
                    try {
                        FileUtil.copy(is, os);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                        lock.releaseLock();
                    }
                    extension = extender.addExtension(ID, testng);
                    extension.addDependency("-pre-pre-compile", "-reinit-tasks"); //NOI18N
                    ProjectManager.getDefault().saveProject(p);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public TestExecutor createExecutor(Project p) {
        return new AntExecutor(p);
    }

    private static class AntExecutor implements TestNGSupportImplementation.TestExecutor {

        private static final String failedConfPath = "build/test/results/testng-failed.xml"; //NOI18N
        private Project p;

        public AntExecutor(Project p) {
            this.p = p;
        }

        @Override
        public boolean hasFailedTests() {
            FileObject projectHome = p.getProjectDirectory();
            //XXX - should rather listen on a fileobject??
            FileUtil.refreshFor(FileUtil.toFile(projectHome));
            FileObject failedTestsConfig = projectHome.getFileObject(failedConfPath);
            return failedTestsConfig != null && failedTestsConfig.isValid();
        }

        @Override
        public void execute(Action action, TestConfig config) throws IOException {
            FileObject projectHome = p.getProjectDirectory();
            Properties props = new Properties();
            if (config.doRerun()) {
                FileObject failedTestsConfig = projectHome.getFileObject(failedConfPath);
                props.put("testng.config", FileUtil.getRelativePath(projectHome, failedTestsConfig));
            } else {
                if (Action.RUN_TESTSUITE.equals(action)) {
                    props.put("testng.config", FileUtil.toFile(config.getTest()).getAbsolutePath());
                } else if (Action.DEBUG_TESTSUITE.equals(action)) {
                    props.put("test.class.or.method", FileUtil.toFile(config.getTest()).getAbsolutePath());
                } else if (Action.RUN_TESTMETHOD.equals(action)) {
                    File f = XMLSuiteSupport.createSuiteforMethod(
                        FileUtil.normalizeFile(new File(System.getProperty("java.io.tmpdir"))), //NOI18N
                        ProjectUtils.getInformation(p).getDisplayName(),
                        config.getPackageName(),
                        config.getClassName(),
                        config.getMethodName());
                    f = FileUtil.normalizeFile(f);
                    props.put("testng.config", f.getAbsolutePath());
                } else {
                    String cls = config.getPackageName() != null
                            ? config.getPackageName() + "." + config.getClassName()
                            : config.getClassName();
                    props.put("test.class", cls);
                    if (config.getMethodName() != null && config.getMethodName().trim().length() > 0) {
                        props.put("test.class.or.method", "-methods " + cls + "." + config.getMethodName());
                    }
                }
            }
            try {
                String target = "run-testng"; //NOI18N
                if (Action.DEBUG_TEST.equals(action) || Action.DEBUG_TESTMETHOD.equals(action) || Action.DEBUG_TESTSUITE.equals(action)) {
                    target = "debug-testng"; //NOI18N
                    FileObject test = config.getTest();
                    FileObject[] testRoots = ClassPath.getClassPath(test, ClassPath.SOURCE).getRoots();
                    FileObject testRoot = null;
                    for (FileObject root : testRoots) {
                        if (FileUtil.isParentOf(root, test)) {
                            testRoot = root;
                            break;
                        }
                    }
                    assert testRoot != null;
                    if (Action.DEBUG_TESTSUITE.equals(action)) {
                        props.put("javac.includes", //NOI18N
                            ActionUtils.antIncludesList(new FileObject[]{testRoot}, testRoot, true));
                    } else {
                        props.put("javac.includes", //NOI18N
                            ActionUtils.antIncludesList(new FileObject[]{test}, testRoot));
                    }
                }
                ActionUtils.runTarget(projectHome.getFileObject("build.xml"), new String[]{target}, props); //NOI18N
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
