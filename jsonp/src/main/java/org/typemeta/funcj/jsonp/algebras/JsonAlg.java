package org.typemeta.funcj.jsonp.algebras;

import org.typemeta.funcj.algebra.Monoid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.json.*;

/**
 * An object algebra interface for JSON values.
 * <p>
 * An object algebra is applied to a {@link JsonValue} by passing it to the
 * {@link JsonAlgStack#apply(JsonValue, JsonAlg)} method.
 * @param <T>       the object algebra result type
 */
public interface JsonAlg<T> {

    /**
     * Process a {@link JsonValue} NULL value.
     * @return          the result of processing the value
     */
    T nul();

    /**
     * Process a {@link JsonValue} TRUE/FALSE value.
     * @param b         the {@code JsonValue} boolean value
     * @return          the result of processing the value
     */
    T bool(boolean b);

    /**
     * Process a {@link JsonNumber} value.
     * @param value     the {@code JsonNumber} int value
     * @return          the result of processing the value
     */
    T num(int value);

    /**
     * Process a {@link JsonNumber} value.
     * @param value     the {@code JsonNumber} double value
     * @return          the result of processing the value
     */
    T num(double value);

    /**
     * Process a {@link JsonNumber} value.
     * @param value     the {@code JsonNumber} {@link BigDecimal} value
     * @return          the result of processing the value
     */
    T num(BigDecimal value);

    /**
     * Process a {@link JsonString} value.
     * @param s         the {@code JsonString} string value
     * @return          the result of processing the value
     */
    T str(String s);
    /**
     * Process a {@link JsonArray} value.
     * @param elems     the {@code JsonArray} elements
     * @return          the result of processing the value
     */
    T arr(List<T> elems);

    /**
     * Process a {@link JsonObject} value.
     * @param fields    the {@code JsonObject} fields
     * @return          the result of processing the value
     */
    T obj(Map<String, T> fields);

    /**
     * A query is a an object algebra operation that traverses the {@code JsonValue},
     * and computes an aggregate value.
     * @param <T>       the object algebra result type
     */
    interface Query<T> extends JsonAlg<T> {

        class Base<T> implements Query<T> {
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

        default T num(int value) {
            return m().zero();
        }

        default T num(double value) {
            return m().zero();
        }

        default T num(BigDecimal value) {
            return m().zero();
        }

        default T str(String s) {
            return m().zero();
        }

        default T arr(List<T> elems) {
            return m().combineAll(elems);
        }

        default T obj(Map<String, T> fields) {
            return m().combineAll(fields.values());
        }
    }

    /**
     * A transform is a an object algebra operation that traverses the {@code JsValue},
     * and constructs another structure.
     * @param <T>       the object algebra result type
     */
    interface Transform<T> extends JsonAlg<T> {
        class Base<T> implements Transform<T> {
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

        default T num(int value) {
            return alg().num(value);
        }

        default T num(double value) {
            return alg().num(value);
        }

        default T num(BigDecimal value) {
            return alg().num(value);
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
}
