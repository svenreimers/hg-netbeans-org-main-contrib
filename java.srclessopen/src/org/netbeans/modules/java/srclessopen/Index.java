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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.java.srclessopen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

/**
 * Index SPI. Represents an index for usages data
 * @author Tomas Zezula
 */
public abstract class Index {    
    
    public enum BooleanOperator {
        AND,
        OR
    };
    
    private static final int VERSION = 0;
    private static final int SUBVERSION = 1;
    private static final String NB_USER_DIR = "netbeans.user";   //NOI18N
    private static final String SEGMENTS_FILE = "segments";      //NOI18N
    private static final String SOURCES = "sources";             //NOI18N
    private static final String SLICE_PREFIX = "s";              //NOI18N    
    private static final String INDEX_DIR = "var"+File.separatorChar+"cache"+File.separatorChar+"javasources"+File.separatorChar+VERSION+'.'+SUBVERSION;    //NOI18N
    
    private static Properties segments;
    private static Map<String, String> invertedSegments;
    private static File cacheFolder;
    private static File segmentsFile;
    private static int index = 0;
    
    private static void loadSegments () throws IOException {
        if (segments == null) {
            File cacheFolder = getCacheFolder();
            assert cacheFolder != null;           
            segments = new Properties ();
            invertedSegments = new HashMap<String,String> ();
            segmentsFile = FileUtil.normalizeFile(new File (cacheFolder, SEGMENTS_FILE));
            if (segmentsFile.exists()) {
                InputStream in = new FileInputStream (segmentsFile);
                try {
                    segments.load (in);
                } finally {
                    in.close();
                }
            }
            for (Map.Entry entry : segments.entrySet()) {
                String segment = (String) entry.getKey();
                String root = (String) entry.getValue();
                invertedSegments.put(root,segment);
                try {
                    index = Math.max (index,Integer.parseInt(segment.substring(SLICE_PREFIX.length())));
                } catch (NumberFormatException nfe) {
                    ErrorManager.getDefault().notify(nfe);
                }
            }
            assert segmentsFile != null;
        }        
    }
    
    
    private static void storeSegments () throws IOException {
        assert segmentsFile != null;       
        OutputStream out = new FileOutputStream (segmentsFile);
        try {
            segments.store(out,null);
        } finally {
            out.close();
        }            
    }
    
    
    public static URL getSourceRootForClassFolder (final URL classFolder) {
        if ("file".equals(classFolder.getProtocol())) {           //NOI18N
            try {
                final File file = FileUtil.normalizeFile(new File (classFolder.toURI()));            
                final File segFolder = file.getParentFile();
                if (segFolder == null) {
                    return null;
                }
                final Object cFolder = segFolder.getParentFile();
                if (cFolder == null || !cFolder.equals(cacheFolder)) {
                    return null;
                }   
                String source = segments.getProperty(segFolder.getName());
                if (source != null) {
                    try {            
                        return new URL (source);
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify(ioe);            
                    }
                }
            } catch (URISyntaxException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return null;
    }
        
    
    public static synchronized File getDataFolder (final URL root) throws IOException {
        return getDataFolder(root, false);
    }
    
    public static synchronized File getDataFolder (final URL root, boolean onlyIfAlreadyExists) throws IOException {
        loadSegments ();
        final String rootName = root.toExternalForm();
        String slice = invertedSegments.get (rootName);
        if ( slice == null) {
            if (onlyIfAlreadyExists)
                return null;
            slice = SLICE_PREFIX + (++index);
            while (segments.getProperty(slice) != null) {                
                slice = SLICE_PREFIX + (++index);
            }
            segments.put (slice,rootName);
            invertedSegments.put(rootName, slice);
            storeSegments ();
        }        
        File result = FileUtil.normalizeFile (new File (cacheFolder, slice));
        if (!result.exists()) {
            if (onlyIfAlreadyExists)
                return null;
            result.mkdir();
        }
        return result;
    }
    
    /**returns null if onlyIfAlreadyExists == true and the cache folder for the given url does not exist.
     */
    public static File getSourceFolder (final URL url, boolean onlyIfAlreadyExists) throws IOException {
        return getSourceFolderImpl(url, onlyIfAlreadyExists);
    }
    
    public static File getSourceFolder (final URL url) throws IOException {                
        return getSourceFolder(url, false);
    }
    
    public static File getSourceFolder (final File root) throws IOException {
        URL url = root.toURI().toURL();
        if (!root.exists()) {
            final String surl = url.toExternalForm();
            if (!surl.endsWith("/")) {
                url = new URL (surl+'/');
            }
        }
        return getSourceFolderImpl(url, false);
    }        
    
    private static File getSourceFolderImpl (final URL url, boolean onlyIfAlreadyExists) throws IOException {
        final File dataFolder = getDataFolder (url, onlyIfAlreadyExists);
        if (onlyIfAlreadyExists && dataFolder == null)
            return null;
        final File result = FileUtil.normalizeFile(new File (dataFolder, SOURCES));
        if (!result.exists()) {
            if (onlyIfAlreadyExists)
                return null;
            result.mkdir();
        }
        return result;
    }
    
    
    
    /**
     *  Returns non cached netbeans user dir.
     *  For performance reasons the returned {@link File} is not normalized.
     *  Client is responsible to call {@link FileUtil.normalizeFile}
     *  before using the returned value.
     *  @return netbeans user dir.
     */
    static String getNbUserDir () {
        final String nbUserProp = System.getProperty(NB_USER_DIR);
        return nbUserProp;
    }
    
    public static synchronized File getCacheFolder () {
        if (cacheFolder == null) {
            final String nbUserDirProp = getNbUserDir();
            assert nbUserDirProp != null;
            final File nbUserDir = new File (nbUserDirProp);
            cacheFolder = FileUtil.normalizeFile(new File (nbUserDir, INDEX_DIR));
            if (!cacheFolder.exists()) {
                boolean created = cacheFolder.mkdirs();                
                assert created : "Cannot create cache folder";  //NOI18N
            }
            else {
                assert cacheFolder.isDirectory() && cacheFolder.canRead() && cacheFolder.canWrite();
            }
        }
        return cacheFolder;
    }
    
    public static boolean isTest () {
        return getNbUserDir() == null;
    }
    
    /**
     * Only for unit tests!
     *
     */
    static synchronized void setCacheFolder (final File folder) {
        assert folder != null && folder.exists() && folder.canRead() && folder.canWrite();
        cacheFolder = folder;
    }
    
}
