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
