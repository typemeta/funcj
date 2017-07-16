package org.funcj.parser;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.funcj.data.Chr;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assume.assumeThat;

@RunWith(JUnitQuickcheck.class)
public class ParserProperties {

    private static <A> void checkSuccess(Parser<Chr, A> p, Input<Chr> input, A expVal, Input<Chr> expInput) {
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

    private static <A> void checkSuccess(Parser<Chr, A> p, Input<Chr> input, A expVal, Chr expNext) {
        p.run(input).handle(
                succ -> {
                    Assert.assertEquals("Parse result value", expVal, succ.value());
                    Assert.assertEquals("Parse result next input", expNext, succ.next().get());
                },
                fail -> {
                    throw new RuntimeException("Unexpected parse failure");
                }
        );
    }

    private static <A> void checkFailure(Parser<Chr, A> p, String s) {
        p.run(Input.of(s)).handle(
                succ -> {
                    throw new RuntimeException("Unexpected parse success");
                },
                fail -> {}
        );
    }
//
//    private static <A> void checkSuccess(Parser<Chr, A> p, Input<Chr> input, A expVal) {
//        checkSuccess(p, input, expVal, input.get());
//    }

    private static void checkSuccess(Parser<Chr, Chr> p, String s, char expVal) {
        final Input<Chr> input = Input.of(s);
        checkSuccess(p, input, Chr.valueOf(expVal), input);
    }
//
//    private static void checkSuccess(Parser<Chr, Chr> p, char expVal) {
//        final Input<Chr> input = Input.of("");
//        checkSuccess(p, input, Chr.valueOf(expVal), input.next());
//    }

    @Property
    public void pureProp(char c) {
        final Parser<Chr, Chr> p = Parser.pure(Chr.valueOf(c));
        final Input<Chr> input = Input.of("");
        checkSuccess(p, input, Chr.valueOf(c), input);
    }

    @Property
    public void valueProp(char c, char c2) {
        assumeThat(c, not(equalTo(c2)));
        final String s = String.valueOf(c);
        final Input<Chr> input = Input.of(s);
        final Parser<Chr, Chr> p = Parser.value(Chr.valueOf(c));
        checkSuccess(p, input, Chr.valueOf(c), input.next());
    }

    @Property
    public void mapProp(char c, char sc) {
//        final String s = String.valueOf(sc);
//        final Parser<Chr, Chr> p = Parser.value(Chr.valueOf(c)).map(x -> Chr.valueOf(x.charValue() + 1));
//        checkSuccess(p, String.valueOf(c), (char)(c + 1));
//
//        final Parser<Chr, Chr> p2 = Parser.value(Chr.valueOf(c)).map(x -> Chr.valueOf(x.charValue() + 1));
//        if (sc == c) {
//            checkSuccess(p2, s, (char) (c + 1));
//        } else {
//            checkFailure(p2, s);
//        }
    }
}
