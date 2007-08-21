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

package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.ConsumeEventPinMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import java.awt.Image;
import java.awt.Paint;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.util.WidgetUtil;
import org.openide.util.Utilities;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.AcceptProvider;
import java.awt.datatransfer.DataFlavor;
import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction.State;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.EdgePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.EventPinPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.IPCPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.NodePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomNodeWidget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satyaranjan
 */
public class IPCGraphScene extends CustomVMDGraphScene {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final Image IMAGE_LIST = Utilities.loadImage("de/eppleton/visualexplorer/resources/list_16.png"); // NOI18N
    private static final Image IMAGE_CANVAS = Utilities.loadImage("de/eppleton/visualexplorer/resources/custom_displayable_16.png"); // NOI18N
    private static final Image IMAGE_COMMAND = Utilities.loadImage("de/eppleton/visualexplorer/resources/command_16.png"); // NOI18N
    private static final Image IMAGE_ITEM = Utilities.loadImage("de/eppleton/visualexplorer/resources/item_16.png"); // NOI18N
    private static final Image GLYPH_PRE_CODE = Utilities.loadImage("de/eppleton/visualexplorer/resources/preCodeGlyph.png"); // NOI18N
    private static final Image GLYPH_POST_CODE = Utilities.loadImage("de/eppleton/visualexplorer/resources/postCodeGlyph.png"); // NOI18N
    private static final Image GLYPH_CANCEL = Utilities.loadImage("de/eppleton/visualexplorer/resources/cancelGlyph.png"); // NOI18N
    private static final Image IMAGE_PORTLET=Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/portletapp.gif");// NOI18N
    private static final Image IMAGE_PUBLISH_EVENT=Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/generate.png");// NOI18N
    private static final Image IMAGE_PROCESS_EVENT=Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/consume.png");// NOI18N

    private static Paint PAINT_BACKGROUND;

    static {
        Image sourceImage = Utilities.loadImage ("org/netbeans/modules/portalpack/portlets/genericportlets/resources/paper_grid.png"); // NOI18N
        int width = sourceImage.getWidth (null);
        int height = sourceImage.getHeight (null);
        BufferedImage image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics ();
        graphics.drawImage (sourceImage, 0, 0, null);
        graphics.dispose ();
        PAINT_BACKGROUND = new TexturePaint (image, new Rectangle (0, 0, width, height));
    }
 
    private static int nodeID = 1;
    private static int edgeID = 1;
    
    private static  Hashtable nodeMap = new Hashtable();
    private static  Hashtable edgeMap = new Hashtable();
   
    private WidgetAction popupMenuAction;
    private WidgetAction editorAction = ActionFactory.createInplaceEditorAction(new EventNameTextFieldEditor(this));
    private WidgetAction eventingPopUpMenuProvider;
    private WidgetAction consumeEventPopUpMenuProvider;
 
    private WidgetAction connectAction;
    private WidgetAction reconnectAction;
    private IPCStoryBoardTopComponent ipcTop;
    private IPCActionsHandler actionsHandler;
    
    /** Creates a new instance of IPCGraphScene */
    
    public IPCGraphScene(IPCStoryBoardTopComponent ipcTop) {
        super();
        this.ipcTop = ipcTop;
        this.actionsHandler = new IPCActionsHandler(this);
      
       connectAction = ActionFactory.createConnectAction (connectionLayer, new SceneConnectProvider (this));
       popupMenuAction = ActionFactory.createPopupMenuAction (new IPCPopUpMenuProvider (this));
       eventingPopUpMenuProvider = ActionFactory.createPopupMenuAction(new EventPinPopUpMenuProvider(this));
       consumeEventPopUpMenuProvider = ActionFactory.createPopupMenuAction(new ConsumeEventPinMenuProvider(this));
       setBorder(BorderFactory.createBevelBorder(true));
       setBackground(PAINT_BACKGROUND);
        getActions().addAction(popupMenuAction);
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {  
            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                Object obj = null;
                try{
                  
                    obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node","application/x-java-openide-nodednd"));
               
                
                }catch(Exception e){
                    e.printStackTrace();
                }
                 
                 if(obj == null)
                 {
                
                     return ConnectorState.REJECT;
                     
                 }else if(obj instanceof PortletNode)
                 {
                     return ConnectorState.ACCEPT;
                 }
                 return ConnectorState.REJECT;
               
            }
            
            public void accept(Widget widget, Point point, Transferable transferable) {
                try{
                    Object obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node","application/x-java-openide-nodednd"));
                    PortletNode node = (PortletNode)obj;
                    addPortletNode(node,point);
                }catch(Exception e){
                    e.printStackTrace();
                }    
               
            }
       
        }));
        
    }
    
    public IPCActionsHandler getTaskHandler()
    {
        return actionsHandler;
    }
    
    public  void addPortletNode(PortletNode node,Point point) {
        
        String name = node.getName();
        String key = node.getID();
        if(checkIfNodePresent(key))
            return;
        
        //check if eventing is supported for this portlet 
        if(!node.getDataObject().getPortletEventingHandler().isEventingSupported())
            return;
        List glyphs = new ArrayList();
        glyphs.add(IMAGE_PORTLET);
        CustomNodeWidget mobileWidget = (CustomNodeWidget)WidgetUtil.createNode(this, point.x, point.y, IMAGE_LIST, key, name, "List", glyphs);
        mobileWidget.getActions().addAction(connectAction);
        mobileWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new NodePopUpMenuProvider(this,key)));
        //mobileWidget.getActions().addAction(ActionFactory.createResizeAction());
        String nodeID = key;
        PortletXMLDataObject dobj = node.getDataObject();
        QName[] events = dobj.getPortletEventingHandler().getPublishEvents(name);
        if(events != null)
        {
             for(int i=0;i<events.length;i++)
             {    
                 addEventPinToNode(nodeID,events[i]);
             }
        }
        
        QName[] processEvents = dobj.getPortletEventingHandler().getProcessEvents(name);
        if(processEvents != null)
        {
            for(int i=0;i<processEvents.length;i++)
            {
                addProcessEventPinToNode(nodeID,processEvents[i]);
            }
        }    
      
        nodeMap.put(nodeID, node);
        checkAndPerformNodeDependency((CustomNodeWidget)mobileWidget);
        validate();
    }
    
   
    
    public boolean checkIfNodePresent(String nodeID)
    {
        if(nodeMap.get(nodeID) != null)
            return true;
        else
            return false;
    }
    
    CustomPinWidget addEventPinToNode(String nodeID,QName event)
    {
         String eventName = event.toString();
        // String localPart = "";
         VMDPinWidget pin1 = WidgetUtil.createPin(this,nodeID, nodeID+"_"+eventName, IMAGE_PUBLISH_EVENT, eventName, "Element");
         ((CustomPinWidget)pin1).setEventName(eventName);
         pin1.getActions().addAction(connectAction);
         pin1.getActions().addAction(editorAction);
         pin1.getActions().addAction(eventingPopUpMenuProvider);
         return (CustomPinWidget)pin1;
    }
    
    CustomPinWidget addProcessEventPinToNode(String nodeID,QName event)
    {
         String eventName = event.toString();
         VMDPinWidget consumePin = WidgetUtil.createPin(this, nodeID, nodeID+"_"+"consume_"+eventName, IMAGE_PROCESS_EVENT, "consume_"+eventName, "Element");
         ((CustomPinWidget)consumePin).setEventName(eventName);
         ((CustomPinWidget)consumePin).getActions().addAction(consumeEventPopUpMenuProvider);
         return (CustomPinWidget)consumePin;
    }
    
    public void deletePortletNodeFromScene(String nodeID,boolean removeRef) {
        Object obj = nodeMap.get(nodeID);
        if(obj != null) {
           this.removeNodeWithEdges(nodeID);
           if(removeRef)
               nodeMap.remove(nodeID);
            
        }
    }
    
    public void addEvent(String nodeKey)
    {
        // findWidget(nodeKey).revalidate();
          PortletNode node = getPortletNode(nodeKey);
          if(node == null)
              return;
          //TODO String evtName = resolveNewEventName(node);
          String evtName = "New Event";
          QName evtQName = new QName(evtName);
          addEventPinToNode(nodeKey, evtQName);
          try{
               if(!node.getDataObject().getPortletEventingHandler().addPublishEvent(node.getName(), evtQName,null))
               return;
          }catch(Exception e){
              e.printStackTrace();
              return;
          }
          //check dependency..
          Widget nodeWidget = findWidget(nodeKey);
          if(nodeWidget != null && nodeWidget instanceof CustomNodeWidget)
          {
            removeEdgesOfNode(nodeKey);
            checkAndPerformNodeDependency((CustomNodeWidget)nodeWidget);
          }
          
          validate();
          //revalidate();
    }
    
    private String resolveNewEventName(PortletNode node)
    {
        String prefix = "New_Event";
        String evtName = prefix;
        int i=1;
//TODO        while(node.getDataObject().getPortletEventingHandler().isPublishEventExists(node.getName(),evtName))
        {
            evtName =  prefix + "_"+i;
            i++;
        }
        return evtName;
    }
    
    private void deleteEdgeFromScene(String edgeID,boolean removeRef) {
        Object obj = edgeMap.get(edgeID);
        if(obj != null) {
           // nodeMap.remove(nodeID);
          //  if(removeRef)
            try{
                this.removeEdge(edgeID);
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }
    
    void checkAndPerformNodeDependency(CustomNodeWidget nodeWidget)
    {
        String orgNodeKey = nodeWidget.getNodeKey();   //getNodeName();
        PortletNode orgPortletNode = (PortletNode)nodeMap.get(orgNodeKey);
        String orgNodename = orgPortletNode.getName();
        QName[] consumeEvts = orgPortletNode.getDataObject().getPortletEventingHandler().getProcessEvents(orgNodename);
        QName[] sourceEvts = orgPortletNode.getDataObject().getPortletEventingHandler().getPublishEvents(orgNodename);
        Set s = nodeMap.keySet();
        Iterator it = s.iterator();
        while(it.hasNext())
        {
            String ndKey = (String)it.next();
            PortletNode portletNode = (PortletNode)nodeMap.get(ndKey);
            if(portletNode == null) continue;
            QName[] evts = portletNode.getDataObject().getPortletEventingHandler().getPublishEvents(portletNode.getName());
            
            for(int i=0;i<consumeEvts.length;i++)
            {
   //TODO             if(hasString(consumeEvts[i], evts))
                {
                    Object ob = findWidget(ndKey+"_"+consumeEvts[i]);
                    if(ob != null && ob instanceof CustomPinWidget)
                    {
                       CustomPinWidget pin = (CustomPinWidget)ob;
                       if(pin == null)
                       {
                            continue;
                       }
                       
                       connectBothPortletNodes(pin,nodeWidget);
                    }
                }
            }
            
            QName[] targetConsumeEvts = portletNode.getDataObject().getPortletEventingHandler().getProcessEvents(portletNode.getName());
            for(int i=0;i<sourceEvts.length;i++)
            {
                //ignore circular dependency 
                if(orgNodeKey.equals(ndKey))
                    continue;
                
     //TODO           if(hasString(sourceEvts[i], targetConsumeEvts))
                {
                    Object ob = findWidget(orgNodeKey+"_"+sourceEvts[i]);
                    if(ob instanceof CustomPinWidget)
                    {
                       CustomPinWidget pin = (CustomPinWidget)ob;
                       if(pin == null)
                       {
                            continue;
                       }
                       VMDNodeWidget targetNodeWidget = (VMDNodeWidget)findWidget(ndKey);
                       if(targetNodeWidget != null)
                             connectBothPortletNodes(pin,(CustomNodeWidget)targetNodeWidget);
                    }
                }
            }
        }
    }
    
    private void connectBothPortletNodes(CustomPinWidget sourceWidget,CustomNodeWidget targetNode)
    {
            String eventName = ((CustomPinWidget)sourceWidget).getEventName();
            CustomPinWidget consumePin = (CustomPinWidget)findWidget(targetNode.getNodeKey()+"_"+"consume_"+eventName);
            
            //a create a cosumer pin
            if(consumePin == null){
                 consumePin = (CustomPinWidget)WidgetUtil.createPin(this, targetNode.getNodeKey(), targetNode.getNodeKey()+"_"+"consume_"+eventName, IMAGE_PROCESS_EVENT, "consume_"+eventName, "Element");
                 consumePin.setEventName(eventName);
                 consumePin.getActions().addAction(consumeEventPopUpMenuProvider);
            }
           // else
            //    System.out.println("Pin Exist..............");
            
            String edge = "edge" + edgeID ++;
            Widget edgeWidget = addEdge (edge);
            edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction (new EdgePopUpMenuProvider(edge,this,consumePin,sourceWidget)));
            setEdgeSource(edge,((CustomPinWidget)sourceWidget).getKey());
            setEdgeTarget(edge,consumePin.getKey());
            edgeMap.put(edge, new Object());
    }
    
    private boolean hasString(String org,String[] arr)
    {
        for(int i=0;i<arr.length;i++)
        {
            if(org.equals(arr[i]))
                return true;
        }
        return false;
    }
    
    public void resetScene() {
       
       
        
        Set edgekeys = edgeMap.keySet();
        Iterator edgeit = edgekeys.iterator();
        while(edgeit.hasNext()) {
            String edgeId = (String)edgeit.next();
     ///       deleteEdgeFromScene(edgeId,false);
        }
        
        Set keys = nodeMap.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
            String nodeId = (String)it.next();
            deletePortletNodeFromScene(nodeId,false);
        }
        //this.validate();
        nodeMap.clear();
        
        nodeMap.clear();
        edgeMap.clear();
        this.removeChildren();
        revalidate(true);
        ipcTop.reset();
        //this.
       
        
        //this.resetScene();
    }
    
   
    
    //inner class started
      private class SceneCreateAction extends WidgetAction.Adapter {

        public State mousePressed (Widget widget, WidgetMouseEvent event) {
            if (event.getClickCount () == 1)
                if (event.getButton () == MouseEvent.BUTTON1 || event.getButton () == MouseEvent.BUTTON2) {

                    //addNode ("node" + nodeCounter ++).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));

                    return State.CONSUMED;
                }
            return State.REJECTED;
        }

    }

    private class SceneConnectProvider implements ConnectProvider {

        private Widget source = null;
    ////    private VMDPinWidget target = null;
        private CustomNodeWidget targetNode = null;
        private IPCGraphScene scene;
        public SceneConnectProvider(IPCGraphScene scene)
        {
            this.scene = scene;
        }

        public boolean isSourceWidget (Widget sourceWidget) {
            
         
            if(sourceWidget instanceof VMDPinWidget)
            {
                VMDPinWidget pinWidget = (VMDPinWidget)sourceWidget;
                
                source = (VMDPinWidget)sourceWidget;
                return true;
               
            }
            else
                return false;
           
        }

        public ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
           
            if(targetWidget != null && targetWidget instanceof CustomNodeWidget)
            {
                targetNode = (CustomNodeWidget)targetWidget;
                return ConnectorState.ACCEPT;
            }else{
                Widget widget = targetWidget.getParentWidget();
                if(widget instanceof CustomNodeWidget)
                {
                   
                    targetNode = (CustomNodeWidget)widget;
                    return ConnectorState.ACCEPT;
                }
                else
                return ConnectorState.REJECT_AND_STOP;
            }
           
        }

        public boolean hasCustomTargetWidgetResolver (Scene scene) {
            return false;
        }

        public Widget resolveTargetWidget (Scene scene, Point sceneLocation) {
           
            return null;
        }

        public void createConnection (Widget sourceWidget, Widget targetWidget) {
            
           
            if(source == null || targetNode == null)
                return;
            if(sourceWidget == null || targetWidget == null)
                return;
            
            String eventName = ((CustomPinWidget)sourceWidget).getEventName();
            CustomPinWidget consumePin = (CustomPinWidget)findWidget(targetNode.getNodeKey()+"_"+"consume_"+eventName);
            
            //a create a cosumer pin
            if(consumePin == null){
                 consumePin = (CustomPinWidget)WidgetUtil.createPin(scene, targetNode.getNodeKey(), targetNode.getNodeKey()+"_"+"consume_"+eventName, IMAGE_PROCESS_EVENT, "consume_"+eventName, "Element");
                 consumePin.setEventName(eventName);
                 consumePin.getActions().addAction(consumeEventPopUpMenuProvider);
            }
           // else
           //   System.out.println("Pin Exist..............");
            String edge = "edge" + edgeID ++;
            Widget edgeWidget = addEdge (edge);
            edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction (new EdgePopUpMenuProvider(edge,scene,consumePin,sourceWidget)));
            setEdgeSource(edge,((CustomPinWidget)sourceWidget).getKey());
            setEdgeTarget(edge,consumePin.getKey());
            edgeMap.put(edge, new Object());
            
            PortletNode targetPortletNode = (PortletNode)nodeMap.get(targetNode.getNodeKey());
            if(targetPortletNode != null)
            {
                try{
//TODO                     targetPortletNode.getDataObject().getPortletEventingHandler().addProcessEvent(targetPortletNode.getName(), eventName,null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //added code to put the data in map
            Widget parentSourceWidget = sourceWidget.getParentWidget();
            if(parentSourceWidget instanceof VMDNodeWidget)
            {
                //System.out.println("Parent Node name is--------------------"+ ((CustomNodeWidget)parentSourceWidget).getNodeKey());
                
            }
            parentSourceWidget = targetWidget.getParentWidget();
            if(parentSourceWidget instanceof VMDNodeWidget)
            {
               // System.out.println("Target Parent Node name is--------------------"+ ((CustomNodeWidget)parentSourceWidget).getNodeKey());
                
            }
         
        }

    }

    private class SceneReconnectProvider implements ReconnectProvider {

        String edge;
        String originalNode;
        String replacementNode;

        public void reconnectingStarted (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public void reconnectingFinished (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public boolean isSourceReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource (edge) : null;
            return originalNode != null;
        }

        public boolean isTargetReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget (edge) : null;
            return originalNode != null;
        }

        public ConnectorState isReplacementWidget (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            Object object = findObject (replacementWidget);
            replacementNode = isNode (object) ? (String) object : null;
            if (replacementNode != null)
                return ConnectorState.ACCEPT;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        public boolean hasCustomReplacementWidgetResolver (Scene scene) {
            return false;
        }

        public Widget resolveReplacementWidget (Scene scene, Point sceneLocation) {
            return null;
        }
        
        public void reconnect (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            if (replacementWidget == null)
                removeEdge (edge);
            else if (reconnectingSource)
                setEdgeSource (edge, replacementNode);
            else
                setEdgeTarget (edge, replacementNode);
        }

    }  
    
    public PortletNode getPortletNode(String nodeKey)
    {
        return (PortletNode)nodeMap.get(nodeKey);
    }
    
    private void removeEdgesOfNode(String nodeKey)
    {
        Collection pins = getNodePins(nodeKey);
        Iterator pinsIt = pins.iterator();
        while(pinsIt.hasNext())
        {
            String pin= (String)pinsIt.next();
            Collection edges = findPinEdges(pin,true,true);//((CustomPinWidget)widget).getKey(),true,true);
        
            Iterator it = edges.iterator();
            while(it.hasNext())
            {
                removeEdge((String)it.next());
            }
        }
    }
    
}

class EventNameTextFieldEditor implements TextFieldInplaceEditor {
    private final IPCGraphScene scene;
    public EventNameTextFieldEditor(IPCGraphScene scene)
    {
        this.scene = scene;
    }
    public boolean isEnabled(Widget widget) {
        return true;
    }

    public String getText(Widget widget) {
        
        return ((CustomPinWidget) widget).getPinName();
    }

    public void setText(final Widget widget, String text) {
        if(!CoreUtil.validateString(text, false))
        {
            NotifyDescriptor nd =new  NotifyDescriptor.Message("Invalid Event Name",NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        final String nodeKey = ((CustomPinWidget) widget).getNodeKey();
        PortletNode pNode = scene.getPortletNode(nodeKey);
        
        if(JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),"Are you sure to rename the Event ? ", "Rename", JOptionPane.YES_NO_OPTION)==
                                                        JOptionPane.NO_OPTION)
            return;
        QName[] existingPublishEvents = pNode.getDataObject().getPortletEventingHandler().getPublishEvents(pNode.getName());
 //TODO       if(WidgetUtil.hasString(text,existingPublishEvents))
        if(1 != 0) //TODO
        {
            
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),"An Event with same name already exists.", "Rename", JOptionPane.ERROR_MESSAGE);
                                                        
            return;
        }
        
        String oldEventName = ((CustomPinWidget) widget).getEventName();
        ((CustomPinWidget) widget).setPinName(text);
        ((CustomPinWidget) widget).setEventName(text);
       
        
       
  //      try{
////TODO          pNode.getDataObject().getPortletEventingHandler().renamePublishEvent(pNode.getName() , oldEventName,text,null);
   //     }catch(PortletEventException e){
    //        e.printStackTrace();
     //   }
        
        //remove edges
        
        Collection pins = scene.getNodePins(nodeKey);
        Iterator pinsIt = pins.iterator();
        while(pinsIt.hasNext())
        {
            String pin= (String)pinsIt.next();
            Collection edges = scene.findPinEdges(pin,true,true);//((CustomPinWidget)widget).getKey(),true,true);
        
            Iterator it = edges.iterator();
            while(it.hasNext())
            {
                scene.removeEdge((String)it.next());
            }
        }
        scene.removePin(((CustomPinWidget)widget).getKey());
//TODO        scene.addEventPinToNode(nodeKey,text);
        Widget nodeWidget = scene.findWidget(nodeKey);
        Object ob = scene.findWidget(nodeKey+"_"+text);
        System.out.println("Find Widget :::::::::::::::::::::::::::; "+ob);
        if(nodeWidget != null && nodeWidget instanceof CustomNodeWidget)
            scene.checkAndPerformNodeDependency((CustomNodeWidget)nodeWidget);
       
        scene.revalidate();
       
    }

}
