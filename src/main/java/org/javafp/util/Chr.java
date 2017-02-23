package org.javafp.util;

import org.javafp.data.IList;

public final class Chr implements Comparable<Chr> {

    private static final int N = 128;

    private static final Chr[] chrs = new Chr[N];

    static {
        for (int i = 0; i != N; ++i) {
            chrs[i] = new Chr(i);
        }
    }

    public static Chr valueOf(char c) {
        return c < N ? chrs[c] : new Chr(c);
    }

    public static boolean isLetterOrDigit(Chr c) {
        return isLetterOrDigit(c.charValue());
    }

    public static boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    public static boolean isLetter(Chr c) {
        return isLetter(c.charValue());
    }

    public static boolean isLetter(char c) {
        return isUpperCase(c) || isLowerCase(c);
    }

    public static boolean isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean isLowerCase(char c) {
        return c >= 'a' && c <= 'z';
    }

    public static boolean isDigit(Chr c) {
        return isDigit(c.charValue());
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isWhitespace(Chr c) {
        return isWhitespace(c.charValue());
    }

    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    public static int getNumericValue(Chr c) {
        return getNumericValue(c.charValue());
    }

    public static int getNumericValue(char d) {
        return d - '0';
    }

    /**
     * Convert a list of Characters into a String.
     */
    public static String listToString(IList<Chr> l) {
        final StringBuilder sb = new StringBuilder();
        for (; !l.isEmpty(); l = l.tail()) {
            sb.append(l.head().charValue());
        }
        return sb.toString();
    }

    /**
     * Convert a String into a list of Characters.
     */
    public static IList<Chr> listToString(String s) {
        IList<Chr> r = IList.nil();
        for (int i = s.length() - 1; i >= 0; --i) {
            r = r.add(Chr.valueOf(s.charAt(i)));
        }
        return r;
    }

    private final char value;

    private Chr(char value) {
        this.value = value;
    }

    public Chr(int value) {
        this.value = (char)value;
    }

    public char charValue() {
        return value;
    }

    @Override
    public int compareTo(Chr rhs) {
        return (value < rhs.value) ? -1 : ((value == rhs.value) ? 0 : 1);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Chr &&
            value == ((Chr) rhs).charValue();
    }

    public boolean equals(char rhs) {
        return value == rhs;
    }

    @Override
    public int hashCode() {
        return (int)value;
    }
}
