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

package org.netbeans.modules.erd.model.component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.erd.graphics.ColumnWidget;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.model.ComponentDescriptor;
import org.netbeans.modules.erd.model.TypeID;

import org.openide.util.Utilities;

public class ColumnDescriptor extends ComponentDescriptor{
    
    public final static String NAME="COLUMN#COMPONENT";
    private static final Image FOREIGN_KEY_IMAGE = Utilities.loadImage ("org/netbeans/modules/erd/resources/key_f.png"); // NOI18N
    private static final Image PRIMARY_KEY_IMAGE = Utilities.loadImage ("org/netbeans/modules/erd/resources/key_p.png"); // NOI18N
    private static final Image PF_KEY_IMAGE = Utilities.loadImage ("org/netbeans/modules/erd/resources/key_pf.png"); // NOI18N
    private static int size=16;
    
    public final static TypeID type=new TypeID(TypeID.TYPE.COMPONENT,NAME);
    
    public enum PROPERTY {TABLE,COLUMN_NAME,IS_PK,IS_FK,SQL_TYPE,PRECISION,IS_NULL};
    
    String id;
    String parent;
    boolean isPK;
    boolean isFK;
    /** Creates a new instance of ColumnComponent */
    public ColumnDescriptor() {
       
    
    }
    
        
    public String getType(){
        return NAME;
    }
    
   
    
    private String getPrecision(){
        String precision=getProperty(PROPERTY.PRECISION);
        if(precision.equals("")){
            return new String();
        }
        else {
            return "("+precision+")";
        }
    }
    private String createLabel(boolean noIcon) {
        String space="";
        if(noIcon)
            space="    ";
        String columnName=getProperty(PROPERTY.COLUMN_NAME);
        String precision=getProperty(PROPERTY.PRECISION);
        
        
        String sqlType=getProperty(PROPERTY.SQL_TYPE);
        
        String label=space+columnName+": "+sqlType+" "+getPrecision();
        return label;
    }
    
    public void presentComponent(ERDScene scene){
        String pinId=getId();
        String table=getProperty(PROPERTY.TABLE);
        String isFK=getProperty(PROPERTY.IS_FK);
        String isPK=getProperty(PROPERTY.IS_PK);
        
        
        
        List<Image> list=getImage(isFK,isPK);
        ((ColumnWidget) scene.addPin (table, pinId)).setProperties (createLabel(list.isEmpty()), list,columnType);
        
    }
    
    private ColumnWidget.CONSTRAINT_TYPE columnType=ColumnWidget.CONSTRAINT_TYPE.ORDINARY;
    
    private void setColumnType(ColumnWidget.CONSTRAINT_TYPE columnType){
        this.columnType=columnType;
    }
    private List<Image> getImage(String isFK,String isPK){
        List<Image> images=new ArrayList<Image>();
        boolean fk=Boolean.parseBoolean(isFK);
        boolean pk=Boolean.parseBoolean(isPK);
        if(fk && pk){
             images.add(PF_KEY_IMAGE);
             setColumnType(ColumnWidget.CONSTRAINT_TYPE.FP_KEY);
        }     
        else     
         if(fk){
            images.add(FOREIGN_KEY_IMAGE);
            setColumnType(ColumnWidget.CONSTRAINT_TYPE.FOREIGN_KEY);
         }
            else      
             if(pk){
                images.add(PRIMARY_KEY_IMAGE);
                setColumnType(ColumnWidget.CONSTRAINT_TYPE.PRIMARY_KEY);
             }
             
        return images;
        
           
    }
 
    
}