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

package org.netbeans.modules.tasklist.usertasks.model;

import junit.framework.Test;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

/**
 *
 * @author  Tor Norbye
 */
public class TaskListTest extends NbTestCase {

    public TaskListTest (String name) {
        super (name);
    }
    
    public static void main (String args []) {
        junit.textui.TestRunner.run (TaskListTest.class);
    }
    
    public static Test suite () {
        return new NbTestSuite(TaskListTest.class);
    }

    protected void setUp () throws Exception {
    }

    protected void tearDown () throws Exception {
    }

    /*
    public void testFilter() throws Exception {
        FilterPanel panel = new FilterPanel(view, view.getFilter());
        // TODO
        panel.add(); // etc
        Filter f = panel.getFilter();
    }
    */

    public void testDummy() throws Exception {
        assertTrue(true);
    }
    
    
    /*  This test is not compileable anymore after the filters
        were refactored by Tim.  TODO  Update this test.
    public void testFilterRelations() throws Exception {
        Boolean b;
        Integer i1;
        Integer i2;
        String s1;
        String s2;

        s1 = "This is a test";
        s2 = new String("This is a test"); // make sure it's a unique instance
        assertTrue("Relation.EQUALS operator broken for strings",
                   Filter.Relation.EQUALS.isTrue(s1, s2));
        assertTrue("Relation.NOTEQUALS operator broken for strings",
                   !Filter.Relation.NOTEQUALS.isTrue(s1, s2));
        
        i1 = new Integer(1);
        i2 = new Integer(1);

        assertTrue("Relation.EQUALS operator broken for integers",
                   Filter.Relation.EQUALS.isTrue(i1, i2));

        s2 = "This is another test";
        assertTrue("Relation.EQUALS operator broken for strings",
                   !Filter.Relation.EQUALS.isTrue(s1, s2));
        assertTrue("Relation.NOTEQUALS operator broken for strings",
                   Filter.Relation.NOTEQUALS.isTrue(s1, s2));

        i2 = new Integer(2);
        assertTrue("Relation.NOTEQUALS operator broken for integers",
                   Filter.Relation.NOTEQUALS.isTrue(i1, i2));


        s2 = new String("This IS a test"); // make sure it's a unique instance
        assertTrue("Relation.EQUALS operator broken for strings",
                   Filter.Relation.EQUALS.isTrue(s1, s2));
        assertTrue("Relation.CEQUALS operator broken for strings",
                   !Filter.Relation.CEQUALS.isTrue(s1, s2));
        s2 = "This is another test";
        assertTrue("Relation.CEQUALS operator broken for strings",
                   !Filter.Relation.CEQUALS.isTrue(s1, s2));
        
        i1 = new Integer(10);
        i2 = new Integer(20);
        assertTrue("Relation.LESSTHAN operator broken for integers",
                   Filter.Relation.LESSTHAN.isTrue(i1, i2));
        assertTrue("Relation.LESSTHAN operator broken for integers",
                   !Filter.Relation.LESSTHAN.isTrue(i2, i1));
        assertTrue("Relation.LESSOREQUALS operator broken for integers",
                   Filter.Relation.LESSOREQUALS.isTrue(i1, i2));
        assertTrue("Relation.LESSOREQUALS operator broken for integers",
                   !Filter.Relation.LESSOREQUALS.isTrue(i2, i1));
        i2 = new Integer(10);
        assertTrue("Relation.LESSOREQUALS operator broken for integers",
                   Filter.Relation.LESSOREQUALS.isTrue(i1, i2));
        assertTrue("Relation.LESSTHAN operator broken for integers",
                   !Filter.Relation.LESSTHAN.isTrue(i1, i2));
        assertTrue("Relation.GREATERTHAN operator broken for integers",
                   !Filter.Relation.GREATERTHAN.isTrue(i1, i2));
        i1 = new Integer(-20);
        i2 = new Integer(10);
        assertTrue("Relation.LESSOREQUALS operator broken for integers",
                   Filter.Relation.LESSOREQUALS.isTrue(i1, i2));

        i1 = new Integer(10);
        i2 = new Integer(20);
        assertTrue("Relation.GREATERTHAN operator broken for integers",
                   Filter.Relation.GREATERTHAN.isTrue(i2, i1));
        assertTrue("Relation.GREATERTHAN operator broken for integers",
                   !Filter.Relation.GREATERTHAN.isTrue(i1, i2));
        assertTrue("Relation.GREATEROREQUALS operator broken for integers",
                   Filter.Relation.GREATEROREQUALS.isTrue(i2, i1));
        assertTrue("Relation.GREATEROREQUALS operator broken for integers",
                   !Filter.Relation.GREATEROREQUALS.isTrue(i1, i2));
        i2 = new Integer(10);
        assertTrue("Relation.LESSOREQUALS operator broken for integers",
                   Filter.Relation.LESSOREQUALS.isTrue(i1, i2));
        assertTrue("Relation.LESSOREQUALS operator broken for integers",
                   Filter.Relation.LESSOREQUALS.isTrue(i2, i1));
        Date d1 = new Date(100000);
        Date d2 = new Date(200000);
        assertTrue("Relation.EARLIERTHAN broken for dates",
                   Filter.Relation.EARLIERTHAN.isTrue(d1, d2));
        assertTrue("Relation.EARLIERTHAN broken for dates",
                   !Filter.Relation.EARLIERTHAN.isTrue(d2, d1));
        assertTrue("Relation.LATERTHAN broken for dates",
                   Filter.Relation.LATERTHAN.isTrue(d2, d1));
        assertTrue("Relation.LATERTHAN broken for dates",
                   !Filter.Relation.LATERTHAN.isTrue(d1, d2));

        s1 = "FullString";
        s2 = "ull";
        assertTrue("Relation.CONTAINS broken",
                   Filter.Relation.CONTAINS.isTrue(s1, s2));
        s2 = "uLl";
        assertTrue("Relation.CONTAINS broken",
                   Filter.Relation.CONTAINS.isTrue(s1, s2));
        s2 = "ullsr";
        assertTrue("Relation.CONTAINS broken",
                   !Filter.Relation.CONTAINS.isTrue(s1, s2));


        s1 = "FullString";
        s2 = "ull";
        assertTrue("Relation.CCONTAINS broken",
                   Filter.Relation.CCONTAINS.isTrue(s1, s2));
        s2 = "ulls";
        assertTrue("Relation.CCONTAINS broken",
                   !Filter.Relation.CCONTAINS.isTrue(s1, s2));
        s2 = "uLl";
        assertTrue("Relation.CCONTAINS broken",
                   !Filter.Relation.CCONTAINS.isTrue(s1, s2));

        
        s2 = "Fu";
        assertTrue("Relation.BEGINSWITH broken",
                   Filter.Relation.BEGINSWITH.isTrue(s1, s2));
        s2 = "fullString";
        assertTrue("Relation.CBEGINSWITH broken",
                   Filter.Relation.BEGINSWITH.isTrue(s1, s2));
        s2 = "FullString";
        assertTrue("Relation.BEGINSWITH broken",
                   Filter.Relation.BEGINSWITH.isTrue(s1, s2));
        s2 = "fullString";
        assertTrue("Relation.CBEGINSWITH broken",
                   !Filter.Relation.CBEGINSWITH.isTrue(s1, s2));
        s2 = "FullString2";
        assertTrue("Relation.BEGINSWITH broken",
                   !Filter.Relation.BEGINSWITH.isTrue(s1, s2));
        s2 = "x";
        assertTrue("Relation.BEGINSWITH broken",
                   !Filter.Relation.BEGINSWITH.isTrue(s1, s2));


        s2 = "Fu";
        assertTrue("Relation.CBEGINSWITH broken",
                   Filter.Relation.CBEGINSWITH.isTrue(s1, s2));
        s2 = "FullString";
        assertTrue("Relation.CBEGINSWITH broken",
                   Filter.Relation.CBEGINSWITH.isTrue(s1, s2));
        s2 = "fullString";
        assertTrue("Relation.CBEGINSWITH broken",
                   !Filter.Relation.CBEGINSWITH.isTrue(s1, s2));
        s2 = "FullString2";
        assertTrue("Relation.CBEGINSWITH broken",
                   !Filter.Relation.CBEGINSWITH.isTrue(s1, s2));
        s2 = "x";
        assertTrue("Relation.CBEGINSWITH broken",
                   !Filter.Relation.CBEGINSWITH.isTrue(s1, s2));


        
        s2 = "ing";
        assertTrue("Relation.ENDSWITH broken",
                   Filter.Relation.ENDSWITH.isTrue(s1, s2));
        s2 = "Ing";
        assertTrue("Relation.ENDSWITH broken",
                   Filter.Relation.ENDSWITH.isTrue(s1, s2));
        s2 = "FullString";
        assertTrue("Relation.ENDSWITH broken",
                   Filter.Relation.ENDSWITH.isTrue(s1, s2));
        s2 = "2FullString";
        assertTrue("Relation.ENDSWITH broken",
                   !Filter.Relation.ENDSWITH.isTrue(s1, s2));
        s2 = "x";
        assertTrue("Relation.ENDSWITH broken",
                   !Filter.Relation.ENDSWITH.isTrue(s1, s2));


        s2 = "ing";
        assertTrue("Relation.CENDSWITH broken",
                   Filter.Relation.CENDSWITH.isTrue(s1, s2));
        s2 = "Ing";
        assertTrue("Relation.CENDSWITH broken",
                   !Filter.Relation.CENDSWITH.isTrue(s1, s2));
        s2 = "FullString";
        assertTrue("Relation.CENDSWITH broken",
                   Filter.Relation.CENDSWITH.isTrue(s1, s2));
        s2 = "2FullString";
        assertTrue("Relation.CENDSWITH broken",
                   !Filter.Relation.CENDSWITH.isTrue(s1, s2));
        s2 = "x";
        assertTrue("Relation.CENDSWITH broken",
                   !Filter.Relation.CENDSWITH.isTrue(s1, s2));


        b = Boolean.TRUE;
        assertTrue("Relation.ISTRUE broken",
                   Filter.Relation.ISTRUE.isTrue(b));
        assertTrue("Relation.ISFALSE broken",
                   !Filter.Relation.ISFALSE.isTrue(b));
        b = Boolean.FALSE;
        assertTrue("Relation.ISTRUE broken",
                   !Filter.Relation.ISTRUE.isTrue(b));
        assertTrue("Relation.ISFALSE broken",
                   Filter.Relation.ISFALSE.isTrue(b));

        // Ensure that we don't throw any exceptions for the relations provided for each class
        try {
            Filter.Relation rels[];
            rels = Filter.getRelationsFor(Integer.TYPE);
            for (int i = 0; i < rels.length; i++) {
                rels[i].isTrue(i1, i2);
            }
            rels = Filter.getRelationsFor(String.class);
            for (int i = 0; i < rels.length; i++) {
                rels[i].isTrue(s1, s2);
            }
            rels = Filter.getRelationsFor(Date.class);
            for (int i = 0; i < rels.length; i++) {
                rels[i].isTrue(d1, d2);
            }
            rels = Filter.getRelationsFor(Boolean.TYPE);
            for (int i = 0; i < rels.length; i++) {
                rels[i].isTrue(b);
            }
        } catch (Exception e) {
            fail("Exception thrown during comparisons provided for each class.");
        }
    }
    */
}
