/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.bugs.scarab;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Date;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


/** Represents on issue in issuezilla.
 * Created by {@link Issuezilla#getBug}
 *
 *
 * tor@netbeans.org:
 * This class is virtually identical to
 *  nbbuild/antsrc/org/netbeans/nbbuild/Issue.java
 * At first, I inclouded its class file directly as part of
 * the build. However, treating Issuezilla as a black box
 * didn't work well because when connections fail (and are
 * retried), or even during a query, there is no feedback - and
 * since issuezilla is so slow, it's hard to know in the GUI
 * that things are working. Therefore, I've modified the java
 * file to give us a little bit more feedback.
 * In CVS I stored the original file as the first revision,
 * so you can easily diff to see what has changed - and generate
 * a patch which you can then apply to an updated version
 * of nbbuild/antsrc/ to keep the two in sync.
 *
 * serff@netbeans.org:
 * This class is almost exactally the same as issuezilla.Issue, but modified to 
 * work with bugzilla. I didn't want to call this class Bug because of the 
 * higher level Bug class.  If you can think of a better name, please let me know
 *
 * @todo think of a better name.
 *
 * @author Ivan Bradac, refactored by Jaroslav Tulach
 */
public final class Issue implements Comparable {

    private HashMap attributes = new HashMap (49);
    
    static final String ISSUE_TYPE = "issue_type";
    static final String ISSUE_ID = "issue_id";
    static final String REPORTER = "created_by";
    static final String ASSIGNED_TO = "assigned_to";
    static final String CREATED = "created";
    static final String SUMMARY = "Summary";
    static final String STATUS = "Status";
    static final String PRIORITY = "Priority";
    static final String COMPONENT = "Component";
    static final String SUBCOMPONENT = "Subomponent";
    static final String KEYWORDS = "Keywords";
    static final String TARGET = "Target";
    static final String VOTES = "Votes";

    /**
     * Gets the id as an Integer.
     *
     * @return the issue_id as 
     */
    public String getId() {
        return string(ISSUE_ID);
    }

    /** Who is assigned to this bug.
     * @return name of person assigned to this bug
     */
    public String getAssignedTo () {
        return string (ASSIGNED_TO);
    }

    /** Who reported the bug.
     * @return name of the reporter
     */
    public String getReportedBy () {
        return string (REPORTER);
    }


    /** Type of the issue: Bug, Enhancement, Task, etc...
     * @return textual name of issue type
     */
    public String getType () {
        return string (ISSUE_TYPE);
    }

    
    /** A time when this issue has been created.
     * @return the date or begining of epoch if wrongly defined
     */
    public Date getCreated () {
        Date d = (Date)getAttribute (CREATED);
        return d == null ? new Date (0) : d;
    }

    /** Getter to return string for given attribute.
     */
    private String string (String name) {
        Object o = getAttribute (name);
        return o instanceof String ? (String)o : "";
    }
    
    /** Getter for array of integers.
     */
    private int[] ints (String name) {
        List l = (List)getAttribute (name);
        if (l == null) {
            return new int[0];
        }
        
        int[] arr = new int[l.size ()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt ((String)l.get (i));
        }
        return arr;
    }

    /** Package private getter, it is expected to add getter for useful
     * issues.
     */
    Object getAttribute(String name) {
        return attributes.get(name);
    }


    /** Setter of values, package private. */
    void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    /** Converts the object to textual representation.
     * @return a text description of the issue
     */
    public String toString() {   
        StringBuffer buffer;
        if (attributes == null) {
            return java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Empty_BugBase");
        }
        Iterator it = attributes.entrySet().iterator();
        buffer = new StringBuffer();
        buffer.append(this.getClass().getName() 
                      + java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("_containing_these_name/value_attribute_pairs:\n"));
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            buffer.append(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("NAME__:_") + entry.getKey() + "\n");
            buffer.append(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("VALUE_:_") + entry.getValue() + "\n");      
        }
        return buffer.toString();
    }

    /** Compares issues by their ID
     */
    public int compareTo (final Object o) {
        final Issue i = (Issue)o;
        return getId ().compareTo(i.getId ());
    }

    public String getSummary()
    {
        return string(SUMMARY);
    }

    String getStatus()
    {
        return string(STATUS);
    }

}
