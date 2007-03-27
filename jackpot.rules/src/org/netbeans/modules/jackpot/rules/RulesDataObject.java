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

package org.netbeans.modules.jackpot.rules;

import java.io.IOException;
import org.netbeans.api.jackpot.Query;
import org.netbeans.spi.jackpot.QueryCookie;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.*;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;

/**
 * RulesDataObject: a DataObject representing Jackpot rules files.
 */
public class RulesDataObject extends MultiDataObject {
    /**
     * Creates a new RulesDataObject
     * @param file the associated file object
     * @param loader the MultiFileLoader
     * @throws org.openide.loaders.DataObjectExistsException 
     */
    public RulesDataObject(final FileObject file, MultiFileLoader loader)
      throws DataObjectExistsException {
        super(file, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        cookies.add(new InstanceCookie() {
            public Class instanceClass() throws IOException,ClassNotFoundException {
                return Query.class;
            }
            public Object instanceCreate() throws IOException,ClassNotFoundException {
                return RulesDataObject.this;
            }
            public String instanceName() {
                return file.getName();
            }
        });
        cookies.add(new QueryCookie() {
            public Query getQuery(String queryDescription) throws Exception {
                return QueryFactory.getQuery(getPrimaryFile(), queryDescription);
            }
        });
    }
    
    public Node createNodeDelegate() {
        return new RulesDataNode(this);
    }
}
