/*
 * TrafficNode.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.openide.nodes.*;
import org.openide.util.Utilities;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * TrafficNode: a FilterNode which adds a "traffic light" to
 * the lower-right portion of a node's icon.  A traffic light
 * may add a green, yellow or red circle, or nothing as
 * specified.
 */
public class TrafficNode extends FilterNode implements Node.Cookie {
    private Light light;
    private Node.Cookie handlerCookie;

    public TrafficNode(Node original, ClassMetrics cm) {
        super(original);
	addCookie(cm);
        light = cm.getWarningLight();
        cm.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ClassMetrics.LIGHT_PROP))
                    setLight((Light)evt.getNewValue());
            }
        });
    }

    public TrafficNode(Node original, MethodMetrics mm) {
        super(original);
	addCookie(mm);
        light = mm.getWarningLight();
        mm.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ClassMetrics.LIGHT_PROP))
                    setLight((Light)evt.getNewValue());
            }
        });
    }

    public void setLight(Light light) {
        if (this.light != light) {
            this.light = light;
            fireIconChange();
            fireOpenedIconChange();
        }
    }

    public Light getLight() {
        return light;
    }

    public Image getIcon (int type) {
        return addBadge(getOriginal().getIcon(type));
    }

    public Image getOpenedIcon (int type) {
        return addBadge(getOriginal().getOpenedIcon(type));
    }

    private Image addBadge(Image image) {
        if (light != Light.NONE) {
            Image badge = light.getBadge();
            if (badge != null)
                return Utilities.mergeImages(image, badge, 16, 8);
        }
        return image;
    }

    public static class Light {
        public final static Light NONE = new Light("none");
        public final static Light GREEN = new Light("green");
        public final static Light YELLOW = new Light("yellow");
        public final static Light RED = new Light("red");

        String name;
        Image badge;

        private Light(String name) {
            this.name = name;
            badge = null;
        }

        public Image getBadge() {
            if (badge == null) {
                String gif = "org/netbeans/modules/metrics/resources/" +
                    name + ".gif";
                badge = Utilities.loadImage(gif);
            }
            return badge;
        }

        public String toString() {
            return name;
        }

    }

    /* Delegates to original, unless it is a NodeHandler cookie.
    *
    * @param type the class to look for
    * @return instance of that class or null if this class of cookie
    *    is not supported
    */
    public Node.Cookie getCookie (Class type) {
	if (type == TrafficNode.class)
	    return this;
	else if (type == NodeHandler.class)
	    return handlerCookie;
        else
	    return super.getCookie (type); // delegate to original node
    }

    private void addCookie(NodeHandler metricsObject) {
	// The Node API doesn't support filters well...
	handlerCookie = metricsObject;
    }
}
