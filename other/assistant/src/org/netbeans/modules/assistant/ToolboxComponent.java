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

package org.netbeans.modules.assistant;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.accessibility.*;

/**
 *
 * @author  Richard Gregor
 */
public class ToolboxComponent extends TopComponent{
    static final long serialVersionUID=6031472310168514674L;
    private static ToolboxComponent component = null;

    private ToolboxComponent(){
        putClientProperty("PersistenceType", "OnlyOpened");
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(ToolboxComponent.class, "LBL_Toolbox_Title"));   //NOI18N                    
        JPanel panel = new JPanel();                 
        setCloseOperation(TopComponent.CLOSE_EACH);
        add(panel);
        initAccessibility();
    }
        
    public static ToolboxComponent createComp(){
        if(component == null)
            component = new ToolboxComponent();
        return component;
    }     
    
    static void clearRef(){
        component = null;
    }
    
    public HelpCtx getHelpCtx(){
        return new HelpCtx("toolbox");
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ToolboxComponent.class, "ACS_Toolbox_DESC")); // NOI18N
    }
}

