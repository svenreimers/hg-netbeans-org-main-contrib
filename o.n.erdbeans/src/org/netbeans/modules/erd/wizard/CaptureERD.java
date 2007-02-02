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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erd.wizard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.*;
import org.openide.loaders.DataFolder;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.dbschema.jdbcimpl.ConnectionProvider;
import org.netbeans.modules.dbschema.*;
import org.netbeans.modules.dbschema.jdbcimpl.SchemaElementImpl;
import org.netbeans.modules.dbschema.jdbcimpl.wizard.DBSchemaWizardData;
import org.netbeans.modules.dbschema.jdbcimpl.wizard.ProgressFrame;
import org.netbeans.modules.erd.io.ERDContext;
import org.openide.ErrorManager;
import org.openide.util.Mutex;
import sun.jdbc.odbc.ee.ConnectionHandler;
import sun.net.www.protocol.http.Handler;


public class CaptureERD {
    static ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.dbschema.jdbcimpl.resources.Bundle"); //NOI18N
    
    private static final String defaultName = bundle.getString("DefaultSchemaName"); //NOI18N
    private ERDContext context;
    private boolean isAlreadyConnected;
    private DatabaseConnection dbconn;
    
    public CaptureERD(ERDContext context){
        this.context=context;
    }
    
    public void createSchemaFromConnection() {
        try {
            
            SwingUtilities.invokeAndWait(
                    new Runnable() {
                public void run(){
                    createSchemaFromConnectionInAWT();
                }
            }
            ) ;
            
        } catch(Exception e){
            
        }
    }
    
    
    private void createSchemaFromConnectionInAWT(){
        try {
            ConnectionProvider cp = createConnectionProvider();
            
             getSchema(cp);
        } catch(Exception exc){
            ErrorManager.getDefault().notify(exc);
        }
        
    }
    
    
    private void getSchema(final ConnectionProvider cp) throws Exception{
        try {
            final ConnectionProvider c = cp;
            if (c == null) {
                throw new SQLException(bundle.getString("EXC_ConnectionNotEstablished"));
            }
            Runnable mytask=new Runnable() {
                public void run() {
                    try {
                        StatusDisplayer.getDefault().setStatusText(bundle.getString("CreatingDatabaseSchema")); //NOI18N
                        
                        final ProgressFrame pf = new ProgressFrame();
                        final SchemaElementImpl sei = new SchemaElementImpl(c);
                        
                        PropertyChangeListener listener = new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent event) {
                                String message;
                                
                                if (event.getPropertyName().equals("totalCount")) { //NOI18N
                                    pf.setMaximum(((Integer)event.getNewValue()).intValue());
                                    return;
                                }
                                
                                if (event.getPropertyName().equals("progress")) { //NOI18N
                                    pf.setValue(((Integer)event.getNewValue()).intValue());
                                    return;
                                }
                                
                                if (event.getPropertyName().equals("tableName")) { //NOI18N
                                    message = MessageFormat.format(bundle.getString("CapturingTable"), new String[] {((String) event.getNewValue()).toUpperCase()}); //NOI18N
                                    pf.setMessage(message);
                                    return;
                                }
                                
                                if (event.getPropertyName().equals("FKt")) { //NOI18N
                                    message = MessageFormat.format(bundle.getString("CaptureFK"), new String[] {((String) event.getNewValue()).toUpperCase(), bundle.getString("CaptureFKtable")}); //NOI18N
                                    pf.setMessage(message);
                                    return;
                                }
                                
                                if (event.getPropertyName().equals("FKv")) { //NOI18N
                                    message = MessageFormat.format(bundle.getString("CaptureFK"), new String[] {((String) event.getNewValue()).toUpperCase(), bundle.getString("CaptureFKview")}); //NOI18N
                                    pf.setMessage(message);
                                    return;
                                }
                                
                                if (event.getPropertyName().equals("viewName")) { //NOI18N
                                    message = MessageFormat.format(bundle.getString("CapturingView"), new String[] {((String) event.getNewValue()).toUpperCase()}); //NOI18N
                                    pf.setMessage(message);
                                    return;
                                }
                                
                                if (event.getPropertyName().equals("cancel")) { //NOI18N
                                    sei.setStop(true);
                                    StatusDisplayer.getDefault().setStatusText(""); //NOI18N
                                    return;
                                }
                            }
                        };
                        
                        pf.propertySupport.addPropertyChangeListener(listener);
                        pf.setVisible(true);
                        
                        sei.propertySupport.addPropertyChangeListener(listener);
                        
                        
                        
                        SchemaElement schema=new SchemaElement(sei);
                        schema.setName(DBIdentifier.create("Identifier"));
                        sei.initTables(cp);
                        pf.finishProgress();
                        
                        if (! sei.isStop()) {
                            pf.setMessage(bundle.getString("SchemaSaved")); //NOI18N
                            StatusDisplayer.getDefault().setStatusText(bundle.getString("SchemaSaved")); //NOI18N
                            
                            pf.setVisible(false);
                            pf.dispose();
                        }
                        
                        schema.setSchema(DBIdentifier.create("Identifier"));
                        context.setSchemaElement(schema);
                        
                        //c.closeConnection();
                       if (isAlreadyConnected)
                            
                                ConnectionManager.getDefault().disconnect(dbconn);
                         else
                            cp.closeConnection();
                        
                    } catch (Exception exc) {
                        ErrorManager.getDefault().notify(exc);
                    }
                }
            };
            mytask.run();
        } catch (Exception exc) {
            String message = MessageFormat.format(bundle.getString("UnableToCreateSchema"), new String[] {exc.getMessage()}); //NOI18N
            StatusDisplayer.getDefault().setStatusText(message);
            
            
            try {
                if (cp != null)
                    cp.closeConnection();
               
                if (isAlreadyConnected)
                    ConnectionManager.getDefault().disconnect(dbconn);
                
            } catch (Exception exc1) {
                
            }
        }
    }
    
    
    private ConnectionProvider createConnectionProvider() throws SQLException ,InterruptedException,Exception{
        
        dbconn = findDatabaseConnection(context.getDataSourceUrl());
        if (dbconn == null) {
            return null;
        }
        if (ensureDatabaseConnection()) {
            ConnectionProvider connectionProvider =
                    new ConnectionProvider(dbconn.getJDBCConnection(), dbconn.getDriverClass());
            connectionProvider.setSchema(dbconn.getSchema());
            
            return connectionProvider;
        }
        
        return null;
    }
    
    private DatabaseConnection findDatabaseConnection(String url) {
        DatabaseConnection dbconns[] = ConnectionManager.getDefault().getConnections();
        for (int i = 0; i < dbconns.length; i++) {
            if (url.equals(dbconns[i].getDatabaseURL())) {
                return dbconns[i];
            }
        }
        return null;
    }
    
    
    
    
    
    private boolean ensureDatabaseConnection() {
        try {
            return init(dbconn);
        } catch(Exception e){
            return false;
        }
    }
    
    private boolean init(DatabaseConnection dbconn) {
        
        if(dbconn.getJDBCConnection()==null){
            ConnectionManager.getDefault().showConnectionDialog(dbconn);
        }
        else {
            isAlreadyConnected=true;
        }
        Connection conn = dbconn.getJDBCConnection();
        try {
            conn.getCatalog();
        } catch (SQLException exc) {
            conn = null;
            return false;
        }
        return true;
    }
    
    
    
}
