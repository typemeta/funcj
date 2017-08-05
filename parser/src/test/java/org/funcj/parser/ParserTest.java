package org.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.funcj.data.Chr;
import org.funcj.util.Functions;
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
        final Chr cc3 = Chr.valueOf(c3);

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
}
