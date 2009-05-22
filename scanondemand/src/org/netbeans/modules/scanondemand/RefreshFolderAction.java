/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.scanondemand;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.modules.parsing.api.indexing.IndexingManager;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Pavel Flaska
 */
public class RefreshFolderAction extends AbstractAction implements ContextAwareAction {

    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new ContextAction(actionContext);
    }

    private final class ContextAction extends AbstractAction implements Presenter.Popup {

        private final Collection<? extends DataFolder> folders;
        
        public ContextAction(Lookup context) {
            // Collect projects corresponding to selected folders.
            folders = context.lookupAll(DataFolder.class);
            putValue(Action.NAME, NbBundle.getMessage(RefreshFolderAction.class, "refreshFolder"));
        }

        public void actionPerformed(ActionEvent e) {
            // Run asynch so that UI is not blocked; might show progress dialog (?).
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    for (DataFolder dataFolder : folders) {
                        FileObject fileFolder = dataFolder.getPrimaryFile();
                        File file = FileUtil.toFile(fileFolder);

                        // filesystem
                        FileUtil.refreshFor(file);
                        // versioning
                        Runnable runnable = (Runnable) fileFolder.getAttribute("ProvidedExtensions.Refresh");
                        if (runnable != null) runnable.run();
                        
                        // indexes
                        try {
                            IndexingManager.getDefault().refreshIndex(file.toURI().toURL(), null);
                        } catch (MalformedURLException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        }

        public JMenuItem getPopupPresenter() {
            class Presenter extends JMenuItem implements DynamicMenuContent {

                public Presenter() {
                    super(ContextAction.this);
                }

                public JComponent[] getMenuPresenters() {
                    return new JComponent[] {this, null};
                }

                public JComponent[] synchMenuPresenters(JComponent[] items) {
                    return getMenuPresenters();
                }
            }
            return new Presenter();
        }
    }
}
