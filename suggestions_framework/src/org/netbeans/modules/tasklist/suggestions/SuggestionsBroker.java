/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.suggestions;

import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.loaders.DataObject;
import org.openide.text.EditorSupport;
import org.openide.text.CloneableEditor;
import org.openide.text.NbDocument;
import org.openide.text.Line;
import org.openide.nodes.Node;
import org.openide.cookies.EditorCookie;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.spi.tasklist.DocumentSuggestionProvider;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Broker actively monitors environment and provides
 * suggestions valid in current context (open document).
 *
 * @author Petr Kuzel
 * @author Tor Norbye (transitive from sacked SuggestionManagerImpl)
 */
public final class SuggestionsBroker {

    private static SuggestionsBroker instance;

    private SuggestionList list;

    private int clientCount = 0;

    private SuggestionManagerImpl manager = (SuggestionManagerImpl) SuggestionManagerImpl.getDefault();

    private SuggestionsBroker() {

    }

    public static SuggestionsBroker getDefault() {
        if (instance == null) {
            instance = new SuggestionsBroker();
        }
        return instance;
    }


    public Job startBroker() {
        clientCount++;
        if (clientCount == 1) {
            manager.dispatchRun();
            startActiveSuggestionFetching();
        }
        return new Job();
    }

    public class Job {
        public void stopBroker() {
            clientCount--;
            if (clientCount == 0) {
                stopActiveSuggestionFetching();
                // Get rid of suggestion cache, we cannot invalidate its
                // entries properly without keeping a listener
                if (cache != null) {
                    cache.flush();
                }
                // it should not be used anyway
                // XXX keep instance for case it's
                list.clear();
            }
        }

        /**
         * Returns live list containing current suggestions.
         * List is made live by invoking {@link #startBroker} and
         * is abandoned ance last client calls {@link Job#stopBroker}.
         * @return
         */
        public SuggestionList getSuggestionsList() {
            return getSuggestionsList();
        }

    }

    final SuggestionList getSuggestionsList() {
        if (list == null) {
            list = new SuggestionList();
        }
        return list;
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


    private Document document = null;
    private DataObject dataobject = null;
    private boolean notSaved = false;


    /** Set to the request generation when a new file has been shown */
    private volatile Long haveShown = null;

    /** Set to the request generation when a file has been saved */
    private volatile Long haveSaved = null;

    /** Set to the request generation when a file has been edited */
    private volatile Long haveEdited = null;

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

        // must be removed in docStop
        TopComponent.getRegistry().addPropertyChangeListener(getWindowSystemMonitor());
        DataObject.getRegistry().addChangeListener(getDataSystemMonitor());

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

        if (pendingScan) {
            return;
        }
        pendingScan = true;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // docStop() might have happened
                // in the mean time - make sure we don't do a
                // findCurrentFile(true) when we're not supposed to
                // be processing views
                if (clientCount > 0) {
                    findCurrentFile(false);
                }
                pendingScan = false;
            }
        });

    }

    /** Cache tracking suggestions in recently visited files */
    private SuggestionCache cache = null;

    /** List of suggestions restored from the cache that we must delete
     when leaving this document */
    private List docSuggestions = null;

    /**
     * Queries passive providers for suggestions. Monitors
     * actual document modification state using DocumentListener
     * and CaretListener. Actual topcomponent is guarded
     * by attached ComponentListener.
     *
     * @param delayed
     */
    private void findCurrentFile(boolean delayed) {
        // Unregister previous listeners
        if (current != null) {
            current.removeComponentListener(getWindowSystemMonitor());
            current = null;
        }
        if (document != null) {
            document.removeDocumentListener(getEditorMonitor());
            handleDocHidden(document, dataobject);
        }
        if (editors != null) {
            removeCaretListeners();
        }


        // Find which component is showing in it
        // Add my own component listener to it
        // When componentHidden, unregister my own component listener
        // Redo above

        // Locate source editor
        Workspace workspace = WindowManager.getDefault().getCurrentWorkspace();

        // HACK ALERT !!! HACK ALERT!!! HACK ALERT!!!
        // Look for the source editor window, and then go through its
        // top components, pick the one that is showing - that's the
        // front one!
        Mode mode = workspace.findMode(EditorSupport.EDITOR_MODE);
        if (mode == null) {
            // The editor window was probablyjust closed
            return;
        }
        TopComponent[] tcs = mode.getTopComponents();
        for (int j = 0; j < tcs.length; j++) {
            TopComponent tc = tcs[j];
            /*
            if (tc instanceof EditorSupport.Editor) {
                // Found the source editor...
                if (tc.isShowing()) {
		    current = tc;
                    break;
                }
            } else */ if (tc instanceof CloneableEditor) {
                // Found the source editor...
                if (tc.isShowing()) {
                    current = tc;
                    break;
                }
            }
        }
        if (current == null) {
            // The last editor-support window in the editor was probably
            // just closed - or was not on top
            return;
        }

        // Listen for changes on this component so we know when
        // it's replaced by something else
        //System.err.println("Add component listener to " + tcToDO(current));
        current.addComponentListener(getWindowSystemMonitor());

        Node[] nodes = current.getActivatedNodes();

        if ((nodes == null) || (nodes.length != 1)) {
/*
            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                err.log(
                  "Unexpected editor component activated nodes " + // NOI18N
                  " contents: " + nodes); // NOI18N
            }
            */
            return;
        }

        Node node = nodes[0];

        final DataObject dao = (DataObject) node.getCookie(DataObject.class);
        //err.log("Considering data object " + dao);
        if (dao == null) {
            return;
        }

        if (!dao.isValid()) {
            //err.log("The data object is not valid!");
            return;
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
            return;
        }

        /* This is probably not necessary now with my new editor-tracking
           scheme: I'm only calling this on visible components. This is
           a leftover from my noderegistry listener days, and I'm keeping
           it around in case I leave the NodeRegistry lister code in as
           a fallback mechanism, since this is all a bit of a hack.
        // See if it looks like this data object is visible
        JEditorPane[] panes = edit.getOpenedPanes();
        if (panes == null) {
            err.log("No editor panes for this data object");
            return;
        }
        int k = 0;
        for (; k < panes.length; k++) {
            if (panes[k].isShowing()) {
            break;
            }
        }
        if (k == panes.length) {
            err.log("No editor panes for this data object are visible");
            return;
        }
        */

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
            //err.log("No document handle...");
            return;
        }

        if (document != null) {
            // Might be a duplicate removeDocumentListener -- that's
            // okay right?
            document.removeDocumentListener(getEditorMonitor());
        }
        document = doc;
        doc.addDocumentListener(getEditorMonitor());

        dataobject = dao;
        notSaved = dao.isModified();

        // TODO: Is MAX_VALUE even feasible here? There's no greater/lessthan
        // comparison, so wrapping around will work just fine, but I may
        // have to check manually and do it myself in case some kind
        // of overflow exception is thrown
        //  Wait, I'm doing a comparison now - look for currRequest.longValue
        currRequest = new Long(currRequest.intValue() + 1);

        manager.dispatchDocShown(doc, dataobject);
        haveShown = currRequest;
        addCaretListeners();

        // XXX Use scheduleRescan instead? (but then I have to call docShown instead of rescan;
        //haveShown = currRequest;
        //scheduleRescan(null, false, showScanDelay);

        if (cache != null) {
            // TODO check scanOnShow too! (when we have scanOnOpen
            // as default instead of scanOnShow as is the case now.
            // The semantics of the flag need to change before we
            // check it here; it's always true. Make it user selectable.)
            docSuggestions = cache.lookup(document);
            if (docSuggestions != null) {
                manager.register(null, docSuggestions, null, getSuggestionsList(), true);
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
                return;
            }
        }

        if (delayed) {
            runTimer = new Timer(ManagerSettings.getDefault().getShowScanDelay(),
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            runTimer = null;
/*
                             if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                                 err.log("Timer expired - time to scan " + dao);
                             }
                             */
                            performRescan(doc, dataobject);
                        }
                    });
            runTimer.setRepeats(false);
            runTimer.setCoalesce(true);
            runTimer.start();
        } else {
            // Do it right away
            performRescan(doc, dataobject);
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
     * @param document The document being edited
     * @param dataobject The Data Object for the file being opened
     */
    private void performRescan(final Document document,
                        final DataObject dataobject) {
        setScanning(true);

        if ((docSuggestions != null) && (docSuggestions.size() > 0)) {
            // Clear out previous items before a rescan
            manager.register(null, null, docSuggestions, getSuggestionsList(), true);
            docSuggestions = null;
        }

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

        final Object origRequest = currRequest;
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {

                boolean saved = (haveSaved == currRequest);
                boolean edited = (haveEdited == currRequest);
                boolean shown = (haveShown == currRequest);

                // Has the request changed? If so, just drop this one
                if (origRequest != currRequest) {  //XXX works only for one request source (broker)
                    return;                    // use Comparable instead defining that it
                                               // must return 0 if compared to foreign request
                }

                if ((saved && ManagerSettings.getDefault().isScanOnSave())
                || (edited && ManagerSettings.getDefault().isScanOnEdit())
                || (shown && ManagerSettings.getDefault().isScanOnShow())
                ) {
                    manager.dispatchRescan(document, dataobject, currRequest);
                }

                // FIXME enforce comparable requests, works only for single request source
                if ((finishedRequest == null) ||
                        ((Comparable)origRequest).compareTo(finishedRequest) > 0) {
                    finishedRequest = (Comparable) origRequest;
                }
                if (currRequest == finishedRequest) {
                    setScanning(false);  // XXX global state, works only for single request source
                }
            }
        });
    }


    /**
     * Grab all the suggestions associated with this document/dataobject
     * and push it into the suggestion cache.
     */
    private void stuffCache(Document document, DataObject dataobject,
                            boolean unregisterOnly) {
        // XXX Performance: if docSuggestions != null, we should be able
        // to just reuse it, since the document must not have been edited!

        SuggestionList tasklist = getSuggestionsList();
        if (tasklist.getTasks() == null) {
            return;
        }
        Iterator it = tasklist.getTasks().iterator();
        List sgs = new ArrayList(tasklist.getTasks().size());
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl) it.next();
            Object seed = s.getSeed();
            // Make sure we don't pick up category nodes here!!!
            if (seed instanceof DocumentSuggestionProvider) {
                sgs.add(s);
            }

            if (s.hasSubtasks()) {
                Iterator sit = s.getSubtasks().iterator();
                while (sit.hasNext()) {
                    s = (SuggestionImpl) sit.next();
                    seed = s.getSeed();
                    if (seed instanceof DocumentSuggestionProvider) {
                        sgs.add(s);
                    }
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
        if (sgs.size() > 0) {
            manager.register(null, null, sgs, tasklist, true);
        }
    }

    /** The topcomponent we're currently tracking as the showing
     editor component */
    private TopComponent current = null;

    private JEditorPane[] editors = null;

    private void addCaretListeners() {
        EditorCookie edit = (EditorCookie) dataobject.getCookie(EditorCookie.class);
        if (edit != null) {
            JEditorPane panes[] = edit.getOpenedPanes();
            if ((panes != null) && (panes.length > 0)) {
                // We want to know about cursor changes in ALL panes
                editors = panes;
                for (int i = 0; i < editors.length; i++) {
                    editors[i].addCaretListener(getEditorMonitor());
                }
            }
        }
    }

    private void removeCaretListeners() {
        if (editors != null) {
            for (int i = 0; i < editors.length; i++) {
                editors[i].removeCaretListener(getEditorMonitor());
            }
        }
        editors = null;
    }



    boolean pendingScan = false;

    /** Timer which keeps track of outstanding scan requests; we don't
     scan briefly selected files */
    private Timer runTimer;


    /** Plan a rescan (meaning: start timer)
     * @param delay If true, don't create a rescan if one isn't already
     * pending, but if one is, delay it.
     * @param docEvent Document edit event. May be null. */
    private void scheduleRescan(final DocumentEvent docEvent, boolean delay,
                                int scanDelay) {
        if (wait) {
            if (docEvent != null) {
                // Caret updates shouldn't clear my docEvent
                waitEvent = docEvent;
            }
            return;
        }

        // This is just a delayer (e.g. for caret motion) - if there isn't
        // already a pending timeout, we're done. Caret motion shouldn't
        // -cause- a rescan, but if one is already planned, we want to delay
        // it.
        if (delay && (runTimer == null)) {
            return;
        }

        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
        if (runTimer != null) {
            runTimer.stop();
            runTimer = null;
        }
        currDelay = scanDelay;
        runTimer = new Timer(currDelay,
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        runTimer = null;
                        if (!wait) {
                            performRescan(document, dataobject);
                        }
                    }
                });
        runTimer.setRepeats(false);
        runTimer.setCoalesce(true);
        runTimer.start();
    }

    /** Most recent delay */
    private int currDelay = 500;

    private DocumentEvent waitEvent = null;
    private boolean wait = false;

    final void setFixing(boolean wait) {
        boolean wasWaiting = this.wait;
        this.wait = wait;
        if (!wait && wasWaiting && (waitEvent != null)) {
            scheduleRescan(waitEvent, false, currDelay);
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

        if (pendingScan) {
            return;
        }
        pendingScan = true;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // docStop() might have happened
                // in the mean time - make sure we don't do a
                // findCurrentFile(true) when we're not supposed to
                // be processing views
                if (clientCount > 0) {
                    findCurrentFile(true);
                }
                pendingScan = false;
            }
        });
    }

    /**
     * Stop scanning for source items, deregistering
     * environment listeners.
     */
    private void stopActiveSuggestionFetching() {
        if (runTimer != null) {
            runTimer.stop();
            runTimer = null;
        }

        TopComponent.getRegistry().removePropertyChangeListener(getWindowSystemMonitor());

        DataObject.getRegistry().removeChangeListener(getDataSystemMonitor());

        // Unregister previous listeners
        if (current != null) {
            current.removeComponentListener(getWindowSystemMonitor());
            current = null;
        }
        if (document != null) {
            document.removeDocumentListener(getEditorMonitor());
            // NOTE: we do NOT null it out since we still need to
            // see if the document is unchanged
        }
        if (editors != null) {
            removeCaretListeners();
        }

        handleDocHidden(document, dataobject);
        document = null;
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

        manager.dispatchDocHidden(document, dataobject, currRequest);
    }

    private WindowSystemMonitor windowSystemMonitor;

    private WindowSystemMonitor getWindowSystemMonitor() {
        if (windowSystemMonitor == null) {
            windowSystemMonitor = new WindowSystemMonitor();
        }
        return windowSystemMonitor;
    }

    private class WindowSystemMonitor implements PropertyChangeListener, ComponentListener {
        /** Reacts to changes */
        public void propertyChange(PropertyChangeEvent ev) {
            String prop = ev.getPropertyName();
            if (prop.equals(TopComponent.Registry.PROP_OPENED)) {
                componentsChanged();
            }
        }

        public void componentShown(ComponentEvent e) {
            // Don't care
        }

        public void componentHidden(ComponentEvent e) {
            componentsChanged();
        }

        public void componentResized(ComponentEvent e) {
            // Don't care
        }

        public void componentMoved(ComponentEvent e) {
            // Don't care
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
            Set mods = DataObject.getRegistry().getModifiedSet();
            boolean wasModified = notSaved;
            notSaved = mods.contains(dataobject);
            if (notSaved != wasModified) {
                if (!notSaved) {
                    haveSaved = currRequest;
                    scheduleRescan(null, false, ManagerSettings.getDefault().getSaveScanDelay());
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
            haveEdited = currRequest;
            scheduleRescan(e, false, ManagerSettings.getDefault().getEditScanDelay());

            // If there's a visible marker annotation on the line, clear it now
            clearMarker();
        }

        public void removeUpdate(DocumentEvent e) {
            haveEdited = currRequest;
            scheduleRescan(e, false, ManagerSettings.getDefault().getEditScanDelay());

            // If there's a visible marker annotation on the line, clear it now
            clearMarker();
        }

        /** Moving the cursor position should cause a delay in document scanning,
         * but not trigger a new update */
        public void caretUpdate(CaretEvent caretEvent) {
            scheduleRescan(null, true, currDelay);

            // Check to see if I have any existing errors on this line - and if so,
            // highlight them.
            if (document instanceof StyledDocument) {
                int offset = caretEvent.getDot();
                int lineno = NbDocument.findLineNumber((StyledDocument) document, offset);
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
                Line line = null;
                line = TLUtils.getLineByNumber(dataobject, lineno + 1);
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

        /** Get rid of any annotations marking the current task */
        private void clearMarker() {
            SuggestionsView tlv = SuggestionsView.getCurrentView();
            if (tlv != null) {
                tlv.hideTask();
            }
        }

    }

}
