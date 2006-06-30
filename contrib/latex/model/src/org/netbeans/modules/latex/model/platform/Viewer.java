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
package org.netbeans.modules.latex.model.platform;

import java.net.URI;
import org.openide.filesystems.FileObject;

/** This class allows to view the content of a particular file.
 *
 *  It also allows to view a particular position in the file.
 *
 *  @author Jan Lahoda
 */
public interface Viewer {

    /** Shows the given file in a viewer.
     *
     *  @param file file to show
     *
     *  @throws NullPointerException is the file is null
     */
    public void show(FileObject file, FilePosition startPosition) throws NullPointerException;

    public String getName();

    public String getDisplayName();

    public boolean isSupported();

    public boolean accepts(URI uri);
    
}
