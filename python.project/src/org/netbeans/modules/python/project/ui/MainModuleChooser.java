/*
 * MainClassChooser.java
 *
 * Created on August 22, 2008, 6:07 PM
 */

package org.netbeans.modules.python.project.ui;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.netbeans.modules.python.api.PythonMIMEResolver;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 */
final class MainModuleChooser extends javax.swing.JPanel {
    
    private final FileObject[] roots;
    private final JButton okButton;
    private final static RequestProcessor RP = new RequestProcessor("MainModuleChooser");   //NOI18N
    
    /** Creates new form MainClassChooser */
    MainModuleChooser (final FileObject[] roots, final JButton okButton) {
        assert roots != null;
        assert okButton != null;
        this.roots = roots;        
        initComponents();
        this.okButton = okButton;
        this.okButton.setEnabled(false);
        ((DefaultListModel)this.mainModules.getModel()).addElement(NbBundle.getMessage(MainModuleChooser.class, "TXT_PleaseWait"));
        RP.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });        
    }
    
    public String getMainModule () {
        return (String) mainModules.getSelectedValue();
    }
    
    
    private void initData () {
        final List<String> data = new LinkedList<>();
        for (FileObject root : roots) {
            final Enumeration<? extends FileObject> fos = root.getChildren(true);
            while (fos.hasMoreElements()) {
                final FileObject fo = fos.nextElement();
                if (isPython (fo)) {
                    data.add(FileUtil.getRelativePath(root, fo));
                }
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultListModel lm = (DefaultListModel)mainModules.getModel();
                lm.clear();
                for (String s : data) {
                    lm.addElement(s);
                }
                okButton.setEnabled(true);
            }
        });        
    }
    
    private static boolean isPython (final FileObject fo) {
        if (fo.isFolder() || !fo.isValid() || fo.isVirtual()) {
            return false;
        }
        return PythonMIMEResolver.PYTHON_MIME_TYPE.equals(FileUtil.getMIMEType(fo));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainModules = new javax.swing.JList();

        setPreferredSize(new java.awt.Dimension(300, 300));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(mainModules);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MainModuleChooser.class, "MainModuleChooser.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel1, gridBagConstraints);

        mainModules.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(mainModules);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList mainModules;
    // End of variables declaration//GEN-END:variables

}
