/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.docbook.project.wizard;

import java.awt.Component;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Tim Boudreau
 */
public class ProjectInfoPanelPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel, ChangeListener {

    public ProjectInfoPanelPanel() {
    }

    private ProjectInfoPanel panel;
    public Component getComponent() {
        boolean wasNull = panel == null;
        Component result = wasNull ? (panel = new ProjectInfoPanel()) : panel;
        if (wasNull) {
            panel.cl = this;
        }
        return result;
    }

    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void readSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        if (panel != null) {
            panel.load (d);
        }
    }

    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        if (panel != null) {
            panel.save (d);
        }
    }

    public boolean isValid() {
        return panel != null && !panel.hasProblem();
    }

    private List listeners = Collections.synchronizedList (new LinkedList());
    public void addChangeListener(ChangeListener l) {
        listeners.add (l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove (l);
    }

    void fire() {
        ChangeListener[] l = (ChangeListener[])
                listeners.toArray (new ChangeListener[0]);
        for (int i=0; i < l.length; i++) {
            l[i].stateChanged(new ChangeEvent (this));
        }
    }

    public void validate() throws WizardValidationException {
        if (panel != null && panel.hasProblem()) {
            throw new WizardValidationException (panel,
                    panel.getProblem(), panel.getProblem());
        }
    }

    public boolean isFinishPanel() {
        return true;
    }

    public void stateChanged(ChangeEvent e) {
        fire();
    }
}
