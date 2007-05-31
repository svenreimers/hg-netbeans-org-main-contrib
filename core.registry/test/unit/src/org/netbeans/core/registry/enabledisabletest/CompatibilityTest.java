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

package org.netbeans.core.registry.enabledisabletest;

import junit.textui.TestRunner;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.core.registry.TestMFS;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataObject;
import org.openide.loaders.FolderLookup;
import org.openide.util.Lookup;
import org.xml.sax.SAXException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;

public class CompatibilityTest extends NbTestCase  {
    private static final String LAYER = "data/compatible_layer.xml";
    private FileSystem mfs;

    private static final String DOC_NAME = "module";    
    private static Context docElementCtx;
    
    private static Exception moduleInitExc;

    static {
        try {
            ModuleUtils.DEFAULT.install();
            ModuleUtils.DEFAULT.enableBookModule(true);
            ModuleUtils.DEFAULT.enableCDModule(true);                    
        } catch (Exception e) {
            moduleInitExc = e;
        }
    }
    
    public CompatibilityTest(String s) {
        super(s);
    }
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(CompatibilityTest.class));
    }

    public void testMethodValue1 () throws Exception {
        getAndTestObject("methodvalue1");
    }

    public void testMethodValue2 () throws Exception {
        getAndTestObject("methodvalue2");
        
    }

    public void testMethodValue3 () throws Exception {
        Object book = getAndTestObject("methodvalue3");        
        
        assertTrue("Author should be Tom. See: " + book.toString(),book.toString().indexOf("Tom") > 0);
        assertTrue("Title should be Sorry.See: " + book.toString(),book.toString().indexOf("Sorry") > 0);
    }
    
    public void testNewValue1 () throws Exception {
        getAndTestObject("newvalue1");        
    }

    public void testBookTest () throws Exception {
        getAndTestObject("bookTest");
    }

    
    private Object getAndTestObject(final String bindingName) throws Exception {
        if (moduleInitExc != null)
            throw moduleInitExc;
        Object retVal = getContext().getSubcontext(bindingName).getObject(bindingName, null);
        assertNotNull(retVal);

        FileObject fileObject = getFileObject(bindingName);
        DataObject dataObject = DataObject.find(fileObject);
        assertNotNull(dataObject);

        InstanceCookie cookie = (InstanceCookie)dataObject.getCookie(InstanceCookie.class);
        assertNotNull(cookie);
        
        Object instance = cookie.instanceCreate();
        assertNotNull(instance);

        Lookup lkp = new FolderLookup(dataObject.getFolder()).getLookup();
        Object lookupItem = lkp.lookup(instance.getClass());
        assertNotNull(lookupItem);
        assertTrue(lookupItem == instance);
        
        //TODO: uncomment - partly tests InstanceDataObject, Lookup, Registry coexistence
        //assertTrue(instance == retVal);
        //assertTrue(lookupItem == retVal);        
                        
        return retVal;        
    }


    private Context getContext() throws PropertyVetoException, IOException, SAXException {
        if (docElementCtx == null) {            
            docElementCtx = SpiUtils.createContext(FileSystemContextFactory.createContext(getFileSystem().getRoot()));
            docElementCtx = docElementCtx.getSubcontext(DOC_NAME);
        }

        return docElementCtx;
    }

    private FileSystem getFileSystem() throws PropertyVetoException, IOException, SAXException {
        if (mfs == null) {
            LocalFileSystem lfs = new LocalFileSystem();
            lfs.setRootDirectory(getWorkDir());
                
            URL u1 = getClass().getResource(LAYER);
                       
            FileSystem xfs1 = new XMLFileSystem( u1 );
            mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
        }
        return mfs;
    }

    
    private FileObject getFileObject(final String bindingName) throws PropertyVetoException, IOException, SAXException {
        final String absolutePath = getContext().getAbsoluteContextName() + "/" + bindingName + "/" + bindingName;        
        final String [] extensions = new String [] {"settings", "instance", "xml"};

        FileObject fo = null;        
        for (int i = 0; i < extensions.length; i++) {
            String name = absolutePath + "." + extensions[i];
            fo = getFileSystem().findResource(name);
            if (fo != null) break;
        }
        
        assertNotNull(fo);        
        return fo;
    }    
}
