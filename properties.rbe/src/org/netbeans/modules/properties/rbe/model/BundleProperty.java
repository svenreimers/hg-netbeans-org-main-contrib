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
 * Contributor(s): Denis Stepanov
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
package org.netbeans.modules.properties.rbe.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The Bundle property
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class BundleProperty implements Comparable<BundleProperty> {

    /** The property bundle */
    private Bundle bundle;
    /** The name of the property */
    private String name;
    /** The key of the property */
    private String key;
    /** Locale properties */
    private Map<Locale, LocaleProperty> localeProperties;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /** Change support constants */
    public final static String LOCALE_PROPERTY_PROP = "LocaleProperty";

    public BundleProperty(Bundle bundle, String name, String fullname) {
        this.name = name;
        this.key = fullname;
        this.bundle = bundle;
        this.localeProperties = new HashMap<Locale, LocaleProperty>(bundle.getLocales().size());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public LocaleProperty getLocalProperty(Locale locale) {
        return localeProperties.get(locale);
    }

    public Collection<LocaleProperty> getLocaleProperties() {
        return localeProperties.values();
    }

    public boolean isExists() {
        return getBundle().isPropertyExists(key);
    }

    public void delete() {
        bundle.deleteProperty(key);
    }

    public int compareTo(BundleProperty o) {
        return this.key.compareTo(o.key);
    }

    public void addLocaleProperty(final Locale locale, final LocaleProperty value) {
        value.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                propertyChangeSupport.firePropertyChange(LOCALE_PROPERTY_PROP, null, value);
            }
        });

        localeProperties.put(locale, value);
    }

    public void removeLocaleProperty(Locale locale) {
        localeProperties.remove(locale);
    }

    public boolean isContainsEmptyLocaleProperty() {
        for (LocaleProperty localeProperty : localeProperties.values()) {
            if (!localeProperty.isCreated() || (localeProperty.getValue() == null) || localeProperty.getValue().trim().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }
}
