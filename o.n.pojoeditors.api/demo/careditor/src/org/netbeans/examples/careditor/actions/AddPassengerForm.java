/*
 * AddPassengerForm.java
 *
 * Created on December 23, 2007, 3:45 PM
 */

package org.netbeans.examples.careditor.actions;

import javax.swing.DefaultComboBoxModel;
import org.netbeans.examples.careditor.pojos.Person;
import org.openide.util.NbBundle;

/**
 *
 * @author  Tim Boudreau
 */
final class AddPassengerForm extends javax.swing.JPanel {
    
    /** Creates new form AddPassengerForm */
    public AddPassengerForm() {
        initComponents();
        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        for (int i=0; i < 120; i++) {
            mdl.addElement(Integer.toString(i));
        }
        jComboBox1.setModel(mdl);
    }
    
    public AddPassengerForm(Person person) {
        this();
        jTextField1.setText(person.getFirstName());
        jTextField2.setText(person.getLastName());
        jComboBox1.setSelectedItem(Integer.toString(person.getAge()));
    }
    
    String getFirstName() {
        String result = jTextField1.getText();
        result = result.trim().length() == 0 ? null : result;
        return result;
    }
    
    String getLastName() {
        String result = jTextField2.getText();
        result = result.trim().length() == 0 ? null : result;
        return result;
    }
    
    int getAge() {
        String result = (String) jComboBox1.getSelectedItem();
        return Integer.parseInt(result);
    }
    
    Person getPerson() throws IllegalArgumentException {
        String fn = getFirstName();
        String ln = getLastName();
        int age = getAge();
        if (fn == null) {
            throw new IllegalArgumentException (NbBundle.getMessage(AddPassengerForm.class,
                    "MSG_NO_FIRST_NAME"));
        }
        if (ln == null) {
            throw new IllegalArgumentException (NbBundle.getMessage(AddPassengerForm.class,
                    "MSG_NO_LAST_NAME"));
        }
        Person result = new Person();
        result.setFirstName (fn);
        result.setLastName (ln);
        result.setAge (age);
        return result;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AddPassengerForm.class, "AddPassengerForm.jLabel1.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(AddPassengerForm.class, "AddPassengerForm.jTextField1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(AddPassengerForm.class, "AddPassengerForm.jLabel2.text")); // NOI18N

        jTextField2.setText(org.openide.util.NbBundle.getMessage(AddPassengerForm.class, "AddPassengerForm.jTextField2.text")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText(org.openide.util.NbBundle.getMessage(AddPassengerForm.class, "AddPassengerForm.jLabel3.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel1)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
    
}
