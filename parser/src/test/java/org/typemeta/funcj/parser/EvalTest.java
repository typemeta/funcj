package org.typemeta.funcj.parser;

import org.junit.Test;
import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.functions.Functions.Op;
import org.typemeta.funcj.functions.Functions.Op2;

import static org.typemeta.funcj.parser.Combinators.choice;
import static org.typemeta.funcj.parser.Text.chr;
import static org.typemeta.funcj.parser.Text.intr;

public class EvalTest {

    enum BinOp {
        ADD {Op2<Integer> op() {return (l, r) -> l + r;}},
        SUB {Op2<Integer> op() {return (l, r) -> l - r;}},
        MUL {Op2<Integer> op() {return (l, r) -> l * r;}},
        DIV {Op2<Integer> op() {return (l, r) -> l / r;}};

        abstract Op2<Integer> op();
    }

    @Test
    public void test() {

        final Ref<Chr, Op<Integer>> expr = Parser.ref();

        final Parser<Chr, Op<Integer>> var = chr('x').map(u -> x -> x);

        final Parser<Chr, Op<Integer>> num = intr.map(i -> x -> i);

        final Parser<Chr, BinOp> binOp =
                choice(
                        chr('+').map(c -> BinOp.ADD),
                        chr('-').map(c -> BinOp.SUB),
                        chr('*').map(c -> BinOp.MUL),
                        chr('/').map(c -> BinOp.DIV)
                );

        final Parser<Chr, Op<Integer>> add =
                chr('(')
                        .andR(expr)
                        .and(binOp)
                        .and(expr)
                        .andL(chr(')'))
                        .map(lhs -> bo -> rhs -> x -> bo.op().apply(lhs.apply(x), rhs.apply(x)));

        expr.set(choice(var, num, add));

        final int i = expr.parse(Input.of("(x*((x/2)+x))")).getOrThrow().apply(4);
        assert(i == 24);
    }
}
