/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.util.ResourceBundle;
import java.util.*;
import org.netbeans.jmi.javamodel.Array;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.PrimitiveType;
import org.netbeans.jmi.javamodel.PrimitiveTypeKind;
import org.netbeans.jmi.javamodel.PrimitiveTypeKindEnum;
import org.netbeans.jmi.javamodel.Resource;

import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.modules.classfile.Method;
import org.openide.src.Type;
import org.openide.src.MethodParameter;
import org.openide.src.Identifier;

/**
 *
 * @author  sdedic
 */
final class Util {

    private static RequestProcessor classProcessor;
    
    static ResourceBundle getBundle() {
        return NbBundle.getBundle(Util.class);
    }
    
    static String getString(String key) {
        return getBundle().getString(key);
    }
        
    public static final Type getReturnType(String signature){
        return new SignatureToType(signature).getReturnType();
    }
    
    public static final MethodParameter[] getParametersType(String signature){
        return new SignatureToType(signature).getMethodParameters();
    }

    public static class SignatureToType{
        private char[] signature;        
        private int len;        
        private int i = 0;
        
        private Type returnType = null; //return type
        private MethodParameter[] params = null;    //return MethodParameter[]
        
        public SignatureToType(String signature){
            this.signature = signature.toCharArray();//.substring(signature.indexOf(')')+1).getBytes();
            len = this.signature.length;
            for( ; i < len; i++ ){
                if (this.signature[i] == '('){
                    i++;
                    Type t; 
                    ArrayList list = new ArrayList();
                    while( ( t = sigToType()) != null ){
                        list.add(t);
                    }                    
                    params = new MethodParameter[list.size()];
                    for( int n = 0; n < list.size(); n++){
                        params[n] = new MethodParameter("", (Type)list.get(n), false);
                    }
                }
                
                returnType = sigToType();
            }
        }
        
        public Type getReturnType(){
            return returnType;
        }
        
        public MethodParameter[] getMethodParameters(){
            return params;
        }

        Type sigToType( ) {
            switch ((char) signature[i]) {
            case 'B':
                i++;
                return Type.BYTE;
            case 'C':
                i++;
                return Type.CHAR;
            case 'D':
                i++;
                return Type.DOUBLE;
            case 'F':
                i++;
                return Type.FLOAT;
            case 'I':
                i++;
                return Type.INT;
            case 'J':
                i++;
                return Type.LONG;
            case 'L':
                return classSigToType();
            case 'S':
                i++;
                return Type.SHORT;
            case 'V':
                i++;
                return Type.VOID;
            case 'Z':
                i++;
                return Type.BOOLEAN;
            case '[':
                return createArrayType();
            case ')':
                i++;
                return null;
            default:
                throw new IllegalArgumentException("Unknown signature"); // NOI18N
            }
        }
        
        Type classSigToType() {
            if (signature[i++] == 'L') {
                int start = i;
                while (signature[i] != ';' ) i++;
                //get FQN replace bad chars
                String fs  = new String(signature, start, i - start ).replace('$', '.').replace('/','.');
                String s;
                
                //filter java.lang
                int idx;
                if( fs.startsWith("java.lang") ){ // NOI18N
                    s = fs.substring( 10 ); //filter 'java.lang.'
                } else {
                    s = fs;
                }
                i++;
                return Type.createClass(Identifier.create(fs, s, Identifier.RESOLVED));
            } else {
                throw new IllegalArgumentException("Method signature has to start with >>L<<"); // NOI18N
            }
        }
        
        Type createArrayType(){
            int depth = 0;
            for( ; i < len; i++ ){
                if( (char)signature[i] == '[' )
                    depth++;
                else
                    break;                
            }
            Type t = sigToType();
            for( int n = 0; n < depth; n++)
                t = Type.createArray(t);
            return t;
        }
    }    
    
    static String createClassName(String signature) {
        return signature.replace('/', '.').replace('$', '.');
    }

    static Type createType(org.netbeans.jmi.javamodel.Type t) {
        if (t == null)
            return null;
        if (t instanceof Array) {
            return Type.createArray(createType(((Array)t).getType()));
        } else if (t instanceof JavaClass) {
            JavaClass ct=(JavaClass)t;
                return Type.createClass(
                    Identifier.create(createClassName(ct.getName())));
        } else if (t instanceof PrimitiveType) {
            PrimitiveTypeKind tag = ((PrimitiveType)t).getKind();
            if (tag == PrimitiveTypeKindEnum.BOOLEAN) {
                return Type.BOOLEAN;
            } else if (tag == PrimitiveTypeKindEnum.BYTE) {
                return Type.BYTE;
            } else if (tag == PrimitiveTypeKindEnum.CHAR) {
                return Type.CHAR;
            } else if (tag == PrimitiveTypeKindEnum.DOUBLE) {
                return Type.DOUBLE;
            } else if (tag == PrimitiveTypeKindEnum.FLOAT) {
                return Type.FLOAT;
            } else if (tag == PrimitiveTypeKindEnum.INT) {
                return Type.INT;
            } else if (tag == PrimitiveTypeKindEnum.LONG) {
                return Type.LONG;
            } else if (tag == PrimitiveTypeKindEnum.SHORT) {
                return Type.SHORT;
            } else if (tag == PrimitiveTypeKindEnum.VOID) {
                return Type.VOID;
            }
        }
        throw new IllegalArgumentException("Invalid TypeDescriptor: " + t); // NOI18N
    }
    
    /**
     * Finds a DataObject for the specified ClassResource given the project
     * context
     */
    static DataObject findDataObject(Lookup context, Resource resource) {
        return null;
    }

    static RequestProcessor getClassProcessor() {
        if (classProcessor==null)
            classProcessor=new RequestProcessor("Clazz",5); // NOI18N
        return classProcessor;
    }
}
