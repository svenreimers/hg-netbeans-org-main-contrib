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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets;

import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.NbBundle;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.javamodel.*;

/**
 *
 * @author Ajit
 */
public class DescriptionWidget extends AbstractTitledWidget implements TabWidget {
    
   // private MethodModel method;
    //private transient JavadocModel model;
    
    private transient Widget buttons;
    private transient ImageLabelWidget headerLabelWidget;
    private transient Widget tabComponent;
    
    /** Creates a new instance of Description
     * @param scene
     * @param method
     */
    public DescriptionWidget(ObjectScene scene,MethodModel method) {
        super(scene,0,RADIUS,1,BORDER_COLOR);
      //  this.method = method;
       // model = method.getJavadoc();
        createContent();
    }
    
    protected Paint getTitlePaint(Rectangle bounds) {
        return TITLE_COLOR_DESC;
    }
    
    private void createContent() {
        populateContentWidget(getContentWidget());
        headerLabelWidget = new ImageLabelWidget(getScene(), getIcon(), getTitle());
        headerLabelWidget.setLabelFont(getScene().getFont().deriveFont(Font.BOLD));
        getHeaderWidget().addChild(new Widget(getScene()),5);
        getHeaderWidget().addChild(headerLabelWidget);
        getHeaderWidget().addChild(new Widget(getScene()),4);
        
        buttons = new Widget(getScene());
        buttons.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.CENTER, 8));
        buttons.addChild(getExpanderWidget());
        getHeaderWidget().addChild(buttons);
    }
    
    private void populateContentWidget(Widget parentWidget) {
 ////        EditorPaneWidget descPaneWidget = new EditorPaneWidget(getScene(),model.getText(),"text/java");
   //     descPaneWidget.setBorder(BorderFactory.createEmptyBorder(1));
     //   descPaneWidget.setEditable(false);
      //  parentWidget.addChild(descPaneWidget);
    }
    
    /*public Object hashKey() {
        return model;
    }*/
    
    public String getTitle() {
        return NbBundle.getMessage(DescriptionWidget.class, "LBL_Description");
    }
    
    public Image getIcon() {
        return null;
    }
    
    public Widget getComponentWidget() {
        if(tabComponent==null) {
            tabComponent = createContentWidget();
            populateContentWidget(tabComponent);
        }
        return tabComponent;
    }
}
