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

package org.netbeans.modules.docbook;

import org.openide.cookies.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.lookup.InstanceContent;

public class DocBookDataObject extends MultiDataObject {

    public DocBookDataObject(FileObject pf, DocBookDataLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        getCookieSet().add(new DocBookEditorSupport(this));
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected Node createNodeDelegate() {
        return new DocBookDataNode(this, new InstanceContent());
    }
    
    final void addSaveCookie(SaveCookie save) {
        getCookieSet().add(save);
    }
     
    final void removeSaveCookie(SaveCookie save) {
        getCookieSet().remove(save);
    }
    
}
