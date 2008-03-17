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
 * Class If, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class If extends DelimitedExpr {
    private final List<IfClause> _clauses;
    private final Option<Block> _elseClause;

    /**
     * Constructs a If.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public If(Span in_span, boolean in_parenthesized, List<IfClause> in_clauses, Option<Block> in_elseClause) {
        super(in_span, in_parenthesized);
        if (in_clauses == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'clauses' to the If constructor was null");
        }
        _clauses = in_clauses;
        if (in_elseClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'elseClause' to the If constructor was null");
        }
        _elseClause = in_elseClause;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(Span in_span, boolean in_parenthesized, List<IfClause> in_clauses) {
        this(in_span, in_parenthesized, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(Span in_span, List<IfClause> in_clauses, Option<Block> in_elseClause) {
        this(in_span, false, in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(Span in_span, List<IfClause> in_clauses) {
        this(in_span, false, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(boolean in_parenthesized, List<IfClause> in_clauses, Option<Block> in_elseClause) {
        this(new Span(), in_parenthesized, in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(boolean in_parenthesized, List<IfClause> in_clauses) {
        this(new Span(), in_parenthesized, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(List<IfClause> in_clauses, Option<Block> in_elseClause) {
        this(new Span(), false, in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public If(List<IfClause> in_clauses) {
        this(new Span(), false, in_clauses, Option.<Block>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected If() {
        _clauses = null;
        _elseClause = null;
    }

    final public List<IfClause> getClauses() { return _clauses; }
    final public Option<Block> getElseClause() { return _elseClause; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forIf(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forIf(this); }

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
        writer.print("If:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp_parenthesized = isParenthesized();
        writer.startLine();
        writer.print("parenthesized = ");
        writer.print(temp_parenthesized);

        List<IfClause> temp_clauses = getClauses();
        writer.startLine();
        writer.print("clauses = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_clauses = true;
        for (IfClause elt_temp_clauses : temp_clauses) {
            isempty_temp_clauses = false;
            writer.startLine("* ");
            if (elt_temp_clauses == null) {
                writer.print("null");
            } else {
                elt_temp_clauses.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_clauses) writer.print(" }");
        else writer.startLine("}");

        Option<Block> temp_elseClause = getElseClause();
        writer.startLine();
        writer.print("elseClause = ");
        if (temp_elseClause.isSome()) {
            writer.print("(");
            Block elt_temp_elseClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_elseClause);
            if (elt_temp_elseClause == null) {
                writer.print("null");
            } else {
                elt_temp_elseClause.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }
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
            If casted = (If) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<IfClause> temp_clauses = getClauses();
            List<IfClause> casted_clauses = casted.getClauses();
            if (!(temp_clauses == casted_clauses || temp_clauses.equals(casted_clauses))) return false;
            Option<Block> temp_elseClause = getElseClause();
            Option<Block> casted_elseClause = casted.getElseClause();
            if (!(temp_elseClause == casted_elseClause || temp_elseClause.equals(casted_elseClause))) return false;
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
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        List<IfClause> temp_clauses = getClauses();
        code ^= temp_clauses.hashCode();
        Option<Block> temp_elseClause = getElseClause();
        code ^= temp_elseClause.hashCode();
        return code;
    }
}
