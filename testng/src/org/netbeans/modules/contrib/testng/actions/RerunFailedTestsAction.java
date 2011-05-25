/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2008-2011 Oracle and/or its affiliates. All rights reserved.
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.contrib.testng.spi.TestConfig;
import org.netbeans.modules.contrib.testng.api.TestNGSupport;
import org.netbeans.modules.contrib.testng.api.TestNGSupport.Action;
import org.netbeans.modules.contrib.testng.spi.TestNGSupportImplementation.TestExecutor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

@ActionID(id = "org.netbeans.modules.contrib.testng.actions.RerunFailedTestsAction", category = "TestNG")
@ActionRegistration(displayName = "#CTL_RerunFailedTestsAction")
@ActionReferences(value = {
    @ActionReference(path = "Loaders/text/x-java/Actions", position = 2195),
    @ActionReference(path = "Editors/text/x-java/Popup/TestNG", position = 400)})
public final class RerunFailedTestsAction extends NodeAction {

    private static final Logger LOGGER = Logger.getLogger(RerunFailedTestsAction.class.getName());

    public RerunFailedTestsAction() {
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return false;
        }
        Lookup l = activatedNodes[0].getLookup();
        Project p = l.lookup(Project.class);
        if (p == null) {
            DataObject dataObject = l.lookup(DataObject.class);
            if (dataObject != null && dataObject.getPrimaryFile() != null) {
                p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            } else {
                return false;
            }
        }
        if (TestNGSupport.isActionSupported(Action.RUN_FAILED, p)) {
            return TestNGSupport.findTestNGSupport(p).createExecutor(p).hasFailedTests();
        }
        return false;
    }

    protected void performAction(Node[] activatedNodes) {
        Lookup l = activatedNodes[0].getLookup();
        Project p = l.lookup(Project.class);
        if (p == null) {
            DataObject dataObject = l.lookup(DataObject.class);
            if (dataObject != null) {
                p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            }
        }
        TestExecutor exec = TestNGSupport.findTestNGSupport(p).createExecutor(p);
        assert exec.hasFailedTests();
        TestConfig conf = TestConfigAccessor.getDefault().createTestConfig(p.getProjectDirectory(), true, null, null, null);
        try {
            exec.execute(Action.RUN_FAILED, conf);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return NbBundle.getMessage(RerunFailedTestsAction.class, "CTL_RerunFailedTestsAction");
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

