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

package org.netbeans.modules.helpbuilder.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.helpbuilder.HelpPreview;
import org.netbeans.modules.helpbuilder.processors.HelpSetProcessor;
import org.netbeans.modules.helpbuilder.processors.MapProcessor;
import org.netbeans.modules.helpbuilder.tree.HelpTreeNode;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

import org.openide.util.NbBundle;

/** A single panel for a wizard.
 * You probably want to make a wizard iterator to hold it.
 *
 * @author  Richard Gregor
 */
public class HelpPreviewPanel extends javax.swing.JPanel {
    private  HelpSet hs;
    private  JHelp help;
    private  ClassLoader loader;
    private  String helpsetPath;
    private JPanel helpPanel;
    private final HelpPreview descriptor; 

    /** Create the wizard panel and set up some basic properties. */
    public HelpPreviewPanel(HelpPreview descriptor) {
        this.descriptor = descriptor;                
        try{
            loader = this.getClass().getClassLoader();            
            URL hsURL = new URL("file:"+getHelpsetPath());
            hs = new HelpSet(loader,hsURL);
        }
        catch(Exception ee){
            ErrorManager.getDefault().notify(ee);        
        }
        
        helpPanel = new JPanel();
        helpPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));        
        help = new JHelp(hs);  
        initComponents ();        
        add(help, java.awt.BorderLayout.CENTER);
        
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ProjectFinishPanel.class, "TITLE_HelpPreviewPanel"));
        
    }
    
    /**
     * Recreates help object
     * Called from wizarddescriptor's method read settings
     */
    public void recreate(){
        System.err.println("recreate");
        try{
            loader = this.getClass().getClassLoader();
            //URL hsURL = HelpSet.findHelpSet(loader,getHelpsetPath());
            URL hsURL = new URL("file:"+getHelpsetPath());
            hs = new HelpSet(loader,hsURL);
        }
        catch(Exception ee){
            ErrorManager.getDefault().notify(ee);        
        }
        
        help = new JHelp(hs);                
        removeAll();
        add(help, java.awt.BorderLayout.CENTER);
       // add(new JLabel("ahoj"),BorderLayout.NORTH);
        repaint();
    }
    
     
    public boolean isValid(){
        return true;
    }
    
    
    private static String getHelpsetPath(){
        return ProjectSetupPanel.getTargetLocation()+File.separator+"HelpSet.hs";
    }
    
   
    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.BorderLayout(10, 10));

        setBorder(new javax.swing.border.EtchedBorder());
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


}
