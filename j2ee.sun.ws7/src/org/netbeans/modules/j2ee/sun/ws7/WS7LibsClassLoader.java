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

/*
 * WS7LibsClassLoader.java
 */

package org.netbeans.modules.j2ee.sun.ws7;

import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.AllPermission;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;



public class WS7LibsClassLoader extends URLClassLoader {

    public WS7LibsClassLoader() throws MalformedURLException, RuntimeException {
        super(new URL[0]);
    }

    public WS7LibsClassLoader( ClassLoader _loader) throws MalformedURLException, RuntimeException {
        super(new URL[0], _loader);
    }
       
        
    public void addURL(File f) throws MalformedURLException, RuntimeException {
        if (f.isFile()){
            addURL(f.toURL());
        }
    }
    
    public Class loadClass(String  name)throws ClassNotFoundException{    
        return super.loadClass(name);
    }
    protected Class findClass(String  name) throws ClassNotFoundException{    
        return super.findClass(name);
    }  

    protected PermissionCollection getPermissions(CodeSource _cs) {
        Permissions p = new Permissions();
        p.add(new AllPermission());
        return p;
    }

}
