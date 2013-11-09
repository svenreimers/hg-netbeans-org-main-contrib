/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb;

import java.io.File;
import java.io.IOException;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/** HTTP and WebSocket server for answering Javac like queries.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
final class JavacServer {
    private static JavacServer INSTANCE;
    private final HttpServer server;
    private JavacServer(HttpServer s) {
        this.server = s;
    }
    
    public synchronized static JavacServer getDefault() {
        if (INSTANCE == null) {
            INSTANCE = new JavacServer(create());
        }
        return INSTANCE;
    }
    
    public int getPort() {
        return server.getListeners().iterator().next().getPort();
    }
    
    //
    // Implementation
    //
    
    private static HttpServer create() {
        String web4dew = System.getProperty("web4dew");
        HttpServer h = HttpServer.createSimpleServer(null, new PortRange(9000, 65535));
        if (web4dew == null) {
            File index = InstalledFileLocator.getDefault().locate("web4dew/index.html", "org.netbeans.modules.dew4nb", true);
            h.getServerConfiguration().addHttpHandler(new StaticHttpHandler(index.getParent()), "/");
        } else {
            StaticHttpHandler sh = new StaticHttpHandler(web4dew);
            sh.setFileCacheEnabled(false);
            h.getServerConfiguration().addHttpHandler(sh, "/");
        }
        final WebSocketAddOn addon = new WebSocketAddOn();
        for (NetworkListener listener : h.getListeners()) {
            listener.registerAddOn(addon);
        }
        WebSocketEngine.getEngine().register("", "/javac", new JavacApplication());
        try {
            h.start();
            return h;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private static class JavacApplication extends WebSocketApplication {
        private final JavacEndpoint endpoint;

        JavacApplication() {
            this.endpoint = JavacEndpoint.newCompiler();
        }

        @Override
        public void onMessage(WebSocket socket, String text) {
            try {
                JavacResult res = endpoint.doCompile(text);
                socket.send(res.toString());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                socket.send("{ \"status\" : \"" + ex.getMessage() + "\"");
            }
        }

    }
}
    
