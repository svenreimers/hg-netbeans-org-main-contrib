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
package org.netbeans.modules.gsf.browser;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;


/**
 * This class is based on ASTBrowserTopComponentAction in the Schliemann prototype by Jan Jancura
 * 
 * @author Jan Jancura
 */
public class AstViewerAction extends AbstractAction {
    public AstViewerAction() {
        super(NbBundle.getMessage(AstViewerAction.class, "CTL_AstViewerAction"));

        //putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(AstViewer.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = AstViewer.findInstance();
        win.open();
        win.requestActive();
    }
}