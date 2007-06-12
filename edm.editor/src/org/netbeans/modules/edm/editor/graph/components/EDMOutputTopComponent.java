/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.edm.editor.graph.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.sql.framework.evaluators.database.DB;
import org.netbeans.modules.sql.framework.evaluators.database.DBFactory;
import org.netbeans.modules.sql.framework.evaluators.database.StatementContext;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.model.SQLGroupBy;
import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.TargetTable;
import org.netbeans.modules.sql.framework.ui.view.DataOutputPanel;
import org.netbeans.modules.sql.framework.ui.view.SQLLogView;
import org.netbeans.modules.sql.framework.ui.view.SQLStatementPanel;
import com.sun.sql.framework.exception.BaseException;
import com.sun.sql.framework.jdbc.DBConstants;

/**
 * Top component which displays something.
 */
public final class EDMOutputTopComponent extends TopComponent {
    
    private static EDMOutputTopComponent instance;
    
    private static SQLLogView logView;
    
    private static SQLStatementPanel statementPanel;
    /** path to the icon used by the component and its open action */
    private static final String ICON_PATH = "org/netbeans/modules/edm/editor/resources/mashup.png";
    
    private static final String PREFERRED_ID = "EDMOutputTopComponent";
    
    private EDMOutputTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(EDMOutputTopComponent.class, "CTL_EDMOutputTopComponent"));
        setToolTipText(NbBundle.getMessage(EDMOutputTopComponent.class, "HINT_EDMOutputTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized EDMOutputTopComponent getDefault() {
        if (instance == null) {
            instance = new EDMOutputTopComponent();
            logView = new SQLLogView();
        }
        return instance;
    }
    
    /**
     * Obtain the EDMOutputTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized EDMOutputTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(EDMOutputTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof EDMOutputTopComponent) {
            return (EDMOutputTopComponent)win;
        }
        Logger.getLogger(EDMOutputTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public void generateOutput(SQLObject obj, SQLDefinition defn) {
        DataOutputPanel outputPanel = null;
        switch(obj.getObjectType()) {
        case SQLConstants.SOURCE_TABLE:
            outputPanel = new DataOutputPanel.SourceQuery((SourceTable) obj, defn);
            break;
        case SQLConstants.TARGET_TABLE:
            outputPanel = new DataOutputPanel.TargetQuery((TargetTable) obj, defn);
            break;
        case SQLConstants.JOIN_VIEW:
            outputPanel = new DataOutputPanel.JoinViewQuery((SQLJoinView) obj, defn);
            break;
        case SQLConstants.JOIN:
            outputPanel = new DataOutputPanel.JoinOperatorQuery((SQLJoinOperator) obj, defn);
        }
        if(outputPanel != null) {
            outputPanel.generateResult(obj);
            addComponent(outputPanel);
        }
    }
    
    public void setLog(String msg) {
        logView.appendToView(msg + "\n");
        logView.appendToView("Logged at " + getTime() + "\n\n");
        addComponent(logView);
    }
    
    public void showSql(SQLObject obj, MashupDataObject mObj) {
        if(statementPanel == null) {
            statementPanel = new SQLStatementPanel(mObj.getEditorView(), obj);
        }
        StringBuilder buf = null;
        try {
            DB db = DBFactory.getInstance().getDatabase(DBConstants.AXION);
            StatementContext context = new StatementContext();
            context.setUsingOriginalSourceTableName(true);
            context.setUseSourceColumnAliasName(false);
            if(obj instanceof SourceTable) {
                buf = new StringBuilder(db.getStatements().
                        getSelectStatement((SourceTable) obj, context).getSQL());
            } else if(obj instanceof SQLJoinView) {
                SQLGroupBy grpby = ((SQLJoinView)obj).getSQLGroupBy();
                ((SQLJoinView)obj).setSQLGroupBy(null);
                buf = new StringBuilder(db.getStatements().
                        getSelectStatement((SQLJoinView)obj, context).getSQL());
                ((SQLJoinView)obj).setSQLGroupBy(grpby);
            } else if(obj instanceof SQLJoinOperator) {
                buf = new StringBuilder(db.getStatements().
                        getSelectStatement((SQLJoinOperator)obj, context).getSQL());
            } else if(obj instanceof SQLGroupBy) {
                SQLObject parent = (SQLObject) ((SQLGroupBy)obj).getParentObject();
                if(parent instanceof SQLJoinView) {
                    buf = new StringBuilder(db.getStatements().
                            getSelectStatement((SQLJoinView)parent, context).getSQL());
                } else if(parent instanceof SourceTable) {
                    buf = new StringBuilder(db.getStatements().
                            getSelectStatement((SourceTable)parent, context).getSQL());
                } else if(parent instanceof TargetTable) {
                    buf = new StringBuilder(db.getStatements().
                            getSelectStatement((TargetTable)parent, context).getSQL());
                }
            } else {
                buf = new StringBuilder(db.getEvaluatorFactory().evaluate(obj, context));
            }
        } catch (BaseException ex) {
            Exceptions.printStackTrace(ex);
        }
        statementPanel.clearView();
        statementPanel.appendToView(buf.toString());
        addComponent(statementPanel);
    }
    
    public void addComponent(Component comp) {
        removeAll();
        setLayout(new BorderLayout());
        add(comp, BorderLayout.CENTER);
        revalidate();
    }
    
    private String getTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String format = "yyyy-MM-dd HH:mm:ss";
        java.text.SimpleDateFormat dataFormat =
                new java.text.SimpleDateFormat(format);
        dataFormat.setTimeZone(TimeZone.getDefault());
        return dataFormat.format(calendar.getTime());
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return EDMOutputTopComponent.getDefault();
        }
    }
    
}
