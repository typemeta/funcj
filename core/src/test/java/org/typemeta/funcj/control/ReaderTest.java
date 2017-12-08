package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.control.Reader.Kleisli;
import org.typemeta.funcj.functions.Functions.F;
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

    private static final Kleisli<Integer, Integer, Integer> pure = Kleisli.of(Reader::pure);
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

    @Test
    public void demo() {
        final F<Ccy, Reader<DAO, Double>> getFxRate = ccy -> dao -> dao.getFxRate(ccy);

        final F2<Ccy, Ccy, Reader<DAO, Double>> getFxRateAB =
                (ccyA, ccyB) ->
                        getFxRate.apply(ccyA).flatMap(fxA ->
                                getFxRate.apply(ccyB).flatMap(fxB ->
                                        Reader.pure(fxA / fxB)));

        final DAO dao = fxRates::get;

        final double gbpEurA = getFxRateAB.apply(Ccy.EUR, Ccy.GBP).apply(dao);
        final double gbpEurE = usdEur/usdGbp;

        assertEquals("GBP/EUR fx rate via DAO injection", gbpEurE, gbpEurA, 1e-8);
    }
}
