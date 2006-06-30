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

package org.netbeans.modules.corba.settings;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import java.beans.*;

import org.openide.util.NbBundle;

/** property editor for viewer property AppletSettings class
*
* @author Karel Gardas
* @version 0.01 April 16, 1999
*/

import org.netbeans.modules.corba.*;

public class ClientBindingPropertyEditor extends PropertyEditorSupport {
						 //    implements PropertyChangeListener {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    private String[] _M_choices = {""}; // NOI18N

    private ORBSettingsWrapper _M_settings;

    public ClientBindingPropertyEditor () {
        if (DEBUG)
            System.out.println ("ClientBindingPropertyEditor () ..."); // NOI18N
	/*
	  CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	  (CORBASupportSettings.class, true);
	  choices = css.getClientBindingsChoices ();
	  css.addPropertyChangeListener (this);
	*/
        //css.setClientBinding (choices[0]);
	/*
	  for (int i=0; i<choices.length; i++)
	  if (DEBUG)
	  System.out.println ("choice: " + choices[i]);
	*/
    }


    /** @return names of the supported orbs*/
    public String[] getTags() {
	try {
	    if (DEBUG)
		System.out.println ("ClientBindingPropertyEditor::getTags () -> " + _M_choices); // NOI18N
	    _M_settings = (ORBSettingsWrapper)getValue ();
	    if (DEBUG)
		System.out.println ("_M_settings: "
				    + _M_settings.getSettings ().hashCode ());
	    List __bindings = _M_settings.getSettings ().getClientBindings ();
	    String[] __choices = new String[__bindings.size ()];
	    ORBBindingDescriptor __binding = null;
	    for (int __i=0; __i<__bindings.size (); __i++) {
		__binding = (ORBBindingDescriptor)__bindings.get (__i);
		__choices[__i] = _M_settings.getSettings ().getLocalizedString 
		    (__binding.getName ());
	    }
		
	    _M_choices = __choices;
	    return _M_choices;
	} catch (Exception e) {
	    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, e);
	}
	return new String[] {""}; // NOI18N
    } 

    /*
      public void setTags (String[] s) {
      String[] old = choices;
      choices = s;
      //firePropertyChange ("choices", (Object)old, (Object)choices);
      //CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
      //	 (CORBASupportSettings.class, true);
      //css.fireChangeChoices ();
      }
    */
    /*
      public void setChoices (String[] s) {
      setTags (s);
      }
    */
    /*
      public void propertyChange (PropertyChangeEvent event) {
      
      if (event == null || event.getPropertyName () == null)
      return;
      
      if (DEBUG)
      System.out.println ("propertyChange in CBPE: " + event.getPropertyName ());

	  if (event.getPropertyName ().equals ("orb")) {
	  CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	  (CORBASupportSettings.class, true);
	  setChoices (css.getClientBindingsChoices ());
	  css.setClientBinding (getTags ()[0]);
	  if (DEBUG) {
	  for (int i=0; i<choices.length; i++)
	  System.out.println ("choice[" + i + "] in cb-editor: " + choices[i]);
	  }
	  }

	
	  }
    */

    /** @return text for the current value */
    public String getAsText () {
	try {
	    ORBSettingsWrapper __tmp = (ORBSettingsWrapper)getValue ();
	    ORBSettings __settings = __tmp.getSettings ();

	    return __settings.getLocalizedString (__tmp.getValue ());
	} catch (Exception __e) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		__e.printStackTrace ();
	}
	return ""; // NOI18N
    }


    /** @param text A text for the current value. */
    public void setAsText (String __value) {
	if (DEBUG)
	    System.out.println ("ClientBindingPropertyEditor::setAsText (" + __value + ")"); // NOI18N
	_M_settings = (ORBSettingsWrapper)getValue ();
	if (DEBUG)
	    System.out.println ("_M_settings: " + _M_settings.getSettings ().hashCode ());
	List __bindings = _M_settings.getSettings ().getClientBindings ();
	List __localized_bindings = new LinkedList ();
	Iterator __iterator = __bindings.iterator ();
	ORBBindingDescriptor __binding = null;
	ORBSettings __settings = _M_settings.getSettings ();
	while (__iterator.hasNext ()) {
	    __binding = (ORBBindingDescriptor)__iterator.next ();
	    __localized_bindings.add (__settings.getLocalizedString (__binding.getName ()));
	}
	__binding = (ORBBindingDescriptor)__bindings.get
	    (__localized_bindings.indexOf (__value));
	String __not_locallized_value = __binding.getName ();
	if (DEBUG)
	    System.out.println ("-> " + __not_locallized_value);
	this.setValue (new ORBSettingsWrapper (__settings, __not_locallized_value));
        //((ORBSettingsWrapper)getValue ()).setValue (__value);
	//setValue (new ORBSettingsWrapper (((ORBSettingsWrapper)getValue ()).getSettings (), 
	//__value));
    }
}

/*
 * <<Log>>
 *  14   Gandalf   1.13        3/7/00   Karel Gardas    naming service browser 
 *       bugfix
 *  13   Gandalf   1.12        11/4/99  Karel Gardas    - update from CVS
 *  12   Gandalf   1.11        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  11   Gandalf   1.10        10/1/99  Karel Gardas    updates from CVS
 *  10   Gandalf   1.9         8/3/99   Karel Gardas    
 *  9    Gandalf   1.8         7/10/99  Karel Gardas    
 *  8    Gandalf   1.7         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    fixed for reading 
 *       configuration from implementations files
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */






