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
package org.netbeans.modules.scala.editing.nodes;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.netbeans.modules.gsf.api.HtmlFormatter;

/**
 *
 * @author Caoyuan Deng
 */
public class Var extends AstElement implements VariableElement {

    private boolean val;
    private boolean implicate;
    private AstExpression expr;

    public Var(AstId id, AstScope bindingScope, ElementKind kind) {
        super(id.getSimpleName().toString(), id.getPickToken(), bindingScope, kind);
        setType(id.asType());
    }

    public Object getConstantValue() {
        return null;
    }

    public void setVal() {
        val = true;
    }

    public boolean isVal() {
        return val;
    }

    public void setImplicate() {
        implicate = true;
    }

    public boolean getImplicate() {
        return implicate;
    }

    public void setExpr(AstExpression expr) {
        this.expr = expr;
        getBindingScope().addExpression(expr);
    }

    @Override
    public boolean isMirroredBy(AstMirror mirror) {
        if (mirror instanceof IdCall) {
            return getSimpleName().toString().equals(mirror.getSimpleName().toString());
        }
        
        return false;
    }

    @Override
    public Type asType() {
        if (type != null) {
            return type;
        }

        if (expr != null) {
            return expr.asType();
        }

        return null;
    }

    @Override
    public boolean mayEqual(AstElement element) {
        switch (element.getKind()) {
            case LOCAL_VARIABLE:
            case PARAMETER:
            case FIELD:
                return getSimpleName().toString().equals(element.getSimpleName().toString());
            default:
                return false;
        }
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        super.htmlFormat(formatter);
        Type myType = asType();
        if (myType != null) {
            formatter.type(true);
            formatter.appendHtml(" :");
            myType.htmlFormat(formatter);
            formatter.type(false);
        }
    }
}
