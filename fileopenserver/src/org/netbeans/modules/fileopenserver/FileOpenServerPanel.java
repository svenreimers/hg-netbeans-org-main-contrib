/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.fileopenserver;

final class FileOpenServerPanel extends javax.swing.JPanel {

    private final FileOpenServerOptionsPanelController controller;

    FileOpenServerPanel(FileOpenServerOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        externalEditorPanel = new javax.swing.JPanel();
        externalEditorCommandLabel = new javax.swing.JLabel();
        externalEditorCommandTextField = new javax.swing.JTextField();
        externalEditorCommandHelpLabel = new javax.swing.JLabel();
        lineNumberStartsWith0CheckBox = new javax.swing.JCheckBox();
        columnNumberStartsWith0CheckBox = new javax.swing.JCheckBox();
        serverPanel = new javax.swing.JPanel();
        portNumberLabel = new javax.swing.JLabel();
        portNumberSpinner = new javax.swing.JSpinner();
        startAtStartupCheckBox = new javax.swing.JCheckBox();
        logRequestsCheckBox = new javax.swing.JCheckBox();

        externalEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("External Editor"));

        org.openide.awt.Mnemonics.setLocalizedText(externalEditorCommandLabel, "External editor command:");

        org.openide.awt.Mnemonics.setLocalizedText(externalEditorCommandHelpLabel, "{0}=filename,{1}=line number,{3}=column number");

        org.openide.awt.Mnemonics.setLocalizedText(lineNumberStartsWith0CheckBox, "Line number starts with 0");
        lineNumberStartsWith0CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineNumberStartsWith0CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(columnNumberStartsWith0CheckBox, "Column number starts with 0");

        org.jdesktop.layout.GroupLayout externalEditorPanelLayout = new org.jdesktop.layout.GroupLayout(externalEditorPanel);
        externalEditorPanel.setLayout(externalEditorPanelLayout);
        externalEditorPanelLayout.setHorizontalGroup(
            externalEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(externalEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(externalEditorCommandLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(externalEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(columnNumberStartsWith0CheckBox)
                    .add(externalEditorCommandHelpLabel)
                    .add(lineNumberStartsWith0CheckBox)
                    .add(externalEditorCommandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
                .addContainerGap())
        );
        externalEditorPanelLayout.setVerticalGroup(
            externalEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(externalEditorPanelLayout.createSequentialGroup()
                .add(externalEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(externalEditorCommandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(externalEditorCommandLabel))
                .add(8, 8, 8)
                .add(externalEditorCommandHelpLabel)
                .add(6, 6, 6)
                .add(lineNumberStartsWith0CheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnNumberStartsWith0CheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        serverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Server"));

        org.openide.awt.Mnemonics.setLocalizedText(portNumberLabel, "Port number:");

        portNumberSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(4050), Integer.valueOf(1025), null, Integer.valueOf(1)));

        org.openide.awt.Mnemonics.setLocalizedText(startAtStartupCheckBox, "Start at startup");

        org.openide.awt.Mnemonics.setLocalizedText(logRequestsCheckBox, "Log requests");

        org.jdesktop.layout.GroupLayout serverPanelLayout = new org.jdesktop.layout.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(portNumberLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(logRequestsCheckBox)
                    .add(startAtStartupCheckBox)
                    .add(portNumberSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(308, Short.MAX_VALUE))
        );

        serverPanelLayout.linkSize(new java.awt.Component[] {logRequestsCheckBox, startAtStartupCheckBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portNumberLabel)
                    .add(portNumberSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(startAtStartupCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(logRequestsCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, externalEditorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, serverPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(serverPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(externalEditorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lineNumberStartsWith0CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineNumberStartsWith0CheckBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_lineNumberStartsWith0CheckBoxActionPerformed
    void load() {
        final FileOpenServerSettings fileOpenServerSettings = FileOpenServerSettings.getInstance();
        externalEditorCommandTextField.setText(fileOpenServerSettings.getExternalEditorCommand());
        portNumberSpinner.setValue(fileOpenServerSettings.getPortNumber());
        startAtStartupCheckBox.setSelected(fileOpenServerSettings.isStartAtStartup());
        logRequestsCheckBox.setSelected(fileOpenServerSettings.isLogRequests());
        lineNumberStartsWith0CheckBox.setSelected(fileOpenServerSettings.isLineNumberStartsWith0());
        columnNumberStartsWith0CheckBox.setSelected(fileOpenServerSettings.isColumnNumberStartsWith0());
    }

    void store() {
        final FileOpenServerSettings fileOpenServerSettings = FileOpenServerSettings.getInstance();
        fileOpenServerSettings.setExternalEditorCommand(externalEditorCommandTextField.getText());
        fileOpenServerSettings.setPortNumber(((Integer)portNumberSpinner.getValue()));
        fileOpenServerSettings.setStartAtStartup(startAtStartupCheckBox.isSelected());
        fileOpenServerSettings.setLogRequests(logRequestsCheckBox.isSelected());
        fileOpenServerSettings.setLineNumberStartsWith0(lineNumberStartsWith0CheckBox.isSelected());
        fileOpenServerSettings.setColumnNumberStartsWith0(columnNumberStartsWith0CheckBox.isSelected());
    }

    boolean valid() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox columnNumberStartsWith0CheckBox;
    private javax.swing.JLabel externalEditorCommandHelpLabel;
    private javax.swing.JLabel externalEditorCommandLabel;
    private javax.swing.JTextField externalEditorCommandTextField;
    private javax.swing.JPanel externalEditorPanel;
    private javax.swing.JCheckBox lineNumberStartsWith0CheckBox;
    private javax.swing.JCheckBox logRequestsCheckBox;
    private javax.swing.JLabel portNumberLabel;
    private javax.swing.JSpinner portNumberSpinner;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JCheckBox startAtStartupCheckBox;
    // End of variables declaration//GEN-END:variables
}