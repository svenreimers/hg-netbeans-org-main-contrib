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

/*
 * MQOperation.java
 *
 * Created on December 14, 2006, 11:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.wsdlextensions.mq;



/**
 *
 * @author rchen
 */
public interface MQOperation extends MQComponent {
    public static final String IN_ONLY = "http://www.w3.org/2004/08/wsdl/in-only";
    
    // common
    public static final String ATTR_QUEUENAME = "queueName";
    public static final String ATTR_TRANSACTION = "transaction";
    
    public String getQueueName();
    public void setQueueName(String val);
    
    public String getTransaction();
    public void setTransaction(String val);
    
}
