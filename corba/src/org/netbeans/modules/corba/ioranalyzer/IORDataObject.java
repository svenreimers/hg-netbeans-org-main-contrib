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

package org.netbeans.modules.corba.ioranalyzer;

import java.io.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.cookies.*;
import org.openide.text.*;
import org.openide.util.*;

public class IORDataObject extends MultiDataObject implements FileChangeListener {

    static final long serialVersionUID = 2206846110094280146L;
    private  IOREditorSupport editorCookie;

    public IORDataObject (FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
	super (fo, loader);
        fo.addFileChangeListener (this);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public Node.Cookie getCookie (Class clazz) {
        if (clazz.isAssignableFrom(IOREditorSupport.class)) {
            this.lazyInit();
            return this.editorCookie;
        }
        else
            return super.getCookie(clazz);
    }
    
    protected Node createNodeDelegate () {
        return new IORNode (this);
    }
    
    public String getContent () {
        FileObject fobj = getPrimaryFile();
        BufferedReader in = null;
        try {
            in = new BufferedReader ( new InputStreamReader ( fobj.getInputStream()));
            return in.readLine();
        }catch (IOException e) {
            return null;
        }
        finally {
            if (in != null)
                try {
                    in.close();
                }catch (IOException ioe){}
        }
    }

	public CookieSet getCookieSet0 () {
		return this.getCookieSet();		
	}

    public void fileDeleted(final org.openide.filesystems.FileEvent event) {
    }
    
    public void fileDataCreated(final org.openide.filesystems.FileEvent event) {
    }
    
    public void fileFolderCreated(final org.openide.filesystems.FileEvent event) {
    }
    
    public void fileRenamed(final org.openide.filesystems.FileRenameEvent event) {
    }
    
    public void fileAttributeChanged(final org.openide.filesystems.FileAttributeEvent event) {
    }
    
    public void fileChanged(final org.openide.filesystems.FileEvent event) {
        org.openide.nodes.Children cld = this.getNodeDelegate().getChildren();
        if (cld instanceof ProfileChildren) {
            ((ProfileChildren)cld).update();
        }
    }
    
    public boolean isLoaded () {
        if (this.editorCookie == null)
            return false;
        return editorCookie.isDocumentLoaded();
    }
    
    private void lazyInit () {
        if (this.editorCookie == null)
            this.editorCookie = new IOREditorSupport (this);
    }
    
}
