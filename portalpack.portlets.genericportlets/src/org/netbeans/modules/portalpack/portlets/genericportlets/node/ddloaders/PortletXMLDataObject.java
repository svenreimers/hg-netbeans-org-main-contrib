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
package org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders;

import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun.SunPortletXmlHandler;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.xml.multiview.DesignMultiViewDesc;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.netbeans.api.xml.cookies.CheckXMLCookie;
import org.netbeans.api.xml.cookies.ValidateXMLCookie;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventingHandler;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun.SunPortletEventingHandlerImpl;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataObject;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.filesystems.FileUtil;

public class PortletXMLDataObject extends XmlMultiViewDataObject
        implements Lookup.Provider {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    //private ModelSynchronizer modelSynchronizer;
    private PortletApp portletApp;
   // private SunPortletXmlHandler sunPortletXmlHandler;
    private PortletEventingHandler portletEventingHandler;
    private FileObject portletXmlFobj;
    private static final int TYPE_TOOLBAR = 0;
    private String applicationName;
    
    public PortletXMLDataObject(FileObject pf, PortletXMLDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        this.portletXmlFobj = pf;
        CookieSet cookies = getCookieSet();
        org.xml.sax.InputSource in = DataObjectAdapters.inputSource(this);
        CheckXMLCookie checkCookie = new CheckXMLSupport(in);
        cookies.add(checkCookie);
        ValidateXMLCookie validateCookie = new ValidateXMLSupport(in);
        cookies.add(validateCookie);
         try{
            this.applicationName = ProjectUtils.getInformation(FileOwnerQuery.getOwner(this.getPrimaryFile())).getName();
        }catch(Exception e){
            applicationName = "";
        }
        
        try {
            parseDocument();
        } catch (IOException ex) {
            logger.log(Level.INFO,"Parse Error",ex);
        }catch(Exception e){
            // logger.log(Level.INFO,"Parse Error",e);
        }catch(Error e){
            logger.log(Level.INFO,"",e);
        }
        
        //Initialize portlet eventing handler
        try{
            portletEventingHandler = new SunPortletEventingHandlerImpl(pf.getParent(),this);
            //parseSunPortletXml(pf.getParent());
        }catch(Throwable e){
            e.printStackTrace();
           /// portletAppExt = null;
        }
        
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }
    
    protected Node createNodeDelegate() {
        return new PortletXMLDataNode(this);//, getLookup());
    }
    
    public void parseDocument() throws IOException {
        if (portletApp==null) {
            portletApp = getPortletApp();
        } else {
            java.io.InputStream is = getEditorSupport().getInputStream();
            PortletApp newPortletApp = null;
            try {
                newPortletApp = PortletXMLFactory.createGraph(is);
            } catch (RuntimeException ex) {
                logger.log(Level.SEVERE,"Parse Error",ex);
            }
            if (newPortletApp!=null) {
                //portletApp.merge(newPortletApp, org.netbeans.modules.schema2beans.BaseBean.MERGE_UPDATE);
                PortletXMLFactory.merge(portletApp,newPortletApp, org.netbeans.modules.schema2beans.BaseBean.MERGE_UPDATE);
            }
        }
    }
    
    public PortletEventingHandler getPortletEventingHandler()
    {
        return portletEventingHandler;
    }
    
    public String getApplicationName()
    {
        return applicationName;
    }
   
    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public PortletApp getPortletApp() throws IOException {
        if (portletApp==null) {
            if(FileUtil.toFile(getPrimaryFile()).exists())
                portletApp = PortletXMLFactory.createGraph(FileUtil.toFile(getPrimaryFile()));
        }
        return portletApp;
    }
    
    public void refreshMe() {
        try{
            parseDocument();
        }catch(Exception e){
            e.printStackTrace();
            //do nothing/
        }
        try{
            portletEventingHandler.refresh();
        }catch(Exception e){
            //do nothing
        }
        //  if(FileUtil.toFile(getPrimaryFile()).exists())
        //          portletApp = PortletApp.createGraph(FileUtil.toFile(getPrimaryFile()));
    }
    
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
    
    protected DesignMultiViewDesc[] getMultiViewDesc() {
        return new DesignMultiViewDesc[]{new DesignView(this,TYPE_TOOLBAR)};
    }
    
    protected String getPrefixMark() {
        return null;
    }
    
    private static class DesignView extends DesignMultiViewDesc {
        private int type;
        DesignView(PortletXMLDataObject dObj, int type) {
            //super(dObj, "Design"+String.valueOf(type));
            super(dObj, "Design");
            this.type=type;
        }
        
        public org.netbeans.core.spi.multiview.MultiViewElement createElement() {
            PortletXMLDataObject dObj = (PortletXMLDataObject)getDataObject();
            //            if (type==TYPE_TOOLBAR) return new BookToolBarMVElement(dObj);
            //            else return new BookTreePanelMVElement(dObj);
            return new PortletAppToolBarMVElement(dObj);
        }
        
        public java.awt.Image getIcon() {
            return org.openide.util.Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/portlet-xml.gif"); //NOI18N
        }
        
        public String preferredID() {
            return "portlet_xml_multiview_"+String.valueOf(type);
        }
    }
    
    
}