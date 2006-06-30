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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jan Lahoda
 */
public class Queue {

    private LinkedList queue;

    /** Creates a new instance of Queue */
    public Queue() {
        queue = new LinkedList();
    }

    public void put(Object obj) {
        queue.addLast(obj);
    }

    public Object pop() {
        return queue.removeFirst();
    }

    public boolean empty() {
        return queue.size() == 0;
    }
    
    public void putAll(Collection c) {
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            put(i.next());
        }
    }
}
