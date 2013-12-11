/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package net.java.html.json;

import java.io.IOException;
import java.io.InputStream;
import org.netbeans.modules.json.JSON;

/** Information about and 
 * operations for classes generated by the {@link Model @Model}
 * annotation.
 *
 * @author Jaroslav Tulach <jaroslav.tulach@apidesign.org>
 */
public final class Models {
    private Models() {
    }
   
    /** Finds out whether given class is a model class - e.g. has been
     * generated by {@link Model @Model} annotation.
     * 
     * @param clazz the class to test
     * @return true, if <code>clazz</code> was generated by {@link Model} annotation
     * @since 0.2
     */
    public static boolean isModel(Class<?> clazz) {
        return JSON.isModel(clazz);
    }
    
    /** Generic method to parse content of a model class from a stream.
     * 
     * @param c context of the technology to use for reading 
     * @param model the model class generated by {@link Model} annotation
     * @param is input stream with data
     * @return new instance of the model class
     */
    public static <M> M parse(Class<M> model, InputStream is) throws IOException {
        return JSON.readStream(model, is);
    }
    
    /** Converts an existing, raw, JSON object into a {@link Model model class}.
     * 
     * @param <M> the type of the model class
     * @param ctx context of the technology to use for converting
     * @param model the model class
     * @param jsonObject original instance of the JSON object
     * @return new instance of the model class
     */
    public static <M> M fromRaw(Class<M> model, Object jsonObject) {
        return JSON.read(model, jsonObject);
    }
    
//    /** Converts an existing {@link Model model} into its associated, raw 
//     * JSON object. The object may, but does not have to, be the same instance
//     * as the model object.
//     * 
//     * @param model the model object
//     * @return the raw JSON object associated with the model
//     * @since 0.7
//     */
//    public static Object toRaw(Object model) {
//        return JSON.toJSON(model);
//    }
}
