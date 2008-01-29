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

package org.netbeans.modules.clearcase.ui.status;

import javax.swing.SwingUtilities;
import org.openide.util.*;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.versioning.spi.VCSContext;

/**
 * Top component of the Versioning view.
 * 
 * @author Maros Sandor
 */
public class VersioningTopComponent extends TopComponent implements Externalizable {
   
    private static final long serialVersionUID = 1L;    
    
    private VersioningPanel         syncPanel;
    private VCSContext              context;
    private String                  contentTitle;
    private String                  branchTitle;
    private long                    lastUpdateTimestamp;
    
    private static VersioningTopComponent instance;

    
    public VersioningTopComponent() {
        
        putClientProperty("SlidingName", NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_Title")); //NOI18N
        
        setName(NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_Title")); // NOI18N
        setToolTipText(NbBundle.getMessage(VersioningTopComponent.class, "HINT_VersioningTopComponent"));
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/clearcase/resources/icons/versioning-view.png"));  // NOI18N
        setLayout(new BorderLayout());
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_Title"));
        syncPanel = new VersioningPanel(this);
        add(syncPanel);
    }

    /**
     * Obtain the VersioningTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized VersioningTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(VersioningTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof VersioningTopComponent) {
            return (VersioningTopComponent) win;
        }
        Logger.getLogger(VersioningTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(getClass());
    }

    protected void componentActivated() {
        updateTitle();
        syncPanel.focus();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(context);
        out.writeObject(contentTitle);
        out.writeLong(lastUpdateTimestamp);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        context = (VCSContext) in.readObject();
        contentTitle = (String) in.readObject();
        lastUpdateTimestamp = in.readLong();
        syncPanel.deserialize();
    }

    protected void componentOpened() {
        super.componentOpened();
        refreshContent();
    }

    protected void componentClosed() {
        super.componentClosed();
    }

    private void refreshContent() {
        if (syncPanel == null) return;  // the component is not showing => nothing to refresh
        updateTitle();
        syncPanel.setContext(context);        
    }

    /**
     * Sets the 'content' portion of Versioning component title.
     * Title pattern: Versioning[ - contentTitle[ - branchTitle]] (10 minutes ago)
     * 
     * @param contentTitle a new content title, e.g. "2 projects"
     */ 
    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
        updateTitle();
    }

    /**
     * Sets the 'branch' portion of Versioning component title.
     * Title pattern: Versioning[ - contentTitle[ - branchTitle]] (10 minutes ago)
     * 
     * @param branchTitle a new content title, e.g. "release40" branch
     */ 
    void setBranchTitle(String branchTitle) {
        this.branchTitle = branchTitle;
        updateTitle();
    }
    
    public void contentRefreshed() {
        lastUpdateTimestamp = System.currentTimeMillis();
        updateTitle();
    }
    
    private void updateTitle() {
        final String age = computeAge(System.currentTimeMillis() - lastUpdateTimestamp);
        SwingUtilities.invokeLater(new Runnable (){
            public void run() {
                if (contentTitle == null) {
                    setName(NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_Title")); // NOI18N
                } else {
                    if (branchTitle == null) {
                        setName(NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_MultiTitle", contentTitle, age)); // NOI18N
                    } else {
                        setName(NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_Title_ContentBranch", contentTitle, branchTitle, age)); // NOI18N
                    }
                }                
            }
        });
    }

    String getContentTitle() {
        return contentTitle;
    }

    private String computeAge(long l) {
        if (lastUpdateTimestamp == 0) {
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeUnknown"); // NOI18N
        } else if (l < 1000) { // 1000 equals 1 second
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeCurrent"); // NOI18N
        } else if (l < 2000) { // age between 1 and 2 seconds
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeOneSecond"); // NOI18N
        } else if (l < 60000) { // 60000 equals 1 minute
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeSeconds", Long.toString(l / 1000)); // NOI18N
        } else if (l < 120000) { // age between 1 and 2 minutes
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeOneMinute"); // NOI18N
        } else if (l < 3600000) { // 3600000 equals 1 hour
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeMinutes", Long.toString(l / 60000)); // NOI18N
        } else if (l < 7200000) { // age between 1 and 2 hours
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeOneHour"); // NOI18N
        } else if (l < 86400000) { // 86400000 equals 1 day
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeHours", Long.toString(l / 3600000)); // NOI18N
        } else if (l < 172800000) { // age between 1 and 2 days
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeOneDay"); // NOI18N
        } else {
            return NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_AgeDays", Long.toString(l / 86400000)); // NOI18N
        }
    }

    public static synchronized VersioningTopComponent getInstance() {
        if (instance == null) {
            instance = (VersioningTopComponent) WindowManager.getDefault().findTopComponent(PREFERRED_ID); // NOI18N
            if (instance == null) {
                Clearcase.LOG.log(Level.INFO, null, new IllegalStateException("Can not find Versioning component")); // NOI18N
                instance = new VersioningTopComponent();
            }
        }
    
        return instance;
    }

    public Object readResolve() {
        return getInstance();
    }

    /**
     * Programmatically invokes the Refresh action.
     */ 
    public void performRefreshAction() {
        syncPanel.performRefreshAction();
    }

    /**
     * Sets files/folders the user wants to synchronize. They are typically activated (selected) nodes.
     * 
     * @param ctx new context of the Versioning view
     */
    public void setContext(VCSContext ctx) {
        syncPanel.cancelRefresh();
        if (ctx == null) {
            setName(NbBundle.getMessage(VersioningTopComponent.class, "MSG_Preparing")); // NOI18N
            setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
            context = ctx;
            setBranchTitle(null);
            refreshContent();
        }
        setToolTipText(getContextFilesList(ctx, NbBundle.getMessage(VersioningTopComponent.class, "CTL_Versioning_TopComponent_Title"))); // NOI18N            
    }
    
    private String getContextFilesList(VCSContext ctx, String def) {
        if (ctx == null || ctx.getRootFiles().size() == 0) return def;
        StringBuffer sb = new StringBuffer(200);
        sb.append("<html>"); // NOI18N
        for (File file : ctx.getRootFiles()) {
            sb.append(file.getAbsolutePath());
            sb.append("<br>"); // NOI18N
        }
        sb.delete(sb.length() - 4, Integer.MAX_VALUE);
        return sb.toString();
    }

    /** Tests whether it shows some content. */
    public boolean hasContext() {
        return context != null && context.getRootFiles().size() > 0;
    }

    protected String preferredID() {
        return PREFERRED_ID;    // NOI18N       
    }

    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";

    private static final String PREFERRED_ID = "VersioningTopComponent";




    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized VersioningTopComponent getDefault() {
        if (instance == null) {
            instance = new VersioningTopComponent();
        }
        return instance;
    }



    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return VersioningTopComponent.getDefault();
        }
    }
    
}
