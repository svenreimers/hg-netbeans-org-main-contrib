/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.Dialog;
import java.util.Hashtable;
import javax.swing.SwingUtilities;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;

import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.CVSPasswd;
import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.LoginPanel;
import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.CvsLoginProgressPanel;
import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.PasswdEntry;
import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.StandardScrambler;

/**
 * This class is used as a CVS login command.
 * @author  Martin Entlicher
 */
public class CvsLoginCheck implements VcsAdditionalCommand {

    private CommandExecutionContext context = null;
    public static final String DEFAULT_PORT_STR = "2401";   // NOI18N

    public void setExecutionContext(CommandExecutionContext context) {
        this.context = context;
    }

    /**
     * Executes the login check command.
     *
     * @param args The array of arguments.<br>
     *        -gui for GUI mode. If not specified, message is provided to stderrNRListener when an error occurs<br>
     *        connection string<br>
     *        password<br>
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        String connectStr = (String) vars.get("CONNECT_STR");
        String password = (String) vars.get("PASSWORD");
        String portStr = (String) vars.get("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
        boolean gui = false;
        int argIndex = 0;
        if (args.length > argIndex) {
            if ("-gui".equals(args[0])) {
                gui = true;
                argIndex++;
            }
        }
        if (args.length > argIndex) {
            connectStr = Variables.expand(vars, args[argIndex], false);
            argIndex++;
        }
        if (args.length > argIndex) {
            password = Variables.expand(vars, args[argIndex], false);
            argIndex++;
        }
        CvsLoginProgressPanel loginPanel = null;
        final Dialog dialog;
        if (gui) {
            loginPanel = new CvsLoginProgressPanel();
            //SwingUtilities.invokeLater(new Runnable
            DialogDescriptor dialogDescr = new DialogDescriptor(loginPanel,
                org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.title"),
                true, new Object[] { NotifyDescriptor.CLOSED_OPTION }, null, 0,
                null/*new HelpCtx(CvsLoginProgressPanel.class)*/, null);
            dialog = DialogDisplayer.getDefault().createDialog(dialogDescr);
            loginPanel.connectingTo((String) vars.get("CVS_SERVER"));
            dialog.pack();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dialog.show();
                }
            });
        } else {
            dialog = null;
        }
        boolean builtIn = "true".equals(vars.get("BUILT-IN"));
        StringBuffer message = new StringBuffer();
        boolean loggedIn = false;
        CVSPasswd pasFile = new CVSPasswd(null);
        Object loggedInText = context.getVariablesAsHashtable().get("LOGGED_IN_TEXT");
        java.util.Map cmdVars = new java.util.HashMap(vars);
        vars.clear(); // Not to unnecessarily update too many variables.
        vars.put("LOGGED_IN_TEXT", loggedInText);
        //System.out.println("CvsLoginCheck: putting LOGGED_IN_TEXT = '"+loggedInText+"'");
        int port = 0;        
        if (portStr != null) {
            if(portStr.equals(DEFAULT_PORT_STR))
                port = 0;
            else{
                try {
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException nfex) {}
            }
        }
        boolean validConnectString = true;
        pasFile.loadPassFile();
        try {
            pasFile.remove(connectStr, port);
        } catch (IllegalArgumentException iaex) {
            validConnectString = false;
            message.append(iaex.getLocalizedMessage());
        }
        if (validConnectString) {
            try {
                if (builtIn) {
                    PasswdEntry entry = new PasswdEntry();
                    String scrambledPassword = StandardScrambler.getInstance().scramble(password);
                    entry.setEntry(connectStr+" "+scrambledPassword);
                    if (port > 0) entry.getCVSRoot().setPort(port);
                    loggedIn = CVSPasswd.checkServer(entry, message);
                    if (loggedIn) {
                        pasFile.add(connectStr, port, password);
                        pasFile.savePassFile();
                    }
                } else {
                    //System.out.println("Adding '"+connectStr+"' with password '"+password+"' into "+pasFile.getHome()+"/.cvspass");
                    pasFile.add(connectStr, port, password);
                    pasFile.savePassFile();
                    loggedIn = CVSPasswd.checkLogin(context, message, cmdVars);
                }
            } catch (java.net.UnknownHostException exc) {
                if (loginPanel != null) {
                    /*
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                            org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.unknownHost")));
                     */
                    loginPanel.loginFinished(org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.unknownHost"));
                } else {
                    stderrNRListener.outputLine(
                        org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.unknownHost"));
                }
                vars.put("USER_IS_LOGGED_IN", "");
                vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_UPDATE_CHANGED_FROM_SELECTOR, "true");
                return false;
            } catch (java.io.IOException exc) {
                if (loginPanel != null) {
                    /*
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                            org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.connectionIOError")));
                     */
                    loginPanel.loginFinished(org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.connectionIOError"));
                } else {
                    stderrNRListener.outputLine(
                        org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.connectionIOError"));
                }
                vars.put("USER_IS_LOGGED_IN", "");
                vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_UPDATE_CHANGED_FROM_SELECTOR, "true");
                return false;
            } finally {
                if (!loggedIn && !builtIn) {
                    pasFile.remove(connectStr, port);
                    pasFile.savePassFile();
                }
            }
        }
        if (!loggedIn) {
            if (loginPanel != null) {
                /*
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(
                        org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.status.failed") + " "+ message));
                 */
                //System.out.println("message = '"+message+"'.");
                loginPanel.loginFinished(org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.status.failed") + " "+ message);
            } else {
                stderrNRListener.outputLine(
                    org.openide.util.NbBundle.getMessage(LoginPanel.class, "LoginDialog.status.failed") + " "+ message);
            }
            vars.put("USER_IS_LOGGED_IN", "");
        } else {
            if (dialog != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setVisible(false);
                    }
                });
            }
            vars.put("USER_IS_LOGGED_IN", "true");
        }
        vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_UPDATE_CHANGED_FROM_SELECTOR, "true");
        return loggedIn;
    }
}

