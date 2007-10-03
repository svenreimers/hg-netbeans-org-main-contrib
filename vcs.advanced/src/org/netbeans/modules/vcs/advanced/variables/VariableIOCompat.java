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

package org.netbeans.modules.vcs.advanced.variables;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.openide.util.NbBundle;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.vcscore.VcsConfigVariable;

/**
 * This class provides input of variables from property file for compatibility
 * with old versions.
 *
 * @author  Martin Entlicher
 */
public class VariableIOCompat extends Object {

    public static final String CONFIG_FILE_EXT = "properties";

    /** Creates new VariableIOCompat */
    private VariableIOCompat() {
    }

    /** Read list of VCS variables from properties. Variables are stored as
    * var.<NAME>.value and optionaly var.<NAME>.label, var.<NAME>.basic,
    * var.<NAME>.localFile or var.<NAME>.localDir.
    * If there is only value specified, label is empty string and basic, localFile
    * and localDir are false.
    */
    public static Vector readVariables(Properties props){
        Vector result=new Vector(20);
        String VAR_PREFIX = "var."; // NOI18N
        for(Iterator iter=props.keySet().iterator(); iter.hasNext();){
            String key=(String)iter.next();
            if(key.startsWith(VAR_PREFIX) && key.endsWith(".value")) { // NOI18N
                int startIndex = VAR_PREFIX.length ();
                int endIndex = key.length() - ".value".length (); // NOI18N

                String name = key.substring(startIndex, endIndex);
                String value = (String) props.get(key);

                String label = (String) props.get(VAR_PREFIX + name + ".label"); // NOI18N
                if (label == null) label = ""; // NOI18N

                String strBasic = (String) props.get(VAR_PREFIX + name + ".basic"); // NOI18N
                boolean basic = (strBasic != null) && (strBasic.equalsIgnoreCase ("true")); // NOI18N

                String strLocalFile = (String) props.get(VAR_PREFIX + name + ".localFile"); // NOI18N
                boolean localFile = (strLocalFile != null) && (strLocalFile.equalsIgnoreCase ("true")); // NOI18N

                String strLocalDir = (String) props.get(VAR_PREFIX + name + ".localDir"); // NOI18N
                boolean localDir = (strLocalDir != null) && (strLocalDir.equalsIgnoreCase ("true")); // NOI18N

                String strExec = (String) props.get(VAR_PREFIX + name + ".executable"); // NOI18N
                boolean exec = (strExec != null) && (strExec.equalsIgnoreCase ("true")); // NOI18N

                String customSelector=(String)props.get(VAR_PREFIX + name + ".selector"); // NOI18N
                if (customSelector == null) customSelector = "";

                String orderStr = (String) props.get(VAR_PREFIX + name + ".order"); // NOI18N
                int order = -1;
                if (orderStr != null) {
                    try {
                        order = Integer.parseInt(orderStr);
                    } catch (NumberFormatException e) {
                        // ignoring
                        order = -1;
                    }
                }
                VcsConfigVariable var = new VcsConfigVariable (name, label, value, basic, localFile, localDir, customSelector, order);
                var.setExecutable(exec);
                result.addElement(var);
            }
        }
        result = VcsConfigVariable.sortVariables(result);
        return result;
    }

    /** Open file and load properties from it.
     * @param configRoot the directory which contains properties.
     * @param name the name of properties to read.
     */
    public static Properties readPredefinedProperties(FileObject configRoot, final String name){
        Properties props=new Properties();
        FileObject config = configRoot.getFileObject(name);
        if (config == null) {
            org.openide.ErrorManager.getDefault().notify(new FileNotFoundException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
            return props;
        }
        try{
            InputStream in = config.getInputStream();
            props.load(in);
            in.close();
        }
        catch(FileNotFoundException e) {
            org.openide.ErrorManager.getDefault().notify(new FileNotFoundException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        }
        catch(IOException e){
            org.openide.ErrorManager.getDefault().notify(new IOException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        }
        return props;
    }

    private static String g(String s) {
        return NbBundle.getBundle(VariableIOCompat.class).getString (s);
    }
    private static String g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
}
