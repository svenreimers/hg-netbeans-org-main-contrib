/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.client;

/**
 * This class represents an enumerated type for suggestion priorities.
 *
 * @author Tor Norbye
 */
final public class SuggestionPriority implements Comparable {
    private int priority;
    private SuggestionPriority(int priority) {
        this.priority = priority;
    }

    /** Highest priority */
    public static final SuggestionPriority HIGH =
        new SuggestionPriority(1); // NOI18N

    /** Normal/default priority */
    public static final SuggestionPriority MEDIUM_HIGH =
        new SuggestionPriority(2); // NOI18N

    /** Normal/default priority */
    public static final SuggestionPriority MEDIUM =
        new SuggestionPriority(3); // NOI18N

    /** Normal/default priority */
    public static final SuggestionPriority MEDIUM_LOW =
        new SuggestionPriority(4); // NOI18N

    /** Lowest priority */
    public static final SuggestionPriority LOW =
        new SuggestionPriority(5); // NOI18N

    /** Return a numeric value for the priority. Lower number means
     *  higher priority. Don't depend on the actual values; they may
     *  change without notice. 
     * @return Numeric value for the priority
     */
    public int intValue() {
        return priority;
    }
    
    /** Provides a useful string representation for a particular priority.
     * Not internationalized. Don't depend on this format or content. 
     * @return A string representation of the priority
     */
    public String toString() {
        switch (priority) {
        case 1: return "high priority"; // NOI18N
        case 2: return "medium-high priority"; // NOI18N
        case 3: return "normal priority"; // NOI18N
        case 4: return "medium-low priority"; // NOI18N
        case 5: return "low priority"; // NOI18N
        default: return "error"; // NOI18N
        }
    }

    public int compareTo(Object o) {
        return ((SuggestionPriority)o).priority - priority;
    }
}
