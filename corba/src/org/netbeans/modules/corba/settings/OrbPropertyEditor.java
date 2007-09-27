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

package org.netbeans.modules.corba.settings;

import java.beans.*;

import org.openide.util.NbBundle;

/** property editor for viewer property AppletSettings class
 *
 * @author Karel Gardas
 * @version 0.01 March 29, 1999
 */

import org.netbeans.modules.corba.*;

public class OrbPropertyEditor extends PropertyEditorSupport {

    /** array of orbs */
    //private static final String[] orbs = {CORBASupport.ORBIX, CORBASupport.VISIBROKER,
    //					CORBASupport.ORBACUS, CORBASupport.JAVAORB};

    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;
    
    private static boolean initialized = false;
    private static String[] orbs; // NOI18N
    private static String[] ext_orbs; // NOI18N
    
    private boolean extended = false;
    
    public OrbPropertyEditor() {
        if (DEBUG)
            System.out.println("OrbPropertyEditor ()..."); // NOI18N
        if (!initialized) {
            CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
            (CORBASupportSettings.class, true);
            java.util.Vector names = css.getNames();
            int length = names.size();
            if (DEBUG)
                System.out.println("length: " + length); // NOI18N
            orbs = new String[length];
            ext_orbs = new String[length + 1];
            ext_orbs[0] = ORBSettingsBundle.CTL_DEFAULT_ORB;
            for (int i = 0; i<length; i++) {
                orbs[i] = (String)names.elementAt(i);
                ext_orbs[i+1] = (String)names.elementAt(i);
                if (DEBUG)
                    System.out.println("name: " + orbs[i]); // NOI18N
            }
            initialized = true;
        }
    }
    
    public OrbPropertyEditor(boolean _extended) {
        this();
        extended = _extended;
    }
    
    /** @return names of the supported orbs*/
    public String[] getTags() {
        return (extended) ? ext_orbs : orbs;
    }
    
    /** @return text for the current value */
    public String getAsText() {
        //System.out.println ("OrbPropertyEditor::getAsText () -> " + this.getValue());
        String name = (String) this.getValue();
        return name != null ? name : ORBSettingsBundle.CTL_DEFAULT_ORB;
    }
    
    /** @param text A text for the current value. */
    public void setAsText(String __value) {
        String __tmp = null;
        if (!extended || !ORBSettingsBundle.CTL_DEFAULT_ORB.equals(__value))
            __tmp = __value;
        //System.out.println ("OrbPropertyEditor::setAsText () <- " + __value);
        //if (__value.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED)) {
        //   __tmp = __value.substring
        //	(0, __value.length () - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
        //}
        //System.out.println ("OrbPropertyEditor::setAsText (): setValue () <- " + __tmp);
        this.setValue(__tmp);
    }
}



