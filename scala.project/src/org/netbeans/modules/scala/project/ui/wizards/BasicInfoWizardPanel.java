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

package org.netbeans.modules.scala.project.ui.wizards;

import java.awt.Component;
import java.io.File;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 * First panel of {@link NewScalaProjectWizardIterator}. Allows user to enter
 * basic informations about Scala project.
 *
 * <ul>
 *  <li>Project name</li>
 *  <li>Project Location</li>
 *  <li>Project Folder</li>
 *  <li>If should be set as a Main Project</li>
 * </ul>
 *
 * @author Martin Krauskopf
 */
final class BasicInfoWizardPanel extends BasicWizardPanel implements WizardDescriptor.ValidatingPanel {
    
    /** Representing visual component for this step. */
    private BasicInfoVisualPanel visualPanel;
    
    /** Creates a new instance of BasicInfoWizardPanel */
    BasicInfoWizardPanel(final NewScalaProjectWizardData data) {
        super(data);
    }
    
//    public void reloadData() {
//        visualPanel.refreshData();
//    }
//    
//    public void storeData() {
//        visualPanel.storeData();
//    }
    
    public Component getComponent() {
        if (visualPanel == null) {
            visualPanel = new BasicInfoVisualPanel(getData());
            visualPanel.addPropertyChangeListener(this);
            visualPanel.setName(getMessage("BasicInfoWizardPanel.title"));
//            visualPanel.updateAndCheck();
        }
        return visualPanel;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(BasicInfoWizardPanel.class);
    }
    
    public void validate() throws WizardValidationException {
        // XXX this is little strange. Since this method is called first time the panel appears.
        // So we have to do this null check (data are uninitialized)
        String prjFolder = getData().getProjectFolder();
        if (prjFolder != null) {
            File prjFolderF = new File(prjFolder);
            if (prjFolderF.mkdir()) {
                prjFolderF.delete();
            } else {
                String message = getMessage("MSG_UnableToCreateProjectFolder");
                throw new WizardValidationException(visualPanel.nameLabel, message, message);
            }
        }
    }
    
}
