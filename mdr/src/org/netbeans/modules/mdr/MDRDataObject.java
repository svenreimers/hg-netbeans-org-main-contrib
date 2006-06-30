/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.mdr;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;

/**
 *
 * @author  mmatula
 * @version
 */
public class MDRDataObject extends MultiDataObject {
    private final String name;
    private final MDRDescriptor descriptor;
    
    public MDRDataObject(FileObject primaryFile, MultiFileLoader fl) throws DataObjectExistsException {
        super(primaryFile, fl);

        String fileName = primaryFile.getName();
        int pos = fileName.indexOf('[');
        int pos2 = fileName.indexOf(']');
        
        this.name = fileName.substring(0, pos);
        String className = fileName.substring(pos + 1, pos2).replace('-', '.');
        Map attributes = new HashMap();

        String attrName;
        
        for (Enumeration en = primaryFile.getAttributes(); en.hasMoreElements();) {
            attrName = (String) en.nextElement();
            attributes.put(attrName, resolveTags(primaryFile.getAttribute(attrName)));
        }
        
        this.descriptor = new MDRDescriptor(className, attributes);
    }
    
    public MDRDescriptor getDescriptor() {
        return descriptor;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isCopyAllowed() {
        return false;
    }
    
    public boolean isDeleteAllowed() {
        return false;
    }
    
    public boolean isMoveAllowed() {
        return false;
    }
    
    public boolean isRenameAllowed() {
        return false;
    }
    
    public boolean isShadowAllowed() {
        return false;
    }
    
    public Node createNodeDelegate() {
        DataNode dataNode = (DataNode) super.createNodeDelegate();
        dataNode.setIconBase("/org/netbeans/modules/mdr/resources/repository");
        return dataNode;
    }
    
    private static final String TAG_START = "{";
    private static final String TAG_END = "}";
    private static final String TAG_BODY_FOLDER = "folder.";
    
    private Object resolveTags(Object attrValue) {
        if (attrValue instanceof String) {
            String value = (String) attrValue;
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "tag: " + value);
            StringBuffer result = new StringBuffer(value.length());
            int pos;
            int ppos = 0;
            
            while ((pos = value.indexOf(TAG_START, ppos)) > -1) {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "tag found at pos: " + pos);
                result.append(value.substring(ppos, pos));
                ErrorManager.getDefault().log("temp. result: " + result);
                ppos = value.indexOf(TAG_END, pos);
                ErrorManager.getDefault().log("tag end found at: " + ppos);
                if (ppos == -1) {
                    ppos = pos;
                    break;
                }
                result.append(getTagValue(value.substring(pos + 1, ppos)));
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "added tag value, temp. result: " + result);
                ppos++;
            }
            
            result.append(value.substring(ppos));
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "finished, result: " + result);
            return result.toString();
        } else {
            return attrValue;
        }
    }
    
    private String getTagValue(String tagName) {
        ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "replacing tag: " + tagName);
        if (tagName.startsWith(TAG_BODY_FOLDER)) {
            String folder = tagName.substring(TAG_BODY_FOLDER.length()).replace('\\', '/');
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "found folder: " + folder);
            String resultFolder = System.getProperty("netbeans.user") + "/var/cache/" + folder;
            try {
                File result = new File(resultFolder);
                result.mkdirs();
                return resultFolder;
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
                return "";
            }
        } else {
            return TAG_START + tagName + TAG_END;
        }
    }
}
