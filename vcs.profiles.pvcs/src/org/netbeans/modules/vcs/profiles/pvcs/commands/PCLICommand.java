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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openide.ErrorManager;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.TextInput;
import org.netbeans.modules.vcscore.commands.TextOutputListener;

/**
 * PCLI command that performs efficient execution using PCLICommandExecutor.
 *
 * @author  Martin Entlicher
 */
public class PCLICommand implements VcsAdditionalCommand, VcsAdditionalCommand.ImmediateOutput,
                                    TextInput {
    
    private String execStr;
    private CommandOutputListener stdoutListener;
    private CommandOutputListener stderrListener;
    private CommandDataOutputListener stdoutDataListener;
    private Pattern dataRegex;
    private CommandDataOutputListener stderrDataListener;
    private Pattern errorRegex;
    private ArrayList stdImmediateOutListeners = new ArrayList();
    private ArrayList stdImmediateErrListeners = new ArrayList();
    private Boolean[] success = { null };
    
    /** Creates a new instance of PCLICommand */
    public PCLICommand() {
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener,
                        CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        PCLICommandExecutor executor = PCLICommandExecutor.getDefault();
        if (args.length < 1 || args[0] == null) {
            stderrListener.outputLine("Expecting the PCLI command as an argument.");
            return false;
        }
        this.execStr = args[0];
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.stdoutDataListener = stdoutDataListener;
        this.stderrDataListener = stderrDataListener;
        if (dataRegex == null) {
            dataRegex = ExecuteCommand.DEFAULT_REGEX;
        }
        if (errorRegex == null) {
            errorRegex = ExecuteCommand.DEFAULT_REGEX;
        }
        try {
            this.dataRegex = Pattern.compile(dataRegex);
        } catch (PatternSyntaxException psex) {
            ErrorManager.getDefault().notify(psex);
        }
        try {
            this.errorRegex = Pattern.compile(errorRegex);
        } catch (PatternSyntaxException psex) {
            ErrorManager.getDefault().notify(psex);
        }
        try {
            return executor.runCommand(this);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public String getStrExec() {
        return execStr;
    }
    
    /**
     * Add a listener to the standard output, that will be noified
     * immediately as soon as the output text is available. It does not wait
     * for the new line and does not send output line-by-line.
     */
    public void addImmediateTextOutputListener(TextOutputListener l) {
        this.stdImmediateOutListeners.add(l);
    }

    /**
     * Add a listener to the standard error output, that will be noified
     * immediately as soon as the output text is available. It does not wait
     * for the new line and does not send output line-by-line.
     */
    public void addImmediateTextErrorListener(TextOutputListener l) {
        this.stdImmediateErrListeners.add(l);
    }

    public void sendInput(String text) {
        PCLICommandExecutor.getDefault().sendInput(text);
    }
    
    void stdOutput(String line) {
        stdoutListener.outputLine(line);
        if (dataRegex != null) {
            String[] sa = ExternalCommand.matchToStringArray(dataRegex, line);
            if (sa != null && sa.length > 0) stdoutDataListener.outputData(sa);
        }
    }
    
    void immediateStdOutput(String text) {
        Iterator it = stdImmediateOutListeners.iterator();
        while(it.hasNext()) {
            ((TextOutputListener) it.next()).outputLine(text);
        }
    }
    
    void errOutput(String line) {
        stderrListener.outputLine(line);
        if (errorRegex != null) {
            String[] sa = ExternalCommand.matchToStringArray(errorRegex, line);
            if (sa != null && sa.length > 0) stderrDataListener.outputData(sa);
        }
    }
    
    void immediateErrOutput(String text) {
        Iterator it = stdImmediateErrListeners.iterator();
        while(it.hasNext()) {
            ((TextOutputListener) it.next()).outputLine(text);
        }
        
    }
    
    void setSucceeded() {
        synchronized (success) {
            success[0] = Boolean.TRUE;
            success.notify();
        }
    }
    
    void setFailed() {
        synchronized (success) {
            success[0] = Boolean.FALSE;
            success.notify();
        }
    }
    
    boolean succeeded() {
        return success[0].booleanValue();
    }
    
    boolean isFinished() {
        return success[0] != null;
    }
    
    public void waitFinished() throws InterruptedException {
        synchronized (success) {
            if (success[0] == null) {
                success.wait();
            }
        }
    }
    
}
