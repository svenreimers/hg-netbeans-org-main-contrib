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

package org.netbeans.modules.jemmysupport;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;

/** Task intended to run class in IDE's JVM. It gets
 * IDE's system class loader, adds given classpath and invokes main method
 * of given class.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class RunInternallyTask extends Task {
    
    /** Execution classpath. */
    protected Path classpath;
    /** Fully qualified name of class to be run. */
    protected String classname;
    /** Property to store arguments. */
    private CommandlineJava cmdl = new CommandlineJava();
    
    
    /** Sets classpath property.
     * @param classpath new value
     */
    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }
    
    /** Creates classpath
     * @return created path
     */
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return classpath.createPath();
    }
    
    /** Sets classname property.
     * @param classname fully-qualified class name
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }
    
    /** Creates main method argument.
     * @param arg arguments to be passed to main method.
     * @return created argument
     */
    public Commandline.Argument createArg() {
        return cmdl.createArgument();
    }
    
    
    /** Gets IDE's system class loader, adds given classpath and invokes main
     * method of given class.
     * @throws BuildException when something's wrong
     */
    public void execute() throws BuildException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            // find NetBeans SystemClassLoader in threads hierarchy
            ThreadGroup tg = Thread.currentThread().getThreadGroup();
            ClassLoader systemClassloader = orig;
            while(!systemClassloader.getClass().getName().endsWith("SystemClassLoader")) { // NOI18N        
                tg = tg.getParent();
                if(tg == null) {
                    throw new BuildException("NetBeans SystemClassLoader not found!");
                }
                Thread[] list = new Thread[tg.activeCount()];
                tg.enumerate(list);
                systemClassloader = list[0].getContextClassLoader();
            }
            URL[] urls = classpathToURL(classpath);
            URLClassLoader testClassLoader = new TestClassLoader(urls, systemClassloader);
            Thread.currentThread().setContextClassLoader(testClassLoader);
            Class classToRun = testClassLoader.loadClass(this.classname);
            Method method = classToRun.getDeclaredMethod("main", new Class[] {String[].class}); // NOI18N
            String[] args = cmdl.getJavaCommand().getArguments();
            method.invoke(null, new Object[] {args});
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause != null) {
                cause.printStackTrace();
                throw new BuildException(cause.getClass().getName()+": "+
                                         cause.getMessage()+" at "+
                                         e.getCause().getStackTrace()[0], cause);
            } else {
                e.printStackTrace();
                throw new BuildException(e.getClass().getName()+": "+e.getMessage(), e);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
    
    /**
     * Converts given classpath to an array of URLs.
     * @param classpath classpath to be converted
     * @return array of URLs
     * @throws MalformedURLException thrown when a path is wrong
     */
    public static URL[] classpathToURL(Path classpath) throws MalformedURLException {
        String[] list = classpath.list();
        URL[] urls = new URL[list.length];
        for(int i=0;i<list.length;i++) {
            urls[i] = new File(list[i]).toURI().toURL();
        }
        return urls;
    }
    
    /** Classloder with overriden getPermissions method because it doesn't
     * have sufficient permissions when run from IDE.
     */
    private static class TestClassLoader extends URLClassLoader {
        
        public TestClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        
        protected PermissionCollection getPermissions(CodeSource codesource) {
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            permissions.setReadOnly();
            
            return permissions;
        }
        
        /** This has to be here because classes cannot be found if modules like
         * xtest, jemmy, jellytools are installed and that's why loaded by a
         * different classloader.
         */
        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.netbeans.jemmy") || name.startsWith("org.netbeans.jellytools") || // NOI18N
                name.startsWith("org.netbeans.junit")) { // NOI18N
                //System.out.println("CLASSNAME="+name);
                // Do not proxy to parent!
                Class c = findLoadedClass(name);
                if (c != null) return c;
                c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            } else {
                return super.loadClass(name, resolve);
            }
        }
    }
}