/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */

package org.netbeans.swing.etable;
import java.awt.Component;
import java.util.Comparator;
import java.util.Properties;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import junit.framework.TestCase;

/**
 * Tests for ETableColumn class.
 * @author David Strupl
 */
public class ETableColumnTest extends TestCase {
    
    public ETableColumnTest(String testName) {
        super(testName);
    }

    /**
     * Test of setSorted method, of class org.netbeans.swing.etable.ETableColumn.
     */
    public void testSetSorted() {
        System.out.println("testSetSorted");
        ETableColumn etc = new ETableColumn(2);
        Comparator c = new Comparator() {
            public int compare(Object a1, Object a2) {
                return 0;
            }
        };
        etc.setSorted(2, c);
        
        assertEquals(2, etc.getSortRank());
        assertEquals(c, etc.getComparator());
        assertTrue(etc.isSorted());
        assertTrue(etc.isAscending());
    }

    /**
     * Test of setAscending method, of class org.netbeans.swing.etable.ETableColumn.
     */
    public void testSetAscending() {
        System.out.println("testSetAscending");
        ETableColumn etc = new ETableColumn(2);
        Comparator c = new Comparator() {
            public int compare(Object a1, Object a2) {
                return 0;
            }
        };
        etc.setSorted(2, c);
        etc.setAscending(false);
        
        assertTrue(etc.getComparator() instanceof ETableColumn.FlippingComparator);
        etc.setAscending(true);
        assertFalse(etc.getComparator() instanceof ETableColumn.FlippingComparator);
    }

    /**
     * Test of setHeaderRenderer method, of class org.netbeans.swing.etable.ETableColumn.
     */
    public void testSetHeaderRenderer() {
        System.out.println("testSetHeaderRenderer");
        TableCellRenderer tcr = new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus, 
					    int row, int column) {
                return null;
            }
        };
        ETableColumn etc = new ETableColumn(0);
        etc.setHeaderRenderer(tcr);
        assertEquals("Externally set headerRenderer should be returned, ", tcr, etc.getHeaderRenderer());
    }

    /**
     * Test of getHeaderRenderer method, of class org.netbeans.swing.etable.ETableColumn.
     */
    public void testGetHeaderRenderer() {
        System.out.println("testGetHeaderRenderer");
        ETableColumn etc = new ETableColumn(0);
        TableCellRenderer tcr1 = etc.createDefaultHeaderRenderer();
        TableCellRenderer tcr2 = etc.getHeaderRenderer();
        assertEquals("createDefaultHeaderRenderer and getHeaderRenderer should return the same object, ", tcr1, tcr2);
    }

    /**
     * Test of readSettings and writeSettings methods, of class org.netbeans.swing.etable.ETableColumn.
     */
    public void testReadWriteSettings() {
        System.out.println("testReadWriteSettings");
        ETableColumn etc1 = new ETableColumn(1, 90);
        etc1.setWidth(100);
        etc1.setSorted(3, etc1.getRowComparator(1));
        Properties p = new Properties();
        etc1.writeSettings(p, 1, "test");
        
        ETableColumn etc2 = new ETableColumn();
        etc2.readSettings(p, 1, "test");
        
        assertEquals(3, etc2.getSortRank());
        assertTrue(etc2.isSorted());
        assertTrue(etc2.isAscending());
        assertEquals(etc1.getWidth(), etc2.getWidth());
        assertEquals(etc1.getPreferredWidth(), etc2.getPreferredWidth());
    }

    /**
     * Test of compareTo method, of class org.netbeans.swing.etable.ETableColumn.
     */
    public void testCompareTo() {
        System.out.println("testCompareTo");
        ETableColumn etc1 = new ETableColumn(1);
        ETableColumn etc2 = new ETableColumn(2);
        assertTrue(etc1.compareTo(etc2) < 0);
    }
}
