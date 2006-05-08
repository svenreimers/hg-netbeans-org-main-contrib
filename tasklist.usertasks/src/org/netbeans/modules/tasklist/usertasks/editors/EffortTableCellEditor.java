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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;

/**
 * TableCellEditor for duration values.
 *
 * @author tl
 */
public class EffortTableCellEditor extends DefaultCellEditor {
    /**
     * This array must be sorted.
     */
    private static final int[] DURATIONS = new int[] {
        0,
        5,
        10,
        15,
        20,
        30,
        45,
        60,
        90,
        120,
        150,
        180,
        240,
        300,
        360,
        420,
        480,
        12 * 60,
        8 * 60 * 2
    };
    
    private List<Integer> durations;
    private List<String> texts;
    
    /**
     * Creates a new instance.
     */
    public EffortTableCellEditor() {
        super(new JComboBox());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        int d;
        if (value instanceof UserTask) {
            d = ((UserTask) value).getEffort();
        } else if (value instanceof Integer) {
            d = ((Integer) value).intValue();
        } else {
            d = 0;
        }

        durations = new ArrayList<Integer>();
        for (int dur: DURATIONS) {
            durations.add(dur);
        }

        int index = Collections.binarySearch(durations, d);
        if (index < 0) {
            index = -index - 1;
            durations.add(index, d);
        }

        DurationFormat df = new DurationFormat(DurationFormat.Type.LONG);
        texts = new ArrayList<String>(durations.size());
        Settings s = Settings.getDefault();
        int hpd = s.getHoursPerDay();
        int dpw = s.getDaysPerWeek();
        for (int dur: durations) {
            texts.add(df.format(new Duration(dur, hpd, dpw)));
        }
        
        ((JComboBox) editorComponent).setModel(
                new DefaultComboBoxModel(
                texts.toArray(new String[texts.size()])));
        ((JComboBox) editorComponent).setSelectedIndex(index);
        return editorComponent;
    }
    
    public Object getCellEditorValue() { 
        int index = ((JComboBox) editorComponent).getSelectedIndex();
        return durations.get(index);
    }
}
