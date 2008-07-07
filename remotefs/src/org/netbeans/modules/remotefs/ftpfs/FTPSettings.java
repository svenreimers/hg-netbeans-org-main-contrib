/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */

package org.netbeans.modules.remotefs.ftpfs;

/** Global FTPFileSystem settings
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPSettings extends org.openide.options.SystemOption {
  static final long serialVersionUID = 6880742148617337695L;

  public static final String PROP_REFRESH_TIME = "refreshtime";
  public static final String PROP_PASSIVE_MODE = "passivemode";

  /** Holds value of property refreshTime. */
  private static int refreshTime = 60000;
  /** Holds value of property refreshServer. */
  private static boolean refreshServer = true;
  /** Holds value of property scanCache. */
  private static boolean scanCache = true;
  /** Holds value of property offlineChanges. */
  private static boolean offlineChanges = true;
  /** Holds value of property downloadServerChangedFile. */
  private static boolean downloadServerChangedFile = true;
  /** Holds value of property refreshAlways. */
  private static boolean refreshAlways = false;
  /** Holds value of property askServerChangedFile. */
  private static boolean askServerChangedFile = true;
  /** Holds value of property askWhichFile. */
  private static boolean askWhichFile = true;
  /** Holds value of property passiveMode. */
  private static boolean passiveMode = false;
  /** Holds value of property askCacheExternalDelete. */
  private static boolean askCacheExternalDelete = true;
  /** Holds value of property cacheExternalDelete. */
  private static boolean cacheExternalDelete = false;
  /** Holds value of property askServerExternalDelete. */
  private static boolean askServerExternalDelete = true;
  /** Holds value of property serverExternalDelete. */
  private static boolean serverExternalDelete = false;
  /** Creates new FTPSettings */
  public FTPSettings() {
  }
  
  /** Returns display name of the settings
   * @return display name
   */
  public String displayName() {
    return "FTP FileSystem";
  }
  /** Getter for property refreshTime.
   * @return Value of property refreshTime.
   */
  public int getRefreshTime() {
    return refreshTime;
  }
  /** Setter for property refreshTime.
   * @param refreshTime New value of property refreshTime.
   */
  public void setRefreshTime(int refreshTime) {
    int old = FTPSettings.refreshTime;
    FTPSettings.refreshTime = refreshTime;
    firePropertyChange(PROP_REFRESH_TIME,new Integer(old),new Integer(FTPSettings.refreshTime));
  }
  /** Getter for property refreshServer.
   * @return Value of property refreshServer.
   */
  public boolean isRefreshServer() {
    return refreshServer;
  }
  /** Setter for property refreshServer.
   * @param refreshServer New value of property refreshServer.
   */
  public void setRefreshServer(boolean refreshServer) {
    FTPSettings.refreshServer = refreshServer;
  }
  /** Getter for property scanCache.
   * @return Value of property scanCache.
   */
  public boolean isScanCache() {
    return scanCache;
  }
  /** Setter for property scanCache.
   * @param scanCache New value of property scanCache.
   */
  public void setScanCache(boolean scanCache) {
    FTPSettings.scanCache = scanCache;
  }
  /** Getter for property offlineChanges.
   * @return Value of property offlineChanges.
   */
  public boolean isOfflineChanges() {
    return offlineChanges;
  }
  /** Setter for property offlineChanges.
   * @param offlineChanges New value of property offlineChanges.
   */
  public void setOfflineChanges(boolean offlineChanges) {
    FTPSettings.offlineChanges = offlineChanges;
  }
  /** Getter for property downloadServerChangedFile.
   * @return Value of property downloadServerChangedFile.
   */
  public boolean isDownloadServerChangedFile() {
    return downloadServerChangedFile;
  }
  /** Setter for property downloadServerChangedFile.
   * @param downloadServerChangedFile New value of property downloadServerChangedFile.
   */
  public void setDownloadServerChangedFile(boolean downloadServerChangedFile) {
    FTPSettings.downloadServerChangedFile = downloadServerChangedFile;
  }
  /** Getter for property refreshAlways.
   * @return Value of property refreshAlways.
   */
  public boolean isRefreshAlways() {
    return refreshAlways;
  }
  /** Setter for property refreshAlways.
   * @param refreshAlways New value of property refreshAlways.
   */
  public void setRefreshAlways(boolean refreshAlways) {
    FTPSettings.refreshAlways = refreshAlways;
  }
  /** Getter for property askServerChangedFile.
   * @return Value of property askServerChangedFile.
   */
  public boolean isAskServerChangedFile() {
    return askServerChangedFile;
  }
  /** Setter for property askServerChangedFile.
   * @param askServerChangedFile New value of property askServerChangedFile.
   */
  public void setAskServerChangedFile(boolean askServerChangedFile) {
    FTPSettings.askServerChangedFile = askServerChangedFile;
  }
  /** Getter for property askWhichFile.
   * @return Value of property askWhichFile.
   */
  public boolean isAskWhichFile() {
    return askWhichFile;
  }
  /** Setter for property askWhichFile.
   * @param askWhichFile New value of property askWhichFile.
   */
  public void setAskWhichFile(boolean askWhichFile) {
    FTPSettings.askWhichFile = askWhichFile;
  }
  /** Getter for property passiveMode.
   * @return Value of property passiveMode.
   */
  public boolean isPassiveMode() {
    return passiveMode;
  }
  /** Setter for property passiveMode.
   * @param passiveMode New value of property passiveMode.
   */
  public void setPassiveMode(boolean passiveMode) {
    boolean old = FTPSettings.passiveMode;
    FTPSettings.passiveMode = passiveMode;
    firePropertyChange(PROP_PASSIVE_MODE,old ? Boolean.TRUE : Boolean.FALSE,FTPSettings.passiveMode ? Boolean.TRUE : Boolean.FALSE);
  }
  /** Getter for property askCacheExternalDelete.
   * @return Value of property askCacheExternalDelete.
   */
  public boolean isAskCacheExternalDelete() {
    return askCacheExternalDelete;
  }
  /** Setter for property askCacheExternalDelete.
   * @param askCacheExternalDelete New value of property askCacheExternalDelete.
   */
  public void setAskCacheExternalDelete(boolean askCacheExternalDelete) {
    FTPSettings.askCacheExternalDelete = askCacheExternalDelete;
  }
  /** Getter for property cacheExternalDelete.
   * @return Value of property cacheExternalDelete.
   */
  public boolean isCacheExternalDelete() {
    return cacheExternalDelete;
  }
  /** Setter for property cacheExternalDelete.
   * @param cacheExternalDelete New value of property cacheExternalDelete.
   */
  public void setCacheExternalDelete(boolean cacheExternalDelete) {
    FTPSettings.cacheExternalDelete = cacheExternalDelete;
  }
  /** Getter for property askServerExternalDelete.
   * @return Value of property askServerExternalDelete.
   */
  public boolean isAskServerExternalDelete() {
    return askServerExternalDelete;
  }
  /** Setter for property askServerExternalDelete.
   * @param askServerExternalDelete New value of property askServerExternalDelete.
   */
  public void setAskServerExternalDelete(boolean askServerExternalDelete) {
    FTPSettings.askServerExternalDelete = askServerExternalDelete;
  }
  /** Getter for property serverExternalDelete.
   * @return Value of property serverExternalDelete.
   */
  public boolean isServerExternalDelete() {
    return serverExternalDelete;
  }
  /** Setter for property serverExternalDelete.
   * @param serverExternalDelete New value of property serverExternalDelete.
   */
  public void setServerExternalDelete(boolean serverExternalDelete) {
    FTPSettings.serverExternalDelete = serverExternalDelete;
  }
  
}