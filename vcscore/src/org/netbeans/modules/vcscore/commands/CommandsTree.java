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

package org.netbeans.modules.vcscore.commands;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * This class represents a tree structure of commands. This structure is used
 * e.g. to construct the popup menu of commands.
 * Every instance of this class represents a single node in the tree structure.
 *
 * @author  Martin Entlicher
 */
public final class CommandsTree extends Object {

    /**
     * An empty commands tree element, that can be used for separators.
     */
    public static final CommandsTree EMPTY = new CommandsTree(null);
    
    private static final Object CHANGE_LOCK = new Object();
    
    private CommandSupport cmdSupport;
    private List children;
    
    /**
     * Creates a new instance of CommandsTree.
     * @param cmd The Command or <code>null</code> for a separator.
     */
    public CommandsTree(CommandSupport cmdSupport) {
        this.cmdSupport = cmdSupport;
        children = new ArrayList();
    }
    
    /**
     * Get the command of this item.
     * @return The command or <code>null</code> for a separator.
     */
    public final CommandSupport getCommandSupport() {
        return cmdSupport;
    }
    
    /**
     * Add a new child to this tree node.
     * @param child The new child to be added.
     */
    public final void add(CommandsTree child) {
        if (this == EMPTY) throw new IllegalArgumentException("No children can be added to CommandsTree.EMPTY");
        synchronized (CHANGE_LOCK) {
            children.add(child);
        }
    }
    
    /**
     * Tells whether this node of the tree has some children.
     * @return True if there are some children.
     */
    public final boolean hasChildren() {
        synchronized (CHANGE_LOCK) {
            return children.size() > 0;
        }
    }
    
    /**
     * Provides the array of children.
     * @return The array of children (empty array if there are no children).
     */
    public final CommandsTree[] children() {
        synchronized (CHANGE_LOCK) {
            return (CommandsTree[]) children.toArray(new CommandsTree[children.size()]);
        }
    }
    
    /**
     * Provider of CommandsTree.
     */
    public static interface Provider {
        
        /**
         * The name of the property, that is fired to the listeners when the
         * provided commands change.
         */
        public static final String PROP_COMMANDS = "commandsTree"; // NOI18N
        
        /**
         * Get the commands.
         * @return The root of the commands tree.
         */
        public CommandsTree getCommands();
        
        /**
         * Set the commands.
         * @param commands The root of the commands tree.
         *
        public void setCommands(CommandsTree commands);
         */
        
        /**
         * Add a property change listener to this provider.
         * The listener is called whenever the provided commands change.
         */
        public void addPropertyChangeListener(PropertyChangeListener l);
        
        /**
         * Remove the property change listener, that is attached to this provider.
         */
        public void removePropertyChangeListener(PropertyChangeListener l);
        
        /**
         * Get the expert mode of this commands provider. If it's true, all commands
         * have the expert mode turned on by default;
         */
        public boolean isExpertMode();
        
        /**
         * Set the expert mode of this commands provider. If it's true, all commands
         * have the expert mode turned on by default;
         */
        public void setExpertMode(boolean expertMode);
        
    }
}
