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

package org.netbeans.modules.apisupport.beanbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

class FileAttrKids extends Children.Keys {

    private FileObject fo;

    public FileAttrKids(FileObject fo) {
        this.fo = fo;
    }

    protected void addNotify() {
        List l = new ArrayList();
        Enumeration e = fo.getAttributes();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof String) {
                l.add(o);
            } else {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, new IllegalStateException("Strange key " + o + " from " + fo)); // NOI18N
            }
        }
        Collections.sort(l);
        setKeys(l);
    }
    
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }
    
    protected Node[] createNodes(Object k) {
        String key = (String) k;
        Node n = PropSetKids.makeObjectNode(fo.getAttribute(key));
        n.setDisplayName(key + " = " + n.getDisplayName());
        return new Node[] { n };
    }
    
}
