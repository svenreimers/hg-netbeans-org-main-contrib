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

package org.netbeans.modules.vcscore;

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.ref.Reference;
import java.text.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;

import org.openide.*;
import org.openide.util.actions.*;
import org.openide.util.NbBundle;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.Status;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.SharedClassObject;
import org.openide.util.UserQuestionException;
import org.openide.util.WeakListener;
import org.openide.util.WeakListeners;
import org.openide.windows.InputOutput;

import org.netbeans.api.queries.SharabilityQuery;

import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cache.CacheHandlerListener;
import org.netbeans.modules.vcscore.cache.CacheHandlerEvent;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.cache.CacheDir;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.cache.CacheReference;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.caching.*;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.grouping.AddToGroupDialog;
import org.netbeans.modules.vcscore.grouping.GroupUtils;
import org.netbeans.modules.vcscore.grouping.VcsGroupSettings;
import org.netbeans.modules.vcscore.objectintegrity.IntegritySupportMaintainer;
import org.netbeans.modules.vcscore.objectintegrity.VcsOISActivator;
import org.netbeans.modules.vcscore.objectintegrity.VcsObjectIntegritySupport;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSRegistryListener;
import org.netbeans.modules.vcscore.runtime.RuntimeFolderNode;
import org.netbeans.modules.vcscore.runtime.VcsRuntimeCommandsProvider;
import org.netbeans.modules.vcscore.search.VcsSearchTypeFileSystem;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.util.virtuals.VcsRefreshRequest;
import org.netbeans.modules.vcscore.util.virtuals.VirtualsDataLoader;
import org.netbeans.modules.vcscore.util.virtuals.VirtualsRefreshing;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.turbo.*;

/** Generic VCS filesystem.
 *
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public abstract class VcsFileSystem extends AbstractFileSystem implements VariableInputDialog.FilePromptDocumentListener,
                                                                          VcsSearchTypeFileSystem, VirtualsRefreshing,
                                                                          AbstractFileSystem.List, AbstractFileSystem.Info,
                                                                          AbstractFileSystem.Change, FileSystem.Status,
                                                                          CommandExecutionContext, CacheHandlerListener,
                                                                          FileObjectExistence, VcsOISActivator, Serializable,
                                                                          FileSystem.HtmlStatus,
                                                                          TurboListener {

    public static interface IgnoreListSupport {

        public ArrayList createInitialIgnoreList ();
        public java.util.List createIgnoreList (String fileName, java.util.List parentIgnoreList);
    }


    private static final int BADGE_ICON_SHIFT_X = 16;
    private static final int BADGE_ICON_SHIFT_Y = 8;

    public static final String VCS_PROVIDER_ATTRIBUTE = "VCS Provider";
    public static final String VCS_FILESYSTEM_ICON_BASE = "VCS Icon Base";

    //public static final String PROP_ROOT = "root"; // NOI18N
    public static final String PROP_VARIABLES = "variables"; // NOI18N
    public static final String PROP_COMMANDS = "commands"; // NOI18N
    public static final String PROP_DEBUG = "debug"; // NOI18N
    public static final String PROP_CALL_EDIT = "edit"; // NOI18N
    public static final String PROP_CALL_EDIT_PROMPT = "editPrompt"; // NOI18N
    public static final String PROP_CALL_LOCK = "lock"; // NOI18N
    public static final String PROP_CALL_LOCK_PROMPT = "lockPrompt"; // NOI18N
    public static final String PROP_EXPERT_MODE = "expertMode"; // NOI18N
    public static final String PROP_PROCESS_UNIMPORTANT_FILES = "processUnimportantFiles"; // NOI18N
    public static final String PROP_ANNOTATION_PATTERN = "annotationPattern"; // NOI18N
    public static final String PROP_ANNOTATION_TYPES = "annotationTypes"; // NOI18N
    public static final String PROP_COMMAND_NOTIFICATION = "commandNotification"; // NOI18N
    public static final String PROP_PASSWORD = "password"; // NOI18N
    public static final String PROP_REMEMBER_PASSWORD = "rememberPassword"; // NOI18N
    public static final String PROP_CREATE_RUNTIME_COMMANDS = "createRuntimeCommands"; // NOI18N
    public static final String PROP_CREATE_VERSIONING_EXPLORER = "createVersioningExplorer"; // NOI18N
    public static final String PROP_CREATE_BACKUP_FILES = "createBackupFiles"; // NOI18N
    public static final String PROP_PROMPT_FOR_VARS_FOR_EACH_FILE = "promptForVarsForEachFile"; // NOI18N
    public static final String PROP_VCS_REFRESH_TIME = "vcsRefreshTime"; // NOI18N
    public static final String PROP_FILE_FILTER = "fileFilter"; // NOI18N
    
    protected static final String PROP_USE_UNIX_SHELL = "useUnixShell"; // NOI18N
    protected static final String PROP_NOT_MODIFIABLE_STATUSES = "notModifiableStatuses"; // NOI18N

    public static final String VAR_TRUE = "true"; // NOI18N
    public static final String VAR_FALSE = "false"; // NOI18N

    /**
     * The value of this variable is used to surround file names.
     */
    public static final String VAR_QUOTING = "QUOTE"; // NOI18N
    /**
     * The test on this variable can be performed in the exec string to decide
     * what options to use or what to ask the user for. When the expert mode
     * is on, this variable is non empty.
     */
    public static final String VAR_EXPERT_MODE = "EXPERT_MODE"; // NOI18N

    /**
     * If Expert mode is off.. this variable carries information that CTrl was pressed down when
     * displaying the Vcsaction = user requested to have the advanced options displayed..
     */

    public static final String VAR_CTRL_DOWN_IN_ACTION = "CTRL_DOWN_IN_ACTION";
    /**
     * This variable can contain the display name, which is prepended to the
     * root path of the file system to create the display name.
     */
    public static final String VAR_FS_DISPLAY_NAME = "FS_DISPLAY_NAME"; // NOI18N
    /**
     * This variable can contain the whole annotation of file system display name.
     */
    public static final String VAR_FS_DISPLAY_NAME_ANNOTATION = "FS_DISPLAY_NAME_ANNOTATION"; // NOI18N
    /**
     * This variable can contain the whole annotation of file system system name.
     */
    public static final String VAR_FS_SYSTEM_NAME_ANNOTATION = "FS_SYSTEM_NAME_ANNOTATION"; // NOI18N

    /**
     * This is a prefix, for environment variable. If a variable name starts with
     * this prefix, it is considered as an environment variable, and its value
     * is added to the process environment.
     */
    public static final String VAR_ENVIRONMENT_PREFIX = "ENVIRONMENT_VAR_";
    /**
     * This is a prefix, for environment variable, that should be removed from the command
     * environment. If a variable name starts with this prefix, variable of this name is
     * removed from the list of environment variables. It's value is ignored.
     */
    public static final String VAR_ENVIRONMENT_REMOVE_PREFIX = "ENVIRONMENT_REMOVE_VAR_";

    public static final String VAR_STATUS_SCHEDULED_ADD = "STATUS_SCHEDULED_ADD";
    public static final String VAR_STATUS_SCHEDULED_REMOVE = "STATUS_SCHEDULED_REMOVE";
    
    /** The user name of current user, that locks files.
     *  It's supposed that LOCK command will lock files with that user name.
     *  If empty, System.getProperty("user.name") is used instead. */
    public static final String VAR_LOCKER_USER_NAME = "LOCKER_USER_NAME"; // NOI18N

    protected static final int REFRESH_TIME = 0; // Disabled by default (see #28352).
    protected volatile int refreshTimeToSet = REFRESH_TIME;

    private static final String LOCAL_FILES_ADD_VAR = "SHOWLOCALFILES"; // NOI18N
    private static final String LOCK_FILES_ON = "LOCKFILES"; // NOI18N
    private static final String PROMPT_FOR_LOCK_ON = "PROMPTFORLOCK"; // NOI18N
    private static final String EDIT_FILES_ON = "CALLEDITONFILES"; // NOI18N
    private static final String PROMPT_FOR_EDIT_ON = "PROMPTFOREDIT"; // NOI18N

    private static final String DEFAULT_QUOTING_VALUE = "\\\""; // NOI18N

    private static final String DEFAULT_CACHE_ID = "VCS_Cache"; // NOI18N

    private static final String FILE_PROMPT_PREFIX = "tmppf"; // NOI18N
    /*
     * The name of the variable for the global additional parameters.
     *
    private static final String USER_GLOBAL_PARAM = "USER_GLOBAL_PARAM";
    /*
     * The name of the variable for the local additional parameters.
     *
    private static final String USER_PARAM = "USER_PARAM";
     */
    private static int last_refreshTime = REFRESH_TIME;
    private static volatile File last_rootFile = new File (System.getProperty("user.home")); // NOI18N

    private static boolean last_useUnixShell = false;

    private transient Hashtable commandsByName=null;
    /** root file */
    private volatile File rootFile = last_rootFile; // NOI18N

    private boolean useUnixShell = last_useUnixShell;

    /** is read only */
    private boolean readOnly;
    protected Hashtable variablesByName = new Hashtable ();

    private boolean lockFilesOn = false;
    private boolean promptForLockOn = true;
    //private volatile boolean promptForLockResult = false;
    private boolean promptForEditOn = true;
    private boolean callEditFilesOn = true;

    private boolean debug = false;

    /** user variables Vector<String> 'name=value' */
    private Vector variables = new Vector(10);
    /**
     * This variable stores the environment variables and their values
     * in the form: "VAR1=Value1", "VAR2=Value2", etc.
     */
    private transient String[] environmentVars = null;

    private String password = null;

    private boolean rememberPassword = false;

    /** Advanced confgiguration.
     * Not used any more, use commandsRoot instead.
     * Needed only for deserialization from NetBeans 3.1 and older
     */
    private Object advanced = null;
    private transient CommandsTree commandsRoot = null;

    private String cacheID = null;
    protected transient FileCacheProvider cache = null;
    protected transient FileStatusProvider statusProvider = null;

    private int[] multiFilesAnnotationTypes = null;
    private String annotationPattern = null;

    //private long cacheId = 0;
    //private String cacheRoot = null; // NOI18N

    private transient VcsFactory factory = null;
    private transient VcsCommandsProvider commandsProvider = new DefaultVcsCommandsProvider(new CommandsTree(null));

    private transient Object processUnimportantFilesLock = new Object();
    private Boolean processUnimportantFiles = Boolean.FALSE;

    /**
     * Table used to transfer status name obtained by refresh to the name presented to the user.
     * Can be used to make localization of file statuses.
     *
    protected HashMap possibleFileStatusesMap = null;
     */

    /** Original file status infos provided by file status provider, that should
     * be retained. */
    private transient Set origPossibleFileStatusInfos = null;
    /** Possible file status infos. */
    private transient Set possibleFileStatusInfos = null;
    /** Map of status names obtained by refresh to the FileStatusInfo object. */
    private transient Map possibleFileStatusInfoMap = null;
    /** Map of generic names (in FileStatusInfo class) to possible VCS status names. */
    private transient Map genericStatusTranslation = null;
    /** The lock for synchronized access to structures holding possible file status information. */
    private transient Object possibleFileStatusesLock = new Object();

    /**
     * The table used to get the icon badge on the objects' data node.
     * The table contains the original statuses (obtained from the VCS tool)
     * as keys and the icons of type <code>Image</code> as values.
     */
    //protected transient HashMap statusIconMap = null;

    /**
     * The default icon badge, that is used when no icon can be obtained from {@link statusIconMap}.
     */
    protected transient Image statusIconDefault = null;

    protected boolean ready=false;
    private boolean askIfDownloadRecursively = true;
    private volatile Hashtable numDoAutoRefreshes = new Hashtable();

    protected transient boolean deserialized; // Whether this class was created by deserialization

    /**
     * Whether to prompt the user for variables for each selected file. Value of this variable
     * will be the default value in the VariableInputDialog and changing the value there will
     * change the value of this variable.
     */
    private boolean promptForVarsForEachFile = false;

    private Vector tempFiles = new Vector();

    /**
     * Additional user parameters to the command. These are global parameters to all commands.
     * Parameters local to each command are stored in UserCommand.userParams.
     * The user is asked for them when acceptUserParams = true
     */
    private volatile String[] userParams = null;
    /**
     * Labels to additional user parameters.
     */
    private volatile String[] userParamsLabels = null;
    /**
     * Labels to local additional user parameters.
     */
    private volatile String[] userLocalParamsLabels = null;

    private volatile boolean acceptUserParams = false;

    /** The expert mode. When true, the user might be prompted for other options.
     */
    private boolean expertMode = false;

    /**
     * Whether to run command when doing refresh of folders. Recommended to turn this property off when working off-line.
     */
    //private boolean doCommandRefresh = true;

    // CommandsPool reference kept for compatibility only.
    private volatile transient CommandsPool commandsPool = null;
    private Integer numberOfFinishedCmdsToCollect = new Integer(RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT);
    private int versioningFileSystemMessageLength = 50;
    private boolean versioningFileSystemShowMessage = true;
    private boolean versioningFileSystemShowLocalFiles = true;
    private boolean versioningFileSystemShowUnimportantFiles = false;
    private boolean versioningFileSystemShowDeadFiles = false;

    private transient ArrayList revisionListeners;
    private transient Object revisionListenersLock;

    /** The offline mode.
     * Whether to run command when doing refresh of folders.
     * Recommended to turn this property on when working off-line.
     */
    private volatile boolean offLine; // is set in the constructor
    private volatile int autoRefresh; // is set in the constructor
    private volatile boolean hideShadowFiles; // is set in the constructor

    private transient PropertyChangeListener settingsChangeListener = null;

    private transient FSRegistryListener addRemoveFSListener;

    private VariableValueAdjustment varValueAdjustment;

    private volatile boolean commandNotification = true;

    private Collection notModifiableStatuses = Collections.EMPTY_SET;
    private String missingFileStatus = null;
    private String missingFolderStatus = null;
    private Collection notMissingableFileStatuses = Collections.EMPTY_SET;
    private Collection notMissingableFolderStatuses = Collections.EMPTY_SET;
    
    private Map lockedFilesToBeModified;

    private Boolean createRuntimeCommands = Boolean.TRUE;

    private Boolean createVersioningSystem = Boolean.FALSE;

    private transient VcsActionSupporter actionSupporter = null;

    private transient IgnoreListSupport ignoreListSupport = null;

    private transient IntegritySupportMaintainer integritySupportMaintainer = null;

    private Boolean createBackupFiles = null;

    private transient VersioningFileSystem versioningSystem = null;

    private transient AbstractFileSystem.List vcsList = null;
    
    private transient LocalFilenameFilter localFilenameFilter = null;

    /** The refresh request instead of the standard refreshing. */
    private transient VcsRefreshRequest refresher = new VcsRefreshRequest (this, 0, this);

    /** The InputOutput used for display of VCS commands output */
    private transient InputOutput cmdIO = null;
    
    /** The display name of the filesystem */
    private transient String displayName;
    
    private String preferredSystemName = null;


    public boolean isLockFilesOn () {
        return lockFilesOn && isEnabledLockFiles();
    }
    public void setLockFilesOn (boolean lock) {
        if (lock != lockFilesOn) {
            lockFilesOn = lock;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(LOCK_FILES_ON);
            if (var == null) {
                var = new VcsConfigVariable(LOCK_FILES_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(lock ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_LOCK, !lockFilesOn ? Boolean.TRUE : Boolean.FALSE, lockFilesOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    public boolean isPromptForLockOn () { return promptForLockOn; }
    public void setPromptForLockOn (boolean prompt) {
        if (prompt != promptForLockOn) {
            promptForLockOn = prompt;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(PROMPT_FOR_LOCK_ON);
            if (var == null) {
                var = new VcsConfigVariable(PROMPT_FOR_LOCK_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(prompt ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_LOCK_PROMPT, !promptForLockOn ? Boolean.TRUE : Boolean.FALSE, promptForLockOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    public boolean getAskIfDownloadRecursively () { return askIfDownloadRecursively; }
    public void setAskIfDownloadRecursively (boolean ask) { askIfDownloadRecursively = ask; }
    public boolean isCallEditFilesOn() {
        return callEditFilesOn && isEnabledEditFiles();
    }
    public void setCallEditFilesOn(boolean edit) {
        if (edit != callEditFilesOn) {
            callEditFilesOn = edit;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(EDIT_FILES_ON);
            if (var == null) {
                var = new VcsConfigVariable(EDIT_FILES_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(edit ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_EDIT, !callEditFilesOn ? Boolean.TRUE : Boolean.FALSE, callEditFilesOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    public boolean isPromptForEditOn () { return promptForEditOn; }
    public void setPromptForEditOn (boolean prompt) {
        if (prompt != promptForEditOn) {
            promptForEditOn = prompt;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(PROMPT_FOR_EDIT_ON);
            if (var == null) {
                var = new VcsConfigVariable(PROMPT_FOR_EDIT_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(prompt ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_EDIT_PROMPT, !promptForEditOn ? Boolean.TRUE : Boolean.FALSE, promptForEditOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    public boolean isUseUnixShell () { return useUnixShell; }

    public boolean isEnabledLockFiles() {
        return (getCommandSupport(VcsCommand.NAME_LOCK) != null);
    }

    public boolean isEnabledEditFiles() {
        return (getCommandSupport(VcsCommand.NAME_EDIT) != null);
    }

    protected void setUseUnixShell (boolean unixShell) {
        if (unixShell != useUnixShell) {
            useUnixShell = unixShell;
            last_useUnixShell = unixShell;
            firePropertyChange(PROP_USE_UNIX_SHELL, !unixShell ? Boolean.TRUE : Boolean.FALSE, unixShell ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public void setAcceptUserParams(boolean acceptUserParams) {
        this.acceptUserParams = acceptUserParams;
    }

    public boolean isAcceptUserParams() {
        return acceptUserParams;
    }

    public void setUserLocalParamsLabels(String[] labels) {
        userLocalParamsLabels = labels;
    }

    public String[] getUserLocalParamsLabels() {
        return userLocalParamsLabels;
    }

    public void setUserParamsLabels(String[] labels) {
        userParamsLabels = labels;
        userParams = new String[labels.length];
    }

    public String[] getUserParamsLabels() {
        return userParamsLabels;
    }

    public void setUserParams(String[] userParams) {
        this.userParams = userParams;
    }

    public String[] getUserParams() {
        return userParams;
    }

    public void setExpertMode(boolean expertMode) {
        if (expertMode != this.expertMode) {
            this.expertMode = expertMode;
            if (commandsProvider instanceof CommandsTree.Provider) {
                ((CommandsTree.Provider) commandsProvider).setExpertMode(expertMode);
            }
            setAcceptUserParams(expertMode);
            firePropertyChange(PROP_EXPERT_MODE, !expertMode ? Boolean.TRUE : Boolean.FALSE, expertMode ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public boolean isExpertMode() {
        return expertMode;
    }

    public void setCommandNotification(boolean commandNotification) {
        if (commandNotification != this.commandNotification) {
            this.commandNotification = commandNotification;
            firePropertyChange(PROP_COMMAND_NOTIFICATION, !commandNotification ? Boolean.TRUE : Boolean.FALSE, commandNotification ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public boolean isCommandNotification() {
        return commandNotification;
    }

    /**
     * Set whether to prompt the user for variables for each selected file. Set the
     * initial default value in the VariableInputDialog.
     */
    public void setPromptForVarsForEachFile(boolean promptForVarsForEachFile) {
        if (this.promptForVarsForEachFile != promptForVarsForEachFile) {
            this.promptForVarsForEachFile = promptForVarsForEachFile;
            firePropertyChange(PROP_PROMPT_FOR_VARS_FOR_EACH_FILE, !promptForVarsForEachFile ? Boolean.TRUE : Boolean.FALSE, promptForVarsForEachFile ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    /**
     * Whether to prompt the user for variables for each selected file. This value
     * will be the default value in the VariableInputDialog and changing the value
     * there will change the value returned.
     */
    public boolean isPromptForVarsForEachFile() {
        return promptForVarsForEachFile;
    }

    /**
     * Get the CommandsPool associated with this filesystem.
     * @deprecated For compatibility only
     */
    public CommandsPool getCommandsPool() {
        return commandsPool;
    }

    public void setProcessUnimportantFiles(boolean processUnimportantFiles) {
        boolean fire = false;
        Boolean old = null;
        synchronized (this.processUnimportantFilesLock) {
            if (processUnimportantFiles != this.processUnimportantFiles.booleanValue()) {
                old = this.processUnimportantFiles;
                this.processUnimportantFiles = processUnimportantFiles ? Boolean.TRUE : Boolean.FALSE;
                fire = true;
            }
        }
        if (fire) {
            firePropertyChange(PROP_PROCESS_UNIMPORTANT_FILES, old, this.processUnimportantFiles);
        }
    }

    public boolean isProcessUnimportantFiles() {
        synchronized (processUnimportantFilesLock) {
            return processUnimportantFiles.booleanValue();
        }
    }

    /** Instruct FS to refrest folders, XXX asynchronous operation. */
    public void refreshExistingFolders() {
        refreshExistingFolders(null);
    }

    protected void refreshExistingFolders(final String name) {
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                FileObject root;
                if (name == null) {
                    root = getRoot();
                } else {
                    root = findResource(name);
                }
                if (root == null) return ;
                Enumeration e = existingFileObjects(root);
                while (e.hasMoreElements()) {
                    FileObject fo = (FileObject) e.nextElement();
                    if (fo.isFolder()) {
                        fo.refresh(true);
                    }
                }
            }
        });
    }

    protected boolean isCreateBackupFilesSet() {
        return createBackupFiles != null;
    }

    public boolean isCreateBackupFiles() {
        return createBackupFiles != null && createBackupFiles.booleanValue();
    }

    public void setCreateBackupFiles(boolean createBackupFiles) {
        if (this.createBackupFiles == null || createBackupFiles != this.createBackupFiles.booleanValue()) {
            this.createBackupFiles = createBackupFiles ? Boolean.TRUE : Boolean.FALSE;
            firePropertyChange(PROP_CREATE_BACKUP_FILES, null, this.createBackupFiles);
        }
    }

    public void setOffLine(boolean offLine) {
        if (offLine != this.offLine) {
            this.offLine = offLine;
            firePropertyChange (GeneralVcsSettings.PROP_OFFLINE, !offLine ? Boolean.TRUE : Boolean.FALSE, offLine ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public boolean isOffLine() {
        return offLine;
    }

    /** Get the mode of auto refresh. */
    public int getAutoRefresh() {
        return autoRefresh;
    }

    /** Set the mode of auto refresh. */
    public void setAutoRefresh(int newAuto) {
        if (newAuto != autoRefresh) {
            int oldAuto = autoRefresh;
            autoRefresh = newAuto;
            firePropertyChange (GeneralVcsSettings.PROP_AUTO_REFRESH, new Integer(oldAuto), new Integer(autoRefresh));
        }
    }

    /** Whether to hide files, which does not exist locally. */
    public boolean isHideShadowFiles() {
        return hideShadowFiles;
    }

    /** Set whether to hide files, which does not exist locally. */
    public void setHideShadowFiles(boolean hideShadowFiles) {
        if (hideShadowFiles != this.hideShadowFiles) {
            this.hideShadowFiles = hideShadowFiles;
            firePropertyChange (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES, !hideShadowFiles ? Boolean.TRUE : Boolean.FALSE, hideShadowFiles ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public boolean isShowDeadFiles() {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(Variables.SHOW_DEAD_FILES);
        if (var == null) {
            return false;
        } else {
            return (var.getValue().trim().length() > 0);
        }
    }

    public void setShowDeadFiles(boolean showDeadFiles) {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(Variables.SHOW_DEAD_FILES);
        if (var == null) {
            if (showDeadFiles) {
                var = new VcsConfigVariable(Variables.SHOW_DEAD_FILES, null, "true", false, false, false, null);
                synchronized (this) {
                    variablesByName.put(Variables.SHOW_DEAD_FILES, var);
                    variables.add(var);
                }
            }
        } else {
            var.setValue(showDeadFiles ? "true" : "");
        }
    }

    public int getNumberOfFinishedCmdsToCollect() {
        //System.out.println("VcsFileSystem.getNumberOfFinishedCmdsToCollect() = "+numberOfFinishedCmdsToCollect.intValue());
        return numberOfFinishedCmdsToCollect.intValue();
    }

    public void setNumberOfFinishedCmdsToCollect(int numberOfFinishedCmdsToCollect) {
        this.numberOfFinishedCmdsToCollect = new Integer(numberOfFinishedCmdsToCollect);
        //System.out.println("VcsFileSystem.setNumberOfFinishedCmdsToCollect("+numberOfFinishedCmdsToCollect+")");
        firePropertyChange(org.netbeans.modules.vcscore.runtime.RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, null, null);
    }

    public int getVFSMessageLength() {
        return versioningFileSystemMessageLength;
    }
    public void setVFSMessageLength(int newVal) {
        versioningFileSystemMessageLength = newVal;
    }

    public boolean getVFSShowLocalFiles() {
        return versioningFileSystemShowLocalFiles;
    }
    public void setVFSShowLocalFiles(boolean newVal) {
        versioningFileSystemShowLocalFiles = newVal;
    }

    public boolean getVFSShowMessage() {
        return versioningFileSystemShowMessage;
    }
    public void setVFSShowMessage(boolean newVal) {
        versioningFileSystemShowMessage = newVal;
    }

    public boolean getVFSShowUnimportantFiles() {
        return versioningFileSystemShowUnimportantFiles;
    }
    public void setVFSShowUnimportantFiles(boolean newVal) {
        versioningFileSystemShowUnimportantFiles = newVal;
    }

    public boolean getVFSShowDeadFiles() {
        return versioningFileSystemShowDeadFiles;
    }
    public void setVFSShowDeadFiles(boolean newVal) {
        versioningFileSystemShowDeadFiles = newVal;
    }

    public void addRevisionListener(RevisionListener listener) {
        synchronized (revisionListenersLock) {
            if (revisionListeners == null) revisionListeners = new ArrayList();
            revisionListeners.add(listener);
        }
    }

    public boolean removeRevisionListener(RevisionListener listener) {
        synchronized (revisionListenersLock) {
            if (revisionListeners == null) return false;
            return revisionListeners.remove(listener);
        }
    }

    public void fireRevisionsChanged(RevisionEvent event) {//int whatChanged, FileObject fo, Object info) {
        java.util.List listeners;
        synchronized (revisionListenersLock) {
            if (revisionListeners == null) return;
            listeners = new ArrayList(revisionListeners);
        }
        for(Iterator it = listeners.iterator(); it.hasNext(); ) {
            //((RevisionListener) it.next()).revisionsChanged(whatChanged, fo, info);
            ((RevisionListener) it.next()).stateChanged(event);
        }
    }

    protected void setNotModifiableStatuses(Collection notModifiableStatuses) {
        this.notModifiableStatuses = notModifiableStatuses;
        firePropertyChange(PROP_NOT_MODIFIABLE_STATUSES, null, notModifiableStatuses);
    }

    protected void setMissingFileStatus(String missingFileStatus) {
        this.missingFileStatus = missingFileStatus;
    }

    protected void setMissingFolderStatus(String missingFolderStatus) {
        this.missingFolderStatus = missingFolderStatus;
    }

    protected void setNotMissingableFileStatuses(Collection notMissingableFileStatuses) {
        this.notMissingableFileStatuses = notMissingableFileStatuses;
    }

    protected void setNotMissingableFolderStatuses(Collection notMissingableFolderStatuses) {
        this.notMissingableFolderStatuses = notMissingableFolderStatuses;
    }

    /**
     * Get whether to perform the auto-refresh in the given directory path.
     * @param path The given directory path
     */
    public boolean getDoAutoRefresh(String path) {
        synchronized (numDoAutoRefreshes) {
            int numDoAutoRefresh = getNumDoAutoRefresh(path);
            if (numDoAutoRefresh > 0) {
                numDoAutoRefresh--;
                if (numDoAutoRefresh > 0) setNumDoAutoRefresh(numDoAutoRefresh, path);
                else removeNumDoAutoRefresh(path);
                return (numDoAutoRefresh == 0);
            } else { return true;} // nothing known about that path, but refresh requested. // NOI18N
        }
    }

    /**
     * Set how many times I call a command after which the auto-refresh is executed in the given path.
     * @param numDoAutoRefresh The number of auto-refreshes
     * @param path The given directory path
     */
    public void setNumDoAutoRefresh(int numDoAutoRefresh, String path) {
        synchronized (numDoAutoRefreshes) {
            numDoAutoRefreshes.put(path, new Integer(numDoAutoRefresh));
        }
    }

    /**
     * Get the number of command calls after which perform the auto-refresh command in the given path.
     * @param path The given path
     */
    public int getNumDoAutoRefresh(String path) {
        synchronized (numDoAutoRefreshes) {
            Integer numDoAutoRefreshObj = (Integer) numDoAutoRefreshes.get(path);
            int numDoAutoRefresh = 0;
            if (numDoAutoRefreshObj != null) {
                numDoAutoRefresh = numDoAutoRefreshObj.intValue();
            }
            return numDoAutoRefresh;
        }
    }

    /**
     * Remove the number of command calls after which perform the auto-refresh command in the given path.
     * @param path The given path
     */
    public void removeNumDoAutoRefresh(String path) {
        if (path == null) return;
        synchronized (numDoAutoRefreshes) {
            numDoAutoRefreshes.remove(path);
        }
    }

    /** Set the number of milliseconds between automatic refreshes of the
     * directory structure. Replaces the functionality of setRefreshTime.
     * You should not use setRefreshTime() in subclasses of VcsFileSystem at all!
     * Use this method instead.
     *
     * @param ms number of milliseconds between two refreshes; if <code><= 0</code> then refreshing is disabled
     */
    protected final void setVcsRefreshTime(int ms) {
        setVcsRefreshTime(ms, true);
    }

    private final void setVcsRefreshTime(int ms, boolean fireChange) {
        int oldMS = getVcsRefreshTime();
        refreshTimeToSet = ms;
        if (oldMS == ms) return ;
        refresher.setRefreshTime(Boolean.getBoolean("netbeans.debug.heap") ? 0 : ms); // NOI18N
        if (fireChange) firePropertyChange(PROP_VCS_REFRESH_TIME, null, null);
    }

    /** Get the number of milliseconds between automatic refreshes of the
     * directory structure. Replaces the functionality of getRefreshTime.
     * You should not use getRefreshTime() in subclasses of VcsFileSystem at all!
     * Use this method instead.
     * By default, automatic refreshing is disabled.
     *
     * @return the number of milliseconds, or <code>0</code> if refreshing is disabled
     */
    protected final int getVcsRefreshTime() {
        VcsRefreshRequest r = refresher;
        return r == null ? 0 : r.getRefreshTime ();
     }

    /**
     * Returns all existing files in the file system (both folders and data).
     * Please note, that the name of this method is misguided. It does not return
     * only existing folders, but also existing files.
     */
    public Enumeration getExistingFolders() {
        return this.existingFileObjects(getRoot());
    }

    /**
     * Returns all existing files in the file system.
     */
    public Enumeration getExistingFiles() {
        return this.existingFileObjects(getRoot());
    }

    /**
     * Do the refresh of a folder.
     */
    public void doVirtualsRefresh(FileObject fo) {
        fo.refresh();
    }

    /** Return the working directory of the file system.
     *  To that, relative mountpoints are added later to enable compilation etc.
     */
    public String getFSRoot() {
      return VcsFileSystem.substractRootDir(getRootDirectory().toString(), getRelativeMountPoint());
    }


    public String getRelativeMountPoint() {
      VcsConfigVariable module = (VcsConfigVariable) variablesByName.get("MODULE");
      if (module == null) return "";
      return module.getValue();
    }

    public void setRelativeMountPoint(final String module) throws PropertyVetoException, IOException {
        runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                String moduleToSet = module;
                if (moduleToSet.length() > 0) {
                    moduleToSet = moduleToSet.replaceAll("/{2,}+", "/");
                    moduleToSet = moduleToSet.replaceAll("\\\\{2,}+", "\\");
                    if (moduleToSet.endsWith("/") || moduleToSet.endsWith("\\")) {
                        moduleToSet = moduleToSet.substring(0, moduleToSet.length() - 1);
                    }
                    if (moduleToSet.startsWith("/") || moduleToSet.startsWith("\\")) {
                        moduleToSet = moduleToSet.substring(1);
                    }
                }
                synchronized (this) {
                    //System.out.println("setRelativeMountPoint('"+module+"' -> '"+moduleToSet+"')");
                    Hashtable vars = variablesByName;
                    String root = VcsFileSystem.this.getFSRoot();
                    VcsConfigVariable mod = (VcsConfigVariable) vars.get("MODULE");
                    if (mod != null && moduleToSet.equals(mod.getValue())) return ;
                    if (mod == null) {
                        mod = new VcsConfigVariable("MODULE", "", moduleToSet, false, false, false, null);
                        variables.add(mod);
                        variablesByName.put("MODULE", mod);
                    }
                    String oldModule = mod.getValue();
                    mod.setValue(moduleToSet);
                    try {
                        setRootDirectory(new File(root));
                    } catch (PropertyVetoException prop) {
                        mod.setValue(oldModule);
                        throw (IOException) ErrorManager.getDefault().annotate(new IOException(), prop);
                    } catch (IOException io) {
                        mod.setValue(oldModule);
                        throw io;
                    }
                }
            }
        });
    }


    /*
     * Mark the file as being unimportant.
     * Unimplemented, obsoleted by SharabilityQuery
     * @param name the file name
     */
    public void markUnimportant(String name) {
    }

    /**
     * Returns true when the given file is sharable.
     * @param name The file name relative to FS root
     */
    public boolean isImportant(String name) {
        int sharability = SharabilityQuery.getSharability(getFile(name));
        return sharability != SharabilityQuery.NOT_SHARABLE;
    }

    private static RequestProcessor statusRequestProcessor;
    private static final Object STATUS_REQUEST_PROCESSOR_LOCK = new Object();

    /**
     * Get a special request processor, that is used to refresh the file status.
     * This is necessary to prevent some kind of deadlocks, that can occure
     * if the default RequestProcessor is used.
     */
    public static RequestProcessor getStatusChangeRequestProcessor() {
        synchronized (STATUS_REQUEST_PROCESSOR_LOCK) {
            if (statusRequestProcessor == null) {
                statusRequestProcessor = new RequestProcessor("VCS Status Update Request Processor"); // NOI18N
            }
        }
        return statusRequestProcessor;
    }

    /**
     * Perform refresh of status information on all children of a directory
     * @param path the directory path
     * @param recursivey whether to refresh recursively
     */
    public void statusChanged (final String path, final boolean recursively) {
        getStatusChangeRequestProcessor().post(new Runnable() {
            public void run() {
                FileObject fo = findExistingResource(path);
                if (fo == null) return;
                Enumeration enum = existingFileObjects(fo);
                //Enumeration enum = fo.getChildren(recursively);
                HashSet hs = new HashSet();
                if (enum.hasMoreElements()) {
                    // First add the root FileObject
                    hs.add(enum.nextElement());
                }
                while(enum.hasMoreElements()) {
                    //fo = (FileObject) enum.nextElement();
                    //hs.add(fo);
                    FileObject chfo = (FileObject) enum.nextElement();
                    if (!recursively && !fo.equals(chfo.getParent())) break;
                    hs.add(chfo);
                }
                Set s = Collections.synchronizedSet(hs);
                fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, s, true, true));
                checkScheduledStates(s);
                checkVirtualFiles(s);
            }
        });
    }

    private transient StatusChangeUpdater statusUpdateRunnable;
    private transient RequestProcessor.Task statusUpdateTask;

    /**
     * Fire status chnage information of a file. Batch to minimally 200ms chunks.
     * @param name the full file name (path to root)
     */
    public void statusChanged (final String name) {
        //System.out.println("statusChanged("+name+")");
        if (statusUpdateTask == null) {
            statusUpdateRunnable = new StatusChangeUpdater();
            statusUpdateTask = getStatusChangeRequestProcessor().post(
                                statusUpdateRunnable, 200, Thread.MIN_PRIORITY);
        }
        statusUpdateRunnable.addNameToUpdate(name);
        statusUpdateTask.schedule(200);
    }

    /**
     * Find the existing resources on this file system.
     * This method is not much efficient, because it collects all existing
     * FileObjects and search them for the desired ones.
     * @param names The collection of file names
     * @return The set of FileObjects
     */
    private Set findExistingResources(Collection names) {
        Enumeration enum = existingFileObjects(getRoot());
        Set fos = new HashSet();
        int n = names.size();
        int i = 0;
        while (enum.hasMoreElements() && i < n) {
            FileObject obj = (FileObject) enum.nextElement();
            String name = obj.getPath();
            if (names.contains(name)) {
                fos.add(obj);
                i++;
            }
        }
        return fos;
    }

    /**
     * Find the existing resource on this file system.
     * This method is not much efficient, because it collects all existing
     * FileObjects and search them for the desired one.
     */
    public FileObject findExistingResource(String name) {
        Enumeration enum = existingFileObjects(getRoot());
        FileObject fo = null;
        while (enum.hasMoreElements()) {
            FileObject obj = (FileObject) enum.nextElement();
            if (name.equals(obj.getPath())) {
                fo = obj;
                break;
            }
        }
        return fo;
    }

    /**
     * Check the scheduled states for the set of file objects.
     * Files scheduled for ADD are controlled by this method. When they change their
     * status, they are removed from the list of scheduled files.
     * @param fos the set of files to check.
     */
    private void checkScheduledStates(Set fos) {
        FileStatusProvider status = getStatusProvider();
        if (status == null) return ;
        VcsConfigVariable schVar = (VcsConfigVariable) variablesByName.get(VAR_STATUS_SCHEDULED_ADD);
        String scheduledStatusAdd = (schVar != null) ? schVar.getValue() : null;
        schVar = (VcsConfigVariable) variablesByName.get(VAR_STATUS_SCHEDULED_REMOVE);
        String scheduledStatusRemove = (schVar != null) ? schVar.getValue() : null;
        //System.out.println("checkScheduledStates(): scheduledStatusAdd = "+scheduledStatusAdd+", scheduledStatusRemove = "+scheduledStatusRemove);
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            //System.out.println("checkScheduledStates("+fo.getPackageNameExt('/', '.')+")");
            String attr = (String) fo.getAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR);
            //System.out.println("attr("+VcsAttributes.VCS_SCHEDULED_FILE_ATTR+") = "+attr);
            if (VcsAttributes.VCS_SCHEDULING_ADD.equals(attr) && scheduledStatusAdd != null &&
                !scheduledStatusAdd.equals(status.getFileStatus(fo.getPath())) &&
                isSchedulingDone(fo.getPath())) {
                try {
                    fo.setAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR, null);
                } catch (IOException exc) {}
                removeScheduledFromPrimary(fo, 1);
            }
            /*
            if (VcsAttributes.VCS_SCHEDULING_REMOVE.equals(attr) && scheduledStatusRemove != null &&
                !scheduledStatusRemove.equals(status.getFileStatus(fo.getPackageNameExt('/', '.')))) {
                try {
                    fo.setAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR, null);
                } catch (IOException exc) {}
                removeScheduledFromPrimary(fo, 0);
            }
             */
        }
    }

    /**
     * Remove a file from the list of scheduled files.
     * @param fo the scheduled file object
     * @param id the scheduling ID (0 for removed, 1 for added).
     */
    private static void removeScheduledFromPrimary(FileObject fo, int id) {
        DataObject dobj;
        try {
            dobj = DataObject.find(fo);
        } catch (org.openide.loaders.DataObjectNotFoundException exc) {
            return ;
        }
        FileObject primary = dobj.getPrimaryFile();
        Set[] scheduled = (Set[]) primary.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
        if (scheduled != null && scheduled[id] != null) {
            scheduled[id].remove(fo.getPath());
            scheduled = cleanScheduledAttrs(scheduled);
            try {
                primary.setAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR, scheduled);
                if (scheduled == null) {
                    primary.setAttribute(VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                }
            } catch (IOException exc) {}
            // We have removed some sec. files and we must assure, that
            // the corresponding node will refresh it's status annotation:
            try {
                FileSystem fs = fo.getFileSystem();
                if (fs instanceof VcsFileSystem) {
                    ((VcsFileSystem) fs).statusChanged(primary.getPath());
                }
            } catch (FileStateInvalidException fsiex) {
                // Ignored. No need to refresh invalid file.
            }
        }
    }

    /**
     * Remove a file from the list of scheduled files.
     * @param scheduledFile the scheduled file name
     * @param primaryFile the associated primary file name
     * @param id the scheduling ID (0 for removed, 1 for added).
     */
    private void removeScheduledFromPrimary(String scheduledFile, String primaryFile, int id) {
        Set[] scheduled = (Set[]) attr.readAttribute(primaryFile, VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
        if (scheduled != null && scheduled[id] != null) {
            scheduled[id].remove(scheduledFile);
            scheduled = cleanScheduledAttrs(scheduled);
            try {
                attr.writeAttribute(primaryFile, VcsAttributes.VCS_SCHEDULED_FILES_ATTR, scheduled);
                if (scheduled == null) {
                    attr.writeAttribute(primaryFile, VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                }
            } catch (IOException exc) {}
            // We have removed some sec. files and we must assure, that
            // the corresponding node will refresh it's status annotation:
            statusChanged(primaryFile);
        }
    }

    public void disableRefresh() {
        synchronized (this) {
            refreshTimeToSet = getVcsRefreshTime();
            if (refresher != null) refresher.setRefreshTime(0); // NOI18N
            //setVcsRefreshTime(0, false);
        }
    }

    public void enableRefresh() {
        synchronized (this) {
            if (refresher != null) refresher.setRefreshTime(Boolean.getBoolean("netbeans.debug.heap") ? 0 : refreshTimeToSet); // NOI18N
            //setVcsRefreshTime(refreshTimeToSet, false);
        }
    }

    public void setRefreshTimeToSet() {
        setVcsRefreshTime(refreshTimeToSet);
    }

    public void setCustomRefreshTime (int time) {
        if (isValid ()) {
            //System.out.println("Filesystem valid, setting the refresh time to "+time); // NOI18N
            setVcsRefreshTime (time);
        } else {
            //System.out.println("Filesystem not valid yet for refresh time "+time); // NOI18N
            refreshTimeToSet = time;
        }
        last_refreshTime = time;
    }

    public int getCustomRefreshTime () {
        if (isValid ()) {
            //System.out.println("Filesystem valid, getting the refresh time "+getVcsRefreshTime ()); // NOI18N
            return getVcsRefreshTime ();
        } else {
            return refreshTimeToSet;
        }
    }

    public void setZeroRefreshTime() {
        setVcsRefreshTime(0);
    }

    protected InputOutput getCommandsIO () {
        if (cmdIO == null) {
            cmdIO = org.openide.windows.IOProvider.getDefault().getIO(
                        NbBundle.getMessage(VcsFileSystem.class, "LBL_VCS_Output"),
                        false);
            cmdIO.setErrSeparated(false);
        }
        return cmdIO;
    }

    /**
     * Clear the debug output.
     */
    public void debugClear() {
        if (getDebug()) {
            try {
                getCommandsIO().getOut().reset();
            } catch (IOException ioex) {}
        }
    }


    /**
     * Print a debug output. If the debug property is true, the message
     * is printed to the Output Window.
     * @param msg The message to print out.
     */
    public void debug(String msg) {
        if (getDebug()) {
            getCommandsIO().getOut().println(msg);
        }
    }

    /**
     * Print an error output. Force the message to print to the Output Window.
     * The debug property is not considered.
     * @param msg the message to print out.
     */
    public void debugErr(String msg) {
        InputOutput out = getCommandsIO();
        out.getErr().println(msg);
        out.select();
    }

    /** Creates Reference. In FileSystem, which subclasses AbstractFileSystem, you can overload method
     * createReference(FileObject fo) to achieve another type of Reference (weak, strong etc.)
     * @param fo is FileObject. It`s reference yourequire to get.
     * @return Reference to FileObject
     */
    protected java.lang.ref.Reference createReference(final FileObject fo) {
        Reference result = null;

        if (Turbo.implemented()) {
            result = Turbo.createFileReference(fo);
            String fullName = fo.getPath();
            if (!getFile(fullName).exists()) {
                // When the file does not exist on disk, it must be virtual.
                ((FileReference)result).setVirtual (true);
            }
            return result;
        }

        // the old implementation
        if (cache != null) {
            result = cache.createReference(fo);
            String fullName = fo.getPath();
            if (!getFile(fullName).exists()) {
                // When the file does not exist on disk, it must be virtual.
                ((CacheReference)result).setVirtual (true);
            }
        } else {
            result =  super.createReference(fo);
        }
        String fullName = fo.getPath();
        return result;
    }

    /**
     * Utility method that find the fileobject reference and if it exists, retypes it to FileReference.
     * @param name pathname of the resource.
     * @return  the FileReference instance if one exists or null
     */
    protected final FileReference getFileReference(String name) {

        assert Turbo.implemented();
        Reference ref = findReference(name);
        if (ref != null && ref instanceof FileReference) {
            FileReference cref = (FileReference) ref;
            return cref;
        }
        return null;
    }


    /**
     * Utility method that find the fileobject reference and if it exists, retypes it to CacheReference.
     * @param name pathname of the resource.
     * @return  the cacheReference instance if one exists or null
     * @deprecated use getFileReference
     */
    protected final CacheReference getCacheReference(String name) {

        assert Turbo.implemented() == false;
       Reference ref = findReference(name);
       if (ref != null && ref instanceof CacheReference) {
          CacheReference cref = (CacheReference) ref;
          return cref;
       }
       return null;
    }

    /**
     * Get the provider of the cache.
     */
    public FileCacheProvider getCacheProvider() {
        return cache;
    }

    /**
     * Get the provider of file status attributes.
     */
    public FileStatusProvider getStatusProvider() {
        return statusProvider;
    }

    //-------------------------------------------
    public void setCache(FileCacheProvider cache) {
        this.cache = cache;
    }

    /**
     * Get the full file path where cache information should be stored.
     * @return the cache file path or null, if no disk cache should be used for this path
     */
    public abstract String getCacheFileName(String path);

    /**
     * Initialize the identification of cache used.
     * The default implementation returns a unique string each time it is called.
     * @return the cache identification string
     */
    public String initCacheIdStr() {
        return DEFAULT_CACHE_ID + new Object().hashCode();
    }

    /**
     * Get the cache identification.
     */
    public String getCacheIdStr() {
        synchronized (this) {
            if (cacheID == null) {
                cacheID = initCacheIdStr();
                firePropertyChange("cacheID", null, cacheID); // NOI18N
            }
        }
        return cacheID;
    }

    public VcsCommandsProvider getCommandsProvider() {
        return commandsProvider;
    }

    /**
     * Gets the default factory {@link DefaultVcsFactory}. Subclasses may override this to return different instance of {@link VcsFactory}.
     */
    public VcsFactory getVcsFactory () {
        synchronized (this) {
            if (factory == null) {
                factory = new DefaultVcsFactory(this);
            }
        }
        return factory;
    }


    /*
    private void createDir(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            return ;
        }
        if (dir.mkdirs() == false) {
            E.err(g("MSG_UnableToCreateDirectory", path)); // NOI18N
            debug(g("MSG_UnableToCreateDirectory", path)); // NOI18N
        }
    }
     */

    /**
     * Perform some initialization job. This method is called both when a new instance
     * of this class is created and after deserialization. Subclasses should call super.init().
     */
    protected void init() {
        revisionListenersLock = new Object();
        lockedFilesToBeModified = new HashMap();
        displayName = computeDisplayName();
        localFilenameFilter = new LocalFilenameFilter();
        if (tempFiles == null) tempFiles = new Vector();
        //cache = new VcsFSCache(this/*, createNewCacheDir ()*/);
        cache = getVcsFactory().getFileCacheProvider();
        statusProvider = getVcsFactory().getFileStatusProvider();
        if (commandsProvider == null) {
            commandsProvider = new DefaultVcsCommandsProvider(new CommandsTree(null));
        }
        /*
        if (possibleFileStatusesMap == null) {
            if (statusProvider != null) {
                possibleFileStatusesMap = statusProvider.getPossibleFileStatusesTable();
            } else {
                possibleFileStatusesMap = new HashMap();
            }
        }
         */
        if (possibleFileStatusInfos == null) {
            if (statusProvider != null) {
                Set statusInfos = Collections.unmodifiableSet(statusProvider.getPossibleFileStatusInfos());
                setPossibleFileStatusInfos(statusInfos, Collections.EMPTY_MAP);
                origPossibleFileStatusInfos = statusInfos;
                //possibleFileStatusInfos = origPossibleFileStatusInfos;
            } else {
                setPossibleFileStatusInfos(Collections.EMPTY_SET, Collections.EMPTY_MAP);
                //possibleFileStatusInfos = Collections.EMPTY_SET;
            }
        }
        /*
        if (statusIconMap == null) {
            if (statusProvider != null) {
                statusIconMap = statusProvider.getStatusIconMap();
            } else {
                statusIconMap = new HashMap();
            }
        }
         */
        //errorDialog = new ErrorCommandDialog(null, new JFrame(), false);
        try {
            setInitRootDirectory(rootFile);
        } catch (PropertyVetoException e) {
            // Could not set root directory
        } catch (IOException e) {
            // Could not set root directory
        }
        if (multiFilesAnnotationTypes == null) {
            multiFilesAnnotationTypes = RefreshCommandSupport.DEFAULT_MULTI_FILES_ANNOTATION_TYPES;
        }
        if (annotationPattern == null) {
            annotationPattern = RefreshCommandSupport.DEFAULT_ANNOTATION_PATTERN;
        }
        if (notModifiableStatuses == null) {
            notModifiableStatuses = Collections.EMPTY_SET;
        }
        if (createRuntimeCommands == null) createRuntimeCommands = Boolean.TRUE;
        if (createVersioningSystem == null) createVersioningSystem = Boolean.FALSE;
        //if (revisionListsByName == null) revisionListsByName = new Hashtable();
        commandsPool = CommandsPool.getInstance();
        if (numberOfFinishedCmdsToCollect == null) {
            numberOfFinishedCmdsToCollect = new Integer(RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT);
        }
        if (varValueAdjustment == null) varValueAdjustment = new VariableValueAdjustment();
        initListeners();
        if (attr instanceof VcsAttributes) {
            ((VcsAttributes) attr).setCommandsProvider(getCommandsProvider());
            if (isCreateRuntimeCommands()) {
                ((VcsAttributes) attr).setRuntimeCommandsProvider(new VcsRuntimeCommandsProvider(this));
            }
        }
        deleteFileCommandQueue = new ArrayList();
        deleteFolderCommandQueue = new ArrayList();

        if (Turbo.implemented()) {
            // listen on turbo changes and redistribute as file status changes
            Turbo.singleton().addTurboListener((TurboListener) WeakListeners.create(TurboListener.class, this, Turbo.singleton()));

            // TODO revisit possible side effects of old implementaion
            integritySupportMaintainer = new IntegritySupportMaintainer(this, this);
            return;
        }

        // the old implementation
        FileSystemCache fsCache = CacheHandler.getInstance().getCache(getCacheIdStr());
        if (fsCache != null) {
            // Hold the IntegritySupportMaintainer so that it's not garbage-collected.
            integritySupportMaintainer = new IntegritySupportMaintainer(this, this);
        }
    }

    public void activate(VcsObjectIntegritySupport objectIntegritySupport) {
        if (Turbo.implemented()) {
            // TODO revisit possible side effects of old implementaion
            //objectIntegritySupport.activate(this, fsCache, getFile("").getAbsolutePath(), this);
            return;
        }

        // the old implementation
        FileSystemCache fsCache = CacheHandler.getInstance().getCache(getCacheIdStr());
        if (fsCache != null) {
            objectIntegritySupport.activate(this, fsCache, getFile("").getAbsolutePath(), this);
        }
    }

    private void initListeners() {
        settingsChangeListener = new SettingsPropertyChangeListener();
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        settings.addPropertyChangeListener(WeakListeners.propertyChange(settingsChangeListener, settings));
        addPropertyChangeListener(new FSPropertyChangeListener());
        addRemoveFSListener = new VcsFileSystem.RegistryListener();
        FSRegistry.getDefault().addFSRegistryListener((FSRegistryListener) WeakListeners.create(FSRegistryListener.class, addRemoveFSListener, FSRegistry.getDefault()));
        // It may happen, that the FSInfo of this FS is already registered
        // (It could be registered when this FS did not exist yet)
        FSInfo[] infos = FSRegistry.getDefault().getRegistered();
        for (int i = 0; i < infos.length; i++) {
            if (VcsFileSystem.this.equals(infos[i].getExistingFileSystem())) {
                notifyFSAdded();
                break;
            }
        }
    }

    protected AbstractFileSystem.List getVcsList() {
        return vcsList;
    }

    protected AbstractFileSystem.Info getVcsInfo() {
        return info;
    }

    /**
     * Notifies this file system that it has been added to the repository.
     * Since we can not rely on the addNotify() method (see issue #30763),
     * this method was introduced to be notified when this FS is really added.
     * Call super.notifyFSAdded() when you're extending this method. Do not
     * call directly.
     */
    protected void notifyFSAdded() {
        //System.out.println("fileSystemAdded("+this+")");
        //System.out.println("isOffLine() = "+isOffLine()+", auto refresh = "+getAutoRefresh()+", deserialized = "+deserialized);
        if (!isOffLine()
            && (getAutoRefresh() == GeneralVcsSettings.AUTO_REFRESH_ON_MOUNT_AND_RESTART
            || (deserialized && getAutoRefresh() == GeneralVcsSettings.AUTO_REFRESH_ON_RESTART)
            || (!deserialized && getAutoRefresh() == GeneralVcsSettings.AUTO_REFRESH_ON_MOUNT))) {
                CommandExecutorSupport.doRefresh(VcsFileSystem.this, "", true);
        }
        if (isCreateVersioningSystem()) {
            org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    //VersioningExplorer.getRevisionExplorer().open();
                    if (versioningSystem == null) {
                        createVersioningFileSystem();
                    }
                }
            });
        }
        enableRefresh();
    }

    /**
     * Notifies this file system that it has been removed from the repository.
     * Since we can not rely on the removeNotify() method (see issue #30763),
     * this method was introduced to be notified when this FS is really removed.
     * Call super.notifyFSRemoved() when you're extending this method. Do not
     * call directly.
     */
    protected void notifyFSRemoved() {
        disableRefresh();
        //System.out.println("fileSystem Removed("+this+")");
        if (versioningSystem != null) {
            org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    if (versioningSystem == null) return ;
                    removeVersioningFileSystem();
                }
            });
        }
    }
    
    protected final void createVersioningFileSystem() {
        versioningSystem = new VcsVersioningSystem(VcsFileSystem.this);
        VersioningRepository.getRepository().addVersioningFileSystem(versioningSystem);
    }
    
    protected final void removeVersioningFileSystem() {
        VersioningRepository.getRepository().removeVersioningFileSystem(versioningSystem);
        versioningSystem = null;
    }

    protected void setCreateRuntimeCommands(boolean createRuntimeCommands) {
        if (this.createRuntimeCommands == null || createRuntimeCommands != this.createRuntimeCommands.booleanValue()) {
            this.createRuntimeCommands = createRuntimeCommands ? Boolean.TRUE : Boolean.FALSE;
            if (attr instanceof VcsAttributes) {
                ((VcsAttributes) attr).setRuntimeCommandsProvider(
                    (createRuntimeCommands) ? new VcsRuntimeCommandsProvider(this) : null);
            }
            firePropertyChange(PROP_CREATE_RUNTIME_COMMANDS, null, this.createRuntimeCommands);
        }
    }

    public boolean isCreateRuntimeCommands() {
        return createRuntimeCommands.booleanValue();
    }

    protected void setCreateVersioningSystem(boolean createVersioningSystem) {
        if (this.createVersioningSystem == null || createVersioningSystem != this.createVersioningSystem.booleanValue()) {
            this.createVersioningSystem = createVersioningSystem ? Boolean.TRUE : Boolean.FALSE;
            firePropertyChange(PROP_CREATE_VERSIONING_EXPLORER, null, this.createVersioningSystem);
        }
    }

    protected boolean isCreateVersioningSystem() {
        return createVersioningSystem.booleanValue();
    }

    public VersioningFileSystem getVersioningFileSystem() {
        return versioningSystem;
    }

    protected VcsActionSupporter getVcsActionSupporter() {
        return actionSupporter;
    }

    protected VcsAttributes getVcsAttributes() {
        return (VcsAttributes) attr;
    }

    private static final long serialVersionUID =8108342718973310275L;

    /**
     * Create a new VCS filesystem.
     */
    public VcsFileSystem() {
        deserialized = false;
        info = this;
        change = this;
        actionSupporter = new VcsActionSupporter();
        VcsAttributes a = new VcsAttributes (rootFile, info, change, this, this, actionSupporter);
        attr = a;
        list = a;
        vcsList = new VcsList();
        setRefreshTime (0); // disable the standard refresh, VcsRefreshRequest is used instead
        setVcsRefreshTime (0, false); // due to customization
        refreshTimeToSet = last_refreshTime;
        /*
        cacheRoot = System.getProperty("netbeans.user")+File.separator+
                    "system"+File.separator+"vcs"+File.separator+"cache"; // NOI18N
         */
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        setOffLine(settings.isOffLine());
        setAutoRefresh(settings.getAutoRefresh());
        setHideShadowFiles(settings.isHideShadowFiles());
        init();
        //possibleFileStatusesMap = statusProvider.getPossibleFileStatusesTable();
    }

    public String[] getPossibleFileStatuses() {
        Set statusInfos = getPossibleFileStatusInfos();
        String[] statuses = new String[statusInfos.size()];
        int i = 0;
        for(Iterator it = statusInfos.iterator(); it.hasNext(); i++) {
            FileStatusInfo statusInfo = (FileStatusInfo) it.next();
            statuses[i] = statusInfo.getDisplayName();
        }
        return statuses;
    }

    /**
     * Get the map of possible pairs of status name and corresponding FileStatusInfo object.
     */
    public Map getPossibleFileStatusInfoMap() {
        return possibleFileStatusInfoMap;
    }

    /**
     * Get a copy of stauses transfer table.
     *
    public HashMap getPossibleFileStatusesTable() {
        HashMap statusesTable;
        if (possibleFileStatusesMap == null) return null;
        synchronized (possibleFileStatusesMap) {
            statusesTable = new HashMap(possibleFileStatusesMap);
        }
        return statusesTable;
    }
     */

    /**
     * Get the set of all possible FileStatusInfo objects.
     */
    public Set getPossibleFileStatusInfos() {
        synchronized (possibleFileStatusesLock) {
            if (possibleFileStatusInfos == null) {
                return Collections.EMPTY_SET;
            } else {
                return possibleFileStatusInfos;
            }
        }
    }
    
    /**
     * Get the map of generic file status names (that are defined in
     * FileStatusInfo class) as keys and translated file status names
     * (that are provided in the possibleFileStatusInfos set) as values.
     */
    public Map getGenericStatusTranslation() {
        return genericStatusTranslation;
    }

    /**
     * Set a set of all possible FileStatusInfo objects. Also a translation of
     * generic status names to provided possible status names is supplied.
     * @param fileStatusInfos The set of FileStatusInfo objects
     * @param genericStatusTranslation The map of generic file status names
     *        (that are defined in FileStatusInfo class) as keys and translated
     *        file status names (that are provided in the fileStatusInfos set)
     *        as values.
     */
    protected final void setPossibleFileStatusInfos(Set fileStatusInfos, Map genericStatusTranslation) {
        synchronized (possibleFileStatusesLock) {
            if (this.origPossibleFileStatusInfos == null || this.origPossibleFileStatusInfos.size() == 0) {
                this.possibleFileStatusInfos = Collections.unmodifiableSet(fileStatusInfos);
            } else {
                this.possibleFileStatusInfos = new HashSet(origPossibleFileStatusInfos);
                for (Iterator newIt = fileStatusInfos.iterator(); newIt.hasNext(); ) {
                    FileStatusInfo newStatusInfo = (FileStatusInfo) newIt.next();
                    for (Iterator origIt = this.origPossibleFileStatusInfos.iterator(); origIt.hasNext(); ) {
                        FileStatusInfo origStatusInfo = (FileStatusInfo) origIt.next();
                        if (origStatusInfo.equals(newStatusInfo)) {
                            possibleFileStatusInfos.remove(origStatusInfo);
                            break;
                        }
                    }
                    possibleFileStatusInfos.add(newStatusInfo);
                }
                this.possibleFileStatusInfos = Collections.unmodifiableSet(this.possibleFileStatusInfos);
            }
            possibleFileStatusInfoMap = new HashMap();
            for (Iterator it = this.possibleFileStatusInfos.iterator(); it.hasNext(); ) {
                FileStatusInfo statusInfo = (FileStatusInfo) it.next();
                possibleFileStatusInfoMap.put(statusInfo.getName(), statusInfo);
            }
            possibleFileStatusInfoMap = Collections.unmodifiableMap(possibleFileStatusInfoMap);
            this.genericStatusTranslation = Collections.unmodifiableMap(genericStatusTranslation);
        }
    }
    
    //-------------------------------------------
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException, NotActiveException {
        // cache is transient
        //System.out.println("VcsFileSystem.readObject() ...");
        //try {
            //ObjectInputStream din = (ObjectInputStream) in;
        deserialized = true;
        possibleFileStatusesLock = new Object();
        boolean localFilesOn = in.readBoolean ();
        in.defaultReadObject();
        refresher = new VcsRefreshRequest (this, 0, this);
        actionSupporter = new VcsActionSupporter();
        if (!(attr instanceof VcsAttributes)) {
            VcsAttributes a = new VcsAttributes (rootFile, info, change, this, this, actionSupporter);
            attr = a;
            list = a;
        } else {
            ((VcsAttributes) attr).setCurrentSupporter(actionSupporter);
        }
        if (vcsList == null) vcsList = new VcsList();
        //last_rootFile = rootFile;
        last_refreshTime = getCustomRefreshTime ();
        last_useUnixShell = useUnixShell;
        updateEnvironmentVars();
        init();
        //cache.setLocalFilesAdd (localFilesOn);
        processUnimportantFilesLock = new Object();
        if (null == processUnimportantFiles) processUnimportantFiles = Boolean.FALSE;
        last_rootFile = new File(getFSRoot());
        //} catch (Throwable thr) {
        //    System.out.println("VcsFileSystem.readObject():");
        //    thr.printStackTrace();
        //}
        setExpertMode(isAcceptUserParams()); // to be compatible with 3.1
    }


    //-------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        // cache is transient

        out.writeBoolean (true/*cache.isLocalFilesAdd ()*/); // for compatibility
        String myPassword = password;
        if (!rememberPassword) password = null;
        out.defaultWriteObject();
        password = myPassword; // to keep the password set if the object is written,
                               // but still in use.
    }


    //-------------------------------------------
    public void setDebug(boolean debug){
        if (this.debug != debug) {
            this.debug = debug;
            firePropertyChange(PROP_DEBUG, !debug ? Boolean.TRUE : Boolean.FALSE, debug ? Boolean.TRUE : Boolean.FALSE);
        }
    }


    //-------------------------------------------
    public boolean getDebug(){
        return debug;
    }

    /**
     * Get the environment variables and their values
     * in the form: "VAR1=Value1", "VAR2=Value2", etc.
     */
    public String[] getEnvironmentVars() {
        return environmentVars;
    }

    public void addEnvironmentVar (String key, String value) {
	String[] tmp = new String[environmentVars.length+1];
	System.arraycopy (this.environmentVars,0,tmp,0,this.environmentVars.length);
	tmp[environmentVars.length] = key+"="+value;
	this.environmentVars = tmp;
    }

    public void setEnvironmentVar (String key, String value) {
	for (int i=0; i<this.environmentVars.length; i++) {
	    StringTokenizer tk = new StringTokenizer (this.environmentVars[i],"=");
            String pairKey;
            if (tk.countTokens() != 2) { // Broken env variable
		if (environmentVars[i].endsWith("=")) { // the value is not defined
                    pairKey = environmentVars[i].substring(0, environmentVars[i].length() - 1);
                } else {
                    continue; // Something's bad
                }
            } else {
                pairKey = tk.nextToken();
            }
	    if (pairKey.equals(key)) {
		this.environmentVars[i]=key+"="+value;
		return;
	    }
	}
	addEnvironmentVar (key, value);
    }

    private void updateEnvironmentVars() {
        Map systemEnv = VcsUtilities.getSystemEnvVars();
        Map env = VcsUtilities.addEnvVars(systemEnv, getVariablesAsHashtable(),
                                          VAR_ENVIRONMENT_PREFIX, VAR_ENVIRONMENT_REMOVE_PREFIX);
        environmentVars = VcsUtilities.getEnvString(env);
    }

    //-------------------------------------------
    public Vector getVariables() {
        return variables;
    }

    /**
     * Get the variable value adjustment utility object.
     */
    public VariableValueAdjustment getVarValueAdjustment() {
        return varValueAdjustment;
    }

    /** Notify the filesystem, that an variable has changed and should be saved.
     * @param name the name of a changed variables. If <code>null</code> we suppose,
     *        that all variables changed. */
    public void variableChanged(String name) {
        firePropertyChange(PROP_VARIABLES, null, variables);
    }

    /**
     * Set the file system's variables.
     * @param variables the vector of <code>VcsConfigVariable</code> objects.
     */
    public void setVariables(Vector variables) {
        //System.out.println("setVariables("+VcsUtilities.toSpaceSeparatedString(variables)+")");
        boolean containsCd = false;
        // Windows != 95 && != 98 needs "cd /D" to change the directory accross disks !!!
        // Windows 95 || 98 do not recognize /D => change the directory accross disks is NOT possible by a single command !!!
        int os = Utilities.getOperatingSystem();
        String cdValue = (Utilities.isWindows()
                          && os != Utilities.OS_WIN95
                          && os != Utilities.OS_WIN98) ? "cd /D" : "cd";
        int len = variables.size ();
        VcsConfigVariable var;
        for(int i = 0; i < len; i++) {
            var = (VcsConfigVariable) variables.get (i);
            /*
            if(var.getName ().equalsIgnoreCase (LOCAL_FILES_ADD_VAR)) {
                if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                    cache.setLocalFilesAdd (true);
                }
                if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                    cache.setLocalFilesAdd (false);
                }
            }
             */
            if(var.getName ().equalsIgnoreCase (LOCK_FILES_ON)) {
                if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                    setLockFilesOn (true);
                }
                if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                    setLockFilesOn (false);
                }
            }
            if(var.getName ().equalsIgnoreCase (PROMPT_FOR_LOCK_ON)) {
                if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                    setPromptForLockOn (true);
                }
                if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                    setPromptForLockOn (false);
                }
            }
            if(var.getName ().equalsIgnoreCase (EDIT_FILES_ON)) {
                if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                    setCallEditFilesOn (true);
                }
                if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                    setCallEditFilesOn (false);
                }
            }
            if(var.getName ().equalsIgnoreCase (PROMPT_FOR_EDIT_ON)) {
                if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                    setPromptForEditOn (true);
                }
                if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                    setPromptForEditOn (false);
                }
            }
            if(var.getName ().equals ("CD")) { // NOI18N
                //var.setValue (cdValue); <- I don't want to change the value if it is set !!
                containsCd = true;
            }
        }
        /*
        if (variables.equals(this.variables)) {
            return ;
        }
         */
        if (!containsCd) {
            variables.add (new VcsConfigVariable ("CD", "cd", cdValue, false, false, false, "", 0)); // NOI18N
        }
        Vector old = this.variables;
        synchronized (this) {
            this.variables = variables;
        }

        VcsConfigVariable mod = (VcsConfigVariable) variablesByName.get("MODULE");
        HashMap newVarsByName = new HashMap();
        for (int i = 0, n = variables.size (); i < n; i++) {
            var = (VcsConfigVariable) variables.get (i);
            newVarsByName.put (var.getName (), var);
        }
        if (!newVarsByName.containsKey("MODULE") && mod != null) { // The module was previosly defined, it has to be copied to new variables.
            this.variables.add(mod);
            newVarsByName.put (mod.getName (), mod);
        }
        synchronized (this) {
            variablesByName = new Hashtable(newVarsByName);
        }
        updateEnvironmentVars();
        varValueAdjustment.setAdjust(getVariablesAsHashtable());

        if (variables.equals(old)) old = null; // To fire the event even when the variables were changed in this vector.
        firePropertyChange(PROP_VARIABLES, old, variables);
        //try {
        setAdjustedSystemName(computeSystemName(rootFile));
        //} catch (PropertyVetoException exc) {}
        displayName = computeDisplayName();
        firePropertyChange(PROP_DISPLAY_NAME, null, getDisplayName());
    }

    public static String substractRootDir(String rDir, String module) {
        if (module == null || module.length() == 0) return rDir;
        String m;
        if (module.charAt(module.length() - 1) == File.separatorChar)
            m = module.substring(0, module.length() - 1);
        else
            m = module.substring(0);
        String rDirSlashes;
        boolean chRDir = false;
        if (File.separatorChar != '/' && rDir.indexOf(File.separatorChar) > 0) {
            rDirSlashes = rDir.replace(File.separatorChar, '/');
            chRDir = true;
        } else rDirSlashes = rDir;
        String moduleSlashes;
        if (File.separatorChar != '/' && m.indexOf(File.separatorChar) > 0) {
            moduleSlashes = m.replace(File.separatorChar, '/');
        } else moduleSlashes = m;
        int i = rDirSlashes.lastIndexOf(moduleSlashes);
        if (i > 0) {
            if (chRDir) rDir = rDir.substring(0, i-1).replace('/', File.separatorChar);
            else rDir = rDir.substring(0, i-1); // I have to remove the slash also.
        }
        if (org.openide.util.Utilities.isWindows() && rDir.length() == 2 &&
            Character.isLetter(rDir.charAt(0)) && ':' == rDir.charAt(1)) {
            rDir += "\\"; // A special case for C:\
        }
        return rDir;
    }


    //-------------------------------------------
    public Hashtable getVariablesAsHashtable() {
        Vector vars;
        String rootDir;
        Hashtable result;
        synchronized (this) {
            vars = getVariables();
            rootDir = getRootDirectory().toString();
            int len = vars.size();
            Map defVars = Variables.getDefaultVariablesMap();
            result = new Hashtable(len + defVars.size() + 5);
            result.putAll(defVars);
            for(int i = 0; i < len; i++) {
                VcsConfigVariable var = (VcsConfigVariable) vars.elementAt (i);
                String value = var.getValue();
                if (value != null) result.put(var.getName (), value);
            }
        }
        
        if (result.get("PS") == null) { // NOI18N
            result.put("PS", File.separator); // NOI18N
        }

        String module = (String) result.get("MODULE"); // NOI18N
        //if (osName.indexOf("Win") >= 0) // NOI18N
        //module=module.replace('\\','/');
        result.put("ROOTDIR", VcsFileSystem.substractRootDir(rootDir, module)); // NOI18N
        result.put(VAR_EXPERT_MODE, expertMode ? "expert" : "");

        return result;
    }

    //-------------------------------------------
    public void setPassword(String password){
        if (this.password == null && password != null ||
            this.password != null && !this.password.equals(password)) {

            this.password = password;
            firePropertyChange(PROP_PASSWORD, null, password);
        }
    }

    //-------------------------------------------
    public String getPassword(){
        return password;
    }

    public void setRememberPassword(boolean remember) {
        if (this.rememberPassword != remember) {
            this.rememberPassword = remember;
            firePropertyChange(PROP_REMEMBER_PASSWORD, !remember ? Boolean.TRUE : Boolean.FALSE, remember ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public boolean isRememberPassword() {
        return rememberPassword;
    }
    
    /**
     * Get the description of the password, typically the name of the service
     * that requests the password. <p>
     * This method returns <code>null</code> by default, subclasses are expected
     * to override this method and return meaningful value.
     * @return The description or <code>null</code> when no description is available.
     */
    public String getPasswordDescription() {
        return null;
    }

    /**
     * Allows some cleanup of the document which the user is asked for.
     * doc The Document
     * promptNum the order of the document
     * docIdentif some identification that can be set in settting the listener.
     */
    public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif) {
        // Let the document unchanged by default
    }

    protected void warnDirectoriesDoNotExists() {

        String module;
        File root;
        synchronized (this) {
            Hashtable vars = getVariablesAsHashtable();
            module = (String) vars.get("MODULE");
            if (module == null) module = "";
            String rootDir = VcsFileSystem.substractRootDir(getRootDirectory().toString(), module);
            root = new File(rootDir);
        }
        if(!root.isDirectory() ){
            final String badDir = root.toString();
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(VcsFileSystem.class).getString("Filesystem.notRootDirectory"), new Object[] { badDir } )));
                                                       }
                                                   });
            return ;
        }
        File moduleDir = new File(root, module);
        if (!moduleDir.isDirectory()) {
            //System.out.println("NOT DIRECTORY: "+moduleDir);
            final String badDir = module;
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(VcsFileSystem.class).getString("Filesystem.notModuleDirectory"), new Object[] { badDir } )));
                                                       }
                                                   });
        }
    }

    public String getAnnotationPattern() {
        return annotationPattern;
    }

    public void setAnnotationPattern(String annotationPattern) {
        String old = this.annotationPattern;
        this.annotationPattern = annotationPattern;
        firePropertyChange(PROP_ANNOTATION_PATTERN, old, this.annotationPattern);
    }

    public int[] getMultiFileAnnotationTypes() {
        return multiFilesAnnotationTypes;
    }

    public void setMultiFileAnnotationTypes(int[] multiFileAnnotationTypes) {
        if (multiFilesAnnotationTypes.length != RefreshCommandSupport.NUM_ELEMENTS) {
            throw new IllegalArgumentException("Wrong length of the array ("+multiFileAnnotationTypes.length+" != "+RefreshCommandSupport.NUM_ELEMENTS+")");
        }
        int[] old = this.multiFilesAnnotationTypes;
        this.multiFilesAnnotationTypes = multiFilesAnnotationTypes;
        firePropertyChange(PROP_ANNOTATION_TYPES, old, this.multiFilesAnnotationTypes);
    }

    //-------------------------------------------
    public FileSystem.Status getStatus(){
        return this;
    }

    public String getStatus(FileObject fo) {
        String status;
        if (statusProvider != null) {
            fo = convertForeignFileObjectToMyFileObject(fo);
            String fullName = fo.getPath();
            status = statusProvider.getFileStatus(fullName).trim();
        } else status = "";
        return status;
    }

    /**
     * Get states of all important files inside the DataObject. To get the status,
     * wait for the status reader command if necessary.
     * @param dobj The DataObject
     * @return The list of important files states. The order of files is kept unchanged.
     */
    public String[] getStates(DataObject dobj) {
        Set files = dobj.files();
        Object[] oo = files.toArray();
        int len = oo.length;
        if (len == 0) return new String[0];
        if (statusProvider != null) {
            for (int i = 0; i < len; i++) {
                oo[i] = convertForeignFileObjectToMyFileObject((FileObject) oo[i]);
            }
            if (len == 1) {
                return new String[] {
                    getRealStatus(((FileObject) oo[0]).getPath())
                };
            } else {
                ArrayList importantFiles = getImportantFiles(oo);
                String[] states = new String[importantFiles.size()];
                for (int i = 0; i < states.length; i++) {
                    states[i] = getRealStatus((String) importantFiles.get(i));
                }
                return states;
            }
        } else return new String[0];
    }

    /** Get the "real" status of a file. If necessary wait for the status to be
     * retrieved from the VCS */
    private String getRealStatus(String fullName) {
        if (cache != null) {
            //findLoadedCacheDir(fullName);
            String dirName = VcsUtilities.getDirNamePart(fullName);
            String fileName = VcsUtilities.getFileNamePart(fullName);
            CacheDir dir = cache.getDir(dirName);
            if (dir == null || dir.isLocal()) {
                dir = findLoadedCacheDir(dirName);
            }
            // first ask for the status - this will either read the file status
            // from memory cache or from disk cache or start a Refresh command
            String firstStatus = statusProvider.getFileStatus(fullName);
            // If the cache directory exists wait till it's fully loaded (till the Refresh command finish)
            if (dir != null && dir instanceof VcsCacheDir) {
                VcsCacheDir vcsdir = (VcsCacheDir) dir;
                if (!vcsdir.isLocal()) {
                    try {
                        vcsdir.waitToLoad();
                    } catch (InterruptedException inex) {}
                }
                // Also ask whether the file is ignored to load the list of ignored files
                dir.isIgnored(fileName);
            }
        }
        // Now the correct status should be ready
        String status = statusProvider.getFileStatus(fullName);
        String trans = null;
        if (status != null) {
            synchronized (possibleFileStatusesLock) {
                FileStatusInfo statusInfo = (FileStatusInfo) possibleFileStatusInfoMap.get(status);
                if (statusInfo != null) {
                    trans = statusInfo.getDisplayName();
                }
            }
        }
        /*
        HashMap possibleFileStatusesMap = statusProvider.getPossibleFileStatusesTable();
        if (possibleFileStatusesMap != null && status != null) {
            synchronized (possibleFileStatusesMap) {
                trans = (String) possibleFileStatusesMap.get(status);
            }
        }
         */
        if (trans != null) {
            status = trans;
        }
        return status;
    }

    /** find a cache dir, that currently does not exist, but can be obtained
     * after it's parents are loaded */
    private CacheDir findLoadedCacheDir(String name) {
        String dirName = VcsUtilities.getDirNamePart(name);
        String fileName = VcsUtilities.getFileNamePart(name);
        if (dirName.length() == 0 || dirName.length() == name.length()) return null;
        CacheDir dir = cache.getDir(dirName);
        if (dir == null || dir.isLocal()) {
            dir = findLoadedCacheDir(dirName);
        }
        if (dir != null && !dir.isLocal() && (dir instanceof VcsCacheDir)) {
            VcsCacheDir vcsdir = (VcsCacheDir) dir;
            if (!vcsdir.isLoaded()) {
                // ask for the status - this will either read the file status
                // from memory cache or from disk cache or start a Refresh command
                String firstStatus = statusProvider.getFileStatus(name);
                // If the cache directory exists wait till it's fully loaded (till the Refresh command finish)
                if (!vcsdir.isLocal()) {
                    try {
                        vcsdir.waitToLoad();
                    } catch (InterruptedException inex) {}
                }
            }
        }
        if (dir != null) {
            // Also ask whether the file is ignored to load the list of ignored files
            dir.isIgnored(fileName);
            dir = dir.getSubDir(fileName);
        }
        return dir;
    }

    /** Converts a FileObject from a different file system (usually
     * a multifilesystem which I'm a part of) to a FileObject from my (this)
     * filesystem. */
    private FileObject convertForeignFileObjectToMyFileObject(FileObject fo) {
        FileSystem fs = null;
        try {
            fs = fo.getFileSystem();
        } catch (FileStateInvalidException fsiex) {}
        if (fs != null) {
            if (!this.equals(fs)) {
                java.io.File file = org.openide.filesystems.FileUtil.toFile(fo);
                if (file != null) {
                    String filePath = file.getAbsolutePath().replace(java.io.File.separatorChar, '/');
                    java.io.File root = org.openide.filesystems.FileUtil.toFile(this.getRoot());
                    String rootPath = root.getAbsolutePath().replace(java.io.File.separatorChar, '/');
                    if (filePath.startsWith(rootPath)) {
                        String foPath = filePath.substring(rootPath.length());
                        while (foPath.startsWith("/")) foPath = foPath.substring(1);
                        fo = findFileObject(foPath);
                    }
                }
            }
        }
        return fo;
    }

    public String getLocker(FileObject fo) {
        String locker;
        if (statusProvider != null) {
            String fullName = fo.getPath();
            locker = statusProvider.getFileLocker(fullName);
            if (locker != null) locker = locker.trim();
            else locker = "";
        } else locker = "";
        return locker;
    }

    //-------------------------------------------
    public Image annotateIcon(Image icon, int iconType, Set files) {
        /*
        System.out.print("annotateIcon(");
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            System.out.print(it.next()+", ");
        }
        System.out.println(")");
        Frame f = new Frame();
        f.add(new Label("Original :"));
        f.add(new JButton(new ImageIcon(icon)));
        f.pack();
        f.setVisible(true);
         */
        Object[] oo = files.toArray();
        int len = oo.length;
        if (len == 0/* || name.indexOf(getRootDirectory().toString()) >= 0*/) {
            return icon;
        }

        if (statusProvider != null) {
            ArrayList importantFiles = getImportantFiles(oo);
            len = importantFiles.size();
            FileStatusInfo status = null;
            if (len == 1) {
                String fullName = (String) importantFiles.get(0);
                if ("".equals(fullName)) return icon; // DO NOT override the root icon !
                status = statusProvider.getFileStatusInfo(fullName);
            } else {
                for (Iterator it = importantFiles.iterator(); it.hasNext(); ) {
                    String fullName = (String) it.next();
                    FileStatusInfo fileStatus = statusProvider.getFileStatusInfo(fullName);
                    if (status == null) status = fileStatus;
                    if (status != null && !status.equals(fileStatus)) {
                        status = null;//statusProvider.getNotInSynchStatus();
                        break;
                    }
                }
            }
            if (status != null) {
                Image img = status.getIcon();//(Image) statusIconMap.get(status);
                //System.out.println("annotateIcon: status = "+status+" => img = "+img);
                if (img == null) img = statusIconDefault;
                if (img != null) {
                    icon = org.openide.util.Utilities.mergeImages(icon, img, BADGE_ICON_SHIFT_X, BADGE_ICON_SHIFT_Y);
                }
            }
        }
        /*
        f = new Frame();
        f.add(new Label("MyReturned :"));
        f.add(new JButton(new ImageIcon(icon)));
        f.pack();
        f.setVisible(true);
         */
        return icon;
    }

    public String annotateNameHtml (String name, java.util.Set files) {
        String result = name;
        if (result == null)
            return result;  // Null name, ignore it
        //Object[] oo = files.toArray();
        int len = files.size();
        if (len == 0 || name.indexOf(getRootDirectory().toString()) >= 0) {
            return result;
        }

        if (statusProvider != null) {
            ArrayList importantFiles = getImportantFiles(files.toArray());
            len = importantFiles.size();
            //if (print) System.out.println(" length of important = "+len);
            if (len == 1) {
                String fullName = (String) importantFiles.get(0);
                //System.out.println(" fullName = "+fullName);
                result = RefreshCommandSupport.getHtmlStatusAnnotation(name, fullName, annotationPattern, statusProvider, null);
            } else {
                //importantFiles = VcsUtilities.reorderFileObjects(importantFiles); -  not needed after DataObject.files() return files in the right order
                //if (print) System.out.println(" importantFiles = "+VcsUtilities.arrayToString((String[]) importantFiles.toArray(new String[0])));
                result = RefreshCommandSupport.getHtmlStatusAnnotation(name, importantFiles, annotationPattern, statusProvider, multiFilesAnnotationTypes);
            }
        }
        //String result = annotateName (name, files);
        String fontColor = "<font color='!controlShadow'>";
        if(javax.swing.UIManager.getDefaults().getColor("Tree.selectionBackground").equals(javax.swing.UIManager.getDefaults().getColor("controlShadow"))){
            fontColor = "<font color='!Tree.selectionBorderColor'>";
        }
        result = Utilities.replaceString(result,name, name + fontColor);
        return result;
    }

    /** Find the file object from a file path.
     * @param fullName the full path to the file
     * @return the file object of that file
     */
    public FileObject findFileObject(String fullName) {
        return findResource(fullName);
    }

    /** Annotate the set of files with additional version control attributes.
     * @param name the original annotation
     * @param files the files to annotate
     * @return the annotation string
     */
    public String annotateName(String name, Set files) {
        /*
        boolean print = false; //"Class1".equals(name);
        if (print) {
            String filesStr = "";
            for (Iterator it = files.iterator(); it.hasNext(); ) filesStr += ((FileObject) it.next()).getNameExt() + ", ";
            System.out.println("annotateName("+name+", "+filesStr.substring(0, filesStr.length() - 2)+")");
            Thread.currentThread().dumpStack();
        }
         */
        String result = name;
        if (result == null)
            return result;  // Null name, ignore it
        //Object[] oo = files.toArray();
        int len = files.size();
        if (len == 0 || name.indexOf(getRootDirectory().toString()) >= 0) {
            return result;
        }

        if (statusProvider != null) {
            ArrayList importantFiles = getImportantFiles(files.toArray());
            len = importantFiles.size();
            //if (print) System.out.println(" length of important = "+len);
            if (len == 1) {
                String fullName = (String) importantFiles.get(0);
                //System.out.println(" fullName = "+fullName);
                result = RefreshCommandSupport.getStatusAnnotation(name, fullName, annotationPattern, statusProvider);
            } else {
                //importantFiles = VcsUtilities.reorderFileObjects(importantFiles); -  not needed after DataObject.files() return files in the right order
                //if (print) System.out.println(" importantFiles = "+VcsUtilities.arrayToString((String[]) importantFiles.toArray(new String[0])));
                result = RefreshCommandSupport.getStatusAnnotation(name, importantFiles, annotationPattern, statusProvider, multiFilesAnnotationTypes);
            }
        }
        //if (print) System.out.println("  annotateName("+name+") -> result='"+result+"'");
        return result;
    }


    /**
     * Get the important files. Also add files scheduled for remove.
     * @return the Vector of important files as Strings
     */
    private ArrayList/*VcsFile*/ getImportantFiles(Object[] oo){
        ArrayList result = new ArrayList();
        int len=oo.length;

        boolean processAll = isProcessUnimportantFiles();
        for(int i = 0; i < len; i++) {
            FileObject ff = (FileObject) oo[i];
            //System.out.println(" getImportantFiles("+ff+")");
            boolean isFromThisFs = true;
            try {
                isFromThisFs = ff.getFileSystem().equals(this);
            } catch (org.openide.filesystems.FileStateInvalidException exc) {
                isFromThisFs = true;
            }
            //System.out.println(" isFromThisFs = "+isFromThisFs);
            if (isFromThisFs) {
                String fullName = ff.getPath();
                //System.out.println(" fullName = "+fullName+", isImportant = "+isImportant(fullName));
                if (processAll || isImportant(fullName)) {
                    result.add(fullName);
                }
            }
            // check for scheduled files:
            Set[] scheduled = (Set[]) ff.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            if (scheduled != null && scheduled[0] != null) {
                String filePath = (String) ff.getAttribute(VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR);
                if (filePath != null) {
                    File currentFile = FileUtil.toFile(ff);
                    if (currentFile != null && !filePath.equals(currentFile.getAbsolutePath())) {
                        // the file was moved/copied/renamed to another location.
                        // Delete all it's scheduling attributes, because they are out-of-date
                        try {
                            ff.setAttribute(VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                            ff.setAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR, null);
                            scheduled[0].clear();
                        } catch (IOException exc) {}
                    }
                }
                // add all scheduled files
                result.addAll(scheduled[0]);
            }
        }
        return result;
    }

    /**
     * Get the VCS actions.
     * @return the actions retrieved from <code>VcsFactory.getActions(null)</code>
     */
    public SystemAction[] getActions() {
        return getVcsFactory ().getActions(null);
    }

    /**
     * Get the VCS actions on a collection of <code>FileObject</code>s.
     * @param fos the collection of <code>FileObject</code>s to act on.
     * @return the actions retrieved from <code>VcsFactory.getActions(fos)</code>
     */
    public SystemAction[] getActions(Set fos) {
        /*
        System.out.print("getActions(");
        for(Iterator it = fos.iterator(); it.hasNext(); ) {
            System.out.print(((FileObject) it.next()).getNameExt()+(it.hasNext() ? ", " : ""));
        }
        System.out.println(")");
         */
        return getVcsFactory ().getActions(fos);
    }
    
    protected String computeDisplayName() {
        //System.out.print("VcsFileSystem.computeDisplayName(): commandsRoot = "+commandsRoot);
        Hashtable vars = getVariablesAsHashtable();
        String displayNameAnnotation = (String) vars.get(VAR_FS_DISPLAY_NAME_ANNOTATION);
        if (displayNameAnnotation != null) {
            if (statusProvider != null) {
                displayNameAnnotation = RefreshCommandSupport.getStatusAnnotation("", "", displayNameAnnotation, statusProvider, vars);
            } else {
                displayNameAnnotation = Variables.expand(vars, displayNameAnnotation, false);
            }
            //System.out.println(displayNameAnnotation);
            return displayNameAnnotation;
        }
        VcsConfigVariable preDisplayNameVar = (VcsConfigVariable) variablesByName.get(VAR_FS_DISPLAY_NAME);
        if (preDisplayNameVar != null) {
            if (Boolean.getBoolean("netbeans.vcs.T9Y")) {
                String builtIn = (String) vars.get("BUILT-IN");
                if (builtIn != null) {
                    if (builtIn.length() == 0) {
                        return preDisplayNameVar.getValue() + "-EXT " + rootFile.toString();
                    } else {
                        return preDisplayNameVar.getValue() + "-INT " + rootFile.toString();
                    }
                }
            }
            //System.out.println(preDisplayNameVar.getValue() + " " + rootFile.toString());
            return preDisplayNameVar.getValue() + " " + rootFile.toString();
        } else if (commandsRoot != null) {
            CommandSupport cmdSupport = commandsRoot.getCommandSupport();
            if (cmdSupport != null) {
                String VCSName = cmdSupport.getDisplayName();
                //System.out.println("VCSName = '"+VCSName+"'");
                if (VCSName != null && VCSName.length() > 0) {
                    //System.out.println("VcsFileSystem.getDisplayName() = "+VCSName + " " + rootFile.toString());
                    return VCSName + " " + rootFile.toString();
                }
            }
        }
        //System.out.println(g("LAB_FileSystemValid", rootFile.toString ()));
        return g("LAB_FileSystemValid", rootFile.toString ()); // NOI18N
    }

    /**
     * Get a human presentable name of the file system
     */
    public final String getDisplayName() {
        //System.out.print("VcsFileSystem.getDisplayName(): commandsRoot = "+commandsRoot);
        return displayName;
    }

    /**
     * Set the root directory of the filesystem to the parameter passed.
     * @param r file to set root to
     * @exception PropertyVetoException if the value if vetoed by someone else (usually
     *    by the {@link org.openide.filesystems.Repository Repository})
     * @exception IOException if the root does not exists or some other error occured
     */
    private synchronized void setInitRootDirectory(File r) throws PropertyVetoException, IOException {
        Hashtable vars = getVariablesAsHashtable();
        String module = (String) vars.get("MODULE");
        if (module == null) module = "";
        String root = r.getCanonicalPath();
        if (module.length() > 0) {
            int i = root.lastIndexOf(module);
            if (i > 0) root = root.substring(0, i - 1);
        }
        r = new File(root);
        setRootDirectory(r);
    }

    /** Set the root directory of the file system. It adds the module name to the parameter.
     * @param r file to set root to plus module name
     * @exception PropertyVetoException if the value if vetoed by someone else (usually
     *    by the {@link org.openide.filesystems.Repository Repository})
     * @exception IOException if the root does not exists or some other error occured
     */
    public synchronized void setRootDirectory (File r) throws PropertyVetoException, IOException {
        setRootDirectory(r, false);
    }

    /** Set the root directory of the file system. It adds the module name to the parameter.
     * @param r file to set root to plus module name
     * @exception PropertyVetoException if the value if vetoed by someone else (usually
     *    by the {@link org.openide.filesystems.Repository Repository})
     * @exception IOException if the root does not exists or some other error occured
     */
    protected final void setRootDirectory (File r, boolean forceToSet) throws PropertyVetoException, IOException {
        if (/*!r.exists() ||*/ r.isFile ()) {
            throw new IOException(g("EXC_RootNotExist", r.toString ())); // NOI18N
        }

        r = FileUtil.normalizeFile(r);
        String rDir = r.getPath();
        if (rDir.length() == 0) {
            throw new PropertyVetoException("Can not set empty root.", null);
        }
        if (org.openide.util.Utilities.isWindows() && rDir.length() == 2 &&
            Character.isLetter(rDir.charAt(0)) && ':' == rDir.charAt(1)) {
            rDir += "\\"; // A special case for C:\
            r = new File(rDir);
        }
        File root;
        String module;
        synchronized (this) {
            module = getRelativeMountPoint();
            root = new File(r, module);
        }
        if (!forceToSet && rootFile.equals(root)) return ;
        // Provide a possibility to veto the change of the root.
        fireVetoableChange (PROP_ROOT, getRoot(), null);
        String name = computeSystemName (root);
        synchronized (this) {
            /* Ignoring other filesystems' names => it is possible to mount VCS filesystem with the same name.
            Enumeration en = TopManager.getDefault ().getRepository ().fileSystems ();
            while (en.hasMoreElements ()) {
                FileSystem fs = (FileSystem) en.nextElement ();
                if (((org.openide.util.Utilities.isWindows() && fs.getSystemName().equalsIgnoreCase(name))
                     || (!org.openide.util.Utilities.isWindows() && fs.getSystemName().equals(name)))
                    && !fs.equals(this)) { // Ignore my name if I'm already mounted
                    // NotifyDescriptor.Exception nd = new NotifyDescriptor.Exception (
                    throw (PropertyVetoException) TopManager.getDefault().getErrorManager().annotate(
                        new PropertyVetoException (g("EXC_DirectoryMounted"), // NOI18N
                            new PropertyChangeEvent (this, PROP_ROOT, getSystemName (), name)), // NOI18N
                        g("EXC_DirectoryMounted")); // NOI18N
                    // TopManager.getDefault ().notify (nd);
                }
            }
             */
            setAdjustedSystemName(name);

            rootFile = root;
            last_rootFile = new File(getFSRoot());
            ready=true ;

            //HACK
      //      this.cache.refreshDir(this.getRelativeMountPoint());

            if (cache != null) {
                cache.setFSRoot(r.getAbsolutePath());
                cache.setRelativeMountPoint(module);
            }
            // When we change the root, we have to create new attributes,
            // that are with respect to the new root.
            VcsAttributes a = new VcsAttributes (rootFile, info, change, this, this, actionSupporter);
            VcsAttributes oldAttrs = getVcsAttributes();
            if (oldAttrs != null) {
                a.setRuntimeCommandsProvider(oldAttrs.getRuntimeCommandsProvider());
            }
            attr = a;
            list = a;
        }
        displayName = computeDisplayName();
        firePropertyChange(PROP_DISPLAY_NAME, null, null);
        firePropertyChange(PROP_ROOT, null, refreshRoot ());
    }

    /** Modified to never throw PropertyVetoException by the name adjustment. */
    public void setAdjustedSystemName(String name) {
        int seed = 0;
        String testName = name;
        do {
            try {
                setSystemName(testName);
                testName = null;
            } catch (PropertyVetoException pvexc) {
                testName = name + " - " + (++seed); // NOI18N
            }
        } while (testName != null);
    }

    /** Get the root directory of the file system.
     * @return root directory
     */
    public File getRootDirectory () {
        return rootFile;
    }

    /** Set whether the file system should be read only.
     * @param flag <code>true</code> if it should
     */
    public void setReadOnly(boolean flag) {
        if (flag != readOnly) {
            readOnly = flag;
            firePropertyChange (PROP_READ_ONLY, !flag ? Boolean.TRUE : Boolean.FALSE, flag ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    /* Test whether file system is read only.
     * @return <code>true</code> if file system is read only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /** Compute the system name of this file system for a given root directory.
     * <P>
     * The default implementation simply returns the filename separated by slashes.
     * @see FileSystem#setSystemName
     * @param rootFile root directory for the filesystem
     * @return system name for the filesystem
     */
    private String computeSystemNameBase (File rootFile) {
        //System.out.println("computeSystemName()");
        if (preferredSystemName != null) {
            return preferredSystemName;
        }
        Hashtable vars = getVariablesAsHashtable();
        String systemNameAnnotation = (String) vars.get(VAR_FS_SYSTEM_NAME_ANNOTATION);
        if (systemNameAnnotation != null) {
            if (statusProvider != null) {
                systemNameAnnotation = RefreshCommandSupport.getStatusAnnotation("", "", systemNameAnnotation, statusProvider, vars);
            } else {
                systemNameAnnotation = Variables.expand(vars, systemNameAnnotation, false);
            }
            return systemNameAnnotation;
        }
        return this.getClass().getName()+" "+rootFile.toString ().replace(File.separatorChar, '/');
    }

    /** Compute the system name of this file system for a given root directory.
     * <P>
     * The default implementation simply returns the filename separated by slashes
     * or looks for special system name annotation variable.
     * This method also assures, that the system name will be unique among
     * mounted filesystems. Thus when this filesystem will be mounted it will
     * not be invalidated.
     * @see FileSystem#setSystemName
     * @param rootFile root directory for the filesystem
     * @return system name for the filesystem
     */
    protected String computeSystemName(File rootFile) {
        String name = computeSystemNameBase(rootFile);
        String testName = name;
        int seed = 0;
        do {
            Enumeration en = org.openide.filesystems.Repository.getDefault().fileSystems ();
            while (en.hasMoreElements ()) {
                FileSystem fs = (FileSystem) en.nextElement ();
                if (fs == this) continue;
                if (fs.getSystemName().equals(testName)) {
                    testName = name + " - " + (++seed);
                    break;
                }
            }
            if (!en.hasMoreElements()) {
                name = testName;
                testName = null;
            }
        } while (testName != null);
        return name;
    }
    
    /**
     * Set a preferred system name of the filesystem.
     * This have higher priority then VAR_FS_SYSTEM_NAME_ANNOTATION.
     */
    public void setPreferredSystemName(String preferredSystemName) {
        this.preferredSystemName = preferredSystemName;
        setAdjustedSystemName(preferredSystemName);
    }
    
    /**
     * Get a preferred system name of the filesystem.
     * This have higher priority then VAR_FS_SYSTEM_NAME_ANNOTATION.
     */
    public String getPreferredSystemName() {
        return preferredSystemName;
    }

    /** Get file representation for given string name.
     * @param name the name
     * @return the file
     */
    public File getFile (String name) {
        File file = new File (rootFile, name);
        String path = file.getAbsolutePath();
        boolean repaired = false;
        if (path.endsWith(File.separator + ".")) {
            path = path.substring(0, path.length() - 2);
            repaired = true;
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
            repaired = true;
        }
        if (repaired) {
            file = new File(path);
            if (!file.isAbsolute()) {
                // On some systems it might not be safe to remove the last
                // File.separator from the path!!! (e.g. "D:\" must be kept
                // with the last backslash!)
                file = new File(path + File.separator);
            }
        }
        file = FileUtil.normalizeFile(file);
        return file;
    }

    //-------------------------------------------
    //
    // List
    //

    String[] getLocalFiles(String name) {
        File dir = new File(getRootDirectory(), name);
        if (!dir.canRead()) return new String[0];
        String files[] = dir.list();
        if (files == null) return new String[0]; // is null when dir is not a directory
        return files;
    }

    /**
     * The file scheduled for remove is on the disk.
     * If it does not contain VcsAttributes.VCS_SCHEDULING_REMOVE,
     * it will be removed from the list of scheduled files, because it was
     * deleted and reappeared.
     */
    private void checkScheduledLocals(String path, Collection locals, Map removedFilesScheduledForRemove) {
        FileStatusProvider status = getStatusProvider();
        if (status == null) return ;
        VcsConfigVariable schVar = (VcsConfigVariable) variablesByName.get(VAR_STATUS_SCHEDULED_REMOVE);
        String scheduledStatusRemove = (schVar != null) ? schVar.getValue() : null;
        if (scheduledStatusRemove == null) return ;
        for (Iterator it = locals.iterator(); it.hasNext(); ) {
            String name = (path.length() > 0) ? (path + "/" + it.next()) : (""+it.next());
            if (removedFilesScheduledForRemove.containsKey(name)) {
                String primary = (String) removedFilesScheduledForRemove.get(name);
                //System.out.println("checkScheduledLocals("+name+")");
                String attribute = (String) attr.readAttribute(name, VcsAttributes.VCS_SCHEDULED_FILE_ATTR);
                //System.out.println("attr("+VcsAttributes.VCS_SCHEDULED_FILE_ATTR+") = "+attr);
                if (!VcsAttributes.VCS_SCHEDULING_REMOVE.equals(attribute) && isSchedulingDone(name)) {
                    removeScheduledFromPrimary(name, primary, 0);
                }
            }
        }
    }

    String[] addLocalFiles(String name, String[] cachedFiles, Map removedFilesScheduledForRemove) {
        String[] files = getLocalFiles(name);
        String[] mergedFiles;
        if (files == null || files.length == 0) {
            files = new String[0];
            mergedFiles = cachedFiles;
        } else {
            java.util.List cached = new LinkedList(Arrays.asList(cachedFiles));
            java.util.List local = new LinkedList(Arrays.asList(files));
            local.removeAll(cached);
            checkScheduledLocals(name, local, removedFilesScheduledForRemove);
            cached.addAll(local);
            mergedFiles = (String[]) cached.toArray(new String[0]);
        }
        if (cache != null) {
            if (missingFileStatus != null || missingFolderStatus != null) {
                markAsMissingFiles(name, files, cachedFiles);
            }
        }
        return mergedFiles;     // merged files are local + cached virtual = rerged - local
    }

    private void markAsMissingFiles(String name, String[] local, String[] cached) {
        java.util.List locals = Arrays.asList(local);
        //if (name.length() > 0) name += "/";
        ArrayList files = new ArrayList();
        if (missingFileStatus != null && missingFileStatus.equals(missingFolderStatus) &&
            notMissingableFileStatuses.equals(notMissingableFolderStatuses)) {

            for (int i = 0; i < cached.length; i++) {
                if (!locals.contains(cached[i])) files.add(cached[i]);//statusProvider.setFileMissing(name + cached[i]);
            }
            if (files.size() > 0) {
                cache.setExistingFileStatus(name, (String[]) files.toArray(new String[0]),
                                            missingFileStatus, notMissingableFileStatuses, true);
            }
        } else {
            ArrayList folders = new ArrayList();
            String name1 = (name.length() > 0) ? name + "/" : name;
            for (int i = 0; i < cached.length; i++) {
                if (!locals.contains(cached[i])) {
                    if (folder(name1 + cached[i])) folders.add(cached[i]);//statusProvider.setFileMissing(name + cached[i]);
                    else files.add(cached[i]);
                }
            }
            if (missingFileStatus != null && files.size() > 0) {
                cache.setExistingFileStatus(name, (String[]) files.toArray(new String[0]),
                                            missingFileStatus, notMissingableFileStatuses, true);
            }
            if (missingFolderStatus != null && folders.size() > 0) {
                cache.setExistingFileStatus(name, (String[]) folders.toArray(new String[0]),
                                            missingFolderStatus, notMissingableFolderStatuses, true);
            }
        }
    }

    private transient boolean isRootReferenced = false;

    private String[] childrenWithTurbo(String name) {
        String[] files = getLocalFiles(name);

        if (isHideShadowFiles() == false) { // show shadow files
            if (isShowDeadFiles() == false) {
                // TODO hide dead files, where is defined dead status?
                //files = filterDeadFilesOut(name,files);
            }
            if (files != null) {
                // files = filterScheduledSecondaryFiles(name, files);
            }
        }
        return files;

    }

    /* Get children files inside a folder
     * @param name the name of the folder
     */
    public String[] children (String name) {
        //System.out.println("children('"+name+"'), refresh time = "+getVcsRefreshTime());
        String[] vcsFiles = null;
        String[] files = null;

        if (!ready) {
            //System.out.println("children: not ready !!"); // NOI18N
            return new String[0];
        }

        if (Turbo.implemented()) {
            return childrenWithTurbo(name);
        }

        // the old implementation
        if (!isRootReferenced) {
            if (cache != null) {
                cache.createReference(getRoot());
            }
            isRootReferenced = true;
        }

        HashMap removedFilesScheduledForRemove = new HashMap();
        if (cache != null && !isHideShadowFiles()) {// && cache.isDir(name)) {
            Object cacheLocker = new Object();
            cache.readDirFromDiskCache(name/*, cacheLocker*/);
            vcsFiles = cache.getFilesAndSubdirs(name);
            if (!isShowDeadFiles()) {
                vcsFiles = filterDeadFilesOut(name, vcsFiles);
            }
            //System.out.println("  getFilesAndSubdirs = "+VcsUtilities.arrayToString(vcsFiles));
            /*
            String p=""; // NOI18N
            try{
                p=rootFile.getCanonicalPath();
            }
            catch (IOException e){
                E.err(e,"getCanonicalPath() failed"); // NOI18N
            }
            files=cache.dirsFirst(p+File.separator+name,vcsFiles);
            return files;
            */
            if (vcsFiles != null) {
                vcsFiles = filterScheduledSecondaryFiles(name, vcsFiles, removedFilesScheduledForRemove);
            }
        }
        if (vcsFiles == null) files = getLocalFiles(name);
        else files = addLocalFiles(name, vcsFiles, removedFilesScheduledForRemove);
        //cleanupNonExistingAddedFiles(name, files);

        if (cache != null) {
            VcsCacheDir cacheDir = (VcsCacheDir) cache.getDir(name);
            //System.out.println("files = "+VcsUtilities.arrayToString(files));
            //System.out.println("cacheDir = "+cacheDir+"; is Loaded = "+((cacheDir != null) ? ""+cacheDir.isLoaded() : "x")+", is Local = "+((cacheDir != null) ? ""+cacheDir.isLocal() : "x"));
            if (files.length == 0 && (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal())) ||
                (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal())) && areOnlyHiddenFiles(files)) cache.readDir(name/*, false*/); // DO refresh when the local directory is empty !
        }
        //System.out.println("children = "+files);
        //System.out.println("  children = "+VcsUtilities.arrayToString(files));
        return files;
    }

    private static boolean areOnlyHiddenFiles(String[] files) {
        ArrayList fileList = new ArrayList(Arrays.asList(files));
        fileList.remove(".nbintdb"); // NOI18N
        fileList.remove(".nbattrs"); // NOI18N
        fileList.remove("fileSystem.attributes"); // NOI18N
        for (int i = 0; i < fileList.size(); i++) {
            String file = (String) fileList.get(i);
            if (file.endsWith("~")) fileList.remove(i--); // NOI18N
        }
        return fileList.size() == 0;
    }

    private String[] filterDeadFilesOut(String name, String[] vcsFiles) {

        assert Turbo.implemented() == false;

        if (vcsFiles == null) return null;
        FileStatusProvider statusProvider = getStatusProvider();
        if (statusProvider == null) return vcsFiles;
        FileCacheProvider cacheProvider = getCacheProvider();
        // If the folder is not in the cache, do not search it's status
        if (cacheProvider != null && !cacheProvider.isDir(name)) return vcsFiles;
        ArrayList files = new ArrayList(Arrays.asList(vcsFiles));
        int n = files.size();
        for (int i = 0; i < n; i++) {
            String file = (name.length() > 0) ? (name + "/" + (String) files.get(i)) : (String) files.get(i);
            if (cacheProvider != null && !cacheProvider.isFile(file)) continue;
            if (VcsCacheFile.STATUS_DEAD.equals(statusProvider.getFileStatus(file))) {
                files.remove(i--);
                n--;
            }
        }
        return (String[]) files.toArray(new String[0]);
    }

    private transient Vector scheduledFilesToBeProcessed;

    /**
     * The scheduling action is started for a file.
     * Mark this file as being processed by a scheduling action.
     */
    void addScheduledFileToBeProcessed(String name) {
        if (scheduledFilesToBeProcessed == null) {
            scheduledFilesToBeProcessed = new Vector();
        }
        scheduledFilesToBeProcessed.add(name);
    }

    /**
     * The scheduling action is done for a file.
     * Remove this file from the list of files being processed by a scheduling action.
     */
    void removeScheduledFileToBeProcessed(String name) {
        if (scheduledFilesToBeProcessed == null) {
            scheduledFilesToBeProcessed = new Vector();
        }
        scheduledFilesToBeProcessed.remove(name);
    }

    /** test, whether the scheduling action was done */
    private boolean isSchedulingDone(String name) {
        return scheduledFilesToBeProcessed == null || !scheduledFilesToBeProcessed.contains(name);
    }

    /**
     * Filter files, that are scheduled for remove.
     * @param packageName the name of a package the files come from.
     * @param files the original list of files
     * @param removedFiles the map of pairs of removed file names and package names.
     *        This map is filled by these pairs in the method.
     * @return the filtered list of files. All files, that are scheduled for remove
     *         are filtered out.
     */
    private String[] filterScheduledSecondaryFiles(String packageName, String[] files, Map removedFiles) {
        ArrayList filtered = new ArrayList(Arrays.asList(files));
        boolean emptyPackage = (packageName.length() == 0);
        for (int i = 0; i < files.length; i++) {
            String fileName = (emptyPackage) ? files[i] : (packageName + "/" + files[i]);
            Set[] scheduled = (Set[]) attr.readAttribute(fileName, VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            //System.out.println("filterScheduledSecondaryFiles("+packageName+"): "+ packageName + "/" + files[i]+" scheduled = "+scheduled);
            if (scheduled != null && scheduled[0] != null) {
                LinkedList toRemove = new LinkedList();
                for (Iterator it = scheduled[0].iterator(); it.hasNext(); ) {
                    String secFile = (String) it.next();
                    //System.out.println("secFile = '"+secFile+"'");
                    if (!emptyPackage && secFile.startsWith(packageName) ||
                        emptyPackage && secFile.indexOf('/') < 0) {
                        //System.out.println("removing '"+secFile.substring(packageName.length() + 1)+"'");
                        String nameOnly = (emptyPackage) ? secFile : secFile.substring(packageName.length() + 1);
                        boolean removed = filtered.remove(nameOnly);
                        // if removed is true, the file was in the original list
                        //System.out.println("removed = "+removed+", filtered.contains("+nameOnly+") = "+filtered.contains(nameOnly));
                        if (!removed) {
                            // if the file was not in the original list, delete it's attibutes,
                            // because it's supposed to be deleted in the VCS repository.
                            toRemove.add(secFile);
                            try {
                                attr.writeAttribute(secFile, VcsAttributes.VCS_SCHEDULED_FILE_ATTR, null);
                            } catch (IOException exc) {}
                        } else {
                            // the file was removed from the list and put to the list of removed files
                            removedFiles.put(secFile, fileName);
                        }
                    }
                }
                if (toRemove.size() > 0) {
                    // we have some files, that were not present in the original list
                    // and therefore their scheduling attributes has to be cleaned.
                    scheduled[0].removeAll(toRemove);
                    scheduled = cleanScheduledAttrs(scheduled);
                    try {
                        attr.writeAttribute(fileName, VcsAttributes.VCS_SCHEDULED_FILES_ATTR, scheduled);
                        if (scheduled == null) {
                            attr.writeAttribute(fileName, VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                        }
                    } catch (IOException exc) {}
                    // We have removed some sec. files and we must assure, that
                    // the corresponding node will refresh it's status annotation:
                    statusChanged(fileName);
                }
            }
        }
        return (String[]) filtered.toArray(new String[0]);
    }

    /**
     * Clean the array of sets of scheduled file names.
     * If any set in this array becomes empty, substitute it by <code>null</code>,
     * if all items in the array are <code>null</code>, return <code>null</code>.
     * @param scheduled the array of sets of scheduled file names.
     * @return the array cleaned as much as possible
     */
    private static final Set[] cleanScheduledAttrs(Set[] scheduled) {
        //System.out.print("cleanScheduledAttrs("+scheduled+") = ");
        boolean canClean = true;
        for (int k = 0; k < scheduled.length; k++) {
            if (scheduled[k] != null && scheduled[k].size() == 0) scheduled[k] = null;
            if (scheduled[k] != null) {
                canClean = false;
                break;
            }
        }
        if (canClean) scheduled = null;
        //System.out.println(scheduled);
        return scheduled;
    }

    //-------------------------------------------
    //
    // Change
    //

    /**
     * Lock the files so that they can not be modified in the IDE.
     * This is necessary for commands, that make changes to the processed files.
     * It's crutial, that the file does not get modified twice - externally via
     * the update command and internally (e.g. through the Editor).
     * One <b>MUST</b> call {@link #unlockFilesToBeModified} after the command
     * finish.
     * @param path The path of the file to be locked or directory in which all
     *             files will be locked.
     * @param recursively Whether the files in directories should be locked recursively.
     */
    public void lockFilesToBeModified(String path, boolean recursively) {
        if (".".equals(path)) path = "";
        synchronized (lockedFilesToBeModified) {
            // Multiple locks are not considered. It's locked just once.
            lockedFilesToBeModified.put(path, recursively ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    /**
     * Unlock the files that were previously locked by {@link #lockFilesToBeModified}
     * method. It's necessary to call this method with appropriate arguments after
     * the command finish so that the user can edit the files.
     */
    public void unlockFilesToBeModified(String path, boolean recursively) {
        if (".".equals(path)) path = "";
        synchronized (lockedFilesToBeModified) {
            lockedFilesToBeModified.remove(path);
        }
    }
    
    /** Check whether the provided name is locked for modification.
     * If yes, IOException is thrown. */
    private void checkModificationLock(final String name) throws IOException {
        boolean isLocked;
        synchronized (lockedFilesToBeModified) {
            isLocked = lockedFilesToBeModified.get(name) != null;
            if (!isLocked) {
                for (Iterator it = lockedFilesToBeModified.keySet().iterator(); !isLocked && it.hasNext(); ) {
                    String path = (String) it.next();
                    if (path.length() == 0 || (name.startsWith(path) && name.charAt(path.length()) == '/')) {
                        boolean recursively = ((Boolean) lockedFilesToBeModified.get(path)).booleanValue();
                        // either we lock it recursively or there are no more path separators '/':
                        isLocked = (recursively || name.indexOf('/', path.length() + 1) < 0);
                    }
                }
            }
        }
        if (isLocked) {
            IOException exc = new IOException("File "+name+" can be altered by a running VCS command, it's modification in the IDE is temporarily disabled.") {
                // It's necessary to define localized message separately, so that it's written to the Status bar !!!!!! See issue #9069.
                public String getLocalizedMessage() {
                    return NbBundle.getMessage (VcsFileSystem.class, "EXC_file_is_being_modified", name);
                }
            };
            //exc = (IOException) ErrorManager.getDefault().annotate(exc, NbBundle.getMessage (VcsFileSystem.class, "EXC_file_is_being_modified", name));
            throw exc;
        }
    }

    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    public void checkForModifications(String path) {
        checkForModifications(path, true, true, true);
    }
    
    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    public void checkForModifications(String path, final boolean recursively,
                                      boolean refreshData, boolean refreshFolders) {
        //System.out.println("checkForModifications("+path+")");
        if (".".equals(path)) path = "";
        FileObject first = this.findResource(path);
        Enumeration enum = existingFileObjects(first);
        while(enum.hasMoreElements()) {
            FileObject fo = (FileObject) enum.nextElement();
            if (!(fo.isData() && refreshData || fo.isFolder() && refreshFolders)) {
                continue;
            }
            if (!recursively) {
                if (!first.equals(fo.getParent())) {
                    if (!first.equals(fo)) break;
                }
            }
            String name = fo.getPath();
            //System.out.println("refreshResource("+name+")");
            refreshResource(name, true);
        }
    }

    /* Creates new folder named name.
     * @param name name of folder
     * @throws IOException if operation fails
     */
    public void createFolder (String name) throws java.io.IOException {
        if( name.startsWith("/") ){ // NOI18N
            // Jarda TODO
            name=name.substring(1);
        }

        File f = getFile (name);
        Object[] errorParams = new Object[] {
                                   f.getName (),
                                   getDisplayName (),
                                   f.toString ()
                               };

        if (name.equals ("")) { // NOI18N
            throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"), errorParams)); // NOI18N
        }

        if (f.exists()) {
            throw new IOException(MessageFormat.format (g("EXC_FolderAlreadyExist"), errorParams)); // NOI18N
        }

        int lastSeparator = name.lastIndexOf ("/"); // NOI18N
        if (lastSeparator > 0) {
            File folder = getFile(name.substring (0, lastSeparator));
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"),
                        new Object[] { folder.getName(), getDisplayName(), folder.toString() } )); // NOI18N
                }
            }
        }

        boolean b = f.mkdir();
        if (!b) {
            throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"), errorParams)); // NOI18N
        }
        if (cache != null) cache.addFolder(name);
    }

    /** Create new data file.
     * @param name name of the file
     * @return the new data file object
     * @exception IOException if the file cannot be created (e.g. already exists)
     */
    public void createData (String name) throws IOException {
        if( name.startsWith("/") ){ // NOI18N
            // Jarda TODO
            name=name.substring(1);
        }

        File f = getFile (name);

        int lastSeparator = name.lastIndexOf ("/"); // NOI18N
        if (lastSeparator > 0) {
            File folder = getFile(name.substring (0, lastSeparator));
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"),
                        new Object[] { folder.getName(), getDisplayName(), folder.toString() } )); // NOI18N
                }
            }
        }

        if (!f.createNewFile ()) {
            throw new IOException(MessageFormat.format (g("EXC_DataAlreadyExist"),
                new Object[] { f.getName (), getDisplayName (), f.toString () } )); // NOI18N
        }
        /* we do not store local files to cache.
        if (cache != null) {
            cache.addFile(name);
        }
         */
        if (statusProvider != null) {
            statusProvider.setFileStatus(name, statusProvider.getLocalFileStatus());
        }
    }

    private void addParentToRefresher(String name) {
        //System.out.println("addParentToRefresher("+name+")");
        int lastIndex = name.lastIndexOf('/');
        String parent;
        if (lastIndex > 0) {
            parent = name.substring(0, lastIndex);
        } else {
            parent = "";
        }
        if (refresher != null) {
            //System.out.println("addPrefferedFolder("+parent+")");
            refresher.addPrefferedFolder(parent);
        }
    }

    /** Rename a file.
     * @param oldName old name of the file
     * @param newName new name of the file
     */
    public void rename(String oldName, String newName) throws IOException {
        File of = getFile (oldName);
        File nf = getFile (newName);

        // #7086 - (nf.exists() && !nf.equals(of)) instead of nf.exists() - fix for Win32
        if ((nf.exists() && !nf.equals(of)) || !of.renameTo (nf)) {
            final String msg = g("EXC_CannotRename", oldName, getDisplayName (), newName); // NOI18N
            throw new IOException(msg) {
                public String getLocalizedMessage() {
                    return msg;
                }
            };
        }
        /*
        if (cache != null) cache.rename(oldName, newName);
        if (statusProvider != null) {
            String newStatus = statusProvider.getFileStatus(newName);
            if (!notModifiableStatuses.contains(newStatus)) {
                statusProvider.setFileModified(newName);
            }
        }
         */
        addParentToRefresher(oldName);
    }

    /** Delete a file.
     * @param name name of the file
     * @exception IOException if the file could not be deleted
     */
    public void delete (final String name) throws IOException {
        final File file = getFile (name);
        if (!deleteFile(file, name)) {
            throw new IOException () {
                public String getLocalizedMessage () {
                    return g("EXC_CannotDelete", name, getDisplayName (), file.toString ()); // NOI18N
                }
            };
        }
    }

    protected boolean deleteFile(final File file, String name) throws IOException {
        if (!file.exists()) return true; // non existing file is successfully deleted
        if (!file.canWrite()/* || !file.canRead()*/) {
            throw new IOException() {
                /** Localized message. */
                public String getLocalizedMessage () {
                    return g("EXC_CannotDeleteReadOnly", file.toString());
                }
            };
        }
        boolean wasDir = file.isDirectory();
        if (wasDir) {
            // first of all delete whole content
            File[] arr = file.listFiles();
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    if (!deleteFile (arr[i], name + "/" + arr[i].getName())) {
                        return false;
                    }
                }
            }
        }
        boolean success = file.delete();
        //Currently vcscore only allows one fixed text for
        //missing status, but some vcs profiler may want to change the missing 
        //status depending on the context. So we don't delete the cache here,and
        //leave it to the vcs profiler.
        //
        addParentToRefresher(name);
        if (success) callDeleteCommand(name, wasDir);
        return success;
    }

    /**
     * When a file or folder was deleted, a command DELETE_FILE
     * or DELETE_DIR is called. Subclasses can do their own actions here.
     */
    protected void callDeleteCommand(String name, boolean isDir) {
        CommandSupport cmd;
        if (isDir) {
            cmd = getCommandSupport(VcsCommand.NAME_DELETE_DIR);
        } else {
            cmd = getCommandSupport(VcsCommand.NAME_DELETE_FILE);
        }
        if (cmd != null) {
            //if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES)) {
            addDeleteCommand(name, isDir);
                /*
            } else {
                Table files = new Table();
                files.put(name, findResource(name));
                VcsAction.doCommand(files, cmd, null, this);
            }
                 */
        }
    }

    private transient ArrayList deleteFileCommandQueue;// initialized in init()
    private transient ArrayList deleteFolderCommandQueue;// initialized in init()
    private transient Thread deleteCommandThread = null;

    private void addDeleteCommand(String name, boolean isDir) {
        synchronized (deleteFileCommandQueue) {
            if (isDir) {
                deleteFolderCommandQueue.add(name);
            } else {
                deleteFileCommandQueue.add(name);
            }
            deleteFileCommandQueue.notifyAll();
            if (deleteCommandThread == null || !deleteCommandThread.isAlive()) {
                deleteCommandThread = createDeleteCommandThread();
                deleteCommandThread.start();
            }
        }
    }

    private Thread createDeleteCommandThread() {
        return new Thread(new Runnable() {
            public void run() {
                do {
                    boolean changed = true;
                    int n = deleteFolderCommandQueue.size() + deleteFileCommandQueue.size();
                    while (changed) {
                        synchronized (deleteFileCommandQueue) {
                            try {
                                deleteFileCommandQueue.wait(1000);
                            } catch (InterruptedException exc) {}
                        }
                        int n1 = deleteFolderCommandQueue.size() + deleteFileCommandQueue.size();
                        changed = n != n1;
                        n = n1;
                    }
                    ArrayList fileCommand = new ArrayList();
                    ArrayList folderCommand = new ArrayList();
                    synchronized (deleteFileCommandQueue) {
                        fileCommand.addAll(deleteFileCommandQueue);
                        deleteFileCommandQueue.clear();
                        folderCommand.addAll(deleteFolderCommandQueue);
                        deleteFolderCommandQueue.clear();
                    }
                    if (fileCommand.size() > 0) {
                        runDeleteFilesCommand(fileCommand, getCommandSupport(VcsCommand.NAME_DELETE_FILE));
                    }
                    if (folderCommand.size() > 0) {
                        runDeleteFilesCommand(folderCommand, getCommandSupport(VcsCommand.NAME_DELETE_DIR));
                    }
                    synchronized (deleteFileCommandQueue) {
                        try {
                            deleteFileCommandQueue.wait(1000);
                        } catch (InterruptedException exc) {}
                    }
                } while (deleteFolderCommandQueue.size() + deleteFileCommandQueue.size() > 0);
            }
        }, "VCS Delete file/dir command");
    }

    private void runDeleteFilesCommand(java.util.List filesList, CommandSupport cmd) {
        ArrayList files = new ArrayList();
        ArrayList diskFiles = new ArrayList();
        //Table files = new Table();
        for (Iterator it = filesList.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            FileObject fo = findResource(name);
            if (fo != null) {
                files.add(fo);
            } else {
                diskFiles.add(getFile(name));
            }
        }
        Command command = cmd.createCommand();
        command.setFiles((FileObject[]) files.toArray(new FileObject[files.size()]));
        if (command instanceof VcsDescribedCommand) {
            ((VcsDescribedCommand) command).setDiskFiles((File[]) diskFiles.toArray(new File[diskFiles.size()]));
        }
        command.setGUIMode(false);
        command.execute();
        //VcsAction.doCommand(files, cmd, null, this);
    }

    //-------------------------------------------
    //
    // Info
    //

    /**
     * Get last modification time.
     * @param name the file to test
     * @return the date
     */
    public java.util.Date lastModified(String name) {
        File file = getFile(name);
        if (!file.exists()) {
            if (Turbo.implemented()) {
                // TODO file does not exist what is thi smodification time?
                return new Date();
            }

            // the old implementation
            if (cache != null) {
                CacheFile cFile = cache.getDir(name);
                if (cFile == null) cFile = cache.getFile(name);
                if (cFile != null) {
                    String date = cFile.getDate();
                    String time = cFile.getTime();
                    if (date != null && date.length() > 0
                        && time != null && time.length() > 0) {
                            Date pdate = null;
                            try {
                                pdate = java.text.DateFormat.getInstance().parse(date + " " + time);
                            } catch (java.text.ParseException pexc) {}
                            if (pdate != null) return pdate;
                    }
                }
            }
            return new Date(System.currentTimeMillis());
        } else {
            return new Date (file.lastModified ());
        }
        //return new java.util.Date (getFile (name).lastModified ());
    }

    /** Test if the file is folder or contains data.
     * @param name name of the file
     * @return true if the file is folder, false otherwise
     */
    public boolean folder (String name) {
        boolean isFolder;
        if (cache != null) {
            isFolder = cache.isDir(name);
            if (!isFolder && !cache.isFile(name)) {
                isFolder = getFile(name).isDirectory();
            }
        } else {
            isFolder = getFile(name).isDirectory();
        }
        return isFolder;
        // return getFile (name).isDirectory ();
    }

    /** Test whether this file can be written to or not.
     * All folders are not read only, they are created before writting into them.
     * @param name the file to test
     * @return <CODE>true</CODE> if file is read-only
     */
    public boolean readOnly (String name) {
        if(folder(name)) return false;
        return !getFile (name).canWrite ();
    }

    public String mimeType (String name) {
        // Mentioned in #42965: do not return non-null without a good reason.
        return null;
    }

    /** Get the size of a file.
     * @param name the file to test
     * @return the size of the file in bytes or zero if the file does not contain data (does not
     *  exist or is a folder).
     */
    public long size (String name) {
        return getFile (name).length ();
    }

    /** Get input stream to a file.
     * @param name the file to test
     * @return an input stream to read the contents of this file
     * @exception FileNotFoundException if the file does not exists or is invalid
     */
    public InputStream inputStream (String name) throws java.io.FileNotFoundException {
        InputStream in = null;
        try {
            in = new FileInputStream (getFile (name));
        } catch (java.io.FileNotFoundException exc) {
            final String fname = name;
            throw (java.io.FileNotFoundException) ErrorManager.getDefault().annotate(
                new java.io.FileNotFoundException() {
                    public String getLocalizedMessage() {
                        return g("MSG_FileNotExist", fname);
                    }
                }, g("MSG_FileNotExist", fname));
        }
        return in;
    }

    private static final Object GROUP_LOCK = new Object();
    private void fileChanged(final String name) {
        if (statusProvider != null) {
            // Fire the change asynchronously to prevent deadlocks.
            org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    String oldStatus = statusProvider.getFileStatus(name);
                    if (!notModifiableStatuses.contains(oldStatus)) {
                        statusProvider.setFileModified(name);
                    }
                    VcsGroupSettings grSettings = (VcsGroupSettings) SharedClassObject.findObject(VcsGroupSettings.class, true);
                    if (!grSettings.isDisableGroups()) {
                        if (grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_TO_DEFAULT
                        || grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_ASK) {

                            FileObject fo = findResource(name);
                            if (fo != null) {
                                try {
                                    DataObject dobj = DataObject.find(fo);
                                    if (VcsFileSystem.this.isImportant(name)) {
                                        synchronized (GROUP_LOCK) {
                                            DataShadow shadow = GroupUtils.findDOInGroups(dobj);
                                            if (shadow == null) {
                                                // it doesn't exist in groups, add it..
                                                if (grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_ASK) {
                                                    AddToGroupDialog.openChooseDialog(dobj);
                                                } else {
                                                    GroupUtils.addToDefaultGroup(new Node[] {dobj.getNodeDelegate()});
                                                }
                                            }
                                        }
                                    }
                                } catch (DataObjectNotFoundException exc) {
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class FileOutputStreamPlus extends FileOutputStream {
        private String name;
        public FileOutputStreamPlus(File file, String name) throws IOException {
            super(file);
            this.name = name;
        }

        public void close() throws IOException {
            super.close();
            // assure, that we mark the file as modified only once.
            // FileOutputStream will call close() on finalization even when close() was called before.
            if (name != null) {
                if (!isIDESettingsFile(name) && !name.endsWith(getBackupExtension())) {
                    VcsFileSystem.this.fileChanged(name);
                }
                name = null;
            }
        }
    }

    protected String getBackupExtension() {
        return "~"; // NOI18N
    }

    private static boolean isIDESettingsFile(String name) {
        name = name.replace(java.io.File.separatorChar, '/');
        return name.equals(".nbintdb") ||               // NOI18N
               name.endsWith("/.nbintdb") ||            // NOI18N
               name.equals(".nbattrs") ||               // NOI18N
               name.endsWith("/.nbattrs") ||            // NOI18N
               name.equals("filesystem.attributes") ||  // NOI18N
               name.endsWith("/filesystem.attributes"); // NOI18N
    }

    protected void createBackupFile(String name) throws java.io.IOException {
        if (name.endsWith(getBackupExtension()) || isIDESettingsFile(name) ||
            !getFile(name).exists() || !isImportant(name)) {

            return ;
        }
        InputStream in = inputStream(name);
        try {
            OutputStream out = outputStream (name + getBackupExtension());
            try {
                FileUtil.copy(in, out);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /** Get output stream to a file.
     * @param name the file to test
     * @return output stream to overwrite the contents of this file
     * @exception IOException if an error occures (the file is invalid, etc.)
     */
    public OutputStream outputStream (String name) throws java.io.IOException {
        if (isCreateBackupFiles()) {
            createBackupFile(name);
        }
        FileOutputStream output = new FileOutputStreamPlus (getFile (name), name);
        return output;
    }

    /*
    public synchronized boolean getPromptForLockResult() {
        return promptForLockResult;
    }

    public synchronized void setPromptForLockResult(boolean promptForLockResult) {
        this.promptForLockResult = promptForLockResult;
    }
     */
    
    /**
     * Test whether the current user has locked file with given locker attribute.
     * @param lockerAttribute The file locker attribute. It can contain more
     *                        lockers separated by commas.
     * @param lockerUserName The locker user name, or <code>null</code> when
     *                       the system user name should be taken.
     * @return Whether there is the locker user name one of the lockers in the
     *         locker attribute.
     */
    public static boolean lockerMatch(String lockerAttribute, String lockerUserName) {
        if (lockerAttribute != null) {
            if (lockerUserName == null || lockerUserName.length() == 0) {
                lockerUserName = System.getProperty("user.name");
            }
            int comma = lockerAttribute.indexOf(',');
            if (comma < 0) {
                if (lockerAttribute.equals(lockerUserName)) {
                    return true;
                }
            } else {
                int begin = 0;
                do {
                    if (lockerAttribute.substring(begin, comma).trim().equals(lockerUserName)) {
                        return true;
                    }
                    begin = comma + 1;
                    if (begin > lockerAttribute.length()) {
                        break;
                    }
                    comma = lockerAttribute.indexOf(',', begin);
                    if (comma < 0) comma = lockerAttribute.length();
                } while (true);
            }
        }
        return false;
    }

    /**
     * Whether the LOCK command should be performed for this file. This method should
     * check whether the file is already locked. <p>
     * The default implementation search for the locker status and compare with
     * the user name stored in {@link #VAR_LOCKER_USER_NAME} variable or user.name
     * system property.
     * The fact, that the locker property can contain more locker user names
     * delimited by commas is taken into account.
     * @return true if the file is not locked yet and lock command should run.
     */
    protected boolean shouldLock(String name) {
        /*
        if (getCommand(VcsCommand.NAME_SHOULD_DO_LOCK) != null) {
            Table files = new Table();
            files.put(name, findResource(name));
            return VcsAction.shouldDoLock (files, VcsFileSystem.this);
        } else */
        if (getCommand(VcsCommand.NAME_LOCK) == null) return false; // The LOCK command is not defined
        if (statusProvider != null) {
            String locker = statusProvider.getFileLocker(name);
            String currentLocker = null;
            VcsConfigVariable currentLockerVar = (VcsConfigVariable) variablesByName.get(VAR_LOCKER_USER_NAME);
            if (currentLockerVar != null) {
                currentLocker = currentLockerVar.getValue();
                if (currentLocker != null && currentLocker.length() > 0) {
                    currentLocker = Variables.expand(getVariablesAsHashtable(), currentLocker, false);
                }
            }
            if (lockerMatch(locker, currentLocker)) {
                return false;
            }
        }
        return true;
    }

    /** Table of files to be locked (java.io.File) and the array of LOCK command executors */
    private Map lockCommandExecutors = new HashMap();
    /** Table of files to be edited (java.io.File) and the array of EDIT command executors */
    private Map editCommandExecutors = new HashMap();

    /** Run the LOCK command to lock the file.
     *
     * @param name name of the file
     */
    public void lock (String name_) throws IOException {
        //System.out.println("lock("+name_+")");
        if (!isImportant(name_)) return; // ignore locking of unimportant files
        checkModificationLock(name_);
        final String name = name_;
        //final VcsFileSystem current = this;
        final File file = getFile (name);
        final String filePath = file.getAbsolutePath().intern();
        if (!file.exists()) return; // Ignore the lock when the file does not exist.
        if (isReadOnly()) { // I'm on a read-only filesystem => can not lock
            throw new IOException ("Cannot Lock "+name); // NOI18N
        }
        if (isCallEditFilesOn()) {

            if (Turbo.implemented()) {
                if (!file.canWrite ()) {
                    FileObject fo = getRoot().getFileObject(name_);
                    FileProperties fprops = Turbo.getCachedMeta(fo);
                    if (fprops != null && !fprops.isLocal () && !name.endsWith (".orig")) { // NOI18N
                        if (isPromptForEditOn()) {
                            VcsConfigVariable msgVar = (VcsConfigVariable) variablesByName.get(Variables.MSG_PROMPT_FOR_AUTO_EDIT);
                            String message;
                            if (msgVar != null && msgVar.getValue().length() > 0) message = msgVar.getValue();
                            else message = g("MSG_EditFileCh");
                            throw new FileLockUserQuestionException(message, "EDIT", name, filePath, editCommandExecutors);
                        } else {
                            runFileLockCommand("EDIT", name, filePath, editCommandExecutors);
                        }
                    }
                }
            } else // the old implementation
            if (!file.canWrite ()) {
                VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
                if (vcsFile != null && !vcsFile.isLocal () && !name.endsWith (".orig")) { // NOI18N
                    if (isPromptForEditOn()) {
                        VcsConfigVariable msgVar = (VcsConfigVariable) variablesByName.get(Variables.MSG_PROMPT_FOR_AUTO_EDIT);
                        String message;
                        if (msgVar != null && msgVar.getValue().length() > 0) message = msgVar.getValue();
                        else message = g("MSG_EditFileCh");
                        throw new FileLockUserQuestionException(message, "EDIT", name, filePath, editCommandExecutors);
                    } else {
                        runFileLockCommand("EDIT", name, filePath, editCommandExecutors);
                    }
                }
            }
        }
        if (isLockFilesOn()) {
            VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
            // *.orig is a temporary file created by AbstractFileObject
            // on saving every file to enable undo if saving fails
            if (shouldLock(name) && vcsFile != null && !vcsFile.isLocal () && !name.endsWith (".orig")) { // NOI18N
                if (isPromptForLockOn ()) {
                    VcsConfigVariable msgVar = (VcsConfigVariable) variablesByName.get(Variables.MSG_PROMPT_FOR_AUTO_LOCK);
                    String message;
                    if (msgVar != null && msgVar.getValue().length() > 0) message = msgVar.getValue();
                    else message = g("MSG_LockFileCh");
                    throw new FileLockUserQuestionException(message, "LOCK", name, filePath, lockCommandExecutors);
                } else {
                    runFileLockCommand("LOCK", name, filePath, lockCommandExecutors);
                }
            }
        }
        if (!file.canWrite () && file.exists()) {
            throw new IOException() {
                /** Localized message. */
                public String getLocalizedMessage () {
                    return g("EXC_CannotLockReadOnly", file.toString());
                }
            };
        }
    }
    
    private final class FileLockUserQuestionException extends UserQuestionException {
        
        private String cmdName;
        private String fileName;
        private String filePath;
        private Map commandExecutors;
        
        FileLockUserQuestionException(String message, String cmdName, String fileName,
                                      String filePath, Map commandExecutors) {
            super(message);
            this.cmdName = cmdName;
            this.fileName = fileName;
            this.filePath = filePath;
            this.commandExecutors = commandExecutors;
        }
        
        public void confirmed() throws java.io.IOException {
            runFileLockCommand(cmdName, fileName, filePath, commandExecutors);
        }
    }
    
    private void runFileLockCommand(String cmdName, String fileName,
                                    String filePath, Map commandExecutors) {
        CommandTask exec = null;
        DialogVisualizerWrapper dialogWrapper = createDialogWrapper();
        synchronized (commandExecutors) {
            CommandTask executor = (CommandTask) commandExecutors.get(filePath);
            if (executor != null) {
                if (executor.isFinished()) {
                    commandExecutors.remove(filePath);
                }
            }
            if (!commandExecutors.containsKey(filePath)) {
                Command command = createCommand(cmdName,  fileName, dialogWrapper);
                boolean customized = VcsManager.getDefault().showCustomizer(command);
                if (customized) {
                    exec = command.execute();
                    if (exec != null) {
                        commandExecutors.put(filePath, exec);
                    }
                }
            } else {
                exec = executor;
                dialogWrapper.setTask(exec);
            }
        }
        if (exec != null) {
            waitForCommand(exec, dialogWrapper);
            synchronized (commandExecutors) {
                commandExecutors.remove(filePath);
            }
        }
    }
    
    /** Create a dialog wrapper, if necessary. */
    private static DialogVisualizerWrapper createDialogWrapper() {
        if (EventQueue.isDispatchThread()) {
            return new DialogVisualizerWrapper();
        } else {
            return null;
        }
    }
    
    /** Create a command of a given name, acting on a given file and associate
     *  a dialog wrapper with it. */
    private Command createCommand(String cmdName, String fileName,
                                  DialogVisualizerWrapper wrapper) {
        CommandSupport cmd = getCommandSupport(cmdName);
        Command command = cmd.createCommand();
        command.setFiles(new FileObject[] { findResource(fileName) });
        if (wrapper != null && command instanceof VcsDescribedCommand) {
            ((VcsDescribedCommand) command).setVisualizerWrapper(wrapper);
        }
        return command;
    }
    
    /** Wait safely for the command. A special modal dialog wrapper is used
     *  when we should wait in AWT thread. */
    private static void waitForCommand(CommandTask exec, DialogVisualizerWrapper dialog) {
        if (dialog != null) {
            //DialogVisualizerWrapper dialog = new DialogVisualizerWrapper(exec);
            dialog.show(); // The dialog will wait for the command to finish
        } else {
            try {
                exec.waitFinished(0);
            } catch (InterruptedException iex) {
                // Interrupted, so we continue...
            }
        }
    }

    /** Call the UNLOCK command to unlock the file.
     *
     * @param name name of the file
     */
    public void unlock (String name) {
        //System.out.println("unlock("+name+")");
        if (!isImportant(name)) return; // ignore unlocking of unimportant files

        if (Turbo.implemented()) {
            if(isLockFilesOn ()) {
                FileObject fo = getRoot().getFileObject(name);
                FileProperties fprops = Turbo.getCachedMeta(fo);
                if (fprops != null && !fprops.isLocal () && !name.endsWith (".orig")) { // NOI18N
                    CommandSupport cmd = getCommandSupport("UNLOCK");
                    if (cmd != null) {
                        Command command = cmd.createCommand();
                        command.setFiles(new FileObject[] { findResource(name) });
                        boolean customized = VcsManager.getDefault().showCustomizer(command);
                        if (customized) {
                            CommandTask exec = command.execute();
                        }
                    }
                }
            }

            return;
        }

        // the old implementation
        if(isLockFilesOn ()) {
            VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
            if (vcsFile != null && !vcsFile.isLocal () && !name.endsWith (".orig")) { // NOI18N
                CommandSupport cmd = getCommandSupport("UNLOCK");
                if (cmd != null) {
                    Command command = cmd.createCommand();
                    command.setFiles(new FileObject[] { findResource(name) });
                    boolean customized = VcsManager.getDefault().showCustomizer(command);
                    if (customized) {
                        CommandTask exec = command.execute();
                    }
                }
            }
        }
    }

    //-------------------------------------------
    /** Does nothing to mark the file as unimportant.
     *
     * @param name the file to mark
     *
    public void markUnimportant (String name) {
      // TODO...
        D.deb(" ==== markUnimportant("+name+") ==== "); // NOI18N
            VcsFile file=cache.getFile(name);
            if( file==null ){
              //E.err("no such file '"+name+"'"); // NOI18N
              return ;
            }
            file.setImportant(false);
}
    */

    // TurboListener implementation of CacheHandlerListener ~~~~~~~~~~~~~~
    public void turboChanged(TurboEvent e) {
        FileObject fo = e.getFileObject();
        try {

            // for this filesystem forward as file status changes

            if (fo.getFileSystem() == this) {
                String path = fo.getPath();
                statusChanged(path);
            }
        } catch (FileStateInvalidException e1) {
            ErrorManager err = ErrorManager.getDefault();
            err.annotate(e1, "FileObject = " + fo);  // NOI18N
            err.notify(ErrorManager.INFORMATIONAL, e1);
        }
    }


//-------------------- methods from CacheHandlerListener------------------------
    public void cacheAdded(CacheHandlerEvent event) {
        String root = getRootDirectory().getAbsolutePath();
        String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                if (root.endsWith(File.separator) || root.endsWith("/")) {
                    path = absPath.substring(root.length(), absPath.length());
                } else {
                    path = absPath.substring(root.length() + 1, absPath.length());
                }
            }
            path = path.replace(File.separatorChar, '/');
            statusChanged(path);
        }
    }

    public void cacheRemoved(CacheHandlerEvent event) {
        String root = getRootDirectory().getAbsolutePath();
        CacheFile removedFile = event.getCacheFile();
        String absPath = removedFile.getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                if (root.endsWith(File.separator) || root.endsWith("/")) {
                    path = absPath.substring(root.length(), absPath.length());
                } else {
                    path = absPath.substring(root.length() + 1, absPath.length());
                }
            }
            path = path.replace(File.separatorChar, '/');
            if (removedFile instanceof CacheDir) {
                // refresh the parent folder first, because the removed dir does not have to exist at all any more.
                String parentDir = VcsUtilities.getDirNamePart(path);
                if (!path.equals(parentDir)) {
                    refreshResource(parentDir, true);
                }
                refreshExistingFolders(path);
                statusChanged(path, true);
            } else {
                statusChanged(path);
            }
        }
    }

    public void statusChanged(CacheHandlerEvent event) {
        //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        //System.out.println("statusChanged called for:" + event.getCacheFile().getAbsolutePath());
        String root = getRootDirectory().getAbsolutePath();
        String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                if (root.endsWith(File.separator) || root.endsWith("/")) {
                    path = absPath.substring(root.length(), absPath.length());
                } else {
                    path = absPath.substring(root.length() + 1, absPath.length());
                }
            }
            path = path.replace(File.separatorChar, '/');
            if (event.getCacheFile() instanceof org.netbeans.modules.vcscore.cache.CacheDir) {
                statusChanged(path, event.isRecursive());
            } else {
                statusChanged(path);
            }
            /*
            FileObject fo = findResource(path);
            if (fo == null) {
                E.err("statusChanged().. could not find FileObject.. name=" + path);
                return;
            }
            fireFileStatusChanged (new FileStatusEvent(this, fo, false, true));
             */
        }
    }

    private void addCommandsToHashTable(CommandsTree root) {
        CommandsTree[] children = root.children();
        synchronized (commandsByName) {
            for (int i = 0; i < children.length; i++) {
                CommandSupport cmdSupp = children[i].getCommandSupport();
                if (cmdSupp != null) {
                    commandsByName.put(cmdSupp.getName(), cmdSupp);
                }
                if (children[i].hasChildren()) addCommandsToHashTable(children[i]);
            }
        }
    }

    /**
     * For compatibility reasons only.
     * This method is to be used when deserialization from NetBeans 3.1 and older is performed.
     */
    private void setAdvancedConfig (Object advanced) {
        //super.setAdvancedConfig (advanced);
        this.advanced = advanced;
        Vector commands = (Vector) advanced;
        int len = commands.size();
        if (len == 0) return ;
        commandsByName = new Hashtable(len * 4 / 3 + 1, 0.75f);
        //mainCommands = new Vector();
        //revisionCommands = new Vector();
        org.netbeans.modules.vcscore.cmdline.UserCommand root =
            new org.netbeans.modules.vcscore.cmdline.UserCommand();
        root.setName("ROOT");
        root.setDisplayName("VCS");
        //Children rootCh = new Children.Array();
        commandsRoot = new CommandsTree(new UserCommandSupport(root, this));
        VcsCommand cmd = (VcsCommand) commands.elementAt(0);
        CommandsTree commandList = new CommandsTree(new UserCommandSupport((org.netbeans.modules.vcscore.cmdline.UserCommand) cmd, this));
        commandsRoot.add(commandList);
        for(int i = 1; i < len; i++) {
            VcsCommand uc = (VcsCommand) commands.elementAt(i);
            commandList.add(new CommandsTree(new UserCommandSupport((org.netbeans.modules.vcscore.cmdline.UserCommand) uc, this)));
        }
    }

    /** Set the tree structure of commands.
     * @param root the tree of {@link VcsCommandNode} objects.
     */
    public void setCommands(CommandsTree root) {
        //System.out.println("setCommands()");
        Object old = commandsRoot;
        if (root == null) {
            if (advanced != null) {
                setAdvancedConfig(advanced);
            }
        } else {
            if (commandsByName != null) {
                removeCmdActionsFromSupporter();
            }
            commandsRoot = root;
            commandsByName = new Hashtable();
            addCommandsToHashTable(root);
            addCmdActionsToSupporter();
        }
        VariableInputDescriptorCompat.createInputDescriptorFormExec(commandsByName);
        ((DefaultVcsCommandsProvider) commandsProvider).setCommands(root);
        firePropertyChange(PROP_COMMANDS, old, commandsRoot);
    }

    /** Get the commands.
     * @return the root command
     */
    public CommandsTree getCommands() {
        return commandsRoot;
    }

    /** Get a command by its name.
     * @param name the name of the command to get
     * @return the command of the given name or <code>null</code>,
     *         when the command is not defined
     * @deprecated Retained for compatibility reasons only to be able to use
     *             the old VCS "API" for commands execution. Use
     *             {@link #getCommandSupport()} instead.
     */
    public VcsCommand getCommand(String name) {
        CommandSupport support = getCommandSupport(name);
        if (support instanceof UserCommandSupport) {
            return ((UserCommandSupport) support).getVcsCommand();
        } else {
            return null;
        }
    }

    /**
     * Get a command support by it's name.
     * @param name the name of the command to get
     * @return the command support of the given name or <code>null</code>,
     *         when the command support is not defined
     */
    public CommandSupport getCommandSupport(String name){
        if (commandsByName == null) {
            CommandsTree commands = getCommands();
            if (commands == null) return null;
            setCommands (commands);
        }
        return (CommandSupport) commandsByName.get(name);
    }

    private void addCmdActionsToSupporter() {
        synchronized (commandsByName) {
            for (Iterator it = commandsByName.values().iterator(); it.hasNext(); ) {
                CommandSupport cmdSupport = (CommandSupport) it.next();
                if (cmdSupport instanceof ActionCommandSupport) {
                    Class actionClass = ((ActionCommandSupport) cmdSupport).getActionClass();
                    if (actionClass != null) {
                        actionSupporter.addSupportForAction(actionClass, cmdSupport);
                    }
                }
            }
        }
    }

    private void removeCmdActionsFromSupporter() {
        synchronized (commandsByName) {
            for (Iterator it = commandsByName.values().iterator(); it.hasNext(); ) {
                CommandSupport cmdSupport = (CommandSupport) it.next();
                if (cmdSupport instanceof ActionCommandSupport) {
                    Class actionClass = ((ActionCommandSupport) cmdSupport).getActionClass();
                    if (actionClass != null) {
                        actionSupporter.removeSupportForAction(actionClass);
                    }
                }
            }
        }
    }

    /**
     * The subclasses can define their own filename filter. This additional
     * filename filter can be set through this method.
     */
    protected final void setAdditionalFileFilter(FilenameFilter additionalFileFilter) {
        localFilenameFilter.setOptionalFilter(additionalFileFilter);
        firePropertyChange(PROP_FILE_FILTER, null, getFileFilter());
    }
    
    /**
     * Get the filename filter of this file system. This file system does not
     * filter these files itself.
     */
    public FilenameFilter getFileFilter() {
        return localFilenameFilter;
    }

    /**
     * This method is called from AbstractFileObject.isVirtual. Tests if file
     * really exists or is missing. Some operation on it may be restricted if returns true.
     * @param name of the file
     * @return  true indicates that the file is missing.
     */
    protected boolean checkVirtual(String name) {
        File file = getFile(name);
        return !file.exists();
    }

    /**
     * Perform the check of whether the file is or is not still virtual. This
     * method is called on every file status change with the set of potentially
     * changed files.
     * This method does nothing, subclasses may override it with some meaningfull
     * action (e.g. call setVirtualDataLoader() and invalidate the current
     * DataObject if the setVirtualDataLoader() returns true).
     * @param foSet set of FileObjects whose status was changed
     */
    protected void checkVirtualFiles(Set foSet) {
        Iterator it = foSet.iterator();
        while (it.hasNext()) {
            FileObject o = (FileObject) it.next();
            if (checkVirtual (o.getPath())) {
                try {
                    o.setAttribute ("NetBeansAttrAssignedLoader", VirtualsDataLoader.class.getName());       //NoI18N
                } catch (IOException e) {}
            }
            else {
                if (Turbo.implemented()) {
                    Reference ref = findReference (o.getPath());
                    if ( (ref instanceof FileReference) && ((FileReference)ref).isVirtual()) {
                        try {
                            o.setAttribute ("NetBeansAttrAssignedLoader",null);       //NoI18N
                        } catch (IOException e) {}
                    }
                    return;
                }

                // the old implementation
                Reference ref = findReference (o.getPath());
                if ( (ref instanceof CacheReference) && ((CacheReference)ref).isVirtual()) {
                    try {
                        o.setAttribute ("NetBeansAttrAssignedLoader",null);       //NoI18N
                    } catch (IOException e) {}
                }
            }
        }
    }


    private void settingsChanged(String propName, Object oldVal, Object newVal) {
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        if (GeneralVcsSettings.PROP_USE_GLOBAL.equals(propName)) {
            if (((Boolean) newVal).booleanValue() == true) {
                setOffLine(settings.isOffLine());
                setAutoRefresh(settings.getAutoRefresh());
                setHideShadowFiles(settings.isHideShadowFiles());
            }
        } else {
            if (settings.isUseGlobal()) {
                if (GeneralVcsSettings.PROP_OFFLINE.equals(propName)) {
                    setOffLine(settings.isOffLine());
                } else if (GeneralVcsSettings.PROP_AUTO_REFRESH.equals(propName)) {
                    setAutoRefresh(settings.getAutoRefresh());
                } else if (GeneralVcsSettings.PROP_HOME.equals(propName)) {
                    updateEnvironmentVars();
                } else if (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES.equals(propName)) {
                    setHideShadowFiles(settings.isHideShadowFiles());
                }
            }
        }
    }

    private class FSPropertyChangeListener implements PropertyChangeListener {

        public FSPropertyChangeListener() {
        }

        public void propertyChange(final PropertyChangeEvent event) {
            String propName = event.getPropertyName();
            Object oldValue = event.getOldValue();
            Object newValue = event.getNewValue();
            if (PROP_ANNOTATION_PATTERN.equals(propName)) {
                FileObject root = findResource("");
                Set foSet = new HashSet();
                Enumeration enum = existingFileObjects(root);
                while (enum.hasMoreElements()) {
                    foSet.add(enum.nextElement());
                }
                fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, foSet, false, true));
            }
        }
    }

    private class SettingsPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            settingsChanged(event.getPropertyName(), event.getOldValue(), event.getNewValue());
        }
    }

    private class VcsList implements AbstractFileSystem.List {

        private static final long serialVersionUID = 9164232967348550668L;

        public String[] children(String name) {
            return list.children(name);
        }

    }

    /**
     * The listener on Repository to be informed when this filesystem was mounted
     * and unmounted. addNotify() and removeNotify() are not reliable methods.
     * They can be called even when this filesystem is added into or removed from
     * a multifilesystem.
     */
    private class RegistryListener extends Object implements FSRegistryListener {

        public void fsAdded(org.netbeans.modules.vcscore.registry.FSRegistryEvent ev) {
            if (VcsFileSystem.this.equals(ev.getInfo().getExistingFileSystem())) {
                notifyFSAdded();
            }
        }
        
        public void fsRemoved(org.netbeans.modules.vcscore.registry.FSRegistryEvent ev) {
            if (VcsFileSystem.this.equals(ev.getInfo().getExistingFileSystem())) {
                notifyFSRemoved();
            }
        }
        
    }

    /**
     * A runnable class, that fires the status change for the collected files.
     */
    private class StatusChangeUpdater extends Object implements Runnable {

        private java.util.Set namesToUpdate = new HashSet();

        public StatusChangeUpdater() {
        }

        public synchronized void addNameToUpdate(String name) {
            namesToUpdate.add(name);
        }

        /**
         * Run the status update process.
         */
        public void run() {
            java.util.Set names;
            synchronized (this) {
                names = namesToUpdate;
                namesToUpdate = new HashSet();
            }
            Set fos = findExistingResources(names);
            //System.out.println("statusChanged: findResource("+names+") = "+fos);
            if (fos.isEmpty()) return;
            fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, fos, true, true));
            checkScheduledStates(fos);
            checkVirtualFiles(fos);
        }

    }
    
    private static class LocalFilenameFilter extends Object implements FilenameFilter {
        
        private final boolean ignoreCase;
        private FilenameFilter optionalFilter;
        
        public LocalFilenameFilter() {
            ignoreCase = Utilities.isWindows();
        }
        
        public void setOptionalFilter(FilenameFilter optionalFilter) {
            this.optionalFilter = optionalFilter;
        }
        
        public boolean accept(File dir, String name) {
            if (ignoreCase && IntegritySupportMaintainer.DB_FILE_NAME.equalsIgnoreCase(name) ||
                !ignoreCase && IntegritySupportMaintainer.DB_FILE_NAME.equals(name)) {
                return false;
            } else if (optionalFilter != null) {
                return optionalFilter.accept(dir, name);
            } else {
                return true;
            }
        }
        
    }

    public String getBundleProperty(String s) {
        return g(s);
    }

    public String getBundleProperty(String s, Object obj) {
        return g(s, obj);
    }

    public IgnoreListSupport getIgnoreListSupport () {
        return this.ignoreListSupport;
    }

    public void setIgnoreListSupport (IgnoreListSupport support) {
        this.ignoreListSupport = support;
    }

    //-------------------------------------------
    protected String g(String s) {
        return NbBundle.getMessage(VcsFileSystem.class, s);
    }
    protected String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    protected String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    protected String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }

}
