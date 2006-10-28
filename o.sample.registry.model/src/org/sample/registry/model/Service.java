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
package org.sample.registry.model;

import org.netbeans.modules.xml.xam.Nameable;
import org.sample.registry.model.impl.Reference;
import org.sample.registry.model.impl.RegistryAttributes;
import org.sample.registry.model.impl.RegistryQNames;

public interface Service extends ReferenceableRegistryComponent, Nameable<RegistryComponent> 
{
    public static final String SERVICE_TYPE_PROPERTY = RegistryAttributes.TYPE.getName();
    public static final String CAPACITY_PROPERTY = RegistryAttributes.CAPACITY.getName();
    public static final String SERVICE_PROVIDER_PROPERTY = RegistryQNames.SERVICE_PROVIDER.getLocalName();
    
    Reference<ServiceType> getServiceType();
    void setServiceType(Reference<ServiceType> type);
    
    Integer getCapacity();
    void setCapacity(Integer capacity);
    
    ServiceProvider getProvider();
    void setProvider(ServiceProvider provider);
}
