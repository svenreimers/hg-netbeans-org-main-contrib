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

package org.netbeans.modules.tasklist.usertasks.util;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * A simple panel for a wizard.
 */
public class SimpleWizardPanel implements WizardDescriptor.FinishablePanel {
    private EventListenerList listeners = new EventListenerList();

    /** created component or null */
    protected Component component;
    
    protected HelpCtx helpCtx = HelpCtx.DEFAULT_HELP;
    
    /** is this panel valid? */
    private boolean valid = true;
    
    /** wizard */
    protected WizardDescriptor wizard;
    
    private boolean finish;
    
    /**
     * Creates a new instance of SimpleWizardPanel
     *
     * @param component panel for the wizard
     */
    public SimpleWizardPanel(Component component) {
        this.component = component;
    }
    
    /**
     * Sets whether this is a finish panel
     *
     * @param finish true = this is a finishable panel
     */
    public void setFinishPanel(boolean finish) {
        this.finish = finish;
    }
    
    public void readSettings(Object settings) {
        wizard = (WizardDescriptor) settings;
        check();
    }

    public void storeSettings(Object settings) {
    }

    /**
     * Sets another error message that will be shown in the bottom of the
     * wizard dialog.
     *
     * @param err new error message or null if none
     */
    public void setErrorMessage(String err) {
        if (wizard != null)
            wizard.putProperty("WizardPanel_errorMessage", err); // NOI18N
        this.valid = err == null;
    }
    
    /**
     * Sets help context for this panel
     *
     * @param h new help context
     */
    public void setHelpContext(HelpCtx h) {
        this.helpCtx = h;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    /**
     * This method should check the content of the panel and
     */
    protected void check() {
    }
    
    public java.awt.Component getComponent() {
        return component;
    }

    public HelpCtx getHelp() {
        return helpCtx;
    }

    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    private void fireChange() {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        ChangeEvent event = null;

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == ChangeListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new ChangeEvent(this);
                ((ChangeListener) l[i+1]).stateChanged(event);
            }
        }
    }
    
    public boolean isFinishPanel() {
        return finish;
    }    
    
    /**
     * Sets the Index of highlighted step in the content.
     *
     * @param index new index
     */
    public void setContentHighlightedIndex(int index) {
        if (wizard != null)
            wizard.putProperty("WizardPanel_contentSelectedIndex", // NOI18N
                new Integer(index));
        else
            ((JComponent) getComponent()).putClientProperty(
                "WizardPanel_contentSelectedIndex", 
                new Integer(index)); // NOI18N
    }
}
