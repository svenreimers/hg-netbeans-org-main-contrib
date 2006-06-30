/* The contents of this file are subject to the terms of the Common Development
/* and Distribution License (the License). You may not use this file except in
/* compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
/* or http://www.netbeans.org/cddl.txt.
/*
/* When distributing Covered Code, include this CDDL Header Notice in each file
/* and include the License file at http://www.netbeans.org/cddl.txt.
/* If applicable, add the following below the CDDL Header, with the fields
/* enclosed by brackets [] replaced by your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
 *
 * Contributor(s): Libor Martinek.
 */

package org.netbeans.modules.remotefs.ftpfs;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

import org.openide.explorer.propertysheet.editors.*;

/** Password editor.
 *
 * @author  Libor Martinek
 * @version 1.1
 */
public class PasswordEditor implements EnhancedPropertyEditor, ActionListener, FocusListener {

  private String password;
  private PropertyChangeSupport support;
  transient private JPasswordField field;

  /** Creates new PasswordEditor. */
  public PasswordEditor() {
    support = new PropertyChangeSupport (this);
  }
  
  public boolean supportsEditingTaggedValues() {
    return false;
  }

  public java.lang.String[] getTags() {
    return new String[] {};
  }

  public boolean hasInPlaceCustomEditor() {
    return true;
  }

  public java.awt.Component getInPlaceCustomEditor() {
    if (field == null) {
      field = new JPasswordField();
      field.addActionListener(this);
      field.addFocusListener(this);
    }
    if (password != null) {
      field.setText(password);
      field.setSelectionStart(0);
      field.setSelectionEnd(password.length());
    }
    return field;
  }

  public void setValue(final java.lang.Object p0) {
    if (p0 instanceof String) password = (String)p0;
    support.firePropertyChange ("", null, null);
  }

  public java.lang.Object getValue() {
    return password;
  }

  public java.lang.String getJavaInitializationString() {
    return "";
  }

  public boolean supportsCustomEditor() {
    return false;
  }

  public java.awt.Component getCustomEditor() {
    return null;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(final java.awt.Graphics p0,final java.awt.Rectangle p1) {
  }

  public java.lang.String getAsText() {
    return "***********";
  }

  public void setAsText(java.lang.String p0) throws java.lang.IllegalArgumentException {
  }

  public void addPropertyChangeListener(final java.beans.PropertyChangeListener p0) {
    support.addPropertyChangeListener(p0);
  }

  public void removePropertyChangeListener(final java.beans.PropertyChangeListener p0) {
    support.removePropertyChangeListener(p0);
  }
  
  public void actionPerformed(ActionEvent ev) {
    setValue(new String(field.getPassword()));
  }
  
  public void focusGained(final java.awt.event.FocusEvent p1) {
  }

  public void focusLost(final java.awt.event.FocusEvent p1) {
      setValue(new String(field.getPassword()));
  }

}

