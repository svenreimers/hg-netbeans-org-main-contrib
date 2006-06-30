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

package org.netbeans.modules.corba.idl.compiler.j2ee;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

//import com.sun.tools.corba.se.idl.GenFileStream;
//import com.sun.tools.corba.se.idl.SymtabFactory;
//import com.sun.tools.corba.se.idl.IncludeEntry;
//import com.sun.tools.corba.se.idl.InterfaceEntry;
//import com.sun.tools.corba.se.idl.InterfaceState;
//import com.sun.tools.corba.se.idl.ModuleEntry;
//import com.sun.tools.corba.se.idl.PrimitiveEntry;
//import com.sun.tools.corba.se.idl.SequenceEntry;
//import com.sun.tools.corba.se.idl.StructEntry;
//import com.sun.tools.corba.se.idl.SymtabEntry;
//import com.sun.tools.corba.se.idl.TypedefEntry;
//import com.sun.tools.corba.se.idl.UnionBranch;
//import com.sun.tools.corba.se.idl.UnionEntry;
//import com.sun.tools.corba.se.idl.ValueEntry;
//import com.sun.tools.corba.se.idl.ValueBoxEntry;
//import com.sun.tools.corba.se.idl.InvalidArgument;

//import com.sun.tools.corba.se.idl.toJavaPortable.Compile;
//import com.sun.tools.corba.se.idl.toJavaPortable.Util;


import com.sun.idl.GenFileStream;
import com.sun.idl.SymtabFactory;
import com.sun.idl.IncludeEntry;
import com.sun.idl.InterfaceEntry;
import com.sun.idl.InterfaceState;
import com.sun.idl.ModuleEntry;
import com.sun.idl.PrimitiveEntry;
import com.sun.idl.SequenceEntry;
import com.sun.idl.StructEntry;
import com.sun.idl.SymtabEntry;
import com.sun.idl.TypedefEntry;
import com.sun.idl.UnionBranch;
import com.sun.idl.UnionEntry;
import com.sun.idl.ValueEntry;
import com.sun.idl.ValueBoxEntry;
import com.sun.idl.InvalidArgument;

import com.sun.idl.toJavaPortable.Compile;
import com.sun.idl.toJavaPortable.Util;


//
// Compiler Wrapper for original IBM's RMI-IIOP IDL Compiler
//


public class Compiler extends com.sun.idl.Compile {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;
  
    public static void main (String[] args) {
	Compiler comp = new Compiler ();
	/*
	 *
	 * options --directory  <dir> --package <package> --tie
	 * --directory => -td
	 * --package => -pkgPrefix
	 * --tie => -fallTIE - else -fall
	 * other options
	 *
	 */
	for (int i=0; i<args.length; i++) {
	    if (DEBUG)
		System.out.println ("param: " + args[i]); // NOI18N
	}
	String file_name = args[args.length - 1];
	if (DEBUG)
	    System.out.println ("idl name: " + file_name); // NOI18N
	String[] parser_args = new String[] { file_name };

	Vector names = new Vector ();
	try {
	    comp.init (parser_args);
	    java.util.Enumeration en = comp.parse ();
	    if (en == null)
		return;
	    while (en.hasMoreElements ()) {
		String name = ((SymtabEntry)en.nextElement ()).fullName ();
		if (DEBUG)
		    System.out.println ("element: " + name); // NOI18N
		if (name.indexOf ('/') == -1) {
		    // top level element
		    names.addElement (name);
		    if (DEBUG)
			System.out.println ("top level element: " + name); // NOI18N
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace ();
	}

	Vector new_args = new Vector ();
	boolean ties = false;
	for (int i=0; i<args.length-1; i++) {
	    if (args[i].equals ("--directory")) { // NOI18N
		new_args.addElement ("-td"); // NOI18N
		new_args.addElement (args[++i]);
		continue;
	    }
	    if (args[i].equals ("--package")) { // NOI18N
		i++;
		for (int j=0; j<names.size (); j++) {
		    new_args.addElement ("-pkgPrefix"); // NOI18N
		    new_args.addElement ((String)names.elementAt (j));
		    new_args.addElement (args[i]);
		}
		continue;
	    }
	    if (args[i].equals ("--tie")) { // NOI18N
		new_args.addElement ("-fallTIE"); // NOI18N
		ties = true;
		continue;
	    }
	    // other parameters (JDK1.3 IDL compliant)
	    new_args.addElement (args[i]);
	}
	if (!ties)
	    new_args.addElement ("-fall"); // NOI18N
	new_args.addElement (file_name);
	String[] args2 = (String[])new_args.toArray (new String[] {});
	if (DEBUG) {
	    for (int i=0; i<args2.length; i++) {
		System.out.println ("new param: " + args2[i]); // NOI18N
	    }
	}

	Compile.main (args2);
    
    }

}

/*
 * $Log
 * $
 */
