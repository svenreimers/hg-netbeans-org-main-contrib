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
package org.netbeans.modules.scala.editor.overridden;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.ui.ElementOpen;
import org.netbeans.modules.csl.core.UiUtils
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
class IsOverriddenPopup(caption: String, declarations: List[ElementDescription]) extends JPanel with FocusListener {

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private var jLabel1: javax.swing.JLabel = _
  private var jList1: javax.swing.JList = _
  private var jScrollPane1: javax.swing.JScrollPane = _
  // End of variables declaration//GEN-END:variables

    
  initComponents

  jList1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))

  addFocusListener(this)
    
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private def initComponents {
    var gridBagConstraints: java.awt.GridBagConstraints = null

    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jList1 = new javax.swing.JList();

    setFocusCycleRoot(true);
    setLayout(new java.awt.GridBagLayout());

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText(caption);
    jLabel1.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    add(jLabel1, gridBagConstraints);

    jList1.setModel(createListModel);
    jList1.setCellRenderer(new RendererImpl())
    jList1.setSelectedIndex(0);
    jList1.setVisibleRowCount(declarations.size);
    jList1.addKeyListener(new java.awt.event.KeyAdapter() {
        override def keyPressed(evt: java.awt.event.KeyEvent) {
          jList1KeyPressed(evt);
        }
      });
    jList1.addMouseListener(new java.awt.event.MouseAdapter() {
        override def mouseClicked(evt: java.awt.event.MouseEvent) {
          jList1MouseClicked(evt);
        }
      });
    jScrollPane1.setViewportView(jList1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(jScrollPane1, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents
    
  private def jList1MouseClicked(evt: java.awt.event.MouseEvent) {//GEN-FIRST:event_jList1MouseClicked
    // TODO add your handling code here:
    if (evt.getButton == MouseEvent.BUTTON1 && evt.getClickCount == 1) {
      openSelected
    }
  }//GEN-LAST:event_jList1MouseClicked
    
  private def jList1KeyPressed(evt: java.awt.event.KeyEvent) {//GEN-FIRST:event_jList1KeyPressed
    // TODO add your handling code here:
    if (evt.getKeyCode == KeyEvent.VK_ENTER && evt.getModifiers == 0) {
      openSelected
    }
  }//GEN-LAST:event_jList1KeyPressed
    
    
  private def openSelected {
    val desc = jList1.getSelectedValue.asInstanceOf[ElementDescription]
        
    if (desc != null) {
      val file = desc.getSourceFile
            
      if (file != null) {
        UiUtils.open(file, desc.getOffset)
      } else {
        Toolkit.getDefaultToolkit.beep
      }
    }
        
    PopupUtil.hidePopup
  }
    
  private def createListModel: ListModel = {
    val dlm = new DefaultListModel
        
    for (el <- declarations) {
      dlm.addElement(el)
    }
        
    dlm
  }
    
  private class RendererImpl extends DefaultListCellRenderer {
    override def getListCellRendererComponent(list: JList,
                                     value: Object,
                                     index: Int,
                                     isSelected: boolean,
                                     cellHasFocus: boolean): Component = {
      val c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            
      value match {
        case desc: ElementDescription =>
          setIcon(desc.getIcon)
          setText(desc.getDisplayName)
        case _ =>
      }
            
      c
    }
  }
    
  override def focusGained(arg0: FocusEvent) {
    jList1.requestFocus
    jList1.requestFocusInWindow
  }
    
  override def focusLost(arg0: FocusEvent) {}
}
