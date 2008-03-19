/*******************************************************************************
    Copyright 2008 Sun Microsystems, Inc.,
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

package com.sun.fortress.compiler.typechecker;

import com.sun.fortress.nodes.*;

import edu.rice.cs.plt.collect.Relation;
import edu.rice.cs.plt.tuple.Option;

import java.util.Collections;
import java.util.Set;

import static com.sun.fortress.nodes_util.NodeFactory.makeGenericArrowType;
import static edu.rice.cs.plt.tuple.Option.*;

/** 
 * A type environment whose outermost lexical scope consists of a map from
 * SimpleNames to FnDefs.
 */
class FnDefTypeEnv extends TypeEnv {
    private Relation<SimpleName, ? extends FnDef> entries;
    private TypeEnv parent;
    
    FnDefTypeEnv(Relation<SimpleName, ? extends FnDef> _entries, TypeEnv _parent) {
        entries = _entries;
        parent = _parent;
    }
    
    /**
     * Return a BindingLookup that binds the given SimpleName to a type
     * (if the given SimpleName is in this type environment).
     */
    public Option<BindingLookup> binding(SimpleName var) {
        Set<? extends FnDef> fns = entries.getSeconds(var);
        if (fns.isEmpty()) { return parent.binding(var); }

        Type type = Types.ANY;
        for (FnDef fn : fns) {
            type = new AndType(type,
                    makeGenericArrowType(fn.getSpan(),
                            fn.getStaticParams(),
                            typeFromParams(fn.getParams()),
                            unwrap(fn.getReturnType()), // all types have been filled in at this point
                            fn.getThrowsClause(),
                            fn.getWhere()));
        }
        return some(new BindingLookup(var, type));
    }
}
