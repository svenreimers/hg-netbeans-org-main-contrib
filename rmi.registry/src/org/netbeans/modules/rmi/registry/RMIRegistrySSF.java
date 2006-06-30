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

package org.netbeans.modules.rmi.registry;

import java.net.*;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import org.openide.util.NbBundle;
import org.openide.util.WeakSet;

/**
 * @author  mryzl
 */

public class RMIRegistrySSF implements java.rmi.server.RMIServerSocketFactory {

    /** Default timeout for the socket. */
    public static final int DEFAULT_TIMEOUT = 1000;

    /** Set of all sockets. */
    private static WeakSet set = new WeakSet();

    /** Currently disabled sockets.
    */
    private static HashMap map = new HashMap();

    /** Creates new RMIRegistrySSF. */
    public RMIRegistrySSF() {
    }
  
    public ServerSocket createServerSocket(int port) throws java.io.IOException {
        Integer key = new Integer(port);
        Long timeout = (Long) map.get(key);
        if (timeout != null) {
            if (System.currentTimeMillis() < timeout.longValue()) throw new SSCanceledException();
            else map.remove(key);
        }
        ServerSocket ss = java.rmi.server.RMISocketFactory.getDefaultSocketFactory().createServerSocket(port);
        set.add(ss);
        return ss;
    }

    /**
    */

    /** Cancel socket.
    * @param port - port of the socket to be canceled
    * @param timeout - the same port could be used only after the timeou
    * @return false if there is no such socket
    */
    public static boolean cancelSocket(int port, int timeout) throws java.io.IOException {
        boolean status = false;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            ServerSocket sss = (ServerSocket) it.next();
            if (sss.getLocalPort() == port) {
                sss.close(); // don't remove it, it should be removed
                                       // automatically
                map.put(new Integer(port), new Long(System.currentTimeMillis() + timeout));                                       
                status = true;
            }
        }
        return status;
    }

    /** Exception that is used when the server socket is being canceled.
    */
    static class SSCanceledException extends java.io.IOException {
        public SSCanceledException() {
            super();
        }

        public SSCanceledException(String msg) {
            super(msg);
        }
    }
}
