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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import org.netbeans.api.vcs.commands.CommandTask;

import org.openide.util.*;
import org.netbeans.modules.vcscore.util.table.*;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.*;
import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 *
 * @author  Richard Gregor
 */
final class StatusTreeInfoPanel extends AbstractTreeInfoPanel {
    
    private StatusInfoPanel statPanel;
    private StatusInformation clearStatusInfo;   
    private Class fsCommand;
    
    private javax.swing.JCheckBox cbUptodate;
    private javax.swing.JCheckBox cbModified;
    private javax.swing.JCheckBox cbNeedsPatch;
    private javax.swing.JCheckBox cbNeedsMerge;
    private javax.swing.JCheckBox cbHasConflict;
    private javax.swing.JCheckBox cbLocAdded;
    private javax.swing.JCheckBox cbLocRemoved;
    private javax.swing.JCheckBox cbNeedsCheckout;
    private javax.swing.JCheckBox cbUnknown;
    private javax.swing.JCheckBox cbInvalid;
    private javax.swing.JLabel    lblCount;
    private javax.swing.JLabel lblTitle;    
    private int totalCount;
    private int selectedCount;
    
    private JRadioButton btnAll;
    private String btnAll_Title;
    private String btnJustModified_Title;
    private JRadioButton btnJustModified;
    private int currentFilter = FILTER_ALL;
    private static final int FILTER_ALL = 0;
    private static final int FILTER_MODIFIED = 1;        
    private VcsCommandsProvider cmdProvider;
 
    
    /** 
     * Creates new form StatusTreeInfoPanel
     */ 
    public StatusTreeInfoPanel(File topDir, VcsCommandsProvider cmdProvider){
        super(topDir);
        this.cmdProvider = cmdProvider;
        debug("topDir:"+topDir.getAbsolutePath());
        debug("exists:"+ Boolean.toString(topDir.exists())); 
        initButtons();
        postInit();
    }    
    
    private void initClearInfo() {
        clearStatusInfo = new StatusInformation();
        clearStatusInfo.setFile(new File("")); // NOI18N
        clearStatusInfo.setRepositoryFileName(""); // NOI18N
        clearStatusInfo.setRepositoryRevision(""); // NOI18N
        clearStatusInfo.setWorkingRevision(""); // NOI18N
        clearStatusInfo.setStatus(StatusInformation.UNKNOWN);
        clearStatusInfo.setStickyDate(""); // NOI18N
        clearStatusInfo.setStickyOptions(""); // NOI18N
        clearStatusInfo.setStickyTag(""); // NOI18N
    }
    
    
    protected void setPanel(Object infoData) {
        StatusInformation statData = (StatusInformation)infoData;
        statPanel.setData(statData);
    }
    
    protected JComponent initPanel() {
        initClearInfo();
        statPanel = new StatusInfoPanel(cmdProvider);
        setClearPanel();
        return statPanel;
    }
    
    protected void setClearPanel() {
        statPanel.setData(clearStatusInfo);
    }
    
    protected boolean canBeAdded(StatusInformation sInfo) {
        if (sInfo.getStatus() == StatusInformation.UP_TO_DATE && cbUptodate.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.MODIFIED && cbModified.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.ADDED && cbLocAdded.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.REMOVED && cbLocRemoved.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.NEEDS_CHECKOUT && cbNeedsCheckout.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.NEEDS_MERGE && cbNeedsMerge.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.NEEDS_PATCH && cbNeedsPatch.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.HAS_CONFLICTS && cbHasConflict.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.UNKNOWN && cbUnknown.isSelected()) return true;
        if (sInfo.getStatus() == StatusInformation.INVALID && cbInvalid.isSelected()) return true;
        return false;
    }
    
    protected boolean addToList(FileInfoContainer info) {
        StatusInformation sInfo = (StatusInformation)info;
        return canBeAdded(sInfo);
    }
    
    protected void addFileNode(FileInfoContainer info,DefaultMutableTreeNode parent) {
        StatusInformation sInfo = (StatusInformation)info;
        totalCount = totalCount + 1;
        if (canBeAdded(sInfo)) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(info);
            parent.add(child);
            selectedCount = selectedCount + 1;
        }
    }
    
    
    private void checkBoxChanged() {
        final Cursor c = getCursor();
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
                    totalCount = 0;
                    selectedCount = 0;
                    recreateModel();
                    Integer selCount = new Integer(selectedCount);
                    Integer totCount = new Integer(totalCount);
                    String txt = NbBundle.getMessage(StatusTreeInfoPanel.class, "StatusTreeInfoPanel.lblCount", // NOI18N
                                            selCount.toString(), totCount.toString());
                    lblCount.setText(txt);
                } finally {
                    setCursor(c);
                }
            }
        });
    }
    
    private void initButtons() {
        JPanel panel = getButtonPanel();
        panel.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.btnPanel")); // NOI18N
        panel.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.btnPanel")); // NOI18N
        cbUptodate = new javax.swing.JCheckBox();
        cbModified = new javax.swing.JCheckBox();
        cbLocAdded = new javax.swing.JCheckBox();
        cbLocRemoved = new javax.swing.JCheckBox();
        cbNeedsCheckout = new javax.swing.JCheckBox();
        cbNeedsMerge = new javax.swing.JCheckBox();
        lblTitle = new javax.swing.JLabel();
        lblCount = new javax.swing.JLabel();
        cbNeedsPatch = new javax.swing.JCheckBox();
        cbHasConflict = new javax.swing.JCheckBox();
        cbUnknown = new javax.swing.JCheckBox();
        cbInvalid = new javax.swing.JCheckBox();
        panel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;

        lblTitle.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblTitle.text")); // NOI18N
        lblTitle.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblTitle.text")); // NOI18N
        lblTitle.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblTitle.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        panel.add(lblTitle, gridBagConstraints1);

        cbUptodate.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUptodate.text")); // NOI18N
        cbUptodate.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUptodate.mnemonic").charAt(0)); // NOI18N
        cbUptodate.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUptodate.text")); // NOI18N
        cbUptodate.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUptodate.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (5, 24, 0, 0);
        panel.add(cbUptodate, gridBagConstraints1);
        
        cbModified.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbModified.text")); // NOI18N
        cbModified.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbModified.mnemonic").charAt(0)); // NOI18N        
        cbModified.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbModified.text")); // NOI18N
        cbModified.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbModified.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (5, 12, 0, 11);
        panel.add(cbModified, gridBagConstraints1);
        
        cbLocAdded.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocAdded.text")); // NOI18N
        cbLocAdded.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocAdded.mnemonic").charAt(0)); // NOI18N        
        cbLocAdded.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocAdded.text")); // NOI18N
        cbLocAdded.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocAdded.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbLocAdded, gridBagConstraints1);
        
        cbLocRemoved.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocRemoved.text")); // NOI18N
        cbLocRemoved.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocRemoved.mnemonic").charAt(0)); // NOI18N        
        cbLocRemoved.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocRemoved.text")); // NOI18N
        cbLocRemoved.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocRemoved.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbLocRemoved, gridBagConstraints1);
        
        cbNeedsMerge.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsMerge.text")); // NOI18N
        cbNeedsMerge.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsMerge.mnemonic").charAt(0)); // NOI18N
        cbNeedsMerge.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsMerge.text")); // NOI18N
        cbNeedsMerge.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsMerge.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbNeedsMerge, gridBagConstraints1);

        cbNeedsPatch.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsPatch.text")); // NOI18N
        cbNeedsPatch.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsPatch.mnemonic").charAt(0)); // NOI18N
        cbNeedsPatch.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsPatch.text")); // NOI18N
        cbNeedsPatch.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsPatch.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbNeedsPatch, gridBagConstraints1);

        cbNeedsCheckout.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsCheckout.text")); // NOI18N
        cbNeedsCheckout.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsCheckout.mnemonic").charAt(0)); // NOI18N
        cbNeedsCheckout.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsCheckout.text")); // NOI18N
        cbNeedsCheckout.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsCheckout.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbNeedsCheckout, gridBagConstraints1);
        
        cbHasConflict.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbHasConflicts.text")); // NOI18N
        cbHasConflict.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbHasConflicts.mnemonic").charAt(0)); // NOI18N
        cbHasConflict.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbHasConflicts.text")); // NOI18N
        cbHasConflict.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbHasConflicts.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbHasConflict, gridBagConstraints1);

        cbUnknown.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUnknown.text")); // NOI18N
        cbUnknown.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUnknown.mnemonic").charAt(0)); // NOI18N
        cbUnknown.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUnknown.text")); // NOI18N
        cbUnknown.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUnknown.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        //gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbUnknown, gridBagConstraints1);
        
        cbInvalid.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbInvalid.text")); // NOI18N
        cbInvalid.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbInvalid.mnemonic").charAt(0)); // NOI18N
        cbInvalid.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbInvalid.text")); // NOI18N
        cbInvalid.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbInvalid.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        //gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbInvalid, gridBagConstraints1);
        
        lblCount.setText(NbBundle.getMessage(StatusTreeInfoPanel.class, "StatusTreeInfoPanel.lblCount", "0", "0")); // NOI18N
        lblCount.getAccessibleContext().setAccessibleName(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblCount")); // NOI18N
        lblCount.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblCount")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 6;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 2, 11);
        panel.add(lblCount, gridBagConstraints1);   

        ActionListener listener = (new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxChanged();
            }
        });
        cbUptodate.addActionListener(listener);        
        cbModified.addActionListener(listener);
        cbNeedsPatch.addActionListener(listener);
        cbNeedsMerge.addActionListener(listener);
        cbHasConflict.addActionListener(listener);
        cbLocAdded.addActionListener(listener);
        cbLocRemoved.addActionListener(listener);
        cbNeedsCheckout.addActionListener(listener);
        cbUnknown.addActionListener(listener);
        cbInvalid.addActionListener(listener);
        cbUptodate.setSelected(true);
        cbModified.setSelected(true);
        cbNeedsPatch.setSelected(true);
        cbNeedsMerge.setSelected(true);
        cbHasConflict.setSelected(true);
        cbLocAdded.setSelected(true);
        cbLocRemoved.setSelected(true);
        cbNeedsCheckout.setSelected(true);
        cbUnknown.setSelected(true);
        cbInvalid.setSelected(true);
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
                                                    boolean sel, boolean expanded, 
                                                    boolean leaf, int row, boolean hasFocus) {
      Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (comp instanceof JLabel) {
          JLabel label = (JLabel) comp;
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
          if (node != null) {
              Object userObj = node.getUserObject();
              if (userObj != null) {
                 if (userObj instanceof StatusInformation) { //is statusInfo
                      StatusInformation info = (StatusInformation)userObj;
                      String status = info.getStatusLC();                      
                      label.setText(info.getFile().getName() + "  [" + status + "]"); // NOI18N
                  }
              }
          }
          
      }
      return comp;
  }

 /** in this method the displayer should use the data returned by the command to
 * produce it's own data structures/ fill in UI components
 * @param resultList - the data from the command. It is assumed the Displayer 
 * knows what command the data comes from and most important in what format. 
 * (which FileInfoContainer class is used).
 */
  public void setDataToDisplay(final Collection resultList) {
      final Cursor c = getCursor();
      RequestProcessor.getDefault().post(new Runnable() {
          public void run() {
              try {
                  setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
                  totalCount = 0;
                  selectedCount = 0;
                  StatusTreeInfoPanel.super.setDataToDisplay(resultList);
                  Integer selCount = new Integer(selectedCount);
                  Integer totCount = new Integer(totalCount);
                  String txt = NbBundle.getMessage(StatusTreeInfoPanel.class, "StatusTreeInfoPanel.lblCount", // NOI18N
                                          selCount.toString(), totCount.toString());
                  lblCount.setText(txt);
              } finally {
                  setCursor(c);
              }
          }
      });
  }
  
  /** to be overidden in case more than the filemane is to be displaed in the Table
   * it needs to be a tablemodel implementing the CommandTableModel methods
 */
  protected TableInfoModel createTableModel() {
        TableInfoModel model = new TableInfoModel();
        Class classa = StatusInformation.class;
        String  column1 = NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTableInfoModel.status"); // NOI18N
        String  column2 = NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTableInfoModel.fileName"); // NOI18N
        try {
            Method method1 = classa.getMethod("getStatus", null);     // NOI18N
            Method method2 = classa.getMethod("getFile", null);     // NOI18N
            Method methodT = classa.getMethod("getToolTipText", null);     // NOI18N
            model.setColumnDefinition(0, column1, method1, true, new StatusComparator());
            model.setColumnDefinition(1, column2, method2, true, new FileComparator());
            model.setColumnToolTipGetter(1, methodT);
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        return model;
  }  

  
  public JComponent getComponent() {
      return this;
  }
  
  public Object getComparisonData() {
      return topDirectory;
  }
  
  
  public File getFileDisplayed() {
      return topDirectory;
  }

  private boolean debug= false;
  private void debug(String msg){
      if(debug)
          System.err.println("StatusTreeInfoPanel:"+msg);
  }
  
}
