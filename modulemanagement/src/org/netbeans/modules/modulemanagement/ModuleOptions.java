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

package org.netbeans.modules.modulemanagement;

import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.netbeans.api.autoupdate.*;
import org.netbeans.api.autoupdate.InstallSupport.Installer;
import org.netbeans.api.autoupdate.InstallSupport.Validator;
import org.netbeans.api.autoupdate.OperationSupport.Restarter;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionGroups;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.util.*;

import static org.netbeans.modules.modulemanagement.Bundle.*;

/**
 *
 * @author Jaroslav Tulach
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.sendopts.OptionProcessor.class)
public class ModuleOptions extends OptionProcessor {
    private static final Logger LOG = Logger.getLogger(ModuleOptions.class.getName());
    
    private Option list;
    private Option install;
    private Option disable;
    private Option enable;
    private Option update;
    private Option refresh;
    private Option both;
    
    /** Creates a new instance of ModuleOptions */
    public ModuleOptions() {
    }

    @NbBundle.Messages({
        "MSG_UpdateModules=Updates all or specified modules",
        "MSG_RefreshModules=Refresh catalog of available modules"
    })
    private Option init() {
        if (both != null) {
            return both;
        }

        String b = "org.netbeans.modules.modulemanagement.Bundle";
        list = Option.shortDescription(
            Option.withoutArgument(Option.NO_SHORT_NAME, "list"), b, "MSG_ListModules"); // NOI18N
        install = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "install"), b, "MSG_InstallModules"); // NOI18N
        disable = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "disable"), b, "MSG_DisableModules"); // NOI18N
        enable = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "enable"), b, "MSG_EnableModules"); // NOI18N
        update = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "update"), b, "MSG_UpdateModules"); // NOI18N
        refresh = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "refresh"), b, "MSG_RefreshModules"); // NOI18N
        
        Option oper = OptionGroups.oneOf(list, install, disable, enable, update, refresh);
        Option modules = Option.withoutArgument(Option.NO_SHORT_NAME, "modules");
        both = OptionGroups.allOf(modules, oper);
        return both;
    }

    public Set<Option> getOptions() {
        return Collections.singleton(init());
    }
    
    private void refresh(Env env) throws CommandException {
        for (UpdateUnitProvider p : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(true)) {
            try {
                env.getOutputStream().println("Refreshing " + p.getDisplayName());
                p.refresh(null, true);
            } catch (IOException ex) {
                throw (CommandException)new CommandException(33, ex.getMessage()).initCause(ex);
            }
        }
    }

    @NbBundle.Messages({
        "MSG_ListHeader_CodeName=Code Name",
        "MSG_ListHeader_Version=Version",
        "MSG_ListHeader_State=State"
    })
    private void listAllModules(PrintStream out) {
        List<UpdateUnit> modules = UpdateManager.getDefault().getUpdateUnits();
        
        PrintTable table = new PrintTable(
            MSG_ListHeader_CodeName(), MSG_ListHeader_Version(), MSG_ListHeader_State()
        );
        table.setLimits(50, -1, -1);
        for (UpdateUnit uu : modules) {
            table.addRow(Status.toArray(uu));
        }
        table.write(out);
        out.flush();
    }

    private static <T extends Throwable> T initCause(T t, Throwable cause) {
        t.initCause(cause);
        return t;
    }

    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        if (optionValues.containsKey(list)) {
            listAllModules(env.getOutputStream());
        }
    
        try {
            if (optionValues.containsKey(install)) {
                install(env, optionValues.get(install));
            }

            if (optionValues.containsKey(disable)) {
                changeModuleState(optionValues.get(disable), false);
            }

            if (optionValues.containsKey(enable)) {
                changeModuleState(optionValues.get(enable), true);
            }
        } catch (InterruptedException ex) {
            throw initCause(new CommandException(4), ex);
        } catch (IOException ex) {
            throw initCause(new CommandException(4), ex);
        } catch (OperationException ex) {
            throw initCause(new CommandException(4), ex);
        }
        if (optionValues.containsKey(update)) {
            updateModules(env, optionValues.get(update));
        }
        if (optionValues.containsKey(refresh)) {
            refresh(env);
        }
    }

    private void changeModuleState(String[] cnbs, boolean enable) throws IOException, CommandException, InterruptedException, OperationException {
        for (String cnb : cnbs) {
            int slash = cnb.indexOf('/');
            if (slash >= 0) {
                cnb = cnb.substring(0, slash);
            }
        }
        
        Set<String> all = new HashSet<String>(Arrays.asList(cnbs));

        List<UpdateUnit> units = UpdateManager.getDefault().getUpdateUnits();
        OperationContainer<OperationSupport> operate = enable ? OperationContainer.createForEnable() : OperationContainer.createForDisable();
        for (UpdateUnit updateUnit : units) {
            if (all.contains(updateUnit.getCodeName())) {
                if (enable) {
                    operate.add(updateUnit, updateUnit.getInstalled());
                } else {
                    operate.add(updateUnit, updateUnit.getInstalled());
                }
            }
        }
        OperationSupport support = operate.getSupport();
        support.doOperation(null);
    }

    private void updateModules(Env env, String[] pattern) throws CommandException {
        installModules(env, pattern, true);
    }

    @NbBundle.Messages({
        "MSG_CantCompileRegex=Cannot understand regular expession '{0}'"
    })
    private static Pattern[] findMatcher(Env env, String[] pattern) {
        Pattern[] arr = new Pattern[pattern.length];
        for (int i = 0; i < arr.length; i++) {
            try {
                arr[i] = Pattern.compile(pattern[i]);
            } catch (PatternSyntaxException e) {
                env.getErrorStream().println(Bundle.MSG_CantCompileRegex(pattern[i]));
            }
        }
        return arr;
    }

    private static boolean matches(String txt, Pattern[] pats) {
        for (Pattern p : pats) {
            if (p == null) {
                continue;
            }
            if (p.matcher(txt).matches()) {
                return true;
            }
        }
        return false;
    }

    private void install(Env env, String[] pattern) throws CommandException {
        installModules(env, pattern, false);
    }
    
    @NbBundle.Messages({
        "MSG_Installing=Installing {0}@{1}",
        "MSG_InstallNoMatch=Cannot install. No match for {0}.",
        "MSG_UpdateNoMatch=Nothing to update. The pattern {0} has no match among available updates.",
        "MSG_Update=Will update {0}@{1} to version {2}"
    })
    private void installModules(Env env, String[] pattern, boolean update) throws CommandException {
        Pattern[] pats = findMatcher(env, pattern);

        List<UpdateUnit> units = UpdateManager.getDefault().getUpdateUnits();
        OperationContainer<OperationSupport> operate = OperationContainer.createForDirectInstall();
        for (UpdateUnit uu : units) {
            if (uu.getInstalled() != null) {
                continue;
            }
            if (!matches(uu.getCodeName(), pats)) {
                continue;
            }
            if (uu.getAvailableUpdates().isEmpty()) {
                continue;
            }
            UpdateElement ue = uu.getAvailableUpdates().get(0);
            if (update) {
                env.getOutputStream().println(
                    Bundle.MSG_Update(uu.getCodeName(), uu.getInstalled().getSpecificationVersion(), ue.getSpecificationVersion()));
            } else {
                env.getOutputStream().println(
                    Bundle.MSG_Installing(uu.getCodeName(), ue.getSpecificationVersion()));
            }
            operate.add(ue);
        }
        final OperationSupport support = operate.getSupport();
        if (support == null) {
            if (update) {
                env.getOutputStream().println(Bundle.MSG_UpdateNoMatch(Arrays.asList(pats)));
            } else {
                env.getOutputStream().println(Bundle.MSG_InstallNoMatch(Arrays.asList(pats)));
            }
            return;
        }
        try {
            Restarter restarter = support.doOperation(null);
            assert restarter == null;
            if (restarter != null) {
                support.doRestart(restarter, null);
            }
        } catch (OperationException ex) {
            throw (CommandException) new CommandException(33, ex.getMessage()).initCause(ex);
        }
    }

}

