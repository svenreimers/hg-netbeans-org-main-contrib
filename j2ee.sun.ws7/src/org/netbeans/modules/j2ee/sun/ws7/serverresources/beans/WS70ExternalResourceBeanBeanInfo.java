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
 * WS70ExternalResourceBeanBeanInfo.java 
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;

import java.beans.*;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.sun.ide.editors.BooleanEditor;

/**
 * @author Administrator
 */
public class WS70ExternalResourceBeanBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor information will be obtained from introspection.//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = null;
    private static BeanDescriptor getBdescriptor(){
//GEN-HEADEREND:BeanDescriptor
    BeanDescriptor beanDescriptor = new BeanDescriptor(WS70ExternalResourceBean.class, null );     
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor

    static private String getLabel(String key){
        return NbBundle.getMessage(WS70ExternalResourceBean.class,key);
    }

    private static final int PROPERTY_jndiName = 0;
    private static final int PROPERTY_externalJndiName = 1;
    private static final int PROPERTY_resType = 2;
    private static final int PROPERTY_factoryClass = 3;
    private static final int PROPERTY_isEnabled = 4;
    private static final int PROPERTY_description = 5;
    private static final int PROPERTY_name = 6;
    
    
    // Properties information will be obtained from introspection.//GEN-FIRST:Properties
    private static PropertyDescriptor[] properties = null;
    private static PropertyDescriptor[] getPdescriptor(){
//GEN-HEADEREND:Properties
        try {
            properties = new PropertyDescriptor[7];
            properties[PROPERTY_jndiName] = new PropertyDescriptor ( "jndiName", WS70ExternalResourceBean.class, "getJndiName", "setJndiName" );
            properties[PROPERTY_jndiName].setDisplayName ( getLabel("LBL_ExternalJndi_JndiName") );
            properties[PROPERTY_jndiName].setShortDescription ( getLabel("DSC_ExternalJndi_JndiName") );            

            properties[PROPERTY_externalJndiName] = new PropertyDescriptor ( "externalJndiName", WS70ExternalResourceBean.class, "getExternalJndiName", "setExternalJndiName" );
            properties[PROPERTY_externalJndiName].setDisplayName ( getLabel("LBL_ExternalJndi") );
            properties[PROPERTY_externalJndiName].setShortDescription ( getLabel("DSC_ExternalJndi") );
            properties[PROPERTY_resType] = new PropertyDescriptor ( "resType", WS70ExternalResourceBean.class, "getResType", "setResType" );
            properties[PROPERTY_resType].setDisplayName ( getLabel("LBL_ResType") );
            properties[PROPERTY_resType].setShortDescription ( getLabel("DSC_ResType") );
            properties[PROPERTY_factoryClass] = new PropertyDescriptor ( "factoryClass", WS70ExternalResourceBean.class, "getFactoryClass", "setFactoryClass" );
            properties[PROPERTY_factoryClass].setDisplayName ( getLabel("LBL_ExternalJndiFactoryClass") );
            properties[PROPERTY_factoryClass].setShortDescription ( getLabel("DSC_ExternalJndiFactoryClass") );            
            properties[PROPERTY_description] = new PropertyDescriptor ( "description", WS70ExternalResourceBean.class, "getDescription", "setDescription" );
            properties[PROPERTY_description].setDisplayName ( getLabel("LBL_Description") );
            properties[PROPERTY_description].setShortDescription ( getLabel("DSC_Description") );            
       
            properties[PROPERTY_isEnabled] = new PropertyDescriptor ( "isEnabled", WS70ExternalResourceBean.class, "getIsEnabled", "setIsEnabled" );
            properties[PROPERTY_isEnabled].setDisplayName ( getLabel("LBL_Enabled") );
            properties[PROPERTY_isEnabled].setShortDescription ( getLabel("DSC_Enabled") );
            properties[PROPERTY_isEnabled].setPropertyEditorClass ( BooleanEditor.class );
      
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", WS70ExternalResourceBean.class, "getName", "setName" );
            properties[PROPERTY_name].setHidden ( true );
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

