/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.util.*;
import javax.swing.*;
import java.awt.event.ActionListener;

import org.openide.*;
import org.openide.awt.JMenuPlus;
import org.openide.util.actions.*;
import org.openide.util.HelpCtx;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.loaders.*;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.Debug;
import org.netbeans.modules.vcscore.caching.VcsFSCache;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.commands.*;

/**
 * The system action of the VcsFileSystem.
 * @author  Pavel Buzek, Martin Entlicher
 * @version 1.0
 */
public class VcsAction extends NodeAction implements ActionListener {
    private Debug E=new Debug("VcsAction", true); // NOI18N
    private Debug D=E;

    protected VcsFileSystem fileSystem = null;
    protected FileObject selectedFileObject = null;
    
    private int actionCommandSubtree; // the command subtree to construct actions from

    public VcsAction(VcsFileSystem fileSystem) {
        this(fileSystem, 0);
    }

    public VcsAction(VcsFileSystem fileSystem, int commandSubtree) {
        this(fileSystem, null);
        actionCommandSubtree = commandSubtree;
    }

    public VcsAction(VcsFileSystem fileSystem, FileObject fo) {
        setFileSystem(fileSystem);
        setSelectedFileObject(fo);
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    protected void setSelectedFileObject(FileObject fo) {
        this.selectedFileObject = fo;
    }

    /*
    public VcsCacheFile parseFromCache (String[] cacheRecord) {
        return null; // TODO
    }
     */

    /**
     * Get a human presentable name of the action.
     * @return the name of the action
     */
    public String getName() {
        return fileSystem.getBundleProperty("CTL_Version_Control"); // NOI18N
    }

    /**
     * Get a help context for the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }

    //public abstract void doList(String path);
    //public abstract void doDetails(Vector files);
    //public abstract void doCheckIn(Vector files);
    //public abstract void doCheckOut(Vector files);
    //public abstract void doAdd(Vector files);
    //public abstract void doRemove(Vector files);
    //public abstract JMenuItem getPopupPresenter();
    //public abstract void doAdditionalCommand(String name, Vector files);
    //protected abstract void doCommand(Vector files, VcsCommand cmd);

    private void killAllCommands() {
        CommandsPool cmdPool = fileSystem.getCommandsPool();
        String[] labels = cmdPool.getRunningCommandsLabels();
        if (labels.length > 0) {
            if (NotifyDescriptor.Confirmation.YES_OPTION.equals (
                            TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                                                             fileSystem.getBundleProperty("MSG_KILL_ALL_CMDS", VcsUtilities.arrayToString(labels)), NotifyDescriptor.Confirmation.YES_NO_OPTION)))) {
                cmdPool.killAll();
            }
        }
    }

    /**
     * Do refresh of a directory.
     * @param path the directory path
     */
    public void doList(String path) {
        //D.deb("doList('"+path+"')"); // NOI18N
        //System.out.println("VcsAction.doList("+path+")");
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        FileCacheProvider cache = fileSystem.getCacheProvider();
        if (statusProvider == null) return;
        if (cache == null || cache.isDir(path)) {
            statusProvider.refreshDir(path);
            return ;
        } else {
            String dirName = VcsUtilities.getDirNamePart(path);
            statusProvider.refreshDir(dirName);
        }
    }

    /**
     * Do recursive refresh of a directory.
     * @param path the directory path
     */
    public void doListSub(String path) {
        //D.deb("doListSub('"+path+"')"); // NOI18N
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        FileCacheProvider cache = fileSystem.getCacheProvider();
        if (statusProvider == null) return;
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY);
        String dirName = ""; // NOI18N
        if (cache == null || cache.isDir(path)) {
            dirName = path;
        }
        else{
            dirName = VcsUtilities.getDirNamePart(path);
        }
        //Object exec = VcsCommandIO.getCommandProperty(cmd, "exec", String.class);
        Object exec = cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (cmd != null && (exec != null && ((String) exec).trim().length() > 0)) {
            statusProvider.refreshDirRecursive(dirName);
        } else {
            RetrievingDialog rd = new RetrievingDialog(fileSystem, dirName, new JFrame(), false);
            VcsUtilities.centerWindow(rd);
            Thread t = new Thread(rd, "VCS Recursive Retrieving Thread - "+dirName); // NOI18N
            t.start();
        }
    }

    /**
     * Lock files in VCS.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public void doLock(Table files) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_LOCK);
        if (cmd != null) doCommand(files, cmd);
    }
    
    /**
     * Unlock files in VCS.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public void doUnlock(Table files) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_UNLOCK);
        if (cmd != null) doCommand(files, cmd);
    }
    
    /**
     * Prepare for edit files in VCS.
     * Note that this method has to block until the command is finished.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public void doEdit(Table files) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_EDIT);
        if (cmd != null) {
            doCommand(files, cmd);
            fileSystem.getCommandsPool().waitToFinish(cmd, files.keySet());
        }
    }

    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @return the command executor of the last executed command.
     */
    public static VcsCommandExecutor doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem) {
        //System.out.println("doCommand("+files+", "+cmd+")");
        boolean[] askForEachFile = null;
        if (files.size() > 1) {
            askForEachFile = new boolean[1];
            askForEachFile[0] = true;
        }
        Hashtable vars = fileSystem.getVariablesAsHashtable();
        VcsCommandExecutor ec = null;
        //Integer synchAccess = new Integer(0);
        //int n = files.size();
        Enumeration filesEnum = files.keys();
        while(filesEnum.hasMoreElements()) {
            String fullName = (String) filesEnum.nextElement();
            if (!filesEnum.hasMoreElements() && askForEachFile != null && askForEachFile[0] == true) {
                askForEachFile = null; // Do not show the check box for the last file.
            }
            /*
            boolean concurrentExecution = false;
            if (!concurrentExecution && ec != null) {
                try {
                    ec.join();
                } catch (InterruptedException e) {
                    // ignoring the interruption -- continuing for next command
                }
            }
             */
            fileSystem.getCommandsPool().waitToRun(cmd, fullName);
            if (additionalVars != null) vars.putAll(additionalVars);
            ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            if (ec == null) return null; // No executor for this command.
            fileSystem.getCommandsPool().add(ec);
            String exec = VcsAction.prepareCommandToExecute(fileSystem, ec, cmd, vars, /*additionalVars, */fullName, (FileObject) files.get(fullName), askForEachFile);
            if (exec == null) return null; // The whole command is canceled
            if (exec.length() == 0) continue; // The command is canceled for this file
            //OutputContainer container = new OutputContainer(cmd);
            //ec = new ExecuteCommand(fileSystem, cmd, vars, exec);
            //ec.setErrorNoRegexListener(container);
            //ec.setOutputNoRegexListener(container);
            //ec.setErrorContainer(container);
            ec.updateExec(exec);
            fileSystem.getCommandsPool().startExecutor(ec);
            //ec.start();
            //cache.setFileStatus(fullName,"Unknown"); // NOI18N
            synchronized (vars) {
                if (askForEachFile != null && askForEachFile[0] == true) {
                    vars = new Hashtable(fileSystem.getVariablesAsHashtable());
                } else {
                    vars = new Hashtable(vars);
                }
            }
        }
        return ec;
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     */
    private void doCommand(Table files, VcsCommand cmd) {
        VcsAction.doCommand(files, cmd, null, fileSystem);
    }

    /**
     * Test if one of the selected nodes is a directory.
     * @return <code>true</code> if at least one selected node is a directory,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnDirectory() {
        if (selectedFileObject != null) return selectedFileObject.isFolder();
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null && dd.getPrimaryFile().isFolder()) return true;
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is a file.
     * @return <code>true</code> if at least one selected node is a file,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnFile() {
        if (selectedFileObject != null) return !selectedFileObject.isFolder();
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null && !dd.getPrimaryFile().isFolder()) return true;
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is the root node.
     * @return <code>true</code> if at least one selected node is the root node,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnRoot() {
        if (selectedFileObject != null) return false;
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd == null) return false;
            String path = dd.getPrimaryFile().getPackageNameExt('/','.');
            //String path = getNodePath(nodes[i]);
            if (path.length() == 0) return true;
        }
        return false;
    }

    /**
     * Add files marked as important.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs which are important.
     */
    protected void addImportantFiles(DataObject dd, Table res) {
        addImportantFiles(dd, res, false);
    }

    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     */
    protected void addImportantFiles(DataObject dd, Table res, boolean all){
        Set ddFiles = dd.files();
        for(Iterator it = ddFiles.iterator(); it.hasNext(); ) {
            FileObject ff = (FileObject) it.next();
            try {
                if (ff.getFileSystem() != fileSystem)
                    continue;
            } catch (FileStateInvalidException exc) {
                continue;
            }
            String fileName = ff.getPackageNameExt('/','.');
            //VcsFile file = fileSystem.getCache().getFile(fileName);
            //D.deb("file = "+file+" for "+fileName);
            //if (file == null || file.isImportant()) {
            if (all || fileSystem.isImportant(fileName)) {
                D.deb(fileName+" is important");
                res.put(fileName, ff);
            }
            else D.deb(fileName+" is NOT important");
        }
    }

    /**
     * Create the command menu item.
     * @param name tha name of the command
     */
    protected JMenuItem createItem(String name){
        JMenuItem item = null;
        VcsCommand cmd = fileSystem.getCommand(name);

        if (cmd == null) {
            //E.err("Command "+name+" not configured."); // NOI18N
            item = new JMenuItem("'"+name+"' not configured.");
            item.setEnabled(false);
            return item;
        }

        //Hashtable vars=fileSystem.getVariablesAsHashtable();
        String label = cmd.getDisplayName();
        //if (label.indexOf('$') >= 0) {
        //    Variables v = new Variables();
        //    label = v.expandFast(vars, label, true);
        //}
        item = new JMenuItem(label);
        String[] props = cmd.getPropertyNames();
        if (props != null && props.length > 0) {
            item.setActionCommand(cmd.getName());
            item.addActionListener(this);
        }
        return item;
    }

    /**
     * Add a popup submenu.
     */
    private void addMenu(Node commands, JMenu parent,
                        boolean onDir, boolean onFile, boolean onRoot) {
        Children children = commands.getChildren();
        for (Enumeration subnodes = children.nodes(); subnodes.hasMoreElements(); ) {
            Node child = (Node) subnodes.nextElement();
            VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
            if (cmd == null) {
                parent.addSeparator();
                continue;
            }
            if (cmd.getDisplayName() == null
                || onDir && !VcsCommandIO.getBooleanPropertyAssumeTrue(cmd, VcsCommand.PROPERTY_ON_DIR)
                || onFile && !VcsCommandIO.getBooleanPropertyAssumeTrue(cmd, VcsCommand.PROPERTY_ON_FILE)
                || onRoot && !VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_ON_ROOT)) {

                continue;
            }
            if (!child.isLeaf()) {
                JMenu submenu;
                String[] props = cmd.getPropertyNames();
                //if (props == null || props.length == 0) {
                submenu = new JMenuPlus(cmd.getDisplayName());
                //} else {
                //    submenu = new JMenuPlus();
                //}
                addMenu(child, submenu, onDir, onFile, onRoot);
                parent.add(submenu);
            } else {
                JMenuItem item = createItem(cmd.getName());
                parent.add(item);
            }
        }
    }

    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        //JMenuItem item=null;
        //Vector commands = fileSystem.getCommands();
        Node commands = fileSystem.getCommands();
        //int len = commands.size();
        //int[] lastOrder = new int[0];
        boolean onRoot = isOnRoot();
        boolean onDir;
        boolean onFile;
        if (onRoot) {
            onDir = onFile = false;
        } else {
            onDir = isOnDirectory();
            onFile = isOnFile();
        }
        Children children = commands.getChildren();
        Node[] commandRoots = children.getNodes();
        if (commandRoots.length <= actionCommandSubtree) return null;
        //int first = 0;
        String name = commandRoots[actionCommandSubtree].getDisplayName();
        /*
        if (len > 0) {
            VcsCommand uc = (VcsCommand) commands.get(first);
            String[] props = uc.getPropertyNames();
            if ((props == null || props.length == 0) && uc.getOrder().length == 1) {
                first++;
                name = uc.getLabel();
            }
        }
        if (name == null) {
            name = fileSystem.getBundleProperty("CTL_Version_Control");
        }
         */
        JMenu menu = new JMenuPlus(name);
        addMenu(commandRoots[actionCommandSubtree], /*first, lastOrder, */menu, onDir, onFile, onRoot);
        return menu;
    }

    /**
     * Test whether the action should be enabled based on the currently activated nodes.
     * @return true for non-empty set of nodes.
     */
    public boolean enable(Node[] nodes) {
        return (nodes.length > 0);
    }

    /**
     * Perform some initial steps before the command can be executed.
     * @return the exec String to run the command on success,
     *         null when execution of this and all associated commands should be cancelled,
     *         an empty String when this command should be skipped, but successive commands
     *         should be run.
     */
    public static String prepareCommandToExecute(VcsFileSystem fileSystem, VcsCommandExecutor ec, VcsCommand cmd,
                                                 Hashtable vars, /*Hashtable additionalVars, */
                                                 String fullName, FileObject fo, boolean[] askForEachFile) {
        String path=""; // NOI18N
        String file=""; // NOI18N
        //if( fileSystem.folder(fullName) ){
        //path=fullName;
        //file=""; // NOI18N
        //}
        //else{
        path = VcsUtilities.getDirNamePart(fullName);
        file = VcsUtilities.getFileNamePart(fullName);
        //}
        
        if (java.io.File.separatorChar != '/') {
            path = path.replace('/', java.io.File.separatorChar);
            fullName = fullName.replace('/', java.io.File.separatorChar);
        }
        vars.put("PATH",fullName); // NOI18N
        vars.put("DIR",path); // NOI18N
        String osName=System.getProperty("os.name");
        //D.deb("osName="+osName); // NOI18N
        if (path.length() == 0 && file.length() > 0 && file.charAt(0) == '/') file = file.substring (1, file.length ());
        vars.put("FILE",file); // NOI18N
        vars.put("MIMETYPE", fo.getMIMEType());
        /*
        if (additionalVars != null) {
            Enumeration keys = additionalVars.keys();
            while(keys.hasMoreElements()) {
                Object key = keys.nextElement();
                vars.put(key, additionalVars.get(key));
            }
        }
         */
        //if (path.length() == 0) vars.put("DIR", "."); // NOI18N
        
        Object confObj = cmd.getProperty(VcsCommand.PROPERTY_CONFIRMATION_MSG);
        String confirmation = (confObj == null) ? "" : (String) confObj; //cmd.getConfirmationMsg();
        if (fileSystem.isImportant(fullName)) {
            confirmation = Variables.expand(vars, confirmation, true);
        } else {
            confirmation = null;
        }
        if (confirmation != null && confirmation.length() > 0) {
            if (NotifyDescriptor.Confirmation.NO_OPTION.equals (
                    TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                        confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                return ""; // The command is cancelled for that file
            }
        }
        
        String exec = ec.preprocessCommand(cmd, vars);
        //PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, cmd, vars);
        //String exec = cmdPerf.process();
        //D.deb("exec from performer = "+exec);
        if (!fileSystem.promptForVariables(exec, vars, cmd, askForEachFile)) {
            fileSystem.debug(fileSystem.getBundleProperty("MSG_CommandCanceled")+"\n"); // NOI18N
            return null;
        }
        
        fileSystem.setNumDoAutoRefresh(fileSystem.getNumDoAutoRefresh(path) + 1, path);
        return exec;
    }
    
    protected void performCommand(final String cmdName, final Node[] nodes) {
        //System.out.println("performCommand("+cmdName+") on "+nodes.length+" nodes.");
        if (cmdName.equals("KILL_ALL_CMDS")) {
            killAllCommands();
            return;
        }
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return;
        boolean processAll = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES) || fileSystem.isProcessUnimportantFiles();
        Table files = new Table();
        //String mimeType = null;
        String path = "";
        boolean refreshDone = false;
        if (selectedFileObject != null) {
            files.put(selectedFileObject.getPackageNameExt('/','.'), selectedFileObject);
            //mimeType = selectedFileObject.getMIMEType();
            if (cmdName.equals(VcsCommand.NAME_REFRESH)) {
                path = selectedFileObject.getPackageName('/');//getNodePath(nodes[i]);
                doList(path);
                refreshDone = true;
            } else if (cmdName.equals(VcsCommand.NAME_REFRESH_RECURSIVELY)) {
                path = selectedFileObject.getPackageName('/');//getNodePath(nodes[i]);
                doListSub(path);
                refreshDone = true;
            }
        } else {
            for(int i = 0; i < nodes.length; i++) {
                //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd != null) addImportantFiles(dd, files, processAll);
                else continue;
                //FileObject ff = dd.getPrimaryFile();
                //mimeType = ff.getMIMEType();
                //ec = (EditorCookie) nodes[i].getCookie(EditorCookie.class);
                if (cmdName.equals(VcsCommand.NAME_REFRESH)) {
                    path = dd.getPrimaryFile().getPackageName('/'); //getNodePath(nodes[i]);
                    doList(path);
                    refreshDone = true;
                } else if (cmdName.equals(VcsCommand.NAME_REFRESH_RECURSIVELY)) {
                    path = dd.getPrimaryFile().getPackageName('/'); //getNodePath(nodes[i]);
                    doListSub(path);
                    refreshDone = true;
                }
            }
            //D.deb("files="+files); // NOI18N
            /*
            if (nodes.length < 1) {
                E.err("No selected nodes error."); // NOI18N
                return ;
            }
             */
        }

        //D.deb("path='"+path+"'"); // NOI18N

        //if (mimeType != null) additionalVars.put("MIMETYPE", mimeType); // NOI18N
        //D.deb("I have MIME = "+mimeType); // NOI18N

        //System.out.println("refreshDone = "+refreshDone+", files.size() = "+files.size());
        if (!refreshDone && files.size() > 0) {
            doCommand (files, cmd);
        }
    }

    public void performAction(Node[] nodes) {
        //D.deb("performAction()"); // NOI18N
        //System.out.println("performAction("+nodes+")");
    }

    public void actionPerformed(final java.awt.event.ActionEvent e){
        //D.deb("actionPerformed("+e+")"); // NOI18N
        //System.out.println("actionPerformed("+e+")");
        final String cmdName = e.getActionCommand();
        //D.deb("cmd="+cmd); // NOI18N
        Runnable cpr;
        if (selectedFileObject != null) {
            cpr = new Runnable() {
                public void run() {
                    performCommand(cmdName, null);
                }
            };
        } else {
            final Node[] nodes = getActivatedNodes();
            cpr = new Runnable() {
                public void run() {
                    performCommand(cmdName, nodes);
                }
            };
        }
        new Thread(cpr, "Vcs Commands Performing Thread").start();
    }

}

/*
 * $Log: 
 *  6    Gandalf-post-FCS1.3.2.1     04/04/00 Martin Entlicher Command run in their
 *       own thread.
 *  5    Gandalf-post-FCS1.3.2.0     03/23/00 Martin Entlicher addImportantFiles()
 *       and isOnRoot() added, some methods moved from CvsAction.
 *  4    Gandalf   1.3         02/10/00 Martin Entlicher 
 *  3    Gandalf   1.2         10/25/99 Pavel Buzek     copyright and log
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         09/30/99 Pavel Buzek     
 * $
 */
