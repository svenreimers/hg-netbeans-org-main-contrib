/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ValuetypeFactoryDialog.java
 *
 * Created on 15.7.02 14:03
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Valuetype Factory" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ValuetypeFactoryDialog extends JDialogOperator {

    /** Creates new ValuetypeFactoryDialog that can handle it.
     */
    public ValuetypeFactoryDialog(boolean customize) {
        super(customize ? "Customize" : "Create Valuetype Factory");
    }

    private JLabelOperator _lblName;
    private JTextFieldOperator _txtName;
    private JLabelOperator _lblParameters;
    private JTextFieldOperator _txtParameters;
    private JButtonOperator _btOk;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblName() {
        if (_lblName==null) {
            _lblName = new JLabelOperator(this, "Name:");
        }
        return _lblName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtName() {
        if (_txtName==null) {
            _txtName = new JTextFieldOperator(this);
        }
        return _txtName;
    }

    /** Tries to find "Parameters:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblParameters() {
        if (_lblParameters==null) {
            _lblParameters = new JLabelOperator(this, "Parameters:");
        }
        return _lblParameters;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtParameters() {
        if (_txtParameters==null) {
            _txtParameters = new JTextFieldOperator(this, 1);
        }
        return _txtParameters;
    }

    /** Tries to find "Ok" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOk() {
        if (_btOk==null) {
            _btOk = new JButtonOperator(this, "Ok");
        }
        return _btOk;
    }

    /** Tries to find "Cancel" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btCancel() {
        if (_btCancel==null) {
            _btCancel = new JButtonOperator(this, "Cancel");
        }
        return _btCancel;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtName
     * @return String text
     */
    public String getName() {
        return txtName().getText();
    }

    /** sets text for txtName
     * @param text String text
     */
    public void setName(String text) {
        txtName().setText(text);
    }

    /** types text for txtName
     * @param text String text
     */
    public void typeName(String text) {
        txtName().typeText(text);
    }

    /** gets text for txtParameters
     * @return String text
     */
    public String getParameters() {
        return txtParameters().getText();
    }

    /** sets text for txtParameters
     * @param text String text
     */
    public void setParameters(String text) {
        txtParameters().setText(text);
    }

    /** types text for txtParameters
     * @param text String text
     */
    public void typeParameters(String text) {
        txtParameters().typeText(text);
    }

    /** clicks on "Ok" JButton
     */
    public void ok() {
        btOk().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of ValuetypeFactoryDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        txtName();
        lblParameters();
        txtParameters();
        btOk();
        btCancel();
    }

    /** Performs simple test of ValuetypeFactoryDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ValuetypeFactoryDialog(false).verify();
        System.out.println("ValuetypeFactoryDialog verification finished.");
    }
}

