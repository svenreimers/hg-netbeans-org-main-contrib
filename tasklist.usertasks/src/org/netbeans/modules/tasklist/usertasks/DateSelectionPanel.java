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
package org.netbeans.modules.tasklist.usertasks;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

/**
 * This is a small panel to allow the user to select a full date. When I
 * started the implementation of the Alarm functionality I had the user to
 * write the complete time/date, and I pretty soon realized that noone will
 * remember the format each time.... Well, the panel "works for me now" so I
 * move on to the next phase in my project, but one should really:
 *
 * @todo The panel is too big...
 *
 * @author  Trond Norbye
 */
public class DateSelectionPanel extends javax.swing.JPanel
    implements EnhancedCustomPropertyEditor {

    private static final long serialVersionUID = 1;

    /**
     * A calendar object I use for varius things (mostly for converting values
     * to string representation...
     */
    private GregorianCalendar       calendar;
    /**
     * A SimpleDateFormat I use for conversion to/from textual representation
     * of date fields
     */
    private SimpleDateFormat        format;
    /**
     * The tablemodel behind the day-selection table (most of the 
     * &quot;logic&quot; resides inside this class....
     */
    private DateSelectionTableModel tablemodel;
    /**
     * The renderer I use to render the cells in the JTable (I wanted the
     * day label to be centered, and todays date to be red....
     */
    private CalendarRenderer        renderer;
    
    /** Creates new form DateSelectionPanel */
    public DateSelectionPanel() {
        this(new Date());
    }
    
    /**
     * Create a new DateSelectionPanel with the given date selected...
     *
     * @param date initial selection
     */
    public DateSelectionPanel(Date date) {
        initComponents();

        // The table model will never change, so I might as well store it
        // here so I don't need to get it each time...
        tablemodel = (DateSelectionTableModel)calendarTable.getModel();
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        renderer = new CalendarRenderer();
        calendarTable.setDefaultRenderer(String.class, renderer);
                
        calendar = new GregorianCalendar();
        yearFld.setText(Integer.toString(calendar.get(Calendar.YEAR)));
        
        format = new SimpleDateFormat("HH:mm:ss"); // NOI18N
        timeFld.setText(format.format(date));        
        format.applyPattern("MMMM"); // NOI18N
        
        int curr = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH,
        calendar.getActualMinimum(Calendar.MONTH));
        
        int no = calendar.getActualMaximum(Calendar.MONTH) - calendar.getActualMinimum(Calendar.MONTH);
        
        for (int ii = 0; ii <= no; ++ii) {
            monthNameCmb.insertItemAt(format.format(calendar.getTime()), ii);
            calendar.roll(Calendar.MONTH, 1);
        }
        monthNameCmb.setSelectedIndex(curr);
        calendar.setTime(date);
        tablemodel.setMonth(calendar.get(Calendar.MONTH));
        tablemodel.setYear(calendar.get(Calendar.YEAR));
        tablemodel.setSelectedDay(calendar.get(Calendar.DAY_OF_MONTH));
        
        calendarTable.getTableHeader().setReorderingAllowed(false);
        calendarTable.getTableHeader().setResizingAllowed(false);
    }
    
    /**
     * Returns the selected date
     *
     * @return selected date
     */
    public Date getDate() {
        Date ret;
        try {
            format.applyPattern("HH:mm:ss"); // NOI18N
            calendar.setTime(format.parse(timeFld.getText()));
            calendar.set(Calendar.MONTH, monthNameCmb.getSelectedIndex());
            calendar.set(Calendar.YEAR, Integer.parseInt(yearFld.getText()));
            calendar.set(Calendar.DAY_OF_MONTH, tablemodel.getSelectedDay());            
            ret = calendar.getTime();
        } catch (Exception e) {
            // When I'm done, this should never happen....
            ret = null;
        }
        
        return ret;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        monthNameCmb = new javax.swing.JComboBox();
        prevYearBtn = new javax.swing.JButton();
        yearFld = new javax.swing.JTextField();
        nextYearBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        timeFld = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        calendarTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(11, 11, 12, 12)));
        jPanel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 11, 0)));
        monthNameCmb.setMaximumRowCount(12);
        monthNameCmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthNameCmbActionPerformed(evt);
            }
        });

        jPanel1.add(monthNameCmb);

        prevYearBtn.setText("<");
        prevYearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevYearBtnActionPerformed(evt);
            }
        });

        jPanel1.add(prevYearBtn);

        yearFld.setColumns(4);
        yearFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yearFld.setInputVerifier(new javax.swing.InputVerifier() {
            public boolean verify(javax.swing.JComponent obj) {
                boolean ret;
                try {
                    Integer.parseInt(((javax.swing.JTextField)obj).getText());
                    ret = true;
                } catch (Exception e) {
                    ret = false;
                }
                return ret;
            }
        });
        yearFld.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                yearFldFocusLost(evt);
            }
        });

        jPanel1.add(yearFld);

        nextYearBtn.setText(">");
        nextYearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextYearBtnActionPerformed(evt);
            }
        });

        jPanel1.add(nextYearBtn);

        jLabel1.setText("@");
        jPanel1.add(jLabel1);

        timeFld.setColumns(8);
        timeFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeFld.setInputVerifier(new javax.swing.InputVerifier() {
            public boolean verify(javax.swing.JComponent obj) {
                boolean ret;
                try {
                    format.applyPattern("HH:mm:ss");
                    format.parse(((javax.swing.JTextField)obj).getText());
                    ret = true;
                } catch (Exception e) {
                    ret = false;
                }
                return ret;
            }
        });
        jPanel1.add(timeFld);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridLayout(1, 1));

        calendarTable.setModel(new DateSelectionTableModel());
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setShowHorizontalLines(false);
        calendarTable.setShowVerticalLines(false);
        jScrollPane1.setViewportView(calendarTable);

        jPanel2.add(jScrollPane1);

        add(jPanel2, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    /**
     * This is probably not the "correct" place to do this, but it works as
     * intended...
     * @param evt not used
     */
    private void yearFldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yearFldFocusLost
        int year = Integer.parseInt(yearFld.getText());
        tablemodel.setYear(year);
    }//GEN-LAST:event_yearFldFocusLost
    
    /**
     * The user pressed the "&lt;" button.. The year field should be decremented
     * @param evt the event
     */
    private void prevYearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevYearBtnActionPerformed
        if (evt.getID() == ActionEvent.ACTION_PERFORMED) {
            int year = Integer.parseInt(yearFld.getText()) - 1;
            yearFld.setText(Integer.toString(year));
            tablemodel.setYear(year);            
        }
    }//GEN-LAST:event_prevYearBtnActionPerformed

    /**
     * The user pressed the "&gt;" button.. The year field should be incremented
     * @param evt the event
     */
    private void nextYearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextYearBtnActionPerformed
        if (evt.getID() == ActionEvent.ACTION_PERFORMED) {
           int year = Integer.parseInt(yearFld.getText()) + 1;
           yearFld.setText(Integer.toString(year));
           tablemodel.setYear(year);            
        }
    }//GEN-LAST:event_nextYearBtnActionPerformed
    
    /**
     * The user changed the month combo..
     * @param evt not used
     */
    private void monthNameCmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthNameCmbActionPerformed
        if (evt.getID() == ActionEvent.ACTION_PERFORMED) {
            tablemodel.setMonth(monthNameCmb.getSelectedIndex());
        }
    }//GEN-LAST:event_monthNameCmbActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable calendarTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox monthNameCmb;
    private javax.swing.JButton nextYearBtn;
    private javax.swing.JButton prevYearBtn;
    private javax.swing.JTextField timeFld;
    private javax.swing.JTextField yearFld;
    // End of variables declaration//GEN-END:variables
    
    /**
     * An inner class used by the table
     */
    private class DateSelectionTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1;

        private Object            columnNames[];
        private GregorianCalendar calendar, today;
        private Integer           days[][];
        
        /**
         * Create a new table model
         */
        public DateSelectionTableModel() {
            columnNames = new Object[7];
            calendar = new GregorianCalendar();
            SimpleDateFormat format = new SimpleDateFormat("EEEE"); // NOI18N
            
            calendar.set(Calendar.DAY_OF_WEEK,             
                         calendar.getFirstDayOfWeek());
            
            
            for (int ii = 0; ii < 7; ++ii) {
                columnNames[ii] = format.format(calendar.getTime());
                calendar.roll(Calendar.DAY_OF_WEEK, 1);
            }
            
            today = new GregorianCalendar();
            // Reset the calendar since I might have changed the current month
            calendar.setTime(new Date());
            
            days = new Integer[6][7];
        }
        
        /**
         * Set a day in the month as selected
         * @param the day to select
         */
        void setSelectedDay(int day) {
            for (int yy = 0; yy < days.length; ++yy) {
                for (int xx = 0; xx < days[yy].length; ++xx) {
                    if (days[yy][xx] != null) {
                        Integer i = (Integer)days[yy][xx];
                        if (i.intValue() == day){ 
                            calendarTable.changeSelection(yy, xx, false, false);                        
                            return;
                        }   
                    }
                }
            }
        }

        /**
         * Get the currently selected day (or the first / last day of the
         * month if a blank field (or none) was selected
         * @return the selected day
         */
        public int getSelectedDay() {
            int x = calendarTable.getSelectedColumn();
            int y = calendarTable.getSelectedRow();
                        
            int retval;
            
            if (x == -1 || y == -1) {
                retval = 1;
            } else if (days[y][x] != null) {
                retval = ((Integer)days[y][x]).intValue();                
            } else {
                if (y == 0) {
                    retval = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                } else {
                    retval = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);                    
                }
            }
            return retval;
        }
        
        /**
         * Lay out all of the day number correctly
         */
        public void updateDays() {
            calendar.set(Calendar.DAY_OF_MONTH,
                         calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            int day = getSelectedDay();
            
            if (calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                renderer.setToday(new Integer(today.get(Calendar.DAY_OF_MONTH)));
            } else {
                renderer.setToday(null);
            }
                                    
            int start = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
            
            if (start < 0) {
                start += 7;
            }
            
            // Reset the data:
            for (int i = 0; i < days.length; ++i) {
                for (int j = 0; j < days[i].length; ++j) {
                    days[i][j] = null;
                }
            }            
            
            int x = start, y = 0;
            int noDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            for (int i = calendar.getActualMinimum(Calendar.DAY_OF_MONTH); i <= noDays; ++i) {
                days[y][x] = new Integer(i);                
                ++x;
                if (x == 7) {
                    x = 0;
                    ++y;
                }
            }
            fireTableDataChanged();
            setSelectedDay(day);
        }
        
        /**
         * Set the current month
         * @param month the new month
         */
        public void setMonth(int month) {
            if (month >= calendar.getActualMinimum(Calendar.MONTH) && month <= calendar.getActualMaximum(Calendar.MONTH)) {
                calendar.set(Calendar.MONTH, month);
                updateDays();
            }
        }
        
        /**
         * Set the current year
         * @param year the new year
         */
        public void setYear(int year) {
            if (year >= calendar.getActualMinimum(Calendar.YEAR) && year <= calendar.getActualMaximum(Calendar.YEAR)) {
                calendar.set(Calendar.YEAR, year);
                updateDays();
            }
        }
        
        /**
         * Get the name of the column..
         * @param col The column to get the name for
         * @return The column name
         */
        public String getColumnName(int col) {
            return columnNames[col].toString();
        }
        
        /**
         * Get the total number of elements in the data model
         * @return the number of elements...
         */
        public int getRowCount() {
            return 6;
        }
        
        /**
         * Get the number of columns
         * @return the number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }
        
        /**
         * Get the value stored at a given position inside the table
         * @param row the row number
         * @param col the column number
         * @return the object stored at [row,col]
         */
        public Object getValueAt(int row, int col) {
            return days[row][col];
        }
        
        /**
         * Is a given cell editable?
         * @param row the row number
         * @param col the column number
         * @return true if the cell can be edited, false otherwise
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        
        /**
         * Get the object class used in a column
         * @param c the column number
         * @return Boolean for column 0, string otherwise...
         */
        public Class getColumnClass(int c) {
            return String.class;
        }
    }

    /**
     * A small little renderer so I can get the text centered, and todays day
     * in red...
     */
    private static class CalendarRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1;

        private Integer today;
        private Color   defaultColor;
        
        /** Create a new CalendarRenderer */
        public CalendarRenderer() {
            super();
            defaultColor = getForeground();
        }
        
        /**
         * Set todays day number
         * @param today or null to turn off
         */
        public void setToday(Integer today) {
            this.today = today;
        }
        
        /**
         * Get the Cell renderer
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (today != null && today.equals(value)) {
                setForeground(Color.red);
            } else {
                setForeground(defaultColor);
            }
            setHorizontalAlignment( javax.swing.SwingConstants.CENTER);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    // When used as a property customizer
    public Object getPropertyValue() throws IllegalStateException {
        return getDate();	    
    }
    
     /**
      * A "main method" if you would like to test the panel...
      * @param argv not used
      */
//    public static void main(String argv[]) {
//        javax.swing.JFrame frm = new javax.swing.JFrame();
//        frm.getContentPane().add(new DateSelectionPanel());
//        frm.pack();
//        frm.setVisible(true);
//        frm.addWindowListener(new java.awt.event.WindowAdapter() {
//            public void windowClosing(java.awt.event.WindowEvent e) {
//                System.exit(0);
//            }
//        });
//    }
    
    public static void test() throws Exception {
        javax.swing.JFrame frm = new javax.swing.JFrame();
        
        DateSelectionPanel panel = new DateSelectionPanel();
        
        frm.getContentPane().add(panel);
        frm.pack();
        frm.setVisible(true);
        frm.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        Calendar c = new GregorianCalendar();
        
        for (int cntr = 1900; cntr < 3000; cntr++) {
            c.set(Calendar.YEAR, cntr);
            for (int cntrX = c.getActualMinimum(Calendar.MONTH); cntrX < c.getActualMaximum(Calendar.MONTH); cntrX++) {
                panel.tablemodel.setYear(cntr);
                panel.tablemodel.setMonth(cntrX);
                
                c.set(Calendar.MONTH, cntrX);
                c.set(Calendar.DAY_OF_MONTH, panel.tablemodel.days[1][0].intValue());
                
                if (c.get(Calendar.DAY_OF_WEEK) != c.getFirstDayOfWeek()) {
                    System.err.println(
                        "The assignment of day of week is incorrect for year: " + // NOI18N
                        c.get(Calendar.YEAR) + 
                        " and month: " + c.get(Calendar.MONTH)); // NOI18N
                }
            }
        }
    }
}
