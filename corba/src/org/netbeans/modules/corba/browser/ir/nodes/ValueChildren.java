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

/*
 * ValueChildren.java
 *
 * Created on August 28, 2000, 7:40 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.Node;
import org.openide.*;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.*;

/**
 *
 * @author  tzezula
 * @version 
 */
public class ValueChildren extends Children implements Refreshable {
    
    private ValueDef value;

    /** Creates new ValueChildren */
    public ValueChildren(ValueDef value) {
        super ();
        this.value = value;
    }
    
    public void addNotify () {
        synchronized (this) {
            this.state = TRANSIENT;
        }
        this.waitNode = new WaitNode ();
        this.add ( new Node[] { this.waitNode});
        org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
    }
    
     public void createKeys() {
         try {
             Contained[] members = this.value.contents(DefinitionKind.dk_all, true);
             Initializer[] initializers = this.value.describe_value().initializers;
             java.lang.Object keys[] = new java.lang.Object [members.length + initializers.length];
             for (int i=0; i < members.length; i++)
                 keys[i] = new IRContainedKey (members[i]);
             for (int i=0; i < initializers.length; i++) 
                 keys[members.length + i] = new IRInitializerKey (initializers[i]);
             this.setKeys(keys);
         }catch (final Exception e) {
             setKeys ( new java.lang.Object[0]);
             java.awt.EventQueue.invokeLater (new Runnable () {
                public void run () {
                    TopManager.getDefault().notify ( new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
         }
    }
    
    public Node[] createNodes (java.lang.Object key) {
        if (key != null) {
            if (key instanceof IRInitializerKey) {
                return new Node[] { new IRInitializerNode (((IRInitializerKey)key).initializer)};
            }
            else if (key instanceof IRContainedKey) {
                Node[] nodes = new Node[1];
                DefinitionKind dk = null;
                boolean operation = false;
                Contained contained = ((IRContainedKey)key).contained;
                // Workaround for bug in Jdk 1.2 implementation
                // if MARSHAL exception ocured, try to introspect
                // object in another way.
                try{
                    dk = contained.def_kind();
                }catch (org.omg.CORBA.MARSHAL marshalException) {
                    if (contained._is_a("IDL:omg.org/CORBA/OperationDef:1.0")){
                    operation = true;
                }
                else
                    throw new RuntimeException("Inner exception is: " + marshalException);
                }    
                if (dk == DefinitionKind.dk_Exception){
                    nodes[0] = new IRExceptionDefNode (contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Struct) {
                    nodes[0] = new IRStructDefNode (contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Union){
                    nodes[0] = new IRUnionDefNode (contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Constant) {
                    nodes[0] = new IRConstantDefNode (contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Attribute) {
                    nodes[0] = new IRAttributeDefNode(contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Operation || operation) {
                    nodes[0] = new IROperationDefNode(contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Alias){
                    nodes[0] = new IRAliasDefNode(contained);
                    return nodes;
                }
                else if (dk== DefinitionKind.dk_Enum){
                    nodes[0] = new IREnumDefNode(contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_Native){
                    nodes[0] = new IRNativeDefNode(contained);
                    return nodes;
                }
                else if (dk == DefinitionKind.dk_ValueMember) {
                    nodes[0] = new IRValueMemberDefNode (contained);
                    return nodes;
                }
            }
        }
        return new Node[0];
    }

   
    
}
