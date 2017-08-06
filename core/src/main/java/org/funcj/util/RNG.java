package org.funcj.util;

import org.funcj.control.State;
import org.funcj.data.Tuple2;

/**
 * Pseudo-random number generator, using the {@link State} monad.
 */
public interface RNG {

    static RNG xorShiftRNG(long seed) {
        return new RNGUtils.XorShiftRNG(seed);
    }

    static State<RNG, Double> nextDbl() {
        return RNG::nextDouble0To1;
    }

    static State<RNG, Long> nextLng() {
        return RNG::nextLong;
    }

    /**
     * @return a random double value in the range 0 to 1 inclusive.
     */
    default Tuple2<RNG, Double> nextDouble0To1() {
        final Tuple2<RNG, Long> rngLng = nextLong();
        final double d = (rngLng._2.doubleValue() - (double)Long.MIN_VALUE) / RNGUtils.SCALE;
        return rngLng.with2(d);
    }

    /**
     * @return a random long value.
     */
    default Tuple2<RNG, Long> nextLong() {
        final Tuple2<RNG, Double> rngDbl = nextDouble0To1();
        final long l = (long)(rngDbl._2 * RNGUtils.SCALE + (double)Long.MIN_VALUE);
        return rngDbl.with2(l);
    }

}

class RNGUtils {
    final static double SCALE = (double)Long.MAX_VALUE - (double)Long.MIN_VALUE + 1.0;

    protected static class XorShiftRNG implements RNG {
        public final long seed;

        public XorShiftRNG(long seed) {
            this.seed = seed;
        }

        @Override
        public Tuple2<RNG, Long> nextLong() {
            final long a = seed ^ (seed >>> 12);
            final long b = a ^ (a << 25);
            final long c = b ^ (b >>> 27);
            final long d = (c == 0) ? -1 : c;
            return Tuple2.of(new XorShiftRNG(d), d * 2685821657736338717L);
        }
    }
}
