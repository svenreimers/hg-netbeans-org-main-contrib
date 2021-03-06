/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.util.virtuals;

import java.beans.*;
import java.io.*;
import java.lang.ref.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.openide.filesystems.*;
import org.openide.ErrorManager;

import org.openide.util.RequestProcessor;

/** Request for parsing of an filesystem. Can be stoped.
* Copied from openide by Milos Kleint.. :) because the class is final and I need to rewrite it..
*
* @author Milos Kleint
*/
public final class VcsRefreshRequest extends Object implements Runnable {
    /** how much folders refresh at one request */
    private static final int REFRESH_COUNT = 30;
    private static final int PREFERRED_REFRESH_DELAY = 800;

    /** fs to work on */
    private Reference system;
    
    private Reference refresher;

    /** enumeration of folders Reference (FileObjects) to process */
    private Enumeration en;

    /** how often invoke itself */
    private int refreshTime;

    /** task to call us */
    private RequestProcessor.Task task;
    
    private HashSet preffered;
    
    private boolean interrupt;

    /** Constructor
    * @param fs file system to refresh
    * @param ms refresh time
    */
    public VcsRefreshRequest(AbstractFileSystem fs, int ms, VirtualsRefreshing refresher) {
        system = new WeakReference (fs);
        this.refresher  = new WeakReference(refresher);
        int randSeed;
        if (ms == 0) {
            refreshTime = Integer.MAX_VALUE;
            randSeed = Integer.MAX_VALUE;
        } else {
            refreshTime = ms;
            // will generate a random seed for starting the refreshing.. that way we have the
            // different filesystems refresh at random times
            randSeed = (int)Math.round(Math.random() * ms) + 1000;
        }
//        System.out.println("seed=" + randSeed + " for=" + fs.getDisplayName());
        task = RequestProcessor.postRequest (this, randSeed, Thread.MIN_PRIORITY);
    }

    /**
     * Getter for the time.
     * @return The refresh time in miliseconds.
     */
    public int getRefreshTime () {
        if (refreshTime == Integer.MAX_VALUE) {
            return 0;
        } else {
            return refreshTime;
        }
    }
    
    /**
     * Setter for the time.
     * @param ms The refresh time in miliseconds.
     */
    public void setRefreshTime (int ms) {
        if (ms == refreshTime) return ;
        refreshTime = (ms == 0) ? Integer.MAX_VALUE : ms;
        synchronized (this) {
            // If the task is running, let it be. If not reschedule it.
            if (task != null) {
                task.schedule(ms);
            } else if (ms == 0) {
                // If the task is running and we should not do the refresh, stop it.
                interrupt = true;
            }
        }
    }

    /** Stops the task.
    */
    public synchronized void stop () {
        refreshTime = 0;

        if (task == null) {        
            // null task means that the request processor is running =>
            // wait for end of task execution
            try {
                wait ();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * allows the filesystem to add a specific folder that should be refreshed with priority
     * @param folderPath the packageNameExt path to the folder
     */
    public  void addPrefferedFolder(String folderPath) {
        synchronized (this) {
            if (preffered == null) {
                preffered = new HashSet();
            }
            preffered.add(folderPath);
            if (task != null) {
                //if (task.getDelay() > 1000 || task.getDelay() < 600) {
//                    System.out.println("rescheduling..");
                task.schedule(PREFERRED_REFRESH_DELAY);
                //}
            } else {
                //task is running - attempt to interrupt..
                interrupt = true;
            }
        }
    }
    
    synchronized boolean hasPrefferedFolder() {
        if (preffered == null || preffered.size() == 0) {
            return false;
        }
        return true;
    }
    
    private boolean shouldBeInterrupted() {
        return interrupt;
    }
    
    FileObject getPrefferedFolder() {
        boolean repeat = false;
        FileObject fo = null;
        while (!repeat) {
            repeat = true;
            String toReturn;
            synchronized (this) {
                if (preffered == null || preffered.size() == 0) {
//                    System.out.println("pref is null");
                    return null;
                }
                toReturn = (String)preffered.iterator().next();
                preffered.remove(toReturn);
            }
            if (toReturn == null) return null;
            AbstractFileSystem fs = (AbstractFileSystem)this.system.get();
            if (fs == null) {
//                System.out.println("fs is null");
                return null;
            }
            fo = fs.findResource(toReturn);
            if (fo == null) {
//                System.out.println("repeating.." +toReturn);
                repeat = false;
            }
        }
//        System.out.println("ok=" + fo.getName());
        return fo;
    }

    /** Refreshes the system.
    */
    public void run () {
        // this code is executed only in RequestProcessor thread
        int ms;
        RequestProcessor.Task t;
        synchronized (this) {
            // the synchronization is here to be sure
            // that 
            ms = refreshTime;
            
            if (ms <= 0) {
                // finish silently if already stopped
                return;
            }
            
            t = task;
            interrupt = false;
        }
        
        try {
          // by setting task to null we indicate that we are currently processing
          // files and that any stop should wait till the processing is over
          task = null;
          
          doLoop ();
        } finally {
             synchronized (this) {
                 // reseting task variable back to indicate that 
                 // the processing is over
                 task = t;
                 interrupt = false;
                 
                 notifyAll ();
                 
             }
             // if there's any prefferer folders, shcedule earlier
             if (hasPrefferedFolder()) {
                t.schedule(PREFERRED_REFRESH_DELAY);
             } else {
             // plan the task for next execution
                t.schedule (ms);
             }
        }
    }
    
    
    private void doLoop () {
        AbstractFileSystem system = (AbstractFileSystem)this.system.get ();
        if (system == null) {
            // end for ever the fs does not exist no more
            refreshTime = 0;
            return;
        }
//        System.out.println("executing for =" + system.getDisplayName());
        VirtualsRefreshing refreshing = (VirtualsRefreshing)this.refresher.get();
        if (refreshing == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "VcsRefreshRequest: Missing refresher. Please file a bug against vcscore module");
            return;
        }
        
        if (en == null || !en.hasMoreElements ()) {
            // start again from root
            en = existingFolders (refreshing);
        }

        FileObject prefFo = getPrefferedFolder();
        while (prefFo != null) {
            refreshing.doVirtualsRefresh(prefFo);
            //if (shouldBeInterrupted()) {
            //    return;
            //}
//            System.out.println("pref refre.." + prefFo.getName());
            prefFo = getPrefferedFolder();
        }
        
        // I should never do the refresh if the refresh time is so high.
        if (refreshTime == Integer.MAX_VALUE) {
            return ;
        }

        for (int i = 0; i < REFRESH_COUNT && en.hasMoreElements (); i++) {
            FileObject fo = (FileObject)en.nextElement ();
            if (fo != null/* && (!fo.isFolder() || fo.isInitialized ())*/) {
                refreshing.doVirtualsRefresh(fo);
            }
            
            if (refreshTime <= 0 || shouldBeInterrupted()) {
                // after each refresh check the current value of refreshTime
                // again and if it goes to zero exit as fast a you can
                return;
            }
        }

        // clear the queue
        if (!en.hasMoreElements ()) {
            en = null;
        }
    }

    /** Existing folders for abstract file objects.
    */
    private static Enumeration existingFolders (VirtualsRefreshing fs) {
        return fs.getExistingFolders();
    }
}
