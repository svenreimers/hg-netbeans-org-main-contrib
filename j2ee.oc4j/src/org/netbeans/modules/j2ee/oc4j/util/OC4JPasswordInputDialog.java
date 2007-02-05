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


package org.netbeans.modules.j2ee.oc4j.util;

import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Michal Mocnak
 */
public class OC4JPasswordInputDialog extends NotifyDescriptor {
    
    private String user;
    
    /**
     * The text field used to enter the input.
     */
    protected OC4JPasswordInputPanel panel;
    
    static protected final String title =NbBundle.getMessage(OC4JPasswordInputDialog.class, "LBL_PasswordTitle");
    
    /** Construct dialog with the specified title and label text.
     * @param text label text
     * @param title title of the dialog
     */
    public OC4JPasswordInputDialog(String user) {
        super(null, title, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE, null, null);
        
        this.user = user;
        panel = new OC4JPasswordInputPanel(user);
        
        super.setMessage(panel);
    }
    
    /**
     * Get the text which the user typed into the input line.
     * @return the text entered by the user
     */
    public String getPassword() {
        return panel.getPassword();
    }
    
    
}