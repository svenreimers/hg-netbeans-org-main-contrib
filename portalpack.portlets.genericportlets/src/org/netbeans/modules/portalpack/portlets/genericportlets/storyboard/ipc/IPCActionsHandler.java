/*
 * IPCActionsHandler.java
 *
 * Created on May 13, 2007, 1:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc;

import java.awt.Point;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.NodePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.util.WidgetUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomNodeWidget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;

/**
 *
 * @author Satyaranjan
 */
public class IPCActionsHandler {
    
    private IPCGraphScene scene;
    /** Creates a new instance of IPCActionsHandler */
    public IPCActionsHandler(IPCGraphScene scene) {
        this.scene = scene;
    }
    
    public void removeEventPinFromNode(CustomPinWidget pin)
    {
        String nodeKey = pin.getNodeKey();
        String evtName = pin.getEventName();
        scene.removePin(pin.getKey());
        PortletNode node = (PortletNode)scene.getPortletNode(nodeKey);
        if(node != null)
        {
            try{
                 node.getDataObject().getPortletEventingHandler().deleteProcessEvent(node.getName(),evtName);
            }catch(Exception e){
                System.out.println("Event could not be deleted  properly");
            }
        }
    }
    
    public void generatePublishEventSource(String nodeKey,String eventName)
    {
        PortletNode portletNode = scene.getPortletNode(nodeKey);
        try{
            portletNode.getDataObject().getPortletEventingHandler().generatePublishEventMethod(portletNode.getName(), eventName);
        }catch(PortletEventException e){
            e.printStackTrace();
        }
    }
     
    public void generateProcessEventSource(String nodeKey,String eventName)
    {
        PortletNode portletNode = scene.getPortletNode(nodeKey);
        try{
            portletNode.getDataObject().getPortletEventingHandler().generateProcessEventMethod(portletNode.getName(), eventName);
        }catch(PortletEventException e){
            e.printStackTrace();
        }
    }
    
}