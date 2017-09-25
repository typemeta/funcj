package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class EitherPropTest {

    @Property
    public void isRight(char c) {
        assertTrue(Either.right(c).isRight());
        assertFalse(Either.left(c).isRight());
    }

    @Property
    public void asOptional(char c) {
        assertTrue(Either.right(c).asOptional().isPresent());
        assertFalse(Either.left(c).asOptional().isPresent());
    }

    @Property
    public void handle (char c) {
        Either.right(c).handle(l -> {throw new RuntimeException("Unexpected left value");}, r -> {});
        Either.left(c).handle(l -> {}, r -> {throw new RuntimeException("Unexpected right value");});
    }

    @Property
    public void match(char c) {
        assertTrue(Either.right(c).match(l -> false, r -> true));
        assertFalse(Either.left(c).match(l -> false, r -> true));
    }

    @Property
    public void map(char c) {
        assertEquals(Either.right(String.valueOf(c)), Either.right(c).map(Object::toString));
        assertEquals(Either.left(c), Either.left(c).map(Object::toString));
    }

    @Property
    public void mapLeft(char c) {
        assertEquals(Either.right(c), Either.right(c).mapLeft(Object::toString));
        assertEquals(Either.left(String.valueOf(c)), Either.left(c).mapLeft(Object::toString));
    }

    @Property
    public void apply(char c) {
        assertEquals(Either.right(String.valueOf(c)), Either.right(c).apply(Either.right(Object::toString)));
        assertEquals(Either.left("fail"), Either.right(c).apply(Either.left("fail")));
        assertEquals(Either.left(c), Either.left(c).apply(Either.right(Object::toString)));
    }

    @Property
    public void flatMap(char c) {
        final char e = c == 'X' ? 'x' : 'X';
        final String cs = String.valueOf(c);
        assertEquals(Either.right(e), Either.right(c).flatMap(d -> Either.right(e)));
        assertEquals(Either.left(cs), Either.right(c).flatMap(d -> Either.left(cs)));
        assertEquals(Either.left(cs), Either.left(cs).flatMap(d -> Either.right(e)));
        assertEquals(Either.left(cs), Either.left(cs).flatMap(d -> Either.left("error")));
    }
}