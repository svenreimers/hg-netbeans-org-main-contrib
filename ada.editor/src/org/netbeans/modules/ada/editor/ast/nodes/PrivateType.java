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

import org.netbeans.modules.ada.editor.ast.nodes.visitors.Visitor;

/**
 *
 * @author Andrea Lucarelli
 */
public class PrivateType extends Expression {

    protected final static Integer PRIVATE = new Integer(BodyDeclaration.Modifier.PRIVATE);
    protected final static Integer TAGGED = new Integer(BodyDeclaration.Modifier.TAGGED);
    protected final static Integer LIMITED = new Integer(BodyDeclaration.Modifier.LIMITED);
    protected final static Integer ABSTRACT = new Integer(BodyDeclaration.Modifier.ABSTRACT);

    private Integer modifier;

    public PrivateType(int start, int end, Integer modifier) {
        super(start, end);
        this.modifier = modifier;
    }

    public Integer getModifier() {
        return modifier;
    }

    public void setPrivate () {
        if ((this.modifier & PRIVATE) != PRIVATE) {
            this.modifier |= PRIVATE;
        }
    }

    public void setTagged () {
        if ((this.modifier & TAGGED) != TAGGED) {
            this.modifier |= TAGGED;
        }
    }

    public void setLimited () {
        if ((this.modifier & LIMITED) != LIMITED) {
            this.modifier |= LIMITED;
        }
    }

    public void setAbstract () {
        if ((this.modifier & ABSTRACT) != ABSTRACT) {
            this.modifier |= ABSTRACT;
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
