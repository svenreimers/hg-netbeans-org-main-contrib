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
 
package org.netbeans.modules.clazz;

import java.io.IOException;
import java.util.*;

import org.openide.util.Task;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.ErrorManager;
import org.openide.loaders.DataFolder;
import org.openide.loaders.FolderInstance;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.ElementNodeFactory;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**
 * A pool for registering FilterFactories for the object browser and the
 * explorer.
 *
 * @author  Svatopluk Dedic
 */
class NodeFactoryPool extends FolderInstance {
    static final FilterFactory[] EMPTY = new FilterFactory[0];

    /**
     * Base factory, which serves as a default and the tail of the chain.
     */
    ElementNodeFactory  base;
    
    /**
     * Factories, which were explicitly added by calls to {@link #addFactory}
     */
    LinkedList          explicit;
    
    /**
     * State of the underlying folder. Contains list of factories registered
     * through the SFS.
     */
    FilterFactory[]     factories = EMPTY;
    
    /**
     * Computed head of the factory chain.
     */
    ElementNodeFactory  head;
    
    /**
     * True, if the folder scan was triggered at least once.
     */
    boolean             initialized;
    
    NodeFactoryPool(DataFolder storage, ElementNodeFactory base) {
        super(storage);
        this.base = base;
        head = base;
    }
    
    final Object sync() {
        return base;
    }

    /**
     * Returns the head of the current factory list. Except for the initialization,
     * the method does not block.
     */
    ElementNodeFactory getHead() {
        // force folder scan the first time the Pool is queried
        if (!initialized) {
            recreate();
            waitFinished();
            initialized = true;
        }
        return head;
    }

    /**
     * Creates an array of factories from the underlying folder. The "product" of
     * the method is the head of the factory list.
     */
    protected Object createInstance(InstanceCookie[] cookies) 
    throws java.io.IOException, ClassNotFoundException {
        Collection l = new ArrayList(cookies.length);
        for (int i = 0; i < cookies.length; i++) {
            try {
                Object o = cookies[i].instanceCreate();
                if (!(o instanceof FilterFactory))
                    continue;
                l.add(o);
            } catch (IOException ex) {
                logError(ex);
            } catch (ClassNotFoundException ex) {
                logError(ex);
            }
        }
        synchronized (sync()) {
            ElementNodeFactory f = relinkFactories(l);
            this.factories = (FilterFactory[])l.toArray(new FilterFactory[l.size()]);
            return f;
        }
    }

    /**
     * Reattaches factories in the logicall factory chain to each other.
     */
    private ElementNodeFactory relinkFactories(Collection first) {
        ElementNodeFactory previous = base;
        FilterFactory f = null;
        Iterator it;
        Collection next = explicit;
        
        if (first == null)
            first = Arrays.asList(factories);
        
        for (it = first.iterator(); it.hasNext(); ) {
            f = (FilterFactory)it.next();
            f.attachTo(previous);
            previous = f;
        }
        if (next != null) {
            for (it = next.iterator(); it.hasNext(); ) {
                f = (FilterFactory)it.next();
                f.attachTo(previous);
                previous = f;
            }
        }
        return f != null ? f : base;
    }

    /**
     * Adds an explicit factory and the head of the chain. Relinks the entire
     * chain as well.
     */
    void addFactory(FilterFactory f) {
        synchronized (sync()) {
            if (explicit == null) {
                explicit = new LinkedList();
            }
            explicit.add(f);
            head = relinkFactories(null);
        }
    }

    /**
     * Removes one factory from the explicit list. Relinks the chain, if the
     * factory was, actually, on the list.
     */
    void removeFactory(FilterFactory f) {
        synchronized (sync()) {
            if (!explicit.remove(f))
                return;
            relinkFactories(null);
        }
    }
    
    void logError(Exception ex) {
        ((ErrorManager)Lookup.getDefault().lookup(ErrorManager.class)).notify(ex);
    }
}
