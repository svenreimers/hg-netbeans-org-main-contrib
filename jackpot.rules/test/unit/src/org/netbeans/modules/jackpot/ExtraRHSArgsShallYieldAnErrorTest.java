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

package org.netbeans.modules.jackpot;

import java.io.File;
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.jackpot.ScriptParsingException;

/** Having a variable on RHS which is not on left shall yeild an error.
 *
 * @author Jaroslav Tulach
 */
public class ExtraRHSArgsShallYieldAnErrorTest extends NbTestCase {
    
    public ExtraRHSArgsShallYieldAnErrorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }

    protected void tearDown() throws Exception {
    }

    public void testCanMatchProperUsage() throws Exception {
        File api = new File(getWorkDir(), "API.java");
        TestUtilities.copyStringToFile(api, 
            "package x.y;\n" +
            "class API {\n" +
            "  public void annotate(Throwable t, String msg, String locMsg, Throwable t2, java.util.Date d) {\n" +
            "  }\n" +
            "  public static void newMethod(Throwable t, String msg, String locMsg, Throwable t2, java.util.Date d) {\n" +
            "  }\n" +
            "}\n"
            );
        String code = 
            "package x.y;\n" +
            "class CallsTheMethod {\n" +
            "  static {\n" +
            "    API a = null;\n" +
            "    a.annotate(new java.io.IOException(), null, \"ahoj\", null, null);\n" +
            "  }\n" +
            "}\n";
        
        String rule = 
            "$API.annotate($throw, null, $locmsg, null, null) =>\n" +
            "  $API.newMethod($throw, $msg, $locmsg, $cause, $date)" +
            "  :: $throw instanceof java.lang.Throwable, " +
            "     $locmsg instanceof java.lang.String || isNull($cause);" +
            "\n";
        
        File java = new File(getWorkDir(), "CallsTheMethod.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);

        try {
            TestUtilities.applyRules(getWorkDir(), ruleFile.toURI().toURL());
        } catch (ScriptParsingException ex) {
            // ok, the $cause and $date are undefined on RHS and thus the 
            // rule is wrong
            return;
        }

        fail("The rule is wrong, and the build of it shall fail");
    }

}
