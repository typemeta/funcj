package org.typemeta.funcj.codec;

import java.util.*;

abstract class TestDataUtils {

    @SafeVarargs
    static <T> ArrayList<T> arrayList(T... vals) {
        final ArrayList<T> l = new ArrayList<T>(vals.length);
        l.addAll(Arrays.asList(vals));
        return l;
    }

    static <K, V> HashMap<K, V> hashMap(K k0, V v0, K k1, V v1) {
        final HashMap<K, V> m = new HashMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    static <K extends Comparable<K>, V> TreeMap<K, V> treeMap(K k0, V v0, K k1, V v1) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    @SafeVarargs
    static <T extends Comparable<T>> TreeSet<T> treeSet(T... vals) {
        return new TreeSet<T>(Arrays.asList(vals));
    }
}
