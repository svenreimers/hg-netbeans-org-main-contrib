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

/*
 * IRContainerNode.java
 *
 * Created on February 23, 2000, 11:35 AM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.openide.TopManager;
import org.openide.util.datatransfer.ExClipboard;
import java.awt.datatransfer.StringSelection;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;

/**
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public abstract class IRContainerNode extends IRAbstractNode implements Node.Cookie, Generatable {

    /** Creates new IRContainerNode */
    public IRContainerNode(Children children) {
        super(children);
        this.getCookieSet().add(this);
    }


    public void refresh () {
        ((Refreshable)this.getChildren()).createKeys ();
    }

    public void generateCode() {
        

        ExClipboard clipboard = TopManager.getDefault().getClipboard();
        StringSelection genCode = new StringSelection ( this.generateHierarchy ());
        clipboard.setContents(genCode,genCode);
    }

    public void generateCode (PrintWriter out) throws IOException {
        String hierarchy = this.generateHierarchy ();
        out.println (hierarchy);
    }
    
    public abstract org.omg.CORBA.Contained getOwner();

    public SystemAction[] createActions (){
        return new SystemAction[] {
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.GenerateCodeAction.class),
            null,
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.RefreshAction.class),
            null,
            SystemAction.get (org.openide.actions.PropertiesAction.class)
        };
    }

    private String generateHierarchy () {
        Node node = this.getParentNode();
        String code ="";

        // Generate the start of namespace
        ArrayList stack = new ArrayList();
        while ( node instanceof IRContainerNode){
            stack.add(node.getCookie (GenerateSupport.class));
            node = node.getParentNode();
        }
        int size = stack.size();
        org.omg.CORBA.StringHolder currentPrefix = new org.omg.CORBA.StringHolder ();
        for (int i = size -1 ; i>=0; i--)
            code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1), currentPrefix);

        // Generate element itself
        code = code + ((GenerateSupport)this.getCookie (GenerateSupport.class)).generateSelf(size, currentPrefix);
        //Generate tail of namespace
        for (int i = 0; i< stack.size(); i++)
            code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
        return code;
    }

}
