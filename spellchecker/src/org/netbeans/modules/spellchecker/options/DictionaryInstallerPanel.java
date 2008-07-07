/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is David Kaspar.
 * Portions created by David Kaspar are Copyright (C) 2004-2007.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): David Kaspar.
 */
package org.netbeans.modules.spellchecker.options;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.swing.JFileChooser;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

/**
 * @author David Kaspar
 */
public class DictionaryInstallerPanel extends javax.swing.JPanel {
    
    /** Creates new form JPanel */
    public DictionaryInstallerPanel () {
        initComponents ();
        initValues ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        tDictionary = new javax.swing.JTextField();
        bBrowse = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cEncoding = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        tLocale = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 9, 9, 9));
        jLabel1.setDisplayedMnemonic(getMessage ("MNE_DictionaryFile").charAt (0));
        jLabel1.setLabelFor(tDictionary);
        jLabel1.setText(getMessage ("DictionaryFile"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel1, gridBagConstraints);

        tDictionary.setPreferredSize(new java.awt.Dimension(300, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(tDictionary, gridBagConstraints);

        bBrowse.setText("...");
        bBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(bBrowse, gridBagConstraints);

        jLabel2.setDisplayedMnemonic(getMessage ("MNE_DictionaryEncoding").charAt (0));
        jLabel2.setLabelFor(cEncoding);
        jLabel2.setText(getMessage ("DictionaryEncoding")
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(cEncoding, gridBagConstraints);

        jLabel3.setDisplayedMnemonic(getMessage ("MNE_DictionaryLocale").charAt (0));
        jLabel3.setLabelFor(tLocale);
        jLabel3.setText(getMessage ("DictionaryLocale"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel3, gridBagConstraints);

        tLocale.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(tLocale, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void bBrowseActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBrowseActionPerformed
        JFileChooser filechooser = new JFileChooser (tDictionary.getText ());
        int ret = filechooser.showOpenDialog (null);
        if (ret == JFileChooser.APPROVE_OPTION)
            tDictionary.setText (filechooser.getSelectedFile ().getAbsolutePath ());
    }//GEN-LAST:event_bBrowseActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton bBrowse;
    public javax.swing.JComboBox cEncoding;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JTextField tDictionary;
    public javax.swing.JTextField tLocale;
    // End of variables declaration//GEN-END:variables
    
    public static String getMessage (String key) {
        return NbBundle.getMessage (DictionaryInstallerPanel.class, key);
    }
    private static String homedir = System.getProperty ("netbeans.home"); // NOI18N
    private static String userdir = System.getProperty ("netbeans.user"); // NOI18N
    private static boolean availHomedir = new File (homedir).canWrite ();
    private static boolean availUserdir = new File (userdir).canWrite ();
    
    public void initValues () {
        tDictionary.setText (System.getProperty ("user.home")); // NOI18N
        Set<String> set = Charset.availableCharsets ().keySet ();
        cEncoding.setModel(new javax.swing.DefaultComboBoxModel(set.toArray (new String[set.size ()])));
        cEncoding.setSelectedItem ("ISO-8859-1"); // NOI18N
        tLocale.setText (""); // NOI18N
//        rAllUsers.setEnabled (availHomedir);
//        rCurrentUser.setEnabled (availUserdir);
//        if (availHomedir)
//            rAllUsers.setSelected (true);
//        else if (availUserdir)
//            rCurrentUser.setSelected (true);
//        else {
//            rAllUsers.setSelected (false);
//            rCurrentUser.setSelected (false);
//        }
    }
    
    private static final int BUFFER_LENGTH = 65536;

    private static File dictionaryFile(String loc, boolean shared) {
        String filename = shared ? homedir : userdir;

        filename += File.separator + "modules" + File.separator + "dict"; // NOI18N
        filename += File.separator + "dictionary";
        if (loc != null  &&  !"".equals (loc))
            filename += "_" + loc;
        return new File (filename + ".txt"); // NOI18N
    }
    
    public static void doInstall (DictionaryDescription description) {
        InputStreamReader input = null;
        OutputStreamWriter output = null;
        try {
            boolean shared;
            if (description.rAllUsers)
                shared = true;
            else if (description.rCurrentUser)
                shared = false;
            else {
                DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message (getMessage ("MSG_NoInstallDirectoryAvailable"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                return;
            }

            File file = dictionaryFile(description.targetLocale, shared);

            file.getParentFile().mkdirs();

            //TODO: if the dictionary already exists, provide user with a warning.
            input = new InputStreamReader (new FileInputStream (description.dictionaryFile), description.fileEncoding);
            output = new OutputStreamWriter (new FileOutputStream (file), "UTF-8"); // NOI18N
            char[] buffer = new char[BUFFER_LENGTH];
            int len = BUFFER_LENGTH;
            do {
                len = input.read (buffer);
                output.write (buffer, 0, len);
            } while (len == BUFFER_LENGTH);
            StatusDisplayer.getDefault ().setStatusText (getMessage ("MSG_DictionaryWasInstalled")); // NOI18N
        } catch (FileNotFoundException e) {
            DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message (getMessage ("MSG_InputFileNotFound"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
        } catch (UnsupportedEncodingException e) {
            DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message (getMessage ("MSG_UnsupportedEncoding"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
        } catch (IOException e) {
            DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message (getMessage ("MSG_IOErrorDuringInstallation"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
        } finally {
            if (input != null) try { input.close (); } catch (IOException e) {}
            if (output != null) try { output.close (); } catch (IOException e) {}
            
            //TODO: Dictionary.clearDictionaries();
        }
    }

    public DictionaryDescription createDescription() {
        return new DictionaryDescription(false, true, /*rAllUsers.isSelected(), rCurrentUser.isSelected(), */tDictionary.getText(), (String) cEncoding.getSelectedItem(), tLocale.getText());
    }

    public static void removeDictionary(Locale remove) {
        File toRemove = dictionaryFile(remove.toString(), false);

        toRemove.delete();
        toRemove = dictionaryFile(remove.toString(), true);
        toRemove.delete();
    }
    
    public static class DictionaryDescription {
        private boolean rAllUsers;
        private boolean rCurrentUser;
        private String  dictionaryFile;
        private String  fileEncoding;
        private String  targetLocale;

        public DictionaryDescription(boolean rAllUsers, boolean rCurrentUser, String dictionaryFile, String fileEncoding, String targetLocale) {
            this.rAllUsers = rAllUsers;
            this.rCurrentUser = rCurrentUser;
            this.dictionaryFile = dictionaryFile;
            this.fileEncoding = fileEncoding;
            this.targetLocale = targetLocale;
        }

        public Locale getLocale() {
            return new Locale(targetLocale);
        }
    }
    
}