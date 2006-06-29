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
/*
 * IntMapTest.java
 * JUnit based test
 *
 * Created on March 29, 2004, 6:52 PM
 */

package org.netbeans.collections.numeric;

import java.util.Arrays;
import junit.framework.*;

/**
 *
 * @author Tim Boudreau
 */
public class IntMapTest extends TestCase {

    public IntMapTest(java.lang.String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IntMapTest.class);
        return suite;
    }

    public void testFirst() {
        System.out.println("testFirst");
   IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
        
        assert indices.length == values.length;
        
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }
        
        assertTrue ("First entry should be 5", map.first() == 5);
    }
    
    public void testNextEntry() {
        System.out.println("testNextEntry");
        IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
        
        assert indices.length == values.length;
        
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }
        
        for (int i=0; i < indices.length-1; i++) {
            int val = indices[i+1];
            int next = map.nextEntry (indices[i]);
            assertTrue ("Entry after " + indices[i] + " should be " + val + " not " + next, next == val);
        }
    }
    
    public void testPrevEntry() {
        System.out.println("testPrevEntry");
        IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
        
        assert indices.length == values.length;
        
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }
        
        for (int i=indices.length-1; i > 0; i--) {
            int val = indices[i-1];
            int next = map.prevEntry (indices[i]);
            assertTrue ("Entry before " + indices[i] + " should be " + val + " not " + next, next == val);
        }
    }
    
    public void testNearest() {
        System.out.println("testNearest");
        IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
        
        assert indices.length == values.length;
        
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }
        
        for (int i=0; i < indices.length-1; i++) {
            int toTest = indices[i] + ((indices[i+1] - indices[i]) / 2);
            int next = map.nearest (toTest, false);
            assertTrue ("Nearest value to " + toTest + " should be " + indices[i+1] + ", not " + next, next == indices[i+1]);
        }
        
        assertTrue ("Value after last entry should be 0th", map.nearest (indices[indices.length-1] + 1000, false) == indices[0]);
        
        assertTrue ("Value before first entry should be last", map.nearest (-1, true) == indices[indices.length-1]);
        
        assertTrue ("Value after < first entry should be 0th", map.nearest (-1, false) == indices[0]);
        
        for (int i = indices.length-1; i > 0; i--) {
//            int toTest = indices[i] - (indices[i-1] + ((indices[i] - indices[i-1]) / 2));
            int toTest = indices[i-1] + ((indices[i] - indices[i-1]) / 2);
            int prev = map.nearest (toTest, true);
            assertTrue ("Nearest value to " + toTest + " should be " + indices[i-1] + ", not " + prev, prev == indices[i-1]);
        }
        
        assertTrue ("Entry previous to value lower than first entry should be last entry", 
            map.nearest(indices[0] - 1, true) == indices[indices.length -1]);
        
        assertTrue ("Value after > last entry should be last 0th", map.nearest(indices[indices.length-1] + 100, false) == indices[0]);
        
        assertTrue ("Value before > last entry should be last entry", map.nearest(indices[indices.length-1] + 100, true) == indices[indices.length-1]);
        
        assertTrue ("Value after < first entry should be 0th", map.nearest(-10, false) == indices[0]);
        
    }    
    
    
    /**
     * Test of get method, of class org.netbeans.core.output2.IntMap.
     */
    public void testGet() {
        System.out.println("testGet");
        
        IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
        
        assert indices.length == values.length;
        
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }
        
        for (int i=0; i < indices.length; i++) {
            assertTrue (map.get(indices[i]) == values[i]);
        }
    }
    
    public void testGetKeys() {
        System.out.println("testGetKeys");
        IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
            
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }            

        int[] keys = map.getKeys();
        assertTrue ("Keys returned should match those written.  Expected: " + i2s(indices) + " Got: " + i2s(keys), Arrays.equals(keys, indices));
    }
    
    public void testIter() {
        System.out.println("testIter");
        IntMap map = new IntMap();
        
        int[] indices = new int [] { 5, 12, 23, 62, 247, 375, 489, 5255};
        
        Object[] values = new Object[] {
            "zeroth", "first", "second", "third", "fourth", "fifth", "sixth", 
            "seventh"};
            
        for (int i=0; i < indices.length; i++) {
            map.put (indices[i], values[i]);
        }
        
        IntMap.Iter iter = map.iter();
        int x = 0;
        while (iter.hasNext()) {
            Object o = iter.current();
            int key = iter.next();
            assertEquals (indices[x], key);
            assertEquals (values[x], o);
            x++;
        }
        
        iter = map.nearestIter(7, false);
        assertEquals (12, iter.next());
        
        iter = map.nearestIter (7, true);
        assertEquals (5, iter.next());
    }
    
    public void testSortingBehavior() {
        System.out.println("testSortingBehavior");
        IntMap map = new IntMap();
        int[] indices = new int [] { 23, 5, 17, 100, 12, 23, 62, 6, 247, 375, 2, 489, 5255 };
        
        String[] values = new String[indices.length];
        for (int i=0; i < indices.length; i++) {
            values[i] = Integer.toString(indices[i]) + " val";
            map.put (indices[i], values[i]);
        }
        
        int[] sorted = new int[indices.length];
        System.arraycopy (indices, 0, sorted, 0, sorted.length);
        Arrays.sort (sorted);
        
        IntMap.Iter iter = map.iter();
        int x = 0;
        while (iter.hasNext()) {
            Object o = iter.current();
            int test = iter.next();
            assertEquals (sorted[x], test);
            String value = Integer.toString(sorted[x]) + " val";
            assertEquals (value, o);
            x++;
        }
    }
    
    public void testRemove() {
        System.out.println("testRemove");
        IntMap map = new IntMap();
        int[] indices = new int [] { 23, 5, 17, 100, 12, 23, 62, 6, 247, 375, 2, 489, 5255 };
        
        IntMap map2 = new IntMap();
        
        String[] values = new String[indices.length];
        for (int i=0; i < indices.length; i++) {
            values[i] = Integer.toString(indices[i]) + " val";
            map.put (indices[i], values[i]);
            map2.put (indices[i], values[i]);
        }
        
        int[] sorted = new int[indices.length];
        
        assertTrue (map.containsKey(5));
        assertTrue (map.containsKey(247));
        
        System.err.println("Old map " + map2);
        
        map.remove (5);
        map.remove (247);
        String s = map.toString();
        assertFalse (s, map.containsKey(5));
        assertFalse (s, map.containsKey(247));
        
        for (IntMap.Iter i=map.iter(); i.hasNext();) {
            Object o = i.current();
            int key = i.next();
            assertFalse (247 == key);
            assertFalse (5 == key);
            assertEquals (map2.get(key), o);
        }
    }
    
    private static String i2s (int[] a) {
        StringBuffer result = new StringBuffer(a.length*2);
        for (int i=0; i < a.length; i++) {
            result.append (a[i]);
            if (i != a.length-1) {
                result.append(',');
            }
        }
        return result.toString();
    }
}
