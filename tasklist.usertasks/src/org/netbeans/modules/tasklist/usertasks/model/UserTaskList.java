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

package org.netbeans.modules.tasklist.usertasks.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.core.util.ActivityListener;
import org.netbeans.modules.tasklist.core.util.ObjectList;

import org.netbeans.modules.tasklist.usertasks.*;


/**
 * This class represents the tasklist itself
 * @todo The tasks that had a due time while the IDE was shut down will not
 *       store it's "alarm-sent-property" unless the tasklist is touched after
 *       the file is beeing parsed... I'll fix this as soon as possible...
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class UserTaskList implements Timeout, ObjectList.Owner {    
    private static UserTaskList tasklist = null;
    
    
    /**
     * Returns the default task list
     *
     * @return default task list
     */
    public static UserTaskList getDefault() throws IOException {
        if (tasklist != null) 
            return tasklist;
                
        File f = getDefaultFile();
        FileObject fo = FileUtil.toFileObject(f);
        if (fo != null) 
            return readDocument(fo);

        File dir = f.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException(
                NbBundle.getMessage(UserTaskList.class,
                    "CannotCreateDir", dir.getAbsolutePath())); // NOI18N
        }
        if (!f.createNewFile()) {
            throw new IOException(
                NbBundle.getMessage(UserTaskList.class,
                    "CannotCreateFile", f.getAbsolutePath())); // NOI18N
        }

        return readDocument(FileUtil.toFileObject(f));
    }
    
    private UserTaskObjectList tasks;

    /** Has the options set changed such that we need to save */
    protected boolean needSave = false;
    protected boolean dontSave = false;
    
    /** 
     * this value is used by ICalImport/ExportFormat to store additional
     * not editable parameters
     */
    public Object userObject;
    
    /** File being shown in this tasklist */
    private FileObject file = null;
    
    /** The current timeout */
    private long currentTimeout;
    
    /**
     * During loading of a tasklist I may encounter items that have expired
     * while the IDE was shut down. Since the load-function turns off the 
     * effect of markChanged, I need to store this information in another
     * variable, and save the tasklist when the load is finished..
     */
    private boolean expiredTask;
    
    /** Timer which keeps track of outstanding save requests - that way
     * deleting multiple items for example will not cause multiple saves. */
    private Timer runTimer = null;


    // User can work on one task at time (simplification) ~~~~~~~~~~~~~~~

    /**
     * Creates a new instance of TaskList
     */
    public UserTaskList() {
        tasks = new UserTaskObjectList(this);
        tasks.addListener(new ObjectList.Listener() {
            public void listChanged(ObjectList.Event ev) {
                markChanged();
            }
        });
        
        expiredTask = false;
        currentTimeout = Long.MAX_VALUE;
    }
    
    /** 
     * Location of the tasklist 
     */
    public FileObject getFile() {
        return file;
    }
    
    /**
     * Searches for categories through all tasks.
     *
     * @return all found categories
     */
    public String[] getCategories() {
        Iterator it = this.getSubtasks().iterator();
        Set cat = new java.util.HashSet();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            findCategories(ut, cat);
        }
        return (String[]) cat.toArray(new String[cat.size()]);
    }
    
    /**
     * Searches for categories
     *
     * @param task search for categories in this task and all of it's subtasks
     * recursively
     * @param cat container for found categories. String[]
     */
    private static void findCategories(UserTask task, Set cat) {
        if (task.getCategory().length() != 0)
            cat.add(task.getCategory());
        
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            findCategories((UserTask) it.next(), cat);
        }
    }
    
    /** Write items out to disk */
    public void save() {
        if (!needSave || dontSave) {
            return;
        }

        // Write out items to disk
        scheduleWrite();
    }
    
    /**
     * Returns the default .ics file.
     *
     * @return default ics file
     */
    private static File getDefaultFile() {
        String name = Settings.getDefault().getExpandedFilename();
        File fname = FileUtil.normalizeFile(new File(name));
        return fname;
    }
    
    /**
     * Reads an ics file
     *
     * @param is an .ics file
     */
    private static UserTaskList readDocument(InputStream is) throws IOException {
        ICalImportFormat io = new ICalImportFormat();

        UserTaskList ret = new UserTaskList();
        ret.dontSave = true;
        try {
            io.read(ret, is);
            ret.orderNextTimeout();
        } catch (IOException e) {
            // NOTE the exception text should be localized!
            DialogDisplayer.getDefault().notify(new Message(e.getMessage(),
               NotifyDescriptor.ERROR_MESSAGE));
        }

        ret.needSave = false;
        ret.dontSave = false;        

        if (ret.expiredTask) {
            // One (or more) tasks expired while the IDE was closed...
            // save the list as soon as possible...
            ret.expiredTask = true;
            ret.markChanged();
        }
        return ret;
    }
    
    /**
     * Reads an ics file
     *
     * @param fo an .ics file
     */
    public static UserTaskList readDocument(FileObject fo) throws IOException {
        if (fo.isValid()) {
            long m = System.currentTimeMillis();
            UserTaskList ret = readDocument(fo.getInputStream());
            UTUtils.LOGGER.fine("File " + fo + " read in " + 
                (System.currentTimeMillis() - m) + "ms"); // NOI18N
            ret.file = fo;
            return ret;
        } else {
            throw new IOException(
                NbBundle.getMessage(UserTaskList.class,
                    "FileNotValid", FileUtil.getFileDisplayName(fo))); // NOI18N
        }
    }

    // Look up a particular item by uid
    public UserTask findItem(Iterator tasks, String uid) {
        while (tasks.hasNext()) {
            UserTask task = (UserTask)tasks.next();
            if (task.getUID().equals(uid)) {
                return task;
            }
            if (!task.getSubtasks().isEmpty()) {
                UserTask f = findItem(task.getSubtasks().iterator(), uid);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    /** Schedule a document save */
    private void scheduleWrite() {
        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
	if (runTimer != null) {
	    runTimer.stop();
	    runTimer = null;
	}
	runTimer = new Timer(300, // 0.3 second delay
		     new ActionListener() {
			 public void actionPerformed(ActionEvent evt) {
                             runTimer = null;
                             
                             // Write out items to disk
                             try {
                                 writeDocument();
                             } catch (IOException ioe) {
                                 ioe.printStackTrace();
                                 DialogDisplayer.getDefault().notify(new Message(
                                    ioe, NotifyDescriptor.ERROR_MESSAGE));
                             }
                             needSave = false;
			 }
		     });
	runTimer.setRepeats(false);
	runTimer.setCoalesce(true);
	runTimer.start();
    }

    /** 
     * Write the list to iCal.
     */
    private void writeDocument() throws IOException {
        if (this.file == null)
            return;
        
        ICalExportFormat io = new ICalExportFormat();
        
        FileLock lock = this.file.lock();
        try {
            OutputStream fos = file.getOutputStream(lock);
            try {
                io.writeList(this, fos);
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        } finally {
            lock.releaseLock();
        }

        // Remove permissions for others on the file when on Unix
        // varieties
        if (new File("/bin/chmod").exists()) { // NOI18N
            try {
                Runtime.getRuntime().exec(
                     new String[] {"/bin/chmod", "go-rwx",  // NOI18N
                         FileUtil.toFile(this.file).getAbsolutePath()});
            } catch (Exception e) {
                // Silently accept
                ErrorManager.getDefault().notify(
                     ErrorManager.INFORMATIONAL, e);
            }
        }

        needSave = false;
    }
    
    /**
     * Removes completed tasks
     */
    public void purgeCompletedItems() {
        if (getSubtasks().size() == 0 ) {
            return;
        }
        dontSave = true;
        Iterator it = getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask task = (UserTask) it.next();
            recursivePurge(task);
        }
        dontSave = false;
        save();
    }
    
    /** 
     * Remove any completed items from the list.
     * Only works if started on the root node.
     * NOTE - if a task is marked done, it AND ALL ITS CHILDREN
     * are removed, even if the children haven't been marked done.
     */
    private void recursivePurge(UserTask node) {
        if (node.getSubtasks() != null) {
            List l = new ArrayList(node.getSubtasks());
            
            // I may have to repeat purges because once I delete an item from
            // the list, the iterator needs to be redone
            Iterator it = l.iterator();
            while (it.hasNext()) {
                UserTask task = (UserTask)it.next();
                if (task.isDone()) {
                    it.remove();
                } else if (!task.getSubtasks().isEmpty()) {
                    recursivePurge(task);
                }
            }
        }
    }

    /**
     * Order a timeout for the next due date
     */
    void orderNextTimeout() {
        long nextTimeout = Long.MAX_VALUE;
        long now = System.currentTimeMillis();
        
        UserTask ref = null;

        Iterator i = getSubtasks().iterator();
        while (i.hasNext()) {
            UserTask t = (UserTask)i.next();            
            long n = t.getDueTime();

            if (n != Long.MAX_VALUE && !t.isDueAlarmSent()) {
                if (n <= now) {
                    showExpiredTask(t);
                    continue;
                } else if (n < nextTimeout) {
                    nextTimeout = n;
                    ref = t;
                }
            }
        }

        if (nextTimeout != currentTimeout) {
            // cancel the previous ordered timeout, and add the new one
            if (currentTimeout != Long.MAX_VALUE) {
                TimeoutProvider.getInstance().cancel(this, null);
            }
            TimeoutProvider.getInstance().add(this, ref, nextTimeout);
            currentTimeout = nextTimeout;
        }
    }
    
    public String toString() {
        return "UserTaskList(" + file + ")"; // NOI18N
    }
    
    /**
     * Callback function for the TimeoutProvider to call when the timeout
     * expired. This function will block the TimeoutProviders thread, so
     * it should be used for a timeconsuming task (one should probably
     * reschedule oneself with the SwingUtilities.invokeLater() ???)
     * @param o the object provided as a user reference
     */
    public void timeoutExpired(Object o) {
        // Show the task...
        showExpiredTask((UserTask)o);

        // order the next timeout for this list
        orderNextTimeout();
    }

    /**
     * Present the user with a dialog that shows information of the task that
     * expired... 
     *
     * @todo Replace the UserTaskDuePanel with the EditTaskPanel????
     * @param task the task to show
     */
    private void showExpiredTask(UserTask task) {
        task.setDueAlarmSent(true);
        expiredTask = true;
        markChanged();
        
        final UserTask t = task;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserTaskDuePanel panel = new UserTaskDuePanel(t);
                
                String title = NbBundle.getMessage(NewTaskAction.class, "TaskDueLabel"); // NOI18N
                DialogDescriptor d = new DialogDescriptor(panel, title);                
                d.setModal(false);
                d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
                d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
                java.awt.Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
                dlg.pack();
                dlg.show();
            }
        });
    }

    protected void setNeedSave(boolean b) {
        needSave = b;
    }

    protected void setDontSave(boolean b) {
        dontSave = b;
    }

    /** 
     * Returns top-level tasks holded by this list. 
     *
     * @return list of top-level tasks
     */
    public final UserTaskObjectList getSubtasks() {
        return tasks;
    }

    /**
     * Notify the task list that some aspect of it has been changed, so
     * it should save itself soon. Eventually calls save 
     */
    public void markChanged() {
        needSave = true;
        save();
    }

    public ObjectList getObjectList() {
        return tasks;
    }
    
    /**
     * Should be called after closing a view. Removes all annotations.
     */
    public void destroy() {
        Iterator it = getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            ut.destroy();
        }
    }
    
    /** For debugging purposes, only. Writes directly to serr. 
    public void print() {
        System.err.println("\nTask List:\n-------------");
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task next = (Task) it.next();
            recursivePrint(next, 0);
        }

        System.err.println("\n\n");
    }

    private void recursivePrint(Task node, int depth) {
        if (depth > 20) { // probably invalid list
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.err.print("   ");
        }
        System.err.println(node);
        if (node.getSubtasks() != null) {
            List l = node.getSubtasks();
            ListIterator it = l.listIterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                recursivePrint(task, depth + 1);
            }
        }
    }*/

    // TaskListener impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
