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

package org.netbeans.modules.refactoring.vcs;

import java.awt.Component;
import java.util.Collection;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Jan Becicka
 */
public class CheckoutPanel extends javax.swing.JPanel {

    /**
     * Creates new form CheckoutPanel
     *
     */
    public CheckoutPanel(Collection files) {
        initComponents();
        fileList.setListData(files.toArray());
        fileList.setCellRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(
                        JList list,
                        Object value,
                        int index,
                        boolean isSelected,
                        boolean cellHasFocus) 
                  {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText("<html>" + ((FileObject) value).getNameExt() + " <font color=#707070>(" + FileUtil.getFileDisplayName((FileObject) value) + ")</font></html>"); //NOI18N
                    return this;
                }
                
                
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        topLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        scrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout(0, 6));

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        org.openide.awt.Mnemonics.setLocalizedText(topLabel, org.openide.util.NbBundle.getMessage(CheckoutPanel.class, "LBL_FilesToUpdate"));
        add(topLabel, java.awt.BorderLayout.NORTH);

        add(progressBar, java.awt.BorderLayout.SOUTH);

        scrollPane.setViewportView(fileList);

        add(scrollPane, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList fileList;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel topLabel;
    // End of variables declaration//GEN-END:variables
    
}
