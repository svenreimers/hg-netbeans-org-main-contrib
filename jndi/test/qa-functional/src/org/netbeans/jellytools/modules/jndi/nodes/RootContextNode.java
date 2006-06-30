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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.jellytools.modules.jndi.nodes;

import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.jndi.actions.*;
import org.netbeans.jellytools.nodes.Node;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import org.netbeans.jellytools.modules.jndi.actions.AddDirectoryAction;
import org.netbeans.jemmy.operators.JTreeOperator;

/** RootContextNode Class
 * @author dave */
public class RootContextNode extends Node {

    private static final Action copyLookupCodeAction = new CopyLookupCodeAction();
    private static final Action copyBindingCodeAction = new CopyBindingCodeAction();
    private static final Action refreshAction = new RefreshAction();
    private static final Action disconnectContextAction = new DisconnectContextAction();
    private static final Action addDirectoryAction = new AddDirectoryAction();
    private static final Action propertiesAction = new PropertiesAction();

    /** creates new RootContextNode
     * @param tree JTreeOperator of tree
     * @param treePath String tree path */
    public RootContextNode(JTreeOperator tree, String treePath) {
        super(tree, JNDIRootNode.NAME + treePath);
    }

    /** creates new RootContextNode
     * @param tree JTreeOperator of tree
     * @param treePath TreePath of node */
    public RootContextNode(JTreeOperator tree, TreePath treePath) {
        super(tree, treePath);
    }

    /** creates new RootContextNode
     * @param parent parent Node
     * @param treePath String tree path from parent Node */
    public RootContextNode(Node parent, String treePath) {
        super(parent, treePath);
    }

    /** tests popup menu items for presence */
    public void verifyPopup() {
        verifyPopup(new Action[]{
            copyLookupCodeAction,
            copyBindingCodeAction,
            refreshAction,
            disconnectContextAction,
            propertiesAction
        });
    }

    /** performs CopyLookupCodeAction with this node */
    public void copyLookupCode() {
        copyLookupCodeAction.perform(this);
    }

    /** performs CopyBindingCodeAction with this node */
    public void copyBindingCode() {
        copyBindingCodeAction.perform(this);
    }

    /** performs RefreshAction with this node */
    public void refresh() {
        refreshAction.perform(this);
    }

    /** performs DisconnectContextAction with this node */
    public void disconnectContext() {
        disconnectContextAction.perform(this);
    }

    /** performs AddDirectoryAction with this node */
    public void addDirectory() {
        addDirectoryAction.perform(this);
    }

    /** performs PropertiesAction with this node */
    public void properties() {
        propertiesAction.perform(this);
    }
}
