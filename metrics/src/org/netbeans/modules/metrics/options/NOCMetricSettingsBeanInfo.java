/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.metrics.options;

import java.beans.*;

/**
 * BeanInfo for the NOCMetricSettings object.
 *
 * @author tball
 */
public class NOCMetricSettingsBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] props = {
        Util.makeProperty(NOCMetricSettings.PROP_DEFAULT_WARNING, 
                          NOCMetricSettings.class,
                          "getWarningLevel", 
                          "setWarningLevel",
                          "PROP_warningLevel",
                          "HINT_warningLevel"),
        Util.makeProperty(NOCMetricSettings.PROP_DEFAULT_ERROR,
                          NOCMetricSettings.class,
                          "getErrorLevel", 
                          "setErrorLevel",
                          "PROP_errorLevel",
                          "HINT_errorLevel")
    };

    public PropertyDescriptor[] getPropertyDescriptors() {
	return props;
    }
}
