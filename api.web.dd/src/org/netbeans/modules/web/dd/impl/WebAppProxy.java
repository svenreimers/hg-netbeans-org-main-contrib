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

package org.netbeans.modules.web.dd.impl;

import org.netbeans.api.web.dd.WebApp;

/**
 * @author  mk115033
 */
public class WebAppProxy implements WebApp {
    private WebApp webApp;
    private String version;
    private java.util.List listeners;
    public boolean writing=false;
    private OutputProvider outputProvider;
    private org.xml.sax.SAXParseException error;
    private int ddStatus;

    /** Creates a new instance of WebAppProxy */
    public WebAppProxy(WebApp webApp, String version) {
        this.webApp=webApp;
        this.version = version;
        listeners = new java.util.ArrayList();
    }
    
    public void setOriginal(WebApp webApp) {
        if (this.webApp!=webApp) {
            for (int i=0;i<listeners.size();i++) {
                java.beans.PropertyChangeListener pcl = 
                    (java.beans.PropertyChangeListener)listeners.get(i);
                if (this.webApp!=null) this.webApp.removePropertyChangeListener(pcl);
                if (webApp!=null) webApp.addPropertyChangeListener(pcl);
                
            }
            this.webApp=webApp;
            if (webApp!=null) setProxyVersion(webApp.getVersion());
        }
    }
    
    public WebApp getOriginal() {
        return webApp;
    }

    public void setProxyVersion(java.lang.String value) {
        if ((version==null && value!=null) || !version.equals(value)) {
            java.beans.PropertyChangeEvent evt = 
                new java.beans.PropertyChangeEvent(this, PROPERTY_VERSION, version, value);
            version=value;
            for (int i=0;i<listeners.size();i++) {
                ((java.beans.PropertyChangeListener)listeners.get(i)).propertyChange(evt);
            }
        }
    }
    /*
    public void setVersion(java.lang.String value) {
    }
    */
    public java.lang.String getVersion() {
        return version;
    }
    public org.xml.sax.SAXParseException getError() {
        return error;
    }
    public void setError(org.xml.sax.SAXParseException error) {
        this.error=error;
    }    
    public int getStatus() {
        return ddStatus;
    }
    public void setStatus(int value) {
        if (ddStatus!=value) {
            java.beans.PropertyChangeEvent evt = 
                new java.beans.PropertyChangeEvent(this, PROPERTY_STATUS, new Integer(ddStatus), new Integer(value));
            ddStatus=value;
            for (int i=0;i<listeners.size();i++) {
                ((java.beans.PropertyChangeListener)listeners.get(i)).propertyChange(evt);
            }
        }
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener pcl) {
        if (webApp!=null) webApp.addPropertyChangeListener(pcl);
        listeners.add(pcl);
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener pcl) {
        if (webApp!=null) webApp.removePropertyChangeListener(pcl);
        listeners.remove(pcl);
    }
    
    public int addContextParam(org.netbeans.api.web.dd.InitParam value) {
        return webApp==null?-1:webApp.addContextParam(value);
    }
    
    public int addEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef value) {
        return webApp==null?-1:webApp.addEjbLocalRef(value);
    }
    
    public int addEjbRef(org.netbeans.api.web.dd.EjbRef value) {
        return webApp==null?-1:webApp.addEjbRef(value);
    }
    
    public int addEnvEntry(org.netbeans.api.web.dd.EnvEntry value) {
        return webApp==null?-1:webApp.addEnvEntry(value);
    }
    
    public int addErrorPage(org.netbeans.api.web.dd.ErrorPage value) {
        return webApp==null?-1:webApp.addErrorPage(value);
    }
    
    public int addFilter(org.netbeans.api.web.dd.Filter value) {
        return webApp==null?-1:webApp.addFilter(value);
    }
    
    public int addFilterMapping(org.netbeans.api.web.dd.FilterMapping value) {
        return webApp==null?-1:webApp.addFilterMapping(value);
    }
    
    public int addListener(org.netbeans.api.web.dd.Listener value) {
        return webApp==null?-1:webApp.addListener(value);
    }
    
    public int addMessageDestination(org.netbeans.api.web.dd.MessageDestination value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.addMessageDestination(value);
    }
    
    public int addMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.addMessageDestinationRef(value);
    }
    
    public int addMimeMapping(org.netbeans.api.web.dd.MimeMapping value) {
        return webApp==null?-1:webApp.addMimeMapping(value);
    }
    
    public int addResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef value) {
        return webApp==null?-1:addResourceEnvRef(value);
    }
    
    public int addResourceRef(org.netbeans.api.web.dd.ResourceRef value) {
        return webApp==null?-1:webApp.addResourceRef(value);
    }
    
    public int addSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint value) {
        return webApp==null?-1:webApp.addSecurityConstraint(value);
    }
    
    public int addSecurityRole(org.netbeans.api.web.dd.SecurityRole value) {
        return webApp==null?-1:webApp.addSecurityRole(value);
    }
    
    public int addServiceRef(org.netbeans.api.web.dd.ServiceRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.addServiceRef(value);
    }
    
    public int addServlet(org.netbeans.api.web.dd.Servlet value) {
        return webApp==null?-1:webApp.addServlet(value);
    }
    
    public int addServletMapping(org.netbeans.api.web.dd.ServletMapping value) {
        return webApp==null?-1:webApp.addServletMapping(value);
    }
    
    public int addTaglib(org.netbeans.api.web.dd.Taglib value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.addTaglib(value);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean createBean(String beanName) throws ClassNotFoundException {
        return webApp==null?null:webApp.createBean(beanName);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean addBean(String beanName, String[] propertyNames, Object[] propertyValues, String keyProperty) throws ClassNotFoundException, org.netbeans.api.web.dd.common.NameAlreadyUsedException {
        return webApp==null?null:webApp.addBean(beanName, propertyNames, propertyValues, keyProperty);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean addBean(String beanName) throws ClassNotFoundException {
        return webApp==null?null:webApp.addBean(beanName);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean findBeanByName(String beanName, String propertyName, String value) {
        return webApp==null?null:webApp.findBeanByName(beanName, propertyName, value);
    }
    
    public java.util.Map getAllDescriptions() {
        return webApp==null?new java.util.HashMap():webApp.getAllDescriptions();
    }
    
    public java.util.Map getAllDisplayNames() {
        return webApp==null?new java.util.HashMap():webApp.getAllDisplayNames();
    }
    
    public java.util.Map getAllIcons() {
        return webApp==null?new java.util.HashMap():webApp.getAllIcons();
    }
    
    public org.netbeans.api.web.dd.InitParam[] getContextParam() {
        return webApp==null?new org.netbeans.api.web.dd.InitParam[0]:webApp.getContextParam();
    }
    
    public org.netbeans.api.web.dd.InitParam getContextParam(int index) {
        return webApp==null?null:webApp.getContextParam(index);
    }
    
    public String getDefaultDescription() {
        return webApp==null?null:webApp.getDefaultDescription();
    }
    
    public String getDefaultDisplayName() {
        return webApp==null?null:webApp.getDefaultDisplayName();
    }
    
    public org.netbeans.api.web.dd.Icon getDefaultIcon() {
        return webApp==null?null:webApp.getDefaultIcon();
    }
    
    public String getDescription(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getDescription(locale);
    }
    
    public String getDisplayName(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getDisplayName(locale);
    }
    
    public org.netbeans.api.web.dd.EjbLocalRef[] getEjbLocalRef() {
        return webApp==null?new org.netbeans.api.web.dd.EjbLocalRef[0]:webApp.getEjbLocalRef();
    }
    
    public org.netbeans.api.web.dd.EjbLocalRef getEjbLocalRef(int index) {
        return webApp==null?null:webApp.getEjbLocalRef(index);
    }
    
    public org.netbeans.api.web.dd.EjbRef[] getEjbRef() {
        return webApp==null?new org.netbeans.api.web.dd.EjbRef[0]:webApp.getEjbRef();
    }
    
    public org.netbeans.api.web.dd.EjbRef getEjbRef(int index) {
        return webApp==null?null:webApp.getEjbRef(index);
    }
    
    public org.netbeans.api.web.dd.EnvEntry[] getEnvEntry() {
        return webApp==null?new org.netbeans.api.web.dd.EnvEntry[0]:webApp.getEnvEntry();
    }
    
    public org.netbeans.api.web.dd.EnvEntry getEnvEntry(int index) {
        return webApp==null?null:webApp.getEnvEntry(index);
    }
    
    public org.netbeans.api.web.dd.ErrorPage[] getErrorPage() {
        return webApp==null?new org.netbeans.api.web.dd.ErrorPage[0]:webApp.getErrorPage();
    }
    
    public org.netbeans.api.web.dd.ErrorPage getErrorPage(int index) {
        return webApp==null?null:webApp.getErrorPage(index);
    }
    
    public org.netbeans.api.web.dd.Filter[] getFilter() {
        return webApp==null?new org.netbeans.api.web.dd.Filter[0]:webApp.getFilter();
    }
    
    public org.netbeans.api.web.dd.Filter getFilter(int index) {
        return webApp==null?null:webApp.getFilter(index);
    }
    
    public org.netbeans.api.web.dd.FilterMapping[] getFilterMapping() {
        return webApp==null?new org.netbeans.api.web.dd.FilterMapping[0]:webApp.getFilterMapping();
    }
    
    public org.netbeans.api.web.dd.FilterMapping getFilterMapping(int index) {
        return webApp==null?null:webApp.getFilterMapping(index);
    }
    
    public java.lang.String getId() {
        return webApp==null?null:webApp.getId();
    }
    
    public String getLargeIcon() {
        return webApp==null?null:webApp.getLargeIcon();
    }
    
    public String getLargeIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getLargeIcon(locale);
    }
    
    public org.netbeans.api.web.dd.Listener[] getListener() {
        return webApp==null?new org.netbeans.api.web.dd.Listener[0]:webApp.getListener();
    }
    
    public org.netbeans.api.web.dd.Listener getListener(int index) {
        return webApp==null?null:webApp.getListener(index);
    }
    
    public org.netbeans.api.web.dd.LocaleEncodingMappingList getSingleLocaleEncodingMappingList() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getSingleLocaleEncodingMappingList();
    }
    
    public org.netbeans.api.web.dd.MessageDestination[] getMessageDestination() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?new org.netbeans.api.web.dd.MessageDestination[0]:webApp.getMessageDestination();
    }
    
    public org.netbeans.api.web.dd.MessageDestination getMessageDestination(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getMessageDestination(index);
    }
    
    public org.netbeans.api.web.dd.MessageDestinationRef[] getMessageDestinationRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?new org.netbeans.api.web.dd.MessageDestinationRef[0]:webApp.getMessageDestinationRef();
    }
    
    public org.netbeans.api.web.dd.MessageDestinationRef getMessageDestinationRef(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getMessageDestinationRef(index);
    }
    
    public org.netbeans.api.web.dd.MimeMapping[] getMimeMapping() {
        return webApp==null?new org.netbeans.api.web.dd.MimeMapping[0]:webApp.getMimeMapping();
    }
    
    public org.netbeans.api.web.dd.MimeMapping getMimeMapping(int index) {
        return webApp==null?null:webApp.getMimeMapping(index);
    }
    
    public org.netbeans.api.web.dd.ResourceEnvRef[] getResourceEnvRef() {
        return webApp==null?null:webApp==null?new org.netbeans.api.web.dd.ResourceEnvRef[0]:webApp.getResourceEnvRef();
    }
    
    public org.netbeans.api.web.dd.ResourceEnvRef getResourceEnvRef(int index) {
        return webApp==null?null:webApp.getResourceEnvRef(index);
    }
    
    public org.netbeans.api.web.dd.ResourceRef[] getResourceRef() {
        return webApp==null?new org.netbeans.api.web.dd.ResourceRef[0]:webApp.getResourceRef();
    }
    
    public org.netbeans.api.web.dd.ResourceRef getResourceRef(int index) {
        return webApp==null?null:webApp.getResourceRef(index);
    }
    
    public org.netbeans.api.web.dd.SecurityConstraint[] getSecurityConstraint() {
        return webApp==null?new org.netbeans.api.web.dd.SecurityConstraint[0]:webApp.getSecurityConstraint();
    }
    
    public org.netbeans.api.web.dd.SecurityConstraint getSecurityConstraint(int index) {
        return webApp==null?null:webApp.getSecurityConstraint(index);
    }
    
    public org.netbeans.api.web.dd.SecurityRole[] getSecurityRole() {
        return webApp==null?new org.netbeans.api.web.dd.SecurityRole[0]:webApp.getSecurityRole();
    }
    
    public org.netbeans.api.web.dd.SecurityRole getSecurityRole(int index) {
        return webApp==null?null:webApp.getSecurityRole(index);
    }
    
    public org.netbeans.api.web.dd.ServiceRef[] getServiceRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?new org.netbeans.api.web.dd.ServiceRef[0]:webApp.getServiceRef();
    }
    
    public org.netbeans.api.web.dd.ServiceRef getServiceRef(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getServiceRef(index);
    }
    
    public org.netbeans.api.web.dd.Servlet[] getServlet() {
        return webApp==null?new org.netbeans.api.web.dd.Servlet[0]:webApp.getServlet();
    }
    
    public org.netbeans.api.web.dd.Servlet getServlet(int index) {
        return webApp==null?null:webApp.getServlet(index);
    }
    
    public org.netbeans.api.web.dd.ServletMapping[] getServletMapping() {
        return webApp==null?new org.netbeans.api.web.dd.ServletMapping[0]:webApp.getServletMapping();
    }
    
    public org.netbeans.api.web.dd.ServletMapping getServletMapping(int index) {
        return webApp==null?null:webApp.getServletMapping(index);
    }
    
    public org.netbeans.api.web.dd.JspConfig getSingleJspConfig() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getSingleJspConfig();
    }
    
    public org.netbeans.api.web.dd.LoginConfig getSingleLoginConfig() {
        return webApp==null?null:webApp.getSingleLoginConfig();
    }
    
    public org.netbeans.api.web.dd.SessionConfig getSingleSessionConfig() {
        return webApp==null?null:webApp.getSingleSessionConfig();
    }
    
    public org.netbeans.api.web.dd.WelcomeFileList getSingleWelcomeFileList() {
        return webApp==null?null:webApp.getSingleWelcomeFileList();
    }
    
    public String getSmallIcon() {
        return webApp==null?null:webApp.getSmallIcon();
    }
    
    public String getSmallIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getSmallIcon(locale);
    }
    
    public org.netbeans.api.web.dd.Taglib[] getTaglib() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?new org.netbeans.api.web.dd.Taglib[0]:webApp.getTaglib();
    }
    
    public org.netbeans.api.web.dd.Taglib getTaglib(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?null:webApp.getTaglib(index);
    }
    
    public Object getValue(String name) {
        return webApp==null?null:webApp.getValue(name);
    }
    
    public boolean isDistributable() {
        return webApp==null?false:webApp.isDistributable();
    }
    
    public void merge(org.netbeans.api.web.dd.common.RootInterface bean, int mode) {
        if (webApp!=null) {
            if (bean instanceof WebAppProxy)
                webApp.merge(((WebAppProxy)bean).getOriginal(), mode);
            else webApp.merge(bean, mode);
        }
    }
    
    public void removeAllDescriptions() {
        
        if (webApp!=null) webApp.removeAllDescriptions();
    }
    
    public void removeAllDisplayNames() {
        if (webApp!=null) webApp.removeAllDisplayNames();
    }
    
    public void removeAllIcons() {
        if (webApp!=null) webApp.removeAllIcons();
    }
    
    public int removeContextParam(org.netbeans.api.web.dd.InitParam value) {
        return webApp==null?-1:webApp.removeContextParam(value);
    }
    
    public void removeDescription() {
        if (webApp!=null) webApp.removeDescription();
    }
    
    public void removeDescriptionForLocale(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.removeDescriptionForLocale(locale);
    }
    
    public void removeDisplayName() {
        if (webApp!=null) webApp.removeDisplayName();
    }
    
    public void removeDisplayNameForLocale(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.removeDisplayNameForLocale(locale);
    }
    
    public int removeEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef value) {
        return webApp==null?-1:webApp.removeEjbLocalRef(value);
    }
    
    public int removeEjbRef(org.netbeans.api.web.dd.EjbRef value) {
        return webApp==null?-1:webApp.removeEjbRef(value);
    }
    
    public int removeEnvEntry(org.netbeans.api.web.dd.EnvEntry value) {
        return webApp==null?-1:webApp.removeEnvEntry(value);
    }
    
    public int removeErrorPage(org.netbeans.api.web.dd.ErrorPage value) {
        return webApp==null?-1:webApp.removeErrorPage(value);
    }
    
    public int removeFilter(org.netbeans.api.web.dd.Filter value) {
        return webApp==null?-1:webApp.removeFilter(value);
    }
    
    public int removeFilterMapping(org.netbeans.api.web.dd.FilterMapping value) {
        return webApp==null?-1:webApp.removeFilterMapping(value);
    }
    
    public void removeIcon() {
        if (webApp!=null) webApp.removeIcon();
    }
    
    public void removeIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.removeIcon(locale);
    }
    
    public void removeLargeIcon() {
        if (webApp!=null) webApp.removeLargeIcon();
    }
    
    public void removeLargeIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.removeLargeIcon(locale);
    }
    
    public int removeListener(org.netbeans.api.web.dd.Listener value) {
        return webApp==null?-1:webApp.removeListener(value);
    }
    
    public int removeMessageDestination(org.netbeans.api.web.dd.MessageDestination value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.removeMessageDestination(value);
    }
    
    public int removeMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.removeMessageDestinationRef(value);
    }
    
    public int removeMimeMapping(org.netbeans.api.web.dd.MimeMapping value) {
        return webApp==null?-1:webApp.removeMimeMapping(value);
    }
    
    public int removeResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef value) {
        return webApp==null?-1:webApp.removeResourceEnvRef(value);
    }
    
    public int removeResourceRef(org.netbeans.api.web.dd.ResourceRef value) {
        return webApp==null?-1:webApp.removeResourceRef(value);
    }
    
    public int removeSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint value) {
        return webApp==null?-1:webApp.removeSecurityConstraint(value);
    }
    
    public int removeSecurityRole(org.netbeans.api.web.dd.SecurityRole value) {
        return webApp==null?-1:webApp.removeSecurityRole(value);
    }
    
    public int removeServiceRef(org.netbeans.api.web.dd.ServiceRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.removeServiceRef(value);
    }
    
    public int removeServlet(org.netbeans.api.web.dd.Servlet value) {
        return webApp==null?-1:webApp.removeServlet(value);
    }
    
    public int removeServletMapping(org.netbeans.api.web.dd.ServletMapping value) {
        return webApp==null?-1:webApp.removeServletMapping(value);
    }
    
    public void removeSmallIcon() {
        if (webApp!=null) webApp.removeSmallIcon();
    }
    
    public void removeSmallIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.removeSmallIcon(locale);
    }
    
    public int removeTaglib(org.netbeans.api.web.dd.Taglib value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?-1:webApp.removeTaglib(value);
    }
    
    public void setAllDescriptions(java.util.Map descriptions) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setAllDescriptions(descriptions);
    }
    
    public void setAllDisplayNames(java.util.Map displayNames) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setAllDisplayNames(displayNames);
    }
    
    public void setAllIcons(String[] locales, String[] smallIcons, String[] largeIcons) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setAllIcons(locales, smallIcons, largeIcons);
    }
    
    public void setContextParam(org.netbeans.api.web.dd.InitParam[] value) {
        if (webApp!=null) webApp.setContextParam(value);
    }
    
    public void setContextParam(int index, org.netbeans.api.web.dd.InitParam value) {
        if (webApp!=null) webApp.setContextParam(index, value);
    }
    
    public void setDescription(String description) {
        if (webApp!=null) webApp.setDescription(description);
    }
    
    public void setDescription(String locale, String description) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setDescription(locale, description);
    }
    
    public void setDisplayName(String displayName) {
        if (webApp!=null) webApp.setDisplayName(displayName);
    }
    
    public void setDisplayName(String locale, String displayName) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setDisplayName(locale, displayName);
    }
    
    public void setDistributable(boolean value) {
        if (webApp!=null) webApp.setDistributable(value);
    }
    
    public void setEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef[] value) {
        if (webApp!=null) webApp.setEjbLocalRef(value);
    }
    
    public void setEjbLocalRef(int index, org.netbeans.api.web.dd.EjbLocalRef value) {
        if (webApp!=null) webApp.setEjbLocalRef(index, value);
    }
    
    public void setEjbRef(org.netbeans.api.web.dd.EjbRef[] value) {
        if (webApp!=null) webApp.setEjbRef(value);
    }
    
    public void setEjbRef(int index, org.netbeans.api.web.dd.EjbRef value) {
        if (webApp!=null) webApp.setEjbRef(index, value);
    }
    
    public void setEnvEntry(org.netbeans.api.web.dd.EnvEntry[] value) {
        if (webApp!=null) webApp.setEnvEntry(value);
    }
    
    public void setEnvEntry(int index, org.netbeans.api.web.dd.EnvEntry value) {
        if (webApp!=null) webApp.setEnvEntry(index, value);
    }
    
    public void setErrorPage(org.netbeans.api.web.dd.ErrorPage[] value) {
        if (webApp!=null) webApp.setErrorPage(value);
    }
    
    public void setErrorPage(int index, org.netbeans.api.web.dd.ErrorPage value) {
        if (webApp!=null) webApp.setErrorPage(index, value);
    }
    
    public void setFilter(org.netbeans.api.web.dd.Filter[] value) {
        if (webApp!=null) webApp.setFilter(value);
    }
    
    public void setFilter(int index, org.netbeans.api.web.dd.Filter value) {
        if (webApp!=null) webApp.setFilter(index, value);
    }
    
    public void setFilterMapping(org.netbeans.api.web.dd.FilterMapping[] value) {
        if (webApp!=null) {
        org.netbeans.api.web.dd.FilterMapping[] oldMappings = getFilterMapping();
        int lenOld = oldMappings.length;
        int lenNew = (value==null?0:value.length);
        if (lenOld<=lenNew) {
            for (int i=0;i<lenOld;i++) {
                webApp.setFilterMapping(i,value[i]);
            }
            for (int i=lenOld;i<lenNew;i++) {
                webApp.addFilterMapping(value[i]);
            }
        } else {
            for (int i=0;i<lenNew;i++) {
                webApp.setFilterMapping(i,value[i]);
            } 
            for (int i=lenOld-1;i>=lenNew;i--) {
                webApp.removeFilterMapping(oldMappings[i]);
            }
        }
        }
    }
    
    public void setFilterMapping(int index, org.netbeans.api.web.dd.FilterMapping value) {
        if (webApp!=null) webApp.setFilterMapping(index, value);
    }
    
    public void setIcon(org.netbeans.api.web.dd.Icon icon) {
        if (webApp!=null) webApp.setIcon(icon);
    }
    
    public void setId(java.lang.String value) {
        if (webApp!=null) webApp.setId(value);
    }
    
    public void setJspConfig(org.netbeans.api.web.dd.JspConfig value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setJspConfig(value);
    }
    
    public void setLargeIcon(String icon) {
        if (webApp!=null) webApp.setLargeIcon(icon);
    }
    
    public void setLargeIcon(String locale, String icon) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setLargeIcon(locale, icon);
    }
    
    public void setListener(org.netbeans.api.web.dd.Listener[] value) {
        if (webApp!=null) {
        org.netbeans.api.web.dd.Listener[] oldListeners = getListener();
        int lenOld = oldListeners.length;
        int lenNew = (value==null?0:value.length);
        if (lenOld<=lenNew) {
            for (int i=0;i<lenOld;i++) {
                webApp.setListener(i,value[i]);
            }
            for (int i=lenOld;i<lenNew;i++) {
                webApp.addListener(value[i]);
            }
        } else {
            for (int i=0;i<lenNew;i++) {
                webApp.setListener(i,value[i]);
            } 
            for (int i=lenOld-1;i>=lenNew;i--) {
                webApp.removeListener(oldListeners[i]);
            }
        }
        }
    }
    
    public void setListener(int index, org.netbeans.api.web.dd.Listener value) {
        if (webApp!=null) webApp.setListener(index, value);
    }
    
    public void setLocaleEncodingMappingList(org.netbeans.api.web.dd.LocaleEncodingMappingList value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setLocaleEncodingMappingList(value);
    }
    
    public void setLoginConfig(org.netbeans.api.web.dd.LoginConfig value) {
        if (webApp!=null) webApp.setLoginConfig(value);
    }
    
    public void setMessageDestination(org.netbeans.api.web.dd.MessageDestination[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setMessageDestination(value);
    }
    
    public void setMessageDestination(int index, org.netbeans.api.web.dd.MessageDestination value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setMessageDestination(index, value);
    }
    
    public void setMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setMessageDestinationRef(value);
    }
    
    public void setMessageDestinationRef(int index, org.netbeans.api.web.dd.MessageDestinationRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setMessageDestinationRef(index, value);
    }
    
    public void setMimeMapping(org.netbeans.api.web.dd.MimeMapping[] value) {
        if (webApp!=null) webApp.setMimeMapping(value);
    }
    
    public void setMimeMapping(int index, org.netbeans.api.web.dd.MimeMapping value) {
        if (webApp!=null) webApp.setMimeMapping(index, value);
    }
    
    public void setResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef[] value) {
        if (webApp!=null) webApp.setResourceEnvRef(value);
    }
    
    public void setResourceEnvRef(int index, org.netbeans.api.web.dd.ResourceEnvRef value) {
        if (webApp!=null) webApp.setResourceEnvRef(index, value);
    }
    
    public void setResourceRef(org.netbeans.api.web.dd.ResourceRef[] value) {
        if (webApp!=null) webApp.setResourceRef(value);
    }
    
    public void setResourceRef(int index, org.netbeans.api.web.dd.ResourceRef value) {
        if (webApp!=null) webApp.setResourceRef(index, value);
    }
    
    public void setSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint[] value) {
        if (webApp!=null) webApp.setSecurityConstraint(value);
    }
    
    public void setSecurityConstraint(int index, org.netbeans.api.web.dd.SecurityConstraint value) {
        if (webApp!=null) webApp.setSecurityConstraint(index, value);
    }
    
    public void setSecurityRole(org.netbeans.api.web.dd.SecurityRole[] value) {
        if (webApp!=null) webApp.setSecurityRole(value);
    }
    
    public void setSecurityRole(int index, org.netbeans.api.web.dd.SecurityRole value) {
        if (webApp!=null) webApp.setSecurityRole(index, value);
    }
    
    public void setServiceRef(org.netbeans.api.web.dd.ServiceRef[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setServiceRef(value);
    }
    
    public void setServiceRef(int index, org.netbeans.api.web.dd.ServiceRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setServiceRef(index, value);
    }
    
    public void setServlet(org.netbeans.api.web.dd.Servlet[] value) {
        if (webApp!=null) webApp.setServlet(value);
    }
    
    public void setServlet(int index, org.netbeans.api.web.dd.Servlet value) {
        if (webApp!=null) webApp.setServlet(index, value);
    }
    
    public void setServletMapping(org.netbeans.api.web.dd.ServletMapping[] value) {
        if (webApp!=null) webApp.setServletMapping(value);
    }
    
    public void setServletMapping(int index, org.netbeans.api.web.dd.ServletMapping value) {
        if (webApp!=null) webApp.setServletMapping(index, value);
    }
    
    public void setSessionConfig(org.netbeans.api.web.dd.SessionConfig value) {
        if (webApp!=null) webApp.setSessionConfig(value);
    }
    
    public void setSmallIcon(String icon) {
        if (webApp!=null) webApp.setSmallIcon(icon);
    }
    
    public void setSmallIcon(String locale, String icon) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setSmallIcon(locale, icon);
    }
    
    public void setTaglib(org.netbeans.api.web.dd.Taglib[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setTaglib(value);
    }
    
    public void setTaglib(int index, org.netbeans.api.web.dd.Taglib value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        if (webApp!=null) webApp.setTaglib(index, value);
    }
    
    public void setWelcomeFileList(org.netbeans.api.web.dd.WelcomeFileList value) {
        if (webApp!=null) webApp.setWelcomeFileList(value);
    }
    
    public int sizeContextParam() {
        return webApp==null?0:webApp.sizeContextParam();
    }
    
    public int sizeEjbLocalRef() {
        return webApp==null?0:webApp.sizeEjbLocalRef();
    }
    
    public int sizeEjbRef() {
        return webApp==null?0:webApp.sizeEjbRef();
    }
    
    public int sizeEnvEntry() {
        return webApp==null?0:webApp.sizeEnvEntry();
    }
    
    public int sizeErrorPage() {
        return webApp==null?0:webApp.sizeErrorPage();
    }
    
    public int sizeFilter() {
        return webApp==null?0:webApp.sizeFilter();
    }
    
    public int sizeFilterMapping() {
        return webApp==null?0:webApp.sizeFilterMapping();
    }
    
    public int sizeListener() {
        return webApp==null?0:webApp.sizeListener();
    }
    
    public int sizeMessageDestination() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?0:webApp.sizeMessageDestination();
    }
    
    public int sizeMessageDestinationRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?0:webApp.sizeMessageDestinationRef();
    }
    
    public int sizeMimeMapping() {
        return webApp==null?0:webApp.sizeMimeMapping();
    }
    
    public int sizeResourceEnvRef() {
        return webApp==null?0:webApp.sizeResourceEnvRef();
    }
    
    public int sizeResourceRef() {
        return webApp==null?0:webApp.sizeResourceRef();
    }
    
    public int sizeSecurityConstraint() {
        return webApp==null?0:webApp.sizeSecurityConstraint();
    }
    
    public int sizeSecurityRole() {
        return webApp==null?0:webApp.sizeSecurityRole();
    }
    
    public int sizeServiceRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?0:webApp.sizeServiceRef();
    }
    
    public int sizeServlet() {
        return webApp==null?0:webApp.sizeServlet();
    }
    
    public int sizeServletMapping() {
        return webApp==null?0:webApp.sizeServletMapping();
    }
    
    public int sizeTaglib() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp==null?0:webApp.sizeTaglib();
    }
    
    public void write(java.io.OutputStream os) throws java.io.IOException {
        if (webApp!=null) {
            writing=true;
            webApp.write(os);
        }
    }
    
    public void write(org.openide.filesystems.FileObject fo) throws java.io.IOException {
        if (webApp!=null) {
            try {
                org.openide.filesystems.FileLock lock = fo.lock();
                try {
                    java.io.OutputStream os = fo.getOutputStream(lock);
                    try {
                        writing=true;
                        write(os);
                    } finally {
                        os.close();
                    }
                } 
                finally {
                    lock.releaseLock();
                }
            } catch (org.openide.filesystems.FileAlreadyLockedException ex) {
                // trying to use OutputProvider for writing changes
                org.openide.loaders.DataObject dobj = org.openide.loaders.DataObject.find(fo);
                if (dobj!=null && dobj instanceof WebAppProxy.OutputProvider)
                    ((WebAppProxy.OutputProvider)dobj).write(this);
                else throw ex;
            }
        }
    }    
    
    public Object clone() {
        WebAppProxy proxy = null;
        if (webApp==null)
            proxy = new WebAppProxy(null,version);
        else {
            WebApp clonedWebApp=(WebApp)webApp.clone();
            proxy = new WebAppProxy(clonedWebApp,version);
            if (WebApp.VERSION_2_3.equals(version)) {
                ((org.netbeans.modules.schema2beans.BaseBean)clonedWebApp).changeDocType
                    ("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN","http://java.sun.com/dtd/web-app_2_3.dtd");
            } else {
                ((org.netbeans.modules.web.dd.impl.model_2_4.WebApp)clonedWebApp)._setSchemaLocation
                    ("http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd");
            }
        }
        proxy.setError(error);
        proxy.setStatus(ddStatus);
        return proxy;
    }
    
    public boolean isWriting() {
        return writing;
    }
    
    public void setWriting(boolean writing) {
        this.writing=writing;
    }
    
    public void setOutputProvider(OutputProvider iop) {
        this.outputProvider=iop;
    }
    
    /** Contract between friend modules that enables 
    * a specific handling of write(FileObject) method for targeted FileObject
    */
    public static interface OutputProvider {
        public void write(WebApp webApp) throws java.io.IOException;
        public org.openide.filesystems.FileObject getTarget();
    }
    
}
