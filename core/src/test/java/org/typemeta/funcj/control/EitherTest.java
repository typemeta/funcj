package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.kleisli.EitherK;
import org.typemeta.funcj.util.Functors;

import java.util.*;

import static org.junit.Assert.*;
import static org.typemeta.funcj.control.EitherTest.Utils.*;

@RunWith(JUnitQuickcheck.class)
public class EitherTest {

    @Property
    public void isRight(char c) {
        assertTrue(Either.right(c).isRight());
        assertFalse(Either.left(c).isRight());
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
    public void fold(char c) {
        assertTrue(Either.right(c).fold(l -> false, r -> true));
        assertFalse(Either.left(c).fold(l -> false, r -> true));
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
        assertEquals(Either.right(String.valueOf(c)), Either.right(c).app(Either.right(Object::toString)));
        assertEquals(Either.left("fail"), Either.right(c).app(Either.left("fail")));
        assertEquals(Either.left(c), Either.left(c).app(Either.right(Object::toString)));
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

    @Test
    public void testSequenceList1() {
        final List<String> l = Arrays.asList("A", "B", "C");
        final List<Either<Integer, String>> le = Functors.map(Either::right, l);
        final Either<Integer, List<String>> result = Either.sequence(le);
        assertEquals(Either.right(l), result);
    }

    @Test
    public void testSequenceList2() {
        final List<Either<Integer, String>> l = new ArrayList<>();
        l.add(Either.right("A"));
        l.add(Either.left(1));
        l.add(Either.right("C"));

        final Either<Integer, List<String>> result = Either.sequence(l);

        assertEquals(Either.left(1), result);
    }

    @Test
    public void testSequenceIList1() {
        final IList<String> l = IList.of("A", "B", "C");
        final IList<Either<Integer, String>> le = l.map(Either::right);
        final Either<Integer, IList<String>> result = Either.sequence(le);
        assertEquals(Either.right(l), result);
    }

    @Test
    public void testSequenceIList2() {
        final IList<Either<Integer, String>> le = IList.of(
                Either.right("A"),
                Either.left(1),
                Either.right("C")
        );

        final Either<Integer, IList<String>> result = Either.sequence(le);
        assertEquals(Either.left(1), result);
    }

    static class Utils {
        static final EitherK<Error, Integer, Integer> pure = EitherK.of(Either::right);

        static final EitherK<Error, Integer, Integer> isPositive = i ->
                (i >= 0) ?
                        Either.right(i) :
                        Either.left(new Error("Negative value"));

        static final EitherK<Error, Integer, Double> isEven = i ->
            (i % 2 == 0) ?
                Either.right((double)i) :
                Either.left(new Error("Odd value"));

        static final EitherK<Error, Double, String> upToFirstZero = d -> {
            final String s = Double.toString(d);
            final int i = s.indexOf('0');
            if (i != -1) {
                return Either.right(s.substring(0, i));
            } else {
                return Either.left(new Error("Negative value"));
            }
        };

        static <T> void check(
                String msg,
                int i,
                EitherK<Error, Integer, T> lhs,
                EitherK<Error, Integer, T> rhs) {
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
