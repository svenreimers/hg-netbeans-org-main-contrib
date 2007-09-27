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

package org.netbeans.modules.corba.wizard.panels;

import java.util.List;
import java.net.URL;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.generator.ImplGenerator;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.wizard.CorbaWizardData;
import org.netbeans.modules.corba.wizard.panels.util.InterfaceListModel;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;

/**
 *
 * @author  Dusan Balek, Tomas Zezula
 */
public class RootInterface extends AbstractCORBAWizardPanel implements javax.swing.event.ListSelectionListener, javax.swing.event.DocumentListener {
    
    
    InterfaceListModel model;
    javax.swing.ImageIcon icon;
    
    /** Creates new form RootInterface */
    public RootInterface() {
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(3));
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_RootInterface"));
    }
    
    public void readCorbaSettings (CorbaWizardData data) {
        this.model = new InterfaceListModel ();

        IDLDataObject ido = data.getIdlSource();
        synchronized (ido) {
	    while (ido.getStatus () != IDLDataObject.STATUS_OK && ido.getStatus () != IDLDataObject.STATUS_ERROR) {
		try {
		    this.wait ();
		} catch (InterruptedException __ex) {
		}
	    }
        }
        if (ido.getStatus() == IDLDataObject.STATUS_OK) {
            IDLElement node = ido.getSources();
            List interfaces = ImplGenerator.get_all_interfaces (node);
            for (int i=0; i< interfaces.size(); i++) {
                String absoluteName = ImplGenerator.element2absolute_scope_name ((IDLElement)interfaces.get(i));
                this.model.add (absoluteName);
            }
        }
        else
            notifyParseException();
        
        this.initComponents();
        this.postInitComponents ();  
        String rootInterface = data.getRootInterface();
        if (rootInterface != null && rootInterface.length() > 0) {
            this.list.setSelectedValue (rootInterface, true);
        }
        else {
            this.list.setSelectedIndex(0);
        }
        if ((data.getGenerate () & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) {
            initDynamicComponents();
            this.cbList.setModel (model);
            this.cbList.addListSelectionListener (this);
            this.cbName.getDocument().addDocumentListener(this);
            String cbInterface = data.getCallBackInterface();
            if (cbInterface != null && cbInterface.length() > 0) {
                this.cbList.setSelectedValue (cbInterface, true);
            }
            else {
                this.cbList.setSelectedIndex(0);
            }
        }
    }
    
    public void storeCorbaSettings (CorbaWizardData data) {
        String rootInterface = this.name.getText();
        if (rootInterface == null || rootInterface.length() == 0)
            data.setRootInterface(null);
        else
            data.setRootInterface (rootInterface);
        this.list.removeListSelectionListener (this);
        this.name.getDocument().removeDocumentListener(this);
        if ((data.getGenerate () & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) {
            String cbInterface = this.cbName.getText();
            if (cbInterface == null || cbInterface.length() == 0)
                data.setCallBackInterface(null);
            else
                data.setCallBackInterface (cbInterface);
            this.cbList.removeListSelectionListener (this);
            this.cbName.getDocument().removeDocumentListener(this);
        }
        removeAll();
    }
    
    public boolean isValid () {
        String text = this.name.getText();
        return (text == null || text.length() == 0) ? false : true;
    }
    
    
    private void postInitComponents () {
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_InterfaceList_MNE").charAt(0));
        this.jLabel3.setDisplayedMnemonic (this.bundle.getString("TXT_SelectedRootInterface_MNE").charAt(0));
        this.list.getAccessibleContext().setAccessibleDescription("AD_InterfaceList");
        this.name.getAccessibleContext().setAccessibleDescription("AD_SelectedRootInterface");
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_RootInterfacePanel"));
        this.list.setModel (model);
        this.list.addListSelectionListener (this);
        this.name.getDocument().addDocumentListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        jScrollPane1.setBorder(new javax.swing.border.EtchedBorder());
        jScrollPane1.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        jLabel3.setText(bundle.getString("TXT_SelectedRootInterface"));
        jLabel3.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(name, gridBagConstraints);

        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_InterfaceList"));
        jLabel1.setLabelFor(list);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void initDynamicComponents() {
        cbScrollPane = new javax.swing.JScrollPane();
        cbList = new javax.swing.JList();
        cbLabel = new javax.swing.JLabel();
        cbLabel2 = new javax.swing.JLabel();
        cbName = new javax.swing.JTextField();
        
        cbLabel2.setText (this.bundle.getString("TXT_CallBackInterfaceList"));
        cbLabel2.setLabelFor (cbList);
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints ();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.insets = new java.awt.Insets (0,12,12,12);
        ((java.awt.GridBagLayout)this.getLayout()).setConstraints (cbLabel2, c);
        this.add (cbLabel2);
        
        cbScrollPane.setViewportView(cbList);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 12, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(cbScrollPane, gridBagConstraints1);
        
        cbLabel.setText(bundle.getString("TXT_SelectedCallBackInterface"));
        cbLabel.setLabelFor (cbName);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 12, 6);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(cbLabel, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 6, 12, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        add(cbName, gridBagConstraints1);
        
        cbLabel.setDisplayedMnemonic (this.bundle.getString("TXT_SelectedCallBackInterface_MNE").charAt(0));
        cbLabel2.setDisplayedMnemonic (this.bundle.getString("TXT_CallBackInterfaceList_MNE").charAt(0));
        cbList.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_CallBackInterfaceList"));
        cbName.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_SelectedCallBackInterface"));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList list;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField name;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JScrollPane cbScrollPane;
    private javax.swing.JList cbList;
    private javax.swing.JLabel cbLabel;
    private javax.swing.JLabel cbLabel2;
    private javax.swing.JTextField cbName;
    
    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");
    
    private void notifyParseException () {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getBundle(RootInterface.class).getString("TXT_ParseException"), NotifyDescriptor.Message.ERROR_MESSAGE);
        TopManager.getDefault().notify (nd);
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent event) {
        if (event.getSource() == this.list) {
            int index = this.list.getSelectedIndex();
            this.name.setText (this.model.getValue(index));
        }
        else if (event.getSource() == this.cbList) {
            int index = this.cbList.getSelectedIndex();
            this.cbName.setText (this.model.getValue(index));
        }
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent p1) {
        this.fireChange(this);
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent p1) {
        this.fireChange(this);
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent p1) {
        this.fireChange(this);
    }
    
}
