package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.kleisli.StateK;
import org.typemeta.funcj.tuples.Tuple2;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.State.pure;
import static org.typemeta.funcj.control.StateTest.Utils.add;
import static org.typemeta.funcj.control.StateTest.Utils.check;
import static org.typemeta.funcj.control.StateTest.Utils.div;
import static org.typemeta.funcj.control.StateTest.Utils.pure;
import static org.typemeta.funcj.control.StateTest.Utils.sub;

@RunWith(JUnitQuickcheck.class)
public class StateTest {
    @Property
    public void testState(String a, String b, String c) {
        final String r =
                State.put(a).flatMap(u -> State.get())
                        .flatMap(s -> State.put(s + b))
                        .flatMap(u -> State.get())
                        .eval(c);

        assertEquals(a+b, r);
    }

    @Property
    public void testState2(String a, String b) {
        final String r =
                State.<String>get()
                        .flatMap(s -> State.put(s + a))
                        .flatMap(u -> State.get())
                        .eval(b);
        assertEquals(b+a, r);
    }

    static <S> State<S, S> modify(Functions.F<S, S> f) {
        return State.<S>get().flatMap(s -> State.put(f.apply(s)).map(u -> s));
    }

    @Test
    public void testSequenceList() {
        final List<State<String, String>> l = new ArrayList<>();
        l.add(modify(s -> s + "A"));
        l.add(modify(s -> s + "B"));
        l.add(modify(s -> s + "C"));

        final Tuple2<String, List<String>> result = State.sequence(l).runState("X").runT();

        assertEquals("XABC", result._1);
        assertEquals(Arrays.asList("X", "XA", "XAB"), result._2);
    }

    @Test
    public void testSequenceIList() {
        final IList<State<String, String>> l = IList.of(
                modify(s -> s + "A"),
                modify(s -> s + "B"),
                modify(s -> s + "C")
        );

        final Tuple2<String, IList<String>> result = State.sequence(l).runState("X").runT();

        assertEquals("XABC", result._1);
        assertEquals(IList.of("X", "XA", "XAB"), result._2);
    }

    @Test
    public void testTraverseIList() {
        final IList<String> l = IList.of("A", "B", "C");

        final Tuple2<String, IList<String>> result =
                State.traverse(l, x -> modify((String s) -> s + x))
                        .runState("X").runT();

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
                State.traverse(l, x -> modify((String s) -> s + x))
                        .runState("X").runT();

        assertEquals("XABC", result._1);
        assertEquals(Arrays.asList("X", "XA", "XAB"), result._2);
    }

    static class Utils {
        static final StateK<Double, Double, Double> pure = StateK.of(State::pure);

        static final StateK<Double, Double, Double> add = d ->
                State.<Double>get().flatMap(x -> State.put(x + d)).flatMap(u -> pure(d));

        static final StateK<Double, Double, Double> sub = d ->
                State.<Double>get().flatMap(x -> State.put(x - d)).flatMap(u -> pure(d));

        static final StateK<Double, Double, Double> div = d ->
                State.<Double>get().flatMap(x -> State.put(x / d)).flatMap(u -> pure(d));

        static final double EPSILON = 1e-32;
        static final double INIT = 12.34;

        static void check(
                String msg,
                double d,
                StateK<Double, Double, Double> lhs,
                StateK<Double, Double, Double> rhs
        ) {
            assertEquals(
                    msg,
                    lhs.apply(d).exec(INIT),
                    rhs.apply(d).exec(INIT),
                    EPSILON);
        }
    }

    @Property
    public void kleisliLeftIdentity(double i) {
        check("State.Kleisli Left-identity 1", i, pure.andThen(add), add);
        check("State.Kleisli Left-identity 2", i, pure.andThen(div), div);
    }

    @Property
    public void kleisliRightIdentity(double i) {
        check("State.Kleisli Right-identity 1", i, add.andThen(pure), add);
        check("State.Kleisli Right-identity 2", i, div.andThen(pure), div);
    }

    @Property
    public void kleisliIsAssociative(double i) {
        check("State.Kleisli Associativity", i, add.andThen(div).andThen(sub), add.andThen(div.andThen(sub)));
    }
}
