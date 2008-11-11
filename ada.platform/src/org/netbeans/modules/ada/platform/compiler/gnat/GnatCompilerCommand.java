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
package org.netbeans.modules.ada.platform.compiler.gnat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.netbeans.api.ada.platform.AdaException;
import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.modules.ada.platform.compiler.CompilerCommand;
import org.netbeans.modules.ada.platform.compiler.gnat.commands.GnatMake;
import org.netbeans.modules.ada.platform.compiler.gnat.commands.GnatCommand;
import org.openide.util.Exceptions;

/**
 *
 * @author  Andrea Lucarelli
 */
public class GnatCompilerCommand extends CompilerCommand {

	private final Map<String,GnatCommand> gnatCommands;

    /**
     * 
     * @param project
     */
    public GnatCompilerCommand(AdaPlatform platform, String projectPath, String sourceFolder, String mainProgram, String executableName, String displayName) {
        super (platform, projectPath, sourceFolder, mainProgram, executableName, displayName);
        gnatCommands = new LinkedHashMap<String, GnatCommand>();
        GnatCommand[] gnatCommandArray = new GnatCommand[] {
            new GnatMake(platform, projectPath, sourceFolder, mainProgram, executableName, displayName)
        };
        for (GnatCommand gnatCommand : gnatCommandArray) {
            gnatCommands.put(gnatCommand.getCommandId(), gnatCommand);
        }
   }

    /**
     *
     * @param commandName
     * @throws IllegalArgumentException
     */
    private void invokeCommand(final String commandName) throws IllegalArgumentException, AdaException {
        final GnatCommand gnatCommand = findCommand(commandName);
        assert gnatCommand != null;
		gnatCommand.invokeCommand();
    }

    /**
     * 
     * @param commandName
     * @return
     */
    private GnatCommand findCommand (final String commandName) {
        assert commandName != null;
        return gnatCommands.get(commandName);
    }

    @Override
    public void Build() {
        try {
            invokeCommand(GnatCommand.GNAT_MAKE);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void Compile() {
        try {
            invokeCommand(GnatCommand.GNAT_COMPILE);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void Clean() {
        try {
            invokeCommand(GnatCommand.GNAT_CLEAN);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (AdaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
