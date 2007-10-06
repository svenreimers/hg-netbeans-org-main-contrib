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

package org.netbeans.modules.tasklist.bugs.issuezilla;

import java.util.LinkedList;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

import org.netbeans.modules.tasklist.core.TaskListView;

import org.netbeans.modules.tasklist.bugs.*;
import org.netbeans.modules.tasklist.bugs.bugzilla.BugzillaQueryPanel;
import org.netbeans.modules.tasklist.bugs.bugzilla.SourcePanel;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;

import javax.swing.*;

/**
 * Bridge which provides Issuezilla data to the BugList
 *
 * @author Tor Norbye
 */
public class IZBugEngine implements BugEngine { // XXX remove the publicness

    public IZBugEngine() {
    }

    /**
     * Return the user name of the engine
     */
    public String getName() {
        return (NbBundle.getMessage(IZBugEngine.class, "IssueZilla")); // NOI18N;
    }

    public void refresh(final BugQuery query, final BugList list) {
        // Do in the background
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                doRefresh(query, list);
            }
        });
    }

    public JComponent getQueryCustomizer(BugQuery query, boolean edit) {
        return new SourcePanel(true);
    }

    public void doRefresh(BugQuery inQuery, BugList list) {
        TaskListView v = TaskListView.getCurrent();
        BugsView view = null;
        if (v instanceof BugsView) {
            view = (BugsView) v;
            view.setCursor(Utilities.createProgressCursor(view));
        }
        try {

            // Do a bug query
            String query = null;
            String baseurl = null;
//        query = System.getProperty("netbeans.tasklist.bugquery");
//        if (query == null) {
//            // TEMPORARY HACK     TODO
//            TopManager.getDefault().notify(new NotifyDescriptor.Message("Tasklist bug summary:\nAdd -J-Dnetbeans.tasklist.bugquery=http://your.bug.url?<query>\nto your runide.sh startup arguments. (This is\nobviously only a temporary hack solution until\nI've added a query customizer.)\n\nTo determine what query to use, go to issuezilla:\n   http://www.netbeans.org/issues/query.cgi\nand create a custom query. Then, add that query as <query> above.\n\nAnd don't forget to make sure to set your\nproxies if you're behind a firewall! You can do that through Tools -> Setup Wizard."));
//            return;
//        } else {
//            //get the baseurl
//            int index = query.indexOf("?");
//            if (index != -1) {
//                baseurl = query.substring(0, index + 1);
//                query = query.substring(index + 1);
//            } else {
//                //we have to have the URL
//                TopManager.getDefault().notify(new NotifyDescriptor.Message("Tasklist bug summary:\nAdd -J-Dnetbeans.tasklist.bugquery=You must have the full URL for this query (Ex. http://www.netbeans.org/issues/buglist.cgi?<query>"));
//            }
//        }
            baseurl = inQuery.getBaseUrl() + "/buglist.cgi?";
            query = inQuery.getQueryString();

            if ((baseurl == null || baseurl.equals("")) || (query == null || query.equals(""))) {
                //They didn't enter anything on the gui
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(IZBugEngine.class,
                        "BadQuery")); // NOI18N
                return;
            }

            //String query= "issue_type=DEFECT&component=projects&issue_status=UNCONFIRMED&issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&version=4.0+dev&email1=&emailtype1=substring&emailassigned_to1=1&email2=&emailtype2=substring&emailreporter2=1&issueidtype=include&issue_id=&changedin=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&short_desc=&short_desc_type=substring&long_desc=&long_desc_type=substring&issue_file_loc=&issue_file_loc_type=substring&status_whiteboard=&status_whiteboard_type=substring&keywords=&keywords_type=anywords&field0-0-0=noop&type0-0-0=noop&value0-0-0=&cmdtype=doit&newqueryname=&order=Reuse+same+sort+as+last+time";


            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(IZBugEngine.class,
                    "Refreshing")); // NOI18N
            URL url = null;
            try {
                url = new URL(baseurl);
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(e);
            }
            if (url != null) {
                Issuezilla iz = new Issuezilla(url);
                try {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(IZBugEngine.class,
                            "DoingQuery")); // NOI18N

                    int bugids[] = iz.query(query);

                    // Provide some update

                    int n = bugids.length;
                    LinkedList issues = new LinkedList();
                    for (int i = 0; i < n; i++) {
                        StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(IZBugEngine.class,
                                "QueryingBug"), // NOI18N
                                new String[]{Integer.toString(bugids[i])}));

                        Issue izbug = iz.getBug(bugids[i]);

                        Bug bug = new Bug(Integer.toString(izbug.getId()),
                                izbug.getSummary(),
                                izbug.getPriority(),
                                izbug.getType(),
                                izbug.getComponent(),
                                izbug.getSubcomponent(),
                                izbug.getCreated(),
                                izbug.getKeywords(),
                                izbug.getAssignedTo(),
                                izbug.getReportedBy(),
                                izbug.getStatus(),
                                izbug.getTargetMilestone(),
                                izbug.getVotes());
                        bug.setEngine(this);

                        issues.add(bug);
                    }

                    // Successful list fetch -- replace the contents
                    list.setBugs(issues);
                } catch (org.xml.sax.SAXException se) {
                    ErrorManager.getDefault().notify(se);
                    System.out.println("Couldn't read bug list: sax exception");
                } catch (java.net.UnknownHostException uhe) {
                    StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(IZBugEngine.class,
                            "NoNet"), // NOI18N
                            new String[]{baseurl}));

                } catch (java.io.IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                    System.out.println("Couldn't read bug list: io exception");
                }
                StatusDisplayer.getDefault().setStatusText("");
            }

        } finally {
            if (view != null) {
                view.setCursor(null);
            }
        }
    }

    /**
     * View a particular bug.
     */
    public void viewBug(Bug bug, String service) {
//	String urlstring = "http://www.netbeans.org/issues/show_bug.cgi?id=" +
//	    bug.getId();
        // Show URL
        try {
            URL url = new URL(new URL(service), "issues/show_bug.cgi?id=" + bug.getId());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}
