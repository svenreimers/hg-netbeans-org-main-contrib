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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.hk2.wizards;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 *
 * @author peterw99
 */
public class Retriever implements Runnable {

    public static final int STATUS_START = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_DOWNLOADING = 2;
    public static final int STATUS_COMPLETE = 3;
    public static final int STATUS_FAILED = 4;
    public static final int STATUS_TERMINATED = 5;
    public static final int STATUS_BAD_DOWNLOAD = 6;
    
    private static final String [] STATUS_MESSAGE = {
        "Ready.",
        "Connecting...",
        "Downloading...",
        "", // "Complete",
        "{0}: {1}",
        "", // "Terminated.",
        "Response from this URL is not a valid WSDL file.",
    };
    
    public interface Updater {
        public void updateMessageText(String msg);
        public void updateStatusText(String status);
        public void clearCancelState();
    }
    
    private Updater updater;
    private String targetUrlName;
    private File targetInstallDir;
    
    
    public Retriever(File installDir, String targetUrl, Updater u) {
        targetInstallDir = installDir;
        targetUrlName = targetUrl;
        updater = u;
    }

    // Thread support for downloading...
    public void stopRetrieval() {
        shutdown = true;
    }
    
    public int getDownloadState() {
        return status;
    }
    
    private void setDownloadState(int newState) {
        status = newState;
        updateStatus(STATUS_MESSAGE[newState]);
    }
    
    private void setDownloadState(int newState, String msg, Exception ex) {
        status = newState;
        Object [] args = new Object [] { msg, ex.getMessage()};
        updateStatus(MessageFormat.format(STATUS_MESSAGE[newState], args));
    }
    
    private void updateMessage(final String text, int count) {
        final String size = countAsString(count);
        updateMessage(text + size);
    }
    
    private void updateMessage(final String msg) {
        updater.updateMessageText(msg);
    }
    
    private void updateStatus(final String status) {
        updater.updateStatusText(status);
    }
    
    private String countAsString(int c) {
        String size = "";
        if(c < 1024) {
            size = c + " bytes";
        } else if(c < 1048676) {
            size = c / 1024 + "k";
        } else {
            int m = c / 1048676;
            int d = (c - m * 1048676)*10 / 1048676;
            size = m + "." + d + "m";
        }
        return " (" + size + ")";
    }

    // Thread plumbing
    private volatile boolean shutdown;
    private volatile int status;
    
    public void run() {
        // Set name of thread for easier debugging in case of deadlocks, etc.
        Thread.currentThread().setName("V3Downloader"); // NOI18N
        
        shutdown = false;
        status = STATUS_START;
        URL targetUrl = null;
        URLConnection connection = null;
        InputStream in = null;
        File backupDir = null;
        long start = System.currentTimeMillis();
        
        try {
            backupDir = backupInstallDir(targetInstallDir);
            targetUrl = new URL(targetUrlName);
            setDownloadState(STATUS_CONNECTING);
            
            connection = targetUrl.openConnection();
            in = connection.getInputStream();
            setDownloadState(STATUS_DOWNLOADING);
            
            // Download and unzip the V3 archive.
            downloadAndInstall(in, targetInstallDir);
            
            if(!shutdown) {
                long end = System.currentTimeMillis();
                updateMessage("Download & Install completed in " + (end - start) + "ms");
                setDownloadState(STATUS_COMPLETE);
            } else {
                updateMessage("Download cancelled.");
                setDownloadState(STATUS_TERMINATED);
            }
        } catch(ConnectException ex) {
            setDownloadState(STATUS_FAILED, "Connection Exception", ex); // NOI18N
        } catch(MalformedURLException ex) {
            setDownloadState(STATUS_FAILED, "Badly formed URL", ex); // NOI18N
        } catch(IOException ex) {
            setDownloadState(STATUS_FAILED, "I/O Exception", ex); // NOI18N
        } catch(RuntimeException ex) {
            setDownloadState(STATUS_FAILED, "Runtime Exception", ex); // NOI18N
        } finally {
            if(shutdown || status != STATUS_COMPLETE) {
                restoreInstallDir(targetInstallDir, backupDir);
            }
            if(in != null) {
                try { in.close(); } catch(IOException ex) { }
            }
            
            updater.clearCancelState();
        }
    }

    private static final int READ_BUF_SIZE = 131072; // 128k
    private static final int WRITE_BUF_SIZE = 131072; // 128k
    
    private boolean downloadAndInstall(final InputStream in, final File targetFolder) throws IOException {
        BufferedInputStream bufferedStream = null;
        JarInputStream jarStream = null;
        try {
            final byte [] buffer = new byte [WRITE_BUF_SIZE];
            bufferedStream = new BufferedInputStream(in, READ_BUF_SIZE);
            jarStream = new JarInputStream(bufferedStream);
            final InputStream entryStream = jarStream;
            JarEntry entry;
            while(!shutdown && (entry = (JarEntry) jarStream.getNextEntry()) != null) {
                String entryName = stripGlassfish(entry.getName());
                if(entryName == null || entryName.length() == 0) {
                    continue;
                }
                final File entryFile = new File(targetFolder, entryName);
                if(entryFile.exists()) {
                    // !PW FIXME entry already exists, offer overwrite option...
                    throw new RuntimeException("Target " + entryFile.getPath() +
                            " already exists.  Terminating archive installation.");
                } else if(entry.isDirectory()) {
                    if(!entryFile.mkdirs()) {
                        throw new RuntimeException("Failed to create folder: " +
                                entryFile.getName() + ".  Terminating archive installation.");
                    }
                } else {
                    File parentFile = entryFile.getParentFile();
                    if(!parentFile.exists() && !parentFile.mkdirs()) {
                        throw new RuntimeException("Failed to create folder: " +
                                parentFile.getName() + ".  Terminating archive installation.");
                    }
                    
                    int bytesRead = 0;
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(entryFile);
                        int len;
                        long lastUpdate = 1;
                        while(!shutdown && (len = entryStream.read(buffer)) >= 0) {
                            bytesRead += len;
                            long update = System.currentTimeMillis() / 333;
                            if(update != lastUpdate) {
                                updateMessage("Installing " + entryName, bytesRead);
                                lastUpdate = update;
                            }
                            os.write(buffer, 0, len);
                        }
                    } finally {
                        if(os != null) {
                            try { os.close(); } catch(IOException ex) { }
                        }
                    }
                }
            }
        } finally {
            if(bufferedStream != null) {
                try { bufferedStream.close(); } catch(IOException ex) { }
            }
            if(jarStream != null) {
                try { jarStream.close(); } catch(IOException ex) { }
            }
        }
        
        return shutdown;
    }
    
    private String stripGlassfish(String name) {
        if(name.startsWith("glassfish")) {
            name = name.substring(9);
            if(name.startsWith("/") || name.startsWith("\\")) {
                name = name.substring(1);
            }
        }
        return name;
    }
    
    private File backupInstallDir(File installDir) throws IOException {
        if(installDir.exists()) {
            File parent = installDir.getParentFile();
            String tempName = installDir.getName();
            for(int i = 1; i < 100; i++) {
                File target = new File(parent, tempName + i);
                if(!target.exists()) {
                    if(!installDir.renameTo(target)) {
                        throw new IOException("Unable to move existing " + (installDir.isDirectory() ? "folder \"" : "file \"")
                                + installDir.getAbsolutePath() + "\" out of the way.");
                    };
                    return target;
                }
            }
            throw new IOException("Unable to backup \"" + installDir.getAbsolutePath() + "\".  Too many V3 backups already.");
        }
        return null;
    }
    
    private void restoreInstallDir(File installDir, File backupDir) {
        if(installDir != null && installDir.exists()) {
            Util.deleteFolder(installDir);
        }

        if(backupDir != null && backupDir.exists()) {
            backupDir.renameTo(installDir);
        }
    }
}
