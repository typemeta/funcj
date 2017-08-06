package org.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.funcj.data.*;
import org.funcj.util.Functions.F;
import org.junit.Assume;
import org.junit.runner.RunWith;

import static org.funcj.parser.Combinators.any;
import static org.funcj.parser.Combinators.value;
import static org.funcj.parser.Parser.ap;
import static org.funcj.parser.Parser.pure;
import static org.funcj.parser.TestUtils.*;
import static org.hamcrest.CoreMatchers.not;

@RunWith(JUnitQuickcheck.class)
public class ParserTest {

    @Property
    public void pureConsumesNoInput(char c1) {
        final Input<Chr> input = Input.of("");

        final Parser<Chr, Chr> parser = pure(Chr.valueOf(c1));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1), input);
    }

    @Property
    public void mapTransformsValue(char c1, char c2) {
        Assume.assumeThat(c1, not(c2));

        final Input<Chr> input = Input.of(String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Chr> parser =
                value(Chr.valueOf(c1))
                        .map(c -> Chr.valueOf(c.charValue() + 1));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1 + 1), input.next());
    }

    @Property
    public void apAppliesF(char c1) {
        final Input<Chr> input = Input.of(String.valueOf(c1));

        final F<Chr, Chr> f = (Chr c) -> Chr.valueOf(c.charValue() + 1);

        final Parser<Chr, Chr> parser = ap(f, any());

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1 + 1), input.next());
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

        ParserCheck.parser(parser)
                .withInput(input1)
                .succeedsWithResult(cc1, input1.next());

        ParserCheck.parser(parser)
                .withInput(input2)
                .succeedsWithResult(cc2, input2.next());

        ParserCheck.parser(parser)
                .withInput(input3)
                .fails();
    }

    @Property
    public void andWithMapAppliesF(char c1, char c2) {
        // String.toCharArray returns a new array each time, so ensure we call it only once.
        final char[] data = ("" + c1 + c2).toCharArray();
        final Input<Chr> input = Input.of(data);

        final Parser<Chr, Tuple2<Chr, Chr>> parser =
                Combinators.<Chr>any().and(any()).map(Tuple2::of);

        final Input<Chr> expInp = Input.of(data).next().next();

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Tuple2.of(Chr.valueOf(c1), Chr.valueOf(c2)), expInp);
    }
}
