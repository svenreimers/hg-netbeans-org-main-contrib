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

package com.netbeans.enterprise.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import com.netbeans.enterprise.modules.corba.browser.ir.util.Refreshable;
import com.netbeans.enterprise.modules.corba.browser.ir.nodes.keys.IRTypeCodeKey;


public class ExceptionChildren extends Children.Keys implements Refreshable {

  
  private ExceptionDef exception;
  
  /** Creates new ExceptionChildren */
  public ExceptionChildren(ExceptionDef exception) {
    super();
    this.exception = exception;
  }
  
  
  public void addNotify(){
    createKeys();
  }
  
  
  public void createKeys(){
    StructMember [] members = this.exception.members();
    java.lang.Object[] keys = new java.lang.Object[members.length];
    for (int i =0; i< keys.length; i++)
      keys[i] = new IRTypeCodeKey (members[i].name, members[i].type);
    setKeys(keys);
  }
  
  
  public Node[] createNodes (java.lang.Object key){
    if (key != null){
      if (key instanceof IRTypeCodeKey) {
        return new Node[]{new IRPrimitiveNode(((IRTypeCodeKey)key).type, ((IRTypeCodeKey)key).name)};
      }
    }
    return new Node[]{new IRUnknownTypeNode()};
  }
  
}
