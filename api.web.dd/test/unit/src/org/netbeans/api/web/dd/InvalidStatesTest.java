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

package ddapi.test.unit.src.org.netbeans.api.web.dd;

import org.netbeans.api.web.dd.*;
import org.netbeans.api.web.dd.common.VersionNotSupportedException;
import org.netbeans.api.web.dd.common.NameAlreadyUsedException;
import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.openide.filesystems.*;
import java.beans.*;


public class InvalidStatesTest extends NbTestCase {
    private static final String VERSION="2.4";
    private static final String[] expectedEvents =
        {"PCE:STATUS[1]:dd_status:2:1",
         "PCE:STATUS[0]:dd_status:1:0",
         "PCE:STATUS[0]:SessionTimeout:null:30"
        };
    private static java.util.List evtList = new java.util.ArrayList();
    
    private WebApp webApp;
    
    public InvalidStatesTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(InvalidStatesTest.class);
        
        return suite;
    }
    
    /** Test of greeting method, of class HelloWorld. */
    public void test_InvalidDataReading () {
        System.out.println("TEST:Invalid Data Reading");
        assertEquals("Incorrect servlet spec.version :",VERSION,webApp.getVersion());
        assertEquals("Incorrect dd status :",WebApp.STATE_INVALID_UNPARSABLE,webApp.getStatus());
        assertNotNull("Error mustn't be null :", webApp.getError());
        System.out.println("Expected Exception :"+webApp.getError());
        assertNull("Session Config must be null :", webApp.getSingleSessionConfig());
    }

    public void test_Correction1 () {
        System.out.println("TEST:Invalid Data Correction - editing");
        // replacing web.xml with web_parsable.xml
        File dataDir = new File(System.getProperty("test.data.dir")+"/invalid");
        FileObject dataFolder = FileUtil.toFileObject(dataDir);
        FileObject fo1 = dataFolder.getFileObject("web_parsable","xml");
        assertTrue("FileObject invalid/web_parsable.xml not found",null != fo1);
        
        try {
            FileLock lock = fo.lock();
            OutputStream os = fo.getOutputStream(lock);
            InputStream is = fo1.getInputStream();
            int b;
            while ((b = is.read())!=-1) 
                os.write(b);
            is.close();
            os.close();
            lock.releaseLock();
        } catch (IOException ex) {
            throw new AssertionFailedErrorException("Writing data Failed ",ex);
        }
        assertEquals("Incorrect dd status :",WebApp.STATE_INVALID_PARSABLE,webApp.getStatus());
        assertNotNull("Error mustn't be null :", webApp.getError());
        System.out.println("Expected Exception :"+webApp.getError());
        assertNotNull("Session Config mustn't be null :", webApp.getSingleSessionConfig());
        assertNull("Session Timeout must be null :", webApp.getSingleSessionConfig().getSessionTimeout());
    }
    
    public void test_Correction2 () {
        System.out.println("TEST:Invalid Data Correction - programmatic correction");
        WebApp webAppCopy=null;
        try {
            webAppCopy = DDProvider.getDefault().getDDRootCopy(fo);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        assertTrue("WebAppCopy not created ", null != webAppCopy);
        assertNotNull("Session Config mustn't be null :", webAppCopy.getSingleSessionConfig());
        
        webAppCopy.getSingleSessionConfig().setSessionTimeout(new java.math.BigInteger("30"));
        System.out.println("status = "+webAppCopy.getStatus());
        try {
            webAppCopy.write(fo);
        } catch (java.io.IOException ex) {
            throw new AssertionFailedErrorException("write method failed",ex);
        }
        System.out.println("sessio nConfig = "+webApp.getSingleSessionConfig());
        assertNotNull("Session Config mustn't be null :", webApp.getSingleSessionConfig());
        assertEquals("Incorrect dd status :",WebApp.STATE_VALID,webApp.getStatus());
        assertNull("Error must be null :", webApp.getError());
        assertNotNull("Session Timeout mustn't be null :", webApp.getSingleSessionConfig().getSessionTimeout());
    }
    
    public void test_CheckEvents () {
        System.out.println("TEST:Property Change Events");
        assertEquals("Incorrect number of PCE :",expectedEvents.length,evtList.size());
        for (int i=0;i<expectedEvents.length;i++) {
            assertEquals("Incorrect PCE["+i+"] :",expectedEvents[i],evtList.get(i));
        }
    }
     
    private static FileObject fo;
    
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("setUp() .......................");
        
        if (fo==null) {
            File dataDir = new File(System.getProperty("test.data.dir")+"/invalid");
            FileObject dataFolder = FileUtil.toFileObject(dataDir);
            fo = dataFolder.getFileObject("web","xml");
        };
        try {
            webApp = DDProvider.getDefault().getDDRoot(fo);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        assertTrue("WebApp object not found", null != webApp);
        list = new MyListener(webApp);
        webApp.addPropertyChangeListener(list);
    }
    
    protected void tearDown() {
        webApp.removePropertyChangeListener(list);
    }
    
    private MyListener list;
    private static class MyListener implements PropertyChangeListener {
        WebApp webApp;
        MyListener (WebApp webApp) {
            this.webApp=webApp;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("propertyChanged() "+evt.getPropertyName()+":"+evt.getOldValue()+":"+evt.getNewValue());
            evtList.add("PCE:STATUS["+webApp.getStatus()+"]:"+getDDProperty(evt.getPropertyName())+":"+evt.getOldValue()+":"+evt.getNewValue());
        }
    }
    
    private static String getDDProperty(String fullName) {
	int index = fullName.lastIndexOf('/');
        return (index>0?fullName.substring(index+1):fullName);
    }
}
