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

package org.netbeans.a11y.ui;

import org.netbeans.a11y.AccComponent;

import javax.swing.JLabel;

/** Panel that displays interesting Accessibility values of components.
 * @author  Marian.Mirilovic@Sun.Com
 */
public class AccPropPanel extends javax.swing.JPanel {

    private java.awt.Color defaultColor = java.awt.Color.blue;
    private final String NO_MNEMONIC = "no mnemonic";
    private final String NO_CHILDREN = "no children";
    private final String NULL = "null";

    /** Creates new panel AccPropPanel */
    public AccPropPanel() {
        initComponents();
        if(!Boolean.getBoolean("a11ytest.name")) {
            jPanel7.remove(componentNameLabel);
            jPanel8.remove(componentName);
        }
    }
    
    /** Update panel for checked component.
     * @param component checked component */
    public void updatePanel(AccComponent component){
        
        String cia = component.implementAccessible;
        if(cia.equals(component.YES)) {
            showGood(implementAccesssible,cia);
        } else
            showBad(implementAccesssible,cia);
        
        
        showLabel(accessibleName, component.accessibleName);
        showLabel(accessibleDescription, component.accessibleDescription);
        showLabel(componentName, component.componentName);
        
        showInt(mnemonic, component.mnemonic);
        showLabel(labelFor, component.componentLabelFor);
        
        showNeutral(showingL,component.showing);
        showNeutral(focusableL,component.focusable);
        showNeutral(enabledL,component.enabled);
        showNeutral(visibleL,component.visible);
        
        showInt(childrenL, component.childrens);
        showLabel(layoutL, component.layout);
        
    }
    
    /** Before showing label test for null value and write null -> showBad else showGood
     * @param label changed JLabel
     * @param text  value */
    private void showLabel(JLabel label, String text) {
        if (text == null)
            showBad(label,NULL);
        else
            showGood(label,text);
    }
    
    
    /** Set text for label as good value ,  foreground = java.awt.Color(0, 102, 0)
     * @param label changed JLabel
     * @param text  value */
    private void showGood(JLabel label, String text) {
        label.setForeground(new java.awt.Color(0, 102, 0));
        label.setText(text);
    }
    
    /** Set text for label as bad value ,  foreground = java.awt.Color(0, 102, 0)
     * @param label changed JLabel
     * @param text  value */
    private void showBad(JLabel label, String text) {
        label.setForeground(new java.awt.Color(153, 0, 0));
        label.setText(text);
    }
    
    /** Set text for label as default value ,  foreground = java.awt.Color(0, 102, 0)
     * @param label changed JLabel
     * @param text  value */
    private void showNeutral(JLabel label, String text) {
        label.setForeground(defaultColor);
        label.setText(text);
    }
    
    /** Set text for label Menomic or Number of childrens
     * @param jLabel changed JLabel
     * @param number number to show
     * @param is_mnemonic true - mnemonic, false - childrens */
    private void showInt(JLabel jLabel, String text) {
        if(text.equals(NO_MNEMONIC) || text.equals(NO_CHILDREN))
            showBad(jLabel, text);
        else if(text.equals(" ")){
            showNeutral(jLabel, text);
        }else
            showGood(jLabel, text);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel81 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        isLabel = new javax.swing.JLabel();
        anLabel = new javax.swing.JLabel();
        adLabel = new javax.swing.JLabel();
        componentNameLabel = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel1211 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel161 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        implementAccesssible = new javax.swing.JLabel();
        accessibleName = new javax.swing.JLabel();
        accessibleDescription = new javax.swing.JLabel();
        componentName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        layoutL = new javax.swing.JLabel();
        childrenL = new javax.swing.JLabel();
        mnemonic = new javax.swing.JLabel();
        labelFor = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        enabledL = new javax.swing.JLabel();
        visibleL = new javax.swing.JLabel();
        showingL = new javax.swing.JLabel();
        focusableL = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jPanel2.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        jPanel81.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel7.setLayout(new java.awt.GridLayout(14, 1));

        defaultColor = this.getForeground();
        isLabel.setText("Implements Accessible");
        jPanel7.add(isLabel);

        anLabel.setText("Accessible Name");
        jPanel7.add(anLabel);

        adLabel.setText("Accessible Description");
        jPanel7.add(adLabel);

        componentNameLabel.setText("Component Name");
        componentNameLabel.setName("componentNameLabel");
        jPanel7.add(componentNameLabel);

        jPanel7.add(jSeparator11);

        jLabel1211.setText(" Layout");
        jPanel7.add(jLabel1211);

        jLabel121.setText(" Children");
        jPanel7.add(jLabel121);

        jLabel13.setText(" Mnemonic");
        jPanel7.add(jLabel13);

        jLabel14.setText(" Label for set");
        jPanel7.add(jLabel14);

        jPanel7.add(jSeparator1);

        jLabel15.setText(" Enabled");
        jPanel7.add(jLabel15);

        jLabel151.setText(" Visible");
        jPanel7.add(jLabel151);

        jLabel16.setText(" Showing");
        jPanel7.add(jLabel16);

        jLabel161.setText(" Focusable");
        jPanel7.add(jLabel161);

        jPanel81.add(jPanel7);

        jPanel8.setLayout(new java.awt.GridLayout(14, 1));

        jPanel8.add(implementAccesssible);

        jPanel8.add(accessibleName);

        jPanel8.add(accessibleDescription);

        componentName.setName("componentName");
        jPanel8.add(componentName);

        jLabel3.setText("  ");
        jPanel8.add(jLabel3);

        jPanel8.add(layoutL);

        jPanel8.add(childrenL);

        jPanel8.add(mnemonic);

        jPanel8.add(labelFor);

        jLabel31.setText("  ");
        jPanel8.add(jLabel31);

        jPanel8.add(enabledL);

        jPanel8.add(visibleL);

        jPanel8.add(showingL);

        jPanel8.add(focusableL);

        jPanel81.add(jPanel8);

        jPanel2.add(jPanel81);

        jScrollPane1.setViewportView(jPanel2);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accessibleDescription;
    private javax.swing.JLabel accessibleName;
    private javax.swing.JLabel adLabel;
    private javax.swing.JLabel anLabel;
    private javax.swing.JLabel childrenL;
    private javax.swing.JLabel componentName;
    private javax.swing.JLabel componentNameLabel;
    private javax.swing.JLabel enabledL;
    private javax.swing.JLabel focusableL;
    private javax.swing.JLabel implementAccesssible;
    private javax.swing.JLabel isLabel;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel1211;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel81;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JLabel labelFor;
    private javax.swing.JLabel layoutL;
    private javax.swing.JLabel mnemonic;
    private javax.swing.JLabel showingL;
    private javax.swing.JLabel visibleL;
    // End of variables declaration//GEN-END:variables
    
}
