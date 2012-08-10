/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011-2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.breadcrumbs;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.explorer.view.NodeRenderer;
import org.openide.nodes.Node;

/**
 *
 * @author lahvac
 */
public class BreadCrumbComponent extends JComponent implements PropertyChangeListener {

    public BreadCrumbComponent() {
        setPreferredSize(new Dimension(0, /*XXX:*/24));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                expand(e);
            }
        });
    }

    private static final int INSET_HEIGHT = 2;
    private static final int SEPARATOR_INSETS = 10;
    private static final int START_INSET = 10;
    private static final int MAX_ROWS_IN_POP_UP = 20;

    private ExplorerManager seenManager;

    private ExplorerManager findManager() {
        ExplorerManager manager = ExplorerManager.find(this);

        if (seenManager != manager) {
            if (seenManager != null) {
                seenManager.removePropertyChangeListener(this);
            }
            if (manager != null) {
                manager.addPropertyChangeListener(this);
            }
            seenManager = manager;
        }

        assert manager != null;

        return manager;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (nodes == null) measurePrepaint();

        int x = START_INSET;

        int height = getHeight();
        int insetHeight = (int) ((height - this.height) / 2);
        
        ((Graphics2D) g).addRenderingHints(Collections.singletonMap(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        for (int i = 0; i < nodes.length; i++) {
            Component rendered = renderer.getListCellRendererComponent(fakeList, nodes[i], i, false, false);
            g.translate(x, insetHeight);
            rendered.setSize(rendered.getPreferredSize());
            rendered.paint(g);
            g.translate(-x, -insetHeight);

            double angleX = height / 4;

            x += sizes[i];
            
            g.drawLine(x + SEPARATOR_INSETS, INSET_HEIGHT, (int) (x + angleX + SEPARATOR_INSETS), (int) (height / 2));
            g.drawLine((int) (x + angleX + SEPARATOR_INSETS), (int) (height / 2), x + SEPARATOR_INSETS, (int) (height));

            x += SEPARATOR_INSETS * 2 + angleX;
        }
    }

    private final JList fakeList = new JList();
    private final NodeRenderer renderer = new NodeRenderer();
    private Node[] nodes;
    private double[] sizes;
    private double height;

    private void measurePrepaint() {
        List<Node> path = computeNodePath();

        int i = 0;
        
        nodes = path.toArray(new Node[path.size()]);
        sizes = new double[path.size()];
        
        int xTotal = 0;
        
        height = /*XXX*/0;

        for (Node n : nodes) {
            Component rendered = renderer.getListCellRendererComponent(fakeList, n, i, false, false);
            Dimension preferedSize = rendered.getPreferredSize();
            xTotal += sizes[i] = preferedSize.width;
            
            height = Math.max(height, preferedSize.height);

            i++;
        }

        double angleX = height / 4;
        
        setPreferredSize(new Dimension((int) (xTotal + (nodes.length - 1) * (SEPARATOR_INSETS * 2 + angleX) + START_INSET), (int) (height + 2 * INSET_HEIGHT)));
    }
    
//    private static final int ICON_INSET = 5;
//    @Override
//    protected void paintComponent(Graphics g) {
//        if (names == null) measurePrepaint();
//
//        int x = START_INSET;
//
//        ((Graphics2D) g).addRenderingHints(Collections.singletonMap(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
//        for (int i = 0; i < names.length; i++) {
//            String dn = names[i];
//            Image icon = icons[i];
//            g.drawImage(icon, x, INSET_HEIGHT, null);
//            x += icon.getWidth(null) + ICON_INSET;
//            x += HtmlRenderer.renderString(dn, g, x, baseline, Integer.MAX_VALUE, getPreferredSize().height, g.getFont(), Color.BLACK, 0, true);
//
//            int angleX = fontHeight / 4;
//
//            g.drawLine(x + SEPARATOR_INSETS, INSET_HEIGHT, x + angleX + SEPARATOR_INSETS, INSET_HEIGHT + fontHeight / 2);
//            g.drawLine(x + angleX + SEPARATOR_INSETS, INSET_HEIGHT + fontHeight / 2, x + SEPARATOR_INSETS, INSET_HEIGHT + fontHeight);
//
//            x += SEPARATOR_INSETS * 2 + angleX;
//        }
//    }
//
//    private String[] names;
//    private Image[] icons;
//    private double[] sizes;
//    private int fontHeight;
//    private int baseline;
//
//    private void measurePrepaint() {
//        //TODO: from CompletionJList:
//        Graphics cellPreferredSizeGraphics = null;
//        if (cellPreferredSizeGraphics == null) {
//            // CompletionJList.this.getGraphics() is null
//            cellPreferredSizeGraphics = java.awt.GraphicsEnvironment.
//                    getLocalGraphicsEnvironment().getDefaultScreenDevice().
//                    getDefaultConfiguration().createCompatibleImage(1, 1).getGraphics();
//            assert (cellPreferredSizeGraphics != null);
//        }
//        
//        FontMetrics fm = cellPreferredSizeGraphics.getFontMetrics(getFont());
//        fontHeight = fm.getHeight();
//        baseline = fm.getAscent() + fm.getLeading() + INSET_HEIGHT;
//        List<Node> path = computeNodePath();
//
//        int i = 0;
//        int xTotal = START_INSET;
//        
//        names = new String[path.size()];
//        icons = new Image[path.size()];
//        sizes = new double[path.size()];
//
//        for (Node n : path) {
//            String html = n.getHtmlDisplayName();
//            icons[i] = n.getIcon(BeanInfo.ICON_COLOR_16x16);
//            int iconSize = icons[i].getWidth(null) + ICON_INSET;
//            if (html != null) {
//                names[i] = "<html>" + html;
//                xTotal += sizes[i] = HtmlRenderer.renderHTML(html, cellPreferredSizeGraphics, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, getFont(), Color.BLACK, 0, true) + iconSize;
//            } else {
//                String dn = n.getDisplayName();
//                names[i] = dn;
//                xTotal += sizes[i] = HtmlRenderer.renderPlainString(dn, cellPreferredSizeGraphics, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, getFont(), Color.BLACK, 0, true) + iconSize;
//            }
//
//            i++;
//        }
//
//        int angleX = fontHeight / 4;
//        setPreferredSize(new Dimension(xTotal + (names.length - 1) * (SEPARATOR_INSETS * 2 + angleX), fontHeight + 2 * INSET_HEIGHT));
//    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                measurePrepaint();
                repaint();
            }
        });
        
    }

    private void expand(MouseEvent e) {
        int clickX = e.getPoint().x;
        int elemX = START_INSET;

        for (int i = 0; i < sizes.length; i++) {
            int startX = elemX;
            elemX += sizes[i];

            double angleX = height / 4;

            elemX += SEPARATOR_INSETS + angleX;
            
            if (clickX <= elemX) {
                //found:
                List<Node> path = computeNodePath();
                expand(startX, path.get(i));
                return ;
            }
            
            elemX += SEPARATOR_INSETS;
        }
    }

    private List<Node> computeNodePath() {
        ExplorerManager manager = findManager();
        List<Node> path = new ArrayList<Node>();
        Node sel = manager.getExploredContext();

        while (sel != null) {
            path.add(sel);
            sel = sel.getParentNode();
        }

        path.remove(path.size() - 1); //XXX

        Collections.reverse(path);
        
        return path;
    }

    private void expand(int startX, final Node what) {
        if (what.getChildren().getNodesCount() == 0) return ;
        
        final ExplorerManager expandManager = new ExplorerManager();
        class Expanded extends JPanel implements ExplorerManager.Provider {
            public Expanded(LayoutManager layout) {
                super(layout);
            }
            @Override public ExplorerManager getExplorerManager() {
                return expandManager;
            }
        }
        final JPanel expanded = new Expanded(new BorderLayout());
        expanded.setBorder(new LineBorder(Color.BLACK, 1));
        expanded.add(new ListView() {
            {
                list.setVisibleRowCount(Math.min(what.getChildren().getNodesCount(), MAX_ROWS_IN_POP_UP));
            }
        }, BorderLayout.CENTER);
        expandManager.setRootContext(what);
        
        Point place = new Point(startX, 0);
        
        SwingUtilities.convertPointToScreen(place, this);
        
        expanded.validate();
        
        final Popup popup = PopupFactory.getSharedInstance().getPopup(this, expanded, place.x, place.y - expanded.getPreferredSize().height);
        
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent && ((MouseEvent) event).getClickCount() > 0) {
                    Object source = event.getSource();
                    while (source instanceof Component) {
                        if (source == expanded) return ; //accept
                        source = ((Component) source).getParent();
                    }
                    popup.hide();
                    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
        
        popup.show();
    }
}
