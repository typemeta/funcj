package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.typemeta.funcj.control.Option.Kleisli;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class OptionTest {

    @Property
    public void issome(char c) {
        assertTrue(Option.some(c).isPresent());
        assertFalse(Option.none().isPresent());
    }

    @Property
    public void asOptional(char c) {
        assertTrue(Option.some(c).asOptional().isPresent());
        assertFalse(Option.none().asOptional().isPresent());
    }

    @Property
    public void handle (char c) {
        Option.some(c).handle(() -> {throw new RuntimeException("Unexpected Option.none value");}, r -> {});
        Option.none().handle(() -> {}, r -> {throw new RuntimeException("Unexpected some value");});
    }

    @Property
    public void match(char c) {
        assertTrue(Option.some(c).match(() -> false, r -> true));
        assertFalse(Option.none().match(() -> false, r -> true));
    }

    @Property
    public void map(char c) {
        assertEquals(Option.some(String.valueOf(c)), Option.some(c).map(Object::toString));
        assertEquals(Option.none(), Option.none().map(Object::toString));
    }

    @Property
    public void apply(char c) {
        assertEquals(Option.some(String.valueOf(c)), Option.some(c).apply(Option.some(Object::toString)));
        assertEquals(Option.none(), Option.some(c).apply(Option.none()));
        assertEquals(Option.none(), Option.none().apply(Option.some(Object::toString)));
    }

    @Property
    public void flatMap(char c) {
        final char e = c == 'X' ? 'x' : 'X';
        final String cs = String.valueOf(c);
        Assert.assertEquals(Option.some(e), Option.some(c).flatMap(d -> Option.some(e)));
        Assert.assertEquals(Option.none(), Option.some(c).flatMap(d -> Option.none()));
        Assert.assertEquals(Option.none(), Option.none().flatMap(d -> Option.some(e)));
        Assert.assertEquals(Option.none(), Option.none().flatMap(d -> Option.none()));
    }

    static class Utils {
        static final Kleisli<Integer, Integer> isPositive = i ->
                (i >= 0) ?
                        Option.some(i) :
                        Option.none();

        static final Kleisli<Integer, Double> isEven = i ->
                (i % 2 == 0) ?
                        Option.some((double)i) :
                        Option.none();

        static final Kleisli<Double, String> upToFirstZero = d -> {
            final String s = Double.toString(d);
            final int i = s.indexOf('0');
            if (i != -1) {
                return Option.some(s.substring(0, i));
            } else {
                return Option.none();
            }
        };

        static final Kleisli<Integer, String> f =
                (isPositive.andThen(isEven)).andThen(upToFirstZero);

        static final Kleisli<Integer, String> g =
                isPositive.andThen(isEven.andThen(upToFirstZero));
    }

    @Property
    public void kleisliIsAssociative(int i) {
        final Option<String> rf = Utils.f.apply(i);
        final Option<String> rg = Utils.g.apply(i);
        assertEquals("", rf, rg);
    }
}