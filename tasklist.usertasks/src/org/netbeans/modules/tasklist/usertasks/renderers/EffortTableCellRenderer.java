package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTask;

/**
 * Renderer for the effort
 */
public class EffortTableCellRenderer extends DurationTableCellRenderer {
    private Font boldFont, normalFont;
    
    /**
     * Constructor
     */
    public EffortTableCellRenderer() {
    }

    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        if (normalFont == null || !normalFont.equals(table.getFont())) {
            normalFont = table.getFont();
            boldFont = normalFont.deriveFont(Font.BOLD);
        }
        setForeground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        UserTask ut = (UserTask) value;
        if (ut != null) {
            boolean b = ut.getEffort() >= ut.getSpentTime() + ut.getRemainingEffort();
            setFont(b ? normalFont : boldFont);
            if (!isSelected && !b)
                setForeground(Color.RED);
        }
        return this;
    }

    protected org.netbeans.modules.tasklist.usertasks.UserTask.Duration getDuration(Object obj) {
        UserTask ut = (UserTask) obj;
        if (ut == null) {
            return null;
        } else {
            return UserTask.splitDuration(ut.getEffort(),
                Settings.getDefault().getHoursPerDay(), 
                Settings.getDefault().getDaysPerWeek());
        }
    }
}
