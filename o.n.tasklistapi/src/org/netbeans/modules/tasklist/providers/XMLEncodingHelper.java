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
package org.netbeans.modules.tasklist.providers;

import org.openide.ErrorManager;

import java.io.*;

/**
 * XML uses inband encoding detection - this class obtains it.
 * 
 * @author  Petr Kuzel
 * @version 1.0
 */
final class XMLEncodingHelper extends Object {

    //
    // taken from XML module xml.core.lib.EncodingHelper
    //

    // heuristic constant guessing max prolog length
    private static final int EXPECTED_PROLOG_LENGTH = 1000;

    /** Detect input stream encoding.
    * The stream stays intact.
    * @return iana encoding names or Java hisrotical ("UTF8", "ASCII", etc.) or null
    * if the stream is not markable or enoding cannot be detected.
    */
    public static String detectEncoding(InputStream in) throws IOException {

        if (! in.markSupported()) {
            ErrorManager.getDefault().log("XMLEncodingHelper got unmarkable stream: " + in.getClass()); // NOI18N
            return null;
        }

        try {
            in.mark(EXPECTED_PROLOG_LENGTH);

            byte[] bytes = new byte[EXPECTED_PROLOG_LENGTH];
            for (int i = 0; i<bytes.length; i++) {
                try {
                    int datum = in.read();
                    if (datum == -1) break;
                    bytes[i] = (byte) datum;
                } catch (EOFException ex) {
                }
            }

            String enc = autoDetectEncoding(bytes);
            if (enc == null) return null;
            
            enc = detectDeclaredEncoding(bytes, enc);
            if (enc == null) return null;
            
            return enc;
        } finally {
            in.reset();
        }
    }

        
    /**
     * @return Java encoding family identifier or <tt>null</tt> for unrecognized
     */
    static String autoDetectEncoding(byte[] buf) throws IOException {
        

        if (buf.length >= 4) {
            switch (buf[0]) {
                case 0:  
                    // byte order mark of (1234-big endian) or (2143) USC-4
                    // or '<' encoded as UCS-4 (1234, 2143, 3412) or UTF-16BE 
                    if (buf[1] == (byte)0x3c && buf[2] == (byte)0x00 && buf[3] == (byte)0x3f) {
                        return "UnicodeBigUnmarked"; // NOI18N
                    }
                    // else it's probably UCS-4
                    break;

                case 0x3c:
                    switch (buf[1]) {
                        // First character is '<'; could be XML without
                        // an XML directive such as "<hello>", "<!-- ...", // NOI18N
                        // and so on.
                        
                        // 3c 00 3f 00 UTF-16 little endian
                        case 0x00:
                            if (buf [2] == (byte)0x3f && buf [3] == (byte)0x00) {
                                return  "UnicodeLittleUnmarked"; // NOI18N
                            }                            
                            break;

                        // 3c 3f 78 6d == ASCII and supersets '<?xm'
                        case '?':
                            if (buf [2] == 'x' && buf [3] == 'm') { // NOI18N
                                return  "UTF8"; // NOI18N
                            }
                            break;
                    }
                    break;

                // 4c 6f a7 94 ... some EBCDIC code page
                case 0x4c:
                    if (buf[1] == (byte)0x6f && buf[2] == (byte)0xa7 && buf[3] == (byte)0x94) {
                        return "Cp037"; // NOI18N
                    }                     
                    break;

                // UTF-16 big-endian marked
                case (byte)0xfe:
                    if (buf[1] == (byte)0xff && (buf[2] != 0 || buf[3] != 0)) {
                        return  "UnicodeBig"; // NOI18N
                    }
                    break;

                // UTF-16 little-endian marked
                case (byte)0xff:
                    if (buf[1] == (byte)0xfe && (buf[2] != 0 || buf[3] != 0)) {                        
                        return "UnicodeLittle"; // NOI18N
                    }
                    break;
                    
                // UTF-8 byte order mark
                case (byte)0xef:
                    if (buf[1] == (byte)0xbb && buf[2] == (byte)0xbf) {
                        return "UTF8";  //NOI18N
                    }
                    break;
                    
            }
        }

        return null;
    }

    /**
     * Look for encoding='' anyway stop at <tt>?></tt>
     * @return found encoding or null if none declared
     */
    static String detectDeclaredEncoding(byte[] data, String baseEncoding) throws IOException {

        StringBuffer buf = new StringBuffer();
        Reader r;
        char delimiter = '"';

        r = new InputStreamReader(new ByteArrayInputStream(data), baseEncoding);
        try {
            for (int c = r.read(); c != -1; c = r.read()) {
                buf.append((char)c);
            }
        } catch (IOException ex) {
            // EOF of data out of boundary
            // dont care try to guess from given data
        }
        
        String s = buf.toString();
        
        int iend = s.indexOf("?>");
        iend = iend == -1 ? s.length() : iend;
        
        int iestart = s.indexOf("encoding");  // NOI18N
        if (iestart == -1 || iestart > iend) return null;
        
        char[] chars = s.toCharArray();
        
        int i = iestart;
        
        for (; i<iend; i++) {
            if (chars[i] == '=') break;
        }
        
        for (; i<iend; i++) {
            if (chars[i] == '\'' || chars[i] == '"') {
                delimiter = chars[i];
                break;
            }
                
        }

        i++;
        
        int ivalstart = i;
        for (; i<iend; i++) {
            if (chars[i] == delimiter) {
                return new String(chars, ivalstart, i - ivalstart);
            }
        }
        
        return null;
    }

}
