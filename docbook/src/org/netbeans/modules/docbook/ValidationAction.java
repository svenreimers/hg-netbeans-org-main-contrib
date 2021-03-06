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
package org.netbeans.modules.docbook;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.docbook.OutputWindowStatus;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Performs the XML validation against the associated schema.
 * @author Tim Boudreau
 */
public class ValidationAction extends AbstractAction implements ContextAwareAction, LookupListener {
    private final Lookup lkp;
    private final Lookup.Result res;
    /** Creates a new instance of SetMainFileAction */
    public ValidationAction () {
        this (Utilities.actionsGlobalContext());
    }

    public ValidationAction (Lookup lkp) {
        this.lkp = lkp;
        putValue (NAME, "Validate XML");
        assert lkp != null;
        this.res = lkp.lookupResult(DataObject.class);
        resultChanged (null);
    }

    public void actionPerformed(ActionEvent e) {
        DataObject ob = (DataObject) lkp.lookup(DataObject.class);
        RequestProcessor.getDefault().post (new Validator(ob));
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new ValidationAction (actionContext);
    }

    public void resultChanged(LookupEvent ev) {
        res.allInstances();
        DataObject dob = (DataObject) lkp.lookup(DataObject.class);
        setEnabled (true); //XXX check content
    }

    private static final class Validator implements Runnable, ErrorHandler {
        private final DataObject o;
        private final OutputWindowStatus status;
        public Validator (DataObject o) {
            this.o = o;
            status = new OutputWindowStatus ("Validating " + o.getName());
        }

        public void run() {
            //XXX wrap no-doctype files in a fake doctype
            final InputSource src = DataObjectAdapters.inputSource(o);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            try {
                XMLReader reader = factory.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                reader.parse(src);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ioe);
                status.failed (ioe);
            } catch (SAXException e) {
                status.failed (e);
            } catch (ParserConfigurationException e) {
                ErrorManager.getDefault().notify (e);
                status.failed(e);
            } finally {
                status.finished("Done.", null);
            }
        }

        public void warning(SAXParseException e) throws SAXException {
            String sid = e.getSystemId();
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            String msg = e.getMessage();
            status.warn("Error: " + msg + " in " + sid + "; Line#: " + line + "; Column#: " + col + ";");
        }

        public void error(SAXParseException e) throws SAXException {
            String sid = e.getSystemId();
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            String msg = e.getMessage();
            status.warn("Error: " + msg + " in " + sid + "; Line#: " + line + "; Column#: " + col + ";");
        }

        public void fatalError(SAXParseException e) throws SAXException {
            String sid = e.getSystemId();
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            String msg = e.getMessage();
            status.warn("Error - Fatal: " + msg + " in " + sid + "; Line#: " + line + "; Column#: " + col + ";");
        }
    }
}
