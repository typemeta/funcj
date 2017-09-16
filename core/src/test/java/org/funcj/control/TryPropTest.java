package org.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class TryPropTest {

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
}