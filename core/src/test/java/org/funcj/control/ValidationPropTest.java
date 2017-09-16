package org.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class ValidationPropTest {

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
}