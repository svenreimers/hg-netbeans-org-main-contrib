/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode.test;

import org.openide.nodes.Node;

/**
 * Simply implementation of MONodeEnhancer that returns
 * hardcoded strings from getURL and toString().
 * @author David Strupl
 */
public class MONodeEnhancerImpl implements MONodeEnhancer {
    
    /** Creates a new instance of MONodeEnhancerImpl */
    public MONodeEnhancerImpl(Node n) {
    }
    
    /**
     * This method is implementation for a method from 
     * interface MONodeEnhancer.
     */
    public String getURL() {
        return "http://www.netbeans.org/";
    }
    
    /**
     * Return something user can read.
     */
    public String toString() {
        return "MONodeEnhancerImpl found!";
    }
}