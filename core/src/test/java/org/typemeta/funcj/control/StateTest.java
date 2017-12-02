package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assert;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.State.*;
import static org.typemeta.funcj.control.StateTest.Utils.*;

@RunWith(JUnitQuickcheck.class)
public class StateTest {
    @Property
    public void testState(String a, String b, String c) {
        final String r =
                put(a).flatMap(u -> get())
                        .flatMap(s -> put(s + b))
                        .flatMap(u -> get())
                        .eval(c);

        assertEquals(a+b, r);
    }

    @Property
    public void testState2(String a, String b) {
        final String r =
                State.<String>get()
                        .flatMap(s -> put(s + a))
                        .flatMap(u -> get())
                        .eval(b);
        assertEquals(b+a, r);
    }

    static class Utils {
        static final Kleisli<Double, Double, Double> pure = Kleisli.of(State::pure);

        static final Kleisli<Double, Double, Double> add = i ->
                State.<Double>get().flatMap(x -> put(x + i)).flatMap(u -> get());

        static final Kleisli<Double, Double, Double> sub = i ->
            State.<Double>get().flatMap(x -> put(x - i)).flatMap(u -> get());

        static final Kleisli<Double, Double, Double> div = i ->
                State.<Double>get().flatMap(x -> put(x / i)).flatMap(u -> get());
    }

    private static final double EPSILON = 1e-32;
    private static final double INIT = 12.34;

    private static void check(
            String msg,
            double d,
            Kleisli<Double, Double, Double> lhs,
            Kleisli<Double, Double, Double> rhs) {
        Assert.assertEquals(
                msg,
                lhs.apply(d).exec(INIT),
                rhs.apply(d).exec(INIT),
                EPSILON);
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
