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

package org.netbeans.modules.tasklist.usertasks.filter;

import javax.swing.JComponent;
import org.netbeans.modules.tasklist.filter.OneOfFilterCondition;

import org.netbeans.modules.tasklist.usertasks.DurationPanel;
import org.openide.util.NbBundle;

/**
 * Condition class for comparisons of durations.
 *
 * @author tl
 */
public class DurationFilterCondition extends OneOfFilterCondition {
    public static final int EQUALS = 0;
    public static final int NOTEQUALS = 1;
    public static final int LESSTHAN = 2;
    public static final int LESSOREQUALS = 3;
    public static final int GREATERTHAN = 4;
    public static final int GREATEROREQUALS = 5;
    
    /**
     * Creates an array of filter conditions for the specified property
     *
     * @param index index of the property
     */
    public static DurationFilterCondition[] createConditions() {
        return new DurationFilterCondition[] {
            new DurationFilterCondition(DurationFilterCondition.EQUALS),
            new DurationFilterCondition(DurationFilterCondition.NOTEQUALS),
            new DurationFilterCondition(DurationFilterCondition.LESSTHAN),
            new DurationFilterCondition(DurationFilterCondition.LESSOREQUALS),
            new DurationFilterCondition(DurationFilterCondition.GREATERTHAN),
            new DurationFilterCondition(DurationFilterCondition.GREATEROREQUALS)
        };
    };
    
    private static String[] NAME_KEYS = {
        "Equals", // NOI18N
        "NotEquals", // NOI18N
        "LessThan", // NOI18N
        "LessEquals", // NOI18N
        "GreaterThan", // NOI18N
        "GreaterEquals" // NOI18N
    };
    
    /** saved constant for comparison */
    private int constant;
    
    /**
     * Creates a condition with the given name.
     *
     * @param prop index of the property this condition uses
     * @param id one of the constants from this class
     */
    public DurationFilterCondition(int id) {
        super(NAME_KEYS, id);
    }
    
    /**
     * Copy constructor.
     */
    public DurationFilterCondition( final DurationFilterCondition rhs) {
        super(rhs);
        this.constant = rhs.constant;
    }
    
    public Object clone() {
        return new DurationFilterCondition(this);
    }
    
    public JComponent createConstantComponent() {
        DurationPanel dp = new DurationPanel();
        dp.setOpaque(false);
        dp.setDuration(constant);
        dp.setToolTipText(NbBundle.getMessage(
                DurationFilterCondition.class, "duration_desc")); // NOI18N
        return dp;
    }
    
    public void getConstantFrom(JComponent cmp) {
        DurationPanel dp = (DurationPanel) cmp;
        constant = dp.getDuration();
    }
    
    public boolean isTrue(Object obj) {
        int n = ((Integer) obj).intValue();
        switch (getId()) {
        case EQUALS:
            return constant == n;
        case NOTEQUALS:
            return constant != n;
        case LESSTHAN:
            return n < constant;
        case LESSOREQUALS:
            return n <= constant;
        case GREATERTHAN:
            return n > constant;
        case GREATEROREQUALS:
            return n >= constant;
        default:
            throw new InternalError("wrong id"); // NOI18N
        }
    }
    
    private static class Convertor extends OneOfFilterCondition.Convertor {
        private static final String ELEM_DATE_CONDITION = "DurationCondition";
        private static final String ATTR_DATE = "duration";
        
        public Convertor() {
            super(ELEM_DATE_CONDITION, NAME_KEYS);
        }
        
        public static DurationFilterCondition.Convertor create() {
            return new DurationFilterCondition.Convertor();
        }
        
        protected Object readElement(org.w3c.dom.Element element)
                throws java.io.IOException, java.lang.ClassNotFoundException {
            DurationFilterCondition cond = new DurationFilterCondition(EQUALS);
            super.readCondition(element, cond);
            cond.constant = Integer.parseInt(element.getAttribute(ATTR_DATE));
            return cond;
        }
        
        protected void writeElement(org.w3c.dom.Document document,
                org.w3c.dom.Element element, Object obj)
                throws java.io.IOException, org.w3c.dom.DOMException {
            DurationFilterCondition cond = (DurationFilterCondition)obj;
            super.writeElement(document, element, cond);
            element.setAttribute(ATTR_DATE, Integer.toString(cond.constant));
        }
    }
}
