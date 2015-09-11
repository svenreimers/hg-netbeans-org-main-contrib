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

/*
 * WS70URIManager.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;
import java.io.File;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;



/**
 *
 * @author Administrator
 */
public class WS70URIManager {

    public static final String WS70SERVERURI = "deployer:Sun:WebServer70::"; //NOI18N

    /**
     * Returns instance properties for the server instance.
     *
     * @param url the url connection string to get the instance deployment manager.
     * @return the InstanceProperties object, null if instance does not exists.
     */
    
    public static InstanceProperties getInstanceProperties(String serverLocation, String host, int port){
        InstanceProperties  instanceProperties =
                InstanceProperties.getInstanceProperties("["+serverLocation+"]"+WS70SERVERURI+host+":"+port);

        return instanceProperties;
    }
    /**
     * Create new instance and returns instance properties for the server instance.
     * 
     * @param url the url connection string to get the instance deployment manager.
     * @param username username which is used by the deployment manager.
     * @param password password which is used by the deployment manager.
     * @param displayName display name which is used by IDE to represent this 
     *        server instance.
     * @return the <code>InstanceProperties</code> object, <code>null</code> if 
     *         instance does not exists.
     * @exception InstanceCreationException when instance with same url already 
     *            registered.
     */
    public static InstanceProperties createInstanceProperties(String serverLocation, String host, String port, String user, String password, String displayName) throws InstanceCreationException {
        InstanceProperties  instanceProperties =  InstanceProperties.createInstanceProperties(
                "["+serverLocation+"]"+WS70SERVERURI+host+":"+port,
                user,
                password,displayName);
        
        return instanceProperties;
    }
    public static String getURIWithoutLocation(String uriwithlocation){
        int index = uriwithlocation.lastIndexOf("]");
        String retval = null;
        if(index!=-1){
            retval =  uriwithlocation.substring(index+1);
        }else{
            retval = uriwithlocation;
        }
        return retval;
    }
    public static String getLocation(String url){
        int index = url.lastIndexOf("]");
        String retval = null;
        if(index!=-1){
            if(url.startsWith("[")){
                retval =  url.substring(1, index);
            }else{
                retval =  url.substring(0, index);
            }
        }
        return retval;        
    }
    public static String getHostFromURI(String uri){
        int index = uri.lastIndexOf("::");
        String newUrl = null;
        if(index!=-1){
            newUrl = uri.substring(index+2);
        }else{
            newUrl = uri;
        }
        
        String[] vals = newUrl.split(":");        
        return vals[0];
        
    }
    public static String getPortFromURI(String uri){
       int index = uri.lastIndexOf("::");
        String newUrl = null;
        if(index!=-1){
            newUrl = uri.substring(index+2);
        }else{
            newUrl = uri;
        }
        
        String[] vals = newUrl.split(":");      
        return vals[1];                
    }

    public static String getUpdatedUri(String uri, String port){
        // uri is a complete connector url
        // prefix is [<serverlocation>]+WS70SERVERURI and suffix is <host>:<port>
        int index = uri.lastIndexOf("::");
        String prefix = (index!=-1) ? uri.substring(0, index+2) : "";
        String suffix = (index!=-1) ? uri.substring(index+2) : uri;
        return prefix + suffix.split(":")[0] + ":" + port;
    }
    
}
