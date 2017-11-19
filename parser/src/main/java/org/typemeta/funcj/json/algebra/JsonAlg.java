package org.typemeta.funcj.json.algebra;

import org.typemeta.funcj.algebra.Monoid;

import java.util.*;

public interface JsonAlg<T> {
    T nul();
    T bool(boolean b);
    T num(double d);
    T str(String s);
    T arr(List<T> elems);
    T obj(Map<String, T> fields);

    interface Transform<T> extends JsonAlg<T> {

        JsonAlg<T> alg();

        default T nul() {
            return alg().nul();
        }

        default T bool(boolean b) {
            return alg().bool(b);
        }

        default T num(double d) {
            return alg().num(d);
        }

        default T str(String s) {
            return alg().str(s);
        }

        default T arr(List<T> elems) {
            return alg().arr(elems);
        }

        default T obj(Map<String, T> fields) {
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

        default T num(double d) {
            return m().zero();
        }

        default T str(String s) {
            return m().zero();
        }

        default T arr(List<T> elems) {
            return m().fold(elems);
        }

        default T obj(Map<String, T> fields) {
            return m().fold(fields.values());
        }
    }
}
