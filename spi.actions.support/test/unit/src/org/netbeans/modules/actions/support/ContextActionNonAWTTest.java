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

package org.netbeans.modules.actions.support;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.actions.support.ContextActionPerformer;
import org.netbeans.spi.actions.support.ContextSelection;
import org.netbeans.spi.actions.support.Factory;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/** Test that cookie actions are in fact sensitive to the correct cookies in the
 * correct numbers, and that changes to either node selection or cookies on the
 * selected nodes trigger a change in the selected state.
 * @author Jesse Glick
 */
public class ContextActionNonAWTTest extends ContextActionTest {
    public ContextActionNonAWTTest(String name) {
        super(name);
    }
    
    protected boolean runInEQ() {
        return false;
    }
    
    protected boolean getIsEnabled(final Action a1) throws InterruptedException, InvocationTargetException {
        assertFalse("Not in AWT", EventQueue.isDispatchThread());
        
        class R implements Runnable {
            boolean is;
            public void run() {
                is = a1.isEnabled();
            }
        }
        R run = new R();
        SwingUtilities.invokeAndWait(run);
        return run.is;
    }
    protected void doActionPerformed(final Action a1, final ActionEvent ev) throws InterruptedException, InvocationTargetException {
        assertFalse("Not in AWT", EventQueue.isDispatchThread());
        
        class R implements Runnable {
            public void run() {
                a1.actionPerformed(ev);
            }
        }
        R run = new R();
        SwingUtilities.invokeAndWait(run);
    }
    
}

