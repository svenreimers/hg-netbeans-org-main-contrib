/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.htmldiff;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

/** Computes differencies for two sets of files.
 *
 * @author  Jaroslav Tulach
 */
public final class ContentDiff extends Object {
    /** base URLs */
    private URL base1;
    private URL base2;
    
    /** names of all pages <String, Page> */
    private HashMap allPages;
    /** source to read or null */
    private Source source;
    /** map of dependencies between pages */
    private Map deps = new HashMap ();
    /** clusters of pages */
    private Cluster[] clusters;
    
    /** no instances outside */
    private ContentDiff (Source source, URL base1, URL base2) {
        this.source = source;
        this.base1 = base1;
        this.base2 = base2;
    }
    
    /** For a given file name finds a page that describes it.
     * @param name the name to search for
     * @return page for that name or null
     */
    public Page findPage (String name) {
        return (Page)allPages.get (name);
    }
    
    /** All pages in the compared documents.
     * @return Set<Page>
     */
    public Set getPages () {
        return new HashSet (allPages.values ());
    }
    

    /** The description of page clusters in this diff.
     * @return array of clusters sorted by dependencies
     */
    public Cluster[] getClusters () {
        if (clusters != null) {
            return clusters;
        }
        
        try {
            List sort = org.openide.util.Utilities.topologicalSort (allPages.keySet (), deps);
            
            // ok, no cycles in between pages
            clusters = new Cluster[sort.size ()];
            for (int i = 0; i < clusters.length; i++) {
                clusters[i] = new Cluster (Collections.singleton (findPage ((String)sort.get (i))));
            }
        } catch (org.openide.util.TopologicalSortException ex) {
            // ok, there are cycles
            
            Set[] sets = ex.topologicalSets();
            clusters = new Cluster[sets.length];
            for (int i = 0; i < clusters.length; i++) {
                HashSet s = new HashSet ();
                Iterator it = sets[i].iterator();
                while (it.hasNext ()) {
                    s.add (findPage ((String)it.next ()));
                }
                clusters[i] = new Cluster (s);
            }
        }
        
        // references <Page -> Cluster>
        HashMap refs = new HashMap ();
        for (int i = 0; i < clusters.length; i++) {
            java.util.Iterator it = clusters[i].getPages ().iterator ();
            while (it.hasNext()) {
                Page p = (Page)it.next ();
                refs.put (p, clusters[i]);
            }
        }
        
        for (int i = 0; i < clusters.length; i++) {
            Set references = new HashSet ();
            java.util.Iterator it = clusters[i].getPages ().iterator ();
            while (it.hasNext()) {
                Page contained = (Page)it.next ();
                Collection c = (Collection)deps.get (contained.getFileName());
                if (c != null) {
                    Iterator d = c.iterator();
                    while (d.hasNext()) {
                        String pageName = (String)d.next ();
                        references.add (refs.get (findPage (pageName)));
                    }
                }
            }
            // prevent self references
            references.remove (clusters[i]);
            clusters[i].references = (Cluster[])references.toArray (new Cluster[0]);
        }
        
        return clusters;
    }
    
    
    
    /** Parses the page for URL dependencies */
    private void parseHRefs (URL page, String name, URL base) throws IOException {
        Reader r = source.getReader (page);
        URL[] refs = ParseURLs.parse (r, page);
        
        String b = base.toExternalForm();
        for (int i = 0; i < refs.length; i++) {
            String e = refs[i].toExternalForm ();
            if (e.startsWith (b)) {
                String s = e.substring (b.length ());
                Collection c = (Collection)deps.get (name);
                if (c == null) {
                    c = new ArrayList ();
                    deps.put (name, c);
                }
                c.add (s);
            }
        }
    }
    
    /** Parses given set of files and creates groups of files 
     * refering sorted topologically with identification of how much
     * each page changed.
     *
     * @param base1 url to base search for the first set of files
     * @param files1 set of filenames <String> to parse
     * @param base2 url to base search for the second set of files
     * @param files2 set of filenames <String> to parse
     * @return result of comparation
     */
    public static ContentDiff diff (URL base1, Set files1, URL base2, Set files2, Source source) throws IOException {
        
        Set allFiles = new HashSet (files1);
        allFiles.addAll (files2);

        HashMap allPages = new HashMap (allFiles.size () * 4 / 3);
        
        ContentDiff result = new ContentDiff (source, base1, base2);
        
        Iterator names = allFiles.iterator();
        while (names.hasNext ()) {
            String fileName = (String)names.next ();
            URL f1 = new URL (base1, fileName);
            URL f2 = new URL (base2, fileName);
            
            boolean isIn1 = files1.contains (fileName);
            if (isIn1 != files2.contains (fileName)) {
                // page is either missing or added
                allPages.put (fileName, result.new Page (fileName, isIn1));
                if (isIn1) {
                    result.parseHRefs (f1, fileName, base1);
                } else {
                    result.parseHRefs (f2, fileName, base2);
                }
            } else {
                allPages.put (fileName, result.new Page (fileName));
                // parse the diffs
                result.parseHRefs (f1, fileName, base1);
                result.parseHRefs (f2, fileName, base2);
            }
        }

        result.allPages = allPages;
            
        return result;
    }
   
    
    
    /** Mapping from URLs to content. Useful for testing.
     */
    interface Source {
        /** Finds a reader for a given file name.
         */
        public java.io.Reader getReader (URL url) throws java.io.IOException;
    } // end of Source

    
    /** Describes a set of pages that seem to be related. E.g. refer to each 
     * other.
     */
    public final class Cluster extends Object {
        /** set of <String> */
        private Set pages;
        /** reference clusters */
        Cluster[] references;
        
        /** create instances just in this class */
        Cluster (Set pages) {
            this.pages = pages;
        }
        
        /** Names of pages that are the in the cluster.
         * @return set of <Page>
         */
        public Set getPages () {
            return pages;
        }
        
        /** Gets an array of clusters this one depends on
         */
        public Cluster[] getReferences () {
            return references;
        }
    } // end of Cluster
    
    
    /** Describes one page in old or new sources.
     */
    public final class Page extends Object {
        /** name of the page */
        private String name;
        /** added, removed or changed */
        private Boolean added;
        /** how much this page changed */
        private float change = -1;
        
        Page (String name) {
            this.name = name;
        }
        
        Page (String name, boolean removed) {
            this (name);
            added = removed ? Boolean.FALSE : Boolean.TRUE;
        }
        
        /** @return the file name of the page 
         */
        public String getFileName () {
            return name;
        }
        
        /** @return true if the page was added
         */
        public boolean isAdded () {
            return Boolean.TRUE.equals (added);
        }
        
        /** @return true if the page was removed
         */
        public boolean isRemoved () {
            return Boolean.FALSE.equals (added);
        }
        
        /** Writes the differences page into provided writer. 
         * @param w writer
         * @exception IOException if I/O fails
         */
        public void writeDiff (Writer w) throws IOException {
            Reader r1 = isAdded() ? new StringReader ("") : source.getReader (new URL (base1, getFileName()));
            Reader r2 = isRemoved () ? new StringReader ("") : source.getReader (new URL (base2, getFileName()));
            
            HtmlDiff[] res = HtmlDiff.diff (r1, r2);

            int len = 0;
            int diff = 0;
            for (int i = 0; i < res.length; i++) {
                if (res[i].isDifference()) {
                    // put there both
                    w.write ("<strike>");
                    w.write (res[i].getOld());
                    len += res[i].getOld ().length();
                    diff += res[i].getOld ().length();
                    w.write ("</strike><span style=\"background: #FFFF00\">");
                    w.write (res[i].getNew());
                    len += res[i].getNew ().length();
                    diff += res[i].getNew ().length();
                    w.write ("</span>");
                } else {
                    w.write (res[i].getNew ());
                    len += 2 * res[i].getNew ().length();
                }
            }
            r1.close ();
            r2.close ();
            
            if (change < 0.0) {
                change = ((float)diff) / ((float)len);
            }
        }
        
        /** Getter for the % of diffs in old and new version. It is wiser, 
         * but not necessary, to call {@link #writeDiff} first.
         *
         * @return value from 0.0 to 1.0
         */
        public float getChanged () {
            if (added != null) {
                return 1.0f;
            }
            
            if (change < 0.0) {
                // this method computes the change
                try {
                    writeDiff (new StringWriter ());
                } catch (IOException ex) {
                    org.openide.ErrorManager.getDefault ().notify (ex);
                }
            }
            return change < 0.0 ? 0.0f : change;
        }
        
    } // end of Page
}
