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

package org.netbeans.modules.vcscore;

import java.io.File;

import org.netbeans.spi.queries.CollocationQueryImplementation;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * VCS collocation query implementation based on VCS filesystems.
 * Files are collocated if they reside on the same VCS filesystem.
 * The root of a file is defined as the root of the appropriate VCS filesystem.
 *
 * <p>XXX Working directory for test under XTest is <module>/test/work/user/
 * and that means all test files will be collocated because of this impl. 
 * Document this somewhere so that test authors requiring control over
 * file collocations are aware of that, e.g. ant/project tests hide this impl.
 *
 * @author Jesse Glick, Martin Entlicher
 */
public class VcsCollocationQueryImpl implements CollocationQueryImplementation {
    
    /** Do nothing */
    public VcsCollocationQueryImpl() {}
    
    public boolean areCollocated(File file1, File file2) {
        FileObject fo1 = FileUtil.toFileObject(file1);
        FileObject fo2 = FileUtil.toFileObject(file2);
        
        // NPE HOTFIX: see the documentation of CollocationQueryImplementation.
        // It explicitely says that files might not exist. Please rewrite to
        // not depend on FileObjects. For now I at least check for null otherwise
        // I'm getting NPE.
        if (fo1 == null || fo2 == null) {
            return false;
        }
        
        Object vcsFS1 = fo1.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        Object vcsFS2 = fo2.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        return vcsFS1 != null && vcsFS2 != null && vcsFS1.equals(vcsFS2);
    }
    
    public File findRoot(File f) {
        FileObject fo = FileUtil.toFileObject(f);
        
        // NPE HOTFIX: see above
        if (fo == null) {
            return null;
        }
        
        VcsFileSystem vcsFS = (VcsFileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (vcsFS == null) return null;
        else return vcsFS.getRootDirectory();
    }
    
}
