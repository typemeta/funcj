package org.javafp.parsec4j;

import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Text.*;

public abstract class ExprTest {
    static final public Ref<Character, Double> expr = Ref.of();

    static {
        final Parser<Character, Op2<Double>> add =
            chr('+').map(c -> (x, y) -> x + y);
        final Parser<Character, Op2<Double>> sub =
            chr('-').map(c -> (x, y) -> x - y);
        final Parser<Character, Op2<Double>> mult =
            chr('*').map(c -> (x, y) -> x * y);
        final Parser<Character, Op2<Double>> div =
            chr('/').map(c -> (x, y) -> x / y);
        final Parser<Character, Op2<Double>> binOp = add.or(sub).or(mult).or(div);

        final Parser<Character, Double> binOpExpr =
            expr.and(binOp).and(expr)
                .map((l, op, r) -> op.apply(l, r));

        final Parser<Character, Double> brks =
            chr('(').andR(binOpExpr).andL(chr(')'));

        expr.set(dble.or(brks));
    }
}
