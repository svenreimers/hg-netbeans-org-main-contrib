/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Customizer;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

import org.openide.ErrorManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.nodes.BeanNode;
import org.openide.util.RequestProcessor;
import org.openide.util.UserCancelException;
import org.openide.util.WeakListener;
//import org.openide.nodes.AbstractNode;
//import org.openide.nodes.Children;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.cmdline.UserCommandCustomizer;
//import org.netbeans.modules.vcscore.runtime.*;
//import org.netbeans.modules.vcscore.cache.FileSystemCache;
//import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.util.Table;
//import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.VariableInputDialog;

import javax.swing.*;

/**
 * This class is used as a container of all external commands which are either running or finished.
 * @author  Martin Entlicher
 */
public class CommandProcessor extends Object /*implements CommandListener */{

    /**
     * The preprocessing of the command was cancelled. The command will not be executed.
     */
    public static final int PREPROCESS_CANCELLED = 0;
    
    /**
     * When there are more files selected, the preprocessing needs to be done for
     * next files again. The command will run on the first file, preprocessing will be
     * done for the rest.
     */
    public static final int PREPROCESS_NEXT_FILE = 1;
    
    /**
     * The preprocessing is done. When more files are selected, the command
     * will not be preprocessed for the rest of them.
     */
    public static final int PREPROCESS_DONE = 2;
    
    /** The maximum number of running commands in the system. This prevents overwhelming
     * the system with too many commands running concurrently */
    private static final int MAX_NUM_RUNNING_COMMANDS = 15;
    /** The maximum number of running refresh commands in the system. This prevents
     * overwhelming the system with too many refresh commands with the ability
     * to still run other commands concurrently */
    private static final int MAX_NUM_RUNNING_LISTS = 7;
    /** The maximum number of running commands with a normal priority.
     * This is necessary to leave some space for high-priority commands. */
    private static final int MAX_NORMAL_PRIORITY = 10;
    
    private static long lastCommandID = 0;
    
    private static CommandProcessor instance = null;
    
    /** Contains instances of Command, which are to be preprocessed.  */
    private ArrayList commandsToPreprocess;
    /** The table of instances of CommandTask and associated CommandTaskInfo */
    private Hashtable taskInfos;
    /** Contains instances of CommandTaskInfos, which are waiting to run. */
    private ArrayList taskWaitQueue;
    /** Contains instances of CommandTaskInfos, which are running. */
    private ArrayList tasksRunning;
    /** Contains instances of CommandTaskInfos, which are running as an exception.
     *  they are executed to prevent deadlock. The deadlock can occure if there
     *  were executed the maximum number of commands and they need to run some
     *  sub-commands. These subcommands can not be executed without an introduction
     *  of these exceptional commands. */
    private ArrayList tasksExceptionallyRunning;
    /** The containers of output of commands. Contains pairs of instances of VcsCommandExecutor
     * and instances of CommandOutputCollector */
    //private Hashtable outputContainers;
    /** The table of currently opened standard Visualizers */
    //private Hashtable outputVisualizers;
    /** Contains finished instances of CommandTaskInfo. */
    //private ArrayList commandsFinished;
    private int numRunningListCommands;
    private int numRunningNormalPriority;
    
    /**
     * The threads pool that is used to execute commands.
     */
    private RequestProcessor threadsPool;
    
    private ThreadLocal threadTaskInfo;
    
    /** Map of instances of VcsCommandProvider and associated list of command
     * process listeners. */
    private Map commandListenersByProviders = new HashMap();
    /** List of listeners registered for all providers */
    private List commandListenersForAllProviders = new ArrayList();
    
    private boolean execStarterLoopStarted = false;
    private boolean execStarterLoopRunning = true;

    /** Creates new CommandProcessor */
    private CommandProcessor() {
        commandsToPreprocess = new ArrayList();
        //commandsToRun = new ArrayList();
        taskInfos = new Hashtable();
        taskWaitQueue = new ArrayList();
        tasksRunning = new ArrayList();
        tasksExceptionallyRunning = new ArrayList();
        //commandsFinished = new ArrayList();
        //outputContainers = new Hashtable();
        //outputVisualizers = new Hashtable();
        numRunningListCommands = 0;
        numRunningNormalPriority = 0;
        threadsPool = RequestProcessor.getDefault();
        threadTaskInfo = new ThreadLocal();
    }
    
    /**
     * Get the instance of CommandProcessor.
     */
    public static synchronized CommandProcessor getInstance() {
        if (instance == null) {
            instance = new CommandProcessor();
        }
        return instance;
    }
    
    protected void finalize () {
        cleanup();
    }
    
    /**
     * This stops the execution starter loop.
     * You will not be able to execute any command by CommandProcessor after this method finishes !
     */
    public void cleanup() {
        synchronized (this) {
            //* The FS may still exist i.e. inside a MultiFileSystem => do not interrupt the loop now
            execStarterLoopRunning = false;
            notifyAll();
            // */
        }
    }
    
    /**
     * Get the task's ID. It's a unique task identification number.
     * @param task The task
     * @return the ID or -1 if the task does not have one.
     */
    public long getTaskID(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw != null) return cw.getCommandID();
        else return -1;
    }
    
    /**
     * Get the parent task if any. Parent task is the task that executed
     * the child task.
     * @param task The task.
     * @return It's parent task or <code>null</code> when there is no parent task.
     */
    public CommandTask getParentTask(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw != null) {
            cw = cw.getSubmittingInfo();
            if (cw != null) {
                return cw.getTask();
            }
        }
        return null;
    }
    
    /**
     * Pre-process the task. This will show the command's customizer (if any)
     * and return the status. This method will block until the customization process
     * is finished. This was made to be called from AWT thread. Use {@link #preprocess}
     * method in other cases.
     */
    public boolean preprocessSynchronous(Command cmd) {
        if (cmd == null) {
            throw new NullPointerException("Can not preprocess a 'null' command.");
        }
        return doPreprocess2(cmd);
    }
    
    /**
     * Pre-process the task. This will do the preprocess in the background.
     * It will show the command's customizer, if any.
     */
    public synchronized void preprocess(Command cmd) {
        if (cmd == null) {
            throw new NullPointerException("Can not preprocess a 'null' command.");
        }
        //taskInfos.put(info.getTask(), info);
        //info.setSubmittingThread(Thread.currentThread());
        commandsToPreprocess.add(cmd);
        notifyAll();
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }

    /**
     * Process the task. This will schedule the task for execution.
     */
    public synchronized void process(CommandTaskInfo info) {
        CommandTask commandTask = info.getTask();
        String commandName = commandTask.getName();
        FileObject[] files = commandTask.getFiles();
        taskInfos.put(commandTask, info);
        info.setSubmittingInfo((CommandTaskInfo) threadTaskInfo.get());
        taskWaitQueue.add(info);
        notifyAll();
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }
    
    private synchronized void doPreprocess(final Command cmd) {
        // Use a different RP for commands customization from the RP that is
        // used for commands execution. This is necessary so that commands
        // can not be accidentally started from a thread, that is later
        // used as an execution thread for another command.
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                doPreprocess2(cmd);
            }
        });
    }
    
    /** Do not call directly unless from a known thread. Read the comment in doPreprocess(). */
    private boolean doPreprocess2(Command cmd) {
        List commandListeners;
        Object provider = null;
        if (cmd instanceof ProvidedCommand) {
            provider = ((ProvidedCommand) cmd).getProvider();
        }
        synchronized (commandListenersByProviders) {
            commandListeners = new ArrayList(commandListenersForAllProviders);
            List providerListeners = getCommandListenersForProvider(provider);
            if (providerListeners != null) commandListeners.addAll(providerListeners);
        }
        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandProcessListener) it.next()).commandPreprocessing(cmd);
        }
        boolean status = false;
        try {
            status = showCustomizer(cmd);
        } catch (IntrospectionException iex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, iex);
            status = false;
        } finally {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandProcessListener) it.next()).commandPreprocessed(cmd, status);
            }
        }
        return status;
    }
    
    private List getCommandListenersForProvider(Object provider) {
        if (provider == null) return null;
        List commandListeners = null;
        for (Iterator it = commandListenersByProviders.keySet().iterator(); it.hasNext(); ) {
            Reference ref = (Reference) it.next();
            Object providerL = ref.get();
            if (providerL == null) {
                commandListenersByProviders.remove(ref); // Remove the listeners when they lost their provider
            } else if (providerL == provider) {
                commandListeners = (List) commandListenersByProviders.get(ref);
            }
        }
        return commandListeners;
    }

    /**
     * Locates command customizer (there are plenty of
     * possible registration methods supported) and shows it.
     * <p>
     * It wraps original cutomizer into a panel with
     * additononal buttons: Set as default (if instance of VID),
     * Cancel and OK.
     *
     * @param cmd
     * @return
     * @throws IntrospectionException
     */
    private boolean showCustomizer(Command cmd) throws IntrospectionException {
        /*
        BeanNode beanNode = new BeanNode(cmd);
        Component cust = beanNode.getCustomizer();
        boolean status;
        if (cust != null) {
            DialogDescriptor dlg = new DialogDescriptor(cust, cmd.getDisplayName(),
                                                        true, DialogDescriptor.OK_CANCEL_OPTION,
                                                        DialogDescriptor.OK_OPTION, null);
            status = NotifyDescriptor.OK_OPTION.equals(TopManager.getDefault().notify(dlg));
        } else {
            //PropertyPanel panel = new PropertyPanel(
            //beanNode.getPropertySets();
            // TODO
            status = false;
        }
        return status;
         */
        JButton btnStoreAsDefault = null;
        Component cust = null;
        DialogDescriptor dlg = null;
        //System.out.println("cmd instanceof BeanInfo = "+(cmd instanceof BeanInfo));
        if (cmd instanceof BeanInfo) {
            Class customizerClass = null;
            BeanDescriptor descriptor = ((BeanInfo) cmd).getBeanDescriptor();
            if (descriptor != null) customizerClass = descriptor.getCustomizerClass();
            //System.out.println("customizerClass = "+customizerClass);
            if (customizerClass != null) {
                try {
                    Customizer c = (Customizer) customizerClass.newInstance();
                    c.setObject(cmd);
                    if (c instanceof Component) {
                        cust = (Component) c;
                    } else if (c instanceof DialogDescriptor) {
                        dlg = (DialogDescriptor) c;
                        //cust = org.openide.TopManager.getDefault().createDialog((org.openide.DialogDescriptor) c);
                    }
                } catch (Exception ex) {}
            }
        // HACK with the PrivilegedAction to get a custom customizer
        } else if (cmd instanceof java.security.PrivilegedAction) {
            Object customizer;
            try {
                customizer = ((java.security.PrivilegedAction) cmd).run();
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable th) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, th);
                return false; // Do not let the command to run when the customization failed.
            }
            //System.out.println("customizer of command "+cmd+" = "+customizer);
            //System.out.println("customizer instanceof Component = "+(customizer instanceof Component));
            if (customizer instanceof UserCancelException) {
                return false;
            }
            if (customizer instanceof UserCommandCustomizer) {
                final UserCommandCustomizer vid = (UserCommandCustomizer) customizer;
                if (vid.hasDefaults() && Boolean.getBoolean("VID.navigationPanelOff")) {
                    btnStoreAsDefault = new JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.text"));
                    btnStoreAsDefault.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.mnemonic").charAt(0));
                    btnStoreAsDefault.setToolTipText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.tooltip"));
                    btnStoreAsDefault.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            vid.storeDefaults();
                        }
                    });
                }
                cust = (Component) customizer;
            } else if (customizer instanceof Component) {
                cust = (Component) customizer;
            } else if (customizer instanceof DialogDescriptor) {
                dlg = (DialogDescriptor) customizer;
                //cust = org.openide.TopManager.getDefault().createDialog((org.openide.DialogDescriptor) customizer);
            } else if (customizer instanceof BeanInfo) {
                BeanDescriptor descriptor = ((BeanInfo) customizer).getBeanDescriptor();
                Class customizerClass = null;
                if (descriptor != null) customizerClass = descriptor.getCustomizerClass();
                //System.out.println("customizerClass = "+customizerClass);
                if (customizerClass != null) {
                    try {
                        Customizer c = (Customizer) customizerClass.newInstance();
                        c.setObject(cmd);
                        if (c instanceof Component) {
                            cust = (Component) c;
                        } else if (c instanceof DialogDescriptor) {
                            dlg = (DialogDescriptor) c;
                            //cust = org.openide.TopManager.getDefault().createDialog((org.openide.DialogDescriptor) c);
                        }
                    } catch (Exception ex) {}
                }
            } else if (customizer != null) {
                BeanNode beanNode = new BeanNode(customizer);
                cust = beanNode.getCustomizer();
            }
        } else if (cust == null && dlg == null) {
            BeanNode beanNode = new BeanNode(cmd);
            cust = beanNode.getCustomizer();
        }
        boolean status;
        //System.out.println("customizer = "+cust);
        if (dlg != null) {
            Object option = DialogDisplayer.getDefault().notify(dlg);
            List closingOptions = Arrays.asList(dlg.getClosingOptions());
            if (closingOptions.contains(option)) {
                if (NotifyDescriptor.CANCEL_OPTION.equals(option) ||
                    NotifyDescriptor.CLOSED_OPTION.equals(option) ||
                    NotifyDescriptor.NO_OPTION.equals(option)) {
                    
                    status = false;
                } else {
                    status = true;
                }
            } else {
                status = false;
            }
        } else if (cust != null) {
            ActionListener actionL = null;
            java.lang.reflect.Method addActionListenerMethod = null;
            java.lang.reflect.Method getDisplayNameMethod = null;
            java.lang.reflect.Method getHelpIDMethod = null;
            java.lang.reflect.Method getInitialFocusedComponentMethod = null;
            java.lang.reflect.Method doPostCustomizationWorkMethod = null;
            if (cust instanceof ActionListener) actionL = (ActionListener) cust;
            try {
                addActionListenerMethod = cust.getClass().getMethod("addActionListener", new Class[] { ActionListener.class });
            } catch (Exception ex) {}
            try {
                getDisplayNameMethod = cust.getClass().getMethod("getDisplayTitle",null);
            } catch (Exception ex) {}
            try {
                getHelpIDMethod = cust.getClass().getMethod("getHelpID", null);
            } catch (Exception ex) {}
            try {
                getInitialFocusedComponentMethod = cust.getClass().getMethod("getInitialFocusedComponent", null);
            } catch (Exception ex) {}
            try {
                doPostCustomizationWorkMethod = cust.getClass().getMethod("doPostCustomizationWork", null);
            } catch (Exception ex) {}
            String displayName = null;
            if (getDisplayNameMethod != null) {
                try {
                    displayName = (String) getDisplayNameMethod.invoke(cust, null);
                } catch (Exception ex) {}
            }
            if (displayName == null) {
                displayName = cmd.getDisplayName();
            }
            org.openide.util.HelpCtx helpCtx = null;
            if (getHelpIDMethod != null) {
                try {
                    String helpID = (String) getHelpIDMethod.invoke(cust, null);
                    if (helpID != null) {
                        helpCtx = new org.openide.util.HelpCtx(helpID);
                    }
                } catch (Exception ex) {}
            }
            dlg = new DialogDescriptor(cust, displayName,
                                       true, DialogDescriptor.OK_CANCEL_OPTION,
                                       DialogDescriptor.OK_OPTION,
                                       DialogDescriptor.DEFAULT_ALIGN, helpCtx,
                                       actionL);

            if (btnStoreAsDefault != null) {
                dlg.setAdditionalOptions(new Object[] {btnStoreAsDefault});
            }

            final Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
            if (getInitialFocusedComponentMethod != null) {
                java.awt.Component initialFocusedComponent = null;
                try {
                    initialFocusedComponent = (java.awt.Component) getInitialFocusedComponentMethod.invoke(cust, null);
                } catch (Exception ex) {
                    //status = NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dlg));
                    ErrorManager.getDefault().notify(ex);
                }
                if (initialFocusedComponent != null) {
                    dialog.setFocusTraversalPolicy(
                        new InitialComponentFocusTraversalPolicy(dialog.getFocusTraversalPolicy(),
                            					 initialFocusedComponent));
                }
            }

            final boolean [] statusContainer = new boolean[1];
            
            if (addActionListenerMethod != null) {
                dlg.setClosingOptions(new Object[] { NotifyDescriptor.CANCEL_OPTION });
                
                final JButton setAsDefault0 = btnStoreAsDefault;
                ActionListener actionListener = new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getID() == ActionEvent.ACTION_PERFORMED) {
                            if (NotifyDescriptor.OK_OPTION.equals(evt.getSource())) {
                                statusContainer[0] = true;
                                dialog.dispose();
                            } else if (NotifyDescriptor.CANCEL_OPTION.equals(evt.getSource())) {
                                statusContainer[0] = false;
                                dialog.dispose();
                            } else if (evt.getSource() == setAsDefault0) {
                                setAsDefault0.doClick(0);
                            }
                        }
                    }
                };
                
                try {
                    //final boolean [] statusContainer = new boolean[1];
                    addActionListenerMethod.invoke(cust, new Object[] { actionListener });
                } catch (Exception ex) {
                    //status = NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dlg));
                    ErrorManager.getDefault().notify(ex);
                }
            }
            dialog.setVisible(true);
            status = statusContainer[0];
            if (status && doPostCustomizationWorkMethod != null) {
                try {
                    doPostCustomizationWorkMethod.invoke(cust, null);
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        } else {
            //PropertyPanel panel = new PropertyPanel(
            //beanNode.getPropertySets();
            // TODO
            status = true;
        }
        //System.out.println("CommandProcessor.showCustomizer() = "+status);
        return status;
    }

    private void commandStarted(final CommandTaskInfo cw) {
        //final VcsCommandExecutor vce = cw.getExecutor();
        //setCommandID(vce);
        //final VcsFileSystem fileSystem = cw.getFileSystem();
        //if (fileSystem == null) return ;
        final CommandTask cmdTask = cw.getTask();
        //waitToRun(cmd, vce.getFiles());
        // Assure that the files we'll be acting on are saved.
        FileObject[] files = cmdTask.getFiles();
        if (files != null) VcsAction.assureFilesSaved(Arrays.asList(files));
        final String name = cmdTask.getDisplayName();
        //if (name == null || name.length() == 0) name = cmd.getName();
        //final String finalName = name;
        if (name != null) {
            String cmdName;
            int i = name.indexOf('&');
            if (i >= 0) cmdName = name.substring(0, i) + name.substring(i + 1);
            else cmdName = name;
            StatusDisplayer.getDefault().setStatusText(g("MSG_Command_name_running", cmdName));
            /*
            if (fileSystem != null) {
                fileSystem.debug(g("MSG_Command_started", name, vce.getExec()));
            }
             */
        }
        //System.out.println("command "+cmdTask.getName()+" STARTED.");
        //Command cmd = cmdTask.getCommand();
        Object provider = null;
        if (cmdTask instanceof ProvidedCommand) {
            provider = ((ProvidedCommand) cmdTask).getProvider();
        }
        List commandListeners;
        synchronized (commandListenersByProviders) {
            commandListeners = new ArrayList(commandListenersForAllProviders);
            List providerListeners = getCommandListenersForProvider(provider);
            if (providerListeners != null) commandListeners.addAll(providerListeners);
        }


        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandProcessListener) it.next()).commandStarting(cw);
        }
        cw.setStartTime(System.currentTimeMillis());
    }
    
    private void commandDone(CommandTaskInfo cw) {
        cw.setFinishTime(System.currentTimeMillis());
        CommandTask cmdTask = cw.getTask();
        //VcsCommandExecutor vce = cw.getExecutor();
        //System.out.println("commandDone("+cw.getExecutor().getCommand().getName()+")");
        //VcsFileSystem fileSystem = cw.getFileSystem();
        //VcsCommand cmd = vce.getCommand();
        String name = cmdTask.getDisplayName();
        //if (name == null || name.length() == 0) name = cmd.getName();
        synchronized (this) {
            if (isListCommandTask(cmdTask)) numRunningListCommands--;
            if (cmdTask.getPriority() == 0) numRunningNormalPriority--;
            tasksRunning.remove(cw);
            //commandsFinished.add(cw);
            tasksExceptionallyRunning.remove(cw);
            taskInfos.remove(cmdTask);
        }
        //Command cmd = cmdTask.getCommand();
        Object provider = null;
        if (cmdTask instanceof ProvidedCommand) {
            provider = ((ProvidedCommand) cmdTask).getProvider();
        }
        List commandListeners;
        synchronized (commandListenersByProviders) {
            commandListeners = new ArrayList(commandListenersForAllProviders);
            List providerListeners = getCommandListenersForProvider(provider);
            if (providerListeners != null) commandListeners.addAll(providerListeners);
        }
        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandProcessListener) it.next()).commandDone(cw);
        }
        synchronized (this) {
            notifyAll(); // Notify starter loop after we have notified all listeners.
            // This is necessary for custom canRun() implementation is tasks,
            // which can listen on tasks via CommandListener
        }
        /*
        if (fileSystem != null) {
            CommandExecutorSupport.postprocessCommand(fileSystem, vce);
        }
         */
        //System.out.println("command "+vce.getCommand()+" DONE, LISTENERS DONE.");
        //System.out.println("command "+cmdTask.getName()+" DONE, LISTENERS DONE.");
        if (name != null) {
            int exit = cmdTask.getExitStatus();
            if (cw.isInterrupted()) exit = CommandTask.STATUS_INTERRUPTED;
            //String name = vce.getCommand().getDisplayName();
            int i = name.indexOf('&');
            if (i >= 0) name = name.substring(0, i) + name.substring(i + 1);
            String message = "";
            switch (exit) {
                case CommandTask.STATUS_SUCCEEDED:
                    message = g("MSG_Command_name_finished", name);
                    break;
                case CommandTask.STATUS_FAILED:
                    if (cmdTask instanceof VcsDescribedTask) {
                        VcsCommand vcsCMD = ((VcsDescribedTask) cmdTask).getVcsCommand();
                        if (VcsCommandIO.getBooleanPropertyAssumeDefault(vcsCMD,
                                                                         VcsCommand.PROPERTY_IGNORE_FAIL)) {
                            message = g("MSG_Command_name_finished", name);
                        } else {
                            message = g("MSG_Command_name_failed", name);
                        }
                    } else {
                        message = g("MSG_Command_name_failed", name);
                    }
                    break;
                case CommandTask.STATUS_INTERRUPTED:
                    message = g("MSG_Command_name_interrupted", name);
                    break;
            }
            StatusDisplayer.getDefault().setStatusText(message);
        }
    }

    private synchronized void executorStarter(final CommandTaskInfo cw) {
        tasksRunning.add(cw);
        threadsPool.post(new Runnable() {
        //new Thread(new Runnable() {
            public void run() {
                VcsCommandExecutor vce;
                CommandTask task = cw.getTask();
                if (isListCommandTask(task)) {
                    synchronized (CommandProcessor.this) {
                        numRunningListCommands++;
                    }
                }
                if (task.getPriority() == 0) {
                    synchronized (CommandProcessor.this) {
                        numRunningNormalPriority++;
                    }
                }
                commandStarted(cw);
                cw.setRunningThread(Thread.currentThread());
                threadTaskInfo.set(cw);
                //System.out.println("RUN: "+cw.getTask().getName()+", ID = "+cw.getCommandID()+", thread = "+Thread.currentThread()+", hash = "+Thread.currentThread().hashCode());
                Error err = null;
                try {
                    cw.run();
                } catch (RuntimeException rexc) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, rexc);
                } catch (ThreadDeath tderr) {
                    err = tderr;
                } catch (Throwable t) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, t);
                } finally {
                    threadTaskInfo.set(null);
                    cw.setRunningThread(null);
                    commandDone(cw);
                    //System.out.println("FINISHED: "+cw.getTask().getName()+", ID = "+cw.getCommandID()+", thread = "+Thread.currentThread()+", hash = "+Thread.currentThread().hashCode());
                }
                if (err != null) throw err;
            }
        //}).start();
        });
    }
    
    private synchronized void executorStarterLoop() {
        do {
            try {
                CommandTaskInfo cw;
                do {
                    cw = null;
                    for (int i = 0; i < taskWaitQueue.size(); i++) {
                        CommandTaskInfo cwTest = (CommandTaskInfo) taskWaitQueue.get(i);
                        if (canRun(cwTest)) { // It can alter taskWaitQueue, if the task calls process()
                            //System.out.println("CommandProcessor: CAN RUN: "+cwTest.getTask().getName()+" ("+cwTest.getCommandID()+")");
                            cw = cwTest;
                            break;
                        } else {
                            //System.out.println("CommandProcessor: CAN NOT RUN: "+cwTest.getTask().getName()+" ("+cwTest.getCommandID()+")");
                        }
                    }
                    if (cw != null) {
                        taskWaitQueue.remove(cw);
                        executorStarter(cw);
                    }
                } while (cw != null);
                Command cmd;
                do {
                    cmd = null;
                    for (Iterator it = commandsToPreprocess.iterator(); it.hasNext(); ) {
                        Command cmdTest = (Command) it.next();
                        if (canPreprocess(cmdTest)) {
                            cmd = cmdTest;
                            //System.out.println("CommandProcessor: CAN Preprocess: "+cmd.getName());
                            break;
                        }
                    }
                    if (cmd != null) {
                        commandsToPreprocess.remove(cmd);
                        doPreprocess(cmd);
                    }
                } while (cmd != null);
                try {
                    wait();
                } catch (InterruptedException intrexc) {
                    // silently ignored
                }
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable th) {
                ErrorManager.getDefault().notify(th);
            }
        } while(execStarterLoopRunning);
    }
    
    private boolean canPreprocess(Command cmd) {
        return true;
    }
    
    private void runExecutorStarterLoop() {
        Thread starterLoopThread = new Thread(new Runnable() {
            public void run() {
                executorStarterLoop();
            }
        }, "VCS Command Tasks Starter Loop");
        starterLoopThread.setDaemon(true);
        starterLoopThread.start();
        execStarterLoopStarted = true;
    }

    /**
     * Whether some command is still running.
     * @return true when at least one command is running, false otherwise.
     */
    public synchronized boolean isSomeRunning() {
        return (tasksRunning.size() > 0);
    }
    
    /**
     * Tells whether a task is waiting. It can either wait till preprocessing
     * finishes or till other commands which can not run in parallel with it finish.
     * @param task The task
     */
    public synchronized boolean isWaiting(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return false;
        return /*commandsToRun.contains(cw) ||*/ taskWaitQueue.contains(cw);
    }
    
    /**
     * Tells whether the executor is still running.
     * @param task The task
     */
    public synchronized boolean isRunning(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        return (cw != null && tasksRunning.contains(cw));
    }
    
    /**
     * Get display names of running commands.
     */
    public synchronized String[] getRunningCommandsLabels() {
        LinkedList names = new LinkedList();
        for(Iterator it = tasksRunning.iterator(); it.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            String label = cw.getTask().getDisplayName();
            if (label == null) label = cw.getTask().getName();
            names.add(label);
        }
        return (String[]) names.toArray(new String[0]);
    }
    
    synchronized CommandTask[] getRunningCommandTasks() {
        LinkedList tasks = new LinkedList();
        for(Iterator it = tasksRunning.iterator(); it.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            tasks.add(cw.getTask());
        }
        return (CommandTask[]) tasks.toArray(new CommandTask[tasks.size()]);
    }
    
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
    
    private boolean isListCommandTask(CommandTask task) {
        return task.getName().startsWith("LIST");
    }
    
    /**
     * Returns true iff all exceptionally running commands are
     * predecessors of the given command.
     */
    private boolean areExcRunningPredecessorsOf(CommandTaskInfo cw) {
        HashSet exceptionallyRunning = new HashSet(tasksExceptionallyRunning);
        boolean is;
        do {
            is = false;
            CommandTaskInfo submittingInfo = cw.getSubmittingInfo();
            for (Iterator it = exceptionallyRunning.iterator(); it.hasNext(); ) {
                CommandTaskInfo testCw = (CommandTaskInfo) it.next();
                if (submittingInfo.equals(testCw)) {
                    cw = testCw;
                    exceptionallyRunning.remove(testCw);
                    is = true;
                    break;
                }
            }
        } while (is);
        return exceptionallyRunning.size() == 0;
    }
    
    /**
     * Say whether the command executor can be run now or not. It should be called
     * with a monitor lock on this object.
     * Check its concurrent property and other running or waiting commands.
     * @return true if the command can be run in the current monitor lock, false otherwise.
     */
    private synchronized boolean canRun(CommandTaskInfo cw) {
        // at first check for the maximum number of running commands
        CommandTask task = cw.getTask();
        //System.out.println("canRun("+task.getName()+")");
        if (tasksRunning.size() >= MAX_NUM_RUNNING_COMMANDS ||
            task.getPriority() == 0 && numRunningNormalPriority >= MAX_NORMAL_PRIORITY ||
            isListCommandTask(task) && numRunningListCommands >= MAX_NUM_RUNNING_LISTS) {
            
            //System.out.println("canRun("+task.getName()+") - limit reached.");
            CommandTaskInfo submitter = cw.getSubmittingInfo();
            //System.out.println("  submitter = "+submitter);
            //System.out.println("  runningThreads = "+getRunningThreadsFromCommands(tasksRunning));
            //Collection threads = getRunningThreadsFromCommands(tasksRunning);
            //System.out.println("  runningThreads = "+threads.size());
            //for (Iterator it = threads.iterator(); it.hasNext(); ) {
            //    Object th = it.next();
            //    System.out.println("   thread: "+th+", hash = "+th.hashCode());
            //}
            //System.out.println("   running contains submitter = "+threads.contains(submitter));
            if (tasksRunning.contains(submitter)) {
                //System.out.println("  tasksExceptionallyRunning = "+tasksExceptionallyRunning);
                /*
                { HashSet exceptionallyRunning = new HashSet(tasksExceptionallyRunning);
                  for (Iterator it = exceptionallyRunning.iterator(); it.hasNext(); ) {
                      CommandTaskInfo testCw = (CommandTaskInfo) it.next();
                      System.out.println("    ex. running "+((testCw.getTask() != null) ? testCw.getTask().getName() : null)+
                                         ", ID = "+testCw.getCommandID()+", thread = "+testCw.getRunningThread()+", hash = "+
                                         ((testCw.getRunningThread() != null) ? testCw.getRunningThread().hashCode() : 0));
                  }
                }
                 */
                if ((tasksExceptionallyRunning.size() == 0 || areExcRunningPredecessorsOf(cw))
                    && cw.canRun()) {
                    
                    tasksExceptionallyRunning.add(cw);
                    //System.out.println("      can run EXCEPTIONALLY");
                    return true;
                } else {
                    //System.out.println("      can NOT EXCEPTIONALLY");
                    return false;
                }
            }
            return false;
        }
        boolean canRun = cw.canRun();
        //System.out.println("canRun("+task.getName()+") = "+canRun);
        return canRun;
    }
    
    private synchronized void addExecutorsOfCommand(ArrayList executors, Command cmd) {
        String name = cmd.getName();
        /*
        Iterator it = commandsToRun.iterator();
        while (it.hasNext()) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            VcsCommandExecutor executor = cw.getExecutor();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
         */
        Iterator it = taskWaitQueue.iterator();
        while (it.hasNext()) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            CommandTask task = cw.getTask();
            if (name.equals(task.getName())) {
                executors.add(task);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commandsToRun.iterator());
        for (Iterator runIt = tasksRunning.iterator(); runIt.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) runIt.next();
            CommandTask task = cw.getTask();
            if (name.equals(task.getName())) {
                executors.add(task);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commands.iterator());
    }
    
    /**
     * Wait to finish the execution of command on a set of files.
     * This methods blocks the current thread untill no task of the command is running on
     * any of provided files.
     * @param cmd the command we wait for to finish
     * @param files the set of files of type FileObject
     */
    public void waitToFinish(Command cmd, Set files) throws InterruptedException {
        boolean haveToWait = false;
        do {
            ArrayList executors = new ArrayList();
            addExecutorsOfCommand(executors, cmd);
            for (Iterator itExecutors = executors.iterator(); itExecutors.hasNext(); ) {
                CommandTask task = (CommandTask) itExecutors.next();
                if (files == null) {
                    haveToWait = true;
                } else {
                    FileObject[] execFiles = task.getFiles();
                    for (int i = 0; i < execFiles.length; i++) {
                        //String file = (String) itFiles.next();
                        if (files.contains(execFiles[i])) {
                            haveToWait = true;
                            break;
                        }
                    }
                }
            }
            if (haveToWait) {
                synchronized (this) {
                    wait();
                }
            }
        } while (haveToWait);
    }
    
    /**
     * Wait to finish the task.
     * This methods blocks the current thread untill the task finishes.
     * @param task the command task
     */
    public void waitToFinish(CommandTask task) throws InterruptedException {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return ;
        //Thread t;
        synchronized (this) {
            while (taskWaitQueue.contains(cw) ||
                   tasksRunning.contains(cw)) {
                //try {
                wait();
                //} catch (InterruptedException iexc) {}
            }
            //t = cw.getRunningThread();
        }
        /*
        if (t != null) {
            if (t.isAlive()) {
                //try {
                t.join();
                //} catch (InterruptedException exc) {
                    // Ignore
                //}
            }
        }
         */
    }
    
    /**
     * Wait to finish the task of a specific ID.
     * This methods blocks the current thread untill the task finishes.
     * @param taskID the command task ID.
     */
    public void waitToFinish(long taskID) throws InterruptedException {
        CommandTask task = null;
        synchronized (this) {
            for (Iterator it = taskInfos.values().iterator(); it.hasNext(); ) {
                CommandTaskInfo cw = (CommandTaskInfo) it.next();
                if (taskID == cw.getCommandID()) {
                    task = cw.getTask();
                    break;
                }
            }
        }
        if (task != null) {
            waitToFinish(task);
        }
    }
    
    /**
     * Kill all running executors. It tries to interrupt them, it is up to
     * executor implementations if they will terminate or not.
     */
    public synchronized void killAll() {
        for(Iterator it = tasksRunning.iterator(); it.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            Thread t = cw.getRunningThread();
            if (t != null) t.interrupt();
            //if (ec.isAlive()) ec.interrupt();
            //commandsFinished.add(ec);
            //commands.remove(i);
            //i--;
        }
    }
    
    /**
     * Kill the executor if it is running. It tries to interrupt it, it is up to
     * executor implementation if it will terminate or not.
     */
    public synchronized void kill(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw != null) {
            Thread t = cw.getRunningThread();
            if (t != null) t.interrupt();
            else {
                taskWaitQueue.remove(cw);
                cw.cancel();
                cw.setInterrupted(true);
                commandDone(cw);
            }
        }
    }
    
    /**
     * Add a command listener.
     */
    public void addCommandProcessListener(CommandProcessListener listener) {
        Object provider = listener.getProvider();
        synchronized (commandListenersByProviders) {
            if (provider == null) {
                commandListenersForAllProviders.add(listener);
            } else {
                List commandListeners = getCommandListenersForProvider(provider);
                if (commandListeners == null) {
                    commandListeners = new ArrayList();
                    commandListenersByProviders.put(new WeakReference(provider), commandListeners);
                }
                if (!commandListeners.contains(listener)) {
                    commandListeners.add(listener);
                }
            }
        }
    }
    
    /**
     * Remove a command listener.
     */
    public void removeCommandProcessListener(final CommandProcessListener listener) {
        Object provider = listener.getProvider();
        synchronized (commandListenersByProviders) {
            if (provider == null) {
                commandListenersForAllProviders.remove(listener);
            } else {
                for (Iterator it = commandListenersByProviders.keySet().iterator(); it.hasNext(); ) {
                    Reference ref = (Reference) it.next();
                    Object providerL = ref.get();
                    if (providerL == provider) {
                        List commandListeners = (List) commandListenersByProviders.get(ref);
                        if (commandListeners != null) {
                            commandListeners.remove(listener);
                            if (commandListeners.size() == 0) {
                                commandListenersByProviders.remove(ref);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    /** The start time of the command or zero, when the command was not started yet
     * or can not be found.
     */
    public long getStartTime(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return 0;
        return cw.getStartTime();
    }
    
    /** The finish time of the command or zero, when the command did not finish yet
     * or can not be found.
     */
    public long getFinishTime(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return 0;
        return cw.getFinishTime();
    }
    
    /** The execution time of the command or zero, when the command did not finish yet
     * or can not be found.
     */
    public long getExecutionTime(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return 0;
        return cw.getExecutionTime();
    }
    
    /**
     * Get the localized string representation of the command exit status.
     * @param exit the exit status, that will be converted to the string.
     */
    public static String getExitStatusString(int exit) {
        String status;
        if (VcsCommandExecutor.SUCCEEDED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.finished");
        } else if (VcsCommandExecutor.FAILED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.failed");
        } else if (VcsCommandExecutor.INTERRUPTED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.interrupted");
        } else {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.unknown");
        }
        return status;
    }

    
    private static String g(String s) {
        return org.openide.util.NbBundle.getMessage(CommandProcessor.class, s);
    }
    
    private static String  g(String s, Object obj) {
        return org.openide.util.NbBundle.getMessage(CommandProcessor.class, s, obj);
    }

    //private static String  g(String s, Object obj, Object obj2) {
    //    return org.openide.util.NbBundle.getMessage(CommandProcessor.class, s, obj, obj2);
    //}
    
    private static class InitialComponentFocusTraversalPolicy extends java.awt.FocusTraversalPolicy {
        
        private java.awt.FocusTraversalPolicy origPolicy;
        private java.awt.Component initialComponent;
        
        public InitialComponentFocusTraversalPolicy(java.awt.FocusTraversalPolicy origPolicy, java.awt.Component initialComponent) {
            if (origPolicy == null) {
                origPolicy = new java.awt.ContainerOrderFocusTraversalPolicy();
            }
            this.origPolicy = origPolicy;
            this.initialComponent = initialComponent;
        }
        
        public Component getComponentAfter(java.awt.Container focusCycleRoot, Component aComponent) {
            return origPolicy.getComponentAfter(focusCycleRoot, aComponent);
        }
        
        public Component getComponentBefore(java.awt.Container focusCycleRoot, Component aComponent) {
            return origPolicy.getComponentBefore(focusCycleRoot, aComponent);
        }
        
        public Component getDefaultComponent(java.awt.Container focusCycleRoot) {
            return origPolicy.getDefaultComponent(focusCycleRoot);
        }
        
        public Component getFirstComponent(java.awt.Container focusCycleRoot) {
            return origPolicy.getFirstComponent(focusCycleRoot);
        }
        
        public Component getLastComponent(java.awt.Container focusCycleRoot) {
            return origPolicy.getLastComponent(focusCycleRoot);
        }
        
        public Component getInitialComponent(java.awt.Window window) {
            return initialComponent;
        }
        
    }

}

