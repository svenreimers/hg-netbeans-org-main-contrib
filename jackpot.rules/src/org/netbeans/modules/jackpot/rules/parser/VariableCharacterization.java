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
package org.netbeans.modules.jackpot.rules.parser;

import org.netbeans.api.java.source.*;
import com.sun.source.util.TreeScanner;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.*;

import java.util.HashMap;

class VariableCharacterization {
    public static final int ASSIGN     = 1<<0;
    public static final int REFERENCE  = 1<<1;
    public static final int PARAMETER  = 1<<5;

    final HashMap<Object,CNode> map = new HashMap<Object,CNode>();
    
    public VariableCharacterization(Tree t) {
	new TreeScanner<Void,Tree>() {
	    TypeSymbol owner = null;
	    public Void visitIdentifier(IdentifierTree tree, Tree parent) {
                JCIdent t = (JCIdent)tree;
		Symbol sym = t.sym;
		checkBoth(t.name, sym, parentContext(tree, parent));
                return null;
	    }
	    public Void visitMemberSelect(MemberSelectTree tree, Tree parent) {
                JCFieldAccess t = (JCFieldAccess)tree;
		checkBoth(t.name, t.sym, parentContext(tree, parent));
		super.visitMemberSelect(tree, parent);
                return null;
	    }
	    public Void visitNewClass(NewClassTree tree, Tree parent) {
		checkBoth(null, ((JCNewClass)tree).constructor, 0);
		super.visitNewClass(tree, parent);
                return null;
	    }
	    public Void visitVariable(VariableTree tree, Tree parent) {
		int flags = 0;
		if(parent!=null && (parent.getKind() == Kind.METHOD || parent.getKind() == Kind.CATCH))
		    flags|=PARAMETER;
                JCVariableDecl t = (JCVariableDecl)tree;
		checkBoth(t.name, t.sym, flags);
		super.visitVariable(tree, parent);
                return null;
	    }
	    public Void visitMethod(MethodTree tree, Tree parent) {
		TypeSymbol oldOwner = owner;
                JCMethodDecl t = (JCMethodDecl)tree;
		if (t.sym != null) 
                    owner = (TypeSymbol) t.sym.owner;
		super.visitMethod(tree, parent);
		owner = oldOwner;
                return null;
	    }
	    public Void visitClass(ClassTree tree, Tree parent) {
		TypeSymbol oldOwner = owner;
                JCClassDecl t = (JCClassDecl)tree;
		if (t.sym != null && t.sym.owner instanceof TypeSymbol) 
                    owner = (TypeSymbol) t.sym.owner;
		super.visitClass(tree, parent);
		owner = oldOwner;
                return null;
	    }
	    int parentContext(Tree tree, Tree parent) {
		if(parent==null) return 0;
		int tag = ((JCTree)parent).tag;
		if(JCTree.BITOR_ASG<=tag && tag<=JCTree.MOD_ASG) {
		    if(((JCAssignOp)parent).lhs==tree) return ASSIGN;
		}
		else if(tag==JCTree.ASSIGN && ((JCAssign)parent).lhs==tree) return ASSIGN;
		return REFERENCE;
	    }
	    private void checkBoth(Name name, Symbol sym, int flags) {
		if(name!=null) checkOne(name, flags);
		if(sym!=null) checkOne(sym, flags);
	    }
	    private boolean checkOne(Object key, int flags) {
		CNode c = map.get(key);
		if(c==null) {
		    map.put(key,c=new CNode());
		    c.usage |= flags;
		    return true;
		}
		c.usage |= flags;
		return false;
	    }
	}.scan(t, null);
    }
    
    private int usage(Object s) {
	CNode cn = map.get(s);
	return cn==null ? 0 : cn.usage;
    }

    public final boolean assigned(Object s) {
	return (usage(s)&ASSIGN)!=0;
    }

    public final boolean referenced(Object s) {
	return (usage(s)&REFERENCE)!=0;
    }

    public final boolean parameter(Object s) {
	return (usage(s)&PARAMETER)!=0;
    }
    
    private static class CNode {
	int usage;
    }
}
