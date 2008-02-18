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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.wsdlextensions.smtp.impl;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.wsdlextensions.smtp.SMTPComponent;
import org.netbeans.modules.wsdlextensions.smtp.SMTPQName;
import org.netbeans.modules.xml.wsdl.model.spi.GenericExtensibilityElement;
import org.w3c.dom.Element;

/**
* @author Sainath Adiraju
*/
public abstract class SMTPComponentImpl extends GenericExtensibilityElement implements SMTPComponent {
    public SMTPComponentImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    protected String getNamespaceURI() {
        return SMTPQName.SMTP_NS_URI;
    }
}
