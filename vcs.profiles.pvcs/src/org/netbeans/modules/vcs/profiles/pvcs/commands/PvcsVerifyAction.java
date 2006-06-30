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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.Statuses;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.actions.AddCommandAction;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.grouping.GroupUtils;
import org.netbeans.modules.vcscore.ui.*;
import org.netbeans.modules.vcscore.util.Table;

import org.netbeans.modules.vcs.profiles.commands.ToLockFilesPanel;
import org.netbeans.modules.vcs.profiles.commands.VerifyUtil;
import org.openide.DialogDisplayer;

/**
 * The verification of PVCS files in a group.
 *
 * @author  Martin Entlicher
 */
public class PvcsVerifyAction extends java.lang.Object implements VcsAdditionalCommand {
    
    private static String UP_TO_DATE = "Current";

    private ArrayList localFiles;
    private ArrayList uptoDateFiles;
    private ArrayList notLockedFiles;
    private NotChangedFilesPanel ncfPanel;
    private ToAddFilesPanel taPanel;
    private ToLockFilesPanel tlPanel;
    private String lockCommand;

    private VcsFileSystem fileSystem = null;

    /** Creates new PvcsVerifyAction */
    public PvcsVerifyAction() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private String getFreshFileStatus(String file, VcsCommand cmd) throws InterruptedException {
        Table files = new Table();
        files.put(file, fileSystem.findFileObject(file));
        VcsCommandExecutor[] execs = VcsAction.doCommand(files, cmd, null, fileSystem, null, null, null, null, true);
        for (int i = 0; i < execs.length; i++) {
            try {
                fileSystem.getCommandsPool().waitToFinish(execs[i]);
            } catch (InterruptedException iexc) {
                for (int j = i; j < execs.length; j++) {
                    fileSystem.getCommandsPool().kill(execs[j]);
                }
                throw iexc;
            }
            if (execs[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED) {
                return UP_TO_DATE;
            } else {
                return "Locally Modified";
            }
        }
        return "";
    }
    
    private void fillFilesByState(List fos, String getStatusCmd) throws InterruptedException {

        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            if (!fileSystem.getFile(fo.getPath()).exists()) {
                // File does not exist locally - same behavior as whan it was not changed locally:
                uptoDateFiles.add(fo);
                continue;
            }
            FileProperties fprops = Turbo.getMeta(fo);
            String status = FileProperties.getStatus(fprops);
            if (Statuses.getLocalStatus().equals(status)) {
                localFiles.add(fo);
                //System.out.println("  is Local");
            } else {
                if (getStatusCmd != null) {
                    status = getFreshFileStatus(fo.getPath(), fileSystem.getCommand(getStatusCmd));
                    //System.out.println("getFreshFileStatus("+file+") = "+status);
                }
                if (UP_TO_DATE.equals(status)) {
                    uptoDateFiles.add(fo);
                    //System.out.println("  is Up to date");
                } else {
                    //System.out.println("  is Unrecognized => should me modified or so.");
                    String locker = fprops != null ? fprops.getLocker() : null;
                    if (locker == null || locker.trim().length() == 0) {
                        notLockedFiles.add(fo);
                    }
                }
            }
        }
    }

    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        localFiles = new ArrayList();
        uptoDateFiles = new ArrayList();
        notLockedFiles = new ArrayList();
        List foList = VerifyUtil.getFOs(fileSystem, vars);
        try {
            fillFilesByState(foList, (args.length > 1) ? args[0] : null);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (args.length > 1) {
            lockCommand = args[1];
        } else if (args.length > 0) {
            lockCommand = args[0];
        } else {
            lockCommand = "LOCK";
        }
        showDialog();
        return true;
    }
    
    private void showDialog() {
        VerifyGroupPanel panel = new VerifyGroupPanel();
        boolean nothing = true;
        if (localFiles.size() > 0) {
            taPanel = new ToAddFilesPanel(localFiles);
            panel.addPanel(taPanel, NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.ToAdd"));
            nothing = false;
        }
        if (uptoDateFiles.size() > 0) {
            List dobjList = getDOForNotChanged(uptoDateFiles);
            if (dobjList.size() > 0) {
                ncfPanel = new NotChangedFilesPanel(dobjList);
                panel.addPanel(ncfPanel, NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.NotChanged"));
                nothing = false;
            }
        }
        if (notLockedFiles.size() > 0) {
            tlPanel = new ToLockFilesPanel(notLockedFiles);
            panel.addPanel(tlPanel, NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.NotLocked"));
            nothing = false;
        }
        String title = NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.title");
        DialogDescriptor dd = new DialogDescriptor(panel, title);
        dd.setModal(false);
        if (nothing) {
            panel.setDescription(NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.NoProblem"));
            JButton btnClose = new JButton(NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.closeButton"));
            Object[] options = new Object[] {btnClose};
            dd.setOptions(options);
            dd.setClosingOptions(options);
        } else {
            panel.setDescription(NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.ProblemsFound"));
            final JButton btnCorrect = new JButton(NbBundle.getBundle(PvcsVerifyAction.class).getString("VcsVerifyAction.correctButton"));
            btnCorrect.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(PvcsVerifyAction.class).getString("ACSD_VcsVerifyAction.correctButton"));
            Object[] options = new Object[] {btnCorrect, DialogDescriptor.CANCEL_OPTION};
            dd.setOptions(options);
            dd.setClosingOptions(options);
            dd.setButtonListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (event.getSource().equals(btnCorrect)) {
                        correctGroup();
                        return;
                    }
                }
            });
        }
        final Dialog dial = DialogDisplayer.getDefault().createDialog(dd);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dial.show();
            }  
        });        
    }
    
    private String getStatus(DataObject dobj) {
        String status = null;
        for (Iterator it = dobj.files().iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            if (!fileSystem.isProcessUnimportantFiles() &&
                !fileSystem.isImportant(fo.getPath())) continue;
            String foStatus;
            if (uptoDateFiles.contains(fo)) {
                foStatus = UP_TO_DATE;
            } else {
                foStatus = fileSystem.getStatus(fo);
            }
            if (status == null) {
                status = foStatus;
            } else if (!status.equals(foStatus)) {
                status += foStatus;
                break;
            }
        }
        return status;
    }

    private List getDOForNotChanged(List list) {
        Iterator it = list.iterator();
        HashSet dobjStat = new HashSet();
        while (it.hasNext()) {
            FileObject fo = (FileObject)it.next();
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fo);
                //System.out.println("datablject=" + dobj.getName());
                String stat = getStatus(dobj);
                //System.out.println("dataobject's status=" + stat);
                if (UP_TO_DATE.equals(stat)) {
                    dobjStat.add(dobj);
                }
                
            } catch (DataObjectNotFoundException exc) {
                continue;
            }
        }
        List toReturn = new ArrayList(dobjStat.size());
        toReturn.addAll(dobjStat);
        return toReturn;
    }
   
    private void correctGroup() {
        if (ncfPanel != null) {
            performNCFCorrection();
        }
        if (taPanel != null) {
            performTACorrection();
        }
        if (tlPanel != null) {
            performTLCorrection();
        }
    }
    
    public void performNCFCorrection() {
        List list = ncfPanel.getSelectedDataObjects();
        if (list != null && list.size() != 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                DataObject obj = (DataObject) it.next();
                DataShadow shadow = GroupUtils.findDOInGroups(obj);
                if (shadow != null) {
                    try {
                        shadow.delete();
                    } catch (java.io.IOException exc) {
                    }
                }
            }
        }
    }
    
    public void performTACorrection() {
        java.util.List foList = taPanel.getFileObjects();
        if (foList != null && foList.size() != 0) {
            //CommandActionSupporter supp = FsCommandFactory.getFsInstance().getSupporter();
            AddCommandAction act = (AddCommandAction) SharedClassObject.findObject(AddCommandAction.class, true);
            FileObject[] fos = new FileObject[foList.size()];
            fos = (FileObject[]) foList.toArray(fos);
            CommandActionSupporter supp = (CommandActionSupporter) fos[0].getAttribute(GeneralCommandAction.VCS_ACTION_ATTRIBUTE);
            supp.performAction(act, fos);
        }
    }
    
    public void performTLCorrection() {
        java.util.List foList = tlPanel.getFileObjects();
        if (foList != null && foList.size() != 0) {
            VcsCommand lock = fileSystem.getCommand(lockCommand);
            if (lock != null) {
                Table files = new Table();
                for (Iterator it = foList.iterator(); it.hasNext(); ) {
                    FileObject file = (FileObject) it.next();
                    files.put(file.getPath(), file);
                }
                VcsAction.doCommand(files, lock, null, fileSystem);
            }
        }
    }
    
}
