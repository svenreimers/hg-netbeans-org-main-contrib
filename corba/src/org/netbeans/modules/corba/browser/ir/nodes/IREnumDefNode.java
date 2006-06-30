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
import org.openide.nodes.Sheet;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IREnumDefNode extends IRContainerNode {

    private EnumDef _enum;
    private static final String ENUM_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/enum";


    private class EnumCodeGenerator implements GenerateSupport {
    
        public EnumCodeGenerator (){
        }
    
        public String generateHead (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas ( _enum.id(), _enum.absolute_name(), currentPrefix, indent);
            String fill ="";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "enum " + _enum.name()+" { ";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead(indent, currentPrefix);
            if (((Children)getChildren()).getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = getChildren().getNodes();
            for (int i=0; i< nodes.length; i++) {
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                if (i!=0)
                    code = code +", ";
                if (gs != null)
                    code = code + gs.generateSelf (indent+1, currentPrefix);
            }
            code = code + generateTail(indent);
            return code;
        }
    
        public String generateTail (int indent){
            return "};\n"+Util.generatePostTypePragmas(_enum.name(),_enum.id(), indent)+"\n";	 
        }
        
        public String getRepositoryId () {
            return _enum.id();
        }
    
    }

    /** Creates new IREnumDefNode */
    public IREnumDefNode(Contained value) {
        super(new EnumChildren(EnumDefHelper.narrow(value)));
        _enum = ((EnumChildren)this.getChildren()).getEnumStub();
        setIconBase(ENUM_ICON_BASE);
        this.getCookieSet().add ( new EnumCodeGenerator ());
    }
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public String getDisplayName(){
        if (this.name == null) {
            if ( _enum != null){
                this.name = this._enum.name();
            }
            else
                this.name = "";
        }
        return this.name;
    }
  
    public Sheet createSheet(){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_EnumName")){
                public java.lang.Object getValue(){
                    return _enum.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_EnumId")){
                public java.lang.Object getValue(){
                    return _enum.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_EnumVersion")){
                public java.lang.Object getValue() {
                    return _enum.version();
                }
            });
        return s;
    }
    
    public org.omg.CORBA.Contained getOwner () {
        return this._enum;
    }
    
    public org.omg.CORBA.IRObject getIRObject () {
        return this._enum;
    }

}
