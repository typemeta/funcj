package org.typemeta.funcj.codec.utils;

import org.typemeta.funcj.util.Exceptions;

public abstract class ClassUtils {
    public static Class<?> forName(String className) {
        return Exceptions.wrap(() -> Class.forName(className));
    }

    public static String name(Class<?> clazzA, Class<?> clazzB) {
        return clazzA.getName() + '|' + clazzB.getName();
    }

    public static String name(Class<?> clazzA, Class<?> clazzB, Class<?> clazzC) {
        return clazzA.getName() + '|' + clazzB.getName() + '|' + clazzC.getName();
    }
}
