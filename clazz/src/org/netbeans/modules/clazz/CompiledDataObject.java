/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.applet.Applet;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import javax.swing.JApplet;
import javax.swing.JButton;

import org.openide.*;
import org.openide.util.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.execution.Executor;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.src.SourceElement;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.SourceElementFilter;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.ElementNodeFactory;
import org.netbeans.modules.classfile.ClassFile;

/* TODO:
  - check the showDeclaredOnly flag - it works different for
    variables/constructors than for methods (i.e. for variables/constructors
    the declaredOnly are not subset of notDecalredOnly
*/

/**
* DataObject which represents .class files.
*
* @author Jan Jancura, Ian Formanek, Petr Hamernik, Dafe Simonek
*/
public class CompiledDataObject extends ClassDataObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -7355104884002106137L;

    /** Name of arguments property. */
    private final static String  PROP_ARGV = "Arguments"; // NOI18N
    /** Name of execution property. */
    private final static String  PROP_EXECUTION = "Execution"; // NOI18N

    // variables ...................................................................................

    /** Support for executing the class */
    transient protected ExecSupport execSupport;

    // constructors ...................................................................................

    /** Constructs a new ClassDataObject */
    public CompiledDataObject(final FileObject fo,final ClassDataLoader loader) throws org.openide.loaders.DataObjectExistsException {
        super (fo, loader);
        initCookies();
    }
    
    /** Performs cookie initialization. */
    protected void initCookies () {
        super.initCookies();

        CookieSet cs = getCookieSet();
        // only JavaBeans should offer `Customize Bean' action
        cs.add(InstanceCookie.Origin.class, this);
        cs.add(ExecSupport.class, this);
    }
    
    protected ExecSupport createExecSupport() {
        if (execSupport != null)
            return execSupport;
        synchronized (this) {
            if (execSupport == null)
                execSupport = new ExecSupport(getPrimaryEntry());
        }
        return execSupport;
    }
    
    protected Node.Cookie createBeanInstanceSupport() {
	if (isJavaBean()) {
	    return createInstanceSupport();
	} else {
	    return null;
	}
    }
    
    public Node.Cookie createCookie(Class c) {
        if (ExecCookie.class.isAssignableFrom(c)) {
            return createExecSupport();
        } else if (InstanceCookie.class.isAssignableFrom(c)) {
	    return createBeanInstanceSupport();
	}
        return super.createCookie(c);
    }

    // DataObject implementation .............................................

    /** Getter for copy action.
    * @return true if the object can be copied
    */
    public boolean isCopyAllowed () {
        boolean isSerializable = false;
        try {
            isSerializable = Serializable.class.isAssignableFrom(createInstanceSupport().instanceClass());
        } catch (Exception exc) {
            // don't allow copying if some error appeared
            // during serializability test
        }
        return isJavaBean () && isSerializable;
    }

    /** Class DO cannot be moved.
    * @return false
    */
    public boolean isMoveAllowed () {
        return false;
    }

    /** Class DO cannot be renamed.
    * @return false
    */
    public boolean isRenameAllowed () {
        return false;
    }

    /** Copies this object to a folder. The copy of the object is required to
    * be deletable and movable.
    *
    * @param f the folder to copy object to
    * @exception IOException if something went wrong
    * @return the new object
    */
    protected DataObject handleCopy (DataFolder f) throws IOException {
        String newName = existInFolder (f);
        Object bean;
        try {
            bean = createInstanceSupport().instanceCreate();
        } catch (ClassNotFoundException ex) {
            throw new IOException (ex.toString ());
        }
        if (bean == null) throw new IOException ();
        FileObject serFile = f.getPrimaryFile ().createData (newName, "ser"); // NOI18N
        FileLock lock = null;
        ObjectOutputStream oos = null;
        try {
            lock = serFile.lock ();
            oos = new ObjectOutputStream (serFile.getOutputStream (lock));
            oos.writeObject (bean);
        }
        finally {
            if (lock != null)
                lock.releaseLock ();
            if (oos != null)
                oos.close ();
        }
        return DataObject.find (serFile);
    }
    
    /**
    * @return class data node
    */
    protected Node createNodeDelegate () {
        return new CompiledDataNode (this);
    }

    // Properties implementation .....................................................................

    boolean isExecutable () {
        return createInstanceSupport().isExecutable ();
    }

    // other methods ..............................................................................

    /** Check if in specific folder exists .ser fileobject with the same name.
    * If it exists user is asked for confirmation to rewrite, rename or
    * cancel operation. Throws UserCancelException if user pressed cancel
    * button.
    * @param f destination folder
    * @return new Name of file in destination
    */
    protected String existInFolder(DataFolder f) throws UserCancelException {
        FileObject fo = getPrimaryFile();
        String name = fo.getName();
        String ext = "ser"; // NOI18N
        String destName = fo.getName();
        if (f.getPrimaryFile().getFileObject(name, ext) != null) {
            // file with the same name exists - ask user what to do
            ResourceBundle bundle = NbBundle.getBundle(ClassDataObject.class);
            String rewriteStr = bundle.getString("CTL_Rewrite");
            String renameStr = bundle.getString("CTL_Rename");
            String cancelStr = bundle.getString("CTL_Cancel");
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                                      new MessageFormat(bundle.getString("MSG_SerExists")).
                                      format(new Object[] { name, f.getName() }));
            nd.setOptions(new Object[] { rewriteStr, renameStr, cancelStr });
            String retStr = (String)TopManager.getDefault().notify(nd);
            if (cancelStr.equals(retStr)) // user cancelled the dialog
                throw new UserCancelException();
            if (renameStr.equals(retStr))
                destName = FileUtil.findFreeFileName (
                               f.getPrimaryFile(), destName, ext);
            if (rewriteStr.equals(retStr)) {
                try {
                    FileObject dest = f.getPrimaryFile().getFileObject(name, ext);
                    FileLock lock = dest.lock();
                    dest.delete(lock);
                    lock.releaseLock();
                }
                catch (IOException e) {
                    return null;
                }
            }
        }
        return destName;
    }

    // innerclasses .......................................................

    /** The implementation of the source cookie.
    * Class data object cannot implement source cookie directly,
    * because it's optional (if there's no instance cookie, then also
    * no source cookie is supported)
    */
    private static final class SourceSupport extends Object
        implements SourceCookie {
        /** The class which acts as a source element data */
        private ClassFile data;
        /** Reference to outer class */
        private ClassDataObject cdo;

        /** Creates source support with asociated class object */
        SourceSupport (ClassFile data, ClassDataObject cdo) {
            this.data = data;
            this.cdo = cdo;
        }

        /** @return The source element for this class data object */
        public SourceElement getSource () {
            return new SourceElement(new SourceElementImpl(data, cdo));
        }

    } // the end of SourceSupport inner class
    
    private static class ExecSupport extends org.openide.loaders.ExecSupport {
        ExecSupport(MultiDataObject.Entry en) {
            super(en);
        }
        
        /**
         * Iterates through Execution service type, looking for some exec
         * service from the java module.
         */
        protected Executor defaultExecutor() {
            Enumeration servs = org.openide.TopManager.getDefault().getServices().
                services(Executor.class);
            while (servs.hasMoreElements()) {
                Object o = servs.nextElement();
                if (o.getClass().getName().startsWith(
                    "org.netbeans.modules.java.JavaProcessExecutor" // NOI18N
                    )) {
                    return (Executor)o;
                }
            }
            return super.defaultExecutor();
        }
    }
}

