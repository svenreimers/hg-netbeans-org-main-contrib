/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package complete.vss_profile;

import junit.framework.*;
import org.netbeans.junit.*;
import org.openide.util.Utilities;

/** XTest / JUnit test class performing testing of whole VSS support.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AllTogether extends NbTestCase {

    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AllTogether(String testName) {
        super(testName);
    }

    /** Method used for explicit test suite definition.
     * @return AllTogether test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String zipFile = "C:\\Program Files\\Microsoft Visual Studio\\vss.zip";
        if (!new java.io.File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTestSuite(RepositoryCreation.class);
        suite.addTestSuite(RegularDevelopment.class);
        suite.addTestSuite(AdditionalCommands.class);
        suite.addTestSuite(AdditionalFeatures.class);
        return suite;
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new java.io.File(workingDir).mkdirs();
        java.io.File outputFile = new java.io.File(workingDir + "/output.txt");
        outputFile.createNewFile();
        java.io.File errorFile = new java.io.File(workingDir + "/error.txt");
        errorFile.createNewFile();
        java.io.PrintWriter outputWriter = new java.io.PrintWriter(new java.io.FileWriter(outputFile));
        java.io.PrintWriter errorWriter = new java.io.PrintWriter(new java.io.FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }

    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}