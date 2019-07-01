package org.typemeta.funcj.codec;

import java.util.*;

public abstract class TestDataUtils {

    @SafeVarargs
    public static <T> List<T> arrayList(T... vals) {
        return Arrays.asList(vals);
    }

    public static <K, V> HashMap<K, V> hashMap(K k0, V v0, K k1, V v1) {
        final HashMap<K, V> m = new HashMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    public static <K extends Comparable<K>, V> TreeMap<K, V> treeMap(K k0, V v0, K k1, V v1) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> TreeSet<T> treeSet(T... vals) {
        return new TreeSet<T>(Arrays.asList(vals));
    }
}
