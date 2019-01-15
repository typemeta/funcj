package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.control.Option.Kleisli;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.util.Functors;

import java.util.*;

import static org.junit.Assert.*;
import static org.typemeta.funcj.control.OptionTest.Utils.*;

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
    public void fold(char c) {
        assertTrue(Option.some(c).fold(() -> false, r -> true));
        assertFalse(Option.none().fold(() -> false, r -> true));
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

    @Test
    public void testSequenceList1() {
        final List<String> l = Arrays.asList("A", "B", "C");
        final List<Option<String>> le = Functors.map(Option::some, l);
        final Option<List<String>> result = Option.sequence(le);
        assertEquals(Option.some(l), result);
    }

    @Test
    public void testSequenceList2() {
        final List<Option<String>> l = new ArrayList<>();
        l.add(Option.some("A"));
        l.add(Option.none());
        l.add(Option.some("C"));

        final Option<List<String>> result = Option.sequence(l);
        assertEquals(Option.none(), result);
    }

    @Test
    public void testSequenceIList1() {
        final IList<String> l = IList.of("A", "B", "C");
        final IList<Option<String>> le = l.map(Option::some);
        final Option<IList<String>> result = Option.sequence(le);
        assertEquals(Option.some(l), result);
    }

    @Test
    public void testSequenceIList2() {
        final IList<Option<String>> le = IList.of(
                Option.some("A"),
                Option.none(),
                Option.some("C")
        );
        final Option<IList<String>> result = Option.sequence(le);
        assertEquals(Option.none(), result);
    }

    static class Utils {
        static final Kleisli<Integer, Integer> pure = Kleisli.of(Option::some);

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
