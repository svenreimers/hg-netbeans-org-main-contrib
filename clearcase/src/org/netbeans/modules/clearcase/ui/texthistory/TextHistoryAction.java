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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.clearcase.ui.texthistory;

import org.netbeans.modules.versioning.spi.VCSContext;
import javax.swing.*;
import java.awt.event.ActionEvent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

public class TextHistoryAction extends AbstractAction {
    
    private final VCSContext context;

    public TextHistoryAction(String name, VCSContext context) {
        this.context = context;
        putValue(Action.NAME, name);
    }
    
    public void actionPerformed(ActionEvent ev) {
        NotifyDescriptor nd = new NotifyDescriptor("Not implemeted yet!", "ClearCase", NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.WARNING_MESSAGE, new Object[]{}, null);        
        DialogDisplayer.getDefault().notify(nd);
    }
}