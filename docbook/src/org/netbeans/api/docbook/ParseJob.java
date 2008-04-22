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
/*
 * ParseJob.java
 *
 * Created on October 16, 2006, 7:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.api.docbook;

import java.util.Collection;
import org.netbeans.modules.docbook.parsing.ParseJobFactory;
import org.openide.filesystems.FileObject;

/**
 * A job that will run asynchronously, parsing a file's XML and calling back
 * the provided Callback with results.
 * 
 * @author Tim Boudreau
 */
public abstract class ParseJob <T extends Callback> {
    /**
     * Throws exception if not the one legal subclass allowed.
     */ 
    protected ParseJob() {
//        if (ParseJobFactory.ParseJobImpl.class.isInstance(this)) {
//            throw new IllegalStateException ("Subclassing not allowed");
//        }
    }

    /**
     * Attach some work that should be done the next time the passed
     * file is parsed, causing a parse to be enqueued if necessary.
     * Once the job is created, call its enqueue() method to prepare it
     * to run.
     */
    public static ParseJob createJob (FileObject file, Callback callback) {
        if (file == null) {
            throw new NullPointerException ("File null");
        }
        if (callback == null) {
            throw new NullPointerException ("Callback null");
        }
        return ParseJobFactory.createJob (file, callback);
    }
    
    /**
     * Create a collection of jobs from a collection of callbacks.
     */ 
    public static Collection <ParseJob> createJobs (FileObject file, Collection <Callback> callbacks) {
        if (file == null) {
            throw new NullPointerException ("File null");
        }
        if (callbacks == null) {
            throw new NullPointerException ("Callback null");
        }
        if (!callbacks.isEmpty()) {
            return ParseJobFactory.createJobs (file, callbacks);
        } else {
            return null;
        }
    }
    

    public final ParseJob enqueue() {
        ParseJobFactory.enqueue(this);
        return this;
    }
    
    public abstract boolean isEnqueued();
    
    public abstract boolean isRunning();
    
    public FileObject getFile() {
        return ParseJobFactory.getFile(this);
    }

    /**
     * Blocks until this job has been run.  If it is not enqueued,
     * returns immediately.
     */ 
    public abstract void waitFinished() throws InterruptedException;

    public void cancel() {
        ParseJobFactory.cancelled (this);
    }

    protected final void done (Callback callback, FileObject ob, ParseJob job) {
        callback.done(ob, job);
    }

    protected final void start (Callback callback, FileObject ob, ParseJob job) {
        callback.start(ob, job);
    }

    protected final void failed (Callback callback, Exception e, FileObject ob, ParseJob job) {
        callback.failed (e, ob, job);
    }



}
