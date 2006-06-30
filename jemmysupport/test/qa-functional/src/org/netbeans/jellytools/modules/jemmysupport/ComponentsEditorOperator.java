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

package org.netbeans.jellytools.modules.jemmysupport;

/*
 * ComponentsEditor.java
 *
 * Created on 7/12/02 10:50 AM
 */

import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.properties.PropertySheetOperator;

/** Class implementing all necessary methods for handling "Components Editor" NbDialog.
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public class ComponentsEditorOperator extends JDialogOperator {

    /** Creates new ComponentsEditor that can handle it.
     */
    public ComponentsEditorOperator() {
        super("Component Editor");
    }

    private JSplitPaneOperator _sppSplitPane;
    private JTreeOperator _treeComponentsTree;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;
    private PropertySheetOperator _propertySheet;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null JSplitPane in this dialog.
     * @return JSplitPaneOperator
     */
    public JSplitPaneOperator sppSplitPane() {
        if (_sppSplitPane==null) {
            _sppSplitPane = new JSplitPaneOperator(this);
        }
        return _sppSplitPane;
    }

    /** Tries to find null JTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treeComponentsTree() {
        if (_treeComponentsTree==null) {
            _treeComponentsTree = new JTreeOperator(sppSplitPane());
        }
        return _treeComponentsTree;
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

    /** Tries to find property sheet in this dialog.
     * @return PropertySheetOperator
     */
    public PropertySheetOperator propertySheet() {
        if (_propertySheet==null) {
            _propertySheet = new PropertySheetOperator(sppSplitPane());
        }
        return _propertySheet;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "OK" JButton
     */
    public void ok() {
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

    /** Performs verification of ComponentsEditor by accessing all its components.
     */
    public void verify() {
        sppSplitPane();
        treeComponentsTree();
        btOK();
        btCancel();
        propertySheet();
    }
}

