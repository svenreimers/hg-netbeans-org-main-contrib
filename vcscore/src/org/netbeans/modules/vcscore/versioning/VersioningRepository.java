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

package org.netbeans.modules.vcscore.versioning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.openide.util.Lookup;

/**
 *
 * @author  Martin Entlicher
 */
public final class VersioningRepository extends Object implements java.io.Serializable {
    
    private static VersioningRepository repository;
    
    private ArrayList verSystems;
    private ArrayList verSystemsCopy;
    
    private Hashtable names;

    private transient EventListenerList listenerList = new EventListenerList();

    /** Creates new VersioningRepository */
    private VersioningRepository() {
        verSystems = new ArrayList();
        names = new Hashtable();
    }
    
    public static VersioningRepository getRepository() {
        if (repository == null) {
            initRepository();
        }
        return repository;
    }
    
    private static synchronized void initRepository() {
        if (repository != null) return;
        Lookup l = Lookup.getDefault();
        repository = (VersioningRepository) l.lookup(org.netbeans.modules.vcscore.versioning.VersioningRepository.class);
        if (repository == null) {
            repository = new VersioningRepository();
        }
    }
    
    public final void addVersioningSystem(VersioningSystem vfs) {
        boolean fireIt;
        synchronized (this) {
            String systemName = vfs.getSystemName();
            if (!names.containsKey(systemName)) {
                verSystems.add(vfs);
                verSystemsCopy = new ArrayList(verSystems);
                names.put(systemName, vfs);
                fireIt = true;
            } else fireIt = false;
        }
        if (fireIt) {
            fireVerSystem(vfs, true);
        }
    }
    
    public final void removeVersioningSystem(VersioningSystem vfs) {
        boolean fireIt;
        synchronized (this) {
            String systemName = vfs.getSystemName();
            if (names.containsKey(systemName)) {
                verSystems.remove(vfs);
                verSystemsCopy = new ArrayList(verSystems);
                names.remove(systemName);
                fireIt = true;
            } else fireIt = false;
        }
        if (fireIt) {
            fireVerSystem(vfs, false);
        }
    }
    
    public final List getVersioningSystems() {
        ArrayList vfsl = new ArrayList(verSystems);
        return vfsl;
    }
    
    public final synchronized VersioningSystem getSystem(String systemName) {
        return (VersioningSystem) names.get(systemName);
    }
    
    public void addRepositoryListener(VersioningRepositoryListener listener) {
        synchronized (listenerList) {
            listenerList.add(VersioningRepositoryListener.class, listener);
        }
    }
    
    public void removeRepositoryListener(VersioningRepositoryListener listener) {
        synchronized (listenerList) {
            listenerList.remove(VersioningRepositoryListener.class, listener);
        }
    }
    
    private void fireVerSystem(VersioningSystem vfs, boolean added) {
        VersioningRepositoryListener[] listeners;
        synchronized (listenerList) {
            listeners = (VersioningRepositoryListener[]) listenerList.getListeners(VersioningRepositoryListener.class);
        }
        VersioningRepositoryEvent ev = new VersioningRepositoryEvent(this, vfs, added);
        if (added) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].versioningSystemAdded(ev);
            }
        } else {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].versioningSystemRemoved(ev);
            }
        }
    }

}
