/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import java.io.File;
import javax.swing.text.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;

import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.URLMapper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** 
 * Various utility methods shared by the various tasklist related modules
 *
 ** TODO - use this method everywhere!!!
 *
 * @author Tor Norbye 
 */
public final class TLUtils {
    private static Logger LOGGER = TLUtils.getLogger(TLUtils.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }

    /** Return the Line object for a particular line in a file
     */
    public static Line getLineByNumber(DataObject dobj, int lineno) {
        // Go to the given line
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc != null) {
                Line.Set ls = lc.getLineSet();
                if (ls != null) {
                    // XXX HACK
                    // I'm subtracting 1 because empirically I've discovered
                    // that the editor highlights whatever line I ask for plus 1
                    Line l = ls.getCurrent(lineno-1);
                    return l;
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            // line was at the end of file and is deleted now

        } catch (Exception e) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "getLineByNumber - file " + dobj + " and lineno=" + lineno); // NOI18N
            ErrorManager.getDefault().
                notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
    }

    /** Replace the given symbol on the line with the new symbol - starting
        roughly at the given column (symbol should be at col or col+1)
        @param sb Buffer to write into
        @param text The text to be copied into the buffer, except for
            the substitution of symbol into newSymbol.
        @param pos Earliest possible starting position of the symbol
        @param symbol The symbol which may occur multiple times; we want
            each reference replaced (provided it's a java identifier - not
            a prefix or suffix of a larger identifier
        @param newSymbol The string to replace the old symbol
        @param bold If true, make the new symbol bold
        @param underlineBegin If -1, underline the newSymbol starting at
            this position, ending at underlineEnd.
        @param underlineEnd Only considererdd if underlineBegin != -1; 
            ending position for underlining started at underlineBegin.
    */
    public static void replaceSymbol(StringBuffer sb, String text, int pos, String symbol, 
                                     String newSymbol, boolean bold,
                                     int underlineBegin, int underlineEnd) {
        //System.out.println("replace('" + text + "', " + pos + ", '" + symbol + "', '" + newSymbol + "')");
        if (pos > 0) {
            // For some compilers, the position is off by 1 so make sure 
            // we catch the earliest possible match
            pos--;
        }
        int from = 0;
        int symLen = symbol.length();
        int texLen = text.length();
        while (true) {
            int n = text.indexOf(symbol, pos);
            if (n == -1) {
                break;
            }
            if ((n+symLen < texLen-1) &&
                Character.isJavaIdentifierPart(text.charAt(n+symLen))) {
                pos = n+symLen;
                continue;
            }
            
            for (int i = from; i < n; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
            if (bold) {
                sb.append("<b>"); // NOI18N
            }
            if (underlineBegin != -1) {
                for (int i = 0; i < underlineBegin; i++) {
                    appendHTMLChar(sb, newSymbol.charAt(i));
                }
                sb.append("<u>"); // NOI18N
                for (int i = underlineBegin; i < underlineEnd; i++) {
                    appendHTMLChar(sb, newSymbol.charAt(i));
                }
                sb.append("</u>"); // NOI18N
                int nl = newSymbol.length();
                for (int i = underlineEnd; i < nl; i++) {
                    appendHTMLChar(sb, newSymbol.charAt(i));
                }
            } else {
                appendHTMLString(sb, newSymbol);
            }
            if (bold) {
                sb.append("</b>"); // NOI18N
            }
            pos = n+symLen;
            from = pos;
        }
        for (int i = from; i < texLen; i++) {
            appendHTMLChar(sb, text.charAt(i));
        }
    }

    /** Append a "window of text with the given line as the middle line.
     * It will escape HTML characters.
     * @param line The line we want to obtain a window for.
     * @param currText If non null, use this line instead of the
     *     text on the current line.
     */
    public static void appendSurroundingLine(StringBuffer sb, Line line, 
                                             int offset) {
        DataObject dobj = org.openide.text.DataEditorSupport.findDataObject (line);
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc == null) {
                return;
            }
            Line.Set ls = lc.getLineSet();
            if (ls == null) {
                return;
            }

            int lineno = line.getLineNumber();
            if (lineno+offset < 0) {
                // Trying to surround the first line - no "before" line
                return;
            }
            Line before = ls.getCurrent(lineno+offset);
            appendHTMLString(sb, before.getText());
        } catch (Exception e) {
            ErrorManager.getDefault().
                notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    /** Compute first difference position for two strings */
    public static int firstDiff(String s1, String s2) {
        int n1 = s1.length();
        int n2 = s2.length();
        int n;
        if (n1 < n2) {
            n = n1;
        } else {
            n = n2;
        }
        for (int i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /** Compute last difference position for two strings. Returns
        DISTANCE FROM THE END! */
    public static int lastDiff(String s1, String s2) {
        int n1 = s1.length()-1;
        int n2 = s2.length()-1;
        int i = 0;
        while ((n2 >= 0) && (n1 >= 0)) {
            if (s1.charAt(n1) != s2.charAt(n2)) {
                return i;
            }
            --n2;
            --n1;
            ++i;
        }
        return i;
    }    
    
    /** Append a character to a StringBuffer intended for HTML
        display - it will escape <, >, etc. such that the char is
        shown properly in HTML.
    */
    public static void appendHTMLChar(StringBuffer sb, char c) {
        // See also HTMLSupport.toHTML if you modify this
        switch (c) {
        case '<': sb.append("&lt;"); break; // NOI18N
        case '>': sb.append("&gt;"); break; // NOI18N
        case '&': sb.append("&amp;"); break; // NOI18N
        case '"': sb.append("&quot;"); break; // NOI18N
        case ' ': sb.append("&nbsp;"); break; // NOI18N
        case '\n': sb.append("<br>"); break; // NOI18N
        default: sb.append(c);
        }
    }

    /** Append a string to a StringBuffer intended for HTML
        display - it will escape <, >, etc. such that they are
        shown properly in HTML.
    */
    public static void appendHTMLString(StringBuffer sb, String s) {
        int n = s.length();
        for (int i = 0; i < n; i++) {
            appendHTMLChar(sb, s.charAt(i));
        }
    }

    /** Append the given string to the given string buffer,
     * underlining from a starting index to an ending index.
     * Also escape HTML characters.
     */
    public static void appendAttributed(StringBuffer sb,
                                        String text, 
                                        int begin, 
                                        int end,
                                        boolean underline,
                                        boolean bold) {
        if (begin != -1) {
            for (int i = 0; i < begin; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
            if (underline) {
                sb.append("<u>"); // NOI18N
            }
            if (bold) {
                sb.append("<b>"); // NOI18N
            }
            for (int i = begin; i < end; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
            if (underline) {
                sb.append("</u>"); // NOI18N
            }
            if (bold) {
                sb.append("</b>"); // NOI18N
            }
            int nl = text.length();
            for (int i = end; i < nl; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
        } else {
            appendHTMLString(sb, text);
        }
    }

    public static Element getElement(Document d, Line line) {
	if (d == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "d was null");
            return null;
	}

        if (!(d instanceof StyledDocument)) {
            ErrorManager.getDefault().log(ErrorManager.USER, "Not a styleddocument");
            return null;
        }
            
        StyledDocument doc = (StyledDocument)d;
        Element e = doc.getParagraphElement(0).getParentElement();
        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement ();
        }
        int lineNumber = line.getLineNumber();
        Element elm = e.getElement(lineNumber);
        return elm;
    }

    public static Document getDocument(Line line) {
        DataObject dao = org.openide.text.DataEditorSupport.findDataObject (line);
        if (!dao.isValid()) {
            //ErrorManager.getDefault().log(ErrorManager.USER, "dataobject was not null");
            return null;
        }
        return getDocument(dao);
    }

    public static Document getDocument(DataObject dao) {
	final EditorCookie edit = (EditorCookie)dao.getCookie(EditorCookie.class);
	if (edit == null) {
            //ErrorManager.getDefault().log(ErrorManager.USER, "no editor cookie!");
	    return null;
	}

        Document d = edit.getDocument(); // Does not block
        return d;
    }

    /** Remove a particular line. Make sure that the line begins with
     * a given prefix, just in case.
     * @param prefix A prefix that the line to be deleted must start with
     */
    public static boolean deleteLine(Line line, String prefix) {
        Document doc = getDocument(line);
        Element elm = getElement(doc, line);
        if (elm == null) {
            return false;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset-offset);
            if (!text.startsWith(prefix)) {
                return false;
            }
            doc.remove(offset, endOffset-offset);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return false;
    }

    /** Comment out a particular line (in a Java file). Make sure that
     * the line begins with a given prefix, just in case.
     * @param prefix A prefix that the line to be commented out must start with
     */
    public static boolean commentLine(Line line, String prefix) {
        Document doc = getDocument(line);
        Element elm = getElement(doc, line);
        if (elm == null) {
            return false;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset-offset);
            if (!text.startsWith(prefix)) {
                return false;
            }
            doc.insertString(offset, "// ", null); // NOI18N
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return false;
    }

    /** Given a file object, produce a URL suitable for inclusion
     * in a tasklist (e.g. it must be persistent, not based on some
     * currently assigned webserver port etc.) */
    public static String toURL(FileObject fo) {
        // Try to construct our own URL since 
        /*
        File file = FileUtil.toFile(fo);
        String filename;
        if (file == null) {
            URL url = URLMapper.findURL(fo, URLMapper.INTERNAL);
            filename = url.toExternalForm();
            // System.out.println("INTERNAL URL was " + filename);
            // url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
            // filename = url.toExternalForm();
            // System.out.println("EXTERNAL URL was " + filename);
            // url = URLMapper.findURL(fo, URLMapper.NETWORK);
            // filename = url.toExternalForm();
            // System.out.println("NETWORK URL was " + filename);
            return null;
        } else {
            filename = file.toURI().toString();
        }
        return filename;
        */
        return URLMapper.findURL(fo, URLMapper.INTERNAL).toExternalForm();
    }

    /** Given a URL created by fileToURL, return a file object representing
     * the given file. Returns null if the URL can (no longer) be resolved.
     */
    public static FileObject[] fromURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
        return URLMapper.findFileObjects(url);
    }
    
    /**
     * Counts all children nodes of the given node recursively.
     *
     * @return node a node or null
     */
    public static int getChildrenCountRecursively(Node node) {
        if (node == null) return 0;
        
        Children children = node.getChildren();
        if(children.getNodesCount() == 0) return 0;

        int n = 0;
        Node[] nodes = children.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            n += getChildrenCountRecursively(nodes[i]) + 1;
        }
        return n;
    }
    
    /**
     * Gets a property descriptor for the specified property name.
     *
     * @param n a node
     * @param prop name of a property
     * @return found property or null
     */
    public static Node.Property getProperty(Node n, String prop) {
        Node.PropertySet[] propsets = n.getPropertySets();
        for (int j = 0; j < propsets.length; ++j) {
            Node.Property[] props = propsets[j].getProperties();
            for (int k = 0; k < props.length; ++k) {
                if (props[k].getName().equals(prop)) {
                    return props[k];
                }
            }
        }
        return null;
    }


    private static boolean currFound;

    /**
     * Locate the next task from the given task.
     * Used for example to jump to the previous or next error when
     * the user presses F12/S-F12.  This will skip over category
     * nodes etc.
     *
     * @param curr The current task from which you want to find
     *   a neighbor
     * @param wrap If true, wrap around the end/front of the list
     *    and return the next/previous element. If false, return null
     *    when you reach the end or the front of the list, depending
     *    on your search direction.
     * @return the next element following curr that is
     *    not a category node */
    public static synchronized Task findNext(Task curr, boolean wrap) {
        currFound = false;
        List tasks = curr.getList().getRoot().getSubtasks();
        if (tasks == null)
            return null;
        
        Task s = findNext(tasks, curr, wrap);
        if ((s == null) && wrap && currFound) {
            // Start search one more time, this time not for
            // curr but just the first eligible element
            s = findNext(tasks, curr, wrap);
        }
        return s;
    }

    private static Task findNext(List tasks, Task curr, boolean wrap) {
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task s = (Task) it.next();
            if (currFound && s.isVisitable()) {
                return s;
            } else if (s == curr) {
                currFound = true;
                if (s.hasSubtasks()) {
                    Task f = findNext(s.getSubtasks(), curr, wrap);  // recursion
                    if (f != null) {
                        return f;
                    }
                }
            } else if (s.hasSubtasks()) {
                Task f = findNext(s.getSubtasks(), curr, wrap); // recursion
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Locate the previous task from the given task.
     * Used for example to jump to the previous or next error when
     * the user presses F12/S-F12.  This will skip over category
     * nodes etc.
     *
     * @param curr The current task from which you want to find
     *   a neighbor.
     * @param wrap If true, wrap around the end/front of the list
     *    and return the next/previous element. If false, return null
     *    when you reach the end or the front of the list, depending
     *    on your search direction.
     * @return the element preceding curr that is
     *    not a category node */
    public static synchronized Task findPrev(Task curr, boolean wrap) {
        currFound = false;
        List tasks = curr.getList().getRoot().getSubtasks();
        Task s = findPrev(tasks, curr, wrap);
        if ((s == null) && wrap && currFound) {
            // Start search one more time, this time not for
            // curr but just the first eligible element
            s = findPrev(tasks, curr, wrap);
        }
        return s;
    }

    /**
     * @todo This method is broken for lists deeper than two levels!
     * Luckily they're pretty rare - suggestions window doesn't have them,
     * the buglist window doesn't have them, the source scan window doesn't
     * have them - the user tasks window is the only candidate, and even there
     * I suspect people aren't doing multi-level categorization.
     */
    private static Task findPrev(List tasks, Task curr, boolean wrap) {
        ListIterator it = tasks.listIterator(tasks.size());
        while (it.hasPrevious()) {
            Task s = (Task) it.previous();
            if (currFound && s.isVisitable()) {
                return s;
            } else if (s == curr) {
                currFound = true;
                if (s.hasSubtasks()) {
                    Task f = findPrev(s.getSubtasks(), curr, wrap);  // recursion
                    if (f != null) {
                        return f;
                    }
                }
            } else if (s.hasSubtasks()) {
                Task f = findPrev(s.getSubtasks(), curr, wrap); // recursion
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Utility method which attempts to find the activated nodes
     *	for the currently showing topcomponent in the editor window.
     *
     * @return editor nodes or null
     */
    public static Node[] getEditorNodes() {
        // First try to get the editor window itself; if you right click
        // on a node in the Todo Window, that node becomes the activated
        // node (which is good - it makes the properties window show the
        // todo item's properties, etc.) but that means that we can't
        // find the editor position via the normal means.
        // So, we go hunting for the topmosteditor tab, and when we find it,
        // ask for its nodes.
        Node[] nodes = null;
        WindowManager wm = WindowManager.getDefault();
        
        // HACK ALERT !!! HACK ALERT!!! HACK ALERT!!!
        // Look for the source editor window, and then go through its
        // top components, pick the one that is showing - that's the
        // front one!
        Mode mode  = wm.findMode(CloneableEditorSupport.EDITOR_MODE);
        if (mode == null) {
            return null;
        }
        TopComponent [] tcs = mode.getTopComponents();
        for (int j = 0; j < tcs.length; j++) {
            TopComponent tc = tcs[j];
            if (tc instanceof CloneableEditor) {
                // Found the source editor...
                if (tcs[j].isShowing()) {
                    nodes = tcs[j].getActivatedNodes();
                    break;
                }
            }
        }
        return nodes;
    }

    /**
     * Finds cursor position.
     *
     * @param nodes nodes to search. May be null
     * @return Object[2]:
     *     [0] - File name as String
     *     [1] - line number as Integer
     * Returns null if nothing found.
     */
    public static Object[] findCursorPosition(Node[] nodes) {
        if (nodes == null) {
            return null;
        }
        
        String filename = null;
        int line = 1;
        
        for (int i = 0; i < nodes.length; i++) {
            DataObject dao = (DataObject)nodes[i].getCookie(DataObject.class);
            if (dao != null) {
                FileObject fo = dao.getPrimaryFile();
                File file = FileUtil.toFile(fo);
                if (file == null) {
                    return null;
                }
                filename = file.getPath();
                EditorCookie ec = (EditorCookie)nodes[i].getCookie(EditorCookie.class);
                if (ec != null) {
                    JEditorPane[] editorPanes = ec.getOpenedPanes();
                    if ((editorPanes != null) && (editorPanes.length > 0)) {
                        line = NbDocument.findLineNumber(
                        ec.getDocument(),
                        editorPanes[0].getCaret().getDot()) + 1;
                    }
                }
                break;
            }
        }
        
        if (filename == null)
            return null;
        
        return new Object[] {filename, new Integer(line)};
    }

    /**
     * Loads column configuration from a view
     *
     * @param v a view
     * @param cc a columns configuration
     */
    public static void loadColumnsFrom(TaskListView v, ColumnsConfiguration cc) {
        LOGGER.log(Level.FINE, "loading columns from " + v.getDisplayName(),
            new Exception());

        // first find the tree column. It will be always the first one.
        ColumnProperty columns[] = v.getColumns();
        ColumnProperty treeColumn = null;
        for (int i = 0; i < columns.length; i++) {
            Object b = columns[i].getValue("TreeColumnTTV"); // NOI18N
            if (b instanceof Boolean && ((Boolean) b).booleanValue()) {
                treeColumn = columns[i];
                break;
            }
        }
        assert treeColumn != null;
        
        // find the sorted column
        ColumnProperty sortedColumn = null;
        boolean ascending = false;
        for (int i = 0; i < columns.length; i++) {
            Boolean sorting = (Boolean) columns[i].getValue(
                "SortingColumnTTV"); // NOI18N
            if ((sorting != null) && (sorting.booleanValue())) {
                sortedColumn = columns[i];
                Boolean desc = (Boolean) columns[i].getValue(
                    "DescendingOrderTTV"); // NOI18N
                ascending = (desc != Boolean.TRUE);
            }
        }

        // widths
        TableColumnModel m = v.getTable().getColumnModel();
        int[] widths = new int[m.getColumnCount()];
        String[] properties = new String[m.getColumnCount()];
        for (int i = 0; i < m.getColumnCount(); i++) {
            TableColumn tc = m.getColumn(i);
            ColumnProperty cp = null;
            for (int j = 0; j < columns.length; j++) {
                if (columns[j].getDisplayName().equals(tc.getHeaderValue())) {
                    cp = columns[j];
                }
            }
            if (cp != null) {
                properties[i] = cp.getName();
            } else {
                // tree column
                properties[i] = treeColumn.getName();
            }
            widths[i] = tc.getWidth();
        }
        
        cc.setValues(properties, widths, 
            sortedColumn == null ? null : sortedColumn.getName(), ascending);
    }
    
    /**
     * Configures view's column widths.
     *
     * @param v view that should be configured
     * @param cc a columns configuration
     */
    public static void configureColumns(TaskListView v, ColumnsConfiguration cc) {
        String[] properties = cc.getProperties();
        int[] widths = cc.getWidths();
        String sortingColumn = cc.getSortingColumn();
        boolean ascending = cc.getSortingOrder();
        
        ColumnProperty columns[] = v.getColumns();
        
        for (int i = 0; i < columns.length; i++) {
            // NOTE reverse logic: this is INvisible
            columns[i].setValue("InvisibleInTreeTableView", // NOI18N
            Boolean.TRUE);
        }
        
        for (int i = 0; i < properties.length; i++) {
            ColumnProperty c = findColumn(columns, properties[i]);
            if (c != null) {
                // Necessary because by default some columns
                // set invisible by default, so I have to
                // override these
                // NOTE reverse logic: this is INvisible
                c.setValue("InvisibleInTreeTableView", // NOI18N
                Boolean.FALSE);
                c.width = widths[i];
                LOGGER.fine("configure width: " + c.getName() + " " + c.width);
            }
        }
        
        // Set sorting attribute
        if (sortingColumn != null) {
            ColumnProperty c = findColumn(columns, sortingColumn);
            if (c != null) {
                c.setValue("SortingColumnTTV", Boolean.TRUE); // NOI18N
                // Descending sort?
                c.setValue("DescendingOrderTTV", // NOI18N
                (!ascending) ? Boolean.TRUE : Boolean.FALSE);
            }
        }
    }
    
    /**
     * Searches a column by name
     *
     * @param columns view columns
     * @param name name of a property
     * @return found column or null
     */
    private static ColumnProperty findColumn(ColumnProperty columns[], String name) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getName().equals(name))
                return columns[i];
        }
        
        return null;
    }
    
    /**
     * Creates a simple logger for the specified class. 
     * Category of the logger will be equals to the class name.
     *
     * @param clazz the name of the class will be used for the logger's category
     * @return logger
     */
    public static Logger getLogger(Class clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        logger.addHandler(ch);
        logger.setLevel(Level.FINE);
        return logger;
    }
}
