/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
// *
// * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.edm.editor.widgets.property;

import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

/**
 *
 * @author karthikeyan s
 */
public class JoinNode extends AbstractNode {
    
    private SQLJoinOperator joinOp;
    
    private MashupDataObject mObj;
    
    public JoinNode(SQLJoinOperator obj, MashupDataObject dObj) {
        super(Children.LEAF);
        mObj = dObj;
        joinOp = obj;
    }
    
    @Override
    public boolean canCopy() {
        return false;
    }
    
    @Override
    public boolean canRename() {
        return false;
    }
    
    @Override
    public boolean canCut() {
        return false;
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
    
    /** Creates a property sheet. */
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.createPropertiesSet();
        try {
            PropertySupport.Reflection joinConditionProp = new PropertySupport.Reflection(
                    this.joinOp.getJoinCondition(), String.class,
                    "getConditionText", "setConditionText");
            joinConditionProp.setName("Join Condition");
            joinConditionProp.setPropertyEditorClass(
                    PropertyEditorManager.getPropertyEditor("JOINCONDITION"));
            set.put(joinConditionProp);
            
            PropertySupport.Reflection joinTypeProp = new PropertySupport.Reflection(
                    this.joinOp, String.class,
                    "getJoinTypeString", "setJoinType");
            joinTypeProp.setName("Join Type");
            joinTypeProp.setPropertyEditorClass(
                    PropertyEditorManager.getPropertyEditor("JOINTYPE"));
            set.put(joinTypeProp);
            
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        sheet.put(set);
        return sheet;
    }
    
    public SQLJoinOperator getJoinOperator() {
        return this.joinOp;
    }
    
    public MashupDataObject getMashupDataObject() {
        return mObj;
    }
}