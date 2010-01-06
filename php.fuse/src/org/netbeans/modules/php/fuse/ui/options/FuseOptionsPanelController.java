/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.fuse.ui.options;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.fuse.FuseFramework;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * @author Martin Fousek
 */
@OptionsPanelController.SubRegistration(
    location=UiUtils.OPTIONS_PATH,
    id=FuseFramework.OPTIONS_SUB_PATH,
    displayName="#LBL_OptionsName",
    position=300
)
public class FuseOptionsPanelController extends OptionsPanelController implements ChangeListener {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private FuseOptionsPanel fuseOptionsPanel = null;
    private volatile boolean changed = false;

    @Override
    public void update() {
        fuseOptionsPanel.setFuse(getOptions().getFuse());

        changed = false;
    }

    @Override
    public void applyChanges() {
        getOptions().setFuse(fuseOptionsPanel.getFuse());

        changed = false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isValid() {
        // support type
        fuseOptionsPanel.updateSupportType();
        // warnings
        String warning = FuseFramework.validate(fuseOptionsPanel.getFuse());
        if (warning != null) {
            fuseOptionsPanel.setUnavailableSupportType();
            fuseOptionsPanel.improveSupportTypeButtonEnabled(false);
            fuseOptionsPanel.setError(warning);
            return true;
        }

        // everything ok
        fuseOptionsPanel.setError(" "); // NOI18N
        if (!new FuseFramework(fuseOptionsPanel.getFuse()).isImproved()) {
            fuseOptionsPanel.improveSupportTypeButtonEnabled(true);
        }
        return true;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        if (fuseOptionsPanel == null) {
            fuseOptionsPanel = new FuseOptionsPanel();
            fuseOptionsPanel.addChangeListener(this);
        }
        return fuseOptionsPanel;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Catch if the status of the panel was changed or not.
     * @param e after which event
     */
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private FuseOptions getOptions() {
        return FuseOptions.getInstance();
    }
}
