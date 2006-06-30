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

package org.netbeans.modules.vcscore.turbo.local;

import junit.framework.TestCase;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.AbstractLookup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

import java.io.File;

/**
 * Tests FileAttributeQuery
 *
 * @author Petr Kuzel
 */
public class FileAttributeQueryTest extends TestCase {

    private static InstanceContent content = new InstanceContent();

    private FileSystem fs;

    private static TestEnvironment env;

    // per testcase static setup
    static {
        env = new TestEnvironment();
        FileAttributeQuery.initEnvironment(env);
    }

    // called before every method
    protected void setUp() throws Exception {

        // prepare simple LFS

        LocalFileSystem fs = new LocalFileSystem();
        File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + "faq-test");
        tmp.mkdir();
        tmp.deleteOnExit();
        File theFile = new File(tmp, "theFile");
        theFile.createNewFile();
        theFile.deleteOnExit();
        fs.setRootDirectory(tmp);

        this.fs = fs;
    }

    /**
     * Once setting an atribute it must be in memory until
     * file reference is GCed. Providers must be queried.
     * Th event must be fired.
     */
    public void testHitAfterSet() throws Exception {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();

        FileObject fo = fs.getRoot().getFileObject("theFile");
        AttributeListener l = new AttributeListener();
        faq.addFileAttributeListener(l);

        faq.writeAttribute(fo, "test", "testValue");
        assertTrue("Missing FAQ event", l.hit());

        // must not raise fail on providers read layer
        faq.readAttribute(fo, "test");

        // it shoud not fire on equel values
        l.reset();
        faq.writeAttribute(fo, "test", "testValue");
        assertFalse("Missing FAQ event", l.hit());
    }

    /**
     * <code<null</code> value serves for invalidation purposes.
     */
    public void testInvalidation() throws Exception {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();

        FileObject fo = fs.getRoot().getFileObject("theFile");

        faq.writeAttribute(fo, "invalidate", "testValue");
        faq.writeAttribute(fo, "invalidate", null);
        assertFalse(faq.isPrepared(fo, "invalidate"));

        // after reading attribute we know that is unknown, it's prepared
        faq.readAttribute(fo, "invalidate");
        assertTrue(faq.isPrepared(fo, "invalidate"));
    }

    private static class TestEnvironment extends FileAttributeQuery.Environment {

        private Lookup l;

        public TestEnvironment() {
            content.add(new AssertingFileAttributeProvider());
            l = new AbstractLookup(content);
        }

        public Lookup getLookup() {
            return l;
        }
    }

    private static class AssertingFileAttributeProvider implements FileAttributeProvider {

        public boolean recognizesAttribute(String name) {
            return "test".equals(name);
        }

        public boolean recognizesFileObject(FileObject fo) {
            return true;
        }

        public Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {
            fail();
            return null;
        }

        public boolean writeAttribute(FileObject fo, String name, Object value) {
            return true;
        }
    }

    private class AttributeListener implements FileAttributeListener {

        private volatile boolean hit = false;

        public void reset() {
            hit = false;
        }

        public boolean hit() {
            return hit;
        }

        public synchronized void attributeChange(FileObject fo, String name, Object value) {
            hit = true;
            notifyAll();
        }

        public synchronized void waitForHit(int timeout) {
            long start = System.currentTimeMillis();
            while (hit == false) {
                try {
                    wait(timeout);
                    if (System.currentTimeMillis() - start > timeout) return;
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }


    /**
     * Test provider that so slow that someone else populates memory faster.
     */
    public void testSlowProvider() throws Exception {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();

        FileObject fo = fs.getRoot().getFileObject("theFile");

        SlowProvider sp = new SlowProvider();
        content.add(sp);
        try {
            faq.prepareAttribute(fo, "slow"); // hey a thread is spawned to load attribute on background

            // simulate faster client

            AttributeListener expecting = new AttributeListener();
            faq.addFileAttributeListener(expecting);
            faq.writeAttribute(fo, "slow", "result"); // expecting synchronous event
            faq.removeFileAttributeListener(expecting);
            assertTrue(expecting.hit());

            // now unblock provider and let it report the same value

            AttributeListener besilent = new AttributeListener();
            faq.addFileAttributeListener(besilent);
            sp.notifyReady(); // an event must not be fired because of the same value
            besilent.waitForHit(100);
            faq.removeFileAttributeListener(besilent);

            // TODO it's safer to fire than to not fire it's just possibly slow
            //assertFalse(besilent.hit());
        } finally {
            content.remove(sp);
        }
    }

    private class SlowProvider implements FileAttributeProvider {

        volatile boolean ready;

        public boolean recognizesAttribute(String name) {
            return "slow".equals(name);
        }

        public boolean recognizesFileObject(FileObject fo) {
            return true;
        }

        public synchronized  Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {
            while (ready == false) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    break;
                }
            }
            return "result";
        }

        public boolean writeAttribute(FileObject fo, String name, Object value) {
            // XXX Note this is coming as result of faster FAQ.writeAttribute
            return true;
        }

        public synchronized void notifyReady() {
            ready = true;
            notifyAll();
        }
    }


    /**
     * Prepare must fire event on successfull prepare
     * XXX yet undefined behaviour otherwise.
     */
    public void testPrepare() throws Exception {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FileObject fo = fs.getRoot().getFileObject("theFile");
        PrepareProvider pp = new PrepareProvider();
        content.add(pp);
        try {

            AttributeListener l = new AttributeListener();
            faq.addFileAttributeListener(l);
            faq.prepareAttribute(fo, "prepare");
            l.waitForHit(500);
            faq.removeFileAttributeListener(l);

            assertTrue(l.hit);

            // be sure that event granularity is given by attr,file pair (not just by file)

            l.reset();
            faq.addFileAttributeListener(l);
            faq.prepareAttribute(fo, "prepare2");
            l.waitForHit(500);
            faq.removeFileAttributeListener(l);

            assertTrue(l.hit);
        } finally {
            content.remove(pp);
        }
    }

    private class PrepareProvider implements FileAttributeProvider {

        public boolean recognizesAttribute(String name) {
            return "prepare".equals(name) || "prepare2".equals(name);
        }

        public boolean recognizesFileObject(FileObject fo) {
            return true;
        }

        public Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {
            return "done";
        }

        public boolean writeAttribute(FileObject fo, String name, Object value) {
            return true;
        }
    }

    /**
     * Register new provider and verify that it's called
     * without throwing an exception.
     */
    public void testWrongProvider() {
        WrongProvider provider = new WrongProvider();

        Lookup.Template t = new Lookup.Template(FileAttributeProvider.class);
        Lookup.Result r = env.getLookup().lookup(t);
        content.add(provider);
        assert r.allInstances().size() == 2 : "r=" + r.allInstances();

        FileAttributeQuery faq = FileAttributeQuery.getDefault();

        FileObject fo = fs.getRoot().getFileObject("theFile");

        try {
            assertNull(faq.readAttribute(fo, "wrong"));
        } catch (RuntimeException ex) {
            fail("WrongProvider crashed framework");
        }

        assertTrue("Provider registration was missed", provider.queried);

        content.remove(provider);
    }

    private class WrongProvider implements FileAttributeProvider {

        volatile boolean queried;

        public boolean recognizesAttribute(String name) {
            queried = true;
            return "wrong".equals(name);
            // throw new RuntimeException(); XXX too early
        }

        public boolean recognizesFileObject(FileObject fo) {
            return true;
        }

        public Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {
            throw new RuntimeException();
        }

        public boolean writeAttribute(FileObject fo, String name, Object value) {
            throw new RuntimeException();
        }
    }

    /** Test how keys handle FileObject identity problems. */
    public void testMemoryKeys() throws Exception{
        FileObject overlap = fs.getRoot().createFolder("nestedFS2");
        try {
            LocalFileSystem ofs = new LocalFileSystem();
            ofs.setRootDirectory(FileUtil.toFile(overlap));

            ofs.getRoot().createData("data.txt");

            FileObject f1 = fs.findResource("nestedFS2/data.txt");
            FileObject f2 = ofs.findResource("data.txt");

            Memory.FileObjectKey k1 = new Memory.FileObjectKey(f1);
            Memory.FileObjectKey k2 = new Memory.FileObjectKey(f2);

            assertTrue(k1.equals(k2));

            k1.makeWeak();
            assertTrue(k1.equals(k2));

            k2.makeWeak();
            assertTrue(k1.equals(k2));

        } finally {
            overlap.delete(overlap.lock());
        }
    }

    /**
     * Netbeans fileobjects have problems with identity.
     * Two fileobject representing same file are not guaranteed to be equivalent.
     * Known causes: MasterFS fileobject and fileobject from wrapped
     * filesystem (it can be spotted only by FS impl). Overlapping
     * LocalFilesystems.
     * <p>
     * It uncovered Memory.FileObjectKey.hashCode problem!
     */
    public void testDuplicatedFileObject() throws Exception {
        FileObject overlap = fs.getRoot().createFolder("nestedFS");
        try {
            LocalFileSystem ofs = new LocalFileSystem();
            ofs.setRootDirectory(FileUtil.toFile(overlap));

            ofs.getRoot().createData("data.txt");

            FileObject f1 = fs.findResource("nestedFS/data.txt");
            FileObject f2 = ofs.findResource("data.txt");
            assert f1 != f2;
            assert f1.equals(f2) == false;


            FileAttributeQuery faq = FileAttributeQuery.getDefault();

            faq.writeAttribute(f1, "identity", "clash");

            Object v1 = faq.readAttribute(f1, "identity");
            Object v2 = faq.readAttribute(f2, "identity");

            assertTrue("clash".equals(v1));
            assertTrue("Unexpected value:" + v2, "clash".equals(v2));

            faq.writeAttribute(f2, "identity", "over!");

            v1 = faq.readAttribute(f1, "identity");
            v2 = faq.readAttribute(f2, "identity");

            assertTrue("over!".equals(v1));
            assertTrue("Unexpected value:" + v2, "over!".equals(v2));

        } finally {
            overlap.delete(overlap.lock());
        }
    }

}
