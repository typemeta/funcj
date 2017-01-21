package org.javafp.parsec4j.expr;

import org.javafp.data.Chr;
import org.javafp.parsec4j.*;
import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Parser.pure;
import static org.javafp.parsec4j.Text.*;
import static org.javafp.parsec4j.expr.Model.*;

public abstract class Grammar {

    public static class Ctx extends Parser.Ctx<Chr> {
        public Ctx(Input<Chr> input) {
            super(input);
        }
    }

    static {
        // To get around circular references.
        final Ref<Chr, Ctx, Expr> expr = Ref.of();

        final Parser<Chr, Ctx, Chr> open = chr('(');
        final Parser<Chr, Ctx, Chr> close = chr(')');
        final Parser<Chr, Ctx, Chr> comma = chr(',');

        final Parser<Chr, Ctx, UnaryOp> plus = Text.<Ctx>chr('+').andR(pure(UnaryOp.POS));
        final Parser<Chr, Ctx, UnaryOp> minus = Text.<Ctx>chr('-').andR(pure(UnaryOp.NEG));

        final Parser<Chr, Ctx, BinOp> add = Text.<Ctx>chr('+').andR(pure(BinOp.ADD));
        final Parser<Chr, Ctx, BinOp> sub = Text.<Ctx>chr('-').andR(pure(BinOp.SUBTRACT));
        final Parser<Chr, Ctx, BinOp> mult = Text.<Ctx>chr('*').andR(pure(BinOp.MULTIPLY));
        final Parser<Chr, Ctx, BinOp> div = Text.<Ctx>chr('/').andR(pure(BinOp.DIVIDE));

        final Parser<Chr, Ctx, NumExpr.Units> pct = Text.<Ctx>string("%").andR(pure(NumExpr.Units.PCT));
        final Parser<Chr, Ctx, NumExpr.Units> bps = Text.<Ctx>string("bp").andR(pure(NumExpr.Units.BPS));

        final Parser<Chr, Ctx, String> funcName =
            Text.<Ctx>string("min").or(string("max"));

        // addSub = add | sub
        final Parser<Chr, Ctx, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Chr, Ctx, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // units = % | bp
        final Parser<Chr, Ctx, NumExpr.Units> units =
            pct.or(bps).or(pure(NumExpr.Units.ABS));

        // num = intr
        final Parser<Chr, Ctx, Expr> num =
            Text.<Ctx>dble().and(units).map(Model::numExpr);

        // brackExpr = open expr close
        final Parser<Chr, Ctx, Expr> brackExpr =
            open.andR(expr).andL(close);

        final Parser<Chr, Ctx, Expr> var =
            Text.<Ctx>alpha().map(c -> Model.varExpr(c.charValue()));

        // funcN = name { args | ε }
        final Parser<Chr, Ctx, Expr> funcN =
            funcName.andL(open).and(expr).andL(comma).and(expr).andL(close)
                .map(Model::func2Expr);

        // sign = + | -
        final Parser<Chr, Ctx, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
        final Parser<Chr, Ctx, Expr> signedExpr =
            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Chr, Ctx, Expr> term =
            num.or(brackExpr).or(funcN).or(var).or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Chr, Ctx, Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final Parser<Chr, Ctx, Expr> parser;

    public static Result<Chr, Expr> parse(String s) {
        return parser.run(new Ctx(Input.of(s)));
    }
}
