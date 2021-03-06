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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.a11y;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;

import java.awt.Component;
import java.awt.Point;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;

import javax.accessibility.AccessibleContext;

/**
 *  A report generator for AccessibilityTester that will show the create a
 *  plain text report with the class, name, description and position of any
 *  component with accessibility problems.
 *
 *  @author Tristan Bonsall, Marian.Mirilovic@Sun.com
 */
public class TextReport extends AccessibilityTester.ReportGenerator{
    
    /** Create a TextReport for an AccessibilityTester.
     *  @param tester the AccesibilityTester */
    public TextReport(AccessibilityTester tester, TestSettings set){
        super(tester, set);
    }
    
    /**
     *  Generate the text report from the tests.
     *  <p>
     *  Reports are sent to a Writer, for instance:
     *  <ul>
     *  <li><code>new java.io.Writer(System.out)</code> will write to std out
     *  <li><code>new java.io.Writer(new java.io.FileWriter(new java.io.File("result.xml")))</code> will write to the file result.txt
     *  </ul>
     *
     *  @param out a Writer to send the report to
     */
    public void getReport(Writer writer){
        boolean fileWriter=false;
        
        if(writer instanceof FileWriter){
            fileWriter = true;
        }
        
        //System.err.println("============================== TextReport.getReport()");
        //Thread.dumpStack();
        
        
        PrintWriter out = getPrintWriter(writer);
        
        if(fileWriter) {
            //   out.println("<TestResults><![CDATA[");
            out.println("<HTML><HEAD>");
            out.println("<TITLE>Output from UIAccessibilityTester for window with title : "+testSettings.getWindowTitle()+" </TITLE>");
            out.println("</HEAD>");
            out.println("<BODY>");
            out.println("<PRE>");
            out.println("Results of Accessibility test, window with title \""+testSettings.getWindowTitle()+"\"");
        }else{
            out.println("Results of Accessibility test");
        }
        
        out.println();
        
        out.println("\n Doesn't implement Accessible :");
        printComponents(getNoAccess(),out, testSettings.accessibleInterface);
        
        out.println("\n No Accessible name :");
        printComponents(getNoName(),out, testSettings.AP_accessibleName);
        
        out.println("\n No Accessible description :");
        printComponents(getNoDesc(),out, testSettings.AP_accessibleDescription);
        
        out.println("\n Label with LABEL_FOR not set :");
        printComponents(getNoLabelFor(),out, testSettings.AP_labelForSet);
        
        out.println("\n Components with no LABEL_FOR pointing to it :");
        printComponents(getNoLabelForPointing(),out, testSettings.AP_noLabelFor);
        
        out.println("\n Components with no mnemonic :");
        printComponents(getNoMnemonic(),out, testSettings.AP_mnemonics);
        
        out.println("\n Components with wrong mnemonic (mnemonic isn't ASCII , label doesn't contain mnemonic):");
        printComponents(getWrongMnemonic(),out, testSettings.AP_mnemonics);
        
        Hashtable hs = getMnemonicConflict();
        if(!hs.isEmpty()){
            out.println("\n Components with potential mnemonics conflict:");
            
            Enumeration enumer = hs.keys();
            
            while(enumer.hasMoreElements()) {
                String key = (String)enumer.nextElement();
                char k = (char) Integer.parseInt(key);
                out.println(" - components with mnemonic '"+k+"' :");
                printComponents((HashSet)hs.get(key),out, testSettings.AP_mnemonics);
            }
            
        }
        
        out.println("\n Components not reachable with tab traversal :");
        printComponents(getNotTraversable(),out, testSettings.tabTraversal);
        
        if(Boolean.getBoolean("a11ytest.name")) {
            out.println("\n No Component name :");
            printComponents(getNoComponentName(), out, testSettings.test_name);
        }
        
        if(fileWriter){
            out.println("</PRE>");
            out.println("</BODY>");
            out.println("</HTML>");
            //  out.println("]]> </TestResults>");
            //  out.println("</Test>");
        }
        
        out.flush();
        /* Commented out because closing the writer for OutputWindow in NetBeans */
        /* erases the contents of the window. Uncomment in future if this changes */
        //out.close();
    }
    
    private void printComponents(HashSet components, PrintWriter pw, boolean tested) {
        if(!tested){
            pw.println("   - not tested.");
        } else {
            if (components.size() > 0){
                LinkedList componentsString = new LinkedList();
                
                Iterator i = components.iterator();
                while(i.hasNext()){
                    Component comp = (Component)(i.next());
                    componentsString.add(printComponentDetails(comp));
                }
                
                Collections.sort(componentsString);
                
                Iterator is = componentsString.iterator();
                while(is.hasNext()){
                    pw.println(is.next());
                }
                
                pw.println();
            }else{
                pw.println("   - none.");
            }
        }
    }
    
    
    /**
     *  Output the details of a component to the writer in text.
     */
    private String printComponentDetails(Component comp){
        StringBuffer componentPrintString = new StringBuffer("");
        
        String classname = comp.getClass().toString();
        if (classname.startsWith("class ")){
            classname = classname.substring(6);
        }
        componentPrintString.append("   Class: " + classname);
        
        if(printName || printDescription){
            componentPrintString.append(" { ");
            AccessibleContext ac = comp.getAccessibleContext();
            
            if (ac != null){
                
                String name = ac.getAccessibleName();
                if ((name != null) && printName){
                    componentPrintString.append(" " + name);
                }
                
                componentPrintString.append(" | ");
                
                String desc = ac.getAccessibleDescription();
                if ((desc != null) && printDescription){
                    componentPrintString.append(" " + desc);
                }
            }
            componentPrintString.append(" } ");
        }
        
        if(printPosition) {
            try{
                Point top = getTestTarget().getLocationOnScreen();
                Point child = comp.getLocationOnScreen();
                componentPrintString.append(" [" + (child.x - top.x) + "," + (child.y - top.y) + "]");
            } catch(Exception e){}
        }
        
        //componentPrintString.append("\n");
        
        return componentPrintString.toString();
    }
    
}