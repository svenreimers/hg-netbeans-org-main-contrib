/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;

import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.FileUtil;

import java.io.File;

/**
 * Access profile specifics data, such as possible statuses,
 * abstract Statuses to VCS statuses mapping and cache
 * file relative path.
 * <p>
 * The implementation forwards to FS that should be hopefully
 * CommandLineVCSFS that knows it's profile.
 *
 * @author Petr Kuzel
 */
final class Profiles {

    /**
     * Consults profile to get proper cache file. Note that
     * one cache file is returned for all files is one directory.
     * It return null if disk caching cannot be used.
     */
    public static File findCacheFile(File file) {
        VcsFileSystem fs = findVcsFileSystem(file);
        if (fs != null) {
            String root = FileUtil.toFile(fs.getRoot()).getAbsolutePath();
            String path = file.getAbsolutePath().substring(root.length());
            File ret = fs.getCacheFileName(file, path);
            // TODO check that it does not return null for FS root subdirs
            // it caused a lot of  problems in original implementation (such entries must be held locked in-memory)
            assert path.length() > 0 || ret != null;
            return ret;
        }
        return null;
    }

    /**
     * Locates VCSFs that cover given file.
     */
    private static VcsFileSystem findVcsFileSystem(File file) {
        String path = file.getAbsolutePath();
        FSRegistry registry = FSRegistry.getDefault();
        FSInfo[] infos = registry.getRegistered();
        for (int i = 0; i<infos.length; i++) {
            FSInfo info = infos[i];
            if (info.isControl() == false) continue;
            File root = info.getFSRoot();
            String rootPath = root.getAbsolutePath();
            if (path.startsWith(rootPath)) {
                return (VcsFileSystem) info.getFileSystem();  // assuming here that VCSFSs cannot overlap
            }
        }
        return null;
    }

    /**
     * Translates VCS specific statuc to abstract one.
     */
    public static FileStatusInfo toStatusInfo(File file, FileProperties fprops) {
        // TODO implement
        return null;
    }

}
