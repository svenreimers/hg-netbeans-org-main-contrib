/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.stripwhitespace;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.GuardedDocument;
import org.openide.util.NbBundle;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.MarkBlock;
import org.netbeans.editor.Registry;
import org.openide.ErrorManager;
import org.openide.util.WeakListeners;

public final class StripWhitespaceAction extends AbstractAction implements ChangeListener {

    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("org.netbeans.modules.stripwhitespace.StripWhitespaceAction"); // NOI18N
    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);

    public StripWhitespaceAction() {
        super(NbBundle.getMessage(StripWhitespaceAction.class, "LBL_StripWhitespaceAction"),
            new ImageIcon(org.openide.util.Utilities.loadImage(
                    "org/netbeans/modules/stripwhitespace/removetralingwhitespace.gif")));

        Registry.addChangeListener (WeakListeners.change(this, Registry.class));
    }

    public void actionPerformed(ActionEvent e) {
        BaseDocument d = getCurrentDocument();
        if (d != null) {
            d.runAtomicAsUser(new Stripper(d));
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private BaseDocument getCurrentDocument() {
        JTextComponent nue = Registry.getMostActiveComponent();
        if (nue == null) {
            return null;
        }

        Document d = nue.getDocument();
        if (d instanceof BaseDocument) {
            return (BaseDocument) d;
        } else {
            return null;
        }
    }

    public void stateChanged(ChangeEvent e) {
        setEnabled(getCurrentDocument() != null);
    }

    private static final class Stripper implements Runnable {
        private final BaseDocument d;
        public Stripper (BaseDocument d) {
            this.d = d;
        }

        public void run() {
            int ct = d.getDefaultRootElement().getElementCount();
            if (LOG) {
                LOGGER.log(ErrorManager.INFORMATIONAL, ct + " elements to strip, document length " + d.getLength() ); // NOI18N
            }
            try {
                for (int i=ct-1; i >=0; i--) {
                    Element curr = d.getDefaultRootElement().getElement(i);
                    String s = d.getText(curr.getStartOffset(), curr.getEndOffset() - curr.getStartOffset());
                    int toRemove = 0;
                    for (int j=s.length()-1; j >= 0; j--) {
                        if (d.isWhitespace(s.charAt(j))) {
                            toRemove++;
                        } else {
                            break;
                        }
                    }
                    if (toRemove > 1) {
                        int removeStartOffset = curr.getEndOffset() - toRemove;
                        int removeLength = toRemove - 1;
                        boolean remove = true;

                        if (d instanceof GuardedDocument) {
                            GuardedDocument gd = (GuardedDocument)d;
                            int comp = gd.getGuardedBlockChain().compareBlock(removeStartOffset, removeStartOffset + removeLength);
                            if ((comp & MarkBlock.OVERLAP) != 0) {
                                remove = false;
                            }
                        }

                        if (remove) {
                            if (LOG) {
                                LOGGER.log(ErrorManager.INFORMATIONAL, "Remove from " + removeStartOffset + " " + removeLength + " chars "); // NOI18N
                            }
                            d.remove(removeStartOffset, removeLength);
                        }
                    }
                }
            } catch (BadLocationException e) {
                //rollback somehow?
                ErrorManager.getDefault().notify(e);
            }
        }
    }
}
