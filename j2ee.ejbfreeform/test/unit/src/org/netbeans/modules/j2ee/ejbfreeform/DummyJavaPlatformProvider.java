/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.java.platform.JavaPlatformProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.modules.SpecificationVersion;

/**
 * Testing Java platform provider, just enough to check ClassPath.BOOT usage.
 * @author Jesse Glick
 */
public final class DummyJavaPlatformProvider implements JavaPlatformProvider {
    
    private final JavaPlatform jdk14 = new DummyJavaPlatform("1.4");
    private final JavaPlatform jdk15 = new DummyJavaPlatform("1.5");
    
    /** Default constructor for lookup. */
    public DummyJavaPlatformProvider() {}
    
    public JavaPlatform getDefaultPlatform() {
        return jdk15;
    }
    
    public JavaPlatform[] getInstalledPlatforms() {
        return new JavaPlatform[] {
            jdk14,
            jdk15,
        };
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {}
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {}
    
    private static final class DummyJavaPlatform extends JavaPlatform {
        
        private final String spec;
        private ClassPath bootcp;
        
        public DummyJavaPlatform(String spec) {
            this.spec = spec;
        }
        
        public FileObject findTool(String toolName) {
            return null;
        }
        
        public ClassPath getBootstrapLibraries() {
            if (bootcp == null) {
                try {
                    bootcp = ClassPathSupport.createClassPath(new URL[] {
                        // This file does not really have to exist - just needs to have a well-known location.
                        // Cf. ClasspathsTest.
                        new URL("jar:file:/c:/java/" + spec + "/jre/lib/rt.jar!/"),
                    });
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
            return bootcp;
        }
        
        public String getDisplayName() {
            return "JDK " + spec;
        }
        
        public Collection getInstallFolders() {
            return Collections.EMPTY_SET;
        }
        
        public List getJavadocFolders() {
            return Collections.EMPTY_LIST;
        }
        
        public Map getProperties() {
            return Collections.EMPTY_MAP;
        }
        
        public ClassPath getSourceFolders() {
            return ClassPathSupport.createClassPath(new FileObject[0]);
        }
        
        public Specification getSpecification() {
            return new Specification("j2se", new SpecificationVersion(spec));
        }
        
        public ClassPath getStandardLibraries() {
            return ClassPathSupport.createClassPath(new FileObject[0]);
        }
        
        public String getVendor() {
            return "test";
        }
        
    }
    
}
