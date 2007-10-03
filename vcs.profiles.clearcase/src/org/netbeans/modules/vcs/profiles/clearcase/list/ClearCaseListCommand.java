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
 * The Original Software is Forte for Java, Community Edition. The Initial
 * Developer of the Original Software is Sun Microsystems, Inc. Portions
 * Copyright 1997-2006 Sun Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.vcs.profiles.clearcase.list;

import java.io.*;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

/**
 * Implements List command for ClearCase.
 * @author  Alan Tai, Martin Entlicher, Peter Wisnovsky
 */
public class ClearCaseListCommand extends AbstractListCommand
{
    /** Directory in which we are executing */
    private String dir = null;

    /** Creates new ClearCaseListCommand */
    public ClearCaseListCommand()
    {
    }

    /**
     * This is odd, since it only calls super.setFileSystem. Yet
     * without it it dumps core. Perhaps someone is doing reflection
     * on the class?
     */
    public void setFileSystem(VcsFileSystem fileSystem) 
    {
        super.setFileSystem(fileSystem);
    }

    private void initDir(Hashtable vars)
    {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null)
        {
            rootDir = "."; // NOI18N
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null)
        {
            this.dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) // NOI18N
        {
            dir=rootDir;
            if (module != null && module.length() > 0)
                dir += File.separator + module;
        } 
        else 
        {
            if (module == null)
                dir=rootDir+File.separator+dir;
            else
                dir=rootDir+File.separator+module+File.separator+dir;
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
    }

    /**
     * List files of the VCS Repository.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * filesByName listing of files with statuses
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex)
    {
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.filesByName = filesByName;
        if (args.length < 1) 
        {
            stderrNRListener.outputLine("Expecting a command name as an argument!"); //NOI18N
            return false;
        }
        initVars(vars);
        initDir(vars);
        try {
            runCommand(vars, args[0], false);
        } catch (InterruptedException iex) {
            return false;
        }

        return !shouldFail;
    }

    /**
     * Output data. The architecture of the VCS generic module for
     * these sort of commands is that the lines from the output of
     * running "cleartool ls" are piped into this method as an array
     * of strings for each line
     * 
     * @param elements the line from running "ct ls"
     */
    public void outputData(String elements[])
    {
        ////////////////////////////////////////////////////////////////
        // Static vars

        // The syntax of the lines is, for versioned files:
        // filename@@/main/branch/version#  Rule: branch [-mkbranch branch]
        //
        // For local files:
        // filename
        //
        // For checked files:
        // filename@@/main/branch/CHECKEDOUT from /main/branch/version#     Rule: CHECKEDOUT
        // 
        // For removed checkedout files
        // filename@@/main/branch/CHECKEDOUT from /main/bugfix/2 [checkedout but removed]
        //
        // For eclipsed files
        // Y.java@@ [eclipsed]

        // Index of the name of the file
		final int nameIndex = 0;

        // The separator between the name
        final String statusSep = "@@";

        String line = elements[0];

        // Is there an @@ in the line?
        int statIndex = line.indexOf(statusSep, nameIndex);

        if (statIndex < 0)
        {
            // If there was no @@, then this must be a local file.
			return;
		}

        // fileInfo holds the filename and annotation of the file in an
        // array indices 0 and 1, respectively
        String[] fileInfo = new String[2];

        // Put the name in the fileInfo
        fileInfo[0] = line.substring(nameIndex, statIndex);

        // Is the file a directory? If so, add a slash to the
        // filename. I don't know why one would need to do this, but
        // we do!
        File file = new File(dir + File.separator + fileInfo[0]);
        if (file != null && file.isDirectory() )
        {
            fileInfo[0] += "/";
        }

        ////////////////////////////////////////////////////////////////
        // Map the status

        if (line.indexOf("[checkedout but removed]") > -1)
            fileInfo[1] = "CHECKEDOUT REMOVED";
        else if (line.indexOf("CHECKEDOUT") > -1)
            fileInfo[1] = "CHECKEDOUT";
        else if (line.indexOf("[eclipsed]") > -1)
            fileInfo[1] = "ECLIPSED";
        else
            fileInfo[1] = line.substring(statIndex + 2, line.indexOf(' ', statIndex + 2)); // Trim the rule
        
        // Put it in the output. Wierd.
    	filesByName.put(fileInfo[0], fileInfo);
    }
}
