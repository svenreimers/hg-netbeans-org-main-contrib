/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase.ui;

import java.util.Set;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.FileInformation;
import org.netbeans.modules.clearcase.FileStatusCache;
import org.netbeans.modules.clearcase.util.Utils;
import org.openide.filesystems.FileUtil;

/**
 * @author Maros Sandor
 */
public class IgnoreAction extends AbstractAction {
    
    private VCSContext context;    
    
    public IgnoreAction(VCSContext context) {
        super(getNameFromContext(context));
        this.context = context;
    }

    @Override
    public boolean isEnabled() {        
        return getStatus(context) != -1;
    }
    
    public void actionPerformed(ActionEvent e) {
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();        
        Set<File> roots = context.getRootFiles();
        if(roots.size() == 0 ) {
            return;
        }
        for (File file : roots) {
            if (ClearcaseModuleConfig.isIgnored(file)) {
                ClearcaseModuleConfig.setUnignored(file);
            } else {
                ClearcaseModuleConfig.setIgnored(file);
            }    
            Clearcase.getInstance().getFileStatusCache().refresh(file, false);
        }                                
        Utils.afterCommandRefresh(roots.toArray(new File[roots.size()]));        
    }

    private static String getNameFromContext(VCSContext context) {
        int status = getStatus(context);
        if(status == FileInformation.STATUS_NOTVERSIONED_IGNORED) {
            return "Unignore";    
        } else {
            return "Ignore";    
        }        
    }
    
    private static int getStatus(VCSContext context) {
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();        
        File[] files = context.getRootFiles().toArray(new File[context.getRootFiles().size()]);        
        int status = -1;
        for (int i = 0 ; i < files.length; i++) {
            File file = files[i];            
            if(i == 0) {
                status = cache.getInfo(file).getStatus();                
                if( ( status & 
                      (FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY | 
                       FileInformation.STATUS_NOTVERSIONED_IGNORED) ) == 0 ) {
                    return -1;
                }
            } else {
                if( cache.getInfo(file).getStatus() != status ) {
                    return -1;
                }            
            }                
        }        
        return status;
    }
    
}
