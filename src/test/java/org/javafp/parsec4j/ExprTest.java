package org.javafp.parsec4j;

import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Text.*;

public abstract class ExprTest {
    public static class Context implements Parser.Context<Character> {
        private final Input<Character> input;

        public Context(Input<Character> input) {
            this.input = input;
        }

        @Override
        public Input<Character> input() {
            return input;
        }
    }

    static final public Ref<Character, Context, Double> expr = Ref.of();

    static {
        final Parser<Character, Context, Op2<Double>> add =
            Text.<Context>chr('+').map(c -> (x, y) -> x + y);
        final Parser<Character, Context, Op2<Double>> sub =
            Text.<Context>chr('-').map(c -> (x, y) -> x - y);
        final Parser<Character, Context, Op2<Double>> mult =
            Text.<Context>chr('*').map(c -> (x, y) -> x * y);
        final Parser<Character, Context, Op2<Double>> div =
            Text.<Context>chr('/').map(c -> (x, y) -> x / y);
        final Parser<Character, Context, Op2<Double>> binOp = add.or(sub).or(mult).or(div);

        final Parser<Character, Context, Double> binOpExpr =
            expr.and(binOp).and(expr)
                .map((l, op, r) -> op.apply(l, r));

        final Parser<Character, Context, Double> brks =
            Text.<Context>chr('(').andR(binOpExpr).andL(chr(')'));

        expr.set(Text.<Context>dble().or(brks));
    }
}
