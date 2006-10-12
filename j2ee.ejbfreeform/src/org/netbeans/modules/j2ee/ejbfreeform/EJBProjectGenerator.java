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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads/writes project.xml.
 *
 * @author  Jesse Glick, David Konecny, Pavel Buzek, Martin Adamek
 */
public class EJBProjectGenerator {

    public static final String NS_GENERAL = "http://www.netbeans.org/ns/freeform-project/1"; // NOI18N
    
    //    /** Keep root elements in the order specified by project's XML schema. */
    private static final String[] rootElementsOrder = new String[]{"name", "properties", "folders", "ide-actions", "export", "view", "subprojects"}; // NOI18N
    private static final String[] viewElementsOrder = new String[]{"items", "context-menu"}; // NOI18N
    
    //    // this order is not required by schema, but follow it to minimize randomness a bit
    private static final String[] folderElementsOrder = new String[]{"source-folder", "build-folder"}; // NOI18N
    private static final String[] viewItemElementsOrder = new String[]{"source-folder", "source-file"}; // NOI18N
    
    private EJBProjectGenerator() {}
    
    /**
     * @param soruces list of pairs[relative path, display name]
     */
    public static void putEJBSourceFolder(AntProjectHelper helper, List/*<String>*/ sources) {
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element foldersEl = Util.findElement(data, "folders", NS_GENERAL); // NOI18N
        if (foldersEl == null) {
            foldersEl = doc.createElementNS(NS_GENERAL, "folders"); // NOI18N
            Util.appendChildElement(data, foldersEl, rootElementsOrder);
        }
        Element viewEl = Util.findElement(data, "view", NS_GENERAL); // NOI18N
        if (viewEl == null) {
            viewEl = doc.createElementNS(NS_GENERAL, "view"); // NOI18N
            Util.appendChildElement(data, viewEl, rootElementsOrder);
        }
        Element itemsEl = Util.findElement(viewEl, "items", NS_GENERAL); // NOI18N
        if (itemsEl == null) {
            itemsEl = doc.createElementNS(NS_GENERAL, "items"); // NOI18N
            Util.appendChildElement(viewEl, itemsEl, viewElementsOrder);
        }

        Iterator it1 = sources.iterator();
        while (it1.hasNext()) {
            String path = (String)it1.next();
            assert it1.hasNext();
            String dispname = (String)it1.next();
            Element sourceFolderEl = doc.createElementNS(NS_GENERAL, "source-folder"); // NOI18N
            Element el = doc.createElementNS(NS_GENERAL, "label"); // NOI18N
            el.appendChild(doc.createTextNode(dispname));
            sourceFolderEl.appendChild(el);
            el = doc.createElementNS(NS_GENERAL, "type"); // NOI18N
            el.appendChild(doc.createTextNode("configFilesRoot"));
            sourceFolderEl.appendChild(el);
            el = doc.createElementNS(NS_GENERAL, "location"); // NOI18N
            el.appendChild(doc.createTextNode(path));
            sourceFolderEl.appendChild(el);
            Util.appendChildElement(foldersEl, sourceFolderEl, folderElementsOrder);
            
            addSourceFolderViewItem(doc, itemsEl, EJBProjectNature.STYLE_CONFIG_FILES, "Configuration Files", path);
        }
        
        helper.putPrimaryConfigurationData(data, true);
    }
    
    // #82897: putting Enterprise Beans node to view section needs to be done separately
    public static void putEJBNodeView(AntProjectHelper helper, List/*<String>*/ sources) {
        // TODO: ma154696: add support for multiple source roots?
        // now I get only first one, have to check impact of more roots 
        // to Enterprise beans node (which one to put into view items
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element foldersEl = Util.findElement(data, "folders", NS_GENERAL); // NOI18N
        Element viewEl = Util.findElement(data, "view", NS_GENERAL); // NOI18N
        Element itemsEl = Util.findElement(viewEl, "items", NS_GENERAL); // NOI18N
        List sourceRootNames = getSourceFolders(doc, foldersEl, "java"); // NOI18N
        if (sourceRootNames.size() > 0) {
            addSourceFolderViewItem(doc, itemsEl, EJBProjectNature.STYLE_EJBS, "Enterprise Beans", (String) sourceRootNames.get(0)); // NOI18N
        }
        helper.putPrimaryConfigurationData(data, true);
    }
    
    /**
     * Adds source-folder element in view items in project.xml as the first child element
     * @param doc document to write to
     * @param itemsEl items element in project.xml
     * @param style a view style; will be one of {@link ProjectNature#getSourceFolderViewStyles}
     * @param label name for the node in logical view (mostly ignored)
     * @param path path to source folder defined by displayed node
     */
    private static void addSourceFolderViewItem(Document doc, Element itemsEl, String style, String label, String path) {
        // creates source-folder element for
        Element sourceFolderEl = doc.createElementNS(NS_GENERAL, "source-folder"); // NOI18N
        sourceFolderEl.setAttribute("style", style); // NOI18N
        Element el = doc.createElementNS(NS_GENERAL, "label"); // NOI18N
        el.appendChild(doc.createTextNode(label));
        sourceFolderEl.appendChild(el);
        el = doc.createElementNS(NS_GENERAL, "location"); // NOI18N
        el.appendChild(doc.createTextNode(path));
        sourceFolderEl.appendChild(el);
        itemsEl.insertBefore(sourceFolderEl, itemsEl.getFirstChild());
    }
    
    /**
     * Creates property entry in project.xml.
     * @param doc document to write to
     * @param parent parent element
     * @param key property name
     * @param value property value
     */
    private static void putProperty(Document doc, Element parent, String key, String value) {
        // TODO: ma154696: check NodeList length
        Element props = Util.findElement(parent, "properties", EJBProjectGenerator.NS_GENERAL); // NOI18N
        if (props == null) {
            // create the <properties> element if it doesn't exist, which it should (#56344)
            props = doc.createElementNS(NS_GENERAL, "properties");
            Util.appendChildElement(parent, props, rootElementsOrder);
        }
        Element property = findPropertyElement(props, key);
        if (property == null) {
            property = doc.createElementNS(NS_GENERAL, "property"); // NOI18N
            property.setAttribute("name", key); // NOI18N
            props.appendChild(property);
        } else {
            while (property.getFirstChild() != null)
                property.removeChild(property.getFirstChild());
        }
        property.appendChild(doc.createTextNode(value));
    }
    
    private static Element findPropertyElement(Element parent, String key) {
        for (Iterator i = Util.findSubElements(parent).iterator(); i.hasNext();) {
            Element element = (Element)i.next();
            if (element.getLocalName().equals("property") && element.getNamespaceURI().equals(NS_GENERAL)) { // NOI18N
                if (element.getAttribute("name").equals(key)) // NOI18N
                    return element;
            }
        }
        return null;
    }
    
    public static void putResourceFolder(AntProjectHelper helper, List/*<String>*/ resources) {
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        String value = (String)resources.get(0) != null ? (String)resources.get(0) : ""; // NOI18N
        putProperty(doc, data, EjbFreeformProperties.RESOURCE_DIR, value);
        helper.putPrimaryConfigurationData(data, true);
    }
    
    public static void putServerInstanceID(AntProjectHelper helper, String instanceID) {
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        putProperty(doc, data, EjbFreeformProperties.J2EE_SERVER_INSTANCE, instanceID);
        helper.putPrimaryConfigurationData(data, true);
    }
    
    public static void putServerID(AntProjectHelper helper, String serverID) {
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        putProperty(doc, data, EjbFreeformProperties.J2EE_SERVER_TYPE, serverID);
        helper.putPrimaryConfigurationData(data, true);
    }
    
    public static void putJ2EELevel(AntProjectHelper helper, String j2eeLevel) {
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        putProperty(doc, data, EjbFreeformProperties.J2EE_PLATFORM, j2eeLevel);
        helper.putPrimaryConfigurationData(data, true);
    }
    
    private static List/*<String>*/ getSourceFolders(Document doc, Element parent, String type) {
        // TODO: ma154696: add check if parent element's name is folder (or better refactor method interface)
        List result = new ArrayList();
        List sourceFolderElements = Util.findSubElements(parent);
        for (int i = 0; i < sourceFolderElements.size(); i++) {
            Element subElement = (Element) sourceFolderElements.get(i);
            Element locationEl = Util.findElement(subElement, "location", EJBProjectGenerator.NS_GENERAL);
            Element typeEl = Util.findElement(subElement, "type", EJBProjectGenerator.NS_GENERAL);
            if (typeEl != null) {
                if (typeEl.getChildNodes().item(0).getNodeValue().equals(type)) {
                    result.add(locationEl.getChildNodes().item(0).getNodeValue());
                }
            }
        }
        return result;
    }
    
    /**
     * Read EJB modules from the project.
     * @param helper AntProjectHelper instance
     * @param aux AuxiliaryConfiguration instance
     * @return list of EJBModule instances
     */
    public static List/*<EJBModule>*/ getEJBmodules(
            AntProjectHelper helper, AuxiliaryConfiguration aux) {
        //assert ProjectManager.mutex().isReadAccess() || ProjectManager.mutex().isWriteAccess();
        ArrayList list = new ArrayList();
        Element data = aux.getConfigurationFragment(EJBProjectNature.EL_EJB, EJBProjectNature.NS_EJB_2, true);
        if (data == null) {
            data = aux.getConfigurationFragment(EJBProjectNature.EL_EJB, EJBProjectNature.NS_EJB, true);
        }
        if (data == null) {
            return list;
        }
        
        List/*<Element>*/ wms = Util.findSubElements(data);
        Iterator it = wms.iterator();
        while (it.hasNext()) {
            Element wmEl = (Element)it.next();
            EJBModule wm = new EJBModule();
            Iterator it2 = Util.findSubElements(wmEl).iterator();
            while (it2.hasNext()) {
                Element el = (Element)it2.next();
                if (el.getLocalName().equals("config-files")) { // NOI18N
                    wm.configFiles = Util.findText(el);
                    continue;
                }
                if (el.getLocalName().equals("classpath")) { // NOI18N
                    wm.classpath = Util.findText(el);
                    continue;
                }
                if (el.getLocalName().equals("j2ee-spec-level")) { // NOI18N
                    wm.j2eeSpecLevel = Util.findText(el);
                }
            }
            list.add(wm);
        }
        return list;
    }
    
    /**
     * Update EJB modules of the project. Project is left modified
     * and you must save it explicitely.
     * @param helper AntProjectHelper instance
     * @param aux AuxiliaryConfiguration instance
     * @param ejbModules list of EJBModule instances
     */
    public static void putEJBModules(AntProjectHelper helper,
            AuxiliaryConfiguration aux, List/*<EJBModule>*/ ejbModules) {
        //assert ProjectManager.mutex().isWriteAccess();
        
        // This /1 vs. /2 logic is mostly copied from JavaProjectGenerator.java
        boolean need2 = false;
        for (Iterator iter = ejbModules.iterator(); iter.hasNext(); ) {
            EJBModule em = (EJBModule) iter.next();
            if (em.j2eeSpecLevel.equals("1.5")) {
                need2 = true;
                break;
            }
        }
        String namespace;
        // Look for existing /2 data.
        Element data = aux.getConfigurationFragment(EJBProjectNature.EL_EJB, EJBProjectNature.NS_EJB_2, true); // NOI18N
        if (data !=  null) {
            // if there is one, use it
            namespace = EJBProjectNature.NS_EJB_2;
        } else {
            // Or, for existing /1 data.
            namespace = need2 ? EJBProjectNature.NS_EJB_2 : EJBProjectNature.NS_EJB;
            data = aux.getConfigurationFragment(EJBProjectNature.EL_EJB, EJBProjectNature.NS_EJB, true);
            if (data != null) {
                if (need2) {
                    // Have to upgrade.
                    aux.removeConfigurationFragment(EJBProjectNature.EL_EJB, EJBProjectNature.NS_EJB, true);
                    data = helper.getPrimaryConfigurationData(true).getOwnerDocument().
                            createElementNS(EJBProjectNature.NS_EJB_2, EJBProjectNature.EL_EJB);
                } // else can use it as is
            } else {
                // Create /1 or /2 data acc. to need.
                data = helper.getPrimaryConfigurationData(true).getOwnerDocument().
                    createElementNS(namespace, EJBProjectNature.EL_EJB);
            }
        }
        
        Document doc = data.getOwnerDocument();
        List wms = Util.findSubElements(data);
        Iterator it = wms.iterator();
        while (it.hasNext()) {
            Element wmEl = (Element)it.next();
            data.removeChild(wmEl);
        }
        Iterator it2 = ejbModules.iterator();
        while (it2.hasNext()) {
            Element wmEl = doc.createElementNS(namespace, "ejb-module"); // NOI18N
            data.appendChild(wmEl);
            EJBModule wm = (EJBModule)it2.next();
            Element el;
            if (wm.configFiles != null) {
                el = doc.createElementNS(namespace, "config-files"); // NOI18N
                el.appendChild(doc.createTextNode(wm.configFiles));
                wmEl.appendChild(el);
            }
            if (wm.classpath != null) {
                el = doc.createElementNS(namespace, "classpath"); // NOI18N
                el.appendChild(doc.createTextNode(wm.classpath));
                wmEl.appendChild(el);
            }
            if (wm.j2eeSpecLevel != null) {
                el = doc.createElementNS(namespace, "j2ee-spec-level"); // NOI18N
                el.appendChild(doc.createTextNode(wm.j2eeSpecLevel));
                wmEl.appendChild(el);
            }
        }
        aux.putConfigurationFragment(data, true);
    }
    
    /**
     * Structure describing EJB module.
     * Data in the struct are in the same format as they are stored in XML.
     */
    public static final class EJBModule {
        public String configFiles;
        public String classpath;
        public String j2eeSpecLevel;
    }
    
}
