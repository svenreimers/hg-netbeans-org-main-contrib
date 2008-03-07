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

package org.netbeans.modules.javafx.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.Error;
import org.netbeans.api.gsf.Index;
import org.netbeans.api.gsf.ParseEvent;
import org.netbeans.api.gsf.ParseListener;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.gsf.SourceFileReader;
import org.netbeans.api.retouche.source.ClasspathInfo;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.gsf.DefaultParserFile;
import org.openide.filesystems.FileObject;

/**
 *
 * @author tor
 */
class TestCompilationInfo extends CompilationInfo {
    private final String text;
    private final Document doc;
    private Source source;
    private ParserResult result;
    
    public TestCompilationInfo(FileObject fileObject, BaseDocument doc, String text) throws IOException {
        super(fileObject);
        this.text = text;
        this.doc = doc;
        setParser(new FXParser());
        if (fileObject != null) {
            source = Source.forFileObject(fileObject);
        }
    }

    public String getText() {
        return text;
    }

    public Index getIndex() {
        ClasspathInfo cpi = source.getClasspathInfo();
        if (cpi != null) {
            return cpi.getClassIndex();
        }
        
        return null;
    }

    @Override
    public Document getDocument() throws IOException {
        return this.doc;
    }
    
    @Override
    public ParserResult getParserResult() {
        ParserResult r = super.getParserResult();
        if (r == null) {
            r = result;
        }
        if (r == null) {
            final ParserResult[] resultHolder = new ParserResult[1];

            ParseListener listener =
                new ParseListener() {
                    public void started(ParseEvent e) {
                        //ParserTaskImpl.this.listener.started(e);
                    }
                    
                    public void error(Error e) {
                        //ParserTaskImpl.this.listener.error(e);
                    }
                    
                    public void exception(Exception e) {
                        //ParserTaskImpl.this.listener.exception(e);
                    }
                    
                    public void finished(ParseEvent e) {
                        // TODO - check state
                        if (e.getKind() == ParseEvent.Kind.PARSE) {
                            resultHolder[0] = e.getResult();
                        }
                        //ParserTaskImpl.this.listener.finished(e);
                    }
                };
            
            List<ParserFile> sourceFiles = new ArrayList<ParserFile>(1);
            ParserFile file = new DefaultParserFile(getFileObject(), null, false);
            sourceFiles.add(file);
            
            SourceFileReader reader =
                new SourceFileReader() {
                    public CharSequence read(ParserFile file) throws IOException {
                        return text;
                    }
                    public int getCaretOffset(ParserFile fileObject) {
                        return -1;
                    }
                };
            
            getParser().parseFiles(sourceFiles, listener, reader);
            
            r = result = resultHolder[0];
        }
        
        return r;
    }
}
