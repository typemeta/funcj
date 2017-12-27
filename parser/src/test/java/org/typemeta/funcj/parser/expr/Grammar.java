package org.typemeta.funcj.parser.expr;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.functions.Functions.Op2;
import org.typemeta.funcj.parser.*;

import static org.typemeta.funcj.parser.Text.*;
import static org.typemeta.funcj.parser.expr.Model.*;

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

        // addSub = add | sub
        final Parser<Chr, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Chr, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // num = <dble>
        final Parser<Chr, Expr> num = dble.map(Model::numExpr);

        // brackExpr = open expr close
        final Parser<Chr, Expr> brackExpr =
            open.andR(expr).andL(close);

        // var = <alpha>
        final Parser<Chr, Expr> var =
            alpha.map(Chr::charValue).map(Model::varExpr);

        // sign = + | -
        final Parser<Chr, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
        final Parser<Chr, Expr> signedExpr =
            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Chr, Expr> term =
            num.or(brackExpr).or(var).or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Chr, Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final Parser<Chr, Expr> parser;

    public static Result<Chr, Expr> parse(String s) {
        return parser.parse(Input.of(s));
    }
}
