
/* ``The contents of this file are subject to the Erlang Public License,
 * Version 1.1, (the "License"); you may not use this file except in
 * compliance with the License. You should have received a copy of the
 * Erlang Public License along with this software. If not, it can be
 * retrieved via the world wide web at http://www.erlang.org/.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Initial Developer of the Original Code is Ericsson Utvecklings AB.
 * Portions created by Ericsson are Copyright 1999, Ericsson Utvecklings
 * AB. All Rights Reserved.''
 * 
 *     $Id$
 */
package com.ericsson.otp.erlang;

import java.io.IOException;
import java.net.Socket;

/**
 * <p> Maintains a connection between a Java process and a remote
 * Erlang, Java or C node. The object maintains connection state and
 * allows data to be sent to and received from the peer. </p>
 *
 * <p> Once a connection is established between the local node and a
 * remote node, the connection object can be used to send and receive
 * messages between the nodes. </p>
 *
 * <p> The various receive methods are all blocking and will return
 * only when a valid message has been received or an exception is
 * raised. </p>
 *
 * <p> If an exception occurs in any of the methods in this class, the
 * connection will be closed and must be reopened in order to resume
 * communication with the peer. </p>
 *
 * <p> The message delivery methods in this class deliver directly to
 * {@link OtpMbox mailboxes} in the {@link OtpNode OtpNode} class.
 * </p>
 * 
 * <p> It is not possible to create an instance of this class
 * directly. OtpCookedConnection objects are created as needed by the
 * underlying mailbox mechanism. </p>
 **/
public class OtpCookedConnection extends AbstractConnection {
  protected OtpNode self;

  /* The connection needs to know which local pids have links that
   * pass through here, so that they can be notified in case of
   * connection failure
   */
  protected Links links = null;

  /*
   * Accept an incoming connection from a remote node. Used by {@link
   * OtpSelf#accept() OtpSelf.accept()} to create a connection
   * based on data received when handshaking with the peer node, when
   * the remote node is the connection intitiator.
   *
   * @exception java.io.IOException if it was not possible to connect to the peer.
   *
   * @exception OtpAuthException if handshake resulted in an authentication error
   */
  // package scope
  OtpCookedConnection(OtpNode self, Socket s)
    throws IOException, OtpAuthException {
    super(self,s);
    this.self = self;
    this.links = new Links(25);
    this.start();
  }

  /*
   * Intiate and open a connection to a remote node.
   *
   * @exception java.io.IOException if it was not possible to connect to the peer.
   *
   * @exception OtpAuthException if handshake resulted in an authentication error.
   */
  // package scope
  OtpCookedConnection(OtpNode self, OtpPeer other)
    throws IOException , OtpAuthException {
    super(self,other);
    this.self = self;
    this.links = new Links(25);
    this.start();
  }

  // pass the error to the node
  public void deliver(Exception e) {
    self.deliverError(this,e);
    return;
  }

  /*
   * pass the message to the node for final delivery. Note that the
   * connection itself needs to know about links (in case of connection
   * failure), so we snoop for link/unlink too here.
   */
  public void deliver(OtpMsg msg) {
    boolean delivered = self.deliver(msg);
    
    switch (msg.type()) {
    case OtpMsg.linkTag:
      if (delivered) {
	links.addLink(msg.getRecipientPid(),msg.getSenderPid());
      }
      else try {
	// no such pid - send exit to sender
	super.sendExit(msg.getRecipientPid(),msg.getSenderPid(),
		       new OtpErlangAtom("noproc"));
      }
      catch (IOException e) {}
      break;

    case OtpMsg.unlinkTag:
    case OtpMsg.exitTag:
      links.removeLink(msg.getRecipientPid(),msg.getSenderPid());
      break;

    case OtpMsg.exit2Tag:
      break;
    }
    
    return;
  }


  /*
   * send to pid
   */
  void send(OtpErlangPid from, OtpErlangPid dest, OtpErlangObject msg)
    throws IOException {
    // encode and send the message
    sendBuf(from,dest,new OtpOutputStream(msg));
  }

  /*
   * send to remote name
   * dest is recipient's registered name, the nodename is implied by
   * the choice of connection.
   */
  void send(OtpErlangPid from, String dest, OtpErlangObject msg)
    throws IOException {
    // encode and send the message
    sendBuf(from,dest,new OtpOutputStream(msg));
  }

  public void close() {
    super.close();
    breakLinks();
  }

  protected void finalize() {
    this.close();
  }

  /*
   * this one called by dying/killed process
   */
  void exit(OtpErlangPid from, OtpErlangPid to, OtpErlangObject reason) {
    try {
      super.sendExit(from,to,reason);
    } catch (Exception e) {}
  }

  /*
   * this one called explicitely by user code => use exit2
   */
  void exit2(OtpErlangPid from, OtpErlangPid to, OtpErlangObject reason) {
    try {
      super.sendExit2(from,to,reason);
    } catch (Exception e) {}
  }

  /*
   * snoop for outgoing links and update own table
   */
  synchronized void link(OtpErlangPid from, OtpErlangPid to)
    throws OtpErlangExit {
    try {
      super.sendLink(from,to);
      links.addLink(from,to);
    }
    catch (IOException e) {
      throw new OtpErlangExit("noproc",to);
    }
  }

  /*
   * snoop for outgoing unlinks and update own table
   */
  synchronized void unlink(OtpErlangPid from, OtpErlangPid to) {
    links.removeLink(from,to);
    try {
      super.sendUnlink(from,to);
    }
    catch (IOException e) { }
  }

  /* When the connection fails - send exit to all local
   * pids with links through this connection
   */
  synchronized void breakLinks() {
    if (links != null) {
      Link[] l = links.clearLinks();

      if (l != null) {
	int len = l.length;

	for (int i=0; i<len; i++) {
	  // send exit "from" remote pids to local ones
	  self.deliver(new OtpMsg(OtpMsg.exitTag,l[i].remote(),l[i].local(),
				  new OtpErlangAtom("noconnection")));
	}
      }
    }
  }
}
