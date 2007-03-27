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

package org.netbeans.modules.ant.freeform.samples;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class FreeformWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {

    private WizardDescriptor wizardDescriptor;
    private FreeformPanelVisual component;
    
    /** Creates a new instance of templateWizardPanel */
    public FreeformWizardPanel() {
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new FreeformPanelVisual(this);
            component.setName(NbBundle.getMessage(FreeformWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        String help = (String) wizardDescriptor.getProperty("help");
        if (help != null) {
            return new HelpCtx(help);
        } else {
            return null;
        }
    }
    
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    
    private final ChangeSupport cs = new ChangeSupport(this);
    public final void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    public final void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }
    protected final void fireChangeEvent() {
        cs.fireChange();
    }
    
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);
    }
    
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
    }
    
    public boolean isFinishPanel() {
        return true;
    }
    
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
    
}
