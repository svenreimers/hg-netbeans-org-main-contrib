/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.venice.sourcemodel.api;

import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.jmi.javamodel.NamedElement;
import org.netbeans.modules.venice.model.Model;
import org.netbeans.modules.venice.model.ModelProvider;
import org.netbeans.modules.venice.sourcemodel.SrcModel;

/**
 * SrcModelProvider is a
 *
 * @author Tim Boudreau
 */
public class SrcModelProvider implements ModelProvider {

    public SrcModelProvider() {
    }

    public boolean accept(Object o) {
	return o instanceof NamedElement;
    }

    
    private static final Map map =  new WeakHashMap();
    public Model createModel(Object o) {
	 assert o instanceof NamedElement;
	 Model result = (Model) map.get(o);
	 if (result == null) {
	     result = new SrcModel ((NamedElement) o);
	     map.put (o, result);
	 }
	 return result;
    }
    
}
