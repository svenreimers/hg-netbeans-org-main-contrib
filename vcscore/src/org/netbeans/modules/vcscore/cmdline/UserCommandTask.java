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

package org.netbeans.modules.vcscore.cmdline;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.SwingUtilities;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandTaskSupport;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.CommandLineVcsDirReader;
import org.netbeans.modules.vcscore.commands.CommandCustomizationSupport;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandExecutorSupport;
import org.netbeans.modules.vcscore.commands.CommandOutputCollector;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.netbeans.modules.vcscore.commands.CommandOutputVisualizer;
import org.netbeans.modules.vcscore.commands.CommandProcessListener;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.CommandTaskInfo;
import org.netbeans.modules.vcscore.commands.InteractiveCommandOutputVisualizer;
import org.netbeans.modules.vcscore.commands.ProvidedCommand;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.commands.VcsDescribedTask;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.netbeans.modules.vcscore.runtime.RuntimeCommandTask;
import org.netbeans.modules.vcscore.runtime.VcsRuntimeCommand;
import org.netbeans.modules.vcscore.ui.ErrorOutputPanel;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 *
 * @author  Martin Entlicher
 */
public class UserCommandTask extends CommandTaskSupport implements VcsDescribedTask,
                                                                   RuntimeCommandTask,
                                                                   ProvidedCommand {
    /**
     * If this variable is true, the visualizer of the parent task is used
     * as a visualizer of this task as well. This is necessary for visualizers
     * which need to wrap more sub-commands.
     */
    public static final String VAR_USE_PARENT_VISUALIZER = "useParentVisualizer"; // NOI18N
    
    private UserCommandSupport cmdSupport;
    private VcsDescribedCommand cmd;
    private VcsCommandExecutor executor;
    private CommandOutputCollector outputCollector;
    private VcsRuntimeCommand runtimeCommand;
    private VcsCommandVisualizer visualizerGUI;
    private VcsCommandVisualizer visualizerText;
    private boolean parentGUIVisualizer;
    private File spawnRefreshFile;
    private boolean spawnRefreshRecursively;
    private boolean printErrorOutput = true;
    
    /**
     * The set of running tasks.
     * This set is necessary for tasks synchronization.
     */
    private static Set runningTasks = new HashSet();
    
    /**
     * The list of pending tasks, preserving the order in which they were scheduled.
     * This list is necessary for tasks synchronization.
     */
    private static List pendingTasks = new LinkedList();
    
    /** Creates a new instance of UserCommandTask */
    public UserCommandTask(UserCommandSupport cmdSupport, VcsDescribedCommand cmd) {//, VcsCommandExecutor executor) {
        super(cmdSupport, cmd);
        this.cmdSupport = cmdSupport;
        this.cmd = cmd;
        this.executor = cmd.getExecutor(); // Allow the command to provide it's own executor. This is necessary for compatibility reasons.
        if (this.executor == null) this.executor = createExecutor();
        if (this.executor instanceof ExecuteCommand) {
            ((ExecuteCommand) this.executor).setTask(this);
        }
        outputCollector = new CommandOutputCollector(this, cmdSupport.getExecutionContext().getCommandsProvider());
        addTaskListener(new TaskListener() {
            public void taskFinished(Task task) {
                synchronized (pendingTasks) {
                    pendingTasks.remove(task); // It finished, it can not be pending. It might be canceled.
                }
                synchronized (runningTasks) {
                    runningTasks.remove(task); // for sure. If canRun() returns true, but the task is canceled.
                }
                if (visualizerGUI != null && !parentGUIVisualizer) {
                    visualizerGUI.setExitStatus(getExitStatus());
                }
                if (visualizerText != null) {
                    visualizerText.setExitStatus(getExitStatus());
                }
                UserCommandTask.this.removeTaskListener(this);
            }
        });
    }
    
    private VcsCommandExecutor createExecutor() {
        VcsFileSystem fileSystem;
        CommandExecutionContext executionContext = cmdSupport.getExecutionContext();
        if (executionContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) executionContext;
        } else {
            fileSystem = null;
        }
        Hashtable vars = executionContext.getVariablesAsHashtable();
        Map additionalVariables = cmd.getAdditionalVariables();
        if (additionalVariables != null) vars.putAll(additionalVariables);
        UserCommand uCmd = (UserCommand) cmd.getVcsCommand();
        VcsCommandExecutor vce;
        if (fileSystem != null && (VcsCommand.NAME_REFRESH.equals(cmd.getName()) ||
                                   (VcsCommand.NAME_REFRESH + VcsCommand.NAME_SUFFIX_OFFLINE).equals(cmd.getName()))) {
                
            vce = createRefresh(fileSystem, vars, uCmd);
        } else if (fileSystem != null && (VcsCommand.NAME_REFRESH_RECURSIVELY.equals(cmd.getName()) ||
                                          (VcsCommand.NAME_REFRESH_RECURSIVELY + VcsCommand.NAME_SUFFIX_OFFLINE).equals(cmd.getName()))) {
            vce = createRecursiveRefresh(fileSystem, vars, uCmd);
        } else {
            vce = new ExecuteCommand(executionContext, uCmd, vars, cmd.getPreferredExec());
        }
        return vce;
    }
    
    private String getRefreshPath(VcsFileSystem fileSystem, Hashtable vars) {
        String path = null;
        FileObject[] fos = cmd.getFiles();
        if (fos != null && fos.length > 0) {
            path = fos[0].getPath();
            int i = path.lastIndexOf('.');
            if (i != -1 && i > path.lastIndexOf('/')) {
                path = path.substring(0, i);
            }
        } else {
            File[] diskFiles = cmd.getDiskFiles();
            if (diskFiles != null && diskFiles.length > 0) {
                path = diskFiles[0].getAbsolutePath();
                String root = fileSystem.getFile("").getAbsolutePath();
                if (path.indexOf(root) == 0) {
                    path = path.substring(root.length());
                    while (path.startsWith(File.separator)) path = path.substring(1);
                }
            }
        }
        return path;
    }
    
    private File getRefreshDir(VcsFileSystem fileSystem, Hashtable vars) {
        File dir = null;
        FileObject[] fos = cmd.getFiles();
        boolean isDir = true;
        if (fos != null && fos.length > 0) {
            dir = FileUtil.toFile(fos[0]);
            isDir = fos[0].isFolder();
        } else {
            File[] diskFiles = cmd.getDiskFiles();
            if (diskFiles != null && diskFiles.length > 0) {
                dir = diskFiles[0];
                isDir = dir.isDirectory();
            }
        }
        if (dir != null && !isDir) dir = dir.getParentFile();
        return dir;
    }
    
    private VcsCommandExecutor createRefresh(VcsFileSystem fileSystem,
                                             Hashtable vars, UserCommand uCmd) {
        //FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        //FileCacheProvider cache = fileSystem.getCacheProvider();
        //if (statusProvider == null) return null;
        DirReaderListener dirListener = cmdSupport.getAttachedDirReaderListener(cmd);
        if (dirListener != null) {
            String file = (String) vars.get("FILE");
            String dir = (String) vars.get("DIR");
            if (dir.length() > 0) {
                dir += Variables.expand(vars, "${PS}", false) + file;
            } else {
                dir = file;
            }
            vars.put("FILE", "");
            vars.put("DIR", dir);
            this.cmd.setAdditionalVariables(vars);
            //System.out.println("\n\ncreateRefresh(), MODULE = "+vars.get("MODULE")+", DIR = "+vars.get("DIR"));
            return new CommandLineVcsDirReader(dirListener, fileSystem, uCmd, vars);
        } else {
            spawnRefreshFile = new File(getRefreshDir(fileSystem, vars), "test");
            spawnRefreshRecursively = false;
            return null;
        }
    }
    
    private VcsCommandExecutor createRecursiveRefresh(VcsFileSystem fileSystem,
                                                      Hashtable vars, UserCommand uCmd) {
        //FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        //FileCacheProvider cache = fileSystem.getCacheProvider();
        //if (statusProvider == null) return null;
        DirReaderListener dirListener = cmdSupport.getAttachedDirReaderListener(cmd);
        if (dirListener != null) {
            String file = (String) vars.get("FILE");
            String dir = (String) vars.get("DIR");
            if (dir.length() > 0) {
                dir += Variables.expand(vars, "${PS}", false) + file;
            } else {
                dir = file;
            }
            vars.put("FILE", "");
            vars.put("DIR", dir);
            this.cmd.setAdditionalVariables(vars);
            return new CommandLineVcsDirReaderRecursive(dirListener, fileSystem, uCmd, vars);
        } else {
            spawnRefreshFile = new File(getRefreshDir(fileSystem, vars), "test");
            spawnRefreshRecursively = true;
            return null;
        }
        
    }
    
    /** This task should only spawn a refresh task. */
    boolean willSpawnRefresh() {
        return spawnRefreshFile != null;
    }
    
    /** Spawn the refresh task. */
    void spawnRefresh(VcsFileSystem fileSystem) {
        FileSystemCache cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
        Object locker = new Object();
        cache.getCacheFile(spawnRefreshFile,
                           (spawnRefreshRecursively) ? CacheHandler.STRAT_REFRESH_RECURS
                                                     : CacheHandler.STRAT_REFRESH,
                           locker);
        locker = null;
    }
    
    public String getDisplayName() {
        if (willSpawnRefresh()) return null;
        return super.getDisplayName();
    }
    
    public VcsCommandExecutor getExecutor() {
        return executor;
    }
    
    public int getPriority() {
        return VcsCommandIO.getIntegerPropertyAssumeZero(cmd.getVcsCommand(), VcsCommand.PROPERTY_EXEC_PRIORITY);
    }
    
    public FileObject[] getFiles() {
        FileObject[] files = cmd.getFiles();
        //System.out.println("UserCommandTask("+getName()+").getFiles() files = "+((files == null) ? null : java.util.Arrays.asList(files)));
        if (files == null) {
            if (executor != null) {
                VcsFileSystem fileSystem = null;
                CommandExecutionContext executionContext = cmdSupport.getExecutionContext();
                if (executionContext instanceof VcsFileSystem) {
                    fileSystem = (VcsFileSystem) executionContext;
                }
                if (fileSystem != null) {
                    Collection fileNames = ExecuteCommand.createProcessingFiles(executionContext, executor.getVariables());
                    Collection fileObjs = new ArrayList();
                    for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
                        String fileName = (String) it.next();
                        if (".".equals(fileName)) fileName = ""; // NOI18N
                        FileObject fo = fileSystem.findFileObject(fileName);
                        if (fo != null) {
                            fileObjs.add(fo);
                        }
                    }
                    if (fileObjs.size() > 0) {
                        files = (FileObject[]) fileObjs.toArray(new FileObject[0]);
                    }
                }
            }
        }
        //System.out.println("  return "+((files == null) ? null : java.util.Arrays.asList(files)));
        return files;
    }
    
    /** Get variables that are used for the task execution.
     * @return the map of variable names and values.
     *
     */
    public Map getVariables() {
        if (executor == null) {
            return java.util.Collections.EMPTY_MAP;
        } else {
            return executor.getVariables();
        }
    }
    
    /** Get the VcsCommand instance associated with this command.
     * @return The VcsCommand.
     *
     */
    public VcsCommand getVcsCommand() {
        if (executor == null) { // in case we will spawn refresh
            return cmd.getVcsCommand();
        } else {
            return executor.getCommand();
        }
    }
    
    public synchronized RuntimeCommand getRuntimeCommand(CommandTaskInfo info) {
        if (willSpawnRefresh()) return null;
        if (runtimeCommand == null) {
            runtimeCommand = new VcsRuntimeCommand(info);
        }
        return runtimeCommand;
    }
    
    private void initVisualizer(final VcsCommandVisualizer outputVisualizer) {
        //final CommandOutputVisualizer outputVisualizer = new CommandOutputVisualizer(this);
        outputVisualizer.setVcsTask(this);
        outputVisualizer.setPossibleFileStatusInfoMap(cmdSupport.getExecutionContext().getPossibleFileStatusInfoMap());
        outputVisualizer.setOutputCollector(outputCollector);
        outputCollector.addTextOutputListener(new TextOutputListener() {
            public void outputLine(String line) {
                outputVisualizer.stdOutputLine(line);
            }
        });
        outputCollector.addTextErrorListener(new TextOutputListener() {
            public void outputLine(String line) {
                outputVisualizer.errOutputLine(line);
            }
        });
        outputCollector.addRegexOutputListener(new RegexOutputListener() {
            public void outputMatchedGroups(String[] data) {
                outputVisualizer.stdOutputData(data);
            }
        });
        outputCollector.addRegexErrorListener(new RegexOutputListener() {
            public void outputMatchedGroups(String[] data) {
                outputVisualizer.errOutputData(data);
            }
        });
    }
    
    public VcsCommandVisualizer getVisualizer(boolean gui) {
        VcsCommandVisualizer visualizer = null;
        if (gui) {
            visualizer = getVisualizerGUI(false, false);
        } else {
            visualizer = getVisualizerText(false);
            if (isFinished() && visualizer != null) {
                visualizer.setExitStatus(executor.getExitStatus());
            }
        }
        return visualizer;
    }
    
    /**
     * Get the GUI visualizer (if defined).
     * @param showPlainOutput When true, text output visualizer can be returned
     *        if GUI visualizer is not defined.
     * @param interactive When true, an interactive output visualizer can be
     *        returned if GUI visualizer is not defined.
     * @return The GUI visualizer or <code>null</code> when not defined. If
     *         showPlainOutput is true, text output visualizer will be returned
     *         in case that GUI visualizer is not defined.
     */
    synchronized VcsCommandVisualizer getVisualizerGUI(boolean showPlainOutput,
                                                       boolean interactive) {
        VcsCommandVisualizer visualizer = getVisualizerGUI();
        if (visualizer == null && (showPlainOutput || interactive)) {
            visualizer = getVisualizerText(interactive);
        }
        if (isFinished() && visualizer != null) {
            visualizer.setExitStatus(executor.getExitStatus());
        }
        return visualizer;
    }
    
    private synchronized VcsCommandVisualizer getVisualizerGUI() {
        if (visualizerGUI == null) {
            String useParent;
            if ((useParent = (String) executor.getVariables().get(VAR_USE_PARENT_VISUALIZER)) != null && useParent.length() > 0) {
                // Ask the parent task for the visualizer. If the parent
                UserCommandTask parentTask = (UserCommandTask) CommandProcessor.getInstance().getParentTask(this);
                if (parentTask != null) {
                    visualizerGUI = parentTask.getVisualizerGUI();
                    parentGUIVisualizer = true;
                }
            }
            if (visualizerGUI == null) {
                visualizerGUI = executor.getVisualizer();
            }
            if (visualizerGUI != null) {
                initVisualizer(visualizerGUI);
            }
        }
        return visualizerGUI;
    }
    
    private synchronized VcsCommandVisualizer getVisualizerText(boolean interactive) {
        if (visualizerText == null) {
            visualizerText = (interactive) ? new InteractiveCommandOutputVisualizer() : new CommandOutputVisualizer();
            initVisualizer(visualizerText);
        }
        return visualizerText;
    }
    
    public boolean hasGUIVisualizer() {
        return visualizerGUI != null || executor.getVisualizer() != null;
    }
    
    /**
     * Tell, whether the task can be executed now. The task may wish to aviod parallel
     * execution with other tasks or other events. This method is to be used for this
     * purpose.
     * @return <code>true</code> if the task is to be executed immediately. This is the
     *                           default implementation.
     *         <code>false</code> if the task should not be executed at this time.
     *                            In this case the method will be called later to check
     *                            whether the task can be executed already.
     */
    protected boolean canExecute() {
        if (willSpawnRefresh()) return true;
        if (!processedPreCommands()) return false;
        return canRun(this);
    }

    protected int execute() {
        int status = STATUS_SUCCEEDED;
        try {
            //runningTasks.add(this);//, Thread.currentThread());
            // Task is added as running when canRun() returns true. It's too late to do it here!
            // canRun() can be called quickly one after another and excute() in a lazy thread later.
            status = super.execute();
        } finally {
            synchronized (runningTasks) {
                runningTasks.remove(this);
                //System.out.println("RUNNING TASK REMOVED: "+this);
            }
        }
        if (executor != null) doPostprocessing();
        return status;
    }
    
    private volatile Boolean preCommandsProcessed;
    
    private boolean processedPreCommands() {
        if (preCommandsProcessed == null) {
            final String[] preCommands = getPreCommands();
            boolean havePreCommands = preCommands != null && preCommands.length > 0;
            preCommandsProcessed = (havePreCommands) ? Boolean.FALSE : Boolean.TRUE;
            if (havePreCommands) {
                final HashSet tasks = new HashSet(preCommands.length);
                CommandProcessor.getInstance().addCommandProcessListener(new CommandProcessListener() {

                    public VcsCommandsProvider getProvider() {
                        return UserCommandTask.this.getProvider();
                    }

                    public void commandPreprocessing(Command cmd){}

                    public void commandPreprocessed(Command cmd, boolean status) {}

                    public void commandStarting(CommandTaskInfo info) {}

                    public void commandDone(CommandTaskInfo info) {
                        int size;
                        synchronized (tasks) {
                            tasks.remove(info.getTask());
                            size = tasks.size();
                        }
                        if (size == 0) {
                            CommandProcessor.getInstance().removeCommandProcessListener(this);
                            preCommandsProcessed = Boolean.TRUE;
                        }
                    }
                    
                });
                for (int i = 0; i < preCommands.length; i++) {
                    Command preCmd = getProvider().createCommand(preCommands[i]);
                    if (preCmd != null) {
                        ((VcsDescribedCommand) preCmd).setAdditionalVariables(executor.getVariables());
                        synchronized (tasks) {
                            tasks.add(preCmd.execute());
                        }
                    }
                }
            }
        }
        return preCommandsProcessed.booleanValue();
    }
    
    private String[] getPreCommands() {
        if (executor != null) {
            VcsCommand cmd = executor.getCommand();
            String preCommandsStr = (String) cmd.getProperty(VcsCommand.PROPERTY_PRE_COMMANDS);
            if (preCommandsStr != null) {
                preCommandsStr = Variables.expand(executor.getVariables(), preCommandsStr, false);
                String[] preCommands = VcsUtilities.getQuotedStrings(preCommandsStr);
                return preCommands;
            }
        }
        return null;
    }
    
    private void doPostprocessing() {
        VcsCommand cmd = executor.getCommand();
        CommandExecutionContext executionContext = cmdSupport.getExecutionContext();
        String message = null;
        String name = cmd.getDisplayName();
        // In case the utility command does not have a parent, report the message as well.
        if (name == null && CommandProcessor.getInstance().getParentTask(this) == null) {
            name = cmd.getName();
        }
        if (name != null) {
            int i = name.indexOf('&');
            if (i >= 0) name = name.substring(0, i) + name.substring(i + 1);
            switch (executor.getExitStatus()) {
                case VcsCommandExecutor.SUCCEEDED:
                    message = g("MSG_Command_name_finished", name);
                    break;
                case VcsCommandExecutor.FAILED:
                    if (VcsCommandIO.getBooleanPropertyAssumeDefault(executor.getCommand(),
                                                                     VcsCommand.PROPERTY_IGNORE_FAIL)) {
                        message = g("MSG_Command_name_finished", name);
                    } else {
                        message = g("MSG_Command_name_failed", name);
                    }
                    break;
                case VcsCommandExecutor.INTERRUPTED:
                    message = g("MSG_Command_name_interrupted", name);
                    break;
            }
        }
        String notification = null;
        if (executor.getExitStatus() != VcsCommandExecutor.SUCCEEDED &&
            !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_IGNORE_FAIL)) {
            
            if (printErrorOutput) {
                printErrorOutput(executionContext);
            }
            if (executionContext.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_FAIL_MSG);
            }
        } else {
            if (message != null && executionContext != null) executionContext.debug(message);
            if (executionContext.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_SUCCESS_MSG);
            }
        }
        String disabledNotification = (String) executor.getVariables().get("COMMAND_NOTIFICATION_DISABLED");
        if (notification != null && (disabledNotification == null || disabledNotification.length() == 0)) {
            CommandCustomizationSupport.commandNotification(executor, notification, executionContext);
        }
        CommandExecutorSupport.postprocessCommand(executionContext, executor);
    }
    
    private void setPrintErrorOutput(boolean printErrorOutput) {
        this.printErrorOutput = printErrorOutput;
    }
    
    private void printErrorOutput(final CommandExecutionContext executionContext) {              
        if (executionContext == null) return ;                  
        String exec = getExecutor().getExec();
        CommandTask parentTask = CommandProcessor.getInstance().getParentTask(this);
        if (parentTask != null) {
            if (hasVisualizer((UserCommandTask) parentTask)) return ; // The parent visualizer should report the error.
            else ((UserCommandTask) parentTask).setPrintErrorOutput(false); // This task reports the error
        }
        if ((visualizerGUI == null || !visualizerGUI.doesDisplayError()) &&
            (visualizerText == null || !visualizerText.doesDisplayError())) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    CommandOutputTopComponent outputComponent = CommandOutputTopComponent.getInstance();
                    ErrorOutputPanel errorPanel = outputComponent.getErrorOutput();
                    String cmdName = executor.getCommand().getDisplayName();
                    if (cmdName == null) cmdName = executor.getCommand().getName();
                    final StringBuffer errBuff = new StringBuffer();
                    outputCollector.addTextErrorListener(new TextOutputListener() {
                        public void outputLine(String line) {
                            errBuff.append(line);
                            errBuff.append("\n");
                        }
                    }, false);
                    errorPanel.errorOutput(cmdName, executor.getExec(), errBuff.toString());
                    outputComponent.open();
                }
            });
        }
    }
    
    private static boolean hasVisualizer(UserCommandTask task) {
        return task.visualizerGUI != null || task.visualizerText != null;
    }
    
    /** Get the VCS commands provider, that provided this Command or CommandTask.
     *
     */
    public VcsCommandsProvider getProvider() {
        return cmdSupport.getExecutionContext().getCommandsProvider();
    }
    
    /** Set the VCS commands provider, that provided this Command or CommandTask.
     * This method should be called just by the implementator.
     *
     */
    public void setProvider(VcsCommandsProvider provider) {
        throw new IllegalStateException("setProvider() method should not be called.");
    }
    
    
    // Methods to assure various synchronization of commands follow:
    
    /** @return true if there are two files contained in the same package folder, false otherwise.
     */
    private static boolean areFilesInSamePackage(Collection files1, Collection files2) {
        for(Iterator it1 = files1.iterator(); it1.hasNext(); ) {
            String file1 = (String) it1.next();
            String dir1 = VcsUtilities.getDirNamePart(file1);
            for(Iterator it2 = files2.iterator(); it2.hasNext(); ) {
                String file2 = (String) it2.next();
                String dir2 = VcsUtilities.getDirNamePart(file2);
                if (dir1.equals(dir2)) return true;
            }
        }
        return false;
    }
    
    /** @return true if a file or folder from <code>files1</code> is contained in a folder
     * from <code>files2</code>
     */
    private static boolean isParentFolder(Collection files1, Collection files2) {
        for(Iterator it1 = files1.iterator(); it1.hasNext(); ) {
            String file1 = (String) it1.next();
            for(Iterator it2 = files2.iterator(); it2.hasNext(); ) {
                String file2 = (String) it2.next();
                if (file1.startsWith(file2)) return true;
            }
        }
        return false;
    }
    
    /**
     * @param concurrencyWith the pairs of command names and concurrent execution integer value
     *   <p> i.e.: "ADD 4", "STATUS 1"
     * @return the map of command names and associated integer values
     */
    private HashMap createConcurrencyMap(String concurrencyWith) {
        HashMap map = new HashMap();
        if (concurrencyWith != null) {
            String[] items = VcsUtilities.getQuotedStrings(concurrencyWith);
            for (int i = 0; i < items.length; i++) {
                int index = items[i].lastIndexOf(' ');
                if (index < 0) continue;
                String cmdName = items[i].substring(0, index);
                String concStr = items[i].substring(index + 1);
                int conc;
                try {
                    conc = Integer.parseInt(concStr);
                } catch (NumberFormatException nfexc) {
                    continue;
                }
                map.put(cmdName, new Integer(conc));
            }
        }
        return map;
    }
    
    /**
     * Say whether the command executor can be run now or not. It should be called
     * with a monitor lock on this object.
     * Check its concurrent property and other running or waiting commands.
     * @return true if the command can be run in the current monitor lock, false otherwise.
     */
    private boolean canRun(UserCommandTask task) {
        synchronized (pendingTasks) {
            if (!pendingTasks.contains(task)) {
                pendingTasks.add(task);
                //System.out.println("PENDING TASK ADDED: "+task);
            }
        }
        VcsCommandExecutor vce = task.getExecutor();
        VcsCommand cmd = vce.getCommand();
        //System.out.println("canRun("+cmd.getName()+")");
        // Do not check the maximum number of running commands -- this is checked in CommandProcessor
        Collection files = vce.getFiles();
        int concurrency = VcsCommandIO.getIntegerPropertyAssumeZero(cmd,
                            VcsCommand.PROPERTY_CONCURRENT_EXECUTION);
        String concurrencyWith = (String) cmd.getProperty(VcsCommand.PROPERTY_CONCURRENT_EXECUTION_WITH);
        //System.out.println("  concurrency = "+concurrency+", concurrencyWith = "+concurrencyWith);
        boolean haveToWait = false;
        if (concurrency != VcsCommand.EXEC_SERIAL_INERT) {
            
            HashMap concurrencyMap = createConcurrencyMap(concurrencyWith);
            String name = cmd.getName();
            boolean serialOnFile = (concurrency & VcsCommand.EXEC_SERIAL_ON_FILE) != 0;
            boolean serialOnPackage = (concurrency & VcsCommand.EXEC_SERIAL_ON_PACKAGE) != 0;
            boolean serialWithParent = (concurrency & VcsCommand.EXEC_SERIAL_WITH_PARENT) != 0;
            boolean serialOfCommand = (concurrency & VcsCommand.EXEC_SERIAL_OF_COMMAND) != 0;
            boolean serialOfAll = (concurrency & VcsCommand.EXEC_SERIAL_ALL) != 0;
            //System.out.println("  serialOnFile = "+serialOnFile);
            //System.out.println("  serialOnPackage = "+serialOnPackage);
            //System.out.println("  serialWithParent = "+serialWithParent);
            //System.out.println("  serialOfCommand = "+serialOfCommand);
            //System.out.println("  serialOfAll = "+serialOfAll);
            //System.out.println("  commandsToTestAgainst = "+commandsToTestAgainst);
            //if (serialOfAll && commandsToTestAgainst.size() > 0) return false;
            //commandsToTestAgainst.addAll(commandsToRun);
            //commandsToTestAgainst.addAll(commandsWaitQueue);
            //commandsToTestAgainst.remove(vce);
            Set tasksToTest;
            synchronized (runningTasks) {
                tasksToTest = new HashSet(runningTasks);
            }
            if ((concurrency & VcsCommand.EXEC_SERIAL_WITH_PENDING) != 0) {
                //tasksToTest = new HashSet(runningTasks);
                synchronized (pendingTasks) {
                    for (Iterator pendingIt = pendingTasks.iterator(); pendingIt.hasNext(); ) {
                        UserCommandTask pendingTask = (UserCommandTask) pendingIt.next();
                        if (pendingTask != task) {
                            tasksToTest.add(pendingTask);
                        } else {
                            break;
                        }
                    }
                }
            }
            for(Iterator iter = tasksToTest.iterator(); iter.hasNext(); ) {
                UserCommandTask cwTest = (UserCommandTask) iter.next();
                VcsCommandExecutor ec = cwTest.getExecutor();
                Collection cmdFiles = ec.getFiles();
                VcsCommand uc = ec.getCommand();
                //System.out.println("  testing with cmd = "+uc.getName()+", cmdFiles = "+cmdFiles+", TASK = "+cwTest);
                int cmdConcurrency = VcsCommandIO.getIntegerPropertyAssumeZero(uc, VcsCommand.PROPERTY_CONCURRENT_EXECUTION);
                //System.out.println("  cmdConcurrency = "+cmdConcurrency);
                if (VcsCommand.EXEC_SERIAL_INERT == cmdConcurrency) continue;
                if (serialOfAll) {
                    haveToWait = true;
                    break;
                }
                String cmdName = uc.getName();
                haveToWait = matchSerial(name, cmdName, files, cmdFiles,
                                         serialOnFile, serialOnPackage,
                                         serialWithParent, serialOfCommand);
                if (!haveToWait) {
                    if ((cmdConcurrency & VcsCommand.EXEC_SERIAL_ALL) != 0) {
                        haveToWait = true;
                        break;
                    }
                    haveToWait = matchSerial(cmdName, name, cmdFiles, files,
                                             (cmdConcurrency & VcsCommand.EXEC_SERIAL_ON_FILE) != 0,
                                             (cmdConcurrency & VcsCommand.EXEC_SERIAL_ON_PACKAGE) != 0,
                                             (cmdConcurrency & VcsCommand.EXEC_SERIAL_WITH_PARENT) != 0,
                                             (cmdConcurrency & VcsCommand.EXEC_SERIAL_OF_COMMAND) != 0);
                }
                if (haveToWait) {
                    break;
                }
                Integer concurrencyWithNum = (Integer) concurrencyMap.get(cmdName);
                if (concurrencyWithNum != null) {
                    if (haveToWaitFor(files, cmdFiles, concurrencyWithNum.intValue())) {
                        haveToWait = true;
                        break;
                    }
                }
            }
        }
        //System.out.println("haveToWait = "+haveToWait);
        if (!haveToWait) {
            synchronized (runningTasks) {
                runningTasks.add(task);
                //System.out.println("RUNNING TASK ADDED: "+task);
            }
            synchronized (pendingTasks) {
                pendingTasks.remove(task);
                //System.out.println("PENDING TASK REMOVED: "+task);
            }
        }
        return !haveToWait;
    }
    
    /**
     * Tell whether the serial execution is matched between two commands.
     * @param name The name of the first command
     * @param name2 The name of the second command
     * @param files The collection of files the first command acts on
     * @param files2 The collection of files the second command acts on
     * @param serialOnFile Whether the first command deserves serial execution
     *        on files
     * @param serialOnPackage Whether the first command deserves serial execution
     *        on files in a single package
     * @param serialWithParent Whether the first command deserves serial execution
     *        with commands running on the parent folder of it's files
     * @param serialOfCommand Whether there can not be two commands of the same
     *        name running.
     */
    private static boolean matchSerial(String name, String name2,
                                       Collection files, Collection files2,
                                       boolean serialOnFile, boolean serialOnPackage,
                                       boolean serialWithParent, boolean serialOfCommand) {
        boolean matchOnFile = false;
        boolean matchOnPackage = false;
        boolean matchWithParent = false;
        boolean matchOfCommand = false;
        if (serialOnFile) {
            for(Iterator it = files.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                if (files2.contains(file)) {
                    matchOnFile = true;
                    break;
                }
            }
        }
        if (serialOnPackage) {
            if (areFilesInSamePackage(files, files2)) {
                matchOnPackage = true;
            }
        }
        if (serialWithParent) {
            if (isParentFolder(files, files2)) {
                matchWithParent = true;
            }
        }
        if (serialOfCommand) {
            matchOfCommand = name.equals(name2);
        }
        // if (serialOfCommand && !matchOfCommand) do not wait
        //System.out.println("  matchOnFile = "+matchOnFile+", matchOnPackage = "+matchOnPackage+", matchWithParent = "+matchWithParent+", matchOfCommand = "+matchOfCommand);
        return (!serialOfCommand || matchOfCommand) && (matchOnFile || matchOnPackage || matchWithParent || matchOfCommand);
    }
    
    private static boolean haveToWaitFor(Collection files, Collection cmdFiles, int concurrency) {
        boolean serialOnFile = (concurrency & VcsCommand.EXEC_SERIAL_ON_FILE) != 0;
        boolean serialOnPackage = (concurrency & VcsCommand.EXEC_SERIAL_ON_PACKAGE) != 0;
        boolean serialWithParent = (concurrency & VcsCommand.EXEC_SERIAL_WITH_PARENT) != 0;
        if (serialOnFile) {
            for(Iterator it = files.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                if (cmdFiles.contains(file)) {
                    return true;
                }
            }
        }
        if (serialOnPackage) {
            if (areFilesInSamePackage(files, cmdFiles)) {
                return true;
            }
        }
        if (serialWithParent) {
            if (isParentFolder(files, cmdFiles)) {
                return true;
            }
        }
        return false;
    }
    
    private static String g(String s) {
        return org.openide.util.NbBundle.getMessage(UserCommandTask.class, s);
    }
    
    private static String  g(String s, Object obj) {
        return org.openide.util.NbBundle.getMessage(UserCommandTask.class, s, obj);
    }

}
