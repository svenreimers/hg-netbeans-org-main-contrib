/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.encoder.custom.aip;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 * The delimiter set property
 *
 * @author Jun Xu
 */
public class DelimiterSetProperty extends PropertySupport.Reflection {
    
    /** Creates a new instance of DelimiterSetProperty */
    public DelimiterSetProperty(EncodingOption encodingOption, Class clazz,
            String name) throws NoSuchMethodException {
        super(encodingOption, clazz, name);
    }

    public PropertyEditor getPropertyEditor() {
        return new DelimiterSetPropertyEditor((EncodingOption) instance);
    }
    
    public boolean isDefaultValue () {
        try {
            return getValue()==null;
        } catch (IllegalArgumentException ex) {
        } catch (InvocationTargetException ex) {
        } catch (IllegalAccessException ex) {
        }
        return false;
    }

    public boolean supportsDefaultValue () {
        return true;
    }
}
