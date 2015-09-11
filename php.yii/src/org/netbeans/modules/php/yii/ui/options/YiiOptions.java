/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.yii.ui.options;

import java.io.File;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.yii.YiiScript;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbPreferences;

/**
 *
 * @author Gevik Babakhani <gevik@netbeans.org>
 */
public final class YiiOptions {
    // Do not change arbitrary - consult with layer's folder OptionsExport
    // Path to Preferences node for storing these preferences

    private static final String PREFERENCES_PATH = "yii"; // NOI18N
    private static final YiiOptions INSTANCE = new YiiOptions();
    // zend script
    private static final String Yii = "Yii"; // NOI18N
    final ChangeSupport changeSupport = new ChangeSupport(this);
    private volatile boolean yiiSearched = false;
    private volatile boolean yiiExtended = false;

    private YiiOptions() {
        getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                changeSupport.fireChange();
            }
        });
    }

    public static YiiOptions getInstance() {
        return INSTANCE;
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public boolean getYiiExtended() {
        return yiiExtended;
    }

    public void setYiiExtended(boolean value) {
        yiiExtended = value;
    }

    public synchronized String getFrameworkPath() {
        String yiiPath = FileUtil.normalizePath(getYii());
        File f = new File(yiiPath);
        return f.getParent();
    }

    public synchronized String getYii() {
        String yii = getPreferences().get(Yii, null);
        if (yii == null && !yiiSearched) {
            yiiSearched = true;
            List<String> scripts = FileUtils.findFileOnUsersPath(YiiScript.SCRIPT_NAME, YiiScript.SCRIPT_NAME_LONG);
            if (!scripts.isEmpty()) {
                yii = scripts.get(0);
                setYii(yii);
            }
        }
        return yii;
    }

    public void setYii(String yii) {
        getPreferences().put(Yii, yii);
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(YiiOptions.class).node(PREFERENCES_PATH);
    }
}
