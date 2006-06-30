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

package org.netbeans.jellytools.modules.corba.poasupport;

import org.netbeans.jemmy.operators.*;

public class NewChildPOACustomizerDialog extends JDialogOperator {

    /** Creates new NewChildPOACustomizerDialog that can handle it.
     */
    public NewChildPOACustomizerDialog() {
        super("Customizer Dialog");
    }

    private JLabelOperator _lblPOAName;
    private JTextFieldOperator _txtPOAName;
    private JLabelOperator _lblVariable;
    private JTextFieldOperator _txtVariable;
    private JLabelOperator _lblManager;
    private JComboBoxOperator _cboManager;
    public static final String ITEM_POA = "poa"; 
    public static final String ITEM_NEWMANAGER = "<new manager>"; 
    private JLabelOperator _lblThread;
    private JComboBoxOperator _cboThread;
    public static final String ITEM_ORB_CTRL_MODEL = "ORB_CTRL_MODEL"; 
    public static final String ITEM_SINGLE_THREAD_MODEL = "SINGLE_THREAD_MODEL"; 
    private JLabelOperator _lblLifespan;
    private JComboBoxOperator _cboLifespan;
    public static final String ITEM_TRANSIENT = "TRANSIENT"; 
    public static final String ITEM_PERSISTENT = "PERSISTENT"; 
    private JLabelOperator _lblIdUniqueness;
    private JComboBoxOperator _cboIdUniqueness;
    public static final String ITEM_UNIQUE_ID = "UNIQUE_ID"; 
    public static final String ITEM_MULTIPLE_ID = "MULTIPLE_ID"; 
    private JLabelOperator _lblIdAssignment;
    private JComboBoxOperator _cboIdAssignment;
    public static final String ITEM_SYSTEM_ID = "SYSTEM_ID"; 
    public static final String ITEM_USER_ID = "USER_ID"; 
    private JLabelOperator _lblServantRetention;
    private JComboBoxNoBlockOperator _cboServantRetention;
    public static final String ITEM_RETAIN = "RETAIN"; 
    public static final String ITEM_NON_RETAIN = "NON_RETAIN"; 
    private JLabelOperator _lblRequestProcessing;
    private JComboBoxOperator _cboRequestProcessing;
    public static final String ITEM_USE_ACTIVE_OBJECT_MAP_ONLY = "USE_ACTIVE_OBJECT_MAP_ONLY"; 
    public static final String ITEM_USE_DEFAULT_SERVANT = "USE_DEFAULT_SERVANT"; 
    public static final String ITEM_USE_SERVANT_MANAGER = "USE_SERVANT_MANAGER"; 
    private JLabelOperator _lblImplicitActivation;
    private JComboBoxOperator _cboImplicitActivation;
    public static final String ITEM_IMPLICIT_ACTIVATION = "IMPLICIT_ACTIVATION"; 
    public static final String ITEM_NO_IMPLICIT_ACTIVATION = "NO_IMPLICIT_ACTIVATION"; 
    private JButtonOperator _btClose;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "POA Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPOAName() {
        if (_lblPOAName==null) {
            _lblPOAName = new JLabelOperator(this, "POA Name:");
        }
        return _lblPOAName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPOAName() {
        if (_txtPOAName==null) {
            _txtPOAName = new JTextFieldOperator(this);
        }
        return _txtPOAName;
    }

    /** Tries to find "Variable:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblVariable() {
        if (_lblVariable==null) {
            _lblVariable = new JLabelOperator(this, "Variable:");
        }
        return _lblVariable;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtVariable() {
        if (_txtVariable==null) {
            _txtVariable = new JTextFieldOperator(this, 1);
        }
        return _txtVariable;
    }

    /** Tries to find "Manager:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblManager() {
        if (_lblManager==null) {
            _lblManager = new JLabelOperator(this, "Manager:");
        }
        return _lblManager;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboManager() {
        if (_cboManager==null) {
            _cboManager = new JComboBoxOperator(this);
        }
        return _cboManager;
    }

    /** Tries to find "Thread:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblThread() {
        if (_lblThread==null) {
            _lblThread = new JLabelOperator(this, "Thread:");
        }
        return _lblThread;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboThread() {
        if (_cboThread==null) {
            _cboThread = new JComboBoxOperator(this, 1);
        }
        return _cboThread;
    }

    /** Tries to find "Lifespan:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLifespan() {
        if (_lblLifespan==null) {
            _lblLifespan = new JLabelOperator(this, "Lifespan:");
        }
        return _lblLifespan;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboLifespan() {
        if (_cboLifespan==null) {
            _cboLifespan = new JComboBoxOperator(this, 2);
        }
        return _cboLifespan;
    }

    /** Tries to find "Id Uniqueness:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIdUniqueness() {
        if (_lblIdUniqueness==null) {
            _lblIdUniqueness = new JLabelOperator(this, "Id Uniqueness:");
        }
        return _lblIdUniqueness;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboIdUniqueness() {
        if (_cboIdUniqueness==null) {
            _cboIdUniqueness = new JComboBoxOperator(this, 3);
        }
        return _cboIdUniqueness;
    }

    /** Tries to find "Id Assignment:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIdAssignment() {
        if (_lblIdAssignment==null) {
            _lblIdAssignment = new JLabelOperator(this, "Id Assignment:");
        }
        return _lblIdAssignment;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboIdAssignment() {
        if (_cboIdAssignment==null) {
            _cboIdAssignment = new JComboBoxOperator(this, 4);
        }
        return _cboIdAssignment;
    }

    /** Tries to find "Servant Retention:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblServantRetention() {
        if (_lblServantRetention==null) {
            _lblServantRetention = new JLabelOperator(this, "Servant Retention:");
        }
        return _lblServantRetention;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxNoBlockOperator cboServantRetention() {
        if (_cboServantRetention==null) {
            _cboServantRetention = new JComboBoxNoBlockOperator(this, 5);
        }
        return _cboServantRetention;
    }

    /** Tries to find "Request Processing:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRequestProcessing() {
        if (_lblRequestProcessing==null) {
            _lblRequestProcessing = new JLabelOperator(this, "Request Processing:");
        }
        return _lblRequestProcessing;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboRequestProcessing() {
        if (_cboRequestProcessing==null) {
            _cboRequestProcessing = new JComboBoxOperator(this, 6);
        }
        return _cboRequestProcessing;
    }

    /** Tries to find "Implicit Activation:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblImplicitActivation() {
        if (_lblImplicitActivation==null) {
            _lblImplicitActivation = new JLabelOperator(this, "Implicit Activation:");
        }
        return _lblImplicitActivation;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboImplicitActivation() {
        if (_cboImplicitActivation==null) {
            _cboImplicitActivation = new JComboBoxOperator(this, 7);
        }
        return _cboImplicitActivation;
    }

    /** Tries to find "Close" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btClose() {
        if (_btClose==null) {
            _btClose = new JButtonOperator(this, "Close");
        }
        return _btClose;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtPOAName
     * @return String text
     */
    public String getPOAName() {
        return txtPOAName().getText();
    }

    /** sets text for txtPOAName
     * @param text String text
     */
    public void setPOAName(String text) {
        txtPOAName().setText(text);
    }

    /** types text for txtPOAName
     * @param text String text
     */
    public void typePOAName(String text) {
        txtPOAName().typeText(text);
    }

    /** gets text for txtVariable
     * @return String text
     */
    public String getVariable() {
        return txtVariable().getText();
    }

    /** sets text for txtVariable
     * @param text String text
     */
    public void setVariable(String text) {
        txtVariable().setText(text);
    }

    /** types text for txtVariable
     * @param text String text
     */
    public void typeVariable(String text) {
        txtVariable().typeText(text);
    }

    /** returns selected item for cboManager
     * @return String item
     */
    public String getSelectedManager() {
        return cboManager().getSelectedItem().toString();
    }

    /** selects item for cboManager
     * @param item String item
     */
    public void selectManager(String item) {
        cboManager().selectItem(item);
    }

    /** types text for cboManager
     * @param text String text
     */
    public void typeManager(String text) {
        cboManager().typeText(text);
    }

    /** returns selected item for cboThread
     * @return String item
     */
    public String getSelectedThread() {
        return cboThread().getSelectedItem().toString();
    }

    /** selects item for cboThread
     * @param item String item
     */
    public void selectThread(String item) {
        cboThread().selectItem(item);
    }

    /** types text for cboThread
     * @param text String text
     */
    public void typeThread(String text) {
        cboThread().typeText(text);
    }

    /** returns selected item for cboLifespan
     * @return String item
     */
    public String getSelectedLifespan() {
        return cboLifespan().getSelectedItem().toString();
    }

    /** selects item for cboLifespan
     * @param item String item
     */
    public void selectLifespan(String item) {
        cboLifespan().selectItem(item);
    }

    /** types text for cboLifespan
     * @param text String text
     */
    public void typeLifespan(String text) {
        cboLifespan().typeText(text);
    }

    /** returns selected item for cboIdUniqueness
     * @return String item
     */
    public String getSelectedIdUniqueness() {
        return cboIdUniqueness().getSelectedItem().toString();
    }

    /** selects item for cboIdUniqueness
     * @param item String item
     */
    public void selectIdUniqueness(String item) {
        cboIdUniqueness().selectItem(item);
    }

    /** types text for cboIdUniqueness
     * @param text String text
     */
    public void typeIdUniqueness(String text) {
        cboIdUniqueness().typeText(text);
    }

    /** returns selected item for cboIdAssignment
     * @return String item
     */
    public String getSelectedIdAssignment() {
        return cboIdAssignment().getSelectedItem().toString();
    }

    /** selects item for cboIdAssignment
     * @param item String item
     */
    public void selectIdAssignment(String item) {
        cboIdAssignment().selectItem(item);
    }

    /** types text for cboIdAssignment
     * @param text String text
     */
    public void typeIdAssignment(String text) {
        cboIdAssignment().typeText(text);
    }

    /** returns selected item for cboServantRetention
     * @return String item
     */
    public String getSelectedServantRetention() {
        return cboServantRetention().getSelectedItem().toString();
    }

    /** selects item for cboServantRetention
     * @param item String item
     */
    public void selectServantRetention(String item) {
        cboServantRetention().selectItem(item);
    }

    /** types text for cboServantRetention
     * @param text String text
     */
    public void typeServantRetention(String text) {
        cboServantRetention().typeText(text);
    }

    /** returns selected item for cboRequestProcessing
     * @return String item
     */
    public String getSelectedRequestProcessing() {
        return cboRequestProcessing().getSelectedItem().toString();
    }

    /** selects item for cboRequestProcessing
     * @param item String item
     */
    public void selectRequestProcessing(String item) {
        cboRequestProcessing().selectItem(item);
    }

    /** types text for cboRequestProcessing
     * @param text String text
     */
    public void typeRequestProcessing(String text) {
        cboRequestProcessing().typeText(text);
    }

    /** returns selected item for cboImplicitActivation
     * @return String item
     */
    public String getSelectedImplicitActivation() {
        return cboImplicitActivation().getSelectedItem().toString();
    }

    /** selects item for cboImplicitActivation
     * @param item String item
     */
    public void selectImplicitActivation(String item) {
        cboImplicitActivation().selectItem(item);
    }

    /** types text for cboImplicitActivation
     * @param text String text
     */
    public void typeImplicitActivation(String text) {
        cboImplicitActivation().typeText(text);
    }

    /** clicks on "Close" JButton
     */
    public void close() {
        btClose().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of NewChildPOACustomizerDialog by accessing all its components.
     */
    public void verify() {
        lblPOAName();
        txtPOAName();
        lblVariable();
        txtVariable();
        lblManager();
        cboManager();
        lblThread();
        cboThread();
        lblLifespan();
        cboLifespan();
        lblIdUniqueness();
        cboIdUniqueness();
        lblIdAssignment();
        cboIdAssignment();
        lblServantRetention();
        cboServantRetention();
        lblRequestProcessing();
        cboRequestProcessing();
        lblImplicitActivation();
        cboImplicitActivation();
        btClose();
    }

    /** Performs simple test of NewChildPOACustomizerDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new NewChildPOACustomizerDialog().verify();
        System.out.println("NewChildPOACustomizerDialog verification finished.");
    }
}

