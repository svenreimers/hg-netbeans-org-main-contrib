/*******************************************************************************
    Copyright 2007 Sun Microsystems, Inc.,
    4150 Network Circle, Santa Clara, California 95054, U.S.A.
    All rights reserved.

    U.S. Government Rights - Commercial software.
    Government users are subject to the Sun Microsystems, Inc. standard
    license agreement and applicable provisions of the FAR and its supplements.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.

    Sun, Sun Microsystems, the Sun logo and Java are trademarks or registered
    trademarks of Sun Microsystems, Inc. in the U.S. and other countries.
 ******************************************************************************/

package com.sun.fortress.parser_util.precedence_opexpr;

import com.sun.fortress.nodes.Op;

/**
 * Class Postfix, a component of the OpExpr composite hierarchy.
 * Note: null is not allowed as a value for any field.
 */
public class Postfix extends Object implements PostfixOpExpr {
   private final Op _op;
   private int _hashCode;
   private boolean _hasHashCode = false;

   /**
    * Constructs a Postfix.
    * @throws java.lang.IllegalArgumentException if any parameter to the constructor is null.
    */
   public Postfix(Op in_op) {
      super();

      if (in_op == null) {
         throw new java.lang.IllegalArgumentException("Parameter 'op' to the Postfix constructor was null. This class may not have null field values.");
      }
      _op = in_op;
   }

   final public Op getOp() { return _op; }

   public <RetType> RetType accept(OpExprVisitor<RetType> visitor) { return visitor.forPostfix(this); }
   public void accept(OpExprVisitor_void visitor) { visitor.forPostfix(this); }

   /**
    * Implementation of toString that uses
    * {@link #output} to generated nicely tabbed tree.
    */
   public java.lang.String toString() {
      java.io.StringWriter w = new java.io.StringWriter();
      output(w);
      return w.toString();
   }

   /**
    * Prints this object out as a nicely tabbed tree.
    */
   public void output(java.io.Writer writer) {
      outputHelp(new TabPrintWriter(writer, 2));
   }

   public void outputHelp(TabPrintWriter writer) {
      writer.print("Postfix" + ":");
      writer.indent();

      writer.print(" ");
      writer.print("op = ");
      Op temp_op = getOp();
      if (temp_op == null) {
         writer.print("null");
      } else {
         writer.print(temp_op.getText());
      }
      writer.unindent();
   }

   /**
    * Implementation of equals that is based on the values
    * of the fields of the object. Thus, two objects
    * created with identical parameters will be equal.
    */
   public boolean equals(java.lang.Object obj) {
      if (obj == null) return false;
      if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
         return false;
      } else {
         Postfix casted = (Postfix) obj;
         if (! (getOp().equals(casted.getOp()))) return false;
         return true;
      }
   }

   /**
    * Implementation of hashCode that is consistent with
    * equals. The value of the hashCode is formed by
    * XORing the hashcode of the class object with
    * the hashcodes of all the fields of the object.
    */
   protected int generateHashCode() {
      int code = getClass().hashCode();
      code ^= getOp().hashCode();
      return code;
   }
   public final int hashCode() {
      if (! _hasHashCode) { _hashCode = generateHashCode(); _hasHashCode = true; }
      return _hashCode;
   }
}
