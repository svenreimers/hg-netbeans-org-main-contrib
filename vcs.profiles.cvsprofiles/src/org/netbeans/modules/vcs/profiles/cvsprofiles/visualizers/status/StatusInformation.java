/*****************************************************************************
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
 * The Original Software is the CVS Client Library.
 * The Initial Developer of the Original Software is Robert Greig.
 * Portions created by Robert Greig are Copyright (C) 2000.
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
 * Contributor(s): Robert Greig.
 *****************************************************************************/
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.*;

import java.io.*;
import java.util.*;


/**
 * Describes status information for a file. This is the result of doing a
 * cvs status command. The fields in instances of this object are populated
 * by response handlers.
 * @author  Robert Greig
 */
public final class StatusInformation extends FileInfoContainer {
     /**
     * The Added status, i.e. the file has been added to the repository
     * but not committed yet.
     */
    public static final String ADDED = "Locally Added"; //NOI18N

    /**
     * The Removed status, i.e. the file has been removed from the repository
     * but not committed yet
     */
    public static final String REMOVED = "Locally Removed"; //NOI18N

    /**
     * The locally modified status, i.e. the file has been modified locally
     * and is out of sync with the repository
     */
    public static final String MODIFIED = "Locally Modified"; //NOI18N

    /**
     * The up-to-date status, i.e. the file is in sync with the repository
     */
    public static final String UP_TO_DATE = "Up-to-date"; //NOI18N

    /**
     * The "needs checkout" status, i.e. the file is out of sync with the
     * repository and needs to be updated
     */
    public static final String NEEDS_CHECKOUT = "Needs Checkout"; //NOI18N

    /**
     * The "needs patch" status, i.e. the file is out of sync with the
     * repository and needs to be patched
     */
    public static final String NEEDS_PATCH = "Needs Patch"; //NOI18N

    /**
     * The "needs merge" status, i.e. the file is locally modified and
     * the file in the repository has been modified too
     */
    public static final String NEEDS_MERGE = "Needs Merge"; //NOI18N

    /**
     * The "conflicts" status, i.e. the file has been merged and now
     * has conflicts that need resolved before it can be checked-in
     */
    public static final String HAS_CONFLICTS = "File had conflicts on merge"; //NOI18N

    /**
     * The unknown status, the file is not versioned.
     */
    public static final String UNKNOWN = "Unknown"; //NOI18N
 
    /**
     * The invalid status, i.e. the file is not known to the CVS repository.
     */
    public static final String INVALID = "Entry Invalid"; //NOI18N
    
    private File file;
    private String fileName;
    private String status;
    private String statusLC;
    private String workingRevision;
    private String repositoryRevision;
    private String repositoryFileName;
    private String stickyDate;
    private String stickyOptions;
    private String stickyTag;
    
    private Exception createdException;

    /**
     * Hold key pairs of existing tags.
     */
    private List tags;

    private StringBuffer symNamesBuffer;

    public StatusInformation() {
        createdException = new Exception();
        setAllExistingTags(null);
    }

    /**
     * Getter for property file.
     * @return Value of property file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter for property file.
     * @param file New value of property file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter for property fileName.
     * @return Value of property fileName.
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Setter for property fileName.
     * @param fileName New value of property fileName.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Getter for property status.
     * @return Value of property status.
     */
    public String getStatus() {
        return status;
    }
    
    private static String findSharedStatus(String status) {
        if (UP_TO_DATE.equals(status)) return UP_TO_DATE;
        if (ADDED.equals(status)) return ADDED;
        if (REMOVED.equals(status)) return REMOVED;
        if (MODIFIED.equals(status)) return MODIFIED;
        if (NEEDS_CHECKOUT.equals(status)) return NEEDS_CHECKOUT;
        if (NEEDS_PATCH.equals(status)) return NEEDS_PATCH;
        if (NEEDS_MERGE.equals(status)) return NEEDS_MERGE;
        if (HAS_CONFLICTS.equals(status)) return HAS_CONFLICTS;
        if (UNKNOWN.equals(status)) return UNKNOWN;
        if (INVALID.equals(status)) return INVALID;
        return status.trim();
    }

    /**
     * Setter for property status.
     * @param status New value of property status.
     */
    public void setStatus(String status) {
        this.status = findSharedStatus(status);
        if (statusLC == null) {
            statusLC = status;
        }
    }

    /**
     * Getter for property status localized.
     * @return Value of property status localized.
     */
    public String getStatusLC() {
        return statusLC;
    }

    /**
     * Setter for property status localized.
     * @param status New value of property status localized.
     */
    public void setStatusLC(String statusLC) {
        this.statusLC = statusLC;
    }

    /**
     * Getter for property workingRevision.
     * @return Value of property workingRevision.
     */
    public String getWorkingRevision() {
        return workingRevision;
    }

    /**
     * Setter for property workingRevision.
     * @param workingRevision New value of property workingRevision.
     */
    public void setWorkingRevision(String workingRevision) {
        this.workingRevision = workingRevision;
    }

    /**
     * Getter for property repositoryRevision.
     * @return Value of property repositoryRevision.
     */
    public String getRepositoryRevision() {
        return repositoryRevision;
    }

    /**
     * Setter for property repositoryRevision.
     * @param repositoryRevision New value of property repositoryRevision.
     */
    public void setRepositoryRevision(String repositoryRevision) {
        this.repositoryRevision = repositoryRevision;
    }

    /**
     * Getter for property repositoryFileName.
     * @return Value of property repositoryFileName.
     */
    public String getRepositoryFileName() {
        return repositoryFileName;
    }

    /**
     * Setter for property repositoryFileName.
     * @param repositoryRevision New value of property repositoryFileName.
     */
    public void setRepositoryFileName(String repositoryFileName) {
        this.repositoryFileName = repositoryFileName;
    }

    /**
     * Getter for property stickyTag.
     * @return Value of property stickyTag.
     */
    public String getStickyTag() {
        return stickyTag;
    }

    /**
     * Setter for property stickyTag.
     * @param stickyTag New value of property stickyTag.
     */
    public void setStickyTag(String stickyTag) {
        this.stickyTag = stickyTag;
    }

    /**
     * Getter for property stickyDate.
     * @return Value of property stickyDate.
     */
    public String getStickyDate() {
        return stickyDate;
    }

    /**
     * Setter for property stickyDate.
     * @param stickyDate New value of property stickyDate.
     */
    public void setStickyDate(String stickyDate) {
        this.stickyDate = stickyDate;
    }

    /**
     * Getter for property stickyOptions.
     * @return Value of property stickyOptions.
     */
    public String getStickyOptions() {
        return stickyOptions;
    }

    /**
     * Setter for property stickyOptions.
     * @param stickyOptions New value of property stickyOptions.
     */
    public void setStickyOptions(String stickyOptions) {
        this.stickyOptions = stickyOptions;
    }

    public void addExistingTag(String tagName, String revisionNumber) {
        if (tags == null) {
            tags = new ArrayList();
        }
        SymName newName = new SymName();
        newName.setTag(tagName);
        newName.setRevision(revisionNumber);
        tags.add(newName);
    }

    public List getAllExistingTags() {
        return tags;
    }

    public void setAllExistingTags(List tags) {
        this.tags = tags;
    }

    /** Search the symbolic names by number of revision. If not found, return null.
     */
    public List getSymNamesForRevision(String revNumber) {
        if (tags == null) {
            return null;
        }

        List list = new LinkedList();

        for (Iterator it = tags.iterator(); it.hasNext();) {
            StatusInformation.SymName item = (StatusInformation.SymName)it.next();
            if (item.getRevision().equals(revNumber)) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Search the symbolic names by name of tag (symbolic name).
     * If not found, return null.
     */
    public StatusInformation.SymName getSymNameForTag(String tagName) {
        if (tags == null) {
            return null;
        }

        for (Iterator it = tags.iterator(); it.hasNext();) {
            StatusInformation.SymName item = (StatusInformation.SymName)it.next();
            if (item.getTag().equals(tagName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Return a string representation of this object. Useful for debugging.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\nFile: "); //NOI18N
        buf.append((file != null) ? file.getAbsolutePath()
                   : "null"); //NOI18N
        buf.append("\nStatus is: "); //NOI18N
        buf.append(getStatus());
        buf.append("\nWorking revision: "); //NOI18N
        buf.append(workingRevision);
        buf.append("\nRepository revision: "); //NOI18N
        buf.append(repositoryRevision);
        buf.append("\nRepository file: "); // NOI18N
        buf.append(repositoryFileName);
        buf.append("\nSticky date: "); //NOI18N
        buf.append(stickyDate);
        buf.append("\nSticky options: "); //NOI18N
        buf.append(stickyOptions);
        buf.append("\nSticky tag: "); //NOI18N
        buf.append(stickyTag);
        if (tags != null && tags.size() > 0) {
            // we are having some tags to print
            buf.append("\nExisting Tags:"); //NOI18N
            for (Iterator it = tags.iterator(); it.hasNext();) {
                buf.append("\n  "); //NOI18N
                buf.append(it.next().toString());
            }
        }
        buf.append("\n");
        buf.append("Created at:\n");
        StackTraceElement[] stack = createdException.getStackTrace();
        int max = stack.length > 10 ? 10 : stack.length;
        for (int i = 1; i<max; i++) {
            buf.append(stack[i].toString() + "\n");  // NOI18N
        }
        return buf.toString();
    }

    /**
     * An inner class storing information about a symbolic name.
     * Consists of a pair of Strings. tag + revision.
     */
    public static class SymName {
        private String tag;
        private String revision;

        public SymName() {
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String symName) {
            tag = symName;
        }

        public void setRevision(String rev) {
            revision = rev;
        }

        public String getRevision() {
            return revision;
        }

        public String toString() {
            return getTag() + " : " + getRevision(); //NOI18N
        }
    }
}
