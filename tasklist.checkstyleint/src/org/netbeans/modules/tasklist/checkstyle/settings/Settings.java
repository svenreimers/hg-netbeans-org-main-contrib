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


package org.netbeans.modules.tasklist.checkstyle.settings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author S. Aubrecht
 */
final public class Settings {
    
    public static final String PROP_CONFIG_URL = "configUrl"; //NOI18N

    private static Settings theInstance;

    private PropertyChangeSupport propertySupport;
    
    /** Creates a new instance of Settings */
    private Settings() {
    }
    
    public static final Settings getDefault() {
        if( null == theInstance )
            theInstance = new Settings();
        return theInstance;
    }
    
    public boolean isExtensionSupported( String fileExtension ) {
        return "JAVA".compareToIgnoreCase( fileExtension ) == 0;
    }
    
    public void addPropertyChangeListener( PropertyChangeListener l ) {
        if( null == propertySupport )
            propertySupport = new PropertyChangeSupport( this );
        propertySupport.addPropertyChangeListener( l );
    }
    
    public void removePropertyChangeListener( PropertyChangeListener l ) {
        if( null != propertySupport )
            propertySupport.removePropertyChangeListener( l );
    }
    
    private Preferences getPreferences() {
        return NbPreferences.forModule( Settings.class );
    }
    
    public String getConfigurationUrl() {
        return getPreferences().get("configUrl", getDefaultConfigurationUrl() ); //NOI18N
    }
    
    private String getDefaultConfigurationUrl() {
        ClassLoader cl = Lookup.getDefault().lookup( ClassLoader.class );
        URL url = cl.getResource("org/netbeans/modules/tasklist/checkstyle/sun_checks.xml"); //NOI18N
        return url.toExternalForm();
    }
    
    public void setConfigurationUrl( String newUrl ) {
        String oldVal = getConfigurationUrl();
        getPreferences().put( "configUrl", newUrl ); //NOI18N
        if( null != propertySupport )
            propertySupport.firePropertyChange( PROP_CONFIG_URL, oldVal, newUrl );
    }
}
