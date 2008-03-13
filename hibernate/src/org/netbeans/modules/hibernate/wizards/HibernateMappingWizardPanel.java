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

/*
 * HibernateMappingWizardPanel.java
 *
 * Created on February 5, 2008, 12:23 PM
 */
package org.netbeans.modules.hibernate.wizards;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;
import org.netbeans.api.java.source.ui.TypeElementFinder;
import javax.swing.SwingUtilities;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  gowri
 */
public class HibernateMappingWizardPanel extends javax.swing.JPanel {

    private Project project;
    ArrayList<FileObject> configFileObjects;

    /** Creates new form HibernateMappingWizardPanel */
    public HibernateMappingWizardPanel(Project project) {
        this.project = project;
        initComponents();
        String[] configFiles = getConfigFilesFromProject(project);
        this.cmbResource.setModel(new DefaultComboBoxModel(configFiles));

        this.browseButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }                
        });
    }
    
    private void browseButtonActionPerformed(ActionEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final ElementHandle<TypeElement> handle = TypeElementFinder.find(null, new TypeElementFinder.Customizer() {

                            public Set<ElementHandle<TypeElement>> query(ClasspathInfo classpathInfo, String textForQuery, NameKind nameKind, Set<SearchScope> searchScopes) {
                                return classpathInfo.getClassIndex().getDeclaredTypes(textForQuery, nameKind, searchScopes);
                            }

                            public boolean accept(ElementHandle<TypeElement> typeHandle) {
                                return true;
                            }
                        });

                if (handle != null) {
                    txtClassName.setText(handle.getQualifiedName());
                }
            }
        });
        
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "LBL_HibernateMappingPanel_Name");
    }

    public String getClassName() {
        if (txtClassName.getText() != null) {
            return txtClassName.getText().trim();
        } 
        return null;
    }

    public FileObject getConfigurationFile() {
        if (cmbResource.getSelectedIndex() != -1) {
            return configFileObjects.get(cmbResource.getSelectedIndex());
        }
        return null;
    }

    // Gets the list of Config files from HibernateEnvironment.
    public String[] getConfigFilesFromProject(Project project) {
        ArrayList<String> configFiles = new ArrayList<String>();
        org.netbeans.api.project.Project enclosingProject = project;
        org.netbeans.modules.hibernate.service.HibernateEnvironment env = enclosingProject.getLookup().lookup(org.netbeans.modules.hibernate.service.HibernateEnvironment.class);
        configFileObjects = env.getAllHibernateConfigFileObjects();
        for (FileObject fo : configFileObjects) {
            configFiles.add(fo.getNameExt());
        }
        return configFiles.toArray(new String[]{});
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtClassName = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbResource = new javax.swing.JComboBox();

        setName(org.openide.util.NbBundle.getMessage(HibernateMappingWizardPanel.class, "LBL_HibernateMappingPanel_Name")); // NOI18N

        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("Map_mnemonic").charAt(0));
        jLabel1.setLabelFor(txtClassName);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(HibernateMappingWizardPanel.class, "HibernateMappingWizardPanel.jLabel1.text")); // NOI18N

        txtClassName.setColumns(30);
        txtClassName.setText(org.openide.util.NbBundle.getMessage(HibernateMappingWizardPanel.class, "HibernateMappingWizardPanel.txtClassName.text")); // NOI18N

        browseButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("Browse_mnemonic").charAt(0));
        browseButton.setText(org.openide.util.NbBundle.getMessage(HibernateMappingWizardPanel.class, "HibernateMappingWizardPanel.browseButton.text")); // NOI18N

        jLabel2.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/hibernate/wizards/Bundle").getString("Configuration_mnemonic").charAt(0));
        jLabel2.setLabelFor(cmbResource);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(HibernateMappingWizardPanel.class, "HibernateMappingWizardPanel.jLabel2.text")); // NOI18N

        cmbResource.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(txtClassName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton))
                    .add(cmbResource, 0, 417, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(browseButton)
                    .add(txtClassName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cmbResource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JComboBox cmbResource;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtClassName;
    // End of variables declaration//GEN-END:variables
}
