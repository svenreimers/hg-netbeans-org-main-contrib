/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.corba.idl.src;

import java.util.Vector;

public class InterfaceElement extends Element {

   public static final boolean DEBUG = false;
   //public static final boolean DEBUG = true;

   private Vector inherited_from;
   private Vector body;

   public InterfaceElement(int id) {
      super(id);
      inherited_from = new Vector ();
      body = new Vector ();
   }
   
   public InterfaceElement(IDLParser p, int id) {
      super(p, id);
      inherited_from = new Vector ();
      body = new Vector ();
   }

   public void addParent (Identifier x) {
      inherited_from.addElement (x);
   }

   public Vector getParents () {
      return inherited_from;
   }

   public void addMemberOfBody (Element e) {
      body.addElement (e);
   }

   public Vector getMembersOfBody () {
      return body;
   }

   public void jjtClose () {
      super.jjtClose ();
      // first header
      if (DEBUG)
	 System.out.println ("InterfaceElement.jjtClose ()");
      int counter = 0;
      int max = super.getMembers ().size ();
      Vector _members = super.getMembers ();
      if (max >= 1) {
	 setName (((Identifier)_members.elementAt (counter)).getName ());
	 setLine (((Identifier)_members.elementAt (counter)).getLine ());
      }
      counter++;
      while ((counter < max) && (_members.elementAt (counter)) instanceof Identifier) {
	 addParent ((Identifier)_members.elementAt (counter));
	 counter++;
      }
      while (counter < max) {
	 addMemberOfBody ((Element)_members.elementAt (counter));
	 counter++;
      }

      // reformating attributes from one attribute with other to many attribute
      for (int i=0; i<max; i++) {
	 if (_members.elementAt (i) instanceof AttributeElement) {
	    Vector attrs = ((AttributeElement)_members.elementAt (i)).getOther ();
	    AttributeElement parent = (AttributeElement)_members.elementAt (i);
	    //for (int j=0; j<attrs.size (); j++) {
	    for (int j=attrs.size () - 1; j >= 0; j--) {
	       AttributeElement attr = new AttributeElement (-1);
	       Identifier id = new Identifier (-1);
	       id.setName ((String)attrs.elementAt (j));
	       attr.setName ((String)attrs.elementAt (j));
	       attr.setType (parent.getType ());
	       attr.setReadOnly (parent.getReadOnly ());
	       attr.setParent (this);
	       //attr.addMember (id);
	       getMembers ().insertElementAt (attr, i + 1);
	    }
	    parent.setOther (new Vector ());
	 }
      }
   }
   
}
