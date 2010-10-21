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
package org.netbeans.modules.licensechanger;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.licensechanger.FileChildren.FileItem;
import org.netbeans.modules.licensechanger.api.FileHandler;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.Utilities;

/**
 *
 * @author Tim Boudreau
 */
public class PreviewPanel extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener, NodeCheckObserver {

    private final ExplorerManager mgr = new ExplorerManager();
    private ItemLoader loader;
    private String licenseText;
    private Map<Object, Object> settings;
    public static final String KEY_ITEMS = "fileItems";

    public PreviewPanel(Map<Object, Object> settings) {
        this.settings = settings;
        Set<FileObject> folders = (Set<FileObject>) settings.get(SelectFoldersPanel.KEY_FOLDERS);
        this.licenseText = (String) settings.get(LicenseChooserPanel.KEY_LICENSE_TEXT);
        initComponents();
        Children kids = Children.create(new FileChildren(folders, settings), true);
        mgr.setRootContext(new AbstractNode(kids));
        mgr.addPropertyChangeListener(this);
        view().setNodeCheckObserver(this);
        jLabel3.setText ("  ");
    }

    private void updateItems() {
        Set<FileItem> s = new HashSet<FileItem>();
        for (Node n : mgr.getRootContext().getChildren().getNodes(true)) {
            if (Boolean.TRUE.equals(n.getValue(CheckboxListView.SELECTED))) {
                s.addAll(n.getLookup().lookupAll(FileItem.class));
            }
        }
        settings.put(KEY_ITEMS, s);
    }

    private CheckboxListView view() {
        return (CheckboxListView) fileList;
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        fileList = new CheckboxListView();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        before = new javax.swing.JEditorPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        after = new javax.swing.JEditorPane();
        jLabel3 = new javax.swing.JLabel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);

        fileList.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jSplitPane1.setTopComponent(fileList);

        jSplitPane2.setDividerLocation(295);
        jSplitPane2.setResizeWeight(0.5);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.jLabel1.text")); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));

        before.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(before);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addContainerGap(252, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.jLabel2.text")); // NOI18N

        jScrollPane2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));
        jScrollPane2.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        after.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setViewportView(after);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addContainerGap(205, Short.MAX_VALUE))
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
        );

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setRightComponent(jSplitPane2);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.jLabel3.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane after;
    private javax.swing.JEditorPane before;
    private javax.swing.JScrollPane fileList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    // End of variables declaration//GEN-END:variables

    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] n = mgr.getSelectedNodes();
            if (n.length > 0) {
                FileItem item = n[0].getLookup().lookup(FileItem.class);
                if (item != null) {
                    setFileItem(item);
                    jLabel3.setText (item.file.getPath());
                } else {
                    before.setText("");
                    jLabel3.setText ("  ");
                }
            } else {
                before.setText("");
                after.setText("");
                jLabel3.setText("  ");
            }
        }
    }

    private void setFileItem(FileItem item) {
        if (item.file.isValid()) {
            before.setContentType("text/plain");
            after.setContentType("text/plain");
            before.setText("Loading " + item.file.getPath());
            after.setText("Loading " + item.file.getPath());
            ItemLoader ldr = new ItemLoader(item);
            setLoader(ldr);
        }
    }

    private synchronized void setLoader(ItemLoader ldr) {
        if (loader != null) {
            loader.cancel();
        }
        this.loader = ldr;
        loader.start();
    }

    public void onNodeChecked(Node node) {
        updateItems();
    }

    public void onNodeUnchecked(Node node) {
        updateItems();
    }

    static String loadFile(FileObject file) throws IOException {
        Charset encoding = FileEncodingQuery.getEncoding(file);
        InputStream in = new BufferedInputStream(file.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) file.getSize());
        try {
            FileUtil.copy(in, out);
            try {
                String result = new String(out.toByteArray(), encoding.name());
                String sep = System.getProperty ("line.separator");
                //Convert everything internally to use \n
                if (!"\n".equals(sep) && sep != null) {
                    return Utilities.replaceString(result, sep, "\n");
                } else {
                    return result;
                }
            } catch (UnsupportedEncodingException q) {
                return new String(out.toByteArray(), FileEncodingQuery.getDefaultEncoding().name());
            }
        } finally {
            in.close();
            out.close();
        }
    }

    private class ItemLoader implements Runnable {

        private final Task task;
        private volatile boolean cancelled;
        private final FileItem item;
        private volatile String beforeText = "Cancelled";
        private volatile String afterText = "Cancelled";

        public ItemLoader(FileItem item) {
            this.item = item;
            task = RequestProcessor.getDefault().create(this);
        }

        void start() {
            task.schedule(200);
        }

        public void cancel() {
            cancelled = true;
            task.cancel();
        }

        private void loadFile() throws IOException {
            beforeText = PreviewPanel.loadFile(item.file);
        }

        public void run() {
            if (cancelled) {
                return;
            }
            if (!EventQueue.isDispatchThread()) {
                try {
                    if (cancelled) {
                        return;
                    }
                    if (!item.file.isValid()) {
                        beforeText = "Invalid file";
                        afterText = "Invalid file";
                        return;
                    }
                    if (!item.file.canRead()) {
                        beforeText = "Cannot read " + item.file.getPath();
                        afterText = "Cannot read " + item.file.getPath();
                        return;
                    }
                    if (item.file.getSize() >= Integer.MAX_VALUE) {
                        beforeText = "File too long: " + item.file.getPath();
                        afterText = "File too long: " + item.file.getPath();
                        return;
                    }
                    if (item.file.getSize() == 0) {
                        beforeText = "Empty file";
                        afterText = "Empty file";
                        return;
                    }
                    if (cancelled) {
                        return;
                    }
                    try {
                        loadFile();
                    } catch (IOException ex) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        PrintWriter w = new PrintWriter(out);
                        ex.printStackTrace(w);
                        beforeText = new String(out.toByteArray());
                        afterText = "Error";
                        return;
                    }
                    if (cancelled) {
                        return;
                    }
                    if (beforeText.length() == 0) {
                        afterText = "";
                        return;
                    }
                    afterText = transform(beforeText, item.handler);
                } finally {
                    if (!cancelled) {
                        EventQueue.invokeLater(this);
                    }
                }
            } else {
                String contentType = item.file.getMIMEType();
                before.setContentType(contentType);
                after.setContentType(contentType);
                before.setText(beforeText);
                after.setText(afterText);
            }
        }

        private String transform(String beforeText, FileHandler handler) {
            return handler.transform(beforeText, licenseText);
        }
    }
}
