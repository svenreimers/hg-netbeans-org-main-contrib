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

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.core.TaskProperties;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;



/**
 *
import org.netbeans.modules.tasklist.core.filter.SuggestionProperty;ocation.
 * This class is coupled to SuggestionImpl, but defined outside just
 * for code readability.
 */
public class SuggestionImplProperties extends TaskProperties   {

    public static final String PROPID_LINE_NUMBER = "line";
    public static final String PROPID_FILENAME = "file";
    public static final String PROPID_LOCATION = "location";
    public static final String PROPID_CATEGORY = "category";
    
    
    public static SuggestionProperty getProperty(String propID) {
        if (propID.equals(PROPID_LINE_NUMBER)) { return PROP_LINE_NUMBER;}
        else if (propID.equals(PROPID_FILENAME)) { return PROP_FILENAME;}
        else if (propID.equals(PROPID_LOCATION)) { return PROP_LOCATION;}
        else if (propID.equals(PROPID_CATEGORY)) { return PROP_CATEGORY;}
        else return TaskProperties.getProperty(propID);
    }
    
    public static final SuggestionProperty PROP_LINE_NUMBER =
    new SuggestionProperty(PROPID_LINE_NUMBER, Integer.class) {
        public Object getValue(Object obj) {return new Integer(((SuggestionImpl) obj).getLineNumber()); }
    };
    
    public static final SuggestionProperty PROP_FILENAME =
    new SuggestionProperty(PROPID_FILENAME, String.class) {
        public Object getValue(Object obj) {return ((SuggestionImpl) obj).getFileBaseName(); }
    };
    
    public static final SuggestionProperty PROP_LOCATION =
    new SuggestionProperty(PROPID_LOCATION, String.class) {
        public Object getValue(Object obj) {return ((SuggestionImpl) obj).getLocation(); }
    };
    
    public static final SuggestionProperty PROP_CATEGORY =
    new SuggestionProperty(PROPID_CATEGORY, String.class) {
        public Object getValue(Object obj) {return ((SuggestionImpl)obj).getCategory(); }
    };
    
}

