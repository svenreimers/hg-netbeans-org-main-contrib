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

package org.netbeans.modules.assistant;

import org.netbeans.modules.assistant.parsing.*;
//import org.openide.*;
import org.xml.sax.*;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.tree.*;
/*
 * AssistantContext.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantContext {
    private String name;
    private Vector ids;

    public AssistantContext(){
        this((AssistantID)null);
    }

    public AssistantContext(AssistantID id){
        ids = new Vector();
        if(id != null)
            addID(id);
    }
    
    public AssistantContext(String xmlFile){
        if(xmlFile == null)
            return;
        File file = new File(xmlFile);
        AssistantParser parser = new AssistantParser(new DefaultAssistantHandler(),null);
        try{
            parser.parse(new InputSource(new FileReader(xmlFile)));
        }catch(Exception e){
            System.err.println("AssistantContext 1: "+e);
            //ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    public AssistantContext(URL xmlURL){
        ids = new Vector();
        if (xmlURL == null)
            return;
        AssistantParser parser = new AssistantParser(new DefaultAssistantHandler(),null);
        try{
            parser.parse(xmlURL);
        }catch(Exception e){
            System.err.println("AssistantContext 2: "+e);
            //ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    public void addID(AssistantID id){
        ids.addElement(id);
    }
    public Enumeration getIDs(){
        Enumeration enum ;
        if(ids != null)
            enum = ids.elements();
        else
            enum = null;
        return enum;
    }
    
    public void addID(AssistantID[] id){
        if(id == null)
            return;
        for(int i = 0 ; i < id.length; i++){
            addID(id[i]);
        }
    }
    
    public class DefaultAssistantHandler implements AssistantHandler {
        private AssistantID id;
        private AssistantSection section;
        private AssistantItem item;
        
        public static final boolean DEBUG = false;
        
        public void handle_item(final java.lang.String data, final AttributeList meta) throws SAXException {            
            if (DEBUG) System.err.println("handle_item: " + data);
            URL url = null;
            url = getClass().getResource(meta.getValue("url"));
            String action = meta.getValue("action");
            int type = AssistantItem.LINK;
            if((meta.getValue("type") != null) &&(meta.getValue("type").equals("text")))
                type = AssistantItem.TEXT;
            item = new AssistantItem(data,url,action,type);
            section.add(new DefaultMutableTreeNode(item));                        
        }
        
        public void start_section(final AttributeList meta) throws SAXException {           
            if (DEBUG) System.err.println("start_section: " + meta);            
            String icon = meta.getValue("icon");            
            URL iconURL = null;
            if(icon != null)
                iconURL = getClass().getResource(icon);
            section = new AssistantSection(meta.getValue("name"),iconURL);
        }
        
        public void end_section() throws SAXException {
            if (DEBUG) System.err.println("end_section()"); 
            id.addSection(section);
        }
        
        public void start_assistant(final AttributeList meta) throws SAXException {
            if (DEBUG) System.err.println("start_assistant: " + meta);
        }
        
        public void end_assistant() throws SAXException {
            if (DEBUG) System.err.println("end_assistant()");
        }
        
        public void start_id(final AttributeList meta) throws SAXException {
            if (DEBUG) System.err.println("start_id: " + meta);
            id = new AssistantID(meta.getValue("name"));
        }
        
        public void end_id() throws SAXException {
            if (DEBUG) System.err.println("end_id()");
            addID(id);
        }
        
    }
}
