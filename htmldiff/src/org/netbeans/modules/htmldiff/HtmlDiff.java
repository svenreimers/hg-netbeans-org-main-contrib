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

package org.netbeans.modules.htmldiff;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Computes HTML based diff.
 *
 * @author  Jaroslav Tulach
 */
public final class HtmlDiff extends Object {
    private String oldText;
    private String newText;

    /** same text
     */
    private HtmlDiff (String same) {
        this (same, same);
    }

    /** Creates a new instance of HtmlDiff */
    private HtmlDiff(String o, String n) {
        this.oldText = o;
    this.newText = n;
    }
    
    
    /** Checks if this is a difference or just the same area.
     */
    public boolean isDifference () {
        return oldText != newText;
    }
    
    /** Gets the old text.
     */
    public String getOld () {
        return oldText;
    }
    
    /** Gets the new one.
     */
    public String getNew () {
        return newText;
    }
    
    
    /** Computes the differences for old and new streams.
     * @param old old stream of HTML document
     * @param new new stream of HTML document
     * @exception IOException if there is I/O problem
     */
    public static HtmlDiff[] diff (Reader old, Reader current) throws IOException {
        List oldSentences = sententize (old);
        List newSentences = sententize (current);
        
        HtmlDiff[] arr = doDiff (oldSentences, newSentences, false);
        
        ArrayList compare = new ArrayList (arr.length);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].isDifference ()) {
                List oldArr = wordize (new StringReader (arr[i].getOld ()));
                List newArr = wordize (new StringReader (arr[i].getNew ()));
                
                HtmlDiff[] diffs = doDiff (oldArr, newArr, true);

                compare.addAll (java.util.Arrays.asList (diffs));
            } else {
                if (arr[i].getNew ().length () > 0) {
                    // just copy
                    compare.add (arr[i]);
                }
            }
        }
        
        return (HtmlDiff[])compare.toArray (new HtmlDiff[0]);
    }
    
    /** Compares items.
     */
    private static HtmlDiff[] doDiff (List oldArr, List newArr, boolean ignoreTags) throws IOException {
        
        org.netbeans.spi.diff.DiffProvider diff = (org.netbeans.spi.diff.DiffProvider)org.openide.util.Lookup.getDefault().lookup(org.netbeans.spi.diff.DiffProvider.class);
        if (diff == null) {
            diff = new org.netbeans.modules.diff.builtin.provider.BuiltInDiffProvider ();
        }
        org.netbeans.api.diff.Difference[] arr = diff.computeDiff(toStream (oldArr), toStream (newArr));

        
        ArrayList res = new ArrayList ();

        int[] oldArrIndex = { 0 };
        int[] newArrIndex = { 0 };
        for (int i = 0; i < arr.length; i++) {
            int os = arr[i].getFirstStart();
            int ns = arr[i].getSecondStart();

            switch (arr[i].getType ()) {
            case org.netbeans.api.diff.Difference.ADD:    
                ns--;
                break;
            case org.netbeans.api.diff.Difference.CHANGE:
                ns--;
                os--;
                break;
            case org.netbeans.api.diff.Difference.DELETE:
                os--;
                ns--;
                break;
            default:
                throw new IllegalStateException ();
            }
            
            // same text
            String oldText = toHTML (oldArr, oldArrIndex, os, null);
            String newText = toHTML (newArr, newArrIndex, ns, null);
            /*
            if (!oldText.equals (newText)) {
                throw new IllegalStateException ("Should be the same: " + oldText + " new: " + newText);
            }
             */
            
            if (
                oldText == null || oldText.equals (newText) ||
                (ignoreTags && oldText.startsWith ("<")) // NOI18N
            ) {
                if (newText.length () > 0) {
                    res.add (new HtmlDiff (newText));
                }
            } else {
                res.add (new HtmlDiff (oldText, newText));
            }
            
            // process the difference
            int oe = arr[i].getFirstEnd ();
            int ne = arr[i].getSecondEnd ();
            
            ArrayList oldItems = new ArrayList ();
            ArrayList newItems = new ArrayList ();

            toHTML (oldArr, oldArrIndex, oe, oldItems);
            toHTML (newArr, newArrIndex, ne, newItems);
            
            int max = Math.max (oldItems.size (), newItems.size ());
            for (int j = 0; j < max; j++) {
                oldText = oldItems.size () > j ? (String)oldItems.get (j) : null;
                newText = newItems.size () > j ? (String)newItems.get (j) : "";
                
                if (
                    oldText == null || oldText.equals (newText) ||
                    (ignoreTags && oldText.startsWith ("<")) // NOI18N
                ) {
                    if (newText.length () > 0) {
                        res.add (new HtmlDiff (newText));
                    }
                } else {
                    res.add (new HtmlDiff (oldText, newText));
                }
            }
        }
        
        // same text
        String oldText = toHTML (oldArr, oldArrIndex, Integer.MAX_VALUE, null);
        String newText = toHTML (newArr, newArrIndex, Integer.MAX_VALUE, null);
        
        if (newText.length() != 0) {
/*
        if (!oldText.equals (newText)) {
            throw new IllegalStateException ("Should be the same: " + oldText + " new: " + newText);
        }
*/
            res.add (new HtmlDiff (newText));
        }
        
        return (HtmlDiff[])res.toArray (new HtmlDiff[0]);
    }
    
    
    /** Converts the stream to pieces of words.
     * @param r strea of characters
     * @return List<Item> of items
     *
     */
    private static List wordize (Reader r) throws IOException {
        BufferedReader buf = new BufferedReader (r);
        
        ArrayList arr = new ArrayList ();
        int state = -1;
        StringBuffer word = null;
        int sectionPre = 0;
        for (;;) {
            int ch = buf.read ();
            if (ch == -1) break;

            switch (state) {
            case 1: // character
                if (!Character.isSpaceChar ((char)ch) && ch != '<' && ch != '\n') {
                    word.append ((char)ch);
                    break;
                } else {
                    arr.add (newWord (word.toString()));
                    word = null;
                    state = -1;
                    // fall thru
                }
            case -1: // undecided state
            case 0: // white line
                if (!Character.isSpaceChar ((char)ch) && ch != '<' && ch != '\n') {
                    if (state == 0 && sectionPre == 0) {
                        // there was a while line
                        arr.add (newSpace ());
                    }
                    word = new StringBuffer ();
                    word.append ((char)ch);
                    state = 1;
                    break;
                }
                if (ch == '<') {
                    if (state == 0 && sectionPre == 0) {
                        // there was a while line
                        arr.add (newSpace ());
                    }
                    state = 2;
                    word = new StringBuffer ();
                    word.append ('<');
                    break;
                }
                if (ch == '\n') {
                    arr.add (newLine ());
                    break;
                }
                // regular space
                state = 0;
                if (sectionPre > 0) {
                    arr.add (newSpace ());
                }
                break;
            case 2: // search for end of tag
                word.append ((char)ch);
                if (ch == '>') {
                    String tag = word.toString();
                    arr.add (newTag (tag));
                    word = null;
                    state = -1;
                    if ("<PRE>".equals (tag.toUpperCase())) {
                        sectionPre++;
                    }
                    if ("</PRE>".equals (tag.toUpperCase())) {
                        sectionPre--;
                    }
                    break;
                }
                break;
            }
        }
        
        if (word != null) {
            arr.add (newWord (word.toString()));
        }
        
        return arr;
    }
    
    private static boolean isDot (char ch) {
        return ch == '.' || ch == ',' || ch == '?' || ch == '!';
    }
    
    /** Converts the stream to pieces of sentences.
     * @param r stream of characters
     * @return List<Item> of items
     *
     */
    private static List sententize (Reader r) throws IOException {
        BufferedReader buf = new BufferedReader (r);
        
        ArrayList arr = new ArrayList ();
        int state = 0;
        int prevState = -1;
        StringBuffer sentence = null;
        for (;;) {
            int ch = buf.read ();
            if (ch == -1) break;

            switch (state) {
                case 0: // spaces
                    if (!Character.isWhitespace ((char)ch)) {
                        state = 1;
                        sentence = new StringBuffer ();
                        // fallthru
                    } else {
                        break;
                    }
                case 1: // character
                    sentence.append ((char)ch);
                    if (ch == '<') {
                        prevState = state;
                        state = 2;
                        break;
                    }
                    
                    if (isDot ((char)ch)) {
                        arr.add (newWord (sentence.toString()));
                        state = 0;
                        sentence = null;
                        break;
                    }
                    break;
                case 2: // search for end of tag
                    sentence.append ((char)ch);
                    if (ch == '>') {
                        state = prevState;
                        break;
                    }
                    break;
                default:
                    throw new IllegalStateException ("state: " + state); // NOI18N
            }
        }
        
        if (sentence != null) {
            arr.add (newWord (sentence.toString()));
        }
        
        return arr;
    }
    
    /** debugging of diff streams */
    private static int debugStream = -1;
    
    /** Converts the text into a stream.
     */
    private static Reader toStream (List items) throws IOException {
        StringWriter to = new StringWriter ();
        
        PrintWriter pw = null;
        if (debugStream >= 0) {
            pw = new PrintWriter (new FileOutputStream (new File ("out-" + debugStream++ + ".txt")));
        }
        Iterator it = items.iterator();
        int[] counter = { 0 };
        while (it.hasNext()) {
            Item i = (Item)it.next ();
            if (pw != null) {
                i.printDiff (pw, new int[1]);
            }
            i.printDiff (to, counter);
        }
        if (pw != null) {
            pw.flush ();
        }
        
        return new StringReader (to.toString ());
    }
    
    /** Gets HTML.
     * @param nonexlusive in exclusive mode it is forbidden to mix different types (e.g. words with tags)
     */
    private static String toHTML (List items, int[] fromPosition, int toIndex, Collection toAdd) throws IOException {
        StringWriter w = new StringWriter ();
        boolean addSpace = false;
        boolean firstOrEager = true;
        int type = -1;
        while (fromPosition[0] < items.size ()) {
            Item item = (Item)items.get (fromPosition[0]);
            
            if (type != -1 && toAdd != null && type != item.type && item.isVisible()) {
                toAdd.add (w.toString ());
                w = new StringWriter ();
                type = -1;
            }
            
            if (firstOrEager && item.index >= toIndex) {
                break;
            }
            firstOrEager = toAdd == null;
            
            addSpace = item.printHTML (w, addSpace);
            if (item.isVisible ()) {
                type = item.type;
            }
                
            fromPosition[0]++;
            
            if (toAdd != null && item.index != -1 && item.index + 1 >= toIndex) {
                break;
            }
        }
        if (toAdd != null) {
            toAdd.add (w.toString ());
        }
        return w.toString ();
    }   
    
    /** In list of items finds the one with given index.
     */
    private static int findIndex (List where, int which, int from) {
        int i = from;
        while (where.size () > i) {
            Item item = (Item)where.get (i);
            if (item.index == which) {
                return i;
            }
            i++;
        }
        return from + 1;
    }
    
    private static Item newLine () {
        return new Item (0x01, null);
    }
    private static Item newSpace () {
        return new Item (0x02, null);
    }
    private static Item newWord (String string) {
        return new Item (0x04, string);
    }
    private static Item newTag (String string) {
        return new Item (0x08, string);
    }
    
    /** Item in HTML document
     */
    private static class Item extends Object {
        private int type;
        private String value;
        private int index = -1;
        
        public Item (int type, String value) {
            this.type = type;
            this.value = value;
        }
        
        public String toString () {
            return type + " value: " + value;
        }
        
        /** Checks whehter the type of item is visible.
         */
        public boolean isVisible () {
            return (type & (0x04 | 0x08)) != 0;
        }

        /** Prints to diff file
         */
        public void printDiff (Writer w, int[] counter) throws IOException {
            switch (type) {
                case 0x02:
                case 0x01: break; // new line
                case 0x04: 
                    w.write (value); 
                    w.write ('\n'); 
                    index = counter[0]++;
                    break; // word
                case 0x08: break; // no tags
                default: 
                    throw new IllegalArgumentException ();
            }
        }
        
        /** Prints to Html 
         * @return true whether there should be a space
         */
        public boolean printHTML (Writer w, boolean needsSpace) throws IOException {
            switch (type) {
                case 0x01: 
                    w.write ('\n');
                    needsSpace = false;
                    break; // new line
                case 0x02: 
                    w.write (' ');
                    needsSpace = false;
                    break; // space
                case 0x04: 
                    if (needsSpace) {
                        w.write (' ');
                    }
                    w.write (value); 
                    needsSpace = true;
                    break; // word
                case 0x08: 
                    w.write (value);
                    needsSpace = false;
                    break; // no tags
                default: 
                    throw new IllegalArgumentException ();
            }
            return needsSpace;
        }
    } // end of Item
}
