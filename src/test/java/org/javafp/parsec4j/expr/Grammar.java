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

        final P<Character, Character> open = chr('(');
        final P<Character, Character> close = chr(')');
        final P<Character, Character> comma = chr(',');

        final P<Character, UnaryOp> plus = chr('+').andR(pure(UnaryOp.POS));
        final P<Character, UnaryOp> minus = chr('-').andR(pure(UnaryOp.NEG));

        final P<Character, BinOp> add = chr('+').andR(pure(BinOp.ADD));
        final P<Character, BinOp> sub = chr('-').andR(pure(BinOp.SUBTRACT));
        final P<Character, BinOp> mult = chr('*').andR(pure(BinOp.MULTIPLY));
        final P<Character, BinOp> div = chr('/').andR(pure(BinOp.DIVIDE));

        final P<Character, NumExpr.Units> pct = string("%").andR(pure(NumExpr.Units.PCT));
        final P<Character, NumExpr.Units> bps = string("bp").andR(pure(NumExpr.Units.BPS));

        final P<Character, String> funcName =
            string("min").or(string("max"));

        // addSub = add | sub
        final P<Character, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final P<Character, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // units = % | bp
        final P<Character, NumExpr.Units> units =
            pct.or(bps).or(pure(NumExpr.Units.ABS));

        // num = intr
        final P<Character, Expr> num = dble.and(units).map(Model::numExpr);

        // brackExpr = open expr close
        final P<Character, Expr> brackExpr =
            open.andR(expr).andL(close);

        final P<Character, Expr> var =
            alpha.map(Model::varExpr);

        // funcN = name { args | ε }
        final P<Character, Expr> funcN =
            funcName.andL(open).and(expr).andL(comma).and(expr).andL(close)
                .map(Model::func2Expr);

        // sign = + | -
        final P<Character, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
        final P<Character, Expr> signedExpr =
            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final P<Character, Expr> term =
            num.or(brackExpr).or(funcN).or(var).or(signedExpr);

        // prod = term chainl1 multDiv
        final P<Character, Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final P<Character, Expr> parser;
}
