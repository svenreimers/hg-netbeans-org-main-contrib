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

package org.netbeans.modules.corba.poasupport.nodes;

import java.io.IOException;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** A simple node with no children.
 *
 * @author Dusan Balek
 */
public abstract class POAMemberElementNode extends AbstractNode implements java.beans.PropertyChangeListener {

    protected POAMemberElement element = null;
    
    private static final SystemAction[] DEFAULT_ACTIONS = new SystemAction[] {
        SystemAction.get (DeleteAction.class),
        SystemAction.get (RenameAction.class),
        null,
        //        SystemAction.get (CutAction.class),
        //        SystemAction.get (CopyAction.class),
        SystemAction.get (PropertiesAction.class)
    };
    
    private static final SystemAction[] NON_WRITEABLE_DEFAULT_ACTIONS = new SystemAction[] {
        SystemAction.get (PropertiesAction.class)
    };
    
    public POAMemberElementNode(POAMemberElement _element) {
        super (Children.LEAF);
        element = _element;
        CookieSet cookie = getCookieSet ();
        cookie.add (element.getOpenCookie());
        super.setName (element.getVarName());
        setActions();
        element.addPropertyChangeListener(this);
    }
    
    /** Set all actions for this node.
     * @param actions new list of actions
     */
    public void setActions() {
        if (!isWriteable())
            systemActions = NON_WRITEABLE_DEFAULT_ACTIONS;
        else
            systemActions = DEFAULT_ACTIONS;
    }
    
    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        getPOAMemberElement().setLinePosition();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }
    
    public boolean isWriteable() {
        return getPOAMemberElement().isWriteable();
    }
    
    public POAMemberElement getPOAMemberElement () {
        return element;
    }
    
    // Handle renaming:
    public boolean canRename () {
        return isWriteable();
    }
    
    public void setName (String nue) {
        super.setName (nue);
    }
    
    // Handle deleting:
    public boolean canDestroy () {
        return isWriteable();
    }
    
    public void destroy () throws IOException {
        if (isWriteable()) {
            ((POAChildren)((POANode)getParentNode()).getChildren ()).addNotify ();
            super.destroy ();
        }
    }
    
/*
    // Handle copying and cutting specially:
    public boolean canCopy () {
        return true;
    }
 
    public boolean canCut () {
        return true;
    }
 */
    
    // Create a property sheet:
    protected Sheet createSheet () {
        Sheet sheet = super.createSheet ();
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        if (ps == null) {
            ps = Sheet.createPropertiesSet ();
            sheet.put (ps);
        }
        ps.put(new PropertySupport.ReadWrite ("var", String.class, POASupport.getString("LBL_POAMemberSheet_Var"), POASupport.getString("MSG_POAMemberSheet_Var")) { // NOI18N
            public Object getValue () {
                return getPOAMemberElement().getVarName ();
            }
            public void setValue(Object value) {
                if (value.equals(getPOAMemberElement().getVarName()))
                    return;
                if (POAChecker.checkPOAMemberVarName((String)value, getPOAMemberElement(), (getPOAMemberElement().getTypeName() != null) && (getPOAMemberElement().getConstructor() != null), false))
                    getPOAMemberElement().setVarName((String)value);
            }
            public boolean canWrite() {
                return isWriteable();
            }
        });
        ps.put(new PropertySupport.ReadWrite ("type", String.class, POASupport.getString("LBL_POAMemberSheet_Type"), POASupport.getString("MSG_POAMemberSheet_Type")) { // NOI18N
            public Object getValue () {
                return getPOAMemberElement().getTypeName ();
            }
            public void setValue(Object value) {
                if (value == null || value.equals("")) // NOI18N
                    getPOAMemberElement().setTypeName(null);
                else if (POAChecker.checkTypeName((String)value, false))
                    getPOAMemberElement().setTypeName((String)value);
            }
            public boolean canWrite() {
                return isWriteable();
            }
        });
        ps.put(new PropertySupport.ReadWrite ("constructor", String.class, POASupport.getString("LBL_POAMemberSheet_Ctor"), POASupport.getString("MSG_POAMemberSheet_Ctor")) { // NOI18N
            public Object getValue () {
                return getPOAMemberElement().getConstructor ();
            }
            public void setValue(Object value) {
                if (value == null || value.equals("")) // NOI18N
                    getPOAMemberElement().setConstructor(null);
                else
                    getPOAMemberElement().setConstructor((String)value);
            }
            public boolean canWrite() {
                return isWriteable();
            }
        });
        return sheet;
    }
    
    // Permit user to customize whole node at once (instead of per-property):
    public boolean hasCustomizer () {
        return isWriteable();
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (p1.getSource() == null)
            return;
        if (p1.getSource() != getPOAMemberElement())
            return;
        if (POAMemberElement.PROP_VAR_NAME.equals(p1.getPropertyName())) {
            super.setName((String)p1.getNewValue());
            return;
        }
        super.firePropertyChange(p1.getPropertyName(), p1.getOldValue(), p1.getNewValue());
    }
    
    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    /*
    public Node cloneNode () {
        // Try to pass in similar constructor params to what you originally got:
        return new ServantNode ();
    }
     */
    
    /*
    public Transferable clipboardCopy () {
        // Add to, do not replace, the default node copy flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCopy ());
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
    public Transferable clipboardCut () {
        // Add to, do not replace, the default node cut flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCut ());
        // This is not so useful because this node will not be destroyed afterwards
        // (it is up to the paste type to decide whether to remove the "original",
        // and it is not safe to assume that getData will only be called once):
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
     */
    
}
