/*
 * MetricsNode.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import java.util.ResourceBundle;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import java.beans.*;
import javax.swing.Action;

/**
 * The base class for specific metric nodes.
 *
 * @author  Tom Ball
 */
class MetricsNode extends AbstractNode implements Cloneable {

    /** ResourceBundle used in this class. */
    static ResourceBundle bundle = 
        NbBundle.getBundle (MetricsNode.class);

    // Associated class
    private ClassMetrics classMetrics;
    
    /** Creates new MetricsNode */
    public MetricsNode(ClassMetrics classMetrics) {
        super (Children.LEAF);
 
        setName(MetricsNode.getString( "LBL_Metrics" ) );
        setShortDescription(MetricsNode.getString( "HINT_Metrics" ) );
        setIconBaseWithExtension("org/netbeans/modules/metrics/resources/barchart.gif");
        this.classMetrics = classMetrics;
	setPropertyListener();
    }

    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }

    private void setPropertyListener() {
        classMetrics.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                resetMetrics();
            }
        });
    }

    /** Update the metrics for this node. */
    private void resetMetrics() {
        Sheet s = getSheet();
        Sheet.Set props = s.get(Sheet.PROPERTIES);

        Metric[] metrics = classMetrics.getMetrics();
        for (int i = 0, n = metrics.length; i < n; i++) {
            Metric m = metrics[i];
            if (!m.needsOtherClasses()) {
                MetricProp mp = (MetricProp)props.get(m.getName());
                mp.setValue(m.getMetricValue());
            }
        }

        firePropertySetsChange(null, null);
    }
    
    /**
     * Returns a Sheet used to change this Property.
     * @return the property sheet
     */
    protected Sheet createSheet () {
        Sheet s = super.createSheet();

        Sheet.Set props = s.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            s.put (props);
        }

        Metric[] metrics = classMetrics.getMetrics();
        for (int i = 0, n = metrics.length; i < n; i++) {
            Metric m = metrics[i];
            if (!m.needsOtherClasses()) {
                MetricProp mp = new MetricProp(m.getName(), 
                                               m.getShortDescription(),
                                               m.getDetailsViewer());
                mp.setValue(m.getMetricValue());
                props.put (mp);
            }
        }

        return s;
    }

    private static class MetricProp extends PropertySupport.ReadOnly {
        private Object value;
        private PropertyEditor viewer;

        public MetricProp (String name, String desc, PropertyEditor viewer) {
            super (name, String.class, name, desc);
            this.viewer = viewer;
        }

        public Object getValue () {
            return value;
        }

        public void setValue (Object nue) {
            value = nue;
            viewer.setValue(nue);
        }
        
        public PropertyEditor getPropertyEditor() {
            return viewer;
        }
    }
    
   /**
     * Returns an Array of Actions allowed by this Node.
     * @return a list of standard actions
     */
    public Action[] getActions (boolean context) {
	if (context)
	    return super.getActions(context);
        if (systemActions == null) {
            systemActions = new SystemAction[] {
               SystemAction.get (ToolsAction.class),
               SystemAction.get (PropertiesAction.class),
	    };
        }
        return systemActions;
    }
    private SystemAction[] systemActions = null;

    public Object clone() throws CloneNotSupportedException {
	MetricsNode node = (MetricsNode)super.clone();
	node.setPropertyListener();
        return node;
    }
  
    public boolean canCopy () {
        return true;
    }
    
    public boolean canCut () {
        return false;
    }
    
    public boolean canRename () {
        return false;
    }
    
    public boolean canDestroy () {
        return false;
    }

    static String getString(String key) {
        return bundle.getString(key);
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(MetricsNode.class);
    }
}
