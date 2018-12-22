package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.State.*;
import static org.typemeta.funcj.control.StateTest.Utils.*;
import static org.typemeta.funcj.control.StateTest.Utils.pure;

@RunWith(JUnitQuickcheck.class)
public class StateTest {
    @Property
    public void testState(String a, String b, String c) {
        final State<String, String> state =
                put(a).flatMap(u -> get())
                        .flatMap(s -> put(s + b))
                        .flatMap(u -> get());
        final String r = state.eval(c);

        assertEquals(a+b, r);
    }

    @Property
    public void testState2(String a, String b) {
        final State<String, String> state =
                State.<String>get()
                        .flatMap(s -> put(s + a))
                        .flatMap(u -> get());
        final String r = state.eval(b);

        assertEquals(b+a, r);
    }

    @Test
    public void testSequenceStream() {
        final State<String, Unit> addA = State.modify(s -> s + "A");
        final State<String, Unit> addB = State.modify(s -> s + "B");
        final State<String, Unit> addC = State.modify(s -> s + "C");

        final List<State<String, Unit>> l = new ArrayList<>();
        l.add(State.modify(s -> s + "A"));
        l.add(State.modify(s -> s + "B"));
        l.add(State.modify(s -> s + "C"));

        final String result = State.sequence(l.stream()).exec("X");

        assertEquals("XABC", result);
    }

    @Test
    public void testSequenceIList() {
        IList<State<String, Unit>> l = IList.nil();
        l = l.add(State.modify(s -> s + "C"));
        l = l.add(State.modify(s -> s + "B"));
        l = l.add(State.modify(s -> s + "A"));

        final String result = State.sequence(l.stream()).exec("X");

        assertEquals("XABC", result);
    }

    @Test
    public void testTraverseIList() {
        final IList<String> l = IList.of("A", "B", "C");

        final String result =
                State.traverse(l, x -> State.modify((String s) -> s + x))
                        .exec("X");

        assertEquals("XABC", result);
    }

    @Test
    public void testTraverseList() {
        final List<String> l = new ArrayList<>();
        l.add("A");
        l.add("B");
        l.add("C");

        final String result =
                State.traverse(l, x -> State.modify((String s) -> s + x))
                        .exec("X");

        assertEquals("XABC", result);
    }

    static class Utils {
        static final Kleisli<Double, Double, Double> pure = Kleisli.of(State::pure);

        static final Kleisli<Double, Double, Double> add = i ->
                State.<Double>get().flatMap(x -> put(x + i)).flatMap(u -> get());

        static final Kleisli<Double, Double, Double> sub = i ->
                State.<Double>get().flatMap(x -> put(x - i)).flatMap(u -> get());

        static final Kleisli<Double, Double, Double> div = i ->
                State.<Double>get().flatMap(x -> put(x / i)).flatMap(u -> get());

        static final double EPSILON = 1e-32;
        static final double INIT = 12.34;

        static void check(
                String msg,
                double d,
                Kleisli<Double, Double, Double> lhs,
                Kleisli<Double, Double, Double> rhs) {
            assertEquals(
                    msg,
                    lhs.apply(d).exec(INIT),
                    rhs.apply(d).exec(INIT),
                    EPSILON);
        }
    }

    @Property
    public void kleisliLeftIdentity(double i) {
        check("Kleisli Left-identity 1", i, pure.andThen(add), add);
        check("Kleisli Left-identity 2", i, pure.andThen(div), div);
    }

    @Property
    public void kleisliRightIdentity(double i) {
        check("Kleisli Right-identity 1", i, add.andThen(pure), add);
        check("Kleisli Right-identity 2", i, div.andThen(pure), div);
    }

    @Property
    public void kleisliIsAssociative(double i) {
        check("Kleisli Associativity", i, (add.andThen(div)).andThen(sub), add.andThen(div.andThen(sub)));
    }
}
