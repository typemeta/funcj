package org.typemeta.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.Chr;

import java.io.StringReader;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

@RunWith(JUnitQuickcheck.class)
public class TextTest {
    private static <T> void parseSuccess(Parser<Chr, T> parser, String s, T exp) {
        Assert.assertEquals(exp, parseSuccess(parser, Input.of(s)));
        Assert.assertEquals(exp, parseSuccess(parser, Input.of(new StringReader(s))));
    }

    private static <T> T parseSuccess(Parser<Chr, T> parser, Input<Chr> in) {
        return parser.apply(in).getOrThrow();
    }

    private static <T> void parseFailure(Parser<Chr, T> parser, String s) {
        assertFalse(parse(parser, Input.of(s)).isSuccess());
        assertFalse(parse(parser, Input.of(new StringReader(s))).isSuccess());
    }

    private static <T> Result<Chr, T> parse(Parser<Chr, T> parser, Input<Chr> in) {
        return parser.apply(in);
    }

    @Property
    public void testAlpha(char c) {
        final Result<Chr, Chr> res = Text.alpha.parse(Input.of("" + c));
        assertEquals("alpha parser applied to " + c, Character.isAlphabetic(c), res.isSuccess());
    }

    @Property
    public void testDigit(char c) {
        final Result<Chr, Chr> res = Text.digit.parse(Input.of("" + c));
        assertEquals("digit parser applied to " + c, Character.isDigit(c), res.isSuccess());
    }

    @Property
    public void testAlphaNum(char c) {
        final Result<Chr, Chr> res = Text.alphaNum.parse(Input.of("" + c));
        assertEquals("alphaNum parser applied to " + c, Character.isLetterOrDigit(c), res.isSuccess());
    }

    @Property
    public void testWs(char c) {
        final Result<Chr, Chr> res = Text.ws.parse(Input.of("" + c));
        assertEquals("ws parser applied to " + c, Character.isWhitespace(c), res.isSuccess());
    }

    @Property
    public void testIntr(int i) {
        {
            final Result<Chr, Integer> res = Text.intr.parse(Input.of("" + i));
            assertEquals("alpha parser applied to " + i, i, res.getOrThrow().intValue());
        }

        if (i > 0) {
            final Result<Chr, Integer> res = Text.uintr.parse(Input.of("" + i));
            assertEquals("alpha parser applied to " + i, i, res.getOrThrow().intValue());
        }
    }

    @Property
    public void testLng(long l) {
        {
            final Result<Chr, Long> res = Text.lng.parse(Input.of("" + l));
            assertEquals("alpha parser applied to " + l, l, res.getOrThrow().longValue());
        }

        if (l > 0) {
            final Result<Chr, Long> res = Text.ulng.parse(Input.of("" + l));
            assertEquals("alpha parser applied to " + l, l, res.getOrThrow().longValue());
        }
    }

    @Property
    public void testDbl(double d) {
        testDblImpl(d);
        testDblImpl(1.0/d);
    }

    @Property
    public void testDbl(long mi, long mf, boolean signB, byte exp) {
        final double sign = signB ? 1.0 : -1.0;
        final int mfd = 1 + (int) Math.log10(mf);
        final double d = ((double) mi + (double) mf / Math.pow(10.0, mfd)) * Math.pow(10.0, sign * exp);

        testDblImpl(d);

        if (Math.abs(d) > 1e-20) {
            testDblImpl(1.0 / d);
        }

        if (mf >= 0) {
            final String s = mi + "." + mf + "E" + exp;
            testDblImpl(s);
        }

    }

    private static void testDblImpl(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return;
        }

        final double eps = Math.abs(d * 1e-12);

        final String s = Double.toString(d);
        final Result<Chr, Double> res = Text.dble.apply(Input.of(s));

        assertTrue("Parsing double : " + s, res.isSuccess());

        assertEquals("Round-tripped double : " + d, d, res.getOrThrow(), eps);
    }

    private static void testDblImpl(String s) {
        final Result<Chr, Double> res = Text.dble.apply(Input.of(s));

        try {
            final double d = Double.parseDouble(s);
            assertTrue("Parsing double expected to succeed: " + s, res.isSuccess());

            final double eps = Math.abs(d * 1e-12);
            assertEquals("", d, res.getOrThrow(), eps);
        } catch (NumberFormatException ex) {
            assertFalse("Parsing double expected to fail: " + s, res.isSuccess());
        }
    }

    @Property
    public void testString(String s) {
        assumeFalse(s.isEmpty());

        final Parser<Chr, String> p = Text.string(s);
        final Result<Chr, String> r = p.parse(Input.of(s));
        assertEquals("string(" + s + ").parse(" + s + ")", s, r.getOrThrow());
    }
//
//    private static <T> Parser<Chr, T> manyTill(Parser<Chr, T> end) {
//
//    }
//
//    @Test
//    public void testBlockComment() {
//        final Parser<Chr, String> start = Text.string("/*");
//        final Parser<Chr, String> end = Text.string("*/");
//        final Result<Chr, Unit> p =
//                start
//                        .andR()
//
//
//    }
}
