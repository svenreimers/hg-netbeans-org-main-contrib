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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.*;

import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The Revision List data parser for PVCS.
 * @author  Martin Entlicher
 */
public class PvcsRevisionListGetter extends Object implements VcsAdditionalCommand, CommandDataOutputListener {

    private static final String archiveStr = new String("Archive:"); // NOI18N
    private static final String workfileStr = new String("Workfile:"); // NOI18N
    private static final String createdStr = new String("Archive created:"); // NOI18N
    private static final String ownerStr = new String("Owner:"); // NOI18N
    private static final String attributesStr = new String("Attributes:"); // NOI18N
    private static final String verLabelsStr = new String("Version labels:"); // NOI18N
    private static final String descriptionStr = new String("Description:"); // NOI18N
    private static final String revisionStr = new String("Rev "); // NOI18N
    private static final String revisionCountStr = new String("Rev count:"); // NOI18N
    private static final String branchesStr = new String("Branches:"); // NOI18N
    private static final String lockedByStr = new String("Locked by:"); // NOI18N
    private static final String dateCIStr = new String("Checked in:"); // NOI18N
    private static final String dateLMStr = new String("Last modified:"); // NOI18N
    private static final String authorStr = new String("Author id: "); // NOI18N
    private static final String linesStr = new String(" lines "); // NOI18N
    //private static final String symbNamesStr = new String("symbolic names"); // NOI18N
    private static final String nextRevisionStr = new String("---------------"); // NOI18N
    private static final String nextFileStr = new String("=================="); // NOI18N

    private boolean matchingDescription = false;
    private String description = "";
    private String archive = "";
    private String workfile = "";
    private String archiveCreated = "";
    private String owner = "";
    private boolean matchingAttributes = false;
    private String attributes = "";
    private String revisionCount = "0";
    private ArrayList revisionItems = new ArrayList();
    private ArrayList lastRevisionItems = null;
    private VcsFileSystem fileSystem = null;
    private VcsCommand logCmd = null;
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;

    /** Creates new PvcsRevisionListGetter */
    public PvcsRevisionListGetter() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Run the vlog command and return the exit status.
     */
    private boolean runLog(Hashtable vars) throws InterruptedException {
        //System.out.println("runLog("+prepared+", "+timeout+", "+logDataRegex+")");
        //matchingSymbolicNames = false;
        matchingDescription = false;
        //symbolicNames = new Hashtable();
        revisionItems = new ArrayList();
        vars = new Hashtable(vars);
        fileSystem.getVarValueAdjustment().revertAdjustedVarValues(vars);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(logCmd, vars);
        vce.addDataOutputListener(this);
        fileSystem.getCommandsPool().preprocessCommand(vce, vars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            throw iexc;
        }
        if (vce.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //E.err("exec failed "+ec.getExitStatus()); // NOI18N
            return false;
        }
        //addTags();
        return true;
    }
    
    /**
     * Executes the vlog command to get the logging informations.
     * @param vars variables needed to run cvs commands
     * @param args the arguments,
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull,
     *         false if some error has occured.
     */
    public boolean exec(final Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.logCmd = fileSystem.getCommand(args[0]);
        boolean status;
        try {
            status = runLog(new Hashtable(vars));
        } catch (InterruptedException iexc) {
            return false;
        }
        if (false == status) return status;
        //ArrayList revisionItems = createRevisionItems();
        final RevisionList list = new RevisionList();
        /*
        RevisionItem rootItem = new NumDotRevisionItem(null);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Archive"), archive);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Workfile"), workfile);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_ArchiveCreated"), archiveCreated);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Owner"), owner);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Attributes"), attributes);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_RevisionCount"), revisionCount);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Description"), description);
         */
        Object[] revisionItemsSorted = revisionItems.toArray(new RevisionItem[0]);
        Arrays.sort(revisionItemsSorted);
        revisionItems = new ArrayList(Arrays.asList(revisionItemsSorted));
        lastRevisionItems = revisionItems;
        for(Iterator it = revisionItems.iterator(); it.hasNext(); ) {
            RevisionItem item = (RevisionItem) it.next();
            //System.out.println("CvsRevisionViewer:list.add("+item.getRevision()+")");
            list.add(item);
        }
        returnEncodedList(list, stdoutListener);
        return true;
    }
    
    private void returnEncodedList(RevisionList list, CommandDataOutputListener dataListener) {
        String encodedList;
        try {
            encodedList = VcsUtilities.encodeValue(list);
        } catch (java.io.IOException ioExc) {
            encodedList = null;
        }
        dataListener.outputData(new String[] { encodedList });
    }
    
    /**
     * Get the last revision item obtained.
     */
    private RevisionItem getLastRevisionItem() {
        RevisionItem item = null;
        for(int i = revisionItems.size() - 1; i >= 0; i--) {
            RevisionItem it = (RevisionItem) revisionItems.get(i);
            if (!it.isBranch()) {
                //System.out.println("getLastRevision: "+it.getRevision());
                item = it;
                break;
            }
        }
        return item;
    }
    
    public void outputData(String[] elements) {
        if (elements == null || elements.length == 0 || elements[0] == null) return;
        int begin;
        //System.out.println("element = "+elements[0]);
        if ((begin = elements[0].indexOf(revisionStr)) >= 0) {
            if (elements[0].indexOf(revisionCountStr) >= 0) {
                revisionCount = elements[0].substring(revisionCountStr.length()).trim();
                return ;
            }
            int endRev = elements[0].indexOf('\n', revisionStr.length() + begin + 1);
            if (endRev < 0) endRev = elements[0].length();
            String revision = elements[0].substring(begin + revisionStr.length(), endRev).trim();
            //System.out.println("revision = "+revision);
            RevisionItem item = new NumDotRevisionItem(revision);
            revisionItems.add(item);
        } else if ((begin = elements[0].indexOf(lockedByStr)) >= 0) {
            if (revisionItems.size() > 0) {
                RevisionItem item = getLastRevisionItem();
                int index1 = begin + lockedByStr.length();
                //int index2 = elements[0].indexOf('\n', index1);
                //System.out.println("index1 = "+index1+", index2 = "+index2);
                //if (index2 < index1) return;
                String lockedBy = elements[0].substring(index1).trim();
                item.addProperty(NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_LockedBy"), lockedBy);
            }
        } else if ((begin = elements[0].indexOf(dateCIStr)) >= 0) {
            if (revisionItems.size() > 0) {
                RevisionItem item = getLastRevisionItem();
                int index1 = begin + dateCIStr.length();
                //int index2 = elements[0].indexOf('\n', index1);
                //System.out.println("index1 = "+index1+", index2 = "+index2);
                //if (index2 < index1) return;
                String date = elements[0].substring(index1).trim();
                item.addProperty(NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_CIDate"), date);
            }
        } else if ((begin = elements[0].indexOf(dateLMStr)) >= 0) {
            if (revisionItems.size() > 0) {
                RevisionItem item = getLastRevisionItem();
                int index1 = begin + dateLMStr.length();
                //int index2 = elements[0].indexOf('\n', index1);
                //if (index2 < index1) return;
                String date = elements[0].substring(index1).trim();
                item.addProperty(NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_LMDate"), date);
            }
        } else if ((begin = elements[0].indexOf(authorStr)) >= 0) {
            if (revisionItems.size() > 0) {
                RevisionItem item = getLastRevisionItem();
                int index1 = begin + authorStr.length();
                int index2 = elements[0].indexOf(linesStr, index1);
                if (index2 < index1) index2 = elements[0].length();
                String date = elements[0].substring(index1, index2).trim();
                item.setAuthor(date);
                int index3 = elements[0].indexOf(':', index2);
                if (index3 < 0) return ;
                String lines = elements[0].substring(index3 + 1).trim();
                item.addProperty(NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Lines"), lines);
                //item.addProperty(NbBundle.getBundle(PvcsRevisionListGetter.class).getString("CTL_Author"), date);
            }
        } else if ((begin = elements[0].indexOf(branchesStr)) >= 0) {
            int bBegin = branchesStr.length() + begin + 1;
            while (bBegin < elements[0].length() && Character.isWhitespace(elements[0].charAt(bBegin))) bBegin++;
            int bEnd = elements[0].indexOf(' ', bBegin);
            if (bEnd < 0) bEnd = elements[0].length();
            while (bBegin < bEnd) {
                String branch = elements[0].substring(bBegin, bEnd).trim();
                RevisionItem item = new NumDotRevisionItem(branch);
                revisionItems.add(item);
                bBegin = bEnd+1;
                while (bBegin < elements[0].length() && Character.isWhitespace(elements[0].charAt(bBegin))) bBegin++;
                bEnd = elements[0].indexOf(' ', bBegin);
                if (bEnd < 0) bEnd = elements[0].length();
            }
        } else if (elements[0].indexOf(nextRevisionStr) != 0 && elements[0].indexOf(nextFileStr) != 0 &&
                    revisionItems.size() > 0) {
            RevisionItem item = getLastRevisionItem();
            String msg = item.getMessage();
            if (msg == null) msg = ""; // NOI18N
            else msg += "\n";
            msg += elements[0];
            item.setMessage(msg);
        }
        if (revisionItems.size() == 0) {
            if (matchingDescription) {
                if (elements[0].indexOf(nextRevisionStr) == 0) matchingDescription = false;
                else description += elements[0];
            }
            if (matchingAttributes) {
                if (elements[0].indexOf(' ') != 0) matchingAttributes = false;
                else attributes += ((attributes.length() == 0) ? "" : " \n") + elements[0].trim();
            }
            if (elements[0].indexOf(descriptionStr) == 0) matchingDescription = true;
            else if (elements[0].indexOf(archiveStr) == 0) {
                archive = elements[0].substring(archiveStr.length()).trim();
            }
            else if (elements[0].indexOf(workfileStr) == 0) {
                workfile = elements[0].substring(workfileStr.length()).trim();
            }
            else if (elements[0].indexOf(createdStr) == 0) {
                archiveCreated = elements[0].substring(createdStr.length()).trim();
            }
            else if (elements[0].indexOf(ownerStr) == 0) {
                owner = elements[0].substring(ownerStr.length()).trim();
            }
            else if (elements[0].indexOf(attributesStr) == 0) matchingAttributes = true;
        }
    }
}
