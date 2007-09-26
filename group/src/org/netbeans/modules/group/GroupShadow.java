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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.group;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.TemplateWizard;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Group shadow - <code>DataObject</code> representing a group of
 * <code>DataObject</code>s on a filesystem.
 * It also defines rules for templating of group members:
 * <ul>
 *     <li>property Template All - if <code>true</code> the group is result,
 *                                otherwise bunch of group members
 *     </li>
 *     <li>property Template Pattern - defines <code>MessageFormat</code> where:
 *         <blockquote>
 *             <table>
 *             <tr><td><code>{0}</code></td>
 *                 <td>is a member name</td></tr>
 *             <tr><td><code>{1}</code></td>
 *                 <td>is a name entered by user
 *                     (during template instantiation)</td></tr>
 *             <tr><td valign="baseline"><code>{2}</code></td>
 *                 <td>is a posfix got from <code>{0}</code> by using part
 *                     following the last &quot;__&quot;<br />
 *                     Examples:<br />
 *                     &nbsp;&nbsp;- postfix of &quot;hello__World&quot;
 *                                   is &quot;World&quot;<br />
 *                     &nbsp;&nbsp;- postfix of &quot;helloWorld&quot;
 *                                   is &quot;&quot; (an empty string)</td></tr>
 *             <tr><td><code>{3}</code></td>
 *                 <td>backward substitution result
 *                     (i.e. __somethingBetween__ =&gt; <code>{1}</code>)
 *                     </td></tr>
 *             </table>
 *         </blockquote>
 *     </li>
 * </ul>
 *
 * @author Martin Ryzl
 * @author Marian Petras
 * @see org.openide.loaders.DataObject
 */
public class GroupShadow extends DataObject
                         implements DataObject.Container {

    /** Generated serial version UID. */
    static final long serialVersionUID =-5086491126656157958L;

    /** extension of files representing groups */
    public static final String GS_EXTENSION = GroupShadowLoader.GS_EXTENSION;

    /** Name of the Show Links Property. */
    public static final String PROP_SHOW_LINKS = "showlinks"; // NOI18N

    /** Name of the Template All property. */
    public static final String PROP_TEMPLATE_ALL = "templateall"; // NOI18N

    /** Name of the Template Pattern property. */
    public static final String PROP_TEMPLATE_PATTERN = "templatepattern"; // NOI18N

    /** Name of the use template pattern property. */
    public static final String PROP_USE_PATTERN = "usepattern"; // NOI18N
    
    /** If true, GroupShadow will show targets for all links. */
    static boolean showLinks = true;

    // http://www.netbeans.org/issues/show_bug.cgi?id=23350
    private static TemplateWizard.Iterator groupTemplateIterator = null;
    
    /** Anti-loop detection. */
    private GroupShadow gsprocessed = null;

    /** Returns a template wizard iterator. */
    private static synchronized TemplateWizard.Iterator
                                getGroupTemplateIterator() {
        if (groupTemplateIterator == null) {
            groupTemplateIterator = new GroupTemplateIterator();
        }
        return groupTemplateIterator;
    }

    
    /**
     * Creates a new group shadow data object.
     *
     * @param  fo  primary file for the new shadow data object
     * @param  dl  data loader which caused this constructor to be caused
     */
    public GroupShadow(final FileObject fo, DataLoader dl)
            throws DataObjectExistsException,
                   IllegalArgumentException,
                   IOException {
        super(fo, dl);
        fo.addFileChangeListener(
                new FileChangeAdapter() {
                    public void fileChanged(FileEvent e) { //group file changed
                        GroupShadow.this.primaryFileChanged();
                   }
                });
    }

    
    /** Creates a group node representing this group shadow node. */
    protected Node createNodeDelegate() {
        GroupNode node = new GroupNode(this, new GroupNodeChildren(this));
        addPropertyChangeListener(node);
        return node;
    }

    /**
     */
    public boolean isDeleteAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    /**
     */
    public boolean isCopyAllowed ()  {
        return true;
    }

    /**
     */
    public boolean isMoveAllowed ()  {
        return !getPrimaryFile ().isReadOnly ();
    }

    /**
     */
    public boolean isRenameAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    /**
     * Copies this <code>GroupShadow</code> to a given folder.
     * If the target folder already contains a file having this object's name,
     * a similar name is used instead, as described in
     * {@link FileUtil#findFreeFileName FileUtil.findFreeFileName(...)}.
     *
     * @param  f  target folder
     * @return  new copy of this <code>GroupShadow</code>
     */
    protected DataObject handleCopy (DataFolder f) throws IOException {
        return handleCopy(f, getName());
    }

    /**
     * Copies this <code>GroupShadow</code> to a given folder
     * and sets it a given name.
     * If the target folder already contains a file having the given name,
     * a similar name is used instead, as described in
     * {@link FileUtil#findFreeFileName FileUtil.findFreeFileName(...)}.
     *
     * @param  f  target folder
     * @param  name  new name of the file (may be the current name)
     * @return  new copy of this <code>GroupShadow</code>
     */
    protected DataObject handleCopy (DataFolder f,
                                     String name) throws IOException {
        String newname = FileUtil.findFreeFileName(f.getPrimaryFile(),
                                                   name,
                                                   GS_EXTENSION);
        FileObject fo = FileUtil.copyFile(getPrimaryFile(),
                                          f.getPrimaryFile(),
                                          newname);
        /*
         * PENDING:
         * Is it correct? Is it enough to just copy the file
         * (without any modification)?
         */
        
        return new GroupShadow(fo, getLoader());
    }

    /**
     * Deletes this <code>GroupShadow</code>.
     */
    protected void handleDelete () throws IOException {
        FileLock lock = getPrimaryFile ().lock ();
        try {
            getPrimaryFile ().delete (lock);
        } finally {
            lock.releaseLock ();
        }
    }

    /**
     * Renames this <code>GroupShadow</code>.
     */
    protected FileObject handleRename (String name) throws IOException {
        FileLock lock = getPrimaryFile ().lock ();
        try {
            getPrimaryFile ().rename (lock, name, GS_EXTENSION);
        } finally {
            lock.releaseLock ();
        }
        return getPrimaryFile ();
    }

    /**
     * Moves this <code>GroupShadow</code> to a given folder.
     * If the target folder already contains a file having this object's name,
     * a similar name is used instead, as described in
     * {@link FileUtil#findFreeFileName FileUtil.findFreeFileName(...)}.
     *
     * @param  f  target folder
     * @return  new primary file of this <code>GroupShadow</code>
     */
    protected FileObject handleMove (DataFolder f) throws IOException {
        String name = FileUtil.findFreeFileName(f.getPrimaryFile(),
                                                getName(),
                                                GS_EXTENSION);
        /*
         * PENDING:
         * Is it correct? Is it enough to just move the file
         * (without any modification)?
         */
        
        return FileUtil.moveFile (getPrimaryFile (), f.getPrimaryFile (), name);
    }


    /*
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx (GroupShadow.class);
    }

    /**
     * Returns a given type of cookie from this object.
     *
     * @param  cookie  type of cookie to be returned
     * @return  if the requested cookie type is
     *          <code>TemplateWizard.Iterator</code>, returns
     *          this group's template wizard iterator;
     *          otherwise it delegates to <code>DataObject</code>'s
     *          <code>getCookie(Class)</code>
     * @see  DataObject#getCookie(Class)
     */
    public Cookie getCookie(Class cookie) {
        if (cookie == TemplateWizard.Iterator.class) {
            return getGroupTemplateIterator();
        } else {
            return super.getCookie (cookie);
        }
    }

    /**
     * Reads names of files contained from a given file.
     *
     * @param  fo  <code>FileObject</code> containing names of contained
     *             files (this object's primary file)
     * @return  names of <code>FileObject</code>s,
     *          each <code>FileObject</code>'s name is relative to the
     *          filesystem the <code>FileObject</code> pertains to
     * @exception  java.io.IOException  if an error occured during reading
     *                                  the given file
     */
    public static List readLinks(FileObject fo) throws IOException {
        List list = new ArrayList();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(fo.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                File f = new File(line);
                /* convert relative to absolute files */
                if (!f.isAbsolute()) {
                    /* I would prefer this but it does not work for "../foo" */
                    // f = FileUtil.toFile(fo.getParent().getFileObject(line));
                    File parentFolder = FileUtil.toFile(fo.getParent());
                    /* use this node's parent as starting reference for the path */
                    f = new File(parentFolder, line).getAbsoluteFile();
                }
                try {
                    /* also "foo/../bar" is problematic, so canonicalize */
                    f = f.getCanonicalFile();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                }
                line = f.getAbsolutePath();
                line = line.replace('\\', '/');
                list.add(line);
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    /* give up */
                }
            }
        }
        return list;
    }

    /**
     * Reads names of contained files from the primary file.
     *
     * @return  list of names of contained files - each file name is relative
     *          to the filesystem the corresponding file is contained in
     * @exception  java.io.IOException  if an error occured during reading
     *                                  the primary file
     */
    public List readLinks() throws IOException {
        return readLinks(getPrimaryFile());
    }

    /**
     * Writes a given list (of file names) to a given file.
     *
     * @param  list  list of <code>String</code>s - file names
     * @param  fo  file to save the list to
     * @exception  java.io.IOException  if an error occured during writing
     *                                  to the file
     */
    public static void writeLinks(List list, FileObject fo) throws IOException {
        BufferedWriter bw = null;
        FileLock lock = null;
        try {
            lock = fo.lock();
            bw = new BufferedWriter(new OutputStreamWriter(
                    fo.getOutputStream(lock)));
            for (Iterator i = list.iterator(); i.hasNext(); ) {
                String line = (String) i.next();
                File f = new File(line);
                FileObject link = FileUtil.toFileObject(f);
                /* try to convert into a relative file */
                if (f.isAbsolute()) {
                    FileObject parent = fo.getParent();
                    /* this is simple if this node's parent is the parent of the file */
                    if (FileUtil.isParentOf(parent, link)) {
                        line = FileUtil.getRelativePath(parent, link);
                    } else {
                        /* otherwise check if this node and the file have the same root */
                        FileObject root = fo.getFileSystem().getRoot();
                        if (root.equals(link.getFileSystem().getRoot())) {
                            /* step up the parents of this node */
                            StringBuffer lineBuf = new StringBuffer(80);
                            while (!root.equals(parent)) {
                                parent = parent.getParent();
                                /* prefix "../" for every step */
                                lineBuf.append("../");                  //NOI18N
                                /* break if a parent of the file is found is found */
                                if (FileUtil.isParentOf(parent, link)) {
                                    lineBuf.append(FileUtil.getRelativePath(parent, link));
                                    line = lineBuf.toString();
                                    break;
                                }
                            }
                        }
                    }
                }
                bw.write(line); 
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
            }
        }
    }

    /**
     * Writes a given list (of file names) to this data object's primary file.
     *
     * @param  list  list of <code>String</code>s - file names
     * @exception  java.io.IOException  if an error occured during writing
     *                                  to the file
     */
    protected void writeLinks(List list) throws IOException {
        writeLinks(list, getPrimaryFile());
    }

    /**
     * Creates a string representation of a link to a given file.
     *
     * @param  fo  file object to get a link string for
     * @return  string representing a link to the given file
     */
    public static String getLinkName(FileObject fo) {
        return fo.getPath();
    }

    /**
     * Finds a <code>DataObject</code> for a given file name.
     * Lookup for a file having the specified file name is performed
     * in just one filesystem.
     * @param  filename  name of the file, relative to the filesystem it
     *                   pertains to
     * @param ref a reference file (look in the same filesystem as this)
     * @return  the found <code>DataObject</code>, or <code>null</code>
     *          if a file having the specified name was not found
     * @throws IOException if there was a problem looking for the file
     */
    static DataObject getDataObjectByName(String filename, FileObject ref)
            throws IOException {
        FileObject file = ref.getFileSystem().findResource(filename);
        return (file != null) ? DataObject.find(file) : null;
    }

    /* interface DataObject.Container */
    /**
     * Returns <code>DataObject</code>s contained in this group.
     * Broken links of the group are ignored.
     *
     * @return  array of <code>DataObject</code>s contained in this group
     */
    public DataObject[] getChildren() {
        Object[] links = getLinks();
        
        if (links.length == 0) {
            return new DataObject[0];
        }
        
        int childrenCount = 0;
        DataObject[] children = new DataObject[links.length];
        for (int i = 0; i < links.length; i++) {
            Object link = links[i];
            if (link.getClass() == String.class) {                 //broken link
                continue;
            }
            children[childrenCount++] = (DataObject) link;
        }
        
        /* If there was at least one broken link, shrink the resulting array: */
        if (childrenCount != links.length) {
            DataObject[] oldChildren = children;
            children = new DataObject[childrenCount];
            System.arraycopy(oldChildren, 0, children, 0, childrenCount);
        }
        
        return children;
    }
    
    /**
     * Called when a change of this group's primary file is detected.
     *
     * Notifies all registered listeners about a change of property
     * {@link DataObject.Container#PROP_CHILDREN}.
     */
    void primaryFileChanged() {
        
        /* Implementation of interface DataObject.Container: */
        firePropertyChange(DataObject.Container.PROP_CHILDREN, null, null);
    }
    
    /**
     * Returns <code>DataObject</code>s contained in this group.
     * Reads contents of this group's primary file (names of nested
     * <code>DataObject</code>s' primary files) and asks for the corresponding
     * <code>DataObject</code>s. In case of broken links (names of non-existing
     * files), names of the files are returned instead of
     * <code>DataObject</code>s. Files that exist but there is no
     * <code>DataObject</code> for them, are silently ignored.
     * 
     * @return  array containing <code>DataObject</code>s
     *          and names of broken links
     */
    public Object[] getLinks() {
        List filenames;
        try {
            filenames = readLinks(getPrimaryFile());
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            return new Object[0];
        }
        
        Set set = new HashSet();
        for (Iterator it = filenames.iterator(); it.hasNext(); ) {
            String filename = (String) it.next();
            try {
                DataObject obj = getDataObjectByName(filename, getPrimaryFile());
                set.add(obj != null ? (Object) obj
                                    : (Object) new String(filename));
            } catch (IOException ex) {
                ex.printStackTrace();
                // can be thrown when the link is not recognized by any data loader
                // in this case I can't help so ignore it
            }
        }
        return set.toArray();
    }

    /**
     * Creates a derivation of a given name by replacing the name's prefix
     * with a new one. If the given name doesn't start with a given prefix,
     * no change is done and the original (instance of) string is returned.
     * If the given name does start with a given prefix, a new instance
     * of string is returned, even if the prefix replacement is equal to
     * the prefix to be replaced.
     *
     * @param  name  name whose derivation is to be created
     * @param  oldPrefix  prefix to be replaced
     * @param  newPrefix  replacement for the original prefix
     * @return  derivation of the original name;
     *          or the original name if it doesn't start with the given prefix
     */
    public static String createName(String name,
                                    String oldPrefix,
                                    String newPrefix) {
        if (name.startsWith(oldPrefix)) {
            return newPrefix + name.substring(oldPrefix.length());
        }
        return name;
    }

    /**
     * Replaces name according to naming pattern defined by property
     * {@link #PROP_TEMPLATE_PATTERN}, or falls to {@link #replaceName0()}
     * if value of the property is <code>null</code>.
     */
    private String replaceName(String name, String replacement) {
        String fmt = getTemplatePattern();
        
        if (!isUsePattern() || fmt == null) {
            return name.replaceAll("__.*?__", replacement);             //NOI18N
        }

        /* filter out all characters before "__" */
        int i = name.lastIndexOf("__");                                 //NOI18N
        String postfix = (i > 0) ? name.substring(i + 2) : "";          //NOI18N

        String subst = string3(name, replacement);
        return MessageFormat.format(
                fmt,
                new String[] {name, replacement, postfix, subst});
    }

    /**
     * Substitution wrapper for special cases.
     *
     * @returns String representing new name after substitution
     */
    private String string3(String name, String replacement) {

        String patch;
        if (name.startsWith("__")) {                                    //NOI18N
            patch = name;
        } else {
            patch = "__" + name;                                        //NOI18N
        }

        String s3 = substitute(patch, replacement);
        
        if (s3.startsWith("__")) {                                      //NOI18N
            s3 = s3.substring(2);
        }
        return s3;
    }

    /**
     * Global backward substitution of substrings matching pattern
     * &quot;<code>__.*?__</code>&quot;. The name is searched for the rightmost
     * occurence of a substring matching the pattern. If it is found,
     * the substring is replaced with a specified replacement and the same
     * process is performed on the result of the substitution, until no matching
     * substring is found.
     *
     * @param  name  string to process with a substitution
     * @param  replacement  string to put in place of matching substrings
     *                      of the name
     * @return  result of the substitutions
     */
    private String substitute(String name, String replacement) {
        int last;
        int lastButOne;
        StringBuffer sb = new StringBuffer(name);
        while ((last = sb.lastIndexOf("__")) >= 0                       //NOI18N
               && (lastButOne = sb.lastIndexOf("__", last - 2)) >= 0) { //NOI18N
            sb.replace(lastButOne, last + 2, replacement);
        }
        return sb.toString();
    }

    /** Implementation of handleCreateFromTemplate for GroupShadow.
     * All members of this group are called to create new objects. */
    protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {
        List createdObjs = createGroupFromTemplate(df, name, true);
        return createdObjs != null && createdObjs.size() > 0
               ? (DataObject) createdObjs.get(0)
               : this;
    }

    /**
     * Creates new objects from all members of this group.
     *
     * @returns  list of created objects
     */
    List createGroupFromTemplate(DataFolder folder,
                                         String name,
                                         boolean root) throws IOException {
        if (gsprocessed == null) {
            gsprocessed = this;
        } else {
            return null;
        }

        if (name == null) {// name is not specified
            name = FileUtil.findFreeFileName(folder.getPrimaryFile(),
                                             getPrimaryFile().getName(),
                                             GS_EXTENSION);
        }
        Object[] objs = getLinks();
        ArrayList createdObjs = new ArrayList(objs.length + 1);
        ArrayList linksList = new ArrayList(objs.length);

        try {
            for (int i = 0; i < objs.length; i++) {
                if (objs[i] instanceof DataObject) {
                    DataObject original = (DataObject) objs[i];

                    if (original instanceof GroupShadow) {
                        GroupShadow gs = (GroupShadow) original;
                        List items = gs.createGroupFromTemplate(folder, name, false);
                        if (items != null) {
                            for (int j = 0, n = items.size(); j < n; j++) {
                                DataObject obj = (DataObject) items.get(j);
                                createdObjs.add(obj);
                                linksList.add(getLinkName(obj.getPrimaryFile()));
                                if (j == 0
                                        && obj instanceof GroupShadow
                                        && gs.getTemplateAll())
                                    break;
                            }
//                            createdObjs.addAll(items);
                        }
                    } else {
                        String repName = replaceName(original.getName(), name);
                        DataObject newObj = original.createFromTemplate(folder, repName);
                        createdObjs.add(newObj);
                        linksList.add(getLinkName(newObj.getPrimaryFile()));
                    }
                }
            }

            if (objs.length == 0 || getTemplateAll()) { // create also the group
                String repName = root ? name : replaceName(getName(), name);
                FileObject fo = folder.getPrimaryFile().createData(repName, GS_EXTENSION);
                writeLinks(linksList, fo);
                GroupShadow gs = (GroupShadow) DataObject.find(fo);
                if (gs == null) {
                    gs = new GroupShadow(fo, getLoader());
                }
                createdObjs.add(0, gs);
            }
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (Error er) {
            er.printStackTrace();
            throw er;
        }
        finally {
            gsprocessed = null;
        }

        return createdObjs;
    }

    /**
     * Setter for showLinks property.
     *
     * @param show if true also show real packages and names of targets */
    public void setShowLinks(boolean show) {
        showLinks = show;
    }

    /** Getter for showLinks property. */
    public boolean getShowLinks() {
        return showLinks;
    }

    /**
     * Setter for template pattern.
     *
     * @exception IOException if error occured
     */
    public void setTemplatePattern(String templatePattern) throws IOException{
        final FileObject fo = getPrimaryFile();
        String old = getTemplatePattern();

        fo.setAttribute(PROP_TEMPLATE_PATTERN, templatePattern);

        if (old != templatePattern) {
            firePropertyChange(PROP_TEMPLATE_PATTERN, old, templatePattern);
        }

    }

    /** Getter for template pattern. */
    public String getTemplatePattern(){
        Object value = getPrimaryFile().getAttribute(GroupShadow.PROP_TEMPLATE_PATTERN);
        return (value instanceof String) ? (String) value
                                         : "{1}";                       //NOI18N
    }

    
    /** Getter for use pattern property. */
    public boolean isUsePattern() {
        Object o = getPrimaryFile().getAttribute(GroupShadow.PROP_USE_PATTERN);
        return Boolean.TRUE.equals(o);
    }

    
    /** Setter for use pattern property. */
    public void setUsePattern(boolean usePattern) throws IOException {
        FileObject fileObject = getPrimaryFile();
        boolean oldValue = isUsePattern();
        
        if (usePattern == oldValue) {
            return;
        }
        fileObject.setAttribute(PROP_USE_PATTERN, Boolean.valueOf(usePattern));
        
        firePropertyChange(PROP_USE_PATTERN, Boolean.valueOf(oldValue),
                                             Boolean.valueOf(usePattern));
    }
    
    /** Getter for template all. */
    public boolean getTemplateAll() {
        Object o = getPrimaryFile().getAttribute(GroupShadow.PROP_TEMPLATE_ALL);
        return Boolean.TRUE.equals(o);
    }


    /** Setter for template all. */
    public void setTemplateAll(boolean templateAll) throws IOException {
        final FileObject fo = getPrimaryFile();
        boolean oldtempl = getTemplateAll();

        fo.setAttribute(PROP_TEMPLATE_ALL, (templateAll ? Boolean.TRUE : null));

        if (oldtempl != templateAll) {
            firePropertyChange(PROP_TEMPLATE_ALL, Boolean.valueOf(oldtempl),
                                                  Boolean.valueOf(templateAll));
        }
    }

    /** Getter for resources */
    static String getLocalizedString (String s) {
        return NbBundle.getBundle (GroupShadow.class).getString (s);
    }

}
