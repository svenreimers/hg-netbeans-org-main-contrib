
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */


package com.sun.tthub.processor;

import com.sun.tthub.util.DataConstants;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import java.util.ArrayList;

import com.sun.tthub.data.*;
/**
 *
 * @author choonyin
 */

/*
All 'Drill Down' requests are processed by the 'DrillDownRequestProcessor'. This
class takes the following 2 steps for processing the 'Drill Down' request.
 
a. Persist the state of the current page in a 'TemporaryPageState' object so
   that the page can be generated using this state whenever it is required. The
   state of the page stored is non-validated and it is required that the user be
   presented with exactly the same page when he returns from a drill down
   request. The StandardTempPageState of a page is generated by the
   StandardPageStateHandler object associated with a page. For the standard
   pages generated by the wizard, the StandardPageStateHandler and the
   StandardTempPageState objects are defined.
   <---------------------------To be implemented----------------------------->
   But, for a custom page that the user
   provides, the TemporaryPageStateHandler and the TemporaryPageState objects
   should be defined by the user.
   </---------------------------To be implemented----------------------------->
 
b. Identify the detailed data entry page to be presented to the user for
   capturing the value of the 'complex object'. Use the PageFormatter to
   generate the return page identified. (Generating the page means routing the
   request to the jsp corresponding to the return page. The remaining formatting
   activities happen inside the jsp)
 
 */

public class DrillDownReqProcessor {
    
    /** Creates a new instance of DrillDownReqProcessor */
    public DrillDownReqProcessor() {
    }
    
    /**
     * This function will perform the DrillDownRequest and fill the UseCaseState Objects with
     * the required values
     *
     * @return the page- return edithpathrepresentation string to the portlet for displaying the complex entry page
     **/
    public String processRequest(AppRequest appRequest,ActionRequest request){
        System.out.println("[DrillDownReqProcessor.processRequest Entry]");
        //// Persist the state of the current page in a 'StandardTempPageState' object and store in session
        ArrayList <GlobalError>globalErrors = new ArrayList<GlobalError>();
        String curEditPath=appRequest.getCurEditPath();
        String fieldName=request.getParameter("fieldName");
        try{
            
            StandardPageStateHandler pageHandler=new StandardPageStateHandler(request);
            pageHandler.extractTempPageState(curEditPath);
            
        }catch(Exception e){
            e.printStackTrace();
            GlobalError globalError=new GlobalError(e.getMessage());
            globalErrors.add(globalError);
        }
        
        String returnEditPath="";
        String statusMessage="";
        if( globalErrors.size()!=0){
            returnEditPath=curEditPath;
            statusMessage=DataConstants.FAILED_MESSAGE+": Unable to access " +fieldName;
        }else{
            if (curEditPath!=null && curEditPath.length() !=1)
                returnEditPath=new String(curEditPath+"/"+fieldName);
            else
                returnEditPath= new String (curEditPath+fieldName);
            statusMessage=DataConstants.SUCCESS_MESSAGE+": Please enter information for "+fieldName;
        }
        //Set Processing Result
        ReqProcessingResult result=new ReqProcessingResult();
        result.setGlobalErrors(globalErrors);
        result.setStatusMessage(statusMessage);
        PortletSession session= request.getPortletSession();
        session.setAttribute(DataConstants.REQPROCESSINGRESULT,result);
        
        return returnEditPath;
    }
}
