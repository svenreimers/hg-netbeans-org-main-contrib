/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.regextester;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.swing.JEditorPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.netbeans.modules.regextester.editor.RegexEditorKit;

/**
 * Top component which displays something.
 */
final class RegexTopComponent extends TopComponent {
    
    private static final long serialVersionUID = 1L;
    private Color defaultForegroundColor;
    private static RegexTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "RegexTopComponent";
    
    private RegexTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(RegexTopComponent.class, "CTL_RegexTopComponent"));
        setToolTipText(NbBundle.getMessage(RegexTopComponent.class, "HINT_RegexTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        new EventHandler(input);
        new EventHandler(pattern);
        defaultForegroundColor = matchTextField.getForeground();
        ((AbstractDocument) pattern.getDocument()).setDocumentFilter(new DocumentFilter() {
            
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                } else {
                    replace(fb, offset, 0, string, attr);
                }
            }
            
            public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
                replace(fb, offset, length, "", null);
            }
            
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.equals("\n")) {
                    System.out.println("### No way!");
                } else {
                    fb.replace(offset, length, text, attrs);
                }
            }
            
        });
    }
    
    protected EditorKit createDefaultEditorKit() {
        return new RegexEditorKit();
    }
    static EditorKit createEditorKitForContentType(String type) {
        return new RegexEditorKit();
    }
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized RegexTopComponent getDefault() {
        if (instance == null) {
            instance = new RegexTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the RegexTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized RegexTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find Regex component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof RegexTopComponent) {
            return (RegexTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return RegexTopComponent.getDefault();
        }
    }
    private void test() {
        
        Matcher matcher = getMatcher(pattern.getText(), input.getText());
        
        boolean found = false;
        String result = "";
        while(matcher.find()) {
            result += "Text \"" + matcher.group() + "\" found at <" + matcher.start() +
                    ", " + matcher.end() + ">\n";
            found = true;
        }
        if (!found) {
            result = ("No match found");
        }
        resultTextArea.setText(result);
        if (matcher.matches()) {
            matchTextField.setText("Input matches pattern.\n");
            matchTextField.setForeground(defaultForegroundColor);
        } else {
            matchTextField.setText("Input doesn't match pattern.\n");
            matchTextField.setForeground(Color.RED);
        }
    }
    
    private static Matcher getMatcher(String patternStr, String input) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        return matcher;
    }
    
    private class EventHandler extends KeyAdapter {

        private JEditorPane editor;
        
        public EventHandler(JEditorPane editorPane) {
            this.editor = editorPane;
            editorPane.addKeyListener(this);
        }
        
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                evt.consume();
                test();
            } else if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                evt.consume();
                if (editor == input) {
                    pattern.requestFocusInWindow();
                } else {
                    input.requestFocusInWindow();
                }
            }
        }
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        input = new javax.swing.JEditorPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        pattern = new javax.swing.JEditorPane();
        matchTextField = new javax.swing.JTextField();

        jLabel1.setText("Input:");

        jLabel2.setText("Pattern:");

        jLabel3.setText("Match:");

        input.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        input.setMaximumSize(new java.awt.Dimension(9, 17));

        resultTextArea.setColumns(20);
        resultTextArea.setRows(5);
        resultTextArea.setMaximumSize(new java.awt.Dimension(20, 10));
        resultTextArea.setPreferredSize(new java.awt.Dimension(160, 20));
        jScrollPane3.setViewportView(resultTextArea);

        pattern.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pattern.setDocument(pattern.getDocument());
        pattern.setContentType("text/x-regex");
        pattern.setMaximumSize(new java.awt.Dimension(9, 17));

        matchTextField.setEditable(false);
        matchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchTextFieldActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel1)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(matchTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .add(pattern, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .add(input, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(input, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2))
                    .add(layout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(pattern, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(matchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void matchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchTextFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_matchTextFieldActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField matchTextField;
    private javax.swing.JEditorPane pattern;
    private javax.swing.JTextArea resultTextArea;
    // End of variables declaration//GEN-END:variables
    
}
