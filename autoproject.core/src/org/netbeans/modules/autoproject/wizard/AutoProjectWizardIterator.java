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

package org.netbeans.modules.autoproject.wizard;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import static org.netbeans.modules.autoproject.wizard.Bundle.*;
import org.openide.util.NbBundle.Messages;

@TemplateRegistration(
    folder="Project/Standard",
    position=410,
    displayName="#template",
    iconBase="org/netbeans/modules/autoproject/core/autoproject.png",
    description="AutoProjectDescription.html"
)
@Messages("template=Automatic Project")
public class AutoProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {
    private int index;

    private WizardDescriptor.Panel[] panels;

    private WizardDescriptor wiz;

    /** public for instantiation from layer */
    public AutoProjectWizardIterator() {}

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new AutoProjectWizardPanel(),
        };
    }

    @Messages("LBL_CreateProjectStep=Location")
    private String[] createSteps() {
        return new String[] {
            LBL_CreateProjectStep()
        };
    }

    public Set<FileObject> instantiate() throws IOException {
        File dir = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
        Cache.put(dir + Cache.PROJECT, "true");
        ProjectChooser.setProjectsFolder(dir.getParentFile());
        FileObject d = FileUtil.toFileObject(dir);
        final FileObject nbproject = d.getFileObject("nbproject");
        if (nbproject != null) { // #153232
            // Need to use RP to avoid getting stack traces from Subversion when running in EQ.
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        FileLock lock = nbproject.lock();
                        try {
                            nbproject.rename(lock, "nbproject.bak", null);
                        } finally {
                            lock.releaseLock();
                        }
                    } catch (IOException x) {
                        Logger.getLogger(AutoProjectWizardIterator.class.getName()).log(Level.WARNING, null, x);
                    }
                }
            })./* otherwise will not recognize dir as AP */waitFinished();
        }
        return Collections.singleton(d);
    }

    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }

    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir", null);
        this.wiz.putProperty("name", null);
        this.wiz = null;
        panels = null;
    }

    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[] {new Integer(index + 1), new Integer(panels.length)});
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    public final void addChangeListener(ChangeListener l) {}

    public final void removeChangeListener(ChangeListener l) {}

}
