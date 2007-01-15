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

package org.netbeans.modules.rectangularedittools.actions;

import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class RectangularUppercaseAction extends AbstractRectangularAction {
    public String getName() {
        return NbBundle.getMessage(RectangularUppercaseAction.class, "CTL_RectangularUppercaseAction");
    }

    protected String iconResource() {
        return "org/netbeans/modules/rectangularedittools/actions/rectangularuppercase.gif";
    }

    protected boolean isReplacingAction() {
        return true;
    }

    protected boolean isCopyingAction() {
        return false;
    }

    protected boolean isRetainSelection() {
        return true;
    }

    protected boolean isPostProcessingAction() {
        return true;
    }

    protected String getReplacementText(int rectangleWidth) {
        return null;
    }

    protected String getPostProcessedText(String originalText) {
        if (originalText == null) {
            return null;
        }
        return originalText.toUpperCase();
    }
}

