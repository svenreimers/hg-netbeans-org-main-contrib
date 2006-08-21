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

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.naming.Context;
import org.netbeans.modules.jndi.settings.JndiSystemOption;

import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;


/** Panel for dialog for adding new Context
 *
 *  @author Ales Novak, Tomas Zezula
 */
final class NewJndiRootPanel extends AbstractNewPanel implements ItemListener{
    /* this two static constants are for testing only
    in final version only valid constructor will be NewJndiRootPanel(String[],String[])*/

    /** ComboBox holding the factory*/
    JComboBox  factory;
    /** TextField holding the label*/
    JTextField label;

    /** Reference to Hashtable holding the Properties of providers */
    HashMap providers;


    /** constructor takes as parameter array of factories and protocols
     * @param fcs array of factories
     * @param proto array of protocols
     */
    public NewJndiRootPanel() {
        super();
        String className = null;
        JndiSystemOption settings = (JndiSystemOption) JndiSystemOption.findObject (JndiSystemOption.class, true);
        this.providers = settings.getProviders (false);
        Iterator it = this.providers.keySet().iterator();
        while (it.hasNext() ) {
            className = (String) it.next();
            this.factory.addItem (className);
        }
        this.label.requestFocus();
    }

    /** Accessor for Factory
     *  @return String name of Factory
     */
    public String getFactory() {
        return (String) factory.getSelectedItem();
    }

    /** Accessor for Label
     *  @return String name of JndiRootNode
     */
    public String getLabel()  {
        return this.label.getText();
    }


    /** Synchronization of Factory and Protocol
     *  @param event ItemEvent
     */
    public void itemStateChanged(ItemEvent event) {

        if (event.getSource() == this.factory) {
            // this.properties.clear();
            Object item = factory.getSelectedItem();
            if (item != null){
                ProviderProperties p =(ProviderProperties)this.providers.get(item);
                if (p!=null){
                    this.context.setText(p.getContext());
                    this.authentification.setText(p.getAuthentification());
                    this.principal.setText(p.getPrincipal());
                    this.credentials.setText(p.getCredentials());
                    this.properties.setData(p.getAdditionalSave());
                    this.root.setText(p.getRoot());
                }
            }
        }
    }

    /** Creates a part of GUI, called grom createGUI */
    JPanel createSubGUI(){
        this.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString("AD_NewJndiRootPanel"));
        this.label = new JTextField();
        this.label.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString("AD_Label"));
        this.factory = new JComboBox();
        this.factory.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString ("AD_Factory"));
        this.factory.setEditable(true);
        this.factory.setSize(this.label.getSize());
        this.factory.addItemListener(this);
        this.context = new JTextField();
        this.authentification = new JTextField();
        this.principal = new JTextField();
        this.credentials= new JTextField();
        this.root = new JTextField();
        JPanel p = new JPanel();
        p.setLayout ( new GridBagLayout());
        GridBagConstraints gridBagConstraints;

        JLabel label = new JLabel (JndiRootNode.getLocalizedString("TXT_ContextLabel"));
        label.setLabelFor (this.label);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_ContextLabel_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Factory"));
        label.setLabelFor (this.factory);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Factory_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_InitialContext"));
        label.setLabelFor (this.context);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_InitialContext_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Root"));
        label.setLabelFor (this.root);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Root_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Auth"));
        label.setLabelFor (this.authentification);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Auth_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Principal"));
        label.setLabelFor (this.principal);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Principal_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Credentials"));
        label.setLabelFor (this.credentials);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Credentials_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.label, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.factory, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.context, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.root, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.authentification, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.principal, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.credentials, gridBagConstraints);
        return p;
    }

    /** Cretes notes panel
     *  @return JPanel or null
     */
    javax.swing.JPanel createNotesPanel(){
        return null;
    }

    /** selects provider
     *  This mrthod does nearly the same as ItemStateChanged,
     *  but sets also the factory
     */
    public void select (String provider){
        if (provider != null){
            ProviderProperties p =(ProviderProperties)this.providers.get(provider);
            if (p!=null){
                this.factory.setSelectedItem(provider);
                this.context.setText(p.getContext());
                this.authentification.setText(p.getAuthentification());
                this.principal.setText(p.getPrincipal());
                this.credentials.setText(p.getCredentials());
                this.properties.setData(p.getAdditionalSave());
                this.root.setText(p.getRoot());
            }
        }
    }



}
