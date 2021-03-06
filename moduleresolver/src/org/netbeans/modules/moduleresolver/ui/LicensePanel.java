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

package org.netbeans.modules.moduleresolver.ui;

import org.netbeans.modules.moduleresolver.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.autoupdate.UpdateElement;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jiri Rechtacek
 */
public class LicensePanel extends javax.swing.JPanel {
    public static final String LICENSE_APPROVED = "license-approved";
    private Map<String, Set<String>> license4plugins;
    private Collection<UpdateElement> elements = null;
    
    /** Creates new form LicenseApprovalPanel */
    public LicensePanel () {
        initComponents ();
        rbDismis.setSelected (true);
        rbAccept.setEnabled (false);
        rbDismis.setEnabled (false);
        taLicenses.setEnabled (false);
    }
    
    public @Override String getName() {
        return NbBundle.getMessage (LicensePanel.class, "LicensePanel_Name");
    }

    public Collection<UpdateElement> getApprovedElements () {
        return elements;
    }
    
    void setElements (Collection<UpdateElement> elems) {
        if (elems != null) {
            elements = elements == null ? new HashSet<UpdateElement> () : elements;
            for (UpdateElement el : elems) {
                elements.addAll (FindBrokenModules.getMissingModules (el));
            }
        } else {
            elements = null;
        }
        if (elements != null) {
            writeLicenses (elements);
            rbAccept.setEnabled (true);
            rbDismis.setEnabled (true);
            taLicenses.setEnabled (true);
        } else {
            rbAccept.setEnabled (false);
            rbDismis.setEnabled (false);
            taLicenses.setEnabled (false);
        }
    }
    
    private void goOverLicenses (Collection<UpdateElement> elements) {
        for (UpdateElement el : elements) {
            if (el.getLicence () != null) {
                if (license4plugins == null) {
                    license4plugins = new HashMap<String, Set<String>> ();
                }
                if (license4plugins.containsKey (el.getLicence ())) {
                    // add plugin
                    license4plugins.get (el.getLicence ()).add (el.getDisplayName ());
                } else {
                    // license
                    Set<String> plugins = new HashSet<String> ();
                    plugins.add (el.getDisplayName ());
                    license4plugins.put (el.getLicence (), plugins);
                }
                //licenses.put (el.getDisplayName (), el.getLicence ());
            }
        }
    }
    
    public boolean isApproved () {
        return rbAccept.isSelected ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgApproveButtons = new javax.swing.ButtonGroup();
        taTitle = new javax.swing.JTextArea();
        rbAccept = new javax.swing.JRadioButton();
        rbDismis = new javax.swing.JRadioButton();
        spLicenses = new javax.swing.JScrollPane();
        taLicenses = new javax.swing.JTextArea();

        taTitle.setEditable(false);
        taTitle.setLineWrap(true);
        taTitle.setText(org.openide.util.NbBundle.getMessage(LicensePanel.class, "LicenseApprovalPanel_taTitle_Text")); // NOI18N
        taTitle.setWrapStyleWord(true);
        taTitle.setMargin(new java.awt.Insets(0, 4, 0, 0));
        taTitle.setOpaque(false);

        bgApproveButtons.add(rbAccept);
        org.openide.awt.Mnemonics.setLocalizedText(rbAccept, org.openide.util.NbBundle.getMessage(LicensePanel.class, "LicenseApprovalPanel_rbAccept_Text")); // NOI18N
        rbAccept.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAcceptActionPerformed(evt);
            }
        });

        bgApproveButtons.add(rbDismis);
        org.openide.awt.Mnemonics.setLocalizedText(rbDismis, org.openide.util.NbBundle.getMessage(LicensePanel.class, "LicenseApprovalPanel_rbDismis_Text")); // NOI18N
        rbDismis.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbDismis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDismisActionPerformed(evt);
            }
        });

        taLicenses.setColumns(20);
        taLicenses.setEditable(false);
        taLicenses.setLineWrap(true);
        taLicenses.setRows(5);
        taLicenses.setWrapStyleWord(true);
        taLicenses.setMargin(new java.awt.Insets(0, 4, 0, 4));
        spLicenses.setViewportView(taLicenses);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, rbDismis, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, rbAccept, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, taTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 460, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spLicenses, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(taTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spLicenses, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbAccept)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbDismis)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void rbDismisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDismisActionPerformed
    firePropertyChange (LICENSE_APPROVED, null, rbAccept.isSelected ());
}//GEN-LAST:event_rbDismisActionPerformed

private void rbAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAcceptActionPerformed
    firePropertyChange (LICENSE_APPROVED, null, rbAccept.isSelected ());
}//GEN-LAST:event_rbAcceptActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgApproveButtons;
    private javax.swing.JRadioButton rbAccept;
    private javax.swing.JRadioButton rbDismis;
    private javax.swing.JScrollPane spLicenses;
    private javax.swing.JTextArea taLicenses;
    private javax.swing.JTextArea taTitle;
    // End of variables declaration//GEN-END:variables
    
    private void writeLicenses (Collection<UpdateElement> elements) {
        goOverLicenses (elements);
        String content = "";
        if (license4plugins == null) {
            return ;
        }
        for (String lic : license4plugins.keySet ()) {
            String title = "";
            for (String plugin : license4plugins.get (lic)) {
                title += (title.length () == 0 ? "" :
                    NbBundle.getMessage (LicensePanel.class, "LicenseApprovalPanel_tpLicense_Delimeter")) + plugin; // NOI18N
            }
            content += NbBundle.getMessage (LicensePanel.class, "LicenseApprovalPanel_tpLicense_Head", title); // NOI18N
            content += "\n"; // NOI18N
            content += lic;
            content += NbBundle.getMessage (LicensePanel.class, "LicenseApprovalPanel_tpLicense_Separator"); // NOI18N
        }
        taLicenses.setText (content);
        taLicenses.setCaretPosition (0);
    }
}
