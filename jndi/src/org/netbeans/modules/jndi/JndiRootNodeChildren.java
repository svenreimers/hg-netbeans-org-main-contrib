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

package org.netbeans.modules.jndi;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.Context;
import org.openide.TopManager;
import org.openide.nodes.*;
import org.netbeans.modules.jndi.settings.JndiSystemOption;

import org.netbeans.modules.projects.CurrentProjectNode;
import org.openide.util.Lookup;

/**
 *
 *
 * @author  Tomas Zezula
 */
public class JndiRootNodeChildren extends Children.Keys implements PropertyChangeListener {
    
    private ArrayList contexts;
    private JndiSystemOption settings;
    private JndiProvidersNode providersNode;

    /** Creates new JndiRootNodeChildren */
    public JndiRootNodeChildren() {
    }
    
    
    /** Called by IDE when the children are needed
     */
    public void addNotify () {
        CurrentProjectNode.getDefault().addPropertyChangeListener(this);
        this.update ();
    }
    
    /** Called by IDE when the children are disposing
     */
    public void removeNotify () {
        CurrentProjectNode.getDefault().removePropertyChangeListener(this);
        this.setKeys (new Object[0]);
    }
    
    /** Adds the node to children list
     *  @param Hashtable environment of the JNDIContext
     */
    public void add (Hashtable contextProperties) {
        if (this.settings == null)
            this.init ();
        this.contexts.add (contextProperties);
        this.settings.setInitialContexts (this.contexts);
        this.update();
    }
    
    /** Adds the node to the specified position
     *  @param Hashtable environment of the JndiContext
     *  @param int index
     */
    public void add (Hashtable contextProperties, int index) {
        
        if (this.settings == null)
            this.init ();
        this.contexts.add (index, contextProperties);
        this.settings.setInitialContexts (this.contexts);
        this.update();
    }
    
    /** Removes node from children list
     *  @param int index
     */
    public void remove (int index) {
        if (index <0 || index >= this.contexts.size())
            return;
        if (this.settings == null)
            this.init ();
        this.contexts.remove (index);
        this.settings.setInitialContexts (this.contexts);
        this.update();
    }
    
    /** Rebuilds the nodes according to contexts
     */
    public void update () {
        if (this.settings == null)
            this.init ();
        ArrayList keys = new ArrayList ();
        keys.add ( new ProvidersKey());
        Iterator it = this.contexts.iterator();
        for (int index=0; it.hasNext(); index++) {
            Hashtable env = (Hashtable) it.next();
            JndiRootCtxKey key = new JndiRootCtxKey (env,index);
            keys.add (key);
        }
        this.setKeys (keys);
    }
    
    public void updateKey (Object key) {
        this.refreshKey (key);
    }
    
    
    public JndiProvidersNode getProvidersNode () {
        if (this.providersNode == null)
            this.providersNode = new JndiProvidersNode ();
        return this.providersNode;
    }
    
    /** Called by TopManager when project changed.
     *  @param PropertyChangeEvent event
     */
    public void propertyChange (PropertyChangeEvent event) {
        if (CurrentProjectNode.PROP_PROJECT_AFTER_OPEN.equals (event.getPropertyName())) {
            // Project has changed
            this.settings = null;
            this.update ();
        }
    }

    /** Create nodes for a given key.
     * @param key the key
     * @return child nodes for this key or null if there should be no
     *   nodes for this key
     */
    protected Node[] createNodes(Object key) {
        if (key instanceof ProvidersKey) {
            return new Node[] {this.getProvidersNode()};
        }
        else if (key instanceof JndiRootCtxKey) {
            JndiRootCtxKey jndiRootCtxKey = (JndiRootCtxKey) key;
            try {
                Context ctx = new JndiDirContext (jndiRootCtxKey.getEnvironment());
                String root = (String)jndiRootCtxKey.getEnvironment().get(JndiRootNode.NB_ROOT);
                if (root != null) {
                    ctx = (Context) ctx.lookup(root);
                }
                else {
                    ((JndiDirContext)ctx).checkContext();
                }
                JndiNode jndiNode = new JndiNode (ctx, jndiRootCtxKey.getIndex());
                return new Node[] {jndiNode};
            }catch (NamingException e) {
                return new Node[] {new JndiDisabledNode (jndiRootCtxKey.getEnvironment(), jndiRootCtxKey.getIndex())};
            }
        }
        else {
            return new Node[0];
        }
    }
    
    
    /** Initializes binding to settings
     */
    private void init () {     
        this.settings = (JndiSystemOption)Lookup.getDefault().lookup(JndiSystemOption.class);
        this.contexts = this.settings.getInitialContexts();
    }
    
}
