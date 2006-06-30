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

package org.netbeans.modules.vcscore.commands;

import java.io.File;
import java.util.Map;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;

/**
 * This class represents a command whose behavior is described by VcsCommand.
 *
 * @author  Martin Entlicher
 */
public interface VcsDescribedCommand extends Command, TextOutputCommand,
                                             RegexOutputCommand, FileReaderCommand,
                                             ChainingCommand {

    /**
     * Set the VcsCommand instance associated with this command.
     * @param cmd the VcsCommand.
     */
    public void setVcsCommand(VcsCommand cmd);
    
    /**
     * Get the VcsCommand instance associated with this command.
     * @return The VcsCommand.
     */
    public VcsCommand getVcsCommand();
    
    /**
     * Set additional variables for the command execution.
     * @param vars The map of variable names and values.
     */
    public void setAdditionalVariables(Map vars);
    
    /**
     * Get additional variables for the command execution.
     * @return the map of variable names and values.
     */
    public Map getAdditionalVariables();
    
    /**
     * Set a preferred execution string, which might have some variables
     * or patterns expanded.
     * @param preferredExec the preferred execution string
     */
    public void setPreferredExec(String preferredExec);
    
    /**
     * Get a preferred execution string, which might have some variables
     * or patterns expanded.
     * @return the preferred execution string
     */
    public String getPreferredExec();
    
    /**
     * Set a preferred structured execution property, which might have some variables
     * or patterns expanded.
     * @param preferredSExec the preferred structured execution property
     */
    public void setPreferredStructuredExec(StructuredExec preferredSExec);
    
    /**
     * Get a preferred structured execution property, which might have some variables
     * or patterns expanded.
     * @return the preferred structured execution property
     */
    public StructuredExec getPreferredStructuredExec();
    
    /**
     * Set the executor, which was already created to take care about executing
     * of this command.
     * @deprecated This is needed only for the compatibility with the old "API".
     */
    public void setExecutor(VcsCommandExecutor executor);
    
    /**
     * Get the executor, which was already created to take care about executing
     * of this command.
     * @deprecated This is needed only for the compatibility with the old "API".
     */
    public VcsCommandExecutor getExecutor();
    
    /**
     * Sometimes the FileObject can not be found for a desired file. In this
     * case this method should be used to specify directly the disk files to act on.
     * The command is expected to act on the union of all set FileObjects and java.io.Files.
     * <p>
     * Some commands require here directories only! TODO assert it rather then leaving
     * them fail for misterious reasons. Typicaly OS 'cd file.txt;' failure.
     * @param files The array of files to act on.
     */
    public void setDiskFiles(File[] files);

    /**
     * Sometimes the FileObject can not be found for a desired file. In this
     * case this method should be used to specify directly the disk files to act on.
     * The command is expected to act on the union of all set FileObjects and java.io.Files.
     * @return The array of files to act on.
     */
    public File[] getDiskFiles();
    
    /**
     * Set a wrapper for the visualizer.
     * A GUI output of the command (visualizer) will be displayed in this wrapper,
     * if any GUI output is available.
     * @param wrapper The wrapper for the command's GUI visualizer
     */
    public void setVisualizerWrapper(VcsCommandVisualizer.Wrapper wrapper);

    /**
     * Get a wrapper for the visualizer.
     * If there is a wrapper defined, the command visualizer will be displayed
     * in this wrapper.
     */
    public VcsCommandVisualizer.Wrapper getVisualizerWrapper();
    
    public Object clone();

}
