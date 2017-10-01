package org.typemeta.funcj.util;

import org.typemeta.funcj.control.StateT;
import org.typemeta.funcj.tuples.Tuple2;

import static org.typemeta.funcj.control.Trampoline.done;
import static org.typemeta.funcj.control.Trampoline.more;

/**
 * Pseudo-random number generator, using the {@link StateT} monad.
 */
public interface RNG {

    static RNG xorShiftRNG(long seed) {
        return new RNGUtils.XorShiftRNG(seed);
    }

    static StateT<RNG, Double> nextDbl() {
        return st -> more(() -> done(st.nextDouble0To1()));
    }

    static StateT<RNG, Long> nextLng() {
        return st -> more(() -> done(st.nextLong()));
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
