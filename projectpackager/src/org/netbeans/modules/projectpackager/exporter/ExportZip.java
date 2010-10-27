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

package org.netbeans.modules.projectpackager.exporter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 * Export zip - main class called by the action from layer.xml
 * @author Roman "Roumen" Strobl
 */
@ActionID(id = "org.netbeans.modules.projectpackager.exporter.ExportZip", category = "Tools")
@ActionRegistration(displayName = "org.netbeans.modules.projectpackager.resources.Bundle#NetBeans_Project(s)_as_Zip...", iconInMenu=false)
@ActionReference(path = "Menu/File/Export", position = 1000)
public class ExportZip implements ActionListener {
    private static ExportZipDialog zpd;

    public @Override void actionPerformed(ActionEvent evt) {        
        if (ExportPackageInfo.isProcessed()) {
            if (zpd.isShowing()) { 
                zpd.requestFocus();
                return;
            } else {
                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Another_instance_of_this_action_is_already_running._Please_wait_untill_it_finishes."), NotifyDescriptor.ERROR_MESSAGE);
                d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_another_instance_is_running"));
                DialogDisplayer.getDefault().notify(d);            
                return;
            }
        }
        
        ExportPackageInfo.setProcessed(true);
        
        if (ExportZipUITools.getListData()!=null) {
            // XXX should rather delete OK/Cancel buttons from frame, and use NotifyDescriptor on a JPanel instead
            zpd = new ExportZipDialog();
            zpd.setVisible(true);
        } else {
            ExportPackageInfo.setProcessed(false);
        }
    }
        
}
