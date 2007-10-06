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

package org.netbeans.modules.tasklist.bugs.issuezilla;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.*;
import java.text.MessageFormat;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.awt.StatusDisplayer;
import org.xml.sax.SAXException;

import org.openide.util.NbBundle;

/**
 * A connection to Issuezilla. Connects to the database and provides
 * descriptions of issues. Is not thread safe, each thread should use
 * its own instance of Issuezilla.
 *
 * tor@netbeans.org:
 * This class is virtually identical to
 *  nbbuild/antsrc/org/netbeans/nbbuild/Issuezilla.java
 * At first, I inclouded its class file directly as part of
 * the build. However, treating Issuezilla as a black box
 * didn't work well because when connections fail (and are
 * retried), or even during a query, there is no feedback - and
 * since issuezilla is so slow, it's hard to know in the GUI
 * that things are working. Therefore, I've modified the java
 * file to give us a little bit more feedback.
 * In CVS I stored the original file as the first revision,
 * so you can easily diff to see what has changed - and generate
 * a patch which you can then apply to an updated version
 * of nbbuild/antsrc/ to keep the two in sync.
 *
 * @author Ivan Bradac, refactored by Jaroslav Tulach
 */
public final class Issuezilla extends java.lang.Object {
    /** url base of issuezilla. For netbeans it is 
     * "http://openide.netbeans.org/issues/"
     */
    private java.net.URL urlBase;
    /** sax parser to use */
    private SAXParser saxParser;

    /** maximum IO failures during connection to IZ */
    private int maxIOFailures = 15;
    
    private Vector proxyServer = null;
    
    private int lastProxy = -1;
   
   
    /** Creates new connection to issuezilla. The urlBase should
     * point to URL where issuezilla produces its XML results.
     * In case of NetBeans the URL is
     * <B>"http://openide.netbeans.org/issues/xml.cgi"</B>
     * @param urlBase a URI to issuezilla's XML service
     */
    public Issuezilla(java.net.URL urlBase) {
        this.urlBase = urlBase;
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance(); 
            factory.setValidating (false);
            saxParser = factory.newSAXParser();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException ("Cannot initialize parser");
        }
    }
    
    public void setProxyPool( String proxyPool ) {
        java.util.StringTokenizer tokens = new java.util.StringTokenizer( proxyPool, "," );
        
        proxyServer = new Vector();
        
        while ( tokens.hasMoreTokens() ) {
            proxyServer.add( tokens.nextToken() );         
        }
        rotateProxy();
    }
    
    private void rotateProxy() {
        if (proxyServer == null) return;
        
        if (proxyServer.size() == 0) return;
        
        if (lastProxy + 2 > proxyServer.size()) lastProxy = 0;
        else lastProxy++;
        
        String proxyString = (String) proxyServer.get( lastProxy );
        String host = proxyString.substring(0, proxyString.indexOf(':'));
        String port = proxyString.substring(proxyString.indexOf(':')+1);
  
        System.out.println("Rotating http proxy server to " + host + ":" + port);
        
        if (!port.equals("")) {
            System.getProperties ().put ("http.proxyPort", port);
        }
        if (!host.equals("")) {
            System.getProperties ().put ("http.proxyHost", host);
        }
    }
    
    /** Getter of an issue for given number.
     * @param number number of the issue
     * @return the issue 
     * @exception IOException if connection fails
     * @exception SAXException if parsing fails
     */
    public Issue getBug (int number) throws SAXException, IOException {
        Issue[] arr = getBugs (new int[] { number });        
        if (arr.length != 1) {
            throw new java.io.InvalidObjectException ("Issue not read");
        }
        
        return arr[0];
    }
    
    /** Getter of more issues at once.
     * @param numbers array of integers with numbers of bugs to retrieve
     * @return the issue array
     * @exception IOException if connection fails
     * @exception SAXException if parsing fails
     */
    public Issue[] getBugs (int[] numbers) throws SAXException, IOException {
        int maxIssuesAtOnce = 10;
        
        Issue[] result = new Issue[numbers.length];
        
        GLOBAL: for (int issueToProcess = 0; issueToProcess < numbers.length; ) {
            int lastIssueRightNow = Math.min (numbers.length, issueToProcess + maxIssuesAtOnce);
        
            StringBuffer sb = new StringBuffer (numbers.length * 8);
            String sep = "xml.cgi?id=";
            IOException lastEx = null;
            for (int i = issueToProcess; i < lastIssueRightNow; i++) {
                sb.append (sep);
                sb.append (numbers[i]);
                sep = ",";
            }
            sb.append ("&show_attachments=false");

            for (int iterate = 0; iterate < maxIOFailures; iterate++) {
                URL u = null;
                try {
                    u = new URL(urlBase, sb.toString());
                    
                    InputStream is = u.openStream();

                    Issue[] arr;
                    try {
                        arr = getBugs(is);
                    } finally {
                        is.close();
                    }
                    
                    // copy the results and go on
                    for (int i = 0; i < arr.length; ) {
                        result[issueToProcess++] = arr[i++];
                    }
                    

                    continue GLOBAL;
                }
                catch (IOException ex) {
                    synchronized ( this ) {
                        try {
                            StatusDisplayer.getDefault().setStatusText(
                                   MessageFormat.format(
                                    NbBundle.getMessage(Issuezilla.class, 
					     "CantConnect"), // NOI18N
				    new String[] { 
                                       new Date().toString(),
                                       urlBase.getHost()
                                   }));
                            rotateProxy();
                            this.wait( 5000 );
                        }
                        catch (InterruptedException ex1) {}
                    }
                    lastEx = ex;
                }
            }
        
            throw lastEx;
        } // end of GLOBAL
        
        return result;
    }
    
    /** Executes a query and returns array of issue numbers that fullfils the query.
     * @param query the query string that should be appended to the URL after question mark part
     * @return array of integers
     */
    public int[] query (String query) throws SAXException, IOException {
        URL u = new URL (urlBase, "buglist.cgi?" + query);
        IOException lastEx = null;
        BufferedReader reader = null;

        for (int iterate = 0; iterate < maxIOFailures; iterate++) {
            try {
                reader = new BufferedReader (
                    new InputStreamReader (u.openStream (), "UTF-8")
                );
            }
            catch (IOException ex) {
                synchronized ( this ) {
                    try {
                        StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(
                                Issuezilla.class, "CantConnect", // NOI18N
                                new Date().toString(),
                                urlBase.getHost()
                            )
                        );
                        rotateProxy();
                        this.wait( 5000 );
                    }
                    catch (InterruptedException ex1) {}
                }
                lastEx = ex;
            }
        }
        if (reader == null) {
            if (lastEx != null) throw lastEx;
            else throw new IOException("Can't get connection to " + u.toString() + " for " + maxIOFailures + "times.");
        }
        
        ArrayList result = new ArrayList ();
        
        String magic = "show_bug.cgi?id=";
        for (;;) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            
            int index = line.indexOf (magic);
            if (index == -1) {
                continue;
            }
            
            index += magic.length ();
            
            int end = line.indexOf ('"', index);
            if (end == -1) {
                throw new IOException ("No ending \" from index " + index + " in " + line);
            }
        
            String number = line.substring (index, end);
            StatusDisplayer.getDefault().setStatusText(
                       MessageFormat.format(
                                    NbBundle.getMessage(Issuezilla.class, 
					     "QueryBug"), // NOI18N
				    new String[] { 
                                       number
            }));

            
            result.add (Integer.valueOf (number));
        }
        
        int[] arr = new int[result.size ()];
        
        Iterator it = result.iterator ();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = ((Integer)it.next ()).intValue();
        }
        
        return arr;
    }
        
        
    
    /**
     * Gets the Issuezilla bugs from the InputStream.
     *
     * @return Issue[] objects from the InputStream containing
     * their XML representation.
     */
    private Issue[] getBugs(InputStream in)
    throws SAXException, IOException  {
        IssuezillaXMLHandler handler = new IssuezillaXMLHandler();
        saxParser.parse(in, handler);
        return getBugsFromHandler(handler);
    }
    
    /**
     * Gets the bugs form the handler. This must be called once the handler
     * finished its work.
     */
    private Issue[] getBugsFromHandler(IssuezillaXMLHandler handler) {
        List bugList = handler.getBugList();
        if (bugList == null) {
            return null;
        }
        Issue[] bugs = new Issue[bugList.size()];
        for (int i = 0; i < bugList.size(); i++) {
            Issue bug = new Issue();
            Map atts = (Map) bugList.get(i);
            Iterator it = atts.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next(); 
                bug.setAttribute((String) entry.getKey(), entry.getValue());
            }
            bugs[i] = bug;
        }
        return bugs;
    }
/*    
    public static void main (String[] args) throws Exception {
        Issuezilla iz = new Issuezilla (new URL ("http://www.netbeans.org/issues/"));
        
        
        //Issue[] arr = new Issue[] { iz.getBug (16000) };
        Issue[] arr = iz.getBugs (new int[] { 10001, 10000 });
        System.out.println("arr: " + arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(i + " = " + arr[i]);
        }
    }
*/

/*    Query *
    public static void main (String[] args) throws Exception {
        Issuezilla iz = new Issuezilla (new URL ("http://www.netbeans.org/issues/"));
        iz.setProxyPool("webcache.czech.sun.com:8080,webczech.uk.sun.com:8080,webcache.holland.sun.com:8080");
        

        int[] res = iz.query ("issue_status=NEW&issue_status=ASSIGNED&issue_status=STARTED&issue_status=REOPENED&email1=tulach&emailtype1=substring&emailassigned_to1=1&email2=&emailtype2=substring&emailreporter2=1&issueidtype=include&issue_id=&changedin=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&short_desc=&short_desc_type=substring&long_desc=&long_desc_type=substring&issue_file_loc=&issue_file_loc_type=substring&status_whiteboard=&status_whiteboard_type=substring&field0-0-0=noop&type0-0-0=noop&value0-0-0=&cmdtype=doit&newqueryname=&order=%27Importance%27");
        
        String sep = "";
        for (int i = 0; i < res.length; i++) {
            System.out.print(sep);
            System.out.print(res[i]);
            sep = ", ";
        }
        System.out.println();
    }
/**/

    /**
     * Retrieves component names
     * @param base service URL e.g. <code>http://tasklist.netbeans.org/issues/</code>
     */
    public static String[] getComponents(URL base) {
        List components = new ArrayList(23);
        try {
            URL list = new URL(base, "enter_bug.cgi");
            URLConnection io = list.openConnection();
            io.connect();
            InputStream in = new BufferedInputStream(io.getInputStream());
            int next = in.read();
            StringBuffer sb = new StringBuffer();
            while (next != -1) {
                sb.append((char) next);
                next = in.read();
            }

            // parse output looking for componet names by MAGIC

            String sample = sb.toString();
            String MAGIC =  "enter_bug.cgi?component=";  // NOi18N

            int entry = 0;
            int end = -1;
            while (true) {
                entry = sample.indexOf(MAGIC, entry);
                if (entry == -1) break;
                end = sample.indexOf("\"", entry);
                if (entry == -1) break;
                String component = sample.substring(entry + MAGIC.length(), end);
                entry = end;
                components.add(component);
            }
            return (String[]) components.toArray(new String[components.size()]);

        } catch (MalformedURLException e) {
            return new String[0];
        } catch (IOException e) {
            return new String[0];
        }
    }

}
