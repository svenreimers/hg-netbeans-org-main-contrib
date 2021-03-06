/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.editor.ast.nodes;

import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.Visitor;

/**
 * Represents a function/procedure formal parameter
 * <pre>e.g.<pre>
 * procedure Foo (a : in bar; b : in out foo_bar; c : out boolean)
 * function Foo (a : in bar; b : in foo_bar) return boolean
 */
public class FormalParameter extends ASTNode {

    public enum Mode {
        IN, OUT, IN_OUT, ACCESS;
    }

    private Variable parameterName;
    private Mode parameterMode;
	private TypeName parameterType;
    private Expression defaultValue;

    public FormalParameter(int start, int end, final Variable parameterName, Mode parameterMode, TypeName type, Expression defaultValue) {
        super(start, end);

        this.parameterName = parameterName;
        this.parameterMode = parameterMode;
        this.parameterType = type;
        this.defaultValue = defaultValue;
    }

    public FormalParameter(int start, int end, final Variable parameterName, Mode parameterMode, TypeName type) {
        this(start, end, parameterName, parameterMode, type, null);
    }

    /**
     * @return default value of this parameter
     */
    public Expression getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return the name of this parameter
     */
    public Variable getParameterName() {
        return parameterName;
    }

	/**
     * @return the mode of this parameter
     */
    public Mode getParameterMode() {
        return parameterMode;
    }

    /**
     * @return the type of this parameter
     */
    public TypeName getParameterType() {
        return parameterType;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
