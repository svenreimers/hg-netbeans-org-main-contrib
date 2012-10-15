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
package org.netbeans.modules.ada.platform.compiler.jgnat.commands;

import org.netbeans.api.ada.platform.AdaException;
import org.netbeans.modules.ada.platform.compiler.jgnat.JGnatCompiler;

/**
 * 
 * @author Andrea Lucarelli
 */
public abstract class JGnatCommand {

    public static final String RUN = "run"; // NOI18N

    // List of GNAT available commands
    public static final String JVM_GNAT_BIND = "jvm-gnatbind"; // NOI18N
    public static final String JVM_GNAT_CHOP = "jvm-gnatchop"; // NOI18N
    public static final String JVM_GNAT_CLEAN = "jvm-gnatclean"; // NOI18N
    public static final String JVM_GNAT_COMPILE = "jvm-gnatmake -f -u -c"; // NOI18N
    public static final String JVM_GNAT_ELIM = "jvm-gnatelim"; // NOI18N
    public static final String JVM_GNAT_FIND = "jvm-gnatfind"; // NOI18N
    public static final String JVM_GNAT_KRUNCH = "jvm-gnatkr"; // NOI18N
    public static final String JVM_GNAT_LINK = "jvm-gnatlink"; // NOI18N
    public static final String JVM_GNAT_LIST = "jvm-gnatls"; // NOI18N
    public static final String JVM_GNAT_MAKE = "jvm-gnatmake"; // NOI18N
    public static final String JVM_GNAT_NAME = "jvm-gnatname"; // NOI18N
    public static final String JVM_GNAT_PREPROCESS = "jvm-gnatprep"; // NOI18N
    public static final String JVM_GNAT_PRETTY = "jvm-gnatpp"; // NOI18N
    public static final String JVM_GNAT_STUB = "jvm-gnatstub"; // NOI18N
    public static final String JVM_GNAT_XREF = "jvm-gnatxref"; // NOI18N

    private final JGnatCompiler jgnatCompiler;

    public JGnatCommand(JGnatCompiler jgnatCompiler) {
        this.jgnatCompiler = jgnatCompiler;
    }

    public abstract String getCommandId();

    public abstract void invokeCommand(String displayTitle, String args) throws IllegalArgumentException, AdaException;

    public JGnatCompiler getJGnatCompiler() {
        return jgnatCompiler;
    }
}
