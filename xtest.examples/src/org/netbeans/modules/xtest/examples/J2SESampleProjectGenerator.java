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

package org.netbeans.modules.xtest.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

import org.openide.modules.InstalledFileLocator;
import org.openide.filesystems.FileLock;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * Create a sample web project by unzipping a template into some directory
 *
 * @author Martin Grebac, Tomas Zezula
 */
public class J2SESampleProjectGenerator {

    private static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/j2se-project/2";   //NOI18N

    private J2SESampleProjectGenerator() {}


    public static FileObject createProjectFromTemplate(final FileObject template, File projectLocation, final String name) throws IOException {
        FileObject prjLoc = null;
        if (template.getExt().endsWith("zip")) {  //NOI18N
            unzip(template.getInputStream(), projectLocation);
            updateXTestProperties(projectLocation, name);
            try {
                // update project.xml
                prjLoc = FileUtil.toFileObject(projectLocation);
                File projXml = FileUtil.toFile(prjLoc.getFileObject(AntProjectHelper.PROJECT_XML_PATH));
                Document doc = XMLUtil.parse(new InputSource(projXml.toURI().toString()), false, true, null, null);
                NodeList nlist = doc.getElementsByTagNameNS(PROJECT_CONFIGURATION_NAMESPACE, "name");       //NOI18N
                if (nlist != null) {
                    for (int i=0; i < nlist.getLength(); i++) {
                        Node n = nlist.item(i);
                        if (n.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }
                        Element e = (Element)n;
                        
                        replaceText(e, name);
                    }
                    saveXml(doc, prjLoc, AntProjectHelper.PROJECT_XML_PATH);                    
                    //update private properties
                    File privateProperties = createPrivateProperties (prjLoc);
                    //No need to load the properties the file is empty
                    Properties p = new Properties ();                    
                    p.put ("javadoc.preview","true");   //NOI18N
                    FileOutputStream out = new FileOutputStream (privateProperties);
                    try {
                        p.store(out,null);                    
                    } finally {
                        out.close ();
                    }
                }                
                
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
            prjLoc.refresh(false);
        }
        return prjLoc;
    }
    
    private static void unzip(InputStream source, File targetFolder) throws IOException {
        //installation
        ZipInputStream zip=new ZipInputStream(source);
        try {
            ZipEntry ent;
            while ((ent = zip.getNextEntry()) != null) {
                File f = new File(targetFolder, ent.getName());
                if (ent.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(f);
                    try {
                        FileUtil.copy(zip, out);
                    } finally {
                        out.close();
                    }
                }
            }
        } finally {
            zip.close();
        }
    }
    
    private static File createPrivateProperties (FileObject fo) throws IOException {
        String[] nameElements = AntProjectHelper.PRIVATE_PROPERTIES_PATH.split("/");
        for (int i=0; i<nameElements.length-1; i++) {
            FileObject tmp = fo.getFileObject (nameElements[i]);
            if (tmp == null) {
                tmp = fo.createFolder(nameElements[i]);
            }
            fo = tmp;
        }
        fo = fo.createData(nameElements[nameElements.length-1]);
        return FileUtil.toFile(fo);
    }

    /**
     * Extract nested text from an element.
     * Currently does not handle coalescing text nodes, CDATA sections, etc.
     * @param parent a parent element
     */
    private static void replaceText(Element parent, String name) {
        NodeList l = parent.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getNodeType() == Node.TEXT_NODE) {
                Text text = (Text)l.item(i);
                text.setNodeValue(name);
                return;
            }
        }
    }
    
    /**
     * Save an XML config file to a named path.
     * If the file does not yet exist, it is created.
     */
    private static void saveXml(Document doc, FileObject dir, String path) throws IOException {
        FileObject xml = FileUtil.createData(dir, path);
        FileLock lock = xml.lock();
        try {
            OutputStream os = xml.getOutputStream(lock);
            try {
                XMLUtil.write(doc, os, "UTF-8"); // NOI18N
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    /** Updates properties xtest.home, xtest.module and netbeans.dest.dir in
     * test/build.xml script.
     */
    private static void updateXTestProperties(File projectLocation, String projectName) throws IOException {
        try {
            FileObject prjLoc = FileUtil.toFileObject(projectLocation);
            String filePath = "test/build.xml";  // NOI18N
            File buildXml = FileUtil.toFile(prjLoc.getFileObject(filePath));
            Document doc = XMLUtil.parse(new InputSource(buildXml.toURI().toString()), false, true, null, null);
            NodeList nlist = doc.getElementsByTagName("property");  //NOI18N
            for (int i=0; i < nlist.getLength(); i++) {
                Element e = (Element)nlist.item(i);
                if(e.getAttribute("name").equals("xtest.home")) { // NOI18N
                    File xtestHome = InstalledFileLocator.getDefault().
                            locate("xtest-distribution", "org.netbeans.modules.xtest", false);  // NOI18N
                    e.setAttribute("location", xtestHome.getAbsolutePath());// NOI18N
                }
                if(e.getAttribute("name").equals("xtest.module")) {  // NOI18N
                    e.setAttribute("value", projectName); // NOI18N
                }
                if(e.getAttribute("name").equals("netbeans.dest.dir")) { // NOI18N
                    File netbeansDestDir = InstalledFileLocator.getDefault().
                            locate("core/core.jar", null, false);  // NOI18N
                    netbeansDestDir = new File(netbeansDestDir, "../../..");
                    e.setAttribute("location", netbeansDestDir.getAbsolutePath());// NOI18N
                }
                if(e.getAttribute("name").equals("jemmy.path")) { // NOI18N
                    File jemmyJar = InstalledFileLocator.getDefault().
                            locate("modules/ext/jemmy.jar", "org.netbeans.modules.jemmy", false);  // NOI18N
                    e.setAttribute("location", jemmyJar.getAbsolutePath());// NOI18N
                }
            }
            saveXml(doc, prjLoc, filePath);                    
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
}
