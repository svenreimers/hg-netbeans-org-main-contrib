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

package org.netbeans.modules.j2ee.oc4j.customizer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;

/**
 * Customizer data support keeps models for all the customizer components,
 * initializes them, tracks model changes and performs save.
 *
 * @author Michal Mocnak
 */
public class OC4JCustomizerDataSupport {
    
    private OC4JCustomizerSupport.PathModel classModel;
    private OC4JCustomizerSupport.PathModel javadocModel;
    
    private boolean javadocModelFlag;
    
    private OC4JPluginProperties properties;
    private OC4JDeploymentManager dm;
    
    /**
     * Creates a new instance of CustomizerDataSupport
     */
    public OC4JCustomizerDataSupport(OC4JDeploymentManager dm) {
        this.dm = dm;
        this.properties = dm.getProperties();
        init();
    }
    
    /**
     * Initialize the customizer models
     */
    private void init() {
        // classModel
        classModel = new OC4JCustomizerSupport.PathModel(properties.getClasses());
        
        // javadocModel
        javadocModel = new OC4JCustomizerSupport.PathModel(properties.getJavadocs());
        javadocModel.addListDataListener(new ModelChangeAdapter() {
            public void modelChanged() {
                javadocModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
    }
    
    public InstanceProperties getInstanceProperties() {
        return dm.getInstanceProperties();
    }
    
    public OC4JCustomizerSupport.PathModel getClassModel() {
        return classModel;
    }
    
    public OC4JCustomizerSupport.PathModel getJavadocsModel() {
        return javadocModel;
    }
    
    /**
     * Save all changes
     */
    private void store() {
        if (javadocModelFlag) {
            properties.setJavadocs(javadocModel.getData());
            javadocModelFlag = false;
        }
    }
    
    /**
     * Adapter that implements several listeners, which is useful for dirty model
     * monitoring.
     */
    private abstract class ModelChangeAdapter implements ListDataListener,
            DocumentListener, ItemListener, ChangeListener {
        
        public abstract void modelChanged();
        
        public void contentsChanged(ListDataEvent e) {
            modelChanged();
        }
        
        public void intervalAdded(ListDataEvent e) {
            modelChanged();
        }
        
        public void intervalRemoved(ListDataEvent e) {
            modelChanged();
        }
        
        public void changedUpdate(DocumentEvent e) {
            modelChanged();
        }
        
        public void removeUpdate(DocumentEvent e) {
            modelChanged();
        }
        
        public void insertUpdate(DocumentEvent e) {
            modelChanged();
        }
        
        public void itemStateChanged(ItemEvent e) {
            modelChanged();
        }
        
        public void stateChanged(javax.swing.event.ChangeEvent e) {
            modelChanged();
        }
    }
}