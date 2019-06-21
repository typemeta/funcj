package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.kleisli.StateRK;
import org.typemeta.funcj.tuples.Tuple2;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.StateR.pure;
import static org.typemeta.funcj.control.StateRTest.Utils.add;
import static org.typemeta.funcj.control.StateRTest.Utils.check;
import static org.typemeta.funcj.control.StateRTest.Utils.div;
import static org.typemeta.funcj.control.StateRTest.Utils.pure;
import static org.typemeta.funcj.control.StateRTest.Utils.sub;

@RunWith(JUnitQuickcheck.class)
public class StateRTest {
    @Property
    public void testStateR(String a, String b, String c) {
        final String r =
                StateR.put(a).flatMap(u -> StateR.get())
                        .flatMap(s -> StateR.put(s + b))
                        .flatMap(u -> StateR.get())
                        .eval(c);

        assertEquals(a+b, r);
    }

    @Property
    public void testStateR2(String a, String b) {
        final String r =
                StateR.<String>get()
                        .flatMap(s -> StateR.put(s + a))
                        .flatMap(u -> StateR.get())
                        .eval(b);
        assertEquals(b+a, r);
    }

    static <S> StateR<S, S> modify(Functions.F<S, S> f) {
        return StateR.<S>get()
                .flatMap(s -> StateR.put(
                        f.apply(s)
                ).map(u -> s));
    }

    @Test
    public void testSequenceList() {
        final List<StateR<String, String>> l = new ArrayList<>();
        l.add(modify(s -> s + "A"));
        l.add(modify(s -> s + "B"));
        l.add(modify(s -> s + "C"));

        final Tuple2<String, List<String>> result = StateR.sequence(l).runState("X");

        assertEquals("XABC", result._1);
        assertEquals(Arrays.asList("X", "XA", "XAB"), result._2);
    }

    @Test
    public void testSequenceIList() {
        final IList<StateR<String, String>> l = IList.of(
                modify(s -> s + "A"),
                modify(s -> s + "B"),
                modify(s -> s + "C")
        );

        final Tuple2<String, IList<String>> result = StateR.sequence(l).runState("X");

        assertEquals("XABC", result._1);
        assertEquals(IList.of("X", "XA", "XAB"), result._2);
    }

    @Test
    public void testTraverseIList() {
        final IList<String> l = IList.of("A", "B", "C");

        final Tuple2<String, IList<String>> result =
                StateR.traverse(l, x -> modify((String s) -> s + x))
                        .runState("X");

        assertEquals("XABC", result._1);
        assertEquals(IList.of("X", "XA", "XAB"), result._2);
    }

    @Test
    public void testTraverseList() {
        final List<String> l = new ArrayList<>();
        l.add("A");
        l.add("B");
        l.add("C");

        final Tuple2<String, List<String>> result =
                StateR.traverse(l, x -> modify((String s) -> s + x))
                        .runState("X");

        assertEquals("XABC", result._1);
        assertEquals(Arrays.asList("X", "XA", "XAB"), result._2);
    }

    static class Utils {
        static final StateRK<Double, Double, Double> pure = StateRK.of(StateR::pure);

        static final StateRK<Double, Double, Double> add = d ->
                StateR.<Double>get().flatMap(x -> StateR.put(x + d)).flatMap(u -> pure(d));

        static final StateRK<Double, Double, Double> sub = d ->
                StateR.<Double>get().flatMap(x -> StateR.put(x - d)).flatMap(u -> pure(d));

        static final StateRK<Double, Double, Double> div = d ->
                StateR.<Double>get().flatMap(x -> StateR.put(x / d)).flatMap(u -> pure(d));

        static final double EPSILON = 1e-32;
        static final double INIT = 12.34;

        static void check(
                String msg,
                double d,
                StateRK<Double, Double, Double> lhs,
                StateRK<Double, Double, Double> rhs) {
            assertEquals(
                    msg,
                    lhs.apply(d).exec(INIT),
                    rhs.apply(d).exec(INIT),
                    EPSILON);
        }
    }

    @Property
    public void kleisliLeftIdentity(double i) {
        check("StateR.Kleisli Left-identity 1", i, pure.andThen(add), add);
        check("StateR.Kleisli Left-identity 2", i, pure.andThen(div), div);
    }

    @Property
    public void kleisliRightIdentity(double i) {
        check("StateR.Kleisli Right-identity 1", i, add.andThen(pure), add);
        check("StateR.Kleisli Right-identity 2", i, div.andThen(pure), div);
    }

    @Property
    public void kleisliIsAssociative(double i) {
        check("StateR.Kleisli Associativity", i, (add.andThen(div)).andThen(sub), add.andThen(div.andThen(sub)));
    }
}
