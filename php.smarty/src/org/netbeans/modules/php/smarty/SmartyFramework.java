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

package org.netbeans.modules.php.smarty;

import org.netbeans.modules.php.api.phpmodule.PhpProgram;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.smarty.ui.options.SmartyOptions;


/**
 * @author Martin Fousek
 */
public class SmartyFramework extends PhpProgram {

    public static final String OPTIONS_SUB_PATH = "Smarty"; // NOI18N
    public static final String BASE_CLASS_NAME = "Smarty"; // NOI18N

    /**
     * Open delimiter in SMARTY templates
     */
    public static String DELIMITER_DEFAULT_OPEN = SmartyOptions.getInstance().getDefaultOpenDelimiter();
    /**
     * Close delimiter in SMARTY templates
     */
    public static String DELIMITER_DEFAULT_CLOSE = SmartyOptions.getInstance().getDefaultCloseDelimiter();

    public static int DEPTH_OF_SCANNING_FOR_TPL = SmartyOptions.getInstance().getScanningDepth();

    public SmartyFramework() {
        super(null);
    }

    @Override
    public String validate() {
        return null;
    }

    /**
     * @return full IDE options Smarty path
     */
    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + getOptionsSubPath(); // NOI18N
    }

    /**
     * @return IDE options Smarty subpath
     */
    public static String getOptionsSubPath() {
        return OPTIONS_SUB_PATH;
    }



}
