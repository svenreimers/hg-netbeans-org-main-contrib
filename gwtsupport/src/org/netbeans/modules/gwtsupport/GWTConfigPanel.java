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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.gwtsupport;

import java.awt.Component;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.gwtsupport.settings.GWTSettings;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author tomslot
 */
public class GWTConfigPanel implements FrameworkConfigurationPanel, WizardDescriptor.FinishablePanel, WizardDescriptor.ValidatingPanel {
    private GWTConfigPanelVisual pnlVisual;
    private final Set <ChangeListener> listeners = new HashSet(1);
    private WizardDescriptor wizardDescriptor;
    
    /** Creates a new instance of GWTConfigPanel */
    public GWTConfigPanel() {
        pnlVisual = new GWTConfigPanelVisual(this);
        GWTSettings gwtSettings = GWTSettings.getDefault();
        File defaultGWTFolder = gwtSettings.getGWTLocation();
        
        if (defaultGWTFolder != null){
            pnlVisual.setGWTFolder(defaultGWTFolder);
        }
    }
    
    public void enableComponents(boolean enabled) {
        pnlVisual.enableComponents(enabled);
    }
    
    public Component getComponent() {
        return pnlVisual;
    }
    
    public HelpCtx getHelp() {
        return null;
    }
    
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
    }
    
    public void storeSettings(Object settings) {
        
    }
    
    public boolean isValid() {
        File gwtFolder = getGwtFolder();
        
        if (!(gwtFolder.exists() && gwtFolder.isDirectory()
                && new File(gwtFolder, "gwt-user.jar").exists())){
            
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(GWTConfigPanel.class, "ERROR_Invalid_GWT_Folder"));
            
            return false;
        }
        
        boolean validModule = true;
        String parts[] = getGWTModule().split("\\.");
        
        if (parts.length < 2 || getGWTModule().endsWith(".")){
            validModule = false;
        } else{
            for (String part : parts){
                if (!isValidJavaIdentifier(part)){
                    validModule = false;
                    break;
                }
            }
        }
        
        if (!validModule){
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    NbBundle.getMessage(GWTConfigPanel.class, "ERROR_Invalid_GWT_Module_Name"));
            
            return false;
        }
        
        wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
        
        return true;
    }
    
    private boolean isValidJavaIdentifier(String txt){
        if (txt.length() == 0 || !Character.isJavaIdentifierStart(txt.charAt(0))) {
            return false;
        }
        
        for (int i=1; i < txt.length(); i++) {
            if (!Character.isJavaIdentifierPart(txt.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    
    public boolean isFinishPanel() {
        return true;
    }
    
    public void validate() throws WizardValidationException {
        
    }
    
    public File getGwtFolder(){
        return pnlVisual.getGWTFolder();
    }
    
    public String getGWTModule(){
        return pnlVisual.getGWTModule();
    }
    
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
}
