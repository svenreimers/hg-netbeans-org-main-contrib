/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.oome;

import insane.*;
import org.openide.ErrorManager;

/** This is a special error manager that detects OOMError and
 * and handles it for debugging purposes.
 *
 * @author Petr Nejedly
 */
public final class OomeErrorManager extends ErrorManager {

    public byte[] headroom = new byte[32*1024*1024]; // 32 MB of headroom

    /** Notifies all the exceptions associated with
     * this thread.
     */
    public synchronized void notify (int severity, Throwable t) {
        if (t instanceof OutOfMemoryError) {
	    headroom = null;
	    
	    // XXX - carefully stop unneeded threads here.
	    
    	    try {
        	Insane.main(null);
    	    } catch (Exception e) {
        	e.printStackTrace();
    	    }
	    org.openide.LifecycleManager.getDefault().exit();
        }        
    }

    // ErrorManager empty stub. We implement only one EM aspect    
    public synchronized Throwable annotate ( Throwable t,
	    int severity, String message, String localizedMessage,
	    Throwable stackTrace, java.util.Date date) { return t; }
    public synchronized Throwable attachAnnotations (Throwable t,
	    Annotation[] arr) { return t; }
    public void log(int severity, String s) { }    
    public final ErrorManager getInstance(String name) { return this; }
    public synchronized Annotation[] findAnnotations (Throwable t) { return null;}
}
