/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.json;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Completions;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Model;
import net.java.html.json.Property;
import org.openide.util.lookup.ServiceProvider;

/** Annotation processor to process {@link Model @Model} annotations and
 * generate appropriate model classes.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
@ServiceProvider(service=Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({
    "net.java.html.json.Model",
    "net.java.html.json.ModelOperation",
    "net.java.html.json.Function",
    "net.java.html.json.OnReceive",
    "net.java.html.json.OnPropertyChange",
    "net.java.html.json.ComputedProperty",
    "net.java.html.json.Property"
})
public final class ModelProcessor extends AbstractProcessor {
    private static final Logger LOG = Logger.getLogger(ModelProcessor.class.getName());
    private final Map<Element,String> models = new WeakHashMap<Element,String>();
    private final Map<Element,Prprt[]> verify = new WeakHashMap<Element,Prprt[]>();
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean ok = true;
        for (Element e : roundEnv.getElementsAnnotatedWith(Model.class)) {
            if (!processModel(e)) {
                ok = false;
            }
        }
        if (roundEnv.processingOver()) {
            models.clear();
            for (Map.Entry<Element, Prprt[]> entry : verify.entrySet()) {
                TypeElement te = (TypeElement)entry.getKey();
                String fqn = processingEnv.getElementUtils().getBinaryName(te).toString();
                Element finalElem = processingEnv.getElementUtils().getTypeElement(fqn);
                if (finalElem == null) {
                    continue;
                }
                Prprt[] props;
                Model m = finalElem.getAnnotation(Model.class);
                if (m == null) {
                    continue;
                }
                props = Prprt.wrap(processingEnv, finalElem, m.properties());
                for (Prprt p : props) {
                    boolean[] isModel = { false };
                    boolean[] isEnum = { false };
                    boolean[] isPrimitive = { false };
                    String t = checkType(p, isModel, isEnum, isPrimitive);
                    if (isEnum[0]) {
                        continue;
                    }
                    if (isPrimitive[0]) {
                        continue;
                    }
                    if (isModel[0]) {
                        continue;
                    }
                    if ("java.lang.String".equals(t)) {
                        continue;
                    }
                    error("The type " + t + " should be defined by @Model annotation", entry.getKey());
                }
            }
            verify.clear();
        }
        return ok;
    }

    private void error(String msg, Element e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
    
    private boolean processModel(Element e) {
        boolean ok = true;
        Model m = e.getAnnotation(Model.class);
        if (m == null) {
            return true;
        }
        String pkg = findPkgName(e);
        Writer w;
        String className = m.className();
        models.put(e, className);
        try {
            StringWriter body = new StringWriter();
            List<String> propsGetSet = new ArrayList<String>();
            List<String> functions = new ArrayList<String>();
            Map<String, Collection<String>> propsDeps = new HashMap<String, Collection<String>>();
            Map<String, Collection<String>> functionDeps = new HashMap<String, Collection<String>>();
            Prprt[] props = createProps(e, m.properties());
            
            if (!generateComputedProperties(body, props, e.getEnclosedElements(), propsGetSet, propsDeps)) {
                ok = false;
            }
            if (!generateProperties(e, body, props, propsGetSet, propsDeps, functionDeps)) {
                ok = false;
            }
            FileObject java = processingEnv.getFiler().createSourceFile(pkg + '.' + className, e);
            w = new OutputStreamWriter(java.openOutputStream());
            try {
                w.append("package " + pkg + ";\n");
                w.append("import net.java.html.json.*;\n");
                w.append("public final class ").append(className).append(" implements Cloneable {\n");
                w.append("  private boolean locked;\n");
                w.append("  private net.java.html.BrwsrCtx context;\n");
                w.append("  private org.apidesign.html.json.impl.Bindings[] ko = { null };\n");
                w.append(body.toString());
                w.append("  private static Class<" + inPckName(e) + "> modelFor() { return null; }\n");
                w.append("  private ").append(className).append("(net.java.html.BrwsrCtx context) {\n");
                w.append("    this.context = context;\n");
                w.append("  };\n");
                w.append("  public ").append(className).append("() {\n");
                w.append("    this(net.java.html.BrwsrCtx.findDefault(").append(className).append(".class));\n");
                for (Prprt p : props) {
                    if (p.array()) {
                        continue;
                    }
                    boolean[] isModel = {false};
                    boolean[] isEnum = {false};
                    boolean isPrimitive[] = {false};
                    String tn = checkType(p, isModel, isEnum, isPrimitive);
                    if (isModel[0]) {
                        w.write("    prop_" + p.name() + " = new " + tn + "();\n");
                    }
                }
                w.append("  };\n");
                if (props.length > 0) {
                    w.append("  public ").append(className).append("(");
                    Prprt firstArray = null;
                    String sep = "";
                    for (Prprt p : props) {
                        if (p.array()) {
                            if (firstArray == null) {
                                firstArray = p;
                            }
                            continue;
                        }
                        String tn = typeName(e, p);
                        w.write(sep);
                        w.write(tn);
                        w.write(" " + p.name());
                        sep = ", ";
                    }
                    if (firstArray != null) {
                        String tn;
                        boolean[] isModel = {false};
                        boolean[] isEnum = {false};
                        boolean isPrimitive[] = {false};
                        tn = checkType(firstArray, isModel, isEnum, isPrimitive);
                        w.write(sep);
                        w.write(tn);
                        w.write("... " + firstArray.name());
                    }
                    w.append(") {\n");
                    w.append("    this(net.java.html.BrwsrCtx.findDefault(").append(className).append(".class));\n");
                    for (Prprt p : props) {
                        if (p.array()) {
                            continue;
                        }
                        w.write("    this.prop_" + p.name() + " = " + p.name() + ";\n");
                    }
                    if (firstArray != null) {
                        w.write("    this.prop_" + firstArray.name() + ".init(" + firstArray.name() + ");\n");
                    }
                    w.append("  };\n");
                }
                w.append("  private org.apidesign.html.json.impl.Bindings intKnckt() {\n");
                w.append("    if (ko[0] != null) return ko[0];\n");
                w.append("    ko[0] = org.apidesign.html.json.impl.Bindings.apply(context, this);\n");
                {
                    w.append("    org.apidesign.html.json.spi.PropertyBinding[] propArr = {\n");
                    for (int i = 0; i < propsGetSet.size(); i += 5) {
                        w.append("      ko[0].registerProperty(\"").append(propsGetSet.get(i)).append("\", this, new P(");
                        w.append((i / 5) + "), " + (propsGetSet.get(i + 2) == null) + "),\n");
                    }
                    w.append("    };\n");
                }
                {
                    w.append("    org.apidesign.html.json.spi.FunctionBinding[] funcArr = {\n");
                    for (int i = 0; i < functions.size(); i += 2) {
                        w.append("      ko[0].registerFunction(\"").append(functions.get(i)).append("\", this, new P(");
                        w.append((i / 2) + ")),\n");
                    }
                    w.append("    };\n");
                }
                w.append("    ko[0].finish(this, propArr, funcArr);\n");
                w.append("    return ko[0];\n");
                w.append("  };\n");
                w.append("  private static final class P implements org.apidesign.html.json.impl.SetAndGet<" + className + ">,\n");
                w.append("  org.apidesign.html.json.impl.Callback<" + className + ">,\n");
                w.append("  org.apidesign.html.json.impl.FromJSON<" + className + "> {\n");
                w.append("    private final int type;\n");
                w.append("    P(int t) { type = t; };\n");
                w.append("    public void setValue(" + className + " data, Object value) {\n");
                w.append("      switch (type) {\n");
                for (int i = 0; i < propsGetSet.size(); i += 5) {
                    final String set = propsGetSet.get(i + 2);
                    String tn = propsGetSet.get(i + 4);
                    String btn = findBoxedType(tn);
                    if (btn != null) {
                        tn = btn;
                    }
                    if (set != null) {
                        w.append("        case " + (i / 5) + ": data." + strip(set) + "(org.apidesign.html.json.impl.JSON.extractValue(" + tn + ".class, value)); return;\n");
                    }
                }
                w.append("      }\n");
                w.append("    }\n");
                w.append("    public Object getValue(" + className + " data) {\n");
                w.append("      switch (type) {\n");
                for (int i = 0; i < propsGetSet.size(); i += 5) {
                    final String get = propsGetSet.get(i + 1);
                    if (get != null) {
                        w.append("        case " + (i / 5) + ": return data." + strip(get) + "();\n");
                    }
                }
                w.append("      }\n");
                w.append("      throw new UnsupportedOperationException();\n");
                w.append("    }\n");
                w.append("    public void call(" + className + " model, Object data, Object ev) {\n");
                w.append("      switch (type) {\n");
                for (int i = 0; i < functions.size(); i += 2) {
                    final String name = functions.get(i);
                    w.append("        case " + (i / 2) + ": model." + name + "(data, ev); return;\n");
                }
                w.append("      }\n");
                w.append("      throw new UnsupportedOperationException();\n");
                w.append("    }\n");
                w.append("    public Class<" + className + "> factoryFor() { return " + className + ".class; }\n");
                w.append("    public " + className + " read(net.java.html.BrwsrCtx c, Object json) { return new " + className + "(c, json); }\n");
                w.append("    public " + className + " cloneTo(Object o, net.java.html.BrwsrCtx c) { return ((" + className + ")o).clone(c); }\n");
                w.append("  }\n");
                w.append("  static { org.apidesign.html.json.impl.JSON.register(new P(0)); }\n");
                w.append("  private ").append(className).append("(net.java.html.BrwsrCtx c, Object json) {\n");
                w.append("    this.context = c;\n");
                int values = 0;
                for (int i = 0; i < propsGetSet.size(); i += 5) {
                    Prprt p = findPrprt(props, propsGetSet.get(i));
                    if (p == null) {
                        continue;
                    }
                    values++;
                }
                w.append("    Object[] ret = new Object[" + values + "];\n");
                w.append("    org.apidesign.html.json.impl.JSON.extract(context, json, new String[] {\n");
                for (int i = 0; i < propsGetSet.size(); i += 5) {
                    Prprt p = findPrprt(props, propsGetSet.get(i));
                    if (p == null) {
                        continue;
                    }
                    w.append("      \"").append(propsGetSet.get(i)).append("\",\n");
                }
                w.append("    }, ret);\n");
                for (int i = 0, cnt = 0, prop = 0; i < propsGetSet.size(); i += 5) {
                    final String pn = propsGetSet.get(i);
                    Prprt p = findPrprt(props, pn);
                    if (p == null) {
                        continue;
                    }
                    boolean[] isModel = { false };
                    boolean[] isEnum = { false };
                    boolean isPrimitive[] = { false };
                    String type = checkType(props[prop++], isModel, isEnum, isPrimitive);
                    if (p.array()) {
                        w.append("    if (ret[" + cnt + "] instanceof Object[]) {\n");
                        w.append("      for (Object e : ((Object[])ret[" + cnt + "])) {\n");
                        if (isModel[0]) {
                            w.append("        this.prop_").append(pn).append(".add(org.apidesign.html.json.impl.JSON.read");
                            w.append("(c, " + type + ".class, e));\n");
                        } else if (isEnum[0]) {
                            w.append("        this.prop_").append(pn);
                            w.append(".add(e == null ? null : ");
                            w.append(type).append(".valueOf(org.apidesign.html.json.impl.JSON.stringValue(e)));\n");
                        } else {
                            if (isPrimitive(type)) {
                                w.append("        this.prop_").append(pn).append(".add(org.apidesign.html.json.impl.JSON.numberValue(e).");
                                w.append(type).append("Value());\n");
                            } else {
                                w.append("        this.prop_").append(pn).append(".add((");
                                w.append(type).append(")e);\n");
                            }
                        }
                        w.append("      }\n");
                        w.append("    }\n");
                    } else {
                        if (isEnum[0]) {
                            w.append("    this.prop_").append(pn);
                            w.append(" = ret[" + cnt + "] == null ? null : ");
                            w.append(type).append(".valueOf(org.apidesign.html.json.impl.JSON.stringValue(ret[" + cnt + "]));\n");
                        } else if (isPrimitive(type)) {
                            w.append("    this.prop_").append(pn);
                            w.append(" = ret[" + cnt + "] == null ? ");
                            if ("char".equals(type)) {
                                w.append("0 : (org.apidesign.html.json.impl.JSON.charValue(");
                            } else if ("boolean".equals(type)) {
                                w.append("false : (org.apidesign.html.json.impl.JSON.boolValue(");
                            } else {
                                w.append("0 : (org.apidesign.html.json.impl.JSON.numberValue(");
                            }
                            w.append("ret[" + cnt + "])).");
                            w.append(type).append("Value();\n");
                        } else if (isModel[0]) {
                            w.append("    this.prop_").append(pn).append(" = org.apidesign.html.json.impl.JSON.read");
                            w.append("(c, " + type + ".class, ");
                            w.append("ret[" + cnt + "]);\n");
                        }else {
                            w.append("    this.prop_").append(pn);
                            w.append(" = (").append(type).append(')');
                            w.append("ret[" + cnt + "];\n");
                        }
                    }
                    cnt++;
                }
                w.append("  };\n");
                writeToString(props, w);
                writeClone(className, props, w);
                w.write("  /** Activates this model instance in the current {@link \n"
                    + "net.java.html.json.Models#bind(java.lang.Object, net.java.html.BrwsrCtx) browser context}. \n"
                    + "In case of using Knockout technology, this means to \n"
                    + "bind JSON like data in this model instance with Knockout tags in \n"
                    + "the surrounding HTML page.\n"
                    + "*/\n"
                );
                w.write("  public " + className + " applyBindings() {\n");
                w.write("    intKnckt().applyBindings();\n");
                w.write("    return this;\n");
                w.write("  }\n");
                w.write("  public boolean equals(Object o) {\n");
                w.write("    if (o == this) return true;\n");
                w.write("    if (o instanceof org.apidesign.html.json.impl.WrapperObject) {\n");
                w.write("      ((org.apidesign.html.json.impl.WrapperObject)o).setRealObject(intKnckt().koData());\n");
                w.write("      return false;\n");
                w.write("    }\n");
                w.write("    if (!(o instanceof " + className + ")) return false;\n");
                w.write("    " + className + " p = (" + className + ")o;\n");
                for (Prprt p : props) {
                    w.write("    if (!org.apidesign.html.json.impl.JSON.isSame(prop_" + p.name() + ", p.prop_" + p.name() + ")) return false;\n");
                }
                w.write("    return true;\n");
                w.write("  }\n");
                w.write("  public int hashCode() {\n");
                w.write("    int h = " + className + ".class.getName().hashCode();\n");
                for (Prprt p : props) {
                    w.write("    h = org.apidesign.html.json.impl.JSON.hashPlus(prop_" + p.name() + ", h);\n");
                }
                w.write("    return h;\n");
                w.write("  }\n");
                w.write("}\n");
            } finally {
                w.close();
            }
        } catch (IOException ex) {
            error("Can't create " + className + ".java", e);
            return false;
        }
        return ok;
    }
    
    private boolean generateProperties(
        Element where,
        Writer w, Prprt[] properties,
        Collection<String> props, 
        Map<String,Collection<String>> deps,
        Map<String,Collection<String>> functionDeps
    ) throws IOException {
        boolean ok = true;
        for (Prprt p : properties) {
            final String tn;
            tn = typeName(where, p);
            String[] gs = toGetSet(p.name(), tn, p.array());
            String castTo;
            
            if (p.array()) {
                w.write("  private org.apidesign.html.json.impl.JSONList<" + tn + "> prop_" + p.name() + " = new org.apidesign.html.json.impl.JSONList<" + tn + ">(ko, \""
                    + p.name() + "\"");
                Collection<String> dependants = deps.get(p.name());
                if (dependants != null) {
                    for (String depProp : dependants) {
                        w.write(", ");
                        w.write('\"');
                        w.write(depProp);
                        w.write('\"');
                    }
                }
                w.write(")");
                
                dependants = functionDeps.get(p.name());
                if (dependants != null) {
                    w.write(".onChange(new Runnable() { public void run() {\n");
                    for (String call : dependants) {
                        w.append("  ").append(call);
                    }
                    w.write("  }})");
                }
                w.write(";\n");
            
                castTo = "java.util.List";
                w.write("  public java.util.List<" + tn + "> " + gs[0] + "() {\n");
                w.write("    if (locked) throw new IllegalStateException();\n");
                w.write("    return prop_" + p.name() + ";\n");
                w.write("  }\n");
            } else {
                castTo = tn;
                w.write("  private " + tn + " prop_" + p.name() + ";\n");
                w.write("  public " + tn + " " + gs[0] + "() {\n");
                w.write("    if (locked) throw new IllegalStateException();\n");
                w.write("    return prop_" + p.name() + ";\n");
                w.write("  }\n");
                w.write("  public void " + gs[1] + "(" + tn + " v) {\n");
                w.write("    if (locked) throw new IllegalStateException();\n");
                w.write("    if (org.apidesign.html.json.impl.JSON.isSame(prop_" + p.name() + ", v)) return;\n");
                w.write("    prop_" + p.name() + " = v;\n");
                w.write("    org.apidesign.html.json.impl.Bindings b = ko[0];\n");
                w.write("    if (b != null) {\n");
                w.write("      b.valueHasMutated(\"" + p.name() + "\");\n");
                Collection<String> dependants = deps.get(p.name());
                if (dependants != null) {
                    for (String depProp : dependants) {
                        w.write("      b.valueHasMutated(\"" + depProp + "\");\n");
                    }
                }
                w.write("    }\n");
                dependants = functionDeps.get(p.name());
                if (dependants != null) {
                    for (String call : dependants) {
                        w.append("  ").append(call);
                    }
                }
                w.write("  }\n");
            }
            
            props.add(p.name());
            props.add(gs[2]);
            props.add(gs[3]);
            props.add(gs[0]);
            props.add(castTo);
        }
        return ok;
    }

    private boolean generateComputedProperties(
        Writer w, Prprt[] fixedProps,
        Collection<? extends Element> arr, Collection<String> props,
        Map<String,Collection<String>> deps
    ) throws IOException {
        boolean ok = true;
        for (Element e : arr) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (e.getAnnotation(ComputedProperty.class) == null) {
                continue;
            }
            if (!e.getModifiers().contains(Modifier.STATIC)) {
                error("Method " + e.getSimpleName() + " has to be static when annotated by @ComputedProperty", e);
                ok = false;
                continue;
            }
            ExecutableElement ee = (ExecutableElement)e;
            final TypeMirror rt = ee.getReturnType();
            final Types tu = processingEnv.getTypeUtils();
            TypeMirror ert = tu.erasure(rt);
            String tn = fqn(ert, ee);
            boolean array = false;
            final TypeMirror toCheck;
            if (tn.equals("java.util.List")) {
                array = true;
                toCheck = ((DeclaredType)rt).getTypeArguments().get(0);
            } else {
                toCheck = rt;
            }
            
            final String sn = ee.getSimpleName().toString();
            
            if (toCheck.getKind().isPrimitive()) {
                // OK
            } else {
                TypeMirror stringType = processingEnv.getElementUtils().getTypeElement("java.lang.String").asType();
                TypeMirror enumType = processingEnv.getElementUtils().getTypeElement("java.lang.Enum").asType();

                if (tu.isSubtype(toCheck, stringType)) {
                    // OK
                } else if (tu.isSubtype(tu.erasure(toCheck), tu.erasure(enumType))) {
                    // OK
                } else if (isModel(toCheck)) {
                    // OK
                } else {
                    ok = false;
                    error(sn + " cannot return " + toCheck, e);
                }
            }
            
            String[] gs = toGetSet(sn, tn, array);
            
            w.write("  public " + tn + " " + gs[0] + "() {\n");
            w.write("    if (locked) throw new IllegalStateException();\n");
            int arg = 0;
            for (VariableElement pe : ee.getParameters()) {
                final String dn = pe.getSimpleName().toString();
                
                if (!verifyPropName(pe, dn, fixedProps)) {
                    ok = false;
                }
                
                final String dt = fqn(pe.asType(), ee);
                String[] call = toGetSet(dn, dt, false);
                w.write("    " + dt + " arg" + (++arg) + " = ");
                w.write(call[0] + "();\n");
                
                Collection<String> depends = deps.get(dn);
                if (depends == null) {
                    depends = new LinkedHashSet<String>();
                    deps.put(dn, depends);
                }
                depends.add(sn);
            }
            w.write("    try {\n");
            w.write("      locked = true;\n");
            w.write("      return " + fqn(ee.getEnclosingElement().asType(), ee) + '.' + e.getSimpleName() + "(");
            String sep = "";
            for (int i = 1; i <= arg; i++) {
                w.write(sep);
                w.write("arg" + i);
                sep = ", ";
            }
            w.write(");\n");
            w.write("    } finally {\n");
            w.write("      locked = false;\n");
            w.write("    }\n");
            w.write("  }\n");

            props.add(e.getSimpleName().toString());
            props.add(gs[2]);
            props.add(null);
            props.add(gs[0]);
            props.add(tn);
        }
        
        return ok;
    }

    private static String[] toGetSet(String name, String type, boolean array) {
        String n = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        String bck2brwsrType = "L" + type.replace('.', '_') + "_2";
        if ("int".equals(type)) {
            bck2brwsrType = "I";
        }
        if ("double".equals(type)) {
            bck2brwsrType = "D";
        }
        String pref = "get";
        if ("boolean".equals(type)) {
            pref = "is";
            bck2brwsrType = "Z";
        }
        final String nu = n.replace('.', '_');
        if (array) {
            return new String[] { 
                "get" + n,
                null,
                "get" + nu + "__Ljava_util_List_2",
                null
            };
        }
        return new String[]{
            pref + n, 
            "set" + n, 
            pref + nu + "__" + bck2brwsrType,
            "set" + nu + "__V" + bck2brwsrType
        };
    }

    private String typeName(Element where, Prprt p) {
        String ret;
        boolean[] isModel = { false };
        boolean[] isEnum = { false };
        boolean isPrimitive[] = { false };
        ret = checkType(p, isModel, isEnum, isPrimitive);
        if (p.array()) {
            String bt = findBoxedType(ret);
            if (bt != null) {
                return bt;
            }
        }
        return ret;
    }
    
    private static String findBoxedType(String ret) {
        if (ret.equals("boolean")) {
            return Boolean.class.getName();
        }
        if (ret.equals("byte")) {
            return Byte.class.getName();
        }
        if (ret.equals("short")) {
            return Short.class.getName();
        }
        if (ret.equals("char")) {
            return Character.class.getName();
        }
        if (ret.equals("int")) {
            return Integer.class.getName();
        }
        if (ret.equals("long")) {
            return Long.class.getName();
        }
        if (ret.equals("float")) {
            return Float.class.getName();
        }
        if (ret.equals("double")) {
            return Double.class.getName();
        }
        return null;
    }

    private boolean verifyPropName(Element e, String propName, Prprt[] existingProps) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Prprt Prprt : existingProps) {
            if (Prprt.name().equals(propName)) {
                return true;
            }
            sb.append(sep);
            sb.append('"');
            sb.append(Prprt.name());
            sb.append('"');
            sep = ", ";
        }
        error(
            propName + " is not one of known properties: " + sb
            , e
        );
        return false;
    }

    private static String findPkgName(Element e) {
        for (;;) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return ((PackageElement)e).getQualifiedName().toString();
            }
            e = e.getEnclosingElement();
        }
    }

    private CharSequence wrapParams(
        ExecutableElement ee, String id, String className, String evName, String dataName
    ) {
        TypeMirror stringType = processingEnv.getElementUtils().getTypeElement("java.lang.String").asType();
        StringBuilder params = new StringBuilder();
        boolean first = true;
        for (VariableElement ve : ee.getParameters()) {
            if (!first) {
                params.append(", ");
            }
            first = false;
            String toCall = null;
            String toFinish = null;
            if (ve.asType() == stringType) {
                if (ve.getSimpleName().contentEquals("id")) {
                    params.append('"').append(id).append('"');
                    continue;
                }
                toCall = "org.apidesign.html.json.impl.JSON.toString(context, ";
            }
            if (ve.asType().getKind() == TypeKind.DOUBLE) {
                toCall = "org.apidesign.html.json.impl.JSON.toNumber(context, ";
                toFinish = ".doubleValue()";
            }
            if (ve.asType().getKind() == TypeKind.INT) {
                toCall = "org.apidesign.html.json.impl.JSON.toNumber(context, ";
                toFinish = ".intValue()";
            }
            if (dataName != null && ve.getSimpleName().contentEquals(dataName) && isModel(ve.asType())) {
                toCall = "org.apidesign.html.json.impl.JSON.toModel(context, " + ve.asType() + ".class, ";
            }

            if (toCall != null) {
                params.append(toCall);
                if (dataName != null && ve.getSimpleName().contentEquals(dataName)) {
                    params.append(dataName);
                    params.append(", null");
                } else {
                    if (evName == null) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Unexpected string parameter name.");
                        if (dataName != null) {
                            sb.append(" Try \"").append(dataName).append("\"");
                        }
                        error(sb.toString(), ee);
                    }
                    params.append(evName);
                    params.append(", \"");
                    params.append(ve.getSimpleName().toString());
                    params.append("\"");
                }
                params.append(")");
                if (toFinish != null) {
                    params.append(toFinish);
                }
                continue;
            }
            String rn = fqn(ve.asType(), ee);
            int last = rn.lastIndexOf('.');
            if (last >= 0) {
                rn = rn.substring(last + 1);
            }
            if (rn.equals(className)) {
                params.append(className).append(".this");
                continue;
            }
            error(
                "The annotated method can only accept " + className + " argument or argument named 'data'",
                ee
            );
        }
        return params;
    }
    
    
    private CharSequence wrapPropName(
        ExecutableElement ee, String className, String propName, String propValue
    ) {
        TypeMirror stringType = processingEnv.getElementUtils().getTypeElement("java.lang.String").asType();
        StringBuilder params = new StringBuilder();
        boolean first = true;
        for (VariableElement ve : ee.getParameters()) {
            if (!first) {
                params.append(", ");
            }
            first = false;
            if (ve.asType() == stringType) {
                if (propName != null && ve.getSimpleName().contentEquals(propName)) {
                    params.append('"').append(propValue).append('"');
                } else {
                    error("Unexpected string parameter name. Try \"" + propName + "\".", ee);
                }
                continue;
            }
            String rn = fqn(ve.asType(), ee);
            int last = rn.lastIndexOf('.');
            if (last >= 0) {
                rn = rn.substring(last + 1);
            }
            if (rn.equals(className)) {
                params.append(className).append(".this");
                continue;
            }
            error(
                "@OnPrprtChange method can only accept String or " + className + " arguments",
                ee);
        }
        return params;
    }
    
    private boolean isModel(TypeMirror tm) {
        if (tm.getKind() == TypeKind.ERROR) {
            return true;
        }
        final Element e = processingEnv.getTypeUtils().asElement(tm);
        if (e == null) {
            return false;
        }
        for (Element ch : e.getEnclosedElements()) {
            if (ch.getKind() == ElementKind.METHOD) {
                ExecutableElement ee = (ExecutableElement)ch;
                if (ee.getParameters().isEmpty() && ee.getSimpleName().contentEquals("modelFor")) {
                    return true;
                }
            }
        }
        return models.values().contains(e.getSimpleName().toString());
    }
    
    private void writeToString(Prprt[] props, Writer w) throws IOException {
        w.write("  public String toString() {\n");
        w.write("    StringBuilder sb = new StringBuilder();\n");
        w.write("    sb.append('{');\n");
        String sep = "";
        for (Prprt p : props) {
            w.write(sep);
            w.append("    sb.append('\"').append(\"" + p.name() + "\")");
                w.append(".append('\"').append(\":\");\n");
            w.append("    sb.append(org.apidesign.html.json.impl.JSON.toJSON(prop_");
            w.append(p.name()).append("));\n");
            sep =    "    sb.append(',');\n";
        }
        w.write("    sb.append('}');\n");
        w.write("    return sb.toString();\n");
        w.write("  }\n");
    }
    private void writeClone(String className, Prprt[] props, Writer w) throws IOException {
        w.write("  public " + className + " clone() {\n");
        w.write("    return clone(context);\n");
        w.write("  }\n");
        w.write("  private " + className + " clone(net.java.html.BrwsrCtx ctx) {\n");
        w.write("    " + className + " ret = new " + className + "(ctx);\n");
        for (Prprt p : props) {
            if (!p.array()) {
                boolean isModel[] = { false };
                boolean isEnum[] = { false };
                boolean isPrimitive[] = { false };
                checkType(p, isModel, isEnum, isPrimitive);
                if (!isModel[0]) {
                    w.write("    ret.prop_" + p.name() + " = prop_" + p.name() + ";\n");
                    continue;
                }
                w.write("    ret.prop_" + p.name() + " =  prop_" + p.name() + "  == null ? null : prop_" + p.name() + ".clone();\n");
            } else {
                w.write("    ret.prop_" + p.name() + ".cloneAll(ctx, prop_" + p.name() + ");\n");
            }
        }
        
        w.write("    return ret;\n");
        w.write("  }\n");
    }

    private String inPckName(Element e) {
        StringBuilder sb = new StringBuilder();
        while (e.getKind() != ElementKind.PACKAGE) {
            if (sb.length() == 0) {
                sb.append(e.getSimpleName());
            } else {
                sb.insert(0, '.');
                sb.insert(0, e.getSimpleName());
            }
            e = e.getEnclosingElement();
        }
        return sb.toString();
    }

    private String fqn(TypeMirror pt, Element relative) {
        if (pt.getKind() == TypeKind.ERROR) {
            final Elements eu = processingEnv.getElementUtils();
            PackageElement pckg = eu.getPackageOf(relative);
            return pckg.getQualifiedName() + "." + pt.toString();
        }
        return pt.toString();
    }

    private String checkType(Prprt p, boolean[] isModel, boolean[] isEnum, boolean[] isPrimitive) {
        TypeMirror tm;
        try {
            String ret = p.typeName(processingEnv);
            TypeElement e = processingEnv.getElementUtils().getTypeElement(ret);
            if (e == null) {
                isModel[0] = true;
                isEnum[0] = false;
                isPrimitive[0] = false;
                return ret;
            }
            tm = e.asType();
        } catch (MirroredTypeException ex) {
            tm = ex.getTypeMirror();
        }
        tm = processingEnv.getTypeUtils().erasure(tm);
        if (isPrimitive[0] = tm.getKind().isPrimitive()) {
            isEnum[0] = false;
            isModel[0] = false;
            return tm.toString();
        }
        final Element e = processingEnv.getTypeUtils().asElement(tm);
        if (e.getKind() == ElementKind.CLASS && tm.getKind() == TypeKind.ERROR) {
            isModel[0] = true;
            isEnum[0] = false;
            return e.getSimpleName().toString();
        }
        
        final Model m = e == null ? null : e.getAnnotation(Model.class);
        String ret;
        if (m != null) {
            ret = findPkgName(e) + '.' + m.className();
            isModel[0] = true;
            models.put(e, m.className());
        } else if (findModelForMthd(e)) {
            ret = ((TypeElement)e).getQualifiedName().toString();
            isModel[0] = true;
        } else {
            ret = tm.toString();
        }
        TypeMirror enm = processingEnv.getElementUtils().getTypeElement("java.lang.Enum").asType();
        enm = processingEnv.getTypeUtils().erasure(enm);
        isEnum[0] = processingEnv.getTypeUtils().isSubtype(tm, enm);
        return ret;
    }
    
    private static boolean findModelForMthd(Element clazz) {
        if (clazz == null) {
            return false;
        }
        for (Element e : clazz.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                ExecutableElement ee = (ExecutableElement)e;
                if (
                    ee.getSimpleName().contentEquals("modelFor") &&
                    ee.getParameters().isEmpty()
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    private Iterable<String> findParamNames(
        Element e, String url, String jsonParam, StringBuilder... both
    ) {
        List<String> params = new ArrayList<String>();
        int wasJSON = 0;

        for (int pos = 0; ;) {
            int next = url.indexOf('{', pos);
            if (next == -1) {
                both[wasJSON].append('"')
                    .append(url.substring(pos))
                    .append('"');
                return params;
            }
            int close = url.indexOf('}', next);
            if (close == -1) {
                error("Unbalanced '{' and '}' in " + url, e);
                return params;
            }
            final String paramName = url.substring(next + 1, close);
            params.add(paramName);
            if (paramName.equals(jsonParam) && !jsonParam.isEmpty()) {
                both[wasJSON].append('"')
                    .append(url.substring(pos, next))
                    .append('"');
                wasJSON = 1;
            } else {
                both[wasJSON].append('"')
                    .append(url.substring(pos, next))
                    .append("\" + ").append(paramName).append(" + ");
            }
            pos = close + 1;
        }
    }

    private static Prprt findPrprt(Prprt[] properties, String propName) {
        for (Prprt p : properties) {
            if (propName.equals(p.name())) {
                return p;
            }
        }
        return null;
    }

    private boolean isPrimitive(String type) {
        return 
            "int".equals(type) ||
            "double".equals(type) ||
            "long".equals(type) ||
            "short".equals(type) ||
            "byte".equals(type) ||
            "char".equals(type) ||
            "boolean".equals(type) ||
            "float".equals(type);
    }

    private static Collection<String> findDerivedFrom(Map<String, Collection<String>> propsDeps, String derivedProp) {
        Set<String> names = new HashSet<String>();
        for (Map.Entry<String, Collection<String>> e : propsDeps.entrySet()) {
            if (e.getValue().contains(derivedProp)) {
                names.add(e.getKey());
            }
        }
        return names;
    }
    
    private Prprt[] createProps(Element e, Property[] arr) {
        Prprt[] ret = Prprt.wrap(processingEnv, e, arr);
        Prprt[] prev = verify.put(e, ret);
        if (prev != null) {
            error("Two sets of properties for ", e);
        }
        return ret;
    }
    
    private static String strip(String s) {
        int indx = s.indexOf("__");
        if (indx >= 0) {
            return s.substring(0, indx);
        } else {
            return s;
        }
    }

    static List<? extends Object> getAnnoValues(ProcessingEnvironment pe, Element e, AnnotationMirror am) {
        try {
            Class<?> trees = Class.forName("com.sun.tools.javac.api.JavacTrees");
            Method m = trees.getMethod("instance", ProcessingEnvironment.class);
            Object instance = m.invoke(null, pe);
            m = instance.getClass().getMethod("getPath", Element.class, AnnotationMirror.class);
            Object path = m.invoke(instance, e, am);
            m = path.getClass().getMethod("getLeaf");
            Object leaf = m.invoke(path);
            m = leaf.getClass().getMethod("getArguments");
            return (List) m.invoke(leaf);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private static class Prprt {
        private final Element e;
        private final AnnotationMirror tm;
        private final Property p;

        public Prprt(Element e, AnnotationMirror tm, Property p) {
            this.e = e;
            this.tm = tm;
            this.p = p;
        }
        
        String name() {
            return p.name();
        }
        
        boolean array() {
            return p.array();
        }

        String typeName(ProcessingEnvironment env) {
            RuntimeException ex;
            try {
                return p.type().getName();
            } catch (IncompleteAnnotationException e) {
                ex = e;
            } catch (AnnotationTypeMismatchException e) {
                ex = e;
            }
            for (Object v : getAnnoValues(env, e, tm)) {
                String s = v.toString().replace(" ", "");
                if (s.startsWith("type=") && s.endsWith(".class")) {
                    return s.substring(5, s.length() - 6);
                }
            }
            throw ex;
        }
        
        
        static Prprt[] wrap(ProcessingEnvironment pe, Element e, Property[] arr) {
            if (arr.length == 0) {
                return new Prprt[0];
            }
            
            if (e.getKind() != ElementKind.CLASS) {
                throw new IllegalStateException("" + e.getKind());
            }
            TypeElement te = (TypeElement)e;
            List<? extends AnnotationValue> val = null;
            for (AnnotationMirror an : te.getAnnotationMirrors()) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : an.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().contentEquals("properties")) {
                        val = (List)entry.getValue().getValue();
                        break;
                    }
                }
            }
            if (val == null || val.size() != arr.length) {
                pe.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + val, e);
                return new Prprt[0];
            }
            Prprt[] ret = new Prprt[arr.length];
            BIG: for (int i = 0; i < ret.length; i++) {
                AnnotationMirror am = (AnnotationMirror)val.get(i).getValue();
                ret[i] = new Prprt(e, am, arr[i]);
                
            }
            return ret;
        }
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return super.getCompletions(element, annotation, member, userText);
    }
    
    private static final Completion methodOf(String method) {
        ResourceBundle rb = ResourceBundle.getBundle("org.apidesign.html.json.impl.Bundle");
        return Completions.of('"' + method + '"', rb.getString("MSG_Completion_" + method));
    }
    
    private boolean findOnError(ExecutableElement errElem, TypeElement te, String name, String className) {
        String err = null;
        METHODS:
        for (Element e : te.getEnclosedElements()) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (!e.getSimpleName().contentEquals(name)) {
                continue;
            }
            if (!e.getModifiers().contains(Modifier.STATIC)) {
                errElem = (ExecutableElement) e;
                err = "Would have to be static";
                continue;
            }
            ExecutableElement ee = (ExecutableElement) e;
            TypeMirror excType = processingEnv.getElementUtils().getTypeElement(Exception.class.getName()).asType();
            final List<? extends VariableElement> params = ee.getParameters();
            boolean error = false;
            if (params.size() != 2) {
                error = true;
            } else {
                String firstType = params.get(0).asType().toString();
                int lastDot = firstType.lastIndexOf('.');
                if (lastDot != -1) {
                    firstType = firstType.substring(lastDot + 1);
                }
                if (!firstType.equals(className)) {
                    error = true;
                }
                if (!processingEnv.getTypeUtils().isAssignable(excType, params.get(1).asType())) {
                    error = true;
                }
            }
            if (error) {
                errElem = (ExecutableElement) e;
                err = "Error method first argument needs to be " + className + " and second Exception";
                continue;
            }
            return true;
        }
        if (err == null) {
            err = "Cannot find " + name + "(" + className + ", Exception) method in this class";
        }
        error(err, errElem);
        return false;
    }
    
}
