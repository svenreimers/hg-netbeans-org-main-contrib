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
package org.netbeans.modules.json.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.html.json.Model;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/** Set of utility methods to be used from classes generated
 * by {@link Model} annotation. These methods are not supposed
 * to be called directly.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public final class JSON {
    private static final Logger LOG = Logger.getLogger(JSON.class.getName());
    
    private JSON() {
    }
    
    public static boolean isModel(Class<?> c) {
        try {
            return c.getDeclaredMethod("modelFor") != null; // NOI18N
        } catch (Exception ex) {
            return false;
        }
    }
    
    @SuppressWarnings("unchecked") static <T> T bindTo(T t) {
        try {
            final Class<?> c = t.getClass();
            return (T)c.cast(c.getMethod("clone").invoke(t)); // NOI18N
        } catch (Exception ex) {
            throw new IllegalStateException("No clone method in " + t.getClass(), ex);
        }
    }

    public static void extract(Object jsonObject, String[] props, Object[] values) {
        if (jsonObject instanceof JSONObject) {
            JSONObject obj = (JSONObject) jsonObject;
            for (int i = 0; i < props.length; i++) {
                try {
                    values[i] = obj.containsKey(props[i]) ? obj.get(props[i]) : null;
                    if (values[i] instanceof JSONArray) {
                        JSONArray jsarr = (JSONArray) values[i];
                        Object[] arr = new Object[jsarr.size()];
                        for (int j=0; j< jsarr.size(); j++) {
                            arr[j] = jsarr.get(j);
                        }
                        values[i] = arr;
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Can't read " + props[i] + " from " + jsonObject, ex);
                }
            }
        }
    }
    private static Object getProperty(Object obj, String prop) {
        if (prop == null) return obj;
        
        String[] arr = { prop };
        Object[] val = { null };
        extract(obj, arr, val);
        return val[0];
    }

    public static Object toJSON(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Enum) {
            value = value.toString();
        }
        if (value instanceof String) {
            String s = (String)value;
            int len = s.length();
            StringBuilder sb = new StringBuilder(len + 10);
            sb.append('"');
            for (int i = 0; i < len; i++) {
                char ch = s.charAt(i);
                switch (ch) {
                    case '\"': sb.append("\\\""); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    case '\\': sb.append("\\\\"); break;
                    default: sb.append(ch);
                }
            }
            sb.append('"');
            return sb.toString();
        }
        if (value instanceof Collection) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            String sep = "";
            for (Object o : ((Collection)value)) {
                sb.append(sep).append(toJSON(o));
                sep = ",";
            }
            sb.append("]");
            return sb.toString();
        }
        return value.toString();
    }

    public static boolean isSame(int a, int b) {
        return a == b;
    }
    
    public static boolean isSame(double a, double b) {
        return a == b;
    }
    
    public static boolean isSame(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
    
    public static int hashPlus(Object o, int h) {
        return o == null ? h : h ^ o.hashCode();
    }

    public static <T> T extractValue(Class<T> type, Object val) {
        if (Number.class.isAssignableFrom(type)) {
            val = numberValue(val);
        }
        if (Boolean.class == type) {
            val = boolValue(val);
        }
        if (String.class == type) {
            val = stringValue(val);
        }
        if (Character.class == type) {
            val = charValue(val);
        }
        if (Integer.class == type) {
            val = val instanceof Number ? ((Number)val).intValue() : 0;
        }
        if (Long.class == type) {
            val = val instanceof Number  ? ((Number)val).longValue() : 0;
        }
        if (Short.class == type) {
            val = val instanceof Number ? ((Number)val).shortValue() : 0;
        }
        if (Byte.class == type) {
            val = val instanceof Number ? ((Number)val).byteValue() : 0;
        }        
        if (Double.class == type) {
            val = val instanceof Number ? ((Number)val).doubleValue() : Double.NaN;
        }
        if (Float.class == type) {
            val = val instanceof Number ? ((Number)val).floatValue() : Float.NaN;
        }
        return type.cast(val);
    }
    
    protected static boolean isNumeric(Object val) {
        return ((val instanceof Integer) || (val instanceof Long) || (val instanceof Short) || (val instanceof Byte));
    }
    
    public static String stringValue(Object val) {
        if (val instanceof Boolean) {
            return ((Boolean)val ? "true" : "false");
        }
        if (isNumeric(val)) {
            return Long.toString(((Number)val).longValue());
        }
        if (val instanceof Float) {
            return Float.toString((Float)val);
        }
        if (val instanceof Double) {
            return Double.toString((Double)val);
        }
        return (String)val;
    }

    public static Number numberValue(Object val) {
        if (val instanceof String) {
            try {
                return Double.valueOf((String)val);
            } catch (NumberFormatException ex) {
                return Double.NaN;
            }
        }
        if (val instanceof Boolean) {
            return (Boolean)val ? 1 : 0;
        }
        return (Number)val;
    }

    public static Character charValue(Object val) {
        if (val instanceof Number) {
            return Character.toChars(numberValue(val).intValue())[0];
        }
        if (val instanceof Boolean) {
            return (Boolean)val ? (char)1 : (char)0;
        }
        if (val instanceof String) {
            String s = (String)val;
            return s.isEmpty() ? (char)0 : s.charAt(0);
        }
        return (Character)val;
    }
    
    public static Boolean boolValue(Object val) {
        if (val instanceof String) {
            return Boolean.parseBoolean((String)val);
        }
        if (val instanceof Number) {
            return numberValue(val).doubleValue() != 0.0;
        }
    
        return Boolean.TRUE.equals(val);
    }
    
    public static <T> T readStream(Class<T> modelClazz, InputStream data) 
    throws IOException {
        return read(modelClazz, toJSON((InputStream)data));
    }
    public static <T> T read(Class<T> modelClazz, Object data) {
        if (data == null) {
            return null;
        }
        if (modelClazz == String.class) {
            return modelClazz.cast(data.toString());
        }
        if (isModel(modelClazz)) {
            try {
                Constructor<T> newC = modelClazz.getDeclaredConstructor(Object.class);
                newC.setAccessible(true);
                return newC.newInstance(data);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
        throw new NullPointerException();
    }
    
    public static <T> void cloneAll(Collection<T> to, Collection<T> from) {
        Boolean isModel = null;
        for (T t : from) {
            if (isModel == null) {
                isModel = JSON.isModel(t.getClass());
            }
            if (isModel) {
                to.add(JSON.bindTo(t));
            } else {
                to.add(t);
            }
        }
    }
    
    public static <T> void init(Collection<T> to, T... values) {
        if (values == null || values.length == 0) {
            return;
        }
        to.addAll(Arrays.asList(values));
    }

    @SuppressWarnings("unchecked")
    public static <T> void init(Collection<T> to, Object values) {
        int len;
        if (values == null || (len = Array.getLength(values)) == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            Object data = Array.get(values, i);
            to.add((T)data);
        }
    }

    private static Object toJSON(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is, "UTF-8"); // NOI18N
        return JSONValue.parse(r);
    }
    
}
