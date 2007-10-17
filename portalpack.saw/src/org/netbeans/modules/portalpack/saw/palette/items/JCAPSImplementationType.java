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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.saw.palette.items;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 *
 * @author Vihang
 */
public class JCAPSImplementationType implements SAWImplementationType {

    public JCAPSImplementationType() {
    }

    public SAWMethod getCheckOut1Tasks() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("checkOutTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" + "com.sun.saw.vo.CheckoutTaskVO checkoutTaskVO = new com.sun.saw.vo.CheckoutTaskVO();" + "       checkoutTaskVO.setTaskIdList(taskIdList);" + "checkoutTaskVO.setUserId(userId);" + "com.sun.saw.vo.OutputVO outputVO = null;" + "outputVO = workFlowManager.checkoutTasks(checkoutTaskVO);" + "return outputVO;  }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getCheckOutTasks() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("checkOutTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" 
                    + "com.sun.saw.vo.CheckoutTaskVO checkoutTaskVO = new com.sun.saw.vo.CheckoutTaskVO();" + 
                    "checkoutTaskVO.setTaskIdList(taskIdList);" + "checkoutTaskVO.setUserId(userId);" + 
                    "com.sun.saw.vo.OutputVO outputVO = null;" + 
                    "outputVO = workFlowManager.checkoutTasks(checkoutTaskVO);" +
                    "return outputVO;}";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getCheckInTasks() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("checkInTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{ com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" 
                    + "com.sun.saw.vo.OutputVO outputVO = null; " + 
                    "com.sun.saw.vo.CheckinTaskVO checkinTaskVO = new com.sun.saw.vo.CheckinTaskVO();" + "checkinTaskVO.setTaskIdList(taskIdList);" 
                    + "checkinTaskVO.setUserId(userId);" + 
                    "outputVO = workFlowManager.checkinTasks(checkinTaskVO);" + 
                    "return outputVO; }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getCompleteTasks() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("completeTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" +
                    "com.sun.saw.vo.OutputVO outputVO = null;" + 
                    "com.sun.saw.vo.CompleteTaskVO completeTaskVO = new com.sun.saw.vo.CompleteTaskVO();" + 
                    "completeTaskVO.setTaskIdList(taskIdList);" + 
                    "completeTaskVO.setUserId(userId);" + 
                    "outputVO = workFlowManager.completeTasks(completeTaskVO);" + 
                    "return outputVO; }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getCountTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SAWMethod getDeleteTask() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("deleteTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" + 
                    "com.sun.saw.vo.OutputVO outputVO = null;" + 
                    "com.sun.saw.vo.DeleteTaskVO deleteTaskVO = new com.sun.saw.vo.DeleteTaskVO();" + 
                    "deleteTaskVO.setTaskIdList(taskIdList);" + 
                    "deleteTaskVO.setUserId(userId);" + 
                    "outputVO = workFlowManager.deleteTasks(deleteTaskVO);" +
                    "return outputVO;  }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getEscalateTask() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("escalateTask");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" + 
                    "com.sun.saw.vo.OutputVO outputVO = null;" + 
                    "com.sun.saw.vo.EscalateTaskVO escalateTaskVO = new com.sun.saw.vo.EscalateTaskVO();" +
                    "escalateTaskVO.setTaskIdList(taskIdList);" + 
                    "escalateTaskVO.setUserId(userId);" + 
                    "outputVO = workFlowManager.escalateTasks(escalateTaskVO);" + 
                    "return outputVO; }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getGetTaskById() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SAWMethod getGetTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SAWMethod getReassignTasks() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("reassignTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        ParamObject paramObj2 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        paramObj2.setParamName("reassignUserIdList");
        paramObj2.setParamType("java.util.List");
        parametersVector.add(paramObj2);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" + 
                    "com.sun.saw.vo.OutputVO outputVO = null;" +
                    "com.sun.saw.vo.ReassignTaskVO reassignTaskVO = new com.sun.saw.vo.ReassignTaskVO();" + 
                    "reassignTaskVO.setTaskIdList(taskIdList);" + 
                    "reassignTaskVO.setUserId(userId);" + 
                    "reassignTaskVO.setReassignUserIdList(reassignUserIdList);" + 
                    "outputVO = workFlowManager.reassignTasks(reassignTaskVO);" + 
                    "return outputVO;  }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod getSaveTasks() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("saveTasks");
        Vector parametersVector = new Vector();
        ParamObject paramObj = new ParamObject();
        ParamObject paramObj1 = new ParamObject();
        ParamObject paramObj2 = new ParamObject();
        ParamObject paramObj3 = new ParamObject();
        paramObj.setParamName("userId");
        paramObj.setParamType("String");
        parametersVector.add(paramObj);
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        paramObj2.setParamName("output");
        paramObj2.setParamType("String");
        parametersVector.add(paramObj2);
        paramObj3.setParamName("customAttributesMap");
        paramObj3.setParamType("java.util.HashMap");
        parametersVector.add(paramObj3);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{ com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" +
                    "com.sun.saw.vo.OutputVO outputVO = null;" + 
                    "com.sun.saw.vo.SaveTaskVO saveTaskVO = new com.sun.saw.vo.SaveTaskVO ();" + 
                    "saveTaskVO.setTaskIdList(taskIdList);" + 
                    "saveTaskVO.setUserId(userId);" + 
                    "saveTaskVO.setOutput(output);" + 
                    "saveTaskVO.setCustomAttributesMap(customAttributesMap);" + 
                    "outputVO = workFlowManager.saveTasks(saveTaskVO);" + 
                    "return outputVO;} ";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }

    public SAWMethod showAuditHistory() {
        SAWMethod sawMethod = new SAWMethod();
        sawMethod.setMethodName("showAuditHistory");
        Vector parametersVector = new Vector();
        ParamObject paramObj1 = new ParamObject();
        paramObj1.setParamName("taskIdList");
        paramObj1.setParamType("java.util.List");
        parametersVector.add(paramObj1);
        sawMethod.setParameters(parametersVector);
        ArrayList exceptionList = new ArrayList();
        exceptionList.add("com.sun.saw.WorkflowException");
        sawMethod.setExceptionList(exceptionList);
        sawMethod.setReturnType("com.sun.saw.vo.OutputVO");
        StringWriter stringWriter = new StringWriter();
        String methodBody = new String();
        try {
            //    methodBody = Utils.mergeTemplateInString("checkout.template", null, stringWriter);
            methodBody = "{com.sun.saw.WorkFlowManager workFlowManager = com.sun.saw.WorkFlowManager.getInstance();" + 
                    "com.sun.saw.vo.AuditHistoryVO auditHistoryVO = new com.sun.saw.vo.AuditHistoryVO();" + 
                    "auditHistoryVO.setTaskIdList(taskIdList);" +
                    "return workFlowManager.showAuditHistory(auditHistoryVO); }";
        } catch (Exception e) {
            e.printStackTrace();
        }
        sawMethod.setMethodBody(methodBody);

        return sawMethod;
    }
}