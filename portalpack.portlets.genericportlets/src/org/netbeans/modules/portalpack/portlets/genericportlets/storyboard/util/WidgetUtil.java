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

package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.util;

import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.CustomVMDGraphScene;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.IPCPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomNodeWidget;
import org.openide.util.Utilities;

/**
 *
 * @author Satyaranjan
 */
public class WidgetUtil {
       private static final Image GLYPH_CONSUME_EVENT=Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/portletapp.gif");
      
    /** Creates a new instance of WidgetUtil */
    public WidgetUtil() {
    }
    public static VMDNodeWidget createNode(CustomVMDGraphScene scene, int x, int y, Image image, String key, String name, String type, List<Image> glyphs) {
        //String nodeID = "node" + IPCGraphScene.nodeID ++;
        String nodeID = key;
        
        CustomNodeWidget widget = (CustomNodeWidget) scene.addNode(nodeID);
        widget.setNodeKey(nodeID);
     //   widget.getActions().addAction(ActionFactory.createPopupMenuAction(new IPCPopUpMenuProvider()));
        widget.setPreferredLocation(new Point(x, y));
        widget.setNodeProperties(image, name, type, glyphs);
        return widget;
    } 
    
    public static VMDPinWidget createPin(CustomVMDGraphScene scene, String nodeID, String pinID, Image image, String name, String type) {
        CustomPinWidget widget = (CustomPinWidget) scene.addPin(nodeID, pinID);
        widget.setKey(pinID);
        widget.setNodeKey(nodeID);     
        List list = new ArrayList();
        list.add(GLYPH_CONSUME_EVENT);
        widget.setProperties(name, list);
        
        return widget;
        
    }
      
    public static Widget createEdge(CustomVMDGraphScene scene, String edgeID, String sourcePinID, String targetNodeID) {
        //String edgeID = "edge" + IPCGraphScene.edgeID ++;
        Widget widget = scene.addEdge(edgeID);
        scene.setEdgeSource(edgeID, sourcePinID);
        scene.setEdgeTarget(edgeID, targetNodeID + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
        return widget;
    }
     
}