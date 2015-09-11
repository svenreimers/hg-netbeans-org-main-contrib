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

public class JavaCacheConstants {

    private JavaCacheConstants() {}

    public static final String SOURCE = "#source";
    public static final String CLASSPATH = "#classpath";
    public static final String PROCESSORPATH = "#processorpath";
    public static final String BOOTCLASSPATH = "#bootclasspath";
    public static final String BINARY = "#binary";
    public static final String SOURCE_LEVEL = "#sourcelevel";
    public static final String INCLUDES = "#includes";
    public static final String EXCLUDES = "#excludes";
    public static final String PROCESSOR_OPTIONS = "#processoropts";
    /**
     * Original location of classes packed into a JAR. Key is JAR, value is dir or path of dirs.
     */
    public static final String JAR = "#jar";
    /**
     * VM args, such as those passed to JUnit.
     * Key is (typically test) source directory plus {@code 0}, {@code 1}, etc.; value is a VM argument (possibly {@code -Dk=v}).
     */
    public static final String VMARGS = "#vmargs.";

}
