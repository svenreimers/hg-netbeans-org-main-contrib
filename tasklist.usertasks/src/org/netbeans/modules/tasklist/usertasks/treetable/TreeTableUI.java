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
package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTableUI;

/**
 * TODO.
 *
 * @author tl
 */
public class TreeTableUI extends BasicTableUI {
    private JTree tree;

    public TreeTableUI(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        g.translate(100, 0);
        c.remove(tree);
        c.add(tree);
        tree.setBounds(100, 0, 400, 400);
        tree.paint(g);
        g.translate(-100, 0);
    }    
}