
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.portletcontrol;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;

/**
 *
 * @author Hareesh Ravindran
 */

public class CheckBoxSetControl extends TTValueControl {
    
    /** Creates a new instance of CheckBoxSetControl */
    public CheckBoxSetControl(FieldInfo fieldInfo) {
        super(fieldInfo);
    }
        
    public String getFieldInfoJspString() throws GDEException {
        StringBuffer buffer = new StringBuffer();
        String fieldName = "chkLst" + 
                    fieldInfo.getFieldMetaData().getFieldName(); 
        
          buffer.append("<table border=\"0\" width=\"100%\">");
          
        SelectionFieldDisplayInfo displayInfo = 
                (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
        Object[] selRange = displayInfo.getSelectionRange();
        Object[] defList = displayInfo.getDefaultSelection();
        for(int i = 0; i < selRange.length; ++i) {
            buffer.append("<tr><td><input type=\"checkbox\" name=\"");
            buffer.append(fieldName);
            buffer.append("\" value=\""); 
            buffer.append(selRange[i].toString());
            buffer.append("\"");
            buffer.append(isInDefValueList(selRange[i], defList) ?
                            "checked />" : "/>");
            buffer.append(selRange[i].toString());
            buffer.append("</td></tr>");
        }
        buffer.append("</table>");
        return buffer.toString();
    }

    private boolean isInDefValueList(Object obj, Object[] defList) {
        for(int i = 0; i < defList.length; ++i) {
            if(obj.equals(defList[i]))
                return true;
        }
        return false;
    }
    
    public String getFieldInfoDeclarationString() throws GDEException {
        return null;
    }
    
    
    public String getFieldInfoInitializationString() throws GDEException {
        return null;
    }

}
