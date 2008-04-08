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
 * MarkedScrollbarUI.java
 *
 * Created on March 5, 2003, 4:32 PM
 */
package org.netbeans.swing.scrollbars.impl;

import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;
import org.netbeans.swing.scrollbars.spi.Mark;
import org.netbeans.swing.scrollbars.spi.MarkingModel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Scrollbar UI that can take a MarkingModel and visually render
 *  rectangles for and respond to the marks alongside the scrollbar.
 *
 * @version 1.1
 * @author  Tim Boudreau
 */
public class WindowsMarkedScrollBarUI extends WindowsScrollBarUI implements PropertyChangeListener, ChangeListener {
    private static final int minsize = 4;

    private int gutterSize=9;
    private MarkingModel model;

    /** Creates a new instance of MarkedScrollbarUI
     * @param model An instance of MarkingModel which will provide marks for it to render alongside
     * the scrollbar.
     */
    public WindowsMarkedScrollBarUI(MarkingModel model) {
        this.model = model;
        model.addChangeListener(this);
        updateUI();
    }

    public void updateUI () {
        if ((scrollbar != null) && scrollbar.isVisible()) {
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                layoutVScrollbar (scrollbar);
            } else {
                layoutHScrollbar (scrollbar);
            }
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension result = super.getPreferredSize(c);
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            result.width += gutterSize / 2;
        } else {
            result.height += gutterSize / 2;
        }
        return result;
    }

    public void installUI(JComponent c)   {
        updateUI();
        super.installUI(c);
        UIManager.addPropertyChangeListener(this);
        c.addComponentListener ((ComponentListener) trackListener);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        UIManager.removePropertyChangeListener(this);
        c.removeComponentListener ((ComponentListener) trackListener);
    }

    /** Listens to the UIManager for changes, so the PLAF can be changed
     *  on the fly.  Will call <code>updateUI()</code> in the case of a change. */
    public final void propertyChange (PropertyChangeEvent pce) {
        updateUI();
    }

    protected void layoutVScrollbar(JScrollBar sb) {
        super.layoutVScrollbar (sb);
        trackRect.width -=gutterSize;
        thumbRect.width -=gutterSize;
        invalidateMarks();
    }

    protected void layoutHScrollbar(JScrollBar sb) {
        super.layoutVScrollbar (sb);
        trackRect.height -=gutterSize;
        thumbRect.height -=gutterSize;
        invalidateMarks();
    }

    public void paint(Graphics g, JComponent c) {
        paintTrack(g, c, getTrackBounds());
        paintThumb(g, c, getThumbBounds());
        paintMarks(g);
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g.setColor(UIManager.getColor("controlDkShadow")); //NOI18N
            g.drawLine(c.getWidth()-2, trackRect.y, c.getWidth()-2, trackRect.y+trackRect.height);
            g.setColor(UIManager.getColor("controlHighlight")); //NOI18N
            g.drawLine(c.getWidth()-1, trackRect.y, c.getWidth()-1, trackRect.y+trackRect.height);
        } else {
            g.setColor(UIManager.getColor("controlDkShadow")); //NOI18N
            g.drawLine(trackRect.x, 2, trackRect.x+trackRect.width, 2);
            g.setColor(UIManager.getColor("controlHighlight")); //NOI18N
            g.drawLine(trackRect.x, 1, trackRect.x+trackRect.width, 1);
        }
    }

    private void paintMarks (Graphics g) {
//        Rectangle r = new Rectangle ((scrollbar.getWidth() + 2) - gutterSize, 20, gutterSize -1, scrollbar.getHeight() - 40);
//        g.setColor (scrollbar.getBackground());
//        g.fillRect (r.x, r.y, r.width+1, r.height);
        Map map = getRectsToMarks();
        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
            Rectangle mrect = (Rectangle) i.next();
            Mark mark = (Mark) map.get(mrect);
            paintMark (mark, g, mrect);
        }
    }

    private boolean invalid = true;
    private void invalidateMarks() {
        invalid = true;
        scrollbar.repaint();
    }

    private Map getRectsToMarks() {
        if (invalid) {
            buildRects();
        }
        return rectsToMarks;
    }

    private void buildRects() {
        rectsToMarks.clear();
        if (model.size() == 0) {
            return;
        }
        boolean vertical = scrollbar.getOrientation() == JScrollBar.VERTICAL;
        int markRange = model.getMaxMarkLocation();

        Rectangle r = getMarksRect();
        for (Enumeration e = model.getMarks(); e.hasMoreElements();) {
            Mark mark = (Mark) e.nextElement();
            Rectangle curr = new Rectangle();
            if (vertical) {
                r.y = translate (mark.getStart(), markRange);
                r.height = Math.max(minsize, translate (mark.getLength(), markRange));
                r.width = gutterSize;
                curr.setBounds(r.x, r.y, r.width, r.height);
                rectsToMarks.put (curr, mark);
            } else {
                r.x = translate (mark.getStart(), markRange);
                r.width = Math.max(minsize, translate (mark.getLength(), markRange));
                r.height = gutterSize;
                curr.setBounds(r.x, r.y, r.width, r.height);
                rectsToMarks.put (curr, mark);
            }
        }
        invalid = false;
    }

    private HashMap rectsToMarks = new HashMap();

    /** Find the mark at a given point in the component's coordinate space
     * @return The Mark object at that point, or null
     * @param p A point in the scrollbar's coordinate space
     */
    public Mark markAtPoint (Point p) {
        Rectangle rect = getMarksRect();
        if (!(rect.contains(p))) return null;
        java.util.Iterator i = rectsToMarks.keySet().iterator();
        while (i.hasNext()) {
            Rectangle r = (Rectangle) i.next();
            if (r.contains(p)) return (Mark) rectsToMarks.get(r);
        }
        return null;
    }

    /** Translate a location as given by a call to <code>someMark.getStart()</code>
     * into the coordinate space of the scrollbar.
     * @param loc The location given by the Mark object
     * @param max The maximum, given by the model
     * @return The coordinate in the space of the scrollbar, as a position along the
     * scrollbar's resizable axis.
     */
    protected final int translate (int loc, int max) {
        int range = getVisibleRange();
        double factor = range / max;
        double pos = factor * loc;
        return Math.round(Math.round(pos));
    }

    /** Get the total number of possible thumb positions on the resizable axis of the
     * scrollbar - the track height or width, depending on the scrollbar's
     * orientation
     * @return The total
     */
    protected final int getVisibleRange () {
        Rectangle mrect = getMarksRect();
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL) ?
            mrect.height : mrect.width;
    }

    /** Get the rectangle within which marks can be displayed.
     * @return The rectangle
     */
    protected final Rectangle getMarksRect () {
        Rectangle result;
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            result = new Rectangle (trackRect.x + trackRect.width + 1, trackRect.y,
                                    trackRect.width +gutterSize, trackRect.height);
        } else {
            result = new Rectangle (trackRect.x, trackRect.y + trackRect.height + 1,
                                    trackRect.width, trackRect.height +gutterSize);
        }
        return result;
    }

    protected TrackListener createTrackListener() {
        return new MarkAndTrackListener ();
    }

    private int translateToScrollbarModel (int i) {
        BoundedRangeModel mod = scrollbar.getModel();
        int min = mod.getMinimum();
        int max = mod.getMaximum();
        int mrange = max - min;

        double factor = mrange / model.getMaxMarkLocation();
        double pos = (i * factor) + min;
        return Math.round(Math.round(pos));
    }

    private void goToMark (Mark m) {
        Rectangle r = getMarksRect();
        int loc = translate (m.getStart(), r.height);
        scrollbar.setValue(translateToScrollbarModel (loc));
        m.select();
    }

    public void stateChanged(javax.swing.event.ChangeEvent e) {
        invalidateMarks();
    }

    protected class MarkAndTrackListener extends TrackListener implements ComponentListener {
        public void mousePressed (MouseEvent e)
        {
            if (scrollbar.getValueIsAdjusting ()) {
                super.mousePressed (e);
                return;
            }
            if (SwingUtilities.isRightMouseButton(e) ||
                SwingUtilities.isMiddleMouseButton(e) || !scrollbar.isEnabled()) {
                return;
            }

            Point p = e.getPoint();
            Rectangle r = getMarksRect();

            if (r.contains (p)) {
                Mark m = markAtPoint (p);
                if (m != null) {
                    goToMark (m);
                }
            } else {
                super.mousePressed(e);
            }
        }

        private boolean notInMarksRect (MouseEvent e) {
            Point p = e.getPoint();
            Rectangle r = getMarksRect();
            return !(r.contains (p));
        }

        public void mouseReleased (MouseEvent e) {
            if (scrollbar.getValueIsAdjusting () || notInMarksRect (e)) {
                super.mouseReleased (e);
            }
        }

    	public void mouseDragged (MouseEvent e) {
            if (scrollbar.getValueIsAdjusting () || notInMarksRect (e)) {
                super.mouseDragged (e);
            }
        }
        String oldtext;
        public void mouseMoved (MouseEvent e) {
            if (scrollbar.getValueIsAdjusting () || notInMarksRect (e)) {
                super.mouseMoved (e);
            } else {
                Point p = e.getPoint();
                Rectangle r = getMarksRect();

                if (r.contains (p)) {
                    Mark m = markAtPoint (p);
                    if (m != null) {
                        scrollbar.setToolTipText (m.getText());
                    } else {
                        scrollbar.setToolTipText (null);
                    }
                }
            }
        }

        /**
         * Invoked when the component's size changes.
         */
        public void componentResized(ComponentEvent e) {
            invalidateMarks();
        }

        /**
         * Invoked when the component's position changes.
         */
        public void componentMoved(ComponentEvent e) {

        }

        /**
         * Invoked when the component has been made visible.
         */
        public void componentShown(ComponentEvent e) {
            invalidateMarks();
        }

        /**
         * Invoked when the component has been made invisible.
         */
        public void componentHidden(ComponentEvent e) {
            rectsToMarks.clear();
            invalid = true;
        }
    }

    public void paintMarkMetal(Mark mark, Graphics g, Rectangle r) {
         Color c = (Color) mark.get ("color"); //NOI18N
         if (c == null) {
             c = UIManager.getColor("windowText"); //NOI18N
         }
         g.setColor (c);
         g.fillRect (r.x-1, r.y, r.width+1, r.height-1);
         g.setColor (c.darker());
         g.drawRect (r.x-1, r.y, r.width-2, r.height-1);
    }



    public void paintMark(Mark mark, Graphics g, Rectangle r) {
        if (getClass() == MetalMarkedScrollBarUI.class) {
            paintMarkMetal (mark, g, r);
        } else {
            paintMarkWindows (mark, g, r);
        }
    }

    private static final int overhang = 4;
    public void paintMarkWindows (Mark mark, Graphics graphics, Rectangle rect) {
        Color c = (Color) mark.get ("color"); //NOI18N
        if (c == null) {
            c = UIManager.getColor("windowText"); //NOI18N
        }
        Paint p = new GradientPaint(rect.x+rect.width-1, rect.y+3, c,
            rect.x-overhang, rect.y+3, new Color (c.getRed(),c.getGreen(),c.getBlue(),35));
        ((Graphics2D)graphics).setPaint(p);

        graphics.fillRect (rect.x-overhang, rect.y, rect.width+4, rect.height);

        Color mColor = c;

        c = UIManager.getColor ("controlHighlight"); //NOI18N
        p = new GradientPaint(rect.x+rect.width-1, rect.y+3, c,
            rect.x-overhang, rect.y+3, new Color (c.getRed(),c.getGreen(),c.getBlue(),0));

        ((Graphics2D)graphics).setPaint(p);

        graphics.drawLine (rect.x - overhang, rect.y, rect.x + rect.width, rect.y);
        graphics.drawLine (rect.x - overhang, rect.y, rect.x - overhang, rect.y + rect.height);

        /*
        c = mColor.darker();
        p = new GradientPaint(rect.x+rect.width-1, rect.y+3, c,
            rect.x-overhang, rect.y+3, new Color (c.getRed(),c.getGreen(),c.getBlue(),10));


        ((Graphics2D)graphics).setPaint(p);

        graphics.drawLine (rect.x - overhang, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);
        graphics.drawLine (rect.x + rect.width - 4, rect.y + rect.height, rect.x + rect.width - 4, rect.y + 1);
          */

/*        graphics.setColor(c.darker());
        graphics.drawLine(rect.x+rect.width, rect.y, rect.x+rect.width, rect.y+rect.height-1);
        graphics.drawLine(rect.x+rect.width-1, rect.y, rect.x+rect.width-1, rect.y+rect.height-1);
        graphics.drawLine(rect.x+rect.width-2, rect.y, rect.x+rect.width-2, rect.y+rect.height-1);
        graphics.drawLine(rect.x+rect.width-3, rect.y, rect.x+rect.width-3, rect.y+rect.height-1);
        */

/*
        graphics.setColor(UIManager.getColor("controlLtHighlight"));
        graphics.drawLine(rect.x-overhang, rect.y,
            rect.x+rect.width+overhang, rect.y);
            */

        p = new GradientPaint(rect.x+rect.width-1, rect.y+3, c.darker(),
            rect.x-overhang, rect.y+3, UIManager.getColor("controlShadow"));
        ((Graphics2D)graphics).setPaint(p);

        /*
        graphics.drawLine(rect.x-overhang, rect.y+rect.height,
            rect.x+rect.width+overhang, rect.y+rect.height);
            */
    }
}