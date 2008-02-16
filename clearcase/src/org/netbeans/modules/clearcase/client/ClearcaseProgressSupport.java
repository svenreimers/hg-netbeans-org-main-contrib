/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.clearcase.client;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Stupka
 */
public abstract class ClearcaseProgressSupport implements Runnable, Cancellable {

    private Cancellable delegate; 
    private volatile boolean canceled;
    
    private ProgressHandle progressHandle = null;    
    private String displayName = "";            // NOI18N
    private String originalDisplayName = "";    // NOI18N
    private RequestProcessor.Task task;
    
    public RequestProcessor.Task start(RequestProcessor rp,  String displayName) {
        setDisplayName(displayName);        
        startProgress();         
        setProgressQueued();                
        task = rp.post(this);
        task.addTaskListener(new TaskListener() {
            public void taskFinished(org.openide.util.Task task) {
                delegate = null;
            }
        });
        return task;
    }

    public void run() {                
        setProgress();
        performIntern();
    }

    protected void performIntern() {
    }

    protected abstract void perform();

    public synchronized boolean isCanceled() {
        return canceled;
    }

    public synchronized boolean cancel() {
        if (canceled) {
            return false;
        }                
       
        if(task != null) {
            task.cancel();
        }
        if(delegate != null) {
            delegate.cancel();
        }        
        getProgressHandle().finish();
        canceled = true;
        return true;
    }

    void setCancellableDelegate(Cancellable cancellable) {
        this.delegate = cancellable;
    }

    public void setDisplayName(String displayName) {
        if(originalDisplayName.equals("")) { // NOI18N
            originalDisplayName = displayName;
        }
        this.displayName = displayName;
        setProgress();
    }

    private void setProgressQueued() {
        if(progressHandle != null) {            
            progressHandle.progress(NbBundle.getMessage(ClearcaseProgressSupport.class,  "LBL_Queued", displayName));
        }
    }

    private void setProgress() {
        if(progressHandle != null) {            
            progressHandle.progress(displayName);
        }
    }
    
    protected String getDisplayName() {
        return displayName;
    }

    protected ProgressHandle getProgressHandle() {
        if(progressHandle == null) {
            progressHandle = ProgressHandleFactory.createHandle(displayName, this);
        }
        return progressHandle;
    }

    protected void startProgress() {
        getProgressHandle().start();
        //getLogger().logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + originalDisplayName); // NOI18N
    }

    protected void finnishProgress() {
        getProgressHandle().finish();
        if (isCanceled() == false) {
           // getLogger().logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + originalDisplayName + " " + org.openide.util.NbBundle.getMessage(SvnProgressSupport.class, "MSG_Progress_Finished")); // NOI18N
        } else {
            //getLogger().logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + originalDisplayName + " " + org.openide.util.NbBundle.getMessage(SvnProgressSupport.class, "MSG_Progress_Canceled")); // NOI18N
        }
    }
}
