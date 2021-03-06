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

package org.netbeans.modules.corba.poasupport;

import java.util.*;
import java.net.*;
import java.io.*;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.settings.POASettings;
import org.netbeans.modules.corba.poasupport.tools.POASourceMaker;

/*
 * @author Dusan Balek
 */

public class RootPOAElement extends POAElement implements java.beans.PropertyChangeListener {

    public static String PROP_ORB_VAR_NAME = "ORBVarName"; // NOI18N

    private ArrayList poaListeners = new ArrayList();
    private String orbVarName;
    private LinkedList poaElements = new LinkedList();
    private LinkedList servantElements = new LinkedList();
    private LinkedList defaultServantElements = new LinkedList();
    private LinkedList servantManagerElements = new LinkedList();
    private LinkedList poaActivatorElements = new LinkedList();
    private Hashtable usedVarNames = new Hashtable();
    
    protected POASourceMaker maker;
    
    public RootPOAElement(String _varName, String _orbVarName, boolean _writeable, POASourceMaker _maker) {
        super(_writeable);
        maker = _maker;
        setPOAName(POASupport.getString("LBL_RootPOA_node"));
        setVarName(_varName);
        usedVarNames.put(_varName, this);
        setORBVarName(_orbVarName);
        usedVarNames.put(_orbVarName, this);
        rootPOA = this;
        addPropertyChangeListener(this);
    }
    
    public boolean isRootPOA() {
        return true;
    }
    
    public boolean canUseAsNewVarNameFor(String name, Object element) {
        Object o = usedVarNames.get(name);
        if (o == null)
            return true;
        return (o.equals(element)) ? true : false;
    }
    
    public boolean canUseAsVarNameFor(String name, Object element) {
        Object o = usedVarNames.get(name);
        if (o == null)
            return true;
        return (o.getClass().equals(element.getClass())) ? true : false;
    }
    
    public String getORBTag() {
        return maker.getORBTag();
    }

    public void setORBTag(String newTag) {
        maker.setORBTag(newTag);
    }

    public String getORBVarName() {
        return orbVarName;
    }
    
    public void setORBVarName(String _orbVarName) {
        if (!_orbVarName.equals(orbVarName)) {
            String oldName = orbVarName;
            orbVarName = _orbVarName;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_ORB_VAR_NAME, oldName, orbVarName));
        }
    }
    
    public synchronized void addPOAListener(POAListener l) {
        poaListeners.add(l);
    }
    
    public synchronized void removePOAListener(POAListener l) {
        poaListeners.remove(l);
    }
    
    public synchronized void addPOAToList(POAElement aPOA) {
        poaElements.add(aPOA);
        if (!usedVarNames.containsKey(aPOA.getVarName()))
            usedVarNames.put(aPOA.getVarName(), aPOA);
        aPOA.addPropertyChangeListener(this);
        firePOAHierarchyChange();
    }
    
    public synchronized void removePOAFromList(POAElement aPOA) {
        Vector chPOAs = aPOA.getChildPOAs();
        for (int i = 0; i < chPOAs.size(); i++)
            removePOAFromList((POAElement)chPOAs.get(i));
        Vector chServants = aPOA.getServants();
        for (int i = 0; i < chServants.size(); i++)
            removeServantFromList((ServantElement)chServants.get(i));
        if (aPOA.getDefaultServant() != null)
            removeDefaultServantFromList(aPOA.getDefaultServant());
        if (aPOA.getServantManager() != null)
            removeServantManagerFromList(aPOA.getServantManager());
        if (aPOA.getPOAActivator() != null)
            removePOAActivatorFromList(aPOA.getPOAActivator());
        poaElements.remove(aPOA);
        if (aPOA.equals(usedVarNames.get(aPOA.getVarName()))) {
            usedVarNames.remove(aPOA.getVarName());
            ListIterator li = getPOAListIterator();
            while (li.hasNext()) {
                POAElement ne = (POAElement)li.next();
                if (ne.getVarName().equals(aPOA.getVarName())) {
                    usedVarNames.put(ne.getVarName(), ne);
                    break;
                }
            }
        }
        ListIterator otherPOAs = getPOAListIterator();
        while (otherPOAs.hasNext()) {
            POAElement _POA = (POAElement)otherPOAs.next();
            if (aPOA.getVarName().equals(_POA.getManager()))
                _POA.setManager(null);
        }
        aPOA.removePropertyChangeListener(this);
        firePOAHierarchyChange();
        firePOAMembersChange();
    }
    
    public synchronized ListIterator getPOAListIterator() {
        return poaElements.listIterator();
    }
    
    public synchronized void addServantToList(ServantElement aServant) {
        servantElements.add(aServant);
        if (!usedVarNames.containsKey(aServant.getVarName()))
            usedVarNames.put(aServant.getVarName(), aServant);
        if (aServant.getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID)
            if (!usedVarNames.containsKey(aServant.getIDVarName()))
                usedVarNames.put(aServant.getIDVarName(), aServant);
        aServant.addPropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized void removeServantFromList(ServantElement aServant) {
        servantElements.remove(aServant);
        if (aServant.equals(usedVarNames.get(aServant.getVarName()))) {
            usedVarNames.remove(aServant.getVarName());
            ListIterator li = getServantListIterator();
            while (li.hasNext()) {
                ServantElement se = (ServantElement)li.next();
                if (se.getVarName().equals(aServant.getVarName())) {
                    usedVarNames.put(se.getVarName(), se);
                    break;
                }
            }
        }
        if (aServant.getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID)
            if (aServant.equals(usedVarNames.get(aServant.getIDVarName()))) {
                usedVarNames.remove(aServant.getIDVarName());
                ListIterator li = getServantListIterator();
                while (li.hasNext()) {
                    ServantElement se = (ServantElement)li.next();
                    if (aServant.getIDVarName().equals(se.getIDVarName())) {
                        usedVarNames.put(se.getIDVarName(), se);
                        break;
                    }
                }
            }
        aServant.removePropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized ListIterator getServantListIterator() {
        return servantElements.listIterator();
    }
    
    public synchronized void addDefaultServantToList(DefaultServantElement aServant) {
        defaultServantElements.add(aServant);
        if (!usedVarNames.containsKey(aServant.getVarName()))
            usedVarNames.put(aServant.getVarName(), aServant);
        aServant.addPropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized void removeDefaultServantFromList(DefaultServantElement aServant) {
        defaultServantElements.remove(aServant);
        if (aServant.equals(usedVarNames.get(aServant.getVarName()))) {
            usedVarNames.remove(aServant.getVarName());
            ListIterator li = getDefaultServantListIterator();
            while (li.hasNext()) {
                DefaultServantElement se = (DefaultServantElement)li.next();
                if (se.getVarName().equals(aServant.getVarName())) {
                    usedVarNames.put(se.getVarName(), se);
                    break;
                }
            }
        }
        aServant.removePropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized ListIterator getDefaultServantListIterator() {
        return defaultServantElements.listIterator();
    }
    
    public synchronized void addServantManagerToList(ServantManagerElement aServant) {
        servantManagerElements.add(aServant);
        if (!usedVarNames.containsKey(aServant.getVarName()))
            usedVarNames.put(aServant.getVarName(), aServant);
        aServant.addPropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized void removeServantManagerFromList(ServantManagerElement aServant) {
        servantManagerElements.remove(aServant);
        if (aServant.equals(usedVarNames.get(aServant.getVarName()))) {
            usedVarNames.remove(aServant.getVarName());
            ListIterator li = getServantManagerListIterator();
            while (li.hasNext()) {
                ServantManagerElement se = (ServantManagerElement)li.next();
                if (se.getVarName().equals(aServant.getVarName())) {
                    usedVarNames.put(se.getVarName(), se);
                    break;
                }
            }
        }
        aServant.removePropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized ListIterator getServantManagerListIterator() {
        return servantManagerElements.listIterator();
    }
    
    public synchronized void addPOAActivatorToList(POAActivatorElement activator) {
        poaActivatorElements.add(activator);
        if (!usedVarNames.containsKey(activator.getVarName()))
            usedVarNames.put(activator.getVarName(), activator);
        activator.addPropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized void removePOAActivatorFromList(POAActivatorElement activator) {
        poaActivatorElements.remove(activator);
        if (activator.equals(usedVarNames.get(activator.getVarName()))) {
            usedVarNames.remove(activator.getVarName());
            ListIterator li = getPOAActivatorListIterator();
            while (li.hasNext()) {
                POAActivatorElement ne = (POAActivatorElement)li.next();
                if (ne.getVarName().equals(activator.getVarName())) {
                    usedVarNames.put(ne.getVarName(), ne);
                    break;
                }
            }
        }
        activator.removePropertyChangeListener(this);
        firePOAMembersChange();
    }
    
    public synchronized ListIterator getPOAActivatorListIterator() {
        return poaActivatorElements.listIterator();
    }
    
    public Vector getAvailablePOAManagers(POAElement e) {
        Vector mgrs = new Vector();
        mgrs.add(getVarName());
        ListIterator poas = getPOAListIterator();
        while (poas.hasNext()) {
            Object next = poas.next();
            if (next == e)
                break;
            mgrs.add(((POAElement)next).getVarName());
        }
        return mgrs;
    }
    
    protected synchronized void firePOAHierarchyChange() {
        ArrayList listeners =(ArrayList)poaListeners.clone();
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            ((POAListener)it.next()).poaHierarchyChanged();
        }
    }
    
    protected synchronized void firePOAMembersChange() {
        ArrayList listeners =(ArrayList)poaListeners.clone();
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            ((POAListener)it.next()).poaMembersChanged();
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (p1.getSource() == null)
            return;
        if (p1.getSource() instanceof POAElement) {
            if (POAElement.PROP_VAR_NAME.equals(p1.getPropertyName())) {
                if (p1.getSource().equals(usedVarNames.get(p1.getOldValue())))
                    usedVarNames.remove(p1.getOldValue());
                if (!usedVarNames.containsKey(p1.getNewValue()))
                    usedVarNames.put(p1.getNewValue(), p1.getSource());
            }
            firePOAHierarchyChange();
            return;
        }
        if (p1.getSource() instanceof POAMemberElement) {
            if (POAMemberElement.PROP_VAR_NAME.equals(p1.getPropertyName())) {
                if (p1.getSource().equals(usedVarNames.get(p1.getOldValue())))
                    usedVarNames.remove(p1.getOldValue());
                if (!usedVarNames.containsKey(p1.getNewValue()))
                    usedVarNames.put(p1.getNewValue(), p1.getSource());
            }
            else if (ServantElement.PROP_ID_VAR_NAME.equals(p1.getPropertyName())) {
                if (p1.getSource().equals(usedVarNames.get(p1.getOldValue())))
                    usedVarNames.remove(p1.getOldValue());
                if (!usedVarNames.containsKey(p1.getNewValue()))
                    usedVarNames.put(p1.getNewValue(), p1.getSource());
            }
            firePOAMembersChange();
            return;
        }
    }
}
