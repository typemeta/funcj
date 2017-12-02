package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assert;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.StateR.*;
import static org.typemeta.funcj.control.StateR.pure;
import static org.typemeta.funcj.control.StateRTest.Utils.*;
import static org.typemeta.funcj.control.StateRTest.Utils.pure;

@RunWith(JUnitQuickcheck.class)
public class StateRTest {
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
                StateR.<String>get()
                        .flatMap(s -> put(s + a))
                        .flatMap(u -> get())
                        .eval(b);
        assertEquals(b+a, r);
    }


    static class Utils {
        static final Kleisli<Double, Double, Double> pure = Kleisli.of(StateR::pure);

        static final Kleisli<Double, Double, Double> add = d ->
                StateR.<Double>get().flatMap(x -> put(x + d)).flatMap(u -> pure(d));

        static final Kleisli<Double, Double, Double> sub = d ->
                StateR.<Double>get().flatMap(x -> put(x - d)).flatMap(u -> pure(d));

        static final Kleisli<Double, Double, Double> div = d ->
                StateR.<Double>get().flatMap(x -> put(x / d)).flatMap(u -> pure(d));
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
