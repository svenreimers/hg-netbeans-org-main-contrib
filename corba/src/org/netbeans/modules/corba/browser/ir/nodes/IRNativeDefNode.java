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
 * IRNativeNode.java
 *
 * Created on August 21, 2000, 8:26 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import java.io.IOException;
import java.io.PrintWriter;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import org.omg.CORBA.*;
import org.openide.TopManager;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;
/**
 *
 * @author  tzezula
 * @version 
 */
public class IRNativeDefNode extends IRLeafNode implements Node.Cookie, Generatable {
    
    private static final String ICON_BASE = 
        "org/netbeans/modules/corba/idl/node/declarator";
    private NativeDef _native;
    private String name;
    
    private class NativeCodeGenerator implements GenerateSupport {
    
        public NativeCodeGenerator (){
        }
    
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_native.id(), _native.absolute_name(), currentPrefix, indent);      //NOI18N
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            code = code + "native " + _native.name()+ ";\n";        //NOI18N
            code = code + generateTail (indent);
            return code;  
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_native.name(), _native.id(), indent);      //NOI18N
        }
        
        public String getRepositoryId () {
            return _native.id();
        }
    }

    /** Creates new IRNativeNode */
    public IRNativeDefNode(Contained contained) {
        super ();
        this._native = NativeDefHelper.narrow (contained);
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add (new NativeCodeGenerator ());
    }
    
    public String getDisplayName () {
        if (this.name == null) {
            if (this._native != null)
                this.name = this._native.name();
            else
                this.name = "";
        }
        return this.name;
    }
    
    public String getName () {
        return this.getDisplayName ();
    }
    
    public Sheet createSheet () {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_NativeName")) { 
                public java.lang.Object getValue() {
                    return name;
                }
            });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_NativeId")) {
            public java.lang.Object getValue () {
                return _native.id();
            }
        });
        
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_NativeVersion")) {
            public java.lang.Object getValue () {
                return _native.version();
            }
        });
        return sheet;
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
    
    public org.omg.CORBA.IRObject getIRObject () {
        return this._native;
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
