/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.api.bookmarks;

import java.beans.PropertyChangeListener;
import org.openide.util.actions.Presenter;

/**
 * Bookmark is somewhat similar to Action. It can be invoked and must
 * have menu and toolbar representation. Integral part of the contract
 * for objects implementing this interface is following persistence
 * requirement: object implementing this interface that will be used
 * with BookmarkService must support default persistence mechanism of
 * the NetBeans platform. It means it either has to be Serializable 
 * or has to provide special convertor (properly registered in the system).
 * @author David Strupl
 */
public interface Bookmark extends Presenter.Menu, Presenter.Toolbar {
        
    /** The name identifes the bookmark. It does not have to be unique.
     * The name can be used in the visual representation.
     * @returns name of the bookmark
     */
    public String getName();
    
    /**
     * The name identifes the bookmark. It does not have to be unique.
     * The name can be used in the visual representation. This setter
     * will be called e.g. when the user will try to rename the
     * stored bookmark.
     */
    public void setName(String newName);
    
    /**
     * Main action method called when the user selects the bookmark.
     * This method is called after the user
     * selects the bookmark from the menu or toolbar. So calling this method
     * is contained in the default implementation of the menu and toolbar
     * presenters for the bookmark.
     */
    public void invoke();
    
    /**
     * The bookmark should fire property change when the the internal state
     * (e.g. name) is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl);
    
    /**
     * The bookmark should fire property change when the the internal state
     * (e.g. name) is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl);
    
    /**
     * This method is used to fire the PropertyChangeEvents.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
}
