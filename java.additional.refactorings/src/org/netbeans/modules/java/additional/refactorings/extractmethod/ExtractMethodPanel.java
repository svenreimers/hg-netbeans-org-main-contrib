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
package org.netbeans.modules.java.additional.refactorings.extractmethod;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.openide.util.Utilities;

/**
 *
 * @author  Tim Boudreau
 */ 
public class ExtractMethodPanel extends javax.swing.JPanel implements CustomRefactoringPanel, DocumentListener, FocusListener {
    
    ExtractMethodUI ui;
    /** Creates new form ExtractMethodPanel */
    public ExtractMethodPanel(ExtractMethodUI ui) {
        this.ui = ui;
        initComponents();
        jTextField1.getDocument().addDocumentListener(this);
        jTextField1.addFocusListener(this);
    }
    
    public String getMethodName() {
        return jTextField1.getText();
    }
    
    public void addNotify() {
        super.addNotify();
        ui.change();
    }
        
    void setProblemText (String s) {
        boolean hadText = getProblemText() != null;
        String txt = s == null ? "   " : s;
        problemLabel.setText(txt);
        boolean hasText = getProblemText() != null;
        if (hadText != hasText) {
            ui.change();
        }
    }
    
    String getProblemText() {
        return problemLabel.getText().trim().length() == 0 ? null :
            problemLabel.getText();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        publicButton = new javax.swing.JRadioButton();
        protectedButton = new javax.swing.JRadioButton();
        packageButton = new javax.swing.JRadioButton();
        privateButton = new javax.swing.JRadioButton();
        finalBox = new javax.swing.JCheckBox();
        problemLabel = new javax.swing.JLabel();

        jLabel1.setLabelFor(jTextField1);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jLabel1.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jTextField1.text")); // NOI18N

        buttonGroup1.add(publicButton);
        publicButton.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jRadioButton1.text")); // NOI18N
        publicButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(protectedButton);
        protectedButton.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jRadioButton2.text")); // NOI18N
        protectedButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(packageButton);
        packageButton.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jRadioButton3.text")); // NOI18N
        packageButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(privateButton);
        privateButton.setSelected(true);
        privateButton.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jRadioButton4.text")); // NOI18N
        privateButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        finalBox.setSelected(true);
        finalBox.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jCheckBox1.text")); // NOI18N
        finalBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        problemLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        problemLabel.setText(org.openide.util.NbBundle.getMessage(ExtractMethodPanel.class, "jLabel2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(problemLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(finalBox)
                            .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(privateButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(packageButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(protectedButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(publicButton)
                                .add(49, 49, 49)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(privateButton)
                    .add(packageButton)
                    .add(protectedButton)
                    .add(publicButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(finalBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(problemLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void initialize() {
        ui.change();
    }
    
    public void requestFocus() {
        jTextField1.requestFocus();
    }

    public Component getComponent() {
        return this;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox finalBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JRadioButton packageButton;
    private javax.swing.JRadioButton privateButton;
    private javax.swing.JLabel problemLabel;
    private javax.swing.JRadioButton protectedButton;
    private javax.swing.JRadioButton publicButton;
    // End of variables declaration//GEN-END:variables
    
    
    private void change() {
        String s = getMethodName();
        if (!Utilities.isJavaIdentifier(s)) {
            setProblemText ("'" + s + "' is not a valid Java identifier");
        } else {
            setProblemText("  ");            
        }
        ui.change();
    }

    public void insertUpdate(DocumentEvent e) {
        change();
    }

    public void removeUpdate(DocumentEvent e) {
        change();
    }

    public void changedUpdate(DocumentEvent e) {
        change();
    }

    public void focusGained(FocusEvent e) {
        jTextField1.selectAll();
        change();
    }
    
    public Set <Modifier> getModifiers() {
        HashSet<Modifier> result = new HashSet <Modifier> ();
        if (finalBox.isSelected())
            result.add (Modifier.FINAL);
        if (privateButton.isSelected())
            result.add (Modifier.PRIVATE);
        if (publicButton.isSelected()) 
            result.add (Modifier.PUBLIC);
        if (protectedButton.isSelected())
            result.add (Modifier.PROTECTED);
        return result;
    }

    public void focusLost(FocusEvent e) {
        //do nothing
    }
}
