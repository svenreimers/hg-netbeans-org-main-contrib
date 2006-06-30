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

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.InterfaceElement;

/**
 * Class IDLInterfaceNode
 *
 * @author Karel Gardas
 */
public class IDLInterfaceNode extends IDLAbstractNode {

    InterfaceElement _interface;

    private static final String INTERFACE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/interface"; // NOI18N

    public IDLInterfaceNode (InterfaceElement value) {
        super (new IDLDocumentChildren ((IDLElement)value));
        //System.out.println ("IDLInterfaceNode (" + value + ");"); // NOI18N
        setIconBase (INTERFACE_ICON_BASE);
        _interface = value;
        setCookieForDataObject (_interface.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _interface;
    }
    /*
      public String getDisplayName () {
      if (_interface != null)
      //return ((Identifier)_interface.jjtGetChild (0)).getName ();
      return _interface.getName ();
      else
      return "NoName :)";
      }
    */
    public String getName () {
        return "interface"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_INTERFACE) { // NOI18N
		public Object getValue () {
		    return _interface.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("abstract", String.class, IDLNodeBundle.ABSTRACT, IDLNodeBundle.ABSTRACT_INTERFACE) { // NOI18N
		public Object getValue () {
		    if (_interface.isAbstract ())
			return IDLNodeBundle.YES;
		    else
			return IDLNodeBundle.NO;
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("inherited", String.class, IDLNodeBundle.INHERITED, // NOI18N
                                              IDLNodeBundle.INHERITED_FROM) {
		public Object getValue () {
		    String inher = ""; // NOI18N
		    if (_interface.getParents ().size () > 0) {
			for (int i=0; i<_interface.getParents ().size (); i++)
                                //inher = inher + ((Identifier)_interface.getParents ().elementAt (i)).getName ()
			    inher = inher + (String)_interface.getParents ().elementAt (i)
				+ ", "; // NOI18N
			inher = inher.substring (0, inher.length () - 2);
		    }
		    else
			inher = ""; // NOI18N
		    return inher;
		}
	    });
	
        return s;
    }

}

/*
 * $Log
 * $
 */

