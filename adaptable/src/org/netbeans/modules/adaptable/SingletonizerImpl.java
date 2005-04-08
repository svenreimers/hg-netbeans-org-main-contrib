/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.adaptable;

import java.util.Collections;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import org.netbeans.api.adaptable.*;

/** A bunch of utility methods that support making functionality
 * "singletonized". There is a more instances, but only one object
 * that does the functionality (like represnted object vs. look).
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerImpl extends Object 
implements ProviderImpl, javax.swing.event.ChangeListener {
    private Class[] classes;
    /** Keeps track of existing lookups. Type of Object to LkpReference<Lkp> */
    private java.util.HashMap lookups = new java.util.HashMap ();
    
    
    /** We control the life cycle */
    private SingletonizerImpl (Class[] classes) {
        this.classes = classes;
    }
    
    /**
     * Creates an Adaptor based on that support sinletonization.
     * @param classes the interfaces that we support
     * @param impl provider of the functionality
     */
    public static Adaptor create (Class[] classes, org.netbeans.spi.adaptable.Singletonizer impl) {
        for (int i = 0; i < classes.length; i++) {
            if (!classes[i].isInterface()) {
                throw new IllegalArgumentException ("Works only on interfaces: " + classes[i].getName ()); // NOI18N
            }
        }
        SingletonizerImpl single = new SingletonizerImpl (classes);
        try {
            impl.addChangeListener (single);
        } catch (java.util.TooManyListenersException ex) {
            throw new IllegalStateException ("addChangeListener should not throw exception: " + impl); // NOI18N
        }
        return Accessor.API.createAspectProvider(single, impl);
    }
    
    //
    // Implementation of ProviderImpl
    //
   
    public Adaptable createLookup(Object obj, Object data) {
        java.lang.ref.Reference ref = (java.lang.ref.Reference)lookups.get (obj);
        Lkp lkp = ref == null ? null : (Lkp)ref.get ();
        if (lkp == null) {
            lkp = new Lkp (obj, (org.netbeans.spi.adaptable.Singletonizer)data, classes);
            ref = new java.lang.ref.WeakReference (lkp);
            lookups.put (obj, ref);
        }
        return lkp;
    }
    
    public void stateChanged (javax.swing.event.ChangeEvent e) {
        /*
        if (e.getSource () instanceof Impl) {
            // refresh all of them
            java.util.Iterator it = lookups.values ().iterator ();
            while (it.hasNext ()) {
                LkpReference ref = (LkpReference)it.next ();
                Lkp lkp = (Lkp)ref.get ();
                if (lkp != null) {
                    lkp.update ();
                }
            }
        } else {
            LkpReference ref = (LkpReference)lookups.get (e.getSource ());
            if (ref == null) {
                return;
            }
            Lkp lkp = (Lkp)ref.get ();
            if (lkp != null) {
                lkp.update ();
            }
        }
         */
    }
    
    //
    // The Lookup that points to a represented object
    //
    
    private static final class Lkp 
    implements Adaptable, java.lang.reflect.InvocationHandler {
        final Object obj;
        final org.netbeans.spi.adaptable.Singletonizer impl;
        final Object proxy; 
        /** array of 0/1 for each class in impl.classes to identify the state 
         * whether it should be enabled or not */
        private byte[] enabled;
        
        public Lkp (Object obj, org.netbeans.spi.adaptable.Singletonizer impl, Class[] classes) {
            this.obj = obj;
            this.impl = impl;
            this.proxy = java.lang.reflect.Proxy.newProxyInstance(
                getClass ().getClassLoader (), 
                classes,
                this
            );
        }
        
        public Object lookup(Class clazz) {
            if (impl.isEnabled (clazz) && clazz.isInstance(proxy)) {
                return proxy;
            }
            return null;
        }
        
        /** Called when one wants an interface provided by this singletonizer
         */
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
            if (isEnabled (method.getDeclaringClass ())) {
                return impl.invoke(obj, method, args);
            } else {
                throw new IllegalStateException ("Method " + method + " cannot be invoked when " + method.getDeclaringClass ());
            }
        }
        
        /** Checks whether a class is enabled or not */
        final boolean isEnabled (Class clazz) {
            Class[] allSupportedClasses = proxy.getClass().getInterfaces ();
            if (enabled == null) {
                enabled = new byte[(allSupportedClasses.length + 7) / 8];
                int offset = 1;
                int index = 0;
                for (int i = 0; i < allSupportedClasses.length; i++) {
                    if (impl.isEnabled (allSupportedClasses[i])) {
                        enabled[index] |= offset;
                    }
                    if (offset == 128) {
                        index++;
                        offset = 1;
                    } else {
                        offset = offset << 1;
                    }
                }
            }

            for (int i = 0; i < allSupportedClasses.length; i++) {
                if (clazz.isAssignableFrom (allSupportedClasses[i])) {
                    int index = i / 8;
                    int offset = 1 << (i % 8);
                    if ((enabled[index] & offset) != 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    } // end of Lkp
}
