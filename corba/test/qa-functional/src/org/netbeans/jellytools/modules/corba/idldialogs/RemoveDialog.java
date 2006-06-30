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
 * RemoveDialog.java
 *
 * Created on 15.7.02 13:55
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Question" NbPresenter.
 *
 * @author dave
 * @version 1.0
 */
public class RemoveDialog extends JDialogOperator {

    /** Creates new RemoveDialog that can handle it.
     */
    public RemoveDialog() {
        super("Question");
    }

    private JLabelOperator _lblAreYouSureYouWantToDeleteFf;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Are you sure, you want to delete ff?" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAreYouSureYouWantToDeleteFf() {
        if (_lblAreYouSureYouWantToDeleteFf==null) {
            _lblAreYouSureYouWantToDeleteFf = new JLabelOperator(this, "Are you sure, you want to delete ff?");
        }
        return _lblAreYouSureYouWantToDeleteFf;
    }

    /** Tries to find "OK" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOK() {
        if (_btOK==null) {
            _btOK = new JButtonOperator(this, "OK");
        }
        return _btOK;
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

    /** clicks on "OK" JButton
     */
    public void oK() {
        btOK().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of RemoveDialog by accessing all its components.
     */
    public void verify() {
        lblAreYouSureYouWantToDeleteFf();
        btOK();
        btCancel();
    }

    /** Performs simple test of RemoveDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new RemoveDialog().verify();
        System.out.println("RemoveDialog verification finished.");
    }
}

