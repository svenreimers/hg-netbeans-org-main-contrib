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

/*
 * WS70MailResourceBeanBeanInfo.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;

import java.beans.*;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.sun.ide.editors.BooleanEditor;

/**
 * @author Administrator
 */
public class WS70MailResourceBeanBeanInfo extends SimpleBeanInfo {

    // Bean descriptor information will be obtained from introspection.//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = null;
    private static BeanDescriptor getBdescriptor(){
//GEN-HEADEREND:BeanDescriptor
    BeanDescriptor beanDescriptor = new BeanDescriptor  ( WS70MailResourceBean.class , null );
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    static private String getLabel(String key){
        return NbBundle.getMessage(WS70MailResourceBean.class,key);
    }
    private static final int PROPERTY_description = 0;
    private static final int PROPERTY_fromAddr = 1;
    private static final int PROPERTY_hostName = 2;    
    private static final int PROPERTY_isEnabled = 3;
    private static final int PROPERTY_jndiName = 4;
    private static final int PROPERTY_name = 5;
    private static final int PROPERTY_storeProt = 6;
    private static final int PROPERTY_storeProtClass = 7;
    private static final int PROPERTY_transProt = 8;
    private static final int PROPERTY_transProtClass = 9;
    private static final int PROPERTY_userName = 10;
    
    // Properties information will be obtained from introspection.//GEN-FIRST:Properties
    private static PropertyDescriptor[] properties = null;
    private static PropertyDescriptor[] getPdescriptor(){
//GEN-HEADEREND:Properties
         try {
            properties = new PropertyDescriptor[11];
            properties[PROPERTY_description] = new PropertyDescriptor ( "description", WS70MailResourceBean.class, "getDescription", "setDescription" );
            properties[PROPERTY_description].setDisplayName ( getLabel("LBL_Description") );
            properties[PROPERTY_description].setShortDescription ( getLabel("DSC_Description") );
            properties[PROPERTY_fromAddr] = new PropertyDescriptor ( "fromAddr", WS70MailResourceBean.class, "getFromAddr", "setFromAddr" );
            properties[PROPERTY_fromAddr].setDisplayName ( getLabel("LBL_from") );
            properties[PROPERTY_fromAddr].setShortDescription ( getLabel("DSC_from") );
            properties[PROPERTY_hostName] = new PropertyDescriptor ( "hostName", WS70MailResourceBean.class, "getHostName", "setHostName" );
            properties[PROPERTY_hostName].setDisplayName ( getLabel("LBL_host") );
            properties[PROPERTY_hostName].setShortDescription ( getLabel("DSC_host") );
            properties[PROPERTY_isEnabled] = new PropertyDescriptor ( "isEnabled", WS70MailResourceBean.class, "getIsEnabled", "setIsEnabled" );
            properties[PROPERTY_isEnabled].setDisplayName ( getLabel("LBL_Enabled") );
            properties[PROPERTY_isEnabled].setShortDescription ( getLabel("DSC_Enabled") );
            properties[PROPERTY_isEnabled].setPropertyEditorClass ( BooleanEditor.class );
            properties[PROPERTY_jndiName] = new PropertyDescriptor ( "jndiName", WS70MailResourceBean.class, "getJndiName", "setJndiName" );
            properties[PROPERTY_jndiName].setDisplayName ( getLabel("LBL_MailJndiName") );
            properties[PROPERTY_jndiName].setShortDescription ( getLabel("DSC_MailJndiName") );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", WS70MailResourceBean.class, "getName", "setName" );
            properties[PROPERTY_name].setHidden ( true );
            properties[PROPERTY_storeProt] = new PropertyDescriptor ( "storeProt", WS70MailResourceBean.class, "getStoreProt", "setStoreProt" );
            properties[PROPERTY_storeProt].setDisplayName ( getLabel("LBL_StoreProtocol") );
            properties[PROPERTY_storeProt].setShortDescription ( getLabel("DSC_StoreProtocol") );
            properties[PROPERTY_storeProtClass] = new PropertyDescriptor ( "storeProtClass", WS70MailResourceBean.class, "getStoreProtClass", "setStoreProtClass" );
            properties[PROPERTY_storeProtClass].setDisplayName ( getLabel("LBL_StoreProtocolClass") );
            properties[PROPERTY_storeProtClass].setShortDescription ( getLabel("DSC_StoreProtocolClass") );
            properties[PROPERTY_transProt] = new PropertyDescriptor ( "transProt", WS70MailResourceBean.class, "getTransProt", "setTransProt" );
            properties[PROPERTY_transProt].setDisplayName ( getLabel("LBL_TransportProtocol") );
            properties[PROPERTY_transProt].setShortDescription ( getLabel("DSC_TransportProtocol") );
            properties[PROPERTY_transProtClass] = new PropertyDescriptor ( "transProtClass", WS70MailResourceBean.class, "getTransProtClass", "setTransProtClass" );
            properties[PROPERTY_transProtClass].setDisplayName ( getLabel("LBL_TransportProtocolClass") );
            properties[PROPERTY_transProtClass].setShortDescription ( getLabel("DSC_TransportProtocolClass") );
            properties[PROPERTY_userName] = new PropertyDescriptor ( "userName", WS70MailResourceBean.class, "getUserName", "setUserName" );
            properties[PROPERTY_userName].setDisplayName ( getLabel("LBL_user") );
            properties[PROPERTY_userName].setShortDescription ( getLabel("DSC_user") );
        }
        catch( IntrospectionException e){
        }       
        // Here you can add code for customizing the properties array.
        
        return properties;     }//GEN-LAST:Properties
    
    // Event set information will be obtained from introspection.//GEN-FIRST:Events
    private static EventSetDescriptor[] eventSets = null;
    private static EventSetDescriptor[] getEdescriptor(){
//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;     }//GEN-LAST:Events
    
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){
//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     }//GEN-LAST:Methods
    
    
    private static int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static int defaultEventIndex = -1;//GEN-END:Idx
    
    
//GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
//GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}

