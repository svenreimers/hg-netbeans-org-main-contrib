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

package org.netbeans.modules.vcs.profiles.cvsprofiles.list;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status.StatusInformation;
import org.openide.filesystems.FileUtil;

/**
 * A builder that assembles files paths in working directories
 * according to the file status information.
 *
 * @author  Martin Entlicher
 */
public class StatusFilePathsBuilder {
    
    private static final String ATTIC = "Attic"; // NOI18N
    private static final String[] EMPTY_DIR = {""}; // NOI18N
    private static final String REPOSITORY_PATH = CvsListCommand.CVS_DIRNAME + File.separator
                                                  + "Repository"; // NOI18N
    private static final String[] ABSOLUTE_REPOSITORY_REGEXS = { "^/.*$", "^[a-zA-Z]:\\.*" }; // NOI18N
    
    private String cvsRepository;
    private File workingDir;
    private Pattern[] absoluteRepositoryRegexs;
    /** A map of local directories relative to <code>workingDir</code> as keys
     *  and absolute repository path as values. */
    private Map workReposPaths = new HashMap();
    private List examiningPaths = new ArrayList();
    /** The paths of processed files when multiple choices exist. This set is
     * checked to assure that one file path is not assigned multiple times. */
    private Set processedFilePaths = new HashSet();
    
    /** Creates a new instance of StatusFilePathsBuilder */
    public StatusFilePathsBuilder(File workingDir, String cvsRepository) {
        this.workingDir = workingDir;
        this.cvsRepository = cvsRepository;
        absoluteRepositoryRegexs = new Pattern[ABSOLUTE_REPOSITORY_REGEXS.length];
        try {
            for (int i = 0; i < absoluteRepositoryRegexs.length; i++) {
                absoluteRepositoryRegexs[i] = Pattern.compile(ABSOLUTE_REPOSITORY_REGEXS[i]);
            }
        } catch (PatternSyntaxException exc) {
            org.openide.ErrorManager.getDefault().notify(exc);
            absoluteRepositoryRegexs = new Pattern[0];
        }
        getRepositoryPaths(workingDir, "");
    }
    
    private void addRepositoryPath(String localPath, File repository) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(repository);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            if (line != null && line.length() > 0) {
                if (!isAbsoluteRepository(line)) {
                    line = cvsRepository + "/" + line; // Get the full path to the repository
                }
                if (line.endsWith(".")) line = line.substring(0, line.length() - 1);
                if (line.endsWith(File.separator)) line = line.substring(0, line.length() - 1);
                workReposPaths.put(localPath/*.replace(File.separatorChar, '/')*/, line);
                //System.out.println("for localDir = "+localDir+"\n  put("+localPath+", "+line+")");
            }
        } catch (FileNotFoundException fnfexc) {
            // Ignored
        } catch (IOException ioexc) {
            // Ignored
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exc) {
                    // Ignored
                }
            }
        }
    }
    
    private void getRepositoryPaths(File dir, String relPath) {
        File repository = new File(dir, REPOSITORY_PATH);
        addRepositoryPath(relPath, repository);
        File[] subDirs = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if (subDirs == null) return ;
        for (int i = 0; i < subDirs.length; i++) {
            getRepositoryPaths(subDirs[i], (relPath.length() > 0) ? (relPath + '/' + subDirs[i].getName()) : subDirs[i].getName());
        }
    }
    
    private boolean isAbsoluteRepository(String line) {
        for (int i = 0; i < absoluteRepositoryRegexs.length; i++) {
            if (absoluteRepositoryRegexs[i].matcher(line).matches()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add a local path that is being examined. All files are supposed to be
     * located folders represented by examining paths.
     */
    public void addExaminingPath(String path) {
        examiningPaths.add(path);
    }
    
    /**
     * Fill in the complete file path in the status information.
     * It's supposed, that this method is called once for every file.
     * This is necessary in order to assure, that one file is not
     * filled twice when there are more possibilities.
     * @param fileName The name of the file (only name without path)
     * @param info the status information about that file
     * @return <code>true</code> when the file path was successfully found.
     */
    public boolean fillStatusInfoFilePath(StatusInformation info) {
        String fileName = info.getFileName();
        String[] filePaths = getFilePaths(fileName, info.getRepositoryFileName());
        if (filePaths == null) {
            if (!examiningPaths.isEmpty()) {
                filePaths = (String[]) examiningPaths.toArray(new String[0]);
            } else {
                filePaths = (String[]) workReposPaths.keySet().toArray(new String[0]);
            }
        }
        boolean[] found = new boolean[] { true };
        File dir = findSuitableDir(workingDir, filePaths, fileName, info,
                                   processedFilePaths, found);
        info.setFile(new File(dir, fileName));
        return found[0];
    }
    
    /**
     * Fill in the complete file path in the status information.
     * It's supposed, that this method is called once for every file.
     * This is necessary in order to assure, that one file is not
     * filled twice when there are more possibilities.
     * @param fileName The name of the file (only name without path)
     * @param info the status information about that file
     * @return The file path relative to the working directory, or <code>null</code>,
     *         when the file path was not found.
     */
    public String getStatusFilePath(String fileName, String repositoryFileName) {
        String[] filePaths = getFilePaths(fileName, repositoryFileName);
        if (filePaths == null) {
            if (!examiningPaths.isEmpty()) {
                filePaths = (String[]) examiningPaths.toArray(new String[0]);
            } else {
                filePaths = (String[]) workReposPaths.keySet().toArray(new String[0]);
            }
        }
        boolean[] found = new boolean[] { true };
        File dir = findSuitableDir(workingDir, filePaths, fileName, null,
                                   processedFilePaths, found);
        if (!found[0]) return null;
        else {
            String dirPath = dir.getAbsolutePath();
            int wl = workingDir.getAbsolutePath().length();
            if (wl < dirPath.length()) {
                dirPath = dirPath.substring(wl);
                while (dirPath.startsWith(File.separator)) dirPath = dirPath.substring(1);
            } else {
                dirPath = "";
            }
            if (dirPath.length() == 0) return fileName;
            else return dirPath + File.separator + fileName;
        }
    }
    
    private static File findSuitableDir(File workingDir, String[] filePaths,
                                        String fileName, StatusInformation info,
                                        Set processedFilePaths, boolean[] found_ptr) {
        File dir;
        if (filePaths.length == 1) {
            if (filePaths[0].length() > 0) {
                dir = new File(workingDir, filePaths[0]);
            } else {
                dir = workingDir;
            }
        } else {
            for (int i = 0; i < filePaths.length; i++) {
                dir = new File(workingDir, filePaths[i]);
                File entriesFile = new File(dir, "CVS/Entries");
                if (!entriesFile.exists() || !entriesFile.canRead()) {
                    continue;
                }
                List entries = CvsListOffline.loadEntries(entriesFile);
                //System.out.println("entries = "+entries);
                Map entriesByFiles = CvsListOffline.createEntriesByFiles(entries);
                if (entriesByFiles == null) {
                    continue;
                }
                String entry = (String) entriesByFiles.get(fileName);
                if (entry == null) {
                    continue;
                }
                String[] entryItems = CvsListOffline.parseEntry(entry);
                String revision = entryItems[1];
                //System.out.println("revision = "+revision);
                String status;
                if (info != null) {
                    if (revision.startsWith("-")) {
                        if (!"Locally Removed".equals(info.getStatus())) {
                            continue;
                        }
                    } else if (revision.equals("0")) {
                        if (!"Locally Added".equals(info.getStatus())) {
                            continue;
                        }
                    }
                }
                String filePath = new File(dir, fileName).getAbsolutePath();
                if (processedFilePaths.contains(filePath)) {
                    continue;
                } else {
                    processedFilePaths.add(filePath);
                    return dir;
                }
            }
            found_ptr[0] = false;
            dir = workingDir; // The dir was not found!
        }
        dir = FileUtil.normalizeFile(dir);
        return dir;
    }

    /**
     * Get the path of file from the output information at given index.
     * @param data the output data
     * @param index the index to the file information
     */
    private String[] getFilePaths(String fileName, String repository) {
        //D.deb("getFilePath("+data.substring(index, Math.min(index + 100, data.length()))+", "+index+", "+fileName+")");
        if (repository == null || repository.length() == 0) return null;
        int nameIndex = repository.lastIndexOf('/');
        //D.deb("nameIndex = "+nameIndex);
        if (nameIndex < 0) return null;
        if (nameIndex == 0) return (cvsRepository.length() > 0) ? null : EMPTY_DIR;
        String path = repository.substring(0, nameIndex); //.replace('\\', '/'); // Because of Windoze unexpectable behavior
        if (path.endsWith(ATTIC)) path = path.substring(0, path.length() - ATTIC.length() - 1);
        int index = path.indexOf(cvsRepository);
        //D.deb("path = "+path+", path.indexOf("+cvsRepository+") = "+index);
        if (index < 0) return null;
        //D.deb("getFilePath(): path = "+path+", index = "+index+", cvsRepository = "+cvsRepository);
        if (path.length() <= cvsRepository.length())
            return EMPTY_DIR;
        else {
            //path = path.substring(index + cvsRepository.length() + 1);
            ArrayList myWorkings = new ArrayList();
            Iterator keysIt = workReposPaths.keySet().iterator();
            while (keysIt.hasNext()) {
                String workPath = (String) keysIt.next();
                String repPath = (String) workReposPaths.get(workPath);
                if (path.equals(repPath)) {
                    myWorkings.add(workPath);
                }
            }
            return (String[]) myWorkings.toArray(new String[0]);
        }
    }

}
