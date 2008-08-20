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

package org.netbeans.modules.j2ee.jetty.customizer;

import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.jetty.ide.JetPluginProperties;

/**
 * One of three panels accessible in properties of installed server
 * @author  novakm
 */
public class JetCustomizerUserTab extends javax.swing.JPanel {
    private InstanceProperties ip;

    /** Creates new form JetCustomizerUserTab, initializase its
     * components and sets defaults values or changed values
     * read from instance properties
     */
    public JetCustomizerUserTab(InstanceProperties ip) {
        this.ip = ip;
        initComponents();
        setInitValues();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rmiPortLabel = new javax.swing.JLabel();
        rmiPortTextField = new javax.swing.JTextField();
        serverLocationLabel = new javax.swing.JLabel();
        serverLocationTextField = new javax.swing.JTextField();
        httpPortTextField = new javax.swing.JTextField();
        httpPortJabel = new javax.swing.JLabel();

        rmiPortLabel.setText(org.openide.util.NbBundle.getMessage(JetCustomizerUserTab.class, "JetCustomizerUserTab.rmiPortLabel.text")); // NOI18N

        rmiPortTextField.setText(org.openide.util.NbBundle.getMessage(JetCustomizerUserTab.class, "JetCustomizerUserTab.rmiPortTextField.text")); // NOI18N
        rmiPortTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmiPortTextFieldActionPerformed(evt);
            }
        });

        serverLocationLabel.setText(org.openide.util.NbBundle.getMessage(JetCustomizerUserTab.class, "JetCustomizerUserTab.serverLocationLabel.text")); // NOI18N

        serverLocationTextField.setEditable(false);
        serverLocationTextField.setText(org.openide.util.NbBundle.getMessage(JetCustomizerUserTab.class, "JetCustomizerUserTab.serverLocationTextField.text")); // NOI18N

        httpPortTextField.setEditable(false);
        httpPortTextField.setText(org.openide.util.NbBundle.getMessage(JetCustomizerUserTab.class, "JetCustomizerUserTab.httpPortTextField.text")); // NOI18N

        httpPortJabel.setText(org.openide.util.NbBundle.getMessage(JetCustomizerUserTab.class, "JetCustomizerUserTab.httpPortJabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(serverLocationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(serverLocationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(51, 51, 51)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(httpPortJabel)
                            .add(rmiPortLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, httpPortTextField)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, rmiPortTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serverLocationLabel)
                    .add(serverLocationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpPortTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpPortJabel))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rmiPortTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rmiPortLabel))
                .addContainerGap(87, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void rmiPortTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmiPortTextFieldActionPerformed
        updateRMIPort();
}//GEN-LAST:event_rmiPortTextFieldActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel httpPortJabel;
    private javax.swing.JTextField httpPortTextField;
    private javax.swing.JLabel rmiPortLabel;
    private javax.swing.JTextField rmiPortTextField;
    private javax.swing.JLabel serverLocationLabel;
    private javax.swing.JTextField serverLocationTextField;
    // End of variables declaration//GEN-END:variables
    private void setInitValues() {
		httpPortTextField.setText(""+JetPluginProperties.HTTP_PORT);
        rmiPortTextField.setText(ip.getProperty(JetPluginProperties.RMI_PORT_PROP));
        serverLocationTextField.setText(ip.getProperty(JetPluginProperties.PROPERTY_JET_HOME));
    }

    private void updateRMIPort() {
        try {
//			parse to ensure it is valid integer value
            int val = Integer.parseInt(rmiPortTextField.getText());
            ip.setProperty(JetPluginProperties.RMI_PORT_PROP, ""+val);
        } catch (NumberFormatException nfe) {
            rmiPortTextField.setText("" + ip.getProperty(JetPluginProperties.RMI_PORT_PROP));
        }
    }
}
