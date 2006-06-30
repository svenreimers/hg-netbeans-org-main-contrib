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
 * __NAME__.java  --  client side implementation
 *
 * Created on __DATE__, __TIME__ for __ORB_NAME__
 * with __CLIENT_BINDING__ binding.
 */

package Templates.CORBA;

__CLIENT_IMPORT__
/**
 *
 * @author  __USER__
 * @version
 */
public class ClientMain {

    /** Creates new __NAME__ */
    public ClientMain () {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {

        __SETTINGS_ORB_PROPERTIES__
        __ORB_CLIENT_INIT__

        __ORB_CLIENT_BINDING__
    }

}
