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


import java.util.*;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import org.netbeans.modules.corba.wizard.CorbaWizardData;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBBindingDescriptor;
import org.netbeans.modules.corba.settings.WizardSettings;
import org.netbeans.modules.corba.settings.WizardRequirement;
import org.netbeans.modules.corba.settings.ORBSettings;
//import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  tzezula
 * @version
 */

public class ORBPanel extends AbstractCORBAWizardPanel {
    
    
    
    private boolean initialized;
    private CORBASupportSettings css;
    private CorbaWizardData data;
    
    /** Creates new form ORBPanel */
    public ORBPanel() {
        this.initialized = false;
        this.css = null;
        initComponents ();
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(2));
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_ImplementationsBindings"));
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_OrbImplementations_MNE").charAt(0));
        this.jLabel2.setDisplayedMnemonic (this.bundle.getString("TXT_BindingMethod_MNE").charAt(0));
        this.orbs.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_OrbImplementations"));
        this.bindings.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_BindingMethod"));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_ORBPanel"));
    }
    
    
    
    /** Sets the panel data from CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    public void readCorbaSettings (CorbaWizardData data){
        if (!initialized){
            this.data = data;
            this.css = (CORBASupportSettings) data.getSettings();
            
            Vector names = this.css.getNames();
            for (int i=0; i< names.size(); i++){
                this.orbs.addItem (names.elementAt(i));
            }

            List list = this.css.getActiveSetting ().getServerBindings ();
            this.bindings.setRenderer(new LocalizedRenderer(this.css.getActiveSetting()));
            if (list != null) {
                ListIterator li = list.listIterator();
                while (li.hasNext()) {
                    ORBBindingDescriptor bd = (ORBBindingDescriptor)li.next();
                    WizardSettings ws = bd.getWizardSettings();
                    if (ws != null && ws.isSupported())
                        this.bindings.addItem (bd.getName());
                }
            }
            
            String value = this.css.getActiveSetting ().getName ();
            
            if (value != null){
                this.orbs.setSelectedItem (value);
                data.setDefaultOrbValue (value);
            }
            value = this.css.getActiveSetting ().getClientBinding().getValue ();
            data.setDefaultClientBindingValue (value);
            value = this.css.getActiveSetting ().getServerBinding().getValue ();
            data.setDefaultServerBindingValue (value);
            this.orbs.addActionListener ( new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    ORBPanel.this.orbChanged (event);
                }
            });
            this.bindings.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    ORBPanel.this.bindingChanged (event);
                }
            });
            this.bindings.setSelectedItem (data.getDefaultServerBindingValue());
            this.initialized = true;
        }
        
        Object value = data.getCORBAImpl();
        if (value != null){
            this.orbs.setSelectedItem (value);
        }
        value = data.getBindMethod ();
        if (value != null){
            this.bindings.setSelectedItem (value);
        }
        
        int mask = data.getGenerate(); 
        this.bindings.setEnabled (((mask & CorbaWizardData.SERVER) == CorbaWizardData.SERVER)
                                     || ((mask & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT)
                                     || ((mask & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT));
    }
    
    
    
    /** Sets the settings from panel to CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    
    public void storeCorbaSettings (CorbaWizardData data){
        data.setCORBAImpl((String)this.orbs.getSelectedItem());
        data.setBindMethod((String)this.bindings.getSelectedItem());
    }
    
    
    
    public boolean isValid () {
        return true;
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        orbs = new javax.swing.JComboBox();
        bindings = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(bundle.getString("TXT_OrbImplementations"));
        jLabel1.setLabelFor(orbs);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 6);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText(bundle.getString("TXT_BindingMethod"));
        jLabel2.setLabelFor(bindings);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 6);
        jPanel1.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 6, 12);
        jPanel1.add(orbs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 12);
        jPanel1.add(bindings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel3, gridBagConstraints);

    }//GEN-END:initComponents
      
  
  
    private void bindingChanged (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bindingChanged
        // Add your handling code here:
        
        String serverBind = (String)this.bindings.getSelectedItem();
        if (serverBind == null)
            return;   //dirty hack
        this.css.getActiveSetting ().setServerBindingFromString (serverBind);
        String clientBind = CorbaWizardData.getClientBindMethod(serverBind);
        this.css.getActiveSetting ().setClientBindingFromString (clientBind);
        
    }//GEN-LAST:event_bindingChanged
    
    
    
    private void orbChanged (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orbChanged
        // Add your handling code here:
        
        if (this.data != null && this.data.getDefaultClientBindingValue() != null)
            this.css.getActiveSetting ().setClientBindingFromString (this.data.getDefaultClientBindingValue());
        if (this.data != null && this.data.getDefaultServerBindingValue() != null)
            this.css.getActiveSetting ().setServerBindingFromString (this.data.getDefaultServerBindingValue());
        this.css.setOrb ((String) this.orbs.getSelectedItem ());
        if (this.data != null){
            this.data.setDefaultClientBindingValue(this.css.getActiveSetting().getClientBinding().getValue());
            this.data.setDefaultServerBindingValue(this.css.getActiveSetting().getServerBinding().getValue());
        }
        List list = this.css.getActiveSetting ().getServerBindings ();
        this.bindings.removeAllItems();
        this.bindings.setRenderer(new LocalizedRenderer(this.css.getActiveSetting()));
        if (list != null) {
            ListIterator li = list.listIterator();
            while (li.hasNext()) {
                ORBBindingDescriptor bd = (ORBBindingDescriptor)li.next();
                WizardSettings ws = bd.getWizardSettings();
                if (ws != null && ws.isSupported())
                    this.bindings.addItem (bd.getName());
            }
        }
    }//GEN-LAST:event_orbChanged
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox bindings;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox orbs;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");    
    
    
    
}

class LocalizedRenderer extends javax.swing.plaf.basic.BasicComboBoxRenderer {
    
    protected ORBSettings _os = null;
    
    LocalizedRenderer(ORBSettings os) {
        _os = os;
    };
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (_os != null)
            setText(_os.getLocalizedString(getText()));
        return this;
    }

}

