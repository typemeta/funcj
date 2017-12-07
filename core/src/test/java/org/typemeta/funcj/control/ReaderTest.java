package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.functions.Functions.F.Kleisli;
import org.typemeta.funcj.functions.Functions.F2;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.functions.Functions.F.konst;

@RunWith(JUnitQuickcheck.class)
public class ReaderTest {

    private static final F<Integer, Integer> add = x -> x + 10;
    private static final F<Integer, Integer> times = x -> x * 2;

    @Property
    public void flatMap(int i) {
        final F<Integer, Integer> comb = add.flatMap(a -> times.flatMap(b -> konst(a + b)));
        final int exp = i*2 + i+10;
        assertEquals("add combined with times", exp, comb.apply(i).intValue());
    }

    private static final Kleisli<Integer, Integer, Integer> pure = Kleisli.of(F::konst);
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
                lhs.apply(i).apply(j),
                rhs.apply(i).apply(j));
    }

    @Property
    public void kleisliLeftIdentity(int i, int j) {
        check("Kleisli Left-identity 1", i, j, pure.andThen(kA), kA);
        check("Kleisli Left-identity 2", i, j, pure.andThen(kB), kB);
        check("Kleisli Left-identity 3", i, j, pure.andThen(kC), kC);
    }

    @Property
    public void kleisliRightIdentity(int i, int j) {
        check("Kleisli Right-identity 1", i, j, kA.andThen(pure), kA);
        check("Kleisli Right-identity 2", i, j, kB.andThen(pure), kB);
        check("Kleisli Right-identity 3", i, j, kC.andThen(pure), kC);
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

    interface RateRDAO {
        double getFxRate(String ccy);
        double getUsdIrRate(Integer years);
    }

    private static final Map<String, Double> fxRates = new HashMap<>();
    private static final Map<Integer, Double> irRates = new HashMap<>();

    static {
        fxRates.put("USD", 1.0);
        fxRates.put("GBP", 0.74566);
        fxRates.put("EUR", 0.84778);

        irRates.put(365, 2.0);
        irRates.put(10*365, 3.0);
        irRates.put(50*365, 5.0);
    }

    private static void test() {
        final Kleisli<RateRDAO, String, Double> getFxRateK = ccy -> dao -> dao.getFxRate(ccy);
        final Kleisli<RateRDAO, Integer, Double> getUsdIrRateK = years -> dao -> dao.getUsdIrRate(years);

        final F<String, F<RateRDAO, Double>> getFxRate = ccy -> dao -> dao.getFxRate(ccy);
        final F<Integer, F<RateRDAO, Double>> getUsdIrRate = years -> dao -> dao.getUsdIrRate(years);

        F2<String, Integer, F<RateRDAO, Double>> irRate =
                (ccy, years) ->
                        getFxRate.apply(ccy).flatMap(fx ->
                                getUsdIrRate.apply(years).flatMap(ir ->
                                        konst(fx * ir)));
    }
}
