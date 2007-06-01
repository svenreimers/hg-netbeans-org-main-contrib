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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import org.netbeans.api.bookmarks.*;

/**
 * Implementation of the NavigationService.
 * @author David Strupl
 */
public class NavigationServiceImpl extends NavigationService implements PropertyChangeListener {

    /**
     * Maps TopComponent --> List (of NavigationEvent)
     * Stores all NavigationEvents in this service.
     */
    private Map navigationEvents;
    
    /**
     * Maps TopComponent --> Integer (index to the list for a given tc). 
     * Keeps track of current navigation event for the TopComponent.
     */
    private Map currentNavigationState;
    
    /**
     * Default constructor adds itself as a listener to
     * the TopComponent.Registry.
     */
    public NavigationServiceImpl() {
        navigationEvents = new WeakHashMap();
        currentNavigationState = new WeakHashMap();
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
    }
    
    /**
     * Navigation events are stored in the NavigationService and allow
     * for the user to use back/forward navigation actions. The navigation
     * events are not stored persistently and are valid only for current
     * session. Each navigation event is bound to a top component so each
     * top component is responsible for creation of navagion events if
     * it wants to use the backward/forward navigation buttons. Please note
     * that the TopComponent has to call this method when it starts and 
     * also always when the top component changes its content. The
     * navigation will use the callback function of NavigationEvent
     * restoreState to call back to the top component after the user
     * invokes the forward/backward buttons.
     * @see NavigationEvent
     */
    public void storeNavigationEvent(NavigationEvent ev) {
        TopComponent tc = ev.getTopComponent();
        List list = (List)navigationEvents.get(tc);
        if (list == null) {
            list = new LimitedSizeList(80, 120);
            navigationEvents.put(tc, list);
        }
        Integer currentIndex = (Integer)currentNavigationState.get(tc);
        if (currentIndex != null) {
            int index = currentIndex.intValue();
            if (index >= 0) {
                for (int i = list.size()-1; i > index; i--) {
                    list.remove(i);
                }
            }
        }
        currentNavigationState.put(tc, new Integer(list.size()));
        list.add(ev);
        fireChangeEvent();
    }

    /**
     * For a given top component this method clears its navigation state.
     */
    public void resetNavigation(TopComponent tc) {
        navigationEvents.remove(tc);
        currentNavigationState.remove(tc);
        fireChangeEvent();
    }
    
    /**
     * This method is called when the user invokes forward action from
     * the GUI. If canNavigateForward() returns true this method should
     * bring the activated top component to the state it stored with
     * storeNavigationEvent().
     */
    public void forward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        Integer currentIndex = (Integer)currentNavigationState.get(tc);
        if ((list == null) || (currentIndex == null)) {
            return;
        }
        int index = currentIndex.intValue();
        if (index + 1 >= list.size()) {
            return;
        }
        NavigationEvent ev = (NavigationEvent)list.get(index+1);
        if (ev.restoreState()) {
            currentNavigationState.put(tc, new Integer(index+1));
            fireChangeEvent();
        }
    }
    
    /**
     * This method is called when the user invokes backward action from
     * the GUI. If canNavigateBackward() returns true this method should
     * bring the activated top component to the state it stored with
     * storeNavigationEvent().
     */
    public void backward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        Integer currentIndex = (Integer)currentNavigationState.get(tc);
        if ((list == null) || (currentIndex == null)) {
            return;
        }
        int index = currentIndex.intValue();
        if (index - 1 < 0) {
            return;
        }
        NavigationEvent ev = (NavigationEvent)list.get(index-1);
        if (ev.restoreState()) {
            currentNavigationState.put(tc, new Integer(index-1));
            fireChangeEvent();
        }
    }
    
    /**
     * This value returned by this method corresponds to the state of
     * the controls for navigation. The returned value depends on
     * the activated TopComponent (see org.openide.windows.WindowManager.getRegistry())
     * and the internal state of the NavigationService with respect
     * to this TopComponent.
     */
    public boolean canNavigateBackward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        Integer currentIndex = (Integer)currentNavigationState.get(tc);
        if ((list == null) || (currentIndex == null)) {
            return false;
        }
        int index = currentIndex.intValue();
        return index > 0;
    }
    
    /**
     * This value returned by this method corresponds to the state of
     * the controls for navigation. The returned value depends on
     * the activated TopComponent (see org.openide.windows.WindowManager.getRegistry())
     * and the internal state of the NavigationService with respect
     * to this TopComponent.
     */
    public boolean canNavigateForward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        Integer currentIndex = (Integer)currentNavigationState.get(tc);
        if ((list == null) || (currentIndex == null)) {
            return false;
        }
        int index = currentIndex.intValue();
        return index+1 < list.size();
    }

    /**
     * We are registered with TopComponent.Registry as propertyChange
     * listener. When the activated top component is changed we
     * recompute the state of our navigation controls.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (NavigationService.getDefault() != this) {
            // this can happen e.g. when the module is uninstalled
            WindowManager.getDefault().getRegistry().removePropertyChangeListener(this);
            return;
        }
        if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
            TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
            fireChangeEvent();
        }
    }
    
    /**
     * Simple subclass of ArrayList that should not grow higher
     * than a certain limit. If this limit is exceeded the size
     * is adjusted to lower bound. NOTE: only the method add(Object)
     * is adapted for such behaviour.
     */
    private static class LimitedSizeList extends ArrayList {
        /** The size of the list is adjusted to this number */
        private int low;
        /** IF the size is greater than this the list is trimmed. */
        private int high;
        
        /**
         * Parameters to the constructor set the limit on the size
         * @param low lower bound on the size when adjusting the size
         * @param high upper bound on the size
         */
        public LimitedSizeList(int low, int high) {
            if (low > high) {
                throw new IllegalArgumentException("Bad arguments low == " + low + " high == " + high); // NOI18N
            }
            this.low = low;
            this.high = high;
        }
        
        /**
         * Before calling the super.add the size is adjusted
         * according to high and low limits.
         */
        public boolean add(Object toAdd) {
            if (size() > high) {
                removeRange(0, high - low);
            }
            return super.add(toAdd);
        }
    }
}
