package org.javafp.parsec4j.text;

import org.javafp.util.Chr;
import org.javafp.util.Functions.Op2;
import org.javafp.parsec4j.expr.Model;

import static org.javafp.parsec4j.text.Parser.*;
import static org.javafp.parsec4j.text.Text.*;
import static org.javafp.parsec4j.expr.Model.*;

public abstract class Grammar {

    static {
        // To get around circular references.
        final Ref<Expr> expr = Ref.of();

        final Parser<Chr> open = chr('(');
        final Parser<Chr> close = chr(')');
        final Parser<Chr> comma = chr(',');

        final Parser<UnaryOp> plus = chr('+').andR(pure(UnaryOp.POS));
        final Parser<UnaryOp> minus = chr('-').andR(pure(UnaryOp.NEG));

        final Parser<BinOp> add = chr('+').andR(pure(BinOp.ADD));
        final Parser<BinOp> sub = chr('-').andR(pure(BinOp.SUBTRACT));
        final Parser<BinOp> mult = chr('*').andR(pure(BinOp.MULTIPLY));
        final Parser<BinOp> div = chr('/').andR(pure(BinOp.DIVIDE));

        final Parser<NumExpr.Units> pct = string("%").andR(pure(NumExpr.Units.PCT));
        final Parser<NumExpr.Units> bps = string("bp").andR(pure(NumExpr.Units.BPS));

        final Parser<String> funcName =
            chr('m')
                .andR(
                    (string("in").map(u -> "min"))
                        .or(string("ax").map(u -> "max")));

        // addSub = add | sub
        final Parser<Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // units = % | bp
        final Parser<NumExpr.Units> units =
            pct.or(bps).or(pure(NumExpr.Units.ABS));

        // num = intr
        final Parser<Expr> num =
            dble.and(units)
                .map(NumExpr::new);

        // brackExpr = open expr close
        final Parser<Expr> brackExpr =
            open.andR(expr).andL(close);

        final Parser<Expr> var =
            alpha.map(Model::varExpr);

        // funcN = name { args | ε }
        final Parser<Expr> funcN =
            funcName.andL(open).and(expr).andL(comma).and(expr).andL(close)
                .map(Func2Expr::new);

        // sign = + | -
        final Parser<UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
//        final Parser<Expr> signedExpr =
//            sign.and(expr)
//                .map(UnaryOpExpr::new);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Expr> term =
            num.or(brackExpr).or(funcN).or(var); //.or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final Parser<Expr> parser;

    public static Result<Expr> parse(String s) {
        return parser.run(Input.of(s));
    }
}
