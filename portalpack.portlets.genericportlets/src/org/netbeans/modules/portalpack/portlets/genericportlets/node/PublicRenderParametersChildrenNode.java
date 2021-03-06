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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Satyaranjan
 */
 class PublicRenderParametersChildrenNode extends Children.Keys{

   private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
   private PortletXMLDataObject dbObj;
  
   public PublicRenderParametersChildrenNode(PortletXMLDataObject dbObj)
   {   
            this.dbObj = dbObj;
   }
   
   public synchronized void updateKeys(){
        TreeSet ts = new TreeSet();
      
        setKeys(ts);
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                List list =new  ArrayList();
                PortletApp portletApp = null;
                try{
                    portletApp = dbObj.getPortletApp();
                }catch(Exception ex){
                    logger.log(Level.SEVERE,"Error getting PortletApp",ex);
                }
                if(portletApp == null) return;
                PublicRenderParameterType[] publicRenderParameterTypes = portletApp.getPublicRenderParameter();
                for(int i=0;i<publicRenderParameterTypes.length;i++)
                {
                     list.add(publicRenderParameterTypes[i]);
                }
                setKeys(list);
                return;
                
            }}, 0);
            
    }
    protected Node[] createNodes(Object key) {
        if(key == null) return new Node[]{};
        
        if(key instanceof PublicRenderParameterType)
            return new Node[]{new PublicRenderParameterNode((PublicRenderParameterType)key,dbObj)};
        else    
            return new Node[]{};
    }
    
    protected void addNotify() {
        updateKeys();
    }
    
    @Override
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }
    
    public void refreshPortletDOB()
    {
        dbObj.refreshMe();
    }

}
