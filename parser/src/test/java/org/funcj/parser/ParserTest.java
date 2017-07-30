package org.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.funcj.data.Chr;
import org.funcj.util.Functions.*;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.not;

@RunWith(JUnitQuickcheck.class)
public class ParserTest {

    private static <I, A> void checkSuccess(Parser<I, A> p, Input<I> input, A expVal, Input<I> expInput) {
        p.run(input).handle(
                succ -> {
                    Assert.assertEquals("Parse result value", expVal, succ.value());
                    Assert.assertEquals("Parse result next input", expInput, succ.next());
                },
                fail -> {
                    throw new RuntimeException("Unexpected parse failure");
                }
        );
    }

    private static <I, A> void checkFailure(Parser<I, A> p, Input<I> input) {
        p.run(input).handle(
                succ -> {
                    throw new RuntimeException("Unexpected parse success");
                },
                fail -> {}
        );
    }

    static class ParserCheck<I, A> {
        static <I, A> ParserCheck<I, A> parser(Parser<I, A> parser) {
            return new ParserCheck<I, A>(parser);
        }

        final Parser<I, A> parser;

        ParserCheck(Parser<I, A> parser) {
            this.parser = parser;
        }

        WithInput withInput(Input<I> input) {
            return new WithInput(input);
        }

        class WithInput {
            final Input<I> input;

            WithInput(Input<I> input) {
                this.input = input;
            }

            void succeedsWithResult(A value, Input<I> next) {
                checkSuccess(parser, input, value, next);
            }

            void fails() {
                checkFailure(parser, input);
            }
        }
    }

    @Property
    public void pureConsumesNoInput(char c1) {
        final Input<Chr> input = Input.of("");

        final Parser<Chr, Chr> parser = Parser.pure(Chr.valueOf(c1));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1), input);
    }

    @Property
    public void valueParsesValue(char c1, char c2) {
        final Input<Chr> input = Input.of( String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Chr> parser = Parser.value(Chr.valueOf(c1));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1), input.next());

        ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }

    @Property
    public void mapTransformsValue(char c1, char c2) {
        Assume.assumeThat(c1, not(c2));

        final Input<Chr> input = Input.of(String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Chr> parser =
                Parser.value(Chr.valueOf(c1))
                        .map(x -> Chr.valueOf(x.charValue() + 1));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Chr.valueOf(c1 + 1), input.next());

        ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }

    @Property
    public void apAppliesFunction(char c1, char c2) {
        final Input<Chr> input = Input.of(String.valueOf(c1));
        final Input<Chr> input2 = Input.of(String.valueOf(c2));

        final Parser<Chr, Optional<Chr>> parser =
                Parser.ap(
                        Parser.pure(F.of(Optional::of)),
                        Parser.value(Chr.valueOf(c1)));

        ParserCheck.parser(parser)
                .withInput(input)
                .succeedsWithResult(Optional.of(Chr.valueOf(c1)), input.next());

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
                Parser.satisfy("test", Predicate.of((Chr c) -> c.charValue() % 2 == 0));

        ParserCheck.parser(parser)
                .withInput(input1)
                .succeedsWithResult(Chr.valueOf(c1), input1.next());

        ParserCheck.parser(parser)
                .withInput(input2)
                .fails();
    }
}
