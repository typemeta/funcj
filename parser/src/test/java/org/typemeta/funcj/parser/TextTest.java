package org.typemeta.funcj.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.Chr;

import java.io.StringReader;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class TextTest {
    private static <T> void parseSuccess(Parser<Chr, T> parser, String s, T exp) {
        Assert.assertEquals(exp, parseSuccess(parser, Input.of(s)));
        Assert.assertEquals(exp, parseSuccess(parser, Input.of(new StringReader(s))));
    }

    private static <T> T parseSuccess(Parser<Chr, T> parser, Input<Chr> in) {
        return parser.parse(in).getOrThrow();
    }

    private static <T> void parseFailure(Parser<Chr, T> parser, String s) {
        assertFalse(parse(parser, Input.of(s)).isSuccess());
        assertFalse(parse(parser, Input.of(new StringReader(s))).isSuccess());
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

    @Property
    public void testDbl(double d) {
        testDblImpl(d);
        testDblImpl(1.0/d);
    }

    @Property
    public void testDbl(long mi, long mf, boolean signB, byte exp) {
        final double sign = signB ? 1.0 : -1.0;
        final int mfd = 1 +(int)Math.log10(mf);
        final double d = ((double)mi + (double)mf / Math.pow(10.0, mfd)) * Math.pow(10.0, sign * exp);

        testDblImpl(d);

        if ( Math.abs(d) > 1e-20) {
            testDblImpl(1.0 / d);
        }

        final String s = mi + "." + mf + "E" + (signB ? '+' : '-') + exp;
        testDblImpl(s);

    }

    private static void testDblImpl(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return;
        }

        final double eps = Math.abs(d * 1e-12);

        final String s = Double.toString(d);
        final Result<Chr, Double> res = Text.dble.parse(Input.of(s));

        assertTrue("Parsing double : " + s, res.isSuccess());

        assertEquals("Round-tripped double : " + d, d, res.getOrThrow().doubleValue(), eps);
    }

    private static void testDblImpl(String s) {
        final Result<Chr, Double> res = Text.dble.parse(Input.of(s));

        try {
            final double d = Double.parseDouble(s);
            assertTrue("Parsing double expected to succeed: " + s, res.isSuccess());

            final double eps = Math.abs(d * 1e-12);
            assertEquals("", d, res.getOrThrow(), eps);
        } catch (NumberFormatException ex) {
            assertFalse("Parsing double expected to fail: " + s, res.isSuccess());
        }
    }
}
