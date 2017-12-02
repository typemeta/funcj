package org.typemeta.funcj.parser;

import org.junit.Test;
import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.functions.Functions.Op2;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.parser.Combinators.choice;
import static org.typemeta.funcj.parser.Text.*;

public class ExprTest {

    private static final Ref<Chr, Double> expr = Parser.ref();

    static {
        final Parser<Chr, Op2<Double>> add = chr('+').map(c -> (x, y) -> x + y);
        final Parser<Chr, Op2<Double>> sub = chr('-').map(c -> (x, y) -> x - y);
        final Parser<Chr, Op2<Double>> mult = chr('*').map(c -> (x, y) -> x * y);
        final Parser<Chr, Op2<Double>> div = chr('/').map(c -> (x, y) -> x / y);
        final Parser<Chr, Op2<Double>> binOp = add.or(sub).or(mult).or(div);

        final Parser<Chr, Double> binOpExpr =
                chr('(')
                        .andR(expr)
                        .and(binOp)
                        .and(expr)
                        .andL(chr(')'))
                        .map((l, op, r) -> op.apply(l, r));

        expr.set(choice(dble, binOpExpr));
    }

    private static final double EPSILON = 1e-8;

    private static void assertEvaluate(String s, double expected) {
        assertEquals(s, expected, expr.parse(Input.of(s)).getOrThrow().doubleValue(), EPSILON);
    }

    @Test
    public void testDble() {
        assertEvaluate("1", 1);
        assertEvaluate("1.2", 1.2);
    }

    @Test
    public void testAdd() {
        assertEvaluate("(1+2)", 3);
        assertEvaluate("(1.234+8.765)", 9.999);
    }

    @Test
    public void testAddMult() {
        assertEvaluate("(1+(2*3))", (1+(2*3)));
        assertEvaluate("(1.23+(4.56*7.89))", (1.23+(4.56*7.89)));
    }
}
