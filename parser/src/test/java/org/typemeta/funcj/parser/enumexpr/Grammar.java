package org.typemeta.funcj.parser.enumexpr;

import org.typemeta.funcj.functions.Functions.Op2;
import org.typemeta.funcj.parser.*;
import org.typemeta.funcj.parser.expr.Model;

import java.util.*;

import static org.typemeta.funcj.parser.Combinators.*;
import static org.typemeta.funcj.parser.expr.Model.*;

public abstract class Grammar {

    private static <A> Parser<Token, A> pure(A a) {
        return Parser.pure(a);
    }

    static {
        // To get around circular references.
        final Ref<Token, Expr> expr = Parser.ref();

        final Parser<Token, Token> open = value(Token.Symbol.OPEN);
        final Parser<Token, Token> close = value(Token.Symbol.CLOSE);

        final Parser<Token, UnaryOp> plus = value(Token.Symbol.PLUS, UnaryOp.POS);
        final Parser<Token, UnaryOp> minus = value(Token.Symbol.MINUS, UnaryOp.NEG);

        final Parser<Token, BinOp> add = value(Token.Symbol.PLUS, BinOp.ADD);
        final Parser<Token, BinOp> sub = value(Token.Symbol.MINUS, BinOp.SUBTRACT);
        final Parser<Token, BinOp> mult = value(Token.Symbol.MULT, BinOp.MULTIPLY);
        final Parser<Token, BinOp> div = value(Token.Symbol.DIV, BinOp.DIVIDE);

        // addSub = add | sub
        final Parser<Token, Op2<Expr>> addSub =
            add.or(sub).map(BinOp::ctor);

        // multDiv = mult | div
        final Parser<Token, Op2<Expr>> multDiv =
            mult.or(div).map(BinOp::ctor);

        // num = <dble>
        final Parser<Token, Expr> num =
                Combinators.satisfy("isNumber", Token::isNumber)
                        .map(Token.Number.class::cast)
                        .map(Token.Number::value)
                        .map(Model::numExpr);

        // brackExpr = open expr close
        final Parser<Token, Expr> brackExpr =
            open.andR(expr).andL(close);

        // var = <alpha>
        final Parser<Token, Expr> var =
                Combinators.satisfy("isVariable", Token::isVariable)
                        .map(Token.Variable.class::cast)
                        .map(Token.Variable::name)
                        .map(Model::varExpr);

        // sign = + | -
        final Parser<Token, UnaryOp> sign = plus.or(minus);

        // signedExpr = sign expr
        final Parser<Token, Expr> signedExpr =
            sign.and(expr).map(Model::unaryOpExpr);

        // term = num | brackExpr | funcN | signedExpr
        final Parser<Token, Expr> term =
            num.or(brackExpr).or(var).or(signedExpr);

        // prod = term chainl1 multDiv
        final Parser<Token, Expr> prod = term.chainl1(multDiv);

        // expr = prod chainl1 addSub
        parser = expr.set(prod.chainl1(addSub));
    }

    public static final Parser<Token, Expr> parser;

    public static Result<Token, Expr> parse(List<Token> tokens) {
        return parser.parse(new ListInput<>(tokens));
    }

    public static Result<Token, Expr> parse(Token[] tokens) {
        return parse(Arrays.asList(tokens));
    }
}
