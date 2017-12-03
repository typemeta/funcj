package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.json.algebra.JsonAlg;

/**
 * Common interface for classes that represent JSON values.
 */
public interface JSValue {

    /**
     * Pretty-print a JSON value as a JSON string.
     * @param width     the maximum line length
     * @return          the string representation of formatted JSON
     */
    default String toString(int width) {
        return JsonToDoc.toString(this, width);
    }

    /**
     * Select the one supplied function that corresponds to the type of this value,
     * and return the result of applying the function to this value.
     * @param fNull     the function to be applied to a {@link JSNull} value
     * @param fBool     the function to be applied to a {@link JSBool} value
     * @param fNum      the function to be applied to a {@link JSNumber} value
     * @param fStr      the function to be applied to a {@link JSString} value
     * @param fArr      the function to be applied to a {@link JSArray} value
     * @param fObj      the function to be applied to a {@link JSObject} value
     * @param <T>       the return type of the functions
     * @return          the result of applying the appropriate function to this value
     */
    <T> T match(
        F<JSNull, T> fNull,
        F<JSBool, T> fBool,
        F<JSNumber, T> fNum,
        F<JSString, T> fStr,
        F<JSArray, T> fArr,
        F<JSObject, T> fObj
    );

    <T> T apply(JsonAlg<T> alg);

    /**
     * @return          true if this value is a {@link JSNull}, otherwise false
     */
    default boolean isNull() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JSBool}, otherwise false
     */
    default boolean isBool() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JSNumber}, otherwise false
     */
    default boolean isNumber() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JSString}, otherwise false
     */
    default boolean isString() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JSArray}, otherwise false
     */
    default boolean isArray() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JSObject}, otherwise false
     */
    default boolean isObject() {
        return false;
    }

    /**
     * If this value is a {@link JSNull} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSNull} then return it.
     * @throws          RuntimeException if this value is not a {@code JSNull}
     */
    default JSNull asNull() {
        throw Utils.nullTypeError(getClass());
    }

    /**
     * If this value is a {@link JSBool} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSBool} then return it.
     * @throws          RuntimeException if this value is not a {@code JSBool}
     */
    default JSBool asBool() {
        throw Utils.boolTypeError(getClass());
    }

    /**
     * If this value is a {@link JSNumber} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSNumber} then return it.
     * @throws          RuntimeException if this value is not a {@code JSNumber}
     */
    default JSNumber asNumber() {
        throw Utils.numberTypeError(getClass());
    }

    /**
     * If this value is a {@link JSString} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSString} then return it.
     * @throws          RuntimeException if this value is not a {@code JSString}
     */
    default JSString asString() {
        throw Utils.stringTypeError(getClass());
    }

    /**
     * If this value is a {@link JSArray} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSArray} then return it.
     * @throws          RuntimeException if this value is not a {@code JSArray}
     */
    default JSArray asArray() {
        throw Utils.arrayTypeError(getClass());
    }

    /**
     * If this value is a {@link JSObject} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSObject} then return it.
     * @throws          RuntimeException if this value is not a {@code JSObject}
     */
    default JSObject asObject() {
        throw Utils.objectTypeError(getClass());
    }

    /**
     * Write this value into the supplied {@code StringBuilder}.
     * @param sb        the {@code StringBuilder}
     * @return          the {@code StringBuilder}
     */
    /* protected */ StringBuilder toString(StringBuilder sb);
}
