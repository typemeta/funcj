package org.typemeta.funcj.util;

import org.typemeta.funcj.data.Tuple2;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Utility methods relating to {@link Stream}s.
 */
public abstract class Streams {
    /**
     * Create a stream of {@link Tuple2}s from a {@link Map}.
     * @param m         the map
     * @param <K>       the map key type
     * @param <V>       the map value type
     * @return          a stream of {@code Tuple2}s
     */
    public static <K, V> Stream<Tuple2<K, V>> of(Map<K, V> m) {
        return m.entrySet().stream().map(en -> Tuple2.of(en.getKey(), en.getValue()));
    }
}
