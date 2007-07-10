package org.netbeans.modules.gsf.tools.lexing;

import java.io.Serializable;
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
final class TokenSpyTopComponent extends TopComponent {
    private CaretListener caretListener;
    private JEditorPane lastPane;
    private Document lastDocument;
    
    private static TokenSpyTopComponent instance;
    /** path to the icon used by the component and its open action */
    //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "TokenSpyTopComponent";
    
    private TokenSpyTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(TokenSpyTopComponent.class, "CTL_TokenSpyTopComponent"));
        setToolTipText(NbBundle.getMessage(TokenSpyTopComponent.class, "HINT_TokenSpyTopComponent"));
        //        setIcon(Utilities.loadImage(ICON_PATH, true));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        offsetLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lengthLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        textLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        languageLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        parentId = new javax.swing.JLabel();
        parentOffset = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        parentLength = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        parentText = new javax.swing.JLabel();
        parentLanguage = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        caretOffset = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Token Id:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Offset:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Length:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "Text:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, "Language:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Embedded Offset:");

        org.openide.awt.Mnemonics.setLocalizedText(parentId, "   ");

        org.openide.awt.Mnemonics.setLocalizedText(parentOffset, "   ");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, "Embedded Length:");

        org.openide.awt.Mnemonics.setLocalizedText(parentLength, "   ");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, "Embedded Id:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Embedded Text:");

        org.openide.awt.Mnemonics.setLocalizedText(parentText, "   ");

        org.openide.awt.Mnemonics.setLocalizedText(parentLanguage, "   ");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "Embedded Language:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, "CARET OFFSET:");

        org.openide.awt.Mnemonics.setLocalizedText(caretOffset, "   ");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel3)
                            .add(jLabel5)
                            .add(jLabel7)
                            .add(jLabel9))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(26, 26, 26)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(layout.createSequentialGroup()
                                        .add(12, 12, 12)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                    .add(layout.createSequentialGroup()
                                                        .add(50, 50, 50)
                                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                            .add(languageLabel)
                                                            .add(textLabel)
                                                            .add(lengthLabel)
                                                            .add(offsetLabel)
                                                            .add(idLabel))
                                                        .addContainerGap(342, Short.MAX_VALUE))
                                                    .add(layout.createSequentialGroup()
                                                        .add(jLabel2)
                                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                        .add(parentText)
                                                        .addContainerGap()))
                                                .add(layout.createSequentialGroup()
                                                    .add(jLabel4)
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                    .add(parentOffset)
                                                    .addContainerGap()))
                                            .add(layout.createSequentialGroup()
                                                .add(jLabel12)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(parentId)
                                                .addContainerGap())))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel10)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(parentLength)
                                        .addContainerGap())))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(parentLanguage)
                                .addContainerGap())))
                    .add(layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(caretOffset)
                        .addContainerGap(379, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(idLabel)
                    .add(parentId)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(offsetLabel)
                    .add(parentOffset)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(lengthLabel)
                    .add(parentLength)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(textLabel)
                    .add(parentText)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(languageLabel)
                    .add(parentLanguage)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(caretOffset))
                .addContainerGap(144, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel caretOffset;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JLabel offsetLabel;
    private javax.swing.JLabel parentId;
    private javax.swing.JLabel parentLanguage;
    private javax.swing.JLabel parentLength;
    private javax.swing.JLabel parentOffset;
    private javax.swing.JLabel parentText;
    private javax.swing.JLabel textLabel;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized TokenSpyTopComponent getDefault() {
        if (instance == null) {
            instance = new TokenSpyTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the TokenSpyTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized TokenSpyTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING,
                    "Cannot find MyWindow component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof TokenSpyTopComponent) {
            return (TokenSpyTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING,
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    public void componentOpened() {
    }
    
    public void componentClosed() {
    }
    
    @Override
    public void componentShowing() {
        Node[] ns = TopComponent.getRegistry().getActivatedNodes();

        if (ns.length != 1) {
            return;
        }

//        DataObject dataObject = (DataObject)ns[0].getLookup().lookup(DataObject.class);
        EditorCookie editorCookie = (EditorCookie)ns[0].getLookup().lookup(EditorCookie.class);

        if (editorCookie == null) {
            return;
        }

        if (editorCookie.getOpenedPanes() == null) {
            return;
        }

        if (editorCookie.getOpenedPanes().length < 1) {
            return;
        }

        JEditorPane pane = editorCookie.getOpenedPanes()[0];

        if (caretListener == null) {
            caretListener = new CListener();
        }

        if ((lastPane != null) && (lastPane != pane)) {
            lastPane.removeCaretListener(caretListener);
            lastPane = null;
            lastDocument = null;
        }

        if (lastPane == null) {
            pane.addCaretListener(caretListener);
            lastPane = pane;
            lastDocument = (NbEditorDocument)pane.getDocument();
        }

        final Document doc = editorCookie.getDocument();

        if ((doc == null) || !(doc instanceof NbEditorDocument)) {
            return;
        }
    }
    
    @Override
    public void componentHidden() {
        if (lastPane != null) {
            lastPane.removeCaretListener(caretListener);
            lastPane = null;
            lastDocument = null;
            org.openide.awt.StatusDisplayer.getDefault().setStatusText("");
        }
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return TokenSpyTopComponent.getDefault();
        }
    }

    private void show(int caret) {
        TokenHierarchy th = null;
        caretOffset.setText(Integer.toString(caret));
        if (lastDocument != null) {
            th = TokenHierarchy.get(lastDocument);
            TokenSequence ts = th.tokenSequence();
            ts.move(caret);
            if (ts.moveNext() || ts.movePrevious()) {
                Token token = ts.token();
                TokenId id = token.id();
                idLabel.setText(id.name() + " : " + id.primaryCategory());
                lengthLabel.setText(Integer.toString(token.length()));
                String text = token.text().toString();
                textLabel.setText(text);
                offsetLabel.setText(Integer.toString(ts.offset()));
                languageLabel.setText(ts.language().toString());

                if (ts.embedded() != null) {
                    ts = ts.embedded();
                    ts.move(caret);
                    if (ts.moveNext() || ts.movePrevious()) {
                        token = ts.token();
                        id = token.id();
                        parentId.setText(id.name() + " : " + id.primaryCategory());
                        parentLength.setText(Integer.toString(token.length()));
                        text = token.text().toString();
                        parentText.setText(text);
                        parentOffset.setText(Integer.toString(ts.offset()));
                        parentLanguage.setText(ts.language().toString());
                    }
                } else {
                    parentId.setText("");
                    parentLength.setText("");
                    parentOffset.setText("");
                    parentText.setText("");
                    parentLanguage.setText("");
                }
                return;
            }
        }
        idLabel.setText("");
        lengthLabel.setText("");
        offsetLabel.setText("");
        textLabel.setText("");
        languageLabel.setText("");

        parentId.setText("");
        parentLength.setText("");
        parentOffset.setText("");
        parentText.setText("");
    }
    
    private class CListener implements CaretListener {
        public void caretUpdate(CaretEvent ev) {
            show(ev.getDot());
        }
    }
    
}