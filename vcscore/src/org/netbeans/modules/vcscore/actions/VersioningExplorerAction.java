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

package org.netbeans.modules.vcscore.actions;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.explorer.ExplorerPanel;
import org.openide.nodes.Node;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.ErrorManager;

import javax.swing.*;

//import org.netbeans.modules.vcscore.versioning.VcsFileObject;

/**
 * This action openes the Versioning Explorer tab.
 *
 * @author  Martin Entlicher
 */
public class VersioningExplorerAction extends GeneralCommandAction {

    private static final long serialVersionUID = -4949229720968764504L;
    protected static boolean nodeFound = false;
    
    /** Creates new VersioningExplorerAction */
    public VersioningExplorerAction() {
    }

    public String getName() {
        return org.openide.util.NbBundle.getMessage(VersioningExplorerAction.class, "LBL_VersioningExplorer");
    }
    
    protected String iconResource () {
        return "org/netbeans/modules/vcscore/versioning/impl/versioning.png";
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction(Node[] nodes) {
        final VersioningExplorer.Panel explorer = VersioningExplorer.getRevisionExplorer();
        explorer.open();
        final HashMap filesByFS = getFilesByFS(nodes);
        if (Boolean.getBoolean("netbeans.vcsdebug")) {
            System.out.println("\nVersioningExplorer.performAction()");
            //System.out.println("SELECTED NODES = "+(new java.util.HashSet(java.util.Arrays.asList(explorer.getExplorerManager().getSelectedNodes()))));
            System.out.println("SELECTED NODES = "+(new java.util.HashSet(java.util.Arrays.asList(nodes))));
            System.out.println("FILES BY FS (size = "+filesByFS.size()+"):");
            for (Iterator it = filesByFS.keySet().iterator(); it.hasNext(); ) {
                String foPath = (String) it.next();
                String fsSystemName = (String) filesByFS.get(foPath);
                System.out.println("  file '"+foPath+"' from '"+fsSystemName+"'");
            }
        //explorer.setActivatedNodes(getVersioningNodes(filesByFS));
            VersioningRepository repository = VersioningRepository.getRepository();
            java.util.List vfsl = repository.getVersioningFileSystems();
            System.out.println("\nList of versioning filesystems (size = "+vfsl.size()+"):");
            for (Iterator it = vfsl.iterator(); it.hasNext(); ) {
                VersioningFileSystem vfs = (VersioningFileSystem) it.next();
                System.out.println(vfs+" with system name = '"+vfs.getSystemName()+"'");
            }
        }
        explorer.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        RequestProcessor.getDefault().post(new Runnable(){
        public void run(){
            selectVersioningFiles(explorer,filesByFS);
            }
        });
        if(nodeFound)
            StatusDisplayer.getDefault().setStatusText("");
        else
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(VersioningExplorerAction.class, "MSG_NodeNotFound"));
        explorer.requestActive();        
    }

    /**
     * Asynchronously opens explorer selecting passed file object.
     * @return Task.EMPTY on failure.
     */
    public final Task showFileObject(FileObject fo) {
        FileObject nativeFileObject = (FileObject) fo.getAttribute(VcsAttributes.VCS_NATIVE_FILEOBJECT);
        if (nativeFileObject != null) fo = nativeFileObject;
        final VersioningExplorer.Panel explorer = VersioningExplorer.getRevisionExplorer();
        explorer.open();
        explorer.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        final Map filesByFS = new  HashMap();
        try {
            filesByFS.put(fo.getPath(), fo.getFileSystem().getSystemName());
            return RequestProcessor.getDefault().post(new Runnable(){
                public void run() {
                    selectVersioningFiles(explorer,filesByFS);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            explorer.requestActive();
                        }
                    });
                }
            });
        } catch (FileStateInvalidException e) {
            ErrorManager err = ErrorManager.getDefault();
            err.notify(e);
        }
        return Task.EMPTY;
    }

    private HashMap getFilesByFS(Node[] nodes) {
        HashMap filesByFS = new HashMap();
        HashMap map = getSupporterMap(nodes);
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            Set foSet = (Set)it.next();
            FileObject[] origFos = new FileObject[foSet.size()];
            if (origFos == null || origFos.length == 0) {
                continue;
            }
            origFos = (FileObject[])foSet.toArray(origFos);
            FileObject[] correctFos = VcsUtilities.convertFileObjects(origFos);
            for (int i = 0; i < correctFos.length; i++) {
                FileObject fo = correctFos[i];
                try {
                    FileSystem fs = fo.getFileSystem();
                    if (fs != null) {
                        filesByFS.put(fo.getPath(), fs.getSystemName());
                    }
                } catch (FileStateInvalidException exc) {
                    continue;
                }
            }
        }
        return filesByFS;
    }
    
    private static void selectVersioningFiles(final ExplorerPanel explorer, final Map filesByFS) {
        VersioningRepository repository = VersioningRepository.getRepository();
        final org.openide.explorer.ExplorerManager manager = explorer.getExplorerManager();
        final LinkedList nodes = new LinkedList();
        for (Iterator it = filesByFS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fileName = (String) entry.getKey();
            String fsName = (String) entry.getValue();
            VersioningFileSystem vs = repository.getSystem(fsName);
            if (Boolean.getBoolean("netbeans.vcsdebug")) {
                System.out.println("Versioning FS of name '"+fsName+"' = "+vs);
            }
            if (vs != null) {
                FileObject fo = vs.getRoot().getFileObject(fileName);
                if (Boolean.getBoolean("netbeans.vcsdebug")) {
                    System.out.println("  resource of name '"+fileName+"' = "+fo);
                }
                if (fo != null) {
                    //try {
                        //Node versioningRoot = org.netbeans.modules.vcscore.versioning.impl.VersioningDataSystem.getVersioningDataSystem();
                        Node fsRoot = manager.getRootContext().getChildren().findChild(vs.getSystemName());
                        if (Boolean.getBoolean("netbeans.vcsdebug")) {
                            System.out.println("  filesystem root = "+fsRoot);
                        }
                        nodes.add(selectVersioningFile(explorer, fsRoot, fileName));
                        //nodes.add(DataObject.find(fo).getNodeDelegate());
                    //} catch (DataObjectNotFoundException exc) {}
                }
            }
        }
        final Node[] nodeArray = (Node[]) nodes.toArray(new Node[nodes.size()]);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < nodeArray.length; i++) {
                    manager.setExploredContext(nodeArray[i]);
                }
                //explorer.setActivatedNodes((Node[]) nodes.toArray(new Node[nodes.size()]));
                try {
                    manager.setSelectedNodes(nodeArray);
                    nodeFound = true;
                } catch (java.beans.PropertyVetoException exc) {
                }
                explorer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        //return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }
    
    private static Node selectVersioningFile(ExplorerPanel explorer, Node root, String fileName) {
        StringTokenizer files = new StringTokenizer(fileName, "/");
        Node node = root;
        while (files.hasMoreTokens()) {
            String file = files.nextToken();
            Node subNode = node.getChildren().findChild(file);
            if (subNode != null) node = subNode;
            else break;
        }
        //System.out.println("setting explored context to "+node);
        //explorer.getExplorerManager().setExploredContext(node);
        if (Boolean.getBoolean("netbeans.vcsdebug")) {
            System.out.println("  node for versioning file '"+fileName+"' = "+node);
        }
        return node;
    }
    
}
