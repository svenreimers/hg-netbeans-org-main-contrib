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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.portalpack.servers.core.WizardPropertyReader;
import org.netbeans.modules.portalpack.servers.core.api.ConfigPanel;
import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerConstant;
import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerType;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.Util;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class ClasspathConfigPanel extends ConfigPanel{

    private DefaultListModel  classPathListModel;
    private boolean isCustomizeMode = false;
    private String dirPath = System.getProperty("user.home");
    /** Creates new form PSConfigServerPanel */
    public ClasspathConfigPanel() {
        initComponents();
        initData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        classPathList = new javax.swing.JList();
        addClassPathButton = new javax.swing.JButton();
        removeClassPathButton = new javax.swing.JButton();
        setFont(new java.awt.Font("Tahoma", 1, 11));
  
        jLabel5.setText(NbBundle.getMessage(ClasspathConfigPanel.class,"LBL_ClassPath"));

        classPathList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        classPathList.setToolTipText(NbBundle.getMessage(ClasspathConfigPanel.class,"TT_ADD_JAR"));
        jScrollPane1.setViewportView(classPathList);

   
        addClassPathButton.setText(NbBundle.getMessage(ClasspathConfigPanel.class,"LBL_Add"));
        addClassPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addClassPathButtonActionPerformed(evt);
            }
        });

    
        removeClassPathButton.setText(NbBundle.getMessage(ClasspathConfigPanel.class,"LBL_Remove"));
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
    }// </editor-fold>                        

    private void removeClassPathButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                      
// TODO add your handling code here:
        int[] index = classPathList.getSelectedIndices();

        for(int i=0;i<index.length; i++)
        {
            classPathListModel.removeElementAt(i);
        }
    }                                                     

    private void addClassPathButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
// TODO add your handling code here:
        File dir = new File(dirPath);
        if(!dir.exists())
            dirPath = System.getProperty("user.home");
        
        JFileChooser fileChooser = new JFileChooser(dirPath);
        fileChooser.setMultiSelectionEnabled(true);
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

            File[] files = fileChooser.getSelectedFiles();          
            for(int i=0;i<files.length;i++){
                classPathListModel.addElement(files[i].getAbsolutePath());
                if(i == files.length - 1)
                {
                    File parentFile = files[i].getParentFile();
                    if(parentFile != null)
                        dirPath = parentFile.getAbsolutePath();
                }
            }
        }
    }       
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton addClassPathButton;
    private javax.swing.JList classPathList;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeClassPathButton;
    // End of variables declaration                   
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

        return Util.encodeClassPath((String [])list.toArray(new String[0]));
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
        setClassPaths(Util.decodeClassPath(ob.getClassPath()));
        isCustomizeMode = true;
    }

     public String getDescription()
     {
         return NbBundle.getMessage(ClasspathConfigPanel.class,"LBL_CLASSES");
     }
}
