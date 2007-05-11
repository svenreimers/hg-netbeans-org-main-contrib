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

package org.netbeans.modules.keybindings.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;
import org.netbeans.modules.editor.options.AllOptionsFolder;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.options.SystemOption;
import org.openide.util.Lookup;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class KeyBindingsHelper {
    public static List getKeyBindings() {
  ArrayList masterList = new ArrayList(90);
        
        String kit;
        
        kit = "";
        Keymap globalMap = (Keymap)Lookup.getDefault().lookup(Keymap.class);
        Action[] actions = globalMap.getBoundActions();
        for (int i = 0; i < actions.length ; i++) {
            KeyStroke[] keyStrokes = globalMap.getKeyStrokesForAction(actions[i]);
            String actionName = (String)actions[i].getValue(Action.NAME);
            if (actionName == null) {
                actionName = "";
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append(actionName);
            removeAmpersand(buffer);
            // Separate each KeyStroke.
            for (int j = 0; j < keyStrokes.length; j++) {
                masterList.add( new KeyBindingsHelper.MultiKeyBinding(
                        new KeyStroke[] {keyStrokes[j]},
                        buffer.toString(),
                        kit));
            }
        }
        
        AllOptionsFolder allOptionsFolder = AllOptionsFolder.getDefault();
        List list = allOptionsFolder.getKeyBindingList();
        int listSize = list.size();
        
        kit = (String) list.get(0);
        for (int i = 1; i < listSize ; i++) {
            if (list.get(i) != null) {
                addKeyBinding(masterList, (JTextPane.KeyBinding) list.get(i), kit);
            }
        }
        
        List installedOptions = allOptionsFolder.getInstalledOptions();
        for (int i = 0; i < installedOptions.size(); i++) {
            BaseOptions baseOptions = (BaseOptions) SystemOption.findObject(((Class)installedOptions.get(i)), true);
            List baseKeyBindings  = baseOptions.getKeyBindingList();
            kit = (String) baseKeyBindings.get(0);
            int baseKeyBindingsSize = baseKeyBindings.size();
            for (int j = 1; j < baseKeyBindingsSize ; j++) {
                if (baseKeyBindings.get(j) instanceof JTextPane.KeyBinding) {
                    addKeyBinding(masterList, (JTextPane.KeyBinding) baseKeyBindings.get(j), kit);
                }
            }
        }
        return masterList;
    }
    
    private static void removeAmpersand(StringBuffer buffer) {
        int size = buffer.length();
        for (int i = (size - 1); i>=0; i--) {
            if (buffer.charAt(i) == '&') {
                buffer.deleteCharAt(i);
            }
        }
    }
    
    private static void addKeyBinding(List list, JTextPane.KeyBinding keyBinding, String kit) {
        KeyBindingsHelper.MultiKeyBinding multiKeyBinding;
        if (keyBinding instanceof org.netbeans.editor.MultiKeyBinding) {
            org.netbeans.editor.MultiKeyBinding netBeansMultiKeyBinding = (org.netbeans.editor.MultiKeyBinding)keyBinding;
            if (netBeansMultiKeyBinding.keys == null) {
                multiKeyBinding = new KeyBindingsHelper.MultiKeyBinding(
                        netBeansMultiKeyBinding.key,
                        netBeansMultiKeyBinding.actionName,
                        kit);
            } else {
                multiKeyBinding = new KeyBindingsHelper.MultiKeyBinding(
                        netBeansMultiKeyBinding.keys,
                        netBeansMultiKeyBinding.actionName,
                        kit);
            }
        } else {
            multiKeyBinding = new KeyBindingsHelper.MultiKeyBinding(
                    keyBinding.key,
                    keyBinding.actionName,
                    kit);
        }
        list.add(multiKeyBinding);
    }
    
    public static class MultiKeyBinding extends JTextPane.KeyBinding {
        public KeyStroke[] keys;
        
        public String kit;
        
        MultiKeyBinding(KeyStroke key, String actionName) {
            this(key, actionName,  "");
        }
        
        MultiKeyBinding(KeyStroke[] keys, String actionName) {
            this(keys, actionName,  "");
        }
        
        MultiKeyBinding(KeyStroke key, String actionName, String kit) {
            super(key, actionName);
            keys = new KeyStroke[] { key };
            this.kit = kit;
        }
        
        MultiKeyBinding(KeyStroke[] keys, String actionName, String kit) {
            super(keys[0], actionName);
            this.keys = keys;
            this.kit = kit;
        }
    }
}
