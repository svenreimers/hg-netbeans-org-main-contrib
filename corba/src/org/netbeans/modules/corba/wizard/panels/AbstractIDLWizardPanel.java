/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.panels;

import org.openide.loaders.TemplateWizard;
import org.netbeans.modules.corba.wizard.IDLWizardData;
/**
 *
 * @author  tzezula
 * @version 
 */
abstract public class AbstractIDLWizardPanel extends AbstractWizardPanel {

    /** Creates new AbstractIDLWizardPanel */
    public AbstractIDLWizardPanel() {
    }
    
    public void readSettings (Object settings) {
        if (settings instanceof TemplateWizard)
            readIDLSettings((TemplateWizard)settings);
    }
  
    public abstract void readIDLSettings (TemplateWizard settings);
  
  
    public void storeSettings (Object settings) {
        if (settings instanceof TemplateWizard)
            storeIDLSettings((TemplateWizard)settings);
    }
  
    public abstract void storeIDLSettings (TemplateWizard settings);

}
