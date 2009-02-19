/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
/*
 * TestIt.java
 *
 * Created on April 27, 2004, 12:34 AM
 */

package org.netbeans.modules.hexedit;

import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.nio.channels.FileChannel;

/**
 * A hex editor component
 *
 * @author  Tim Boudreau
 */
public class HexEditPanel extends JPanel implements Runnable {
    private JComboBox modeCombo;
    private JLabel modeLabel;
    private JLabel columnsLabel;
    private JPanel controlPanel;
    private JScrollPane mainScrollPane;
    private JSpinner columnsSpinner;
    private JSplitPane splitPane;
    JTable hexTable = new JTable();
    JList jList1 = new JList();
    HexTableCellRenderer renderer = new HexTableCellRenderer();
    private JFileChooser jfc = null;

    public HexEditPanel() {
        initComponents();
    }
    
    public HexEditPanel (File f) throws FileNotFoundException {
        this();
        if (f.length() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException (
                    Util.formatMessage("MSG_FILE_TOO_LARGE", new Object[]{f})); //NOI18N
        }
        if (f.isFile() && f.exists() && f.canRead()) {
            RandomAccessFile raf = new RandomAccessFile (f, f.canWrite() ? "rw" : "r");

            FileChannel channel = raf.getChannel();
            setFileChannel (channel, (int) f.length());
        }
        setName(f.toString());
    }

    public void setFileChannel (FileChannel channel, int length) {
        HexTableModel htm = new HexTableModel (getColumnCount(), channel,
            length, false);
        sizeLabel.setText (Integer.toString(length));

        htm.setMode (mode);
        ByteListModel blm = new ByteListModel (htm);
        hexTable.setModel(htm);
        jList1.setModel(blm);
        updateListWidth();
        revalidate();
        repaint();
    }

    private void initComponents() {
        setLayout (new BorderLayout());
        mainScrollPane = new javax.swing.JScrollPane();
        splitPane = new javax.swing.JSplitPane();
        controlPanel = new javax.swing.JPanel();
        modeLabel = new javax.swing.JLabel();
        modeCombo = new javax.swing.JComboBox();
        columnsLabel = new javax.swing.JLabel();
        columnsSpinner = new javax.swing.JSpinner();

        mainScrollPane.setViewportView(splitPane);
        mainScrollPane.setBorder (BorderFactory.createEmptyBorder());
        mainScrollPane.setViewportBorder (BorderFactory.createEmptyBorder());

        addComponentListener (new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                resetSplitterPosition();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                resetSplitterPosition();
            }
        }) ;

        add(mainScrollPane, java.awt.BorderLayout.CENTER);

        controlPanel.setOpaque(false);
        modeLabel.setLabelFor(modeCombo);
        modeLabel.setText(Util.getMessage("LBL_MODE")); //NOI18N
        modeLabel.setToolTipText(Util.getMessage("TIP_MODE")); //NOI18N
        controlPanel.add(modeLabel);

        modeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeComboChanged();
            }
        });

        controlPanel.add(modeCombo);

        columnsLabel.setText(Util.getMessage("LBL_COLUMNS")); //NOI18N
        columnsLabel.setLabelFor(columnsSpinner);
        controlPanel.add(columnsLabel);

        columnsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                columnsSpinnerChanged();
            }
        });

        controlPanel.add(columnsSpinner);

        add(controlPanel, java.awt.BorderLayout.NORTH);

        splitPane.setLeftComponent(hexTable);

        splitPane.setRightComponent(jList1);
        hexTable.setGridColor(UIManager.getColor("controlShadow")); //NOI18N
        splitPane.setDividerLocation(0.75d);

        Integer i = (Integer) UIManager.get("customFontSize"); //NOI18N
        int fontSize = 13;
        if (i != null) {
            fontSize = i.intValue();
        }

        Font f = new Font ("Monospaced", Font.PLAIN, fontSize); //NOI18N
        hexTable.setFont (f);
        jList1.setFont (f);

        Listener sel = new Listener();

        jList1.getSelectionModel().addListSelectionListener(sel);
        hexTable.getSelectionModel().addListSelectionListener(sel);
        jList1.addMouseMotionListener(sel);
        hexTable.addMouseMotionListener(sel);
        jList1.addMouseListener(sel);
        hexTable.addMouseListener(sel);
        hexTable.setAutoCreateColumnsFromModel(true);

        Integer[] vals = new Integer [] {
            new Integer(HexTableModel.MODE_BYTE),
            new Integer(HexTableModel.MODE_SHORT),
            new Integer(HexTableModel.MODE_INT),
            new Integer(HexTableModel.MODE_LONG),
            new Integer(HexTableModel.MODE_CHAR),
        };

        modeCombo.setModel(new DefaultComboBoxModel (vals));
        modeCombo.setRenderer(new ModeComboRenderer());
        hexTable.setDefaultRenderer (Number.class, renderer);
        hexTable.setDefaultRenderer (Character.class, renderer);
        hexTable.setTableHeader(null);
        jList1.setCellRenderer(renderer);

        HexCellEditor ed = new HexCellEditor();

        hexTable.setDefaultEditor(Number.class, ed);
        hexTable.setDefaultEditor(Character.class, ed);
        hexTable.setSurrendersFocusOnKeystroke(true);

        columnsSpinner.setValue(new Integer(8));

        //These lines are *critical* for scalability - if fixed sizes are
        //not set, it will read in the entire file to determine the preferred
        //size of the jList just to see if a horizontal scrollbar is needed
        jList1.setFixedCellWidth(120);
        jList1.setFixedCellHeight(10);
        hexTable.setRowHeight(10);

        columnsSpinner.setMinimumSize(new Dimension (80, 20));

        Color shad = UIManager.getColor ("controlShadow");
        if (shad == null) {
            shad = Color.gray;
        }
        Border b = BorderFactory.createMatteBorder (0,0,0,1,shad);
        fileLabel.setBorder(b);
        sizeLabel.setBorder(b);
        valueLabel.setBorder(b);

        JPanel statusBar = new JPanel(new FlowLayout());
        statusBar.setBorder (BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder (1, 0, 0, 0, shad),
                BorderFactory.createEmptyBorder (2, 0, 0, 0)
        ));
        statusBar.add (fileLabel);
        statusBar.add (sizeLabel);
        statusBar.add (positionLabel);
        positionLabel.setHorizontalAlignment(SwingConstants.LEADING);
        statusBar.add (valueLabel);
        statusBar.setFont (f);

        add (statusBar, BorderLayout.SOUTH);
    }

    private JLabel fileLabel = new JLabel("       "); //NOI18N
    private JLabel sizeLabel = new JLabel("       "); //NOI18N
    private JLabel valueLabel = new JLabel("       "); //NOI18N
    private JLabel positionLabel = new JLabel("       "); //NOI18N
    private static final String posString = Util.getMessage ("LBL_POSITION");
    private static final String valString = Util.getMessage ("LBL_VALUE");

    public void run() {
        Dimension r = splitPane.getRightComponent().getPreferredSize();
        int width = (hexTable.getModel().getColumnCount() * 3) * charWidth;
        jList1.setFixedCellWidth(width);
//        splitPane.setDividerLocation(0.75d);
        if (r.width < splitPane.getWidth()) {
            splitPane.setDividerLocation(splitPane.getWidth() - r.width);
        } else {
            splitPane.setDividerLocation(-1);
        }
        mainScrollPane.invalidate();
        invalidate();
        revalidate();
        repaint();
    }

    public void resetSplitterPosition() {
        EventQueue.invokeLater(this);
    }

    private class Listener extends MouseAdapter implements ListSelectionListener, MouseMotionListener {
        boolean adjusting = false;
        public void valueChanged(ListSelectionEvent e) {
            if (adjusting) {
                return;
            }
            try {
                adjusting = true;
                if (e.getSource() == jList1.getSelectionModel()) {
                    hexTable.changeSelection(e.getFirstIndex(), hexTable.getColumnCount(), false, false);
                } else {
                    jList1.setSelectedIndex(hexTable.getSelectedRow());
                }
            } finally {
                adjusting = false;
            }
        }
        
        public void mouseDragged(MouseEvent e) {
            //do nothing
        }
        
        public void mouseMoved(MouseEvent e) {
            if (!(hexTable.getModel() instanceof HexTableModel)) {
                //no file loaded yet
                return;
            }
            if (!hexTable.isEditing()) {
                Point p = e.getPoint();
                if (e.getSource() == jList1) {
                
                    int row = p.y / jList1.getFixedCellHeight();
                    if (row >= 0) {
                        Rectangle r = jList1.getCellBounds(row, row);
                        if (r != null && r.contains (p)) {
                            int col = (p.x / charWidth) / ((HexTableModel) hexTable.getModel()).bytesPerElement();
                            if (col < hexTable.getColumnCount()) {
                                int prevCol = renderer.setHighlightColumn(col);
                                int prevRow = renderer.setHighlightRow (row);
                                maybeRepaintRow (prevRow, prevCol, row, col);
                            } else {
                                int prevCol = renderer.setHighlightColumn(-1);
                                int prevRow = renderer.setHighlightRow(-1);
                                maybeRepaintRow (prevRow, prevCol, -1, -1);
                            }
                        }
                    }
                } else {
                    int col = hexTable.columnAtPoint(p);
                    int row = hexTable.rowAtPoint(p);
                    if (col < hexTable.getColumnCount()) {
                        int prevCol = renderer.setHighlightColumn(col);
                        int prevRow = renderer.setHighlightRow (row);
                        maybeRepaintRow (prevRow, prevCol, row, col);
                    } else {
                        int prevCol = renderer.setHighlightColumn(-1);
                        int prevRow = renderer.setHighlightRow(-1);
                        maybeRepaintRow (prevRow, prevCol, -1, -1);
                    }
                }
            } else {
                renderer.setHighlightColumn (hexTable.getEditingColumn());
                renderer.setHighlightRow (hexTable.getEditingRow());
            }
        }
        
        private void maybeRepaintRow (int prevRow, int prevCol, int row, int col) {
            if (prevRow != row) {
                if (prevRow != -1) {
                    Rectangle r = hexTable.getCellRect (prevRow, 0, false);
                    r.x = 0;
                    r.width = hexTable.getWidth();
                    hexTable.repaint (r);
                    
                    r = jList1.getCellBounds(prevRow, prevRow);
                    jList1.repaint (r);
                }
                if (row != -1) {
                    Rectangle r = hexTable.getCellRect (row, 0, false);
                    r.x = 0;
                    r.width = hexTable.getWidth();
                    hexTable.repaint (r);
                    
                    r = jList1.getCellBounds (row, row);
                    jList1.repaint(r);
                }
                updateLabels(row, col);
            } else if (prevRow != -1) {
                if (prevCol != -1) {
                    Rectangle r = hexTable.getCellRect (prevRow, prevCol, false);
                    hexTable.repaint (r);
                }
                if (col != -1) { 
                    Rectangle r = hexTable.getCellRect (row, col, false);
                    hexTable.repaint (r);
                }
                if (prevCol != col) {
                    Rectangle r = jList1.getCellBounds (row, row);
                    jList1.repaint(r);
                    updateLabels(row, col);
                }
            }
        }


        private void updateLabels (int row, int col) {
            if (row == -1 || col == -1 || row > hexTable.getModel().getRowCount() || col > hexTable.getModel().getColumnCount()) {
                positionLabel.setText ("      "); //NOI18N
                valueLabel.setText ("      ");
            } else {
                Object o = hexTable.getValueAt(row, col);
                int bytesPerElement = ((HexTableModel) hexTable.getModel()).bytesPerElement();
                String position = o != null ? Integer.toString((row * bytesPerElement) + col * bytesPerElement) : "      "; //NOI18N
                positionLabel.setText (posString + ' ' + position); //NOI18N
                valueLabel.setText(o == null ? "     " : valString + ' ' + o.toString()); //NOI18N
            }
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            int prevCol = renderer.setHighlightColumn(-1);
            int prevRow = renderer.setHighlightRow(-1);
            maybeRepaintRow (prevRow, prevCol, -1, -1);
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == jList1) {
                Point p = e.getPoint();
                int row = p.y / jList1.getFixedCellHeight();
                if (row >= 0) {
                    Rectangle r = jList1.getCellBounds(row, row);
                    if (r != null && r.contains (p)) {
                        int col = (p.x / charWidth) / ((HexTableModel) hexTable.getModel()).bytesPerElement();
                        if (col < hexTable.getColumnCount() && row < hexTable.getRowCount()) {
                            hexTable.changeSelection (row, col, false, false);
                            hexTable.editCellAt(row, col, e);
                            if (hexTable.isEditing()) { //single click only selects
                                HexCellEditor ed = (HexCellEditor) hexTable.getDefaultEditor(Number.class);
                                ed.focusEditor();
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean firstPaint = true;
    private int charWidth = 0;
    
    public void paint (Graphics g) {
        if (firstPaint) {
            FontMetrics fm = g.getFontMetrics (hexTable.getFont());
            int fontHeight = fm.getHeight() + 2;
            hexTable.setRowHeight(fontHeight);
            jList1.setFixedCellHeight(fontHeight);
            charWidth = fm.charWidth('J');
            firstPaint = false;
            return;
        }
        super.paint(g);
    }

    private void columnsSpinnerChanged() {
        if (hexTable.getModel() instanceof HexTableModel) {
            //won't be until first load
        }
        setColumns (((Integer) columnsSpinner.getValue()).intValue());
    }

    private int columns = 12;
    public int getColumns() {
        if (hexTable.getModel() instanceof HexTableModel) {
            return ((HexTableModel) hexTable.getModel()).getColumnCount();
        }
        //first load
        return columns;
    }

    public void setColumns (int columns) {
        int old = getColumns();
        if (hexTable.getModel() instanceof HexTableModel) {
            ((HexTableModel) hexTable.getModel()).setColumnCount(columns);
            ((HexTableModel) hexTable.getModel()).setColumnCount (getColumnCount());
            hexTable.createDefaultColumnsFromModel();
            updateListWidth();
            revalidate();
            repaint();
        }
        this.columns = columns;
        if (old != columns) {
            firePropertyChange ("columns", old, columns); //NOI18N
        }
    }

    private void modeComboChanged() {
        setMode (((Integer) modeCombo.getSelectedItem()).intValue());
        resetSplitterPosition();
        repaint();
    }

    private int mode = HexTableModel.MODE_BYTE;
    public void setMode (int mode) {
        int old = getMode();
        if (hexTable.getModel() instanceof HexTableModel) {
            ((HexTableModel) hexTable.getModel()).setMode (mode);
            revalidate();
            repaint();
        }
        this.mode = mode;
        if (mode != old) {
            firePropertyChange ("mode", old, mode); //NOI18N
        }
    }

    private void updateListWidth() {
        resetSplitterPosition();
    }

    public int getMode() {
        if (hexTable.getModel() instanceof HexTableModel) {
            return ((HexTableModel) hexTable.getModel()).getMode();
        }
        //first load
        return mode;
    }

    private int getColumnCount() {
        Integer i = (Integer)columnsSpinner.getValue();
        return i.intValue();
    }

    public void setName (String name) {
        super.setName(name);
        fileLabel.setText(name);
    }
}
