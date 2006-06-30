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

package org.netbeans.modules.vcscore.cmdline;

import java.util.*;

import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

/**
 * An abstract list command, that every class has to implement to provide a listing information.
 *
 * @author  Martin Entlicher
 */
public abstract class VcsListCommand extends Object {

    /**
     * Makes a single String from the array of Strings.
     */
    protected String array2string(String[] sa){
        StringBuffer sb=new StringBuffer(255);
        if (sa != null)
            for(int i=0;i<sa.length;i++){
                sb.append(sa[i]+" "); // NOI18N
            }
        return new String(sb);
    }

    /**
     * This method is called when the content of the directory is to be listed.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param filesByName return the files read from the given directory, or the error
     *                    description when an error occures. The keys in this Hashtable
     *                    are supposed to be file names, values are supposed to be an array
     *                    of String containing statuses.
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                           satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                           satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public abstract boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                                 CommandOutputListener stdoutListener,
                                 CommandOutputListener stderrListener,
                                 CommandDataOutputListener stdoutDataListener, String dataRegex,
                                 CommandDataOutputListener stderrDataListener, String errorRegex);


}
