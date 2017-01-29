package org.javafp.parsec4j;

import org.javafp.util.Chr;
import org.javafp.util.Functions.*;

import static org.javafp.parsec4j.Text.*;

public abstract class ExprTest {
    public static class Ctx extends Parser.Ctx<Chr> {
        public Ctx(Input<Chr> input) {
            super(input);
        }
    }

    static final public Ref<Chr, Ctx, Double> expr = Ref.of();

    static {
        final Parser<Chr, Ctx, Op2<Double>> add =
            Text.<Ctx>chr('+').map(c -> (x, y) -> x + y);
        final Parser<Chr, Ctx, Op2<Double>> sub =
            Text.<Ctx>chr('-').map(c -> (x, y) -> x - y);
        final Parser<Chr, Ctx, Op2<Double>> mult =
            Text.<Ctx>chr('*').map(c -> (x, y) -> x * y);
        final Parser<Chr, Ctx, Op2<Double>> div =
            Text.<Ctx>chr('/').map(c -> (x, y) -> x / y);
        final Parser<Chr, Ctx, Op2<Double>> binOp = add.or(sub).or(mult).or(div);

        final Parser<Chr, Ctx, Double> binOpExpr =
            expr.and(binOp).and(expr)
                .map((l, op, r) -> op.apply(l, r));

        final Parser<Chr, Ctx, Double> brks =
            Text.<Ctx>chr('(').andR(binOpExpr).andL(chr(')'));

        expr.set(Text.<Ctx>dble().or(brks));
    }
}
