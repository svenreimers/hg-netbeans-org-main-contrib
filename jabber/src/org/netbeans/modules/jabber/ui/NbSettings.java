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
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber.ui;

import java.io.IOException;
import java.io.Serializable;
import org.netbeans.modules.jabber.Settings;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 *
 * @author  nenik
 */
class NbSettings extends Settings implements Serializable {

    private static NbSettings INSTANCE;

    private FileObject fo;

    public static synchronized NbSettings getDefault() {
        if (INSTANCE == null) {
            INSTANCE = new NbSettings();
            INSTANCE.fo = Repository.getDefault().getDefaultFileSystem().findResource("Services/org-netbeans-modules-jabber/config");
        }
        return INSTANCE;
    }
    
    public String getUserJid() { return (String)fo.getAttribute("jid"); }
    
    public void setUserJid(String jid) { set("jid", jid); }
    
    public String getPassword() { return (String)fo.getAttribute("pass"); }
    
    public void setPassword(String password) { set( "pass", password); }
    
    public String getResource() { return (String)fo.getAttribute("res"); }
    
    public void setResource(String resource) { set("res", resource); }

    public long getTimeout() {
        Long val = (Long)fo.getAttribute("timeout");
        if (val != null) return val.longValue();
        return 0;
    }
    
    public void setTimeout(int timeout) { set ("timeout", new Long(timeout)); }
    
    public int getAutologin() {
        Integer val = (Integer)fo.getAttribute("auto");
        if (val != null) return val.intValue();
        return 0;
    }
    
    public void setAutologin(int state) { set ("auto", new Integer(state)); }
    

    private void set(String key, Object o) {
        try {
            fo.setAttribute(key, o);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
}
