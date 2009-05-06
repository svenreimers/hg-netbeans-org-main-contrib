/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.netbeans.modules.editor.NbEditorUtilities;
import javax.swing.text.Document;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.editor.BaseAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.scala.editing.ScalaParserResult;
import org.netbeans.modules.scala.editing.actions.FixImportsHelper.ImportCandidate;
import org.openide.util.Exceptions;

/**
 *
 * @author schmidtm
 */
public class FixImportsAction extends BaseAction implements Runnable {

    private final Logger LOG = Logger.getLogger(FixImportsAction.class.getName());
    Document doc = null;
    private FixImportsHelper helper = new FixImportsHelper();

    public FixImportsAction() {
        super(NbBundle.getMessage(FixImportsAction.class, "fix-scala-imports"), 0); // NOI18N
    }

    @Override
    public boolean isEnabled() {
        // here should go all the logic whether there are in fact missing 
        // imports we're able to fix.
        return true;
    }

    public void actionPerformed(ActionEvent evt, JTextComponent comp) {
        LOG.log(Level.FINEST, "actionPerformed(final JTextComponent comp)");

        assert comp != null;
        doc = comp.getDocument();

        if (doc != null) {
            RequestProcessor.getDefault().post(this);
        }
    }

    public void run() {
        DataObject dob = NbEditorUtilities.getDataObject(doc);

        if (dob == null) {
            LOG.log(Level.FINEST, "Could not get DataObject for document");
            return;
        }

        final List<String> missingNames = new ArrayList<String>();

        FileObject fo = dob.getPrimaryFile();
        try {
            Source source = Source.create(fo);
            // FIXME can we move this out of task (?)
            ParserManager.parse(Collections.singleton(source), new UserTask() {

                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ScalaParserResult pResult = (ScalaParserResult) resultIterator.getParserResult();
                    if (pResult != null) {
                        List<? extends Error> errors = pResult.getDiagnostics();
                        if (errors == null) {
                            LOG.log(Level.FINEST, "Could not get list of errors");
                            return;
                        }

                        // loop over the list of errors, remove duplicates and
                        // populate list of missing imports.

                        for (Error error : errors) {
                            String missingClassName = FixImportsHelper.getMissingClassName(error.getDescription());
                            if (missingClassName != null) {
                                if (!missingNames.contains(missingClassName)) {
                                    missingNames.add(missingClassName);
                                }
                            }
                        }
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

        // go over list of missing imports, fix it - if there is only one 
        // candidate or populate choosers input list.

        Map<String, List<FixImportsHelper.ImportCandidate>> multipleCandidates = new HashMap<String, List<FixImportsHelper.ImportCandidate>>();

        for (String name : missingNames) {
            List<ImportCandidate> importCandidates = helper.getImportCandidate(fo, name);

            if (importCandidates.isEmpty()) {
                // nothing to import
                return;
            }

            int size = importCandidates.size();

            if (size == 1) {
                helper.doImport(fo, importCandidates.get(0).getFqnName());
            } else {
                LOG.log(Level.FINEST, "Adding to multipleCandidates: " + name);
                multipleCandidates.put(name, importCandidates);
            }
        }

        // do we have multiple candidate? In this case we need to present a
        // chooser

        List<String> listToFix = null;

        if (!multipleCandidates.isEmpty()) {
            LOG.log(Level.FINEST, "multipleCandidates.size(): " + multipleCandidates.size());
            listToFix = presentChooser(multipleCandidates);
        }

        if (listToFix != null && !listToFix.isEmpty()) {
            LOG.log(Level.FINEST, "listToFix.size(): " + listToFix.size());
            for (String fqn : listToFix) {
                helper.doImport(fo, fqn);
            }
        }

        return;
    }

    private List<String> presentChooser(Map<String, List<FixImportsHelper.ImportCandidate>> multipleCandidates) {
        LOG.log(Level.FINEST, "presentChooser()");
        List<String> result = new ArrayList<String>();
        ImportChooserInnerPanel panel = new ImportChooserInnerPanel();

        panel.initPanel(multipleCandidates);

        DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(FixImportsAction.class, "FixImportsDialogTitle")); //NOI18N
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);

        d.setVisible(true);
        d.setVisible(false);
        d.dispose();

        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            result = panel.getSelections();
        }

        return result;
    }
}
