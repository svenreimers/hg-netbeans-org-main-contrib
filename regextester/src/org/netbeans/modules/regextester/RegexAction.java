/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.regextester;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows Regex component.
 */
public class RegexAction extends AbstractAction {
    
    public RegexAction() {
        putValue(NAME, NbBundle.getMessage(RegexAction.class, "CTL_RegexAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(RegexTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = RegexTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
