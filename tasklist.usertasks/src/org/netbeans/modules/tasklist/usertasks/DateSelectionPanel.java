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
package org.netbeans.modules.tasklist.usertasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

/**
 * This is a small panel to allow the user to select a full date. When I
 * started the implementation of the Alarm functionality I had the user to
 * write the complete time/date, and I pretty soon realized that noone will
 * remember the format each time.... Well, the panel "works for me now" so I
 * move on to the next phase in my project, but one should really:
 *
 * @author  Trond Norbye
 */
public class DateSelectionPanel extends javax.swing.JPanel
    implements EnhancedCustomPropertyEditor {

    private static final long serialVersionUID = 1;

    /**
     * A SimpleDateFormat I use for conversion to/from textual representation
     * of date fields.
     */
    private SimpleDateFormat format;
   
    /** Creates new form DateSelectionPanel. */
    public DateSelectionPanel() {
        this(new Date());
    }

    /**
     * Create a new DateSelectionPanel with the given date selected...
     *
     * @param date initial selection
     */
    public DateSelectionPanel(Date date) {
        initComponents();
        jCalendar.setDate(date);
    }
    
    /**
     * Returns the selected date
     *
     * @return selected date
     */
    public Date getDate() {
        return jCalendar.getDate();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jCalendar = new com.toedter.calendar.JCalendar();
        jLabel1 = new javax.swing.JLabel();
        timeFld = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(11, 11, 12, 12)));
        jPanel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 11, 0)));
        jPanel1.add(jCalendar);

        jLabel1.setText("@");
        jPanel1.add(jLabel1);

        timeFld.setColumns(8);
        timeFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeFld.setInputVerifier(new javax.swing.InputVerifier() {
            public boolean verify(javax.swing.JComponent obj) {
                boolean ret;
                try {
                    format.applyPattern("HH:mm:ss");
                    format.parse(((javax.swing.JTextField)obj).getText());
                    ret = true;
                } catch (Exception e) {
                    ret = false;
                }
                return ret;
            }
        });
        jPanel1.add(timeFld);

        add(jPanel1, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JCalendar jCalendar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField timeFld;
    // End of variables declaration//GEN-END:variables
    
    // When used as a property customizer
    public Object getPropertyValue() throws IllegalStateException {
        return getDate();	    
    }
}
