/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger.spi.wizard;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.modules.licensechanger.spi.wizard.utils.FolderChildren;
import org.netbeans.modules.licensechanger.spi.wizard.utils.WizardProperties;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.CheckableNode;
import org.openide.explorer.view.OutlineView;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tim Boudreau
 * @author Nils Hoffmann (Refactoring)
 */
public class SelectFoldersPanel extends javax.swing.JPanel implements ExplorerManager.Provider {
    
    private final ExplorerManager mgr = new ExplorerManager();
    
    public SelectFoldersPanel() {
        initComponents();
        updateView();
        setName("Select Folders");
    }
    
    private void updateFolders() {
        Set<FileObject> folders = getSelectedFolders();
//        System.out.println("Selected folders: " + folders);
        firePropertyChange(WizardProperties.KEY_FOLDERS, null, folders);
    }
    
    private void enableUI() {
        jLabel1.setEnabled(true);
        updateView();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new OutlineView();
        jLabel1 = new javax.swing.JLabel();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow")));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SelectFoldersPanel.class, "SelectFoldersPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }
    
    public void setRootFiles(Set<FileObject> roots) {
        AbstractNode root = new AbstractNode(Children.create(new FolderChildren(roots.toArray(new FileObject[roots.size()])) {
            @Override
            public void onAllNodesCreated() {
                enableUI();
                updateFolders();
            }
        }, true));
        mgr.setRootContext(root);
        firePropertyChange("rootFiles", null, roots);
    }
    
    public Set<FileObject> getSelectedFolders() {
        Set<FileObject> folders = new TreeSet<FileObject>(new Comparator<FileObject>() {
            @Override
            public int compare(FileObject t, FileObject t1) {
                return t.getPath().compareTo(t1.getPath());
            }
        });
        for (Node n : mgr.getRootContext().getChildren().getNodes(true)) {
            CheckableNode cn = n.getLookup().lookup(CheckableNode.class);
            if (cn != null && cn.isSelected()) {
                FileObject f = n.getLookup().lookup(FileObject.class);
                if (f != null) {
                    folders.add(f);
                }
            }
        }
        return folders;
    }
    
    private void updateView() {
        OutlineView ov = (OutlineView) jScrollPane1;
        ov.getOutline().setRootVisible(false);
        ov.setPopupAllowed(false);
        ov.setTreeSortable(false);
        String headerName = org.openide.util.NbBundle.getMessage(SelectFoldersPanel.class, "SelectFoldersPanel.nodesLabel.text"); // NOI18N
        ((DefaultOutlineModel) ov.getOutline().getOutlineModel()).setNodesColumnLabel(headerName);
        setName("Select Folders");
    }
}
