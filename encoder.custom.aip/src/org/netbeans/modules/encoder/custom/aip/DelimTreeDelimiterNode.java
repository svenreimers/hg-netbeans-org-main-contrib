/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.encoder.custom.aip;

import com.sun.encoder.custom.appinfo.Delimiter;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 * The node that represents the delimiter in the delimiter tree.
 *
 * @author Jun Xu
 */
public class DelimTreeDelimiterNode extends AbstractNode
        implements PropertyChangeListener, DelimiterSetChangeNotificationSupport {
    
    private static final ResourceBundle _bundle =
            ResourceBundle.getBundle("org/netbeans/modules/encoder/custom/aip/Bundle");
    private final DelimiterSetChangeNotifier mChangeNotifier = 
            new DelimiterSetChangeNotifier();
    private final DelimiterOption mDelimOption;
    
    /** Creates a new instance of DelimTreeDelimiterNode */
    public DelimTreeDelimiterNode(Delimiter delim, Lookup lookup) {
        super(new Children.Array(), lookup);
        mDelimOption = DelimiterOption.create(delim);
        mDelimOption.addPropertyChangeListener(this);
    }
    
    public boolean canRename() {
        return false;
    }

    public boolean canCut() {
        return false;
    }

    public boolean canCopy() {
        return false;
    }
    
    public String getName() {
        return "Delimiter"; //NOI18N
    }

    public String getDisplayName() {
        return _bundle.getString("delim_tree_delim_node.lbl.delimiter");
    }
    
    public Image getIcon(int i) {
        return Utilities.loadImage("org/netbeans/modules/encoder/custom/aip/delimIcon.PNG");  //NOI18N
    }

    public Image getOpenedIcon(int i) {
        return Utilities.loadImage("org/netbeans/modules/encoder/custom/aip/delimOpenIcon.PNG");  //NOI18N
    }

    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set propSet = Sheet.createPropertiesSet();
        try {
            //The Node Type Property
            PropertySupport.Reflection kindProp =
                    new PropertySupport.Reflection(mDelimOption,
                            String.class, "kind");  //NOI18N
            kindProp.setName("kind");  //NOI18N
            kindProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.type"));
            kindProp.setPropertyEditorClass(DelimKindPropertyEditor.class);
            propSet.put(kindProp);
            
            PropertySupport.Reflection bytesProp =
                    new PropertySupport.Reflection(mDelimOption,
                            String.class, "bytes");  //NOI18N
            bytesProp.setName("bytes");  //NOI18N
            bytesProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.delim_bytes"));
            bytesProp.setPropertyEditorClass(DelimiterPropertyEditor.class);
            propSet.put(bytesProp);
            
            PropertySupport.Reflection precedenceProp =
                    new PropertySupport.Reflection(mDelimOption,
                            short.class, "precedence");  //NOI18N
            precedenceProp.setName("precedence");  //NOI18N
            precedenceProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.precedence"));
            precedenceProp.setPropertyEditorClass(DelimPrecedencePropertyEditor.class);
            propSet.put(precedenceProp);
            
            PropertySupport.Reflection optionModeProp =
                    new PropertySupport.Reflection(mDelimOption,
                            String.class, "optionMode");  //NOI18N
            optionModeProp.setName("optionMode");  //NOI18N
            optionModeProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.opt_mode"));
            optionModeProp.setPropertyEditorClass(DelimOptionModePropertyEditor.class);
            propSet.put(optionModeProp);
            
            PropertySupport.Reflection termModeProp =
                    new PropertySupport.Reflection(mDelimOption,
                            String.class, "termMode");  //NOI18N
            termModeProp.setName("termMode");  //NOI18N
            termModeProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.term_mode"));
            termModeProp.setPropertyEditorClass(DelimTermModePropertyEditor.class);
            propSet.put(termModeProp);

            PropertySupport.Reflection offsetProp =
                    new PropertySupport.Reflection(mDelimOption,
                            int.class, "offset");  //NOI18N
            offsetProp.setName("offset");  //NOI18N
            offsetProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.offset"));
            offsetProp.setPropertyEditorClass(DelimOffsetPropertyEditor.class);
            propSet.put(offsetProp);
            
            PropertySupport.Reflection lengthProp =
                    new PropertySupport.Reflection(mDelimOption,
                            short.class, "length");  //NOI18N
            lengthProp.setName("length");  //NOI18N
            lengthProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.length"));
            lengthProp.setPropertyEditorClass(DelimLengthPropertyEditor.class);
            propSet.put(lengthProp);
            
            PropertySupport.Reflection skipLeadingProp =
                    new PropertySupport.Reflection(mDelimOption,
                            boolean.class, "skipLeading");  //NOI18N
            skipLeadingProp.setName("skipLeading");  //NOI18N
            skipLeadingProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.skip_leading"));
            propSet.put(skipLeadingProp);
            
            PropertySupport.Reflection collapseProp =
                    new PropertySupport.Reflection(mDelimOption,
                            boolean.class, "collapse");  //NOI18N
            collapseProp.setName("collapse");  //NOI18N
            collapseProp.setDisplayName(_bundle.getString("delim_tree_delim_node.lbl.collapse"));
            propSet.put(collapseProp);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(_bundle.getString("delim_tree_delim_node.exp.no_such_mthd"), e);
        }
        sheet.put(propSet);
        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        mChangeNotifier.fireDelimiterSetChangeEvent(
                evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
    
    public void addDelimiterSetChangeListener(DelimiterSetChangeListener listener) {
        mChangeNotifier.addDelimiterSetChangeListener(listener);
    }

    public DelimiterSetChangeListener[] getDelimiterSetChangeListeners() {
        return mChangeNotifier.getDelimiterSetChangeListeners();
    }

    public void removeDelimiterSetChangeListener(DelimiterSetChangeListener listener) {
        mChangeNotifier.removeDelimiterSetChangeListener(listener);
    }

    public void addDelimiterSetChangeListener(DelimiterSetChangeListener[] listeners) {
        mChangeNotifier.addDelimiterSetChangeListener(listeners);
    }

    public void removeDelimiterSetChangeListener(DelimiterSetChangeListener[] listeners) {
        mChangeNotifier.removeDelimiterSetChangeListener(listeners);
    }
}
