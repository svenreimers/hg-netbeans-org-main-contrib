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

package org.netbeans.modules.corba.idl.src;

public class UnionMemberElement extends TypeElement {

    //private

    private String _cases;

    static final long serialVersionUID =6067453167467867759L;
    public UnionMemberElement(int id) {
        super(id);
    }

    public UnionMemberElement(IDLParser p, int id) {
        super(p, id);
    }

    public String getCases () {
        return _cases;
    }

    public void setCases (String s) {
        _cases = s;
    }

    /*
    public void jjtClose () {
       super.jjtClose ();
       setName (((DeclaratorElement)getMember (getMembers ().size () - 1)).getName ());
}
    */
    public void jjtSetParent (Node n) {
        super.jjtSetParent (n);
        setName (((DeclaratorElement)getMember (getMembers ().size () - 1)).getName ());
    }
}
