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

package org.netbeans.modules.tasklist.usertasks.renderers;

import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;
import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;

/**
 * Renderer for the due date
 *
 * @author tl
 */
public class DueDateTableCellRenderer extends DateTableCellRenderer {
    private Font boldFont, normalFont;
    
    protected Duration getDuration(Object obj) {
        UserTask ut = (UserTask) obj;
        if (ut == null) {
            return null;
        } else {
            return new Duration(ut.getEffort(),
                Settings.getDefault().getMinutesPerDay(), 
                Settings.getDefault().getDaysPerWeek(), true);
        }
    }

    public Component getTableCellRendererComponent(
        JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        Object node = ((TreeTable) table).getNodeForRow(row);
        if (normalFont == null || !normalFont.equals(table.getFont())) {
            normalFont = table.getFont();
            boldFont = normalFont.deriveFont(Font.BOLD);
        }
        setForeground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        if (node instanceof UTBasicTreeTableNode) {
            UTBasicTreeTableNode n = (UTBasicTreeTableNode) node;
            UserTask ut = (UserTask) n.getUserTask();
            Date due = ut.getDueDate();
            boolean overdue = false;
            if (!ut.isDone()) {
                if (due != null &&
                    due.getTime() < System.currentTimeMillis())
                    overdue = true;
            } else {
                if (due != null &&
                        due.getTime() < ut.getCompletedDate())
                    overdue = true;
            }
            setFont(overdue ? boldFont : normalFont);
            if (!isSelected && overdue)
                setForeground(Color.RED);
        }

        return this;
    }
}
