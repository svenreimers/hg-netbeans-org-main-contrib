/*******************************************************************************
    Copyright 2007 Sun Microsystems, Inc.,
    4150 Network Circle, Santa Clara, California 95054, U.S.A.
    All rights reserved.

    U.S. Government Rights - Commercial software.
    Government users are subject to the Sun Microsystems, Inc. standard
    license agreement and applicable provisions of the FAR and its supplements.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.

    Sun, Sun Microsystems, the Sun logo and Java are trademarks or registered
    trademarks of Sun Microsystems, Inc. in the U.S. and other countries.
 ******************************************************************************/

package com.sun.fortress.syntax_abstractions.rules;

import xtc.parser.GrammarVisitor;

import com.sun.fortress.syntax_abstractions.rats.util.ModuleEnum;

/**
 * Rules should be compositional
 */
public abstract class Rule {

	protected ModuleEnum module;
	protected GrammarVisitor ruleRewriter;
	
	protected Rule() {}
	
	public Rule(ModuleEnum localdecl,
			GrammarVisitor visitor) {
		this.module = localdecl;
		this.ruleRewriter = visitor;
	}

	public ModuleEnum getModule() {
		return module;
	}
	
	public void setModule(ModuleEnum module) {
		this.module = module;
	}
	
	public GrammarVisitor getRuleRewriter() {
		return ruleRewriter;
	}
	
	public void setRuleRewriter(GrammarVisitor ruleRewriter) {
		this.ruleRewriter = ruleRewriter;
	}
}
