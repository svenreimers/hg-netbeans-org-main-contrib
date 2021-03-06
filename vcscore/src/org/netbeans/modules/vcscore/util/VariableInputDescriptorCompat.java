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

package org.netbeans.modules.vcscore.util;

import java.util.*;

import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.VcsCommand;

/**
 * This class assures compatibility of variable input expressions used in 3.1 release
 * with the variable input descriptor used in 3.2 release.
 * @author  Martin Entlicher
 */
public final class VariableInputDescriptorCompat extends Object {

    /**
     * The name of the variable for which we get user input.
     */
    private static final String PROMPT_FOR = "PROMPT_FOR"; // NOI18N
    /**
     * The name of the variable for which we get user to set true or false.
     */
    private static final String ASK_FOR = "ASK_FOR"; // NOI18N
    /**
     * The name of the variable for which we get user file input.
     */
    private static final String PROMPT_FOR_FILE_CONTENT = "PROMPT_FOR_FILE_CONTENT"; // NOI18N

    public static final String PROMPT_DEFAULT_VALUE_SEPARATOR = "\"";

    private static int variableGeneratorSeed = 0;
    
    /** Creates new VariableInputDescriptorCompat */
    private VariableInputDescriptorCompat() {
    }
    
    /**
     * For commands which do not define {@link VcsCommand.PROPERTY_INPUT_DESCRIPTOR},
     * create the input descriptor from its execution string and put the appropriate
     * variables to the execution string instead of old keywords.
     */
    public static void createInputDescriptorFormExec(Map commandsByName) {
        Iterator cmdIt = commandsByName.values().iterator();
        while (cmdIt.hasNext()) {
            UserCommandSupport cmdSupport = (UserCommandSupport) cmdIt.next();
            VcsCommand cmd = cmdSupport.getVcsCommand();
            if (cmd != null && cmd.getProperty(VcsCommand.PROPERTY_INPUT_DESCRIPTOR) == null) {
                createInputDescriptorFormExec(cmd);
            }
        }
    }
    
    private static void createInputDescriptorFormExec(VcsCommand cmd) {
        String execStr = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (execStr == null) return ;
        StringBuffer exec = new StringBuffer(execStr);
        StringBuffer inputDescriptor = new StringBuffer();
        String name = cmd.getDisplayName();
        if (name == null) name = cmd.getName();
        inputDescriptor.append(VariableInputDescriptor.INPUT_STR_LABEL + "(" + name + ") ");
        int num = createFields(exec, inputDescriptor);
        num += createAreas(exec, inputDescriptor);
        num += createAsks(exec, inputDescriptor);
        if (num > 0) {
            cmd.setProperty(VcsCommand.PROPERTY_INPUT_DESCRIPTOR, inputDescriptor.toString());
            cmd.setProperty(VcsCommand.PROPERTY_EXEC, exec.toString());
        }
    }
    
    private static int createFields(StringBuffer exec, StringBuffer inputDescriptor) {
        String search = "${"+PROMPT_FOR; //+"(";
        int pos = 0;
        int index;
        int num = 0;
        while((index = exec.toString().indexOf(search, pos)) >= 0) {
            int begin = index;
            index += search.length();
            int parIndex = exec.toString().indexOf("(", index);
            if (parIndex < 0) break;
            String promptType = exec.substring(index, parIndex);
            String promptIdentifier = exec.substring(index - PROMPT_FOR.length(), parIndex);
            if (PROMPT_FOR_FILE_CONTENT.equals(promptIdentifier)) {
                pos = parIndex;
                continue;
            }
            index = parIndex + 1;
            int index2 = exec.toString().indexOf(")", index);
            if (index2 < 0) break;
            int end = exec.toString().indexOf("}", index2);
            if (end < 0) break;
            end++;
            String str = exec.substring(index, index2);
            String var = varNameGenerator();
            String defaultValue = "";
            if (promptType.startsWith(PROMPT_DEFAULT_VALUE_SEPARATOR)) {
                int defValueEnd = promptType.indexOf(PROMPT_DEFAULT_VALUE_SEPARATOR, 1);
                if (defValueEnd > 0) {
                    defaultValue = promptType.substring(PROMPT_DEFAULT_VALUE_SEPARATOR.length(), defValueEnd);
                    promptType = promptType.substring(defValueEnd + PROMPT_DEFAULT_VALUE_SEPARATOR.length());
                }
            }
            if (promptType.startsWith("[") && promptType.endsWith("]")) {
                promptType = "_CMD_" + promptType.substring(1, promptType.length() - 1);
            }
            String selector = (promptType.length() > 0) ? VariableInputDescriptor.SELECTOR + promptType.substring(1) : null;
            exec.replace(begin, end, "${"+var+"}");
            inputDescriptor.append(VariableInputDescriptor.INPUT_STR_PROMPT_FIELD + "(" + var + ", \"" + str + "\", \""+defaultValue+"\""+((selector == null) ? "" : ", "+selector)+") ");
            num++;
            //pos = index2;
        }
        if (exec.toString().indexOf("${REASON}") > 0) {
            inputDescriptor.append(VariableInputDescriptor.INPUT_STR_PROMPT_FIELD + "(REASON, \""+g("MSG_Reason")+"\") ");
            num++;
        }
        return num;
    }

    private static int createAreas(StringBuffer exec, StringBuffer inputDescriptor) {
        int num = 0;
        String search = "${"+PROMPT_FOR_FILE_CONTENT+"(";
        int pos = 0;
        int index;
        while((index = exec.toString().indexOf(search, pos)) >= 0) {
            int begin = index;
            int varBegin = index + 2;
            index += search.length();
            int index2 = exec.toString().indexOf(",", index);
            int index3 = VcsUtilities.getPairIndex(exec.toString(), begin + search.length(), '(', ')');
            //int index3 = exec.toString().indexOf(")}", index);
            if (index3 < 0) break;
            if (index2 < 0 || index2 > index3) index2 = index3;
            int end = index3 + 2;
            String message = exec.substring(index, index2);
            //message = Variables.expandFast(vars, message, true);
            pos = index2 + 1;
            String fileName = "";
            if (pos < index3) {
                fileName = exec.substring(pos, index3).trim();
                //fileName = Variables.expand(vars, fileName, true);
                pos = index3;
            }
            String var = varNameGenerator();
            exec.replace(begin, end, "${"+var+"}");
            inputDescriptor.append(VariableInputDescriptor.INPUT_STR_PROMPT_AREA + "(" + var + ", \"" + message + "\", "+fileName+") ");
            num++;
            pos = begin;
        }
            //D.deb("needPrompForFileContent(): message = "+message+", fileName = "+fileName);
            //results.put(message, fileName);
            //varNames.put(exec.substring(varBegin, index3 + 1), message);
        return num;
    }

    private static int createAsks(StringBuffer exec, StringBuffer inputDescriptor) {
        int num = 0;
        String search = /*"${"+*/ASK_FOR; //+"("; // to be able to put this to conditional expression
        int pos = 0;
        int index;
        while((index = exec.toString().indexOf(search, pos)) >= 0) {
            int begin = index;
            index += search.length();
            int parIndex = exec.toString().indexOf("(", index);
            if (parIndex < 0) break;
            String promptType = exec.substring(index, parIndex);
            index = parIndex + 1;
            int index2 = exec.toString().indexOf(")", index);
            if (index2 < 0) break;
            int end = index2 + 1;
            String str = exec.substring(index, index2);
            String var = varNameGenerator();
            exec.replace(begin, end, var);
            inputDescriptor.append(VariableInputDescriptor.INPUT_STR_ASK + "(" + var + ", \"" + str + "\") ");
            num++;
            //pos = index2;
        }
        return num;
    }

    private static String varNameGenerator() {
        return "CONVERSION_VAR_" + (variableGeneratorSeed++);
    }

    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(VariableInputDescriptorCompat.class).getString(s);
    }
    
}
