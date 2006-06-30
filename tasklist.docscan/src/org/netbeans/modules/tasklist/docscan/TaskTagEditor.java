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

package org.netbeans.modules.tasklist.docscan;

import java.util.ArrayList;
import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;

import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.Task;

/**
 * PropertyEditor for task tags
 *
 * @author Tor Norbye
 */
public final class TaskTagEditor extends PropertyEditorSupport
implements ExPropertyEditor {
    
    public TaskTagEditor() {
    }


    /** sets new value */
    public void setAsText(String s) {
//        TaskTags tags = new TaskTags();
//        int i = 0;
//        int n = s.length();
//        ArrayList list = new ArrayList();
//        while (i < n) {
//            if (s.charAt(i++) != '[') {
//                return;
//            }
//            // Get token
//            StringBuffer token = new StringBuffer();
//            while (i < n) {
//                char c = s.charAt(i++);
//                boolean escaped = (c == '\\');
//                if (escaped) {
//                    if (i == n) {
//                        break;
//                    }
//                    c = s.charAt(i++);
//                }
//                if (!escaped && ((c == ',') || (c == ']'))) {
//                    break;
//                }
//                token.append(c);
//            }
//            StringBuffer priostr = new StringBuffer();
//            while (i < n) {
//                char c = s.charAt(i++);
//                boolean escaped = (c == '\\');
//                if (escaped) {
//                    if (i == n) {
//                        break;
//                    }
//                    c = s.charAt(i++);
//                }
//                if (!escaped && ((c == ',') || (c == ']'))) {
//                    break;
//                }
//                priostr.append(c);
//            }
//            String prioString = priostr.toString();
//            String[] prios = Task.getPriorityNames();
//            SuggestionPriority priority = SuggestionPriority.MEDIUM;
//            for (int j = 0; j < prios.length; j++) {
//                if (prios[j].equals(prioString)) {
//                    priority = Task.getPriority(j+1);
//                    break;
//                }
//            }
//            TaskTag tag = new TaskTag(token.toString(), priority);
//            list.add(tag);
//            if (i == n) {
//                break;
//            }
//            if (s.charAt(i) == ']') {
//                break;
//            }
//            if (s.charAt(i) == ',') {
//                i++;
//            } else {
//                break;
//            }
//        }
//        TaskTag[] tagArray = (TaskTag[])list.toArray(new TaskTag[list.size()]);
//        tags.setTags(tagArray);
//        setValue(tags);
    }

    public String getAsText() {
        Object val = getValue();
        if (val == null) {
            return "";
        } else {
            TaskTags tags = (TaskTags)val;
            return tags.getScanRegexp().pattern();
//            StringBuffer sb = new StringBuffer(500);
//            TaskTag[] tgs = tags.getTags();
//            String[] prios = Task.getPriorityNames();
//            for (int i = 0; i < tgs.length; i++) {
//                if (i > 0) {
//                    sb.append(',');
//                }
//                sb.append('[');
//
//                String s = tgs[i].getToken();
//                int n = s.length();
//                for (int j = 0; j < n; j++) {
//                    char c = s.charAt(j);
//                    // escape some metachars
//                    if ((c == ',') || (c == '\\') ||
//                        (c == '[') || (c == ']')) {
//                        sb.append('\\');
//                    }
//                    sb.append(c);
//                }
//                sb.append(',');
//                sb.append(prios[tgs[i].getPriority().intValue()-1]);
//                sb.append(']');
//            }
//            return sb.toString();
        }
    }


    public boolean supportsCustomEditor () {
        return true;
    }

    public Component getCustomEditor() {
        TaskTags d = (TaskTags)getValue();
        if (d == null) {
            d = new TaskTags();
            setValue(d);
        }
        return new TaskTagsPanel(d);
    }

    public void attachEnv(PropertyEnv env) {        
        FeatureDescriptor desc = env.getFeatureDescriptor();
        if (desc instanceof Node.Property){
            Node.Property prop = (Node.Property)desc;
            editable = prop.canWrite();
        }
    }

    // bugfix# 9219 added editable field and isEditable() "getter" to be used in StringCustomEditor    
    // TODO Tor - is the above relevant for our property editor?
    private boolean editable=true;   

    /** gets information if the text in editor should be editable or not */
    public boolean isEditable(){
        return editable;
    }
}
