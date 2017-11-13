package org.typemeta.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.*;
import org.typemeta.funcj.functions.Functions.Predicate;

@RunWith(JUnitQuickcheck.class)
public class CombinatorsPropTests {

    @Property
    public void failAlwaysFails(char c1) {
        final Input<Chr> input = Input.of(String.valueOf(c1));

        final Parser<Chr, Chr> parser = Combinators.fail();

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .fails();
    }

    @Property
    public void eofSucceedsOnEmptyInput(char c1) {
        final Input<Chr> input = Input.of("");

        final Parser<Chr, Unit> parser = Combinators.eof();

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Unit.UNIT, input);
    }

    @Property
    public void eofFailsOnNonEmptyInput(char c1) {

        final Parser<Chr, Unit> parser = Combinators.eof();

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of("" + c1))
                .fails();
    }

    @Property
    public void valueMatchesValue(char c1, char c2) {
        final Input<Chr> input1 = Input.of(String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Chr> parser = Combinators.value(Chr.valueOf(c1));

        TestUtils.ParserCheck.parser(parser)
                .withInput(input1)
                .succeedsWithResult(Chr.valueOf(c1), input1.next());

        TestUtils.ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }

    @Property
    public void valueFailsOnNonEmptyInput(char c1) {

        final Parser<Chr, Chr> parser = Combinators.value(Chr.valueOf(c1));

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(""))
                .fails();
    }

    @Property
    public void satisfyAppliesPredicate(char c0) {
        final char even;
        final char odd;
        if (c0 % 2 == 0) {
            even = c0;
            odd = (char)((even + 1) % 0xffff);
        } else {
            odd = c0;
            even = (char)((odd + 1) % 0xffff);
        }

        final Input<Chr> input1 = Input.of(String.valueOf(even));
        final Input<Chr> input2 = Input.of(String.valueOf(odd));

        final Parser<Chr, Chr> parser =
                Combinators.satisfy("even", Predicate.of((Chr c) -> c.charValue() % 2 == 0));

        TestUtils.ParserCheck.parser(parser)
                .withInput(input1)
                .succeedsWithResult(Chr.valueOf(even), input1.next());

        TestUtils.ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }

    @Property
    public void satisfyFailsOnNonEmptyInput() {

        final Parser<Chr, Chr> parser =
                Combinators.satisfy("even", Predicate.of((Chr c) -> c.charValue() % 2 == 0));

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(""))
                .fails();
    }

    @Property
    public void anyMatchesAnything(char c1) {
        final Input<Chr> input = Input.of(String.valueOf(c1));

        final Parser<Chr, Chr> parser = Combinators.any();

        TestUtils.ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1), input.next());
    }

    @Property
    public void anyFailsOnNonEmptyInput() {

        final Parser<Chr, Chr> parser = Combinators.any();

        TestUtils.ParserCheck.parser(parser)
                .withInput(Input.of(""))
                .fails();
    }
}
