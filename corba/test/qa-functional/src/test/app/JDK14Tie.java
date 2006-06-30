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

package test.app;

import java.io.IOException;
import java.io.PrintStream;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestCase;
import org.openide.loaders.DataObject;
import test.app.AppGenerator;
import util.Environment;
import util.NameService;

public class JDK14Tie extends NbTestCase {

    public JDK14Tie(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new JDK14Tie("testJDK14Tie_Create"));
        test.addTest(new JDK14Tie("testJDK14Tie_RunNS"));
        test.addTest(new JDK14Tie("testJDK14Tie_RunFI"));
        test.addTest(new JDK14Tie("testJDK14Tie_RunIO"));
        return test;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    static AppGenerator app = null;
    static boolean done = false;
    
    public void testJDK14Tie_Create() {
        String work;
        try {
            work = Environment.replaceWinSeparator (getWorkDirPath());
        } catch (IOException e) {
            throw new AssertionFailedErrorException (e);
        }
        app = new AppGenerator (getRef (), getLog (), work);
        app.init ("JDK14", true, true, "data/app/jdk14tie/");
        DataObject daoCNS = app.doCNS ();
        app.doModifySource(daoCNS, new String[] {
            "addBefore", "ORB orb = ORB.init(args, null);", "args = new String[] { \"-ORBInitialPort\", \"1052\" };\n",
        });
        app.dumpFile (daoCNS);
        DataObject daoSNS = app.doSNS ();
        app.doModifySource(daoSNS, new String[] {
            "addBefore", "ORB orb = ORB.init(args, null);", "args = new String[] { \"-ORBInitialPort\", \"1052\" };\n",
        });
        app.dumpFile (daoSNS);
        app.dumpFile (app.doCFI ());
        app.dumpFile (app.doSFI ());
        app.dumpFile (app.doCIO ());
        app.dumpFile (app.doSIO ());
        app.doGenerateAndCompile();
        done = true;
    }
    
    public void testJDK14Tie_RunNS () {
        if (!done)
            return;
        NameService ns = new NameService (getRef ());
        ns.start (1052);
        app.setStreams (getRef (), getLog ());
        app.runNS ();
        ns.stop ();
        compareReferenceFiles();
    }
    
    public void testJDK14Tie_RunFI () {
        if (!done)
            return;
        app.setStreams (getRef (), getLog ());
        app.runFI ();
        compareReferenceFiles();
    }
    
    public void testJDK14Tie_RunIO () {
        if (!done)
            return;
        app.setStreams (getRef (), getLog ());
        app.runIO ();
        compareReferenceFiles();
    }
    
}
