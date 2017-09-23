package io.typemeta.funcj.parser.expr;

import io.typemeta.funcj.parser.*;
import io.typemeta.funcj.data.Chr;
import io.typemeta.funcj.util.Functions.Op2;

import static io.typemeta.funcj.parser.Combinators.chainl1;
import static io.typemeta.funcj.parser.Text.*;
import static io.typemeta.funcj.parser.expr.Model.*;

public abstract class Grammar {

    private static <A> Parser<Chr, A> pure(A a) {
        return Parser.pure(a);
    }

    static {
        // To get around circular references.
        final Ref<Chr, Expr> expr = Parser.ref();

        final Parser<Chr, Chr> open = chr('(');
        final Parser<Chr, Chr> close = chr(')');
        final Parser<Chr, Chr> comma = chr(',');

        final Parser<Chr, UnaryOp> plus = chr('+').andR(pure(UnaryOp.POS));
        final Parser<Chr, UnaryOp> minus = chr('-').andR(pure(UnaryOp.NEG));

        final Parser<Chr, BinOp> add = chr('+').andR(pure(BinOp.ADD));
        final Parser<Chr, BinOp> sub = chr('-').andR(pure(BinOp.SUBTRACT));
        final Parser<Chr, BinOp> mult = chr('*').andR(pure(BinOp.MULTIPLY));
        final Parser<Chr, BinOp> div = chr('/').andR(pure(BinOp.DIVIDE));

        final Parser<Chr, NumExpr.Units> pct = string("%").andR(pure(NumExpr.Units.PCT));
        final Parser<Chr, NumExpr.Units> bps = string("bp").andR(pure(NumExpr.Units.BPS));

        final Parser<Chr, String> funcName =
            chr('m')
                .andR(
                    (string("in").map(u -> "min"))
                        .or(string("ax").map(u -> "max")));

        // addSub = add | sub
        final Parser<Chr, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Chr, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // units = % | bp
        final Parser<Chr, NumExpr.Units> units =
            pct.or(bps).or(pure(NumExpr.Units.ABS));

        // num = intr
        final Parser<Chr, Expr> num =
            dble.and(units).map(Model::numExpr);

        // brackExpr = open expr close
        final Parser<Chr, Expr> brackExpr =
            open.andR(expr).andL(close);

        final Parser<Chr, Expr> var =
            alpha.map(Model::varExpr);

        // funcN = name { args | ε }
        final Parser<Chr, Expr> funcN =
            funcName.andL(open).and(expr).andL(comma).and(expr).andL(close)
                .map(Model::func2Expr);

        // sign = + | -
        final Parser<Chr, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
//        final Parser<Chr, Expr> signedExpr =
//            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Chr, Expr> term =
            num.or(brackExpr).or(funcN).or(var); //.or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Chr, Expr> prod = chainl1(term, multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(chainl1(prod, addSub));
    }

    public static final Parser<Chr, Expr> parser;

    public static Result<Chr, Expr> parse(String s) {
        return parser.run(Input.of(s));
    }
}
