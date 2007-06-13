
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gdelib.fields;

import com.sun.tthub.gdelib.InvalidArgumentException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Hareesh Ravindran
 */
public class ComplexEntryFieldDisplayInfo extends FieldDisplayInfo {
    
    protected boolean usePopupWindow;
    protected String popupFileName;
    
    /**
     * Map containing the field names of the fields of this complex data type and
     * the corresponding FieldInfo objects. The values in the map can be simple
     * to extremely complex, depending on the nature of the class passed to it.
     */
    protected Map fieldInfoMap;
    
    /**
     * Creates a new instance of ComplexEntryFieldDisplayInfo
     */
    public ComplexEntryFieldDisplayInfo() {}
    
    /**
     * The usePopupWindow parameter will be used to decide between the usage of
     * the variables popupFileName and the fieldInfoMap.
     */
    public ComplexEntryFieldDisplayInfo(String fieldDisplayName,
            UIComponentType compType, boolean usePopupWindow, 
            String popupFileName, Map fieldInfoMap) {
        super(fieldDisplayName, compType);        
        this.usePopupWindow = usePopupWindow;
        if(usePopupWindow) {            
            if (popupFileName == null || popupFileName.trim().equals("")) {
                        throw new InvalidArgumentException("The popup filename " +
                                "has to be provided. It cannot be empty.");
            }
            this.popupFileName =  popupFileName;
        } else {
            if(fieldInfoMap == null || fieldInfoMap.size() < 1) {
                throw new InvalidArgumentException("The field display parameters map" +
                        "cannot be null or empty.");
            }
            this.fieldInfoMap = fieldInfoMap;     
        }
    }
    
    public int getFieldDataEntryNature() {
        return FieldDataEntryNature.TYPE_ENTRY;
    }
    
    public boolean getUsePopupWindow() { return this.usePopupWindow; }
    public void setUsePopupWindow(boolean usePopupWindow) {
        this.usePopupWindow = usePopupWindow;
    }
    
    public String getPopupFileName() { return this.popupFileName; }
    public void setPopupFileName(String popupFileName) {
        if(popupFileName == null || popupFileName.trim().equals("")) {
            throw new InvalidArgumentException("The popup fileName has to be " +
                    "provided. It cannot be null.");
        }
        this.fieldInfoMap = null; // unset the field display params.
        this.popupFileName = popupFileName;
    }
    
    public Map getFieldInfoMap() { return this.fieldInfoMap; }
    public void setFieldInfoMap(Map fieldInfoMap) {
        if(fieldInfoMap == null || fieldInfoMap.size() < 1) {
            throw new InvalidArgumentException("The field display parameters map" +
                    "cannot be null or empty.");
        }
        this.popupFileName = null;
        this.fieldInfoMap = fieldInfoMap;
    }
    
    public Object clone() throws CloneNotSupportedException {
        Collection coll = fieldInfoMap.entrySet();
        if(usePopupWindow) {
            return new ComplexEntryFieldDisplayInfo(fieldDisplayName, 
                    displayUIComponentType, true, 
                    popupFileName, null);
        }        
        Map map = new HashMap();
        for(Iterator it = coll.iterator(); it.hasNext(); ) {            
            Map.Entry entry = (Map.Entry) it.next();
            // Create a copy of each display control attribute in the 
            // hash map.
            FieldInfo fieldInfo = (FieldInfo) entry.getValue();
            FieldInfo fieldInfoClone = 
                            (FieldInfo) fieldInfo.clone();            
            map.put(entry.getKey(), fieldInfoClone);
        }
        return new ComplexEntryFieldDisplayInfo(fieldDisplayName, 
                displayUIComponentType, false, null, map);
    }
    
    public boolean equals(Object obj) {
        if(!super.equals(obj))
            return false;
        if(!(obj instanceof ComplexEntryFieldDisplayInfo))
            return false;
        ComplexEntryFieldDisplayInfo fieldDisplayInfo = 
                    (ComplexEntryFieldDisplayInfo) obj;
        if(popupFileName != null) {
            return popupFileName.equals(fieldDisplayInfo.getPopupFileName());
        }
        return fieldInfoMap.equals(fieldDisplayInfo.getFieldInfoMap());
    }
    
    public String getComplexFieldInfoStr(int complexityLevel) {
        StringBuffer buffer = new StringBuffer();
        Collection coll = fieldInfoMap.entrySet();
        int tabSpace = 4;
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fieldName = (String) entry.getKey();
            FieldInfo fieldInfo = (FieldInfo) entry.getValue();
            FieldMetaData metaData = fieldInfo.getFieldMetaData();
            FieldDisplayInfo displayInfo = fieldInfo.getFieldDisplayInfo();
            
            String dataTypeNatureStr = (metaData.getFieldDataTypeNature() == 
                    DataTypeNature.NATURE_SIMPLE) ? "Simple Datatype" : 
                    "Complex Datatype";
            
            // append the field title to the string buffer. This will be 
            // terminated by a newline character.
            appendTabsToStringBuffer(complexityLevel, buffer);
            buffer.append(fieldName);
            buffer.append("  [");
            buffer.append(metaData.getFieldDataType());
            buffer.append(" : ");
            buffer.append(dataTypeNatureStr);
            buffer.append("]\n");

            // append the display info details of the field. If the field is
            // a complex data type, invoke the same method on the displayinfo
            // object with a complexity level one more than the current object.            
            
            String displayInfoStr = null; 
            if(metaData.getFieldDataTypeNature() 
                                    == DataTypeNature.NATURE_COMPLEX)  {
                displayInfoStr = ((ComplexEntryFieldDisplayInfo)displayInfo
                            ).getComplexFieldInfoStr(complexityLevel + 1);
            } else {
                displayInfoStr = displayInfo.getDisplayInfoStr();
            }
            
            appendTabsToStringBuffer(complexityLevel + 1, buffer);            
            buffer.append(displayInfoStr);
            buffer.append("\n");
        }
        return buffer.toString();
    }
    
    public String getDisplayInfoStr() {
        // Call the getComplexFieldInfoStr function with complexity level of
        // zero. So, no tabs will be appended to the fields of the current 
        // object.
        return getComplexFieldInfoStr(0);
    }
        
    private void appendTabsToStringBuffer(int noOfTabs, 
                            StringBuffer buffer) {
        int tabWidth = 4;
        for(int i = 0; i < noOfTabs * tabWidth; ++i) {
            buffer.append(' '); // append space to the buffer.
        } 
    }
    
    // String representation of this class.
    public String toString() {
        StringBuffer strBuf = new StringBuffer(super.toString());
        if(usePopupWindow) {
            strBuf.append(", Popup File Name: '");
            strBuf.append(this.popupFileName);
        } else {
            strBuf.append(", fieldInfoMap: '");
            strBuf.append(this.fieldInfoMap);
        }

        strBuf.append("'");
        return strBuf.toString();        
    }
}

