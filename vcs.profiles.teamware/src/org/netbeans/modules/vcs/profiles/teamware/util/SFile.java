/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 * 
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import org.netbeans.api.diff.Difference;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.spi.diff.DiffProvider;
import org.openide.util.Lookup;

public class SFile {
    
    private File sFile;
    private Map variables;
    private SRevisionList revisionList;
    /** The highest delta number, including removed revisions */
    private int greatestSerialNumber;
    
    public SFile(File baseFile) {
        this.sFile = new File(baseFile.getParent(),
            "SCCS" + File.separator + "s." + baseFile.getName());
    }
    
    /** Does the S File exist? */
    public boolean exists() {
        return sFile.exists();
    }
    
    /** Is the file uuencoded? */
    public boolean isEncoded() {
        if (variables == null) {
            getRevisions();
        }
        return "1".equals(variables.get("e"));
    } 
    
    public synchronized void edit() throws IOException {
        File baseFile = new File(sFile.getParentFile().getParent(),
            sFile.getName().substring(2));
        File pFile = new File(sFile.getParent(),
            "p." + baseFile.getName());
        if (baseFile.exists() && baseFile.canWrite()) {
            throw new IOException(baseFile.getName()
                + " exists and is writable");
        }
        if (pFile.exists()) {
            throw new IOException("SCCS" + File.separator + pFile.getName()
                + " exists");
        }
        SRevisionItem activeRevision = getRevisions().getActiveRevision();
        String[] components = activeRevision.getRevision().split("\\.");
        int newLeaf = 1 + Integer.parseInt(components[components.length - 1]);
        StringBuffer newRevBuffer = new StringBuffer();
        for (int i = 0; i < components.length - 1; i++) {
            newRevBuffer.append(components[i]);
            newRevBuffer.append(".");
        }
        newRevBuffer.append(newLeaf);
        Writer pFileWriter = new FileWriter(pFile);
        pFileWriter.write(activeRevision.getRevision() + " ");
        pFileWriter.write(newRevBuffer.toString() + " ");
        pFileWriter.write(System.getProperty("user.name") + " ");
        pFileWriter.write(dateTime(System.currentTimeMillis()) + "\n");
        pFileWriter.close();
        baseFile.delete();
        OutputStream baseFileOut = new FileOutputStream(baseFile);
        baseFileOut.write(getAsBytes(activeRevision, false));
        baseFileOut.close();
    }
    
    public synchronized void unedit() throws IOException {
        String name = sFile.getName().substring(2);
        File pFile = new File(sFile.getParent(),
            "p." + name);
        if (!pFile.exists()) {
            throw new IOException(name + " is not being edited");
        }
        pFile.delete();
        get();
    }
    
    public synchronized void get() throws IOException {
        File baseFile = new File(sFile.getParentFile().getParent(),
            sFile.getName().substring(2));
        baseFile.delete();
        OutputStream baseFileOut = new FileOutputStream(baseFile);
        baseFileOut.write(getAsBytes(getRevisions().getActiveRevision() , true));
        baseFileOut.close();
        baseFile.setReadOnly();
    }
    
    public synchronized void delget(String comment) throws IOException {
        File baseFile = new File(sFile.getParentFile().getParent(),
            sFile.getName().substring(2));
        File pFile = new File(sFile.getParent(),
            "p." + baseFile.getName());
        if (!pFile.exists()) {
            throw new IOException(baseFile.getName()
                + " is not being edited");
        }
        byte[] fileData = readFileData(baseFile);
        boolean encoded = isEncoded();
        String fileContents =
            encoded
                ? UU.encode(fileData, 0, fileData.length)
                : new String(fileData);                
        BufferedReader pFileReader = new BufferedReader(new FileReader(pFile));
        String[] pFileData = pFileReader.readLine().split(" ");
        pFileReader.close();
        SRevisionItem oldRevision =
            getRevisions().getRevisionByName(pFileData[0]);
        BufferedReader br1 =
            new BufferedReader(new StringReader(getAsString(oldRevision, false)));
        BufferedReader br2 = new BufferedReader(new StringReader(fileContents));
        DiffProvider dp =
            (DiffProvider) Lookup.getDefault().lookup(DiffProvider.class);
        final Difference[] diffs = dp.computeDiff(br1, br2);
        if (diffs.length == 0) {
            unedit();
            return;
        }
        final StringBuffer sb = new StringBuffer();
        int inserted = 0;
        int deleted = 0;
        for (int i = 0; i < diffs.length; i++) {
            switch (diffs[i].getType()) {
                case Difference.ADD:
                    inserted += diffs[i].getSecondEnd() - diffs[i].getSecondStart() + 1;
                    break;
                case Difference.DELETE:
                    deleted += diffs[i].getFirstEnd() - diffs[i].getFirstStart() + 1;
                    break;
                case Difference.CHANGE:
                    deleted += diffs[i].getFirstStart() - diffs[i].getFirstStart() + 1;
                    inserted += diffs[i].getSecondEnd() - diffs[i].getSecondStart() + 1;
            }
        }
        int linesInNewVersion = countLines(fileContents);
        int unchanged = linesInNewVersion - inserted;
        sb.append("\u0001s "
            + pad(inserted, 5) + "/" + pad(deleted, 5) + "/" + pad(unchanged, 5)
            + "\n");
        sb.append("\u0001d D " + pFileData[1] + " "
            + dateTime(System.currentTimeMillis()) + " "
            + System.getProperty("user.name") + " ");

        SRevisionItem activeRevision = getRevisions().getActiveRevision();
        int predecessor = activeRevision.getSerialNumber();
        final int serialNumber = greatestSerialNumber + 1;
        sb.append(serialNumber + " " + predecessor + "\n");
        String[] commentLines = comment.split("\n");
        for (int i = 0; i < commentLines.length; i++) {
            sb.append("\u0001c " + commentLines[i] + "\n");
        }
        sb.append("\u0001e\n");

        BufferedReader r = new BufferedReader(new FileReader(sFile));
        boolean done = false;
        while (!done) {
            String s = r.readLine();
            if (s == null || s.length() < 2 || s.charAt(0) != (char) 1) {
                done = true;
                continue;
            }
            switch (s.charAt(1)) {
                case 'h':
                    break;
                case 'T':
                    done = true;
                case 's':
                case 'i':
                case 'x':
                case 'g':
                case 'm':
                case 'u':
                case 'U':
                case 't':
                case 'c':
                case 'd':
                case 'e':
                    sb.append(s);
                    sb.append("\n");
                    break;
                case 'f':
                    if (s.substring(2).trim().startsWith("e")) {
                        s = "\u0001f e " + (encoded ? "1" : "0");
                    }
                    sb.append(s);
                    sb.append("\n");
                    break;
                    
            }
        }
        r.close();

        class DeltaVisitor extends LineVisitor {
            int lineNumber = 1;
            int diffIndex = 0;
            int linesToDelete = 0;
            private void checkDiffs() {
                if (linesToDelete == 0 && diffIndex < diffs.length) {
                    if (diffs[diffIndex].getSecondStart() == lineNumber) {
                        Difference diff = diffs[diffIndex++];
                        switch (diff.getType()) {
                            case Difference.ADD: {
                                sb.append("\u0001I " + serialNumber + "\n");
                                sb.append(diff.getSecondText());
                                sb.append("\u0001E " + serialNumber + "\n");
                                lineNumber += diff.getSecondEnd() - diff.getSecondStart() + 1;
                                break;
                            }
                            case Difference.DELETE: {
                                sb.append("\u0001D " + serialNumber + "\n");
                                linesToDelete = diff.getFirstEnd() - diff.getFirstStart() + 1;
                                break;
                            }
                            case Difference.CHANGE: {
                                sb.append("\u0001I " + serialNumber + "\n");
                                sb.append(diff.getSecondText());
                                sb.append("\u0001E " + serialNumber + "\n");
                                lineNumber += diff.getSecondEnd() - diff.getSecondStart() + 1;
                                sb.append("\u0001D " + serialNumber + "\n");
                                linesToDelete = diff.getFirstEnd() - diff.getFirstStart() + 1;
                                break;
                            }
                        }
                    }
                }
            }
            public void includeLine(String line) {
                checkDiffs();
                sb.append(line);
                sb.append("\n");
                if (linesToDelete > 0) {
                    linesToDelete --;
                    if (linesToDelete == 0) {
                        sb.append("\u0001E " + serialNumber + "\n");
                    }
                } else {
                    lineNumber ++;
                }

            }
            public void excludeLine(String line) {
                sb.append(line);
                sb.append("\n");
            }
            public void controlLine(char type, String sn) {
                checkDiffs();
                sb.append("\u0001" + type + " " + sn + "\n");
            }
        };
        DeltaVisitor visitor = new DeltaVisitor();
        retrieveRevision(visitor, oldRevision, false);
        if (visitor.linesToDelete > 0) {
            sb.append("\u0001E " + serialNumber + "\n");
        }
        writeSFile(sb);
        pFile.delete();
        revisionList = null;
        get();
    }
    
    public synchronized void create() throws IOException {
        File baseFile = new File(sFile.getParentFile().getParent(),
            sFile.getName().substring(2));
        if (sFile.exists()) {
            throw new IOException(sFile.getName() + " exists");
        }
        byte[] fileData = readFileData(baseFile);
        boolean encoded = dataNeedsEncoding(fileData);
        String fileContents =
            encoded
                ? UU.encode(fileData, 0, fileData.length)
                : new String(fileData);

        int lineCount = countLines(fileContents);
        StringBuffer sb = new StringBuffer();
        sb.append("\u0001s ");
        sb.append(pad(lineCount, 5));
        sb.append("/00000/00000\n");
        sb.append("\u0001d D 1.1 ");
        sb.append(dateTime(System.currentTimeMillis()));
        sb.append(" ");
        sb.append(System.getProperty("user.name"));
        sb.append(" 1 0\n");
        sb.append("\u0001c Initial Version\n");
        sb.append("\u0001e\n");
        sb.append("\u0001u\n");
        sb.append("\u0001U\n");
        sb.append("\u0001f e ");
        sb.append((encoded ? "1\n" : "0\n"));
        sb.append("\u0001t\n");
        sb.append("\u0001T\n");
        sb.append("\u0001I 1\n");
        sb.append(fileContents);
        if (!fileContents.endsWith("\n")) {
            sb.append("\n");
        }
        sb.append("\u0001E 1\n");
        
        writeSFile(sb);
        get();
    }

    public synchronized void fix() throws IOException {
        File baseFile = new File(sFile.getParentFile().getParent(),
            sFile.getName().substring(2));
        File pFile = new File(sFile.getParent(),
            "p." + baseFile.getName());
        if (baseFile.exists() && baseFile.canWrite()) {
            throw new IOException(baseFile.getName()
                + " exists and is writable");
        }
        if (pFile.exists()) {
            throw new IOException("SCCS" + File.separator + pFile.getName()
                + " exists");
        }
        SRevisionItem activeRevision = getRevisions().getActiveRevision();
        int previousSN = activeRevision.getPredecessor();
        SRevisionItem previousRevision =
            getRevisions().getRevisionBySerialNumber(String.valueOf(previousSN));
        
        // set the base file to the current version
        baseFile.delete();
        OutputStream baseFileOut = new FileOutputStream(baseFile);
        baseFileOut.write(getAsBytes(activeRevision, false));
        baseFileOut.close();

        // create the p-file
        Writer pFileWriter = new FileWriter(pFile);
        pFileWriter.write(previousRevision.getRevision() + " ");
        pFileWriter.write(activeRevision.getRevision() + " ");
        pFileWriter.write(System.getProperty("user.name") + " ");
        pFileWriter.write(dateTime(System.currentTimeMillis()) + "\n");
        pFileWriter.close();
        
        // update the s-file
        StringBuffer sb = new StringBuffer();
        String oldRevLine = "\u0001d D " + activeRevision.getRevision() + " ";
        BufferedReader r = new BufferedReader(new FileReader(sFile));
        for (String s; (s = r.readLine()) != null;) {
            if (s.startsWith(oldRevLine)) {
                // replace the 'D' with 'R'
                s = s.substring(0, 3) + "R" + s.substring(5);
            }
            sb.append(s);
        }
        r.close();
        writeSFile(sb);
        
        // the revision list is no longer valid
        revisionList = null;
    }
    
    private static byte[] readFileData(File file) throws IOException {
        BufferedInputStream in =
            new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for (int bytesRead; (bytesRead = in.read(buffer)) != -1;) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
    
    private static boolean dataNeedsEncoding(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            if (b > 127 || b < 32) {
                if (b != '\t' && b != '\n' && b != '\r') {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static int countLines(String s) throws IOException {
        int lineCount = 0;
        BufferedReader r = new BufferedReader(new StringReader(s));
        while (r.readLine() != null) {
            lineCount ++;
        }
        r.close();
        return lineCount;
    }
    
    private static byte[] checksum(byte[] b) {
        int checksum = 0;
        for (int i = 0; i < b.length; i++) {
            checksum += ((int) b[i]) & 0xff;
        }
        byte[] cs = String.valueOf(checksum & 0xffff).getBytes();
        if (cs.length == 5) {
            return cs;
        } else {
            // pad the checksum with zeros
            byte[] cs2 = new byte[5];
            for (int i = 0; i < 5 - cs.length; i++) {
                cs2[i] = (byte) '0';
            }
            System.arraycopy(cs, 0, cs2, 5 - cs.length, cs.length);
            return cs2;
        }
    }

    private void writeSFile(StringBuffer sb) throws IOException {
        byte[] sFileContents = sb.toString().getBytes();
        if (sFile.exists()) {
            sFile.delete();
        }
        File sccsDir = sFile.getParentFile();
        if (!sccsDir.exists()) {
            sccsDir.mkdir();
        }
        OutputStream sFileOut = new BufferedOutputStream(
                new FileOutputStream(sFile));
        sFileOut.write(1);
        sFileOut.write('h');
        sFileOut.write(checksum(sFileContents));
        sFileOut.write('\n');
        sFileOut.write(sFileContents);
        sFileOut.close();
        sFile.setReadOnly();
    }
    
    private static String pad(int val, int chars) {
        String s = String.valueOf(val);
        StringBuffer sb = new StringBuffer();
        for (int i = chars - s.length(); i > 0; i--) {
            sb.append("0");
        }
        sb.append(s);
        return sb.toString();
    }
    
    public void annotate(final CommandOutputListener out) throws IOException {
        class AnnotationLineVisitor extends LineVisitor {
            Stack insertions = new Stack();
            Map itemMap = new HashMap();
            List lines = new ArrayList();
            boolean dosFormat = true;
            public void includeLine(String line) {
                String sn = (String) insertions.peek();
                RevisionItem item = (RevisionItem) itemMap.get(sn);
                if (item == null) {
                    item = getRevisions().getRevisionBySerialNumber(sn);
                    itemMap.put(sn, item);
                }
                if (!line.endsWith("\r")) {
                    dosFormat = false;
                }
                lines.add(item.getRevision() + ":"
                    + item.getDate() + ":"
                    + item.getAuthor() + ":"
                    + line);
            }
            public void controlLine(char type, String sn) {
                switch (type) {
                    case 'I': {
                        insertions.push(sn);
                        break;
                    }
                    case'E': {
                        insertions.remove(sn);
                        break;
                    }
                }
            }
            void write() {
                for (Iterator i = lines.iterator(); i.hasNext();) {
                    String line = (String) i.next();
                    if (dosFormat) {
                        line = line.substring(0, line.length() - 1);
                    }
                    out.outputLine(line);
                }
            }
        };
        AnnotationLineVisitor visitor = new AnnotationLineVisitor();
        retrieveRevision(visitor, getRevisions().getActiveRevision(), true);
        visitor.write();
    }
    
    public synchronized SRevisionList getRevisions() {
        if (revisionList == null) {
            BufferedReader in = null;
            try {
                in = new LineReader(new FileReader(sFile));
                readHeader(in);
            } catch (Exception e) {
                // return an empty list
                return new SRevisionList();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return revisionList;
    }

    /** Parse the SCCS file to get revision details */
    private void readHeader(BufferedReader in) throws IOException {
        this.revisionList = new SRevisionList();
        this.greatestSerialNumber = 0;
        SRevisionItem item = null;
        this.variables = new HashMap();
        boolean done = false;
        while (!done) {
            String s = in.readLine();
            if (s == null || s.length() < 2 || s.charAt(0) != (char) 1) {
                done = true;
                continue;
            }
            switch (s.charAt(1)) {
                case 'h':
                case 's':
                    break;
                case 'i': {
                    if (item != null) {
                        String[] data = s.split(" ");
                        for (int i = 1; i < data.length; i++) {
                            item.includeSerialNumber(data[i]);
                        }
                    }
                    break;
                }
                case 'x': {
                    if (item != null) {
                        String[] data = s.split(" ");
                        for (int i = 1; i < data.length; i++) {
                            item.excludeSerialNumber(data[i]);
                        }
                    }
                    break;
                }
                case 'g': {
                    if (item != null) {
                        String[] data = s.split(" ");
                        for (int i = 1; i < data.length; i++) {
                            item.ignoreSerialNumber(data[i]);
                        }
                    }
                    break;
                }
                case 'm':
                case 'u':
                case 'U':
                case 't':
                    break;
                case 'f': {
                    String data = s.substring(2).trim();
                    int i = data.indexOf(" ", 1);
                    if (i == -1) {
                        i = data.indexOf("\t", 1);
                    }
                    try {
                        String flag = data.substring(0, i);
                        String text = data.substring(i).trim();
                        variables.put(flag, text);
                    } catch (StringIndexOutOfBoundsException e) {
                        // ignore this flag
                    }
                    break;
                }
                case 'd': {
                    String[] data = s.split(" ");
                    int sn1 = Integer.parseInt(data[6]);
                    int sn2 = Integer.parseInt(data[7]);
                    this.greatestSerialNumber =
                        Math.max(this.greatestSerialNumber, sn1);
                    if (data[1].equals("D")) {
                        item = new SRevisionItem(data[2]);
                        item.setAuthor(data[5]);
                        item.setDate(data[3]);
                        item.setTime(data[4]);
                        item.setMessage("");
                        item.setSerialNumber(sn1);
                        item.setPredecessor(sn2);
                    }
                    break;
                }
                case 'c': {
                    if (s.length() > 2 && item != null) {
                        String commentText = s.substring(3);
                        String existingComment = item.getMessage();
                        if (existingComment.length() > 0) {
                            item.setMessage(existingComment + "\n" + commentText);
                        } else {
                            item.setMessage(commentText);
                        }
                    }
                    break;
                }
                case 'e': {
                    if (item != null) {
                        revisionList.add(item);
                        item = null;
                    }
                    break;
                }
                default:
                    done = true;
            }
        }
    }
    
    /** Parse the S file to get a specific revision */
    public String getAsString(SRevisionItem revision, boolean expandVariables)
            throws IOException {
        class GetStringVisitor extends LineVisitor {
            boolean dosFormat = false;//true;
            List lines = new ArrayList();
            public void includeLine(String line) {
                lines.add(line);
            }
            String getAsString() {
                StringBuffer sb = new StringBuffer();
                for (Iterator i = lines.iterator(); i.hasNext();) {
                    String line = (String) i.next();
                    if (dosFormat) {
                        sb.append(line.substring(0, line.length() - 1));
                    } else {
                        sb.append(line);
                    }
                    sb.append('\n');
                }
                return sb.toString();
            }
        };
        GetStringVisitor visitor = new GetStringVisitor();
        retrieveRevision(visitor, revision, expandVariables);
        return visitor.getAsString();
    }

    public byte[] getAsBytes(SRevisionItem revision, boolean expandVariables)
        throws IOException {

        class GetBytesVisitor extends LineVisitor {
            boolean dosFormat = false;//true;
            List lines = new ArrayList();
            public void includeLine(String line) {
                lines.add(line);
                if (!line.endsWith("\r")) {
                    dosFormat = false;
                }
            }
            byte[] getAsBytes() throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                boolean encoded = isEncoded();
                for (Iterator i = lines.iterator(); i.hasNext();) {
                    String line = (String) i.next();
                    if (encoded) {
                        UU.decode(line, baos);
                    } else {
                        if (dosFormat) {
                            line = line.substring(0, line.length() - 1);
                        }
                        baos.write(line.getBytes());
                        baos.write((byte) '\n');
                    }
                }
                return baos.toByteArray();
            }
        };
        GetBytesVisitor visitor = new GetBytesVisitor();
        retrieveRevision(visitor, revision, expandVariables);
        return visitor.getAsBytes();
    }
    
    public static abstract class LineVisitor {
        public abstract void includeLine(String line);
        public void excludeLine(String line) { }
        public void controlLine(char type, String sn) { }
    }
    
    public static class LineVisitorWrapper extends LineVisitor {
        private CommandOutputListener out;
        public LineVisitorWrapper(CommandOutputListener out) {
            this.out = out;
        }
        public void includeLine(String line) {
            out.outputLine(line);
        }
    }
    
    /** LineReader is like BufferedReader, but it includes trailing carriage
     * returns (these are necessary for accurately imitating SCCS)
     */
    static class LineReader extends BufferedReader {
        LineReader(Reader r) {
            super(r);
        }
        public String readLine() throws IOException {
            StringBuffer sb = new StringBuffer();
            for (int ch; (ch = read()) != '\n';) {
                if (ch == -1) {
                    if (sb.length() == 0) {
                        return null;
                    } else {
                        return sb.toString();
                    }
                }
                sb.append((char) ch);
            }
            return sb.toString();
        }
    }
    
    /* A file is in DOS format if each line ends with a carriage return */
    private boolean isDOSFormat() throws IOException {
        BufferedReader in = new LineReader(new FileReader(sFile));
        boolean dosFormat = true;
        for (String line; (line = in.readLine()) != null && dosFormat;) {
            if (!line.startsWith("\u0001") && !line.endsWith("\r")) {
                dosFormat = false;
            }
        }
        in.close();
        return dosFormat;
    }
    
    /** Parse the S file to get a specific revision */
    public void retrieveRevision(LineVisitor visitor,
        SRevisionItem revision, boolean expandVariables) throws IOException {
            
        BufferedReader in = new LineReader(new FileReader(sFile));
        readHeader(in);
        List sns = new ArrayList(getRevisions().getSerialNumbers(revision));
        Collections.sort(sns, new Comparator() {
            public int compare(Object o1, Object o2) {
                int i1 = Integer.parseInt((String) o1);
                int i2 = Integer.parseInt((String) o2);
                return i1 - i2;
            }
        });
        List inhibitors = new ArrayList();
        List overriddenInhibitors = new ArrayList();
        Set includes = new HashSet();
        for (String line; (line = in.readLine()) != null;) {
            if (line.startsWith("\u0001D")) {
                String sn = line.substring(2).trim();
                if (sns.contains(sn)) {
                    inhibitors.add("D" + sn);
                }
                visitor.controlLine('D', sn);
            } else if (line.startsWith("\u0001I")) {
                String sn = line.substring(2).trim();
                if (!sns.contains(sn)) {
                    inhibitors.add("I" + sn);
                } else {
                    includes.add(sn);
                    // see if any inhibitors are overridden by this include
                    int iValue = Integer.parseInt(sn);
                    for (ListIterator i = inhibitors.listIterator(); i.hasNext();) {
                        String inhibitor = (String) i.next();
                        int integerSN = Integer.parseInt(inhibitor.substring(1));
                        if (integerSN < iValue) {
                            i.remove();
                            overriddenInhibitors.add(sn + ":" + inhibitor);
                        }
                    }
                }
                visitor.controlLine('I', sn);
            } else if (line.startsWith("\u0001E")) {
                String sn = line.substring(2).trim();
                inhibitors.remove("D" + sn);
                inhibitors.remove("I" + sn);
                includes.remove(sn);
                // see if any inhibitors can be restored
                String prefix = sn + ":";
                for (ListIterator i = overriddenInhibitors.listIterator(); i.hasNext();) {
                    String inhibitor = (String) i.next();
                    if (inhibitor.startsWith(prefix)) {
                        i.remove();
                        inhibitors.add(inhibitor.substring(prefix.length()));
                    }
                }
                visitor.controlLine('E', sn);
            } else if (inhibitors.isEmpty()) {
                if (expandVariables) {
                    line = expandVariables(line, revision);
                }
                visitor.includeLine(line);
            } else {
                visitor.excludeLine(line);
            }
        }
    }
        
    private String expandVariables(String line, SRevisionItem item) {
        // variables are not expanded in binary files
        if (isEncoded()) {
            return line;
        }
        int i = line.indexOf("%");
        while (i > -1) {
            if (i >= line.length() - 2) {
                break;
            }
            if (line.charAt(i + 2) == '%') {
                String replacement = null;
                switch (line.charAt(i + 1)) {
                    case 'A': {
                        replacement = "%Z%%Y%\t%M%\t%I%%Z%";
                        break;
                    }
                    case 'B': {
                        String[] components = item.getRevision().split("\\.");
                        replacement = components.length >= 3
                            ? components[2]
                            : "0";
                        break;
                    }
                    case 'D': {
                        replacement = yymmdd(System.currentTimeMillis());
                        break;
                    }
                    case 'E': {
                        replacement = yymmdd(item.getLongDate());
                        break;
                    }
                    case 'F': {
                        replacement = sFile.getName();
                        break;
                    }
                    case 'G': {
                        replacement = mmddyy(item.getLongDate());
                        break;
                    }
                    case 'H': {
                        replacement = mmddyy(System.currentTimeMillis());
                        break;
                    }
                    case 'I': {
                        replacement = item.getRevision();
                        break;
                    }
                    case 'L': {
                        replacement = item.getRevision().split("\\.")[1];
                        break;
                    }
                    case 'M': {
                        String mVar = (String) variables.get("m");
                        if (mVar == null) {
                            replacement = sFile.getName().substring(2);
                        } else {
                            replacement = mVar;
                        }
                        break;
                    }
                    case 'P': {
                        replacement = sFile.toString();
                        break;
                    }
                    case 'Q': {
                        replacement = (String) variables.get("q");
                        break;
                    }
                    case 'R': {
                        replacement = item.getRevision().split("\\.")[0];
                        break;
                    }
                    case 'S': {
                        String[] components = item.getRevision().split("\\.");
                        replacement = components.length >= 4
                            ? components[3]
                            : "0";
                        break;
                    }
                    case 'T': {
                        replacement = time(System.currentTimeMillis());
                        break;
                    }
                    case 'U': {
                        replacement = item.getTime();
                        break;
                    }
                    case 'W': {
                        replacement = "%Z%%M%\t%I%";
                        break;
                    }
                    case 'Y': {
                        replacement = (String) variables.get("t");
                        break;
                    }
                    case 'Z': {
                        replacement = "@(#)";
                        break;
                    }
                }
                if (replacement != null) {
                    line = line.substring(0, i)
                        + replacement
                        + line.substring(i + 3);
                    if (replacement.startsWith("%")) {
                        // don't update "i"
                        continue;
                    }
                }
            }
            i = line.indexOf("%", i + 1);
        }
        return line;
    }
    
    private static String yymmdd(long date) {
        DateFormat df = new SimpleDateFormat("yy/MM/dd");
        return df.format(new Date(date));
    }

    private static String mmddyy(long date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        return df.format(new Date(date));
    }
    
    private static String time(long time) {
        DateFormat df = new SimpleDateFormat("HH:MM:SS");
        return df.format(new Date(time));
    }
    
    private static String dateTime(long time) {
        return new SimpleDateFormat("yy/MM/dd HH:mm:ss")
            .format(new Date(time));
    }
        
}
