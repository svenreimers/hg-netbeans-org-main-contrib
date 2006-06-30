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
 * ConstantDialog.java
 *
 * Created on 15.7.02 13:56
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Constant" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ConstantDialog extends JDialogOperator {

    /** Creates new ConstantDialog that can handle it.
     */
    public ConstantDialog(boolean customize) {
        super(customize ? "Customize" : "Create Constant");
    }

    private JLabelOperator _lblName;
    private JLabelOperator _lblType;
    private JLabelOperator _lblValue;
    private JTextFieldOperator _txtName;
    private JTextFieldOperator _txtType;
    private JTextFieldOperator _txtValue;
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

    /** Tries to find "Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblType() {
        if (_lblType==null) {
            _lblType = new JLabelOperator(this, "Type:");
        }
        return _lblType;
    }

    /** Tries to find "Value:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblValue() {
        if (_lblValue==null) {
            _lblValue = new JLabelOperator(this, "Value:");
        }
        return _lblValue;
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

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtType() {
        if (_txtType==null) {
            _txtType = new JTextFieldOperator(this, 1);
        }
        return _txtType;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtValue() {
        if (_txtValue==null) {
            _txtValue = new JTextFieldOperator(this, 2);
        }
        return _txtValue;
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

    /** gets text for txtType
     * @return String text
     */
    public String getType() {
        return txtType().getText();
    }

    /** sets text for txtType
     * @param text String text
     */
    public void setType(String text) {
        txtType().setText(text);
    }

    /** types text for txtType
     * @param text String text
     */
    public void typeType(String text) {
        txtType().typeText(text);
    }

    /** gets text for txtValue
     * @return String text
     */
    public String getValue() {
        return txtValue().getText();
    }

    /** sets text for txtValue
     * @param text String text
     */
    public void setValue(String text) {
        txtValue().setText(text);
    }

    /** types text for txtValue
     * @param text String text
     */
    public void typeValue(String text) {
        txtValue().typeText(text);
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

    /** Performs verification of ConstantDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        lblType();
        lblValue();
        txtName();
        txtType();
        txtValue();
        btOk();
        btCancel();
    }

    /** Performs simple test of ConstantDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ConstantDialog(false).verify();
        System.out.println("ConstantDialog verification finished.");
    }
}

