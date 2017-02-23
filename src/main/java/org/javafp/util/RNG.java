package org.javafp.util;

import org.javafp.control.State;
import org.javafp.data.*;

public interface RNG {

    static XorShiftRNG xorShiftRNG(long seed) {
        return new XorShiftRNG(seed);
    }

    static State<RNG, Double> nextDbl() {
        return RNG::nextDouble0To1;
    }

    static State<RNG, Long> nextLng() {
        return RNG::nextLong;
    }

    /**
     * @return a random double in the range 0 to 1 inclusive.
     */
    default Tuple2<RNG, Double> nextDouble0To1() {
        final Tuple2<RNG, Long> rngLng = nextLong();
        final double d = (rngLng._2.doubleValue() - (double)Long.MIN_VALUE) / Utils.SCALE;
        return rngLng.with2(d);
    }

    /**
     * @return a random double in the range 0 to 1 inclusive.
     */
    default Tuple2<RNG, Long> nextLong() {
        final Tuple2<RNG, Double> rngDbl = nextDouble0To1();
        final long l = (long)(rngDbl._2 * Utils.SCALE + (double)Long.MIN_VALUE);
        return rngDbl.with2(l);
    }

    class XorShiftRNG implements RNG {
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

class Utils {
    final static double SCALE = (double)Long.MAX_VALUE - (double)Long.MIN_VALUE + 1.0;
}
