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

package org.netbeans.modules.tasklist.filter;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
 * Basic condition class for string comparisons.
 * This class can also be used to compare objects of classes other than
 * strings. It uses getAsText() to convert properties to strings.
 *
 * @author Tor Norbye
 */
public class StringFilterCondition extends OneOfFilterCondition {
    public static final int EQUALS = 0;
    public static final int NOTEQUALS = 1;
    public static final int CONTAINS = 2;
    public static final int DOESNOTCONTAIN = 3;
    public static final int BEGINSWITH = 4;
    public static final int ENDSWITH = 5;
    public static final int CEQUALS = 6;
    public static final int CCONTAINS = 7;
    public static final int CDOESNOTCONTAIN = 8;
    public static final int CBEGINSWITH = 9;
    public static final int CENDSWITH = 10;
    
    /**
     * Creates an array of filter conditions for the specified property
     *
     * @param index index of the property
     */
    public static StringFilterCondition[] createConditions() {
        return new StringFilterCondition[] {
            new StringFilterCondition(StringFilterCondition.CONTAINS),
            new StringFilterCondition(StringFilterCondition.DOESNOTCONTAIN),
            new StringFilterCondition(StringFilterCondition.BEGINSWITH),
            new StringFilterCondition(StringFilterCondition.ENDSWITH),
            new StringFilterCondition(StringFilterCondition.EQUALS),
            new StringFilterCondition(StringFilterCondition.NOTEQUALS),
            new StringFilterCondition(StringFilterCondition.CCONTAINS),
            new StringFilterCondition(StringFilterCondition.CDOESNOTCONTAIN),
            new StringFilterCondition(StringFilterCondition.CBEGINSWITH),
            new StringFilterCondition(StringFilterCondition.CENDSWITH),
            new StringFilterCondition(StringFilterCondition.CEQUALS)
        };
    }
    
    private static String[] NAME_KEYS = {
        "Equals", // NOI18N
        "NotEquals", // NOI18N
        "Contains", // NOI18N
        "DoesNotContain", // NOI18N
        "BeginsWith", // NOI18N
        "EndsWith", // NOI18N
        "CEquals", // NOI18N
        "CContains", // NOI18N
        "CDoesNotContain", // NOI18N
        "CBeginsWith", // NOI18N
        "CEndsWith" // NOI18N
    };
    
    /** saved constant for comparison */
    private String constant = ""; // NOI18N
    
    /**
     * Creates a condition with the given name.
     *
     * @param id one of the constants from this class
     */
    public StringFilterCondition(int id) {
        super(NAME_KEYS, id);
    }

    /**
     * Creates a condition with the given name.
     *
     * @param id one of the constants from this class
     * @param value the value to compare the property with
     */
    public StringFilterCondition(int id, String value) {
        this(id);
        this.constant = value;
    }

    
    public StringFilterCondition(final StringFilterCondition rhs) {
        super(rhs);
        this.constant = rhs.constant;
    }
    
    public Object clone() {
        return new StringFilterCondition(this);
    }    

    private StringFilterCondition() { super(NAME_KEYS); constant = null; }
    
    /** Return the value that Strings are compared with.
     * @return the value that Strings are compared with */
    public String getConstant() {
        return constant;
    }

    public JComponent createConstantComponent() {
        final JTextField tf = new JTextField();
        tf.setText(constant);
        tf.setToolTipText(Util.getString("string_desc"));
        tf.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                Boolean valid = Boolean.valueOf("".equals(tf.getText()) == false);
                tf.putClientProperty(FilterCondition.PROP_VALUE_VALID, valid);
            }

            public void insertUpdate(DocumentEvent e) {
                Boolean valid = Boolean.valueOf("".equals(tf.getText()) == false);
                tf.putClientProperty(FilterCondition.PROP_VALUE_VALID, valid);
            }

            public void removeUpdate(DocumentEvent e) {
                Boolean valid = Boolean.valueOf("".equals(tf.getText()) == false);
                tf.putClientProperty(FilterCondition.PROP_VALUE_VALID, valid);
            }

        });
        return tf;
    }

    public void getConstantFrom(JComponent cmp) {
        JTextField tf = (JTextField) cmp;
        constant = tf.getText();
    }
    
    public boolean isTrue(Object obj) {
        String s = obj == null ? "" : obj.toString();
        switch (getId()) {
            case EQUALS:
                return s.equalsIgnoreCase(constant);
            case NOTEQUALS:
                return !s.equalsIgnoreCase(constant);
            case CONTAINS:
                return s.toLowerCase().indexOf(constant.toLowerCase()) >= 0;
            case DOESNOTCONTAIN:
                return s.toLowerCase().indexOf(constant.toLowerCase()) < 0;
            case BEGINSWITH:
                return s.toLowerCase().startsWith(constant.toLowerCase());
            case ENDSWITH:
                return s.toLowerCase().endsWith(constant.toLowerCase());
            case CEQUALS:
                return s.equals(constant);
            case CCONTAINS:
                return s.indexOf(constant) >= 0;
            case CDOESNOTCONTAIN:
                return s.indexOf(constant) < 0;
            case CBEGINSWITH:
                return s.startsWith(constant);
            case CENDSWITH:
                return s.endsWith(constant);
            default:
                throw new InternalError("wrong id");
        }
    }

  private static class Convertor extends OneOfFilterCondition.Convertor {
    private static final String ELEM_STRING_CONDITION = "StringCondition";
    private static final String ATTR_CONSTANT = "constant";

    public Convertor() { super(ELEM_STRING_CONDITION, NAME_KEYS);}
    public static StringFilterCondition.Convertor create() { 
      return new StringFilterCondition.Convertor();
    }

    protected Object readElement(org.w3c.dom.Element element) throws java.io.IOException, java.lang.ClassNotFoundException {
      StringFilterCondition cond = new StringFilterCondition();
      super.readCondition(element, cond);
      cond.constant = element.getAttribute(ATTR_CONSTANT);
      return cond;
    }
   
    // write methods for supported condition types
    protected void writeElement(org.w3c.dom.Document document, org.w3c.dom.Element element, Object obj) 
      throws java.io.IOException, org.w3c.dom.DOMException 
    {
      StringFilterCondition cond = (StringFilterCondition)obj;
      super.writeCondition(document, element, cond);
      element.setAttribute(ATTR_CONSTANT, cond.constant);

    }   
  }

}
