/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.corba;

import java.io.*;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.netbeans.ide.util.NbBundle;
import com.netbeans.ide.filesystems.FileLock;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.filesystems.FileUtil;

/** Support for execution applets for applets
*
* @author Ales Novak
* @version 0.10 May 07, 1998
*/
public class CORBASupport {

    /** bundle to obtain text information from */
    public static ResourceBundle bundle = NbBundle.getBundle(CORBASupport.class);

    public static final String ORBIX = CORBASupport.bundle.getString ("CTL_Orbix");

    public static final String VISIBROKER = CORBASupport.bundle.getString ("CTL_Visibroker");

    public static final String ORBACUS = CORBASupport.bundle.getString ("CTL_Orbacus");

    public static final String JAVAORB = CORBASupport.bundle.getString ("CTL_JavaORB");

    public static final String INHER = CORBASupport.bundle.getString ("CTL_Inher");

    public static final String TIE = CORBASupport.bundle.getString ("CTL_Tie");

    public static final String SB1 = CORBASupport.bundle.getString ("CTL_ServerBinding1");
  
    public static final String SB2 = CORBASupport.bundle.getString ("CTL_ServerBinding2");

    public static final String SB3 = CORBASupport.bundle.getString ("CTL_ServerBinding3");

    public static final String SB4 = CORBASupport.bundle.getString ("CTL_ServerBinding4");

    public static final String CB1 = CORBASupport.bundle.getString ("CTL_ClientBinding1");

    public static final String CB2 = CORBASupport.bundle.getString ("CTL_ClientBinding2");

    public static final String CB3 = CORBASupport.bundle.getString ("CTL_ClientBinding3");

    public static final String CB4 = CORBASupport.bundle.getString ("CTL_ClientBinding4");

    public static final String ORBIX_IMPORT = CORBASupport.bundle.getString 
	("CTL_ORBIX_IMPORT");
    public static final String ORBIX_PROPS_SETTINGS = CORBASupport.bundle.getString 
	("CTL_ORBIX_SETTINGS_ORB_PROPERTIES");
    public static final String ORBIX_INIT = CORBASupport.bundle.getString ("CTL_ORBIX_ORB_INIT");

    public static final String ORBIX_DIR_PARAM = CORBASupport.bundle.getString 
	("CTL_ORBIX_DIR_PARAM");
    public static final String ORBIX_PACKAGE_PARAM = CORBASupport.bundle.getString
	("CTL_ORBIX_PACKAGE_PARAM");
    public static final String ORBIX_COMPILER = CORBASupport.bundle.getString
	("CTL_ORBIX_COMPILER");
    //public static final String ORBIX_PACKAGE_DELIMITER = CORBASupport.bundle.getString
    //	("CTL_ORBIX_PACKAGE_DELIMITER");

    public static final String VISIBROKER_IMPORT = CORBASupport.bundle.getString 
	("CTL_VISIBROKER_IMPORT");
    public static final String VISIBROKER_PROPS_SETTINGS = CORBASupport.bundle.getString 
	("CTL_VISIBROKER_SETTINGS_ORB_PROPERTIES");
    public static final String VISIBROKER_INIT = CORBASupport.bundle.getString 
	("CTL_VISIBROKER_ORB_INIT");

    public static final String VISIBROKER_DIR_PARAM = CORBASupport.bundle.getString 
	("CTL_VISIBROKER_DIR_PARAM");
    public static final String VISIBROKER_PACKAGE_PARAM = CORBASupport.bundle.getString 
	("CTL_VISIBROKER_PACKAGE_PARAM");
    public static final String VISIBROKER_COMPILER = CORBASupport.bundle.getString 
	("CTL_VISIBROKER_COMPILER");

    public static final String ORBACUS_IMPORT = CORBASupport.bundle.getString 
	("CTL_ORBACUS_IMPORT");
    public static final String ORBACUS_PROPS_SETTINGS = CORBASupport.bundle.getString 
	("CTL_ORBACUS_SETTINGS_ORB_PROPERTIES");
    public static final String ORBACUS_INIT = CORBASupport.bundle.getString ("CTL_ORBACUS_ORB_INIT");
    public static final String ORBACUS_DIR_PARAM = CORBASupport.bundle.getString 
	("CTL_ORBACUS_DIR_PARAM");
    public static final String ORBACUS_PACKAGE_PARAM = CORBASupport.bundle.getString 
	("CTL_ORBACUS_PACKAGE_PARAM");
    public static final String ORBACUS_COMPILER = CORBASupport.bundle.getString 
	("CTL_ORBACUS_COMPILER");
    //public static final String ORBACUS_PACKAGE_DELIMITER = CORBASupport.bundle.getString
    //	("CTL_ORBACUS_PACKAGE_DELIMITER");
    public static final String ORBACUS_ERROR_EXPRESSION = CORBASupport.bundle.getString
	("CTL_ORBACUS_ERROR_EXPRESSION");
    public static final String ORBACUS_FILE_POSITION = CORBASupport.bundle.getString
	("CTL_ORBACUS_FILE_POSITION");
    public static final String ORBACUS_LINE_POSITION = CORBASupport.bundle.getString
	("CTL_ORBACUS_LINE_POSITION");
    public static final String ORBACUS_COLUMN_POSITION = CORBASupport.bundle.getString
	("CTL_ORBACUS_COLUMN_POSITION");
    public static final String ORBACUS_MESSAGE_POSITION = CORBASupport.bundle.getString
	("CTL_ORBACUS_MESSAGE_POSITION");
    
    
    public static final String JAVAORB_IMPORT = CORBASupport.bundle.getString 
	("CTL_JAVAORB_IMPORT");
    public static final String JAVAORB_PROPS_SETTINGS = CORBASupport.bundle.getString 
	("CTL_JAVAORB_SETTINGS_ORB_PROPERTIES");
    public static final String JAVAORB_INIT = CORBASupport.bundle.getString ("CTL_JAVAORB_ORB_INIT");
    public static final String JAVAORB_DIR_PARAM = CORBASupport.bundle.getString 
	("CTL_JAVAORB_DIR_PARAM");
    public static final String JAVAORB_PACKAGE_PARAM = CORBASupport.bundle.getString 
	("CTL_JAVAORB_PACKAGE_PARAM");
    public static final String JAVAORB_COMPILER = CORBASupport.bundle.getString 
	("CTL_JAVAORB_COMPILER");
    //public static final String JAVAORB_PACKAGE_DELIMITER = CORBASupport.bundle.getString
    //	("CTL_JAVAORB_PACKAGE_DELIMITER");
    
    /** constant for idl extension */
    private static final String IDL_EXT = "idl";
    
    /** constant for java extension */
    private static final String JAVA_EXT = "java";

    /**
     * no-arg constructor
     */
    CORBASupport() {
	System.out.println ("CORBASupport");
    }

}

/*
 * <<Log>>
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */
