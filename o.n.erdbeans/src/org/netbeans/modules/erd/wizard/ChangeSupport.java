/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.erd.wizard;

import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Andrei Badea
 */
public class ChangeSupport {

    private Object source;
    private LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();

    public ChangeSupport(Object source) {
        this.source = source;
    }

    public synchronized void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void fireChange() {
        fireChange(new ChangeEvent(source));
    }

    public void fireChange(ChangeEvent event) {
        HashSet<ChangeListener> listenersCopy = null;
        synchronized (this) {
            listenersCopy = new HashSet<ChangeListener>(listeners);
        }
        for (ChangeListener l : listenersCopy) {
            l.stateChanged(event);
        }
    }
}
