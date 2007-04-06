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

package org.netbeans.modules.jackpot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.api.jackpot.Query;
import org.netbeans.modules.jackpot.engine.PropertySheetInfo;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;

/**
 * A lightweight wrapper for inspection commands, including JackpotCommands
 * and rule files.
 */
public final class Inspection {
    private FileObject fo;
    
    Inspection(DataObject dobj) {
        this(dobj.getPrimaryFile());
    }
    
    Inspection(JackpotCommand cmd) {
        this(cmd.getFileObject());
    }
    
    Inspection(FileObject fo) {
        this.fo = fo;
    }
    
    FileObject getFileObject() {
        return fo;
    }
    
    public DataObject getDataObject() throws DataObjectNotFoundException {
        return DataObject.find(fo);
    }
    
    public String getName() {
        return fo.getName();
    }
    
    public String getInspector() {
        return (String)fo.getAttribute("inspector");
    }
    
    public void setInspector(String text) {
        setAttribute("inspector", text, true);
    }
    
    public String getTransformer() {
        return (String)fo.getAttribute("transformer");
    }
    
    public void setTransformer(String text) {
        setAttribute("transformer", text, true);
    }
    
    public String getDescription() {
        return (String)fo.getAttribute("description");
    }
    
    public void setDescription(String desc) {
        setAttribute("description", desc, true);
    }
    
    void setAttribute(String attribute, String value, boolean notify) {
        try {
            fo.setAttribute(attribute, value);
            if (notify) {
                InspectionsList list = InspectionsList.instance();
                int index = list.indexOf(this);
                list.inspectionUpdated(index);
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public String getCommand() {
        String cmd = (String)fo.getAttribute("command");
        if (cmd == null) {
            File f = FileUtil.toFile(fo);
            cmd = f != null && f.exists() ? f.getAbsolutePath() : fo.getPath();
        }
        return cmd;
    }
    
    public Query getQuery() throws Exception {
        JackpotModule module = JackpotModule.getInstance();
        String cmd = getCommand();
        if (module.isQueryScript(fo))
            return module.createScript(getInspector(), cmd);
        else
            return module.createCommand(cmd);
    }

    public JComponent getOptionsPanel() {
        JComponent panel = new JPanel();  // empty panel means no properties
        try {
            Class cls = getCommandClass();
            if (cls != null) {
                PropertySheetInfo pi = PropertySheetInfo.find(cls);
                if(pi.nonEmpty())
                    panel = pi.buildPanel();
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
        return panel;
    }
    
    public void export(File file) throws IOException {
        if (file.exists())
            file.delete();
        FileOutputStream out = new FileOutputStream(file);
        InputStream in = fo.getInputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) > 0)
            out.write(buf, 0, n);
        in.close();
        out.close();
    }

    private Class getCommandClass() {
        String className = (String)fo.getAttribute("command");
        if (className != null) {
            Class cls;
            Lookup.Result<Query> result = Lookup.getDefault ().lookup (new Lookup.Template<Query>(Query.class));
            for (Object o : result.allClasses()) { // can eliminate cast for NetBeans 6.0
                cls = (Class)o;
                if (cls.getName().equals(className))
                    return cls;
            }
            try {
                return Class.forName(className, true, getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                try {
                    // it's not a class, see if any QueryProviders recognize it
                    return getQuery().getClass();
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }
}
