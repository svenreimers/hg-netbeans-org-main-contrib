/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.callgraph.impl;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.callgraph.api.Call;
import org.netbeans.modules.cnd.callgraph.api.Function;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;

/**
 * @author David Kaspar
 */
public class CallGraphScene extends GraphScene<Function,Call> {

    private static final Border BORDER_4 = BorderFactory.createLineBorder (4);

    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;

    private WidgetAction moveAction = ActionFactory.createMoveAction ();
    private WidgetAction openAction = ActionFactory.createEditAction (new EditProvider() {
            public void edit (Widget widget) {
            }
        });

    public CallGraphScene() {
        mainLayer = new LayerWidget (this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget (this);
        addChild(connectionLayer);
    }

    protected Widget attachNodeWidget (Function node) {
        LabelWidget label = new LabelWidget (this, node.getName());
        label.setBorder (BORDER_4);
        label.getActions().addAction(moveAction);
        label.getActions().addAction(ActionFactory.createEditAction(new NodeEditProvider(node)));
        mainLayer.addChild (label);
        return label;
    }

    protected Widget attachEdgeWidget(Call edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        connection.getActions().addAction(ActionFactory.createEditAction(new EdgeEditProvider(edge)));
        connectionLayer.addChild (connection);
        return connection;
    }

    protected void attachEdgeSourceAnchor(Call edge, Function oldSourceNode, Function sourceNode) {
        Widget w = sourceNode != null ? findWidget (sourceNode) : null;
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    protected void attachEdgeTargetAnchor(Call edge, Function oldTargetNode, Function targetNode) {
        Widget w = targetNode != null ? findWidget (targetNode) : null;
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    private static class NodeEditProvider implements EditProvider {
        private Function node;
        private NodeEditProvider(Function node){
            this.node = node;
        }
        
        public void edit(Widget widget) {
            CsmUtilities.openSource((CsmFunction)node.getDefinition());
        }
    }

    private static class EdgeEditProvider implements EditProvider {
        private Call call;
        private EdgeEditProvider(Call call){
            this.call = call;
        }
        
        public void edit(Widget widget) {
            CsmUtilities.openSource((CsmReference)call.getReferencedCall());
        }
    }
}
