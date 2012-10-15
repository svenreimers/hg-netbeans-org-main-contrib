/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.ada.editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.text.DateFormat;
import java.util.HashMap;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.filesystems.FileObject;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.FileEntry;
import org.openide.loaders.UniFileLoader;
import org.openide.modules.InstalledFileLocator;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.CreateFromTemplateAttributesProvider;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * DataLoader for recognizing C/C++/Fortran (C-C-F) source files.
 *
 * It also defines an inner class, AdaFormat, whose derived classes are
 * used to format template files (e.g. substitute values for parameters such as
 * __FILENAME__, __NAME__, __DATE__, __TIME__, __USER__, __GUARD_NAME etc.).
 */
public abstract class SourceAbstractDataLoader extends UniFileLoader {

    protected SourceAbstractDataLoader(String representationClassName) {
        super(representationClassName);
    }

    protected abstract String getMimeType();

    @Override
    protected final void initialize() {
        super.initialize();
        getExtensions().addMimeType(getMimeType());
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        // Entry for the important file: by default, is preserved
        // during all operations.
        return new AdaFormat(obj, primaryFile);
    }

    @Override
    protected String actionsContext() {
        return "Loaders/text/x-ada+sourcefile/Actions/"; // NOI18N
    }

// Inner class: Substitute important template parameters...
    /*package*/
    private static class AdaFormat extends FileEntry.Format {

        public AdaFormat(MultiDataObject obj, FileObject primaryFile) {
            super(obj, primaryFile);
        }

        @Override
        protected java.text.Format createFormat(FileObject target, String name, String ext) {

            Map<String, Object> map = new HashMap<String, Object>();

            String packageName = target.getPath().replace('/', '_');
            // add an underscore to the package name if it is not an empty string
            if (!packageName.isEmpty()) { // NOI18N
                packageName = packageName + "_"; // NOI18N
            }
            map.put("PACKAGE_AND_NAME", packageName + name); // NOI18N
            map.put("NAME", name); // NOI18N
            map.put("EXTENSION", ext); // NOI18N
//            String guardName = (name + "_" + ext).replace('-', '_').replace('.', '_'); // NOI18N
            String fullName = name + "_" + ext; //NOI18N
            StringBuilder guardName = new StringBuilder();
            for (int i = 0; i < fullName.length(); i++) {
                char c = fullName.charAt(i);
                guardName.append(Character.isJavaIdentifierPart(c) ? Character.toUpperCase(c) : '_');
            }

            map.put("GUARD_NAME", guardName.toString()); // NOI18N
            /*
            This is a ugly hack but I don't have a choice. That's because
            NetBeans will not pass me the name the user typed in as the
            "root" name; instead I get the substituted name for each
            template file. In other words, suppose I use the parameter
            __NAME__ in my template source files. The name passed to
            createFormat is then the *filename* instead of just the
            Name: field the user had entered. e.g. if I'm instantiating the
            following files:
            __sample___foo.cc
            __sample___bar.cc
            Then for the first file, __NAME__ becomes <myname>_foo and in
            the second file, __NAME__ becomes <myname>_bar. But I really
            need the Name itself, so that I can for example have have
            #include "<myname>_externs.h"
            in the templates!
             */

            int crop = name.lastIndexOf('_');
            if (crop != -1) {
                name = name.substring(0, crop);
            }
            map.put("CROPPEDNAME", name);  // NOI18N
            map.put("DATE", DateFormat.getDateInstance // NOI18N
                    (DateFormat.LONG).format(new Date()));
            map.put("TIME", DateFormat.getTimeInstance // NOI18N
                    (DateFormat.SHORT).format(new Date()));
            //	    map.put("USER", System.getProperty("user.name"));	// NOI18N
            String nbHome = null; //System.getProperty("netbeans.home");
            File file = InstalledFileLocator.getDefault().locate("lib", null, false); // NOI18N
            if (file != null) {
                nbHome = file.getParent();
            }
            if (nbHome == null) {
                nbHome = "";
            }
            map.put("NBDIR", nbHome); // NOI18N
            map.put("QUOTES", "\""); // NOI18N

            for (CreateFromTemplateAttributesProvider provider :
                    Lookup.getDefault().lookupAll(CreateFromTemplateAttributesProvider.class)) {
                Map<String, ?> attrs = provider.attributesFor(getDataObject(),
                        DataFolder.findFolder(target), name);
                if (attrs != null) {
                    for (Map.Entry<String, ?> entry : attrs.entrySet()) {
                        map.put(entry.getKey().toUpperCase(), entry.getValue());
                    }
                }
            }

            org.openide.util.MapFormat format = new org.openide.util.MapFormat(map);

            // Use "%<%" and "%>%" instead of "__" (which most other templates
            // use) since "__" is used for many C++ tokens and we don't want
            // any conflicts with valid code. For example, __FILE__ is a valid
            // construct in Sun C++ files and the compiler will replace the
            // current file name during compilation.
            format.setLeftBrace("%<%"); // NOI18N
            format.setRightBrace("%>%"); // NOI18N
            return format;
        }

        /**
         * Creates dataobject from template. Copies the file and applies
         * substitutions provided by the createFormat method.
         *
         * Overriding parent implementation to add encoding conversion (IZ 163832).
         * Copied with minor modifications from IndentFileEntry (java.source module).
         *
         * @param f  the folder to create instance in
         * @param name  name of the file or null if it should be chosen automatically
         * @return  created file
         * @throws java.io.IOException
         */
        @Override
        public FileObject createFromTemplate(FileObject f, String name) throws IOException {
            // we don't want extension to be taken from template filename for our customized dialog
            String ext;

            // use default
            ext = getFile().getExt();

            FileObject fo = f.createData(name, ext);

            java.text.Format frm = createFormat(f, name, ext);

            EditorKit kit = createEditorKit(getFile().getMIMEType());
            Document doc = kit.createDefaultDocument();

            BufferedReader r = new BufferedReader(new InputStreamReader(
                    getFile().getInputStream(), FileEncodingQuery.getEncoding(getFile())));
            try {
                FileLock lock = fo.lock();
                try {
                    Charset encoding = FileEncodingQuery.getEncoding(fo);
                    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                            fo.getOutputStream(lock), encoding));
                    try {
                        String current;
                        String line = null;
                        int offset = 0;
                        while ((current = r.readLine()) != null) {
                            if (line != null) {
                                doc.insertString(offset, "\n", null); // NOI18N
                                offset++;
                            }
                            line = frm.format(current);
                            doc.insertString(offset, line, null);
                            offset += line.length();
                        }
                        doc.insertString(doc.getLength(), "\n", null); // NOI18N
                        offset++;
                        Reformat reformat = Reformat.get(doc);
                        reformat.lock();
                        try {
                            reformat.reformat(0, doc.getLength());
                        } finally {
                            reformat.unlock();
                        }
                        w.write(doc.getText(0, doc.getLength()));
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        w.close();
                    }
                } finally {
                    lock.releaseLock();
                }
            } finally {
                r.close();
            }

            // copy attributes
            FileUtil.copyAttributes(getFile(), fo);

            // unmark template state
            setTemplate(fo, false);

            return fo;
        }
    }

    private static EditorKit createEditorKit(String mimeType) {
        EditorKit kit;
        kit = JEditorPane.createEditorKitForContentType(mimeType);
        if (kit == null) {
            kit = new javax.swing.text.DefaultEditorKit();
        }
        return kit;
    }

    private static boolean setTemplate(FileObject fo, boolean newTempl) throws IOException {
        boolean oldTempl = false;

        Object o = fo.getAttribute(DataObject.PROP_TEMPLATE);
        if ((o instanceof Boolean) && ((Boolean) o).booleanValue()) {
            oldTempl = true;
        }
        if (oldTempl == newTempl) {
            return false;
        }

        fo.setAttribute(DataObject.PROP_TEMPLATE, (newTempl ? Boolean.TRUE : null));

        return true;
    }

}
