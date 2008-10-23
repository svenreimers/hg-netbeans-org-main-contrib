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

package org.netbeans.modules.scala.editing.nodes.exprs;

import org.netbeans.modules.scala.editing.nodes.*;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import java.util.List;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.lexer.Token;

/**
 *
 * @author Caoyuan Deng
 */
public class SimpleExpr extends AstExpression implements Postfixable {
        
    private AstNode base;
    
    private List<Type> typeArgs;
    private List<AstMirror> memberChain;
    
    private String prefixOp;
    private AstId postfixOp;
    
    public SimpleExpr(Token[] boundsTokens) {
        super(boundsTokens);
    }

    public void setBase(AstNode base) {
        this.base = base;
    }
    
    public AstNode getBase() {
        return base;
    }
    
    public void setTypeArgs(List<Type> typeArgs) {
        this.typeArgs = typeArgs;
    }
    
    public void setMemberChain(List<AstMirror> memberChain) {
        this.memberChain = memberChain;
    }

    public void setPrefix(String prefixOp) {
        this.prefixOp = prefixOp;
    }
 
    public void setPostfixOp(AstId postfixOp) {
        this.postfixOp = postfixOp;
    }

    public AstId getPostfixOp() {
        return postfixOp;
    }
        
    
    @Override
    public TypeMirror asType() {
        if (type != null) {
            return type;
        }
        
        if (memberChain != null && !memberChain.isEmpty()) {
            return memberChain.get(memberChain.size() - 1).asType();
        } else {
            return base.asType();
        }
    }
        
}