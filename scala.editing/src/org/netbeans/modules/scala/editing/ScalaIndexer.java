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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.IndexDocument;
import org.netbeans.modules.gsf.api.IndexDocumentFactory;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.Importing;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.openide.filesystems.FileObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 * 
 * @author Caoyuan Deng
 */
public class ScalaIndexer implements Indexer {

    static final boolean PREINDEXING = Boolean.getBoolean("gsf.preindexing");
    // I need to be able to search several things:
    // (1) by function root name, e.g. quickly all functions that start
    //    with "f" should find unknown.foo.
    // (2) by namespace, e.g. I should be able to quickly find all
    //    "foo.bar.b*" functions
    // (3) constructors
    // (4) global variables, preferably in the same way
    // (5) extends so I can do inheritance inclusion!

    // Solution: Store the following:
    // class:name for each class
    // extend:old:new for each inheritance? Or perhaps do this in the class entry
    // fqn: f.q.n.function/global;sig; for each function
    // base: function;fqn;sig
    // The signature should look like this:
    // ;flags;;args;offset;docoffset;browsercompat;types;
    // (between flags and args you have the case sensitive name for flags)
    static final String FIELD_SIGNATURE = "signature"; //NOI18N
    static final String FIELD_QUALIFIED_NAME = "qualifiedName"; //NOI18N
    static final String FIELD_QUALIFIED_NAME_CASE_INSENSITIVE = "ciQualifiedName"; //NOI18N
    static final String FIELD_SIMPLE_NAME = "simpleName"; //NOI18N
    static final String FIELD_SIMPLE_NAME_CASE_INSENSITIVE = "ciSimpleName"; //NOI18N
    static final String FIELD_PACKAGE_NAME = "packageName"; //NOI18N
    /** Attributes: "i" -> private, "o" -> protected, ", "s" - static/notinstance, "d" - documented */
    static final String FIELD_ATTRIBUTES = "attributes"; //NOI18N
    static final String FIELD_EXTENDS_NAME = "extends"; //NOI18N
    static final String FIELD_IMPORT = "import"; //NOI18N
    private FileObject cachedFo;
    private boolean cachedIndexable;

    public String getIndexVersion() {
        return "6.119"; // NOI18N

    }

    public String getIndexerName() {
        return "scala"; // NOI18N

    }

    public boolean isIndexable(ParserFile file) {
        FileObject fo = file.getFileObject();
        if (fo == null) {
            /**
             * Not each kind of MIME files hava FileObject, for instance:
             * ParserFile with name as ".LCKxxxxx.erl~" etc will have none FileObject.
             */
            return false;
        }

        String extension = file.getExtension();

        double maxMemoryInMBs = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);
        if (extension.equals("scala")) {
            /**
             * @TODO: a bad hacking:
             * try to ignore these big files according to max memory size */
            double fileSizeInKBs = fo.getSize() / 1024.0;
            /**
             * 250M:  < 200KB
             * 500M:  < 400KB
             * 1500M: < 1200KB
             */
            double factor = (maxMemoryInMBs / 250.0) * 200;
            if (fileSizeInKBs > factor) {
                if (file.isPlatform()) {
                    //io.getErr().println("Indexing: " + fo.getPath() + " (skipped due to too big!)");
                }
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean acceptQueryPath(String url) {
        return url.indexOf("/ruby2/") == -1 && url.indexOf("/gems/") == -1 && url.indexOf("lib/ruby/") == -1; // NOI18N

    }

    public String getPersistentUrl(File file) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
            // Make relative URLs for urls in the libraries
            //return JsIndex.getPreindexUrl(url);
            return url;
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return file.getPath();
        }

    }

    private static int getModifiersFlag(Set<Modifier> modifiers) {
        int flags = modifiers.contains(Modifier.STATIC) ? IndexedElement.STATIC : 0;
        if (modifiers.contains(Modifier.PRIVATE)) {
            flags |= IndexedElement.PRIVATE;
        } else if (modifiers.contains(Modifier.PROTECTED)) {
            flags |= IndexedElement.PROTECTED;
        }

        return flags;
    }

    public File getPreindexedData() {
        return null;
    }
    private static FileObject preindexedDb;

    /** For testing only */
    public static void setPreindexedDb(FileObject preindexedDb) {
        ScalaIndexer.preindexedDb = preindexedDb;
    }

    public FileObject getPreindexedDb() {
        if (preindexedDb == null) {
            File preindexed = InstalledFileLocator.getDefault().locate(
                    "preindexed-scala", "org.netbeans.modules.scala.editing", false); // NOI18N

//            if (preindexed == null || !preindexed.isDirectory()) {
//                throw new RuntimeException("Can't locate preindexed directory. Installation might be damaged"); // NOI18N
//
//            }
//            preindexedDb = FileUtil.toFileObject(preindexed);
        }
        return preindexedDb;
    }

    public List<IndexDocument> index(ParserResult result, IndexDocumentFactory factory) throws IOException {
        ParserFile file = result.getFile();
        if (file.isPlatform()) {
            System.out.println("Platform file" + file.getNameExt());
        }

        ScalaParserResult pResult = (ScalaParserResult) result;
        AstScope root = pResult.getRootScope();
        if (root == null) { // NOI18N

            return null;
        }

        TreeAnalyzer analyzer = new TreeAnalyzer(pResult, factory);
        analyzer.analyze();

        return analyzer.getDocuments();
    }

    private static class TreeAnalyzer {

        private final ParserFile file;
        private String url;
        private final ScalaParserResult pResult;
        private IndexDocumentFactory factory;
        private List<IndexDocument> documents = new ArrayList<IndexDocument>();

        private TreeAnalyzer(ScalaParserResult pResult, IndexDocumentFactory factory) {
            this.pResult = pResult;
            this.file = pResult.getFile();
            this.factory = factory;
        }

        List<IndexDocument> getDocuments() {
            return documents;
        }

        public void analyze() throws IOException {
            FileObject fo = file.getFileObject();
            if (pResult.getInfo() != null) {
            } else {
                // openide.loaders/src/org/openide/text/DataEditorSupport.java
                // has an Env#inputStream method which posts a warning to the user
                // if the file is greater than 1Mb...
                //SG_ObjectIsTooBig=The file {1} seems to be too large ({2,choice,0#{2}b|1024#{3} Kb|1100000#{4} Mb|1100000000#{5} Gb}) to safely open. \n\
                //  Opening the file could cause OutOfMemoryError, which would make the IDE unusable. Do you really want to open it?
                // I don't want to try indexing these files... (you get an interactive
                // warning during indexing
                if (fo.getSize() > 1024 * 1024) {
                    return;
                }

            }

            try {
                url = fo.getURL().toExternalForm();

            // Make relative URLs for urls in the libraries
            //url = JsIndex.getPreindexUrl(url);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }


            AstScope root = pResult.getRootScope();
            if (root == null) {
                return;
            }

            List<AstElement> templates = new ArrayList<AstElement>();
            scan(root, templates);
            analyze(templates);

        //IndexDocument document = factory.createDocument(40); // TODO - measure!

        //documents.add(document);

        }

        private void scan(AstScope scope, List<AstElement> templates) {
            for (AstElement element : scope.getElements()) {
                if (element instanceof Template) {
                    templates.add(element);
                }
            }

            for (AstScope _scope : scope.getScopes()) {
                scan(_scope, templates);
            }
        }

        private void analyze(List<AstElement> templates) {
            for (AstElement element : templates) {
                if (element instanceof Template) {
                    analyzeTemplate((Template) element);
                }
            }
        }

        private void analyzeTemplate(Template template) {
            //int previousDocMode = docMode;
            try {
                int flags = 0;

                boolean nodoc = false;
                if (file.isPlatform() || PREINDEXING) {
                    // Should we skip this class? This is true for :nodoc: marked
                    // classes for example. We do NOT want to skip all children;
                    // in ActiveRecord for example we have this:
                    //    module ActiveRecord
                    //      module ConnectionAdapters # :nodoc:
                    //        module SchemaStatements
                    // and we definitely WANT to index SchemaStatements even though
                    // ConnectionAdapters is not there
//                    int newDocMode = RubyIndexerHelper.isNodocClass(element, doc);
//                    if (newDocMode == RubyIndexerHelper.DOC) {
//                        docMode = RubyIndexerHelper.DEFAULT_DOC;
//                    } else if (newDocMode == RubyIndexerHelper.NODOC_ALL) {
//                        flags |= IndexedElement.NODOC;
//                        nodoc = true;
//                        docMode = RubyIndexerHelper.NODOC_ALL;
//                    } else if (newDocMode == RubyIndexerHelper.NODOC || docMode == RubyIndexerHelper.NODOC_ALL) {
//                        flags |= IndexedElement.NODOC;
//                        nodoc = true;                    
//                    }
                }


                IndexDocument document = factory.createDocument(40); // TODO Measure

                StringBuilder signature = new StringBuilder();

                String sName = template.getSimpleName().toString();
                String qName = template.getQualifiedName().toString();
                String attrs = IndexedElement.encodeAttributes(template, pResult.getTokenHierarchy());
                signature.append(qName.toLowerCase());
                signature.append(';');
                signature.append(';');
                signature.append(qName);
                signature.append(';');
                signature.append(attrs);

                Type superClass = template.getSuperclass();
                if (superClass != null) {
                    String superClz = superClass.getSimpleName().toString();
                    document.addPair(FIELD_EXTENDS_NAME, qName.toLowerCase() + ";" + qName + ";" + superClz, true); // NOI18N
                }
                List<Type> withTraits = template.getInterfaces();
                if (withTraits.size() > 0) {
                    for (Type withTrait : withTraits) {
                        String superClz = withTrait.getSimpleName().toString();
                        document.addPair(FIELD_EXTENDS_NAME, qName.toLowerCase() + ";" + qName + ";" + superClz, true); // NOI18N
                    }

                    ClassCache.INSTANCE.refresh();
                }

                List<Importing> imports = template.getBindingScope().getVisibleElements(Importing.class);

                if (imports.size() > 0) {
                    Set<String> importPkgs = new HashSet<String>();
                    for (Importing importExpr : imports) {
                        String pkgName = importExpr.getPackageName();
                        StringBuilder importAttr = new StringBuilder();
                        importAttr.append(qName.toLowerCase()).append(";").append(qName).append(";").append(pkgName).append(";");
                        if (importExpr.isWild()) {
                            importAttr.append("_").append(";");

                            importPkgs.add(pkgName);
                            document.addPair(FIELD_IMPORT, importAttr.toString(), true);
                        } else {
                            List<Type> importedTypes = importExpr.getImportedTypes();
                            for (Type type : importedTypes) {
                                importAttr.append(type.getSimpleName()).append(";");

                                importPkgs.add(pkgName);
                                document.addPair(FIELD_IMPORT, importAttr.toString(), true);
                            }
                        }
                    }

                    ScalaTypeInferencer.updateClassToImportPkgsCache(qName, importPkgs);
                }

//                boolean isDocumented = isDocumented(node);
//                int documentSize = getDocumentSize(node);
//                if (documentSize > 0) {
//                    flags |= IndexedElement.DOCUMENTED;
//                }

//                StringBuilder attributes = new StringBuilder();
//                attributes.append(IndexedElement.flagToFirstChar(flags));
//                attributes.append(IndexedElement.flagToSecondChar(flags));
//                if (documentSize > 0) {
//                    attributes.append(";");
//                    attributes.append(Integer.toString(documentSize));
//                }
//                document.addPair(FIELD_CLASS_ATTRS, attributes.toString(), false);

                /* Don't prune modules without documentation because
                 * this may be an existing module that we're defining
                 * new (documented) classes for*/
//                if (file.isPlatform() && (template.getKind() == ElementKind.CLASS) &&
//                        !INDEX_UNDOCUMENTED && !isDocumented) {
//                    // XXX No, I might still want to recurse into the children -
//                    // I may have classes with documentation in an undocumented
//                    // module!!
//                    return;
//                }

                document.addPair(FIELD_QUALIFIED_NAME, qName, true);
                document.addPair(FIELD_QUALIFIED_NAME_CASE_INSENSITIVE, qName.toLowerCase(), true);
                document.addPair(FIELD_SIMPLE_NAME, sName, true);
                document.addPair(FIELD_SIMPLE_NAME_CASE_INSENSITIVE, sName.toLowerCase(), true);
                document.addPair(FIELD_ATTRIBUTES, attrs, true);

                // Add the fields, etc.. Recursively add the children classes or modules if any
                for (AstElement element : template.getBindingScope().getElements()) {

                    switch (element.getKind()) {
                        case CLASS:
                        case INTERFACE: {
                            if (element instanceof Template) {
                                analyzeTemplate((Template) element);
                            }
                            break;
                        }
                        case CONSTRUCTOR:
                        case METHOD: {
                            break;
                        }
                        case FIELD: {
                            break;
                        }
                    }
                }

                documents.add(document);
            } finally {
                //docMode = previousDocMode;
            }
        }
    }
}   