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

package org.netbeans.modules.tasklist.usertasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.fortuna.ical4j.data.ParserException;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.netbeans.modules.tasklist.usertasks.util.AWTThread;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * Represents a tasklist object in the Repository.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class TaskListDataObject extends MultiDataObject implements OpenCookie,
ChangeListener {
    private static final long serialVersionUID = 1;

    private UserTaskList utl;
    
    /**
     * Constructor.
     * 
     * @param pf .ics file
     * @param loader TaskListLoader
     * @throws org.openide.loaders.DataObjectExistsException 
     */
    public TaskListDataObject(FileObject pf, TaskListLoader loader)
            throws DataObjectExistsException {
	super(pf, loader);
    	CookieSet cookies = getCookieSet();
	cookies.add(this); // OpenCookie
    }

    /**
     * Releases internal reference to the UserTaskList object.
     */
    public void release() {
        if (utl != null) {
            utl.removeChangeListener(this);
            utl = null;
        }
    }

    /**
     * Returns the task list contained in the file.
     * 
     * @return task list 
     */
    @AWTThread
    public UserTaskList getUserTaskList() throws IOException {
        if (utl == null) {
            utl = readDocument(getPrimaryFile());
            utl.addChangeListener(this);
        }
        return utl;
    }
    
    @Override
    public void setModified(boolean modif) {
        firePropertyChange(TaskListDataObject.PROP_COOKIE, null, null);
        super.setModified(modif);
    }
    
    protected Node createNodeDelegate() {
	return new TaskListDataNode(this);
    }

    // Implements OpenCookie    
    public void open() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                open_();
            }
        });
    }
    
    /**
     * Opens the TC in the Swing thread
     */
    private void open_() {
	UserTaskView view = UserTaskViewRegistry.getInstance().
                findView(getPrimaryEntry().getFile());
        if (view == null) {
            FileObject fo = getPrimaryEntry().getFile();
            view = new UserTaskView(fo, false);
            view.showInMode();
        } else {
            // This view already exists, show it...
            view.showInMode();
        }
    }   

    @Override
    public <T extends Node.Cookie> T getCookie(Class<T> c) {
        if (c == SaveCookie.class && isModified())
            return (T) new UTSaveCookie(this, utl);
        else
            return super.getCookie(c);
    }

    /**
     * Reads an ics file. Shows error messages if it cannot be read.
     *
     * @param fo an .ics file
     */
    @AWTThread
    private static UserTaskList readDocument(FileObject fo) throws IOException {
        if (!fo.isValid()) 
            throw new IOException(
                NbBundle.getMessage(UserTaskList.class,
                    "FileNotValid", FileUtil.getFileDisplayName(fo))); // NOI18N
        InputStream is = fo.getInputStream();
        UserTaskList ret = null;
        try {
            long m = System.currentTimeMillis();
            ICalImportFormat io = new ICalImportFormat();

            ret = new UserTaskList();
            try {
                io.read(ret, is);
            } catch (ParserException e) {
                // NOTE the exception text should be localized!
                DialogDisplayer.getDefault().notify(new Message(e.getMessage(),
                        NotifyDescriptor.ERROR_MESSAGE));
                UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            } catch (IOException e) {
                // NOTE the exception text should be localized!
                DialogDisplayer.getDefault().notify(new Message(e.getMessage(),
                        NotifyDescriptor.ERROR_MESSAGE));
                UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            }

            UTUtils.LOGGER.fine("File " + fo + " read in " + // NOI18N
                    (System.currentTimeMillis() - m) + "ms"); // NOI18N
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                UTUtils.LOGGER.log(Level.WARNING, 
                        "closing file failed", e); // NOI18N
            }
        }
        return ret;
    }

    public void stateChanged(ChangeEvent e) {
        setModified(true);
    }
}
