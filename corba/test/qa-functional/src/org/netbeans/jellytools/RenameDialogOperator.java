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

package org.netbeans.jellytools;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Rename" NbPresenter.
 *
 * @author dave
 * @version 1.0
 */
public class RenameDialogOperator extends NbDialogOperator {

    /** Creates new RenameDialogOperator that can handle it.
     */
    public RenameDialogOperator() {
        super("Rename");
    }

    private JLabelOperator _lblNewName;
    private JTextFieldOperator _txtNewName;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "New Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblNewName() {
        if (_lblNewName==null) {
            _lblNewName = new JLabelOperator(this, "New Name:");
        }
        return _lblNewName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtNewName() {
        if (_txtNewName==null) {
            _txtNewName = new JTextFieldOperator(this);
        }
        return _txtNewName;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtNewName
     * @return String text
     */
    public String getNewName() {
        return txtNewName().getText();
    }

    /** sets text for txtNewName
     * @param text String text
     */
    public void setNewName(String text) {
        txtNewName().setText(text);
    }

    /** types text for txtNewName
     * @param text String text
     */
    public void typeNewName(String text) {
        txtNewName().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of RenameDialogOperator by accessing all its components.
     */
    public void verify() {
        lblNewName();
        txtNewName();
    }

    /** Performs simple test of RenameDialogOperator
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new RenameDialogOperator().verify();
        System.out.println("RenameDialogOperator verification finished.");
    }
}

