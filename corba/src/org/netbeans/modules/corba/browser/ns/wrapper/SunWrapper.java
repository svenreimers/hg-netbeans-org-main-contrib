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
 * SunWrapper.java
 * Sun JDK 1.2 Wrapper
 * Created on November 6, 2000, 3:42 PM
 */

package org.netbeans.modules.corba.browser.ns.wrapper;

/**
 *
 * @author  tzezula
 * @version
 */
public class SunWrapper extends AbstractWrapper {

    /** Creates new SunWrapper */
    public SunWrapper() {
    }


    public void run () {
            java.util.Properties properties = new java.util.Properties ();
            properties.put ("org.omg.CORBA.ORBClass","com.sun.CORBA.iiop.ORB");
            try {

            Class orbClass = Class.forName ("org.omg.CORBA.ORB");
            java.lang.Object[] params = new Object[]{new String[0],properties};
            java.lang.reflect.Method m = orbClass.getMethod ("init", new Class[]{params[0].getClass(),params[1].getClass()});
            Object orb = m.invoke (null,params);
            
//            com.sun.CORBA.iiop.ORB orb = (com.sun.CORBA.iiop.ORB) org.omg.CORBA.ORB.init (new String[0],env);

            Class transientNameServiceClass = Class.forName ("com.sun.CosNaming.TransientNameService");
            java.lang.reflect.Constructor c = transientNameServiceClass.getConstructor (new Class[] {org.omg.CORBA.ORB.class});
            Object transientNameService = c.newInstance (new java.lang.Object[]{orb});
            
//            com.sun.CosNaming.TransientNameService tns = new com.sun.CosNaming.TransientNameService (orb);
            
            m = transientNameServiceClass.getMethod ("initialNamingContext",new Class[0]);
            java.lang.Object namingContext = m.invoke (transientNameService, new Object[0]);
            
//            org.omg.CosNaming.NamingContext namingContext = tns.initialNamingContext();
            
            params = new java.lang.Object[]{namingContext};
            m = orbClass.getMethod ("object_to_string", new Class[]{org.omg.CORBA.Object.class});
            this.ior = (String) m.invoke (orb,params);
            
//            this.ior = orb.object_to_string (namingContext);
            
            properties.put ("NameService",this.ior);
            
            Class bootstrapServerClass = Class.forName ("com.sun.CosNaming.BootstrapServer");
            params = new Object[] {orb,new Integer (this.port),null,properties};
            c = bootstrapServerClass.getConstructor (new Class[]{params[0].getClass(),Integer.TYPE,java.io.File.class,params[3].getClass()});
            java.lang.Object bootstrapServer = c.newInstance (params);
            
//            com.sun.CosNaming.BootstrapServer bserver = new com.sun.CosNaming.BootstrapServer (orb, (int) this.port, null, properties);
            
                try {
                    m = bootstrapServerClass.getMethod ("start", new Class[0]);
                    m.invoke (bootstrapServer, new java.lang.Object[0]);
//                    bserver.start();
                    synchronized (this) {
                        this.state = INITIALIZED;
                        this.notify();
                    }
                    java.lang.Object sync = new java.lang.Object();
                    synchronized (sync) {
                        try {
                            sync.wait();
                        }catch (InterruptedException ie) {}
                    }
                
                }catch (org.omg.CORBA.SystemException se) {
                    synchronized (this) {
                        this.state = ERROR;
                        this.notify();
                    }
                }
            

            }catch (Exception e) {
                synchronized (this) {
                    this.state = ERROR;
                    this.notify();
                }
            }
    }
        
}
