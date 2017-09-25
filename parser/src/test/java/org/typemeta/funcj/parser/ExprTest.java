package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.util.Functions;

import static org.typemeta.funcj.parser.Text.*;

public abstract class ExprTest {

    static final public Ref<Chr, Double> expr = Parser.ref();

    static {
        final Parser<Chr, Functions.Op2<Double>> add =
            chr('+').map(c -> (x, y) -> x + y);
        final Parser<Chr, Functions.Op2<Double>> sub =
            chr('-').map(c -> (x, y) -> x - y);
        final Parser<Chr, Functions.Op2<Double>> mult =
            chr('*').map(c -> (x, y) -> x * y);
        final Parser<Chr, Functions.Op2<Double>> div =
            chr('/').map(c -> (x, y) -> x / y);
        final Parser<Chr, Functions.Op2<Double>> binOp = add.or(sub).or(mult).or(div);

        final Parser<Chr, Double> binOpExpr =
            expr.and(binOp).and(expr)
                .map((l, op, r) -> op.apply(l, r));

        final Parser<Chr, Double> brks =
            chr('(').andR(binOpExpr).andL(chr(')'));

        expr.set(dble.or(brks));
    }
}
