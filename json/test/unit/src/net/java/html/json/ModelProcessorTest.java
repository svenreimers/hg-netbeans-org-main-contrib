/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package net.java.html.json;

import java.io.IOException;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/** Verify errors emitted by the processor.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class ModelProcessorTest {
    @Test public void verifyWrongType() throws IOException {
        String html = "<html><body>"
            + "</body></html>";
        String code = "package x.y.z;\n"
            + "import net.java.html.json.Model;\n"
            + "import net.java.html.json.Property;\n"
            + "@Model(className=\"XModel\", properties={\n"
            + "  @Property(name=\"prop\", type=Runnable.class)\n"
            + "})\n"
            + "class X {\n"
            + "}\n";
        
        Compile c = Compile.create(html, code);
        assertFalse(c.getErrors().isEmpty(), "One error: " + c.getErrors());
        boolean ok = false;
        StringBuilder msgs = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> e : c.getErrors()) {
            String msg = e.getMessage(Locale.ENGLISH);
            if (msg.contains("Runnable")) {
                ok = true;
            }
            msgs.append("\n").append(msg);
        }
        if (!ok) {
            fail("Should contain warning about Runnable:" + msgs);
        }
    }
    
    @Test public void warnOnNonStatic() throws IOException {
        String html = "<html><body>"
            + "</body></html>";
        String code = "package x.y.z;\n"
            + "import net.java.html.json.Model;\n"
            + "import net.java.html.json.Property;\n"
            + "import net.java.html.json.ComputedProperty;\n"
            + "@Model(className=\"XModel\", properties={\n"
            + "  @Property(name=\"prop\", type=int.class)\n"
            + "})\n"
            + "class X {\n"
            + "    @ComputedProperty int y(int prop) {\n"
            + "        return prop;\n"
            + "    }\n"
            + "}\n";
        
        Compile c = Compile.create(html, code);
        assertFalse(c.getErrors().isEmpty(), "One error: " + c.getErrors());
        boolean ok = false;
        StringBuilder msgs = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> e : c.getErrors()) {
            String msg = e.getMessage(Locale.ENGLISH);
            if (msg.contains("y has to be static")) {
                ok = true;
            }
            msgs.append("\n").append(msg);
        }
        if (!ok) {
            fail("Should contain warning about non-static method:" + msgs);
        }
    }
    
    @Test public void computedCantReturnVoid() throws IOException {
        String html = "<html><body>"
            + "</body></html>";
        String code = "package x.y.z;\n"
            + "import net.java.html.json.Model;\n"
            + "import net.java.html.json.Property;\n"
            + "import net.java.html.json.ComputedProperty;\n"
            + "@Model(className=\"XModel\", properties={\n"
            + "  @Property(name=\"prop\", type=int.class)\n"
            + "})\n"
            + "class X {\n"
            + "    @ComputedProperty static void y(int prop) {\n"
            + "    }\n"
            + "}\n";
        
        Compile c = Compile.create(html, code);
        assertFalse(c.getErrors().isEmpty(), "One error: " + c.getErrors());
        boolean ok = false;
        StringBuilder msgs = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> e : c.getErrors()) {
            String msg = e.getMessage(Locale.ENGLISH);
            if (msg.contains("y cannot return void")) {
                ok = true;
            }
            msgs.append("\n").append(msg);
        }
        if (!ok) {
            fail("Should contain warning about non-static method:" + msgs);
        }
    }
    
    @Test public void computedCantReturnRunnable() throws IOException {
        String html = "<html><body>"
            + "</body></html>";
        String code = "package x.y.z;\n"
            + "import net.java.html.json.Model;\n"
            + "import net.java.html.json.Property;\n"
            + "import net.java.html.json.ComputedProperty;\n"
            + "@Model(className=\"XModel\", properties={\n"
            + "  @Property(name=\"prop\", type=int.class)\n"
            + "})\n"
            + "class X {\n"
            + "    @ComputedProperty static Runnable y(int prop) {\n"
            + "       return null;\n"
            + "    }\n"
            + "}\n";
        
        Compile c = Compile.create(html, code);
        assertFalse(c.getErrors().isEmpty(), "One error: " + c.getErrors());
        boolean ok = false;
        StringBuilder msgs = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> e : c.getErrors()) {
            String msg = e.getMessage(Locale.ENGLISH);
            if (msg.contains("y cannot return java.lang.Runnable")) {
                ok = true;
            }
            msgs.append("\n").append(msg);
        }
        if (!ok) {
            fail("Should contain warning about non-static method:" + msgs);
        }
    }
    
    @Test public void canWeCompileWithJDK1_5SourceLevel() throws IOException {
        String html = "<html><body>"
            + "</body></html>";
        String code = "package x.y.z;\n"
            + "import net.java.html.json.Model;\n"
            + "import net.java.html.json.Property;\n"
            + "import net.java.html.json.ComputedProperty;\n"
            + "@Model(className=\"XModel\", properties={\n"
            + "  @Property(name=\"prop\", type=long.class)\n"
            + "})\n"
            + "class X {\n"
            + "  @ComputedProperty static double derived(long prop) { return prop; }"
            + "}\n";
        
        Compile c = Compile.create(html, code, "1.5");
        assertTrue(c.getErrors().isEmpty(), "No errors: " + c.getErrors());
    }
    
}
