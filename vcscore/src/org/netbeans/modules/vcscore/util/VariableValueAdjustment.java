/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * This class is used to adjust variable values so that they can be expanded
 * to the execution string. Some shells needs some characters to be prepended
 * by an escape character to hide their special meaning.
 *
 * @author  Martin Entlicher
 */
public class VariableValueAdjustment {

    /** The variable name, that contains characters, that need to be prepended
     * by a special prefix character before execution.
     * The first character in this string is the prefix character.
     */
    private static final String VAR_ADJUST_CHARS = "ADJUST_CHARS";
    /** The variable names, whose values need to be adjusted before execution.
     * The {@link VAR_ADJUST_CHARS} defines the adjusting characters.
     */
    private static final String VAR_ADJUST_VARS = "ADJUST_VARS";

    private char adjustingChar;
    private HashSet adjustedChars = null;
    private HashSet adjustedVars = null;
    
    /** Creates new VariableValueAdjustment */
    public VariableValueAdjustment() {
    }
    
    public synchronized void setAdjust(Hashtable vars) {
        String adjustCharsStr = (String) vars.get(VAR_ADJUST_CHARS);
        if (adjustCharsStr == null) return ;
        this.adjustingChar = adjustCharsStr.charAt(0);
        this.adjustedChars = new HashSet();
        for (int i = 1; i < adjustCharsStr.length(); i++) {
            adjustedChars.add(new Character(adjustCharsStr.charAt(i)));
        }
        String adjustVarsStr = (String) vars.get(VAR_ADJUST_VARS);
        if (adjustVarsStr == null) return ;
        String[] adjustVars = VcsUtilities.getQuotedStrings(adjustVarsStr);
        this.adjustedVars = new HashSet(Arrays.asList(adjustVars));
    }
    
    /**
     * Adjust the variable values for the command-line execution.
     * @param vars the map of variable names and their values
     */
    public void adjustVarValues(Hashtable vars) {
        if (adjustedVars == null) return ;
        for (Iterator it = adjustedVars.iterator(); it.hasNext(); ) {
            String var = (String) it.next();
            String value = (String) vars.get(var);
            if (value != null) {
                vars.put(var, adjustVarValue(value));
            }
        }
    }

    /**
     * Adjust the variable value for the command-line execution.
     * @param value the value to be adjusted
     * @return the adjusted value
     */
    public String adjustVarValue(String value) {
        if (adjustedChars == null) return value;
        if (value == null) return value;
        for (int i = 0; i < value.length(); i++) {
            Character c = new Character(value.charAt(i));
            if (adjustedChars.contains(c)) {
                value = value.substring(0, i) + adjustingChar + value.substring(i);
                i++;
            }
        }
        return value;
    }

    /**
     * Revert the adjusted variable value.
     * @param value the value to be reverted
     * @return the reverted value
     */
    public String revertAdjustedVarValue(String value) {
        if (adjustedChars == null) return value;
        if (value == null) return value;
        int index = value.indexOf(adjustingChar);
        while(index >= 0 && index < (value.length() - 1)) {
            if (adjustedChars.contains(new Character(value.charAt(index + 1)))) {
                value = value.substring(0, index) + value.substring(index + 1);
            }
            index = value.indexOf(adjustingChar, index + 1);
        }
        return value;
    }

}
