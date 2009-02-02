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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.spellchecker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
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
import javax.swing.JEditorPane;
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
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
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
import org.openide.util.NbBundle;
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
        
        final List<int[]> localHighlights = new LinkedList<int[]>();
        
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

            final boolean[] cont = new boolean [1];
            
            doc.render(new Runnable() {
                public void run() {
                    if (isCanceled()) {
                        cont[0] = false;
                        return;
                    } else {
                        l.setStartOffset(span[0]);
                        cont[0] = true;
                    }
                }
            });
            
            if (!cont[0]) {
                return ;
            }

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
                                    localHighlights.add(new int[] {l.getCurrentWordStartOffset(), l.getCurrentWordStartOffset() + word[0].length()});
                                }
                            }
                        });
                }
            }
        } finally {
            if (!isCanceled()) {
                doc.render(new Runnable() {
                    public void run() {
                        if (isCanceled()) {
                            return;
                        }
                        try {
                            if (!(pane instanceof JEditorPane)) {
                                Highlighter h = pane.getHighlighter();

                                if (h != null) {
                                    h.removeAllHighlights();
                                    for (int[] current : localHighlights) {
                                        h.addHighlight(current[0], current[1], new ErrorHighlightPainter());
                                    }
                                }
                            } else {
                                OffsetsBag localHighlightsBag = new OffsetsBag(doc);

                                for (int[] current : localHighlights) {
                                    localHighlightsBag.addHighlight(current[0], current[1], ERROR);
                                }
                                SpellcheckerHighlightLayerFactory.getBag(pane).setHighlights(localHighlightsBag);
                            }
                        } catch (BadLocationException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                });
                
                FileObject file = getFile(doc);

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
            AuxiliaryConfiguration ac = ProjectUtils.getAuxiliaryConfiguration(p);
            project2Reference.put(p, new WeakReference<DictionaryImpl>(d = new DictionaryImpl(p, ac)));
        }
        
        return d;
    }
    
    public static synchronized Dictionary getDictionary(Document doc) {
        Dictionary result = doc2DictionaryCache.get(doc);
        
        if (result != null) {
            return result;
        }
        
        Locale locale;
        
        FileObject file = getFile(doc);

        if (file != null) {
            locale = LocaleQuery.findLocale(file);
        } else {
            locale = DefaultLocaleQueryImplementation.getDefaultLocale();
        }
        
        Dictionary d = ACCESSOR.lookupDictionary(locale);
        
        if (d == null)
            return null; //XXX
        
        List<Dictionary> dictionaries = new LinkedList<Dictionary>();
        
        dictionaries.add(getUsersLocalDictionary(locale));
        
        if (file != null) {
            Project p = FileOwnerQuery.getOwner(file);

            if (p != null) {
                Dictionary projectDictionary = getProjectDictionary(p);

                if (projectDictionary != null) {
                    dictionaries.add(projectDictionary);
                }
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

    private static final AttributeSet ERROR = AttributesUtilities.createImmutable(EditorStyleConstants.WaveUnderlineColor, Color.RED, EditorStyleConstants.Tooltip, NbBundle.getMessage(ComponentPeer.class, "TP_MisspelledWord"));

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
                    
                    if (validity != ValidityType.VALID) {
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
            
                Locale locale = LocaleQuery.findLocale(file);

                result.add(new AddToDictionaryHint(this, getUsersLocalDictionary(locale), currentWord, "Add \"%s\" to your private dictionary.", "2" + currentWord));
            }
            
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
    
    private class ErrorHighlightPainter implements HighlightPainter {
        private ErrorHighlightPainter() {
        }

        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            g.setColor(Color.RED);
            
            try {
                Rectangle start = pane.modelToView(p0);
                Rectangle end = pane.modelToView(p1);

                int waveLength = end.x + end.width - start.x;
                if (waveLength > 0) {
                    int[] wf = {0, 0, -1, -1};
                    int[] xArray = new int[waveLength + 1];
                    int[] yArray = new int[waveLength + 1];

                    int yBase = (int) (start.y + start.height - 2);
                    for (int i = 0; i <= waveLength; i++) {
                        xArray[i] = start.x + i;
                        yArray[i] = yBase + wf[xArray[i] % 4];
                    }
                    g.drawPolyline(xArray, yArray, waveLength);
                }
            } catch (BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

}
