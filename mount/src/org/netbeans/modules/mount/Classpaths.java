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

package org.netbeans.modules.mount;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.WeakListeners;

/**
 * Manages the global classpath corresponding to mounts.
 * @author Jesse Glick
 */
final class Classpaths implements ClassPathProvider {
    
    private final ClassPath mounts;
    private final ClassPath boot;
    
    public Classpaths() {
        boot = JavaPlatformManager.getDefault().getDefaultPlatform().getBootstrapLibraries();
        mounts = ClassPathFactory.createClassPath(new MountPath());
    }

    public ClassPath findClassPath(FileObject file, String type) {
        // XXX check that file is actually in mount list
        if (type.equals(ClassPath.SOURCE) || type.equals(ClassPath.COMPILE) || type.equals(ClassPath.EXECUTE)) {
            return mounts;
        } else if (type.equals(ClassPath.BOOT)) {
            return boot;
        } else {
            return null;
        }
    }
    
    /**
     * List me in the global path registry.
     */
    public void register() {
        GlobalPathRegistry gpr = GlobalPathRegistry.getDefault();
        gpr.register(ClassPath.SOURCE, new ClassPath[] {mounts});
        gpr.register(ClassPath.COMPILE, new ClassPath[] {mounts});
        gpr.register(ClassPath.EXECUTE, new ClassPath[] {mounts});
        gpr.register(ClassPath.BOOT, new ClassPath[] {boot});
    }
    
    private static final class MountPath implements ClassPathImplementation, ChangeListener {

        private final List/*<PropertyChangeListener>*/ listeners = new ArrayList();
        
        public MountPath() {
            MountList.DEFAULT.addChangeListener(WeakListeners.change(this, MountList.DEFAULT));
        }

        public List/*<PathResourceImplementation>*/ getResources() {
            FileObject[] roots = MountList.DEFAULT.getMounts();
            List/*<PathResourceImplementation>*/ resources = new ArrayList(roots.length);
            for (int i = 0; i < roots.length; i++) {
                try {
                    resources.add(ClassPathSupport.createResource(roots[i].getURL()));
                } catch (FileStateInvalidException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            return resources;
        }

        public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
            listeners.add(listener);
        }

        public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
            listeners.remove(listener);
        }

        private void firePropertyChange() {
            PropertyChangeListener[] ls;
            synchronized (this) {
                if (listeners.isEmpty()) {
                    return;
                }
                ls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[listeners.size()]);
            }
            PropertyChangeEvent ev = new PropertyChangeEvent(this, ClassPathImplementation.PROP_RESOURCES, null, null);
            for (int i = 0; i < ls.length; i++) {
                ls[i].propertyChange(ev);
            }
        }

        public void stateChanged(ChangeEvent e) {
            firePropertyChange();
        }
        
    }
    
}
