package org.typemeta.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.tuples.Tuple2;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.parser.Combinators.*;
import static org.typemeta.funcj.parser.Parser.ap;
import static org.typemeta.funcj.parser.Text.intr;

@RunWith(JUnitQuickcheck.class)
public class ParserTest {

    @Property
    public void pureConsumesNoInput(char c1) {
        final Input<Chr> input = Input.of("");

        final Parser<Chr, Chr> parser = Parser.pure(Chr.valueOf(c1));

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1), input);
    }

    @Property
    public void mapTransformsValue(char c1) {
        final Input<Chr> input = Input.of(String.valueOf(c1));

        final Parser<Chr, Chr> parser =
                value(Chr.valueOf(c1))
                        .map(c -> Chr.valueOf(c.charValue() + 1));

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1 + 1), input.next());
    }

    @Property
    public void apAppliesF(char c1) {
        final Input<Chr> input = Input.of(String.valueOf(c1));

        final F<Chr, Chr> f = c -> Chr.valueOf(c.charValue() + 1);

        final Parser<Chr, Chr> parser = ap(f, any());

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1 + 1), input.next());
    }

    @Property
    public void apChainsParsers(char c1, char c2) {
        Assume.assumeThat(c1, not(c2));

        final Chr cc1 = Chr.valueOf(c1);
        final Chr cc2 = Chr.valueOf(c2);

        // String.toCharArray creates a new array each time, so ensure we call it only once.
        final char[] ca12 = String.valueOf("" + c1 + c2).toCharArray();
        final char[] ca11 = String.valueOf("" + c1 + c1).toCharArray();

        final Parser<Chr, Tuple2<Chr, Chr>> parser =
                ap(
                        ap(
                                a -> b -> Tuple2.of(a, b),
                                value(cc1)
                        ), value(cc2)
                );

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(ca12))
                .succeedsWithResult(Tuple2.of(cc1, cc2), Input.of(ca12).next().next());

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(ca11))
                .fails();
    }

    @Property
    public void orAppliesEitherParser(char c1, char c2, char c3) {
        Assume.assumeThat(c1, not(c2));
        Assume.assumeThat(c1, not(c3));
        Assume.assumeThat(c2, not(c3));

        final Input<Chr> input1 = Input.of(String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));
        final Input<Chr> input3 = Input.of(String.valueOf(c3));

        final Chr cc1 = Chr.valueOf(c1);
        final Chr cc2 = Chr.valueOf(c2);

        final Parser<Chr, Chr> parser = value(cc1).or(value(cc2));

        TestUtils.ParserCheck.parser(parser)
                .withInput(input1)
                .succeedsWithResult(cc1, input1.next());

        TestUtils.ParserCheck.parser(parser)
                .withInput(input2)
                .succeedsWithResult(cc2, input2.next());

        TestUtils.ParserCheck.parser(parser)
                .withInput(input3)
                .fails();
    }

    @Property
    public void andWithMapAppliesF(char c1, char c2) {
        // String.toCharArray returns a new array each time, so ensure we call it only once.
        final char[] data = ("" + c1 + c2).toCharArray();
        final Input<Chr> input = Input.of(data);

        final Parser<Chr, Tuple2<Chr, Chr>> parser =
                any(Chr.class)
                        .and(any())
                        .map(Tuple2::of);

        final Input<Chr> expInp = Input.of(data).next().next();

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Tuple2.of(Chr.valueOf(c1), Chr.valueOf(c2)), expInp);
    }

    @Property
    public void manyMatchesMany(char c1, char c2) {
        final String s = "" + c1 + c2;
        final char[] ca = s.toCharArray();

        final Parser<Chr, String> parser = any(Chr.class).many().map(Chr::listToString);

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(ca))
                .succeedsWithResult(s, Input.of(ca).next().next());
    }

    @Property
    public void manySuccedsOnNonEmptyInput() {
        final Input<Chr> input = Input.of("");

        final Parser<Chr, String> parser = any(Chr.class).many().map(Chr::listToString);

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult("", input);
    }

    @Property
    public void many1MatchesMany(char c1, char c2) {
        final String s = "" + c1 + c2;
        final char[] ca = s.toCharArray();

        final Parser<Chr, String> parser = any(Chr.class).many().map(Chr::listToString);

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(ca))
                .succeedsWithResult(s, Input.of(ca).next().next());
    }

    @Property
    public void many1FailsOnNonEmptyInput() {
        final Input<Chr> input = Input.of("");

        final Parser<Chr, String> parser = any(Chr.class).many().map(Chr::listToString);

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .fails();
    }

    private static void assertEvaluate(Parser<Chr, Integer> parser, String s, int expected) {
        assertEquals(s, expected, parser.parse(Input.of(s)).getOrThrow().intValue());
    }

    private static final Parser<Chr, Op2<Integer>> subtr = value(Chr.valueOf('-'), Op2.of((x, y) -> x - y));

    private static final int Z = 1000;

    @Test
    public void testChainl() {
        final Parser<Chr, Integer> parser = intr.chainl(subtr, Z);

        assertEvaluate(parser, "", Z);
        assertEvaluate(parser, "1", 1);
        assertEvaluate(parser, "1-2", -1);
        assertEvaluate(parser, "1-2-3", (1-2)-3);
    }

    @Test
    public void testChainr() {
        final Parser<Chr, Integer> parser = intr.chainr(subtr, Z);

        assertEvaluate(parser, "", Z);
        assertEvaluate(parser, "1", 1);
        assertEvaluate(parser, "1-2", -1);
        assertEvaluate(parser, "1-2-3", 1-(2-3));
    }

    @Test
    public void testChainl1() {
        final Parser<Chr, Integer> parser = intr.chainl1(subtr);

        assertEvaluate(parser, "1", 1);
        assertEvaluate(parser, "1-2", -1);
        assertEvaluate(parser, "1-2-3", (1-2)-3);
    }

    @Test
    public void testChainr1() {
        final Parser<Chr, Integer> parser = intr.chainr1(subtr);

        assertEvaluate(parser, "1", 1);
        assertEvaluate(parser, "1-2", -1);
        assertEvaluate(parser, "1-2-3", 1-(2-3));
    }
}
