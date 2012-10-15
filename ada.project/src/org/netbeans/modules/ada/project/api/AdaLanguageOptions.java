/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.project.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.netbeans.modules.ada.project.AdaLanguageOptionsAccessor;
import org.netbeans.modules.ada.project.AdaProject;
import org.netbeans.modules.ada.project.options.AdaOptions;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Helper class to get Ada language properties like Ada dialects etc.
 * @author Andrea Lucarelli
 */
public final class AdaLanguageOptions {
    /**
     * Property which is fired when {@link Properties#getAdaVersion()} changes.
     */
    public static final String PROP_ADA_VERSION = AdaLanguageOptions.class.getName() + ".adaVersion";

    /**
     * Ada version, currently used only for hints.
     * @see Properties#getAdaVersion()
     * @see #getProperties(FileObject)
     */
    public static enum AdaVersion {
        ADA_83(NbBundle.getMessage(AdaLanguageOptions.class, "ADA_83")),
        ADA_95(NbBundle.getMessage(AdaLanguageOptions.class, "ADA_95")),
        ADA_2005(NbBundle.getMessage(AdaLanguageOptions.class, "ADA_2005")),
        ADA_2012(NbBundle.getMessage(AdaLanguageOptions.class, "ADA_2012"));

        private final String displayName;

        AdaVersion(String displayName) {
            assert displayName != null;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    };

    static final AdaLanguageOptions INSTANCE = new AdaLanguageOptions();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    static {
        AdaLanguageOptionsAccessor.setDefault(new AdaLanguageOptionsAccessor() {

            @Override
            public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
                INSTANCE.firePropertyChange(propertyName, oldValue, newValue);
            }
        });
    }

    private AdaLanguageOptions() {
    }

    /**
     * Get the default instance of {@link AdaLanguageOptions} class.
     * @return the default instance of {@link AdaLanguageOptions} class.
     */
    public static AdaLanguageOptions getDefault() {
        return INSTANCE;
    }

    /**
     * Get {@link Properties Ada language properties} for the given file (can be <code>null</code>).
     * These properties are project specific. If no project is found for the file, then properties with the default values are returned.
     * @param file a file which could belong to a project (if not or <code>null</code>, properties with the default values are returned).
     * @return {@link Properties properties}.
     * @see AdaVersion
     */
    public Properties getProperties(FileObject file) {
        AdaVersion adaVersion = AdaVersion.ADA_95;

        if (file != null) {
            AdaProject adaProject = org.netbeans.modules.ada.project.ui.Utils.getAdaProject(file);
            if (adaProject != null) {
                adaVersion = getAdaVersion(adaProject);
            }
        }
        return new Properties(adaVersion);
    }

    public static AdaVersion getAdaVersion(AdaProject project) {
        String value = project.getEvaluator().getProperty(AdaOptions.ADA_DIALECTS);
        AdaVersion adaVersion = null;
        if (value != null) {
            try {
                adaVersion = AdaVersion.valueOf(value);
            } catch (IllegalArgumentException iae) {
                // ignored
            }
        }
        return adaVersion != null ? adaVersion : AdaVersion.ADA_95;

    }

    /**
     * Add listener that is notified when any "important" Ada language property changes.
     * @param listener a listener to add
     * @see #removePropertyChangeListener(PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove listener.
     * @param listener a listener to remove
     * @see #addPropertyChangeListener(PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Data object for Ada language properties.
     */
    public static final class Properties {
        private final AdaVersion adaVersion;

        Properties(AdaVersion adaVersion) {
            this.adaVersion = adaVersion;
        }


        /**
         * Get the {@link AdaVersion Ada version} of the project.
         * If not specified, {@link AdaVersion#ADA_95 Ada 95} is returned.
         * @return the {@link AdaVersion Ada version} of the project
         */
        public AdaVersion getAdaVersion() {
            return adaVersion;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(100);
            sb.append(getClass().getName());
            sb.append(" [Ada Version: ");
            sb.append(adaVersion);
            sb.append("]");
            return sb.toString();
        }
    }
}
