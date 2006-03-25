/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.loaderswitcher;

import java.io.IOException;
import java.util.logging.Level;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.*;

/** Checks the functional behaviour of the Object Type Switcher.
 *
 * @author Jaroslav Tulach
 */
public class ObjectTypeTest extends NbTestCase
implements DataLoader.RecognizedFiles {
    
    public ObjectTypeTest(String testName) {
        super(testName);
    }

    protected Level logLevel() {
        return Level.ALL;
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testFindAndSwitch() throws Exception {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fnd = fs.getRoot().createFolder("testFindAndSwitch");
        
        DataObject obj = InstanceDataObject.create(DataFolder.findFolder(fnd), "ahoj", java.awt.FlowLayout.class);
        
        DataLoader[] x = ObjectType.findPossibleLoaders(obj, this);
        
        assertEquals("Two loaders shall be interested", 2, x.length);
        assertEquals("First one is the actual loader", obj.getLoader(), x[0]);

        assertEquals("Name", "ahoj", obj.getName());

        ObjectType.convertTo(obj, x[1]);

        assertFalse("Old object is invalidated", obj.isValid());

        DataObject n = DataObject.find(obj.getPrimaryFile());

        if (n == obj) {
            fail("They should be different: " + n);
        }

        assertEquals("The right loader", x[1], n.getLoader());


        assertEquals("Name with extension", "ahoj.instance", n.getName());

        n.rename("kuk.unknown");

        DataLoader[] arr = ObjectType.findPossibleLoaders(n, this);
        assertEquals("Just one loader now", 1, arr.length);
        assertEquals("and it is the default loader", n.getLoader(), arr[0]);

    }

    public void markRecognized(FileObject fo) {
    }
}
