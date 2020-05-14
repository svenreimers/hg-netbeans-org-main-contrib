/*
 * CustomizerRun.java
 *
 * Created on August 22, 2008, 4:52 PM
 */

package org.netbeans.modules.python.project2.ui.customizer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.python.project2.PythonProject2;
import org.netbeans.modules.python.project2.ui.Utils;

/**
 *
 */
public class CustomizerRun extends javax.swing.JPanel {

    private final PythonProject2 project;
    private final DocListener listener;

    /** Creates new form CustomizerRun */
    public CustomizerRun(final PythonProject2 project) {
        assert project != null;
        this.project = project;
        initComponents();
        String mainModule = project.getMainModule();
        if (mainModule != null) {
            this.mainModule.setText(mainModule);
        }
        String appArgs = project.getApplicationArgs();
        if (appArgs != null) {
            this.appArgs.setText(appArgs);
        }
        this.listener = new DocListener ();
        this.mainModule.getDocument().addDocumentListener(listener);
        this.appArgs.getDocument().addDocumentListener(listener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        mainModule = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        appArgs = new javax.swing.JTextField();

        jLabel1.setLabelFor(mainModule);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "CustomizerRun.mainModule.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "CustomizerRun.browseMain.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setLabelFor(appArgs);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "CustomizerRun.appArgs.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainModule, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addComponent(appArgs, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(mainModule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(appArgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(234, Short.MAX_VALUE))
        );

        mainModule.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "CustomizerRun.mainModule.ad")); // NOI18N
        jButton1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "CustomizerRun.browseMain.ad")); // NOI18N
        appArgs.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "CustomizerRun.appArgs.ad")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    String main = Utils.chooseMainModule(project);
    if (main != null) {
        mainModule.setText(main);
    }
}//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField appArgs;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField mainModule;
    // End of variables declaration//GEN-END:variables


    private class DocListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleDocEvent (e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleDocEvent(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            handleDocEvent(e);
        }

        private void handleDocEvent (final DocumentEvent e) {
            final Document doc = e.getDocument();
            if (doc == mainModule.getDocument()) {
                //System.out.println("Updating main Module to " + mainModule.getText() );

                project.setMainModule(mainModule.getText());
            }
            else if (doc == appArgs.getDocument()) {
                project.setApplicationArgs(appArgs.getText());
            }
        }

    }
}
