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
package org.netbeans.modules.autoclose;

import org.openide.modules.ModuleInstall;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public final class AddOnModule extends ModuleInstall {
    
    /** Creates a new instance of AddOnModule */
    public AddOnModule() {
    }

    public void restored() {
        super.restored();
        TopComponent.getRegistry().addPropertyChangeListener(new AutoCloseImpl());
    }
    
}
