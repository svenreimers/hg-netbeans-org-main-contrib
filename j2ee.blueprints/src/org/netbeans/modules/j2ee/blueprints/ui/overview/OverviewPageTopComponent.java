/*
 * OverviewPageTopComponent.java
 *
 * Created on June 14, 2006, 2:38 AM
 *
 */
package org.netbeans.modules.j2ee.blueprints.ui.overview;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays Blueprint project Information.
 */
/**
 *
 * @author Nitya Doraisamy
 */
public class OverviewPageTopComponent extends TopComponent implements HyperlinkListener {
    
    private static final long serialVersionUID = 6051472310161712674L;
    
    private static OverviewPageTopComponent instance;
    /** path to the icon used by the component and its open action */
    //static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "BluePrintsSolution";
    
    private String overviewFile;
    
    private HtmlBrowser.URLDisplayer browser = HtmlBrowser.URLDisplayer.getDefault();
    
    private static boolean firstTime = true;
    
    private String optionalTabName = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/blueprints/ui/overview/Bundle").
            getString("SOLUTION_EXTRA_PAGE_TAB");
    
    private OverviewPageTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(OverviewPageTopComponent.class, "CTL_OverviewPageTopComponent"));
        setToolTipText(NbBundle.getMessage(OverviewPageTopComponent.class, "HINT_OverviewPageTopComponent"));
        //setIcon(Utilities.loadImage(ICON_PATH, true));
        
        jTabbedPane1.remove(optionalScrollPane);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainScrollPane = new javax.swing.JScrollPane();
        mainEditorPane = new javax.swing.JEditorPane();
        optionalScrollPane = new javax.swing.JScrollPane();
        optionalEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        mainScrollPane.setViewportView(mainEditorPane);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/blueprints/ui/overview/Bundle").getString("SOLUTION_PAGE_TAB"), mainScrollPane);

        optionalScrollPane.setViewportView(optionalEditorPane);

        jTabbedPane1.addTab("tab2", optionalScrollPane);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JEditorPane mainEditorPane;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JEditorPane optionalEditorPane;
    private javax.swing.JScrollPane optionalScrollPane;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized OverviewPageTopComponent getDefault() {
        if (instance == null) {
            instance = new OverviewPageTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the OverviewPageTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized OverviewPageTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find OverviewPage component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof OverviewPageTopComponent) {
            return (OverviewPageTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    public void componentOpened() {
        mainEditorPane.setEditorKit(new HTMLEditorKit());
        mainEditorPane.setEditorKitForContentType("text/html", new HTMLEditorKit()); //NOI18N
        mainEditorPane.setEditable(false);
        mainEditorPane.addHyperlinkListener(this);
        try {
            URL demoDetailsURL = new URL("nbresloc:" + this.overviewFile);
            mainEditorPane.setPage(demoDetailsURL);
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
    }
    
    public void hyperlinkUpdate(HyperlinkEvent e) {
        HyperlinkEvent.EventType type = e.getEventType();
        if (type == HyperlinkEvent.EventType.ACTIVATED) {
            updatePageWithURL(e.getURL());
        } 
    }
    
    protected void updatePageWithURL(URL u) {
        if (u != null) {
            String protocol = u.getProtocol();
            // Launch browser for these types of links
            if (protocol != null && (protocol.equals("http") || protocol.equals("https"))) {
                browser.showURL(u);
            } else {
                Cursor currentC = mainEditorPane.getCursor();
                Cursor busyC = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                try {
                   //if(firstTime){
                        optionalEditorPane.setEditorKit(new HTMLEditorKit());
                        mainEditorPane.setEditorKitForContentType("text/html", new HTMLEditorKit()); //NOI18N
                        optionalEditorPane.setEditable(false);        
                        optionalEditorPane.addHyperlinkListener(this);
                        optionalEditorPane.setPage(u);
                        firstTime = false;
                    //}   
                    optionalEditorPane.setPage(u);
                    String fileName = optionalTabName;
                    File f = new File(u.getFile());
                    if(f != null)
                        fileName = f.getName();
                    jTabbedPane1.addTab(fileName, optionalScrollPane);
                    jTabbedPane1.setSelectedComponent(optionalScrollPane);
                    optionalEditorPane.setCursor(busyC);
                } catch (IOException e) {
                     mainEditorPane.setCursor(currentC);
                } finally {
                     mainEditorPane.setCursor(currentC);
                }
            }
        }
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    public void setOverviewFile(String overviewFile){
        this.overviewFile = overviewFile;
        jTabbedPane1.removeAll();
        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/blueprints/ui/overview/Bundle").getString("SOLUTION_PAGE_TAB"), mainScrollPane);
    }
        
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return OverviewPageTopComponent.getDefault();
        }
    }
    
}