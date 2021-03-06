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

package org.netbeans.modules.tasklist.html;


import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.openide.util.Utilities;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

import org.netbeans.modules.html.*;

import org.w3c.tidy.*;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.client.SuggestionAgent;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;


/**
 * This class lists problems in HTML documents (based on a
 * doc scan by the Tidy utility)
 * <p>
 *
 * @author Tor Norbye
 */
public class TidySuggester extends DocumentSuggestionProvider
    implements ErrorReporter  {

    final private static String TYPE = "nb-html-errors"; // NOI18N
    private SuggestionContext env;

    public String getType() {
        return TYPE;
    }
    
    static boolean isHTML(DataObject dobj) {
         // XXX instanceof not good - I've heard data object
         // instancing like this is going away. Look for
         // some kind of HTML related cookie instead?
         return dobj instanceof HtmlDataObject;
    }

    static boolean isJSP(DataObject dobj) {
        String file = dobj.getPrimaryFile().getNameExt();
        return file.endsWith(".jsp") || // NOI18N
            file.endsWith(".JSP") || // NOI18N
            // There are several data objects in web/core/.../jsploader
            // so just look for the jsploader package instead of
            // and actual classname
            (dobj.getClass().getName().indexOf("jsploader") != -1); // NOI18N
    }

    static boolean isXML(DataObject dobj) {
        String file = dobj.getPrimaryFile().getNameExt();
        return file.endsWith(".xml") || // NOI18N
            file.endsWith(".XML") || // NOI18N
            (dobj.getClass().getName().indexOf("XMLDataObject") != -1); // NOI18N
    }
                         
    public List scan(SuggestionContext env) {

        DataObject dobj = null;
        try {
            dobj = DataObject.find(env.getFileObject());
        } catch (DataObjectNotFoundException e) {
            return null;
        }

        // XXX instanceof not good - I've heard data object
         // instancing like this is going away. Look for
         // some kind of HTML related cookie instead?
         boolean isHTML = isHTML(dobj);
         boolean isJSP = false;
         boolean isXML = false;
         if (!isHTML) {
             isJSP = isJSP(dobj);
             if (!isJSP) {
                 isXML = isXML(dobj);
             }
         }
         if (!(isHTML || isJSP || isXML)) {
             return null;
         }
        SuggestionManager manager = SuggestionManager.getDefault();
        
        parseTasks = null;
        parseObject = dobj;
        if (manager.isEnabled(TYPE)) {
            InputStream input = null;
            try {
                input = env.getFileObject().getInputStream();
            } catch (FileNotFoundException e) {
                return null;
            }

            if (tidy == null) {
                tidy = new Tidy();
            }
            tidy.setOnlyErrors(true);
            tidy.setShowWarnings(true);
            tidy.setQuiet(true);
            // XXX Apparently JSP pages (at least those involving
            // JSF) need XML handling in order for JTidy not to choke on them
            tidy.setXmlTags(isXML || isJSP);

            PrintWriter output = new ReportWriter(this);
            tidy.setErrout(output);
            // Where do I direct its output? If it really obeys
            // setQuiet(true) it shouldn't matter...
            tidy.parse(input, System.err);
        }
        return parseTasks;
    }

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;

    /** List being built during a scan */
    private List parseTasks = null;
    private DataObject parseObject = null;

    public void reportError(int line, int col, boolean error, String message) {
        //System.err.println("reportError(" + line + ", " + col + ", " + error + ", " + message + ")");
        
        SuggestionManager manager = SuggestionManager.getDefault();
        SuggestionAgent s = manager.createSuggestion(TYPE,
                                                message,
                                                null,
                                                this);
        if (line != -1) {
            Line l = TLUtils.getLineByNumber(parseObject, line);
            s.setLine(l);
        }
        if (error) {
            Image taskIcon = Utilities.loadImage("org/netbeans/modules/tasklist/html/error.gif"); // NOI18N
            s.setIcon(taskIcon);
            s.setPriority(SuggestionPriority.HIGH);
        }
        if (parseTasks == null) {
            parseTasks = new ArrayList(30);
        }
        parseTasks.add(s.getSuggestion());
    }

    private Object request = null;
    private Tidy tidy = null;
}
