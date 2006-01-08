/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Accelerators module. 
 * The Initial Developer of the Original Code is Andrei Badea. 
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 * 
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Andrei Badea
 */
public class AcceleratorsOptionsBeanInfo extends SimpleBeanInfo {
    
    // XXX not a very nice icon
    private static final String ACCELERATORS_ICON = "org/netbeans/modules/accelerators/resources/accelerators.gif"; // NOI18N
    
    public Image getIcon(int iconKind) {
        if (iconKind == ICON_COLOR_16x16) {
            return Utilities.loadImage(ACCELERATORS_ICON);
        }
        return null;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor[] props = new PropertyDescriptor[2];
            props[0] = new PropertyDescriptor(AcceleratorsOptions.PROP_TERMINAL_COMMAND, AcceleratorsOptions.class); // NOI18N
            props[0].setDisplayName(NbBundle.getMessage(AcceleratorsOptionsBeanInfo.class, "LBL_TerminalCommand"));
            props[0].setShortDescription(NbBundle.getMessage(AcceleratorsOptionsBeanInfo.class, "HINT_TerminalCommand"));
            props[1] = new PropertyDescriptor(AcceleratorsOptions.PROP_FILE_SEARCH_CASE_SENSITIVE, AcceleratorsOptions.class); // NOI18N
            props[1].setHidden(true);
            return props;
        } catch (IntrospectionException e) {
            throw new AssertionError(e);
        }
    }
}
