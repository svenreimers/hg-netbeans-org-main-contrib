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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import org.openide.util.*;
//import org.netbeans.modules.cvsclient.commands.*;
import javax.swing.event.TableModelEvent;

import java.util.*;
import org.netbeans.modules.vcscore.util.table.*;

/**
 *
 * @author  mkleint
 */
public class GrowingTableInfoModel extends TableInfoModel {
      
      public GrowingTableInfoModel() {
          super();
      }

    public GrowingTableInfoModel(int estimatedSize) {
        super(estimatedSize);
    }


      public void addElement(Object object)
      {
          super.addElement(object);
          fireTableRowsInserted(getRowCount(), getRowCount());
      }      
      
      public void prependElement(Object object) {
          super.prependElement(object);
          fireTableRowsInserted(0,0);
      }
}

