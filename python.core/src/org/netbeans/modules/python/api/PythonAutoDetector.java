/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.python.api;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

public class PythonAutoDetector {
    private static final Logger LOGGER = Logger.getLogger(PythonAutoDetector.class.getName());

    private final ArrayList<String> matches = new ArrayList<>();
    boolean searchNestedDirectoies = true;

    private void processAction(File dir) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Inspecting: {0}", dir.getAbsolutePath());
        }
        if(dir.isFile()){
            int pos = dir.getName().indexOf(".");
            String name;
            String ext;
            if (pos > -1 ){
                 name = dir.getName().substring(0, pos);
                 ext = dir.getName().substring(pos+1);
            }else{
                name = dir.getName();
                ext = "";
            }
            if (name.equalsIgnoreCase("jython")
                    || name.equalsIgnoreCase("python")
                    || name.equalsIgnoreCase("python2")
                    || name.equalsIgnoreCase("python3")
                    || name.equalsIgnoreCase("pypy")
                    || name.equalsIgnoreCase("pypy3")) {
                if (Utilities.isWindows()) {
                    if (ext.equalsIgnoreCase("exe") || ext.equalsIgnoreCase("bat")) {
                        if( addMatch(dir.getAbsolutePath())) { //don't report duplicates
                            if (LOGGER.isLoggable(Level.CONFIG)) {
                                LOGGER.log(Level.CONFIG, "Match (Windows): {0}", dir.getAbsolutePath());                           
                            }
                        }
                    }
                } else if(Utilities.isMac()) {
                    if (ext.equalsIgnoreCase("")) {
                        if( addMatch(dir.getAbsolutePath())) {
                            if (LOGGER.isLoggable(Level.CONFIG)) {
                                LOGGER.log(Level.CONFIG, "Match (Mac): {0}", dir.getAbsolutePath());                           
                            }
                        }
                    }
                } else { // Not Windows or Mac, must be Unix-like system...
                    if (ext.equalsIgnoreCase("")) {
                        if( addMatch(dir.getAbsolutePath())) {
                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.log(Level.FINE, "Match (Unix-like): {0}", dir.getAbsolutePath());                           
                            }
                        }                     
                    }
                }
            }
        }
        
        if(dir.isDirectory()){
            if(dir.getName().toLowerCase().contains("jython") ||
                    dir.getName().toLowerCase().contains("python")){
                String[] children = dir.list();
                if(children != null){
                    for (String child : children) {
                        searchNestedDirectoies = true;
                        traverse(new File(dir, child), false); // false had been searchNestedDirectoies
                    }
                }
            }
        }

    }

    public ArrayList<String> getMatches() {
        return matches;
    }
    
    public int countMatches(){
        return matches.size();
    }
    
    private boolean addMatch( String sMatch){
        // ensure that no match is added twice (no duplicates!)
        // tweaking may be required... especially for /python vs. /python/bin
        for( String sTest : matches) { // TODO: Do not compare trailing \ or / 
            if(sMatch.equalsIgnoreCase(sTest)){ // perhaps shouldn't ignore case for non-Windows
                return false; // do not add if it already exists!
            }
//        Iterator<String> iterator = matches.iterator();
//        while (iterator.hasNext()) {
//            String sTest = iterator.next(); // TODO: Do not compare trailing \ or / 
//            if(sMatch.equalsIgnoreCase(sTest)){ // perhaps shouldn't ignore case for non-Windows
//                return false; // do not add if it already exists!
//            }
        }
        matches.add(sMatch); // no preexisting match... add new.
        return true;
    }
    
    public void traverseEnvPaths() throws SecurityException{
        String delims = "[" + System.getProperty("path.separator") + "]";
        String sEnvPath;
        try{
            // Env variables must be upper-case in Unix. Windows is case-insensitive.
            sEnvPath = System.getenv("PATH");
        } catch (SecurityException se) {
            Exceptions.printStackTrace(se);
            return;
        } 
     
        String[] paths = sEnvPath.split(delims);
        int iCount = countMatches();
        // search ONLY paths that contain the substring python/jython
        for(String spath: paths) { 
            if( spath.toLowerCase().contains("jython") ||
                spath.toLowerCase().contains("python") ){
                searchNestedDirectoies = true;
                processAction(new File(spath)); //traverse(new File(spath), false);
                if( iCount < matches.size()){ // take only the first match
                    return;
                }
            }
        }
        // no python found!!!
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG, "Python/Jython not found in environment path {0}", sEnvPath);            
        }
    }

    // Given a directory, search all grandchild directories 
    //   for bin directories containing files to be processed
    // This is specific to Mac which installs its Pythons like
    //  /Library/Frameworks/Python.framework/Versions/3.4/bin/python3
    //      and
    //  /System/Library/Frameworks/Python.framework/Versions/2.7/    
    public void traverseMacDirectories(File dir) {
        
        // Make sure the path is a directory named "Versions".
        if (! dir.getName().endsWith("Versions") || ! dir.isDirectory()) {
            return;
        }

        // Directories which are not symbolic links.
        FileFilter directoriesOnlyFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() &! Files.isSymbolicLink(Paths.get(file.getAbsolutePath()));
            }
        };

        // "bin" directory
        FileFilter binDirectoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().equals("bin") && file.isDirectory();
            }
        };

        File[] versionDirs = dir.listFiles(directoriesOnlyFilter);
        File[] binDirs;  // Should have either 0 or 1 element.
        File[] binContents;
        for (File versionDir : versionDirs) {
            binDirs = versionDir.listFiles(binDirectoryFilter);
            if (binDirs.length < 1) {
                continue;
            }

            File binDir = binDirs[0];
            binContents = binDir.listFiles(new FileFilter(){
                @Override public boolean accept(File file){
                    return file.isFile(); // Regular files only.
                }
            });

            for (File binContent : binContents) {
                searchNestedDirectoies = false;
                processAction(binContent);
            }
        }// END for each version dir
    }   
    
    // Given a directory, for each subdirectory within, 
    //   search the subdirectory and next-level subdirectories only
    public void traverseDirectory( File dir) {
        if (dir.isDirectory()) { //are we already IN the ?ython dir?
            String spath = dir.getName();
            if( spath.toLowerCase().contains("jython") ||
                spath.toLowerCase().contains("python") ||
                spath.toLowerCase().contains("pypy") ){
                searchNestedDirectoies = true; // must set each time
                processAction(dir);
                return;
            }
            // otherwise, we check the next level for the ?ython dir?
            String[] children = dir.list();
            if(children != null){
                for (int i=0; i<children.length; i++) {
                    File fDirectory = new File(dir, children[i]);
                    if (fDirectory.isDirectory() || fDirectory.isFile()) {
                        spath = fDirectory.getName();
                        if (spath.toLowerCase().contains("jython")
                                || spath.toLowerCase().contains("python")
                                || spath.toLowerCase().contains("pypy")) {
                            searchNestedDirectoies = true; // must set each time
                            processAction(fDirectory);
                        }
                    }
                }
            }
        }        
    } 
    
    public void traverse(File dir, boolean recersive) {

        processAction(dir);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null){
                if(searchNestedDirectoies){// recursive is bad - better only search one or two levels
                    if(searchNestedDirectoies != recersive)
                        searchNestedDirectoies = recersive;
                    for (String child : children) {
                        traverse(new File(dir, child), recersive);
                    }
                }
            }
        }

    }
    
}
