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

package org.netbeans.api.adaptable;

import java.util.Set;
import org.netbeans.spi.adaptable.SingletonizerEvent;

/** Implementation of accessor for friend access to hidden features
 * of the API.
 *
 * @author Jaroslav Tulach
 */
final class AccessorImpl extends org.netbeans.modules.adaptable.Accessor {

    public Adaptor createAspectProvider(org.netbeans.modules.adaptable.ProviderImpl impl, Object data) {
        return new Adaptor (impl, data);
    }

    public Object getData(Adaptor adaptor) {
        return adaptor.data;
    }

    public org.netbeans.modules.adaptable.ProviderImpl getProviderImpl(Adaptor adaptor) {
        return adaptor.impl;
    }

    public AdaptableEvent createEvent(Adaptable source, Set<Class> affected) {
        return new AdaptableEvent(source, affected);
    }

    public Object getAffectedObject(SingletonizerEvent ev) {
        assert false;
        return null;
    }

    public Set<Class> getAffectedClasses(SingletonizerEvent ev) {
        assert false;
        return null;
    }
}
