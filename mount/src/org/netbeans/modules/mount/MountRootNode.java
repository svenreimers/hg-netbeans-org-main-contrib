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

package org.netbeans.modules.mount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.queries.VisibilityQuery;
import org.openide.ErrorManager;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.RenameAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;


/**
 * Shows mounts.
 * @author Jesse Glick
 */
final class MountRootNode extends AbstractNode {
    
    public MountRootNode() {
        super(new MountChildren());
        setIconBaseWithExtension("org/netbeans/modules/mount/mount.gif");
        // XXX add Index cookie so user can reorder mounts
        // XXX add DummyProject.instance to its lookup, just for fun
    }

    public @Override String getName() {
        return "mount"; // NOI18N
    }

    public @Override String getDisplayName() {
        return "Filesystems";
    }

    public @Override boolean canRename() {
        return false;
    }

    public @Override boolean canDestroy() {
        return false;
    }

    public @Override boolean canCut() {
        return false;
    }

    public @Override boolean canCopy() {
        return false;
    }
    
    public @Override Action[] getActions(boolean context) {
        return new Action[] {
            new AddMountAction(false),
            new AddMountAction(true),
            // XXX add various other context actions to build etc.
        };
    }

    public @Override Action getPreferredAction() {
        return null;
    }

    public @Override Node.Handle getHandle() {
        return new MountHandle();
    }
    
    private static final class MountHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        public MountHandle() {}
        public @Override Node getNode() throws IOException {
            return new MountRootNode();
        }
    }

    public @Override Node cloneNode() {
        return new MountRootNode();
    }
    
    /**
     * Holds a list of mount roots and creates one node for each.
     */
    private static final class MountChildren extends Children.Keys<FileObject> implements ChangeListener {
        
        public MountChildren() {}

        protected @Override Node[] createNodes(FileObject fo) {
            DataFolder folder = DataFolder.findFolder(fo);
            Children ch = folder.createNodeChildren(MountFilter.DEFAULT);
            return new Node[] {new MountNode(folder.new FolderNode(ch))};
        }
        
        private void refreshKeys() {
            setKeys(MountList.DEFAULT.getMounts());
        }

        protected @Override void addNotify() {
            super.addNotify();
            MountList.DEFAULT.addChangeListener(this);
            refreshKeys();
        }

        protected @Override void removeNotify() {
            MountList.DEFAULT.removeChangeListener(this);
            super.removeNotify();
        }

        public @Override void stateChanged(ChangeEvent e) {
            refreshKeys();
        }
        
    }
    
    /**
     * Represents the root node of one mount point.
     */
    private static final class MountNode extends FilterNode {
        
        public MountNode(Node orig) {
            super(orig);
        }
        
        private FileObject root() {
            return getLookup().lookup(DataObject.class).getPrimaryFile();
        }
        
        public @Override String getName() {
            try {
                return root().getURL().toExternalForm();
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);
                return ""; // NOI18N
            }
        }
        
        public @Override String getDisplayName() {
            return FileUtil.getFileDisplayName(root());
        }

        public @Override Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>(Arrays.asList(super.getActions(context)));
            actions.remove(SystemAction.get(CutAction.class));
            actions.remove(SystemAction.get(RenameAction.class));
            actions.remove(SystemAction.get(DeleteAction.class));
            actions.add(0, null);
            // XXX needs to use a singleton action which is a ContextAwareAction to support multiselections
            actions.add(0, new UnmountAction(root()));
            return actions.toArray(new Action[actions.size()]);
        }

        public @Override Action getPreferredAction() {
            return null;
        }

        public @Override boolean canRename() {
            return false;
        }

        public @Override boolean canDestroy() {
            return false;
        }

        public @Override boolean canCut() {
            return false;
        }

    }

    /**
     * Filter used to exclude *.class and specially masked files.
     */
    private static final class MountFilter implements ChangeableDataFilter, ChangeListener {
        
        public static final MountFilter DEFAULT = new MountFilter();
        
        private final ChangeSupport cs = new ChangeSupport(this);
        
        @SuppressWarnings("LeakingThisInConstructor")
        private MountFilter() {
            VisibilityQuery.getDefault().addChangeListener(WeakListeners.change(this, VisibilityQuery.getDefault()));
        }

        public @Override boolean acceptDataObject(DataObject obj) {
            FileObject fo = obj.getPrimaryFile();
            if (fo.hasExt("class") && FileUtil.toFile(fo) != null) { // NOI18N
                // Hide *.class on disk. Show them inside JARs.
                return false;
            }
            return VisibilityQuery.getDefault().isVisible(fo);
        }
        
        public @Override synchronized void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }
        
        public @Override synchronized void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }
        
        public @Override void stateChanged(ChangeEvent ev) {
            cs.fireChange();
        }
        
    }

}
