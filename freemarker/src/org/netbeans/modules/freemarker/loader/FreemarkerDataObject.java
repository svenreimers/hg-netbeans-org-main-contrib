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
package org.netbeans.modules.freemarker.loader;

import java.io.IOException;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.modules.freemarker.TopLevelFreeMarkerTokenId;
import org.netbeans.modules.freemarker.MimeTypes;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.PrintCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableOpenSupport;

public class FreemarkerDataObject extends MultiDataObject {
    public FreemarkerDataObject(FileObject pf, FreemarkerDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        CloneableEditorSupport des = new SimpleES(this, getPrimaryEntry(), cookies);
        des.setMIMEType(MimeTypes.prepareCompoundMimeType(pf));
        cookies.add((Node.Cookie) des);
    }
    

    @Override
    protected Node createNodeDelegate() {
        return new FreemarkerDataNode(this, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }


    private static final class SimpleES extends DataEditorSupport
            implements OpenCookie, EditCookie, EditorCookie.Observable, PrintCookie, CloseCookie {
        /** SaveCookie for this support instance. The cookie is adding/removing 
         * data object's cookie set depending on if modification flag was set/unset. */
        private final SaveCookie saveCookie = new SaveCookie() {
            /** Implements <code>SaveCookie</code> interface. */
            public void save() throws IOException {
                SimpleES.this.saveDocument();
            }
        };

        private CookieSet set;

        /** Constructor. 
         * @param obj data object to work on
         * @param set set to add/remove save cookie from
         */
        SimpleES(DataObject obj, MultiDataObject.Entry entry, CookieSet set) {
            super(obj, new Environment(obj, entry));
            this.set = set;
        }

        /** 
         * Overrides superclass method. Adds adding of save cookie if the document has been marked modified.
         * @return true if the environment accepted being marked as modified
         *    or false if it has refused and the document should remain unmodified
         */
        @Override
        protected boolean notifyModified() {
            if (!super.notifyModified()) {
                return false;
            }

            addSaveCookie();

            return true;
        }

        /** Overrides superclass method. Adds removing of save cookie. */
        @Override
        protected void notifyUnmodified() {
            super.notifyUnmodified();

            removeSaveCookie();
        }

        @Override
        protected StyledDocument createStyledDocument(EditorKit kit) {
            StyledDocument result = super.createStyledDocument(kit);
            InputAttributes attributes = new InputAttributes();
            
            attributes.setValue(LanguagePath.get(TopLevelFreeMarkerTokenId.language()),
                                MimeTypes.MIME_TYPE_PROPERTY,
                                result.getProperty(MimeTypes.MIME_TYPE_PROPERTY),
                                true);
            
            result.putProperty(InputAttributes.class, attributes);
            
            return result;
        }

        /** Helper method. Adds save cookie to the data object. */
        private void addSaveCookie() {
            DataObject obj = getDataObject();

            // Adds save cookie to the data object.
            if (obj.getCookie(SaveCookie.class) == null) {
                set.add(saveCookie);
                obj.setModified(true);
            }
        }

        /** Helper method. Removes save cookie from the data object. */
        private void removeSaveCookie() {
            DataObject obj = getDataObject();

            // Remove save cookie from the data object.
            Cookie cookie = obj.getCookie(SaveCookie.class);

            if (cookie != null && cookie.equals(saveCookie)) {
                set.remove(saveCookie);
                obj.setModified(false);
            }
        }
        /** Nested class. Environment for this support. Extends
         * <code>DataEditorSupport.Env</code> abstract class.
         */
        private static class Environment extends DataEditorSupport.Env {
            private static final long serialVersionUID = 5451434321155443431L;

            private MultiDataObject.Entry entry;

            /** Constructor. */
            public Environment(DataObject obj, MultiDataObject.Entry entry) {
                super(obj);
                this.entry = entry;
            }

            /** Implements abstract superclass method. */
            protected FileObject getFile() {
                return entry.getFile();
            }

            /** Implements abstract superclass method.*/
            protected FileLock takeLock() throws IOException {
                return entry.takeLock();
            }

            /** 
             * Overrides superclass method.
             * @return text editor support (instance of enclosing class)
             */
            @Override
            public CloneableOpenSupport findCloneableOpenSupport() {
                return getDataObject().getCookie(SimpleES.class);
            }
        } // End of nested Environment class.

    }
    
}
