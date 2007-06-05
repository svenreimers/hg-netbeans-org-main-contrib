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

package org.netbeans.api.registry;

import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

public class ContextTest extends NbTestCase {
    public ContextTest (String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ContextTest.class));
    }

    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testContext() throws Exception {
        Context subctx = getContext().createSubcontext ("a");
        subctx = subctx.createSubcontext ("b");
        subctx = subctx.createSubcontext ("c");
        subctx = subctx.createSubcontext ("d");
        subctx = subctx.createSubcontext ("e");
        assertEquals ("getAbsoluteContextName does not work", subctx.getAbsoluteContextName(), "/a/b/c/d/e");
        subctx = subctx.getParentContext();
        assertEquals ("getParentContext does not work", subctx.getContextName(), "d");
        subctx = subctx.getParentContext();
        assertEquals ("getParentContext does not work", subctx.getContextName(), "c");
        subctx = subctx.getParentContext();
        assertEquals ("getParentContext does not work", subctx.getContextName(), "b");
        subctx = subctx.getParentContext();
        assertEquals ("getParentContext does not work", subctx.getContextName(), "a");
        subctx = subctx.getParentContext();
        assertEquals ("getParentContext does not work", subctx.getContextName(), "/");
        getContext().destroySubcontext("a");
    }

    protected Context getContext() {
        return Context.getDefault();
    }
}