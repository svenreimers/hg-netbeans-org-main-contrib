/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.util.TreePath;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.infrastructure.ErrorHintsTestBase;
import org.netbeans.spi.editor.hints.Fix;


/**
 *
 * @author Jan Lahoda
 */
public class TypeInForEachLoopTest extends ErrorHintsTestBase {

    public TypeInForEachLoopTest(String name) {
        super(name);
    }

    public void testSimple1() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "     public void test() {\n" +
                       "         for (ttt |: java.util.Collections.emptyList()) {}\n" +
                       "     }\n" +
                       "}\n",
                       "FixImpl",
                       ("package test;\n" +
                       "public class Test {\n" +
                       "     public void test() {\n" +
                       "         for (Object ttt : java.util.Collections.emptyList()) {}\n" +
                       "     }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    public void testSimple2() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "     public void test() {\n" +
                       "         java.util.List<? extends Number> l = null;\n" +
                       "         for (ttt |: l) {}\n" +
                       "     }\n" +
                       "}\n",
                       "FixImpl",
                       ("package test;\n" +
                       "public class Test {\n" +
                       "     public void test() {\n" +
                       "         java.util.List<? extends Number> l = null;\n" +
                       "         for (Number ttt : l) {}\n" +
                       "     }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    public void testSimple3() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "     public void test() {\n" +
                       "         int[] a = null;\n" +
                       "         for (ttt |: a) {}\n" +
                       "     }\n" +
                       "}\n",
                       "FixImpl",
                       ("package test;\n" +
                       "public class Test {\n" +
                       "     public void test() {\n" +
                       "         int[] a = null;\n" +
                       "         for (int ttt : a) {}\n" +
                       "     }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    @Override
    protected List<Fix> computeFixes(CompilationInfo info, int pos, TreePath path) throws Exception {
        return new TypeInForEachLoop().run(info, null, pos, path, null);
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        return "FixImpl";
    }

}