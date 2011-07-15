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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.hudsonfindbugs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider;
import org.netbeans.modules.hudsonfindbugs.spi.FindBugsQueryImplementation;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Grebac
 */
@ProjectServiceProvider(service=FindBugsQueryImplementation.class, projectType="org-netbeans-modules-apisupport-project")
public final class NBMFindBugsQueryProvider implements FindBugsQueryImplementation {

    private final static String NB_HUDSON_FBUGS_URLROOT = 
            "http://qa-findbugs.netbeans.org/job/FindBugsResults/lastSuccessfulBuild/artifact/";

    private final Project project;

    public NBMFindBugsQueryProvider(Project project) {
        this.project = project;
    }
    
    @CheckForNull
    public URL getFindBugsUrl(boolean remote) {
        URL url = null;
        NbModuleProvider prov = project.getLookup().lookup(NbModuleProvider.class);
        if (prov != null) {
            if (!remote) {
                File nbbuild = new File(FileUtil.toFile(project.getProjectDirectory()).getParentFile(), "nbbuild");
                if (nbbuild.isDirectory()) {
                    File findbugsFile = new File(nbbuild, "build" + File.separator + "findbugs" + File.separator
                            + prov.getCodeNameBase().replace('.', '-') + ".xml");
                    if (findbugsFile.exists() && findbugsFile.isFile() && findbugsFile.canRead()) {
                        try {
                            return findbugsFile.toURI().toURL();
                        } catch (MalformedURLException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }

            try {
                String urlStr = NB_HUDSON_FBUGS_URLROOT + prov.getCodeNameBase().replace('.', '-') + ".xml";
                url = new URL(urlStr);
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return url;
    }

}