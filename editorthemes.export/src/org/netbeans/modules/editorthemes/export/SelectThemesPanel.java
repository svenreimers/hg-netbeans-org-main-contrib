/*
 * SelectThemesPanel.java
 *
 * Created on July 10, 2007, 2:32 PM
 */

package org.netbeans.modules.editorthemes.export;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.NbBundle;

/**
 *
 * @author  Tim Boudreau
 */
public class SelectThemesPanel extends javax.swing.JPanel implements DocumentListener {
    
    /** Creates new form SelectThemesPanel */
    public SelectThemesPanel(Iterable<String> themes) {
        initComponents();
        for (String theme : themes) {
            JCheckBox box = new JCheckBox(theme);
            jPanel1.add(box);
        }
        Component[] comps = getComponents();
        for (Component cc : comps) {
            if (cc instanceof JTextField) {
                JTextField jtf = (JTextField) cc;
                jtf.getDocument().addDocumentListener(this);
            }
        }
        
    }
    
    public Collection <String> getSelectedThemes() {
        Component[] cc = jPanel1.getComponents();
        List <String> result = new ArrayList <String> (cc.length );
        for (Component comp : cc) {
            JCheckBox box = (JCheckBox) comp;
            if (box.isSelected()) {
                result.add (box.getText());
            }
        }
        return result;
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        codeNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        authorField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        displayNameField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        versionField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        outfileField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SelectThemesPanel.class, "SelectThemesPanel.jLabel1.text")); // NOI18N

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel1);

        jLabel2.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.jLabel2.text")); // NOI18N

        codeNameField.setText(createRandomPackageName());
        codeNameField.setToolTipText(NbBundle.getMessage(getClass(), "SelectThemesPanel.codeNameField.toolTipText")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.jLabel3.text")); // NOI18N

        authorField.setText(System.getProperty("user.name")==null ? "Your Name" : System.getProperty("user.name").toString());

        jLabel4.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.jLabel4.text")); // NOI18N

        displayNameField.setText(System.getProperty("user.name") == null ? "Somebody's Themes" : System.getProperty ("user.name").toString() + "'s Themes");

        jLabel5.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.jLabel5.text")); // NOI18N

        versionField.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.versionField.text")); // NOI18N

        jLabel6.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.jLabel6.text")); // NOI18N

        outfileField.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.outfileField.text")); // NOI18N

        jButton1.setText(NbBundle.getMessage(getClass(), "SelectThemesPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3)
                            .add(jLabel6)
                            .add(jLabel5)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, authorField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, codeNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, displayNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, versionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(outfileField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton1)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(26, 26, 26)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(codeNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(authorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(displayNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(versionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outfileField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    JFileChooser jfc = null;
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (jfc == null) {
            jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            outfileField.setText(jfc.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    
    public String getAuthor() {
        return authorField.getText();
    }
    
    public String getCodeName() {
        return codeNameField.getText();
    }
    
    public File getOutfile() {
        return new File (outfileField.getText());
    }
    
    public String getDisplayName() {
        return displayNameField.getText();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField authorField;
    private javax.swing.JTextField codeNameField;
    private javax.swing.JTextField displayNameField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField outfileField;
    private javax.swing.JTextField versionField;
    // End of variables declaration//GEN-END:variables

    private static String createRandomPackageName() {
        Random r = new Random(System.currentTimeMillis());
        StringBuilder res = new StringBuilder();
        three (res, r);
        res.append ('.');
        three (res, r);
        res.append ('.');
        three (res, r);
        res.append ('.');
        res.append ("themes");
        return res.toString();
    }
    
    public String getVersion() {
        return versionField.getText();
    }
    
    private static void three(StringBuilder sb, Random r) {
        sb.append (randomChar(r)).append(randomChar(r)).append(randomChar(r));
    }
    
    private static char randomChar(Random r) {
        int val = 'a' + r.nextInt('z' - 'a');
        return (char) val;
    }
    
    boolean isValidData() {
        boolean result = true;
        Component[] comps = getComponents();
        for (Component cc : comps) {
            if (cc instanceof JTextField) {
                JTextField jtf = (JTextField) cc;
                String s = jtf.getText().trim();
                if (s.length() == 0) {
                    result = false;
                    break;
                }
            }
            String s = getVersion();
            try {
                Float.parseFloat(s);
            } catch (NumberFormatException e) {
                result = false;
            }
        }
        return result;
    }
    
    ChangeListener c;
    void addChangeListener (ChangeListener c) {
        this.c = c;
    }
    
    private void change() {
        if (c != null) {
            c.stateChanged(new ChangeEvent(this));
        }
    }

    public void insertUpdate(DocumentEvent arg0) {
        change();
    }

    public void removeUpdate(DocumentEvent arg0) {
        change();
    }

    public void changedUpdate(DocumentEvent arg0) {
        change();
    }
}
