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

package org.netbeans.modules.vcscore.ui.views.types;


import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

import org.openide.util.*;
import org.openide.explorer.*;


import org.netbeans.modules.vcscore.ui.views.*;

/**
 *
 * @author  mkleint
 * @version
 */
public class LogTreeInfoPanel extends AbstractTreeInfoPanel implements ChildrenInfoFilter {

    private LogInfoPanel logPanel;
    private JTextField txFilter;
    private JLabel lblFilter;
    private JLabel lblTitle;
    private String filterString = null;
    private int totalCount = 0;
    private int selectedCount = 0;
    private javax.swing.JLabel lblCount;   

    /** Creates new form StatusTreeInfoPanel */
    public LogTreeInfoPanel() {
        super();
//        initPanelComponents();
        postInit();
 //       recreateModel();
    }
    
    
    protected boolean hasFilter() {
        return true;
    }
    
    protected JComponent createFilterComponent() {
        JPanel panel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblFilter = new javax.swing.JLabel();
        txFilter = new javax.swing.JTextField();
        lblCount = new javax.swing.JLabel();
        panel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        lblTitle.setText(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblTitle.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (12, 12, 0, 11);
        panel.add(lblTitle, gridBagConstraints1);
        
        lblFilter.setText(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filterLog")); // NOI18N
        lblFilter.setDisplayedMnemonic(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filterLog.mnemonic").charAt(0)); // NOI18N
        lblFilter.setLabelFor(txFilter);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(5, 24, 11, 0);
        panel.add(lblFilter, gridBagConstraints1);
        
        //txFilter.setPreferredSize(new java.awt.Dimension(100, 20));
        txFilter.setText(""); // NOI18N
        txFilter.setMinimumSize(new java.awt.Dimension(100, 20));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 11, 11);
        panel.add(txFilter, gridBagConstraints1);
        
        lblCount.setText(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblCount")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 2, 11);
        panel.add(lblCount, gridBagConstraints1);
 

//---- ENDO OF COPIED STUFF --- ===================================================
// =========================================================================
        txFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                filterLogMessages();
            }    
        });      
        return panel;
    }    
    
  protected JComponent initPanel() {
//      initClearInfo();
      logPanel = new LogInfoPanel();
//      setClearPanel();
      return logPanel;
  }
  
  public void addNotify() {
      super.addNotify();
      // run under mutex
      
      ExplorerManager em = ExplorerManager.find(this);
      
      if (em != null) {
          FileVcsInfo info = (FileVcsInfo)em.getRootContext().getCookie(FileVcsInfo.class);
          totalCount = 0;
          selectedCount = 0;
          totalCount = computeChildren(info, true);
          selectedCount = computeChildren(info, false);
          showCounts(totalCount, selectedCount);
      }
  }

  
  private void filterLogMessages() {
      String filter = txFilter.getText(); 
      if (filter.trim().equals("")) filterString = null; // NOI18N
      else filterString = filter;

      ExplorerManager em = ExplorerManager.find (this);
      if (em != null) {
          FileVcsInfo info = (FileVcsInfo)em.getRootContext().getCookie(FileVcsInfo.class);
          totalCount = 0;
          selectedCount = 0;
          info.setChildrenFilter(this);
          totalCount = computeChildren(info, true);
          selectedCount = computeChildren(info, false);
      }
      showCounts(totalCount, selectedCount);
  }    
  
  private void showCounts(int total, int selected) {
      Integer selCount = new Integer(selected);
      Integer totCount = new Integer(total);
      String txt = NbBundle.getMessage(LogTreeInfoPanel.class, "LogTreeInfoPanel.lblCount", // NOI18N
                                selCount.toString(), totCount.toString());
      lblCount.setText(txt);
  }
  
  private int computeChildren(FileVcsInfo parent, boolean total) {
      int count = 0;
      if (parent.getChildren() instanceof FileVcsInfoChildren) {
          FileVcsInfoChildren childs = (FileVcsInfoChildren)parent.getChildren();
          Iterator it;
          if (total) {
              it = childs.getAllKeys();
          } else {
              it = childs.getFilteredKeys();
          }
          while (it.hasNext()) {
              FileVcsInfo info = (FileVcsInfo)it.next();
              if (info.getType().equals(LogInfoPanel.TYPE)) {
                  count = count + 1;
              } else {
                  count = count + computeChildren(info, total);
              }
          }
      }
      return count;
  }
  
  /** the filtering method that tells wheather the info should be included in
   * the set. For unwanted infos returns false.
   */
  public boolean checkFileInfo(FileVcsInfo info) {
      if (filterString == null) {
          return true;
      }
      if (!info.getType().equals(LogInfoPanel.TYPE)) {
          if (info.getType().startsWith(FileVcsInfo.BLANK)) {
              if (((FileVcsInfoChildren)info.getChildren()).getFilteredKeyCount() == 0) {
                  return false;
              }
          }
          return true;
      }
      FileVcsInfo.Composite composite = (FileVcsInfo.Composite)info.getAttribute(LogInfoPanel.REVISIONS_LIST);
      if (composite != null) {
          for (int i = 0; i < composite.getCount(); i++) {
              FileVcsInfo.CompositeItem item = (FileVcsInfo.CompositeItem)composite.getRow(i);
              if (item.getAttributeNonNull(LogInfoPanel.REVISION_MESSAGE).indexOf(filterString) >= 0) {
                  return true;
              }
          }
      }
      return false;
  }
  
}
