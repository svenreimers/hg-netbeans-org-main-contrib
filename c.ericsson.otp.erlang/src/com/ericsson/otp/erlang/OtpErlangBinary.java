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
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Provides a Java representation of Erlang binaries. Anything that
 *  can be represented as a sequence of bytes can be made into an
 *  Erlang binary.
 **/
public class OtpErlangBinary extends OtpErlangObject implements Serializable, Cloneable {
  // don't change this!
  static final long serialVersionUID = -3781009633593609217L;

  // binary contents
  private byte[] bin;
  
  /**
   * Create a binary from a byte array
   *
   * @param bin the array of bytes from which to create the binary.
   **/
  public OtpErlangBinary(byte[] bin) {
    this.bin = new byte[bin.length];
    System.arraycopy(bin,0,this.bin,0,bin.length);
  }

  /**
   * Create a binary from a stream containinf a binary encoded in
   * Erlang external format.
   *
   * @param buf the stream containing the encoded binary.
   *
   * @exception OtpErlangDecodeException if the buffer does not
   * contain a valid external representation of an Erlang binary.
   **/
  public OtpErlangBinary(OtpInputStream buf) 
    throws OtpErlangDecodeException {
    this.bin = buf.read_binary();
  }

  /**
   * Create a binary from an arbitrary Java Object. The object must
   * implement java.io.Serializable or java.io.Externalizable.
   *
   * @param o the object to serialize and create this binary from.
   **/
  public OtpErlangBinary(Object o) {
    try {
      this.bin = toByteArray(o);
    }
    catch (IOException e) {
      throw new java.lang.IllegalArgumentException("Object must implement Serializable");
    }
  }

  private static byte[] toByteArray(Object o)
    throws java.io.IOException {

    if (o == null) return null;

    /* need to synchronize use of the shared baos */
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream();
    java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);

    oos.writeObject(o);
    oos.flush();
    
    return baos.toByteArray();
  }

  private static Object fromByteArray(byte[] buf) {
    if (buf == null) return null;

    try {
      java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
      java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
      return ois.readObject();
    }
    catch (java.lang.ClassNotFoundException e) {}
    catch (java.io.IOException e) {}

    return null;
  }

  /**
   * Get the byte array from a binary.
   * 
   * @return the byte array containing the bytes for this binary.
   **/
  public byte[] binaryValue() {
    return bin;
  }
  
  /**
   * Get the size of the binary.
   * 
   * @return the number of bytes contained in the binary.
   **/
  public int size() {
    return bin.length;
  }

  /**
   * Get the java Object from the binary. If the binary contains a
   * serialized Java object, then this method will recreate the
   * object.
   *
   * 
   * @return the java Object represented by this binary, or null if
   * the binary does not represent a Java Object.
   **/
  public Object getObject() {
    return fromByteArray(this.bin);
  }
  

  /**
   * Get the string representation of this binary object. A binary is
   * printed as #Bin&lt;N&gt;, where N is the number of bytes
   * contained in the object.
   *
   * @return the Erlang string representation of this binary.
   **/
  public String toString() {
    return "#Bin<" + bin.length + ">";
  }

  /**
   * Convert this binary to the equivalent Erlang external representation.
   *
   * @param buf an output stream to which the encoded binary should be
   * written.
   **/
  public void encode(OtpOutputStream buf) {
    buf.write_binary(this.bin);
  }
  
  /**
   * Determine if two binaries are equal. Binaries are equal if they have
   * the same length and the array of bytes is identical.
   *
   * @param o the binary to compare to.
   *
   * @return true if the byte arrays contain the same bytes, false
   * otherwise.
   **/
  public boolean equals(Object o) {
    if (!(o instanceof OtpErlangBinary)) return false;
    
    OtpErlangBinary bin = (OtpErlangBinary)o;
    int size = this.size();

    if (size != bin.size()) return false;

    for (int i=0; i<size; i++) {
      if (this.bin[i] != bin.bin[i]) return false; // early exit
    }

    return true;
  }
  
  public Object clone() {
    OtpErlangBinary newBin = (OtpErlangBinary)(super.clone());
    newBin.bin = (byte[])bin.clone();
    return newBin;
  }
}
