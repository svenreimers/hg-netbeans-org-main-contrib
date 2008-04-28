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

package org.netbeans.modules.groovy.grails.api;

import org.netbeans.junit.NbTestCase;

/**
 *
 * @author Petr Hejl
 */
public class GrailsEnvironmentTest extends NbTestCase{

    public GrailsEnvironmentTest(String name) {
        super(name);
    }

    public void testForString() {
        assertEquals(GrailsEnvironment.DEVELOPMENT, GrailsEnvironment.forString("Development"));
        assertEquals(GrailsEnvironment.PRODUCTION, GrailsEnvironment.forString("Production"));
        assertEquals(GrailsEnvironment.TEST, GrailsEnvironment.forString("Test"));

        try {
            GrailsEnvironment.forString(null);
            fail("Method forString accepts null");
        } catch (NullPointerException ex) {
            // expected
        }

        try {
            GrailsEnvironment.forString("Something");
            fail("Unknown environment allowed");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testToString() {
        assertEquals("Development", GrailsEnvironment.DEVELOPMENT.toString());
        assertEquals("Production", GrailsEnvironment.PRODUCTION.toString());
        assertEquals("Test", GrailsEnvironment.TEST.toString());
    }
}