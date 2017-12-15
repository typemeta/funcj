package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.typemeta.funcj.control.Try.Kleisli;

import static org.junit.Assert.*;
import static org.typemeta.funcj.control.TryTest.Utils.*;

@RunWith(JUnitQuickcheck.class)
public class TryTest {

    private static <T> Try<T> failure(String msg) {
        return Try.failure(new RuntimeException(msg));
    }

    @Property
    public void isSuccess(char c) {
        assertTrue(Try.success(c).isSuccess());
        assertFalse(failure("fail").isSuccess());
    }

    @Property
    public void asOptional(char c) {
        assertTrue(Try.success(c).asOptional().isPresent());
        assertFalse(failure("fail").asOptional().isPresent());
    }

    @Property
    public void handle (char c) {
        Try.success(c).handle(l -> {throw new RuntimeException("Unexpected failure value");}, r -> {});
        failure("fail").handle(l -> {}, r -> {throw new RuntimeException("Unexpected success value");});
    }

    @Property
    public void match(char c) {
        assertTrue(Try.success(c).match(l -> false, r -> true));
        assertFalse(failure("fail").match(l -> false, r -> true));
    }

    @Property
    public void map(char c) {
        assertEquals(Try.success(String.valueOf(c)), Try.success(c).map(Object::toString));
        assertEquals(failure("fail"), failure("fail").map(Object::toString));
    }

    @Property
    public void apply(char c) {
        assertEquals(Try.success(String.valueOf(c)), Try.success(c).apply(Try.success(Object::toString)));
        assertEquals(failure("fail"), Try.success(c).apply(failure("fail")));
        assertEquals(failure("fail"), failure("fail").apply(Try.success(Object::toString)));
    }

    @Property
    public void flatMap(char c) {
        final char e = c == 'X' ? 'x' : 'X';
        final String cs = String.valueOf(c);
        assertEquals(Try.success(e), Try.success(c).flatMap(d -> Try.success(e)));
        assertEquals(failure(cs), Try.success(c).flatMap(d -> failure(cs)));
        assertEquals(failure(cs), failure(cs).flatMap(d -> Try.success(e)));
        assertEquals(failure(cs), failure(cs).flatMap(d -> failure("error")));
    }

    private static final Try<Integer> fail = Try.failure(new Exception(""));

    @Property
    public void andMap(int a, int b) {
        final Try<Integer> tiA = Try.success(a);
        final Try<Integer> tiB = Try.success(b);

        assertEquals("", Try.success(a+b), tiA.and(tiB).map(iA -> iB -> iA + iB));
        assertEquals("", fail, fail.and(tiB).map(iA -> iB -> iA + iB));
        assertEquals("", fail, tiA.and(fail).map(iA -> iB -> iA + iB));
    }

    static class Utils {
        static final Kleisli<Integer, Integer> pure = Kleisli.of(Try::success);

        static final Kleisli<Integer, Integer> isPositive = i ->
                (i >= 0) ?
                        Try.success(i) :
                        Try.failure(new Error("Negative value"));

        static final Kleisli<Integer, Double> isEven = i ->
                (i % 2 == 0) ?
                        Try.success((double)i) :
                        Try.failure(new Error("Odd value"));

        static final Kleisli<Double, String> upToFirstZero = d -> {
            final String s = Double.toString(d);
            final int i = s.indexOf('0');
            if (i != -1) {
                return Try.success(s.substring(0, i));
            } else {
                return Try.failure(new Error("Negative value"));
            }
        };

        static <T> void check(
                String msg,
                int i,
                Kleisli<Integer, T> lhs,
                Kleisli<Integer, T> rhs) {
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
