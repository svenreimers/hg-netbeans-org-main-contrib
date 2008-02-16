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
/*
 * CommonPropertyPanel.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.awt.Component;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroup;
import org.netbeans.modules.j2ee.sun.sunresources.beans.Wizard;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroupHelper;

/**
 * Code reused from Appserver common API module 
 * 
 */
public class CommonPropertyPanel implements WizardDescriptor.Panel, WS70WizardConstants{
    
    private CommonPropertyVisualPanel component;
    private ResourceConfigHelper helper;
    private Wizard wiz;
    boolean firstTime = false;
    /** Creates a new instance of CommonPropertyPanel */
    public CommonPropertyPanel(ResourceConfigHelper helper, Wizard wiz) {
        this.helper = helper;
        this.wiz = wiz;
    }
    
     // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new CommonPropertyVisualPanel(this);
        }
        return component;
    }
    
    public void setInitialFocus(){
        if(component != null) {
            component.refreshFields();
            component.setInitialFocus();
        }
    }
    
    public FieldGroup getFieldGroup(String groupName) {
        return FieldGroupHelper.getFieldGroup(wiz, groupName);
    }
    
    public ResourceConfigHelper getHelper() {
        return helper;
    }
    
    public HelpCtx getHelp() {
        if (wiz.getName().equals(__JdbcResource)) {
            return new HelpCtx("AS_Wiz_DataSource_props"); //NOI18N
        //}else if (wiz.getName().equals(__PersistenceManagerFactoryResource)) {
           // return new HelpCtx("AS_Wiz_PMF_props"); //NOI18N
        }else {
            return HelpCtx.DEFAULT_HELP;
        }
    }
    
    public boolean isValid() {
        ResourceConfigData data = helper.getData();
        Vector vec = data.getProperties();
        for (int i = 0; i < vec.size(); i++) {
            NameValuePair pair = (NameValuePair)vec.elementAt(i);
            if (pair.getParamName() == null || pair.getParamValue() == null ||
            pair.getParamName().length() == 0 || pair.getParamValue().length() == 0)
                return false;
        }
        return true;
    }
    
   private final Set listeners = new HashSet(1); 
    
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
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        if(firstTime){
            ChangeEvent ev = new ChangeEvent(this);
            while (it.hasNext()) {
                ((ChangeListener) it.next()).stateChanged(ev);
            }
        }else{
            firstTime = true;
        }    
    }
    
     // You can use a settings object to keep track of state.
    // Normally the settings object will be the WizardDescriptor,
    // so you can use WizardDescriptor.getProperty & putProperty
    // to store information entered by the user.
    public void readSettings(Object settings) {
    }
    public void storeSettings(Object settings) {
    }
    
}
