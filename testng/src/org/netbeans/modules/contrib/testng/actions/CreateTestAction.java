/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.contrib.testng.actions;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.modules.contrib.testng.BuildScriptHandler;
import org.netbeans.modules.contrib.testng.TestNGProjectUpdater;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class CreateTestAction extends CookieAction {

    private static final Logger LOGGER = Logger.getLogger(CreateTestAction.class.getName());
    
    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject pFile = dataObject.getPrimaryFile();
        ClassPath cp = ClassPath.getClassPath(pFile, ClassPath.SOURCE);
        FileObject cpRoot = cp.findOwnerRoot(pFile);
        //XXX - find proper package name from java file using JAVAC API
        String s = FileUtil.getRelativePath(cpRoot, pFile);
        DummyUI gui = new DummyUI(s.substring(0, s.length() - 5).replace('/', '.') + "Test");
        Object result = DialogDisplayer.getDefault().notify(new DialogDescriptor(gui, "Create TestNG Test"));
        if (DialogDescriptor.OK_OPTION.equals(result)) {
            FileObject templateFO = Repository.getDefault().getDefaultFileSystem().findResource("Templates/TestNG/TestNGTest.java");
            DataObject templateDO = null;
            try {
                templateDO = DataObject.find(templateFO);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            String n = gui.getTestName();
            String pkg = n.substring(0, n.lastIndexOf("."));
            String name = n.substring(n.lastIndexOf('.') + 1);
            URL[] test = UnitTestForSourceQuery.findUnitTests(cpRoot);
            FileObject testFolder = URLMapper.findFileObject(test[0]);
            FileObject targetFolder = null;
            try {
                targetFolder = FileUtil.createFolder(testFolder, pkg.replace('.', '/'));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (templateDO != null) {
                DataObject createdFile = null;
                try {
                    createdFile = templateDO.createFromTemplate(DataFolder.findFolder(targetFolder), name, Collections.singletonMap("package", pkg));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    TestNGProjectUpdater.updateProject(createdFile.getPrimaryFile());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                final LineCookie lc = createdFile.getCookie(LineCookie.class);
                if (lc != null) {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            //XXX - should find correct line # programatically
                            Line l = lc.getLineSet().getOriginal(16);
                            l.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS);
                        }
                    });
                } else {
                    final EditorCookie ec = createdFile.getCookie(EditorCookie.class);
                    if (ec != null) {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                ec.open();

                            }
                        });
                    } else {
                        LOGGER.info("Didn't get LineCookie nor EditorCookie for: " + createdFile.getPrimaryFile()); //NOI18N
                    }
                }
            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(CreateTestAction.class, "CTL_CreateTestAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

