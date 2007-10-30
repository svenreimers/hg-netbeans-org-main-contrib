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
package org.netbeans.napi.gsfret.source.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Registry;
import org.netbeans.modules.gsf.LanguageRegistry;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 * This file is originally from Retouche, the Java Support 
 * infrastructure in NetBeans. I have modified the file as little
 * as possible to make merging Retouche fixes back as simple as
 * possible. 
 *
 *
 * @author Jan Lahoda
 */
class OpenedEditors implements ChangeListener, PropertyChangeListener {

    private List<JTextComponent> visibleEditors = new ArrayList<JTextComponent>();
    private Map<JTextComponent, FileObject> visibleEditors2Files = new HashMap<JTextComponent, FileObject>();
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    private static OpenedEditors DEFAULT;

    private OpenedEditors() {
        Registry.addChangeListener(this);
    }

    public static synchronized OpenedEditors getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new OpenedEditors();
        }

        return DEFAULT;
    }

    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    private void fireChangeEvent() {
        ChangeEvent e = new ChangeEvent(this);
        List<ChangeListener> listenersCopy = null;

        synchronized (this) {
            listenersCopy = new ArrayList(listeners);
        }

        for (ChangeListener l : listenersCopy) {
            l.stateChanged(e);
        }
    }

    public synchronized List<JTextComponent> getVisibleEditors() {
        return Collections.unmodifiableList(visibleEditors);
    }

    public synchronized Collection<FileObject> getVisibleEditorsFiles() {
        return Collections.unmodifiableCollection(visibleEditors2Files.values());
    }

    public synchronized void stateChanged(ChangeEvent e) {
        for (JTextComponent c : visibleEditors) {
            c.removePropertyChangeListener(this);
            visibleEditors2Files.remove(c);
        }

        visibleEditors.clear();

        JTextComponent editor = Registry.getMostActiveComponent();

        if (editor instanceof JEditorPane && LanguageRegistry.getInstance().isSupported((((JEditorPane) editor).getContentType()))) {
            visibleEditors.add(editor);
        }

        for (JTextComponent c : visibleEditors) {
            c.addPropertyChangeListener(this);
            visibleEditors2Files.put(c, getFileObject(c));
        }

        fireChangeEvent();
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        JTextComponent c = (JTextComponent) evt.getSource();
        FileObject originalFile = visibleEditors2Files.get(c);
        FileObject nueFile = getFileObject(c);

        if (originalFile != nueFile) {
            visibleEditors2Files.put(c, nueFile);
            fireChangeEvent();
        }
    }

    static FileObject getFileObject(JTextComponent pane) {
        DataObject file = (DataObject) pane.getDocument().getProperty(Document.StreamDescriptionProperty);
        
        if (file != null) {
            return file.getPrimaryFile();
        }

        return null;
    }

}
