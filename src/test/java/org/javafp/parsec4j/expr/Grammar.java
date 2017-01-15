package org.javafp.parsec4j.expr;

import org.javafp.parsec4j.*;
import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Parser.pure;
import static org.javafp.parsec4j.Text.*;
import static org.javafp.parsec4j.expr.Model.*;

public abstract class Grammar {

    public static class Ctx extends Parser.CtxImpl<Character> {
        public Ctx(Input<Character> input) {
            super(input);
        }
    }

    static {
        // To get around circular references.
        final Ref<Character, Ctx, Expr> expr = Ref.of();

        final Parser<Character, Ctx, Character> open = chr('(');
        final Parser<Character, Ctx, Character> close = chr(')');
        final Parser<Character, Ctx, Character> comma = chr(',');

        final Parser<Character, Ctx, UnaryOp> plus = Text.<Ctx>chr('+').andR(pure(UnaryOp.POS));
        final Parser<Character, Ctx, UnaryOp> minus = Text.<Ctx>chr('-').andR(pure(UnaryOp.NEG));

        final Parser<Character, Ctx, BinOp> add = Text.<Ctx>chr('+').andR(pure(BinOp.ADD));
        final Parser<Character, Ctx, BinOp> sub = Text.<Ctx>chr('-').andR(pure(BinOp.SUBTRACT));
        final Parser<Character, Ctx, BinOp> mult = Text.<Ctx>chr('*').andR(pure(BinOp.MULTIPLY));
        final Parser<Character, Ctx, BinOp> div = Text.<Ctx>chr('/').andR(pure(BinOp.DIVIDE));

        final Parser<Character, Ctx, NumExpr.Units> pct = Text.<Ctx>string("%").andR(pure(NumExpr.Units.PCT));
        final Parser<Character, Ctx, NumExpr.Units> bps = Text.<Ctx>string("bp").andR(pure(NumExpr.Units.BPS));

        final Parser<Character, Ctx, String> funcName =
            Text.<Ctx>string("min").or(string("max"));

        // addSub = add | sub
        final Parser<Character, Ctx, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Character, Ctx, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // units = % | bp
        final Parser<Character, Ctx, NumExpr.Units> units =
            pct.or(bps).or(pure(NumExpr.Units.ABS));

        // num = intr
        final Parser<Character, Ctx, Expr> num =
            Text.<Ctx>dble().and(units).map(Model::numExpr);

        // brackExpr = open expr close
        final Parser<Character, Ctx, Expr> brackExpr =
            open.andR(expr).andL(close);

        final Parser<Character, Ctx, Expr> var =
            Text.<Ctx>alpha().map(Model::varExpr);

        // funcN = name { args | ε }
        final Parser<Character, Ctx, Expr> funcN =
            funcName.andL(open).and(expr).andL(comma).and(expr).andL(close)
                .map(Model::func2Expr);

        // sign = + | -
        final Parser<Character, Ctx, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
        final Parser<Character, Ctx, Expr> signedExpr =
            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Character, Ctx, Expr> term =
            num.or(brackExpr).or(funcN).or(var).or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Character, Ctx, Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final Parser<Character, Ctx, Expr> parser;

    public static Result<Character, Expr> parse(String s) {
        return parser.run(new Ctx(Input.of(s)));
    }
}
