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

package org.netbeans.api.javafx.source;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.JarFileSystem;
import org.openide.loaders.DataObject;

/**
 *
 * @author Tomas Zezula
 */
public class SourceFileObject implements JavaFileObject {
    
    final FileObject file;
    final FileObject root;
    private final Kind kind;
    private URI uri;        //Cache for URI
    private String text;
    
    public static SourceFileObject create (final FileObject file, final FileObject root) {        
        try {
            return new SourceFileObject (file, root);
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
            return null;
        }        
    }
    
    /** Creates a new instance of SourceFileObject */
    public SourceFileObject (final FileObject file, final FileObject root) throws IOException {
        assert file != null;
        this.file = file;
        this.root = root;
        String ext = this.file.getExt();        
        this.kind = Kind.SOURCE;
    }
    

    public boolean isNameCompatible (String simplename, JavaFileObject.Kind kind) {
        assert simplename != null;
        return this.kind == kind && this.getNameWithoutExtension().equals(simplename);
    }

    public CharBuffer getCharContent(boolean ignoreEncodingErrors) throws IOException {
        String _text;
        synchronized (this) {
            _text = this.text;
        }
        if (_text != null) {
            return CharBuffer.wrap(_text);
        }
        else {
            return getCharContentImpl(false);
        }
    }   

    public java.io.Writer openWriter() throws IOException {
        return new OutputStreamWriter (this.openOutputStream());
    }

    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {        
        String _text;
        synchronized (this) {
            _text = text;
        }
        if (_text != null) {
            return new StringReader (_text);
        }
        else {
            Reader r = new InputStreamReader (new BufferedInputStream (this.file.getInputStream()));
            return r;
        }
    }

    public java.io.OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public InputStream openInputStream() throws IOException {
        String _text;
        synchronized (this) {
            _text = text;
        }
        if (_text != null) {
            return new ByteArrayInputStream (_text.getBytes());
        }
        else {
            return this.file.getInputStream();
        }
    }

    public boolean delete() {
        if (isModified()!=null) {
            //If the file is modified in editor do not delete it
            return false;
        }
        else {
            try {
                FileLock lock = this.file.lock();
                try {
                    this.file.delete (lock);
                    return true;
                }
                finally {
                    lock.releaseLock();
                }
            } catch (IOException e) {
                return false;
            }
        }
    }


    public JavaFileObject.Kind getKind() {
        return this.kind;
    }

    public String getName() {
       return this.file.getNameExt();
    }

    public String getNameWithoutExtension() {
        return this.file.getName();
    }
    
    public synchronized URI toUri () {
        if (this.uri == null) {
            try {            
                this.uri = URI.create(this.file.getURL().toExternalForm());
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);                
            }
        }
        return this.uri;
    }

    /**
     * Returns the mtime of the file, in the case of opened
     * modified file, the mtime is not known, this method returns
     * the current time.
     */
    public long getLastModified() {
        if (isModified()==null) {
            try {
                //Prefer class files to packed sources, the packed sources may have wrong time stamps.
                if (this.file.getFileSystem() instanceof JarFileSystem) {
                    return 0L;
                }
            } catch (FileStateInvalidException e) {
                //Handled below
            }
            return this.file.lastModified().getTime();
        }
        else {
            return System.currentTimeMillis();
        }
    }
    
    public javax.lang.model.element.NestingKind getNestingKind() {
        return null;
    }

    public Modifier getAccessLevel() {
        return null;
    }
    
    public @Override String toString () {
        return this.file.getPath();
    }
    
    public @Override boolean equals (Object other) {
        if (other instanceof SourceFileObject) {
            SourceFileObject otherSource = (SourceFileObject) other;
            return this.file.equals (otherSource.file);
        }
        else {
            return false;
        }
    }
    
    public @Override int hashCode () {
        return this.file.hashCode();
    }
    
    
    @SuppressWarnings ("unchecked")     // NOI18N
    private EditorCookie isModified () {
        DataObject.Registry regs = DataObject.getRegistry();
        Set<DataObject> modified = regs.getModifiedSet();
        for (DataObject dobj : modified) {
            if (this.file.equals(dobj.getPrimaryFile())) {
                EditorCookie ec = dobj.getCookie(EditorCookie.class);
                return ec;
            }
        }
        return null;
    }
        
    private CharBuffer getCharContentImpl (boolean assign) throws IOException {
	char[] result = null;
        int length = 0;

        Reader in = this.openReader (true);
            int red = 0, rv;
            try {
                int len = (int)this.file.getSize();
                result = new char [len+1];
                while ((rv=in.read(result,red,len-red))>0 && (red=red+rv)<len);
            } finally {
                in.close();
            }
            int j=0;
            for (int i=0; i<red;i++) {
                if (result[i] =='\r') {                                          //NOI18N
                    if (i+1>=red || result[i+1]!='\n') {                         //NOI18N
                        result[j++] = '\n';                                      //NOI18N
                    }
                }
                else {
                    result[j++] = result[i];
                }
            }
            length = j;
        
	result[length]='\n'; //NOI18N
        
        String str = new String(result,0,length);
	CharBuffer charBuffer = CharBuffer.wrap (str);
        if (assign) text = str;
        return charBuffer;
    }
            
    
    
}
