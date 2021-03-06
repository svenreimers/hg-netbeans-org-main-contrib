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

package org.netbeans.modules.vcs.advanced.variables;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents a condition, that defines a variable as true or false
 * depending on values of other variables.
 * This class is not thread safe. If variables or sub-conditions are set in one
 * thread, the condition should not be queried in another thread at the same time.
 *
 * @author  Martin Entlicher
 */
public final class Condition extends Object implements Cloneable {
    
    /**
     * The variable value is tested for the equality with the specified value.
     */
    public static final int COMPARE_VALUE_EQUALS = 0;
    /**
     * The variable value is tested for the equality with the specified value,
     * but a difference in upper/lower case is ignored.
     */
    public static final int COMPARE_VALUE_EQUALS_IGNORE_CASE = 1;
    /**
     * The variable value is tested whether it contains the specified value
     * as a substring.
     */
    public static final int COMPARE_VALUE_CONTAINS = 2;
    /**
     * The variable value is tested whether it contains the specified value
     * as a substring, but a difference in upper/lower case is ignored.
     */
    public static final int COMPARE_VALUE_CONTAINS_IGNORE_CASE = 3;
    
    public static final int LOGICAL_AND = 0;
    public static final int LOGICAL_OR = 1;
    
    private String name;
    private int operation;
    /** The variables to compare with a Boolean positive comparison flag. */
    private Map cmpVars = new IdentityHashMap();
    /** The sub-conditions with a Boolean positive comparison flag. */
    private Map cmpCond = new IdentityHashMap();
    
    /**
     * Creates a new instance of Condition with default logical operation being
     * {@link #LOGICAL_AND}
     * @param name The name of the variable
     */
    public Condition(String name) {
        this.name = name;
        this.operation = LOGICAL_AND;
    }
    
    /**
     * Get the name of the condition (== the name of the variable, that should
     * be set by this condition.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the condition (== the name of the variable, that should
     * be set by this condition.
     * @param name The name of the condition.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Add a new condition for a variable value.
     * @param name The name of the variable to which the condition is to be applied.
     * @param value The value that is to be compared to the variable value.
     * @param compareValue The type of the comparison of the variable value
     * @param positive Whether the comparison should be positive or negative
     */
    public void addVar(String name, String value, int compareValue, boolean positive) {
        Var var = new Var(name, value, compareValue);
        addVar(var, positive);
    }
    
    /**
     * Add a new condition for a variable value.
     * @param var The variable pattern to test the variables with.
     * @param positive Whether the comparison should be positive or negative
     */
    public void addVar(Var var, boolean positive) {
        if (var == null) throw new NullPointerException("Null variable can not be added.");
        cmpVars.put(var, positive ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Remove a variable value.
     * @param var The variable pattern to be removed.
     */
    public void removeVar(Var var) {
        cmpVars.remove(var);
    }
    
    /**
     * Get the list of all variable patterns that are tested in this condition.
     */
    public Var[] getVars() {
        return (Var[]) cmpVars.keySet().toArray(new Var[0]);
    }
    
    /**
     * Find out whether the variable pattern is tested in a positive way.
     */
    public boolean isPositiveTest(Var var) {
        Boolean positive = (Boolean) cmpVars.get(var);
        if (positive != null) {
            return positive.booleanValue();
        } else {
            return true;
        }
    }
    
    /**
     * Add a new sub-condition.
     * @param condition The sub-condition to add.
     * @param positive Whether the comparison should be positive or negative
     */
    public void addCondition(Condition condition, boolean positive) {
        if (condition == null) throw new NullPointerException("Null condition can not be added.");
        cmpCond.put(condition, positive ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Remove a sub-condition.
     * @param var The variable pattern to be removed.
     */
    public void removeCondition(Condition condition) {
        cmpCond.remove(condition);
    }
    
    /**
     * Get the list of all sub-conditions that are tested in this condition.
     */
    public Condition[] getConditions() {
        return (Condition[]) cmpCond.keySet().toArray(new Condition[0]);
    }
    
    /**
     * Find out whether the sub-condition is tested in a positive way.
     */
    public boolean isPositiveTest(Condition c) {
        Boolean positive = (Boolean) cmpCond.get(c);
        if (positive != null) {
            return positive.booleanValue();
        } else {
            return true;
        }
    }
    
    /**
     * Set the logical operation that is applied to the variables.
     */
    public void setLogicalOperation(int operation) {
        this.operation = operation;
    }
    
    /**
     * Get the logical operation that is applied to the variables.
     */
    public int getLogicalOperation() {
        return operation;
    }
    
    /*
    private boolean checkIntegrity() {
        boolean success = true;
        for (Iterator cmpCondIt = cmpCond.keySet().iterator(); cmpCondIt.hasNext(); ) {
            Condition c = (Condition) cmpCondIt.next();
            Boolean b = (Boolean) cmpCond.get(c);
            if (b == null) {
                System.out.println("ERROR in Condition: c = "+this+", sub_c = "+c+", positive = "+b);
                success = false;
            }
        }
        if (!success) {
            for (Iterator cmpCondIt = cmpCond.keySet().iterator(); cmpCondIt.hasNext(); ) {
                Condition c = (Condition) cmpCondIt.next();
                Boolean b = (Boolean) cmpCond.get(c);
                System.out.println("  c = "+c+", b = "+b+"; name = '"+name+"'");
            }
        }
        return success;
    }
     */
    
    /**
     * Tells, whether the condition is satisfied for the given map of variable
     * names and their values.
     */
    public boolean isSatisfied(Map vars) {
        //if (!checkIntegrity()) System.out.println("isSatisfied() Integrity check failed.");
        boolean satisfied;
        if (operation == LOGICAL_AND) {
            satisfied = true;
            for (Iterator cmpVarsIt = cmpVars.keySet().iterator(); cmpVarsIt.hasNext(); ) {
                Var cmpVar = (Var) cmpVarsIt.next();
                boolean positive = ((Boolean) cmpVars.get(cmpVar)).booleanValue();
                satisfied = satisfied && (cmpVar.isSatisfied(vars) == positive);
                if (!satisfied) break;
            }
            if (satisfied) {
                for (Iterator cmpCondIt = cmpCond.keySet().iterator(); cmpCondIt.hasNext(); ) {
                    Condition c = (Condition) cmpCondIt.next();
                    boolean positive = ((Boolean) cmpCond.get(c)).booleanValue();
                    satisfied = satisfied && (c.isSatisfied(vars) == positive);
                    if (!satisfied) break;
                }
            }
        } else if (operation == LOGICAL_OR) {
            satisfied = false;
            for (Iterator cmpVarsIt = cmpVars.keySet().iterator(); cmpVarsIt.hasNext(); ) {
                Var cmpVar = (Var) cmpVarsIt.next();
                boolean positive = ((Boolean) cmpVars.get(cmpVar)).booleanValue();
                satisfied = satisfied || (cmpVar.isSatisfied(vars) == positive);
                if (satisfied) break;
            }
            if (!satisfied) {
                for (Iterator cmpCondIt = cmpCond.keySet().iterator(); cmpCondIt.hasNext(); ) {
                    Condition c = (Condition) cmpCondIt.next();
                    boolean positive = ((Boolean) cmpCond.get(c)).booleanValue();
                    satisfied = satisfied || (c.isSatisfied(vars) == positive);
                    if (satisfied) break;
                }
            }
        } else { // Unknown logical operation!
            satisfied = false;
        }
        return satisfied;
    }
    
    /**
     * Override equals() method so that we are able to check whether two
     * conditions will behave the same way.
     * @return true if the conditions are in fact the same logical conditions.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Condition)) return false;
        Condition c = (Condition) o;
        if (!this.name.equals(c.name)) return false;
        if (this.operation != c.operation) return false;
        if (!this.cmpVars.equals(c.cmpVars)) return false;
        if (!this.cmpCond.equals(c.cmpCond)) return false;
        return true;
    }
    
    /**
     * Override hashCode() to return the same value for equal conditions.
     */
    public int hashCode() {
        return name.hashCode() + operation + cmpVars.hashCode() + cmpCond.hashCode();
    }
    
    public Object clone() {
        Condition c = new Condition(name);
        c.operation = operation;
        for (Iterator it = cmpVars.keySet().iterator(); it.hasNext(); ) {
            Var cmpVar = (Var) it.next();
            c.cmpVars.put(cmpVar.clone(), cmpVars.get(cmpVar));
        }
        for (Iterator it = cmpCond.keySet().iterator(); it.hasNext(); ) {
            Condition cc = (Condition) it.next();
            c.cmpCond.put(cc.clone(), cmpCond.get(cc));
        }
        return c;
    }
    
    public static String printCondition(Condition c) {
        StringBuffer b = new StringBuffer();
        b.append(c.getName());
        b.append(": ");
        b.append((c.getLogicalOperation() == c.LOGICAL_AND) ? "AND" : "OR");
        Condition.Var[] vars = c.getVars();
        b.append(" vars = ");
        for (int i = 0; i < vars.length; i++) {
            if (!c.isPositiveTest(vars[i])) {
                b.append("!");
            }
            b.append("(");
            b.append(vars[i].getName());
            b.append(", '");
            b.append(vars[i].getValue());
            b.append("', ");
            b.append(vars[i].getCompareValue());
            b.append(")");
        }
        Condition[] subc = c.getConditions();
        if (subc.length > 0) {
            b.append(" subcond = ");
            for (int i = 0; i < subc.length; i++) {
                if (!c.isPositiveTest(subc[i])) {
                    b.append("!");
                }
                b.append("(");
                b.append(printCondition(subc[i]));
                b.append(")");
            }
        }
        return b.toString();
    }
    
    /**
     * The variable pattern that is used to test the variables with.
     */
    public static final class Var extends Object implements Cloneable {
        
        private String name;
        private String value;
        private int compareValue;
        
        /**
         * Create a new test pattern.
         * @param name The name of the variable that is to be tested
         * @param value The value that is to be compared to the variable value.
         * @param compareValue The type of the comparison of the variable value
         */
        public Var(String name, String value, int compareValue) {
            this.name = name;
            this.value = value;
            this.compareValue = compareValue;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        public int getCompareValue() {
            return compareValue;
        }
        
        public void setCompareValue(int compareValue) {
            this.compareValue = compareValue;
        }
        
        /**
         * Tells, whether this variable is compared successfully to the
         * appropriate variable in the given map of variable names and their
         * values.
         */
        public boolean isSatisfied(Map vars) {
            String varValue = (String) vars.get(name);
            if (varValue == null) varValue = ""; // NOI18N
            return (compareValue == COMPARE_VALUE_EQUALS && varValue.equals(value) ||
                    compareValue == COMPARE_VALUE_EQUALS_IGNORE_CASE && varValue.equalsIgnoreCase(value) ||
                    compareValue == COMPARE_VALUE_CONTAINS && varValue.indexOf(value) >= 0 ||
                    compareValue == COMPARE_VALUE_CONTAINS_IGNORE_CASE && varValue.toUpperCase().indexOf(value.toUpperCase()) >= 0);
        }
        
        /**
         * Override equals() method so that we are able to check whether two
         * variable comparisons will behave the same way.
         * @return true if the variable comparisons are in fact the same logical
         *         conditions.
         */
        public boolean equals(Object o) {
            if (!(o instanceof Var)) return false;
            Var v = (Var) o;
            if (!this.name.equals(v.name)) return false;
            if (this.value == null) {
                if (v.value != null) return false;
            } else {
                if (!this.value.equals(v.value)) return false;
            }
            return this.compareValue == v.compareValue;
        }
        
        /**
         * Override hashCode() to return the same value for equal conditions.
         */
        public int hashCode() {
            return name.hashCode() + value.hashCode() + compareValue;
        }
        
        public Object clone() {
            return new Var(name, value, compareValue);
        }
    
    }
    
}
