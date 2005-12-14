/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.timerwin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.tasklist.core.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.StartedUserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Panel with running task. 
 *
 * @author tl
 */
public class TimeAccPanel extends javax.swing.JPanel {
    private PropertyChangeListener pcl;
    private ObjectList.Listener oll;
    private ChangeListener registryListener;
    
    /** 
     * Creates new form TimeAccPanel 
     */
    public TimeAccPanel() {        
        initComponents();

        registryListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                viewsChanged();
            }
        };
        UserTaskViewRegistry.getInstance().addChangeListener(
                registryListener);
        
        pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String n = evt.getPropertyName();
                if (n == UserTask.PROP_SPENT_TIME || 
                        n == UserTask.PROP_PROGRESS)
                    jComboBoxTasks.repaint();
            }
        };
        
        oll = new ObjectList.Listener() {
            public void listChanged(ObjectList.Event e) {
                if (e.getType() == ObjectList.Event.EVENT_REMOVED) {
                    Object[] list = e.getObjects();
                    for (int i = 0; i < list.length; i++) {
                        int index = findUserTask((UserTask) list[i]);
                        if (index >= 0)
                            removeUserTask(index);
                    }
                }
            }
        };
        
        StartedUserTask.getInstance().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                startedTaskChanged();
            }
        });
        
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        m.addElement(null);
        jComboBoxTasks.setModel(m);
        
        UserTask ut = StartedUserTask.getInstance().getStarted();
        if (ut != null) {
            addUserTask(ut);
            jComboBoxTasks.setSelectedIndex(1);
        }
        
        jComboBoxTasks.setRenderer(new UserTaskListCellRenderer());
    }

    /**
     * A task was stopped or started.
     */
    private void startedTaskChanged() {
        UserTask started = StartedUserTask.getInstance().getStarted();
        if (started == null) {
            jComboBoxTasks.setSelectedIndex(0);
        } else {
            DefaultComboBoxModel m = 
                    (DefaultComboBoxModel) jComboBoxTasks.getModel();
            int index = findUserTask(started);
            if (index < 0) {
                addUserTask(started);
                index = 1;
            }
            jComboBoxTasks.setSelectedIndex(index);
        }
    }
    
    /**
     * A view was closed or opened.
     */
    private void viewsChanged() {
        DefaultComboBoxModel m = 
                (DefaultComboBoxModel) jComboBoxTasks.getModel();
        for (int i = 0; i < m.getSize(); ) {
            UserTask ut = (UserTask) m.getElementAt(i);
            if (ut == null) {
                i++;
                continue;
            }
            
            if (ut.getList() == null) {
                removeUserTask(i);
                continue;
            } 
            
            UserTaskView utv = UserTaskViewRegistry.getInstance().
                    findView(ut.getList().getFile());
            if (utv == null) {
                removeUserTask(i);
                continue;
            }
            
            i++;
        }
    }

    /**
     * Removes a task from the combobox.
     *
     * @param index index of the task
     */
    private void removeUserTask(int index) {
        DefaultComboBoxModel m = 
                (DefaultComboBoxModel) jComboBoxTasks.getModel();
        UserTask ut = (UserTask) m.getElementAt(index);
        ut.removePropertyChangeListener(pcl);
        if (ut.getParent() != null)
            ut.getParent().getSubtasks().removeListener(oll);
        if (ut.getList() != null)
            ut.getList().getSubtasks().removeListener(oll);
        m.removeElementAt(index);
    }
    
    /**
     * Adds a task to the combobox.
     *
     * @param ut a task
     */
    private void addUserTask(UserTask ut) {
        DefaultComboBoxModel m = 
                (DefaultComboBoxModel) jComboBoxTasks.getModel();
        m.insertElementAt(ut, 1);
        ut.addPropertyChangeListener(pcl);
        if (ut.getParent() != null)
            ut.getParent().getSubtasks().addListener(oll);
        else
            ut.getList().getSubtasks().addListener(oll);
    }
    
    /**
     * Searches a UT in the combo box.
     *
     * @param ut a task
     * @return it's index or -1
     */
    private int findUserTask(UserTask ut) {
        DefaultComboBoxModel m = 
                (DefaultComboBoxModel) jComboBoxTasks.getModel();
        int index = -1;
        for (int i = 0; i < m.getSize(); i++) {
            if (m.getElementAt(i) == ut) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jComboBoxTasks = new javax.swing.JComboBox();
        jButtonChoose = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setOpaque(false);
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 22));
        add(jPanel1);

        jComboBoxTasks.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxTasks.setPreferredSize(new java.awt.Dimension(200, 22));
        jComboBoxTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTasksActionPerformed(evt);
            }
        });

        add(jComboBoxTasks);

        jButtonChoose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/tasklist/timerwin/dots.gif")));
        jButtonChoose.setPreferredSize(new java.awt.Dimension(22, 22));
        jButtonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseActionPerformed(evt);
            }
        });

        add(jButtonChoose);

        jButtonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/tasklist/timerwin/close.gif")));
        jButtonClose.setPreferredSize(new java.awt.Dimension(22, 22));
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        add(jButtonClose);

    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxTasksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTasksActionPerformed
        Object sel = jComboBoxTasks.getSelectedItem();
        if (sel instanceof UserTask) {
            if (StartedUserTask.getInstance().getStarted() != null)
                StartedUserTask.getInstance().start(null);
            StartedUserTask.getInstance().start((UserTask) sel);
        } else {
            if (StartedUserTask.getInstance().getStarted() != null)
                StartedUserTask.getInstance().start(null);
        }
    }//GEN-LAST:event_jComboBoxTasksActionPerformed

    private void jButtonChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseActionPerformed
        UserTask ut = UTChooserPanel.choose();
        if (ut != null) {
            if (StartedUserTask.getInstance().getStarted() != null)
                StartedUserTask.getInstance().start(null);
            StartedUserTask.getInstance().start(ut);
        }
    }//GEN-LAST:event_jButtonChooseActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        SwingUtilities.getWindowAncestor(this).setVisible(false);
    }//GEN-LAST:event_jButtonCloseActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChoose;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JComboBox jComboBoxTasks;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
}
