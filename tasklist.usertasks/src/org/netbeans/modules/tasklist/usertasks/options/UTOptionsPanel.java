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

package org.netbeans.modules.tasklist.usertasks.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.jdesktop.layout.GroupLayout;
import org.netbeans.modules.tasklist.core.checklist.CheckList;
import org.netbeans.modules.tasklist.core.checklist.DefaultCheckListModel;
import org.netbeans.modules.tasklist.usertasks.util.TimeComboBox;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Panel with options.
 *
 * @author tl
 */
public class UTOptionsPanel extends javax.swing.JPanel implements ActionListener,
ListDataListener {
    private TimeComboBox cbDayStart;
    private TimeComboBox cbPauseStart;
    private TimeComboBox cbPauseEnd;
    private TimeComboBox cbDayEnd;
    private CheckList clWorkingDays;
    private boolean changed;

    /**
     * Localizes a message.
     * 
     * @param key a key
     * @return localized value 
     */
    private static String i18n(String key) {
        return NbBundle.getMessage(UTOptionsPanel.class, key);
    }
    
    /** 
     * Creates new form UTOptionsPanel.
     */
    public UTOptionsPanel() {
        initComponents();
        jTextFieldFile.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                fileChanged();
            }
            public void insertUpdate(DocumentEvent e) {
                fileChanged();
            }
            public void removeUpdate(DocumentEvent e) {
                fileChanged();
            }
        });
        GroupLayout layout = new GroupLayout(jPanelWorkingHours);
        jPanelWorkingHours.setLayout(layout);
        layout.setAutocreateContainerGaps(true);
        layout.setAutocreateGaps(true);
        JLabel lDayStart = new JLabel(i18n("StartWorkingDay")); // NOI18N
        JLabel lPauseStart = new JLabel(i18n("StartPause")); // NOI18N
        JLabel lPauseEnd = new JLabel(i18n("EndPause")); // NOI18N
        JLabel lDayEnd = new JLabel(i18n("EndWorkingDay")); // NOI18N
        JLabel lWorkingDays = new JLabel(i18n("WorkingDays")); // NOI18N
        cbDayStart = new TimeComboBox();
        cbDayStart.addActionListener(this);
        cbPauseStart = new TimeComboBox();
        cbPauseStart.addActionListener(this);
        cbPauseEnd = new TimeComboBox();
        cbPauseEnd.addActionListener(this);
        cbDayEnd = new TimeComboBox();
        cbDayEnd.addActionListener(this);
        Calendar c = Calendar.getInstance();
        c.set(2006, 0, 2); // monday
        String[] days = new String[7];
        DateFormat df = new SimpleDateFormat("EEEE"); // NOI18N
        for (int i = 0; i < days.length; i++) {
            days[i] = df.format(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        DefaultCheckListModel m = new DefaultCheckListModel(
                new boolean[7], days);
        m.addListDataListener(this);
        clWorkingDays = new CheckList(m);
        GroupLayout.ParallelGroup hpg1 = layout.createParallelGroup().
                add(lDayStart).add(lPauseStart).add(lPauseEnd).add(lDayEnd).
                add(lWorkingDays);
        GroupLayout.ParallelGroup hpg2 = layout.createParallelGroup().
                add(cbDayStart).add(cbPauseStart).add(cbPauseEnd).add(cbDayEnd).
                add(clWorkingDays);
        GroupLayout.SequentialGroup hsg1 = layout.createSequentialGroup().
                add(hpg1).add(hpg2);
        layout.setHorizontalGroup(hsg1);
        GroupLayout.ParallelGroup vpg1 = 
                layout.createParallelGroup(GroupLayout.BASELINE).
                add(lDayStart).add(cbDayStart);
        GroupLayout.ParallelGroup vpg2 = 
                layout.createParallelGroup(GroupLayout.BASELINE).
                add(lPauseStart).add(cbPauseStart);
        GroupLayout.ParallelGroup vpg3 = 
                layout.createParallelGroup(GroupLayout.BASELINE).
                add(lPauseEnd).add(cbPauseEnd);
        GroupLayout.ParallelGroup vpg4 = 
                layout.createParallelGroup(GroupLayout.BASELINE).
                add(lDayEnd).add(cbDayEnd);
        GroupLayout.ParallelGroup vpg5 =
                layout.createParallelGroup(GroupLayout.LEADING).
                add(lWorkingDays).add(clWorkingDays);
        GroupLayout.SequentialGroup vsg = layout.createSequentialGroup().
                add(vpg1).add(vpg2).add(vpg3).add(vpg4).add(vpg5);
        layout.setVerticalGroup(vsg);
    }

    /**
     * Component should update its content.
     */
    public void update() {
        Settings s = Settings.getDefault();
        jCheckBoxAppend.setSelected(s.getAppend());
        jCheckBoxCollectWorkPeriods.setSelected(s.getCollectWorkPeriods());
        jTextFieldFile.setText(s.getFilename());
        jCheckBoxDetectInactivity.setSelected(s.getDetectInactivity());
        jCheckBoxAutoSwitchToComputed.setSelected(s.getAutoSwitchToComputed());
        Preferences p = Settings.getPreferences();
        cbDayStart.setTime(s.getWorkingDayStart());
        cbPauseStart.setTime(s.getPauseStart());
        cbPauseEnd.setTime(s.getPauseEnd());
        cbDayEnd.setTime(s.getWorkingDayEnd());
        boolean[] wd = s.getWorkingDays();
        DefaultCheckListModel m = 
                (DefaultCheckListModel) clWorkingDays.getModel();
        for (int i = 0; i < 7; i++) {
            m.setChecked(i, wd[i]);
        }
        changed = false;
    }

    /**
     * This method is called when Options Dialog "OK" button is pressed.
     */
    public void applyChanges() {
        if (!changed)
            return;
        
        if (!isContentValid()) {
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(i18n(
                    "SettingValidationError"))); // NOI18N
            return;
        }
        
        Settings s = Settings.getDefault();
        s.setAppend(jCheckBoxAppend.isSelected());
        s.setCollectWorkPeriods(jCheckBoxCollectWorkPeriods.isSelected());
        s.setFilename(jTextFieldFile.getText());
        s.setDetectInactivity(jCheckBoxDetectInactivity.isSelected());
        s.setAutoSwitchToComputed(jCheckBoxAutoSwitchToComputed.isSelected());
        Preferences p = Settings.getPreferences();
        p.putInt("dayStart", cbDayStart.getTime());
        p.putInt("pauseStart", cbPauseStart.getTime());
        p.putInt("pauseEnd", cbPauseEnd.getTime());
        p.putInt("dayEnd", cbDayEnd.getTime());
        DefaultCheckListModel m = 
                (DefaultCheckListModel) clWorkingDays.getModel();
        for (int i = 0; i < 7; i++) {
            p.putBoolean("weekDay" + i, m.isChecked(i));
        }
    }

    /**
     * This method is called when Options Dialog "Cancel" button is pressed.
     */
    public void cancel() {
        
    }
    
    /**
     * Will be called if the text in the default file text field has changed.
     */
    private void fileChanged() {
        changed = true;
    }
    
    /**
     * Was the data changed?
     *
     * @return true = changed
     */
    public boolean isChanged() {
        return changed;
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBoxAppend = new javax.swing.JCheckBox();
        jCheckBoxCollectWorkPeriods = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldFile = new javax.swing.JTextField();
        jPanelWorkingHours = new javax.swing.JPanel();
        jCheckBoxDetectInactivity = new javax.swing.JCheckBox();
        jCheckBoxAutoSwitchToComputed = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxAppend, org.openide.util.NbBundle.getMessage(UTOptionsPanel.class, "AppendVsPrepend")); // NOI18N
        jCheckBoxAppend.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxAppend.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxAppend.setOpaque(false);
        jCheckBoxAppend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAppendActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCollectWorkPeriods, org.openide.util.NbBundle.getMessage(UTOptionsPanel.class, "CollectWorkPeriods")); // NOI18N
        jCheckBoxCollectWorkPeriods.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxCollectWorkPeriods.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxCollectWorkPeriods.setOpaque(false);
        jCheckBoxCollectWorkPeriods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxCollectWorkPeriodsActionPerformed(evt);
            }
        });

        jLabel3.setLabelFor(jTextFieldFile);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(UTOptionsPanel.class, "DefaultUserTasksFile")); // NOI18N

        jPanelWorkingHours.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(UTOptionsPanel.class, "Work"))); // NOI18N
        jPanelWorkingHours.setOpaque(false);

        org.jdesktop.layout.GroupLayout jPanelWorkingHoursLayout = new org.jdesktop.layout.GroupLayout(jPanelWorkingHours);
        jPanelWorkingHours.setLayout(jPanelWorkingHoursLayout);
        jPanelWorkingHoursLayout.setHorizontalGroup(
            jPanelWorkingHoursLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 237, Short.MAX_VALUE)
        );
        jPanelWorkingHoursLayout.setVerticalGroup(
            jPanelWorkingHoursLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 194, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDetectInactivity, org.openide.util.NbBundle.getBundle(UTOptionsPanel.class).getString("DetectInactivity")); // NOI18N
        jCheckBoxDetectInactivity.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxDetectInactivity.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxDetectInactivity.setOpaque(false);
        jCheckBoxDetectInactivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDetectInactivityActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxAutoSwitchToComputed, org.openide.util.NbBundle.getBundle(UTOptionsPanel.class).getString("AutoSwithToComputed")); // NOI18N
        jCheckBoxAutoSwitchToComputed.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxAutoSwitchToComputed.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxAutoSwitchToComputed.setOpaque(false);
        jCheckBoxAutoSwitchToComputed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAutoSwitchToComputedActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBoxAppend)
                    .add(jCheckBoxCollectWorkPeriods)
                    .add(jCheckBoxDetectInactivity)
                    .add(jCheckBoxAutoSwitchToComputed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 664, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextFieldFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 504, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanelWorkingHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jCheckBoxAppend)
                .add(11, 11, 11)
                .add(jCheckBoxCollectWorkPeriods)
                .add(11, 11, 11)
                .add(jCheckBoxDetectInactivity)
                .add(11, 11, 11)
                .add(jCheckBoxAutoSwitchToComputed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jTextFieldFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelWorkingHours, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxAutoSwitchToComputedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAutoSwitchToComputedActionPerformed
        changed = true;
    }//GEN-LAST:event_jCheckBoxAutoSwitchToComputedActionPerformed

    private void jCheckBoxDetectInactivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDetectInactivityActionPerformed
        changed = true;
    }//GEN-LAST:event_jCheckBoxDetectInactivityActionPerformed

    private void jCheckBoxCollectWorkPeriodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxCollectWorkPeriodsActionPerformed
        changed = true;
    }//GEN-LAST:event_jCheckBoxCollectWorkPeriodsActionPerformed

    private void jCheckBoxAppendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAppendActionPerformed
        changed = true;
    }//GEN-LAST:event_jCheckBoxAppendActionPerformed

    /**
     * Checks the content of this panel.
     * 
     * @return true = OK 
     */
    public boolean isContentValid() {
        if (jTextFieldFile.getText().trim().length() == 0)
            return false;
        int a = cbDayStart.getTime();
        int b = cbPauseStart.getTime();
        int c = cbPauseEnd.getTime();
        int d = cbDayEnd.getTime();
        if (b < a || c < b || d < c)
            return false;
        boolean all = false;
        DefaultCheckListModel m = (DefaultCheckListModel) 
                clWorkingDays.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            all |= m.isChecked(i);
        }
        if (!all)
            return false;
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        changed = true;
    }

    public void intervalAdded(ListDataEvent e) {
        changed = true;
    }

    public void intervalRemoved(ListDataEvent e) {
        changed = true;
    }

    public void contentsChanged(ListDataEvent e) {
        changed = true;
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox jCheckBoxAppend;
    public javax.swing.JCheckBox jCheckBoxAutoSwitchToComputed;
    public javax.swing.JCheckBox jCheckBoxCollectWorkPeriods;
    public javax.swing.JCheckBox jCheckBoxDetectInactivity;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JPanel jPanelWorkingHours;
    public javax.swing.JTextField jTextFieldFile;
    // End of variables declaration//GEN-END:variables
}
