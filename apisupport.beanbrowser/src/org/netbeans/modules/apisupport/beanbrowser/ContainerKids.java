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

package org.netbeans.modules.apisupport.beanbrowser;

import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Collections;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/** Children list of an AWT Container. */
public class ContainerKids extends Children.Keys {

    private Container container;
    private ContainerListener containerListener = null;

    public ContainerKids(Container container) {
        this.container = container;
    }

    protected void addNotify() {
        updateKeys();
        if (containerListener == null) {
            containerListener = new ContainerListener() {
                public void componentAdded(ContainerEvent ev) {
                    updateKeys();
                }
                public void componentRemoved(ContainerEvent ev) {
                    updateKeys();
                }
            };
            container.addContainerListener(containerListener);
        }
    }

    protected void removeNotify() {
        if (containerListener != null) {
            container.removeContainerListener(containerListener);
            containerListener = null;
        }
        setKeys(Collections.EMPTY_SET);
    }
    
    private void updateKeys() {
        setKeys(container.getComponents());
    }
    
    protected Node[] createNodes(Object key) {
        return new Node[] { PropSetKids.makeObjectNode(key) };
    }
    
}
