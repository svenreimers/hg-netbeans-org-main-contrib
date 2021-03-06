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

package org.netbeans.modules.vcs.advanced.commands;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import org.netbeans.modules.vcs.advanced.conditioned.ConditionedStructuredExec;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;

import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;

/**
 * Builder of the ConditionedCommands.
 *
 * @author  Martin Entlicher
 */
public final class ConditionedCommandsBuilder {
    
    /** The tree of commands. The commands, that are affected by a condition,
     * represent just the name (the real command has to be found through
     * conditions). */
    private CommandsTree commands;
    /** An array of conditions, that are defined for commands.
     * The keys are just command names, values are Condition[]. *
    private Map conditionsByCommands;
    /** Commands defined when the condition is true. *
    private Map commandsByConditions;
     */
    
    /** A map of command names as keys and associated ConditionedCommand
     * instances as values. */
    private Map conditionedCommandsByName;
    
    private ConditionedCommands ccommands;
    
    /**
     * Creates a new instance of ConditionedCommandsBuilder based on the commands
     * tree.
     * Changing the structure of this tree later will cause modifications to this
     * object as well.
     */
    public ConditionedCommandsBuilder(CommandsTree commands) {
        this.commands = commands;
        conditionedCommandsByName = new HashMap();
        //conditionsByCommands = new HashMap();
        //commandsByConditions = new IdentityHashMap();
        ccommands = new ConditionedCommands(commands, conditionedCommandsByName, this);
    }
    
    /**
     * Get the conditioned commands, created from the user command conditions.
     * The changes to this builder are reflected to the conditioned commands.
     */
    public ConditionedCommands getConditionedCommands() {
        return ccommands;
    }
    
    /**
     * Add a command under a certain condition.
     */
    public void addConditionedCommand(UserCommandSupport supp, Condition condition) {
        String name = supp.getName();
        ConditionedCommand ccmd = (ConditionedCommand) conditionedCommandsByName.get(name);
        if (ccmd == null) {
            ccmd = new ConditionedCommand(name);
            conditionedCommandsByName.put(name, ccmd);
        }
        ccmd.addCommand(new ConditionedPropertiesCommand(supp), condition);
        
        //Condition[] cmdConditions = (Condition[]) conditionsByCommands.get(suppName);
        //conditionsByCommands.put(suppName, addCondition(condition, cmdConditions));
        //commandsByConditions.put(condition, supp);
        
        //cmdConditions = (Condition[]) mainConditionsByCommands.get(suppName);
        //mainConditionsByCommands.put(suppName, addCondition(condition, cmdConditions));
        //commandsByMainConditions.put(condition, supp);
    }
    
    private static Condition[] addCondition(Condition condition, Condition[] conditions) {
        if (conditions == null) {
            conditions = new Condition[] { condition };
        } else {
            Condition[] newConditions = new Condition[conditions.length + 1];
            System.arraycopy(conditions, 0, newConditions, 0, conditions.length);
            newConditions[conditions.length] = condition;
            conditions = newConditions;
        }
        return conditions;
    }
    
    /**
     * Add a conditioned property to a command.
     * The command must already be present in the commands tree.
     * @param cmdName The name of the command
     * @param condition The condition under which the command was added (there
     *                  can be more commands of the same name, but different
     *                  conditions defined). This parameter can be
     *                  <code>null</code> if the command is not added under any
     *                  condition.
     * @param property The conditioned property
     */
    // This method change the command from UserCommandSupport to
    // ConditionedPropertiesCommand.
    public boolean addPropertyToCommand(String cmdName, Condition condition,
                                        ConditionedProperty property) {
        ConditionedCommand ccmd = (ConditionedCommand) conditionedCommandsByName.get(cmdName);
        ConditionedPropertiesCommand cpcmd = null;
        if (ccmd == null) {
            CommandsTree cmdTree = findCommand(cmdName, commands);
            if (cmdTree == null) return false;
            UserCommandSupport cmdSupp = (UserCommandSupport) cmdTree.getCommandSupport();
            ccmd = new ConditionedCommand(cmdName);
            conditionedCommandsByName.put(cmdName, ccmd);
            cpcmd = new ConditionedPropertiesCommand(cmdSupp);
            ccmd.addCommand(cpcmd, condition);
        }
        if (cpcmd == null) {
            cpcmd = ccmd.getCommandFor(condition);
            if (cpcmd == null) return false;
        }
        cpcmd.addConditionedProperty(property);
        /*
        UserCommandSupport cmdSupp = null;
        Condition[] cmdConditions = (Condition[]) conditionsByCommands.get(cmdName);
        if (cmdConditions == null) {
            CommandsTree cmdTree = findCommand(cmdName, commands);
            if (cmdTree == null) return false;
            cmdSupp = (UserCommandSupport) cmdTree.getCommandSupport();
            splitCommand(cmdSupp, property);
        } else {
            splitCommands(cmdName, condition, cmdConditions, property);
        }
        if (condition == null) {
            Collection properties = (Collection) conditionedPropertiesByCommands.get(cmdName);
            if (properties == null) {
                properties = new HashSet();
                conditionedPropertiesByCommands.put(cmdName, properties);
            }
            properties.add(property);
        } else {
            Collection properties = (Collection) conditionedPropertiesByMainConditions.get(condition);
            if (properties == null) {
                properties = new HashSet();
                conditionedPropertiesByMainConditions.put(condition, properties);
            }
            properties.add(property);
        }
         */
        return true;
    }

    private static CommandsTree findCommand(String cmdName, CommandsTree commands) {
        CommandSupport supp = commands.getCommandSupport();
        if (supp != null && cmdName.equals(supp.getName())) {
            return commands;
        }
        CommandsTree[] children = commands.children();
        for (int i = 0; i < children.length; i++) {
            CommandsTree command = findCommand(cmdName,  children[i]);
            if (command != null) return command;
        }
        return null;
    }
    
    
    /**
     * This class represents a command that is defined under various conditions.
     */
    public static final class ConditionedCommand extends Object {
        
        private final String name;
        private Condition[] conditions;
        private ConditionedPropertiesCommand[] commands;
        
        public ConditionedCommand(String name) {
            this.name = name;
        }
        
        public void addCommand(ConditionedPropertiesCommand cmd, Condition c) {
            conditions = addCondition(c, conditions);
            commands = addCommand(cmd, commands);
        }
        
        /**
         * Get the array of conditions under which the individual conditioned
         * commands are defined.
         */
        public Condition[] getConditions() {
            return conditions;
        }
        
        /**
         * Get the command, that is defined for the given condition (if any).
         */
        public ConditionedPropertiesCommand getCommandFor(Condition c) {
            if (conditions == null) return null;
            for (int i = 0; i < conditions.length; i++) {
                if (c == conditions[i]) {
                    return commands[i];
                }
            }
            return null;
        }
        
        /**
         * Get the UserCommandSupport that is defined when for the set of
         * conditional variables.
         * @return the instance of UserCommandSupport or <code>null</code>
         *         when the conditions are not satisfied.
         */
        public UserCommandSupport getCommand(Map conditionalVars) {
            if (conditions == null) return null;
            for (int i = 0; i < conditions.length; i++) {
                if (conditions[i] == null || conditions[i].isSatisfied(conditionalVars)) {
                    return commands[i].createCommand(conditionalVars);
                }
            }
            return null;
        }
        
        private static ConditionedPropertiesCommand[] addCommand(ConditionedPropertiesCommand command, ConditionedPropertiesCommand[] commands) {
            if (commands == null) {
                commands = new ConditionedPropertiesCommand[] { command };
            } else {
                ConditionedPropertiesCommand[] newCommands = new ConditionedPropertiesCommand[commands.length + 1];
                System.arraycopy(commands, 0, newCommands, 0, commands.length);
                newCommands[commands.length] = command;
                commands = newCommands;
            }
            return commands;
        }
    
    }
    
    /**
     * This class represents a command, that have some conditioned properties.
     * The actual instance of UserCommandSupport is created from the
     * conditioned properties based on the conditional variables.
     */
    public static final class ConditionedPropertiesCommand extends Object {
        
        /** The user command support whose UserCommand already have set all
         * unconditional properties to it. */
        private final UserCommandSupport cmd;
        private ConditionedProperty[] conditionedProperties;
        
        public ConditionedPropertiesCommand(UserCommandSupport cmd) {
            this.cmd = cmd;
        }
        
        void addConditionedProperty(ConditionedProperty property) {
            conditionedProperties = addProperty(property, conditionedProperties);
        }
        
        private static ConditionedProperty[] addProperty(ConditionedProperty property, ConditionedProperty[] properties) {
            if (properties == null) {
                properties = new ConditionedProperty[] { property };
            } else {
                ConditionedProperty[] newProperties = new ConditionedProperty[properties.length + 1];
                System.arraycopy(properties, 0, newProperties, 0, properties.length);
                newProperties[properties.length] = property;
                properties = newProperties;
            }
            return properties;
        }
        
        /**
         * Get the represented command, which have the unconditioned properties
         * set. Get the conditioned properties from {@link #getConditionedProperties()}.
         */
        public UserCommandSupport getCommand() {
            return cmd;
        }
        
        /**
         * Get the list of conditioned properties.
         */
        public ConditionedProperty[] getConditionedProperties() {
            return conditionedProperties;
        }
    
        /**
         * Create a command, that have the set of properties defined
         * according to the provided map of conditional variables.
         */
        public UserCommandSupport createCommand(Map conditionalVars) {
            if (conditionedProperties == null) {
                return cmd;
            } else {
                UserCommand ucmd = (UserCommand) cmd.getVcsCommand().clone();
                for (int i = 0; i < conditionedProperties.length; i++) {
                    ConditionedProperty property = conditionedProperties[i];
                    Object value = property.getValue(conditionalVars);
                    if (value != null) {
                        ucmd.setProperty(property.getName(), value);
                    }
                }
                return new UserCommandSupport(ucmd, cmd.getExecutionContext());
            }
        }
        
        
    }
    
    /**
     * This class represents a command's property, that is dependent on some
     * conditions.
     */
    public static final class ConditionedProperty extends Object {
        
        private final String name;
        private final Condition c;
        private final Map valuesByConditions;
        
        /**
         * Create a new conditioned property.
         * @param name The name of the property
         * @param c The condition under which this property is defined
         * @param valuesByConditions The map of conditions as keys and
         *        corresponding Object values. The conditions already contain
         *        the main condition <code>c</code>.
         */
        public ConditionedProperty(String name, Condition c, Map valuesByConditions) {
            this.name = name;
            this.c = c;
            this.valuesByConditions = valuesByConditions;
        }
        
        /**
         * Get the name of this property.
         */
        public String getName() {
            return name;
        }
        
        /**
         * Get the main condition under which this property is defined.
         */
        public Condition getCondition() {
            return c;
        }
        
        /**
         * Get a map of conditions as keys and corresponding Object values.
         * The conditions already contain the main condition.
         */
        public Map getValuesByConditions() {
            return valuesByConditions;
        }
        
        /**
         * Get the value of this condition.
         */
        public Object getValue(Map conditionalVars) {
            for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
                Condition c = (Condition) it.next();
                if (c == null || c.isSatisfied(conditionalVars)) {
                    Object value = valuesByConditions.get(c);
                    if (value instanceof ConditionedStructuredExec) {
                        //value = getStructuredExec((ConditionedStructuredExec) value, conditionalVars);
                        ConditionedStructuredExec cexec = (ConditionedStructuredExec) value;
                        value = new StructuredExec(cexec.getWorking(), cexec.getExecutable(), cexec.getArguments(conditionalVars));
                    }
                    return value;
                }
            }
            return null;
        }
        
    }
    
}
