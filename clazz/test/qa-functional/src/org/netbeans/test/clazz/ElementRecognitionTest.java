/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

/*
 * ElementRecognitionTest.java
 *
 * Created on March 26, 2002, 4:22 PM
 */

package org.netbeans.test.clazz;

import org.netbeans.junit.NbTestCase;
import org.openide.src.*;
import org.netbeans.junit.NbTest;
import org.netbeans.junit.NbTestSuite;
import java.lang.reflect.Modifier;

/**
 *
 * @author  jb105785
 */
public class ElementRecognitionTest extends NbTestCase {

    ClassElement clazz;
    public ElementRecognitionTest() {
        super("");
    }

    public ElementRecognitionTest(java.lang.String testName) {
        super(testName);
    }
    
    public static NbTest suite() {
        return new NbTestSuite(ElementRecognitionTest.class);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public void setUp() throws Exception {
        super.setUp();
        clazz = ClassElement.forName("org.netbeans.test.ClazzTest");
        assertNotNull(clazz);
    }
    
    public void testClass() {
        assertEquals(clazz.getName().getName(), "ClazzTest");
        assertEquals(clazz.getName().getFullName(), "org.netbeans.test.ClazzTest");
        assertEquals(clazz.getSuperclass().getFullName(), "javax.swing.JPanel");
        assertEquals(clazz.getInterfaces().length, 0);
        
        assertEquals(clazz.getModifiers(), Modifier.PUBLIC);
    }
    
    public void testInnerClasses() {
        ClassElement inner = clazz.getClass(Identifier.create("InnerClass"));
        
        assertNotNull(inner);
        assertEquals(inner.getName().getName(), "InnerClass");
        assertEquals(inner.getName().getFullName(), "org.netbeans.test.ClazzTest.InnerClass");
        assertEquals("InnerClass should be public static!", inner.getModifiers(), Modifier.PUBLIC | Modifier.STATIC );
    }
    
    public void testInnerInterfaces() {
        ClassElement inner = clazz.getClass(Identifier.create("InnerInterface"));
        
        assertNotNull(inner);
        assertEquals(inner.getName().getName(), "InnerInterface");
        assertEquals(inner.getName().getFullName(), "org.netbeans.test.ClazzTest.InnerInterface");
        assertEquals(inner.getModifiers(), Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.STATIC );
    }

    public void testMethods() {
        assertEquals(clazz.getMethods().length, 4);
    }
        
    public void testMethodInitComponents() {
        MethodElement method = clazz.getMethod(Identifier.create("initComponents"), new Type[]{});
        
        assertNotNull(method);
        assertEquals(method.getName().getName(), "initComponents");
        assertEquals(method.getName().getFullName(), "initComponents");
        assertEquals(method.getModifiers(), Modifier.PRIVATE);
        assertEquals(method.getReturn(), Type.VOID);
        assertEquals(method.getParameters().length, 0);
        assertEquals(method.getExceptions().length, 0);
    }

    public void testMethodIsOk() {
        MethodElement method = clazz.getMethod(Identifier.create("isOk"), new Type[]{});
        
        assertNotNull(method);
        assertEquals(method.getName().getName(), "isOk");
        assertEquals(method.getName().getFullName(), "isOk");
        assertEquals(method.getModifiers(), Modifier.PUBLIC);
        assertEquals(method.getReturn(), Type.BOOLEAN);
        assertEquals(method.getParameters().length, 0);
        assertEquals(method.getExceptions().length, 0);
        
    }

    public void testMethodSetOk() {
        MethodElement method = clazz.getMethod(Identifier.create("setOk"), new Type[]{Type.BOOLEAN});
        
        assertNotNull(method);
        assertEquals(method.getName().getName(), "setOk");
        assertEquals(method.getName().getFullName(), "setOk");
        assertEquals(method.getModifiers(), Modifier.PUBLIC);
        assertEquals(method.getReturn(), Type.VOID);
        assertEquals(method.getParameters()[0], new MethodParameter(null, Type.BOOLEAN, false));
        assertEquals(method.getExceptions().length, 0);
    }

    public void testMethodTestMethod() throws IllegalArgumentException {
        MethodElement method = clazz.getMethod(Identifier.create("testMethod"), new Type[]{Type.parse("java.lang.String")});
        
        assertNotNull(method);
        assertEquals(method.getName().getName(), "testMethod");
        assertEquals(method.getName().getFullName(), "testMethod");
        assertEquals(method.getModifiers(), Modifier.STATIC | Modifier.PROTECTED);
        assertEquals(method.getReturn(), Type.VOID);
        assertEquals(method.getParameters()[0], new MethodParameter(null, Type.parse("java.lang.String"), false));
        assertEquals(method.getExceptions()[0], Identifier.create("java.io.IOException"));
    }
    
    public void testFields() {
        assertEquals(clazz.getFields().length, 3);
    }
    
    public void testFieldCONSTANT() {
        FieldElement field = clazz.getField(Identifier.create("CONSTANT"));
        
        assertNotNull(field);
        assertEquals(field.getName().getName(), "CONSTANT");
        assertEquals(field.getName().getFullName(), "CONSTANT");
        assertEquals(field.getModifiers(), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        assertEquals(field.getType(), Type.CHAR);
        assertEquals(field.getInitValue(), "");
    }
    
    public void testFieldTestField() {
        FieldElement field = clazz.getField(Identifier.create("testField"));
        
        assertNotNull(field);
        assertEquals(field.getName().getName(), "testField");
        assertEquals(field.getName().getFullName(), "testField");
        assertEquals(field.getModifiers(), Modifier.PRIVATE);
        assertEquals(field.getType(), Type.parse("java.lang.Object"));
        assertEquals(field.getInitValue(),"");
    }
    
    public void testFieldOk() {
        FieldElement field = clazz.getField(Identifier.create("ok"));
        
        assertNotNull(field);
        assertEquals(field.getName().getName(), "ok");
        assertEquals(field.getName().getFullName(), "ok");
        assertEquals(field.getModifiers(), Modifier.PRIVATE);
        assertEquals(field.getType(), Type.BOOLEAN);
        assertEquals(field.getInitValue(), "");
    }
    
    public void testConstructors() {
        assertEquals(clazz.getConstructors().length, 2);
    }

     public void testConstructorDefault() {
        ConstructorElement constr = clazz.getConstructor(new Type[]{});
        
        assertNotNull(constr);
        assertEquals(constr.getName().getName(), "ClazzTest");
        assertEquals(constr.getName().getFullName(), "ClazzTest");
        assertEquals(constr.getModifiers(), Modifier.PUBLIC);
        assertEquals(constr.getParameters().length, 0);
        assertEquals(constr.getExceptions().length, 0);
    }

    public void testConstructorString() {
        ConstructorElement constr = clazz.getConstructor(new Type[]{Type.parse("java.lang.String")});
        
        assertNotNull(constr);
        assertEquals(constr.getName().getName(), "ClazzTest");
        assertEquals(constr.getName().getFullName(), "ClazzTest");
        assertEquals(constr.getModifiers(), Modifier.PUBLIC);
        assertEquals(constr.getParameters()[0], new MethodParameter(null, Type.parse("java.lang.String"), true));
        assertEquals(constr.getExceptions()[0], Identifier.create("java.io.IOException"));
    }

    
    
    
    
    
}
