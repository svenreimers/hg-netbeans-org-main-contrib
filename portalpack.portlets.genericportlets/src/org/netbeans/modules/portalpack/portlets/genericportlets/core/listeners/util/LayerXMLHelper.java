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
package org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.InitialPageListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * This class reads Layer.xml file to get the PortletXMLChangeListener instances
 * @author satyaranjan
 */
public class LayerXMLHelper {

    private static String PORTLET_XML_CHANGE_LISTENER_FOLDER = "portalpack/listeners/portletxml";
    private static String INITIAL_PAGE_LISTENER_FOLDER = "portalpack/listeners/initial-page";
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

    private static FileObject getFolder(String listenerFolder) {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.getRoot().getFileObject(listenerFolder);
        //For NB 7.0
        //FileObject fo = FileUtil.getConfigFile(listenerFolder);
        return fo;
    }

    public static List<PortletXMLChangeListener> getRegisteredPortletXMLListeners() {
        FileObject fo = getFolder(PORTLET_XML_CHANGE_LISTENER_FOLDER);        
        if (fo == null) {
            return Collections.EMPTY_LIST;
        }
        List instanceList = new ArrayList();
        DataFolder df = DataFolder.findFolder(fo);
        if(df == null) 
            return Collections.EMPTY_LIST;
        DataObject[] childs = df.getChildren();
        DataObject dob;
        Object instanceObj;

        for (int i = 0; i < childs.length; i++) {
            dob = childs[i];
            if (dob.getPrimaryFile().isFolder()) {
                continue;
            } else {
                InstanceCookie ck = (InstanceCookie) dob.getCookie(InstanceCookie.class);
                try {
                    instanceObj = ck.instanceCreate();
                } catch (Exception ex) {
                    instanceObj = null;
                    logger.info(ex.getMessage());
                }

                if (instanceObj == null) {
                    continue;
                }

                if (instanceObj instanceof PortletXMLChangeListener) {
                    instanceList.add((PortletXMLChangeListener)instanceObj);
                } 
            }
        }
        
        return instanceList;
    }
    
     public static List<InitialPageListener> getInitialPageListeners() {
        FileObject fo = getFolder(INITIAL_PAGE_LISTENER_FOLDER);        
        if (fo == null) {
            return Collections.EMPTY_LIST;
        }
        List instanceList = new ArrayList();
        DataFolder df = DataFolder.findFolder(fo);
        if(df == null) 
            return Collections.EMPTY_LIST;
        DataObject[] childs = df.getChildren();
        DataObject dob;
        Object instanceObj;

        for (int i = 0; i < childs.length; i++) {
            dob = childs[i];
            if (dob.getPrimaryFile().isFolder()) {
                continue;
            } else {
                InstanceCookie ck = (InstanceCookie) dob.getCookie(InstanceCookie.class);
                try {
                    instanceObj = ck.instanceCreate();
                } catch (Exception ex) {
                    instanceObj = null;
                    logger.info(ex.getMessage());
                }

                if (instanceObj == null) {
                    continue;
                }

                if (instanceObj instanceof InitialPageListener) {
                    instanceList.add((InitialPageListener)instanceObj);
                } 
            }
        }
        
        return instanceList;
    }
}
