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

public class ValueElement extends ValueAbsElement {

    private boolean _M_is_custom;

    public ValueElement(int id) {
        super(id);
        _M_is_custom = false;
    }

    public ValueElement(IDLParser p, int id) {
        super(p, id);
        _M_is_custom = false;
    }

    public boolean isAbstract () {
        //return is_abstract; // because ValueElement is never abstract
	return false;
    }

    public void setCustom (boolean value) {
        _M_is_custom = value;
    }

    public boolean isCustom () {
        return _M_is_custom;
    }
    /*
      public void setInherited (Vector __value) {
      _M_inherited = __value;
      }
      
      public Vector getInherited () {
      return _M_inherited;
      }
    */

    public void jjtClose () {
        super.jjtClose ();
        Vector __members = super.getMembers ();
	Vector __new_members = new Vector ();
	Object __element;
	// translates states -> sates with one DeclaratorElement
	for (int __i=0; __i<__members.size (); __i++) {
	    __element = __members.elementAt (__i);
	    if (__element instanceof StateMemberElement) {
		
	    }
	    else {
		__new_members.add (__element);
	    }
	} 
	//System.out.println ("members: " + __members); // NOI18N
        ValueHeaderElement __header = (ValueHeaderElement)__members.elementAt (0);
	//System.out.println ("members of header: " + __header.getMembers ()); // NOI18N
	try {
	    ValueInheritanceSpecElement __inheritance
		= (ValueInheritanceSpecElement)__header.getMembers ().elementAt (1);
	    //System.out.println ("inherited: " + __inheritance.getValues ()); // NOI18N
	    this.setParents (__inheritance.getValues ());
	    //System.out.println ("supports: " + __inheritance.getInterfaces ()); // NOI18N
	    this.setSupported (__inheritance.getInterfaces ());
	} catch (ClassCastException __ex) {
	    // this valuetype don't inherits or supports any value or interface(s)
	} catch (Exception __ex) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) { // NOI18N
		__ex.printStackTrace ();
	    }
	}
        this.setName (__header.getName ());
	this.setLine (__header.getLine ());
	this.setColumn (__header.getColumn ());
        this.setCustom (__header.isCustom ());
	this.setFileName (__header.getFileName ());
    }

}


