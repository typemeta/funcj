package io.typemeta.funcj.parser;

import io.typemeta.funcj.data.Chr;
import org.junit.*;

import java.io.StringReader;

public class TextTest {
    private static <T> void parseSuccess(Parser<Chr, T> parser, String s, T exp) {
        Assert.assertEquals(exp, parseSuccess(parser, Input.of(s)));
        Assert.assertEquals(exp, parseSuccess(parser, Input.of(new StringReader(s))));
    }

    private static <T> T parseSuccess(Parser<Chr, T> parser, Input<Chr> in) {
        return parser.parse(in).getOrThrow();
    }

    private static <T> void parseFailure(Parser<Chr, T> parser, String s) {
        Assert.assertFalse(parse(parser, Input.of(s)).isSuccess());
        Assert.assertFalse(parse(parser, Input.of(new StringReader(s))).isSuccess());
    }

    private static <T> Result<Chr, T> parse(Parser<Chr, T> parser, Input<Chr> in) {
        return parser.parse(in);
    }

    @Test
    public void testAlpha() {

    }

    @Test
    public void testDigit() {

    }

    @Test
    public void testAlphaNum() {

    }

    @Test
    public void testWs() {

    }

    @Test
    public void testUintr() {

    }

    @Test
    public void testintr() {

    }

    @Test
    public void testUlng() {

    }

    @Test
    public void testLng() {

    }

    @Test
    public void testDbl() {
        parseSuccess(Text.dble, "0", 0.0);
    }
}
