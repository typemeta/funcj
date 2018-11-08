package org.typemeta.funcj.codec.utils;

public abstract class ClassUtils {
    public static String name(Class<?> clazzA, Class<?> clazzB) {
        return clazzA.getName() + '|' + clazzB.getName();
    }

    public static String name(Class<?> clazzA, Class<?> clazzB, Class<?> clazzC) {
        return clazzA.getName() + '|' + clazzB.getName() + '|' + clazzC.getName();
    }
}
