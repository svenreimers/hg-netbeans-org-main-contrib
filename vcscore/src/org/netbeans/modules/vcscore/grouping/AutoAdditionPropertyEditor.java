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

package org.netbeans.modules.vcscore.grouping;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.NbBundle;

/** Property editor for AutoAddition property of the Vgs Group Settings object
*
* @author Milos Kleint

*/
public class AutoAdditionPropertyEditor extends PropertyEditorSupport {

    /** localized string*/
    private final static String MANUAL = NbBundle.getMessage(AutoAdditionPropertyEditor.class, "AutoAddition.manual"); // NOI18N

    /** localized string*/
    private final static String TO_DEFAULT = NbBundle.getMessage(AutoAdditionPropertyEditor.class, "AutoAddition.toDefaultGroup"); // NOI18N

    /** localized string*/
    private final static String ASK = NbBundle.getMessage(AutoAdditionPropertyEditor.class, "AutoAddition.ask"); // NOI18N

    /** array of hosts */
    private static final String[] modes = {MANUAL, TO_DEFAULT};

    /** @return names of the supported LookAndFeels */
    public String[] getTags() {
        return modes;
    }

    /** @return text for the current value */
    public String getAsText () {
        Integer mode = (Integer) getValue();
        if (mode.intValue() == 0 ) {
            return MANUAL;
        } 
        else if (mode.intValue() == 2) {
            return ASK;
        }
        else {
            return TO_DEFAULT;
        }
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        if (text.equals(MANUAL)) {
            setValue(new Integer(0));
            return;
        }
        if (text.equals(TO_DEFAULT)) {
            setValue(new Integer(1));
            return;
        }
        if (text.equals(ASK)) {
            setValue(new Integer(2));
            return;
        }
        throw new IllegalArgumentException ();
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
}

