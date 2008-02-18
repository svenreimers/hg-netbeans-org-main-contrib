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

package org.netbeans.jellytools.modules.jemmysupport;

/*
 * ResourceBundleLookupOperator.java
 *
 * Created on 7/12/02 11:40 AM
 */
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jemmy.ComponentIsNotVisibleException;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Resource Bundle Look [Resource Bundle Lookup]" TopFrameTypeImpl.
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public class ResourceBundleLookupOperator extends TopComponentOperator {

    /** Creates new ResourceBundleLookupOperator that can handle it.
     */
    public ResourceBundleLookupOperator() {
        super("Resource Bundle Lookup");
    }

    private JLabelOperator _lblSearchedText;
    private JTextFieldOperator _txtSearchedText;
    private JCheckBoxOperator _cbCaseSensitiveText;
    private JCheckBoxOperator _cbSubstringText;
    private JCheckBoxOperator _cbRegularExpressionText;
    private JCheckBoxOperator _cbUseResourceBundleFilter;
    private JButtonOperator _btSearch;
    private JButtonOperator _btStop;
    private JTableOperator _tabSearchResults;
    private JLabelOperator _lblFilterText;
    private JTextFieldOperator _txtFilterText;
    private JCheckBoxOperator _cbCaseSensitiveBundle;
    private JCheckBoxOperator _cbSubstringBundle;
    private JCheckBoxOperator _cbRegularExpressionBundle;
    private JLabelOperator _lblSearchResults;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Searched text: " JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSearchedText() {
        if (_lblSearchedText==null) {
            _lblSearchedText = new JLabelOperator(this, "Searched text: ");
        }
        return _lblSearchedText;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtSearchedText() {
        if (_txtSearchedText==null) {
            _txtSearchedText = new JTextFieldOperator(this);
        }
        return _txtSearchedText;
    }

    /** Tries to find " Case Sensitive" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCaseSensitiveText() {
        if (_cbCaseSensitiveText==null) {
            _cbCaseSensitiveText = new JCheckBoxOperator(this, " Case Sensitive");
        }
        return _cbCaseSensitiveText;
    }

    /** Tries to find " Substring" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbSubstringText() {
        if (_cbSubstringText==null) {
            _cbSubstringText = new JCheckBoxOperator(this, " Substring");
        }
        return _cbSubstringText;
    }

    /** Tries to find " Regular Expression" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbRegularExpressionText() {
        if (_cbRegularExpressionText==null) {
            _cbRegularExpressionText = new JCheckBoxOperator(this, " Regular Expression");
        }
        return _cbRegularExpressionText;
    }

    /** Tries to find " Use Resource Bundle Filter:" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbUseResourceBundleFilter() {
        if (_cbUseResourceBundleFilter==null) {
            _cbUseResourceBundleFilter = new JCheckBoxOperator(this, " Use Resource Bundle Filter:");
        }
        return _cbUseResourceBundleFilter;
    }

    /** Tries to find "Search" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSearch() {
        if (_btSearch==null) {
            _btSearch = new JButtonOperator(this, "Search");
        }
        return _btSearch;
    }

    /** Tries to find "Stop" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btStop() {
        if (_btStop==null) {
            _btStop = new JButtonOperator(this, "Stop");
        }
        return _btStop;
    }

    /** Tries to find null JTable in this dialog.
     * @return JTableOperator
     */
    public JTableOperator tabSearchResults() {
        if (_tabSearchResults==null) {
            _tabSearchResults = new JTableOperator(this);
        }
        return _tabSearchResults;
    }

    /** Tries to find "Filter Text:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblFilterText() {
        if (_lblFilterText==null) {
            _lblFilterText = new JLabelOperator(this, "Filter Text:");
        }
        return _lblFilterText;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtFilterText() {
        if (_txtFilterText==null) {
            _txtFilterText = new JTextFieldOperator(this, 1);
        }
        return _txtFilterText;
    }

    /** Tries to find " Case Sensitive" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCaseSensitiveBundle() {
        if (_cbCaseSensitiveBundle==null) {
            _cbCaseSensitiveBundle = new JCheckBoxOperator(this, " Case Sensitive", 1);
        }
        return _cbCaseSensitiveBundle;
    }

    /** Tries to find " Substring" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbSubstringBundle() {
        if (_cbSubstringBundle==null) {
            _cbSubstringBundle = new JCheckBoxOperator(this, " Substring", 1);
        }
        return _cbSubstringBundle;
    }

    /** Tries to find " Regular Expression" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbRegularExpressionBundle() {
        if (_cbRegularExpressionBundle==null) {
            _cbRegularExpressionBundle = new JCheckBoxOperator(this, " Regular Expression", 1);
        }
        return _cbRegularExpressionBundle;
    }

    /** Tries to find "Search Results:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSearchResults() {
        if (_lblSearchResults==null) {
            _lblSearchResults = new JLabelOperator(this, "Search Results:");
        }
        return _lblSearchResults;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtSearchedText
     * @return String text
     */
    public String getSearchedText() {
        return txtSearchedText().getText();
    }

    /** sets text for txtSearchedText
     * @param text String text
     */
    public void setSearchedText(String text) {
        txtSearchedText().setText(text);
    }

    /** types text for txtSearchedText
     * @param text String text
     */
    public void typeSearchedText(String text) {
        txtSearchedText().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCaseSensitiveText(boolean state) {
        if (cbCaseSensitiveText().isSelected()!=state) {
            cbCaseSensitiveText().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkSubstringText(boolean state) {
        if (cbSubstringText().isSelected()!=state) {
            cbSubstringText().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkRegularExpressionText(boolean state) {
        if (cbRegularExpressionText().isSelected()!=state) {
            cbRegularExpressionText().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkUseResourceBundleFilter(boolean state) {
        if (cbUseResourceBundleFilter().isSelected()!=state) {
            cbUseResourceBundleFilter().push();
        }
    }

    /** clicks on "Search" JButton
     */
    public void search() {
        try {
            btSearch().push();
        } catch (ComponentIsNotVisibleException ce) {
        } catch (JemmyException e) {
            if (!(e.getInnerException() instanceof ComponentIsNotVisibleException))
                throw e;
        }
    }

    /** clicks on "Stop" JButton
     */
    public void stop() {
        try {
            btStop().push();
        } catch (ComponentIsNotVisibleException ce) {
        } catch (JemmyException e) {
            if (!(e.getInnerException() instanceof ComponentIsNotVisibleException))
                throw e;
        }
    }

    /** gets text for txtFilterText
     * @return String text
     */
    public String getFilterText() {
        return txtFilterText().getText();
    }

    /** sets text for txtFilterText
     * @param text String text
     */
    public void setFilterText(String text) {
        txtFilterText().setText(text);
    }

    /** types text for txtFilterText
     * @param text String text
     */
    public void typeFilterText(String text) {
        txtFilterText().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCaseSensitiveBundle(boolean state) {
        if (cbCaseSensitiveBundle().isSelected()!=state) {
            cbCaseSensitiveBundle().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkSubstringBundle(boolean state) {
        if (cbSubstringBundle().isSelected()!=state) {
            cbSubstringBundle().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkRegularExpressionBundle(boolean state) {
        if (cbRegularExpressionBundle().isSelected()!=state) {
            cbRegularExpressionBundle().push();
        }
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    public void verifyStatus(String status) {
        long t = getTimeouts().getTimeout("ComponentOperator.WaitComponentTimeout");
        getTimeouts().setTimeout("ComponentOperator.WaitComponentTimeout", 30000);
        try {
            new JLabelOperator(this, status);
        } finally {
            getTimeouts().setTimeout("ComponentOperator.WaitComponentTimeout", t);
        }
    }
    
    /** Performs verification of ResourceBundleLookupOperator by accessing all its components.
     */
    public void verify() {
        lblSearchedText();
        txtSearchedText();
        cbCaseSensitiveText();
        cbSubstringText();
        cbRegularExpressionText();
        cbUseResourceBundleFilter();
        btSearch();
        tabSearchResults();
        lblFilterText();
        txtFilterText();
        cbCaseSensitiveBundle();
        cbSubstringBundle();
        cbRegularExpressionBundle();
        lblSearchResults();
    }

    public static ResourceBundleLookupOperator invoke() {
        new Action("Tools|Resource Bundle Lookup", null).performMenu();
        return new ResourceBundleLookupOperator();
    }
}

