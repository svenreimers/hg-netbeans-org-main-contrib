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
 * The Original Software is the Simple Edit Module.
 * The Initial Developer of the Original Software is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
 *
 * Contributor(s): David Strupl.
 */
package cz.solutions.simpleedit;

import java.util.HashSet;
import java.util.Set;
import org.openide.loaders.DataObject;

/**
 *
 * @author David Strupl
 */
public class OpenedFiles {

    private static OpenedFiles instance = new OpenedFiles();

    private Set opened = new HashSet();

    /** Creates a new instance of FilesHistory */
    private OpenedFiles() {
    }

    public static OpenedFiles getDefault() {
        return instance;
    }
    
    public void addDataObject(DataObject dobj) {
        opened.add(dobj);
    }
    
    public void removeDataObject(DataObject dobj) {
        opened.remove(dobj);
    }
    
    public boolean keepOpened(DataObject dobj) {
        return opened.contains(dobj);
    }
}
