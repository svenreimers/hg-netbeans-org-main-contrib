/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.commands.RegexOutputCommand;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.openide.ErrorManager;

//import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionChildren;

/**
 * The VersioningSystem used by VcsFileSystem. VcsFileSystem contructs it
 * then adds it into RevisionRepository.
 *
 * @author  Martin Entlicher
 * @author  Petr Kuzel (encapsulation improvements)
 */
class VcsVersioningSystem extends VersioningFileSystem {

    private VcsFileSystem fileSystem;
    //private VersioningFileSystem.Status status;
    private VersioningFileSystem.Versions versions;
    //private FileStatusListener fileStatus;
    private Hashtable revisionListsByName;
    /** Holds value of property showMessages. */
    private boolean showMessages = true;
    /** Holds value of property showUnimportantFiles. */
    private boolean showUnimportantFiles = false;
    /** Holds value of property showLocalFiles. */
    private boolean showLocalFiles = true;
    
    private transient LocalFilenameFilter localFilenameFilter = null;
    
    /** Holds value of property messageLength. */
    private int messageLength = 50;    
    
    public static final String PROP_SHOW_DEAD_FILES = "showDeadFiles"; //NOI18N
    public static final String PROP_SHOW_MESSAGES = "showMessages"; //NOI18N
    public static final String PROP_MESSAGE_LENGTH = "messageLength"; //NOI18N
    public static final String PROP_SHOW_UNIMPORTANT_FILES = "showUnimportantFiles"; //NOI18N
    public static final String PROP_SHOW_LOCAL_FILES = "showLocalFiles"; // NOI18N

    /** Creates new VcsVersioningSystem */
    VcsVersioningSystem(VcsFileSystem fileSystem) {
        super(fileSystem);
        this.fileSystem = fileSystem;
        this.versions = new VersioningVersions();
        revisionListsByName = new Hashtable();
        localFilenameFilter = new LocalFilenameFilter();

        showMessages = ((Boolean)loadProperty(PROP_SHOW_MESSAGES, Boolean.valueOf(showMessages))).booleanValue();
        messageLength = ((Integer)loadProperty(PROP_MESSAGE_LENGTH, new Integer(messageLength))).intValue();
        showLocalFiles = ((Boolean)loadProperty(PROP_SHOW_LOCAL_FILES, Boolean.valueOf(showLocalFiles))).booleanValue();
        showUnimportantFiles = ((Boolean)loadProperty(PROP_SHOW_UNIMPORTANT_FILES, Boolean.valueOf(showUnimportantFiles))).booleanValue();
    }
    
    /**
     * Get the delegated file system.
     * Just to be able to access this method in this package.
     */
    protected FileSystem getFileSystem() {
        return fileSystem;
    }
    
    public VersioningFileSystem.Versions getVersions() {
        return versions;
    }
    
    public FileSystem.Status getStatus() {
        return fileSystem.getStatus();
    }        
    
    public boolean isShowDeadFiles() {
        return fileSystem.isShowDeadFiles();
    }

    public void setShowDeadFiles(boolean showDeadFiles) {
        fileSystem.setShowDeadFiles(showDeadFiles);
        firePropertyChange(PROP_SHOW_DEAD_FILES, !showDeadFiles ? Boolean.TRUE : Boolean.FALSE, showDeadFiles ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isShowUnimportantFiles() {
        return showUnimportantFiles;
    }
    
    public void setShowUnimportantFiles(boolean showUnimportantFiles) {
        if (this.showUnimportantFiles != showUnimportantFiles) {
            this.showUnimportantFiles = showUnimportantFiles;
            storeProperty(PROP_SHOW_UNIMPORTANT_FILES, Boolean.valueOf(showUnimportantFiles));
            firePropertyChange(PROP_SHOW_UNIMPORTANT_FILES, !showUnimportantFiles ? Boolean.TRUE : Boolean.FALSE, showUnimportantFiles ? Boolean.TRUE : Boolean.FALSE);
            refreshExistingFolders();
        }
    }

    public boolean isShowLocalFiles() {
        return showLocalFiles;
    }
    
    public void setShowLocalFiles(boolean showLocalFiles) {
        if (this.showLocalFiles != showLocalFiles) {
            this.showLocalFiles = showLocalFiles;
            storeProperty(PROP_SHOW_LOCAL_FILES, Boolean.valueOf(showLocalFiles));
            firePropertyChange(PROP_SHOW_LOCAL_FILES, !showLocalFiles ? Boolean.TRUE : Boolean.FALSE, showLocalFiles ? Boolean.TRUE : Boolean.FALSE);
            refreshExistingFolders();
        }
    }

    /** Getter for property showMessages.
     * @return Value of property showMessages.
     */
    public boolean isShowMessages() {
        return this.showMessages;
    }
    
    /** Setter for property showMessages.
     * @param showMessages New value of property showMessages.
     */
    public void setShowMessages(boolean showMessages) {
        if (this.showMessages != showMessages) {
            this.showMessages = showMessages;
            storeProperty(PROP_SHOW_MESSAGES, Boolean.valueOf(showMessages));
            firePropertyChange(PROP_SHOW_MESSAGES, !showMessages ? Boolean.TRUE : Boolean.FALSE, showMessages ? Boolean.TRUE : Boolean.FALSE);
            redisplayRevisions();
        }
    }
    
    private void redisplayRevisions() {
        Iterator it = this.revisionListsByName.values().iterator();
        while (it.hasNext()) {
            RevisionList list = (RevisionList)it.next();
            displayRevisions(list);
        }
    }
    
    private void displayRevisions(RevisionList list) {
        Iterator it2 = list.iterator();
        while (it2.hasNext()) {
            RevisionItem item = (RevisionItem)it2.next();
            if (item.isBranch()) continue;
            if (isShowMessages()) {
                String messageString = item.getMessage();
                item.setDisplayName(item.getRevisionVCS() + ((messageString != null) ? ("  " + cutMessageString(messageString)) : "")); //NOI18N
            } else {
                if (item.getMessage() != null) {
                    item.setDisplayName(item.getRevisionVCS());
                }
            }
        }
        
    }
    
    
    private String cutMessageString(String message) {
        String toReturn = message;
        if (message != null && message.length() > (getMessageLength() + 3)) {
            toReturn = message.substring(0, getMessageLength()) + "..."; //NOI18N
        }
        if (toReturn != null) {
            toReturn = toReturn.replace('\n', ' ');
        }
        return toReturn;
    }
        
    /** Getter for property messageLength.
     * @return Value of property messageLength.
     */
    public int getMessageLength() {
        return this.messageLength;
    }
    
    /** Setter for property messageLength.
     * @param messageLength New value of property messageLength.
     */
    public void setMessageLength(int messageLength) {
        int oldLength = this.messageLength;
        this.messageLength = messageLength;
        if (messageLength < 0) {
            this.messageLength = 0;
        }
        storeProperty(PROP_MESSAGE_LENGTH, new Integer(messageLength));
        firePropertyChange(PROP_MESSAGE_LENGTH, new Integer(oldLength), new Integer(messageLength));
        redisplayRevisions();
        
    }    
    
    private static Object vsActionAccessLock = new Object();

    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        VcsRevisionAction action = (VcsRevisionAction) SystemAction.get(VcsRevisionAction.class);
        synchronized (vsActionAccessLock) {
            action.setFileSystem(fileSystem);
            action.setFileObject(fo);
            action.setSelectedRevisionItems(revisionItems);
        }
        return new SystemAction[] { action };
    }

    public FilenameFilter getFileFilter() {
        return localFilenameFilter;
    }
    
    private class LocalFilenameFilter extends Object implements FilenameFilter {
        
        public boolean accept(File dir, String name) {
            if (!fileSystem.getFileFilter().accept(dir, name)) {
                return false;
            }
            return true;
            /* We should not call fileSystem.isImportant() here, because it consults 
             * SharabilityQuery and it can cause problems when called from AWT
            if (!isShowUnimportantFiles()) {
                File root = fileSystem.getRootDirectory();
                File file = new File(dir, name);
                String filePath;
                try {
                    filePath = file.getCanonicalPath();
                } catch (IOException ioex) {
                    filePath = file.getAbsolutePath();
                }
                String path = filePath.substring(root.getAbsolutePath().length());
                path.replace(File.separatorChar, '/');
                while (path.startsWith("/")) path = path.substring(1);
                return fileSystem.isImportant(path);
            } else {
                return true;
            }
             */
        }
        
    }

    private class VersioningVersions extends Object implements VersioningFileSystem.Versions {
        
        private static final long serialVersionUID = -8842749866809190554L;
        
        public VersioningVersions() {
            fileSystem.addRevisionListener(new RevisionListener() {
                public void stateChanged(javax.swing.event.ChangeEvent ev) {
                    //System.out.println("revision state changed:"+ev);
                    if (!(ev instanceof RevisionEvent)) return ;
                    RevisionEvent event = (RevisionEvent) ev;
                    String name = event.getFilePath();
                    //System.out.println("  name = "+name);
                    //public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                    RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                    //System.out.println("old List = "+oldList);
                    if (oldList != null) {
                        RevisionList newList = createRevisionList(name);
                        if (newList == null) return ;
                        ArrayList workNew = new ArrayList(newList);
                        synchronized (oldList) {
                            ArrayList workOld = new ArrayList(oldList);
                            workNew.removeAll(oldList);
                            //System.out.println("ADDING new revisions: "+workNew);
                            oldList.addAll(workNew); // add all new revisions
                            workOld.removeAll(newList);
                            //System.out.println("ADDING new revisions: "+workNew);
                            oldList.removeAll(workOld); // remove all old revisions (some VCS may perhaps allow removing revisions)

                            FileObject fo = findResource(name);
                            FileProperties fprops = Turbo.getMeta(fo);
                            String revision = fprops != null ? fprops.getRevision() : null;
                            if (revision != null) {
                                for (Iterator it = oldList.iterator(); it.hasNext(); ) {
                                    RevisionItem item = (RevisionItem) it.next();
                                    item.setCurrent(revision.equals(item.getRevision()));
                                }
                            }
                        }
                    }
                }
            });
        }
        
        public RevisionList getRevisions(String name, boolean refresh) {
            RevisionList list = (RevisionList) revisionListsByName.get(name);//new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList();
            if (list == null || refresh) {
                //org.openide.util.RequestProcessor.postRequest(new Runnable() {
                //    public void run() {
                list = createRevisionList(name);
                if (list != null) revisionListsByName.put(name, list);
                        //versioningSystem.fireRevisionChange(name);
                //    }
                //});
                //System.out.println("createRevisionList("+name+") = "+list);
            }
            //list.add(new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem("1.1"));
            //list.add(new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem("1.2"));
            return list;
        }
        
        private RevisionList createRevisionList(final String name) {
            //System.out.println("createRevisionList("+name+")");
            CommandSupport cmdSupport = fileSystem.getCommandSupport(VcsCommand.NAME_REVISION_LIST);
            if (cmdSupport == null) return null;
            Command cmd = cmdSupport.createCommand();
            if (cmd == null || !(cmd instanceof RegexOutputCommand)) return null;
            FileObject fo = fileSystem.findFileObject(name);
            if (fo == null) fo = VcsVersioningSystem.this.findResource(name);
            if (fo != null) {
                FileObject[] files = new FileObject[] { fo };
                files = cmd.getApplicableFiles(files);
                if (files == null) {
                    RevisionList list = new RevisionList();
                    list.setFileObject(fo);
                    return list; // Return an empty list when the command can not be executed!
                }
                cmd.setFiles(files);
            } else if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setDiskFiles(new java.io.File[] {
                    fileSystem.getFile(name)
                });
            }
            final StringBuffer dataBuffer = new StringBuffer();
            RegexOutputListener dataListener = new RegexOutputListener() {
                public void outputMatchedGroups(String[] data) {
                    if (data != null && data.length > 0) {
                        if (data[0] != null) dataBuffer.append(data[0]);
                    }
                }
            };
            ((RegexOutputCommand) cmd).addRegexOutputListener(dataListener);
            //VcsCommandExecutor[] vces = VcsAction.doCommand(files, cmd, null, fileSystem, null, null, dataListener, null);
            cmd.execute().waitFinished();
            RevisionList list = getEncodedRevisionList(name, dataBuffer.toString());
            if (list != null) displayRevisions(list);
            /*
            if (vces.length > 0) {
                final VcsCommandExecutor vce = vces[0];
                try {
                    fileSystem.getCommandsPool().waitToFinish(vce);
                } catch (InterruptedException iexc) {
                    return null;
                }
                list = getEncodedRevisionList(name, dataBuffer.toString());
                if (list != null) displayRevisions(list);
            }
             */
            return list;//(RevisionList) revisionListsByName.get(name);
        }

        private RevisionList getEncodedRevisionList(final String name, String encodedRL) {
            //System.out.println("addEncodedRevisionList("+name+", "+encodedRL.length()+")");
            if (encodedRL.length() == 0) return null;
            RevisionList list = null;
            try {
                list = (RevisionList) VcsUtilities.decodeValue(encodedRL);
            } catch (java.io.IOException ioExc) {
                //ioExc.printStackTrace();
                list = null;
            }
            return list;
            /*
            if (list != null) {
                /*
                addRevisionListener(new RevisionListener() {
                    public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                        RevisionList newList = createRevisionList(name);
                        RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                        if (oldList != null) {
                            oldList.clear();
                            oldList.addAll(newList);
                        }
                    }
                });
                 *
                revisionListsByName.put(name, list);
            }
             */
            //versioningSystem.fireRevisionChange(name, new RevisionEvent());
        }
        
        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException {
            CommandSupport cmdSupport = fileSystem.getCommandSupport(VcsCommand.NAME_REVISION_OPEN);
            if (cmdSupport == null) return null;
            Command command = cmdSupport.createCommand();
            if (command == null || !(command instanceof VcsDescribedCommand)) return null;
            VcsDescribedCommand cmd = (VcsDescribedCommand) command;
            Hashtable additionalVars = new Hashtable();
            additionalVars.put("REVISION", revision);
            cmd.setAdditionalVariables(additionalVars);

            FileObject resource = findResource(name);
            if (resource == null) {
                throw (java.io.FileNotFoundException)
                        ErrorManager.getDefault().annotate(new java.io.FileNotFoundException(name),
                            "Warning: can not find a resource for file '"+name+"' in "+this); // NOI18N
            }
            FileObject[] files = new FileObject[] { resource };
            files = command.getApplicableFiles(files);
            if (files == null) {
                throw (java.io.FileNotFoundException)
                        ErrorManager.getDefault().annotate(new java.io.FileNotFoundException(name),
                            "Warning: resource '"+name+"' is not applicable to command '"+VcsCommand.NAME_REVISION_OPEN+"'"); // NOI18N
            }
            cmd.setFiles(files);
            final StringBuffer fileBuffer = new StringBuffer();
            TextOutputListener fileListener = new TextOutputListener() {
                public void outputLine(String line) {
                    if (line != null) {
                        fileBuffer.append(line + "\n");
                    }
                }
            };
            cmd.addTextOutputListener(fileListener);
            //VcsCommandExecutor[] vces = VcsAction.doCommand(files, cmd, additionalVars, fileSystem, fileListener, null, null, null);
            CommandTask task = cmd.execute();
            task.waitFinished();
            boolean success = task.getExitStatus() == task.STATUS_SUCCEEDED;
            /*
            for (int i = 0; i < vces.length; i++) {
                try {
                    fileSystem.getCommandsPool().waitToFinish(vces[i]);
                } catch (InterruptedException iexc) {
                    throw (java.io.FileNotFoundException)
                        TopManager.getDefault().getErrorManager().annotate(new java.io.FileNotFoundException(), iexc);
                }
                success = success && (vces[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            }
             */
            if (VcsCommandIO.getBooleanProperty(cmd.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL)) success = true;
            if (!success) {
                throw (java.io.FileNotFoundException) ErrorManager.getDefault().annotate(
                    new java.io.FileNotFoundException(),
                    NbBundle.getMessage(VcsVersioningSystem.class, "MSG_RevisionOpenCommandFailed", name, revision));
            }
            return new ByteArrayInputStream(fileBuffer.toString().getBytes());
        }
    }
}
