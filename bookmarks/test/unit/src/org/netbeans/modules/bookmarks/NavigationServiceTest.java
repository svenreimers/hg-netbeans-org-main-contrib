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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.bookmarks;

import javax.naming.*;
import javax.swing.*;
import javax.swing.event.*;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.*;
import org.openide.util.Utilities;
import org.openide.windows.*;

import org.netbeans.api.bookmarks.*;

/**
 * Tests in this class should test the NavigationService
 * implementation.
 * @author David Strupl
 */
public class NavigationServiceTest extends NbTestCase {
    
    static {
        // Get Lookup right to begin with.
        ActionsInfraHid.class.getName();
    }
    
    public NavigationServiceTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(NavigationServiceTest.class));
    }

    /**
     * This tests verifies that the NavigationService properly
     * fires event when someone calls storeNavigationEvent.
     * The tests performs following steps:
     * <OL><LI> Register a listener to the NavigationService
     *     <LI> Stores a navigation event
     *     <LI> checks whether property change event was fired
     * </OL>
     */
    public void testFiresChangeEvent() throws Exception {
        final boolean []fired = new boolean[1];
        NavigationService ns = NavigationService.getDefault();
        ns.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                fired[0] = true;
            }
        });
        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                return true;
            }
            public TopComponent getTopComponent() {
                return new TopComponent();
            }
        });
        assertTrue("Event not fired", fired[0]);
    }
    
    /**
     * This test checks whether we can navigate back after storing
     * an event.
     * <OL><LI> creates a testing top component
     *     <LI> stores an initial navigation event
     *     <LI> activates the top component
     *     <LI> checks whether it really is activated
     *     <LI> stores a navigation event with this top component
     *     <LI> checks whether it is possible to go back
     *     <LI> performs the step back
     * </OL>
     */
    public void testCanNavigateBackwardAfterStoring() throws Exception {
        final TopComponent tc = new TopComponent();
        NavigationService ns = NavigationService.getDefault();
        final boolean []restored = new boolean[1];
        // the initial event
        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                restored[0] = true;
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        // activate the top component
        ActionsInfraHid.UT.setActivated(tc);
        assertEquals("our top component should be activated",
            WindowManager.getDefault().getRegistry().getActivated(), tc);

        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        assertTrue("Should be possible to navigate back", ns.canNavigateBackward());
        
        ns.backward();
        assertTrue("Did not restore the state" , restored[0]);
        
        tc.close();
    }
    
    /**
     * This test checks whether we can navigate forward after
     * going back.
     * <OL><LI> creates a testing top component
     *     <LI> stores an initial navigation event
     *     <LI> activates the top component
     *     <LI> checks whether it really is activated
     *     <LI> stores a navigation event with this top component
     *     <LI> checks whether it is possible to go back
     *     <LI> performs the step back
     *     <LI> checks whether it is possible to navigate forward
     *     <LI> performs the step forward
     *     <LI> checks whether the step forward was done
     *     <LI> now it should be possible to go back again
     * </OL>
     */
    public void testCanNavigateForwardAfterBackward() throws Exception {
        final TopComponent tc = new TopComponent();
        NavigationService ns = NavigationService.getDefault();
        // the initial event
        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        // activate the top component
        ActionsInfraHid.UT.setActivated(tc);
        assertEquals("our top component should be activated",
            WindowManager.getDefault().getRegistry().getActivated(), tc);
        
        final boolean []restored = new boolean[1];
        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                restored[0] = true;
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        assertTrue("Should be possible to navigate back", ns.canNavigateBackward());
        
        ns.backward();
        
        assertTrue("Should be able to navigate forward now", ns.canNavigateForward());
        
        ns.forward();
        
        assertTrue("The state should be restored", restored[0]);
        assertTrue("Should be possible to navigate back", ns.canNavigateBackward());
        
        tc.close();
    }
    
    /**
     * After storing the event it should be impossible to step
     * forward.
     * <OL><LI> creates a testing top component
     *     <LI> stores an initial navigation event
     *     <LI> activates the top component
     *     <LI> checks whether it really is activated
     *     <LI> stores a navigation event with this top component
     *     <LI> checks whether it is impossible to go forward
     */
    public void testCannotForwardAfterStoring() throws Exception {
        final TopComponent tc = new TopComponent();
        NavigationService ns = NavigationService.getDefault();
        // the initial event
        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        // activate the top component
        ActionsInfraHid.UT.setActivated(tc);
        assertEquals("our top component should be activated",
            WindowManager.getDefault().getRegistry().getActivated(), tc);

        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        assertTrue("Should be impossible to navigate forward", ! ns.canNavigateForward());
        
        tc.close();
    }
    
    /**
     * This test verifies that storing the navigation event does not lead
     * to navigation when our top component is not activated.
     * <OL><LI> activate some top componnet
     *     <LI> create another one
     *     <LI> Store the initial event
     *     <LI> store one more event for the same top component
     *     <LI> shold be impossible to navigate
     * </OL>
     */
    public void testCannotNavigateWhenNotActivated() throws Exception {
        final TopComponent tc1 = new TopComponent();
        // activate the top component
        ActionsInfraHid.UT.setActivated(tc1);
        final TopComponent tc = new TopComponent();
        NavigationService ns = NavigationService.getDefault();
        assertTrue("1. Should be impossible to navigate back", ! ns.canNavigateBackward());
        final boolean []restored = new boolean[1];
        // the initial event
        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                restored[0] = true;
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        assertTrue("our top component should not be activated",
            WindowManager.getDefault().getRegistry().getActivated() != tc);

        ns.storeNavigationEvent(new NavigationEvent() {
            public boolean restoreState() {
                return true;
            }
            public TopComponent getTopComponent() {
                return tc;
            }
        });
        assertTrue("2. Should be impossible to navigate back", ! ns.canNavigateBackward());
        
        tc.close();
        tc1.close();
    }
}
