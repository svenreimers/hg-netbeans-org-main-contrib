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

import java.io.Serializable;

/**
 * Provides a Java representation of Erlang PIDs. PIDs represent
 * Erlang processes and consist of a nodename and a number of
 * integers.
 **/
public class OtpErlangPid extends OtpErlangObject implements Serializable, Cloneable {
  // don't change this!
  static final long serialVersionUID = 1664394142301803659L;
  
  private String node;
  private int id;
  private int serial;
  private int creation;

  /**
   * Create a unique Erlang PID belonging to the local node.
   *
   * @param self the local node.
   *
   @deprecated use OtpLocalNode:createPid() instead
   **/
  public OtpErlangPid(OtpLocalNode self) {
    OtpErlangPid p = self.createPid();
    
    this.id = p.id;
    this.serial=p.serial;
    this.creation = p.creation;
    this.node = p.node;
  }
  
  /**
   * Create an Erlang PID from a stream containing a PID encoded in
   * Erlang external format.
   *
   * @param buf the stream containing the encoded PID.
   * 
   * @exception OtpErlangDecodeException if the buffer does not
   * contain a valid external representation of an Erlang PID.
   **/
  public OtpErlangPid(OtpInputStream buf) 
    throws OtpErlangDecodeException {
    OtpErlangPid p = buf.read_pid();

    this.node = p.node();
    this.id = p.id();
    this.serial = p.serial();
    this.creation = p.creation();
  }

  /**
   * Create an Erlang pid from its components.
   *
   * @param node the nodename.
   *
   * @param id an arbitrary number. Only the low order 15 bits will
   * be used.
   *
   * @param serial another arbitrary number. Only the low order 13 bits
   * will be used.
   *
   * @param creation yet another arbitrary number. Only the low order
   * 2 bits will be used.
   **/
  public OtpErlangPid(String node, int id, int serial, int creation) {
    this.node = node;
    this.id = id & 0x7fff; // 15 bits
    this.serial = serial & 0x1fff ;  // 13 bits
    this.creation = creation & 0x03 ; // 2 bits
  }
  
  /**
   * Get the serial number from the PID.
   *
   * @return the serial number from the PID.
   **/
  public int serial() {
    return serial;
  }

  /**
   * Get the id number from the PID.
   *
   * @return the id number from the PID.
   **/
  public int id() {
    return id;
  }

  /**
   * Get the creation number from the PID.
   *
   * @return the creation number from the PID.
   **/
  public int creation() {
    return creation;
  }

  /**
   * Get the node name from the PID.
   *
   * @return the node name from the PID.
   **/
  public String node() {
    return node;
  }
  
  /**
   * Get the string representation of the PID. Erlang PIDs are printed
   * as #Pid&lt;node.id.serial&gt;
   *
   * @return the string representation of the PID.
   **/
  public String toString() {
    return "#Pid<" + node.toString() + "." + id + "." + serial + ">";
  }

  /**
   * Convert this PID to the equivalent Erlang external representation.
   *
   * @param buf an output stream to which the encoded PID should be
   * written.
   **/
  public void encode(OtpOutputStream buf) {
    buf.write_pid(node,id,serial,creation);
  }
  
  /**
   * Return the hashCode for this Pid.
   *
   * @return the hashCode for this Pid.
   **/
  public int hashCode() {
    return id;
  }
  
  /**
   * Determine if two PIDs are equal. PIDs are equal if their
   * components are equal.
   *
   * @param port the other PID to compare to.
   *
   * @return true if the PIDs are equal, false otherwise.
   **/
  public boolean equals(Object o) {
    if (!(o instanceof OtpErlangPid)) return false;
    
    OtpErlangPid pid = (OtpErlangPid)o;
      
    return ((this.creation == pid.creation) &&
	    (this.serial == pid.serial) &&
	    (this.id == pid.id) &&
	    (node.compareTo(pid.node) == 0));
  }
}
