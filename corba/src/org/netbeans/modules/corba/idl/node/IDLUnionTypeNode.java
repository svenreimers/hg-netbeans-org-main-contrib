/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.netbeans.modules.corba.idl.src.UnionTypeElement;
import org.netbeans.modules.corba.idl.src.TypeElement;

/**
 * Class IDLUnionTypeNode
 *
 * @author Karel Gardas
 */
public class IDLUnionTypeNode extends IDLTypeNode {

    private static final String UNION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/union"; // NOI18N

    private UnionTypeElement _union_type;

    public IDLUnionTypeNode (TypeElement value) {
        super (value);
        _union_type = (UnionTypeElement) value;
        setIconBase (UNION_ICON_BASE);
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_UNION) { // NOI18N
		public Object getValue () {
		    return _type.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE) { // NOI18N
		public Object getValue () {
		    return _type.getType ().getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("switch type", String.class, IDLNodeBundle.SWITCH_TYPE, IDLNodeBundle.SWITCH_TYPE) { // NOI18N
		public Object getValue () {
		    return _union_type.getSwitchType ();
		}
	    });
        return s;
    }

}

/*
 * $Log
 * $
 */
