package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.json.algebras.*;

/**
 * Common interface for classes that represent JSON values.
 */
public interface JsValue {

    /**
     * Value type enumeration.
     */
    enum Type {
        ARRAY,
        BOOL,
        OBJECT,
        NUMBER,
        NULL,
        STRING
    }

    /**
     * Pretty-print a JSON value as a JSON string.
     * @param width     the maximum line length
     * @return          the string representation of formatted JSON
     */
    default String toString(int width) {
        return JsonToDoc.toString(this, width);
    }

    /**
     * Write this value into the given {@code StringBuilder}.
     * @param sb        the {@code StringBuilder}
     * @return          the {@code StringBuilder}
     */
    default StringBuilder toString(StringBuilder sb) {
        return JsonToString.toString(this, sb);
    }

    /**
     * Return the type of this value.
     * @return          the type of this value
     */
    Type type();

    /**
     * Select the one given function that corresponds to the type of this value,
     * and return the result of applying the function to this value.
     * @param fNull     the function to be applied to a {@link JsNull} value
     * @param fBool     the function to be applied to a {@link JsBool} value
     * @param fNum      the function to be applied to a {@link JsNumber} value
     * @param fStr      the function to be applied to a {@link JsString} value
     * @param fArr      the function to be applied to a {@link JsArray} value
     * @param fObj      the function to be applied to a {@link JsObject} value
     * @param <T>       the return type of the functions
     * @return          the result of applying the appropriate function to this value
     */
    <T> T match(
        F<JsNull, T> fNull,
        F<JsBool, T> fBool,
        F<JsNumber, T> fNum,
        F<JsString, T> fStr,
        F<JsArray, T> fArr,
        F<JsObject, T> fObj
    );

    /**
     * Apply an object algebra to this value.
     * @param alg       the object algebra
     * @param <T>       the object algebra result type
     * @return          the result of applying the object algebra
     */
    default <T> T apply(JsonAlg<T> alg) {
        return JsonAlgStack.apply(this, alg);
    }

    /**
     * @return          true if this value is a {@link JsNull}, otherwise false
     */
    default boolean isNull() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JsBool}, otherwise false
     */
    default boolean isBool() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JsNumber}, otherwise false
     */
    default boolean isNumber() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JsString}, otherwise false
     */
    default boolean isString() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JsArray}, otherwise false
     */
    default boolean isArray() {
        return false;
    }

    /**
     * @return          true if this value is a {@link JsObject}, otherwise false
     */
    default boolean isObject() {
        return false;
    }

    /**
     * If this value is a {@link JsNull} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSNull} then return it.
     * @throws          RuntimeException if this value is not a {@code JSNull}
     */
    default JsNull asNull() {
        throw Utils.nullTypeError(getClass());
    }

    /**
     * If this value is a {@link JsBool} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSBool} then return it.
     * @throws          RuntimeException if this value is not a {@code JSBool}
     */
    default JsBool asBool() {
        throw Utils.boolTypeError(getClass());
    }

    /**
     * If this value is a {@link JsNumber} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSNumber} then return it.
     * @throws          RuntimeException if this value is not a {@code JSNumber}
     */
    default JsNumber asNumber() {
        throw Utils.numberTypeError(getClass());
    }

    /**
     * If this value is a {@link JsString} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSString} then return it.
     * @throws          RuntimeException if this value is not a {@code JSString}
     */
    default JsString asString() {
        throw Utils.stringTypeError(getClass());
    }

    /**
     * If this value is a {@link JsArray} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSArray} then return it.
     * @throws          RuntimeException if this value is not a {@code JSArray}
     */
    default JsArray asArray() {
        throw Utils.arrayTypeError(getClass());
    }

    /**
     * If this value is a {@link JsObject} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSObject} then return it.
     * @throws          RuntimeException if this value is not a {@code JSObject}
     */
    default JsObject asObject() {
        throw Utils.objectTypeError(getClass());
    }
}
