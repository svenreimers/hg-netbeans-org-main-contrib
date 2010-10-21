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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.mount;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * "Project" corresponding to mounts.
 * @author Jesse Glick
 */
final class DummyProject implements Project {

    /** Hold it permanently so that it is not collected; singleton. */
    private static Project INSTANCE;
    
    public static synchronized Project getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = ProjectManager.getDefault().findProject(DummyProjectFactory.PROJECT_DIR);
            } catch (IOException e) {
                assert false : e;
            }
        }
        return INSTANCE;
    }
    
    private final Lookup lookup;

    DummyProject() {
        final Classpaths classpaths = new Classpaths();
        lookup = Lookups.fixed(new Object[] {
            this,
            new Info(),
            classpaths,
            new SourcesForBinary(),
            new MountSources(),
            new Actions(),
            // XXX SourceLevelQueryImplementation
        });
        ProjectManager.mutex().postWriteRequest(new Runnable() {
            public void run() {
                classpaths.register();
            }
        });
    }
    
    public Lookup getLookup() {
        return lookup;
    }

    public FileObject getProjectDirectory() {
        return DummyProjectFactory.PROJECT_DIR;
    }
    
    private final class Info implements ProjectInformation {
        
        public Info() {}
        
        public String getName() {
            return DummyProject.class.getName();
        }
        
        public String getDisplayName() {
            return "Filesystems";
        }
        
        public Icon getIcon() {
            // XXX
            return null;
        }
        
        public Project getProject() {
            return DummyProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {}
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {}
        
    }
    
}
