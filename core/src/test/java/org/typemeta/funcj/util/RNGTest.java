package org.typemeta.funcj.util;

import org.junit.*;
import org.typemeta.funcj.control.State;
import org.typemeta.funcj.data.IList;

public class RNGTest {

    private static final int N = 10000;

    private static State<RNG, String> randomStr() {
        return
            RNG.nextDbl().flatMap(d ->
                RNG.nextLng().map(l ->
                        d + " : " + l
                )
            );
    }

    @Test
    public void testXorShift() {
        final String s = randomStr().eval(RNG.xorShiftRNG(1234));
        Assert.assertEquals("0.23568849406740433 : 5609927630774915935", s);
    }

    @Test
    public void testSequence() {
        final IList<State<RNG, Double>> rngs = generate(RNG.nextDbl(), N);

        final IList<Double> result = State.sequence(rngs).eval(RNG.xorShiftRNG(0));
        Assert.assertEquals("Number of generated random doubles", N, result.size());

        result.forEach(d -> {
            Assert.assertTrue("Random double is in range 0 to 1", 0 <= d || d <= 1);
        });

        final double m = result.foldLeft((x, y) -> x + y, 0.0) / N;
        final double sd = Math.sqrt(result.map(x -> (x-m)*(x-m)).foldLeft(Double::sum, 0.0) / N);

        Assert.assertEquals("Mean", 0.5, m, 0.01);
        Assert.assertEquals("Standard deviation", 1.0/Math.sqrt(12.0), sd, 0.01);
    }

    private static <T> IList<T> generate(T value, int n) {
        IList<T> result = IList.nil();
        for (int i = 0; i < n; ++i) {
            result = result.add(value);
        }
        return result;
    }
}
