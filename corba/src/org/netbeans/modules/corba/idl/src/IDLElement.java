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

import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

//import org.openide.nodes.Node;
import org.openide.cookies.OpenCookie;

import org.netbeans.modules.corba.IDLDataObject;

/*
 * @author Karel Gardas
 */

public class IDLElement extends SimpleNode
    implements Serializable, OpenCookie {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    private String _M_name;
    private int _M_line;
    private int _M_column;
    private Vector _M_members;
    private String _M_file_name;

    private IDLElement _M_parent;

    private IDLDataObject _M_ido;

    public IDLElement (int i) {
        super (i);
        _M_members = new Vector ();
        _M_name = ""; // NOI18N
    }

    public IDLElement (IDLParser p, int i) {
        super (p, i);
        _M_members = new Vector ();
        _M_name = ""; // NOI18N
    }

    public void setDataObject (IDLDataObject val) {
        if (DEBUG)
            System.out.println ("IDLElement ``" + getName () + " '' ::setDataObject (val)"); // NOI18N
        _M_ido = val;
        setDataObjectForMembers (val);
    }

    public void setDataObjectForMembers (IDLDataObject val) {
        for (int i=0; i<getMembers ().size (); i++) {
            ((IDLElement)getMember (i)).setDataObject (val);
        }
    }

    public IDLDataObject getDataObject () {
        return _M_ido;
    }

    public void setLine (int i) {
        if (DEBUG)
            System.out.println ("set line for " + getName () + " : " + i); // NOI18N
        _M_line = i;
        //getLine (); // debug check
    }

    public int getLine () {
        if (DEBUG)
            System.out.println ("get line for " + getName () + " : " + _M_line); // NOI18N
        return _M_line;
    }

    public void setColumn (int i) {
        if (DEBUG)
            System.out.println ("set column for " + getName () + " : " + i); // NOI18N
        _M_column = i;
        //getColumn (); // debug check
    }

    public int getColumn () {
        if (DEBUG)
            System.out.println ("get column for " + getName () + " : " + _M_column); // NOI18N
        return _M_column;
    }

    public void setName (String v) {
        if (DEBUG)
            System.out.println ("setName: " + v); // NOI18N
        _M_name = v;
    }

    public String getName () {
        if (DEBUG)
            System.out.println ("getName: " + _M_name); // NOI18N
        return _M_name;
    }

    public void addMember (Node x) {
        _M_members.addElement (x);
    }

    public Vector getMembers () {
        return _M_members;
    }

    public void setMembers (Vector __value) {
	_M_members = __value;
    }

    /*
         public Object getMember (int i) {
         return members.elementAt (i);
         }
    */

    public IDLElement getMember (int i) {
        return (IDLElement)_M_members.elementAt (i);
    }

    public void setParent (IDLElement e) {
        _M_parent = e;
    }

    public IDLElement getParent () {
        return _M_parent;
    }

    public void open () {
        if (DEBUG)
            System.out.println ("open action :-))"); // NOI18N
    }

    public void setFileName (String __name) {
	_M_file_name = __name;
    }

    public String getFileName () {
	return _M_file_name;
    }

    public String deepToString (IDLElement element) {
        if (DEBUG)
            System.out.println ("IDLElement::deepToString (" + element + ");"); // NOI18N

        // for tests
        //return element.getName ();

        String names = element.getName () + ":" + element.getLine () + ":" // NOI18N
                       + element.getColumn () + ":" + "("; // NOI18N
        Vector members = element.getMembers ();
        for (int i=0; i<members.size (); i++) {
            IDLElement tmp = (IDLElement)members.elementAt (i);
            //names = names + " " + tmp.getName () + " (" + deepToString (tmp) + ")"; // NOI18N
            names = names + tmp.getName () + ":" + tmp.getLine () + ":" + tmp.getColumn () + ":" // NOI18N
                    + " (" + deepToString (tmp) + ")"; // NOI18N
            //names = names + " " + tmp.getName (); // NOI18N
        }

        if (DEBUG)
            System.out.println ("-> " + names); // NOI18N
        return names + ")"; // NOI18N

    }

    public boolean equals (Object obj) {
        IDLElement element;
        if (!(obj instanceof IDLElement)) {
            if (DEBUG) {
                System.out.println (this.getName () + "::equals (" + obj + ");"); // NOI18N
                System.out.println ("isn't IDLElement"); // NOI18N
            }
            return false;
        } else {
            element = (IDLElement)obj;
        }

        if (DEBUG)
            System.out.println (this.getName () + "::equals (" + ((IDLElement)element).getName () // NOI18N
                                + ");"); // NOI18N

        if (element.className ().equals (className ())) {
            IDLElement tmp_element = (IDLElement)element;
            String this_names = deepToString (this);
            String object_names = deepToString ((IDLElement)element);
            if (this_names.equals (object_names)) {
                if (DEBUG)
                    System.out.println ("return true;"); // NOI18N
                return true;
            }
        }
        if (DEBUG)
            System.out.println ("return false;"); // NOI18N
        return false;
    }


    public String className () {
        String tmp = this.getClass ().getName ();
        return tmp.substring (tmp.lastIndexOf (".") + 1, tmp.length ()); // NOI18N
    }


    public int hashCode () {
        String name = className () + deepToString (this);
        int code = name.hashCode ();
        if (DEBUG)
            System.out.println ("IDLElement::hashCode () : " + name + " : " + code); // NOI18N
        return code;
    }


    public void jjtClose () {
        //if (DEBUG)
        //  System.out.println ("IDLElement.jjtClose ()"); // NOI18N
        for (int i=0; i<jjtGetNumChildren (); i++) {
            addMember (jjtGetChild (i));
        }
        for (int i=0; i<getMembers ().size (); i++) {
            ((IDLElement)getMember (i)).setParent (this);
        }

    }

    public void xDump (String s) {
        //System.out.println ("dump: " + members); // NOI18N
        for (int i=0; i<_M_members.size (); i++) {
            System.out.println (s + _M_members.elementAt (i));
            ((IDLElement)_M_members.elementAt (i)).xDump (s + " "); // NOI18N
        }
    }

    public static Node jjtCreate(int id) {
        return new IDLElement (id);
    }

    public static Node jjtCreate(IDLParser p, int id) {
        return new IDLElement (p, id);
    }

    public String toString() {
	/*
	  return IDLParserTreeConstants.jjtNodeName[id] + ":" + this.getName ()
	  + " - " + this.hashCode () + " in file: " + this.getFileName ();
	*/
	String __value = "unknown";
	try {
	    __value = IDLParserTreeConstants.jjtNodeName[id] + ":" + this.getName ()
		+ " - " + this.hashCode ();
	} catch (Exception __ex) {
	    __ex.printStackTrace ();
	}
	return __value;
    }


}

/*
 * <<Log>>
 *  6    Gandalf   1.5         2/8/00   Karel Gardas    
 *  5    Gandalf   1.4         11/27/99 Patrik Knakal   
 *  4    Gandalf   1.3         11/4/99  Karel Gardas    - update from CVS
 *  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  2    Gandalf   1.1         10/5/99  Karel Gardas    
 *  1    Gandalf   1.0         8/3/99   Karel Gardas    initial revision
 * $
 */

