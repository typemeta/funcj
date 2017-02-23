package org.javafp.util;

import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class Functional {
    public static <T, U> List<U> map(Functions.F<T, U> f, List<T> l) {
        return l.stream().map(f::apply).collect(toList());
    }
}
