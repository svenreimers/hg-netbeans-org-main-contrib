/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.blueprints.ui;

import java.net.URL;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Category;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Solution;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Writeup;

/**
 * Tab Panel containing a browser with the contents of the article.
 *
 * @author Mark Roth
 */
public class SolutionTab 
    extends BluePrintsTabPanel
{
    /** Creates new form SolutionTab */
    public SolutionTab(BluePrintsPanel bluePrintsPanel) {
        super(bluePrintsPanel);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        solutionBrowser = new HtmlBrowserWithScrollPosition(false, false);

        setLayout(new java.awt.BorderLayout());

        add(solutionBrowser, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel solutionBrowser;
    // End of variables declaration//GEN-END:variables
    
    public void setScrollPosition(int scrollPosition) {
        ((HtmlBrowserWithScrollPosition)solutionBrowser).
            setScrollPosition(scrollPosition);
    }

    public int getScrollPosition() {
        return ((HtmlBrowserWithScrollPosition)solutionBrowser).
            getScrollPosition();
    }
    
    public void updateTab() {
        Category category = bluePrintsPanel.getSelectedCategory();
        Solution solution = bluePrintsPanel.getSelectedArticle();
        if(solution != null) {
            Writeup writeup = solution.getWriteup();
            String articleURLString = BluePrintsPanel.CATALOG_RESOURCES_URL 
                + "/web/" + writeup.getArticlePath(); // NOI18N
            URL articleURL = getClass().getResource(articleURLString);
            ((HtmlBrowserWithScrollPosition)solutionBrowser).setURL(
                articleURL);
        }
    }
}
