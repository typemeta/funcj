package org.typemeta.funcj.json.algebra;

import org.typemeta.funcj.algebra.Monoid;

import java.util.*;

public interface JsonAlg<T> {
    T nul();
    T bool(boolean b);
    T num(Object value);
    T str(String s);
    T arr(List<T> elems);
    T obj(LinkedHashMap<String, T> fields);

    interface Transform<T> extends JsonAlg<T> {
        class Base<T> implements JsonAlg.Transform<T> {
            protected final JsonAlg<T> alg;

            public Base(JsonAlg<T> alg) {
                this.alg = alg;
            }

            @Override
            public JsonAlg<T> alg() {
                return alg;
            }
        }

        JsonAlg<T> alg();

        default T nul() {
            return alg().nul();
        }

        default T bool(boolean b) {
            return alg().bool(b);
        }

        default T num(Object value) {
            return alg().num(value);
        }

        default T str(String s) {
            return alg().str(s);
        }

        default T arr(List<T> elems) {
            return alg().arr(elems);
        }

        default T obj(LinkedHashMap<String, T> fields) {
            return alg().obj(fields);
        }
    }

    interface Query<T> extends JsonAlg<T> {
        Monoid<T> m();

        default T nul() {
            return m().zero();
        }

        default T bool(boolean b) {
            return m().zero();
        }

        default T num(Object value) {
            return m().zero();
        }

        default T str(String s) {
            return m().zero();
        }

        default T arr(List<T> elems) {
            return m().combineAll(elems);
        }

        default T obj(LinkedHashMap<String, T> fields) {
            return m().combineAll(fields.values());
        }
    }
}
