package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.StateR.*;
import static org.typemeta.funcj.control.StateR.pure;
import static org.typemeta.funcj.control.StateRTest.Utils.*;
import static org.typemeta.funcj.control.StateRTest.Utils.pure;

@RunWith(JUnitQuickcheck.class)
public class StateRTest {
    @Property
    public void testStateR(String a, String b, String c) {
        final String r =
                put(a).flatMap(u -> get())
                        .flatMap(s -> put(s + b))
                        .flatMap(u -> get())
                        .eval(c);

        assertEquals(a+b, r);
    }

    @Property
    public void testStateR2(String a, String b) {
        final String r =
                StateR.<String>get()
                        .flatMap(s -> put(s + a))
                        .flatMap(u -> get())
                        .eval(b);
        assertEquals(b+a, r);
    }


    @Test
    public void testSequenceStream() {
        final StateR<String, Unit> addA = StateR.modify(s -> s + "A");
        final StateR<String, Unit> addB = StateR.modify(s -> s + "B");
        final StateR<String, Unit> addC = StateR.modify(s -> s + "C");

        final List<StateR<String, Unit>> l = new ArrayList<>();
        l.add(StateR.modify(s -> s + "A"));
        l.add(StateR.modify(s -> s + "B"));
        l.add(StateR.modify(s -> s + "C"));

        final String result = StateR.sequence(l.stream()).exec("X");

        assertEquals("XABC", result);
    }

    @Test
    public void testSequenceIList() {
        IList<StateR<String, Unit>> l = IList.nil();
        l = l.add(StateR.modify(s -> s + "C"));
        l = l.add(StateR.modify(s -> s + "B"));
        l = l.add(StateR.modify(s -> s + "A"));

        final String result = StateR.sequence(l.stream()).exec("X");

        assertEquals("XABC", result);
    }

    @Test
    public void testTraverseIList() {
        final IList<String> l = IList.of("A", "B", "C");

        final String result =
                StateR.traverse(l, x -> StateR.modify((String s) -> s + x))
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
                StateR.traverse(l, x -> StateR.modify((String s) -> s + x))
                        .exec("X");

        assertEquals("XABC", result);
    }

    static class Utils {
        static final Kleisli<Double, Double, Double> pure = Kleisli.of(StateR::pure);

        static final Kleisli<Double, Double, Double> add = d ->
                StateR.<Double>get().flatMap(x -> put(x + d)).flatMap(u -> pure(d));

        static final Kleisli<Double, Double, Double> sub = d ->
                StateR.<Double>get().flatMap(x -> put(x - d)).flatMap(u -> pure(d));

        static final Kleisli<Double, Double, Double> div = d ->
                StateR.<Double>get().flatMap(x -> put(x / d)).flatMap(u -> pure(d));

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
