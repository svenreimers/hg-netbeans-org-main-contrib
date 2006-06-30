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

package org.netbeans.api.web.dd;
import org.netbeans.api.web.dd.common.*;
/**
 * Generated interface for MessageDestinationRef element.
 *
 *<p><b><font color="red"><em>Important note: Do not provide an implementation of this interface unless you are a DD API provider!</em></font></b>
 *</p>
 * @deprecated Use the API for web module deployment descriptor in j2ee/ddapi module.
 */
public interface MessageDestinationRef extends CommonDDBean, DescriptionInterface {
        /** Setter for message-destination-ref-name property.
         * @param value property value
         */
	public void setMessageDestinationRefName(java.lang.String value);
        /** Getter for message-destination-ref-name property.
         * @return property value 
         */
	public java.lang.String getMessageDestinationRefName();
        /** Setter for message-destination-type property.
         * @param value property value
         */
	public void setMessageDestinationType(java.lang.String value);
        /** Getter for message-destination-type property.
         * @return property value 
         */
	public java.lang.String getMessageDestinationType();
        /** Setter for message-destination-usage property.
         * @param value property value
         */
	public void setMessageDestinationUsage(java.lang.String value);
        /** Getter for message-destination-usage property.
         * @return property value 
         */
	public java.lang.String getMessageDestinationUsage();
        /** Setter for message-destination-link property.
         * @param value property value
         */
	public void setMessageDestinationLink(java.lang.String value);
        /** Getter for message-destination-link property.
         * @return property value 
         */
	public java.lang.String getMessageDestinationLink();

}
