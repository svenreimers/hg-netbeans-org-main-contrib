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


package org.netbeans.modules.tasklist.suggestions;

import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.loaders.DataObject;
import org.openide.text.*;
import org.openide.nodes.Node;
import org.openide.cookies.EditorCookie;
import org.openide.util.RequestProcessor;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;

/**
 * Broker actively monitors environment and provides
 * suggestion lists (jobs) for:
 * <ul>
 *   <li>{@link #startBroker}, currently opened document (list managed by SuggestionsManager, see getListByRequest)
 *   <li>{@link #startAllOpenedBroker}, all opened documents (list managed by this class)
 *       XXX it does not catch changes/suggestions made by providers on their own
 * </ul>
 *
 * @author Petr Kuzel
 * @author Tor Norbye (transitive from sacked SuggestionManagerImpl)
 */
public final class SuggestionsBroker {

    private static SuggestionsBroker instance;

    private SuggestionList list;

    private int clientCount = 0;

    private SuggestionManagerImpl manager = (SuggestionManagerImpl) SuggestionManagerImpl.getDefault();

    // hook for unit tests
    Env env = new Env();

    // FileObject, Set<Suggestion>
    private Map openedFilesSuggestionsMap = new HashMap();

    private int allOpenedClientsCount = 0;

    /** all opened mode is a client of currently opened  job (this field). */
    private Job allOpenedJob;

    private SuggestionList allOpenedList;

    private static final Logger LOGGER = TLUtils.getLogger(SuggestionsBroker.class);

    // It's list as it need to support duplicates, see overlayed add/remove in start/stop methods
    private List acceptors = new ArrayList(5);

    private final ProviderAcceptor compound = new ProviderAcceptor() {
        public boolean accept(SuggestionProvider provider) {
            Iterator it = acceptors.iterator();
            while (it.hasNext()) {
                ProviderAcceptor acceptor = (ProviderAcceptor) it.next();
                if (acceptor.accept(provider)) return true;
            }
            return false;
        }
    };

    private SuggestionsBroker() {
    }

    public static SuggestionsBroker getDefault() {
        if (instance == null) {
            instance = new SuggestionsBroker();
        }
        return instance;
    }


    public Job startBroker(ProviderAcceptor acceptor) {
        clientCount++;
        if (clientCount == 1) {
            manager.dispatchRun();
            startActiveSuggestionFetching();
        }
        return new Job(acceptor);
    }

    /** Handle for suggestions foe active document. */
    public class Job {

        private boolean stopped = false;
        private final ProviderAcceptor acceptor;

        private Job(ProviderAcceptor acceptor) {
            this.acceptor = acceptor;
            acceptors.add(acceptor);
        }

        public void stopBroker() {
            if (stopped) return;
            stopped = true;
            acceptors.remove(acceptor);

            clientCount--;
            if (clientCount == 0) {
                stopActiveSuggestionFetching();
                // Get rid of suggestion cache, we cannot invalidate its
                // entries properly without keeping a listener
                if (cache != null) {
                    cache.flush();
                }
                list = null;
                instance = null;
            }
        }

        /**
         * Returns live list containing current suggestions.
         * List is made live by invoking {@link SuggestionsBroker#startBroker} and
         * is abandoned ance last client calls {@link Job#stopBroker}.
         * <p>
         * It's global list so listeners must be carefully unregistered
         * unfortunatelly it's rather complex because list
         * is typically passed to  other clasess (TaskChildren).
         * Hopefully you can WeakListener.
         */
        public SuggestionList getSuggestionsList() {
            return getCurrentSuggestionsList();
        }

    }

    /** Starts monitoring all opened files */
    public AllOpenedJob startAllOpenedBroker(ProviderAcceptor acceptor) {
        allOpenedClientsCount++;
        if (allOpenedClientsCount == 1) {
            acceptors.add(acceptor);
            TopComponent[] documents = SuggestionsScanner.openedTopComponents();
            SuggestionsScanner scanner = SuggestionsScanner.getDefault();
            List allSuggestions = new LinkedList();
            for (int i = 0; i<documents.length; i++) {
                DataObject dobj = extractDataObject(documents[i]);
                if (dobj == null) continue;
                FileObject fileObject = dobj.getPrimaryFile();

                // do not scan form files (any file with more topcomponents (editors)) twice
                if (openedFilesSuggestionsMap.keySet().contains(fileObject) == false) {
                    List suggestions = scanner.scanTopComponent(documents[i], compound);
                    openedFilesSuggestionsMap.put(fileObject, suggestions);
                    allSuggestions.addAll(suggestions);
                }
            }
            getAllOpenedSuggestionList().addRemove(allSuggestions, null, true, null, null);

            allOpenedJob = startBroker(acceptor);
        }
        return new AllOpenedJob(acceptor);
    }

    /** Handle to suggestions for all opened files request. */
    public class AllOpenedJob {

        private boolean stopped = false;
        private final ProviderAcceptor acceptor;

        private AllOpenedJob(ProviderAcceptor acceptor) {
            this.acceptor = acceptor;
            acceptors.add(acceptor);
        }

        public SuggestionList getSuggestionList() {
            return getAllOpenedSuggestionList();
        }

        public void stopBroker() {

            if (stopped) return;
            stopped = true;
            acceptors.remove(acceptor);

            allOpenedClientsCount--;
            if (allOpenedClientsCount == 0) {
                allOpenedJob.stopBroker();
                openedFilesSuggestionsMap.clear();
            }
        }
    }

    private SuggestionList getCurrentSuggestionsList() {
        if (list == null) {
            list = new SuggestionList();
        }
        return list;
    }

    private SuggestionList getAllOpenedSuggestionList() {
        if (allOpenedList == null) {
            allOpenedList = new SuggestionList();
        }
        return allOpenedList;
    }

    /*
     * Code related to Document scanning. It listens to the source editor and
     * tracks document opens and closes, as well as "current document" changes.
     * <p>
     * For lightweight document analysis, you can redo the scanning
     * whenever the editor is shown and hidden; for more expensive analysis,
     * you may only want to do it when the document is opened (after a timeout).
     * <p>
     * The API does not define which thread these methods are called on,
     * so don't make any assumptions. If you want to post something on
     * the AWT event dispatching thread for example use SwingUtilities.
     * <p>
     * Note that changes in document attributes only are "ignored" (in
     * the sense that they do not cause document edit notification.)
     *
     * @todo Document threading behavior
     * @todo Document timer behavior (some of the methods are called after
     *   a delay, others are called immediately.)
     *
     */


    /** Current request reference. Used to correlate register()
     * calls with requests sent to rescan()/clear()
     */
    private volatile Long currRequest = new Long(0);

    /** Points to the last completed request. Set to currRequest
     * when rescan() is done.
     */
    private volatile Comparable finishedRequest = null;

    final Object getCurrRequest() {
        return currRequest;
    }

    /**
     * Start scanning for source items.
     * Attaches top component registry and data object
     * registry listeners to monitor currently edited file.
     */
    private void startActiveSuggestionFetching() {

        LOGGER.info("Starting active suggestions fetching....");  // NOI18N

        // must be removed in docStop
        WindowSystemMonitor monitor = getWindowSystemMonitor();
        monitor.enableOpenCloseEvents();
        env.addTCRegistryListener(monitor);
        env.addDORegistryListener(getDataSystemMonitor());

        /* OLD:
        org.openide.windows.TopComponent.getRegistry().
            addPropertyChangeListener(this);

        // Also scan the current node right away: pretend source listener was
        // notified of the change to the current node (which has already occurred)
        // ... unfortunately this is not as easy as just calling getActivatedNodes
        // on the registry -- because that node may not be the last EDITORvisible
        // node... So resort to some hacks.
        Node[] nodes = NewTaskAction.getEditorNodes();
        if (nodes != null) {
            scanner.propertyChange(new PropertyChangeEvent(
              this,
              TopComponent.Registry.PROP_ACTIVATED_NODES,
              null,
              nodes));
        } else {
            // Most likely you're not looking at a panel that has an
            // associated node, e.g. the welcome screen, or the editor isn't
            // open
if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
err.log("Couldn't find current nodes...");
}
        }
        */

        // NEW:
        /** HACK: We need to always know what the current source file
         in the editor is - and even when there isn't a source file
         there, we need to know: if you for example switch to the
         Welcome screen we should remove the tasks for the formerly
         shown source file.

         I've tried listening to the global node, since we should
         always get notified when the current node changes. However,
         this has a couple of problems. First, we may get notified
         of the node change BEFORE the source file is done editing;
         in that case we can't find the node in the editor (we need
         to check that a node is in the editor since we don't want
         the task window to for example show the tasks for the
         current selection in the explorer).  Another problem is
         a scenario I just ran into where if you open A, B from
         explorer, then select A in the explorer, then select B in
         the editor: when you now double click A in the explorer
         there's no rescan. (I may have to debug this).

         So instead I will go to a more reliable scheme, which
         unfortunately smells more like a hack from a NetBeans
         perspective. The basic idea is this: I can find the
         source editor, and which top component is showing in
         the source editor.  I can get notified of when this
         changes - by listening for componentHidden of the
         top most pane. Then I just have to go and see which
         component is now showing, and switch my component listener
         to this new component. (From the component I can discover
         which source file it's editing).  This has the benefit
         that I'll know precisely when a new file has been loaded
         in, etc. It may have the disadvantage that if you open
         source files in other modes (by docking and undocking
         away from the standard configuration) things get
         broken. Perhaps I can keep my old activated-node-listener
         scheme in place as a backup solution when locating the
         source editor mode etc. fails.

         It gets more complicated. What if you open the task window
         when the editor is not visible? Then you can't attach a
         listener to the current window - so you don't get notified
         when a new file is opened. For that reason we also need to
         listen to the workspace's property change notification, which
         will tell us when the set of modes changes in the workspace.

         ...and of course the workspace itself can change. So we need
         to listen to the workspace change notification in the window
         manager as well...
         */

        /*
        WindowManager manager = WindowManager.getDefault();
        manager.addPropertyChangeListener(this);
        Workspace workspace = WindowManager.getDefault().
            getCurrentWorkspace();
        workspace.addPropertyChangeListener(this);
        */

        prepareRescanInAWT(false);
    }

    /** Cache tracking suggestions in recently visited files */
    private SuggestionCache cache = null;

    /** List of suggestions restored from the cache that we must delete
     when leaving this document */
    private List docSuggestions = null;

    /**
     * Prepares current environment. Monitors
     * actual document modification state using DocumentListener
     * and CaretListener. Actual TopComponent is guarded
     * by attached ComponentListener.
     * <p>
     * Must be called from <b>AWT thread only</b>.
     */
    private boolean prepareCurrent() {

        assert SwingUtilities.isEventDispatchThread() : "This method must be run in the AWT thread"; // NOI18N

        // Unregister previous listeners
        if (currentTC != null) {
            currentTC.removeComponentListener(getWindowSystemMonitor());
            currentTC = null;
        }
        if (currentDocument != null) {
            currentDocument.removeDocumentListener(getEditorMonitor());
            handleDocHidden(currentDocument, currentDO);
        }
        removeCurrentCaretListeners();

        // Find which component is showing in it
        // Add my own component listener to it
        // When componentHidden, unregister my own component listener
        // Redo above

        // Locate source editor
        TopComponent tc = env.findActiveEditor();
        if (tc == null) {
            // The last editor-support window in the editor was probably
            // just closed - or was not on top

            // remove suggestions 
            List previous = new ArrayList(getCurrentSuggestionsList().getTasks());
            getCurrentSuggestionsList().addRemove(null, previous, false, null, null);
            // for opened files list it's done ahandeTopComponentCloased

            LOGGER.fine("Cannot find active source editor!");   // during startup
            return false;
        }


        DataObject dao = extractDataObject(tc);
        if (dao == null) {
            // here we have tc found by findActiveEditor() that uses classification logic to detect real editors
            LOGGER.warning("Suspicious editor without dataobject: " + tc);   // NOI18N
            return false;
        }

        /*
        if (dao == lastDao) {
            // We've been asked to scan the same dataobject as last time;
            // don't do that.
            // Most likely you've temporarily switched to another (non-editor)
            // node, and switched back (for example, double clicking on a node
            // in the task window) and we're still on the same file so there's
            // no reason to rescan.  We track changes to the currently scanned
            // object differently (through a document listener).
            err.log("Same dao as last time - not doing anything");
            return; // Don't scan again
        }
        lastDao = dao;
        */

        final EditorCookie edit = (EditorCookie) dao.getCookie(EditorCookie.class);
        if (edit == null) {
            //err.log("No editor cookie - not doing anything");
            return false;
        }

        final Document doc = edit.getDocument(); // Does not block

        /* This comment applies to the old implementation, where
           we're listening on activated node changes. Now that we're
           listening for tab changes, the document should already
           have been read in by the time the tab changes and we're
           notified of it:

        // We might have a race condition here... you open the
        // document, and our property change listener gets notified -
        // but the document hasn't completed loading yet despite our
        // 1 second timer. Thus we might not get a document... However
        // since we continue listening for changes, eventually we WILL
        // discover the document
        */
        if (doc == null) {
            LOGGER.fine("No document is loaded in editor!");  // can happen during startup
            return false;
        }

        currentDocument = doc;
        currentDocument.addDocumentListener(getEditorMonitor());

        // Listen for changes on this component so we know when
        // it's replaced by something else XXX looks like PROP_ACTIVATED duplication
        currentTC = tc;
        currentTC.addComponentListener(getWindowSystemMonitor());

        currentDO = dao;
        currentModified = dao.isModified();

        addCurrentCaretListeners();

        return true;

    }

    /** If served by cache returns true. */
    private boolean serveByCache() {
        if (cache != null) {
            // TODO check scanOnShow too! (when we have scanOnOpen
            // as default instead of scanOnShow as is the case now.
            // The semantics of the flag need to change before we
            // check it here; it's always true. Make it user selectable.)
            docSuggestions = cache.lookup(currentDocument);
            if (docSuggestions != null) {
                manager.register(null, docSuggestions, null, getCurrentSuggestionsList(), true);
                // TODO Consider putting the above on a runtimer - but
                // a much shorter runtimer (0.1 seconds or something like
                // that) such that the editor gets a chance to draw itself
                // etc.

                // Also wipe out the cache items since we will replace them
                // when docHidden is called, or when docEdited is called,
                // etc.
                //cache.remove(document);

                // Remember that we're done "scanning"
                finishedRequest = currRequest;
                return true;
            }
        }
        return false;
    }

    private static  DataObject extractDataObject(TopComponent topComponent) {
        DataObject dobj = (DataObject) topComponent.getLookup().lookup(DataObject.class);
        if (dobj != null && dobj.isValid()) {
            return dobj;
        } else {
            return null;
        }
    }

    /**
     * The given document has been edited or saved, and a time interval
     * (by default around 2 seconds I think) has passed without any
     * further edits or saves.
     * <p>
     * Update your Suggestions as necessary. This may mean removing
     * previously registered Suggestions, or editing existing ones,
     * or adding new ones, depending on the current contents of the
     * document.
     * <p>
     * Spawns <b>Suggestions Broker thread</b> that finishes actula work
     * asynchronously.
     *
     * @param tc
     * @param dataobject The Data Object for the file being opened
     * @param delay postpone the action by delay miliseconds
     *
     * @return parametrized task that rescans given dataobject in delay miliseconds
     */
    private RequestProcessor.Task performRescanInRP(final TopComponent tc, final DataObject dataobject, int delay) {

        /* Scan requests are run in a separate "background" thread.
           However, what happens if the user switches to a different
           tab -while- a scan job is running? If the scan hasn't
           started, the timer is removed, but if the scan is in
           progress, we have to know to discard registered results.
           For that reason, we have a "current request" reference that
           we pass with scan requests, and that scanners will hand
           back with scan results. The reference is an integer.
           When we switch to a new tab, we increment the integer.
           So if we get a registration, with an "old" integer (not the
           current one), we know the results are obsolete.
           We also need to know if the current scan is done (to know
           whether or not we should flush these results into the cache,
           or if scanning must begin from the beginning when we return
           to this file.)   For that reason, we also have a "finished
           request" integer which points to the most recent finished
           request; we only stuff the cache if finished == current.
           We can also use the request flag to bail in the middle of
           iterating over providers in case a new request has arrived.
        */

        // Is MAX_VALUE even feasible here? There's no greater/lessthan
        // comparison, so wrapping around will work just fine, but I may
        // have to check manually and do it myself in case some kind
        // of overflow exception is thrown
        //  Wait, I'm doing a comparison now - look for currRequest.longValue
        assert currRequest.longValue() != Long.MAX_VALUE : "Wrap around logic needed!";  // NOI18N
        currRequest = new Long(currRequest.longValue() + 1);
        final Object origRequest = currRequest;

        // free AWT && Timer threads
        return serializeOnBackground(new Runnable() {
            public void run() {

                scheduledRescan = null;

                // Stale request If so, just drop this one
                //if (origRequest != currRequest) return;

                // code is fixing (modifing) document
                if (wait) {
                    waitingEvent = true;
                    return;
                }

                // reassure that Tc was not meanwhile closed. Both current file job
                // and all opened files job monitors just files opened in editor pane

                if (isTopComponentOpened(tc) == false) {
                    return;
                }

                LOGGER.fine("Dispatching rescan() request to providers...");

                setScanning(true);

                List scannedSuggestions = manager.dispatchScan(dataobject, compound);

                // once again reassure that Tc was not meanwhile closed. Both current file job
                // and all opened files job monitors just files opened in editor pane

                if (isTopComponentOpened(tc) == false) {
                    return;
                }

                // update "allOpened" suggestion list

                if (allOpenedClientsCount > 0) {
                    FileObject fo = dataobject.getPrimaryFile();
                    List previous = (List) openedFilesSuggestionsMap.remove(fo);
                    openedFilesSuggestionsMap.put(fo, scannedSuggestions);

                    // fast check if anything have changed, avoid firing events from tasklist
                    if (previous == null || previous.size() != scannedSuggestions.size() || previous.containsAll(scannedSuggestions) == false) {
                        getAllOpenedSuggestionList().addRemove(scannedSuggestions, previous, false, null, null);
                    }
                }

                if (clientCount > 0) {
                    List previous = new ArrayList(getCurrentSuggestionsList().getTasks());

                    // fast check if anything have changed, avoid firing events from tasklist
                    if (previous == null || previous.size() != scannedSuggestions.size() || previous.containsAll(scannedSuggestions) == false) {
                        getCurrentSuggestionsList().addRemove(scannedSuggestions, previous, false, null, null);
                    }
                }

                // enforce comparable requests, works only for single request source
                if ((finishedRequest == null) ||
                        ((Comparable)origRequest).compareTo(finishedRequest) > 0) {
                    finishedRequest = (Comparable) origRequest;
                }
                if (currRequest == finishedRequest) {
                    setScanning(false);  // XXX global state, works only for single request source
                    LOGGER.fine("It was last pending request.");
                }
            }
        }, delay);
    }

    /**
     * Tests if given topcomponent is opend, Thread safe alternatiove
     * to TopComponent.isOpened().
     */
    private static boolean isTopComponentOpened(final TopComponent tc) {
        if (tc == null) return false;
        final boolean[] isOpened = new boolean[1];
        int attempt = 0;
        while (attempt < 57) {  // reattempt on interrupt that I got from unknow thread
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        isOpened[0] = tc.isOpened();   // must be called from AWT
                    }
                });
                return isOpened[0];
            } catch (InterruptedException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, "[TODO] while get " + tc.getDisplayName() + " state, interrupted, ignoring...");  // NOI18N
                err.notify(ErrorManager.INFORMATIONAL, e);
                attempt++;
            } catch (InvocationTargetException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, "[TODO] cannot get " + tc.getDisplayName() + " state.");  // NOI18N
                err.notify(e);
                return false;
            } finally {
                if (attempt != 0) Thread.currentThread().interrupt();  // retain the interrupted flag
            }
        }
        return false;
    }

    private RequestProcessor rp = new RequestProcessor("Suggestions Broker");  // NOI18N

    /** Enqueue request and perform it on background later on. */
    private RequestProcessor.Task serializeOnBackground(Runnable request, int delay) {
        return rp.post(request, delay , Thread.MIN_PRIORITY);
    }

    /**
     * Grab all the suggestions associated with this document/dataobject
     * and push it into the suggestion cache.
     */
    private void stuffCache(Document document, DataObject dataobject,
                            boolean unregisterOnly) {

        boolean filteredTaskListFixed = false;  //XXX register bellow
        if (filteredTaskListFixed == false) return;

        // XXX Performance: if docSuggestions != null, we should be able
        // to just reuse it, since the document must not have been edited!

        SuggestionList tasklist = getCurrentSuggestionsList();
        if (tasklist.getTasks().size() == 0) {
            return;
        }
        Iterator it = tasklist.getTasks().iterator();
        List sgs = new ArrayList(tasklist.getTasks().size());
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl) it.next();
            Object seed = s.getSeed();
            // Make sure we don't pick up category nodes here!!!
            if (seed != SuggestionList.CATEGORY_NODE_SEED) {
                sgs.add(s);
            }

            Iterator sit = s.subtasksIterator();
            while (sit.hasNext()) {
                s = (SuggestionImpl) sit.next();
                seed = s.getSeed();
                if (seed != SuggestionList.CATEGORY_NODE_SEED) {
                    sgs.add(s);
                }
            }
        }
        if (!unregisterOnly) {
            if (cache == null) {
                cache = new SuggestionCache();
            }
            cache.add(document, dataobject, sgs);
        }

        // Get rid of tasks from list
        // XXX is not it already done by providers, it causes problems
        if (sgs.size() > 0) {
            manager.register(null, null, sgs, tasklist, true);
        }
    }

    /** The top-component we're currently tracking (active one) */
    private TopComponent currentTC = null;

    /** The document we're currently tracking (active one) */
    private Document currentDocument = null;

    /** The data-object we're currently tracking (active one) */
    private DataObject currentDO = null;

    /** The panes we're currently tracking (active one) */    //XXX first element should be enough
    private JEditorPane[] editorsWithCaretListener = null;

    /** The modification status sampled on tracing start and save operation */
    private boolean currentModified = false;

    /** Add caret listener to dataobject's editor panes. */
    private void addCurrentCaretListeners() {

        assert editorsWithCaretListener == null : "addCaretListeners() must not be called twice without removeCaretListeners() => memory leak";  // NOI18N

        EditorCookie edit = (EditorCookie) currentDO.getCookie(EditorCookie.class);
        if (edit != null) {
            JEditorPane panes[] = edit.getOpenedPanes();
            if ((panes != null) && (panes.length > 0)) {
                // We want to know about cursor changes in ALL panes
                editorsWithCaretListener = panes;
                for (int i = 0; i < editorsWithCaretListener.length; i++) {
                    editorsWithCaretListener[i].addCaretListener(getEditorMonitor());
                }
            }
        }
    }

    /** Unregister previously added caret listeners. */
    private void removeCurrentCaretListeners() {
        if (editorsWithCaretListener != null) {
            for (int i = 0; i < editorsWithCaretListener.length; i++) {
                editorsWithCaretListener[i].removeCaretListener(getEditorMonitor());
            }
        }
        editorsWithCaretListener = null;
    }

    boolean pendingScan = false;

    /** Timed task which keeps track of outstanding scan requests; we don't
     scan briefly selected files */
    private volatile RequestProcessor.Task scheduledRescan;

    /**
     * Plan a rescan (meaning: put delayed task into RP). In whole
     * broker there is only one scheduled task (and at maximum one
     * running concurrenly if delay is smaller than execution time).
     *
     * @param delay If true, don't create a rescan if one isn't already
     * pending, but if one is, delay it.
     * @param scanDelay actual delay value in ms
     *
     */
    private void scheduleRescan(boolean delay, final int scanDelay) {

        // This is just a delayer (e.g. for caret motion) - if there isn't
        // already a pending timeout, we're done. Caret motion shouldn't
        // -cause- a rescan, but if one is already planned, we want to delay
        // it.
        if (delay && (scheduledRescan == null)) {
            return;
        }

        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
        if (scheduledRescan != null) {
            scheduledRescan.cancel();
            scheduledRescan = null;
            LOGGER.fine("Scheduled rescan task delayed by " + scanDelay + " ms.");  // NOI18N
        }

        // prepare environment in AWT and post to RP

        Runnable task = new Runnable() {
            public void run() {
                if (prepareCurrent()) {
                    // trap, randomly triggered by multiview
                    assert currentDO.equals(extractDataObject(currentTC)) : "DO=" + currentDO + "  TC=" + currentTC;
                    scheduledRescan = performRescanInRP(currentTC, currentDO, scanDelay);
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    /** An event ocurred during quiet fix period. */
    private boolean waitingEvent = false;
    private boolean wait = false;

    /**
     * Set fix mode (quiet period) in which self initialized modifications are expected.
     * @param wait <ul> <li> true postpone all listeners until ...
     *                  <li> false ressurect listeners activity
     */
    final void setFixing(boolean wait) {
        boolean wasWaiting = this.wait;
        this.wait = wait;
        if (!wait && wasWaiting && (waitingEvent)) {
            scheduleRescan(false, ManagerSettings.getDefault().getEditScanDelay());
            waitingEvent = false;
        }
    }


    /** The set of visible top components changed */
    private void componentsChanged() {
        // We may receive "changed events" from different sources:
        // componentHidden (which is the only source which tells us
        // when you've switched between two open tabs) and
        // TopComponent.registry's propertyChange on PROP_OPENED
        // (which is the only source telling us about tabs closing).

        // However, there is some overlap - when you open a new
        // tab, we get notified by both. So coalesce these events by
        // enquing a change lookup on the next iteration through the
        // event loop; if a second notification comes in during the
        // same event processing iterationh it's simply discarded.

        prepareRescanInAWT(true);

    }

    /**
     * It sends asynchronously to AWT thread (selected editor TC must be grabbed in AWT).
     * Once prepared it sends request to a background thread.
     * @param delay if true schedule later acording to user settings otherwise do immediatelly
     */
    private void prepareRescanInAWT(final boolean delay) {

        // XXX unify with scheduleRescan

        Runnable performer = new Runnable() {
            public void run() {
                if (clientCount > 0) {
                    prepareCurrent();
                    if (serveByCache() == false) {
                        if (ManagerSettings.getDefault().isScanOnShow()) {
                            if (delay) {
                                performRescanInRP(currentTC, currentDO, ManagerSettings.getDefault().getShowScanDelay());
                            } else {
                                performRescanInRP(currentTC, currentDO, 0);
                            }
                        }
                    }
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            performer.run();
        } else {
            // docStop() might have happen
            // in the mean time - make sure we don't do a
            // delay=true when we're not supposed to
            // be processing views
            SwingUtilities.invokeLater(performer);
        }
    }

    /**
     * Stop scanning for source items, deregistering
     * environment listeners.
     */
    private void stopActiveSuggestionFetching() {

        LOGGER.info("Stopping active suggestions fetching....");  // NOI18N

        if (scheduledRescan != null) {
            scheduledRescan.cancel();
            scheduledRescan = null;
        }

        env.removeTCRegistryListener(getWindowSystemMonitor());
        env.removeDORegistryListener(getDataSystemMonitor());

        // Unregister previous listeners
        if (currentTC != null) {
            currentTC.removeComponentListener(getWindowSystemMonitor());
            currentTC = null;
        }
        if (currentDocument != null) {
            currentDocument.removeDocumentListener(getEditorMonitor());
            // NOTE: we do NOT null it out since we still need to
            // see if the document is unchanged
        }
        removeCurrentCaretListeners();

        handleDocHidden(currentDocument, currentDO);
        currentDocument = null;
    }


    private void setScanning(boolean scanning) {
        // XXX fishy direct access to view assuming 1:1 relation with list
//        SuggestionList tasklist = getList();
//        TaskListView v = tasklist.getView();
//        if (v instanceof SuggestionsView) {
//            SuggestionsView view = (SuggestionsView) v;
//            view.setScanning(scanning);
//        }
    }

    private void handleDocHidden(Document document, DataObject dataobject) {
        // This is not right - runTimer is telling us whether we have
        // a request pending - (and we should indeed kill the timer
        // if we do) - but we need to know if a RequestProcessor is
        // actually running.
        if (currRequest != finishedRequest) {
            if (cache != null) {
                cache.remove(document);
            }
            // Remove the items we've registered so far... (partial
            // registration) since we're in the middle of a request
            stuffCache(document, dataobject, true);
        } else {
            stuffCache(document, dataobject, false);
        }

        docSuggestions = null;
    }

    private void handleTopComponentClosed(TopComponent tc) {

        //System.err.println("[TODO] closing: " + tc.getDisplayName());

        componentsChanged();

        DataObject dobj = extractDataObject(tc);
        if (dobj == null) {
            //System.err.println("[TODO] has no DO: " + tc.getDisplayName());
            return;
        }

        List previous = (List) openedFilesSuggestionsMap.remove(dobj.getPrimaryFile());
        if (previous != null) {
            //System.err.println("[TODO] removing TODOs: " + tc.getDisplayName() + " :" + previous);
            getAllOpenedSuggestionList().addRemove(null, previous, false, null, null);
        } else {
            //System.err.println("[TODO] has no TODOs: " + tc.getDisplayName());
        }
    }

    private void handleTopComponentOpened(TopComponent tc) {
        //System.err.println("[TODO] opened: " + tc.getDisplayName());
        if (tc.isShowing()) {
            // currently selected one
            componentsChanged();
        } else {
            // it is not selected anymore, it was opened in burst
            DataObject dao = extractDataObject(tc);
            if (dao == null) return;
            performRescanInRP(tc, dao, ManagerSettings.getDefault().getShowScanDelay());
        }
    }

    private WindowSystemMonitor windowSystemMonitor;

    /** See note on {@link WindowSystemMonitor#enableOpenCloseEvents} */
    private WindowSystemMonitor getWindowSystemMonitor() {
        if (windowSystemMonitor == null) {
            windowSystemMonitor = new WindowSystemMonitor();
        }
        return windowSystemMonitor;
    }

    // The code is unnecesary comples there is pending issue #48937
    private class WindowSystemMonitor implements PropertyChangeListener, ComponentListener {

        /** Previous Set&lt;TopComponent> */
        private Set openedSoFar = Collections.EMPTY_SET;

        /**
         * Must be called before adding this listener to environment if in hope that
         * it will provide (initial) open/close events.
         */
        private void enableOpenCloseEvents() {
            List list = Arrays.asList(SuggestionsScanner.openedTopComponents());
            openedSoFar = new HashSet(list);

            Iterator it = list.iterator();
            while (it.hasNext()) {
                TopComponent tc = (TopComponent) it.next();                
                tc.addComponentListener(new ComponentAdapter() {
                                          public void componentShown(ComponentEvent e) {
                                           TopComponent tcomp = (TopComponent) e.getComponent();
                                           tcomp.removeComponentListener(this);
                                           handleTopComponentOpened(tcomp);
                                          }
                                        });

            }
        }

        /** Reacts to changes */
        public void propertyChange(PropertyChangeEvent ev) {

            String prop = ev.getPropertyName();
            if (prop.equals(TopComponent.Registry.PROP_OPENED)) {

                LOGGER.fine("EVENT opened top-components changed");

//                if (allOpenedClientsCount > 0) {
                    // determine what components have been closed, window system does not
                    // provide any other listener to do it in more smart way

                    List list = Arrays.asList(SuggestionsScanner.openedTopComponents());
                    Set actual = new HashSet(list);

                    if (openedSoFar != null) {
                        Iterator it = openedSoFar.iterator();
                        while(it.hasNext()) {
                            TopComponent tc = (TopComponent) it.next();
                            if (actual.contains(tc) ) continue;
                            handleTopComponentClosed(tc);
                        }

                        Iterator ita = actual.iterator();
                        while(ita.hasNext()) {
                            TopComponent tc = (TopComponent) ita.next();
                            if (openedSoFar.contains(tc)) continue;
                            // defer actual action to componentShown, We need to assure opened TC is
                            // selected one. At this moment previous one is still selected.
                            tc.addComponentListener(new ComponentAdapter() {
                                public void componentShown(ComponentEvent e) {
                                    TopComponent tcomp = (TopComponent) e.getComponent();
                                    tcomp.removeComponentListener(this);
                                    handleTopComponentOpened(tcomp);
                                }
                            });
                        }
                    }

                    openedSoFar = actual;
  //              } else {
//                    componentsChanged();
  //                  openedSoFar = null;
//                }
            } else if (TopComponent.Registry.PROP_ACTIVATED.equals(prop)) {
                LOGGER.fine("EVENT top-component activated");

                TopComponent activated = WindowManager.getDefault().getRegistry().getActivated();
                if (clientCount > 0 && isSelectedEditor(activated) && currentTC == null) {
                    prepareRescanInAWT(false);
                }
            }
        }

        public void componentShown(ComponentEvent e) {
            // Don't care
        }

        public void componentHidden(ComponentEvent e) {

            LOGGER.fine("EVENT " + e.getComponent() + " has been hidden");

            //XXX it does not support both "current file" and "all opened" clients at same time
            if (allOpenedClientsCount == 0) {
                componentsChanged();
            }
        }

        public void componentResized(ComponentEvent e) {
            // Don't care
        }

        public void componentMoved(ComponentEvent e) {
            // Don't care
        }

        private boolean isSelectedEditor(Component tc) {
            Mode mode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
            TopComponent selected = null;
            if (mode != null) {
                selected  = mode.getSelectedTopComponent();
            }
            return selected == tc;
        }
    }


    private DataSystemMonitor dataSystemMonitor;

    private DataSystemMonitor getDataSystemMonitor() {
        if (dataSystemMonitor == null) {
            dataSystemMonitor =  new DataSystemMonitor();
        }
        return dataSystemMonitor;
    }

    /**
     * Listener for DataObject.Registry changes.
     *
     * This class listens for modify-changes of dataobjects such that
     * it can notify files of Save operations.
     */
    private class DataSystemMonitor implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            /* Not sure what the source is, but it isn't dataobject
                 and the javadoc doesn't say anything specific, so
                 I guess I can't rely on that as a filter
            if (e.getSource() != dataobject) {
                // If you reinstate this in some way, make sure it
                // works for Save ALL as well!!!
                return;
            }
            */

            LOGGER.fine("EVENT " + e.getSource() + " changed.");

            Set mods = DataObject.getRegistry().getModifiedSet();
            boolean wasModified = currentModified;
            currentModified = mods.contains(currentDO);
            if (currentModified != wasModified) {
                if (!currentModified) {
                    if (ManagerSettings.getDefault().isScanOnSave()) {
                        scheduleRescan(false, ManagerSettings.getDefault().getSaveScanDelay());
                    }
                }
            }
        }
    }

    private EditorMonitor editorMonitor;

    private EditorMonitor getEditorMonitor() {
        if (editorMonitor == null) {
            editorMonitor = new EditorMonitor();
        }
        return editorMonitor;
    }

    private class EditorMonitor implements DocumentListener, CaretListener {

        //XXX missing reset logic
        private int prevLineNo = -1;

        public void changedUpdate(DocumentEvent e) {
            // Do nothing.
            // Changed update is only called for ATTRIBUTE changes in the
            // document, which I define as not relevant to the Document
            // Suggestion Providers.
        }

        public void insertUpdate(DocumentEvent e) {

            LOGGER.fine("EVENT document changed");

            if (ManagerSettings.getDefault().isScanOnEdit()) {
                scheduleRescan(false, ManagerSettings.getDefault().getEditScanDelay());
            }
        }

        public void removeUpdate(DocumentEvent e) {

            LOGGER.fine("EVENT document changed");

            if (ManagerSettings.getDefault().isScanOnEdit()) {
                scheduleRescan(false, ManagerSettings.getDefault().getEditScanDelay());
            }
        }

        /** Moving the cursor position should cause a delay in document scanning,
         * but not trigger a new update */
        public void caretUpdate(CaretEvent caretEvent) {

            LOGGER.fine("EVENT caret moved");

            scheduleRescan(true, ManagerSettings.getDefault().getEditScanDelay());

            // Check to see if I have any existing errors on this line - and if so,
            // highlight them.
            if (currentDocument instanceof StyledDocument) {
                int offset = caretEvent.getDot();
                int lineno = NbDocument.findLineNumber((StyledDocument) currentDocument, offset);
                if (lineno == prevLineNo) {
                    // Just caret motion on the same line as the previous one -- ignore
                    return;
                }
                prevLineNo = lineno;

                // Here we could add 1 to the line number, since findLineNumber
                // returns a 0-based line number, and most APIs return a 1-based
                // line number; however, Line.Set.getOriginal also expects
                // something zero based, so instead of doing the usual bit
                // of subtracting there, we drop the add and subtract altogether

                // Go to the given line
                Line line = TLUtils.getLineByNumber(currentDO, lineno + 1);
                /*
                try {
                    LineCookie lc = (LineCookie)dataobject.getCookie(LineCookie.class);
                    if (lc != null) {
                        Line.Set ls = lc.getLineSet();
                        if (ls != null) {
                            line = ls.getCurrent(lineno);
                        }
                    }
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
                */
                if (line != null) {
                    // XXX badge editor suggestion in tasklist
                    //[SuggestionsView]setCursorLine(line);
                }
            }
        }

    }



    /**
     * Binding to outer world that can be changed by unit tests
     */
    static class Env {

        void addTCRegistryListener(PropertyChangeListener pcl) {
            TopComponent.getRegistry().addPropertyChangeListener(pcl);
        }

        void removeTCRegistryListener(PropertyChangeListener pcl) {
            TopComponent.getRegistry().removePropertyChangeListener(pcl);
        }

        void addDORegistryListener(ChangeListener cl) {
            DataObject.getRegistry().addChangeListener(cl);

        }

        void removeDORegistryListener(ChangeListener cl) {
            DataObject.getRegistry().removeChangeListener(cl);
        }

        /**
         * Locates active editor topComponent. Must be run in AWT
         * thread. Eliminates Welcome screen, JavaDoc
         * and orher non-editor stuff in EDITOR_MODE.
         * @return tc or <code>null</code> for non-editor selected topcomponent
         */
        private TopComponent findActiveEditor() {
            Mode mode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
            if (mode == null) {
                // The editor window was probablyjust closed
                return null;
            }
            TopComponent tc = mode.getSelectedTopComponent();

            // form files within MultiViewCloneableTopComponent contantly returns null
            // so I got suggestion to use instanceof CloneableEditorSupport.Pane workaround
            // if (tc != null && tc.getLookup().lookup(EditorCookie.class)  != null) {
            if (tc instanceof CloneableEditorSupport.Pane) {
                // Found the source editor...
//                if (tc.isShowing()) {   // FIXME it returns false for components I can positivelly see
                                          // hopefully mode does not return hidden TC as selected one.
                                          // It happens right after startup
                    return tc;
//                }
            }
            return null;
        }
    }

}
