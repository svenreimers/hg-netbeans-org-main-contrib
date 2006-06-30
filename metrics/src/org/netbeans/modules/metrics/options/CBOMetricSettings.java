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

package org.netbeans.modules.metrics.options;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/** Options for CBO metric.
 *
 * @author  tball
 */
public class CBOMetricSettings extends SystemOption implements MetricSettings {

    private static final long serialVersionUID = 6187796499098479549L;

    public static final String PROP_DEFAULT_WARNING =
        "CBOMetric.default_warning_value";
    public static final String PROP_DEFAULT_ERROR =
        "CBOMetric.default_error_value";
    public static final String PROP_INCLUDE_JDK_CLASSES = 
        "CBOMetric.include_JDK_classes";
    public static final String PROP_INCLUDE_OPENIDE_CLASSES = 
        "CBOMetric.include_OpenIDE_classes";

    /** Singleton instance */
    private static CBOMetricSettings singleton;

    protected void initialize () {
        super.initialize ();
        setWarningLevel(5);
        setErrorLevel(10);
        setIncludeJDKClasses(true);
        setIncludeOpenIDEClasses(true);
    }

    public String displayName () {
        return NbBundle.getMessage(CBOMetricSettings.class, "LBL_CBOMetricSettings");
    }

    /** Default instance of this system option. */
    public static CBOMetricSettings getDefault() {
        if (singleton == null) {
            singleton = (CBOMetricSettings) 
                findObject(CBOMetricSettings.class, true);
        }
        return singleton;
    }

    public int getWarningLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_WARNING)).intValue();
    }

    public void setWarningLevel (int value) {
        // Automatically fires property changes if needed etc.:
        putProperty(PROP_DEFAULT_WARNING, new Integer(value), true);
    }

    public int getErrorLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_ERROR)).intValue();
    }

    public void setErrorLevel (int value) {
        putProperty(PROP_DEFAULT_ERROR, new Integer(value), true);
    }

    public boolean includeJDKClasses() {
        return ((Boolean)getProperty(PROP_INCLUDE_JDK_CLASSES)).booleanValue();
    }

    public void setIncludeJDKClasses(boolean value) {
        putProperty(PROP_INCLUDE_JDK_CLASSES, value ? Boolean.TRUE : Boolean.FALSE, true);
    }

    public boolean includeOpenIDEClasses() {
        return ((Boolean)getProperty(PROP_INCLUDE_OPENIDE_CLASSES)).booleanValue();
    }

    public void setIncludeOpenIDEClasses(boolean value) {
        putProperty(PROP_INCLUDE_OPENIDE_CLASSES, value ? Boolean.TRUE : Boolean.FALSE, true);
    }

    // this metric doesn't support per-method values
    public int getMethodWarningLevel() { return -1; }
    public void setMethodWarningLevel(int value) {}
    public int getMethodErrorLevel() { return -1; }
    public void setMethodErrorLevel(int value) {}
}
