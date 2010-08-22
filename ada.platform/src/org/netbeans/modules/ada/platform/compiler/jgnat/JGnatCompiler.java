/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.platform.compiler.jgnat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.netbeans.api.ada.platform.AdaException;
import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.spi.ada.platform.Compiler;
import org.netbeans.modules.ada.platform.compiler.jgnat.commands.JGnatCommand;
import org.netbeans.modules.ada.platform.compiler.jgnat.commands.JGnatClean;
import org.netbeans.modules.ada.platform.compiler.jgnat.commands.JGnatMake;
import org.netbeans.modules.ada.platform.compiler.jgnat.commands.JRun;
import org.openide.util.Exceptions;

/**
 *
 * @author  Andrea Lucarelli
 */
public class JGnatCompiler implements Compiler {

    private final Map<String, JGnatCommand> jgnatCommands;
    private final AdaPlatform platform;
    private final String projectName;
    private final String projectPath;
    private final ArrayList<String> sourceFolders;
    private final String mainFile;
    private final String outputFile;
    private final String commandName;
    private final String spcPostfix;
    private final String bdyPostfix;
    private final String sepPostfix;
    private final String spcExt;
    private final String bdyExt;
    private final String sepExt;

    /**
     * 
     * @param project
     */
    public JGnatCompiler(AdaPlatform platform, String projectName, String projectPath, ArrayList<String> sourceFolders, String mainFile, String outputFile, String commandName,
            String spcPostfix, String bdyPostfix, String sepPostfix, String spcExt, String bdyExt, String sepExt) {

        assert platform != null;

        this.platform = platform;
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.sourceFolders = sourceFolders;
        this.mainFile = mainFile;
        this.outputFile = outputFile;
        this.commandName = commandName;

        jgnatCommands = new LinkedHashMap<String, JGnatCommand>();
        JGnatCommand[] gnatCommandArray = new JGnatCommand[]{
            new JGnatMake(this),
            new JGnatClean(this),
            new JRun(this)
        };
        for (JGnatCommand gnatCommand : gnatCommandArray) {
            jgnatCommands.put(gnatCommand.getCommandId(), gnatCommand);
        }
        this.spcExt = spcExt;
        this.bdyExt = bdyExt;
        this.sepExt = sepExt;
        this.spcPostfix = spcPostfix;
        this.bdyPostfix = bdyPostfix;
        this.sepPostfix = sepPostfix;
    }

    /**
     *
     * @param commandName
     * @throws IllegalArgumentException
     */
    private void invokeCommand(final String commandName, final String displayTitle, final String args) throws IllegalArgumentException, AdaException {
        final JGnatCommand jgnatCommand = findCommand(commandName);
        assert jgnatCommand != null;
        jgnatCommand.invokeCommand(displayTitle, args);
    }

    /**
     * 
     * @param commandName
     * @return
     */
    private JGnatCommand findCommand(final String commandName) {
        assert commandName != null;
        return jgnatCommands.get(commandName);
    }

    public void Build() {
        try {
            invokeCommand(JGnatCommand.JVM_GNAT_MAKE, this.getProjectName() + "(" + this.getCommandName() + ")", null);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void Run(String args) {
        try {
            invokeCommand(JGnatCommand.RUN, this.getProjectName() + "(" + this.getCommandName() + ")", args);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void Compile() {
        try {
            invokeCommand(JGnatCommand.JVM_GNAT_COMPILE, this.getProjectName() + "(" + this.getCommandName() + ")", null);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void Clean() {
        try {
            invokeCommand(JGnatCommand.JVM_GNAT_CLEAN, this.getProjectName() + "(" + this.getCommandName() + ")", null);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public AdaPlatform getPlatform() {
        return platform;
    }

    public String getExecutableFile() {
        return outputFile;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getMainFile() {
        return mainFile;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public ArrayList<String> getSourceFolders() {
        return sourceFolders;
    }

    public String getBdyExt() {
        return bdyExt;
    }

    public String getSepExt() {
        return sepExt;
    }

    public String getSpcExt() {
        return spcExt;
    }

    public String getBdyPostfix() {
        return bdyPostfix;
    }

    public String getSepPostfix() {
        return sepPostfix;
    }

    public String getSpcPostfix() {
        return spcPostfix;
    }
}
