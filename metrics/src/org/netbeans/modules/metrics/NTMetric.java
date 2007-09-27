/*
 * NTMetric.java
 *
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
