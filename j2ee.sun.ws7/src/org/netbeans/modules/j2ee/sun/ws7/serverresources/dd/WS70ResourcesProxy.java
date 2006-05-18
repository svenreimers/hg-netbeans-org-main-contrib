/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70ResourcesProxy.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.dd;
import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;

/**
 *
 * Code reused from Appserver common API module 
 */
public class WS70ResourcesProxy implements WS70Resources {
    
    private WS70Resources resourcesRoot;
    private java.util.List listeners; 
    
    /**
     * Creates a new instance of WS70ResourcesProxy
     */
    public WS70ResourcesProxy(WS70Resources resourcesRoot) {
        this.resourcesRoot = resourcesRoot;
    }

    public void write(java.io.Writer w) throws java.io.IOException, org.netbeans.modules.j2ee.sun.dd.api.DDException {
        if (resourcesRoot!=null) resourcesRoot.write(w);
    }

    public void setWS70JdbcResource(int index, WS70JdbcResource value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70JdbcResource(index, value);
    }

    public int addWS70ExternalJndiResource(WS70ExternalJndiResource value) {
        return resourcesRoot==null?-1:resourcesRoot.addWS70ExternalJndiResource(value);
    }

    public int removeWS70ExternalJndiResource(WS70ExternalJndiResource value) {
        return resourcesRoot==null?-1:resourcesRoot.removeWS70ExternalJndiResource(value);
    }

    public int size(String name) {
        return resourcesRoot==null?-1:resourcesRoot.size(name);
    }

    public Object[] getValues(String name) {
        return resourcesRoot==null?null:resourcesRoot.getValues(name);
    }

    public Object getValue(String propertyName) {
        return resourcesRoot==null?null:resourcesRoot.getValue(propertyName);
    }

    public org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean getPropertyParent(String name) {
        return resourcesRoot==null?null:resourcesRoot.getPropertyParent(name);
    }

    public String getAttributeValue(String name) {
        return resourcesRoot==null?null:resourcesRoot.getAttributeValue(name);
    }
    public WS70MailResource getWS70MailResource(int index) {
        return resourcesRoot==null?null:resourcesRoot.getWS70MailResource(index);
    }


    public WS70JdbcResource getWS70JdbcResource(int index) {
        return resourcesRoot==null?null:resourcesRoot.getWS70JdbcResource(index);
    }


    public WS70ExternalJndiResource getWS70ExternalJndiResource(int index) {
        return resourcesRoot==null?null:resourcesRoot.getWS70ExternalJndiResource(index);
    }

    public WS70CustomResource getWS70CustomResource(int index) {
        return resourcesRoot==null?null:resourcesRoot.getWS70CustomResource(index);
    }

    public void setValue(String name, int index, Object value) {
        if (resourcesRoot!=null) resourcesRoot.setValue(name, index, value);
    }

    public int addValue(String name, Object value) {
        return resourcesRoot==null?-1:resourcesRoot.addValue(name, value);
    }

    public String[] findPropertyValue(String propName, Object value) {
        return resourcesRoot==null?null:resourcesRoot.findPropertyValue(propName, value);
    }

    public int removeValue(String name, Object value) {
        return resourcesRoot==null?-1:resourcesRoot.removeValue(name, value);
    }

    public void setValue(String name, Object value) {
        if (resourcesRoot!=null) resourcesRoot.setValue(name, value);
    }


    public void setWS70CustomResource(int index, WS70CustomResource value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70CustomResource(index, value);
    }


    public void setValue(String name, Object[] value) {
        if (resourcesRoot!=null) resourcesRoot.setValue(name, value);
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener pcl) {
        if (resourcesRoot != null) 
            resourcesRoot.addPropertyChangeListener(pcl);
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener pcl) {
        if (resourcesRoot != null) 
            resourcesRoot.removePropertyChangeListener(pcl);
        listeners.remove(pcl);
    }

    public void setWS70MailResource(int index, WS70MailResource value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70MailResource(index, value);
    }

    public int addWS70MailResource(WS70MailResource value) {
        return resourcesRoot==null?-1:resourcesRoot.addWS70MailResource(value);
    }

    public int removeWS70MailResource(WS70MailResource value) {
        return resourcesRoot==null?-1:resourcesRoot.removeWS70MailResource(value);
    }

    public void setWS70ExternalJndiResource(WS70ExternalJndiResource[] value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70ExternalJndiResource(value);
    }


    public int addWS70CustomResource(WS70CustomResource value) {
        return resourcesRoot==null?-1:resourcesRoot.addWS70CustomResource(value);
    }

    public int removeWS70CustomResource(WS70CustomResource value) {
        return resourcesRoot==null?-1:resourcesRoot.removeWS70CustomResource(value);
    }

    public int addWS70JdbcResource(WS70JdbcResource value) {
        return resourcesRoot==null?-1:resourcesRoot.addWS70JdbcResource(value);
    }

    public int removeWS70JdbcResource(WS70JdbcResource value) {
        return resourcesRoot==null?-1:resourcesRoot.removeWS70JdbcResource(value);
    }


    public void setWS70ExternalJndiResource(int index, WS70ExternalJndiResource value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70ExternalJndiResource(index, value);
    }

    public void setWS70CustomResource(WS70CustomResource[] value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70CustomResource(value);
    }

    public void setWS70JdbcResource(WS70JdbcResource[] value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70JdbcResource(value);
    }

    public void setWS70MailResource(WS70MailResource[] value) {
        if (resourcesRoot!=null) resourcesRoot.setWS70MailResource(value);
    }

    public void write(java.io.OutputStream os) throws java.io.IOException {
        if (resourcesRoot != null) {
            resourcesRoot.write(os);
        }
    }

    public void merge(org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean root, int mode) {
        if (root != null) {
            if (root instanceof WS70ResourcesProxy)
                resourcesRoot.merge(((WS70ResourcesProxy)root).getOriginal(), mode);
            else resourcesRoot.merge(root, mode);
        }
    }

    public WS70Resources getOriginal() {
        return resourcesRoot;
    }
    
    public Object getValue(String name, int index) {
        return resourcesRoot==null?null:resourcesRoot.getValues(name);
    }

    public void removeValue(String name, int index) {
        if (resourcesRoot!=null) resourcesRoot.removeValue(name, index);
    }


    public int sizeWS70MailResource() {
        return resourcesRoot==null?-1:resourcesRoot.sizeWS70MailResource();
    }


    public int sizeWS70JdbcResource() {
        return resourcesRoot==null?-1:resourcesRoot.sizeWS70JdbcResource();
    }


    public int sizeWS70ExternalJndiResource() {
        return resourcesRoot==null?-1:resourcesRoot.sizeWS70ExternalJndiResource();
    }

    public int sizeWS70CustomResource() {
        return resourcesRoot==null?-1:resourcesRoot.sizeWS70CustomResource();
    }

    public WS70CustomResource newWS70CustomResource() {
        return resourcesRoot==null?null:resourcesRoot.newWS70CustomResource();
    }



    public WS70MailResource[] getWS70MailResource() {
        return resourcesRoot==null?null:resourcesRoot.getWS70MailResource();
    }



    public WS70JdbcResource[] getWS70JdbcResource() {
        return resourcesRoot==null?null:resourcesRoot.getWS70JdbcResource();
    }



    public WS70ExternalJndiResource[] getWS70ExternalJndiResource() {
        return resourcesRoot==null?null:resourcesRoot.getWS70ExternalJndiResource();
    }

    public String dumpBeanNode() {
        return resourcesRoot==null?null:resourcesRoot.dumpBeanNode();
    }

 
    public String getAttributeValue(String propName, String name) {
        return resourcesRoot==null?null:resourcesRoot.getAttributeValue(propName, name);
    }

    public String getAttributeValue(String propName, int index, String name) {
        return resourcesRoot==null?null:resourcesRoot.getAttributeValue(propName, index, name);
    }

 

    public WS70CustomResource[] getWS70CustomResource() {
        return resourcesRoot==null?null:resourcesRoot.getWS70CustomResource();
    }

    public WS70ExternalJndiResource newWS70ExternalJndiResource() {
        return resourcesRoot==null?null:resourcesRoot.newWS70ExternalJndiResource();
    }

    public WS70JdbcResource newWS70JdbcResource() {
        return resourcesRoot==null?null:resourcesRoot.newWS70JdbcResource();
    }



    public WS70MailResource newWS70MailResource() {
        return resourcesRoot==null?null:resourcesRoot.newWS70MailResource();
    }

    public void setAttributeValue(String name, String value) {
        if (resourcesRoot!=null) resourcesRoot.setAttributeValue(name, value);
    }

    public void setAttributeValue(String propName, String name, String value) {
        if (resourcesRoot!=null) resourcesRoot.setAttributeValue(propName, name, value);
    }

    public void setAttributeValue(String propName, int index, String name, String value) {
        if (resourcesRoot!=null) resourcesRoot.setAttributeValue(propName, index, name, value);
    }

    public Object clone() {
       WS70ResourcesProxy proxy = null;
        if (resourcesRoot==null)
            proxy = new WS70ResourcesProxy(null);
        else {
            WS70Resources clonedResources=(WS70Resources)resourcesRoot.clone();
            proxy = new WS70ResourcesProxy(clonedResources);
        }
        return proxy;
    }
   /** Resources have no version (at present) so we could throw 
       UnsupportedOperationException but it's probably just better to 
       clone it since that is what this method does anyway.
    *
     */
    public CommonDDBean cloneVersion(String version) {
        return (CommonDDBean) clone();
    }


    public void write(java.io.File f) throws java.io.IOException, org.netbeans.modules.schema2beans.Schema2BeansRuntimeException {
        if (resourcesRoot!=null) resourcesRoot.write(f);
    }
    
}
