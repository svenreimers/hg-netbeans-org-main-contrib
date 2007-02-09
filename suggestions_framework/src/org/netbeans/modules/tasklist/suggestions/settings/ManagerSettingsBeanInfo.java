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

package org.netbeans.modules.tasklist.suggestions.settings;

import java.beans.*;
import java.util.ResourceBundle;
import java.awt.*;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class ManagerSettingsBeanInfo extends SimpleBeanInfo {

    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( ManagerSettings.class , null );
        beanDescriptor.setExpert ( true );//GEN-HEADEREND:BeanDescriptor
        beanDescriptor.setDisplayName(NbBundle.getBundle(ManagerSettings.class).getString("BK0007"));
        beanDescriptor.setShortDescription(NbBundle.getBundle(ManagerSettings.class).getString("BK0008"));


        return beanDescriptor;
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
        int PROPERTY_editScanDelay = 0;
        int PROPERTY_node = 1;
        int PROPERTY_saveScanDelay = 2;
        int PROPERTY_scanOnEdit = 3;
        int PROPERTY_scanOnSave = 4;
        int PROPERTY_scanOnShow = 5;
        int PROPERTY_showScanDelay = 6;
        PropertyDescriptor[] properties = new PropertyDescriptor[7];

        try {
            properties[PROPERTY_editScanDelay] = new PropertyDescriptor ( "editScanDelay", ManagerSettings.class, "getEditScanDelay", "setEditScanDelay" );
            properties[PROPERTY_editScanDelay].setExpert ( true );
            properties[PROPERTY_node] = new PropertyDescriptor ( "node", ManagerSettings.class, "getNode", null );
            properties[PROPERTY_node].setHidden ( true );
            properties[PROPERTY_saveScanDelay] = new PropertyDescriptor ( "saveScanDelay", ManagerSettings.class, "getSaveScanDelay", "setSaveScanDelay" );
            properties[PROPERTY_saveScanDelay].setExpert ( true );
            properties[PROPERTY_scanOnEdit] = new PropertyDescriptor ( "scanOnEdit", ManagerSettings.class, "isScanOnEdit", null );
            properties[PROPERTY_scanOnEdit].setHidden ( true );
            properties[PROPERTY_scanOnSave] = new PropertyDescriptor ( "scanOnSave", ManagerSettings.class, "isScanOnSave", null );
            properties[PROPERTY_scanOnSave].setHidden ( true );
            properties[PROPERTY_scanOnShow] = new PropertyDescriptor ( "scanOnShow", ManagerSettings.class, "isScanOnShow", null );
            properties[PROPERTY_scanOnShow].setHidden ( true );
            properties[PROPERTY_showScanDelay] = new PropertyDescriptor ( "showScanDelay", ManagerSettings.class, "getShowScanDelay", "setShowScanDelay" );
            properties[PROPERTY_showScanDelay].setExpert ( true );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties

        ResourceBundle bundle = NbBundle.getBundle(ManagerSettingsBeanInfo.class);

        properties[PROPERTY_showScanDelay].setName(bundle.getString("BK0001"));
        properties[PROPERTY_showScanDelay].setShortDescription(bundle.getString("BK0002"));
        properties[PROPERTY_editScanDelay].setName(bundle.getString("BK0003"));
        properties[PROPERTY_editScanDelay].setShortDescription(bundle.getString("BK0004"));
        properties[PROPERTY_saveScanDelay].setName(bundle.getString("BK0005"));
        properties[PROPERTY_saveScanDelay].setShortDescription(bundle.getString("BK0006"));

        return properties;         
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[0];
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return new MethodDescriptor[0];
    }
    
    public Image getIcon(int iconKind) {
        return Utilities.loadImage("org/netbeans/modules/tasklist/suggestions/settings/setting.gif");
    }
}

