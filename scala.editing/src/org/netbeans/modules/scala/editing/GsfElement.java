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
package org.netbeans.modules.scala.editing;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Caoyuan Deng
 */
public class GsfElement implements ElementHandle {

    private Element element;
    private FileObject fileObject;
    private Object info;
    private ElementKind kind;
    private Set<Modifier> modifiers;
    private boolean deprecated;
    private boolean inherited;
    private boolean smart;
    private boolean scalaFromClass;

    public static ElementKind getGsfKind(Element element) {
        switch (element.getKind()) {
            case CLASS:
                return ElementKind.CLASS;
            case CONSTRUCTOR:
                return ElementKind.CONSTRUCTOR;
            case ENUM:
                return ElementKind.CLASS;
            case ENUM_CONSTANT:
                return ElementKind.CONSTANT;
            case EXCEPTION_PARAMETER:
                return ElementKind.OTHER;
            case FIELD:
                return ElementKind.FIELD;
            case INTERFACE:
                return ElementKind.MODULE;
            case LOCAL_VARIABLE:
                return ElementKind.VARIABLE;
            case METHOD:
                return ElementKind.METHOD;
            case OTHER:
                return ElementKind.OTHER;
            case PACKAGE:
                return ElementKind.PACKAGE;
            case PARAMETER:
                return ElementKind.PARAMETER;
            case TYPE_PARAMETER:
                return ElementKind.CLASS;
            default:
                return ElementKind.OTHER;
        }
    }

    public static Set<Modifier> getGsfModifiers(Element element) {
        Set<Modifier> modifiers = new HashSet<Modifier>();

        for (javax.lang.model.element.Modifier mod : element.getModifiers()) {
            switch (mod) {
                case PRIVATE:
                    modifiers.add(Modifier.PRIVATE);
                    continue;
                case PROTECTED:
                    modifiers.add(Modifier.PROTECTED);
                    continue;
                case PUBLIC:
                    modifiers.add(Modifier.PUBLIC);
                    continue;
                default:
                    continue;
            }
        }

        return modifiers;
    }

    /**
     * @param element, that to be wrapped
     * @param info, CompilationInfo
     */
    public GsfElement(Element element, FileObject fileObject, Object info) {
        this.element = element;
        this.fileObject = fileObject;
        this.info = info;
    }

    public GsfElement(ElementKind kind) {
        this.kind = kind;
    }

    public Element getElement() {
        return element;
    }

    public void setScalaFromClass() {
        scalaFromClass = true;
    }

    public boolean isScalaFromClass() {
        return scalaFromClass;
    }

    @Override
    public FileObject getFileObject() {
        if (info instanceof org.netbeans.modules.csl.spi.ParserResult) {
            return fileObject;
        } else if (info instanceof org.netbeans.api.java.source.CompilationInfo) {
            return JavaUtilities.getOriginFileObject((org.netbeans.api.java.source.CompilationInfo) info, element);
        } else {
            assert false;
            return null;
        }
    }

    @Override
    public String getIn() {
        if (isScala()) {
            return ((AstElement) element).getIn();
        } else {
            if (element != null) {
                Element enclElement = element.getEnclosingElement();
                if (enclElement instanceof PackageElement) {
                    return ((PackageElement) enclElement).getQualifiedName().toString();
                } else if (enclElement instanceof TypeElement) {
                    return ((TypeElement) enclElement).getQualifiedName().toString();
                } else {
                    return enclElement.toString();
                }
            } else {
                return "";
            }
        }
    }

    @Override
    public ElementKind getKind() {
        if (kind == null) {
            kind = getGsfKind(element);
        }
        return kind;
    }

    @Override
    public String getMimeType() {
        return isScala() ? ((AstElement) element).getMimeType() : "text/x-scala";
    }

    @Override
    public Set<Modifier> getModifiers() {
        if (modifiers == null) {
            modifiers = getGsfModifiers(element);
        }

        return modifiers;
    }

    @Override
    public String getName() {
        if (element == null) {
            return "";
        }

        String name = element.getSimpleName().toString();
        if (isScalaFromClass() && element.getKind() == javax.lang.model.element.ElementKind.METHOD) {
            return JavaScalaMapping.toScalaOpName(name);
        } else {
            return name;
        }
    }

    @Override
    public boolean signatureEquals(ElementHandle handle) {
        return false;
    }

    public Object getCompilationInfo() {
        return info;
    }

    public String getDocComment() {
        String docComment = null;
        if (info instanceof org.netbeans.modules.csl.spi.ParserResult) {
            docComment = ScalaUtils.getDocComment((org.netbeans.modules.csl.spi.ParserResult) info, (AstElement) element);
        } else if (info instanceof org.netbeans.api.java.source.CompilationInfo) {
            try {
                docComment = JavaUtilities.getDocComment((org.netbeans.api.java.source.CompilationInfo) info, element);
                if (docComment != null) {
                    docComment = "/**" + docComment + "*/";
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return docComment;
    }

    @Override
    public OffsetRange getOffsetRange(ParserResult result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getOffset() {
        int offset = 0;
        if (isScala()) {
            /** @Todo remove case of IndexedElement */
            if (element instanceof IndexedElement) {
                return ((IndexedElement) element).getOffset();
            }
            return ScalaUtils.getOffset((org.netbeans.modules.csl.spi.ParserResult) info, (AstElement) element);
        } else {
            try {
                offset = JavaUtilities.getOffset((org.netbeans.api.java.source.CompilationInfo) info, element);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return offset;
    }

    public void htmlFormat(HtmlFormatter formatter) {
        String in = getIn().trim();
        if (in.length() > 0) {
            formatter.appendText(in);
            formatter.appendText(".");
        }
        formatter.appendText(getName());
//        if (isScala()) {
//            ((AstElement) element).htmlFormat(formatter);
//        }
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isScala() {
        return element instanceof AstElement;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setSmart(boolean smart) {
        this.smart = smart;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
