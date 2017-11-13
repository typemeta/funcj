package org.typemeta.funcj.json;

import java.util.LinkedHashMap;

public interface JsonAlg<T> {
    T nul();
    T bool(boolean b);
    T num(double d);
    T str(String s);
    T arr(T[] elems);
    T obj(LinkedHashMap<String, T> fields);
}
