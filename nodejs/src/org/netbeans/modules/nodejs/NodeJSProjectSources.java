/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.nodejs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tim Boudreau
 */
public class NodeJSProjectSources implements Sources, SourceGroup {
    private final NodeJSProject project;
    private final ChangeSupport supp = new ChangeSupport(this);

    public NodeJSProjectSources(NodeJSProject project) {
        this.project = project;
    }

    @Override
    public SourceGroup[] getSourceGroups(String type) {
        return new SourceGroup[] { this };
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        supp.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        supp.removeChangeListener(listener);
    }

    @Override
    public FileObject getRootFolder() {
        return project.getProjectDirectory();
    }

    @Override
    public String getName() {
        return Sources.TYPE_GENERIC;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(NodeJSProjectSources.class, "SOURCES");
    }

    @Override
    public Icon getIcon(boolean opened) {
        return null;
    }

    @Override
    public boolean contains(FileObject file) throws IllegalArgumentException {
        FileObject srcDir = project.getLookup().lookup(NodeJSProjectProperties.class).getSourceDir();
        return FileUtil.isParentOf(srcDir, file);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        //do nothing
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        //do nothing
    }
}
