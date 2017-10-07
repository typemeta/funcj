package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions.F;

/**
 * Common interface for classes represent JSON values.
 */
public interface JSValue {

    /**
     * Pretty-print this value as a JSON string.
     * @param width     maximum line length
     * @return          string representation of formatted JSON
     */
    default String toString(int width)  {
        return DocFormat.format(width, toDocument());
    }

    /**
     * Select the one supplied function that corresponds to the type of this value,
     * and return the result of applying the function to this value.
     * @param nl        the function to be applied to a {@link JSNull} value
     * @param bl        the function to be applied to a {@link JSBool} value
     * @param nm        the function to be applied to a {@link JSNumber} value
     * @param st        the function to be applied to a {@link JSString} value
     * @param ar        the function to be applied to a {@link JSArray} value
     * @param ob        the function to be applied to a {@link JSObject} value
     * @param <T>       the return type of the functions
     * @return          the result of applying the appropriate function to this value
     */
    <T> T match(
        F<JSNull, T> nl,
        F<JSBool, T> bl,
        F<JSNumber, T> nm,
        F<JSString, T> st,
        F<JSArray, T> ar,
        F<JSObject, T> ob
    );

    /**
     * @return          true if this value is a {@link JSNull}, otherwise false
     */
    boolean isNull();

    /**
     * @return          true if this value is a {@link JSBool}, otherwise false
     */
    boolean isBool();

    /**
     * @return          true if this value is a {@link JSNumber}, otherwise false
     */
    boolean isNumber();

    /**
     * @return          true if this value is a {@link JSString}, otherwise false
     */
    boolean isString();

    /**
     * @return          true if this value is a {@link JSArray}, otherwise false
     */
    boolean isArray();

    /**
     * @return          true if this value is a {@link JSObject}, otherwise false
     */
    boolean isObject();

    /**
     * If this value is a {@link JSNull} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSNull} then return it.
     * @throws          RuntimeException if this value is not a {@code JSNull}
     */
    JSNull asNull();

    /**
     * If this value is a {@link JSBool} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSBool} then return it.
     * @throws          RuntimeException if this value is not a {@code JSBool}
     */
    JSBool asBool();

    /**
     * If this value is a {@link JSNumber} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSNumber} then return it.
     * @throws          RuntimeException if this value is not a {@code JSNumber}
     */
    JSNumber asNumber();

    /**
     * If this value is a {@link JSString} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSString} then return it.
     * @throws          RuntimeException if this value is not a {@code JSString}
     */
    JSString asString();

    /**
     * If this value is a {@link JSArray} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSArray} then return it.
     * @throws          RuntimeException if this value is not a {@code JSArray}
     */
    JSArray asArray();

    /**
     * If this value is a {@link JSObject} then downcast it, otherwise throw an exception.
     * @return          if this value is a {@code JSObject} then return it.
     * @throws          RuntimeException if this value is not a {@code JSObject}
     */
    JSObject asObject();

    /**
     * Write this value into the supplied {@code StringBuilder}.
     * @param sb        the {@code StringBuilder}
     * @return          the {@code StringBuilder}
     */
    /* private */ StringBuilder toString(StringBuilder sb);

    /**
     * Construct a {@link Document} instance from the value,
     * which can thern be used to pretty-print the JSON text representation
     * of this value.
     * @return          the {@code Document} representation of this value
     */
    /* private */ Document toDocument();
}
