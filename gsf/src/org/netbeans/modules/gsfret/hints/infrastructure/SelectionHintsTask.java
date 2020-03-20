/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.gsfret.hints.infrastructure;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.Language;
import org.netbeans.modules.gsf.api.Hint;
import org.netbeans.modules.gsf.api.HintsProvider;
import org.netbeans.modules.gsf.api.RuleContext;
import org.netbeans.modules.gsf.spi.GsfUtilities;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.modules.gsfret.editor.semantic.ScanningCancellableTask;
import org.netbeans.napi.gsfret.source.support.SelectionAwareSourceTaskFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;

/**
 *
 */
public class SelectionHintsTask extends ScanningCancellableTask<CompilationInfo> {
    
    public SelectionHintsTask() {
    }
    
    public void run(CompilationInfo info) throws Exception {
        resume();
        
        Document doc = info.getDocument();
        if (doc == null) {
            return;
        }

        if (GsfUtilities.isCodeTemplateEditing(doc)) {
            return;
        }

        int[] range = SelectionAwareSourceTaskFactory.getLastSelection(info.getFileObject());
        
        if (range == null || range.length != 2 || range[0] == -1 || range[1] == -1) {
            return;
        }

        int start = range[0];
        int end = range[1];
        
        Language language = SuggestionsTask.getHintsProviderLanguage(doc, start);
        if (language == null) {
            return;
        }

        HintsProvider provider = language.getHintsProvider();
        assert provider != null; // getHintsProviderLanguage will return null if there's no provider
        GsfHintsManager manager = language.getHintsManager();
        if (manager == null) {
            return;
        }

        List<ErrorDescription> result = new ArrayList<ErrorDescription>();
        List<Hint> hints = new ArrayList<Hint>();
        
        if (start != end) {
            RuleContext ruleContext = manager.createRuleContext(info, language, -1, start, end);
            if (ruleContext != null) {
                provider.computeSelectionHints(manager, ruleContext, hints, Math.min(start,end), Math.max(start,end));
                for (Hint hint : hints) {
                    ErrorDescription desc = manager.createDescription(hint, ruleContext, false);
                    result.add(desc);
                }
            }
        }
        
        if (isCancelled()) {
            return;
        }
        
        HintsController.setErrors(info.getFileObject(), SelectionHintsTask.class.getName(), result);
    }
}
