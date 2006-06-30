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
package org.netbeans.api.xmi;

import java.util.Collection;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.XmiReader;
import javax.jmi.xmi.MalformedXMIException;
import java.io.InputStream;
import java.io.IOException;

/** Base class for enhanced XMI readers.
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIReader implements XmiReader {
    //----------------
    // public methods
    //----------------

    /** Returns configuration object of this XMIReader. Any changes to the returned
     * object will have immediate effect on the XMIReader's configuration.
     */
    public abstract XMIInputConfig getConfiguration();
    
    //-------------------------------------------
    // javax.jmi.xmi.XmiReader interface methods
    //-------------------------------------------
    
    /** Standard JMI method for reading XMI documents from an InputStream.
     *
     * @param stream Input stream to be used for reading an XMI document (if
     * <code>null</code> is passed, XMIReader will try to open a connection to the
     * spefified URI.
     *
     * @param URI URI of the XMI document to be read from. If set to <code>null</code>,
     * XMIReader will not be able to resolve relative hrefs.
     * @param extent Target package extent for deserialized objects.
     * @throws IOException I/O error during reading of XMI document.
     * @throws MalformedXMIException XMI document contains invalid XMI.
     * @return Collection of outermost objects read from XMI document.
     */    
    public abstract Collection read(InputStream stream, String URI, RefPackage extent) throws IOException, MalformedXMIException;    

    /** Standard JMI method for reading XMI documents without URI specified.
     *
     * @param URI URI of the XMI document to be read. Cannot be <code>null</code>.
     * @param extent Target package extent for deserialized objects.
     * @throws IOException I/O error during reading of XMI document.
     * @throws MalformedXMIException XMI document contains invalid XMI.
     * @return Collection of outermost objects read from XMI document.
     */    
    public abstract Collection read(String URI, RefPackage extent) throws IOException, MalformedXMIException;
}
