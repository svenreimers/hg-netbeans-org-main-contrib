/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.vss.list;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openide.ErrorManager;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

import org.netbeans.modules.vcs.profiles.vss.commands.GetAdjustedRelevantMasks;
import org.netbeans.modules.vcs.profiles.vss.commands.GetInitializationVariable;

/**
 * List command for VSS.
 * @author  Martin Entlicher
 */
public class VssListCommand extends AbstractListCommand implements CommandOutputListener {

    static final String PROJECT_BEGIN_ENG = "$/"; // NOI18N
    static final String PROJECT_BEGIN_LOC = org.openide.util.NbBundle.getBundle(VssListCommand.class).getString("VSS_ProjectBegin");
    static final String STATUS_MISSING = "Missing"; // NOI18N
    static final String STATUS_CURRENT = "Current"; // NOI18N
    static final String STATUS_LOCALLY_MODIFIED = "Locally Modified"; // NOI18N
    static final String LOCAL_FILES_ENG = "Local files not in the current project:"; // NOI18N
    static final String LOCAL_FILES_LOC = org.openide.util.NbBundle.getBundle(VssListCommand.class).getString("VSS_LocalFiles");
    static final String SOURCE_SAFE_FILES_ENG = "SourceSafe files not in the current folder:"; // NOI18N
    static final String SOURCE_SAFE_FILES_LOC = org.openide.util.NbBundle.getBundle(VssListCommand.class).getString("VSS_SourceSafeCurrent");
    static final String DIFFERENT_FILES_ENG = "SourceSafe files different from local files:"; // NOI18N
    static final String DIFFERENT_FILES_LOC = org.openide.util.NbBundle.getBundle(VssListCommand.class).getString("VSS_SourceSafeDifferent");
    static final String IGNORED_FILE = "vssver.scc"; // NOI18N
    static final String ARG_LOC = "-loc";

    static final int STATUS_POSITION = 19;
    
    private String dir=null; //, rootDir=null;
    private String relDir = null;
    private String[] args=null;
    private volatile String[] statuses = null;
    private Hashtable vars = null;
    private VcsFileSystem fileSystem = null;
    private HashSet currentFiles = null;
    private HashSet missingFiles = null;
    private HashSet differentFiles = null;
    private HashSet ignoredFiles = null;
    
    private String PROJECT_BEGIN, LOCAL_FILES, SOURCE_SAFE_FILES, DIFFERENT_FILES;

    /** Creates new VssListCommand */
    public VssListCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        super.setFileSystem(fileSystem);
        this.fileSystem = fileSystem;
    }

    private void initDir(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR");
        if (rootDir == null) {
            rootDir = ".";
            vars.put("ROOTDIR",".");
        }
        this.dir = (String) vars.get("DIR");
        if (this.dir == null) {
            this.dir = "";
            vars.put("DIR","");
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        //D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        relDir = new String(dir);
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
                relDir = new String(module);
            }
        } else {
            if (module == null)
                dir=rootDir+File.separator+dir;
            else {
                dir=rootDir+File.separator+module+File.separator+dir;
                relDir = new String(module+File.separator+dir);
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        //D.deb("dir="+dir);
    }

    private boolean runCommand(Hashtable vars, String cmdName) throws InterruptedException {
        return runCommand(vars, cmdName, null);
    }
    
    private boolean runCommand(Hashtable vars, String cmdName, final boolean[] errMatch) throws InterruptedException {
        String workingDirPath = "${ROOTDIR}${PS}${MODULE}${PS}${DIR}"; // NOI18N
        workingDirPath = Variables.expand(vars, workingDirPath, false);
        File workingDir = new File(workingDirPath);
        File tmpEmptyDir = null;
        if (!workingDir.exists()) {
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            String tmpDirName = "refresh"; // NOI18N
            for (int i = 0; ; i++) {
                tmpEmptyDir = new File(tmpDir, tmpDirName + i);
                if (!tmpEmptyDir.exists()) {
                    tmpEmptyDir.mkdir();
                    break;
                }
            }
            vars.put("EMPTY_REFRESH_FOLDER", tmpEmptyDir.getAbsolutePath());
        }
        try {
            VcsCommand cmd = fileSystem.getCommand(cmdName);
            VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            ec.addDataOutputListener(this);
            if (errMatch != null && errMatch.length > 0) {
                ec.addDataErrorOutputListener(new CommandDataOutputListener() {
                    public void outputData(String[] data) {
                        if (data != null) errMatch[0] = true;
                    }
                });
            }
            ec.addErrorOutputListener(this);
            fileSystem.getCommandsPool().preprocessCommand(ec, vars, fileSystem);
            fileSystem.getCommandsPool().startExecutor(ec);
            try {
                fileSystem.getCommandsPool().waitToFinish(ec);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(ec);
                throw iexc;
            }
            return (ec.getExitStatus() == VcsCommandExecutor.SUCCEEDED);
        } finally {
            if (tmpEmptyDir != null) {
                tmpEmptyDir.delete();
            }
        }
    }
    
    /**
     * List files of VSS Repository.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * filesByName listing of files with statuses
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.args = args;
        this.vars = new Hashtable(vars);
        this.filesByName = filesByName;
        boolean localized = false;
        if (args.length > 1 && ARG_LOC.equals(args[0])) {
            localized = true;
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            args = newArgs;
        }
        if (args.length < 2) {
            if (stderrNRListener != null) stderrNRListener.outputLine("Bad number of arguments. "+
                                                                      "Expecting two arguments: directory reader and status reader.\n"+
                                                                      "Directory status reader is an optional third argument to improve "+
                                                                      "performance on large directories.");
            return false;
        }
        PROJECT_BEGIN = (localized) ? PROJECT_BEGIN_LOC : PROJECT_BEGIN_ENG;
        LOCAL_FILES = (localized) ? LOCAL_FILES_LOC : LOCAL_FILES_ENG;
        SOURCE_SAFE_FILES = (localized) ? SOURCE_SAFE_FILES_LOC : SOURCE_SAFE_FILES_ENG;
        DIFFERENT_FILES = (localized) ? DIFFERENT_FILES_LOC : DIFFERENT_FILES_ENG;
        initVars(this.vars);
        initDir(this.vars);
        String ssDir = (String) vars.get("ENVIRONMENT_VAR_SSDIR"); // NOI18N
        String userName = (String) vars.get("USER_NAME"); // NOI18N
        if (userName == null || userName.length() == 0) {
            userName = System.getProperty("user.name");
        }
        String relevantMasks;
        try {
            relevantMasks = GetInitializationVariable.getVariable(ssDir, userName, GetAdjustedRelevantMasks.RELEVANT_MASKS);
        } catch (IOException ioex) {
            relevantMasks = null;
        }
        readLocalFiles(dir, relevantMasks);
        missingFiles = new HashSet();
        differentFiles = new HashSet();
        //parseCommands();
        boolean[] errMatch = new boolean[1];
        try {
            runCommand(this.vars, args[0], errMatch);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (!errMatch[0]) {
            flushLastFile();
            currentFiles.removeAll(differentFiles);
            try {
                fillFilesByName();
            } catch (InterruptedException iexc) {
                return false;
            }
        }

        return !errMatch[0];
    }
    
    private void fillFilesByName() throws InterruptedException {
        /*
        System.out.println("fillFilesByName():\n"+
                           "currentFiles = "+VcsUtilities.arrayToString((String[]) currentFiles.toArray(new String[0]))+
                           "\nmissingFiles = "+VcsUtilities.arrayToString((String[]) missingFiles.toArray(new String[0]))+
                           "\ndifferentFiles = "+VcsUtilities.arrayToString((String[]) differentFiles.toArray(new String[0]))+
                           "\n");
         */
        // At first try to run ss status once for the whole folder and retrieve
        // status of files distinguishable at first 19 characters
        if (args.length > 2) fillPossibleFilesAtOnce();
        // If some files differ at characters after 19, we need to precess them
        // file-by-file
        fillFilesByName(currentFiles, STATUS_CURRENT);
        fillFilesByName(missingFiles, STATUS_MISSING);
        fillFilesByName(differentFiles, STATUS_LOCALLY_MODIFIED);
    }
    
    private void fillPossibleFilesAtOnce() throws InterruptedException {
        HashMap distinguishable = new HashMap();
        HashSet undistinguishable = new HashSet();
        HashSet allFiles = (currentFiles != null) ? new HashSet(currentFiles) : new HashSet();
        if (missingFiles != null) allFiles.addAll(missingFiles);
        if (differentFiles != null) allFiles.addAll(differentFiles);
        for (Iterator it = allFiles.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            String pattern;
            if (file.length() <= STATUS_POSITION) {
                pattern = file;
            } else {
                pattern = file.substring(0, STATUS_POSITION);
            }
            if (distinguishable.containsKey(pattern)) {
                // This pattern was already processed => it's not distinguishable any more
                distinguishable.remove(pattern);
                undistinguishable.add(file);
            } else if (!undistinguishable.contains(file)) {
                distinguishable.put(pattern, file);
            }
        }
        if (distinguishable.size() > 2) { // We have to have a reasonable number of files to take care about
            fillFromFolderStatus(distinguishable);
        }
    }
    
    private void fillFromFolderStatus(final Map distinguishable) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(args[2]);
        final Set processedFiles = Collections.synchronizedSet(new HashSet());
        if (cmd != null) {
            Hashtable cmdVars = new Hashtable(vars);
            VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, cmdVars);
            vce.addDataOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] elements) {
                    if (elements != null) {
                        if (elements[0].indexOf(PROJECT_BEGIN) == 0) return ; // skip the $/... folder
                        int index = Math.min(STATUS_POSITION + 1, elements[0].length());
                        int index2 = elements[0].indexOf("  ", index);
                        if (index2 < 0) index2 = elements[0].length();
                        if (index < index2) {
                            String pattern = elements[0].substring(0, STATUS_POSITION).trim();
                            String file = (String) distinguishable.get(pattern);
                            if (file != null) {
                                String[] statuses = (String[]) filesByName.get(file);
                                if (statuses == null) {
                                    statuses = new String[3];
                                }
                                statuses[0] = file;
                                if (currentFiles != null && currentFiles.contains(file)) {
                                    statuses[1] = STATUS_CURRENT;
                                } else if (missingFiles != null && missingFiles.contains(file)) {
                                    statuses[1] = STATUS_MISSING;
                                } else {
                                    statuses[1] = STATUS_LOCALLY_MODIFIED;
                                }
                                String lockerName = elements[0].substring(index, index2).trim();
                                statuses[2] = addLocker(statuses[2], parseLocker(lockerName));
                                filesByName.put(statuses[0], statuses);
                                stdoutListener.outputData(statuses);
                                processedFiles.add(file);
                            }
                        }
                    }
                }
            });
            vce.addErrorOutputListener(this);
            fileSystem.getCommandsPool().preprocessCommand(vce, cmdVars, fileSystem);
            fileSystem.getCommandsPool().startExecutor(vce);
            try {
                fileSystem.getCommandsPool().waitToFinish(vce);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(vce);
                throw iexc;
            }
            Collection distinguishableFiles = distinguishable.values();
            Set noStatusFiles = new HashSet(distinguishableFiles);
            noStatusFiles.removeAll(processedFiles);
            for (Iterator it = noStatusFiles.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                String[] statuses = new String[3];
                statuses[0] = file;
                if (currentFiles != null && currentFiles.contains(file)) {
                    statuses[1] = STATUS_CURRENT;
                } else if (missingFiles != null && missingFiles.contains(file)) {
                    statuses[1] = STATUS_MISSING;
                } else {
                    statuses[1] = STATUS_LOCALLY_MODIFIED;
                }
                statuses[2] = "";
                filesByName.put(statuses[0], statuses);
                stdoutListener.outputData(statuses);
            }
            if (currentFiles != null) {
                currentFiles.removeAll(distinguishableFiles);
            }
            if (missingFiles != null) {
                missingFiles.removeAll(distinguishableFiles);
            }
            if (differentFiles != null) {
                differentFiles.removeAll(distinguishableFiles);
            }
        }
    }
    
    static final String parseLocker(String lockerName) {
        if (lockerName.endsWith(" Exc")) {
            lockerName = lockerName.substring(0, lockerName.length() - " Exc".length());
        }
        return lockerName;
    }
    
    private void fillFilesByName(Set files, String status) throws InterruptedException {
        if (files == null) return ;
        for (Iterator fileIt = files.iterator(); fileIt.hasNext(); ) {
            String file = (String) fileIt.next();
            statuses = new String[3];
            statuses[0] = file;
            statuses[1] = status;
            VcsCommand cmd = fileSystem.getCommand(args[1]);
            if (cmd != null) {
                Hashtable varsCmd = new Hashtable(vars);
                varsCmd.put("FILE", file);
                //cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, "^"+file.substring(0, Math.min(STATUS_POSITION, file.length()))+" (.*$)");
                statuses[2] = null;
                VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCmd);
                vce.addDataOutputListener(new CommandDataOutputListener() {
                    public void outputData(String[] elements2) {
                        if (elements2 != null) {
                            //D.deb(" ****  status match = "+VcsUtilities.arrayToString(elements));
                            if (elements2[0].indexOf(PROJECT_BEGIN) == 0) return ; // skip the $/... folder
                            if (statuses[2] != null) return ; // The status was already set and we get some garbage
                            addStatuses(elements2, statuses);
                        }
                    }
                });
                vce.addErrorOutputListener(this);
                fileSystem.getCommandsPool().preprocessCommand(vce, varsCmd, fileSystem);
                fileSystem.getCommandsPool().startExecutor(vce);
                try {
                    fileSystem.getCommandsPool().waitToFinish(vce);
                } catch (InterruptedException iexc) {
                    fileSystem.getCommandsPool().kill(vce);
                    throw iexc;
                }
            } else statuses[2] = "";
            filesByName.put(statuses[0], statuses);
            stdoutListener.outputData(statuses);
        }
    }
    
    /**
     * Add multiple locker status. Lockers are separated by commas.
     */
    static String addLocker(String lockerStatus, String locker) {
        if (lockerStatus == null || lockerStatus.length() == 0) return locker;
        if (locker == null || locker.length() == 0) return lockerStatus;
        return lockerStatus + ", " + locker;
    }
    
    static void createMaskRegularExpressions(String relevantMasks,
                                             Pattern[] regExpPositivePtr,
                                             Pattern[] regExpNegativePtr) {
        if (relevantMasks != null) {
            List ignoreListPositive = new ArrayList();
            List ignoreListNegative = new ArrayList();
            String[] masks = VcsUtilities.getQuotedStrings(relevantMasks);
            //System.out.println("mask = "+relevantMasks);
            for (int i = 0; i < masks.length; i++) {
                //System.out.println("  mask["+i+"] = "+masks[i]);
                if (masks[i].startsWith("!")) {
                    ignoreListNegative.add(masks[i].substring(1));
                } else {
                    ignoreListPositive.add(masks[i]);
                }
            }
            if (ignoreListPositive.size() > 0) {
                String unionExp = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreListPositive);
                try {
                    regExpPositivePtr[0] = Pattern.compile(unionExp);
                    //System.out.println(" **** GOT positive reg EXP: '"+regExpPositivePtr[0]+"' *********");
                } catch (PatternSyntaxException malformedRE) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, malformedRE);
                }
            }
            if (ignoreListNegative.size() > 0) {
                String unionExp = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreListNegative);
                try {
                    regExpNegativePtr[0] = Pattern.compile(unionExp);
                    //System.out.println(" **** GOT negative reg EXP: '"+regExpNegativePtr[0]+"' *********");
                } catch (PatternSyntaxException malformedRE) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, malformedRE);
                }
            }
        }
    }
    
    private void readLocalFiles(String dir, String relevantMasks) {
        Pattern[] regExpPositivePtr = new Pattern[] { null };
        Pattern[] regExpNegativePtr = new Pattern[] { null };
        createMaskRegularExpressions(relevantMasks, regExpPositivePtr, regExpNegativePtr);
        Pattern regExpPositive = regExpPositivePtr[0];
        Pattern regExpNegative = regExpNegativePtr[0];
        //System.out.println("regExpPositive = "+regExpPositive);
        //System.out.println("regExpNegative = "+regExpNegative);
        File fileDir = new File(dir);
        currentFiles = new HashSet();
        ignoredFiles = new HashSet();
        String[] subFiles = fileDir.list();
        if (subFiles == null) return ;
        for (int i = 0; i < subFiles.length; i++) {
            String fileName = subFiles[i];
            File file = new File(dir, fileName);
            if (file.isFile()) {
                if (!IGNORED_FILE.equalsIgnoreCase(fileName) &&
                    (regExpPositive == null || regExpPositive.matcher(fileName).matches()) &&
                    (regExpNegative == null || !regExpNegative.matcher(fileName).matches())) {
                    currentFiles.add(fileName);
                    //System.out.println("current = "+fileName);
                } else {
                    ignoredFiles.add(fileName);
                    //System.out.println("ignored = "+fileName);
                }
            }
        }
    }

    static void addStatuses(String[] elements, String[] statuses) {
        //D.deb(" !!!!!!!!!!  adding statuses "+VcsUtilities.arrayToString(elements));
        /*
        for (int i = 1; i < Math.min(elements.length, statuses.length); i++)
          statuses[i] = elements[i];
        */
        //if (statuses[2] != null) return ; // The status is already set (it can be called more than once with some garbage then)
        int fileIndex = statuses[0].lastIndexOf('/');
        if (fileIndex < 0) fileIndex = 0;
        else fileIndex++;
        String file = statuses[0].substring(fileIndex);
        if (file.length() <= STATUS_POSITION) {
            if (!elements[0].startsWith(file)) {
                if (statuses[2] == null) statuses[2] = "";
                // The element does not start with the file name
                return ;
            }
        } else {
            if (!file.startsWith(elements[0].substring(0, Math.min(STATUS_POSITION, elements[0].length())))) {
                if (statuses[2] == null) statuses[2] = "";
                // The element does not start with the file name
                return ;
            }
        }
        int index = Math.min(STATUS_POSITION + 1, elements[0].length());
        int index2 = elements[0].indexOf("  ", index);
        if (index2 < 0) index2 = elements[0].length();
        if (index < index2) {
            statuses[2] = addLocker(statuses[2], parseLocker(elements[0].substring(index, index2).trim()));
        } else {
            if (statuses[2] == null) statuses[2] = "";
        }
    }
    
    private static int findWhiteSpace(String str) {
        return findWhiteSpace(str, 0);
    }
    
    private static int findWhiteSpace(String str, int index) {
        for (int i = index; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    static String getFileFromRow(Collection currentFiles, String row) {
        int index;
        //System.out.println("getFileFromRow("+row+")");
        for (index = findWhiteSpace(row); index >= 0 && index < row.length(); index = findWhiteSpace(row, index + 1)) {
            String file = row.substring(0, index).trim();
            //System.out.println("    test file = '"+file+"', contains = "+currentFiles.contains(file));
            if (currentFiles.contains(file)) break;
        }
        //System.out.println("   have CVS file = "+((index > 0 && index < row.length()) ? row.substring(0, index) : row));
        if (index > 0 && index < row.length()) return row.substring(0, index);
        else return row;
    }
    
    private boolean gettingFolders = true;
    private boolean gettingLocalFiles = false;
    private boolean gettingSourceSafeFiles = false;
    private boolean gettingDifferentFiles = false;
    private String lastFileName = "";
    
    private void removeLocalFiles() {
        //System.out.println("removeLocalFiles: "+lastFileName);
        if (lastFileName == null) return ;
        while (lastFileName.length() > 0) {
            String fileName = getFileFromRow(currentFiles, lastFileName);
            //System.out.println("file From Row = '"+fileName+"'");
            currentFiles.remove(fileName.trim());
            lastFileName = lastFileName.substring(fileName.length());
        }
    }
    
    private void flushLastFile() {
        if (lastFileName == null || lastFileName.length() == 0) return ;
        if (gettingSourceSafeFiles) {
            String fileName = lastFileName.trim();
            if (ignoredFiles.contains(fileName)) {
                currentFiles.add(fileName);
            } else {
                missingFiles.add(fileName);
            }
            lastFileName = "";
        } else if (gettingDifferentFiles) {
            differentFiles.add(lastFileName.trim());
            lastFileName = "";
        } else if (gettingLocalFiles) {
            removeLocalFiles();
        }
    }
    
    /** Parse the output of "ss dir -F- && ss diff" commands
     * ss dir -F- gives the subfolders in the given folder
     * ss diff gives the differences between the current folder and the repository.
     */
    public void outputData(String[] elements) {
        String line = elements[0];
        //System.out.println("outputData("+line+")");
        if (line == null) return;
        String file = line.trim();
        if (LOCAL_FILES.equals(file)) {
            //System.out.println("LOCAL_FILES");
            gettingFolders = false;
            gettingLocalFiles = true;
        } else if (SOURCE_SAFE_FILES.equals(file)) {
            //System.out.println("SOURCE_SAFE_FILES");
            gettingFolders = false;
            if (gettingLocalFiles) {
                removeLocalFiles();
                gettingLocalFiles = false;
            }
            gettingSourceSafeFiles = true;
        } else if (DIFFERENT_FILES.equals(file)) {
            //System.out.println("DIFFERENT_FILES");
            gettingFolders = false;
            if (gettingLocalFiles) {
                removeLocalFiles();
                gettingLocalFiles = false;
            }
            flushLastFile();
            gettingSourceSafeFiles = false;
            gettingDifferentFiles = true;
        } else if (gettingFolders) {
            if(!file.startsWith(PROJECT_BEGIN) && file.startsWith("$")) {
                String fname = file.substring(1, file.length());
                File f = new File(dir + File.separator + fname);
                statuses = new String[3];
                statuses[0] = fname + "/";
                if (f.exists()) statuses[1] = STATUS_CURRENT;
                else            statuses[1] = STATUS_MISSING;
                filesByName.put(statuses[0], statuses);
                stdoutListener.outputData(statuses);
            }
        } else if (gettingLocalFiles) {
            lastFileName += line;
        } else if (gettingSourceSafeFiles || gettingDifferentFiles) {
            if (line.startsWith("  ")) {
                flushLastFile();
            }
            lastFileName += line;
        }
    }
    
    /**
     * Propagate the error output.
     */
    public void outputLine(String line) {
        stderrNRListener.outputLine(line);
    }
    
}
