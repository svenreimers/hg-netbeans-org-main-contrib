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

import java.net.Socket;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Provides methods for registering, unregistering and looking up
 * nodes with the Erlang portmapper daemon (Epmd). For each registered
 * node, Epmd maintains information about the port on which incoming
 * connections are accepted, as well as which versions of the Erlang
 * communication protocolt the node supports.
 *
 * <p> Nodes wishing to contact other nodes must first request
 * information from Epmd before a connection can be set up, however
 * this is done automatically by {@link OtpSelf#connect(OtpPeer)
 * OtpSelf.connect()} when necessary.
 *
 * <p> The methods {@link #publishPort(OtpLocalNode) publishPort()} and
 * {@link #unPublishPort(OtpLocalNode) unPublishPort()} will fail if an
 * Epmd process is not running on the localhost. Additionally {@link
 * #lookupPort(AbstractNode) lookupPort()} will fail if there is no Epmd
 * process running on the host where the specified node is running.
 * See the Erlang documentation for information about starting Epmd.
 *
 * <p> This class contains only static methods, there are no
 * constructors.
 **/
public class OtpEpmd {
  // common values
  private static final int epmdPort = 4369;
  private static final byte stopReq =      (byte) 115;

  // version specific value
  private static final byte port3req =     (byte) 112;
  private static final byte publish3req =  (byte)  97;
  private static final byte publish3ok  =  (byte)  89;
  
  private static final byte port4req =     (byte) 122;
  private static final byte port4resp =    (byte) 119;
  private static final byte publish4req =  (byte) 120;
  private static final byte publish4resp = (byte) 121;

  private static int traceLevel = 0;
  private static final int traceThreshold = 4;
  
  static {
    // debug this connection?
    String trace = System.getProperties().getProperty("OtpConnection.trace");
    try {
      if (trace != null) traceLevel = Integer.valueOf(trace).intValue();
    }
    catch (NumberFormatException e) {
      traceLevel = 0;
    }
  }

  // only static methods: no public constructors
  // hmm, idea: singleton constructor could spawn epmd process
  private OtpEpmd() {
  }
  
  /**
   * Determine what port a node listens for incoming connections on.
   *
   * @return the listen port for the specified node, or 0 if the node
   * was not registered with Epmd.
   *
   * @exception java.io.IOException if there was no response from the
   * name server.
   **/
  public static int lookupPort(AbstractNode node)
    throws IOException {
    try {
      return r4_lookupPort(node);
    }
    catch (IOException e) {
      return r3_lookupPort(node);
    }
  }

  /**
   * Register with Epmd, so that other nodes are able to find and
   * connect to it. 
   *
   * @param node the server node that should be registered with Epmd.
   *
   * @return true if the operation was successful. False if the node
   * was already registered.
   *
   * @exception java.io.IOException if there was no response from the
   * name server.
   **/
  public static boolean publishPort(OtpLocalNode node)
    throws IOException {
    Socket s = null;
    
    try {
      s = r4_publish(node);
    }
    catch (IOException e) {
      s = r3_publish(node);
    }

    node.setEpmd(s);

    return (s != null);
  }

  // Ask epmd to close his end of the connection.
  // Caller should close his epmd socket as well.
  // This method is pretty forgiving...
  /**
   * Unregister from Epmd. Other nodes wishing to connect will no
   * longer be able to.
   *
   * <p> This method does not report any failures.
   **/
  public static void unPublishPort(OtpLocalNode node) {
    Socket s = null;

    try {
      s = new Socket(InetAddress.getLocalHost(),epmdPort);
      OtpOutputStream obuf = new OtpOutputStream();
      obuf.write2BE(node.alive().length() + 1);
      obuf.write1(stopReq);
      obuf.writeN(node.alive().getBytes());
      obuf.writeTo(s.getOutputStream());
      // don't even wait for a response (is there one?) 
      if (traceLevel >= traceThreshold) {
	System.out.println("-> UNPUBLISH " + node + " port=" + node.port());
	System.out.println("<- OK (assumed)");
      }
    }
    catch (Exception e) {/* ignore all failures */}
    finally {
      try {
	if (s != null) s.close();
      }
      catch (IOException e) { /* ignore close failure */}
      s = null;
    }
  }    

  private static int r3_lookupPort(AbstractNode node) 
    throws IOException {
    int port = 0;
    Socket s = null;

    try {
      OtpOutputStream obuf = new OtpOutputStream();
      s = new Socket(node.host(),epmdPort);
      
      // build and send epmd request
      // length[2], tag[1], alivename[n] (length = n+1)
      obuf.write2BE(node.alive().length() + 1);
      obuf.write1(port3req);
      obuf.writeN(node.alive().getBytes());

      // send request
      obuf.writeTo(s.getOutputStream());

      if (traceLevel >= traceThreshold) System.out.println("-> LOOKUP (r3) " + node);

      // receive and decode reply
      byte[] tmpbuf = new byte[100];

      s.getInputStream().read(tmpbuf);
      OtpInputStream ibuf = new OtpInputStream(tmpbuf);

      port = ibuf.read2BE();
    }
    catch (IOException e) {
      if (traceLevel >= traceThreshold) System.out.println("<- (no response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when looking up " + node.alive());
    }
    catch (OtpErlangDecodeException e) {
      if (traceLevel >= traceThreshold) System.out.println("<- (invalid response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when looking up " + node.alive());
    }
    finally {
      try { 
	if (s != null) s.close();
      }
      catch (IOException e) { /* ignore close errors */}
      s = null;
    }
    
    if (traceLevel >= traceThreshold) {
      if (port == 0)  System.out.println("<- NOT FOUND");
      else System.out.println("<- PORT " + port);
    }
    return port;
  }

  private static int r4_lookupPort(AbstractNode node) 
    throws IOException {
    int port = 0;
    Socket s = null;

    try {
      OtpOutputStream obuf = new OtpOutputStream();
      s = new Socket(node.host(),epmdPort);
      
      // build and send epmd request
      // length[2], tag[1], alivename[n] (length = n+1)
      obuf.write2BE(node.alive().length() + 1);
      obuf.write1(port4req);
      obuf.writeN(node.alive().getBytes());

      // send request
      obuf.writeTo(s.getOutputStream());

      if (traceLevel >= traceThreshold) System.out.println("-> LOOKUP (r4) " + node);

      // receive and decode reply
      // resptag[1], result[1], port[2], ntype[1], proto[1],
      // disthigh[2], distlow[2], nlen[2], alivename[n],
      // elen[2], edata[m]
      byte[] tmpbuf = new byte[100];

      int n = s.getInputStream().read(tmpbuf);

      if (n < 0) {
	// this was an r3 node => not a failure (yet)
	if (s != null) s.close();
	throw new IOException("Nameserver not responding on " + node.host() + " when looking up " + node.alive());
      }
      
      OtpInputStream ibuf = new OtpInputStream(tmpbuf);

      int response = ibuf.read1();
      if (response == port4resp) {
	int result = ibuf.read1();
	if (result == 0) {
	  port = ibuf.read2BE();

	  node.ntype = ibuf.read1(); 
	  node.proto = ibuf.read1();
	  node.distHigh = ibuf.read2BE();
	  node.distLow = ibuf.read2BE();
	  // ignore rest of fields
	}
      }
    }
    catch (IOException e) {
      if (traceLevel >= traceThreshold) System.out.println("<- (no response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when looking up " + node.alive());
    }
    catch (OtpErlangDecodeException e) {
      if (traceLevel >= traceThreshold) System.out.println("<- (invalid response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when looking up " + node.alive());
    }
    finally {
      try {
	if (s != null) s.close();
      }
      catch (IOException e) { /* ignore close errors */}
      s = null;
    }
    
    if (traceLevel >= traceThreshold) {
      if (port == 0)  System.out.println("<- NOT FOUND");
      else System.out.println("<- PORT " + port);
    }
    return port;
  }

  private static Socket r3_publish(OtpLocalNode node)
    throws IOException {
    Socket s = null;

    try {
      OtpOutputStream obuf = new OtpOutputStream();
      s = new Socket(InetAddress.getLocalHost(),epmdPort);
    
      obuf.write2BE(node.alive().length() + 3);
    
      obuf.write1(publish3req);
      obuf.write2BE(node.port());
      obuf.writeN(node.alive().getBytes());

      // send request
      obuf.writeTo(s.getOutputStream());
      if (traceLevel >= traceThreshold) System.out.println("-> PUBLISH (r3) " +
							   node + " port=" + node.port());

      byte[] tmpbuf = new byte[100];

      int n = s.getInputStream().read(tmpbuf);

      if (n < 0) {
	if (s != null) s.close();
	if (traceLevel >= traceThreshold) System.out.println("<- (no response)");
	return null; 
      }
    
      OtpInputStream ibuf = new OtpInputStream(tmpbuf);
    
      if (ibuf.read1() == publish3ok) {
	node.creation = ibuf.read2BE();
	if (traceLevel >= traceThreshold) System.out.println("<- OK");
	return s; // success - don't close socket
      }
    }
    catch (IOException e) {
      // epmd closed the connection = fail
      if (s != null) s.close();
      if (traceLevel >= traceThreshold) System.out.println("<- (no response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when publishing " + node.alive());
    }
    catch (OtpErlangDecodeException e) {
      if (s != null) s.close();
      if (traceLevel >= traceThreshold) System.out.println("<- (invalid response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when publishing " + node.alive());
    }

    if (s != null) s.close();
    return null; // failure
  }

  /* this function will get an exception if it tries to talk to an r3
   * epmd, or if something else happens that it cannot forsee. In both
   * cases we return an exception (and the caller should try again, using
   * the r3 protocol).
   * If we manage to successfully communicate with an r4 epmd, we return
   * either the socket, or null, depending on the result.
   */
  private static Socket r4_publish(OtpLocalNode node)
    throws IOException {
    Socket s = null;

    try {
      OtpOutputStream obuf = new OtpOutputStream();
      s = new Socket(InetAddress.getLocalHost(),epmdPort);
    
      obuf.write2BE(node.alive().length() + 13);
    
      obuf.write1(publish4req);
      obuf.write2BE(node.port());
    
      obuf.write1(node.type());
    
      obuf.write1(node.proto());
      obuf.write2BE(node.distHigh()); 
      obuf.write2BE(node.distLow());
    
      obuf.write2BE(node.alive().length());
      obuf.writeN(node.alive().getBytes());
      obuf.write2BE(0); // No extra

      // send request
      obuf.writeTo(s.getOutputStream());

      if (traceLevel >= traceThreshold) System.out.println("-> PUBLISH (r4) " +
							   node + " port=" + node.port());

      // get reply
      byte[] tmpbuf = new byte[100];
      int n = s.getInputStream().read(tmpbuf);

      if (n < 0) {
	// this was an r3 node => not a failure (yet)
	if (s != null) s.close();
	throw new IOException("Nameserver not responding on " + node.host() + " when publishing " + node.alive());
      }
      
      OtpInputStream ibuf = new OtpInputStream(tmpbuf);

      int response = ibuf.read1();
      if (response == publish4resp) {
	int result = ibuf.read1();
	if (result == 0) {
	  node.creation = ibuf.read2BE();
	  if (traceLevel >= traceThreshold) System.out.println("<- OK");
	  return s; // success
	}
      }
    }
    catch (IOException e) {
      // epmd closed the connection = fail
      if (s != null) s.close();
      if (traceLevel >= traceThreshold) System.out.println("<- (no response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when publishing " + node.alive());
    }
    catch (OtpErlangDecodeException e) {
      if (s != null) s.close();
      if (traceLevel >= traceThreshold) System.out.println("<- (invalid response)");
      throw new IOException("Nameserver not responding on " + node.host() + " when publishing " + node.alive());
    }

    if (s != null) s.close();
    return null; 
  }

}
