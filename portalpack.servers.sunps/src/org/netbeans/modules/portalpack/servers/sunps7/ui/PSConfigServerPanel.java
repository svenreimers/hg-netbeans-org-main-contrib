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

package org.netbeans.modules.portalpack.servers.sunps7.ui;

import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerConstant;
import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerType;
import org.netbeans.modules.portalpack.servers.core.api.ConfigPanel;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.portalpack.servers.core.WizardPropertyReader;
import org.netbeans.modules.portalpack.servers.core.util.DirectoryChooser;
import org.openide.WizardDescriptor;

/**
 *
 * @author  Satya
 */
public class PSConfigServerPanel extends ConfigPanel{

    private DefaultListModel  classPathListModel;
    private boolean isCustomizeMode = false;
    /** Creates new form PSConfigServerPanel */
    public PSConfigServerPanel() {
        initComponents();
        initData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        classPathList = new javax.swing.JList();
        addClassPathButton = new javax.swing.JButton();
        removeClassPathButton = new javax.swing.JButton();

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText(org.openide.util.NbBundle.getMessage(PSConfigServerPanel.class, "LBL_CLASSPATH")); // NOI18N

        classPathList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        classPathList.setToolTipText("Add jar library which will be used only during compilation. Those will not be included in war.");
        jScrollPane1.setViewportView(classPathList);

        addClassPathButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        addClassPathButton.setText(org.openide.util.NbBundle.getMessage(PSConfigServerPanel.class, "LBL_ADD")); // NOI18N
        addClassPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addClassPathButtonActionPerformed(evt);
            }
        });

        removeClassPathButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        removeClassPathButton.setText(org.openide.util.NbBundle.getMessage(PSConfigServerPanel.class, "LBL_REMOVE")); // NOI18N
        removeClassPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeClassPathButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .add(13, 13, 13)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 353, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(removeClassPathButton)
                    .add(addClassPathButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(41, 41, 41)
                        .add(addClassPathButton)
                        .add(14, 14, 14)
                        .add(removeClassPathButton))
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeClassPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeClassPathButtonActionPerformed
// TODO add your handling code here:
        int[] index = classPathList.getSelectedIndices();

        for(int i=0;i<index.length; i++)
        {
            classPathListModel.removeElementAt(i);
        }
    }//GEN-LAST:event_removeClassPathButtonActionPerformed

    private void addClassPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addClassPathButtonActionPerformed
// TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter(){
            public boolean accept(File f) {
                if(f.getName().endsWith(".jar"))
                    return true;
                else if(f.isDirectory())
                    return true;
                return false;
            }

            public String getDescription() {
                return "Jar Files";
            }

        });


        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();

            classPathListModel.addElement(file.getAbsolutePath());
        }


    }//GEN-LAST:event_addClassPathButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addClassPathButton;
    private javax.swing.JList classPathList;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeClassPathButton;
    // End of variables declaration//GEN-END:variables
    private String checkForNull(String txt) {
        if(txt == null)
            return "";
        else return txt.trim();
    }

    private NetbeansServerType getNetbeansServerType(String type) {
        for(int i=0;i<NetbeansServerConstant.AVAILABLE_SERVERS.length;i++) {
            if(NetbeansServerConstant.AVAILABLE_SERVERS[i].getType().equalsIgnoreCase(type.trim()))
                return NetbeansServerConstant.AVAILABLE_SERVERS[i];
        }
        return null;
    }

    private void initData() {

        classPathListModel = new DefaultListModel();
        classPathList.setModel(classPathListModel);


    }

    public void store(WizardDescriptor d) {

        WizardPropertyReader wr = new WizardPropertyReader(d);
        wr.setClassPath(getClassPaths());
       /* d.putProperty("SERVER_TYPE",getServerType());
        d.putProperty("SERVER_HOME",getServerHome());
        d.putProperty("INSTANCE_ID",getDomainDir());
        d.putProperty("ADMIN_PORT",getAdminPort());
        d.putProperty("CLASSPATH",getClassPaths());*/
    }

    public void read(WizardDescriptor d){
        WizardPropertyReader wr = new WizardPropertyReader(d);
    }

    public String getClassPaths() {

        Enumeration en = classPathListModel.elements();

        List list = new ArrayList();
        while(en.hasMoreElements())
        {
            String elm = (String)en.nextElement();
            list.add(elm);
        }

        return encodeClassPath((String [])list.toArray(new String[0]));
    }

    public void setClassPaths(String[] path)
    {
        classPathListModel.removeAllElements();
        for(int i=0;i<path.length;i++)
        {
            classPathListModel.addElement(path[i]);
        }
    }

    public boolean validate(Object wizardDescriptor) {
        return true;
    }

    public void populateDataForCustomizer(PSConfigObject ob)
    {
        setClassPaths(decodeClassPath(ob.getClassPath()));
        isCustomizeMode = true;
    }

     public String getDescription()
     {
         return "Server";
     }

     public String encodeClassPath(String[] strs)
     {
         if(strs == null || strs.length == 0)
             return "";

         StringBuffer sb = new StringBuffer();
         for(int i=0;i<strs.length;i++)
         {
             sb.append(strs[i]).append(";");

         }
         return sb.toString();
     }

     public String[] decodeClassPath(String str)
     {
         List classPathList = new ArrayList();
         StringTokenizer st = new StringTokenizer(str,";");

         while(st.hasMoreTokens())
         {
             String temp = st.nextToken();
             if(temp != null || temp.trim().equals(""))
                 classPathList.add(temp);
         }

         return (String[])classPathList.toArray(new String[0]);

     }
}