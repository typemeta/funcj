package org.typemeta.funcj.parser.expr;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.functions.Functions;

/**
 * A model for simple mathematical expressions.
 */
public class Model {

    public static Expr numExpr(double val) {
        return new NumExpr(val);
    }

    public static Expr varExpr(char name) {
        return new VarExpr(name);
    }

    public static Expr unaryOpExpr(UnaryOp op, Expr expr) {
        return new UnaryOpExpr(op, expr);
    }

    public static Expr binOpExpr(Expr lhs, BinOp op, Expr rhs) {
        return new BinOpExpr(lhs, op, rhs);
    }

    public static abstract class Expr {

        @Override
        public String toString() {
            return string(new StringBuilder()).toString();
        }

        public abstract StringBuilder string(StringBuilder sb);
    }

    public static class NumExpr extends Expr {
        public final double value;

        public NumExpr(double value) {
            this.value = value;
        }

        @Override
        public StringBuilder string(StringBuilder sb) {
            return sb.append(value);
        }
    }

    public static class VarExpr extends Expr {

        public final char name;

        public VarExpr(char name) {
            this.name = name;
        }

        @Override
        public StringBuilder string(StringBuilder sb) {
            return sb.append(name);
        }
    }

    public enum UnaryOp {
        POS('+'),
        NEG('-');

        final char code;

        UnaryOp(char code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return String.valueOf(code);
        }
    }

    public static class UnaryOpExpr extends Expr {

        public final UnaryOp op;
        public final Expr expr;

        public UnaryOpExpr(UnaryOp op, Expr expr) {
            this.op = op;
            this.expr = expr;
        }

        @Override
        public StringBuilder string(StringBuilder sb) {
            sb.append(op);
            sb.append('(');
            sb.append(expr);
            return sb.append(')');
        }
    }

    public enum BinOp {
        ADD('+'),
        SUBTRACT('-'),
        MULTIPLY('*'),
        DIVIDE('/');

        final char code;

        BinOp(char code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return String.valueOf(code);
        }

        public Functions.Op2<Expr> ctor() {
            return (lhs, rhs) -> new BinOpExpr(lhs, this, rhs);
        }
    }

    public static class BinOpExpr extends Expr {

        public final Expr lhs;
        public final BinOp op;
        public final Expr rhs;

        protected BinOpExpr(Expr lhs, BinOp op, Expr rhs) {
            this.lhs = lhs;
            this. op = op;
            this.rhs = rhs;
        }

        @Override
        public StringBuilder string(StringBuilder sb) {
            sb.append('(');
            lhs.string(sb);
            sb.append(op);
            rhs.string(sb);
            return sb.append(')');
        }
    }
}
