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

package org.netbeans.modules.whichelement;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.awt.StatusLineElementProvider;

/**
 * This creates a read-only text field to display the information of element under the caret.
 * The text field is added to the status bar.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class WhichElementStatusElementProvider implements StatusLineElementProvider {
    
    private WhichElementPanel whichElementPanel;
    public WhichElementStatusElementProvider() {
        whichElementPanel = new WhichElementPanel();
    }
    
    public Component getStatusLineElement() {
        return whichElementPanel;
    }
    
    static class WhichElementPanel extends JPanel {
        private JLabel iconLabel;
        
        private JTextField whichElementTextField;
        
        WhichElementPanel() {
            super(new FlowLayout(FlowLayout.LEADING, 0,0));
            
            iconLabel = new JLabel();
            add(iconLabel, BorderLayout.WEST);
            
            // Create the text field
            whichElementTextField = new JTextField(40) {
                Point tooltipLocation;
                
                // Consider the font's size to compute the location of the
                // tooltip
                public void addNotify() {
                    super.addNotify();
                    tooltipLocation = new Point(0, -2 * getFont().getSize());
                }
                
                public Point getToolTipLocation(MouseEvent event) {
                    return tooltipLocation;
                }
            };
            
            // Set the text field to read-only
            whichElementTextField.setEditable(false);
            
            add(whichElementTextField, BorderLayout.CENTER);
        }
        
        void setIcon(Icon icon) {
            iconLabel.setIcon(icon);
        }
        
        void setText(String text) {
            whichElementTextField.setText(text);
        }
        
        public void setToolTipText(String text) {
            whichElementTextField.setToolTipText(text);
        }
    }
}
