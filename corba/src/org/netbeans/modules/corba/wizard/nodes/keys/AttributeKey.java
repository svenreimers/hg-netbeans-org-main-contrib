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

package org.netbeans.modules.corba.wizard.nodes.keys;

/**
 *
 * @author  root
 * @version
 */
public class AttributeKey extends NamedKey {

    private boolean readonly;
    private String type;

    /** Creates new AttributeKey */
    public AttributeKey (int kind, String name, String type, boolean readonly) {
        super (kind, name);
        this.type = type;
        this.readonly = readonly;
    }

    public String getType () {
        return this.type;
    }

    public void setType (String type) {
        this.type = type;
    }
  
    public boolean isReadOnly () {
        return this.readonly;
    }
    
    public void setReadOnly (boolean ro) {
        this.readonly = ro;
    }
  
}
