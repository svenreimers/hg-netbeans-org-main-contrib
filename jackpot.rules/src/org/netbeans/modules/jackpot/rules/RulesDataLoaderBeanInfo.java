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

package org.netbeans.modules.jackpot.rules;

import java.beans.*;
import java.awt.Image;
import org.openide.loaders.MultiFileLoader;
import org.openide.ErrorManager;
import org.openide.util.Utilities;

public final class RulesDataLoaderBeanInfo extends SimpleBeanInfo {

    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (MultiFileLoader.class) };
        } catch (IntrospectionException ie) {
	    ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public Image getIcon(final int type) {
        return Utilities.loadImage("org/netbeans/modules/jackpot/rules/resources/Rule_file_16.png"); // NOI18N
    }
}
