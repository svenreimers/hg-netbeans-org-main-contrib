/*
 * NTMetric.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.metrics.options.*;

import java.text.MessageFormat;
import java.util.*;

/**
 * The "Number of Tramps" metric class.  A tramp is traditionally defined
 * as an unused parameter in a method.  For Java, this definition has
 * been enhanced to ignore unused parameters for methods whose signature
 * is either defined by an interface or a superclass.  In those cases,
 * the class author does not control the method's definition.
 *
 * @author  tball
 */
public class NTMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_NTMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_NTMetric");

    /** Creates new NTMetric */
    protected NTMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "NTMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }
    
    public MetricSettings getSettings() {
	return NTMetricSettings.getDefault();
    }

    private void buildMetric() {
        if (metric == null) {
            MessageFormat detailsMsgTemplate = new MessageFormat(
                MetricsNode.bundle.getString ("STR_TrampDetails"));
            MessageFormat selfMsgTemplate = new MessageFormat(
                MetricsNode.bundle.getString ("STR_SelfTramp"));
            Object[] msgParams = new Object[2];

            Set detailMsgs = new TreeSet();
            int totalTramps = 0;

            Iterator iter = classMetrics.getMethods().iterator();
            while (iter.hasNext()) {
                MethodMetrics mm = (MethodMetrics)iter.next();
                if (!mm.isSynthetic() && mm.hasTramps()) {
                    totalTramps += mm.getTrampCount();
                    Iterator iter2 = mm.getTramps();
                    while (iter2.hasNext()) {
                        String msg;
                        MethodMetrics.Parameter p = 
                            (MethodMetrics.Parameter)iter2.next();
                        int pIdx = p.getIndex();
                        msgParams[0] = mm.getFullName();
                        msgParams[1] = new Integer(pIdx);
                        if (pIdx == 0) {
                            msg = selfMsgTemplate.format(msgParams);
                        } else {
                            msg = detailsMsgTemplate.format(msgParams);
                        }
                        detailMsgs.add(msg);
                    }
                }
            }
            metric = new Integer(totalTramps);
            
            StringBuffer sb = new StringBuffer();
            sb.append(MetricsNode.bundle.getString ("STR_Tramps"));
            iter = detailMsgs.iterator();
            while (iter.hasNext()) {
                sb.append("\n   ");
                sb.append((String)iter.next());
            }
            details = sb.toString();
        }
    }
    
    public Integer getMetricValue() {
        buildMetric();
        return metric;
    }
    
    public String getDetails() {
        buildMetric();
        return details;
    }

    public boolean needsOtherClasses() {
        return false;
    }

    public boolean isMethodMetric() {
	return true;
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	return new Integer(mm.getTrampCount());
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new NTMetric(cm);
	}
	public Class getMetricClass() {
	    return NTMetric.class;
	}
    }
}
