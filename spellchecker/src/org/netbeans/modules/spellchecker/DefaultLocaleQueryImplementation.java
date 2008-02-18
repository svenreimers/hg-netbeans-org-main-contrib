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
package org.netbeans.modules.spellchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.StringTokenizer;
import org.netbeans.modules.spellchecker.spi.LocaleQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class DefaultLocaleQueryImplementation implements LocaleQueryImplementation {
    
    /** Creates a new instance of DefaultLocaleQueryImplementation */
    public DefaultLocaleQueryImplementation() {
    }

    public Locale findLocale(FileObject file) {
        return getDefaultLocale();
    }
    
    private static final String FILE_NAME = "spellchecker-default-locale";
    
    private static FileObject getDefaultLocaleFile() {
        return Repository.getDefault().getDefaultFileSystem().findResource(FILE_NAME);
    }
    
    public static Locale getDefaultLocale() {
        FileObject file = getDefaultLocaleFile();
        
        if (file == null) {
            setDefaultLocale(Locale.getDefault());
            file = getDefaultLocaleFile();
            
            assert file != null;
        }
        
        Charset UTF8 = Charset.forName("UTF-8");
        
        BufferedReader r = null;
        
        try {
            r = new BufferedReader(new InputStreamReader(file.getInputStream(), UTF8));
            
            String localeLine = r.readLine();
            
            if (localeLine == null)
                return null;
            
            String language = "";
            String country = "";
            String variant = "";
            
            StringTokenizer stok = new StringTokenizer(localeLine, "_");
            
            language = stok.nextToken();
            
            if (stok.hasMoreTokens()) {
                country = stok.nextToken();
                
                if (stok.hasMoreTokens())
                    variant = stok.nextToken();
            }
            
            return new Locale(language, country, variant);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
        }
        
        return null;
    }
    
    public static void setDefaultLocale(Locale locale) {
        FileObject file = getDefaultLocaleFile();
        Charset UTF8 = Charset.forName("UTF-8");
        FileLock lock = null;
        PrintWriter pw = null;
        
        try {
            if (file == null) {
                file = Repository.getDefault().getDefaultFileSystem().getRoot().createData(FILE_NAME);
            }
            
            lock = file.lock();
            pw = new PrintWriter(new OutputStreamWriter(file.getOutputStream(lock), UTF8));
            
            pw.println(locale.toString());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            if (pw != null)
                pw.close();
            
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }
}
