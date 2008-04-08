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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.util.*;
import java.awt.event.KeyEvent;
import org.openide.DialogDisplayer;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsBranchFrame extends javax.swing.JFrame {

    static final long serialVersionUID =7007193175692956307L;
    /** Creates new form CvsBranchFrame */
    public CvsBranchFrame(CvsLogInfo logInfo, CvsBranches branches) {
        this.logInfo = logInfo;
        this.branches = branches;
        initComponents ();
        initAccessibility();
        initBranchDraw ();
        setTitle(NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.title", branches.getFileName()));
        pack ();
        HelpCtx.setHelpIDString (getRootPane (), CvsBranchFrame.class.getName ());
        this.addWindowListener (new WindowAdapter() {
            public void windowClosing (WindowEvent event) {
                if (CvsBranchFrame.this.branches != null)
                    CvsBranchFrame.this.branches.close();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        headPanel = new javax.swing.JPanel();
        infoPanel1 = new javax.swing.JPanel();
        revALabel = new javax.swing.JLabel();
        revATextField = new javax.swing.JTextField();
        infoPanel2 = new javax.swing.JPanel();
        revBLabel = new javax.swing.JLabel();
        revBTextField = new javax.swing.JTextField();
        branchPanel = new javax.swing.JPanel();
        branchScrollPane = new javax.swing.JScrollPane();
        hintLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        diffButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        headPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(headPanel, gridBagConstraints);

        infoPanel1.setLayout(new java.awt.GridBagLayout());

        revALabel.setText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.revALabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        infoPanel1.add(revALabel, gridBagConstraints);

        revATextField.setToolTipText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.revATextField.toolTipText"));
        revATextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        infoPanel1.add(revATextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        getContentPane().add(infoPanel1, gridBagConstraints);

        infoPanel2.setLayout(new java.awt.GridBagLayout());

        revBLabel.setText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.revBLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        infoPanel2.add(revBLabel, gridBagConstraints);

        revBTextField.setToolTipText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.revBTextField.toolTipText"));
        revBTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        infoPanel2.add(revBTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        getContentPane().add(infoPanel2, gridBagConstraints);

        branchPanel.setLayout(new java.awt.GridBagLayout());

        branchPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        branchScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        branchPanel.add(branchScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        getContentPane().add(branchPanel, gridBagConstraints);

        hintLabel.setText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.hintLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        getContentPane().add(hintLabel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        diffButton.setText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.diffButton.text"));
        diffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        buttonPanel.add(diffButton, gridBagConstraints);

        closeButton.setText(org.openide.util.NbBundle.getMessage(CvsBranchFrame.class, "CvsBranchFrame.closeButton.text"));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        buttonPanel.add(closeButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        getContentPane().add(buttonPanel, gridBagConstraints);

    }//GEN-END:initComponents

    private void initAccessibility() {
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CvsBranchFrame.class, "ACSD_CvsBranchFrame"));
        revATextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CvsBranchFrame.class, "ACSN_CvsBranchFrame.revATextField"));
        revATextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CvsBranchFrame.class, "ACSD_CvsBranchFrame.revATextField"));
        revBTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CvsBranchFrame.class, "ACSN_CvsBranchFrame.revBTextField"));
        revBTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CvsBranchFrame.class, "ACSD_CvsBranchFrame.revBTextField"));
        revALabel.setLabelFor(revATextField);
        revBLabel.setLabelFor(revBTextField);
        diffButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CvsBranchFrame.class, "ACSD_CvsBranchFrame.diffButton"));
        closeButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CvsBranchFrame.class, "ACSD_CvsBranchFrame.closeButton"));
    }
    
  private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
// Add your handling code here:
      if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
          closeButtonActionPerformed(null);
      }
  }//GEN-LAST:event_formKeyReleased

    private void closeButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // Add your handling code here:
        exitForm(null);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void diffButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffButtonActionPerformed
        // Add your handling code here:
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                String revision1 = revATextField.getText();
                String revision2 = revBTextField.getText();
                if (revision1.length() == 0) revision1 = null;
                if (revision2.length() == 0) revision2 = null;
                if (!branches.doDiff(revision1, revision2)) {
                    DialogDisplayer.getDefault().notify(new org.openide.NotifyDescriptor.Message(/*java.text.MessageFormat.format (*/
                    org.openide.util.NbBundle.getBundle(CvsBranchFrame.class).getString("CvsBranchFrame.diffCommandFailed")/*, new Object[] { file } )*/));
                }
            }
        });
        
    }//GEN-LAST:event_diffButtonActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        branches.close();
        dispose();
    }//GEN-LAST:event_exitForm

    private void initBranchDraw() {
        branchDraw = new BranchDraw();
        branchScrollPane.setViewportView(branchDraw);
        branchDraw.addMouseListener(new BranchMouseListener());
        closeButton.setMnemonic(org.openide.util.NbBundle.getBundle(CvsBranchFrame.class).getString("CvsBranchFrame.closeButton.mnemonic").charAt(0));
        diffButton.setMnemonic(org.openide.util.NbBundle.getBundle(CvsBranchFrame.class).getString("CvsBranchFrame.diffButton.mnemonic").charAt(0));
    }

    public void setPositions(int graphWidth, int graphHeight, Hashtable branchPositions) {
        this.graphWidth = graphWidth;
        this.graphHeight = graphHeight;
        this.branchPositions = branchPositions;
    }

    public void refresh (CvsLogInfo logInfo) {
        this.logInfo = logInfo;
        this.repaint ();
    }

    private String getRevision(int xPos, int yPos, CvsRevisionGraphItem item) {
        if (item == null) return null;
        if (item.getXPos() == xPos && item.getYPos() == yPos) return item.getRevision();
        else {
            String rev = getRevision(xPos, yPos, item.next);
            if (rev != null) return rev;
            Vector branches = item.getBranches();
            if (branches != null) {
                Enumeration en = branches.elements();
                while (en.hasMoreElements()) {
                    CvsRevisionGraphItem branch = (CvsRevisionGraphItem) en.nextElement();
                    //if (branch.getXPos() == xPos && branch.getYPos() == yPos) return branch.getRevision();
                    rev = getRevision(xPos, yPos, branch.next);
                    if (rev != null) return rev;
                }
            }
            return null;
        }
    }

    private String getRevision(int xPos, int yPos) {
        CvsRevisionGraphItem root = logInfo.getRevisionGraph().getRoot();
        return getRevision(xPos, yPos, root);
    }

    private String getRevisionAt(int x, int y) {
        //System.out.println("Mouse Released at ("+x+", "+y+")"); // NOI18N
        x -= BranchDraw.xStart;
        y -= BranchDraw.yStart;
        int xPos = x/(branchDraw.revWidth + BranchDraw.xSpace);
        int yPos = y/(branchDraw.revHeight + BranchDraw.ySpace);
        //System.out.println("xPos = "+xPos+", yPos = "+yPos); // NOI18N
        int lx = x - xPos*(branchDraw.revWidth + BranchDraw.xSpace);
        int ly = y - yPos*(branchDraw.revHeight + BranchDraw.ySpace);
        //System.out.println("lx = "+lx+", ly = "+ly+"; revWidth = "+branchDraw.revWidth+", revHeight = "+ // NOI18N
        //                   branchDraw.revHeight);
        if (lx < 0 || ly < 0 || lx - branchDraw.revWidth > 0 || ly - branchDraw.revHeight > 0)
            return null;
        else return getRevision(xPos, yPos);
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        new CvsBranchFrame (null, null).show ();
    }

    class BranchDraw extends javax.swing.JPanel /*java.awt.Canvas*/ {

        static final int xBound = 5;
        static final int yBound = 3;
        static final int xSpace = 20;
        static final int ySpace = 5;
        static final int xStart = 10;
        static final int yStart = 2;

        int revWidth = 0;
        int revHeight = 0;

        static final long serialVersionUID =-2029660479720769206L;
        public BranchDraw() {
        }

        private int getMaxWidthRev(java.awt.Graphics g) {
            int width = 0;
            java.awt.FontMetrics fm = g.getFontMetrics();
            Vector revisions = logInfo.getRevisions();
            Enumeration en = revisions.elements();
            while (en.hasMoreElements()) {
                String revision = (String) en.nextElement();
                int w = fm.stringWidth(revision);
                if (w > width) width = w;
            }
            return width;
        }

        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            //java.awt.Rectangle viewRect = branchScrollPane.getViewport().getViewRect();
            //g.setClip(viewRect.x, viewRect.y, viewRect.width, viewRect.height);
            //System.out.println("BranchFrame.paint() setting clip("+viewRect.x+", "+ // NOI18N
            //                    viewRect.y+", "+viewRect.width+", "+viewRect.height+")"); // NOI18N
            if (revWidth == 0) revWidth = getMaxWidthRev(g) + 2*xBound;
            if (revHeight == 0) revHeight = g.getFontMetrics().getHeight() + 2*yBound;
            setPreferredSize(new java.awt.Dimension(graphWidth*(revWidth + xSpace) + 2*xStart,
                                                    graphHeight*(revHeight + ySpace) + 2*yStart));
            //setSize(graphWidth*(revWidth + xSpace) + 2*xStart,
            //       graphHeight*(revHeight + ySpace) + 2*yStart);
            paint(g, 0, 0, logInfo.getRevisionGraph().getRoot());
            //branchScrollPane.validate();
            revalidate();
        }

        private void paint(java.awt.Graphics g, int xPos, int yPos, CvsRevisionGraphItem item) {
            if (item == null) return;
            //paintRevision(g, xPos, yPos, item.getRevision());
            paintRevision(g, item.getXPos(), item.getYPos(), item.getRevision());
            Vector branches = item.getBranches();
            if (branches != null) {
                Enumeration en = branches.elements();
                while (en.hasMoreElements()) {
                    CvsRevisionGraphItem branch = (CvsRevisionGraphItem) en.nextElement();
                    String branchName = branch.getRevision();
                    //java.awt.Point point = (java.awt.Point) branchPositions.get(branchName);
                    //paintBranch(g, point.x, point.y - 1, branchName);
                    //paint(g, point.x, point.y, branch.next);
                    paintBranch(g, branch.getXPos(), branch.getYPos(), branchName);
                    g.setColor(lineColor);
                    g.drawLine(item.getXPos()*(revWidth + xSpace) + revWidth + xStart,
                               item.getYPos()*(revHeight + ySpace) + revHeight/2 + yStart,
                               branch.getXPos()*(revWidth + xSpace) + revWidth/2 + xStart,
                               branch.getYPos()*(revHeight + ySpace) + yStart);
                    paint(g, item.getXPos(), item.getYPos(), branch.next);
                }
            }
            if (item.next != null) paint(g, xPos, yPos + 1, item.next);
        }

        private void paintRevision(java.awt.Graphics g, int xPos, int yPos, String text) {
            int x = xPos*(revWidth + xSpace) + xStart;
            int y = yPos*(revHeight + ySpace) + yStart;
            g.setColor(revisionColor);
            g.drawRect(x, y, revWidth, revHeight);
            g.drawString(text, x + xBound, y + yBound + g.getFontMetrics().getAscent());
        }

        private void paintBranch(java.awt.Graphics g, int xPos, int yPos, String text) {
            int x = xPos*(revWidth + xSpace) + xStart;
            int y = yPos*(revHeight + ySpace) + yStart;
            g.setColor(branchColor);
            g.drawRect(x, y, revWidth, revHeight);
            //g.drawRoundRect(x, y, revWidth, revHeight, revWidth, revHeight);
            g.drawString(text, x + xBound, y + yBound + g.getFontMetrics().getAscent());
        }
    }

    private class BranchMouseListener extends javax.swing.event.MouseInputAdapter {
        public void mouseReleased(java.awt.event.MouseEvent e) {
            // The right button comes with e.isMetaDown(), we use this to distinguish it
            // from the left button. (according to http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/MouseEvent.html
            // and http://www.faqs.org/docs/javap/c6/s4.html).
            if (SwingUtilities.isLeftMouseButton(e) && !e.isMetaDown()) {
                String revision = getRevisionAt(e.getX(), e.getY());
                //System.out.println("Revision = "+revision); // NOI18N
                if (revision != null) {
                    revATextField.setText(revision);
                    revATextField.repaint();
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                String revision = getRevisionAt(e.getX(), e.getY());
                //System.out.println("Revision = "+revision); // NOI18N
                if (revision != null) {
                    revBTextField.setText(revision);
                    revBTextField.repaint();
                }
            }
        }
    }

    private CvsLogInfo logInfo;
    private CvsBranches branches;
    private int graphWidth = 0;
    private int graphHeight = 0;
    private Hashtable branchPositions = null;
    private BranchDraw branchDraw;
    private java.awt.Color branchColor = java.awt.Color.red;
    private java.awt.Color revisionColor = java.awt.Color.black;
    private java.awt.Color lineColor = java.awt.Color.black;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel infoPanel2;
    private javax.swing.JPanel infoPanel1;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextField revATextField;
    private javax.swing.JTextField revBTextField;
    private javax.swing.JLabel revBLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel revALabel;
    private javax.swing.JPanel branchPanel;
    private javax.swing.JButton diffButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JPanel headPanel;
    private javax.swing.JScrollPane branchScrollPane;
    // End of variables declaration//GEN-END:variables

}