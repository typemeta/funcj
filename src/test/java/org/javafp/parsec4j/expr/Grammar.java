package org.javafp.parsec4j.expr;

import org.javafp.parsec4j.*;
import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Parsers.*;
import static org.javafp.parsec4j.Text.*;
import static org.javafp.parsec4j.expr.Model.*;

public abstract class Grammar {

    static {
        // To get around circular references.
        final Ref<Character, Expr> expr = Ref.of();

        final Parser<Character, Character> open = chr('(');
        final Parser<Character, Character> close = chr(')');
        final Parser<Character, Character> comma = chr(',');

        final Parser<Character, UnaryOp> plus = chr('+').andR(pure(UnaryOp.POS));
        final Parser<Character, UnaryOp> minus = chr('-').andR(pure(UnaryOp.NEG));

        final Parser<Character, BinOp> add = chr('+').andR(pure(BinOp.ADD));
        final Parser<Character, BinOp> sub = chr('-').andR(pure(BinOp.SUBTRACT));
        final Parser<Character, BinOp> mult = chr('*').andR(pure(BinOp.MULTIPLY));
        final Parser<Character, BinOp> div = chr('/').andR(pure(BinOp.DIVIDE));

        final Parser<Character, NumExpr.Units> pct = string("%").andR(pure(NumExpr.Units.PCT));
        final Parser<Character, NumExpr.Units> bps = string("bp").andR(pure(NumExpr.Units.BPS));

        final Parser<Character, String> funcName =
            string("min").or(string("max"));

        // addSub = add | sub
        final Parser<Character, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Character, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // units = % | bp
        final Parser<Character, NumExpr.Units> units =
            pct.or(bps).or(pure(NumExpr.Units.ABS));

        // num = intr
        final Parser<Character, Expr> num = dble.and(units).map(Model::numExpr);

        // brackExpr = open expr close
        final Parser<Character, Expr> brackExpr =
            open.andR(expr).andL(close);

        final Parser<Character, Expr> var =
            alpha.map(Model::varExpr);

        // funcN = name { args | ε }
        final Parser<Character, Expr> funcN =
            funcName.andL(open).and(expr).andL(comma).and(expr).andL(close)
                .map(Model::func2Expr);

        // sign = + | -
        final Parser<Character, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
        final Parser<Character, Expr> signedExpr =
            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Character, Expr> term =
            num.or(brackExpr).or(funcN).or(var).or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Character, Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final Parser<Character, Expr> parser;
}
