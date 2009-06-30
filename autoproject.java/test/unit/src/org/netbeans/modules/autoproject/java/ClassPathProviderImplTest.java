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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.MockPropertyChangeListener;
import org.openide.util.test.TestFileUtils;

public class ClassPathProviderImplTest extends NbTestCase {

    public ClassPathProviderImplTest(String n) {
        super(n);
    }

    protected @Override void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        Cache.clear();
    }

    public void testEmptyClasspath() throws Exception {
        File r = getWorkDir();
        FileObject fo = FileUtil.toFileObject(r);
        String s = r.getAbsolutePath();
        Cache.put(s + JavaCacheConstants.SOURCE, s);
        Cache.put(s + JavaCacheConstants.CLASSPATH, "");
        ClassPathProvider cpp = new ClassPathProviderImpl(null);
        assertEquals(Collections.emptyList(), cpp.findClassPath(fo, ClassPath.COMPILE).entries());
    }

    public void testInferredSourceRoots() throws Exception {
        File src = new File(getWorkDir(), "src");
        File clazz = new File(src, "pkg/Clazz.java");
        TestFileUtils.writeFile(clazz, "package pkg; public class Clazz {}");
        ClassPathProvider cpp = new ClassPathProviderImpl(null);
        ClassPath sourcepath = cpp.findClassPath(FileUtil.toFileObject(clazz), ClassPath.SOURCE);
        assertNotNull(sourcepath);
        assertEquals(Collections.singletonList(FileUtil.toFileObject(src)), Arrays.asList(sourcepath.getRoots()));
    }

    public void testIncludesExcludes() throws Exception {
        File r = getWorkDir();
        FileObject fo = FileUtil.toFileObject(r);
        String s = r.getAbsolutePath();
        Cache.put(s + JavaCacheConstants.SOURCE, s);
        Cache.put(s + JavaCacheConstants.CLASSPATH, "");
        Cache.put(s + JavaCacheConstants.INCLUDES, "com/");
        Cache.put(s + JavaCacheConstants.EXCLUDES, "com/foreign1/,com/foreign2/");
        ClassPathProvider cpp = new ClassPathProviderImpl(null);
        ClassPath sourcepath = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertNotNull(sourcepath);
        List<ClassPath.Entry> entries = sourcepath.entries();
        assertEquals(1, entries.size());
        ClassPath.Entry entry = entries.get(0);
        assertEquals(fo, entry.getRoot());
        assertTrue(entry.includes("com/domestic/Class.java"));
        assertFalse(entry.includes("com/foreign1/Class.java"));
        assertFalse(entry.includes("com/foreign2/Class.java"));
        MockPropertyChangeListener pcl = new MockPropertyChangeListener(ClassPath.PROP_INCLUDES);
        sourcepath.addPropertyChangeListener(pcl);
        Cache.put(s + JavaCacheConstants.EXCLUDES, "com/foreign1/");
        pcl.assertEvents(ClassPath.PROP_INCLUDES);
        assertTrue(entry.includes("com/domestic/Class.java"));
        assertFalse(entry.includes("com/foreign1/Class.java"));
        assertTrue(entry.includes("com/foreign2/Class.java"));
    }

}
