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
package org.netbeans.modules.clazz;

import org.openide.util.Task;
import org.openide.util.TaskListener;
import java.lang.reflect.InvocationTargetException;
import java.io.*;
import java.util.*;

import org.openide.cookies.SourceCookie;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.openide.filesystems.FileObject;
import org.netbeans.api.java.classpath.ClassPath;

/**
 * This class overrides a few methods from ClassDataNode presenting the contents
 * as a sourceless class. It also adds .class - specific behaviour such as
 * execution and parameters property.
 *
 * @author  sdedic
 * @version 
 */
public class CompiledDataNode extends ClassDataNode {
    private final static String PROP_IS_EXECUTABLE = "isExecutable"; // NOI18N
    private final static String PROP_FILE_PARAMS = "fileParams"; // NOI18N
    private final static String PROP_EXECUTION = "execution"; // NOI18N
    private final static String EXECUTION_SET_NAME     = "Execution"; // NOI18N
    
    /** Icon bases for icon manager */
    protected final static String CLASS_BASE =
        "org/netbeans/modules/clazz/resources/class"; // NOI18N
    private final static String CLASS_MAIN_BASE =
        "org/netbeans/modules/clazz/resources/classMain"; // NOI18N
    private final static String ERROR_BASE =
        "org/netbeans/modules/clazz/resources/classError"; // NOI18N
    private final static String BEAN_BASE =
        "org/netbeans/modules/clazz/resources/bean"; // NOI18N
    private final static String BEAN_MAIN_BASE =
        "org/netbeans/modules/clazz/resources/beanMain"; // NOI18N


    /** Creates new CompiledDataNode */
    public CompiledDataNode(final CompiledDataObject obj) {
        super(obj);
    }
    
    private CompiledDataObject getCompiledDataObject() {
	return (CompiledDataObject)getDataObject();
    }

    boolean isExecutable() {
        return getCompiledDataObject().isExecutable();
    }

    protected Sheet createSheet () {
        Sheet s = super.createSheet();
        ResourceBundle bundle = NbBundle.getBundle(ClassDataNode.class);
        Sheet.Set ps = s.get(Sheet.PROPERTIES);

        ps.put(new PropertySupport.ReadOnly (
                   PROP_IS_EXECUTABLE,
                   Boolean.TYPE,
                   bundle.getString ("PROP_isExecutable"),
                   bundle.getString ("HINT_isExecutable")
               ) {
                   public Object getValue () throws InvocationTargetException {
                       return getCompiledDataObject().isExecutable() ? Boolean.TRUE : Boolean.FALSE;
                   }
               });
        return s;
    }

    protected String initialIconBase() {
        return CLASS_BASE;
    }
    
    /** Find right icon for this node. */
    protected void resolveIcons () {      
        iconResolved = true;
    }

    /**
     * Requests construction / retrieval of metadata
     */
    protected void requestResolveIcon() {
        CompiledDataObject dataObj = getCompiledDataObject();
        FileObject fo=dataObj.getPrimaryFile();
        ClassPath libs = ClassPath.getClassPath (fo, ClassPath.COMPILE);
        ClassPath source = ClassPath.getClassPath (fo, ClassPath.SOURCE);
        ClassPath exec = ClassPath.getClassPath (fo, ClassPath.EXECUTE);
        if ((libs == null || !libs.contains(fo)) && (source == null || !source.contains(fo))
            && (exec == null || !exec.contains(fo))) {
            setIconBase(ERROR_BASE);
        } else if (dataObj.isJavaBean ()) {
            if (dataObj.isExecutable ())
                setIconBase(BEAN_MAIN_BASE);
            else
                setIconBase(BEAN_BASE);
        } else if (dataObj.isExecutable ())
            setIconBase(CLASS_MAIN_BASE);
        else
            setIconBase(CLASS_BASE);
        iconResolved = true;
    }
}
