/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.core.browser.djnsswt;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.core.browser.api.WebBrowser;
import org.openide.awt.HtmlBrowser;

/**
 * HTML browser implementation which uses embedded native browser component.
 *
 * @author S. Aubrecht
 */
class HtmlBrowserImpl extends HtmlBrowser.Impl {

    private WebBrowser browser;
    private final Object LOCK = new Object();
    private final BrowserType type;

    public HtmlBrowserImpl(BrowserType type) {
        super();
        this.type = type;
    }

    @Override
    public Component getComponent() {
        return getBrowser().getComponent();
    }

    private WebBrowser getBrowser() {
        synchronized( LOCK ) {
            if( null == browser ) {
                browser = new WebBrowserImpl(type);
            }
            return browser;
        }
    }

    @Override
    public void reloadDocument() {
        getBrowser().reloadDocument();
    }

    @Override
    public void stopLoading() {
        getBrowser().stopLoading();
    }

    @Override
    public void setURL(final URL url) {
//        if( !SwingUtilities.isEventDispatchThread() ) {
//            SwingUtilities.invokeLater( new Runnable() {
//
//                @Override
//                public void run() {
//                    setURL( url );
//                }
//            });
//            return;
//        }
        getBrowser().setURL(url.toString());
    }

    @Override
    public URL getURL() {
        if( !SwingUtilities.isEventDispatchThread() )
            return null;
        String strUrl = getBrowser().getURL();
        if( null == strUrl )
            return null;
        try {
            return new URL(strUrl);
        } catch( MalformedURLException ex ) {
            Logger.getLogger(HtmlBrowserImpl.class.getName()).log(Level.FINE, null, ex);
        }
        return null;
    }

    @Override
    public String getLocation() {
        return getBrowser().getURL();
    }

    @Override
    public void setLocation(String str) {
        getBrowser().setURL(str);
    }

    @Override
    public String getStatusMessage() {
        return getBrowser().getStatusMessage();
    }

    @Override
    public String getTitle() {
        return getBrowser().getTitle();
    }

    @Override
    public boolean isForward() {
        return getBrowser().isForward();
    }

    @Override
    public void forward() {
        getBrowser().forward();
    }

    @Override
    public boolean isBackward() {
        return getBrowser().isBackward();
    }

    @Override
    public void backward() {
        getBrowser().backward();
    }

    @Override
    public boolean isHistory() {
        return getBrowser().isHistory();
    }

    @Override
    public void showHistory() {
        getBrowser().showHistory();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        getBrowser().addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        getBrowser().removePropertyChangeListener(l);
    }

    @Override
    public void dispose() {
        synchronized( LOCK ) {
            if( null != browser ) {
                browser.dispose();
            }
            browser = null;
        }
    }
}
