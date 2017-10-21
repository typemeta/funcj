package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.Unit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class ValidationTest {
    private static Validation<String, Double> parseDbl(String s) {
        return Validation.of(
            () -> Double.parseDouble(s),
            Exception::getMessage
        );
    }

    private static Validation<String, Integer> parseInt(String s) {
        return Validation.of(
            () -> Integer.parseInt(s),
            Exception::getMessage
        );
    }

    @Test
    public void testAndMapGoodGood() {
        final Validation<String, Double> r =
            parseInt("1234")
                .and(parseDbl("1.2"))
                .map(i -> d -> i+d);
        Assert.assertTrue("Validation is success", r.isSuccess());
    }

    @Test
    public void testAndMapGoodBad() {
        final Validation<String, Double> r =
            parseInt("1234")
                .and(parseDbl("1.2z"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validation is failure", r.isSuccess());
    }

    @Test
    public void testAndMapBadGood() {
        final Validation<String, Double> r =
            parseInt("1234z")
                .and(parseDbl("1.2"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validation is failure", r.isSuccess());
    }

    @Test
    public void testAndMapBadBad() {
        final Validation<String, Double> r =
            parseInt("1234z")
                .and(parseDbl("1.2z"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validation is failure", r.isSuccess());
    }

    private static <String, T> Validation<String, T> failure(String msg) {
        return Validation.failure(msg);
    }

    @Property
    public void isSuccess(char c) {
        assertTrue(Validation.success(c).isSuccess());
        assertFalse(failure("fail").isSuccess());
    }

    @Property
    public void asOptional(char c) {
        assertTrue(Validation.success(c).asOptional().isPresent());
        assertFalse(failure("fail").asOptional().isPresent());
    }

    @Property
    public void handle (char c) {
        Validation.success(c).handle(l -> {throw new RuntimeException("Unexpected failure value");}, r -> {});
        failure("fail").handle(l -> {}, r -> {throw new RuntimeException("Unexpected success value");});
    }

    @Property
    public void match(char c) {
        assertTrue(Validation.success(c).match(l -> false, r -> true));
        assertFalse(failure("fail").match(l -> false, r -> true));
    }

    @Property
    public void map(char c) {
        assertEquals(Validation.success(String.valueOf(c)), Validation.success(c).map(Object::toString));
        assertEquals(failure("fail"), failure("fail").map(Object::toString));
    }

    @Property
    public void apply(char c) {
        assertEquals(Validation.success(String.valueOf(c)), Validation.success(c).apply(Validation.success(Object::toString)));
        assertEquals(failure("fail"), Validation.success(c).apply(failure("fail")));
        assertEquals(failure("fail"), failure("fail").apply(Validation.success(Object::toString)));
    }

    @Property
    public void flatMap(char c) {
        final char e = c == 'X' ? 'x' : 'X';
        final String cs = String.valueOf(c);
        assertEquals(Validation.success(e), Validation.success(c).flatMap(d -> Validation.success(e)));
        assertEquals(failure(cs), Validation.success(c).flatMap(d -> failure(cs)));
        assertEquals(failure(cs), failure(cs).flatMap(d -> Validation.success(e)));
        assertEquals(failure(cs), failure(cs).flatMap(d -> failure("error")));
    }

    static class Utils {
        private static Validation<Unit, Integer> parse(String s) {
            try {
                return Validation.success(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                return Validation.failure(Unit.UNIT);
            }
        }

        private static Validation<Unit, Double> sqrt(int d) {
            final double x = Math.sqrt(d);
            if (Double.isNaN(x)) {
                return Validation.failure(Unit.UNIT);
            } else {
                return Validation.success(x);
            }
        }
    }

    @Property
    public void kleisli(int i) {
        final String s = Integer.toString(i);
        final Validation.Kleisli<Unit, String, Double> tk = Validation.Kleisli.of(Utils::parse).andThen(Utils::sqrt);
        final Validation<Unit, Double> td = tk.run(s);
        final Validation<Unit, Double> expected =
                (i >= 0) ?
                        Validation.success(Math.sqrt(i)) :
                        Validation.failure(Unit.UNIT);
        assertEquals(expected, td);
    }
}
