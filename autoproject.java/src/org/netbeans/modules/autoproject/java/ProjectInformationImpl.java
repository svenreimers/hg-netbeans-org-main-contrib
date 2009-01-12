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

package org.netbeans.modules.autoproject.java;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import org.apache.tools.ant.module.api.AntProjectCookie;
import org.apache.tools.ant.module.api.support.AntScriptUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.w3c.dom.Document;

/**
 * Display name inferred from Ant script etc.
 */
class ProjectInformationImpl implements ProjectInformation {

    private final Project p;

    public ProjectInformationImpl(Project p) {
        this.p = p;
    }

    public String getName() {
        return FileUtil.getFileDisplayName(p.getProjectDirectory());
    }

    public String getDisplayName() {
        FileObject f = p.getProjectDirectory().getFileObject("build.xml");
        if (f != null) {
            AntProjectCookie apc = AntScriptUtils.antProjectCookieFor(f);
            if (apc != null) {
                Document doc = apc.getDocument();
                if (doc != null) {
                    String name = doc.getDocumentElement().getAttribute("name");
                    if (name.length() > 0) {
                        return name;
                    }
                }
            }
        }
        return p.getProjectDirectory().getNameExt();
    }

    public Icon getIcon() {
        return ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/modules/autoproject/core/autoproject.png", true)); // NOI18N
    }

    public Project getProject() {
        return p;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // XXX would be good to fire changes
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // XXX
    }

}
