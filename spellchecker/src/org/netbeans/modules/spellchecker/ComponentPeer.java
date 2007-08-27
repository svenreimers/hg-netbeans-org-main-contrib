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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.api.LocaleQuery;
import org.netbeans.modules.spellchecker.hints.AddToDictionaryHint;
import org.netbeans.modules.spellchecker.hints.DictionaryBasedHint;
import org.netbeans.modules.spellchecker.spi.dictionary.DictionaryProvider;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Jan Lahoda
 */
public class ComponentPeer implements PropertyChangeListener, DocumentListener, ChangeListener, CaretListener {

    private static final Logger LOG = Logger.getLogger(ComponentPeer.class.getName());
    
    public static void assureInstalled(JTextComponent pane) {
        if (pane.getClientProperty(ComponentPeer.class) == null) {
            pane.putClientProperty(ComponentPeer.class, new ComponentPeer(pane));
        }
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (doc != pane.getDocument()) {
            if (doc != null) {
                doc.removeDocumentListener(this);
            }
            doc = pane.getDocument();
            doc.addDocumentListener(this);
            doc = pane.getDocument();
            doUpdateCurrentVisibleSpan();
        }
    }

    private JTextComponent pane;
    private Document doc;

    private RequestProcessor WORKER = new RequestProcessor("Spellchecker");
    
    private RequestProcessor.Task checker = WORKER.create(new Runnable() {
        public void run() {
            try {
                process();
            } catch (BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
        }
    });

    private RequestProcessor.Task computeHint = WORKER.create(new Runnable() {
        public void run() {
            computeHint();
        }
    });
    
    public void reschedule() {
        cancel();
        checker.schedule(100);
    }
    
    private synchronized Document getDocument() {
        return doc;
    }

    /** Creates a new instance of ComponentPeer */
    private ComponentPeer(JTextComponent pane) {
        this.pane = pane;
//        reschedule();
        pane.addPropertyChangeListener(this);
        pane.addCaretListener(this);
        doc = pane.getDocument();
        doc.addDocumentListener(this);
    }
    
    private Component parentWithListener;

    private int[] computeVisibleSpan() {
        Component parent = pane.getParent();

        if (parent instanceof JViewport) {
            JViewport vp = (JViewport) parent;

            Point start = vp.getViewPosition();
            Dimension size = vp.getExtentSize();
            Point end = new Point((int) (start.getX() + size.getWidth()), (int) (start.getY() + size.getHeight()));

            int startPosition = pane.viewToModel(start);
            int endPosition = pane.viewToModel(end);

            if (parentWithListener != vp) {
                vp.addChangeListener(WeakListeners.change(this, vp));
                parentWithListener = vp;
            }
            return new int[] {startPosition, endPosition};
        }

        return new int[] {0, pane.getDocument().getLength()};
    }

    private void updateCurrentVisibleSpan() {
        //check possible change in visible rect:
        int[] newSpan = computeVisibleSpan();
        
        synchronized (this) {
            if (currentVisibleRange == null || currentVisibleRange[0] != newSpan[0] || currentVisibleRange[1] != newSpan[1]) {
                currentVisibleRange = newSpan;
                reschedule();
            }
        }
    }

    private int[] currentVisibleRange;

    private synchronized int[] getCurrentVisibleSpan() {
        return currentVisibleRange;
    }

    private TokenList l;
    
    private synchronized TokenList getTokenList() {
        if (l == null) {
            l = ACCESSOR.lookupTokenList(getDocument());
            
            if (l != null)
                l.addChangeListener(this);
        }
        
        return l;
    }
    
    private void process() throws BadLocationException {
        final Document doc = getDocument();
        
        if (doc.getLength() == 0)
            return ;
        
        FileObject file = getFile(doc);
        
        if (file == null) {
            return ;
        }
        
        final OffsetsBag localHighlights = new OffsetsBag(doc);
        long startTime = System.currentTimeMillis();
        
        try {
            resume();
            
            final TokenList l = getTokenList();
            
            if (l == null) {
                //nothing to do:
                return ;
            }

            Dictionary d = getDictionary(doc);

            if (d == null)
                return ;
            
            final int[] span = getCurrentVisibleSpan();

            if (span == null || span[0] == (-1)) {
                //not initialized yet:
                doUpdateCurrentVisibleSpan();
                return ;
            }

            l.setStartOffset(span[0]);

            final boolean[] cont = new boolean [1];
            final CharSequence[] word = new CharSequence[1];
            
            while (!isCanceled()) {
                doc.render(new Runnable() {
                    public void run() {
                        if (isCanceled()) {
                            cont[0] = false;
                            return ;
                        }
                        
                        if (cont[0] = l.nextWord()) {
                            if (l.getCurrentWordStartOffset() > span[1]) {
                                cont[0] = false;
                                return ;
                            }
                            
                            word[0] = l.getCurrentWordText();
                        }
                    }
                });
                
                if (!cont[0])
                    break;
                
                LOG.log(Level.FINER, "going to test word: {0}", word[0]);
                
                if (word[0].length() < 2) {
                    //ignore single letter words
                    LOG.log(Level.FINER, "too short");
                    continue;
                }
                
                ValidityType validity = d.validateWord(word[0]);
                
                LOG.log(Level.FINER, "validity: {0}", validity);

                switch (validity) {
                    case PREFIX_OF_VALID:
                    case BLACKLISTED:
                    case INVALID:
                        doc.render(new Runnable() {
                            public void run() {
                                if (!isCanceled()) {
                                    localHighlights.addHighlight(l.getCurrentWordStartOffset(), l.getCurrentWordStartOffset() + word[0].length(), ERROR);
                                }
                            }
                        });
                }
            }
        } finally {
            if (!isCanceled()) {
                SpellcheckerHighlightLayerFactory.getBag(pane).setHighlights(localHighlights);
                Logger.getLogger("TIMER").log(Level.FINE, "Spellchecker",
                        new Object[] {file, System.currentTimeMillis() - startTime});
            }
        }
    }

    private static Map<Document, Dictionary> doc2DictionaryCache = new WeakHashMap<Document, Dictionary>();
    private static Map<Locale, DictionaryImpl> locale2UsersLocalDictionary = new HashMap<Locale, DictionaryImpl>();
    private static Map<Project, Reference<DictionaryImpl>> project2Reference = new WeakHashMap<Project, Reference<DictionaryImpl>>();
    
    public static synchronized DictionaryImpl getUsersLocalDictionary(Locale locale) {
        DictionaryImpl d = locale2UsersLocalDictionary.get(locale);
        
        if (d != null)
            return d;
        
        String userdir = System.getProperty("netbeans.user");
        File cache = new File(userdir, "private-dictionary-" + locale.toString());
        
        locale2UsersLocalDictionary.put(locale, d = new DictionaryImpl(cache));
        
        return d;
    }
    
    public static synchronized DictionaryImpl getProjectDictionary(Project p) {
        Reference<DictionaryImpl> r = project2Reference.get(p);
        DictionaryImpl d = r != null ? r.get() : null;
        
        if (d == null) {
            AuxiliaryConfiguration ac = p.getLookup().lookup(AuxiliaryConfiguration.class);
            
            if (ac != null) {
                project2Reference.put(p, new WeakReference<DictionaryImpl>(d = new DictionaryImpl(ac)));
            }
        }
        
        return d;
    }
    
    public static synchronized Dictionary getDictionary(Document doc) {
        Dictionary result = doc2DictionaryCache.get(doc);
        
        if (result != null) {
            return result;
        }
        
        FileObject file = getFile(doc);

        if (file == null)
            return null;

        Locale locale = LocaleQuery.findLocale(file);
        
        Dictionary d = ACCESSOR.lookupDictionary(locale);
        
        if (d == null)
            return null; //XXX
        
        List<Dictionary> dictionaries = new LinkedList<Dictionary>();
        
        dictionaries.add(getUsersLocalDictionary(locale));
        
        Project p = FileOwnerQuery.getOwner(file);
        
        if (p != null) {
            Dictionary projectDictionary = getProjectDictionary(p);
            
            if (projectDictionary != null) {
                dictionaries.add(projectDictionary);
            }
        }
        
        dictionaries.add(d);
        
        result = CompoundDictionary.create(dictionaries.toArray(new Dictionary[0]));
        
        doc2DictionaryCache.put(doc, result);
        
        return result;
    }

    private synchronized boolean isCanceled() {
        return cancel;
    }

    private synchronized void cancel() {
        cancel = true;
    }

    private synchronized void resume() {
        cancel = false;
    }

    private boolean cancel = false;

    private static final AttributeSet ERROR = AttributesUtilities.createImmutable(EditorStyleConstants.WaveUnderlineColor, Color.RED);

    private static FileObject getFile(Document doc) {
        DataObject file = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);

        if (file == null)
            return null;

        return file.getPrimaryFile();
    }

    public void insertUpdate(DocumentEvent e) {
        documentUpdate();
    }

    public void removeUpdate(DocumentEvent e) {
        documentUpdate();
    }

    public void changedUpdate(DocumentEvent e) {
    }

    private void documentUpdate() {
        doUpdateCurrentVisibleSpan();
        cancel();
    }
    
    private void doUpdateCurrentVisibleSpan() {
        if (SwingUtilities.isEventDispatchThread()) {
            updateCurrentVisibleSpan();
            reschedule();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateCurrentVisibleSpan();
                    reschedule();
                }
            });
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == l) {
            reschedule();
        } else {
            updateCurrentVisibleSpan();
        }
    }
    
    public void caretUpdate(CaretEvent e) {
        synchronized (this) {
            lastCaretPosition = e.getDot();
        }
        
        LOG.fine("scheduling hints computation");
        
        computeHint.schedule(100);
    }
    
    private int lastCaretPosition = -1;
            
    private void computeHint() {
        LOG.entering(ComponentPeer.class.getName(), "computeHint");
        
        final Dictionary d = ComponentPeer.getDictionary(doc);
        
        if (d == null) {
            LOG.fine("dictionary == null");
            LOG.exiting(ComponentPeer.class.getName(), "computeHint");
            return ;
        }
        
        final TokenList l = getTokenList();
        
        if (l == null) {
            //nothing to do:
            LOG.fine("token list == null");
            LOG.exiting(ComponentPeer.class.getName(), "computeHint");
            return ;
        }
        
        final int[] lastCaretPositionCopy = new int[1];
        final Position[] span = new Position[2];
        final CharSequence[]   word = new CharSequence[1];
        
        synchronized (this) {
            lastCaretPositionCopy[0] = lastCaretPosition;
        }
        
        doc.render(new Runnable() {
            public void run() {
                LOG.log(Level.FINE, "lastCaretPosition={0}", lastCaretPositionCopy[0]);
                l.setStartOffset(lastCaretPositionCopy[0]);
                
                if (!l.nextWord()) {
                    LOG.log(Level.FINE, "l.nextWord() == false");
                    return ;
                }
                
                int currentWSO = l.getCurrentWordStartOffset();
                CharSequence w = l.getCurrentWordText();
                int length     = w.length();
                
                LOG.log(Level.FINE, "currentWSO={0}, w={1}, length={2}", new Object[] {currentWSO, w, length});
                
                if (currentWSO <= lastCaretPositionCopy[0] && (currentWSO + length) >= lastCaretPositionCopy[0]) {
                    ValidityType validity = d.validateWord(w);
                    
                    if (validity == ValidityType.BLACKLISTED || validity == ValidityType.INVALID) {
                        try {
                            span[0] = doc.createPosition(currentWSO);
                            span[1] = doc.createPosition(currentWSO + length);
                            word[0] = w;
                        } catch (BadLocationException e) {
                            LOG.log(Level.INFO, null, e);
                        }
                    }
                }
            }
        });
        
        List<Fix> result = new ArrayList<Fix>();
        
        LOG.log(Level.FINE, "word={0}", word[0]);
        
        if (span[0] != null && span[1] != null) {
            String currentWord = word[0].toString();
            
            for (String proposal : d.findProposals(currentWord)) {
                result.add(new DictionaryBasedHint(currentWord, proposal, doc, span, "0" + currentWord));
            }
            
            FileObject file = getFile(doc);

            if (file != null) {
                Project p = FileOwnerQuery.getOwner(file);
                
                if (p != null) {
                    DictionaryImpl projectDictionary = getProjectDictionary(p);
                    
                    if (projectDictionary != null) {
                        result.add(new AddToDictionaryHint(this, projectDictionary, currentWord, "Add \"%s\" to the project's dictionary.", "1" + currentWord));
                    }
                }
            }
            
            Locale locale = LocaleQuery.findLocale(file);
            
            result.add(new AddToDictionaryHint(this, getUsersLocalDictionary(locale), currentWord, "Add \"%s\" to your private dictionary.", "2" + currentWord));
            
            if (!result.isEmpty()) {
                HintsController.setErrors(doc, ComponentPeer.class.getName(), Collections.singletonList(ErrorDescriptionFactory.createErrorDescription(Severity.HINT, "Misspelled word", result, doc, span[0], span[1])));
            } else {
                HintsController.setErrors(doc, ComponentPeer.class.getName(), Collections.<ErrorDescription>emptyList());
            }
        } else {
            HintsController.setErrors(doc, ComponentPeer.class.getName(), Collections.<ErrorDescription>emptyList());
        }
    }
    
    public static LookupAccessor ACCESSOR = new LookupAccessor() {
        public Dictionary lookupDictionary(Locale locale) {
            for (DictionaryProvider p : Lookup.getDefault().lookupAll(DictionaryProvider.class)) {
                Dictionary d = p.getDictionary(locale);
                
                if (d != null)
                    return d;
            }
            
            return null;
        }
        public TokenList lookupTokenList(Document doc) {
            Object mimeTypeObj = doc.getProperty("mimeType");
            String mimeType = "text/plain";
            
            if (mimeTypeObj instanceof String) {
                mimeType = (String) mimeTypeObj;
            }
            
            for (TokenListProvider p : MimeLookup.getLookup(MimePath.get(mimeType)).lookupAll(TokenListProvider.class)) {
                TokenList l = p.findTokenList(doc);
                
                if (l != null)
                    return l;
            }
            
            return null;
            
        }
    };

}
