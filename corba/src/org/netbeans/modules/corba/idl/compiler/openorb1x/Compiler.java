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

package org.netbeans.modules.corba.idl.compiler.openorb1x;

import java.io.PrintStream;

import java.lang.reflect.Method;

public class Compiler extends PrintStream {

    public static void main (String[] __args) throws Exception {
	//System.out.println ("OpenORB 1.x IDL Compiler Wrapper 1.0");
        System.setOut(new Compiler (System.out));
        //org.openorb.compiler.IdlCompiler.main(__args);
	String[] __s_array = new String[0];
	Class __class = Class.forName ("org.openorb.compiler.IdlCompiler");
	Method __main = __class.getDeclaredMethod
	    ("main", new Class [] {__s_array.getClass ()});
	__main.invoke (null, new Object[] {__args});
    }

    Compiler (PrintStream out) {
        super (out);
    }

    public void println(String x) {
        if (x != null && x.startsWith("file:") && x.charAt(7) == ':') {
            super.print(x.substring(0, 5));
            println(x.substring(6));
        }
        else
            super.println(x);
    }

}
