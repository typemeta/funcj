package org.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.funcj.data.Chr;
import org.funcj.util.Functions.*;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.funcj.parser.TestUtils.*;
import static org.hamcrest.CoreMatchers.not;

@RunWith(JUnitQuickcheck.class)
public class CombinatorsTest {

    @Property
    public void valueParsesValue(char c1, char c2) {
        final Input<Chr> input = Input.of( String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Chr> parser = Combinators.value(Chr.valueOf(c1));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1), input.next());

        ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }

    @Property
    public void satisfyWorks(char c0) {
        final char c1;
        final char c2;
        if (c0 % 2 == 0) {
            c1 = c0;
            c2 = (char)((c1 + 1) % 0xffff);
        } else {
            c2 = c0;
            c1 = (char)((c2 + 1) % 0xffff);
        }

        final Input<Chr> input1 = Input.of(String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Chr> parser =
                Combinators.satisfy("test", Predicate.of((Chr c) -> c.charValue() % 2 == 0));

        ParserCheck.parser(parser)
                .withInput(input1)
                .succeedsWithResult(Chr.valueOf(c1), input1.next());

        ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }
}
