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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd;

import java.util.*;

import org.netbeans.lib.cvsclient.CVSRoot;

/** This class stores info about one cvs repository. It's read by {@link org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.CVSPasswd }
 *
 * @author Milos Kleint
 */
    public class PasswdEntry {

        private CVSRoot cvsroot;
        private String passwd = null;
        boolean format1 = false;

      /**
       * Create an empty password entry.
       * Use {@link #setEntry()} to set the entry.
       */
      public PasswdEntry() {
      }

      /**
       * Set the entry and initalize this object.
       * @param entry The entry (a line from .cvspass file).
       * @return Whether the entry was set successfully.
       */
      public boolean setEntry(String entry) {
        if (entry.startsWith("/1 ")) {
            format1 = true;
            entry = entry.substring("/1 ".length());
        }
        int spacePos = entry.indexOf(" A");
        if (spacePos < 0) {
            return false; // corrupted line.
        }
        String main = entry.substring(0, spacePos);
        passwd = entry.substring(spacePos + 1);
        
        try {
            cvsroot = CVSRoot.parse(main);
        } catch (IllegalArgumentException iaex) {
            return false; // Can not parse the CVSROOT.
        }
        if (format1 && cvsroot.getPort() == 0) {
            cvsroot.setPort(CVSPasswd.STANDARD_PSERVER_PORT);
        }
        return true;
     }

      /**
       * Get the String representation of this entry.
       */
      public String getEntry() {
        if (cvsroot == null) return null; // The entry was not set successfully.
        format1 = cvsroot.getPort() != 0; // The port could be set later
        String prefix = (format1) ? "/1 " : ""; // NOI18N
        //String entry = prefix + ":" + type + ":" + user + "@" + server + ":" +
        //               ((port != null) ? port : "") + root;
        //if (withPasswd) {entry = entry + " " + passwd;}
        return prefix + cvsroot.toString() + " " + passwd;
        //return entry;
      }
      
      /**
       * Get the CVSROOT of this entry.
       */
      public CVSRoot getCVSRoot() {
          return cvsroot;
      }
      
      /**
       * Get the scrambled password.
       */
      public String getPasswd() {
          return passwd;
      }
      
      /**
       * @return The same as {@link #getEntry()}
       */
      public String toString() {
          return getEntry();
      }

/** Method used to compose the authentification string that is send to the server in the beginning of the communication.
 * For details see CVS Client/Server protocol specification.
 * @return the authentification string
 */
      public String getAuthString() {
          if (cvsroot == null) return null; // The entry was not set.
          return (cvsroot.getRepository() + "\n" + cvsroot.getUserName() + "\n" + getPasswd() + "\n");
      }

  }
