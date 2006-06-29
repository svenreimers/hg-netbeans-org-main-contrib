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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.eview;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.event.DocumentListener;
import org.netbeans.api.eview.ControlFactory;
import org.netbeans.api.registry.Context;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * ControlFactory creating JComboBoxes.
 * Supports additional attributes "initValue" and "verifier".
 * @author David Strupl
 */
public class ComboBoxControlFactory implements ControlFactory {
    
//    static {
//        // enable logging for now
//        System.setProperty(ComboBoxControlFactory.class.getName(), "-1");
//    }
    private static ErrorManager log = ErrorManager.getDefault().getInstance(ComboBoxControlFactory.class.getName());
    private static boolean LOGGABLE = log.isLoggable(ErrorManager.INFORMATIONAL);

    /** Shared event instance. */
    private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(ComboBoxControlFactory.class, "selItem", null, null);
    
    /** String value that is put into the combo right after initialization */
    private String initValue;
    
    /** Display names that are displayed as content of the combo */
    private List/*<String>*/ myNames;
    /** Mapping from names to the values that are returned when the users
     * selects something in the combo.
     */
    private Map/*<String, Object>*/ myValues;
    
    /**
     * Creates a new instance of ComboBoxControlFactory 
     */
    public ComboBoxControlFactory(FileObject f) {
        initConfig(f);
    }

    /**
     * Called by the constructor to read the configuration.
     */
    protected void initConfig(FileObject f) {
        Object o1 = f.getAttribute("configFolder");
        if (o1 instanceof String) {
            String folderName = o1.toString();
            FileObject configFolder = f.getParent().getFileObject(folderName);
            if (configFolder != null) {
                myNames = new ArrayList();
                myValues = new HashMap();
                scanConfigFolder(configFolder, myNames, myValues);
            }
        }
    }
    
    protected final void scanConfigFolder(FileObject folder, List names, Map values) {
        // in order to get the order we need Registry API:
        Context con = Context.getDefault().getSubcontext(folder.getPath());
        List orderedNames = con.getOrderedNames();
        for (Iterator it = orderedNames.iterator(); it.hasNext();) {
            String name = (String) it.next();
            if (LOGGABLE) log.log("scanFolder checking " + name);
            if (name.endsWith("/")) {
                name = name.substring(0, name.length()-1);
            }
            FileObject child = folder.getFileObject(name);
            String []extensions = { "instance", "ser", "setting", "xml", "shadow" };
            int extNum = 0;
            while ((child == null) && (extNum < extensions.length)) {
                child = folder.getFileObject(name, extensions[extNum++]);
            }
            if (child == null) {
                log.log("child == null: Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (! child.isValid()) {
                log.log("!child.isValid(): Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (child.isData()) {
                String displayName = child.getName();
                try {
                    displayName = child.getFileSystem ().getStatus ().annotateName(child.getName(), Collections.singleton(child));
                } catch (Exception x) {
                    log.notify(ErrorManager.EXCEPTION, x);
                }
                Object value = displayName;
                Object a1 = child.getAttribute("value");
                if (a1 != null) {
                    value = a1;
                }
                names.add(displayName);
                values.put(displayName, value);
            }
        }
    }
    
    protected void setNamesValues(List newNames, Map newValues) {
        myNames = newNames;
        myValues = newValues;
    }
    
    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
            jcb.addActionListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JComboBox result = new JComboBox();
        if (myNames != null) {
            result.setModel(new DefaultComboBoxModel(myNames.toArray()));
        }
        return result;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            Object selItem = jcb.getSelectedItem();
            if ((selItem != null) && (myValues != null)) {
                return myValues.get(selItem);
            }
        }
        return null;
    }
    
    public String convertValueToString(JComponent c, Object value) {
        if (myNames == null) {
            return null;
        }
        for (Iterator it = myNames.iterator(); it.hasNext();) {
            String key = (String)it.next();
            Object val = myValues.get(key);
            if ((val != null) && (val.equals(value))) {
                return key;
            }
        }
        return null;
    }

    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            if (myNames != null) {
                for (Iterator it = myNames.iterator(); it.hasNext();) {
                    Object name = it.next();
                    if (myValues != null) {
                        Object v = myValues.get(name);
                        if ( (v != null) && (v.equals(value))) {
                            jcb.setSelectedItem(name);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Listener attached to the control to propagate changes to our
     * listeners.
     */
    private class ControlListener implements ActionListener {
        private PropertyChangeListener pcl;
        public ControlListener(PropertyChangeListener pcl){
            this.pcl = pcl;
        }
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            pcl.propertyChange(pcEvent);
        }

        public boolean equals(Object anotherObject) {
            if ( ! ( anotherObject instanceof ControlListener ) ) {
                return false;
            }
            ControlListener theOtherOne = (ControlListener)anotherObject;
            return pcl.equals(theOtherOne.pcl);
        }
        public int hashCode() {
            return pcl.hashCode();
        }
    }
}
