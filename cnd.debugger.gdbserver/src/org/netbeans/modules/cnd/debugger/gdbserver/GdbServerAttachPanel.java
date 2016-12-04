/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * GdbServerAttachPanel.java
 *
 * Created on Aug 14, 2009, 7:39:50 PM
 */

package org.netbeans.modules.cnd.debugger.gdbserver;

import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.netbeans.modules.cnd.api.remote.RemoteSyncSupport;
import org.netbeans.modules.cnd.debugger.common2.debugger.NativeDebuggerInfo;
import org.netbeans.modules.cnd.debugger.common2.debugger.NativeDebuggerManager;
import org.netbeans.modules.cnd.debugger.common2.debugger.debugtarget.DebugTarget;
import org.netbeans.modules.cnd.debugger.common2.debugger.options.DebuggerOption;
import org.netbeans.modules.cnd.debugger.common2.debugger.remote.CndRemote;
import org.netbeans.modules.cnd.debugger.common2.utils.ProjectComboBoxSupport;
import org.netbeans.modules.cnd.debugger.common2.utils.ProjectComboBoxSupport.ProjectCBItem;
import org.netbeans.modules.cnd.debugger.gdb2.options.GdbDebuggerInfoFactory;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationSupport;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

/**
 *
 * @author Egor Ushakov
 */
public class GdbServerAttachPanel extends JPanel implements HelpCtx.Provider {
    private final Controller controller;

    private static final String TARGET_KEY = "last-gdbserver-target"; //NOI18N

    /** Creates new form GdbServerAttachPanel */
    public GdbServerAttachPanel() {
        controller = new GdbServerAttachController();
        initComponents();
        // Fill the Projects combo box
        ProjectComboBoxSupport.fillProjectsCombo(projectCB, null);
        targetTF.setText(NbPreferences.forModule(GdbServerAttachPanel.class).get(TARGET_KEY, "remote host:port")); //NOI18N
    }

    Controller getController() {
        return controller;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectLabel = new javax.swing.JLabel();
        projectCB = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        targetTF = new javax.swing.JTextField();

        projectLabel.setText(org.openide.util.NbBundle.getMessage(GdbServerAttachPanel.class, "GdbServerAttachPanel.projectLabel.text")); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(GdbServerAttachPanel.class, "GdbServerAttachPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectLabel)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(targetTF, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(projectCB, 0, 395, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(targetTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectLabel)
                    .addComponent(projectCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox projectCB;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField targetTF;
    // End of variables declaration//GEN-END:variables

    private class GdbServerAttachController implements Controller {

        @Override
        public boolean cancel() {
            return true;
        }

        @Override
        public boolean ok() {
            String targetValue = targetTF.getText();
            if (targetValue.length() == 0) {
                return false;
            }
            
            //store last values
            NbPreferences.forModule(GdbServerAttachPanel.class).put(TARGET_KEY, targetValue);

            ProjectCBItem pi = (ProjectCBItem) projectCB.getSelectedItem();
            if (pi != null) {
                MakeConfiguration conf = ConfigurationSupport.getProjectActiveConfiguration(pi.getProject()).clone();
                DebugTarget dt = new DebugTarget(conf);
                
                // set executable
                String path = conf.getAbsoluteOutputValue().replace("\\", "/"); // NOI18N
                ExecutionEnvironment exEnv = conf.getDevelopmentHost().getExecutionEnvironment();
                path = RemoteSyncSupport.getPathMap(exEnv, pi.getProject()).getRemotePath(path, true);
                dt.setExecutable(path);

                // always use gdb
                NativeDebuggerInfo gdi = GdbDebuggerInfoFactory.create(dt, 
                        CndRemote.userhostFromConfiguration(conf), conf, NativeDebuggerManager.ATTACH, targetValue);                
                String symbolFile = DebuggerOption.SYMBOL_FILE.getCurrValue(gdi.getDbgProfile().getOptions());
                symbolFile = ((MakeConfiguration) conf).expandMacros(symbolFile);
                gdi.setSymbolFile(symbolFile);
                NativeDebuggerManager.get().debugNoAsk(gdi);
//                try {
//                    GdbDebugger.attachGdbServer(target, pi.getProjectInformation());
//                } catch (DebuggerStartException dse) {
//                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
//                            NbBundle.getMessage(GdbServerAttachPanel.class,
//                           "ERR_UnexpecedAttachGdbServerFailure", target))); // NOI18N
//                }
            }
            return true;
        }
        
        @Override
        public boolean isValid() {
            return projectCB.getItemCount() > 0;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
        }

    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gdbserver"); // NOI18N
    }
}
