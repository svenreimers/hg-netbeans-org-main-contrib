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

package org.netbeans.modules.vcscore.runtime;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

/**
 * An abstract class that encapsulates the module dependant features that are needed
 * when creating a runtime tab node for a command.
 * If your module is using the CommandsPool and VcsCommandExecutor classes form vcscore, 
 * you don't have to do anything and your commands will be automatically added.
 * You need an subclasses' instance when creating the RuntimeCommandNode.
 * See VcsRuntimeCommand for default implementation.
 * @author  Milos Kleint
 */
public abstract class RuntimeCommand implements Node.Cookie {

    /**
     * Constant that means a command was successfully exited.
     * It should be returned by the getExitStatus() method.
     * 
     */
    public static final int SUCCEEDED = VcsCommandExecutor.SUCCEEDED;
    /**
     * Constant that means a command failed.
     * It should be returned by the getExitStatus() method.
     * 
     */
    public static final int FAILED = VcsCommandExecutor.FAILED;

    /**
     * Constant that means a command was interrupted.
     * It should be returned by the getExitStatus() method.
     * 
     */
    
    public static final int INTERRUPTED = VcsCommandExecutor.INTERRUPTED;
    
    public static final int STATE_WAITING = RuntimeCommandNode.STATE_WAITING;

    public static final int STATE_RUNNING = RuntimeCommandNode.STATE_RUNNING;
    
    public static final int STATE_DONE = RuntimeCommandNode.STATE_DONE;
    
    public static final int STATE_CANCELLED = RuntimeCommandNode.STATE_CANCELLED;
    
    public static final int STATE_KILLED_BUT_RUNNING = RuntimeCommandNode.STATE_KILLED_BUT_RUNNING;
    
    public static final String PROP_STATE = "state";
    public static final String PROP_DISPLAY_NAME = "displayName";
    
    private Reference nodeDelegate = new WeakReference(null);
    
    private PropertyChangeSupport changeSupport;
    
    /** Creates new RuntimeCommand */
    public RuntimeCommand() {
        changeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Subclasses should return a name of the command. 
     */
    public abstract String getName();
    
    /**
     * Subclasses should return a display name of the command. 
     * If not defined, the getName()'s result is used instead.
     */
    
    public abstract String getDisplayName();
    
    /**
     * When the command finishes this method should return the exit atatus of the vcs command.
     * See constants in this class.
     */
    public abstract int getExitStatus();
    
    /**
     * this method returns the set of actions that should be available for the runtime tab node of the command.
     *
     */
    public abstract SystemAction[] getActions();
    
    /**
     * If you returned the CommandOutputViewAction among the other actions in getActions() method,
     *you should implement this method and display the output in the CommandOutputPanel.
     *Otherwise just leave it blank.
     */
    public abstract void openCommandOutputDisplay();
    
    /**
     * Create a property sheet for the node.
     */
    public abstract Sheet createSheet();
    
    /**
     * provide a unique id for the node.
     */
    public abstract String getId();
    
    /**
     * If you returned the KillRunningCommandAction among the other actions in getActions() method,
     * this method will be called and should attempt  to stop the running command.
     */
    public abstract void killCommand();
    
    public abstract int getState();
    
    public abstract void setState(int state);
    
    /** Called when this command is removed from the Runtime tab. Can be used by subclasses
     *  to do some cleanup when this command was restroyed. */
    public void notifyRemoved() {
    }
    
    public final synchronized Node getNodeDelegate() {
        Node node = (Node) nodeDelegate.get();
        if (node == null) {
            node = createNodeDelegate();
            nodeDelegate = new WeakReference(node);
        }
        return node;
    }
    
    protected Node createNodeDelegate() {
        return new RuntimeCommandNode(this);
    }

    /**
     * Get the node delegate if exists.
     * @return The node delegate or <code>null</code> if the node delegate is not created.
     */
    protected final Node getExistingNodeDelegate() {
        return (Node) nodeDelegate.get();
    }
    
    public final void addPropertyChangeListener(PropertyChangeListener propertyListener) {
        changeSupport.addPropertyChangeListener(propertyListener);
    }
    
    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener propertyListener) {
        changeSupport.addPropertyChangeListener(propertyName, propertyListener);
    }
    
    public final void removePropertyChangeListener(PropertyChangeListener propertyListener) {
        changeSupport.removePropertyChangeListener(propertyListener);
    }
    
    public final void removePropertyChangeListener(String propertyName, PropertyChangeListener propertyListener) {
        changeSupport.removePropertyChangeListener(propertyName, propertyListener);
    }
    
    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
