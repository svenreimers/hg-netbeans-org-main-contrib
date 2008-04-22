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
package org.netbeans.modules.spellchecker.completion;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.spellchecker.ComponentPeer;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
public class WordCompletion implements CompletionProvider {
    
    /** Creates a new instance of WordCompletion */
    public WordCompletion() {
    }

    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType == COMPLETION_QUERY_TYPE) {
            return new AsyncCompletionTask(new Query(), component);
        }
        
        return null;
    }
    
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }
    
    private static class Query extends AsyncCompletionQuery {
        
        protected void query(CompletionResultSet resultSet, Document doc, final int caretOffset) {
            Dictionary d = ComponentPeer.getDictionary(doc);
            final TokenList  l = ComponentPeer.ACCESSOR.lookupTokenList(doc);
            
            if (d != null && l != null && doc instanceof BaseDocument) {
                final BaseDocument bdoc = (BaseDocument) doc;
                final String[] prefix = new String[1];
                
                doc.render(new Runnable() {
                    public void run() {
                        try {
                            int lineStart = Utilities.getRowStart(bdoc, caretOffset);
                            
                            l.setStartOffset(lineStart);
                            
                            while (l.nextWord()) {
                                int start = l.getCurrentWordStartOffset();
                                int end   = l.getCurrentWordStartOffset() + l.getCurrentWordText().length();
                                
                                if (start < caretOffset && end >= caretOffset) {
                                    prefix[0] = l.getCurrentWordText().subSequence(0, caretOffset - start).toString();
                                    return ;
                                }
                            }
                        } catch (BadLocationException e) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                        }
                    }
                });
                
                if (prefix[0] != null) {
                    for (String proposal : d.findValidWordsForPrefix(prefix[0])) {
                        resultSet.addItem(new WordCompletionItem(caretOffset - prefix[0].length(), proposal));
                    }
                }
            }
            
            resultSet.finish();
        }
    }
    
}
