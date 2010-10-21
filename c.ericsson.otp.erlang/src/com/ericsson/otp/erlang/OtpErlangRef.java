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
 * Provides a Java representation of Erlang refs. There are two
 * styles of Erlang refs, old style (one id value) and new style
 * (array of id values). This class manages both types.
 **/
public class OtpErlangRef extends OtpErlangObject implements Serializable, Cloneable {
  // don't change this!
  static final long serialVersionUID = -7022666480768586521L;

  private String node;
  private int creation;

  // old style refs have one 18-bit id
  // r6 "new" refs have array of ids, first one is only 18 bits however
  private int ids[] = null;
  
  /**
   * Create a unique Erlang ref belonging to the local node. 
   *
   * @param self the local node.
   *
   @deprecated use OtpLocalNode:createRef() instead
   **/
  public OtpErlangRef(OtpLocalNode self) {
    OtpErlangRef r = self.createRef();
    
    this.ids = r.ids;
    this.creation = r.creation;
    this.node = r.node;
  }
  
  /**
   * Create an Erlang ref from a stream containing a ref encoded in
   * Erlang external format.
   *
   * @param buf the stream containing the encoded ref.
   * 
   * @exception OtpErlangDecodeException if the buffer does not
   * contain a valid external representation of an Erlang ref.
   **/
  public OtpErlangRef(OtpInputStream buf) 
    throws OtpErlangDecodeException {
    OtpErlangRef r = buf.read_ref();

    this.node = r.node();
    this.creation = r.creation();
    
    this.ids = r.ids();
  }

  /**
   * Create an old style Erlang ref from its components.
   *
   * @param node the nodename.
   *
   * @param id an arbitrary number. Only the low order 18 bits will
   * be used.
   *
   * @param creation another arbitrary number. Only the low order
   * 2 bits will be used.
   **/
  public OtpErlangRef(String node, int id, int creation) {
    this.node = node;
    this.ids = new int[1];
    this.ids[0] = id & 0x3ffff; // 18 bits
    this.creation = creation & 0x03 ; // 2 bits
  }

  /**
   * Create a new style Erlang ref from its components.
   *
   * @param node the nodename.
   *
   * @param ids an array of arbitrary numbers. Only the low order 18
   * bits of the first number will be used. If the array contains only
   * one number, an old style ref will be written instead. At most
   * three numbers will be read from the array.
   *
   * @param creation another arbitrary number. Only the low order
   * 2 bits will be used.
   **/
  public OtpErlangRef(String node, int[] ids, int creation) {
    this.node = node;
    this.creation = creation & 0x03 ; // 2 bits

    // use at most 82 bits (18 + 32 + 32)
    int len = ids.length;
    this.ids = new int[3];
    this.ids[0] = 0; 
    this.ids[1] = 0;
    this.ids[2] = 0;

    if (len > 3) len = 3; 
    System.arraycopy(ids,0,this.ids,0,len);
    this.ids[0] &= 0x3ffff; // only 18 significant bits in first number
  }

  /**
   * Get the id number from the ref. Old style refs have only one id
   * number. If this is a new style ref, the first id number is returned.
   *
   * @return the id number from the ref.
   **/
  public int id() {
    return ids[0];
  }

  /**
   * Get the array of id numbers from the ref. If this is an old style
   * ref, the array is of length 1. If this is a new style ref, the
   * array has length 3.
   *
   * @return the array of id numbers from the ref.
   **/
  public int[] ids() {
    return ids;
  }

  /**
   * Determine whether this is a new style ref.
   *
   * @return true if this ref is a new style ref, false otherwise.
   **/
  public boolean isNewRef() {
    return (ids.length > 1);
  }
  
  /**
   * Get the creation number from the ref.
   *
   *  @return the creation number from the ref.
   **/
  public int creation() {
    return creation;
  }

  /**
   * Get the node name from the ref.
   *
   * @return the node name from the ref.
   **/
  public String node() {
    return node;
  }

  /**
   * Get the string representation of the ref. Erlang refs are printed
   * as #Ref&lt;node.id&gt;
   *
   * @return the string representation of the ref.
   **/
  public String toString() {
    String s = "#Ref<" + node;

    for (int i=0; i<ids.length; i++) {
      s += "." + ids[i];
    }

    s += ">";
    
    return s;
  }

  /**
   * Convert this ref to the equivalent Erlang external representation.
   *
   * @param buf an output stream to which the encoded ref should be
   * written.
   **/
  public void encode(OtpOutputStream buf) {
    buf.write_ref(node,ids,creation);
  }

  /**
   * Determine if two refs are equal. Refs are equal if their
   * components are equal. New refs and old refs are considered equal
   * if the node, creation and first id numnber are equal.
   *
   * @param o the other ref to compare to.
   *
   * @return true if the refs are equal, false otherwise.
   **/
  public boolean equals(Object o) {
    if (!(o instanceof OtpErlangRef)) return false;


    OtpErlangRef ref = (OtpErlangRef)o;

    if (! (this.node.equals(ref.node()) &&
	   this.creation == ref.creation())) return false;
    
    if (this.isNewRef() && ref.isNewRef()) {
      return (this.ids[0] == ref.ids[0] &&
	      this.ids[1] == ref.ids[1] &&
	      this.ids[2] == ref.ids[2]);
    }
    return (this.ids[0] == ref.ids[0]);
  }

  public Object clone() {
    OtpErlangRef newRef = (OtpErlangRef)(super.clone());
    newRef.ids = (int[])ids.clone();
    return newRef;
  }
}
