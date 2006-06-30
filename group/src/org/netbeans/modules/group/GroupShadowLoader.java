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


package org.netbeans.modules.group;


import org.openide.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


/**
 * Loader for <code>GroupShadow</code> data object.
 *
 * @author Jaroslav Tulach
 */
public class GroupShadowLoader extends DataLoader {

    /** generated serial version UID */
    static final long serialVersionUID =-2768192459953761627L;

    /**
     * extension of files representing groups.
     * This extension is initially the only item in the list of recognized
     * extensions.
     *
     * @see  #setExtensions
     */
    public static final String GS_EXTENSION = "group"; // NOI18N
    
    /**
     * list of extensions of group shadow files
     *
     * @see  #setExtensions
     */
    private ExtensionList extensions;

    
    /** Creates a new loader. */
    public GroupShadowLoader() {
        super("org.netbeans.modules.group.GroupShadow"); // NOI18N
        
        extensions = new ExtensionList();
        extensions.addExtension(GS_EXTENSION);
    }
    
    
    /** */
    protected String defaultDisplayName() {
        return NbBundle.getMessage(GroupShadowLoader.class,
                                   "PROP_GroupShadowName");             //NOI18N
    }
    
    /** */
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenLocalExplorerAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(SaveAsTemplateAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class)
        };
    }

    
    /**
     * @return  {@link GroupShadow} for the given <code>FileObject</code>;
     *          or <code>null</code> if the <code>FileObject</code>
     *          does not have the expected extension
     * @see  #GS_EXTENSION
     */
    protected DataObject handleFindDataObject(
            FileObject fo,
            DataLoader.RecognizedFiles recognized) throws java.io.IOException {
        if (getExtensions().isRegistered(fo)) {
            return new GroupShadow(fo, this);
        }
        return null;
    }

    /**
     * Returns a list of extensions of group shadow files.
     * Files having an extension among the specified extensions are
     * recognized as files representing group shadow files.
     *
     * @return  list of extensions of group shadow files
     */
    public ExtensionList getExtensions() {
        return extensions;
    }

    /**
     * Sets a list of extensions of group shadow files.
     * Files having an extension among the specified extensions will then be
     * recognized as files representing group shadow files.
     *
     * @param  extensions  new list of extensions
     */
    public void setExtensions(ExtensionList extensions) {
        this.extensions = extensions;
    }
}
