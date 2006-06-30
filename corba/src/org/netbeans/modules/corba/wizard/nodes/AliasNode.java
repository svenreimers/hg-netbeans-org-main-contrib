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

package org.netbeans.modules.corba.wizard.nodes;

import java.util.StringTokenizer;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.AliasPanel;
/**
 *
 * @author  root
 * @version
 */
public class AliasNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/type";

    /** Creates new AliasNode */
    public AliasNode (NamedKey key) {
        super (key);
        this.setName(key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        for (int i=0; i<indent; i++)
            code = code + SPACE; // No I18N
        AliasKey key = (AliasKey) this.key;
        code = code + "typedef " + key.getType () +" ";
        code = code + this.getName() + " ";
        if (key.getLength().length () > 0) {
            StringTokenizer tk = new StringTokenizer (key.getLength(),",");
            while (tk.hasMoreTokens ()) {
                code = code +"["+ tk.nextToken().trim() +"] ";
            }
        }
        code = code.substring(0,code.length()-1) + ";\n";
        return code;
    }
    
    public ExPanel getEditPanel () {
        AliasPanel p = new AliasPanel ();
        String length = ((AliasKey)this.key).getLength();
        String type = ((AliasKey)this.key).getType();
        p.setName (this.getName());
        p.setLength(length);
        p.setType(type);
        return p;
    }
    
    public void reInit (ExPanel panel) {
        if (panel instanceof AliasPanel) {
            String newName = ((AliasPanel)panel).getName();
            if (!newName.equals(this.getName())) {
                this.setName (newName);
                ((AliasKey)this.key).setName(newName);
            }
            String newType = ((AliasPanel)panel).getType();
            if (!newType.equals(((AliasKey)this.key).getType()))
                ((AliasKey)this.key).setType(newType);
            String newLength = ((AliasPanel)panel).getLength();
            if (!newLength.equals(((AliasKey)this.key).getLength()))
                ((AliasKey)this.key).setLength(newLength);
        }
    }
  
}
