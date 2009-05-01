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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.java.source.JavaSourceAccessor;
import org.netbeans.modules.scala.editing.ScalaCodeCompletion.CompletionRequest;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.openide.util.Exceptions;

/**
 * 
 * @author Caoyuan Deng 
 */
public abstract class ScalaCompletionItem implements CompletionProposal {

    private static ImageIcon keywordIcon;
    protected CompletionRequest request;
    protected GsfElement element;
    protected IndexedElement indexedElement;

    private ScalaCompletionItem(GsfElement element, CompletionRequest request) {
        this.element = element;
        this.request = request;
    }

    private ScalaCompletionItem(CompletionRequest request, IndexedElement element) {
        this(new GsfElement(element, request.fileObject, request.info), request);
        this.indexedElement = element;
    }

    public int getAnchorOffset() {
        return request.anchor;
    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public String getInsertPrefix() {
        return getName();
//            if (getKind() == ElementKind.PACKAGE) {
//                return getSimpleName() + ".";
//            } else {
//                return getSimpleName();
//            }
    }

    @Override
    public String getSortText() {
        return getName();
    }

    public int getSortPrioOverride() {
        return 0;
    }

    @Override
    public ElementHandle getElement() {
        return element;
    }

    @Override
    public org.netbeans.modules.csl.api.ElementKind getKind() {
        return getElement().getKind();
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getLhsHtml(HtmlFormatter formatter) {
        org.netbeans.modules.csl.api.ElementKind kind = getKind();
        boolean emphasize = (kind != org.netbeans.modules.csl.api.ElementKind.PACKAGE && indexedElement != null) ? !indexedElement.isInherited() : false;
        if (emphasize) {
            formatter.emphasis(true);
        }
        boolean strike = indexedElement != null && indexedElement.isDeprecated();
        if (strike) {
            formatter.deprecated(true);
        }
        formatter.name(kind, true);
        formatter.appendText(getName());
        formatter.name(kind, false);
        if (strike) {
            formatter.deprecated(false);
        }
        if (emphasize) {
            formatter.emphasis(false);
        }

        if (indexedElement != null) {
            TypeMirror type = indexedElement.asType();
            if (type != null) {
                formatter.appendHtml(" :"); // NOI18N
                formatter.type(true);
                formatter.appendText(type.toString());
                formatter.type(false);
            }
        }

        return formatter.getText();
    }

    @Override
    public String getRhsHtml(HtmlFormatter formatter) {
        if (element.getKind() == ElementKind.PACKAGE || element.getKind() == ElementKind.CLASS) {
            if (element.getElement() instanceof IndexedElement) {
                String origin = ((IndexedElement) element.getElement()).getOrigin();
                if (origin != null) {
                    formatter.appendText(origin);
                    return formatter.getText();
                }
            }

            return null;
        }

        String in = element.getIn();

        if (in != null) {
            formatter.appendText(in);
            return formatter.getText();
        } else if (element.getElement() instanceof IndexedElement) {
            IndexedElement ie = (IndexedElement) element.getElement();
            String filename = ie.getFileObject().getPath();
            if (filename != null) {
                if (filename.indexOf("jsstubs") == -1) { // NOI18N

                    int index = filename.lastIndexOf('/');
                    if (index != -1) {
                        filename = filename.substring(index + 1);
                    }
                    formatter.appendText(filename);
                    return formatter.getText();
                } else {
                    String origin = ie.getOrigin();
                    if (origin != null) {
                        formatter.appendText(origin);
                        return formatter.getText();
                    }
                }
            }

            return null;
        }

        return null;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return getElement().getModifiers();
    }

    @Override
    public String toString() {
        String cls = this.getClass().getName();
        cls = cls.substring(cls.lastIndexOf('.') + 1);

        return cls + "(" + getKind() + "): " + getName();
    }

    public boolean isSmart() {
        return false;
    //return indexedElement != null ? indexedElement.isSmart() : true;
    }

    @Override
    public String getCustomInsertTemplate() {
        return null;
    }

    protected static class FunctionItem extends ScalaCompletionItem {

        private ExecutableElement function;

        FunctionItem(GsfElement element, CompletionRequest request) {
            super(element, request);
            assert element.getElement() instanceof ExecutableElement;
            function = (ExecutableElement) element.getElement();
        }

        FunctionItem(IndexedElement element, CompletionRequest request) {
            super(request, element);
        //function = (IndexedFunction) element;
        }

        @Override
        public String getInsertPrefix() {
            return getName();
        }

        @Override
        public org.netbeans.modules.csl.api.ElementKind getKind() {
            return org.netbeans.modules.csl.api.ElementKind.METHOD;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            JavaSourceAccessor.getINSTANCE().lockJavaCompiler();
            try {

                org.netbeans.modules.csl.api.ElementKind kind = getKind();
                boolean strike = false;
                if (!strike && element.isDeprecated()) {
                    strike = true;
                }
                if (strike) {
                    formatter.deprecated(true);
                }
                boolean emphasize = !element.isInherited();
                if (emphasize) {
                    formatter.emphasis(true);
                }
                formatter.name(kind, true);
                formatter.appendText(getName());
                formatter.name(kind, false);
                if (emphasize) {
                    formatter.emphasis(false);
                }
                if (strike) {
                    formatter.deprecated(false);
                }
                List<? extends TypeParameterElement> typeParams = function.getTypeParameters();
                if (!typeParams.isEmpty()) {
                    formatter.appendHtml("[");

                    for (Iterator<? extends TypeParameterElement> itr = typeParams.iterator(); itr.hasNext();) {
                        TypeParameterElement typeParam = itr.next();
                        TypeParam.htmlFormat(typeParam, formatter);

                        if (itr.hasNext()) {
                            formatter.appendText(", "); // NOI18N
                        }
                    }

                    formatter.appendHtml("]");
                }


                List<? extends VariableElement> params = function.getParameters();
                if (!params.isEmpty()) {
                    formatter.appendHtml("("); // NOI18N

                    for (Iterator<? extends VariableElement> itr = params.iterator(); itr.hasNext();) { // && tIt.hasNext()) {
                        formatter.parameters(true);

                        VariableElement param = itr.next();

                        formatter.appendText(param.getSimpleName().toString());
                        formatter.parameters(false);
                        formatter.appendHtml(" :");
                        formatter.parameters(true);

                        TypeMirror paramType = param.asType();
    //                    int funParamNum = JavaScalaMapping.isFunctionType(paramType);
    //                    if (funParamNum != -1) {
    //                        List<? extends AnnotationMirror> annots = function.getAnnotationMirrors();
    //
    //                        List<VariableElement> funParams = new ArrayList<VariableElement>(funParamNum);
    //                        for (int i = 0; i < funParamNum; i++) {
    //                            if (itr.hasNext()) {
    //                                funParams.add(itr.next());
    //                            }
    //                        }
    //                        formatter.appendText(JavaScalaMapping.classFunctionToScalaSName(paramType, funParamNum, funParams));
    //                    } else {
                            if (param.asType() != null) {
                                formatter.type(true);
                                Type.htmlFormat(formatter, paramType, true);
                                formatter.type(false);
                            }
    //                    }

                        formatter.parameters(false);

                        if (itr.hasNext()) {
                            formatter.appendText(", "); // NOI18N
                        }

                    }

                    formatter.appendHtml(")"); // NOI18N
                }

                TypeMirror retType = function.getReturnType();
                if (retType != null && element.getKind() != ElementKind.CONSTRUCTOR) {
                    formatter.appendHtml(" :");
                    formatter.type(true);
                    Type.htmlFormat(formatter, retType, true);
                    formatter.type(false);
                }
            } finally {
              JavaSourceAccessor.getINSTANCE().unlockJavaCompiler();
            }

            return formatter.getText();
        }

        public List<String> getInsertParams() {
            List<? extends VariableElement> params = function.getParameters();
            if (!params.isEmpty()) {
                List<String> insertParams = new ArrayList<String>();
                for (VariableElement param : params) {
                    insertParams.add(param.getSimpleName().toString());
                }
                return insertParams;
            } else {
                return Collections.<String>emptyList();
            }
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder sb = new StringBuilder();

            final String insertPrefix = getInsertPrefix();
            sb.append(insertPrefix);


            if (function.getParameters().isEmpty()) {
                return sb.toString();
            }

            List<String> params = getInsertParams();
            String startDelimiter = "(";
            String endDelimiter = ")";
            int paramCount = params.size();

            sb.append(startDelimiter);

            int id = 1;
            for (int i = 0; i < paramCount; i++) {
                String paramDesc = params.get(i);
                sb.append("${"); //NOI18N
                // Ensure that we don't use one of the "known" logical parameters
                // such that a parameter like "path" gets replaced with the source file
                // path!

                sb.append("js-cc-"); // NOI18N

                sb.append(Integer.toString(id++));
                sb.append(" default=\""); // NOI18N

                int typeIndex = paramDesc.indexOf(':');
                if (typeIndex != -1) {
                    sb.append(paramDesc, 0, typeIndex);
                } else {
                    sb.append(paramDesc);
                }
                sb.append("\""); // NOI18N

                sb.append("}"); //NOI18N

                if (i < paramCount - 1) {
                    sb.append(", "); //NOI18N

                }
            }
            sb.append(endDelimiter);

            sb.append("${cursor}"); // NOI18N

            // Facilitate method parameter completion on this item
            try {
                ScalaCodeCompletion.callLineStart = Utilities.getRowStart(request.doc, request.anchor);
                ScalaCodeCompletion.callMethod = function;
            } catch (BadLocationException ble) {
                Exceptions.printStackTrace(ble);
            }

            return sb.toString();
        }
    }

    protected static class KeywordItem extends ScalaCompletionItem {

        private static final String KEYWORD = "org/netbeans/modules/scala/editing/resources/scala16x16.png"; //NOI18N
        private final String keyword;
        private final String description;

        KeywordItem(String keyword, String description, CompletionRequest request) {
            super(null, request);
            this.keyword = keyword;
            this.description = description;
        }

        @Override
        public String getName() {
            return keyword;
        }

        @Override
        public org.netbeans.modules.csl.api.ElementKind getKind() {
            return org.netbeans.modules.csl.api.ElementKind.KEYWORD;
        }

        //@Override
        //public String getLhsHtml() {
        //    // Override so we can put HTML contents in
        //    ElementKind kind = getKind();
        //    HtmlFormatter formatter = request.formatter;
        //    formatter.reset();
        //    formatter.name(kind, true);
        //    //formatter.appendText(getSimpleName());
        //    formatter.appendHtml(getSimpleName());
        //    formatter.name(kind, false);
        //
        //    return formatter.getText();
        //}
        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (description != null) {
                //formatter.appendText(description);
                formatter.appendHtml(description);

                return formatter.getText();
            } else {
                return null;
            }
        }

        @Override
        public ImageIcon getIcon() {
            if (keywordIcon == null) {
                keywordIcon = new ImageIcon(org.openide.util.Utilities.loadImage(KEYWORD));
            }

            return keywordIcon;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ElementHandle getElement() {
            // For completion documentation
            return new GsfElement(org.netbeans.modules.csl.api.ElementKind.KEYWORD);
        }

        @Override
        public boolean isSmart() {
            return false;
        }
    }

    protected static class TagItem extends ScalaCompletionItem {

        private final String tag;
        private final String description;
        private final org.netbeans.modules.csl.api.ElementKind kind;

        TagItem(String keyword, String description, CompletionRequest request, org.netbeans.modules.csl.api.ElementKind kind) {
            super(null, request);
            this.tag = keyword;
            this.description = description;
            this.kind = kind;
        }

        @Override
        public String getName() {
            return tag;
        }

        @Override
        public org.netbeans.modules.csl.api.ElementKind getKind() {
            return kind;
        }

        //@Override
        //public String getLhsHtml() {
        //    // Override so we can put HTML contents in
        //    ElementKind kind = getKind();
        //    HtmlFormatter formatter = request.formatter;
        //    formatter.reset();
        //    formatter.name(kind, true);
        //    //formatter.appendText(getSimpleName());
        //    formatter.appendHtml(getSimpleName());
        //    formatter.name(kind, false);
        //
        //    return formatter.getText();
        //}
        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (description != null) {
                //formatter.appendText(description);
                formatter.appendHtml("<i>");
                formatter.appendHtml(description);
                formatter.appendHtml("</i>");

                return formatter.getText();
            } else {
                return null;
            }
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ElementHandle getElement() {
            // For completion documentation
            return new GsfElement(org.netbeans.modules.csl.api.ElementKind.KEYWORD);
        }

        @Override
        public boolean isSmart() {
            return true;
        }
    }

    protected static class PlainItem extends ScalaCompletionItem {

        PlainItem(GsfElement element, CompletionRequest request) {
            super(element, request);
        }

        PlainItem(CompletionRequest request, IndexedElement element) {
            super(request, element);
        }
    }

    protected static class PackageItem extends ScalaCompletionItem {

        PackageItem(GsfElement element, CompletionRequest request) {
            super(element, request);

        }

        PackageItem(CompletionRequest request, IndexedElement element) {
            super(request, element);
        }

        @Override
        public org.netbeans.modules.csl.api.ElementKind getKind() {
            return org.netbeans.modules.csl.api.ElementKind.PACKAGE;
        }

        @Override
        public String getName() {
            String name = element.getElement().getSimpleName().toString();
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                name = name.substring(lastDot + 1, name.length());
            }
            return name;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            org.netbeans.modules.csl.api.ElementKind kind = getKind();
            boolean strike = indexedElement != null && indexedElement.isDeprecated();
            if (strike) {
                formatter.deprecated(true);
            }
            formatter.name(kind, true);
            formatter.appendText(getName());
            formatter.name(kind, false);
            if (strike) {
                formatter.deprecated(false);
            }

            return formatter.getText();
        }

        @Override
        public boolean isSmart() {
            return true;
        }
    }

    protected static class TypeItem extends ScalaCompletionItem {

        TypeItem(GsfElement element, CompletionRequest request) {
            super(element, request);

        }

        TypeItem(CompletionRequest request, IndexedElement element) {
            super(request, element);
        }

        @Override
        public org.netbeans.modules.csl.api.ElementKind getKind() {
            return org.netbeans.modules.csl.api.ElementKind.CLASS;
        }

        @Override
        public String getName() {
            String name = element.getElement().getSimpleName().toString();
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                name = name.substring(lastDot + 1, name.length());
            }
            return name;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            org.netbeans.modules.csl.api.ElementKind kind = getKind();
            boolean strike = indexedElement != null && indexedElement.isDeprecated();
            if (strike) {
                formatter.deprecated(true);
            }
            formatter.name(kind, true);
            formatter.appendText(getName());
            formatter.name(kind, false);
            if (strike) {
                formatter.deprecated(false);
            }

            return formatter.getText();
        }
    }

}
