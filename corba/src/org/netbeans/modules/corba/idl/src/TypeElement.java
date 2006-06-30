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

import java.util.Vector;

public class TypeElement extends IDLElement {

    //private static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    private String type_name;
    private IDLType type;
    //private Vector members;

    public TypeElement (int id) {
        super(id);
        //members = new Vector ();
        setName ("typedef"); // NOI18N
        setTypeName ("none"); // NOI18N
    }

    public TypeElement (IDLParser p, int id) {
        super(p, id);
        //members = new Vector ();
        setName ("typedef"); // NOI18N
        setTypeName ("none"); // NOI18N
    }

    public void setTypeName (String t) {
        type_name = t;
    }

    public String getTypeName () {
        return type_name;
    }

    public IDLType getType () {
        return type;
    }

    public void setType (IDLType val) {
        type = val;
    }

    public void jjtSetParent (Node n) {
        super.jjtSetParent (n);
        String type;
        //if (DEBUG) {
        //  System.out.println ("TypeElement.jjtSetParent ()"); // NOI18N
        //  if (getType () != null)
        //System.out.println ("type: " + getType ().getName ()); // NOI18N
        //}
        //if (getMember (0) instanceof Identifier)
	IDLElement __first_element = (IDLElement)this.getMember (0);
        this.setName (__first_element.getName ());
	this.setLine (__first_element.getLine ());
	this.setColumn (__first_element.getColumn ());
	this.setFileName (__first_element.getFileName ());

        if ((getMember (0) instanceof StructTypeElement)
                || (getMember (0) instanceof UnionTypeElement)
                || (getMember (0) instanceof EnumTypeElement)) {
            setType (new IDLType (-1 ,(((TypeElement)getMember (0)).getName ())));
        }

        if (DEBUG)
            System.out.println ("name: " + getName ()); // NOI18N

        else {
            // constr type
            //setType (((Identifier)getMember (0).getMember (0)).getName ());
            //setType ("typedef"); // NOI18N
            //for (int i = 1; i<getMembers ().size (); i++)
        }
        for (int i = 0; i<getMembers ().size (); i++) {
            if (getMembers ().elementAt (i) instanceof DeclaratorElement) {
                //((DeclaratorElement)getMembers ().elementAt (i)).setType (new Type (-1, getName ()));
                ((DeclaratorElement)getMembers ().elementAt (i)).setType (getType ());
            }
        }
    }


    public String toString () {
	try {
	    return super.toString () + ": type = " + this.getType ().toString ();
	} catch (Exception __ex) {
	    return super.toString () + ": type = null";
	}
    }

}


