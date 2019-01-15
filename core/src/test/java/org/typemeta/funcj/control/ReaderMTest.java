package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.control.ReaderM.Kleisli;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.functions.Functions.F2;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.ReaderM.pure;
import static org.typemeta.funcj.functions.Functions.F.konst;

@RunWith(JUnitQuickcheck.class)
public class ReaderMTest {

    private static final F<Integer, Integer> add10 = x -> x + 10;
    private static final F<Integer, Integer> times2 = x -> x * 2;

    @Property
    public void flatMap(int i) {
        final F<Integer, Integer> addTimes = add10.flatMap(a -> times2.flatMap(b -> konst(a + b)));
        final int exp = i*2 + i+10;
        assertEquals("add combined with times", exp, addTimes.apply(i).intValue());
    }

    private static final Kleisli<Integer, Integer, Integer> kPure = Kleisli.of(ReaderM::pure);
    private static final Kleisli<Integer, Integer, Integer> kA = x -> y -> x + y;
    private static final Kleisli<Integer, Integer, Integer> kB = x -> y -> x * y;
    private static final Kleisli<Integer, Integer, Integer> kC = x -> y -> x - y;

    private static <T> void check(
            String msg,
            int i,
            int j,
            Kleisli<Integer, Integer, T> lhs,
            Kleisli<Integer, Integer, T> rhs) {
        assertEquals(
                msg,
                lhs.apply(i).run(j),
                rhs.apply(i).run(j));
    }

    @Property
    public void kleisliLeftIdentity(int i, int j) {
        check("Kleisli Left-identity 1", i, j, kPure.andThen(kA), kA);
        check("Kleisli Left-identity 2", i, j, kPure.andThen(kB), kB);
        check("Kleisli Left-identity 3", i, j, kPure.andThen(kC), kC);
    }

    @Property
    public void kleisliRightIdentity(int i, int j) {
        check("Kleisli Right-identity 1", i, j, kA.andThen(kPure), kA);
        check("Kleisli Right-identity 2", i, j, kB.andThen(kPure), kB);
        check("Kleisli Right-identity 3", i, j, kC.andThen(kPure), kC);
    }

    @Property
    public void kleisliIsAssociative(int i, int j) {
        check(
                "Kleisli Associativity",
                i,
                j,
                (kA.andThen(kB)).andThen(kC),
                kA.andThen(kB.andThen(kC)));
    }

    interface DAO {
        double getFxRate(Ccy ccy);
    }

    enum Ccy {
        USD, EUR, GBP
    }

    private static final double usdGbp = 0.74566;
    private static final double usdEur = 0.84778;

    private static final Map<Ccy, Double> fxRates = new HashMap<>();

    static {
        fxRates.put(Ccy.USD, 1.0);
        fxRates.put(Ccy.GBP, usdGbp);
        fxRates.put(Ccy.EUR, usdEur);
    }

    private static final F<Ccy, ReaderM<DAO, Double>> getFxRate = ccy -> dao -> dao.getFxRate(ccy);

    @Test
    public void monadDemo() {

        final F2<Ccy, Ccy, ReaderM<DAO, Double>> getFxRateAB =
                (ccyA, ccyB) ->
                        getFxRate.apply(ccyA).flatMap(fxA ->
                                getFxRate.apply(ccyB).flatMap(fxB ->
                                        pure(fxA / fxB)));

        final DAO dao = fxRates::get;

        final double gbpEurA = getFxRateAB.apply(Ccy.EUR, Ccy.GBP).run(dao);
        final double gbpEurE = usdEur/usdGbp;

        assertEquals("GBP/EUR fx rate via DAO injection", gbpEurE, gbpEurA, 1e-8);
    }

    @Test
    public void applicDemo() {

        final F2<Ccy, Ccy, ReaderM<DAO, Double>> getFxRateAB =
                (ccyA, ccyB) ->
                        getFxRate.apply(ccyB).app(
                                getFxRate.apply(ccyA)
                                        .app(pure(fxA -> fxB -> fxA / fxB))
                        );

        final DAO dao = fxRates::get;

        final double gbpEurA = getFxRateAB.apply(Ccy.EUR, Ccy.GBP).run(dao);
        final double gbpEurE = usdEur/usdGbp;

        assertEquals("GBP/EUR fx rate via DAO injection", gbpEurE, gbpEurA, 1e-8);
    }
}
