/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Accelerators module. 
 * The Initial Developer of the Original Code is Andrei Badea. 
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 * 
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import org.netbeans.api.queries.VisibilityQuery;
import org.openide.filesystems.FileObject;
import org.openide.util.Enumerations;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
/**
 * This class is not thread-safe. All methods should be called from the 
 * same thread.
 *
 * @author Andrei Badea
 */
class SearchWorker implements Runnable {
    
    private RequestProcessor rp;
    
    private FileObject[] roots;
    private FileSearchResult result;
    private SearchFilter filter;
    
    private RequestProcessor.Task searchTask;
    
    private volatile boolean hasFilterTask;
    private Task filterTask;
    
    public SearchWorker(RequestProcessor rp, FileObject[] roots, FileSearchResult result) {
        assert rp != null;
        assert roots != null;
        assert result != null;
        this.rp = rp;
        this.roots = roots;
        this.result = result;
    }
    
    public void setFilter(final SearchFilter filter) {
        assert filter != null;
        synchronized (this) {
            if (searchTask == null) {
                this.filter = filter;
            } else {
                filterTask = new Task(new Runnable() {
                    public void run() {
                        SearchWorker.this.filter = filter;
                    }
                });
                hasFilterTask = true;
            }
        }
        if (filterTask != null) {
            filterTask.waitFinished();
            filterTask = null;
            assert !hasFilterTask;
        }
    }
    
    public SearchFilter getFilter() {
        return filter;
    }
    
    public void start() {
        cancel();
        synchronized (this) {
            searchTask = rp.post(this);
        }
            
    }
    
    public void cancel() {
        RequestProcessor.Task t = null;
        synchronized (this) {
            t = searchTask;
        }
        if (t != null) {
            t.cancel();
            t.waitFinished();
        }
        synchronized (this) {
            searchTask = null;
        }
    }
    
    void waitFinished() {
        RequestProcessor.Task t = null;
        synchronized (this) {
            t = searchTask;
        }
        if (t != null) {
            t.waitFinished();
        }
    }
    
    public void run() {
        for (int i = 0; i < roots.length; i++) {
            Enumeration e = getFileObjectChildren(roots[i]);
            
            while (e.hasMoreElements()) {
                FileObject fo = (FileObject)e.nextElement();

                checkFilterTask();

                // cancel if requested
                if (Thread.interrupted()) {
                    return;
                }

                if (filter.accept(fo)) {
                    result.add(fo);
                }
            }
        }
        
        synchronized (this) {
            // in case the filterTask field was set just after the last 
            // checkFilterTask() call in the while statement
            checkFilterTask();
            searchTask = null;
        }
    }
    
    private void checkFilterTask() {
        if (hasFilterTask) {
            // acknowledge the request
            // this must be done before running the task
            // if done afterwards, it could acknowledge the next request
            hasFilterTask = false;
            
            // run the task
            Task t = null;
            // need to synchronize to ensure visibility
            synchronized (this) { 
                t = filterTask;
            }
            t.run();
        }
    }
    
    private static Enumeration getFileObjectChildren(FileObject fo) {
        
        class WithChildren implements Enumerations.Processor {
            public Object process(Object obj, Collection toAdd) {
                FileObject fo = (FileObject) obj;

                if (fo.isFolder()) {
                    FileObject[] children = fo.getChildren();
                    for (int i = 0 ;i < children.length; i++) {
                        if (VisibilityQuery.getDefault().isVisible(children[i])) {
                            toAdd.add(children[i]);
                        }
                    }
                }

                return fo;
            }
        }

        if (VisibilityQuery.getDefault().isVisible(fo)) {
            Enumeration init = Enumerations.singleton(fo);
            if (fo.isData()) {
                return init;
            } else {
                return Enumerations.queue(init, new WithChildren());
            }
        } else {
            return Enumerations.empty();
        }
    }
}
