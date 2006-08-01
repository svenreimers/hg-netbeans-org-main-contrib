/*The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License. You can obtain a copy of the License at 
http://www.netbeans.org/cddl.html or http://www.netbeans.org/cddl.txt.
When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"  */
package syntaxtreenavigator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;

/**
 *
 * @author  Tim Boudreau
 */
public class ViewPanel extends javax.swing.JPanel {
    
    /** Creates new form ViewPanel */
    public ViewPanel() {
    }
    
    public void addNotify() {
        super.addNotify();
        initComponents();
        jSplitPane1.setDividerLocation(0.5D);
    }
    
    public void setObject (Object o) {
        if (o instanceof Collection) {
            handleList ((Collection) o);
        } else {
            if (o == null) {
                methods.setModel (new DefaultListModel());
                classNameLbl.setText ("[null]");
                body.setText(" ");
                objNameLbl.setText (" ");
                return;
            }
            classNameLbl.setText (o.getClass().getName());
            Method[] m = o.getClass().getMethods();
            DefaultListModel mdl = new DefaultListModel();
            Object name = null;
            for (int i = 0; i < m.length; i++) {
                mdl.addElement(sig(m[i]));
                if (m[i].getName().equals("getName") && m[i].getTypeParameters().length == 0) {
                    try {
                        name = m[i].invoke(o);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            methods.setModel (mdl);
            body.setText (o.toString());
            if (name != null) {
                objNameLbl.setText (name.toString());
            } else {
                objNameLbl.setText(" ");
            }
        }
    }
    
    static String strippedName(String s) {
        int ix1 = s.lastIndexOf('.');
        int ix2 = s.lastIndexOf('$');
        int ix = Math.max(ix1, ix2);
        if (ix != s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return s;
        }
    }
    
    static String sig (Method m) {
        StringBuffer sb = new StringBuffer(strippedName (m.getReturnType().getName()));
        sb.append(' ');
        sb.append (m.getName());
        sb.append (" (");
        Class[] tps = m.getParameterTypes();
        for (int i = 0; i < tps.length; i++) {
            sb.append (strippedName(tps[i].getName()));
            if (i != tps.length - 1) {
                sb.append (',');
            }
        }
        sb.append (')');
        return sb.toString();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        bodypane = new javax.swing.JScrollPane();
        body = new javax.swing.JEditorPane();
        objNameLbl = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        methodspane = new javax.swing.JScrollPane();
        methods = new javax.swing.JList();
        classNameLbl = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jSplitPane1.setDividerLocation(50);
        jSplitPane1.setResizeWeight(0.5);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        bodypane.setFont(new java.awt.Font("Monospaced", 0, 11));
        bodypane.setViewportView(body);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 200;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.weighty = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(bodypane, gridBagConstraints);

        objNameLbl.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(objNameLbl, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        methods.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        methodspane.setViewportView(methods);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(methodspane, gridBagConstraints);

        classNameLbl.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel1.add(classNameLbl, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void handleList(Collection c) {
        String tp = c instanceof List ? "List of " : "Collection of ";
        StringBuffer txt = new StringBuffer(tp);
        txt.append (c.size());
        txt.append (" items\n");
        Set classes = new HashSet();
        for (Iterator i=c.iterator();i.hasNext();) {
            Object o = i.next();
            classes.add (o.getClass());
            txt.append (o.getClass().getName());
            txt.append ("\n");
            txt.append (o.toString());
            if (i.hasNext()) {
                txt.append ("\n--------------------------------------\n");
            }
        }
        body.setText (txt.toString());
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane body;
    private javax.swing.JScrollPane bodypane;
    private javax.swing.JLabel classNameLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JList methods;
    private javax.swing.JScrollPane methodspane;
    private javax.swing.JLabel objNameLbl;
    // End of variables declaration//GEN-END:variables
    
}
