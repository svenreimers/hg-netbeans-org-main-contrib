/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.cmdline.VcsListRecursiveCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

public class TeamwareRefreshRecursivelyCommand extends VcsListRecursiveCommand {

    public boolean listRecursively(
        Hashtable vars, String[] args,
        VcsDirContainer filesByName,
        CommandOutputListener stdoutListener,
        CommandOutputListener stderrListener,
        CommandDataOutputListener stdoutDataListener,
        String dataRegex,
        CommandDataOutputListener stderrDataListener,
        String errorRegex) {
            
        String rootDir = (String) vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        String dirName = (String) vars.get("DIR");
        File root = new File(rootDir);
        File dir = (module != null) ? new File(root, module) : root;
        if (dirName != null) {
            dir = new File(dir, dirName);
        }
        String path = dir.toString().substring(root.toString().length())
            .replace(File.separatorChar, '/');
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        listDir(path, dir, filesByName, stdoutListener, stderrListener);
        return true;

    }
    
    private void listDir(String path, File dir, VcsDirContainer filesByName,
        CommandOutputListener stdout,
        CommandOutputListener stderr) {

        if (TeamwareRefreshSupport.ignoreFile(dir)) {
            stderr.outputLine("Ignoring " + dir);
            return;
        }
        File[] files = TeamwareRefreshSupport.listFilesInDir(dir);
        filesByName.setPath(path);
        filesByName.setName(dir.getName());
        Hashtable table = (Hashtable) filesByName.getElement();
        if (table == null) {
            table = new Hashtable();
            filesByName.setElement(table);
        }
        File sccsDir = new File(dir, "SCCS");
        for (int i = 0 ; i < files.length; i++) {
            String[] data = TeamwareRefreshSupport.listFile(files[i], sccsDir, stderr);
            if (data == null) {
                continue;
            }
            stdout.outputLine(dir + File.separator + data[1] + " [" + data[0] + "]");
            table.put(data[1], data);
            if (data[0].equals("Ignored")) {
                continue;
            }
            String subpath = path;
            if (path.length() > 0) {
                subpath += "/";
            }
            subpath += data[1];
            if (files[i].isDirectory()) {
                VcsDirContainer subDir = filesByName.getContainerWithPath(subpath);
                if (subDir == null) {
                    subDir = filesByName.addSubdir(data[1]);
                }
                listDir(subpath, files[i], subDir, stdout, stderr);
            }
        }
    }
    
}
