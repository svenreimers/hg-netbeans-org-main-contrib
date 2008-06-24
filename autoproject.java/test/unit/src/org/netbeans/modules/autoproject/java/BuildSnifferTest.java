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
import java.io.IOException;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.TestFileUtils;

public class BuildSnifferTest extends NbTestCase {

    public BuildSnifferTest(String n) {
        super(n);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        Cache.clear();
    }

    public void testBasicJavac() throws Exception {
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s' destdir='c' source='1.5' classpath='x.jar'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        String prefix = getWorkDirPath() + File.separator;
        assertEquals(prefix + "s", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "c", Cache.get(prefix + "s" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "x.jar", Cache.get(prefix + "s" + JavaCacheConstants.CLASSPATH));
        assertEquals("1.5", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE_LEVEL));
    }

    public void testSourceRootCompiledMultiply() throws Exception {
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s' destdir='c' classpath='x.jar'/>\n" +
                "  <javac srcdir='s' destdir='c' classpath='y.jar'/>\n" +
                "  <javac srcdir='s' destdir='c' classpath='x.jar'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        String prefix = getWorkDirPath() + File.separator;
        assertEquals(prefix + "s", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "c", Cache.get(prefix + "s" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "x.jar" + File.pathSeparator + prefix + "y.jar", Cache.get(prefix + "s" + JavaCacheConstants.CLASSPATH));
    }

    public void testDestDirUsedMultiply() throws Exception { // #137861
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s1'/>\n" +
                "  <mkdir dir='s2'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s1' destdir='c' classpath='x.jar'/>\n" +
                "  <javac srcdir='s2' destdir='c' classpath='x.jar:y.jar'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        String prefix = getWorkDirPath() + File.separator;
        assertEquals(prefix + "s1" + File.pathSeparator + prefix + "s2", Cache.get(prefix + "s1" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "s1" + File.pathSeparator + prefix + "s2", Cache.get(prefix + "s2" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "c", Cache.get(prefix + "s1" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "c", Cache.get(prefix + "s2" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "x.jar" + File.pathSeparator + prefix + "y.jar", Cache.get(prefix + "s1" + JavaCacheConstants.CLASSPATH));
        assertEquals(prefix + "x.jar" + File.pathSeparator + prefix + "y.jar", Cache.get(prefix + "s2" + JavaCacheConstants.CLASSPATH));
    }

    private void write(String file, String body) throws IOException {
        TestFileUtils.writeFile(new File(getWorkDir(), file), body);
    }

    private void runAnt() throws IOException {
        int res = ActionUtils.runTarget(FileUtil.toFileObject(new File(getWorkDir(), "build.xml")), null, null).result();
        assertEquals("Ant script failed", 0, res);
    }

}
