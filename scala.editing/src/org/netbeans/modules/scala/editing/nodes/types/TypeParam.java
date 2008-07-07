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
package org.netbeans.modules.scala.editing.nodes.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.AstId;

/**
 *
 * @author Caoyuan Deng
 */
public class TypeParam extends TypeDef implements TypeParameterElement {

    private String bound;
    private Type boundType;
    private String variant;
    private List<TypeParam> params;

    public TypeParam(AstId id, AstScope bindingScope) {
        super(id, bindingScope);
    }

    public List<? extends TypeMirror> getBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element getGenericElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBound(String bound) {
        this.bound = bound;
    }

    public String getBound() {
        return bound;
    }

    public void setBoundType(Type boundType) {
        this.boundType = boundType;
    }

    public Type getBoundType() {
        return boundType;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    public void setParams(List<TypeParam> params) {
        this.params = params;
    }

    public List<TypeParam> getParams() {
        return params == null ? Collections.<TypeParam>emptyList() : params;
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        htmlFormat(this, formatter);
    }

    public static void htmlFormat(TypeParameterElement typeParam, HtmlFormatter formatter) {
        formatter.appendText(typeParam.getSimpleName().toString());
        if (typeParam instanceof TypeParam) {
            if (!((TypeParam) typeParam).getParams().isEmpty()) {
                Iterator<TypeParam> itr = ((TypeParam) typeParam).getParams().iterator();
                formatter.appendText("[");
                while (itr.hasNext()) {
                    htmlFormat(itr.next(), formatter);
                    
                    if (itr.hasNext()) {
                        formatter.appendText(", ");
                    }
                }
                formatter.appendText("]");
            }
        } else {
            /** @todo */
        }
    }
}