/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.registry;

import java.beans.*;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.Registry;

import org.openide.util.HelpCtx;
import org.openide.nodes.*;
import org.openide.options.*;
import org.openide.util.*;

import org.netbeans.modules.rmi.*;
import org.netbeans.modules.rmi.registry.settings.RMIRegistrySettings;

/** Class representing set of all used registries.
*
* @author Martin Ryzl
*/
public class RMIRegistryChildren extends Children.Keys implements PropertyChangeListener {

    private static final boolean DEBUG = false;

    /** Refresh task. */
    private transient RequestProcessor.Task task = null;
    private transient RefreshTask taskRunnable = null;
    private volatile int timeout = 0;
    
    /** Schedule refresh task.
     * @param millis schedule refresh task in millis, disable completely if millis == 0;
     */
    protected synchronized void scheduleRefreshTask(int millis) {
        if (millis == 0) {
            task = null;
            taskRunnable = null;
            return;
        } else {
            if (task == null) {
                taskRunnable = new RefreshTask();
                task = RequestProcessor.getDefault().create(taskRunnable);
                task.setPriority(Thread.MIN_PRIORITY);
            }
            task.schedule(millis);
            if (DEBUG) System.err.println("RCH: planned to " + timeout + " ms."); // NOI18B
        }
    }

    /** Called to notify that the children has been asked for
    * children after and that they should set its keys.
    */
    protected void addNotify() {
        setKeys(RMIRegistrySettings.getInstance().getRegistryItems());
        timeout = RMIRegistrySettings.getInstance().getRefreshTime();
        scheduleRefreshTask(1);
        RMIRegistrySettings.getInstance().addPropertyChangeListener(this);
    }
    
    /** Called to notify that the children has lost all of its references to its nodes
    * associated to keys and that the keys could be cleared without affecting any 
    * nodes (because nobody listens to that nodes).
    */
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
        task = null;
        taskRunnable = null;
        RMIRegistrySettings.getInstance().removePropertyChangeListener(this);
    }

    /** Refresh keys.
    */
    public void refreshIt() {
        // start refresh task immediately
        scheduleRefreshTask(1);
    }

    /** Refresh one key.
     * @param key - key to update.
     */
    public void refresh(final Object key) {
        refreshKey(key);
    }

    protected Node[] createNodes(Object key) {
        Node node;
        RegistryItem item = (RegistryItem) key;
        Collection services;
        if ((services = item.getServices()) != null) {
            node = new RegistryItemNode.ValidNode(item, new RegistryItemChildren(item));
        } else {
            node = new RegistryItemNode.InvalidNode(item);
        }
        return new Node[] {node};
    }

    void setCleanFlag(boolean flag) {
        if (taskRunnable != null) {
            taskRunnable.setCleanFlag(flag);
        }
    }
        
    /** Refresh all keys on background. */
    private class RefreshTask implements Runnable {

        /** Indicates hard refresh. */
        private boolean cleanFlag = false;

        public synchronized void setCleanFlag(boolean flag) {
            this.cleanFlag = flag;
        }

        private synchronized boolean getCleanFlag() {
            return this.cleanFlag;
        }

        private void resetLoader() {
            try {
                Class clazz = Class.forName("sun.rmi.server.LoaderHandler"); // NOI18N
                java.lang.reflect.Field f = clazz.getDeclaredField("loaderTable"); // NOI18N
                f.setAccessible(true);
                synchronized (clazz) {
                    HashMap map = (HashMap) f.get(null);
                    if (map != null) {
                        map.clear();
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        public void run() {
            final boolean cleanFlag = getCleanFlag();
            setCleanFlag(false);

            if (cleanFlag) {
                resetLoader();
            }

            if (DEBUG) System.err.println("RCH: Refresh Task invoked. "); // NOI18B
            RegistryItem[] items = RMIRegistrySettings.getInstance().getRegistryItems();

            for(int i = 0; i < items.length; i++) {
                if (cleanFlag) {
                    items[i].setServices(null);
                }
                items[i].updateServices();
            }
            setKeys(items);

            scheduleRefreshTask(timeout);
            if (DEBUG) System.err.println("RCH: RefreshTask finished."); // NOI18B
        }
    }

    /** PropertyChangeListener. It listens on RMIRegistrySettings for change of the refresh timeout.
    */
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (DEBUG) {
            System.err.println("RMIRegistryChildren: property change " + evt);
            System.err.println(evt.getPropertyName());
            System.err.println(evt.getOldValue());
            System.err.println(evt.getNewValue());
        }
        Object source = evt.getSource();

        if (source instanceof RMIRegistrySettings) {
            if (RMIRegistrySettings.PROP_INTERNAL_REGISTRY_PORT.equals(evt.getPropertyName())) {
                refreshIt();
            }

            if (RMIRegistrySettings.PROP_REGISTRY_ITEMS.equals(evt.getPropertyName())) {
                refreshIt();
            }

            if (RMIRegistrySettings.PROP_REFRESH_TIME.equals(evt.getPropertyName())) {
                timeout = RMIRegistrySettings.getInstance().getRefreshTime();
                scheduleRefreshTask(timeout);
            }

        }
    }

    /** Update status of an item.
    */
    public static void updateItem(final RegistryItem item) {
        // perform update in separate thread
        RequestProcessor.getDefault().postRequest(
            new Runnable() {
                public void run() {
                    item.updateServices();
                }
            },
            0,
            Thread.MIN_PRIORITY + 1
        );
    }


}












