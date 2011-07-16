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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.hudsonfindbugs.spi.FindBugsQueryImplementation;
import org.netbeans.modules.java.j2seproject.api.J2SEPropertyEvaluator;
import org.netbeans.spi.project.ProjectServiceProvider;

/**
 *
 * @author Martin Grebac
 */
@ProjectServiceProvider(service=FindBugsQueryImplementation.class, projectType="org-netbeans-modules-java-j2seproject")
public final class J2seFindBugsQueryProvider implements FindBugsQueryImplementation {

    private static final Logger LOG = Logger.getLogger(J2seFindBugsQueryProvider.class.getName());

    private final Project project;

    public J2seFindBugsQueryProvider(Project project) {
        this.project = project;
    }
    
    @CheckForNull
    public URL getFindBugsUrl(boolean remote) {
        if (!remote) {
            return null;
        }

        URL url = null;
        String urlValue = null;
        // TODO when the provider is only in project's lookup this condition is not necessary.
        J2SEPropertyEvaluator eval = project.getLookup().lookup(J2SEPropertyEvaluator.class);
        if (eval != null) {
            try {
                urlValue = eval.evaluator().getProperty(FINDBUGS_PROJECT_PROPERTY);
                if (urlValue != null) {
                    url = new URL(urlValue);
                }
            } catch (MalformedURLException ex) {
                LOG.log(Level.INFO, "URL incorrect: {0} {1}", new Object[] {urlValue, ex.getLocalizedMessage()});
            }
        }
        return url;
    }

}