package org.typemeta.funcj.codec.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class StreamUtils {
    public static <T, K, U> Collector<T, ?, Map<K,U>> toLinkedHashMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new);
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
