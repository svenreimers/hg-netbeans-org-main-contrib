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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.core.ObservableList;
import org.netbeans.modules.tasklist.core.TaskListener;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskList;

import java.util.*;

/**
 * Delegate actions to original task list while
 * narrowing them to source tasks.
 *
 * @author Petr Kuzel
 */
final class FilteredTasksList implements ObservableList {

    /*
     * There is one realy strict implementation assumption
     * that source tasks never nest. It simplifies
     * implementation a lot because it degrades to list.
     *
     * It also assumes that visible view must have attached
     * TaskListener
     */

    private final TaskList peer;
    private List listeners = new ArrayList(1);
    private List tasks = new LinkedList();
    private EventHandler handler;
    private boolean silent = false;

    public FilteredTasksList(TaskList peer) {
        this.peer = peer;
    }

    public List getTasks() {
        return tasks;
    }

    public synchronized void addTaskListener(TaskListener l) {
        // we do not add directly to peer
        // because we filter fired events
        assert l != null;
        assert listeners.contains(l) == false; // missing removeListener ?
        ArrayList clisteners = new ArrayList(listeners);
        clisteners.add(l);
        if (clisteners.size() == 1) {
            handler = new EventHandler();
            peer.addTaskListener(handler);
        }
        listeners = clisteners;
    }

    public synchronized void removeTaskListener(TaskListener l) {
        ArrayList clisteners = new ArrayList(listeners);
        clisteners.remove(l);
        if (clisteners.size() == 0) {
            peer.removeTaskListener(handler);  // nobody is interested in changes
            handler = null;
        }
        listeners = clisteners;
    }

    /**
     * Notify that it's not needed anymore.
     */
    synchronized void byebye() {
        if (listeners.size() > 0) {
//            System.err.println("Leaked listeners: " + listeners);
        }
        listeners.clear();
        peer.removeTaskListener(handler);  // nobody is interested in changes
        handler = null;
    }

    private void fireStructureChanged(Task task) {
//        if (silent) return;  // the event comes from root.updatedStructure

        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            TaskListener listener = (TaskListener) it.next();
            listener.structureChanged(task);
        }
    }

    /** Client must fire structure changed event */
    private void refreshSnapshot() {
        tasks.clear();
        loadSourceTasks(peer.getTasks());
    }

    private void loadSourceTasks(List tasks) {
        if (tasks.size() == 0) return;
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.getSeed() instanceof SourceTaskProvider) {
                tasks.add(task);
            } else {
                // There are those nesting category tasks
                // if grouping treshold is matched.
                // Eliminate them to sustain list assumption.
                if (task.hasSubtasks()) {
                    loadSourceTasks(task.getSubtasks());  // recursion
                }
            }
        }
    }

    /**
     * Forward filtered events
     */
    private class EventHandler implements TaskListener {
        public void selectedTask(Task t) {
            if (getTasks().contains(t)) {
                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.selectedTask(t);
                }
            }
        }

        public void warpedTask(Task t) {
            assert false : "Not implemented";
        }

        public void addedTask(Task t) {
            if (t.getSeed() instanceof SourceTaskProvider) {
                try {
                    silent = true;
                    tasks.add(t);
                } finally {
                    silent = false;
                }

                // fire event

                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.addedTask(t);
                }
            } else if (t.hasSubtasks()) {
                // category nodes
                Iterator it = t.subtasksIterator();
                while (it.hasNext()) {
                    Task task = (Task) it.next();
                    addedTask(task);  // recursion
                }
            }
        }

        public void removedTask(Task pt, Task t) {
            if (t.getSeed() instanceof SourceTaskProvider) {
                boolean removed = false;
                try {
                    silent = true;
                    removed = tasks.remove(t);
                } finally {
                    silent = false;
                }

                // fire event

                if (removed) {
                    Iterator it = listeners.iterator();
                    while (it.hasNext()) {
                        TaskListener listener = (TaskListener) it.next();
                        listener.removedTask(null, t);
                    }
                }
            } else if (t.hasSubtasks()) {
                // category nodes
                Iterator it = t.subtasksIterator();
                while (it.hasNext()) {
                    Task task = (Task) it.next();
                    removedTask(null, task);  // recursion
                }
            }
        }

        public void structureChanged(Task t) {
            // need to build it again
            try {
                silent = true;
                refreshSnapshot();
            } finally {
                silent = false;
            }
            fireStructureChanged(t);
        }
    }

}
