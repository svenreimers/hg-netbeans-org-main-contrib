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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.java.additional.refactorings.extractmethod;

import javax.swing.Action;
import org.netbeans.modules.java.additional.refactorings.*;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class ExtractMethodAction extends JavaRefactoringGlobalAction {

    public ExtractMethodAction() {
        super (NbBundle.getMessage(ExtractMethodAction.class,
                "LBL_EXTRACT_METHOD"), null); //NOI18N
        putValue (Action.SHORT_DESCRIPTION,
                NbBundle.getMessage(ExtractMethodAction.class,
                "DESC_EXTRACT_METHOD")); //NOI18N
    }

    public void performAction(final Lookup context) {
        EditorCookie ec = super.getTextComponent(context.lookup(Node.class));
        if (ec != null) {
            new TextComponentRunnable(ec) {
                @Override
                protected RefactoringUI createRefactoringUI(TreePathHandle selectedElement,int startOffset,int endOffset, CompilationInfo info) {
                    return new ExtractMethodUI(context, selectedElement, startOffset, endOffset, info);
                }
            }.run();
        }
    }

    protected boolean enable(Lookup context) {
        EditorCookie ck = super.getTextComponent(context.lookup(Node.class));
        return ck != null && ck.getOpenedPanes().length > 0 && ck.getOpenedPanes()[0].getSelectionStart() != ck.getOpenedPanes()[0].getSelectionEnd();
    }
}
