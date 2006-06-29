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

package org.netbeans.modules.projectpackager.tools;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;

/**
 * @author Roman "Roumen" Strobl
 */
public class NotifyDescriptorInputPassword extends DialogDescriptor {

    private javax.swing.JPasswordField passwordField;

    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title) {
        this(text, title, null);
    }
    
    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title, String description) {
        this(text, title, null, description, new JPasswordField[1]);
    }
    
    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title, char mnemonic) {
        this(text, title, mnemonic, null);
    }

    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title, char mnemonic, String description) {
        this(text, title, new Character(mnemonic), description, new JPasswordField[1]);
    }
    
    /**
     * Use this constructor as a hack to set passwordField variable.
     */
    private NotifyDescriptorInputPassword (String text, String title, Character mnemonic,
                                           String description, JPasswordField[] passwordFieldPtr) {
        super (createDesign(text, description, mnemonic, passwordFieldPtr), title);
        this.passwordField = passwordFieldPtr[0];
    }

    private static Component createDesign (String text, String description, Character mnemonic,
                                           JPasswordField passwordFieldPtr[]) {
        JPanel panel = new JPanel();
        JLabel textLabel = new JLabel(text);
        textLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 6, 6));
        if (description != null) {
            JLabel descriptionLabel = new JLabel(description);
            panel.add(descriptionLabel, BorderLayout.NORTH);
            descriptionLabel.setBorder(new CompoundBorder(descriptionLabel.getBorder(), new EmptyBorder(2, 0, 11, 0)));
        }
        panel.add("West", textLabel); // NOI18N
        javax.swing.JPasswordField passwordField = new javax.swing.JPasswordField (25);
        panel.add("Center", passwordField); // NOI18N
        passwordField.setBorder(new CompoundBorder(passwordField.getBorder(), new EmptyBorder(2, 0, 2, 0)));
        passwordField.requestFocus();

        javax.swing.KeyStroke enter = javax.swing.KeyStroke.getKeyStroke(
                                          java.awt.event.KeyEvent.VK_ENTER, 0
                                      );
        javax.swing.text.Keymap map = passwordField.getKeymap ();

        map.removeKeyStrokeBinding (enter);
        textLabel.setLabelFor(passwordField);
        panel.getAccessibleContext().setAccessibleDescription(
            NbBundle.getBundle(Constants.BUNDLE).getString("NotifyDescriptorInputPassword.dialog"));
        passwordField.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(Constants.BUNDLE).getString("NotifyDescriptorInputPassword.passwordField"));
        if (mnemonic != null) {
            textLabel.setDisplayedMnemonic(mnemonic.charValue());
        }
        passwordFieldPtr[0] = passwordField;
        return panel;
    }

    /**
    * Get the text which the user typed into the input line.
    * @return the text entered by the user
    */
    public String getInputText () {
        if(passwordField==null) {
            return ""; // NOI18N
        } else return new String(passwordField.getPassword ());
    }

    /**
    * Set the text on the input line.
    * @param text the new text
    */
    public void setInputText (String text) {
        passwordField.setText (text);
    }

}
