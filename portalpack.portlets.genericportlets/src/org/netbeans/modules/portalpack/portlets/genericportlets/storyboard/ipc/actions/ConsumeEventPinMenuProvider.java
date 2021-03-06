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

package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.IPCGraphScene;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class ConsumeEventPinMenuProvider implements PopupMenuProvider, ActionListener{

    
     private IPCGraphScene scene;
     private static final String ACTION_GENERATE_SOURCE = "Generate Process Event Source"; //NOI18N
     private static final String ACTION_REMOVE_PROCESS_EVENT = "Remove_Process_Event"; //NOI18N
     private static final String ACTION_ADD_ALIAS = "Add_Alias";//NOI18N
     private JPopupMenu menu;
     private CustomPinWidget widget;
     /** Creates a new instance of NodePopUpMenuProvider */
    public ConsumeEventPinMenuProvider(IPCGraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu(NbBundle.getMessage(ConsumeEventPinMenuProvider.class, "MENU_POP_UP"));
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(ConsumeEventPinMenuProvider.class, "MENU_GENERATE_CONSUME_EVENT_SOURCE"));
        item.setActionCommand(ACTION_GENERATE_SOURCE);
        item.addActionListener(this);
        item.setBackground(Color.WHITE);
        menu.add(item);
        
        JMenuItem item2 = new JMenuItem(NbBundle.getMessage(ConsumeEventPinMenuProvider.class, "MENU_ADD_ALIAS"));
        item2.setActionCommand(ACTION_ADD_ALIAS);
        item2.addActionListener(this);
        item2.setBackground(Color.WHITE);
        menu.add(item2);
        
        
        JMenuItem item1 = new JMenuItem(NbBundle.getMessage(ConsumeEventPinMenuProvider.class, "MENU_REMOVE_PROCESS_EVENT"));
        item1.setActionCommand(ACTION_REMOVE_PROCESS_EVENT);
        item1.addActionListener(this);
        item1.setBackground(Color.WHITE);
        menu.add(item1);
        
    }

    public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
        if(widget instanceof CustomPinWidget)
            this.widget = (CustomPinWidget)widget;
        widget = null;
       return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ACTION_GENERATE_SOURCE)){
            if(widget == null) return;
            scene.getTaskHandler().generateProcessEventSource(widget.getNodeKey(),widget.getEvent());
        }else if(e.getActionCommand().equals(ACTION_REMOVE_PROCESS_EVENT)){
            
            if(widget == null) return;          
                scene.getTaskHandler().removeEventPinFromNode((CustomPinWidget)widget);
            
        } else if(e.getActionCommand().equals(ACTION_ADD_ALIAS)){
            if(widget == null) return;
            scene.getTaskHandler().addAliasForEvent(widget);
        }
    }

}
