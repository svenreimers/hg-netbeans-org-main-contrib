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
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
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
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber;

import java.util.Vector;
import javax.swing.JPanel;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/**
 * A Manager encapsulates one Jabber connection and provides UI for it.
 *
 * @author  nenik
 */
public class Manager {
    private Settings settings;
    private Connection con;
    private MessageQueue messages;
    private Forwarder listener = new Forwarder();
    private Contact.List cList;
    private Vector listeners = new Vector();
    

    public static final int CONNECTION_ERROR = -1;
    public static final int CONNECTION_DISCONNECTED = 0;
    public static final int CONNECTION_CONNECTING = 1;
    public static final int CONNECTION_LOGGING = 2;
    public static final int CONNECTION_DATAPHASE = 3;
    public static final int CONNECTION_READY = 4;
    
    public static final int STATUS_XA = 2;
    public static final int STATUS_AWAY = 3;
    public static final int STATUS_DND = 4;
    public static final int STATUS_ONLINE = 5;
    public static final int STATUS_CHATTY = 6;
    
    /* current state of connection.
     * available states from CONNECTION_*,  see above */
    private int connectionStatus = 0;

    /* current published presence.
     * available states from STATUS_* and 0, see above */
    private int presenceStatus = 0;
    
        
    
    /** Creates a new instance of Manager */
    public Manager(Settings set) /*throws XMPPException */{
        this.settings = set;
        messages = new MessageQueue();
        
        // XXX - provisional, will handle timeouts in user UI
	int timeout = (int)set.getTimeout();
        if (timeout > 0) SmackConfiguration.setPacketReplyTimeout(timeout);

        con = new Connection(this);

        cList = new Contact.List(this);

        if (settings.getAutologin() == 1 || settings.getAutologin() == 3) {
            new Thread("Jabber autologin") {
                public void run() {
                    try {
                        login(); // XXX - do asynchronous login
                    } catch (Exception e) {
                        // XXX - just log?
                    }
                }
            }.start();
        }

    }
    
    
    
    /*
     * @exception IllegalArgumentException if the configuration is wrong
     */ 
    public void login() throws IllegalArgumentException, XMPPException {
        boolean ok = false;
        try {
            String[] parts = Contact.parseJid(settings.getUserJid());

            setConnectionStatus(CONNECTION_CONNECTING);
            con.connect(parts[1]);
            con.getCurrentConnection().addPacketListener(listener, new PacketTypeFilter(Message.class));

            setConnectionStatus(CONNECTION_LOGGING);
            con.getCurrentConnection().login(parts[0], settings.getPassword());
            presenceStatus = STATUS_ONLINE; // XXX -Smack did send presence for us
            
            setConnectionStatus(CONNECTION_DATAPHASE);
            cList.updateRoster();
            
            setConnectionStatus(CONNECTION_READY);
            ok = true;
        } finally {
            if (!ok) setConnectionStatus(CONNECTION_ERROR);
        }
    }
    
    public void logoff() {
        con.disconnect();
        cList.updateRoster();
        presenceStatus = 0;
        setConnectionStatus(CONNECTION_DISCONNECTED);
    }
    
    public Settings getSettings() {
        return settings;
    }
    
    void shutdown() {
        int al = settings.getAutologin();
        if ((al & 2) != 0) {
            settings.setAutologin(connectionStatus == CONNECTION_DISCONNECTED ? 2 : 3);
        }
    }

    private static Presence.Mode[] PRESENCES = new Presence.Mode[] {
        null, Presence.Mode.INVISIBLE, Presence.Mode.EXTENDED_AWAY,
        Presence.Mode.AWAY, Presence.Mode.DO_NOT_DISTURB,
        Presence.Mode.AVAILABLE, Presence.Mode.CHAT
    };
    
    public void addListener(Listener l) {
        listeners.add(l);
    }
    
    public void removeListener(Listener l) {
        listeners.remove(l);
    }
    

    
    public void setStatus(int status) {
        Presence packet = new Presence(Presence.Type.AVAILABLE, "", 0, PRESENCES[status]);
        con.sendPacket(packet);
        presenceStatus = status;
        fireStateChanged();
    }
    
    public int getStatus() {
        return presenceStatus;
    }
    
    public int getConnectionStatus() {
        return connectionStatus;
    }
    

    public Connection getConnection() {
        return con;
    }
    
    public MessageQueue getMessageQueue() {
        return messages;
    }
    
    public Contact.List getContactList() {
        return cList;
    }
    
    public Result sendPacketWithResult(Packet packet, Result.Callback callback) { // XXX move the impl to Connection
        
        class Helper implements PacketListener, Result.Callback {
            Result res;
            private Result.Callback callback;
            
            Helper(Packet packet, Result.Callback callback) {
                this.callback = callback;
                res = new Result(this);
                con.getCurrentConnection().addPacketListener(this, new PacketIDFilter(packet.getPacketID()));
                con.sendPacket(packet);
            }
            
            public void processPacket(Packet packet) {
               res.markFinished(packet);
            }
            
            public void resultFinished(Result result) {
                assert res == result;
                
                con.getCurrentConnection().removePacketListener(this);
                callback.resultFinished(result);
            }
        }
        
        return new Helper(packet, callback).res;
    }

    /** A listener for watching the connection state.
     *
     * Whenever a connection state changes, all registered listeners will be
     * notified. It is up to them to figure out what has happened and act
     * accordingly (update UI, dismiss connection dialog, ...)
     *
     * The event is fired for both connection state changes {@see getConnectionState}
     * and for presence changes {@see setStatus}
     *
     */
    public static interface Listener {
        public void stateChanged();
    }
    
    
    private void setConnectionStatus(int newStatus) {
        connectionStatus = newStatus;
        fireStateChanged();
    }
    
    private static Listener[] EMPTY = new Listener[0];
    
    private void fireStateChanged() {
        Listener[] all = (Listener[]) listeners.toArray(EMPTY);
        for (int i=0; i<all.length; i++) all[i].stateChanged();
    }
    
    private class Forwarder implements PacketListener {
        
        public void processPacket(org.jivesoftware.smack.packet.Packet packet) {
            // XXX - May need filtering according to type and ongoing chats            
            messages.put((Message)packet);
        }
        
    }
    
}
