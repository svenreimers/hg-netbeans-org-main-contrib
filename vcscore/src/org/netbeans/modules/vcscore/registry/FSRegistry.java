/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

/**
 * just for awhile
 * @author  Richard Gregor
 */
public class FSRegistry {
    
    /** Creates a new instance of FSRegistry */
    public FSRegistry() {
    }
    
    public static FSRegistry getDefault(){
        return new FSRegistry();
    }
    
    public void addFSRegistryListener(FSRegistryListener l){
        //
    }
    
    public FSInfo[] getRegistered(){
        return null;
    }
    
    public void unregister(FSInfo info){
        //
    }
}

