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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Cell renderer for sorting column header.
 * Originally copied from org.openide.explorer.view.TreeTableView
 */
public class SortingHeaderRenderer extends JPanel implements TableCellRenderer {
    /**
     * A simple layout for an icon and a label
     *               
     *        [-----]
     * [----] [-----]
     * [Icon] [Label]
     * [----] [-----]
     *        [-----]
     */
    private static class IconAndLabelLayout implements java.awt.LayoutManager {
        private Component icon, label;
        
        /**
         * Constructor
         *
         * @param icon icon component
         * @param label label component
         */
        public IconAndLabelLayout(Component icon, Component label) {
            this.icon = icon;
            this.label = label;
        }
        
        public java.awt.Dimension preferredLayoutSize(java.awt.Container parent) {
            Dimension d = new Dimension();
            Dimension iconPref = icon.getPreferredSize();
            Dimension labelPref = label.getPreferredSize();
            d.width = iconPref.width + labelPref.width + 2;
            d.height = Math.max(iconPref.height, labelPref.height);
            return d;
        }
        
        public void removeLayoutComponent(Component comp) {
        }
        
        public void addLayoutComponent(String name, Component comp) {
        }
        
        public void layoutContainer(java.awt.Container parent) {
            Dimension iconPref = icon.getPreferredSize();
            Dimension labelPref = label.getPreferredSize();
            Insets insets = parent.getInsets();
            
            int y = (parent.getHeight() - insets.top - insets.bottom -
                iconPref.height) / 2;
            int x = (parent.getWidth() - insets.left - insets.right -
                iconPref.width - 2 - labelPref.width) / 2;
            if (y < 0)
                y = 0;
            if (x < 0)
                x = 0;
            
            x += insets.left;
            y += insets.top;
            
            int w = parent.getWidth() - iconPref.width - 2;
            if (w < 0)
                w = 0;
            
            icon.setBounds(x, y, iconPref.width, iconPref.height);
            
            if (iconPref.width != 0)
                x = x + iconPref.width + 2;
            label.setBounds(x, 0, w, parent.getHeight());

            /*
            UTUtils.LOGGER.fine("icon " + icon.getBounds().toString()); // NOI18N
            UTUtils.LOGGER.fine("label" + label.getBounds().toString()); // NOI18N
            */
        }
        
        public java.awt.Dimension minimumLayoutSize(java.awt.Container parent) {
            return new Dimension(0, 0);
        }
    }
    
    private static final long serialVersionUID = 1;

    private static ImageIcon SORT_DESC_ICON =
        new ImageIcon(org.openide.util.Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/treetable/columnsSortedDesc.gif")); // NOI18N
    private static ImageIcon SORT_ASC_ICON = 
        new ImageIcon(org.openide.util.Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/treetable/columnsSortedAsc.gif")); // NOI18N

    private JLabel label = new JLabel();
    private DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    
    /**
     * Constructor
     */
    public SortingHeaderRenderer() {
/*        setBorder(new CompoundBorder(
            UIManager.getBorder("TableHeader.cellBorder"), 
            new EmptyBorder(0, 2, 0, 2)));*/
        
        renderer.setHorizontalTextPosition(SwingConstants.LEFT);
        renderer.setOpaque(false);
        
        add(label);
        add(renderer);
        
        setLayout(new SortingHeaderRenderer.IconAndLabelLayout(label, renderer));
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                renderer.setFont(header.getFont());
            }
            TableColumnModel tcm = header.getColumnModel();
            int modelIndex = tcm.getColumn(column).getModelIndex();
            SortingModel tableModel = ((TreeTable) table).getSortingModel();
            if (tableModel != null) {
                if (tableModel.getSortedColumn() == modelIndex) {
                    renderer.setIcon(
                        tableModel.isSortOrderDescending() ? 
                        SORT_DESC_ICON : SORT_ASC_ICON);
                    renderer.setFont(this.getFont().deriveFont(Font.BOLD));
                } else {
                    renderer.setIcon(null);
                }
            }
        }

        renderer.setText((value == null) ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        
        return this;
    }
    
    /**
     * Sets an icon
     *
     * @param new icon
     */
    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }
    
    /**
     * Returns the icon
     *
     * @return icon
     */
    public Icon getIcon() {
        return label.getIcon();
    }
}


