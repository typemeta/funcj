package org.typemeta.funcj.json.model;

import org.typemeta.funcj.algebra.Monoid;

import java.util.*;

/**
 * An object algebra interface for JSON values.
 * <p>
 * An object algebra is applied to a {@link JsValue} by passing it to the
 * {@link JsValue#apply(JsonAlg)} method.
 * @param <T>       the object algebra result type
 */
public interface JsonAlg<T> {
    T nul();
    T bool(boolean b);
    T num(double value);
    T str(String s);
    T arr(List<T> elems);
    T obj(LinkedHashMap<String, T> fields);

    /**
     * A query is a an object algebra operation that traverses the {@code JsValue},
     * and computes an aggregate value.
     * @param <T>       the object algebra result type
     */
    interface Query<T> extends JsonAlg<T> {
        class Base<T> implements JsonAlg.Query<T> {
            protected final Monoid<T> m;

            public Base(Monoid<T> m) {
                this.m = m;
            }

            @Override
            public Monoid<T> m() {
                return m;
            }
        }

        Monoid<T> m();

        default T nul() {
            return m().zero();
        }

        default T bool(boolean b) {
            return m().zero();
        }

        default T num(double value) {
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

    /**
     * A transform is a an object algebra operation that traverses the {@code JsValue},
     * and constructs another structure.
     * @param <T>       the object algebra result type
     */
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

        default T num(double value) {
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
}
