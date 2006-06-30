/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.Hashtable;

import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.openide.DialogDisplayer;

/**
 * A selector of a PVCS project database.
 *
 * @author  Martin Entlicher
 */
public class PvcsProjectDatabaseSelector extends Object implements VcsAdditionalCommand {
    
    private VcsFileSystem fileSystem;
    
    /** Creates a new instance of PvcsProjectDatabaseSelector */
    public PvcsProjectDatabaseSelector() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /** This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args, CommandOutputListener stdoutNRListener,
                        CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        String projectDB = (String) vars.get("PROJECT_DB");
        projectDB = fileSystem.getVarValueAdjustment().revertAdjustedVarValue(projectDB);
        PvcsDatabaseSelectorPanel panel = new PvcsDatabaseSelectorPanel(fileSystem, args, projectDB);
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(
            new DialogDescriptor(panel, panel.getName(), true,
                                 DialogDescriptor.OK_CANCEL_OPTION,
                                 DialogDescriptor.OK_OPTION,
                                 DialogDescriptor.DEFAULT_ALIGN,
                                 new HelpCtx(PvcsProjectDatabaseSelector.class),
                                 null)))) {
                
            String dbLocation = panel.getSelectedDatabase();
            if (dbLocation != null) {
                stdoutListener.outputData(new String[] { dbLocation });
            }
        }
        panel.killRunningCommands();
        return true;
    }
    
}
