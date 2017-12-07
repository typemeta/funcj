package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;
import org.typemeta.funcj.control.Validation.Kleisli;

import static org.junit.Assert.*;
import static org.typemeta.funcj.control.ValidationTest.Utils.*;

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
        static final Kleisli<Error, Integer, Integer> pure = Kleisli.of(Validation::success);

        static final Kleisli<Error, Integer, Integer> isPositive = i ->
                (i >= 0) ?
                        Validation.success(i) :
                        Validation.failure(new Error("Negative value"));

        static final Kleisli<Error, Integer, Double> isEven = i ->
                (i % 2 == 0) ?
                        Validation.success((double)i) :
                        Validation.failure(new Error("Odd value"));

        static final Kleisli<Error, Double, String> upToFirstZero = d -> {
            final String s = Double.toString(d);
            final int i = s.indexOf('0');
            if (i != -1) {
                return Validation.success(s.substring(0, i));
            } else {
                return Validation.failure(new Error("Negative value"));
            }
        };

        static <T> void check(
                String msg,
                int i,
                Kleisli<Error, Integer, T> lhs,
                Kleisli<Error, Integer, T> rhs) {
            assertEquals(
                    msg,
                    lhs.apply(i),
                    rhs.apply(i));
        }
    }

    @Property
    public void kleisliLeftIdentity(int i) {
        check("Kleisli Left-identity", i, pure.andThen(isPositive), isPositive);
    }

    @Property
    public void kleisliRightIdentity(int i) {
        check("Kleisli Right-identity", i, isPositive.andThen(pure), isPositive);
    }

    @Property
    public void kleisliIsAssociative(int i) {
        check(
                "Kleisli Associativity",
                i,
                (isPositive.andThen(isEven)).andThen(upToFirstZero),
                isPositive.andThen(isEven.andThen(upToFirstZero)));
    }
}
