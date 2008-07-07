package com.sun.fortress.nodes;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.fortress.nodes_util.*;
import com.sun.fortress.parser_util.*;
import com.sun.fortress.parser_util.precedence_opexpr.*;
import com.sun.fortress.useful.*;
import edu.rice.cs.plt.tuple.Option;

/**
 * Class UnitParam, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class UnitParam extends IdStaticParam {
    private final Option<Type> _dim;
    private final boolean _absorbs;

    /**
     * Constructs a UnitParam.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public UnitParam(Span in_span, Id in_name, Option<Type> in_dim, boolean in_absorbs) {
        super(in_span, in_name);
        if (in_dim == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'dim' to the UnitParam constructor was null");
        }
        _dim = in_dim;
        _absorbs = in_absorbs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Span in_span, Id in_name, Option<Type> in_dim) {
        this(in_span, in_name, in_dim, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Span in_span, Id in_name, boolean in_absorbs) {
        this(in_span, in_name, Option.<Type>none(), in_absorbs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Span in_span, Id in_name) {
        this(in_span, in_name, Option.<Type>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Id in_name, Option<Type> in_dim, boolean in_absorbs) {
        this(new Span(), in_name, in_dim, in_absorbs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Id in_name, Option<Type> in_dim) {
        this(new Span(), in_name, in_dim, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Id in_name, boolean in_absorbs) {
        this(new Span(), in_name, Option.<Type>none(), in_absorbs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitParam(Id in_name) {
        this(new Span(), in_name, Option.<Type>none(), false);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected UnitParam() {
        _dim = null;
        _absorbs = false;
    }

    final public Option<Type> getDim() { return _dim; }
    final public boolean isAbsorbs() { return _absorbs; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forUnitParam(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forUnitParam(this); }

    /** Generate a human-readable representation that can be deserialized. */
    public java.lang.String serialize() {
        java.io.StringWriter w = new java.io.StringWriter();
        serialize(w);
        return w.toString();
    }
    /** Generate a human-readable representation that can be deserialized. */
    public void serialize(java.io.Writer writer) {
        outputHelp(new TabPrintWriter(writer, 2), true);
    }

    public void outputHelp(TabPrintWriter writer, boolean lossless) {
        writer.print("UnitParam:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Id temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        Option<Type> temp_dim = getDim();
        writer.startLine();
        writer.print("dim = ");
        if (temp_dim.isSome()) {
            writer.print("(");
            Type elt_temp_dim = edu.rice.cs.plt.tuple.Option.unwrap(temp_dim);
            if (elt_temp_dim == null) {
                writer.print("null");
            } else {
                elt_temp_dim.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        boolean temp_absorbs = isAbsorbs();
        writer.startLine();
        writer.print("absorbs = ");
        writer.print(temp_absorbs);
        writer.unindent();
    }

    /**
     * Implementation of equals that is based on the values of the fields of the
     * object. Thus, two objects created with identical parameters will be equal.
     */
    public boolean equals(java.lang.Object obj) {
        if (obj == null) return false;
        if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
            return false;
        } else {
            UnitParam casted = (UnitParam) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Option<Type> temp_dim = getDim();
            Option<Type> casted_dim = casted.getDim();
            if (!(temp_dim == casted_dim || temp_dim.equals(casted_dim))) return false;
            boolean temp_absorbs = isAbsorbs();
            boolean casted_absorbs = casted.isAbsorbs();
            if (!(temp_absorbs == casted_absorbs)) return false;
            return true;
        }
    }

    /**
     * Implementation of hashCode that is consistent with equals.  The value of
     * the hashCode is formed by XORing the hashcode of the class object with
     * the hashcodes of all the fields of the object.
     */
    public int generateHashCode() {
        int code = getClass().hashCode();
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        Option<Type> temp_dim = getDim();
        code ^= temp_dim.hashCode();
        boolean temp_absorbs = isAbsorbs();
        code ^= temp_absorbs ? 1231 : 1237;
        return code;
    }
}