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


package org.netbeans.modules.jemmysupport.bundlelookup;
/*
 * ComponentGeneratorTest.java
 *
 * Created on July 11, 2002, 2:27 PM
 */

import java.io.*;

import junit.framework.*;
import org.netbeans.junit.*;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.actions.PasteAction;
import org.netbeans.jellytools.modules.jemmysupport.*;

/** JUnit test suite with Jemmy/Jelly2 support
 *
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public class ResourceBundleLookupTest extends JellyTestCase {
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public ResourceBundleLookupTest(String testName) {
        super(testName);
    }
    
    /** method used for explicit testsuite definition
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new ResourceBundleLookupTest("testSearch1"));
        suite.addTest(new ResourceBundleLookupTest("testSearch2"));
        suite.addTest(new ResourceBundleLookupTest("testSearch3"));
        suite.addTest(new ResourceBundleLookupTest("testSearch4"));
        suite.addTest(new ResourceBundleLookupTest("testSearch5"));
        suite.addTest(new ResourceBundleLookupTest("testSearch6"));
        suite.addTest(new ResourceBundleLookupTest("testResultsPopup"));
        return suite;
    }
    
    /** method called before each testcase
     */
    protected void setUp() throws IOException {
    }
    
    /** method called after each testcase
     */
    protected void tearDown() {
    }
    
    /** Use for internal test execution inside IDE
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
                
    /** simple test case
     */
    public void testSearch1() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(false);
        if (op.cbRegularExpressionText().isEnabled())
            op.checkRegularExpressionText(false);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch2() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(true);
        op.checkSubstringText(true);
        if (op.cbRegularExpressionText().isEnabled())
            op.checkRegularExpressionText(false);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch3() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(true);
        if (op.cbRegularExpressionText().isEnabled())
            op.checkRegularExpressionText(false);
        op.search();
        op.verifyStatus("Found 4 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch4() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        if (!op.cbRegularExpressionText().isEnabled()) return;
        op.setSearchedText("te[s]t.alue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(false);
        op.checkRegularExpressionText(true);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch5() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        if (!op.cbRegularExpressionText().isEnabled()) return;
        op.setSearchedText("te[s]t.alue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(true);
        op.checkSubstringText(true);
        op.checkRegularExpressionText(true);
        op.search();
        op.verifyStatus("Found 2 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testSearch6() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        if (!op.cbRegularExpressionText().isEnabled()) return;
        op.setSearchedText("te[s]t.alue");
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        
        op.checkCaseSensitiveText(false);
        op.checkSubstringText(true);
        op.checkRegularExpressionText(true);
        op.search();
        op.verifyStatus("Found 4 keys.");
        op.close();
    }
                
    /** simple test case
     */
    public void testResultsPopup() throws Exception {
        ResourceBundleLookupOperator op=ResourceBundleLookupOperator.invoke();
        op.setSearchedText("testvalue");
        op.checkCaseSensitiveText(true);
        op.checkSubstringText(false);
        op.checkRegularExpressionText(false);
        op.checkUseResourceBundleFilter(true);
        op.setFilterText("org.netbeans.modules.jemmysupport.bundlelookup.data");
        op.search();
        op.verifyStatus("Found 1 keys.");
        int i=op.tabSearchResults().findCellRow("testkey");
        op.tabSearchResults().selectCell(i, 0);
        op.tabSearchResults().clickForPopup();
        JPopupMenuOperator popup=new JPopupMenuOperator();
        new JMenuItemOperator(popup, "Copy: java.util.ResourceBundle.getBundle(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\").getString(\"testkey\")");
        new JMenuItemOperator(popup, "Copy: org.openide.util.NbBundle.getBundle(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\").getString(\"testkey\")");
        new JMenuItemOperator(popup, "Copy: org.netbeans.jellytools.Bundle.getString(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\", \"testkey\")");
        new JMenuItemOperator(popup, "Copy: org.netbeans.jellytools.Bundle.getStringTrimmed(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\", \"testkey\")").push();
        op.setSearchedText("");
        op.txtSearchedText().clickMouse();
        new PasteAction().performShortcut(op.txtSearchedText());
        assertEquals("org.netbeans.jellytools.Bundle.getStringTrimmed(\"org.netbeans.modules.jemmysupport.bundlelookup.data.Bundle\", \"testkey\")", op.getSearchedText());
        op.setSearchedText("");
        op.verify();
        op.close();
    }
}
