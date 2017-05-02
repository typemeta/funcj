package org.funcj.parser;

import org.funcj.data.Chr;
import org.funcj.util.Functions.Op2;

import static org.funcj.parser.Text.chr;
import static org.funcj.parser.Text.dble;

public abstract class ExprTest {

    static final public Ref<Chr, Double> expr = Ref.of();

    static {
        final Parser<Chr, Op2<Double>> add =
            chr('+').map(c -> (x, y) -> x + y);
        final Parser<Chr, Op2<Double>> sub =
            chr('-').map(c -> (x, y) -> x - y);
        final Parser<Chr, Op2<Double>> mult =
            chr('*').map(c -> (x, y) -> x * y);
        final Parser<Chr, Op2<Double>> div =
            chr('/').map(c -> (x, y) -> x / y);
        final Parser<Chr, Op2<Double>> binOp = add.or(sub).or(mult).or(div);

        final Parser<Chr, Double> binOpExpr =
            expr.and(binOp).and(expr)
                .map((l, op, r) -> op.apply(l, r));

        final Parser<Chr, Double> brks =
            chr('(').andR(binOpExpr).andL(chr(')'));

        expr.set(dble.or(brks));
    }
}
