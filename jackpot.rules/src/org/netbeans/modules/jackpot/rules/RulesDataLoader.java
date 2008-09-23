/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.jackpot.rules;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.MapFormat;
import org.openide.util.NbBundle;

/**
 * RulesDataLoader: a DataLoader for Jackpot rules files.
 */
public class RulesDataLoader extends UniFileLoader {
    static final String MIME_TYPE = "text/x-rules"; // NOI18N

    /** Create a new rules file data loader. */
    public RulesDataLoader() {
        super("org.netbeans.modules.jackpot.rules.RulesDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(RulesDataLoader.class, "TYPE_JackpotRules");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(MIME_TYPE);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile)
    throws DataObjectExistsException, IOException {
        return new RulesDataObject(primaryFile, this);
    }

    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new RulesFileEntry(obj, primaryFile);
    }
    
    protected String actionsContext() {
        return "Loaders/" + MIME_TYPE + "/Actions";
    }

    /**
     * FileEntry.Format subclass that expands template macros.
     */
    public static class RulesFileEntry extends FileEntry.Format {
        RulesFileEntry(MultiDataObject obj, FileObject file) {
            super(obj, file);
        }
        
        protected java.text.Format createFormat (FileObject target, String name, String ext) {
            HashMap<String,Object> map = new HashMap<String,Object>();
            Date now = new Date();

            map.put ("NAME", name); // NOI18N
            map.put ("DATE", DateFormat.getDateInstance (DateFormat.LONG).format (now)); // NOI18N
            map.put ("TIME", DateFormat.getTimeInstance (DateFormat.SHORT).format (now)); // NOI18N
            map.put ("USER", System.getProperty ("user.name")); // NOI18N

            MapFormat format = new MapFormat (map);
            format.setLeftBrace ("__"); // NOI18N
            format.setRightBrace ("__"); // NOI18N
            format.setExactMatch (false);
            return format;
        }
    }
}