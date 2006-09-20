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

package org.netbeans.modules.modulemanagement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.sendopts.CommandLine;
import org.netbeans.junit.NbTestCase;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author Jaroslav Tulach
 */
public class EnableDisableTest extends NbTestCase {
    private Logger LOG;
    
    public EnableDisableTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        clearWorkDir();
        
        LOG = Logger.getLogger("TEST-" + getName());

        System.setProperty("netbeans.user", getWorkDirPath() + File.separator + "userdir");

        // initialize whole infra
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    protected void tearDown() throws Exception {
    }
    
    protected Level logLevel() {
        return Level.FINE;
    }
    
    public void testAModuleIsPrinted() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        CommandLine.getDefault().process(new String[] { "--listmodules" }, System.in, os, err, new File("."));

        assertEquals("No error", 0, err.size());

        if (os.toString().indexOf("org.netbeans.bootstrap") < 0) {
            fail("We want default module to be found: " + os.toString());
        }
        if (os.toString().indexOf("org.my.module") != -1) {
            fail("module not found yet: " + os.toString());
        }

        Manifest m = new Manifest ();
        m.getMainAttributes ().putValue (java.util.jar.Attributes.Name.MANIFEST_VERSION.toString (), "1.0");
        m.getMainAttributes ().putValue ("OpenIDE-Module", "org.my.module/3");
        File simpleJar = generateJar (new String[0], m);

        CommandLine.getDefault().process(new String[] { "--installmodules", simpleJar.toString() }, System.in, os, err, new File("."));

        assertEquals("No error2", 0, err.size());

        os.reset();
        CommandLine.getDefault().process(new String[] { "--listmodules" }, System.in, os, err, new File("."));

        assertEquals("No error", 0, err.size());

        if (os.toString().indexOf("org.my.module") < 0) {
            fail("our module should be found: " + os.toString());
        }

        os.reset();
        
        Thread.sleep(1000);
        
        LOG.info("About to disable module");
        CommandLine.getDefault().process(new String[] { "--disablemodules", "org.my.module"}, System.in, os, err, new File("."));
        assertEquals("No error", 0, err.size());
        LOG.info("Disabling done");

        os.reset();
        CommandLine.getDefault().process(new String[] { "--listmodules" }, System.in, os, err, new File("."));
        if (os.toString().indexOf("org.my.module") < 0) {
            fail("our module should be found: " + os.toString());
        }

        Matcher mac = Pattern.compile("org.my.module/3 *disabled").matcher(os.toString());
        if (!mac.find()) {
            fail("and should be disabled now: " + os.toString());
        }

        Thread.sleep(1000);
        
        os.reset();
        CommandLine.getDefault().process(new String[] { "--enablemodules", "org.my.module"}, System.in, os, err, new File("."));
        assertEquals("No error", 0, err.size());

        os.reset();
        CommandLine.getDefault().process(new String[] { "--listmodules" }, System.in, os, err, new File("."));
        if (os.toString().indexOf("org.my.module") < 0) {
            fail("our module should be found: " + os.toString());
        }

        mac = Pattern.compile("org.my.module/3 *enabled").matcher(os.toString());
        if (!mac.find()) {
            fail("and should be disabled now: " + os.toString());
        }

    }

    protected final File generateJar (String[] content, Manifest manifest) throws IOException {
        File f;
        int i = 0;
        for (;;) {
            f = new File (this.getWorkDir(), i++ + ".jar");
            if (!f.exists ()) break;
        }
        
        JarOutputStream os;
        if (manifest != null) {
            os = new JarOutputStream (new FileOutputStream (f), manifest);
        } else {
            os = new JarOutputStream (new FileOutputStream (f));
        }
        
        for (i = 0; i < content.length; i++) {
            os.putNextEntry(new JarEntry (content[i]));
            os.closeEntry();
        }
        os.closeEntry ();
        os.close();
        
        return f;
    }
}
