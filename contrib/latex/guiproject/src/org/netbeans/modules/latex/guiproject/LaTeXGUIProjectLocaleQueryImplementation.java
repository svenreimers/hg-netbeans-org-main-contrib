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
package org.netbeans.modules.latex.guiproject;

import java.util.Locale;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.modules.spellchecker.spi.LocaleQueryImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectLocaleQueryImplementation implements LocaleQueryImplementation {

    private LaTeXGUIProject p;

    /** Creates a new instance of LaTeXGUIProjectLocaleQueryImplementation */
    public LaTeXGUIProjectLocaleQueryImplementation(LaTeXGUIProject p) {
        this.p = p;
    }

    public Locale findLocale(FileObject fileObject) {
        return ProjectSettings.getDefault(p).getLocale();
    }
    
}
