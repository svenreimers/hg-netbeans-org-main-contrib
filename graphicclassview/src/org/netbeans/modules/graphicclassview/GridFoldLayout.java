/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.graphicclassview;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.graphicclassview.JavaScene.Conn;
import org.netbeans.modules.graphicclassview.grid.AffinityProvider;
import org.netbeans.modules.graphicclassview.grid.G;

/**
 *
 * @author Tim Boudreau
 */
public class GridFoldLayout extends GraphLayout <SceneElement, Conn> {

    protected void performGraphLayout(UniversalGraph<SceneElement, Conn> graph) {
        performNodesLayout(graph, graph.getNodes());
    }

    protected void performNodesLayout(UniversalGraph<SceneElement, Conn> graph, Collection<SceneElement> nodes) {
        List<SceneElement> l = new ArrayList<SceneElement>(nodes);
        AffinityProvider<SceneElement> ap = new AffinityProvider<SceneElement>() {
            public int getAffinity(SceneElement a, SceneElement b) {
                if (a == null || b == null) return 0;
                int result = a.getInboundReferences(SceneElement.class).contains(b) ? 1 : 0;
                result += a.getOutboundReferences(SceneElement.class).contains(b) ? 1 : 0;
                return result;
            }
        };
        System.err.println("ELEMENT COUNT " + l.size());
        int width = (int) Math.sqrt(l.size());
        if (width * width < l.size()) {
            width += 1;
        }
        System.err.println("Width is " + width);
        G<SceneElement> g = G.create(l, width, width, ap);
        g.optimize();
        int boxW = 275;
        int boxH = 70;
        for (int y= 0; y < g.getHeight(); y++) {
            for (int x= 0; x < g.getWidth(); x++) {
                SceneElement el = g.get(x, y);
                if (el != null) {
                    int xPos = x * boxW;
                    int yPos = y * boxH;
                    System.err.println("Set loc of ");
                    Point p = new Point (xPos, yPos);
                    System.err.println("Locate " + el.getName() + " at " + p);
                    setResolvedNodeLocation(graph, el, p);
                }
            }
        }
    }

//    private static class ConnectednessComparator implements Comparator<SceneElement> {
//        final SceneElement el;
//
//        public int compare(SceneElement o1, SceneElement o2) {
//            int result = 0;
//            if (el.getOutboundReferences(SceneElement.class).contains(o1)) {
//                result++;
//            }
//            if (el.getO)
//        }
//
//    }
}
