package org.funcj.data;

/**
 * Simplified boxed equivalent of {@code char} primitive type.
 */
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

    public static Chr valueOf(int c) {
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

    public static final int ERROR = -1;

    public static int digit(char digit, int radix) {
        if (radix <= 10) {
            final int r = digit - '0';
            if (r < radix) {
                return r;
            } else {
                return ERROR;
            }
        } else {
            final int r;

            if ('0' <= digit && digit <= '9') {
                return digit - '0';
            } else if ('a' <= digit && digit <= 'z') {
                r = digit - 'a';
            } else if ('A' <= digit && digit <= 'Z') {
                r = digit - 'A';
            } else {
                return ERROR;
            }

            if (r < radix) {
                return r;
            } else {
                return ERROR;
            }
        }
    }

    /**
     * Convert a list of {@code Chr}s into a {@code String}.
     * @param l     the list of {@code Chr}s
     * @return      the {@code String}
     */
    public static String listToString(IList<Chr> l) {
        final StringBuilder sb = new StringBuilder();
        for (; !l.isEmpty(); l = l.tail()) {
            sb.append(l.head().charValue());
        }
        return sb.toString();
    }

    /**
     * Convert a {@code String} into a list of {@code Chr}s.
     * @param s     the {@code String}
     * @return      the list of {@code Chr}s
     */
    public static IList<Chr> stringToList(String s) {
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

    private Chr(int value) {
        this.value = (char)value;
    }

    public char charValue() {
        return value;
    }

    @Override
    public int compareTo(Chr rhs) {
        return Character.compare(value, rhs.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Chr &&
            value == ((Chr) rhs).value;
    }

    public boolean equals(Chr rhs) {
        return value == rhs.value;
    }

    public boolean equals(char rhs) {
        return value == rhs;
    }

    @Override
    public int hashCode() {
        return (int)value;
    }
}
