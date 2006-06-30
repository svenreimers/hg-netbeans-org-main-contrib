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

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRTypeCodeKey;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;


public class StructChildren extends Children {

    private static final boolean DEBUG = true;
    //private static final boolean DEBUG = false;

    private StructDef struct;
    private boolean container;

    /** Creates new StructChildren */
    public StructChildren(Contained contained) {
        super();
        try {
            this.container = (contained._is_a ("IDL:omg.org/CORBA/Container:1.0") || contained._is_a ("IDL:omg.org/CORBA/Container:2.3"));
        }catch (org.omg.CORBA.SystemException se) {
            this.container = false;
        }
        this.struct = StructDefHelper.narrow(contained);
    }
    
    
    public StructDef getStructStub () {
        return this.struct;
    }

    public void addNotify(){
        synchronized (this) {
            if (this.state == SYNCHRONOUS) {
                this.createKeys();
                this.state = INITIALIZED;
            }
            else {
                this.state = TRANSIENT;
                this.waitNode = new WaitNode ();
                this.add ( new Node[] { this.waitNode});
                org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
            }
        }
    }


    public void createKeys(){
        try {
            // Keys for inner types
            
            // Keys for members
            StructMember[] members = this.struct.members();
            Contained[] contained = null;
            
            if (this.container) {
                // The struct is CORBA 2.3 and implements CORBA::Container
                contained = this.struct.contents (DefinitionKind.dk_all, false);
            }
            else {
                contained = new Contained [0];
            }
            
            
            java.lang.Object[] keys = new java.lang.Object[members.length + contained.length];
 
            for (int i=0; i<contained.length; i++)
                keys[i] = new IRContainedKey (contained[i]);
            for (int i=0; i<members.length; i++)
                keys[contained.length + i] = new IRTypeCodeKey (members[i].name, members[i].type_def);
            
            setKeys(keys);
        }catch (final SystemException e) {
            if (DEBUG)
                e.printStackTrace();
            setKeys (new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify(new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }

    public Node[] createNodes(java.lang.Object key){
        if (key != null){
            if (key instanceof IRTypeCodeKey){
                try {
                    return new Node[]{new IRPrimitiveNode(((IRTypeCodeKey)key).type,((IRTypeCodeKey)key).name)};
                }catch (final Exception t) {
                    if (DEBUG)
                        t.printStackTrace();
                    java.awt.EventQueue.invokeLater ( new Runnable () {
                        public void run () {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (t.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                        }});
                    return new Node[0];
                }
            }
            else if (key instanceof IRContainedKey) {
                Contained contained = ((IRContainedKey)key).contained;
                switch (contained.def_kind().value()) {
                    case DefinitionKind._dk_Struct:
                        return new Node[] { new IRStructDefNode (contained)};
                    case DefinitionKind._dk_Union:
                        return new Node[]{ new IRUnionDefNode(contained)};
                    case DefinitionKind._dk_Enum:
                        return new Node[]{ new IREnumDefNode(contained)};
                    default:
                        return new Node[]{ new IRUnknownTypeNode()};
                }
            }
        }
        return new Node[] { new IRUnknownTypeNode()};
    }

}
