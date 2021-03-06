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

package org.netbeans.modules.tasklist.checkstyle.settings;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JFileChooser;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author  S. Aubrecht
 */
public class CheckStyleCustomizer extends javax.swing.JPanel {
    
    private boolean isChanged = false;
    
    /** Creates new form CheckStyleCustomizer */
    public CheckStyleCustomizer() {
        initComponents();
    }

    void applyChanges() {
        if( !isValid() )
            return;
        String currentFileName = txtFileName.getText();
        if( null == currentFileName || currentFileName.length() == 0 )
            return;
        try {
            URL url = new URL( currentFileName );
            Settings.getDefault().setConfigurationUrl( url.toExternalForm() );
            isChanged = false;
        } catch( MalformedURLException ex ) {
            //ignore
        }
    }

    boolean isChanged() {
        return isChanged;
    }

    boolean isDataValid() {
        String currentFileName = txtFileName.getText();
        if( null == currentFileName || currentFileName.length() == 0 )
            return false;
        try {
            URL url = new URL( currentFileName );
            File f = new File( url.toURI() );
            return f.exists();
        } catch( MalformedURLException ex ) {
            //ignore
        } catch( URISyntaxException ex ) {
            //ignore
        }
        return false;
    }

    void update() {
        txtFileName.setText( Settings.getDefault().getConfigurationUrl() );
        isChanged = false;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFileName = new javax.swing.JLabel();
        txtFileName = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();

        lblFileName.setLabelFor(txtFileName);
        lblFileName.setText(org.openide.util.NbBundle.getMessage(CheckStyleCustomizer.class, "CheckStyleCustomizer.lblFileName.text")); // NOI18N

        txtFileName.setText(org.openide.util.NbBundle.getMessage(CheckStyleCustomizer.class, "CheckStyleCustomizer.txtFileName.text")); // NOI18N

        btnBrowse.setText(org.openide.util.NbBundle.getMessage(CheckStyleCustomizer.class, "CheckStyleCustomizer.btnBrowse.text")); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browse(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lblFileName)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(txtFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnBrowse))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(lblFileName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnBrowse)
                    .add(txtFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void browse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browse
    String currentFileName = txtFileName.getText();
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle( NbBundle.getMessage( CheckStyleCustomizer.class, "BrowseDialogTitle" ) ); //NOI18N
    chooser.setDialogType( JFileChooser.OPEN_DIALOG );
    chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
    chooser.setMultiSelectionEnabled( false );
    chooser.setSelectedFile( new File(currentFileName) );
    int ret = chooser.showOpenDialog( WindowManager.getDefault().getMainWindow() );
    if( ret != JFileChooser.APPROVE_OPTION ) {
        return;
    }
    File selFile = chooser.getSelectedFile();
    if( null == selFile )
        return;
    if( !selFile.exists() ) {
        showErrorMessage();
        return;
    }
    try {
        txtFileName.setText( selFile.toURL().toExternalForm() );
        isChanged = true;
    } catch( MalformedURLException ex ) {
        showErrorMessage();
    }
}//GEN-LAST:event_browse
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JTextField txtFileName;
    // End of variables declaration//GEN-END:variables
    
    private void showErrorMessage() {
        NotifyDescriptor nd = new NotifyDescriptor( 
                NbBundle.getMessage( CheckStyleCustomizer.class, "Msg_SelectExistingFile" ), //NOI18N
                NbBundle.getMessage( CheckStyleCustomizer.class, "Msg_Title" ), //NOI18N
                NotifyDescriptor.DEFAULT_OPTION,
                NotifyDescriptor.ERROR_MESSAGE,
                new Object[] { NotifyDescriptor.OK_OPTION },
                NotifyDescriptor.OK_OPTION
                );
        DialogDisplayer.getDefault().notify( nd );
    }
}
