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
/*
 * DocbookFileNode.java
 *
 * Created on October 14, 2006, 2:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.docbook.project;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collections;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.netbeans.api.docbook.Callback;
import org.netbeans.api.docbook.ParsingService;
import org.netbeans.api.docbook.PatternCallback;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tim Boudreau
 */
public class DbFileFilterNode extends FilterNode {
    private final DbProject project;
    private RequestProcessor.Task task;
    public DbFileFilterNode(Node orig, DbProject project, FileObject parentFolder) throws DataObjectNotFoundException {
        super (orig, new ImageChildren (parentFolder));
        this.project = project;
        DataObject ob = orig.getLookup(  ).lookup( org.openide.loaders.DataObject.class );
        setName (ob.getName());
    }

    private String cachedName = null;
    private final Object lock = new Object();
    @Override
    public String getDisplayName() {
        String result;
        String nm = super.getDisplayName();
        String cachedName;
        synchronized (lock) {
            cachedName = this.cachedName;
        }
        if (cachedName == null) {
            enqueue();
            result = super.getDisplayName();
        } else {
            result = NO_NAME.equals(cachedName) ?
                nm : cachedName;
            FileObject ob = ((DataNode) getOriginal()).getDataObject().getPrimaryFile();
            try {
                String nue = ob.getFileSystem().getStatus().annotateName(nm,
                        Collections.singleton(ob));
                result = nue.replace(nm, result);
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    @Override
    public String getHtmlDisplayName() {
        FileObject ob = ((DataNode) getOriginal()).getDataObject().getPrimaryFile();
        String origHtml = getOriginal().getHtmlDisplayName();
        String result = null;
        boolean main = ob.equals(project.getMainFile());
        if (main || origHtml != null) {
            result = getDisplayName(); //NOI18N
        }
        if (result != null) {
            try {
                 FileSystem.Status stat =
                     ob.getFileSystem().getStatus();
                 if (stat instanceof FileSystem.HtmlStatus) {
                     FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;

                     String old = result;
                     result = hstat.annotateNameHtml (
                         result, Collections.singleton(ob));

                     if (main) result = "<b>" + (result == null ? old : result);

                     //Make sure the super string was really modified
                     if (!super.getDisplayName().equals(result)) {
                         return result;
                     }
                 }
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    private volatile boolean enqueued = false;
    private void enqueue() {
        if (!enqueued) {
            ParsingService serv = ((DataNode) getOriginal()).getLookup().lookup(ParsingService.class);
            if (serv != null) {
                serv.register(nameUpdater);
                enqueued = true;
            }
        }
    }
    
//    void detach() {
//        ParsingService serv = ((DataNode) getOriginal()).getLookup().lookup(ParsingService.class);
//        if (serv != null) {
//            serv.unregister(nameUpdater);
//            enqueued = false;
//        }
//    }

    private Callback <Pattern> nameUpdater = new NameUpdater();
    private class NameUpdater extends PatternCallback {
        public NameUpdater () {
            super (TITLE_PATTERN);
        }

        public boolean process(FileObject f, MatchResult match, CharSequence content) {
            String s = match.group(1).trim();
            String old;
            synchronized (lock) {
                old = cachedName;
                cachedName = s;
            }
            if (!s.equals(old)) {
                fireDisplayNameChange(old, s);
            }
            enqueued = false;
            //XXX in the future we want to listen, but not by being permanently
            //registered.  For now, we never update node title.
            ParsingService serv = ((DataNode) getOriginal()).getLookup().lookup(ParsingService.class);
            serv.unregister(this);
            return false;
        }
    }

    private static final String NO_NAME = "Unknown"; //NOI18N

    public String getShortDescription() {
        return super.getDisplayName();
    }

    private static final CharsetDecoder decoder =
            Charset.defaultCharset().newDecoder();

    private static final Pattern TITLE_PATTERN =
            Pattern.compile("<title>\\s*(.*)\\s*</title>"); //NOI18N


    //<?xml version="1.0" encoding="UTF-8"?>
    private static final String CONTENT_TYPE_PATTERN =
            ".*<\\?xml.*?\\s*encoding=\\s*\\\"(.*?)\\\".*?>"; //NOI18N

    private static final Pattern encodingPattern =
            Pattern.compile(CONTENT_TYPE_PATTERN,
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}
