package org.typemeta.funcj.util;

import org.typemeta.funcj.control.State;
import org.typemeta.funcj.tuples.Tuple2;

import static org.typemeta.funcj.control.Trampoline.*;

/**
 * Pseudo-random number generator, using the {@link State} monad.
 * <p>
 * Implementations of this interface provide pseudo-random number generation.
 * Implementations must implement at least one of {@link RNG#generateDouble0To1()}
 * or {@link RNG#generateLong()}
 */
public interface RNG {

    /**
     * Return a {@code RNG} based on the XorShift algorithm.
     * @see <a href="https://en.wikipedia.org/wiki/Xorshift">https://en.wikipedia.org/wiki/Xorshift</a>
     * @param seed      the initial seed for the RNG
     * @return          the {@code RNG}
     */
    static RNG xorShiftRNG(long seed) {
        return new RNGUtils.XorShiftRNG(seed);
    }

    /**
     * @return          a state processor which will generate the next random double
     */
    static State<RNG, Double> nextDbl() {
        return st -> defer(() -> done(st.generateDouble0To1()));
    }

    /**
     * @return          a state processor which will generate the next random long
     */
    static State<RNG, Long> nextLng() {
        return st -> defer(() -> done(st.generateLong()));
    }

    /**
     * Return a pair containing the next RNG state and the next random double.
     * @return          a pair containing a random double value in the range 0 to 1 inclusive
     */
    default Tuple2<RNG, Double> generateDouble0To1() {
        final Tuple2<RNG, Long> rngLng = generateLong();
        final double d = (rngLng._2.doubleValue() - (double)Long.MIN_VALUE) / RNGUtils.SCALE;
        return rngLng.with2(d);
    }

    /**
     * Return a pair containing the next RNG state and the next random long.
     * @return          a pair containing a random long value
     */
    default Tuple2<RNG, Long> generateLong() {
        final Tuple2<RNG, Double> rngDbl = generateDouble0To1();
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
        public Tuple2<RNG, Long> generateLong() {
            final long a = seed ^ (seed >>> 12);
            final long b = a ^ (a << 25);
            final long c = b ^ (b >>> 27);
            final long d = (c == 0) ? -1 : c;
            return Tuple2.of(new XorShiftRNG(d), d * 2685821657736338717L);
        }
    }
}
