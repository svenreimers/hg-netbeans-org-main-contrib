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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex;


import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import junit.framework.Assert;
import org.netbeans.api.lexer.Language;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.editor.bibtex.BiBTeXLanguage;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.impl.NBUtilities;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.xml.sax.SAXException;

/**Inspired by org.netbeans.api.project.TestUtil.
 *
 * @author Jan Lahoda
 */
public class UnitUtilities extends ProxyLookup {
    
    public static UnitUtilities DEFAULT_LOOKUP = null;
    
    public UnitUtilities() {
        Assert.assertNull(DEFAULT_LOOKUP);
        DEFAULT_LOOKUP = this;
    }
    
    /**
     * Set the global default lookup with some fixed instances including META-INF/services/*.
     */
    public static void setLookup(Object[] instances, ClassLoader cl) {
        DEFAULT_LOOKUP.setLookups(new Lookup[] {
            Lookups.fixed(instances),
            Lookups.metaInfServices(cl),
            Lookups.singleton(cl),
        });
    }
    
    public static void prepareTest(String[] additionalLayers, Object[] additionalLookupContent) throws IOException, SAXException, PropertyVetoException {
        URL[] layers = new URL[additionalLayers.length + 1];
        
        layers[0] = Utilities.class.getResource("/org/netbeans/modules/latex/model/resources/mf-layer.xml");
        
        for (int cntr = 0; cntr < additionalLayers.length; cntr++) {
            layers[cntr + 1] = Utilities.class.getResource(additionalLayers[cntr]);
        }
        
        XMLFileSystem system = new XMLFileSystem();
        system.setXmlUrls(layers);
        FileSystem fs = new MFSImpl(new FileSystem[] {FileUtil.createMemoryFileSystem(), system});
        
        Repository repository = new Repository(fs);
        Object[] lookupContent = new Object[additionalLookupContent.length + 2];
        
        System.arraycopy(additionalLookupContent, 0, lookupContent, 2, additionalLookupContent.length);
        
        lookupContent[0] = repository;
        lookupContent[1] = new ModelUtilities();
        
        DEFAULT_LOOKUP.setLookup(lookupContent, Utilities.class.getClassLoader());
    }
    
    private static final class MFSImpl extends MultiFileSystem {
        public MFSImpl(FileSystem[] fs) {
            super(fs);
            try {
                setSystemName("A");
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    static {
        UnitUtilities.class.getClassLoader().setDefaultAssertionStatus(true);
        System.setProperty("org.openide.util.Lookup", UnitUtilities.class.getName());
        Assert.assertEquals(UnitUtilities.class, Lookup.getDefault().getClass());
    }
    
    public static void initLookup() {
        //currently nothing.
    }
    
    /**Copied from org.netbeans.api.project.
     * Create a scratch directory for tests.
     * Will be in /tmp or whatever, and will be empty.
     * If you just need a java.io.File use clearWorkDir + getWorkDir.
     */
    public static FileObject makeScratchDir(NbTestCase test) throws IOException {
        test.clearWorkDir();
        File root = test.getWorkDir();
        assert root.isDirectory() && root.list().length == 0;
        FileObject fo = FileUtil.toFileObject(root);
        if (fo != null) {
            // Presumably using masterfs.
            return fo;
        } else {
            // For the benefit of those not using masterfs.
            LocalFileSystem lfs = new LocalFileSystem();
            try {
                lfs.setRootDirectory(root);
            } catch (PropertyVetoException e) {
                assert false : e;
            }
            Repository.getDefault().addFileSystem(lfs);
            return lfs.getRoot();
        }
    }
    
    private static class ModelUtilities extends NBUtilities {
        
        public Object getFile(Document doc) {
            return ((DataObject) doc.getProperty(Document.StreamDescriptionProperty)).getPrimaryFile();
        }
        
        private Map/*<File,Document>*/ file2Document = null;
        
        public Document openDocument(Object obj) throws IOException {
            if (file2Document == null) {
                file2Document = new HashMap();
            }
            
            Document doc = (Document) file2Document.get(obj);
            
            if (doc != null)
                return doc;
            
            InputStream  fis = null;
            
            try {
                FileObject   file = (FileObject) obj;
                int          read;
                StringBuffer test = new StringBuffer();
                
                fis = file.getInputStream();
                
                while ((read = fis.read()) != (-1)) {
                    test.append((char) read);
                }
                
                try {
                    doc = new DefaultStyledDocument();//new PlainDocument();
                    
                    doc.insertString(0, test.toString(), null);
                    doc.putProperty(Document.StreamDescriptionProperty,  DataObject.find((FileObject) obj));
                    
                    if ("tex".equals(file.getExt())) {
                        doc.putProperty("mime-type", "text/x-tex");
                        doc.putProperty(Language.class, TexLanguage.description());
                    }
                    
                    if ("bib".equals(file.getExt())) {
                        doc.putProperty("mime-type", "text/x-bibtex");
                        doc.putProperty(Language.class, BiBTeXLanguage.description());
                    }
                    
                    file2Document.put(obj, doc);
                    return doc;
                } catch (BadLocationException e) {
                    System.err.println("Should !never! happen:");
                    e.printStackTrace();
                    
                    return null;
                }
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            }
        }
        
    }
}
