/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.AnnotationProcessingQuery.Result;
import org.netbeans.api.java.queries.AnnotationProcessingQuery.Trigger;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.java.queries.AnnotationProcessingQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

class AnnotationProcessingQueryImpl implements AnnotationProcessingQueryImplementation {

    public AnnotationProcessingQueryImpl() {}

    public Result getAnnotationProcessingOptions(FileObject file) {
        File f = FileUtil.toFile(file);
        if (f == null) {
            return null;
        }
        File root = f;
        while (root != null) {
            if (Cache.get(root + JavaCacheConstants.SOURCE) != null) {
                return optionsForRoot(root);
            }
            root = root.getParentFile();
        }
        return null;
    }

    private Result optionsForRoot(File root) {
        return new ResultImpl(root);
    }

    private static class ResultImpl implements Result, PropertyChangeListener {

        private final File root;
        private final ChangeSupport cs = new ChangeSupport(this);

        @SuppressWarnings("LeakingThisInConstructor")
        ResultImpl(File root) {
            this.root = root;
            Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
        }

        private List<String> opts() {
            String v = Cache.get(root + JavaCacheConstants.PROCESSOR_OPTIONS);
            if (v == null || v.isEmpty()) {
                return Collections.emptyList();
            }
            return Arrays.asList(v.split(" "));
        }

        public Set<? extends Trigger> annotationProcessingEnabled() {
            return opts().contains("-proc:none")
                    ? EnumSet.noneOf(Trigger.class) : EnumSet.allOf(Trigger.class);
        }

        public Iterable<? extends String> annotationProcessorsToRun() {
            List<String> opts = opts();
            int i = opts.indexOf("-processor");
            if (i != -1) {
                return Arrays.asList(opts.get(i + 1).split(","));
            } else {
                return null;
            }
        }

        public URL sourceOutputDirectory() {
            List<String> opts = opts();
            int i = opts.indexOf("-s");
            if (i != -1) {
                return FileUtil.urlForArchiveOrDir(new File(opts.get(i + 1)));
            } else {
                return null;
            }
        }

        public Map<? extends String, ? extends String> processorOptions() {
            List<String> opts = opts();
            Map<String,String> r = new HashMap<String,String>();
            for (String opt : opts) {
                if (opt.startsWith("-A")) {
                    int i = opt.indexOf('=');
                    if (i == -1) {
                        r.put(opt.substring(2), null);
                    } else {
                        r.put(opt.substring(2, i), opt.substring(i + 1));
                    }
                }
            }
            return r;
        }

        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            cs.fireChange();
        }
    }

}
